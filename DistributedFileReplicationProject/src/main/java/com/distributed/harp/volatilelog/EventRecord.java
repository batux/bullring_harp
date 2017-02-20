package com.distributed.harp.volatilelog;

import java.io.Serializable;
import java.util.Date;

public class EventRecord implements Serializable {

	private static final long serialVersionUID = -7657252807029662339L;
	private long index;
	private char data;
	private EventRecordOperationType operationType;
	private Date eventRecordInsertionDateTime;
	private boolean active;
	private double failureProbability;
	
	public EventRecord(long index, char data, EventRecordOperationType operationType, Date eventRecordInsertionDateTime) {
		this.setActive(true);
		this.setIndex(index);
		this.setData(data);
		this.setOperationType(operationType);
		this.setEventRecordInsertionDateTime(eventRecordInsertionDateTime);
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public char getData() {
		return data;
	}

	public void setData(char data) {
		this.data = data;
	}

	public EventRecordOperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(EventRecordOperationType operationType) {
		this.operationType = operationType;
	}

	public Date getEventRecordInsertionDateTime() {
		return eventRecordInsertionDateTime;
	}

	public void setEventRecordInsertionDateTime(Date eventRecordInsertionDateTime) {
		this.eventRecordInsertionDateTime = eventRecordInsertionDateTime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public String toString() {
		return "EventRecord [index=" + index + ", data=" + data + ", probability=" + failureProbability + ", operationType=" + operationType + ", eventRecordInsertionDateTime=" + eventRecordInsertionDateTime + "]";
	}

	@Override
	public EventRecord clone() throws CloneNotSupportedException {
		
		EventRecord clonnedEventRecord = new EventRecord(index, data, operationType, eventRecordInsertionDateTime);
		clonnedEventRecord.setActive(this.isActive());
		clonnedEventRecord.setFailureProbability(this.getFailureProbability());
		return clonnedEventRecord;
	}

	public double getFailureProbability() {
		return failureProbability;
	}

	public void setFailureProbability(double failureProbability) {
		this.failureProbability = failureProbability;
	}
}
