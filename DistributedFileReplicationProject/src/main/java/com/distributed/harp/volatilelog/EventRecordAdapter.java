package com.distributed.harp.volatilelog;

import java.util.Date;

import com.distributed.requestgenerator.Request;

public class EventRecordAdapter {

	public static EventRecord convertToEventRecord(long index, Request request) {
		
		EventRecord eventRecord = new EventRecord(index, request.getData(), EventRecordOperationType.WRITE, new Date());
		eventRecord.setFailureProbability(request.getFailProbability());
		return eventRecord;
	}
	
}
