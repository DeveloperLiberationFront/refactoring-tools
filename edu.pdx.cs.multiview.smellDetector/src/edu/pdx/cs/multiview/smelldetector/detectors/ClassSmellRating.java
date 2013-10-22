package edu.pdx.cs.multiview.smelldetector.detectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ITypeBinding;

public abstract class ClassSmellRating <MethodRating extends MethodSmellRating<T>,T> extends CachedSmellInstance{

	public Map<IMethod, MethodRating> rs = 
		new HashMap<IMethod, MethodRating>();
	
	public void cache(IMethod m) {
		
		if(!rs.containsKey(m))
			rs.put(m,createMethodRating(m));
	}

	protected abstract MethodRating createMethodRating(IMethod m);
	
	public List<ITypeBinding> classesReferenced(){
		List<ITypeBinding> types = new LinkedList<ITypeBinding>();
		for(MethodRating er : ratings())
			types.addAll(er.classesReferenced());
		return types;
	}
	
	public List<T> items() {
		List<T> members = new LinkedList<T>();
		for(MethodRating er : ratings())
			members.addAll(er.smells());		
		return members;
	}

	protected Collection<MethodRating> ratings() {
		return rs.values();
	}
	
	public int uniqueClassesReferenced() {	
		return new HashSet<Object>(classesReferenced()).size();
	}
	
	public int uniqueItemsReferenced() {
		return new HashSet<Object>(items()).size();
	}
}
