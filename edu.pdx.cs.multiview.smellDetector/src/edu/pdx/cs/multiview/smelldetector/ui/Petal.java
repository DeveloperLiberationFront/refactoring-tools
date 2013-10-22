package edu.pdx.cs.multiview.smelldetector.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.swt.geometry.Angle;
import edu.pdx.cs.multiview.swt.geometry.Coordinate;

//TODO: could be made more efficient by not converting back and forth to radians
public class Petal extends Angle{
	

	private static final int maxRadius = 100;
	private static final Color TOOLTIP_COLOR = new Color(null,255,255,204);
	
	private SmellDetector<?> detector;
	private boolean isActive = false;
	private Point lastOrigin = new Point(-1,-1);
	private Color color;

	public Petal(int angle, int incrementAngle, SmellDetector<?> petal, Color color) {
		super(Math.toRadians(angle),Math.toRadians(incrementAngle));
		this.detector = petal;
		this.color = color;
	}
	
	public boolean contains(Point mousePoint){
		return contains(Coordinate.create(mousePoint,lastOrigin));
	}
	
	public boolean setInactive() {		
		return setActive(false);
	}

	public boolean setActive(boolean setActive) {
		if(setActive!=isActive){
			isActive = setActive;
			return true;
		}
		return false;
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	@Override
	public boolean contains(Coordinate other){			
		return super.contains(other) && (other.radius() <= maxRadius);
	}
	

	
	//TODO: Coordinate class with
	//		conversions to/from Point
	//		AND those which carry around the widget on
	//		which they are located

	public void draw(Control parent, GC gc, Point cp) {		

		lastOrigin = cp;
		DrawPoint centerPoint = new DrawPoint(cp,gc);
		
		gc.setBackground(color);
		
		gc.setAlpha(isActive ? 255 : 70);
		
		centerPoint.fillArc(this,radius());

		gc.setAlpha(255);
		
		if(isActive){
			gc.setForeground(Flower.BLACK);
			
			centerPoint.drawArc(this,maxRadius);
			
			centerPoint.drawLineTo(theta, maxRadius);
			centerPoint.drawLineTo(theta+increment, maxRadius);
		
			Point bottomRight = parent.toDisplay(halfWayPoint(centerPoint));
			showLabel(parent.getShell(),bottomRight);
		}else{
			hideLabel();
		}
	}
	
	private Shell shell;

	private void showLabel(Shell parent, Point bottomRight) {
		if(shell==null){
			shell = new Shell(parent,SWT.NO_TRIM | SWT.TOOL);
			GridLayout layout = new GridLayout();
			int margin = 3;
			layout.marginHeight = layout.marginWidth = margin;
			shell.setLayout(layout);
			
			Label l = new Label(shell,SWT.NONE);			
			l.setText(detector.getName() + " [+]");				
			l.pack();
	
			Point size = l.getSize();
			size.x += 2*margin;
			size.y += 2*margin;
			shell.setSize(size);
			
			l.setBackground(TOOLTIP_COLOR);
			shell.setBackground(TOOLTIP_COLOR);
			
			l.addMouseListener(new MouseListener(){

				public void mouseDown(MouseEvent e) {
					try {
						detector.showDetails();
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
				}
				
				public void mouseDoubleClick(MouseEvent e) {}
				public void mouseUp(MouseEvent e) {}				
			});
		}
					
		shell.setLocation(bottomRight.x - shell.getSize().x, 
						bottomRight.y - shell.getSize().y);
		
		shell.setVisible(true);
	}

	private void hideLabel() {
		if(shell!=null)
			shell.setVisible(false);
	}
	
	private int radius() {
		return (int)(detector.size()*maxRadius);
	}
	
	public Point halfWayPoint(DrawPoint centerPoint){
		return centerPoint.perimiterPointAt(halfWayRadian(), maxRadius);
	}
	
	public void deactivate(){
		if(shell!=null){
			shell.dispose();
			shell = null;
			isActive = false;
		}
	}

}