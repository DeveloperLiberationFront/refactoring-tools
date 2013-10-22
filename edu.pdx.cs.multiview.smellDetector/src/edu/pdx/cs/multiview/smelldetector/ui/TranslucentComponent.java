package edu.pdx.cs.multiview.smelldetector.ui;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import edu.pdx.cs.multiview.jface.annotation.Highlight;

public abstract class TranslucentComponent {
	
	public Image createIcon(Widget w, Color color){
		final Image icon = createIcon(color);			
		w.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				icon.dispose();
			}
		});
		return icon;
	}
	
	private Image createIcon(Color color) {
		Image i = new Image(Display.getCurrent(),new Rectangle(0,0,10,10));
		
		GC gc = new GC(i);
		gc.setAlpha(Highlight.ALPHA_LEVEL);
		gc.setBackground(color);
		gc.fillRectangle(gc.getClipping());
		gc.dispose();
		
		return i;
	}
}