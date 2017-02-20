package com.distributed.election;

import com.distributed.requestgenerator.Request;

public class ElectionController {

	private double successPossibility;
	
	public ElectionController(double successPossibility) {
		this.setSuccessPossibility(successPossibility);
	}
	
	public boolean checkFailure(Request request) {
		
		double failProbability = request.getFailProbability();
		
		if(failProbability > this.getSuccessPossibility()) {
			return true;
		}
		
		return false;
	}

	public double getSuccessPossibility() {
		return successPossibility;
	}

	public void setSuccessPossibility(double successPossibility) {
		this.successPossibility = successPossibility;
	}
	
}
