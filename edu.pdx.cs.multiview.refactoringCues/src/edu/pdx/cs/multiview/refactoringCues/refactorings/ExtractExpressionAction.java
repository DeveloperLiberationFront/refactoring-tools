package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;

public abstract class ExtractExpressionAction extends ASTAction<ASTNode> {

	@Override
	protected <T extends ASTNode> ASTNode[] pruneNodes(T original, ASTNode[] nodes) {
		
		List<ASTNode> nodeList = Arrays.asList(nodes);
		List<ASTNode> newList = new ArrayList<ASTNode>();
		
		for(ASTNode newNode : nodeList){
			if(inIsomorphicParents(original,newNode)){
				newList.add(newNode);
			}
		}
		
		return newList.toArray(new ASTNode[newList.size()]);
	}

	protected boolean inIsomorphicParents(ASTNode a, ASTNode b) {
		
		if(a==null && b==null)
			return true;
		
		if(a==null || b==null)
			return false;
		
		if(!a.getClass().equals(b.getClass()))
			return false;
		
		if(a instanceof MethodDeclaration){
			MethodDeclaration declA = (MethodDeclaration)a;
			MethodDeclaration declB = (MethodDeclaration)b;
			
			String aName = declA.getName().getFullyQualifiedName();
			String bName = declB.getName().getFullyQualifiedName();
			
			if(!aName.equals(bName)){
				return false;
			}
		}
		
		return inIsomorphicParents(a.getParent(), b.getParent());
	}

	@Override
	protected boolean isAcceptable(ASTNode node) {
		return JDTUtils.isExtractableExpression(node);
	}
}
