package com.distributed.harp.fileoperations;

public class AsyncFileWriter extends Thread {

	private AsyncFileWriterRunnable asyncFileWriterRunnable;
	
	public AsyncFileWriter(AsyncFileWriterRunnable asyncFileWriterRunnable) {
		super(asyncFileWriterRunnable);
		this.setAsyncFileWriterRunnable(asyncFileWriterRunnable);
	}

	public AsyncFileWriterRunnable getAsyncFileWriterRunnable() {
		return asyncFileWriterRunnable;
	}

	public void setAsyncFileWriterRunnable(AsyncFileWriterRunnable asyncFileWriterRunnable) {
		this.asyncFileWriterRunnable = asyncFileWriterRunnable;
	}
	
}
