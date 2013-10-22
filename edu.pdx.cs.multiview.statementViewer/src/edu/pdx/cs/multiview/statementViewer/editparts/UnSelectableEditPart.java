package edu.pdx.cs.multiview.statementViewer.editparts;

import org.eclipse.swt.graphics.Color;

//bah!  this should be a trait
public class UnSelectableEditPart extends ASTEditPart {

	@Override
	protected void colorFigure(Color color) {
		//nothing - I'm uncolorable
	}
}
