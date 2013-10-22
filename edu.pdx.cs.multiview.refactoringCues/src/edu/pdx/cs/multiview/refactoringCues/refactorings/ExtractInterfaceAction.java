package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.Collection;

import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

public class ExtractInterfaceAction extends RefactoringAction {

	@Override
	protected Regions calculateRegions(WrappedEditor activeEditor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return RefactoringCoreMessages.ExtractInterfaceRefactoring_name;
	}

	@Override
	protected RefactoringBundle getRefactoring(Object t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection getSelectedItems(Regions regions2) {
		// TODO Auto-generated method stub
		return null;
	}

}
