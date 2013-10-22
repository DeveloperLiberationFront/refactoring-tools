package edu.pdx.cs.multiview.smelldetector.ui;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import edu.pdx.cs.multiview.swt.geometry.Angle;
import edu.pdx.cs.multiview.swt.geometry.Coordinate;

class DrawPoint{
	
	private final int x,y;
	private GC gc;
	
	public DrawPoint(Point p, GC graphics){
		x = p.x;
		y = p.y;
		gc = graphics;
	}
	
	public void fillArc(Angle a, int radius){
		gc.fillArc(	x-radius, y-radius, 
					2*radius, 2*radius, 
					a.degAngle(), a.degIncrement());
	}
	
	public void drawArc(Angle a, int radius) {
		gc.drawArc(	x-radius, y-radius, 
					2*radius, 2*radius, 
					a.degAngle(), a.degIncrement());
	}
	
	public void drawLineTo(double theta, double radius) {
		Point p = perimiterPointAt(theta,radius);
		gc.drawLine(x,y,p.x, p.y);
	}
	
	public Point perimiterPointAt(double theta, double radius) {
		Coordinate c = Coordinate.create(radius, theta);				
		c = c.toJavaCoordinate(Coordinate.create(x, y));
		return new Point(c.x(),c.y());
	}
}