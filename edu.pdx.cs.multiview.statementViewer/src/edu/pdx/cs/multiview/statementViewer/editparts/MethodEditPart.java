package edu.pdx.cs.multiview.statementViewer.editparts;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.statementViewer.models.MethodModel;

public class MethodEditPart extends UnSelectableEditPart{
	
	@Override
	public String getText(){
		return JDTUtils.getMethodSignature(getModel().getASTNode()) + "(...)";
	}
	
	@Override
	public MethodModel getModel(){
		return (MethodModel)super.getModel();
	}
	
	@Override
	protected ASTEditPart getRevealParent(){
		return this;
	}
}
