package com.distributed.election;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.serverfactory.ServerGroup;

public class BullyElectionAlgorithm {

	private ServerGroup serverGroup;
	
	private List<FileServer> candidateFileServers;
	
	public BullyElectionAlgorithm(ServerGroup serverGroup) {
		this.setServerGroup(serverGroup);
		this.setCandidateFileServers(new ArrayList<FileServer>());
	}
	
	public void prepareFileServersForBullyElection() {
		
		this.getCandidateFileServers().clear();
		
		List<FileServer> backupServers = this.getServerGroup().getBackupServers();
		
		createBullyStructure(backupServers);
		
		this.getCandidateFileServers().addAll(backupServers);
	}
	
	private void createBullyStructure(List<FileServer> backupServers) {
		
		for(int i=0; i < backupServers.size(); i++) {
			
			FileServer currentBackupServer = backupServers.get(i);
			
			List<FileServer> fileServersForElection = new ArrayList<FileServer>();
			
			for(int j=0; j < backupServers.size(); j++) {
				
				FileServer backupServer = backupServers.get(j);
				
				if(currentBackupServer.getPriorityValue() < backupServer.getPriorityValue()) {
					fileServersForElection.add(backupServer);
				}
				
			}
			
			currentBackupServer.setFileServersForElection(fileServersForElection);
		}
		
	}
	
	public void runBullyAlgorithm() {
		
		List<FileServer> candidateFileServers = this.getCandidateFileServers();;
		
		Collections.sort(candidateFileServers, new FileServerPriorityComparator());
		
		List<FileServer> backupServers = this.getServerGroup().getBackupServers();
		
		for(int i=0; i < backupServers.size(); i++) {
			
			FileServer currentBackupServer = backupServers.get(i);
			currentBackupServer.setCandidateFileServers(candidateFileServers);
		}
		
		for(FileServer candidateFileServer : candidateFileServers) {
			String logInformation = "[" + candidateFileServer.getServerName() + "'s PV: " + candidateFileServer.getPriorityValue();
			ApplicationLogManager.getApplicationLogger().write(logInformation);
		}
		
		FileServer firstCandidateFileServer = candidateFileServers.get(0);
		
		for(FileServer candidateFileServer : firstCandidateFileServer.getFileServersForElection()) {
			candidateFileServer.getCommunicationSystem().getClient().sendInitElectionMessage(firstCandidateFileServer.getServerName(), firstCandidateFileServer.getPriorityValue());
		}
		
	}

	public ServerGroup getServerGroup() {
		return serverGroup;
	}

	public void setServerGroup(ServerGroup serverGroup) {
		this.serverGroup = serverGroup;
	}

	public List<FileServer> getCandidateFileServers() {
		return candidateFileServers;
	}

	public void setCandidateFileServers(List<FileServer> candidateFileServers) {
		this.candidateFileServers = candidateFileServers;
	}
	
}
