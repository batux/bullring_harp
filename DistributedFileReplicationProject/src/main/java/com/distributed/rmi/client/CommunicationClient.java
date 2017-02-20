package com.distributed.rmi.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import com.distributed.harp.volatilelog.EventRecord;
import com.distributed.requestgenerator.Request;
import com.distributed.rmi.server.CommunicationSystemPrototype;

public class CommunicationClient {

	private String rmiUri;
	
	private CommunicationSystemPrototype communicationSystem;
	
	public CommunicationClient(String rmiUri) {
		
		this.setRmiUri(rmiUri);
		
		try {
			this.communicationSystem = (CommunicationSystemPrototype) Naming.lookup(this.getRmiUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendRingCoordinatorMessage(String coordinatorFileServerNameOfElection, boolean shouldFinishCoordinatorProcess) {
		try {
			this.communicationSystem.sendRingCoordinatorMessage(coordinatorFileServerNameOfElection, shouldFinishCoordinatorProcess);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendInitRingElectionMessage(String coordinatorFileServerNameOfElection, List<String> visitedServerNames) {
		try {
			this.communicationSystem.sendInitRingElectionMessage(coordinatorFileServerNameOfElection, visitedServerNames);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendCoordinatorMessage() {
		try {
			this.communicationSystem.sendCoordinatorMessage();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendInitElectionMessage(String serverName, long priorityValue) {
		try {
			this.communicationSystem.sendInitElectionMessage(serverName, priorityValue);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendInitElectionResponse(String electionAckType) {
		try {
			this.communicationSystem.sendInitElectionResponse(electionAckType);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendCommitPoint(long commitPoint, EventRecord eventRecord) {
		try {
			this.communicationSystem.sendCommitPoint(commitPoint, eventRecord);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendLowerBoundPoint(long lowerBoundPoint) {
		try {
			this.communicationSystem.sendLowerBoundPoint(lowerBoundPoint);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendRequestToHarpFileSystem(Request request) {
		try {
			this.communicationSystem.sendRequestToHarpFileSystem(request);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized long receiveLowerBoundPoint() {
		try {
			return this.communicationSystem.receiveLowerBoundPoint();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return Long.MAX_VALUE;
	}
	
	public synchronized long receiveCommitPoint() {
		try {
			return this.communicationSystem.receiveCommitPoint();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return Long.MAX_VALUE;
	}

	public String getRmiUri() {
		return rmiUri;
	}

	public void setRmiUri(String rmiUri) {
		this.rmiUri = rmiUri;
	}
	
}
