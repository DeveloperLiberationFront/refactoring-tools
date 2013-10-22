package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;

/**
 * I am an annotation that marks a selection
 * 
 * @author emerson
 */
public class SelectionAnnotation extends Annotation implements ISelfDrawingAnnotation{

	public void draw(GC gc, StyledText textWidget, int offset, int length) {
	
		int oldLineStyle = gc.getLineStyle();
		
		gc.setForeground(new Color(null, 255, 0, 255));
		gc.setLineStyle(SWT.LINE_DOT);
		gc.setLineWidth(2);

		drawManhattanLine(gc, textWidget, textWidget.getLocationAtOffset(offset), 1);
		drawManhattanLine(gc, textWidget, textWidget.getLocationAtOffset(offset+length), -1);
		
		gc.setLineStyle(oldLineStyle);
	}

	private void drawManhattanLine(GC gc, StyledText textWidget, Point p, int nudge) {

		
		int bottom = p.y + textWidget.getLineHeight();
		
		//vertical line
		gc.drawLine(p.x+nudge, p.y, p.x+nudge, bottom);
		
		//bottom line
		gc.drawLine(0, bottom+nudge, p.x, bottom+nudge);
		
		//top line
		gc.drawLine(p.x, p.y+nudge, textWidget.getClientArea().width, p.y+nudge);
	}
	
	public static PAnnotation<SelectionAnnotation> annotationFor(ITextSelection selection) {
		return PAnnotation.create(new SelectionAnnotation(), new Position(selection.getOffset(),selection.getLength()));
	}

	
}
