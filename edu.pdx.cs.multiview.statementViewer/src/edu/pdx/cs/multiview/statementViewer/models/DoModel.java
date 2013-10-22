package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.DoStatement;

public class DoModel extends LoopModel<DoStatement>{

	
	public DoModel(DoStatement t, Point p) {
		super(t, p);
	}

	@Override
	public void setASTNode(DoStatement w){
		super.setASTNode(w);

		setBody(		StatementModel.getStatementFor(getASTNode().getBody(),
				getBodyLocation()));
		
		setExpression(	ExpressionModel.getExpressionFor(getASTNode().getExpression(),
							getExpressionLocation()));
	}
	
	@Override
	public List<ASTModel> buildChildren(){
		List<ASTModel> children = new ArrayList<ASTModel>(3);
		
		children.add(getBody());
		if(getExpression()!=null)
			children.add(getExpression());
		
		return children;
	}
	
	@Override
	protected Point getExpressionLocation() {

		if(getBody()!=null)
			return getBodyLocation().translate(-10,getBody().getDimensions().height+5);
		else
			return getBodyLocation();
	}
	
	@Override
	protected Point getBodyLocation() {
		return new Point(20,10);
	}
}
