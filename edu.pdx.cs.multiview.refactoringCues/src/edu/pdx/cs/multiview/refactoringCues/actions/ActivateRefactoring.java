package edu.pdx.cs.multiview.refactoringCues.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import edu.pdx.cs.multiview.refactoringCues.refactorings.RefactoringAction;
import edu.pdx.cs.multiview.refactoringCues.refactorings.RefactoringActionManager;
import edu.pdx.cs.multiview.refactoringCues.views.RefactoringCueView;

public class ActivateRefactoring implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		execute();
		
		return null;
	}

	public static void execute() {
		RefactoringAction<?> action = RefactoringActionManager.executeOutstandingActions();
		RefactoringCueView.collapse(action);
	}

	public boolean isEnabled() {return true;}
	public boolean isHandled() {return true;}
	
	public void addHandlerListener(IHandlerListener handlerListener) {}
	public void removeHandlerListener(IHandlerListener handlerListener) {}
	
	public void dispose() {}
}
