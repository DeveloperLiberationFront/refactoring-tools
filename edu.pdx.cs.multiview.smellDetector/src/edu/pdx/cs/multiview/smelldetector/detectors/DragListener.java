package edu.pdx.cs.multiview.smelldetector.detectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class DragListener implements DragDetectListener,MouseListener, MouseMoveListener{
	
	private final SmellExplanationWindow window;
	private Point dragStart;
	private Control control;
	private Region region;
	
	public DragListener(SmellExplanationWindow window, Control title){
		this.window = window;
		title.addDragDetectListener(this);		
		title.addMouseListener(this);
		title.addMouseMoveListener(this);
		this.control = title;
	}
	
	public void dispose(){
		control.removeDragDetectListener(this);		
		control.removeMouseListener(this);
		control.removeMouseMoveListener(this);
		
		getRegion().dispose();
	}

	private Region getRegion() {
		if(region!=null)
			region.dispose();
		
		//this region should really be about 3 pixels,
		//	half transparent, like View drags
		region = new Region();
		Point size = window.getSize();
		int width = 2;
		region.add(0,0,size.x,width);
		region.add(0,0,width,size.y);
		region.add(size.x-width,0,width,size.y);
		region.add(0,size.y-width,size.x,width);
		
		return region;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		endDrag();
	}

	public void mouseDown(MouseEvent e) {
		endDrag();
	}

	public void mouseUp(MouseEvent e) {
		if(dragStart != null && dragRectangle.isVisible()){				
			Point loc = window.getParent().toControl(dragRectangle.getLocation());
			window.setLocationPrivate(loc.x,loc.y);
			endDrag();
		}
	}

	private void endDrag() {
		dragStart = null;
		if(dragRectangle!=null){
			dragRectangle.dispose();
			dragRectangle = null;
		}
	}
	
	private Shell dragRectangle;
	
	public void dragDetected(DragDetectEvent e) {
		dragStart = new Point(e.x,e.y);
		dragRectangle = new Shell(e.display,SWT.NO_TRIM | SWT.ON_TOP);
		dragRectangle.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		Region r = getRegion();
		dragRectangle.setBounds(r.getBounds());			
		dragRectangle.setRegion(r);
	}

	public void mouseMove(MouseEvent e) {
		if(dragRectangle!=null){
			Point upperLeft = window.getParent().toDisplay(window.getLocation());
			upperLeft.x += e.x - dragStart.x;
			upperLeft.y += e.y - dragStart.y;
			Point lowerRight = new Point(upperLeft.x+window.getBounds().width,
										 upperLeft.y+window.getBounds().height);
			if(withinEditorBounds(upperLeft) && withinEditorBounds(lowerRight)){
				dragRectangle.setLocation(upperLeft);
				dragRectangle.setVisible(true);
			}
		}
	}

	private boolean withinEditorBounds(Point loc) {
		Composite editor = this.window.getParent();			
		loc = editor.toControl(loc);
		return editor.getParent().getClientArea().contains(loc);
	}
}