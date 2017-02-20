package com.distributed.harp.thread;

import com.distributed.harp.runnable.EventRecordAppenderRunnable;

public class EventRecordAppender extends Thread {

	private EventRecordAppenderRunnable eventRecordAppenderRunnable;
	
	public EventRecordAppender(EventRecordAppenderRunnable eventRecordAppenderRunnable) {
		super(eventRecordAppenderRunnable);
		this.setEventRecordAppenderRunnable(eventRecordAppenderRunnable);
	}

	public EventRecordAppenderRunnable getEventRecordAppenderRunnable() {
		return eventRecordAppenderRunnable;
	}

	public void setEventRecordAppenderRunnable(EventRecordAppenderRunnable eventRecordAppenderRunnable) {
		this.eventRecordAppenderRunnable = eventRecordAppenderRunnable;
	}
	
}
