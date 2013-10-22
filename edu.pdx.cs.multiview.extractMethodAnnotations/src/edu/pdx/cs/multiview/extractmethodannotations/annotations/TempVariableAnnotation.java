package edu.pdx.cs.multiview.extractmethodannotations.annotations;


import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference;
import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;
import edu.pdx.cs.multiview.util.editor.AnnotationUtils;


public class TempVariableAnnotation extends ColorableAnnotation 
									implements ISelfDrawingAnnotation{

	private static Color writeBorderColor = new Color(null,0,0,0);
	
	private final boolean isWrite;
	private final String id;
	
	public static PAnnotation<TempVariableAnnotation> getAnnotation(Variable_Reference name){
		TempVariableAnnotation v = new TempVariableAnnotation(name);
		return PAnnotation.create(v,name.getSourceRange());
	}
	
	private TempVariableAnnotation(Variable_Reference name) {
		setColor(new Color(null,200,200,200));
		isWrite = name.isWrite();
		id = name.getIdentifier(); 
	}

	
	public String getIdentifier(){
		return id;
	}

	public void draw(GC gc, StyledText textWidget, int offset, int length) {
		if (gc != null) {
			
			//draw rectangle
			drawBackground(gc, textWidget, offset, length);
			
			//draw outline and text
			gc.setFont(textWidget.getFont());
			
			if(isWrite){
				gc.setLineWidth(1);
				gc.setForeground(writeBorderColor);
				Rectangle rec = AnnotationUtils.drawOutline(gc,textWidget,offset,length);
				rec.x += 2;
				rec.y += 2;
				rec.height -= 3;
				rec.width -= 3;
				gc.setLineWidth(2);
				gc.setForeground(new Color(null,255,255,255));
				gc.drawRectangle(rec);
			}
			
			drawText(gc, textWidget, offset, length);
			
		} else {
			textWidget.redrawRange(offset, length, true);
		} 
	}
	

	private void drawText(GC gc, StyledText textWidget, int start, int length) {
		gc.setForeground(new Color(null,0,0,0));
		AnnotationUtils.drawText(gc,textWidget,id,start,length);
	}

	private void drawBackground(GC gc, StyledText textWidget, int start, int length) {
		gc.setBackground(getColor());
		AnnotationUtils.fill(gc, textWidget, start, length);
	}
}
