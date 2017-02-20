package com.distributed.election.runnable;

import java.util.List;

import com.distributed.election.PrimaryFileServerHolder;
import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.serverfactory.ServerGroup;

public class ElectionMessageSenderRunnable implements Runnable {

	private volatile ServerGroup serverGroup;
	
	private volatile HarpFileSystem harpFileSystem;
	
	private volatile List<String> visitedServerNames;
	
	private volatile String coordinatorFileServerNameOfElection;
	
	public ElectionMessageSenderRunnable(ServerGroup serverGroup, HarpFileSystem harpFileSystem, String coordinatorFileServerNameOfElection, List<String> visitedServerNames) {
		this.serverGroup = serverGroup;
		this.harpFileSystem = harpFileSystem;
		this.visitedServerNames = visitedServerNames;
		this.coordinatorFileServerNameOfElection = coordinatorFileServerNameOfElection;
	}
	
	public void run() {
		
		synchronized (this) {
			
			FileServer ownerServer = this.harpFileSystem.getOwnerServer();
			
			// Calls next server in Ring
			
			FileServer neighborFileServer = ownerServer.getFileServersForElection().get(0);
			
			visitedServerNames.add(ownerServer.getServerName());
			
			String logInformation = "["+ ownerServer.getServerName() +"] " + "Visited File Names: " + visitedServerNames;
//				System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
			
			if(coordinatorFileServerNameOfElection.equals(neighborFileServer.getServerName()) && (visitedServerNames.size() > 0)) {
				
				// Selects the highest value and Starts Coordinator Communication
				
				List<FileServer> backupFileServers = this.serverGroup.getBackupServers();
				
				FileServer highestValueFileServer = backupFileServers.get(0);
				
				for(FileServer backupFileServer : backupFileServers) {
				
					if(highestValueFileServer.getPriorityValue() < backupFileServer.getPriorityValue()) {
						highestValueFileServer = backupFileServer;
					}
				
				}
				
				PrimaryFileServerHolder.primaryFileServer = highestValueFileServer;
				
				neighborFileServer.getCommunicationSystem().getClient().sendRingCoordinatorMessage(coordinatorFileServerNameOfElection, false);
				
			}
			else {
				neighborFileServer.getCommunicationSystem().getClient().sendInitRingElectionMessage(coordinatorFileServerNameOfElection, visitedServerNames);
			}
		
		}
	}

}
