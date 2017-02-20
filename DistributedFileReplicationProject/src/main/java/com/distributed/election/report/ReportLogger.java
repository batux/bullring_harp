package com.distributed.election.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportLogger {
	
	private File file;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private String filePath;
	private boolean opened;
	
	public ReportLogger(String fileName) {
		this.filePath = "C:\\Distributed Systems\\log\\" + fileName;
		this.setOpened(false);
	}
	
	public void write(String data) throws IOException {
		this.bufferedWriter.write(data);
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
