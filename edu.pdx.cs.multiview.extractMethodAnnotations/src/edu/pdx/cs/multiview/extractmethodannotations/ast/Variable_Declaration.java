package edu.pdx.cs.multiview.extractmethodannotations.ast;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference.Type;

public class Variable_Declaration extends AST_Node<VariableDeclaration> {


	public Variable_Declaration(VariableDeclaration node) {
		super(node);
	}
	
	//TODO: I would like to do without this getter, or maybe without this class
	public Variable_Reference getName(){
		return new Variable_Reference(node.getName(),Type.write);
	}

	public int getLength() {
		return node.getLength();
	}

}
