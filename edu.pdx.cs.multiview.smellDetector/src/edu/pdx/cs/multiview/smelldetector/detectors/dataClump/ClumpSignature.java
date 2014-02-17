package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class ClumpSignature implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2852374595513404071L;
	
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

	public static List<ClumpSignature> from(String[] names) {
		LinkedList<String> strings = new LinkedList<String>();
		for(String name : names){
			strings.add(name);
		}
		if(names.length < 2){
			List<ClumpSignature> sigs = new ArrayList<ClumpSignature>(1);
			if(names.length == 1)
				sigs.add(new ClumpSignature(strings));
			return sigs;
		}
		
		return combination(strings);
	}
	
    // print all subsets of the characters in s
    public static List<ClumpSignature> combination(List<String> strings) { 
    	return combination(strings.subList(0, 0),strings); 
    }
 
    private static List<ClumpSignature> combination(List<String> prefix, List<String> rest) {
    	List<ClumpSignature> sigs = new LinkedList<ClumpSignature>();
    	if (rest.size() > 0) {
    		List<String> newPrefix = new LinkedList<String>(prefix);
    		newPrefix.add(rest.get(0));
    		if(newPrefix.size()>1){//we're not interested in prefixes of length 1	    		
	    		sigs.add(new ClumpSignature(newPrefix));
    		}
            sigs.addAll(combination(newPrefix, rest.subList(1, rest.size())));
            sigs.addAll(combination(prefix,    rest.subList(1, rest.size())));
        }
    	return sigs;
    }  

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		ClumpSignature other = (ClumpSignature) obj;
		if (names == null) {
			if (other.names != null)
				return false;
		} else if (!names.equals(other.names))
			return false;
		
		if (signature != other.signature)
			return false;
		
		if(other.names.size() != names.size())
			return false;
		
		for(String name: other.names){
			if(!names.contains(name)){
				return false;
			}
		}
		
		return true;
	}
	
	
	
	@Override
	public String toString(){
		return names.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return signature;
	}

	public boolean contains(String identifier) {
		return names.contains(identifier);
	}

	public int size() {
		return names.size();
	}
}