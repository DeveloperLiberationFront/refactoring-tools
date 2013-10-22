package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.RegionList;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

/**
 * I create ASTRegions out of program elements within 
 * method or initializers.
 * 
 *  Subclasses should override my 
 *  {@link AstRegionVisitor#isAcceptable(ASTNode)} method.
 * 
 * @author emerson
 */
abstract class AstRegionVisitor extends ASTVisitor {
	private final RegionList regions;
	private final WrappedEditor e;
	private Stack<ASTNode> contextStack =
		new Stack<ASTNode>();

	public AstRegionVisitor(WrappedEditor e) {
		this.e = e;
		regions = RegionList.newSortedOnLength();
	}
	
	public Regions getRegions(){
		return regions;
	}

	public boolean visit(Block b){
		if(b.getParent() instanceof MethodDeclaration)
			return push(b);
		else
			return true;
	}

	public void endVisit(Block b){
		if(b.getParent() instanceof MethodDeclaration)
			pop();
	}

	public boolean visit(Initializer a){
		return push(a);
	}

	public void endVisit(Initializer a){
		pop();
	}

	private boolean push(ASTNode a) {
		contextStack.push(a);
		return true;
	}

	private void pop() {
		contextStack.pop();
	}

	public void preVisit(ASTNode node){
		if(!contextStack.isEmpty() && isAcceptable(node)){
			regions.add(new ASTRegion(e,node));
		}
	}
	
	protected abstract boolean isAcceptable(ASTNode node);
}