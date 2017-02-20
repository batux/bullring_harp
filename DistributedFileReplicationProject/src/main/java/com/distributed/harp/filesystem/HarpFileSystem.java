package com.distributed.harp.filesystem;

import com.distributed.election.application.logger.ApplicationLogManager;
import com.distributed.fileserver.FileServer;
import com.distributed.harp.fileoperations.FileProcessor;
import com.distributed.serverfactory.ServerGroup;

public class HarpFileSystem {
	
	private ServerGroup serverGroup;
	
//	private FileServer primaryServer;
	
	private FileServer ownerServer;

	private FileProcessor fileProcessor;
	
	private VolatileLogProperty volatileLogProperty;
	
	private ApplyProcessProperty applyProcessProperty;
	
	private FileOperationCompletionObserverProperty fileOperationCompletionObserverProperty;
	
	public HarpFileSystem(String fileName, FileServer ownerServer, ServerGroup serverGroup) {
		
		this.serverGroup = serverGroup;
		this.setOwnerServer(ownerServer);
//		this.primaryServer = this.serverGroup.getPrimaryServer();
		this.volatileLogProperty = new VolatileLogProperty();
		
		if(fileName.length() > 0) {
			this.fileProcessor = new FileProcessor(fileName);
			this.fileProcessor.open();
		}
		
		String logInformation = this.getOwnerServer().getServerName() + "'s Harp File System was created.";
//		System.out.println(logInformation);
		ApplicationLogManager.getApplicationLogger().write(logInformation);
	}
	
	public void enableFileOperationProperties() {
		
		if(this.getFileProcessor() != null && !this.getFileProcessor().isOpened()) {
			this.getFileProcessor().open();
		}
		
		this.fileOperationCompletionObserverProperty = new FileOperationCompletionObserverProperty(this.serverGroup, this);
		this.fileOperationCompletionObserverProperty.setHarpFileSystemProperty(this.volatileLogProperty);
		
		this.applyProcessProperty = new ApplyProcessProperty(this.fileProcessor, this, this.fileOperationCompletionObserverProperty.getFileOperationCompletionObserver());
		this.applyProcessProperty.setHarpFileSystemProperty(fileOperationCompletionObserverProperty);
		this.applyProcessProperty.activateSystemProperty();
	}
	
	public void pauseAllThreads() {
		
		if(this.getFileProcessor().isOpened()) {
			this.getFileProcessor().close();
		}
		
		// Pause Apply Thread!
		this.applyProcessProperty.getApplyProcess().pause();
	}
	
	public void resumeAllThreads() {
		
		if(!this.getFileProcessor().isOpened()) {
			this.getFileProcessor().open();
		}
		
		// Resume Apply Thread!
		this.applyProcessProperty.getApplyProcess().resume();
		
	}
	
	public void disableFileOperationProperties() {
		
		if(this.getFileProcessor() != null && this.getFileProcessor().isOpened()) {
			this.getFileProcessor().close();
		}
		
		this.applyProcessProperty.getApplyProcess().stop();
		this.fileOperationCompletionObserverProperty.getFileOperationCompletionObserver().stop();
		
		this.applyProcessProperty = null;
		this.fileOperationCompletionObserverProperty = null;
	}
	
	public void startHarpFileSystem() {
		this.fileProcessor.open();
	}
	
	public void stopHarpFileSystem() {
		this.fileProcessor.close();
	}

	public FileProcessor getFileProcessor() {
		return fileProcessor;
	}

	public void setFileProcessor(FileProcessor fileProcessor) {
		this.fileProcessor = fileProcessor;
	}

	public VolatileLogProperty getVolatileLogProperty() {
		return volatileLogProperty;
	}

	public void setVolatileLogProperty(VolatileLogProperty volatileLogProperty) {
		this.volatileLogProperty = volatileLogProperty;
	}

	public FileServer getOwnerServer() {
		return ownerServer;
	}

	public void setOwnerServer(FileServer ownerServer) {
		this.ownerServer = ownerServer;
	}
	
	public ApplyProcessProperty getApplyProcessProperty() {
		return applyProcessProperty;
	}

	public void setApplyProcessProperty(ApplyProcessProperty applyProcessProperty) {
		this.applyProcessProperty = applyProcessProperty;
	}
	
	public FileOperationCompletionObserverProperty getFileOperationCompletionObserverProperty() {
		return fileOperationCompletionObserverProperty;
	}

	public void setFileOperationCompletionObserverProperty(
			FileOperationCompletionObserverProperty fileOperationCompletionObserverProperty) {
		this.fileOperationCompletionObserverProperty = fileOperationCompletionObserverProperty;
	}
}
