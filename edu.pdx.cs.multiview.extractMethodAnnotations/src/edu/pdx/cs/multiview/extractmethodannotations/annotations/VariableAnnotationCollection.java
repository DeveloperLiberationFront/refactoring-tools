package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.bag.HashBag;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.extractmethodannotations.util.TempVariableWithDependencies;



/**
 * I represent a collection of annotations
 * 
 * @author emerson
 */
public class VariableAnnotationCollection{

	private Bag<PAnnotation<? extends ColorableAnnotation>> annotations;
	private Bag<PAnnotation<? extends DependencyAnnotation>> returns;
	
	private boolean hasDependencies;
	
	public VariableAnnotationCollection(){
		annotations = new HashBag<PAnnotation<? extends ColorableAnnotation>>();
		returns = new HashBag<PAnnotation<? extends DependencyAnnotation>>();
		hasDependencies = false;
	}

	public void addParameter(PAnnotation<? extends DependencyAnnotation> a) {
		annotations.add(a);
		hasDependencies = true;
	}

	public void addReturn(PAnnotation<? extends DependencyAnnotation> a) {
		returns.add(a);
		hasDependencies = true;
	}

	public void putBackwardReturn(PAnnotation<? extends DependencyAnnotation> a) {
		returns.add(a);
		hasDependencies = true;
	}

	public boolean hasDependencies() {
		return hasDependencies;
	}

	public void merge(VariableAnnotationCollection collection) {
		annotations.addAll(collection.annotations);
		returns.addAll(collection.returns);
	}

	/**
	 * Checks to see if any of my return annotations are conflicting.
	 * If so, sets them all to conflicting mode.
	 *
	 */
	public void checkForMultiReturn() {
		if(areReturnsConflicting())
			for(PAnnotation<? extends DependencyAnnotation> a : returns)
				a.annotation.setConflicting(true);
	}


	public void addVariableAnnotations(TempVariableWithDependencies var) {
		annotations.addAll(var.getAnnotations());
	}

	public Collection<PAnnotation> getAnnotations() {
		List<PAnnotation> copy = new LinkedList<PAnnotation>();
		copy.addAll(annotations);
		copy.addAll(returns);
		return copy;
	}

	private boolean areReturnsConflicting() {
		
		String annName = null;
		
		for(PAnnotation<? extends DependencyAnnotation> ann : returns){
			if(annName == null)
				annName = ann.annotation.getName();
			else if(!annName.equals(ann.annotation.getName()))
				return true;
		}
		
		return false;
	}

	public void setColor(final Color c) {
		
		Closure<PAnnotation<? extends ColorableAnnotation>> p = new Closure<PAnnotation<? extends ColorableAnnotation>>(){
			public void execute(PAnnotation<? extends ColorableAnnotation> ann) {
				ann.annotation.setColor(c);
			}};
		
		CollectionUtils.forAllDo(annotations,p);
		CollectionUtils.forAllDo(returns, p);
	}
}
