package com.distributed.harp.filesystem;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.harp.volatilelog.VolatileLog;

public class VolatileLogProperty implements HarpFileSystemProperty {

	private volatile VolatileLog volatileLog;
	
	public VolatileLogProperty() {
		this.setVolatileLog(new VolatileLog());
	}
	
	public void activateSystemProperty() {
		String logInformation = "Volatile Log was created!";
//		System.out.println(logInformation);
		ApplicationLogManager.getApplicationLogger().write(logInformation);
	}

	public VolatileLog getVolatileLog() {
		return volatileLog;
	}

	public void setVolatileLog(VolatileLog volatileLog) {
		this.volatileLog = volatileLog;
	}

}
