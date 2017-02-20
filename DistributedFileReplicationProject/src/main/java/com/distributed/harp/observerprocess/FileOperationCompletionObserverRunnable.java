package com.distributed.harp.observerprocess;

import java.util.List;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.harp.volatilelog.VolatileLog;
import com.distributed.serverfactory.ServerGroup;

public class FileOperationCompletionObserverRunnable implements Runnable {

	private Object lock;
	
	private Object readLock;
	
	private volatile HarpFileSystem harpFileSystem;
	
	private volatile VolatileLog volatileLog;

	private volatile ServerGroup serverGroup;
	
	private volatile boolean pause;
	
	private volatile boolean stopThread;
	
	public FileOperationCompletionObserverRunnable(ServerGroup serverGroup, HarpFileSystem harpFileSystem) {
		
		this.pause = true;
		this.stopThread = false;
		this.lock = new Object();
		this.readLock = new Object();
		this.harpFileSystem = harpFileSystem;
		this.serverGroup = serverGroup;
		this.volatileLog = this.harpFileSystem.getVolatileLogProperty().getVolatileLog();
	}
	
	public void run() {
		
		while(!this.stopThread) {
			
			synchronized (this.lock) {
				
				if(this.pause) {
					try {
						this.lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
				
				synchronized (this.readLock) {
					
					long lowerBoundPoint = this.volatileLog.getLowerBoundPoint();
					long applicationPoint = this.volatileLog.getApplicationPoint();
					
					if((lowerBoundPoint <= applicationPoint) && applicationPoint > 0) {
						
						if(lowerBoundPoint > this.volatileLog.getLogEventRecords().size()) {
							break;
						}
						
						this.volatileLog.increaseLowerBoundPoint();
//						System.out.println("[" + this.harpFileSystem.getOwnerServer().getFileServerType().toString() + "] " + this.harpFileSystem.getOwnerServer().getServerName() + "'s LB: " + this.volatileLog.getLowerBoundPoint());
						
						// TODO: Determine GLB with group members via RMI
						List<FileServer> backupServers = this.serverGroup.getBackupServers();
						List<FileServer> witnessServers = this.serverGroup.getWitnessServers();
						
						long globalLowerBoundPoint = this.volatileLog.getLowerBoundPoint();
						//this.serverGroup.getPrimaryServer().getHarpFileSystem().getVolatileLogProperty().getVolatileLog().getLowerBoundPoint();
						
						FileServer ownerFileServer = this.harpFileSystem.getOwnerServer();
						
						if(ownerFileServer == this.serverGroup.getPrimaryServer()) {
							for(int i=0; i < backupServers.size(); i++) {
								
								FileServer backupServer = backupServers.get(i);
								
								long backupServerLowerBoundPoint = backupServer.getCommunicationSystem().getClient().receiveLowerBoundPoint();
								
								if((backupServerLowerBoundPoint < globalLowerBoundPoint) && (backupServerLowerBoundPoint > 0)) {
									globalLowerBoundPoint = backupServerLowerBoundPoint;
								}
								
							}
						}
						else {
							for(int i=0; i < backupServers.size(); i++) {
								
								FileServer backupServer = backupServers.get(i);
								
								if(backupServer == ownerFileServer) continue;
								
								long backupServerLowerBoundPoint = backupServer.getCommunicationSystem().getClient().receiveLowerBoundPoint();
								
								if((backupServerLowerBoundPoint < globalLowerBoundPoint) && (backupServerLowerBoundPoint > 0)) {
									globalLowerBoundPoint = backupServerLowerBoundPoint;
								}
								
							}
							
							long primaryLowerBoundPoint = this.serverGroup.getPrimaryServer().getCommunicationSystem().getClient().receiveLowerBoundPoint();
							
							if((primaryLowerBoundPoint < globalLowerBoundPoint) && (primaryLowerBoundPoint > 0)) {
								globalLowerBoundPoint = primaryLowerBoundPoint;
							}
						}
						
						String logInformation = "GLB for group: " + globalLowerBoundPoint;
						System.out.println(logInformation);
						ApplicationLogManager.getApplicationLogger().write(logInformation);
						
						if(ownerFileServer == this.serverGroup.getPrimaryServer()) {
							for(int i=0; i < backupServers.size(); i++) {
								
								FileServer backupServer = backupServers.get(i);
								FileServer witnessServer = witnessServers.get(i);
								
								backupServer.getCommunicationSystem().getClient().sendLowerBoundPoint(globalLowerBoundPoint);
								witnessServer.getCommunicationSystem().getClient().sendLowerBoundPoint(globalLowerBoundPoint);
							}
						}
						else {
							
							for(int i=0; i < backupServers.size(); i++) {
								
								FileServer backupServer = backupServers.get(i);
								FileServer witnessServer = witnessServers.get(i);
								
								if(backupServer == ownerFileServer) continue;
								if(witnessServer == ownerFileServer) continue;
								
								backupServer.getCommunicationSystem().getClient().sendLowerBoundPoint(globalLowerBoundPoint);
								witnessServer.getCommunicationSystem().getClient().sendLowerBoundPoint(globalLowerBoundPoint);
							}
							
							this.serverGroup.getPrimaryServer().getCommunicationSystem().getClient().sendLowerBoundPoint(globalLowerBoundPoint);
						}
						
					}
					
					pause();
				}
				
			}
			
		}
		
	}
	
	public synchronized void stop() {
		this.stopThread = true;
	}
	
	public synchronized void pause() {
		this.pause = true;
	}
	
	public synchronized void notifyObserver() {
		
		synchronized (this.lock) {
		
			this.pause = false;
			this.lock.notifyAll();
			
		}
		
	}

}
