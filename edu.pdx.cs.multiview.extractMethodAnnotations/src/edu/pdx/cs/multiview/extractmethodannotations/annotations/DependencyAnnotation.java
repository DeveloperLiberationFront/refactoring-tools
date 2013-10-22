package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference;
import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;
import edu.pdx.cs.multiview.util.editor.AnnotationUtils;

/**
 * I am an arrow that relates variables by drawing a line.
 * 
 * @author emerson
 *
 */
public abstract class DependencyAnnotation extends ColorableAnnotation
									implements ISelfDrawingAnnotation{
	
	//whether this annotation is visible
	private boolean visible = true;
	
	protected final boolean sourceBeforeTarget;
	
	//the name of the variable
	protected final String name;
	
	//true if I represent a parameter, false if I represent a return value
	public final boolean isParameter;
	
	//if this annotation is in conflict (two returns values)
	private boolean isConflicting = false;

	protected DependencyAnnotation(Variable_Reference target, Variable_Reference source, boolean isParameter) {
		this.isParameter = isParameter;
		this.name = target.getIdentifier();
		this.sourceBeforeTarget = source.before(target);
	}
	
	/**
	 * Sets whether and X should be drawn over this annotation
	 * 
	 * @param conflicting
	 */
	public void setConflicting(boolean conflicting){
		isConflicting = conflicting;
	}
	
	/**
	 * @return	the identifier of the things I connect
	 */
	public String getName() {
		return name;
	}
	

	/**
	 * Set whether this annotation should be drawn
	 * 
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		this.visible = 	isVisible;
	}
	
	public void draw(GC gc, StyledText textWidget, int offset, int length) {
		if(!visible)
			return;
		
		gc.setAntialias(SWT.ON);
		gc.setForeground(getColor());
		gc.setLineWidth(2);
		
		Point endPoint = draw2(gc,textWidget, offset, length);
		
		if(isConflicting){
			endPoint.y -= 20;
			AnnotationUtils.drawTacha(gc, endPoint);
		}
	}
	
	protected abstract Point draw2(GC gc, StyledText textWidget, int start, int length);

	public static class VectorDependencyAnnotation extends DependencyAnnotation{
		
		public static PAnnotation<VectorDependencyAnnotation> getAnnotation(Variable_Reference target, 
													Variable_Reference source, boolean isParameter, int lineTarget){
			
			VectorDependencyAnnotation ann = new VectorDependencyAnnotation(target,source,isParameter);
			
			Position p = new VectorPosition(lineTarget,isParameter,target,source);
			return PAnnotation.create(ann, p);
			
		}
		
		private VectorDependencyAnnotation(Variable_Reference target, Variable_Reference source, boolean isParameter) {
			super(target, source, isParameter);
		}
	
		@Override
		protected Point draw2(GC gc, StyledText textWidget, int offset, int length) {
			
			VectorPosition vp = new VectorPosition(offset,length);
			
			Point target = vp.targetPoint(textWidget, isParameter, name.length());
			Point source = vp.sourcePoint(textWidget, isParameter, target);
			
			//if it's a parameter dependency..
			if(isParameter)
				DrawArrow.drawArrow(gc, source.x, source.y, target.x, target.y);
			
			//else it's an output dependency...
			//if the source is before the parent...
			else if(!sourceBeforeTarget)
				DrawArrow.drawArrow(gc, target.x, target.y, source.x, source.y);
			
			//else the child is above the parent...
			else{
				//so we draw a little hooked arrow
				int stepWidth = 10;
				gc.drawLine(target.x, target.y, source.x, source.y);
				gc.drawLine(source.x, source.y, source.x+stepWidth, source.y);
				DrawArrow.drawArrow(gc, source.x+stepWidth, source.y, source.x+stepWidth, source.y-stepWidth*2);
			}
				
			return source;
		}
	}
	
	private static class VectorPosition extends Position{
		
		public VectorPosition(int start, int length){
			super(start,length);
		}
		
		public VectorPosition(int lineTarget, boolean isParameter, Variable_Reference target, Variable_Reference source){
			super();
			if(isParameter){
				setOffset(lineTarget);
				setLength(target.getEndPosition()-offset);
			}else{
				setOffset(source.getStartPosition());
				setLength(lineTarget-offset);
			}
		}
		
		public Point targetPoint(StyledText textWidget, boolean isParameter, int nameLength){
			
			int options = isParameter ? SWT.TOP | SWT.CENTER :
				  								SWT.BOTTOM | SWT.CENTER;
			
			return isParameter ?
							AnnotationUtils.anchorOf(textWidget,options,offset+length-nameLength,nameLength):
							AnnotationUtils.anchorOf(textWidget,options,offset,nameLength);
		}
		
		public Point sourcePoint(StyledText textWidget, boolean isParameter, Point startPoint){
			int endY = textWidget.getLocationAtOffset(isParameter ? offset : offset+length).y;
			Point sourcePoint = new Point(startPoint.x,endY);
			sourcePoint.y += isParameter ? -30 : 50;//place above or below lines
			
			return sourcePoint;
		}
	}
}