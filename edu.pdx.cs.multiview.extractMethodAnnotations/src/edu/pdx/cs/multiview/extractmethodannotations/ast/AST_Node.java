package edu.pdx.cs.multiview.extractmethodannotations.ast;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.collections15.Predicate;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;

public class AST_Node<T extends ASTNode> {

	protected T node;
	
	protected AST_Node(T node){
		this.node = node;
	}
	
	public Position getSourceRange() {
		return new Position(node.getStartPosition(),node.getLength());
	}
	
	public int getStartPosition() {
		return node.getStartPosition();
	}
	
	/**
	 * @param nodes
	 * 
	 * @return the node in the collection with the greatest start index
	 */
	public static <T extends AST_Node> T lastNode(Collection<T> nodes) {
	
		if(nodes.isEmpty())
			throw new IllegalArgumentException();
		
		return Collections.max(nodes,nodeComparator());	
	}

	/**
	 * @return	a comparator that can sort nodes by document order
	 */
	public static <T extends AST_Node> Comparator<T> nodeComparator() {
		return new Comparator<T>(){
			public int compare(T o1, T o2) {
				return o1.getStartPosition()-o2.getStartPosition();
			}
		};
	}

	public Position rangeTo(AST_Node node){
		return range(this.node,node.node);
	}
	
	/**
	 * 
	 * @param startNode
	 * @param endNode
	 * 
	 * @return			a position spanning the arguments
	 */
	private static Position range(ASTNode startNode, ASTNode endNode) {
		
		int start = endNode.getStartPosition();
		int length = endOf(startNode) - start;
		
		if(length<0){
			return range(endNode,startNode);
		}
		
		return new Position(start,length);
	}

	public int getLength() {
		return node.getLength();
	}
	
	public boolean before(AST_Node b) {
		return 0 < nodeComparator().compare(this, b);
	}

	public static Position getRange(ASTNode node){
		
		if(node==null)
			return null;
		
		int startPosition = node.getStartPosition();
		int length = node.getLength();
		return new Position(startPosition,length);
	}
	

	public int getEndPosition() {
		return endOf(node);
	}
	
	/**
	 * Returns the end position of the argument
	 * 
	 * @param name
	 * @return
	 */
	protected static int endOf(ASTNode name) {
		return name.getStartPosition()+name.getLength();
	}
	
	public static Predicate<AST_Node> withinPredicate(final ITextSelection scope){
		return new Predicate<AST_Node>(){
			public boolean evaluate(AST_Node e) {
				return within(scope,e);
			}
		};
	}
	
	public static boolean within(ITextSelection scope, ASTNode node){
		
		int scopeEnd = scope.getOffset() + scope.getLength();
		int nodeEnd = node.getStartPosition() + node.getLength();
		
		return scope.getOffset() <= node.getStartPosition() && 
				scopeEnd >= nodeEnd;
	}
	
	public static boolean within(ITextSelection scope, AST_Node node){
		return within(scope,node.node);
	}
	
	public String toString(){
		return node.toString();
	}
	
	public static <T extends ASTNode> AST_Node<T> create(T node){
		return new AST_Node<T>(node);
	}
}

