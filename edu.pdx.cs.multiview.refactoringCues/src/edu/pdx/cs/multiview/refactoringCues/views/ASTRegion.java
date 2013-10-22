package edu.pdx.cs.multiview.refactoringCues.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import edu.pdx.cs.multiview.util.eclipse.EclipseHacks;
import edu.pdx.cs.multiview.util.editor.AnnotationUtils;

public class ASTRegion{
	
	public final ASTNode node;
	private WrappedEditor editor;
	
	public ASTRegion(WrappedEditor editor, ASTNode node){		
		this.editor = editor;
		this.node = node;
		
		IDocument document = 
			editor.getDocument();
		
		try {
			String nodeText = document.get(getOffset(), getLength());
			int splitIndex = nodeText.lastIndexOf('\n');
			
			annotations = new HashMap<BoxAnnotation, Position>();
			
			if(splitIndex<0){//it's a single line node
				annotations.put(new SingleBox(), new Position(getOffset(),getLength()));
			}else{
				annotations.put(new HeadBox(), new Position(getOffset(),splitIndex));
				annotations.put(new TailBox(), new Position(getOffset()+splitIndex,getLength()-splitIndex));
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		
		
	}


	public boolean overlaps(ITextSelection selection) {
		
		int otherStart = selection.getOffset();
		int otherEnd = otherStart + selection.getLength();
		return overlaps(otherStart, otherEnd);
	}

	private boolean overlaps(int otherStart, int otherEnd) {
		return	!(getEnd() < otherStart || otherEnd < getOffset());
	}
	
	private boolean overlaps(ASTRegion other) {
		int otherStart = other.getOffset();
		int otherEnd = otherStart + other.getLength();
		return overlaps(otherStart, otherEnd);
	}
	

	protected boolean containedBy(ITextSelection selection) {
		return getOffset() >= selection.getOffset() &&
				getEnd() <= selection.getOffset()+selection.getLength();
	}

	private int getEnd() {
		return getOffset()+getLength();
	}
	
	public int getOffset(){
		return node.getStartPosition();
	}
	
	public int getLength(){
		return node.getLength();	
	}

	public void toggleActivation() {
		
		if(widget!=null){			
			isSelected = !isSelected;
			widget.redraw();
		}
	}
	
	

	private boolean isSelected = false;
	private StyledText widget;
	

	private Map<BoxAnnotation,Position> annotations;
	
	public void addTo(Map<Annotation, Position> annotations) {
		annotations.putAll(this.annotations);
	}
	
	public void addTo(List<Annotation> annotations) {
		annotations.addAll(this.annotations.keySet());
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public WrappedEditor getEditor() {
		return editor;
	}

	public boolean overlapsAny(Collection<? extends ASTRegion> others) {
		for(ASTRegion other : others)
			if(this.overlaps(other))
				return true;
		return false;
	}
	
	private abstract class BoxAnnotation extends Annotation{		
		
		@Override
		public String getType(){
			return ANN_ID;
		}

		public ASTRegion getRegion(){
			return ASTRegion.this;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setWidget(StyledText w) {
			widget = w;
		}

		public boolean endsWith(String text) {
			return node.toString().replaceAll("[ \n\r]","").endsWith(text);
		}
		
		public boolean isTail(){return false;}
		public boolean startsWith(String text){return false;}
		public boolean isSingleLine() {return false;}
		
		public abstract void drawBox(String text, GC gc, Rectangle textBox);
	}
	
	class HeadBox extends BoxAnnotation{		
		public boolean startsWith(String text) {
			return node.toString().replaceAll("[ \n\r]","").startsWith(text);
		}

		@Override
		public void drawBox(String text, GC gc,
				Rectangle rec) {
			
			if(startsWith(text)){
				drawLeftVertical(gc,rec);
				drawLeftToRightMargin(gc,rec);
				drawLeftToLeftMargin(gc,rec);
			}
		}
		
		private void drawLeftVertical(GC gc, Rectangle rec) {
			gc.drawLine(rec.x1, rec.y1, rec.x1, rec.y2);
		}
		
		private void drawLeftToLeftMargin(GC gc, Rectangle rec) {
			gc.drawLine(rec.x1, rec.y2, 0, rec.y2);
		}
		
		private void drawLeftToRightMargin(GC gc, Rectangle rec) {
			gc.drawLine(rec.x1, rec.y1, widget.getBounds().width, rec.y1);
		}
	}
	
	class TailBox extends BoxAnnotation{
		public boolean isTail() {return true;}

		@Override
		public void drawBox(String text, GC gc,Rectangle rec) {

			drawRightVertical(gc,rec);
			drawRightToLeftMargin(gc,rec);
			drawRightToRightMargin(gc,rec);
		}
		
		private void drawRightToRightMargin(GC gc, Rectangle rec) {
			gc.drawLine(rec.x2, rec.y1, widget.getBounds().width, rec.y1);
		}
		
		private void drawRightToLeftMargin(GC gc, Rectangle rec) {
			gc.drawLine(rec.x2, rec.y2, 0, rec.y2);
		}
		
		private void drawRightVertical(GC gc, Rectangle rec) {
			gc.drawLine(rec.x2, rec.y1, rec.x2, rec.y2);
		}
	}
	
	class SingleBox extends BoxAnnotation{
		public boolean isSingleLine(){return true;}
		
		public boolean endsWith(String text) {
			return false;
		}

		@Override
		public void drawBox(String text, GC gc, Rectangle textBox) {
			gc.drawRectangle(textBox.r);
		}
	}

	private static final String ANN_ID = "edu.pdx.cs.multiview.refactoring.astregion";//from plugin.xml
	private static final String STRAT_ID = "edu.pdx.cs.multiview.refactoring.astregion_drawstrat";
	private static final Color GREEN = new Color(null,0,255,0);
	private static final Color RED = new Color(null,255,0,0);
	private static IDrawingStrategy DRAW_STRATEGY =  new IDrawingStrategy(){

		public void draw(Annotation annotation, GC gc,
				StyledText widget, int offset, int length, Color color) {
			
			
			
			if(gc==null){
				widget.redrawRange(offset, length, true);
				return;
			}
			
//			TODO: could use a StyledRange for background, 
			//but it doesn't paint the whole line
			//could probably hack up StyledTextRenderer to do
			//the right thing, though
//			StyleRange range = new StyleRange();
//			range.background = GREEN;
//			range.start = offset;
//			range.length = length;
//			widget.setStyleRange(range);
			
			BoxAnnotation ann = (BoxAnnotation) annotation;
			ann.setWidget(widget);
			
			String textPaintingOver; 
			if(length==0)
				textPaintingOver = ""; 
			else
				textPaintingOver = widget.getText(offset, offset+length-1).replace(" ", "");				
			
			Rectangle rec = getRectangle(widget, offset, length, ann, textPaintingOver);
			
			if(ann.isSelected()){
				gc.setAlpha(50);
				gc.setBackground(RED);
			}else{
				gc.setAlpha(20);
				gc.setBackground(GREEN);
			}
			
			
			gc.fillRectangle(rec.r);
			gc.setAlpha(100);
			
			ann.drawBox(textPaintingOver, gc, rec);
		}

		private Rectangle getRectangle(StyledText widget, int offset,
				int length, BoxAnnotation ann, String textPaintingOver) {
			
			org.eclipse.swt.graphics.Rectangle bounds = AnnotationUtils.getBounds(widget, offset, length);
			
			if(!ann.isSingleLine()){
				
				if(!ann.startsWith(textPaintingOver)){
					bounds.x = 0;
				}
				
				if(!ann.isTail()){
					bounds.width = widget.getBounds().width;;
				}
			}
			
			Rectangle rec = new Rectangle(bounds);
			return rec;
		}
		
	};
	

	
	
	/**
	 * Prepare the editor for drawing 
	 * 
	 * @param e
	 */
	public static void prep(AbstractDecoratedTextEditor e) {
		AnnotationPainter painter = EclipseHacks.getAnnotationPainter(e);
		painter.addAnnotationType(ANN_ID, STRAT_ID);
		painter.setAnnotationTypeColor(ANN_ID, GREEN);
		painter.addDrawingStrategy(STRAT_ID, DRAW_STRATEGY);
	}
}

class Rectangle{
	public Rectangle(org.eclipse.swt.graphics.Rectangle rec) {
		x1 = rec.x;
		y1 = rec.y;
		x2 = rec.x + rec.width;
		y2 = rec.y + rec.height;
		r = rec;
	}

	public final int x1,x2,y1,y2;
	public final org.eclipse.swt.graphics.Rectangle r;
}

