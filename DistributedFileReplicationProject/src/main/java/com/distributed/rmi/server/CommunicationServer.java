package com.distributed.rmi.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.serverfactory.ServerGroup;

public class CommunicationServer {

	private int portNo;
	
	private String rmiUri;
	
	private String hostName;
	
	private String communicationServerName;
	
	private CommunicationSystemPrototype communicationSystem;

	public CommunicationServer(ServerGroup serverGroup, String hostName, int portNo, String communicationServerName, HarpFileSystem harpFileSystem) {
		
		try {
			this.setHostName(hostName);
			this.setPortNo(portNo);
			this.setCommunicationServerName(communicationServerName);
			this.setCommunicationSystem(new CommunicationSystemImpl(serverGroup, harpFileSystem));
			
			String rmiUri = "rmi://" + this.getHostName() + ":" + this.getPortNo() + "/" + this.getCommunicationServerName();
			this.setRmiUri(rmiUri);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() {
		
		try {
			
			LocateRegistry.createRegistry(this.getPortNo());
			
			Naming.rebind(this.getRmiUri(), this.getCommunicationSystem());
			
			String logInformation = "RMI Url: " + this.getRmiUri();
//			System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void stopServer() {
		
		try {
			
			Naming.unbind(this.getRmiUri());
		
			String logInformation = "RMI Server was stopped!";
//			System.out.println(logInformation);
			ApplicationLogManager.getApplicationLogger().write(logInformation);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public CommunicationSystemPrototype getCommunicationSystem() {
		return communicationSystem;
	}

	public void setCommunicationSystem(CommunicationSystemPrototype communicationSystem) {
		this.communicationSystem = communicationSystem;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCommunicationServerName() {
		return communicationServerName;
	}

	public void setCommunicationServerName(String communicationServerName) {
		this.communicationServerName = communicationServerName;
	}

	public String getRmiUri() {
		return rmiUri;
	}

	public void setRmiUri(String rmiUri) {
		this.rmiUri = rmiUri;
	}
	
}
