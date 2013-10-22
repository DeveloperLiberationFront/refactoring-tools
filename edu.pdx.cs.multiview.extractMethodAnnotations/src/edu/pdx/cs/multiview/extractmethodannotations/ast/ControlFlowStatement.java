package edu.pdx.cs.multiview.extractmethodannotations.ast;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.ControlFlowAnnotationCollection;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.IControlFlowAnnotation;

public abstract class ControlFlowStatement<T extends ASTNode> extends AST_Node<ASTNode>{

	public ControlFlowStatement(T node) {
		super(node);
	}
	
	public void addAnnotation(ControlFlowAnnotationCollection c) {
		annotation().addTo(c, this);
	}
	
	protected abstract IControlFlowAnnotation annotation();
}
