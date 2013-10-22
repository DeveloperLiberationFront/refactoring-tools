package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

/**
 * I model a MethodDeclaration ASTNode.
 * 
 * @author emerson
 *
 */
public class MethodModel extends ASTModel<MethodDeclaration> {
	
	public MethodModel(MethodDeclaration t, Point p) {
		super(t, p);
	}

	private List<ASTModel> statements;

	/**
	 * Add a child to this model
	 * 
	 * @param model
	 */
	private void addChild(StatementModel model) {
		statements.add(model);
		fireEvent(P_CHILDREN, null, model);
	}

	/**
	 * @return	my children
	 */
	public List<ASTModel> buildChildren() {
		
		if(statements==null)
			initChildren();
		
		return statements;
	}

	private void initChildren() {
		
		statements = new ArrayList<ASTModel>(10);
		
		Block body = getASTNode().getBody();
		
		if(body==null)
			return;//no body - probably abstract
		
		Point currentChildLocation =  getFirstChildLocation();
		for(Object o: body.statements()){
			StatementModel statement = StatementModel.getStatementFor((Statement)o,currentChildLocation);
			Dimension size = statement.getDimensions();
			currentChildLocation.translate(0,size.height+MARGIN);
			addChild(statement);
		}
	}
	
	private Point getFirstChildLocation() {
		return new Point(10,25);
	}
}
