package com.distributed.harp.fileoperations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.distributed.election.application.logger.ApplicationLogManager;

public class FileProcessor {

	private File file;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private String filePath;
	private boolean opened;
	private String newLineCharacter = System.getProperty("line.separator");
	
	public FileProcessor(String fileName) {
		this.filePath = "C:\\Distributed Systems\\log\\" + fileName;
		this.setOpened(false);
	}
	
	public synchronized void write(String data) throws IOException {

		this.bufferedWriter.write(data + newLineCharacter);
		this.bufferedWriter.flush();
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
			
			ApplicationLogManager.getApplicationLogger().write("File Writer is opened!");
			
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
			
//			System.out.println("File Writer is closed!");
			ApplicationLogManager.getApplicationLogger().write("File Writer is closed!");
			
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
