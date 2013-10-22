package edu.pdx.cs.multiview.refactoringCues.refactorings;


import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;

@SuppressWarnings("restriction")
public class PullUpAction extends MemberAction{

	public PullUpAction() {
		super(true,true);
	}

	//TODO: store these for later... maybe just go to general refactoring store
	private boolean deleteOriginal = false;
	private boolean useDestinationTypeWherePossible = true;
	
	@Override
	public String getName() {
		return "Pull Up";
	}

	@Override
	protected RefactoringBundle getRefactoring(IMember m) throws Exception{
		
		CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(m.getJavaProject());
		IMember[] members = new IMember[] {m};
		PullUpRefactoringProcessor proc = new PullUpRefactoringProcessor(members, settings);		
		Refactoring refactoring = new ProcessorBasedRefactoring(proc);

		IType[] candidateTypes = proc.getCandidateTypes(new RefactoringStatus(), 
				new NullProgressMonitor());
		
		proc.setDestinationType(candidateTypes[candidateTypes.length-1]);
		
		if(deleteOriginal && m instanceof IMethod)
			proc.setDeletedMethods(new IMethod[] {(IMethod)m});
		
		proc.setInstanceOf(useDestinationTypeWherePossible);
		
		return new RefactoringBundle(refactoring);
	}

	@Override
	public Composite initConfigurationGUI(Composite parent) {
		
		//TODO: static configuration options are OK, but dynamic ones
		//		(like what class to pull into) must have some other way of configuring
		//		either;
		//			-Add refactoring elements to Refactoring Cue View, or
		//			-Add configuration in popups next to the item
		
		Composite c = new Composite(parent,SWT.NONE);
		c.setLayout(new GridLayout(1,true));
		
		addDeleteOption(c);
		addUseDestinationTypeOption(c);
		
		return c;
	}

	private void addDeleteOption(Composite c) {
		final Button b = new Button(c,SWT.CHECK);
		b.setText("Delete original method");
		b.setSelection(deleteOriginal);
		b.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				deleteOriginal = b.getSelection();
			}

			public void widgetSelected(SelectionEvent e) {
				deleteOriginal = b.getSelection();
			}
			
		});
	} 
	
	private void addUseDestinationTypeOption(Composite c) {
		final Button b = new Button(c,SWT.CHECK);
		b.setText("Use destination type where possible");
		b.setSelection(useDestinationTypeWherePossible);
		b.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				useDestinationTypeWherePossible = b.getSelection();
			}

			public void widgetSelected(SelectionEvent e) {
				useDestinationTypeWherePossible = b.getSelection();
			}
			
		});
	} 
}