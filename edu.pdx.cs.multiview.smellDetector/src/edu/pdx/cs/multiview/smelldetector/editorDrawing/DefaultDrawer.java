/**
 * 
 */
package edu.pdx.cs.multiview.smelldetector.editorDrawing;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

class DefaultDrawer implements IDrawable{

	private final int size = 100;
	private final Color COLOR = new Color(null,100,255,100);
	
	public void draw(GC gc, Rectangle drawArea) {
		gc.setBackground(COLOR);			
		gc.fillRectangle(drawArea.x+drawArea.width-size, 
						drawArea.y+drawArea.height-size, 
						size, size);
	}
	
}