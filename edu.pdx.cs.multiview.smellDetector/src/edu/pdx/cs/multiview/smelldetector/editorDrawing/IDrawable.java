package edu.pdx.cs.multiview.smelldetector.editorDrawing;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public interface IDrawable{		
	public void draw(GC gc, Rectangle drawArea);
}