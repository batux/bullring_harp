package com.distributed.requestgenerator;

public class RequestSender extends Thread {

	private RequestSenderRunnable requestSenderRunnable;
	
	public RequestSender(RequestSenderRunnable requestSenderRunnable) {
		super(requestSenderRunnable);
		this.setRequestSenderRunnable(requestSenderRunnable);
	}

	public RequestSenderRunnable getRequestSenderRunnable() {
		return requestSenderRunnable;
	}

	public void setRequestSenderRunnable(RequestSenderRunnable requestSenderRunnable) {
		this.requestSenderRunnable = requestSenderRunnable;
	}
	
}
