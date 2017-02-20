package com.distributed.requestgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.distributed.failuregenerator.FailureGenerator;

public class RequestGenerator {

	private int requestMaxLimit;
	private List<Request> requests;
	private boolean writeOfflineRequestDataSet;
	private RequestRecordSetWriter requestRecordSetWriter;
	private RequestRecordSetReader requestRecordSetReader;
	
	private String requestOfflineDataFileName = "RequestOfflineDataSet.txt";
	
	public RequestGenerator(boolean writeOfflineRequestDataSet) {
		this.setRequestMaxLimit(1000);
		this.setWriteOfflineRequestDataSet(writeOfflineRequestDataSet);
		this.setRequests(new ArrayList<Request>(this.getRequestMaxLimit()));
		
		if(writeOfflineRequestDataSet) {
			this.setRequestRecordSetWriter(new RequestRecordSetWriter(this.requestOfflineDataFileName));
			this.getRequestRecordSetWriter().open();
		}
		
		this.setRequestRecordSetReader(new RequestRecordSetReader(this.requestOfflineDataFileName));
	}
	
	public RequestGenerator(int requestMaxLimit, boolean writeOfflineRequestDataSet) {
		this.setRequestMaxLimit(requestMaxLimit);
		this.setWriteOfflineRequestDataSet(writeOfflineRequestDataSet);
		this.setRequests(new ArrayList<Request>(this.getRequestMaxLimit()));
		
		if(writeOfflineRequestDataSet) {
			this.setRequestRecordSetWriter(new RequestRecordSetWriter(this.requestOfflineDataFileName));
			this.getRequestRecordSetWriter().open();
		}
		
		this.setRequestRecordSetReader(new RequestRecordSetReader(this.requestOfflineDataFileName));
	}
	
	public void createRequests(boolean loadOfflineRequestDataSet) {
		
		if(loadOfflineRequestDataSet) {
			
			List<String> offlineRequestRecords = this.getRequestRecordSetReader().read();
			
			int recordCounter = 0;
			
			for(String offlineRequestRecord : offlineRequestRecords) {
				
				String[] requestRawDataItem = offlineRequestRecord.split("-");
				
				char data = String.valueOf(requestRawDataItem[0]).charAt(0);
				double failureProbabilityForRequest = Double.valueOf(requestRawDataItem[1]);
				
				Request request = new Request(data, failureProbabilityForRequest);
				this.getRequests().add(request);
				
				recordCounter++;
				
				if(this.getRequestMaxLimit() <= recordCounter) {
					break;
				}
			}
			
		}
		else {
			
			String newLineCharacter = System.getProperty("line.separator");

			for(int i=0; i < this.getRequestMaxLimit(); i++) {
				
				double failureProbabilityForRequest = FailureGenerator.prepareFailureProbability();
				
				char data = RandomStringUtils.randomAlphabetic(100).charAt(0);
				
				Request request = new Request(data, failureProbabilityForRequest);
				this.getRequests().add(request);
				
				if(this.isWriteOfflineRequestDataSet()) {
					try {
						this.getRequestRecordSetWriter().write(data + "-" + failureProbabilityForRequest + newLineCharacter);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			if(this.isWriteOfflineRequestDataSet()) {
				this.getRequestRecordSetWriter().close();
			}
		}
		
	}
	
	public void printRequests() {
		
		for(int i=0; i < this.getRequestMaxLimit(); i++) {
			System.out.println(this.getRequests().get(i));
		}
	}

	public List<Request> getRequests() {
		return requests;
	}

	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}

	public int getRequestMaxLimit() {
		return requestMaxLimit;
	}

	public void setRequestMaxLimit(int requestMaxLimit) {
		this.requestMaxLimit = requestMaxLimit;
	}
	
	public RequestRecordSetWriter getRequestRecordSetWriter() {
		return requestRecordSetWriter;
	}

	public void setRequestRecordSetWriter(RequestRecordSetWriter requestRecordSetWriter) {
		this.requestRecordSetWriter = requestRecordSetWriter;
	}

	public boolean isWriteOfflineRequestDataSet() {
		return writeOfflineRequestDataSet;
	}

	public void setWriteOfflineRequestDataSet(boolean writeOfflineRequestDataSet) {
		this.writeOfflineRequestDataSet = writeOfflineRequestDataSet;
	}

	public RequestRecordSetReader getRequestRecordSetReader() {
		return requestRecordSetReader;
	}

	public void setRequestRecordSetReader(RequestRecordSetReader requestRecordSetReader) {
		this.requestRecordSetReader = requestRecordSetReader;
	}

	@Override
	public String toString() {
		return "RequestGenerator [requestMaxLimit=" + requestMaxLimit + ", requests=" + requests + "]";
	}
	
}
