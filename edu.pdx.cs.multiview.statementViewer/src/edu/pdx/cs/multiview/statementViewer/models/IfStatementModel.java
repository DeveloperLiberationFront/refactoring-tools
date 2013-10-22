package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.IfStatement;

public class IfStatementModel extends StatementModel<IfStatement> {

	//children
	private StatementModel thenStatement, elseStatement;
	private ExpressionModel expression;
	
	public IfStatementModel(IfStatement t, Point p) {
		super(t,p);
	}

	@Override
	public void setASTNode(IfStatement t){
		super.setASTNode(t);

		setExpression(		ExpressionModel.getExpressionFor(getASTNode().getExpression(),
										getExpressionLocation())	);
		
		setThenStatement(	StatementModel.getStatementFor(getASTNode().getThenStatement(),
										getThenLocation()));
		
		setElseStatement(	StatementModel.getStatementFor(getASTNode().getElseStatement(),
										getElseLocation()));
	}

	
	public StatementModel getElseStatement() {
		return elseStatement;
	}


	public ExpressionModel getExpression() {
		return expression;
	}

	public StatementModel getThenStatement() {
		return thenStatement;
	}


	private void setElseStatement(StatementModel m) {
		if(m!=null){
			this.elseStatement = m;
			fireEvent(P_CHILDREN, null, m);
		}
	}

	private void setExpression(ExpressionModel m) {
		this.expression = m;
		fireEvent(P_CHILDREN, null, m);
	}


	private void setThenStatement(StatementModel m) {
		this.thenStatement = m;
		fireEvent(P_CHILDREN, null, m);
	}
	
	@Override
	public List<ASTModel> buildChildren(){
		List<ASTModel> children = new ArrayList<ASTModel>(3);
		
		children.add(getExpression());
		children.add(getThenStatement());
		if(getElseStatement()!=null)
			children.add(getElseStatement());
		
		return children;
	}

	private Point getExpressionLocation() {
		return new Point(10,10);
	}

	private Point getThenLocation() {
		return getExpressionLocation().translate(10,getExpression().getDimensions().height+5);
	}

	private Point getElseLocation(){
		Point elseLocation = getThenStatement().getLocation().getCopy();
		elseLocation.translate(0,getThenStatement().getDimensions().height + 5);
		return elseLocation;
	}
	
}
