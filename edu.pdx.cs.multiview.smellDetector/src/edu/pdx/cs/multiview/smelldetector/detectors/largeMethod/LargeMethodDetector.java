package edu.pdx.cs.multiview.smelldetector.detectors.largeMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class LargeMethodDetector extends SmellDetector<LargeMethodInstance>{
	
	public LargeMethodDetector(Flower f) {
		super(f);
	}

	@Override
	public double obviousness() {	return 0.95;		}
	
	@Override
	public LargeMethodInstance calculateComplexity(List<IMethod> visibleMethods) {
		
		LargeMethodInstance inst = new LargeMethodInstance();
		
		for(IMethod m : visibleMethods){
			inst.put(m,inst.sizeOf(m));
		}
		
		return inst;
	}

	@Override
	public String getName() {
		return "Large Method";
	}
	
	public void showDetails() {		
		new LargeMethodExplanationWindow(currentSmell(), sourceViewer());
	}
}

class LargeMethodInstance implements SmellInstance{

	private Map<IMethod, Integer> methodsToSizes = new HashMap<IMethod, Integer>();
	private List<Entry<IMethod, Integer>> sortedPairsCache;
	
	
	/**
	 * 	@return	methodsToSizes as a list sorted by method size
	 */
	public List<Entry<IMethod, Integer>> sortedPairs() {
		
		if(sortedPairsCache==null){
			sortedPairsCache = new ArrayList<Entry<IMethod,Integer>>(methodsToSizes.entrySet());
			Collections.sort(sortedPairsCache, new Comparator<Entry<IMethod, Integer>>(){
				public int compare(Entry<IMethod, Integer> o1, Entry<IMethod, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}				
			});
		}
		return sortedPairsCache;
	}

	public void put(IMethod m, int i) {
		methodsToSizes.put(m,i);
		sortedPairsCache = null;
	}

	public int size() {
		return methodsToSizes.size();
	}
	
	public int shortestLength() {
		
		if(size()==0)
			return -1;
		
		return sortedPairs().get(size()-1).getValue();
	}

	public int longestLength() {
		
		if(size()==0)
			return -1;
		
		return sortedPairs().get(0).getValue();
	}

	public int lengthOf(IMethod m) {
		return methodsToSizes.get(m);
	}

	public double magnitude() {
		double maxSeverity = 0;
		for(IMethod m : methodsToSizes.keySet()){
			maxSeverity = Math.max(sizeOf(m)-100,maxSeverity);
		}
		
		return maxSeverity / 3000;
	}

	public int sizeOf(IMethod m) {
		try {
			//might be better if we could the number of 
			//	lines or semicolons
			return m.getSourceRange().getLength();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
