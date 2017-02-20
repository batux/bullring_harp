package com.distributed.harp.runnable;

import java.util.List;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.harp.volatilelog.EventRecord;
import com.distributed.harp.volatilelog.VolatileLog;
import com.distributed.serverfactory.ServerGroup;

public class CommitPointUpdaterRunnable implements Runnable {

	private Object lock;
	
	private volatile HarpFileSystem harpFileSystem;
	
	private volatile VolatileLog volatileLog;
	
	private volatile ServerGroup serverGroup;
	
	private volatile boolean pause;
	
	public CommitPointUpdaterRunnable(ServerGroup serverGroup, HarpFileSystem harpFileSystem) {
		this.lock = new Object();
		this.harpFileSystem = harpFileSystem;
		this.setVolatileLog(this.harpFileSystem.getVolatileLogProperty().getVolatileLog());
		this.setServerGroup(serverGroup);
	}
	
	public void run() {
		
		synchronized (this.lock) {
			
			if(this.pause) {
				try {
					this.lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(400);
				
				this.volatileLog.increaseCommitPoint();
				
				String logInformation = "[" + this.harpFileSystem.getOwnerServer().getFileServerType().toString() + "]" + this.harpFileSystem.getOwnerServer() + "'s CP : " + this.volatileLog.getCommitPoint();
						
				System.out.println(logInformation);
				
				ApplicationLogManager.getApplicationLogger().write(logInformation);
				
				EventRecord eventRecord = this.volatileLog.getLogEventRecords().get((int)(this.volatileLog.getCommitPoint() - 1));
				
				// TODO:Send CP to group members via RMI
				List<FileServer> backupServers = this.getServerGroup().getBackupServers();
				List<FileServer> witnessServers = this.getServerGroup().getWitnessServers();
				
				for(int i=0; i < backupServers.size(); i++) {
					
					FileServer backupServer = backupServers.get(i);
					FileServer witnessServer = witnessServers.get(i);
					
					backupServer.getCommunicationSystem().getClient().sendCommitPoint(this.volatileLog.getCommitPoint(), eventRecord);
					witnessServer.getCommunicationSystem().getClient().sendCommitPoint(this.volatileLog.getCommitPoint(), eventRecord);
				}
				
//				System.out.println("Commit Point was sended to all group members.");
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void pause() {
		this.pause = true;
	}
	
	public synchronized void resume() {
		
		synchronized (this.lock) {
			this.pause = false;
			this.lock.notifyAll();
		}
		
	}

	public VolatileLog getVolatileLog() {
		return volatileLog;
	}

	public void setVolatileLog(VolatileLog volatileLog) {
		this.volatileLog = volatileLog;
	}

	public ServerGroup getServerGroup() {
		return serverGroup;
	}

	public void setServerGroup(ServerGroup serverGroup) {
		this.serverGroup = serverGroup;
	}

}
