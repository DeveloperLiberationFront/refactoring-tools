package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractConstantRefactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;

public class ExtractConstantAction extends ASTAction<ASTNode>{
	
	private boolean replace = true, qualifyRefs = false;	
	
	@Override
	protected boolean isAcceptable(ASTNode node) {
		switch (node.getNodeType()) {
			case ASTNode.BOOLEAN_LITERAL :
			case ASTNode.CHARACTER_LITERAL :
			case ASTNode.NULL_LITERAL :
			case ASTNode.NUMBER_LITERAL :
			case ASTNode.STRING_LITERAL :
				return true;					
			default :
				return false;
		}
	}

	@Override
	public String getName() {
		//TODO: change all these getNames...
		return RefactoringCoreMessages.ExtractConstantRefactoring_name;
	}

	@Override
	protected RefactoringBundle getRefactoring(ASTNode node, 
												ICompilationUnit cu)
			throws Exception {
		
		ExtractConstantRefactoring r = new ExtractConstantRefactoring(
					cu,node.getStartPosition(),node.getLength());
		
		r.setReplaceAllOccurrences(replace);
		r.setQualifyReferencesWithDeclaringClassName(qualifyRefs);
		
		RefactoringBundle bundle = new RefactoringBundle(r);
		r.setConstantName(bundle.generateConstantName(cu.getSource()));
		
		return bundle;
	}

	@Override
	public Composite initConfigurationGUI(Composite parent) {
		
		Composite c = new Composite(parent,SWT.NONE);
		c.setLayout(new GridLayout(1,true));
		
		addReplaceBox(c);
		addQualifyBox(c);
		
		return c;
	}

	private void addReplaceBox(Composite c) {
		final Button b = new Button(c,SWT.CHECK);
		b.setLayoutData(new GridData());
		b.setText(RefactoringCoreMessages.ExtractConstantRefactoring_replace_occurrences);
		b.setSelection(replace);
		b.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				replace = b.getSelection();
			}

			public void widgetSelected(SelectionEvent e) {
				replace = b.getSelection();
			}
			
		});
	} 
	
	private void addQualifyBox(Composite c) {
		final Button b = new Button(c,SWT.CHECK);
		b.setText(RefactoringCoreMessages.ExtractConstantRefactoring_qualify_references);
		b.setSelection(qualifyRefs);
		b.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				qualifyRefs = b.getSelection();
			}

			public void widgetSelected(SelectionEvent e) {
				qualifyRefs = b.getSelection();
			}
			
		});
	} 
}
