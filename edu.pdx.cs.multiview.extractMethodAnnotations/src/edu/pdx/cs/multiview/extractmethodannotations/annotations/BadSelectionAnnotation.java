package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import edu.pdx.cs.multiview.jface.annotation.ControlAnnotation;

public class BadSelectionAnnotation extends ControlAnnotation{
	
	private static BadSelectionAnnotation annotation;
	private String message;
	
	private BadSelectionAnnotation(String message){
		this.message = message; 
	}

	@Override
	protected void disposeControl(Control l) {
		l.dispose();
	}

	@Override
	protected Control initControl() {
		Composite s = new Composite(textWidget,SWT.TOOL | SWT.NO_TRIM | SWT.SHADOW_NONE);

		s.setLayout(new RowLayout());
	
		s.setBackground(new Color(null,255,100,100));
		Label l = new Label(s,SWT.HORIZONTAL);
		l.setText("< " + breakUp(message));
		l.setBackground(new Color(null,255,255,255));
		
		s.pack();
		
		return s;
	}
	
	public static String breakUp(String s) {
		
		StringBuffer b = new StringBuffer();
		String[] subStrings = s.split(" ");
		
		int inc = 20;
		int lengthOfThisLine = 0;
		for(int i = 0; i<subStrings.length; i++){
			if(lengthOfThisLine > inc){
				lengthOfThisLine = 0;
				b.append("\r   ");
			}
		
			b.append(subStrings[i]+" ");
			lengthOfThisLine += subStrings[i].length();
		}
			
		return b.toString().trim();
	}
	
	public static PAnnotation<BadSelectionAnnotation> 
						newAnnotationFor(ITextSelection selection, String message) {
		
		annotation = new BadSelectionAnnotation(message);
		Position p = new Position(selection.getOffset()+selection.getLength(),1);
		
		return PAnnotation.create(annotation,p);
	}

	public void draw(GC gc, StyledText textWidget, int offset, int length) {
		Point loc = textWidget.getLocationAtOffset(offset);
		loc.x = textWidget.getClientArea().width;
		
		draw(textWidget, loc, offset, length);
		
		if(!annotation.isActive())
			annotation.activate();
	}
}
