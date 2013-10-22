package edu.pdx.cs.multiview.smelldetector.editorDrawing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class StyledTextBackgroundDrawer {

	private Map<IDrawable,ResizeListener> resizeListeners = 
		new HashMap<IDrawable, ResizeListener>();
	private Composite canvas;
	private Image image;
	
	public void drawFigureOn(final Composite c, final IDrawable d) {
		
		this.canvas = c;
		
		c.getParent().setBackgroundMode(SWT.INHERIT_FORCE);
		
		addResizeListener(d,c);		
		draw(d);		
	}
	
	private void draw(IDrawable d) {
		
		initImage();
		Rectangle r = canvas.getClientArea();
		r.x += canvas.getBounds().x;
		r.y += canvas.getBounds().y;
		
		GC gc = new GC(image);
		d.draw(gc, r);
		gc.dispose();
		
		canvas.getParent().setBackgroundImage(image);
	}

	private void initImage() {
		if(image!=null)
			image.dispose();
		
		Point p = bottomRight(canvas,true);
		image = new Image(canvas.getDisplay(),
				new Rectangle(0,0,p.x,p.y));
	}
	

	public static Point bottomRight(Composite c, boolean includeTrim){
		
		Rectangle textArea = c.getClientArea();
		
		Point p = new Point(textArea.width,textArea.height); 
		if(includeTrim){
			p.x += c.getLocation().x;
			p.y += c.getLocation().y;
		}
		return p;
	}
	
	public void dispose(){
		//make background opaque and dispose the image
		
		for(ResizeListener l : resizeListeners.values()){
			try {l.dispose();} catch (RuntimeException _) {}
		}
		resizeListeners.clear();
		
		try{canvas.getParent().setBackgroundMode(SWT.INHERIT_NONE);
			canvas.getParent().setBackgroundImage(null);
			}catch(Exception _){}
		
		try{image.dispose();
			image = null;
			}catch(Exception _){}
	}
	
	private class ResizeListener implements Listener {
		private final IDrawable d;
		private Composite c;
		
		private ResizeListener(IDrawable d, Composite c) {
			this.d = d;
			this.c = c;
			c.addListener(SWT.Resize,this);
		}
		
		public void dispose(){
			c.removeListener(SWT.Resize,this);
		}

		public void handleEvent(Event event) {
			draw(d);	
		}
	}
	protected void addResizeListener(IDrawable d, Composite c){
		if(!resizeListeners.containsKey(d)){			
			ResizeListener listener = new ResizeListener(d,c);
			c.addListener(SWT.Resize,listener);
			resizeListeners.put(d, listener);
		}
	}
	
	public static void main(String[] args){
		
		//set up
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		//put a text box on top
		StyledText styledText = new StyledText(shell,
		        //SWT.NONE
		        SWT.V_SCROLL
		);
		

		shell.open();
		
		try {
			new StyledTextBackgroundDrawer().drawFigureOn(styledText,new DefaultDrawer());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		while (!shell.isDisposed()) {
		    if (!display.readAndDispatch())
		        display.sleep();
		    }
		display.dispose(); 
	}
}
