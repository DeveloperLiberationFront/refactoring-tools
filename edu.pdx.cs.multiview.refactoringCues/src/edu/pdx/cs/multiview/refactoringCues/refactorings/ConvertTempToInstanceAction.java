package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.code.PromoteTempToFieldRefactoring;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;

public class ConvertTempToInstanceAction extends TempAction{


	@Override
	public String getName() {
		return "Convert Local Variable to Field";
	}

	@Override
	protected RefactoringBundle getRefactoring(VariableDeclaration decl, 
												ICompilationUnit icu) throws Exception {
		
		return  new RefactoringBundle(new PromoteTempToFieldRefactoring(decl));
			
	}
}
