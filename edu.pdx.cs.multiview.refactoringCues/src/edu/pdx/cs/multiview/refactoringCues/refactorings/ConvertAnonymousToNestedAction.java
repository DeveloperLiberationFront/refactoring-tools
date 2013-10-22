package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.code.ConvertAnonymousToNestedRefactoring;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;

public class ConvertAnonymousToNestedAction extends ASTAction<AnonymousClassDeclaration>{

	@Override
	public String getName() {
		return "Convert Anonymous to Nested";
	}

	@Override
	protected RefactoringBundle getRefactoring(AnonymousClassDeclaration node,
			ICompilationUnit cu) throws Exception {
		
		ConvertAnonymousToNestedRefactoring r = 
			new ConvertAnonymousToNestedRefactoring(node);
		
		RefactoringBundle bundle = new RefactoringBundle(r);
		
		r.setClassName(bundle.generateClassName(cu.getSource()));
		
		return bundle;
	}

	@Override
	protected boolean isAcceptable(ASTNode node) {
		return node.getNodeType()==
				ASTNode.ANONYMOUS_CLASS_DECLARATION;
	}
}
