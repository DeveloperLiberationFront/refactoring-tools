package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

public class BlockModel extends StatementModel<Block>{
	
	public BlockModel(Block b, Point p) {
		super(b, p);
	}


	public List<ASTModel> buildChildren() {
		
		List<ASTModel> statements = new ArrayList<ASTModel>(10);
		
		Point currentChildLocation =  getFirstChildLocation();
		for(Object o: getASTNode().statements()){
			StatementModel statement = StatementModel.getStatementFor((Statement)o,currentChildLocation);
			Dimension size = statement.getDimensions();
			currentChildLocation.translate(0,size.height+MARGIN);
			statements.add(statement);
			fireEvent(P_CHILDREN, null, statement);
		}
		
		return statements;
	}
	
	private Point getFirstChildLocation() {
		return new Point(10,10);
	}
}
