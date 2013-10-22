package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * The model of a Statement
 * 
 * @author emerson
 */
public class StatementModel <S extends Statement> extends ASTModel<S>{
	
	
	public StatementModel(S t, Point p) {
		super(t,p);
	}

	/**
	 * @param s		a statement
	 * @param p		the location the statement should be placed at
	 * 
	 * @return		a {@link StatementModel} that wraps the arguments
	 */
	public static StatementModel getStatementFor(Statement s, Point p){
		
		if(s == null)
			return null;
		
		StatementModel model;
		
		if(s instanceof IfStatement){
			model = new IfStatementModel((IfStatement)s,p);
		}else if(s instanceof Block){
			model = new BlockModel((Block)s,p);
		}else if(s instanceof WhileStatement){
			model =  new WhileModel((WhileStatement)s,p);
		}else if(s instanceof ForStatement){
			model = new ForModel((ForStatement)s,p);
		}else if(s instanceof TryStatement){
			model = new TryModel((TryStatement)s,p);
		}else if(s instanceof SynchronizedStatement){
			model = new SynchronizedBlockModel((SynchronizedStatement)s,p);
		}else if(s instanceof DoStatement){
			model = new DoModel((DoStatement)s,p);
		}else{
			model = new StatementModel<Statement>(s,p);
		}
		
		return model;
	}

	@Override
	public List<ASTModel> buildChildren() {
		return new ArrayList<ASTModel>(0);
	}
}
