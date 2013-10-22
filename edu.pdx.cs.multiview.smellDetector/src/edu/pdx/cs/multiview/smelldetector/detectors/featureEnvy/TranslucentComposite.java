/**
 * 
 */
package edu.pdx.cs.multiview.smelldetector.detectors.featureEnvy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.smelldetector.ui.TranslucentComponent;

public class TranslucentComposite extends TranslucentComponent{
	
	final public Composite composite;
	
	public TranslucentComposite(Composite parent){
		composite = new Composite(parent,SWT.NONE);
	}			

	public void setColor(Color color){
		Image icon = createIcon(composite,color);
		composite.setBackgroundImage(icon);
	}
}