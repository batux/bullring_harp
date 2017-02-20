package com.distributed.election;

import java.util.Comparator;

import com.distributed.fileserver.FileServer;

public class FileServerPriorityComparator implements Comparator<FileServer> {

	public int compare(FileServer o1, FileServer o2) {
		
		if(o1.getPriorityValue() < o2.getPriorityValue()) {
			return -1;
		}
		else if(o1.getPriorityValue() == o2.getPriorityValue()) {
			return 0;
		}
		else {
			return 1;
		}
		
	}

}
