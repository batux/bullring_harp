package com.distributed.election.application.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ApplicationLogger {

	private File file;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private String filePath;
	private boolean opened;
	private String newLineCharacter;
	
	public ApplicationLogger(String fileName) {
		this.filePath = "C:\\Distributed Systems\\log\\" + fileName;
		this.setOpened(false);
		this.newLineCharacter = System.getProperty("line.separator");
	}
	
	public synchronized void write(String data) {
		
		try {
			this.bufferedWriter.write(data + this.newLineCharacter);
			this.bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void open() {
		
		try {
			this.file = new File(this.filePath);
			
			if (!this.file.exists()) {
				this.file.createNewFile();
			}
			
			this.fileWriter = new FileWriter(this.file.getAbsolutePath(), true);
			this.bufferedWriter = new BufferedWriter(this.fileWriter);
			
			this.setOpened(true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		
		try {
			
			if(this.bufferedWriter != null) {
				this.setOpened(false);
				this.bufferedWriter.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	
}
