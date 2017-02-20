package com.distributed.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import com.distributed.election.ElectionAckTypes;
import com.distributed.election.PrimaryFileServerHolder;
import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.election.runnable.ElectionMessageSenderRunnable;
import com.distributed.election.runnable.RingCoordinatorMessageSenderRunnable;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.harp.runnable.CommitPointUpdaterRunnable;
import com.distributed.harp.runnable.EventRecordAppenderRunnable;
import com.distributed.harp.thread.CommitPointUpdater;
import com.distributed.harp.thread.EventRecordAppender;
import com.distributed.harp.volatilelog.EventRecord;
import com.distributed.harp.volatilelog.VolatileLog;
import com.distributed.requestgenerator.Request;
import com.distributed.serverfactory.ServerGroup;

public class CommunicationSystemImpl extends UnicastRemoteObject implements CommunicationSystemPrototype {

	private static final long serialVersionUID = 7472595791224158363L;
	
	private ServerGroup serverGroup;
	
	private HarpFileSystem harpFileSystem;
	
	private Object lockForRequestToHarpFileSystem;
	private Object lockSendCommitPoint;
	private Object lockSendLowerBoundPoint;
	private Object lockReceiveLowerBoundPoint;
	private Object lockReceiveCommitPoint;
	private Object lockSendInitElectionMessage;
	private Object lockSendInitElectionResponse;
	private Object lockSendInitRingMessage;
	private Object lockSendRingCoordinatorMessage;
	private Object lockSendCoordinatorMessage;
	
	public CommunicationSystemImpl(ServerGroup serverGroup, HarpFileSystem harpFileSystem) throws RemoteException {
		super();
		this.setServerGroup(serverGroup);
		this.setHarpFileSystem(harpFileSystem);
		
		this.lockSendCommitPoint = new Object(); 
		this.lockReceiveCommitPoint = new Object();
		this.lockSendLowerBoundPoint = new Object();
		this.lockSendInitRingMessage = new Object();
		this.lockSendCoordinatorMessage = new Object();
		this.lockReceiveLowerBoundPoint = new Object();
		this.lockSendInitElectionMessage = new Object();
		this.lockSendInitElectionResponse = new Object();
		this.lockForRequestToHarpFileSystem = new Object();
		this.lockSendRingCoordinatorMessage = new Object();
	}
	
	public void sendRingCoordinatorMessage(String coordinatorFileServerNameOfElection, boolean shouldFinishCoordinatorProcess) throws RemoteException {
		
		synchronized (this.lockSendRingCoordinatorMessage) {
			
			RingCoordinatorMessageSenderRunnable ringCoordinatorMessageSenderRunnable =  new RingCoordinatorMessageSenderRunnable(serverGroup, harpFileSystem, coordinatorFileServerNameOfElection, shouldFinishCoordinatorProcess);
			
			Thread ringCoordinatorMessageThread = new Thread(ringCoordinatorMessageSenderRunnable);
			ringCoordinatorMessageThread.start();
		}
		
	}
	
	public void sendInitRingElectionMessage(String coordinatorFileServerNameOfElection, List<String> visitedServerNames) throws RemoteException {
		
		synchronized (this.lockSendInitRingMessage) {
			
			ElectionMessageSenderRunnable electionMessageSenderRunnable = new ElectionMessageSenderRunnable(this.serverGroup, this.harpFileSystem, coordinatorFileServerNameOfElection, visitedServerNames);
			
			Thread initRingElectionMessageThread = new Thread(electionMessageSenderRunnable);
			initRingElectionMessageThread.start();
		}
		
	}
	
	public void sendInitElectionMessage(String serverName, long priorityValue) throws RemoteException {
		
		synchronized (this.lockSendInitElectionMessage) {
			
			FileServer ownerServer = getHarpFileSystem().getOwnerServer();
			
			if(ownerServer.isElectionStateStarted() == false) {
			
				final String serverNameData = serverName;
				final long priorityValueData = priorityValue;
				
				Thread threadInitElection = new Thread(new Runnable() {
					
					public void run() {
						
						synchronized (this) {
						
							List<FileServer> backupServers = getServerGroup().getBackupServers();
							
							for(FileServer backupServer : backupServers) {
								
								if(backupServer.getServerName().equals(serverNameData)) {
									
									FileServer ownerServer = getHarpFileSystem().getOwnerServer();
									
										ElectionAckTypes electionResponse = null;
										
										if(ownerServer.getPriorityValue() > priorityValueData) {
											// REJECT Election
											electionResponse = ElectionAckTypes.REJECT_ELECTION;
										}
										else {
											// ACCEPT Election
											electionResponse = ElectionAckTypes.ACCEPT_ELECTION;
										}
										
										backupServer.getCommunicationSystem().getClient().sendInitElectionResponse(electionResponse.toString());
										
										if(electionResponse == ElectionAckTypes.REJECT_ELECTION) {
											
											// Creates a new Election
											
											String serverName1 = ownerServer.getServerName();
											long priorityValue1 = ownerServer.getPriorityValue();
											
											String logInformation = "[" + serverName1 + "] creates an election. " + serverName1 + "'s PV: " + priorityValue1 + ", " + backupServer.getServerName() + "'s PV: " + priorityValueData;
											
											System.out.println(logInformation);
											
											ApplicationLogManager.getApplicationLogger().write(logInformation);
											
											List<FileServer> candidateFileServers = ownerServer.getFileServersForElection();
											
											if(candidateFileServers.size() > 0) {
												for(FileServer candidateFileServer : candidateFileServers) {
													
													if(candidateFileServer.isElectionStateStarted()) continue;
													
													candidateFileServer.getCommunicationSystem().getClient().sendInitElectionMessage(serverName1, priorityValue1);
												}
												ownerServer.setElectionStateStarted(true);
											}
											
										}
										
										break;
									
									}
								}
								
						
						}
					}
				});
				
				threadInitElection.start();
				
			}
		}
		
	}

	public void sendInitElectionResponse(String electionAckType) throws RemoteException {
		
		synchronized (this.lockSendInitElectionResponse) {
			
			final String electionAckTypeData = electionAckType;
			
			Thread threadInitElectionResponse = new Thread(new Runnable() {
				
				public void run() {
					
					synchronized (this) {
						
						if(electionAckTypeData.equals("REJECT_ELECTION")) {
							
							FileServer ownerFileServer = getHarpFileSystem().getOwnerServer();
							if(ownerFileServer.getCandidateFileServers().contains(ownerFileServer)) {
								
								ownerFileServer.getCandidateFileServers().remove(ownerFileServer);
								
								String serverName = ownerFileServer.getServerName();
								
								String logInformation = "[" + serverName + "] retreats from election. " + serverName;
								
								System.out.println(logInformation);
								
								ApplicationLogManager.getApplicationLogger().write(logInformation);
								
								if(ownerFileServer.getCandidateFileServers().size() == 1) {
									
									FileServer newPrimaryFileServer = ownerFileServer.getCandidateFileServers().get(0);
									newPrimaryFileServer.setElectionStateStarted(true);
									
									PrimaryFileServerHolder.primaryFileServer = newPrimaryFileServer;
									
									ownerFileServer.getCandidateFileServers().clear();
									
									List<FileServer> backupServers = getServerGroup().getBackupServers();
									
									for(FileServer subBackupServer : backupServers) {
										
										if(subBackupServer == newPrimaryFileServer) continue;
										
										subBackupServer.getCommunicationSystem().getClient().sendCoordinatorMessage();
									}
									
									newPrimaryFileServer.getRequestSenderRunnable().resumeRequestSender();
								}
								
							}
						}
					
					}
				}
				
			});
			
			threadInitElectionResponse.start();
		}
		
	}
	
	public void sendCoordinatorMessage() throws RemoteException {
		
		synchronized (this.lockSendCoordinatorMessage) {
			
			FileServer ownerFileServer = getHarpFileSystem().getOwnerServer();
			
			String logInformation = "[" + ownerFileServer.getServerName() + "] accepted Bully Coordinator ACK.";
			
			System.out.println(logInformation);
			
			ApplicationLogManager.getApplicationLogger().write(logInformation);
		}
		
	}
	

	public void sendRequestToHarpFileSystem(Request request) throws RemoteException {
		
//		VolatileLog volatileLog = this.harpFileSystem.getVolatileLogProperty().getVolatileLog();
		
		synchronized (this.lockForRequestToHarpFileSystem) {
			
			EventRecordAppenderRunnable eventRecordAppenderRunnable = new EventRecordAppenderRunnable(request, this.harpFileSystem);
			EventRecordAppender appendEventRecordThread = new EventRecordAppender(eventRecordAppenderRunnable);
			appendEventRecordThread.start();
			
			try {
				appendEventRecordThread.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			CommitPointUpdaterRunnable commitPointUpdaterRunnable = new CommitPointUpdaterRunnable(this.getServerGroup(), this.harpFileSystem);
			CommitPointUpdater commitPointIncrementThread = new CommitPointUpdater(commitPointUpdaterRunnable);
			commitPointIncrementThread.start();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	public void sendCommitPoint(long commitPoint, EventRecord eventRecord) throws RemoteException {
		
		synchronized (this.lockSendCommitPoint) {
			
			final long commitPointData = commitPoint;
			
			final EventRecord eventRecordData = eventRecord;
			
//			Thread commitPointUpdater = new Thread(new Runnable() {
//				
//				public void run() {
//					synchronized (this) {
//
//						try {
//							Thread.sleep(50);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						
						VolatileLog volatileLog = harpFileSystem.getVolatileLogProperty().getVolatileLog();
						volatileLog.appendEventRecord(eventRecordData);
						volatileLog.setCommitPoint(commitPointData);
						
						String logInformation = "[" + harpFileSystem.getOwnerServer().getFileServerType().toString() + "] " + harpFileSystem.getOwnerServer().getServerName() + " accepts CP from primary server. CP: " + commitPointData;
						System.out.println(logInformation);
						ApplicationLogManager.getApplicationLogger().write(logInformation);
//					}
//				}
//			});
//			
//			commitPointUpdater.start();
		}
		
	}
	

	public void sendLowerBoundPoint(long lowerBoundPoint) throws RemoteException {
		
		synchronized (this.lockSendLowerBoundPoint) {
			
			final long lowerBoundPointData = lowerBoundPoint;
			
			Thread lowerBoundPointUpdater = new Thread(new Runnable() {
				
				public void run() {
					synchronized (this) {
						
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						VolatileLog volatileLog = harpFileSystem.getVolatileLogProperty().getVolatileLog();
						volatileLog.setGlobalLowerBoundPoint(lowerBoundPointData);
						
						// INFO: Remove event records which are below GLB value.
						for(int i=0; i < lowerBoundPointData; i++) {
							volatileLog.getLogEventRecords().get(i).setActive(false);
						}
						
//						System.out.println("[" + harpFileSystem.getOwnerServer().getFileServerType().toString() + "] " + harpFileSystem.getOwnerServer().getServerName() + " accepts GLB from primary server. GLB: " + lowerBoundPointData);
					}
				}
			});
			
			lowerBoundPointUpdater.start();
		}
		
	}
	
	
	public long receiveLowerBoundPoint() throws RemoteException {
		
		synchronized (this.lockReceiveLowerBoundPoint) {
			
			VolatileLog volatileLog = this.harpFileSystem.getVolatileLogProperty().getVolatileLog();
			
			long lowerBoundPoint = volatileLog.getLowerBoundPoint();
			
			if(!"PRIMARY".equals(harpFileSystem.getOwnerServer().getFileServerType().toString())) {
				
				String logInformation = "[" + harpFileSystem.getOwnerServer().getFileServerType().toString() + "] " + harpFileSystem.getOwnerServer().getServerName() + " sends LB to primary server. LB: " + lowerBoundPoint;
				
				System.out.println(logInformation);
				
				ApplicationLogManager.getApplicationLogger().write(logInformation);
			}
			
			return lowerBoundPoint;
		}
		
	}
	
	
	public long receiveCommitPoint() throws RemoteException {
		
		synchronized (this.lockReceiveCommitPoint) {
			
			VolatileLog volatileLog = harpFileSystem.getVolatileLogProperty().getVolatileLog();
			
			long commitPoint = volatileLog.getCommitPoint();
			
			String logInformation = "[" + harpFileSystem.getOwnerServer().getFileServerType().toString() + "] " + harpFileSystem.getOwnerServer().getServerName() + " sends CP to primary server. CP: " + commitPoint;
			
			System.out.println(logInformation);
			
			ApplicationLogManager.getApplicationLogger().write(logInformation);
			
			return commitPoint;
		}
		
	}
	

	public synchronized HarpFileSystem getHarpFileSystem() {
		return harpFileSystem;
	}

	public void setHarpFileSystem(HarpFileSystem harpFileSystem) {
		this.harpFileSystem = harpFileSystem;
	}

	public synchronized ServerGroup getServerGroup() {
		return serverGroup;
	}

	public void setServerGroup(ServerGroup serverGroup) {
		this.serverGroup = serverGroup;
	}

}
