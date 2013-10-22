package edu.pdx.cs.multiview.refactoringCues.refactorings;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractTempRefactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.refactoringCues.refactorings.ExtractMethodFromExpressionAction.ExtractMethodGUI;



@SuppressWarnings("restriction")
public class ExtractTempAction extends ExtractExpressionAction{
	@Override
	public String getName() {
		return "Extract Local Variable";
	}

	@Override
	protected RefactoringBundle getRefactoring(ASTNode node, 
											ICompilationUnit icu) throws Exception {
		
		CompilationUnit root = (CompilationUnit)node.getRoot();
		
		ExtractTempRefactoring refactoring = new ExtractTempRefactoring(root,node.getStartPosition(),node.getLength());
		
		
		refactoring.setDeclareFinal(gui.declareAsFinal);
		refactoring.setReplaceAllOccurrences(gui.removeDuplicates);
		
		RefactoringBundle rb = new RefactoringBundle(refactoring);
		refactoring.setTempName(rb.generateIdName(icu.getSource()));
		
		return rb;		
	}
	
	private ExtractTempGUI gui;
	
	@Override
	public Composite initConfigurationGUI(Composite parent) {
		return gui = new ExtractTempGUI(parent);
	}
	
	static class ExtractTempGUI extends Composite{
				
		public boolean declareAsFinal = false;
		public boolean removeDuplicates = false;

		public ExtractTempGUI(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout(1,true));
			
			addDeclFinalButton();
			addRemovedupsButton();
		}

		private void addRemovedupsButton() {
			final Button comment = new Button(this,SWT.CHECK);
			comment.setText("Replace all occurrences with variable");
			comment.setSelection(removeDuplicates);
			comment.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					removeDuplicates = comment.getSelection();
				}

				public void widgetSelected(SelectionEvent e) {
					removeDuplicates = comment.getSelection();
				}
				
			});
		}

		private void addDeclFinalButton() {
			final Button ex = new Button(this,SWT.CHECK);
			ex.setText("Declare the local variable as 'final'");
			ex.setSelection(declareAsFinal);
			ex.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					declareAsFinal = ex.getSelection();
				}

				public void widgetSelected(SelectionEvent e) {
					declareAsFinal = ex.getSelection();
				}
				
			});
		}
	}
}