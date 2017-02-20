package com.distributed.harp.runnable;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.harp.filesystem.HarpFileSystem;
import com.distributed.harp.volatilelog.EventRecord;
import com.distributed.harp.volatilelog.EventRecordAdapter;
import com.distributed.harp.volatilelog.VolatileLog;
import com.distributed.requestgenerator.Request;

public class EventRecordAppenderRunnable implements Runnable {

	private Request request;
	
	private HarpFileSystem harpFileSystem;
	
	public EventRecordAppenderRunnable(Request request, HarpFileSystem harpFileSystem) {
		this.request = request;
		this.setHarpFileSystem(harpFileSystem);
	}
	
	public void run() {
		
		synchronized (this) {
			
			VolatileLog volatileLog = this.harpFileSystem.getVolatileLogProperty().getVolatileLog();
			
			EventRecord eventRecord = EventRecordAdapter.convertToEventRecord(volatileLog.getLastEventRecordIndex() + 1, request);
			
			volatileLog.appendEventRecord(eventRecord);
			
			String logInformation = "[" + this.harpFileSystem.getOwnerServer().getFileServerType().toString() +"] " + this.harpFileSystem.getOwnerServer().getServerName() + " server added request to volatile log. Data: " + eventRecord.getData() + " Date: " + eventRecord.getEventRecordInsertionDateTime();
			
//			System.out.println(logInformation);
		
			ApplicationLogManager.getApplicationLogger().write(logInformation);
		}
	}

	public HarpFileSystem getHarpFileSystem() {
		return harpFileSystem;
	}

	public void setHarpFileSystem(HarpFileSystem harpFileSystem) {
		this.harpFileSystem = harpFileSystem;
	}

}
