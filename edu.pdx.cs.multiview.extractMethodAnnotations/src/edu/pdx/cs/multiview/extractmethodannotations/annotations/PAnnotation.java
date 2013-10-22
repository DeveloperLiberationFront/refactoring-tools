package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import java.util.Collection;

import org.eclipse.jface.text.Position;

import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;

/**
 * I represent an annotation and a position.
 * 
 * @author emerson
 */
public class PAnnotation <A extends ISelfDrawingAnnotation>{

	public final A annotation;
	public final Position position;
	
	private PAnnotation(A annotation, Position position){
		
		if(annotation==null)
			throw new IllegalArgumentException();
		
		this.annotation = annotation;
		this.position = position;
	}
	
	public static <A extends ISelfDrawingAnnotation> PAnnotation<A> create(A annotation, Position position){
		return new PAnnotation<A>(annotation,position);
	}

	public static ISelfDrawingAnnotation[] toArray(Collection<PAnnotation> annotations) {
		ISelfDrawingAnnotation[] annArray = new ISelfDrawingAnnotation[annotations.size()];
		
		int i = 0;
		for(PAnnotation a : annotations)
			annArray[i++] = a.annotation;
		return annArray;
	}
}
