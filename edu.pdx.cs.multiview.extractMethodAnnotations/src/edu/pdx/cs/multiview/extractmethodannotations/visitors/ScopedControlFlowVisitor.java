package edu.pdx.cs.multiview.extractmethodannotations.visitors;

import java.util.Collection;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.CollectionUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.ITextSelection;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.ControlFlowAnnotationCollection;
import edu.pdx.cs.multiview.extractmethodannotations.ast.AST_Node;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Break_Statement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Continue_Statement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.ControlFlowStatement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.LocalCFStatement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Return_Statement;

public class ScopedControlFlowVisitor extends ControlFlowVisitor{

	private boolean containsStatement = false;
	private ITextSelection scope;
	
	public ScopedControlFlowVisitor(ITextSelection selection){
		this.scope = selection;
	}

	protected Collection<Break_Statement> breaks(){
		return CollectionUtils.select(super.breaks(), LocalCFStatement.betweenPredicate(scope));
	}

	protected Collection<Continue_Statement> continues(){
		return CollectionUtils.select(super.continues(), LocalCFStatement.betweenPredicate(scope));
	}
	
	protected Collection<Return_Statement> returns(){
		return CollectionUtils.select(super.returns(), AST_Node.withinPredicate(scope));
	}

	public ControlFlowAnnotationCollection getAnnotations(boolean badControlPath) {
		
		ControlFlowAnnotationCollection cfAnnotations =  
						new ControlFlowAnnotationCollection();
		
		CollectionUtils.forAllDo(returns(), addTo(cfAnnotations));
		CollectionUtils.forAllDo(breaks(), addTo(cfAnnotations));
		CollectionUtils.forAllDo(continues(), addTo(cfAnnotations));
		
		cfAnnotations.addFlowAnnotationIfNecessary(scope,badControlPath);
		
		return cfAnnotations;
	}
	
	@Override
	public void preVisit(ASTNode node) {
		if(node instanceof Statement && AST_Node.withinPredicate(scope).evaluate(AST_Node.create(node)))
			containsStatement = true;
	}

	/**
	 * @return	whether at least one statement is selected
	 */
	public boolean isAStatementSelected() {
		return containsStatement;
	}

	private static Closure<ControlFlowStatement> addTo(final ControlFlowAnnotationCollection c){
		return new Closure<ControlFlowStatement>(){
			public void execute(ControlFlowStatement input) {
				input.addAnnotation(c);
			}};
	}
}
