package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.eclipse.jface.text.Position;

import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;

public class AnnotationCollection {

	private PAnnotation<BadSelectionAnnotation> badSelectionAnn;
	private Bag<PAnnotation> annotations;
	
	public AnnotationCollection(){
		annotations = new HashBag<PAnnotation>();
	}
	
	public void setControlFlowAnns(ControlFlowAnnotationCollection controlFlowAnns) {
		annotations.addAll(controlFlowAnns.getAnnotations());
	}
	
	public void setVariableAnns(VariableAnnotationCollection variableAnns) {
		annotations.addAll(variableAnns.getAnnotations());
	}

	public void setBadSelectionAnn(PAnnotation<BadSelectionAnnotation> a) {
		badSelectionAnn = a;
	}
	
	public void setSelectionAnn(PAnnotation<SelectionAnnotation> anns) {
		annotations.add(anns);
	}
	
	public Collection<PAnnotation> getAnnotations(){
		Bag<PAnnotation> b = new HashBag<PAnnotation>();
		for(PAnnotation ann : annotations)
			b.add(ann);
		if(badSelectionAnn!=null)
			b.add(badSelectionAnn);
		return b;
	}

	public void markSelectionAsDeleted() {
		if(badSelectionAnn!=null)
			badSelectionAnn.annotation.dispose();
	}

	public void clear() {
		badSelectionAnn = null;
		annotations.clear();
	}

	public Map<ISelfDrawingAnnotation,Position> getAnnotationMap() {
		Map<ISelfDrawingAnnotation,Position> m = new HashMap<ISelfDrawingAnnotation,Position>();
		for(PAnnotation ann : annotations)
			m.put(ann.annotation,ann.position);
		if(badSelectionAnn!=null)
			m.put(badSelectionAnn.annotation, badSelectionAnn.position);
		return m;
	}

}
