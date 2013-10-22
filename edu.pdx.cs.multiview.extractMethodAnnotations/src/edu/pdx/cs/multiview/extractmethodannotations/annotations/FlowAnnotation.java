package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;
import edu.pdx.cs.multiview.util.editor.AnnotationUtils;

public class FlowAnnotation extends Annotation implements ISelfDrawingAnnotation{
	
	public void draw(GC gc, StyledText textWidget, int offset, int length) {
		
		gc.setForeground(new Color(null, 0, 0, 0));
		gc.setLineWidth(1);
		Rectangle bounds = AnnotationUtils.getBounds(textWidget, 
													offset, 
													length);
		drawArrow(gc, bounds);
	}
	private void drawArrow(GC gc, Rectangle r) {

		int yOffset = 10;
		Point topPoint = new Point(yOffset,r.y+1); 
		Point bottomPoint = new Point(yOffset,r.y+r.height-2);
		
		DrawArrow.drawArrow(gc, topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y);
		
	}
}
