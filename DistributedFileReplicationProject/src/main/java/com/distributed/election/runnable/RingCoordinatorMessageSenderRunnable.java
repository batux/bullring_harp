package com.distributed.election.runnable;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.serverfactory.ServerGroup;

public class RingCoordinatorMessageSenderRunnable implements Runnable {

	private volatile ServerGroup serverGroup;
	
	private volatile HarpFileSystem harpFileSystem;
	
	private volatile boolean shouldFinishCoordinatorProcess;
	
	private volatile String coordinatorFileServerNameOfElection;
	
	public RingCoordinatorMessageSenderRunnable(ServerGroup serverGroup, HarpFileSystem harpFileSystem, String coordinatorFileServerNameOfElection, boolean shouldFinishCoordinatorProcess) {
		this.serverGroup = serverGroup;
		this.harpFileSystem = harpFileSystem;
		this.shouldFinishCoordinatorProcess = shouldFinishCoordinatorProcess;
		this.coordinatorFileServerNameOfElection = coordinatorFileServerNameOfElection;
	}
	
	public void run() {

		synchronized (this) {
		
			FileServer ownerFileServer = this.harpFileSystem.getOwnerServer();
			
			String logInformation = "[" + ownerFileServer.getServerName() + "] accepted Ring Coordinator ACK.";
//			System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
			
			if(shouldFinishCoordinatorProcess == false) {
				shouldFinishCoordinatorProcess = true;
			}
			
			FileServer neighborFileServer = ownerFileServer.getFileServersForElection().get(0);
			
			if(neighborFileServer.getServerName().equals(coordinatorFileServerNameOfElection) && shouldFinishCoordinatorProcess == true) {
				// Assign new primary file server
				neighborFileServer.getRequestSenderRunnable().resumeRequestSender();
			}
			else {
				neighborFileServer.getCommunicationSystem().getClient().sendRingCoordinatorMessage(coordinatorFileServerNameOfElection, shouldFinishCoordinatorProcess);
			}
			
		}
		
	}

}
