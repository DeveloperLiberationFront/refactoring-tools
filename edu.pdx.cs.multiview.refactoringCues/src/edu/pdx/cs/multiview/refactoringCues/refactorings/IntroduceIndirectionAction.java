package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.corext.refactoring.code.IntroduceIndirectionRefactoring;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;

@SuppressWarnings("restriction")
public class IntroduceIndirectionAction extends MemberAction {

	public IntroduceIndirectionAction() {
		super(true,false);
	}
	
	@Override
	public String getName() {
		return "Introduce Indirection";
	}

	@Override
	protected RefactoringBundle getRefactoring(IMember m) throws Exception {
		
		IntroduceIndirectionRefactoring refactoring = 
			new IntroduceIndirectionRefactoring((IMethod)m);//TODO: no casting
		
		RefactoringBundle rb = new RefactoringBundle(refactoring);
		String name = rb.generateIdName(m.getCompilationUnit().getSource());
		
		refactoring.setIntermediaryMethodName(name);
		
		return rb;
	}

}
