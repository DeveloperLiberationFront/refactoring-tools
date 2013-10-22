package edu.pdx.cs.multiview.refactoringCues.refactorings;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineTempRefactoring;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;



public class InlineTempAction extends TempAction{
	
	@Override
	public String getName() {
		return "Inline Temporary Refactoring";
	}

	@Override
	protected RefactoringBundle getRefactoring(VariableDeclaration decl, 
												ICompilationUnit icu) throws Exception {
		
		CompilationUnit cu = (CompilationUnit)decl.getRoot();
		return new RefactoringBundle(
				new InlineTempRefactoring(icu,cu,decl.getStartPosition(),decl.getLength())
			);
			
	}
}

