package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.pdx.cs.multiview.extractmethodannotations.ast.ControlFlowStatement;
import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;
import edu.pdx.cs.multiview.util.editor.AnnotationUtils;

public class ReturnAnnotation extends Annotation implements IControlFlowAnnotation, ISelfDrawingAnnotation {

	private boolean isConflicting = false;
	private int returnLength = 1;

	public void setConflicting(boolean b){
		isConflicting = b;
	}
	
	public void addTo(ControlFlowAnnotationCollection cfas, ControlFlowStatement statement) {
		this.returnLength = statement.getLength();
		cfas.addReturn(PAnnotation.create(this,
						new Position(statement.getStartPosition(),statement.getLength())));
	}

	public void draw(GC gc, StyledText textWidget, int offset, int length) {
		gc.setForeground(new Color(null,0,0,0));
		gc.setLineWidth(1);
		Rectangle r = AnnotationUtils.drawOutline(gc, 
									textWidget, 
									offset+length-returnLength,
									returnLength);
		
		drawArrow(gc, r, isConflicting);
	}
	
	private void drawArrow(GC gc, Rectangle r, boolean drawTacha) {
		
		Point leftPoint = new Point(2,r.y + r.height/2);
		Point rightPoint = new Point(r.x,leftPoint.y);
		DrawArrow.drawArrow(gc, rightPoint.x, rightPoint.y, leftPoint.x, leftPoint.y);
		
		if(drawTacha)
			AnnotationUtils.drawTacha(gc, leftPoint, rightPoint);
	}
}
