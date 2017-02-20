package com.distributed.harp.applyprocess;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.harp.fileoperations.AsyncFileWriter;
import com.distributed.harp.fileoperations.AsyncFileWriterRunnable;
import com.distributed.harp.fileoperations.FileProcessor;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.harp.observerprocess.FileOperationCompletionObserverRunnable;
import com.distributed.harp.volatilelog.EventRecord;
import com.distributed.harp.volatilelog.VolatileLog;

public class ApplyProcessRunnable implements Runnable {

	private Object lock;
	
	private volatile boolean pause;
	
	private volatile boolean stopThread;
	
	private volatile HarpFileSystem harpFileSystem;
	
	private volatile VolatileLog volatileLog;
	
	private volatile FileProcessor fileProcessor;
	
	private volatile FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable;
	
	
	public ApplyProcessRunnable(FileProcessor fileProcessor, HarpFileSystem harpFileSystem, FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable) {
		
		this.pause = false;
		this.stopThread = false;
		this.lock = new Object();
		this.fileProcessor = fileProcessor;
		this.harpFileSystem = harpFileSystem;
		this.volatileLog = this.harpFileSystem.getVolatileLogProperty().getVolatileLog();
		this.fileOperationCompletionObserverRunnable = fileOperationCompletionObserverRunnable;
	}
	
	public void run() {
		
		while(!this.stopThread) {
			
			synchronized (this.lock) {
				
				if(this.pause) {
					try {
						String logInformation = "[" + this.harpFileSystem.getOwnerServer().getFileServerType().toString() + "] " + this.harpFileSystem.getOwnerServer().getServerName() + "'s APPLY thread PAUSED.";
//						System.out.println(logInformation);
						ApplicationLogManager.getApplicationLogger().write(logInformation);
						this.lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
				
				long commitPoint = this.volatileLog.getCommitPoint();
				long applicationPoint = this.volatileLog.getApplicationPoint();
				
				if((applicationPoint <= commitPoint) && commitPoint > 0) {
					
					this.volatileLog.increaseApplicationPoint();
					
					applicationPoint = this.volatileLog.getApplicationPoint();					
					
					if(applicationPoint > this.volatileLog.getLogEventRecords().size()) {
						
						applicationPoint = applicationPoint - 1;
						this.volatileLog.setApplicationPoint(applicationPoint);
						continue;
					}
					
					EventRecord eventRecordForPerformingFileOperation = this.volatileLog.getLogEventRecords().get((int)(applicationPoint - 1));
					
					if(eventRecordForPerformingFileOperation.isActive()) {
						
						String logInformation = "[" + this.harpFileSystem.getOwnerServer().getFileServerType().toString() +"] " +
												this.harpFileSystem.getOwnerServer().getServerName() + " reads " + "[Data: " + eventRecordForPerformingFileOperation.getData() + " Date: " + eventRecordForPerformingFileOperation.getEventRecordInsertionDateTime() + "]";
						ApplicationLogManager.getApplicationLogger().write(logInformation);
						
//						System.out.println(logInformation);
						
						logInformation = "[" + this.harpFileSystem.getOwnerServer().getFileServerType().toString() +"] " + this.harpFileSystem.getOwnerServer().getServerName() + "'s AP: " + applicationPoint;
						ApplicationLogManager.getApplicationLogger().write(logInformation);
						
//						System.out.println(logInformation);
						
						// Perform File Operation in a Thread
						String data = eventRecordForPerformingFileOperation.getIndex() + "-" + String.valueOf(eventRecordForPerformingFileOperation.getData());
//						String data = String.valueOf(eventRecordForPerformingFileOperation.getData()) + "-" + eventRecordForPerformingFileOperation.getFailureProbability();
						AsyncFileWriterRunnable asyncFileWriterRunnable = new AsyncFileWriterRunnable(data, this.volatileLog, this.harpFileSystem.getOwnerServer(), this.fileProcessor, this.fileOperationCompletionObserverRunnable);
						AsyncFileWriter asyncFileWriter = new AsyncFileWriter(asyncFileWriterRunnable);
						asyncFileWriter.start();
					}
					else {
						
						applicationPoint = applicationPoint - 1;
						this.volatileLog.setApplicationPoint(applicationPoint);
					}
					
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
//				else {
//					System.out.println("No read operation in Apply Process");
//				}
			}
			
		}
		
	}
	
	public synchronized void stop() {
		this.stopThread = true;
	}

	public synchronized void pause() {
		this.pause = true;
//		this.fileOperationCompletionObserverRunnable.pause();
	}
	
	public synchronized void resume() {
		
		synchronized (this.lock) {
			this.pause = false;
			this.lock.notifyAll();
		}

	}

}
