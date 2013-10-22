package edu.pdx.cs.multiview.extractmethodannotations.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.pdx.cs.multiview.extractmethodannotations.ast.Break_Statement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Continue_Statement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Return_Statement;

/**
 * I determine the exit points of a range of code
 * 
 * @author emerson
 */
public class ControlFlowVisitor extends ASTVisitor{
	
	private TargetMap targetMap = new TargetMap();
	
	@Override
	public boolean visit(LabeledStatement ls){
		targetMap.addLabeledStatement(ls);
		return true;
	}
	
	@Override
	public boolean visit(ForStatement node){
		targetMap.addStatement(node);
		return true;
	}
	
	@Override
	public boolean visit(WhileStatement node){
		targetMap.addStatement(node);
		return true;
	}
	
	@Override
	public boolean visit(DoStatement node){
		targetMap.addStatement(node);
		return true;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node){
		targetMap.addStatement(node);
		return true;
	}
	
	@Override
	public boolean visit(SwitchStatement node){
		targetMap.addStatement(node);
		return true;
	}
	
	@Override
	public void postVisit(ASTNode node){
		targetMap.end(node);
	}
	
	@Override
	public boolean visit(BreakStatement node){
		targetMap.addBreak(node);
		return true;
	}
	
	@Override
	public boolean visit(ContinueStatement node){
		targetMap.addContinue(node);
		return true;
	}
	
	@Override
	public boolean visit(ReturnStatement node){
		targetMap.addReturn(node);
		return true;
	}
	
	protected Collection<Break_Statement> breaks(){
		return CollectionUtils.collect(
						targetMap.breaks().entrySet(),
						Break_Statement.breakTransform()
					);
	}
	
	protected Collection<Continue_Statement> continues(){
		return CollectionUtils.collect(
						targetMap.continues().entrySet(),
						Continue_Statement.continueTransform()
					);
	}
	
	protected Collection<Return_Statement> returns() {
		return CollectionUtils.collect(
						targetMap.returns(),
						Return_Statement.returnTransform()
					);
	}
	
	private class TargetMap{
		
		private Map<SimpleName,Statement> labeledStatements =
			new HashMap<SimpleName, Statement>();
		
		private Stack<Statement> context = 
			new Stack<Statement>();
		
		private List<ReturnStatement> returns = 
			new ArrayList<ReturnStatement>();
		
		private Map<BreakStatement,Statement> breaksToTargets = 
			new HashMap<BreakStatement, Statement>();
		
		private Map<ContinueStatement,Statement> continuesToTargets = 
			new HashMap<ContinueStatement, Statement>();
		
		public void addLabeledStatement(LabeledStatement ls) {
			labeledStatements.put(ls.getLabel(), ls.getBody());
		}

		public void addReturn(ReturnStatement node) {
			returns.add(node);
		}

		public void end(ASTNode node) {			
			if(!context.isEmpty() && context.peek().equals(node))
				context.pop();
		}

		public void addStatement(Statement node) {
			context.push(node);
		}
		
		public void addBreak(BreakStatement bs) {
			Statement target = bs.getLabel()!=null ?
					targetFor(bs.getLabel()) :
					context.peek();
			breaksToTargets.put(bs, target);
		}
		
		public void addContinue(ContinueStatement cs) {
			Statement target = cs.getLabel()!=null ?
					targetFor(cs.getLabel()) :
					context.peek();
			continuesToTargets.put(cs, target);
		}
		
		private Statement targetFor(final SimpleName label) {
			
			Statement value = CollectionUtils.find(labeledStatements.entrySet(), new Predicate<Map.Entry<SimpleName,Statement>>(){
				public boolean evaluate(Entry<SimpleName, Statement> entry) {
					return entry.getKey().getIdentifier().equals(label.getIdentifier());
				}}).getValue();
			
			if(value==null)
				throw new IllegalArgumentException("No target found for "+label.getParent());
			
			return value;
		}

		public Map<ContinueStatement, Statement> continues() {
			return continuesToTargets;
		}

		public Map<BreakStatement, Statement> breaks() {
			return breaksToTargets;
		}
		
		public List<ReturnStatement> returns() {
			return returns;
		}
	}
}
