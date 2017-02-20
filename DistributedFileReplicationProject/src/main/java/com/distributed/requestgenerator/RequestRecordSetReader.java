package com.distributed.requestgenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestRecordSetReader {

	private FileReader fileReader;
	private BufferedReader bufferedReader;
	private String filePath;
	
	public RequestRecordSetReader(String fileName) {
		this.setFilePath("C:\\Distributed Systems\\log\\" + fileName);
	}
	
	public List<String> read() {
		
		String currentLine = "";
		
		List<String> offlineRequestRecords = new ArrayList<String>();
		
		try {
			
			this.fileReader = new FileReader(this.getFilePath());
			this.bufferedReader = new BufferedReader(this.fileReader);
			
			while ((currentLine = this.bufferedReader.readLine()) != null) {
				offlineRequestRecords.add(currentLine);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return offlineRequestRecords;
	}
	
	public void close() {
		
		try {
			if(this.bufferedReader != null) {
				this.bufferedReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public FileReader getFileReader() {
		return fileReader;
	}

	public void setFileReader(FileReader fileReader) {
		this.fileReader = fileReader;
	}

	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
