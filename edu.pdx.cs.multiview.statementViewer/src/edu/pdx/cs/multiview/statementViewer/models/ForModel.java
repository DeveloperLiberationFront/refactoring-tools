package edu.pdx.cs.multiview.statementViewer.models;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.ForStatement;

public class ForModel extends LoopModel<ForStatement>{

	public ForModel(ForStatement t, Point p) {
		super(t, p);
	}
	
	@Override
	public void setASTNode(ForStatement f){
		super.setASTNode(f);

		if(getASTNode().getExpression()!=null){
			setExpression(	ExpressionModel.getExpressionFor(getASTNode().getExpression(),
							getExpressionLocation()));
		}
		
		setBody(		StatementModel.getStatementFor(getASTNode().getBody(),
						getBodyLocation()));
	}
		
}
