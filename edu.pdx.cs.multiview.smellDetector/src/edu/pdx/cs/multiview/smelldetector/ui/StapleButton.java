package edu.pdx.cs.multiview.smelldetector.ui;

import org.eclipse.swt.widgets.Composite;


public class StapleButton extends DrawnButton{
	
	private boolean isPinned;
	
	public StapleButton(Composite parent, boolean isPinned) {
		super(parent);
		this.isPinned = isPinned;
		reinitImage();
	}

	public boolean togglePinned() {
		isPinned = !isPinned;
		reinitImage();
		return isPinned;
	}
	
	@Override
	public int[] polygon(int x, int y) {
		return isPinned ? stapleDown(x,y) : stapleUp(x, y);
	}

	public int[] stapleUp(int x, int y) {
		return new int[] {	x+6,y,
							x+10,y+4,
							x+10,y+6,
							x+9,y+6,
							x+6,y+3,
							x+3,y+6,
							x+6,y+9,
							x+6,y+10,
							x+4,y+10,
							y,x+6};
	}
	
	public int[] stapleDown(int x, int y) {
		return new int[] {	x+9,y+3,
							x+10,y+4,
							x+10,y+6,
							x+9,y+6,
							x+6,y+9,
							x+6,y+10,
							x+4,y+10,
							y+3,x+9};
	}
}
