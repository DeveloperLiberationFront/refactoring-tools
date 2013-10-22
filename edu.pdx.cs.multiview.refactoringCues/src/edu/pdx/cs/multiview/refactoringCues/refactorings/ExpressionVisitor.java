package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

class ExpressionVisitor extends AstRegionVisitor{
	
	public ExpressionVisitor(WrappedEditor e) {
		super(e);
	}

	protected boolean isAcceptable(ASTNode node) {
		return JDTUtils.isExtractableExpression(node);
	}
}