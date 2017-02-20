package com.distributed.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.distributed.harp.volatilelog.EventRecord;
import com.distributed.requestgenerator.Request;

public interface CommunicationSystemPrototype extends Remote {

	public void sendCommitPoint(long commitPoint, EventRecord eventRecord) throws RemoteException;
	public void sendLowerBoundPoint(long lowerBoundPoint) throws RemoteException;
	public void sendRequestToHarpFileSystem(Request request) throws RemoteException;
	public long receiveLowerBoundPoint() throws RemoteException;
	public long receiveCommitPoint() throws RemoteException;
	
	
	public void sendInitElectionMessage(String serverName, long priorityValue) throws RemoteException;
	public void sendInitElectionResponse(String electionAckType) throws RemoteException;
	public void sendCoordinatorMessage() throws RemoteException;
	
	public void sendRingCoordinatorMessage(String coordinatorFileServerNameOfElection, boolean shouldFinishCoordinatorProcess) throws RemoteException;
	public void sendInitRingElectionMessage(String coordinatorFileServerNameOfElection, List<String> visitedServerNames) throws RemoteException;
	
//	public void sendCoordinatorMessage(ElectionAckTypes electionAckType) throws RemoteException;
}
