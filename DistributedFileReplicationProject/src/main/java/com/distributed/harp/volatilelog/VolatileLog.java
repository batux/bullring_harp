package com.distributed.harp.volatilelog;

import java.util.ArrayList;
import java.util.List;

public class VolatileLog {

	private volatile long eventRecordPoint;
	private volatile long commitPoint;
	private volatile long applicationPoint;
	private volatile long lowerBoundPoint;
	private volatile long globalLowerBoundPoint;
	
	private volatile List<EventRecord> logEventRecords;
	
	public VolatileLog() {
		
		this.setEventRecordPoint(0);
		this.setCommitPoint(0);
		this.setApplicationPoint(0);
		this.setLowerBoundPoint(0);
		this.setGlobalLowerBoundPoint(0);
		this.setLogEventRecords(new ArrayList<EventRecord>());
	}
	
	public synchronized void appendEventRecord(EventRecord eventRecord) {
		
		this.getLogEventRecords().add(eventRecord);
		
		long eventRecordPoint = this.getEventRecordPoint() + 1;
		
		this.setEventRecordPoint(eventRecordPoint);
		
	}
	
	public synchronized long getLastEventRecordIndex() {
		
		return this.getEventRecordPoint();
	}
	
	public synchronized void increaseCommitPoint() {
		
		long commitPoint = this.getCommitPoint() + 1;
		
		this.setCommitPoint(commitPoint);
	}
	
	public synchronized void increaseApplicationPoint() {
		
		long applicationPoint = this.getApplicationPoint() + 1;
		
		this.setApplicationPoint(applicationPoint);
	}
	
	public synchronized void increaseLowerBoundPoint() {
		
		long lowerBoundPoint = this.getLowerBoundPoint() + 1;
		
		this.setLowerBoundPoint(lowerBoundPoint);
	}
	
	public synchronized void increaseGlobalLowerBoundPoint() {
		
		long globalLowerBoundPoint = this.getGlobalLowerBoundPoint() + 1;
		
		this.setGlobalLowerBoundPoint(globalLowerBoundPoint);
	}
	
	public synchronized long getEventRecordPoint() {
		return eventRecordPoint;
	}
	
	public synchronized void setEventRecordPoint(long eventRecordPoint) {
		this.eventRecordPoint = eventRecordPoint;
	}
	
	public synchronized long getCommitPoint() {
		return commitPoint;
	}
	
	public synchronized void setCommitPoint(long commitPoint) {
		this.commitPoint = commitPoint;
	}
	
	public synchronized long getApplicationPoint() {
		return applicationPoint;
	}
	
	public synchronized void setApplicationPoint(long applicationPoint) {
		this.applicationPoint = applicationPoint;
	}
	
	public synchronized long getLowerBoundPoint() {
		return lowerBoundPoint;
	}
	
	public synchronized void setLowerBoundPoint(long lowerBoundPoint) {
		this.lowerBoundPoint = lowerBoundPoint;
	}
	
	public synchronized long getGlobalLowerBoundPoint() {
		return globalLowerBoundPoint;
	}
	
	public synchronized void setGlobalLowerBoundPoint(long globalLowerBoundPoint) {
		this.globalLowerBoundPoint = globalLowerBoundPoint;
	}

	public synchronized List<EventRecord> getLogEventRecords() {
		return logEventRecords;
	}

	public synchronized void setLogEventRecords(List<EventRecord> logEventRecords) {
		this.logEventRecords = logEventRecords;
	}

}
