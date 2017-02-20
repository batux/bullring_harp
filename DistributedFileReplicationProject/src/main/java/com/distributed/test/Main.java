package com.distributed.test;

import com.distributed.election.ElectionAlgorithms;
import com.distributed.requestgenerator.RequestSender;
import com.distributed.requestgenerator.RequestSenderRunnable;
import com.distributed.serverfactory.ServerGroup;

public class Main {

	public static void main(String[] args) {
		
		ServerGroup serverGroup = new ServerGroup(12);
		serverGroup.createFileServerGroup();
		
		RequestSenderRunnable requestSenderRunnable = new RequestSenderRunnable(serverGroup, ElectionAlgorithms.BULLY);
		RequestSender requestSender = new RequestSender(requestSenderRunnable);
		requestSender.start();
	}

}
