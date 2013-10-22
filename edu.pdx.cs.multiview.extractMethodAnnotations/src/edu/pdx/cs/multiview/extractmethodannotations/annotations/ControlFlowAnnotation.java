package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.pdx.cs.multiview.extractmethodannotations.ast.ControlFlowStatement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.LocalCFStatement;
import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;
import edu.pdx.cs.multiview.util.editor.AnnotationUtils;

public abstract class ControlFlowAnnotation extends Annotation 
											implements IControlFlowAnnotation, 
														ISelfDrawingAnnotation{
	

	private LineType lineType = LineType.manhattan;
	
	private static enum LineType{
		connector,manhattan,vector
	}
	
	public void addTo(ControlFlowAnnotationCollection cfas, ControlFlowStatement s){
		addTo(cfas,s,((LocalCFStatement)s).target);
	}
	

	protected abstract void addTo(ControlFlowAnnotationCollection cfas, ControlFlowStatement s, Statement target);
	protected abstract Position getSourceRange(int offset, int length);
	protected abstract Position getTargetRange(int offset, int length);
	
	public void draw(GC gc, StyledText textWidget, int offset, int length){
		Position sourceRange = getSourceRange(offset,length);
		Position targetRange = getTargetRange(offset,length);
		
		Rectangle sourceRec = AnnotationUtils.drawOutline(gc, textWidget, sourceRange.getOffset(), 
														sourceRange.getLength());
		
		Rectangle targetRec = AnnotationUtils.drawOutline(gc, textWidget, targetRange.getOffset(), 
													targetRange.getLength());
		
		Point endPoint = AnnotationUtils.anchorOf(SWT.CENTER | SWT.LEFT, sourceRec);
		int startPointFlag = sourceRange.getOffset() < targetRange.getOffset() ? SWT.TOP : SWT.BOTTOM;
		Point startPoint = AnnotationUtils.anchorOf(SWT.CENTER | startPointFlag, targetRec);
		
		gc.setForeground(new Color(null,0,0,0));
		gc.setLineWidth(2);
		
		if(lineType==LineType.manhattan)
			drawManhattanLine(gc, startPoint, endPoint);
		else
			drawStraightLine(gc, endPoint, startPoint);
	}
	
	private void drawStraightLine(GC gc, Point endPoint, Point startPoint) {
		gc.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		AnnotationUtils.drawTacha(gc, startPoint, endPoint);
	}
	
	private void drawManhattanLine(GC gc, Point startPoint, Point endPoint) {
		
		int[] points = new int[] {	startPoint.x,	endPoint.y,
									endPoint.x,		endPoint.y
								 };
		
		gc.drawPolyline(points);
		AnnotationUtils.drawTacha(gc, endPoint, new Point(startPoint.x,endPoint.y));
		DrawArrow.drawArrow(gc, startPoint.x,	endPoint.y, startPoint.x,	startPoint.y);
	}
}
