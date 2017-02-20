package com.distributed.requestgenerator;

import java.io.IOException;
import java.util.List;
import com.distributed.election.BullyElectionAlgorithm;
import com.distributed.election.CommitPointCollector;
import com.distributed.election.ElectionAlgorithms;
import com.distributed.election.ElectionController;
import com.distributed.election.PrimaryFileServerHolder;
import com.distributed.election.RingElectionAlgorithm;
import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.election.report.ReportLogger;
import com.distributed.fileserver.FileServer;
import com.distributed.fileserver.FileServerTypes;
import com.distributed.harp.volatilelog.VolatileLog;
import com.distributed.rmi.client.CommunicationClient;
import com.distributed.serverfactory.ServerGroup;

public class RequestSenderRunnable implements Runnable {

	private Object lockForElection;
	
	private Object lockForRequestSender;
	
	private volatile boolean pause;
	
	private volatile boolean stopThread;
	
	private volatile int requestCounter;
	
	private volatile FileServer primaryServer;
	
	private volatile ServerGroup serverGroup;
	
	private volatile ReportLogger reportLogger;
	
	private volatile RequestGenerator requestGenerator;
	
	private volatile ElectionController electionController;
	
	private volatile CommitPointCollector commitPointCollector;
	
	private volatile ElectionAlgorithms electionAlgorithms;
	
	private volatile RingElectionAlgorithm ringElectionAlgorithm;
	
	private volatile BullyElectionAlgorithm bullyElectionAlgorithm;
	
	
	public RequestSenderRunnable(ServerGroup serverGroup, ElectionAlgorithms electionAlgorithms) {
		
		this.electionController = new ElectionController(0.7);

		this.requestGenerator = new RequestGenerator(25, false);
		this.requestGenerator.createRequests(true);
		
		this.pause = false;
		this.stopThread = false;
		this.requestCounter = 0;
		this.lockForElection = new Object();
		this.lockForRequestSender = new Object();
		
		this.serverGroup = serverGroup;
		this.primaryServer = this.serverGroup.getPrimaryServer();
		
		this.electionAlgorithms = electionAlgorithms;
		
		String electionAlgorithmLogFileName = (this.electionAlgorithms == ElectionAlgorithms.BULLY) ? "BullyExperimentReport.txt" : "RingExperimentReport.txt";
		
		this.reportLogger = new ReportLogger(electionAlgorithmLogFileName);
		this.reportLogger.open();
		
		this.commitPointCollector = new CommitPointCollector(this.serverGroup);
		
		this.ringElectionAlgorithm = new RingElectionAlgorithm(this.serverGroup);
		this.bullyElectionAlgorithm = new BullyElectionAlgorithm(this.serverGroup);
	}
	
	public void run() {
		
		FileServer primaryFileServer = this.serverGroup.getPrimaryServer();
		primaryFileServer.setRequestSenderRunnable(this);
		
		List<FileServer> backupFileServers = this.serverGroup.getBackupServers();
		for(FileServer backupFileServer : backupFileServers) {
			backupFileServer.setRequestSenderRunnable(this);
		}
		
		List<FileServer> witnessFileServers = this.serverGroup.getWitnessServers();
		for(FileServer witnessFileServer : witnessFileServers) {
			witnessFileServer.setRequestSenderRunnable(this);
		}

		
		while(!this.stopThread) {
			
			synchronized (this.lockForRequestSender) {
				
				if(this.pause) {
					try {
						this.lockForRequestSender.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
				
				if(this.requestCounter >= requestGenerator.getRequests().size()) {
					this.reportLogger.close();
					break;
				}
				
				Request request = requestGenerator.getRequests().get(this.requestCounter);
				
				boolean hasFailure = this.electionController.checkFailure(request);
				
				if(hasFailure && !request.isSkip() && this.serverGroup.getBackupServers().size() > 2) {
					
					try {
						
						long electionStartTime = System.currentTimeMillis();
						
						request.setSkip(true);
						
						// Pause Request Sender
						pause();
						
						// Pause Harp File System
						pauseHarpFileSystem();
						
						
						this.commitPointCollector.prepareAllFileServersPriorityInformation();
						
						
						if(this.electionAlgorithms == ElectionAlgorithms.BULLY) {
							
//							System.out.println("Bully Election Algorithm is performing ...");
							String logInformation = "Bully Election Algorithm is performing ...";
							ApplicationLogManager.getApplicationLogger().write(logInformation);
							
							this.bullyElectionAlgorithm.prepareFileServersForBullyElection();
							
							this.bullyElectionAlgorithm.runBullyAlgorithm();
						}
						else if(this.electionAlgorithms == ElectionAlgorithms.RING) {
							
//							System.out.println("Ring Election Algorithm is performing ...");
							String logInformation = "Ring Election Algorithm is performing ...";
							ApplicationLogManager.getApplicationLogger().write(logInformation);
							
							this.ringElectionAlgorithm.prepareFileServersForRingElection();
							
							this.ringElectionAlgorithm.runRingAlgorithm();
						}
						
						synchronized (this.lockForElection) {
							this.lockForElection.wait();
						}
						
						
						FileServer newPrimaryServer = PrimaryFileServerHolder.primaryFileServer;
						newPrimaryServer.setFileServerType(FileServerTypes.PRIMARY);
						
						VolatileLog oldPrimaryServerVolatileLog = this.getPrimaryServer().getHarpFileSystem().getVolatileLogProperty().getVolatileLog();
						newPrimaryServer.getHarpFileSystem().getVolatileLogProperty().setVolatileLog(oldPrimaryServerVolatileLog);
						
						this.serverGroup.getBackupServers().remove(newPrimaryServer);
						this.serverGroup.setPrimaryServer(newPrimaryServer);
						
						FileServer previousPrimaryFileServer = this.getPrimaryServer();
						previousPrimaryFileServer.getCommunicationSystem().getServer().stopServer();
						
						this.setPrimaryServer(newPrimaryServer);
						
						long electionEndTime = System.currentTimeMillis();
						
						double totalSeconds = ((double)(electionEndTime - electionStartTime)) / ((double)1000);
						
						if(totalSeconds == 0) {
							totalSeconds = 0.001;
						}
						
						
						String newLineCharacter = System.getProperty("line.separator");
						
						this.reportLogger.write("Previous Primary Server Name: " + previousPrimaryFileServer.getServerName() + 
								", New Primary Server Name: " + newPrimaryServer.getServerName() + 
								", Election Completion Time in seconds: " + totalSeconds + newLineCharacter);
						
						// Resume Harp File System
						resumeHarpFileSystem();
						
						// Resume Request Sender
						resume();
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				else {
					CommunicationClient client = this.primaryServer.getCommunicationSystem().getClient();
					client.sendRequestToHarpFileSystem(request);
					
					try {
						Thread.sleep(120);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					this.requestCounter++;
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
	
	public synchronized void resume() {
		
		synchronized (this.lockForRequestSender) {
		
			this.pause = false;
			this.notifyAll();
		}
		
	}
	
	public synchronized void resumeRequestSender() {
		
		synchronized (this.lockForElection) {
			this.lockForElection.notifyAll();
		}
		
	}
	
	private synchronized void pauseHarpFileSystem() {
		
		FileServer primaryServer = this.serverGroup.getPrimaryServer();
		primaryServer.getHarpFileSystem().disableFileOperationProperties();
		
		List<FileServer> backupServers = this.serverGroup.getBackupServers();
		for(FileServer backupServer : backupServers) {
			backupServer.getHarpFileSystem().pauseAllThreads();
		}
		
	}
	
	private synchronized void resumeHarpFileSystem() {
		
		FileServer primaryServer = this.serverGroup.getPrimaryServer();
		primaryServer.getHarpFileSystem().resumeAllThreads();
		
		List<FileServer> backupServers = this.serverGroup.getBackupServers();
		for(FileServer backupServer : backupServers) {
			backupServer.getHarpFileSystem().resumeAllThreads();
		}
		
	}
	
	public FileServer getPrimaryServer() {
		return primaryServer;
	}

	public void setPrimaryServer(FileServer primaryServer) {
		this.primaryServer = primaryServer;
	}

}
