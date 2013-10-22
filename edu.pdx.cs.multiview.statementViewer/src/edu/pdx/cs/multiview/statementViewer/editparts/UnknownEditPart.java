package edu.pdx.cs.multiview.statementViewer.editparts;


import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import edu.pdx.cs.multiview.draw2d.FreeformLabel;

/**
 * I am an edit part with some model
 * 
 * @author emerson
 */
public class UnknownEditPart extends AbstractGraphicalEditPart {

	@Override
	protected IFigure createFigure() {
		return new FreeformLabel(getModel().toString());
	}

	@Override
	protected void createEditPolicies() {/*noe*/}
}
