package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Color;

public abstract class ColorableAnnotation extends Annotation{

	private Color color;
	
	public void setColor(Color c){
		this.color = c;
	}
	
	public Color getColor(){
		return this.color;
	}

}