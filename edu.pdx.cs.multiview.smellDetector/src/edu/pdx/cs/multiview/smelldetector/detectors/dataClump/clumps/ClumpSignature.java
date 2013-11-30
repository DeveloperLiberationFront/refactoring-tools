package edu.pdx.cs.multiview.smelldetector.detectors.dataClump.clumps;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClumpSignature implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362164141149052190L;
	
	private final int signature;
	private final Set<String> names;

	ClumpSignature(List<String> ns) {
		signature = getHashCodeForParameterNames(ns);
		names = new HashSet<String>(ns);
	}

	private int getHashCodeForParameterNames(List<String> ns) {
		int s = 0;
		for (String name : ns) {
			s += name.hashCode();
		}
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ClumpSignature)) {
			return false;
		}

		return ((ClumpSignature) o).signature == this.signature;
	}

	@Override
	public String toString() {
		return names.toString();
	}

	@Override
	public int hashCode() {
		return signature;
	}

}