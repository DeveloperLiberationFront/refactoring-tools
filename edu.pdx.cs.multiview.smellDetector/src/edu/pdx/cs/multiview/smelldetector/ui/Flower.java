package edu.pdx.cs.multiview.smelldetector.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.pdx.cs.multiview.smelldetector.EditorViewportListener;
import edu.pdx.cs.multiview.smelldetector.EditorViewportListener.ContextType;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.editorDrawing.IDrawable;
import edu.pdx.cs.multiview.smelldetector.editorDrawing.StyledTextBackgroundDrawer;

/*
 * TODO: if we can do a non-tiling image, then we can save some processing
 */
public class Flower implements IDrawable{
	
	public static final Color BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	
	private static final int radius = 10;
	private static final int maxRadius = 100;

	private StateMonitor mouseListener = new StateMonitor();
	private Composite parent;
	private List<Petal> petals = new LinkedList<Petal>();
	
	private StyledTextBackgroundDrawer drawer = new StyledTextBackgroundDrawer();

	public Point getLocation() {
		Point p = StyledTextBackgroundDrawer.bottomRight(parent,true);
		return new Point(p.x-radius,p.y/2-radius);
	}
	
	private Point getCenter(boolean includeTrim){
		Point p = StyledTextBackgroundDrawer.bottomRight(parent,includeTrim);
		return new Point(p.x,p.y/2);
	}
	
	private Rectangle boundsRelativeToBackground(){
		Point upperLeft = getCenter(false);
		return new Rectangle(upperLeft.x-maxRadius,upperLeft.y-maxRadius,maxRadius,maxRadius*2);
	}
	
	private Rectangle boundsRelativeToDisplay() {
		Point upperLeft = getCenter(true);
		Rectangle bounds = new Rectangle(upperLeft.x-maxRadius,upperLeft.y-maxRadius,maxRadius,maxRadius*2);
		Point displayPoint = parent.toDisplay(bounds.x, bounds.y);
		bounds = new Rectangle(displayPoint.x,displayPoint.y,bounds.width,bounds.height);
		return bounds;
	}
	
	public void draw(GC gc, Rectangle drawArea) {
		
		Point center = getLocation();						
		Point topLeft = new Point(center.x+radius,center.y+radius);
		for(Petal p : petals) 
			p.draw(parent.getParent(),gc,topLeft);
		
		gc.setAlpha(mouseListener.isActive() ? 255 : 25);
		
		gc.setBackground(BLACK);
		gc.drawOval(center.x, center.y, radius*2, radius*2);
		
		//draw trip wire
		if(EditorViewportListener.context != ContextType.ALL_ON_SCREEN){
			
			if(EditorViewportListener.cursorPosition > 0){
				//draw nothing; the cursor is enough of a visual indicator
			}else{
				//draw a line to the center of the screen
				gc.drawLine(0, center.y+radius, center.x, center.y+radius);
			}
		}
	}
	
	public void redraw() {
		drawer.drawFigureOn(parent, this);
	}

	public void moveTo(Composite c) {
		dispose();
		this.parent = c;
		redraw();
		mouseListener.listenTo(parent);
	}

	public void dispose() {
		drawer.dispose();
		mouseListener.dispose();
		for(Petal p : petals)
			p.deactivate();
	}
	
	public void attachPetals(Map<SmellDetector<?>, Color> detectors) {
		petals.clear();
		int size = detectors.size();
		double inc = 180.0/size;
		double currentAngle = 90.0;
		for(Entry<SmellDetector<?>, Color> e : detectors.entrySet()){
			SmellDetector<?> smellDetector = e.getKey();
			Color color = e.getValue();
			petals.add(new Petal((int)currentAngle,(int)inc,smellDetector,color));
			currentAngle += inc;
		}
	}
	

	private enum State{ACTIVE,INACTIVE}
	
	class StateMonitor implements MouseMoveListener, MouseTrackListener{
	
		private State state = State.INACTIVE; 		
		
		public void listenTo(Composite c){
			dispose();
			parent.addMouseMoveListener(this);
			parent.addMouseTrackListener(this);
		}
		
		public void dispose(){
			if(parent!=null && !parent.isDisposed()){
				parent.removeMouseMoveListener(this);
				parent.removeMouseTrackListener(this);
			}
		}
		
		public void mouseMove(MouseEvent e) {	
			
			if(!isActive())
				return;
			
			testPoint(e);
		}
		

		public void mouseHover(MouseEvent e) {
			testPoint(e);
		}

		private void testPoint(MouseEvent e) {
			Point mousePoint = new Point(e.x,e.y);
			
			if(boundsRelativeToBackground().contains(mousePoint)){
				
				setState(State.ACTIVE);

				Petal petalToActivate = getPetalToActivate(mousePoint);														
				if(petalToActivate!=null){
					petalToActivate.setActive(true);
					for(Petal p : petals)
						if(p!=petalToActivate)
							p.setActive(false);
					
					redraw();
				}
				
				scheduleDeactivation();
			}
		}

		private Petal getPetalToActivate(Point mousePoint) {
			
			mousePoint = fromForegroundToBackground(mousePoint);
			
			Petal petalToActivate = null;
			for(Petal p : petals)
				if(p.contains(mousePoint) && !p.isActive())
					petalToActivate = p;
			
			return petalToActivate;
		}

		private void scheduleDeactivation() {
			deactivator.cancel();
			deactivator = new Deactivator();
			timer.schedule(deactivator, 2000);//2 seconds to deactivation
		}

		public boolean isActive() {
			return state.equals(State.ACTIVE);
		}
		
		private void setState(State s){
			state = s;
		}

		private Point fromForegroundToBackground(Point p) {			
			Point newP = parent.getLocation();
			newP.x += p.x;
			newP.y += p.y;
			return newP;
		}

		public void mouseEnter(MouseEvent e) {}
		public void mouseExit(MouseEvent e) {}
		
		private Timer timer = new Timer();
		private Deactivator deactivator = new Deactivator();
		private class Deactivator extends TimerTask{
			@Override
			public void run() {								
				Display.getDefault().asyncExec(redraw);
			}			
			
			private Runnable redraw = new Runnable(){
				public void run() {
					Point cursorPt = Display.getDefault().getCursorLocation();
					if(boundsRelativeToDisplay().contains(cursorPt))
						scheduleDeactivation();//recurse
					else{
						//go ahead and deactivate
						boolean dirty = false;
						setState(Flower.State.INACTIVE);
						for(Petal p : petals)
							dirty |= p.setInactive();
						
						if(dirty)
							redraw();
					}
				}
			};
		}
	}
}

