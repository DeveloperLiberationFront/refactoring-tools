package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.Statement;

/**
 * I am a superclass for models who have loops,
 * such as for-loops and while-loops
 * 
 * @author emerson
 *
 * @param <S>
 */
public class LoopModel<S extends Statement> extends StatementModel<S>{

	private StatementModel body;
	private ExpressionModel<?> expression;
	
	public LoopModel(S t, Point p) {
		super(t, p);
	}
	
	protected Point getExpressionLocation() {
		return new Point(10,10);
	}
	
	protected Point getBodyLocation() {
		if(getExpression()!=null)
			return getExpressionLocation().translate(10,getExpression().getDimensions().height+5);
		else
			return getExpressionLocation();
	}

	protected ASTModel<?> getExpression() {
		return expression;
	}
	
	protected ASTModel<?> getBody() {
		return body;
	}
	
	protected void setExpression(ExpressionModel m) {
		this.expression = m;
		fireEvent(P_CHILDREN, null, m);
	}
	
	protected void setBody(StatementModel m) {
		this.body = m;
		fireEvent(P_CHILDREN, null, m);
	}
	
	public List<ASTModel> buildChildren(){
		List<ASTModel> children = new ArrayList<ASTModel>(3);
		
		if(getExpression()!=null)
			children.add(getExpression());
		children.add(getBody());
		
		return children;
	}

}
