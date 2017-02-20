package com.distributed.fileserver;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.requestgenerator.RequestSenderRunnable;
import com.distributed.rmi.CommunicationSystem;
import com.distributed.rmi.client.CommunicationClient;
import com.distributed.rmi.server.CommunicationServer;
import com.distributed.serverfactory.ServerGroup;

public class FileServer {

	private String serverName;
	private ServerGroup serverGroup;
	private HarpFileSystem harpFileSystem;
	private FileServerTypes fileServerType;
	private long priorityValue; 				// INFO: Priority value which will be used for Election Algorithms
	private long serverDistanceToPrimaryServer; // INFO: Heuristic value which will be used for Election Algorithms
	private CommunicationSystem communicationSystem;
	private boolean enableFileOperationProperties;
	private boolean electionStateStarted;
	private List<FileServer> candidateFileServers;
	private List<FileServer> fileServersForElection;
	private RequestSenderRunnable requestSenderRunnable;
	
	public FileServer(FileServerTypes fileServerType, boolean enableFileOperationProperties, String rmiServerName, int rmiServerPortno, ServerGroup serverGroup) {
		this(fileServerType, "", enableFileOperationProperties, rmiServerName, rmiServerPortno, serverGroup);
	}
	
	public FileServer(FileServerTypes fileServerType, String replicatedFileName, boolean enableFileOperationProperties, String rmiServerName, int rmiServerportno, ServerGroup serverGroup) {
		
		this.setElectionStateStarted(false);
		
		this.setServerGroup(serverGroup);
		
		this.setFileServerType(fileServerType);
		
		String serverName = RandomStringUtils.randomAlphabetic(6); //+ RandomStringUtils.randomAlphabetic(100) + RandomStringUtils.randomAlphabetic(100) + RandomStringUtils.randomAlphabetic(100) + RandomStringUtils.randomAlphabetic(100) + RandomStringUtils.randomAlphabetic(100);
		this.setServerName(serverName);
		
		this.harpFileSystem = new HarpFileSystem(replicatedFileName, this, this.getServerGroup());
		
		if(enableFileOperationProperties) {
			this.harpFileSystem.enableFileOperationProperties();
		}
		
		this.setEnableFileOperationProperties(enableFileOperationProperties);
		
		CommunicationServer server = new CommunicationServer(this.getServerGroup(), "localhost", rmiServerportno, rmiServerName, this.harpFileSystem);
		server.startServer();
		
		CommunicationClient client = new CommunicationClient(server.getRmiUri());
		
		this.communicationSystem = new CommunicationSystem(client, server);
		
		String logInformation = "File Server " + this.getServerName() + " was created. Server Type: " + this.getFileServerType().toString();
//		System.out.println(logInformation);
		ApplicationLogManager.getApplicationLogger().write(logInformation);
	}
	
	public HarpFileSystem getHarpFileSystem() {
		return harpFileSystem;
	}

	public void setHarpFileSystem(HarpFileSystem harpFileSystem) {
		this.harpFileSystem = harpFileSystem;
	}

	public CommunicationSystem getCommunicationSystem() {
		return communicationSystem;
	}

	public void setCommunicationSystem(CommunicationSystem communicationSystem) {
		this.communicationSystem = communicationSystem;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public FileServerTypes getFileServerType() {
		return fileServerType;
	}

	public void setFileServerType(FileServerTypes fileServerType) {
		this.fileServerType = fileServerType;
	}

	public ServerGroup getServerGroup() {
		return serverGroup;
	}

	public void setServerGroup(ServerGroup serverGroup) {
		this.serverGroup = serverGroup;
	}

	public boolean isEnableFileOperationProperties() {
		return enableFileOperationProperties;
	}

	public void setEnableFileOperationProperties(boolean enableFileOperationProperties) {
		this.enableFileOperationProperties = enableFileOperationProperties;
	}

	public long getServerDistanceToPrimaryServer() {
		return serverDistanceToPrimaryServer;
	}

	public void setServerDistanceToPrimaryServer(long serverDistanceToPrimaryServer) {
		this.serverDistanceToPrimaryServer = serverDistanceToPrimaryServer;
	}

	public long getPriorityValue() {
		return priorityValue;
	}

	public void setPriorityValue(long priorityValue) {
		this.priorityValue = priorityValue;
	}

	public List<FileServer> getFileServersForElection() {
		return fileServersForElection;
	}

	public void setFileServersForElection(List<FileServer> fileServersForElection) {
		this.fileServersForElection = fileServersForElection;
	}

	public synchronized List<FileServer> getCandidateFileServers() {
		return candidateFileServers;
	}

	public void setCandidateFileServers(List<FileServer> candidateFileServers) {
		this.candidateFileServers = candidateFileServers;
	}

	public RequestSenderRunnable getRequestSenderRunnable() {
		return requestSenderRunnable;
	}

	public void setRequestSenderRunnable(RequestSenderRunnable requestSenderRunnable) {
		this.requestSenderRunnable = requestSenderRunnable;
	}

	public synchronized boolean isElectionStateStarted() {
		return electionStateStarted;
	}

	public synchronized void setElectionStateStarted(boolean electionStateStarted) {
		this.electionStateStarted = electionStateStarted;
	}


}
