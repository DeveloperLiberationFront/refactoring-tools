package edu.pdx.cs.multiview.extractmethodannotations.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jface.text.Position;

import edu.pdx.cs.multiview.extractmethodannotations.util.TempVariable;

public class Variable_Reference extends AST_Node<SimpleName> {

	public static enum Type {read,write,readAndWrite}
	
	public final Type type;
	
	public Variable_Reference(SimpleName node, Type t) {
		super(node);
		this.type = t;
	}

	public String getIdentifier() {
		return node.getIdentifier();
	}
	
	/**
	 * @param aNode
	 * @param i 
	 * 
	 * @return	the range highest loop cotaining node that occursbefore i
	 * 			
	 */
	public Position outermostLoopRangeBefore(int i) {
		
		ASTNode aNode = outermostLoopBeforeInternal(this.node, i);
		
		return getRange(aNode);
	}

	private static ASTNode outermostLoopBeforeInternal(ASTNode aNode, int i) {
		
		if(aNode != null){
			
			ASTNode loop = outermostLoopBeforeInternal(aNode.getParent(), i);
			if(loop!=null)
				return loop;
			
			if(isLoop(aNode) && aNode.getStartPosition()<i)
				return aNode;
		}
		
		return null;
	}
	
	/**
	 * @param node
	 * 
	 * @return		whether the argument is a loop
	 */
	private static boolean isLoop(ASTNode node) {
		return node instanceof ForStatement ||
				node instanceof WhileStatement ||
				node instanceof DoStatement;
	}
	
	/**
	 * @param name		a variable name
	 * @param variable	a local variable
	 * 
	 * @return			if the variable refers to the local variable
	 */
	public boolean equals(TempVariable variable) {
		return variable.decl.getName().
				getIdentifier().equals(getIdentifier());
	}

	public boolean isWrite() {
		return type!=Type.read;
	}

	public boolean isRead(){
		return type==Type.read;
	}

}
