package com.distributed.harp.filesystem;

import com.distributed.harp.applyprocess.ApplyProcess;
import com.distributed.harp.applyprocess.ApplyProcessRunnable;
import com.distributed.harp.fileoperations.FileProcessor;
import com.distributed.harp.observerprocess.FileOperationCompletionObserverRunnable;

public class ApplyProcessProperty extends HarpPropertyDecorator {

	private ApplyProcess applyProcess;
	
	private ApplyProcessRunnable applyProcessRunnable;
	
	public ApplyProcessProperty(FileProcessor fileProcessor, HarpFileSystem harpFileSystem, FileOperationCompletionObserverRunnable fileOperationCompletionObserverRunnable) {
		
		this.applyProcessRunnable = new ApplyProcessRunnable(fileProcessor, harpFileSystem, fileOperationCompletionObserverRunnable);
		this.applyProcess = new ApplyProcess(this.applyProcessRunnable);
	}
	
	public void activateSystemProperty() {
		super.activateSystemProperty();
		this.applyProcess.start();
	}

	public ApplyProcessRunnable getApplyProcess() {
		return this.applyProcessRunnable;
	}
}
