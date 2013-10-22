package edu.pdx.cs.multiview.smelldetector.detectors.featureEnvy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.smelldetector.ui.TranslucentComponent;

public class TranslucentLabel extends TranslucentComponent{
	
	private CLabel label;
	
	public TranslucentLabel(Composite parent){
		label = new CLabel(parent,SWT.NONE);
	}
	
	public void setText(String text) {
		label.setText(text);
	}

	public void setColor(Color color){
		Image icon = createIcon(label,color);
		label.setBackground(icon);
	}
}