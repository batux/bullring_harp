package com.distributed.serverfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.fileserver.FileServerTypes;

public class ServerGroup {

	private FileServer primaryServer;
	private List<FileServer> backupServers;
	private List<FileServer> witnessServers;
	private Random heuristicDistanceGenerator;
	private int n;

	public ServerGroup(int n) {
		this.setPrimaryServer(null);
		this.setBackupServers(new ArrayList<FileServer>());
		this.setWitnessServers(new ArrayList<FileServer>());
		this.heuristicDistanceGenerator = new Random();
		this.n = n;
	}
	
	public synchronized void createFileServerGroup() {
		
		int basePortNo = 60000;
		
		FileServer primaryServer = new FileServer(FileServerTypes.PRIMARY, "ServerFile0.txt", true, "Primary_RMI_Server", basePortNo, this);
		primaryServer.setServerDistanceToPrimaryServer(0);
		this.setPrimaryServer(primaryServer);
		
		int serverFileCounter = 0;
		
		for(int i=0; i < this.n; i++) {
			
			basePortNo++;
			serverFileCounter++;
			
			FileServer backupServer = new FileServer(FileServerTypes.BACKUP, ("ServerFile" + serverFileCounter + ".txt"), true, ("Backup_RMI_Server" + i), basePortNo, this);
			backupServer.setServerDistanceToPrimaryServer((long)(this.heuristicDistanceGenerator.nextDouble()*1000));
			this.getBackupServers().add(backupServer);
			
			basePortNo++;
			
			FileServer witnessServer = new FileServer(FileServerTypes.WITNESS, false, "Witness_RMI_Server", basePortNo, this);
			witnessServer.setServerDistanceToPrimaryServer((long)(this.heuristicDistanceGenerator.nextDouble()*1000));
			this.getWitnessServers().add(witnessServer);
		}
		
		String logInformation = "Server Group was created.";
//		System.out.println("Server Group was created.");
		ApplicationLogManager.getApplicationLogger().write(logInformation);
	}

	public synchronized FileServer getPrimaryServer() {
		return primaryServer;
	}

	public synchronized void setPrimaryServer(FileServer primaryServer) {
		this.primaryServer = primaryServer;
	}

	public synchronized List<FileServer> getBackupServers() {
		return backupServers;
	}

	public synchronized void setBackupServers(List<FileServer> backupServers) {
		this.backupServers = backupServers;
	}

	public synchronized List<FileServer> getWitnessServers() {
		return witnessServers;
	}

	public synchronized void setWitnessServers(List<FileServer> witnessServers) {
		this.witnessServers = witnessServers;
	}
}
