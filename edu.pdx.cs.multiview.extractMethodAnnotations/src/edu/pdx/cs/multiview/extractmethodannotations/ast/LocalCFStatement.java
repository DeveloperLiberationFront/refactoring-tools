package edu.pdx.cs.multiview.extractmethodannotations.ast;

import org.apache.commons.collections15.Predicate;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.ITextSelection;

public abstract class LocalCFStatement<T extends ASTNode> extends ControlFlowStatement<T> {

	public final Statement target;
	
	public LocalCFStatement(T node, Statement target) {
		super(node);
		this.target = target;
	}

	public static Predicate<LocalCFStatement> betweenPredicate(final ITextSelection scope) {
		return new Predicate<LocalCFStatement>(){
			public boolean evaluate(LocalCFStatement s) {
				ASTNode source = s.node;
				Statement target = s.target;
				return (within(scope, source) && !within(scope, target));
			}};
	}

	public int getEndOfTarget() {
		return endOf(target);
	}
}
