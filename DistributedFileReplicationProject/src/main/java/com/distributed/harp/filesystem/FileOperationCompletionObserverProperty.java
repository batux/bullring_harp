package com.distributed.harp.filesystem;

import com.distributed.harp.observerprocess.FileOperationCompletionObserver;
import com.distributed.harp.observerprocess.FileOperationCompletionObserverRunnable;
import com.distributed.serverfactory.ServerGroup;

public class FileOperationCompletionObserverProperty extends HarpPropertyDecorator {

	private FileOperationCompletionObserver fileOperationCompletionObserver;
	
	private FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable;
	
	public FileOperationCompletionObserverProperty(ServerGroup serverGroup, HarpFileSystem harpFileSystem)  {
		
		this.fileOperationCompletionObserverRunnable = new FileOperationCompletionObserverRunnable(serverGroup, harpFileSystem);
		this.fileOperationCompletionObserver = new FileOperationCompletionObserver(this.fileOperationCompletionObserverRunnable);
	}
	
	public void activateSystemProperty() {
		super.activateSystemProperty();
		this.fileOperationCompletionObserver.start();
	}

	public FileOperationCompletionObserverRunnable getFileOperationCompletionObserver() {
		return this.fileOperationCompletionObserverRunnable;
	}
}
