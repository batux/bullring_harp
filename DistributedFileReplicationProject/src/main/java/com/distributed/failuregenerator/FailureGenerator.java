package com.distributed.failuregenerator;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class FailureGenerator {

	private static UniformRealDistribution uniformRealDistribution = new UniformRealDistribution(0,1);
	
	public static double prepareFailureProbability() {
		return uniformRealDistribution.sample();
	}
	
}
