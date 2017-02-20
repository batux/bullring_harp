package com.distributed.harp.observerprocess;

public class FileOperationCompletionObserver extends Thread {

	private FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable;
	
	public FileOperationCompletionObserver(FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable) {
		super(fileOperationCompletionObserverRunnable);
		this.setFileOperationCompletionObserverRunnable(fileOperationCompletionObserverRunnable);
	}

	public FileOperationCompletionObserverRunnable getFileOperationCompletionObserverRunnable() {
		return fileOperationCompletionObserverRunnable;
	}

	public void setFileOperationCompletionObserverRunnable(FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable) {
		this.fileOperationCompletionObserverRunnable = fileOperationCompletionObserverRunnable;
	}
	
}
