package com.distributed.harp.applyprocess;

public class ApplyProcess extends Thread {

	private ApplyProcessRunnable applyProcessRunnable;
	
	public ApplyProcess(ApplyProcessRunnable applyProcessRunnable) {
		super(applyProcessRunnable);
		this.setApplyProcessRunnable(applyProcessRunnable);
	}

	public ApplyProcessRunnable getApplyProcessRunnable() {
		return applyProcessRunnable;
	}

	public void setApplyProcessRunnable(ApplyProcessRunnable applyProcessRunnable) {
		this.applyProcessRunnable = applyProcessRunnable;
	}
	
}
