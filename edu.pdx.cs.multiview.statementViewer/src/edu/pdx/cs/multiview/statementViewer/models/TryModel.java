package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryModel extends StatementModel<org.eclipse.jdt.core.dom.TryStatement>{

	private StatementModel body;
	private List<BlockModel> catchClauses;
	private StatementModel finallyBlock;
	
	public TryModel(TryStatement t, Point p) {
		super(t, p);
	}

	@Override
	public void setASTNode(TryStatement t){
		super.setASTNode(t);
		
		setBody(StatementModel.getStatementFor(getASTNode().getBody(),
										getBodyLocation()));
		
		setCatchClauses();
		
		setFinallyStatement(	StatementModel.getStatementFor(getASTNode().getFinally(),
										getFinallyLocation()));
	}

	private void setCatchClauses() {

		catchClauses = new ArrayList<BlockModel>(getASTNode().catchClauses().size());
		Point currentLocation = getFirstCatchLocation();
		for(Object o : getASTNode().catchClauses()){
			CatchClause cc = (CatchClause)o;
			BlockModel b = new BlockModel(cc.getBody(),currentLocation);
			addCatchClause(b);
			currentLocation.translate(0,b.getDimensions().height);
		}
	}
	

	private void setBody(StatementModel b) {
		this.body = b;
		fireEvent(P_CHILDREN, null, b);
	}

	private void addCatchClause(BlockModel b) {
		catchClauses.add(b);
		fireEvent(P_CHILDREN, null, b);
	}


	private void setFinallyStatement(StatementModel b) {
		if(b!=null){
			finallyBlock = b;
			fireEvent(P_CHILDREN, null, b);
		}
	}
	
	private Point getBodyLocation(){
		return new Point(10,10);
	}
	
	private Point getFirstCatchLocation(){
		return getBodyLocation().translate(0,getBody().getDimensions().height+5);
	}
	
	private Point getFinallyLocation() {
		
		if(catchClauses.isEmpty())
			return getBodyLocation().translate(0, getBody().getDimensions().height);
		
		BlockModel lastCatch = catchClauses.get(catchClauses.size()-1);
		return lastCatch.getLocation().getCopy().translate(0,lastCatch.getDimensions().height);
	}

	private StatementModel getBody() {
		return body;
	}
	
	private StatementModel getFinallyBlock(){
		return finallyBlock;
	}
	
	@Override
	public List<ASTModel> buildChildren(){
		
		List<ASTModel> children = new ArrayList<ASTModel>(3);
		
		children.add(getBody());
		children.addAll(catchClauses);
		if(getFinallyBlock()!=null)
			children.add(getFinallyBlock());
		
		return children;
	}
}
