package com.distributed.election.application.logger;

public class ApplicationLogManager {

	private static ApplicationLogger applicationLogger;
	
	public synchronized static ApplicationLogger getApplicationLogger() {
		
		if(applicationLogger == null) {
			applicationLogger = new ApplicationLogger("applicationlogs.txt");
			applicationLogger.open();
		}
		
		return applicationLogger;
	}
	
}
