package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class ClumpSignature{

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

	@Override
	public boolean equals(Object o){
		if(!(o instanceof ClumpSignature)){
			return false;
		}
		
		return ((ClumpSignature)o).signature==this.signature;
	}
	
	@Override
	public String toString(){
		return names.toString();
	}
	
	@Override
	public int hashCode(){
		return signature;
	}

	public boolean contains(String identifier) {
		return names.contains(identifier);
	}

	public int size() {
		return names.size();
	}
}