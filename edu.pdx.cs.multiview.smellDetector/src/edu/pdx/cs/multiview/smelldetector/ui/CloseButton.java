package edu.pdx.cs.multiview.smelldetector.ui;

import org.eclipse.swt.widgets.Composite;

public class CloseButton extends DrawnButton{
	
	public CloseButton(Composite parent) {
		super(parent);
	}
	
	public int[] polygon(int x, int y) {
		return new int[] {x,y, x+2,y, x+4,y+2, x+5,y+2, x+7,y, x+9,y, 
						                 x+9,y+2, x+7,y+4, x+7,y+5, x+9,y+7, x+9,y+9,
				                         x+7,y+9, x+5,y+7, x+4,y+7, x+2,y+9, x,y+9,
				                         x,y+7, x+2,y+5, x+2,y+4, x,y+2};
	}
}
