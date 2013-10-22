package edu.pdx.cs.multiview.statementViewer.editparts;


import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.statementViewer.models.TypeModel;

public class TypeEditPart extends UnSelectableEditPart{

	@Override
	protected Label createFigure(){
		
		Label label = super.createFigure();
		label.setBackgroundColor(new Color(null,230,230,230));
		return label;
	}
	
	public String getText(){
		return "class " + getModel().getASTNode().getName().getIdentifier();
	}
	
	@Override
	public TypeModel getModel(){
		return (TypeModel)super.getModel();
	}
}
