package com.distributed.election;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.serverfactory.ServerGroup;

public class RingElectionAlgorithm {

	private ServerGroup serverGroup;
	
	private List<FileServer> candidateFileServers;
	
	public RingElectionAlgorithm(ServerGroup serverGroup) {
		this.setServerGroup(serverGroup);
		this.setCandidateFileServers(new ArrayList<FileServer>());
	}
	
	public void prepareFileServersForRingElection() {
		
		this.getCandidateFileServers().clear();
		
		List<FileServer> backupServers = this.getServerGroup().getBackupServers();
		
		this.getCandidateFileServers().addAll(backupServers);
		
		Collections.sort(this.getCandidateFileServers(), new FileServerPriorityComparator());
		
		for(FileServer candidateFileServer : this.getCandidateFileServers()) {
			String logInformation = "Server Name: " + candidateFileServer.getServerName() + " Priority Value: " + candidateFileServer.getPriorityValue();
//			System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
		}
		
		createRingStructure();
	}
	
	private void createRingStructure() {
		
		FileServer candidateFileServer = this.getCandidateFileServers().get(0);
		
		List<FileServer> neighborFileServers = new ArrayList<FileServer>();
		neighborFileServers.add(this.getCandidateFileServers().get(1));
		
		candidateFileServer.setFileServersForElection(neighborFileServers);
		
		String logInformation = "Current File Server: " + candidateFileServer.getServerName() + ", Next File Server: " + candidateFileServer.getFileServersForElection().get(0).getServerName();
//		System.out.println(logInformation);
		ApplicationLogManager.getApplicationLogger().write(logInformation);
		
		for(int i=1; i < this.getCandidateFileServers().size() - 1; i++) {
			
			candidateFileServer = this.getCandidateFileServers().get(i);
			
			neighborFileServers = new ArrayList<FileServer>();
			neighborFileServers.add(this.getCandidateFileServers().get(i+1));
			
			candidateFileServer.setFileServersForElection(neighborFileServers);
			
			logInformation = "Current File Server: " + candidateFileServer.getServerName() + ", Next File Server: " + candidateFileServer.getFileServersForElection().get(0).getServerName();
//			System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
		}
		
		
		candidateFileServer = this.getCandidateFileServers().get(this.getCandidateFileServers().size() - 1);
		
		neighborFileServers = new ArrayList<FileServer>();
		neighborFileServers.add(this.getCandidateFileServers().get(0));
		
		candidateFileServer.setFileServersForElection(neighborFileServers);
		
		logInformation = "Current File Server: " + candidateFileServer.getServerName() + ", Next File Server: " + candidateFileServer.getFileServersForElection().get(0).getServerName();
//		System.out.println(logInformation);
		ApplicationLogManager.getApplicationLogger().write(logInformation);
	}
	
	public void runRingAlgorithm() {
		
	  	FileServer firstFileServer = this.getCandidateFileServers().get(0);
	  	firstFileServer.getCommunicationSystem().getClient().sendInitRingElectionMessage(firstFileServer.getServerName(), new ArrayList<String>());
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
