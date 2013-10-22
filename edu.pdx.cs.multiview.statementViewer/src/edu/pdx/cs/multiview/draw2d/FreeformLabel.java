package edu.pdx.cs.multiview.draw2d;

import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;

public class FreeformLabel extends Label implements FreeformFigure{

	private FreeformHelper helper = new FreeformHelper(this);
	
	public FreeformLabel(String label){
		super(label);
	}

	/**
	 * @see IFigure#add(IFigure, Object, int)
	 */
	public void add(IFigure child, Object constraint, int index) {
		super.add(child, constraint, index);
		helper.hookChild(child);
	}

	/**
	 * @see FreeformFigure#addFreeformListener(FreeformListener)
	 */
	public void addFreeformListener(FreeformListener listener) {
		addListener(FreeformListener.class, listener);
	}

	/**
	 * @see FreeformFigure#fireExtentChanged()
	 */
	public void fireExtentChanged() {
		Iterator iter = getListeners(FreeformListener.class);
		while (iter.hasNext())
			((FreeformListener)iter.next())
				.notifyFreeformExtentChanged();
	}

	/**
	 * Overrides to do nothing.
	 * @see Figure#fireMoved()
	 */
	@SuppressWarnings("deprecation")
	protected void fireMoved() { }

	/**
	 * @see FreeformFigure#getFreeformExtent()
	 */
	public Rectangle getFreeformExtent() {
		return helper.getFreeformExtent();
	}

	/**
	 * @see Figure#primTranslate(int, int)
	 */
	public void primTranslate(int dx, int dy) {
		bounds.x += dx;
		bounds.y += dy;
	}

	/**
	 * @see IFigure#remove(IFigure)
	 */
	public void remove(IFigure child) {
		helper.unhookChild(child);
		super.remove(child);
	}

	/**
	 * @see FreeformFigure#removeFreeformListener(FreeformListener)
	 */
	public void removeFreeformListener(FreeformListener listener) {
		removeListener(FreeformListener.class, listener);
	}

	/**
	 * @see FreeformFigure#setFreeformBounds(Rectangle)
	 */
	public void setFreeformBounds(Rectangle bounds) {
		helper.setFreeformBounds(bounds);
	}
	
}
