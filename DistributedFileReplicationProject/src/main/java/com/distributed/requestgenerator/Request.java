package com.distributed.requestgenerator;

import java.io.Serializable;

public class Request implements Serializable{

	private static final long serialVersionUID = -8349520280972407024L;
	private char data;
	private double failProbability;
	private boolean skip;
	
	public Request() {
		this.setData(' ');
		this.setSkip(false);
		this.setFailProbability(0.0);
	}
	
	public Request(char data, double failProbability) {
		this.setData(data);
		this.setSkip(false);
		this.setFailProbability(failProbability);
	}

	public char getData() {
		return data;
	}

	public void setData(char data) {
		this.data = data;
	}

	public double getFailProbability() {
		return failProbability;
	}

	public void setFailProbability(double failProbability) {
		this.failProbability = failProbability;
	}

	@Override
	public String toString() {
		return "Request [data=" + data + ", failProbability=" + failProbability + "]";
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	
}
