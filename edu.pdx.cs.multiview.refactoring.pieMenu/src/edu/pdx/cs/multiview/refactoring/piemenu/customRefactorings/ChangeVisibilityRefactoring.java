package edu.pdx.cs.multiview.refactoring.piemenu.customRefactorings;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

public class ChangeVisibilityRefactoring extends Refactoring{

	private Modifier oldModifier;
	private ModifierKeyword newModifier;
	private ICompilationUnit cu;
	
	public ChangeVisibilityRefactoring(ICompilationUnit cu, 
				Modifier oldModifier, ModifierKeyword newModifier){
		this.oldModifier = oldModifier;
		this.newModifier = newModifier;
		this.cu = cu;
	}
	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		//TODO: should really do precondition checking...
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		ASTRewrite rewrite = ASTRewrite.create(oldModifier.getAST());
		
		Modifier newMod = oldModifier.getAST().newModifier(newModifier);
		newMod.setFlags(Flags.AccDefault);
		rewrite.replace(oldModifier, newMod, new TextEditGroup("xxx"));	
		
		TextEdit astEdit = rewrite.rewriteAST();
		
		TextFileChange change = new TextFileChange(cu.getElementName(),
												(IFile)cu.getResource());
		change.setTextType("java");
		change.setEdit(astEdit);
		
		return change;
	}

	@Override
	public String getName() {
		return "Change Visibility";
	}
}