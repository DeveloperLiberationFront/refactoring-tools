package edu.pdx.cs.multiview.smelldetector.detectors.largeClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class LargeClassDetector extends SmellDetector<LargeClassInstance>{

	private LargeClassCollector collector = new LargeClassCollector();
	
	public LargeClassDetector(Flower f) {	super(f);	}

	@Override
	public double obviousness() {
		return 0.97;
	}
	
	@Override
	public LargeClassInstance calculateComplexity(List<IMethod> visibleMethods) {
		
		Collection<IType> visibleTypes = typesFrom(visibleMethods);
		for(IType t : visibleTypes){
			try {
				collector.add(t);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		return new LargeClassInstance(collector,visibleTypes);
	}
	
	private Collection<IType> typesFrom(List<IMethod> visibleMethods) {
		
		Set<IType> types = new HashSet<IType>();
		for(IMethod m : visibleMethods){
			types.add(m.getDeclaringType());
		}
		
		return types;
	}

	@Override
	public void showDetails() {
		
	}
	
	@Override
	public String getName() {
		return "Large Class";
	}
}

class LargeClassCollector{

	private Map<IType, Integer> sizes = 
		new HashMap<IType, Integer>();
	
	public void add(IType t) throws JavaModelException{
		if(!sizes.containsKey(t)){
			int size = t.getSourceRange().getLength();
			size = Math.max(size-500,0);
			sizes.put(t,size);
		}
	}

	public Integer get(IType t) {
		return sizes.get(t);
	}	
}

class LargeClassInstance implements SmellInstance{
	
	private LargeClassCollector collector;
	private Collection<IType> types;
	
	public LargeClassInstance(LargeClassCollector collector, Collection<IType> types) {
		this.collector = collector;
		this.types = types;
	}

	public double magnitude() {
		double severity = 0;
		for (Integer rating : visibleRatings()) {
			severity += rating - 500;
		}
		return severity / 20000;
	}

	private List<Integer> visibleRatings() {
		
		List<Integer> ratings = new ArrayList<Integer>();
		for(IType t : types)
			ratings.add(collector.get(t));
		
		return ratings;
	}
}