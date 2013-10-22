package edu.pdx.cs.multiview.smelldetector.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public abstract  class DrawnButton extends Canvas{
	
	//copy of CTabFolder.Close_FILL;
	static final RGB CLOSE_FILL = new RGB(252, 160, 160);
	
	protected Rectangle drawSpace = new Rectangle(0,0,11,11);
	private Image image;
	private boolean mouseOver = false;
	
	public DrawnButton(Composite parent) {
		super(parent,SWT.NONE);
		initImage();
		
		this.addMouseTrackListener(new MouseTrackListener(){

			public void mouseEnter(MouseEvent e) {
				mouseOver = true;
				reinitImage();
			}

			public void mouseExit(MouseEvent e) {
				mouseOver = false;
				reinitImage();
			}

			public void mouseHover(MouseEvent e) {}
		});
	}

	protected void reinitImage(){
		if(image!=null)
			image.dispose();
		
		initImage();
	}
	
	private void initImage() {
		
		image = new Image(this.getDisplay(),drawSpace);
		
		GC gc = new GC(image);
		draw(gc);
		gc.dispose();

		setBackgroundImage(image);
	}

	@Override
	public void dispose(){
		image.dispose();
		super.dispose();
	}
	
	@Override
	public Point computeSize(int hint, int hint2, boolean changed) {
		return new Point(drawSpace.width,drawSpace.height);
	}
	
	void draw(GC gc) {

		int x = drawSpace.x;
		int y = drawSpace.y;
		
		Color outline = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		Color fill = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		
		if(mouseOver){
			fill = new Color(null,CLOSE_FILL);
		}
		gc.setBackground(fill);
		if(mouseOver){
			fill.dispose();
		}
		
		int[] shape = polygon(x, y);
		
		gc.fillPolygon(shape);
		gc.setForeground(outline);
		gc.drawPolygon(shape);
	}

	public abstract int[] polygon(int x, int y);
}
