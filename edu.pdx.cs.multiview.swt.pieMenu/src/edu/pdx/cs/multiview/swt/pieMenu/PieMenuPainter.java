package edu.pdx.cs.multiview.swt.pieMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import edu.pdx.cs.multiview.swt.geometry.Arc;
import edu.pdx.cs.multiview.swt.geometry.Coordinate;

/*
 * Based on SATIN
 */
public class PieMenuPainter implements PaintListener {

	private final PieMenu pieMenu;

	static final double DEFAULT_START = Math.PI / 2;
	
	//FIXME these should really be disposed
	private static Color selectedColor;
	private static Font font;

	public PieMenuPainter(PieMenu pieMenu) {
		this.pieMenu = pieMenu;
	}

	private int smallCircleCoord() {
		return getRadius() - smallRadius() / 2;
	}

	/*
	 * The radius of the small, inner circle
	 */
	private int smallRadius() {
		return 20;
	}

	/**
	 * @return	the radius of the circle
	 */
	public static int getRadius() {
		return 110;
	}

	/*
	 * Determines how far out the text will be pushed from circle center
	 */
	private double scalingFactor() {
		return 0.76;
	}

	private Color getSelectedColor() {
		
		if(selectedColor==null){
			selectedColor = new Color(Display.getCurrent(),153,217,234);
		}
		
		return selectedColor;
	}
	
	private static Font getFont() {
		if(font==null)
			font = new Font(Display.getCurrent(), "Calibri", 13, SWT.NONE);
		return font;
	}

	private Color getLineColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
	}

	private Color getSelectedFontColor() {
		return Display.getCurrent().getSystemColor(
				SWT.COLOR_BLACK);
	}

	private Color getFontColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}

	private Color getFillColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	private void renderString(GC gc, Coordinate circleCenter, String str,
			Coordinate textPosition, boolean selected) {

		gc.setFont(getFont());
		
		// // 1. Compute the width of the string.
		FontMetrics fmetric = gc.getFontMetrics(); // current font
		// metrics
		float charHeight = fmetric.getHeight(); // height is constant		

		Coordinate pt = textPosition.movedInOrOutBy(scalingFactor())
				.toJavaCoordinate(circleCenter);

		// // 3. Tokenize the String, in case it has newlines and such.
		StringTokenizer strtok = new StringTokenizer(str, "\r\n\t");
		int offset = 0;
		String token;

		// // 3.1. Draw each token so that it is centered at pt.x and pt.y.
		int y = pt.y()
				- (int) (((((float) (strtok.countTokens() - 1f)) / 2f) * charHeight));
		int x = pt.x();
		gc.setForeground(selected ? getSelectedFontColor() : getFontColor());		
		
		while (strtok.hasMoreTokens()) {
			token = strtok.nextToken();
			
			int tokenWidth = 0;
			for(char c : token.toCharArray()){
				tokenWidth += gc.getAdvanceWidth(c);
			}
			
			gc.drawString(token, (int) (x - 0.5 * tokenWidth),
					(int) (y - 0.5 * charHeight) + offset);
			offset += charHeight;
		}
	}
	
	private void renderSubmenuIcon(GC gc, Coordinate circleCenter, double angle) {
		gc.drawPolygon(pointer(getRadius(),angle,circleCenter));
	}
	
	public int[] pointer(double distance, double angle, Coordinate center){
		
		Coordinate outerTip = Coordinate.create(distance, angle).toJavaCoordinate(center);
		Coordinate left = Coordinate.create(distance-10, angle-0.1).toJavaCoordinate(center);
		Coordinate right = Coordinate.create(distance-10, angle+0.1).toJavaCoordinate(center);
		
		int[] points = new int [6];
		points[0] = left.x();
		points[1] = left.y();
		points[2] = outerTip.x();
		points[3] = outerTip.y();
		points[4] = right.x();
		points[5] = right.y();
		
		return points;
	}

	private void fillSmallCircle(GC gc) {
		gc.fillOval(smallCircleCoord(), smallCircleCoord(), smallRadius(),
				smallRadius());
	}

	private void drawSmallCircle(GC gc) {
		gc.drawOval(smallCircleCoord(), smallCircleCoord(), smallRadius(),
				smallRadius());
	}
	
	private void drawLargeCircle(GC gc){
		int diameter = getRadius()*2-1;
		gc.drawOval(1, 1, diameter, diameter);
	}

	boolean getAllClipping() {
		return false;
	}

	private double getStartRadian() {
		int numItems = pieMenu.getItemCount();

		if (getLineNorth() || numItems <= 1) {
			return (PieMenuPainter.DEFAULT_START);
		} else {
			double offset = 2 * Math.PI / numItems;
			return (PieMenuPainter.DEFAULT_START - offset / 2);
		}
	}

	private boolean getLineNorth() {
		return false;
	}

	public void paintControl(PaintEvent e) {

		GC gc = e.gc;

		int numItems = pieMenu.getItemCount();
		if (numItems <= 0) {
			gc.setForeground(getFillColor());
			gc.fillOval(0, 0, 2 * getRadius(), 2 * getRadius());
		} else {

			gc.setForeground(getLineColor());

			Coordinate circleCenter = Coordinate.create(getRadius(),
					getRadius());

			for (IndexedArc arc : getArcs()) {
				
				boolean isSelected = pieMenu.getSelectedItem() == arc.index;
				gc.setBackground(isSelected ? getSelectedColor()
						: getFillColor());
				arc.arc.fill(gc);

				IPieMenu item = pieMenu.getItem(arc.index);

				Coordinate textPosition = arc.arc.center().movedInOrOutBy(scalingFactor());

				renderString(gc, circleCenter, item.getText(), textPosition, isSelected);
				
				if(!item.isEmpty())
					renderSubmenuIcon(gc,circleCenter,textPosition.theta());
			}
		}

		gc.setLineWidth(1);
		
		gc.setForeground(getLineColor());						
		drawLargeCircle(gc);
		
		if (numItems > 1)
			for (IndexedArc arc : getArcs())
				arc.arc.drawLine(gc);
		
		gc.setBackground(pieMenu.getSelectedItem() < 0 ? 
				getSelectedColor() : getFillColor());
		
		fillSmallCircle(gc);
		drawSmallCircle(gc);
	}



	private ArcSet arcset;

	private ArcSet getArcs() {
		if (arcset == null || arcset.size() != pieMenu.getItemCount())
			arcset = new ArcSet(pieMenu.getItemCount());
		return arcset;
	}

	private class ArcSet implements Iterable<IndexedArc> {

		private List<IndexedArc> arcs = new ArrayList<IndexedArc>();

		public ArcSet(int size) {

			double stepRadian = 2 * Math.PI / size;
			double currentRadian = getStartRadian();

			for (int i = 0; i < size; i++) {

				Arc arc = new Arc(currentRadian, stepRadian, getRadius());

				arcs.add(new IndexedArc(arc, i));
				currentRadian += stepRadian;
			}
		}

		public int size() {
			return arcs.size();
		}

		public Iterator<IndexedArc> iterator() {
			return arcs.iterator();
		}
	}

	public class IndexedArc {

		public final Arc arc;
		public final int index;

		public IndexedArc(Arc arc, int index) {
			this.arc = arc;
			this.index = index;
		}
	}

	public int getItemIndex(Coordinate c) {

		if(c.radius()<this.smallRadius())
			return -1;
		
		for (IndexedArc arc : getArcs()) {
			if (arc.arc.contains(c)) {
				return arc.index;
			}
		}

		return -1;
	}
}

/*
 
This class was originally derived from another piece of software:
 
Copyright (c) 2000 Regents of the University of California.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:

      This product includes software developed by the Group for User 
      Interface Research at the University of California at Berkeley.

4. The name of the University may not be used to endorse or promote products 
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
*/
