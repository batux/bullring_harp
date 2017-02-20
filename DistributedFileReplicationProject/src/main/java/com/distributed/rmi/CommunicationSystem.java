package com.distributed.rmi;

import com.distributed.rmi.client.CommunicationClient;
import com.distributed.rmi.server.CommunicationServer;

public class CommunicationSystem {

	private CommunicationClient client;
	private CommunicationServer server;
	
	public CommunicationSystem(CommunicationClient client, CommunicationServer server) {
		
		this.setClient(client);
		this.setServer(server);
	}
	
	public CommunicationClient getClient() {
		return client;
	}
	public void setClient(CommunicationClient client) {
		this.client = client;
	}
	public CommunicationServer getServer() {
		return server;
	}
	public void setServer(CommunicationServer server) {
		this.server = server;
	}
	
}
