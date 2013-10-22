package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;


@SuppressWarnings("restriction")
public class ExtractMethodFromExpressionAction extends ExtractExpressionAction{
	
	private static ExtractMethodGUI extractMethodGUI;

	@Override
	protected RefactoringBundle getRefactoring(ASTNode node, ICompilationUnit cu)
			throws Exception {
		
		ExtractMethodRefactoring refactoring = new ExtractMethodRefactoring(cu,
				node.getStartPosition(),node.getLength());
		
		RefactoringBundle rb = new RefactoringBundle(refactoring);
		refactoring.setMethodName(rb.generateIdName(cu.getSource()));
		refactoring.setGenerateJavadoc(extractMethodGUI.generateComment);
		refactoring.setThrowRuntimeExceptions(extractMethodGUI.declareRuntimeExceptions);
		
		return rb;
	}

	@Override
	public String getName() {
		return "Extract Method (Expression)";
	}
	
	@Override
	public Composite initConfigurationGUI(Composite parent) {
		return extractMethodGUI = new ExtractMethodGUI(parent);
	}
	
	static class ExtractMethodGUI extends Composite{
				
		public boolean declareRuntimeExceptions = false;
		public boolean generateComment = false;

		public ExtractMethodGUI(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout(1,true));
			
			addExceptionButton();
			addCommentButton();
		}

		private void addCommentButton() {
			final Button comment = new Button(this,SWT.CHECK);
			comment.setText("Generate method comment");
			comment.setSelection(generateComment);
			comment.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					generateComment = comment.getSelection();
				}

				public void widgetSelected(SelectionEvent e) {
					generateComment = comment.getSelection();
				}
				
			});
		}

		private void addExceptionButton() {
			final Button ex = new Button(this,SWT.CHECK);
			ex.setText("Declare thrown runtime exceptions");
			ex.setSelection(declareRuntimeExceptions);
			ex.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					declareRuntimeExceptions = ex.getSelection();
				}

				public void widgetSelected(SelectionEvent e) {
					declareRuntimeExceptions = ex.getSelection();
				}
				
			});
		}
	}
}