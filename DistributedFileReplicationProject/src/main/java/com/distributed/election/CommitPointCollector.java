package com.distributed.election;

import java.util.List;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.serverfactory.ServerGroup;

public class CommitPointCollector {

	private ServerGroup serverGroup;
	
	public CommitPointCollector(ServerGroup serverGroup) {
		this.setServerGroup(serverGroup);
	}
	
	public void prepareAllFileServersPriorityInformation() {
		
//		List<FileServerPriorityInformation> fileServerPriorityInformationList = new ArrayList<FileServerPriorityInformation>();
		
		List<FileServer> backupServers = this.getServerGroup().getBackupServers();
		
		for(FileServer backupServer : backupServers) {
			
			backupServer.setElectionStateStarted(false);
			
			long commitPoint = backupServer.getCommunicationSystem().getClient().receiveCommitPoint();
			backupServer.setPriorityValue(commitPoint);
			
			
			String logInformation = backupServer.getServerName() + " File Server - " + " Commit Point: " + commitPoint;
//			System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
			
//			FileServerPriorityInformation fileServerPriorityInformation = new FileServerPriorityInformation(commitPoint, backupServer);
//			fileServerPriorityInformationList.add(fileServerPriorityInformation);
		}
		
		
//		boolean allPriorityValuesSame = areAllPriorityValuesSame(backupServers);
//		
//		if(allPriorityValuesSame) {
			
			for(FileServer backupServer : backupServers) {
				
				long modifiedPriorityValue = backupServer.getPriorityValue() + (100000 - backupServer.getServerDistanceToPrimaryServer());
				backupServer.setPriorityValue(modifiedPriorityValue);
			}
			
//		}
		
//		return fileServerPriorityInformationList;
	}
	
//	private boolean areAllPriorityValuesSame(List<FileServer> backupServers) {
//		
//		boolean allPriorityValuesSame = true;
//		
//		Map<String, Integer> priorityMap = new HashMap<String, Integer>();
//		
//		for(FileServer backupServer : backupServers) {
//			
//			String priorityValueAsText = String.valueOf(backupServer.getPriorityValue());
//			if(priorityMap.get(priorityValueAsText) == null) {
//				priorityMap.put(priorityValueAsText, 0);
//			}
//			else {
//				int countValueOfPriorityValue = priorityMap.get(priorityValueAsText);
//				countValueOfPriorityValue++;
//				priorityMap.put(priorityValueAsText, countValueOfPriorityValue);
//			}
//		}
//		
//		Set<String> keys = priorityMap.keySet();
//		for(String key : keys) {
//			int countValue = priorityMap.get(key);
//			if(countValue == 1) {
//				allPriorityValuesSame = false;
//				break;
//			}
//		}
//		
//		return allPriorityValuesSame;
//	}

	public ServerGroup getServerGroup() {
		return serverGroup;
	}

	public void setServerGroup(ServerGroup serverGroup) {
		this.serverGroup = serverGroup;
	}
	
}
