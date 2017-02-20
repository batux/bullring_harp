package com.distributed.harp.fileoperations;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.observerprocess.FileOperationCompletionObserverRunnable;
import com.distributed.harp.volatilelog.VolatileLog;

public class AsyncFileWriterRunnable implements Runnable {

	private Object lock;
	
	private volatile String data;
	
	private volatile FileServer ownerServer;
	
	private volatile VolatileLog volatileLog;
	
	private volatile FileProcessor fileProcessor;
	
	private volatile FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable;
	
	public AsyncFileWriterRunnable(String data, VolatileLog volatileLog, FileServer ownerServer, FileProcessor fileProcessor, FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable) {
		
		this.data = data;
		this.lock = new Object();
		this.volatileLog = volatileLog;
		this.ownerServer = ownerServer;
		this.fileProcessor = fileProcessor;
		this.fileOperationCompletionObserverRunnable = fileOperationCompletionObserverRunnable;
	}
	
	public void run() {
		
		synchronized (this.lock) {
		
			try {
				this.fileProcessor.write(this.data);
				String logInformation = "[" + this.ownerServer.getFileServerType().toString() + "] " + this.ownerServer.getServerName() + " wrote '" + this.data + "'";
				ApplicationLogManager.getApplicationLogger().write(logInformation);
				System.out.println(logInformation);
				this.fileOperationCompletionObserverRunnable.notifyObserver();
			} catch (Exception e) {
				long applicationPoint = this.volatileLog.getApplicationPoint();
				applicationPoint = applicationPoint - 1;
				this.volatileLog.setApplicationPoint(applicationPoint);
				String logInformation = "[WRITE ERROR]" + "[" + this.ownerServer.getFileServerType().toString() + "] " + this.ownerServer.getServerName() + "'s AP: " + this.volatileLog.getApplicationPoint();
				System.out.println(logInformation);
				ApplicationLogManager.getApplicationLogger().write(logInformation);
			}
			
		}

	}

}
