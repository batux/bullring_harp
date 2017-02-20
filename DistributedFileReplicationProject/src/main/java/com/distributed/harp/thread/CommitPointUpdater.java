package com.distributed.harp.thread;

import com.distributed.harp.runnable.CommitPointUpdaterRunnable;

public class CommitPointUpdater extends Thread {

	private CommitPointUpdaterRunnable commitPointUpdaterRunnable;
	
	public CommitPointUpdater(CommitPointUpdaterRunnable commitPointUpdaterRunnable) {
		super(commitPointUpdaterRunnable);
		this.setCommitPointUpdaterRunnable(commitPointUpdaterRunnable);
	}

	public CommitPointUpdaterRunnable getCommitPointUpdaterRunnable() {
		return commitPointUpdaterRunnable;
	}

	public void setCommitPointUpdaterRunnable(CommitPointUpdaterRunnable commitPointUpdaterRunnable) {
		this.commitPointUpdaterRunnable = commitPointUpdaterRunnable;
	}
}
