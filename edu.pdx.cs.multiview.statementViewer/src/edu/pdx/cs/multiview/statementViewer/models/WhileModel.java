package edu.pdx.cs.multiview.statementViewer.models;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.WhileStatement;

public class WhileModel extends LoopModel<WhileStatement>{

	
	public WhileModel(WhileStatement t, Point p) {
		super(t, p);
	}

	@Override
	public void setASTNode(WhileStatement w){
		super.setASTNode(w);

		setExpression(	ExpressionModel.getExpressionFor(getASTNode().getExpression(),
							getExpressionLocation()));
		
		setBody(		StatementModel.getStatementFor(getASTNode().getBody(),
							getBodyLocation()));

	}
}
