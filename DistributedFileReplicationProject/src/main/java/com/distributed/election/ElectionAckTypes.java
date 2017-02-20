package com.distributed.election;

import java.io.Serializable;

public enum ElectionAckTypes implements Serializable {

	ACCEPT_ELECTION,
	REJECT_ELECTION,
	COORDINATOR;
	
}
