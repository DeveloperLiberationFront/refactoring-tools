package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import java.util.Collection;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;

public class ControlFlowAnnotationCollection {

	private Bag<PAnnotation<BreakAnnotation>> breakAnnotations;
	private Bag<PAnnotation<ContinueAnnotation>> continueAnnotations;
	private Bag<PAnnotation<ReturnAnnotation>> returnAnnotations;
	
	private PAnnotation<FlowAnnotation> flowAnnotation;
	
	public ControlFlowAnnotationCollection(){
		breakAnnotations = new HashBag<PAnnotation<BreakAnnotation>>();
		continueAnnotations = new HashBag<PAnnotation<ContinueAnnotation>>();
		returnAnnotations = new HashBag<PAnnotation<ReturnAnnotation>>();
	}
	
	public void addBreak(PAnnotation<BreakAnnotation> b){
		breakAnnotations.add(b);
	}
	
	public void addContinue(PAnnotation<ContinueAnnotation> c){
		continueAnnotations.add(c);
	}
	
	public void addReturn(PAnnotation<ReturnAnnotation> r){
		returnAnnotations.add(r);
	}
	
	public Collection<PAnnotation> getAnnotations(){
		Collection<PAnnotation> anns = new HashBag<PAnnotation>();
		anns.addAll(breakAnnotations);
		anns.addAll(continueAnnotations);
		anns.addAll(returnAnnotations);
		if((flowAnnotation!=null))
			anns.add(flowAnnotation);
		return anns;
	}

	public void addFlowAnnotationIfNecessary(ITextSelection selection, boolean badSelection) {
		if(returnAnnotations.isEmpty() || badSelection){
			Position p = new Position(selection.getOffset(),selection.getLength());
			flowAnnotation = PAnnotation.create(new FlowAnnotation(),p);
		}
		
		if(badSelection)
			annotateReturnsAsConflicting();
	}
	
	private void annotateReturnsAsConflicting(){
		for(PAnnotation<ReturnAnnotation> returnAnn : returnAnnotations)
			returnAnn.annotation.setConflicting(true);
	}
}
