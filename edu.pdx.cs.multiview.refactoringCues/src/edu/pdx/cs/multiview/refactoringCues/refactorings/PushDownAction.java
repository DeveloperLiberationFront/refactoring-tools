package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.corext.refactoring.structure.PushDownRefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;

@SuppressWarnings("restriction")
public class PushDownAction extends MemberAction{

	public PushDownAction() {
		super(true,true);
	}

	@Override
	public String getName() {
		return "Push Down";
	}

	@Override
	protected RefactoringBundle getRefactoring(IMember t) throws Exception {
		
		IMember[] members = new IMember[] {t};		
		return new RefactoringBundle(new ProcessorBasedRefactoring(new PushDownRefactoringProcessor(members)));
	}

}
