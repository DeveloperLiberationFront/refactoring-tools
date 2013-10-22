package edu.pdx.cs.multiview.extractmethodannotations;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.AnnotationCollection;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.BadSelectionAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.PAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.SelectionAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.visitors.LocalVariableVisitor;
import edu.pdx.cs.multiview.extractmethodannotations.visitors.ScopedControlFlowVisitor;
import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.jface.annotation.AnnotationPainter;
import edu.pdx.cs.multiview.util.eclipse.EclipseHacks;

/**
 * Adds and removes extract method annotations
 * 
 * @author emerson
 */
@SuppressWarnings("restriction")
public class SwitchModeAction extends AnnotatedEditorAction {

	private AnnotationCollection annotations = new AnnotationCollection();
	
	public void init(IWorkbenchWindow window) {
		super.init(window);//TODO: delete
	}
	
	protected boolean toggleOn() {
		
		try {
			annotateEditor(getPainter().getSelection(),new NullProgressMonitor());
		} catch (JavaModelException e) {
			Activator.logError(e);
		} catch (Exception e) {
			Activator.logError(e);
		}

		return true;
	}

	protected void toggleOff() {
		getPainter().replaceAnnotations(
				PAnnotation.toArray(annotations.getAnnotations()), 
				null
			);
		
		annotations.markSelectionAsDeleted();
			
	}

	protected void annotateEditor(ITextSelection selection, IProgressMonitor monitor) throws Exception, JavaModelException {
		
		annotations.clear();
		
		if(selection==null)
			selection = getPainter().getSelection();
		
		ExtractMethodRefactoring refactoring = extractMethodRefactoring(selection);
		RefactoringStatus status = refactoring.checkInitialConditions(monitor);
		CompilationUnit astRoot = EclipseHacks.getRoot(refactoring);
		
		if(badSelection(status)){
			
			annotations.setBadSelectionAnn(BadSelectionAnnotation.
					newAnnotationFor(selection,status.getEntries()[0].getMessage()));
			
		}else{
			
			ScopedControlFlowVisitor controlVisitor = new ScopedControlFlowVisitor(selection);
			astRoot.accept(controlVisitor);
			
			if(badSelection(controlVisitor)){
				annotations.setBadSelectionAnn(BadSelectionAnnotation.
						newAnnotationFor(selection,"Refactoring Annotations currently handle only" +
								" statement(s) for extraction.  Please select one or more statement(s)."));
			}else{
					
				
				annotations.setControlFlowAnns(controlVisitor.
						getAnnotations(containsConditionalReturn(status)));
				
				LocalVariableVisitor varVisitor = new LocalVariableVisitor();			
				astRoot.accept(varVisitor);
				
				annotations.setVariableAnns(varVisitor.getAnnotations(selection));
			}
		}
		
		annotations.setSelectionAnn(SelectionAnnotation.annotationFor(selection));
		getPainter().replaceAnnotations(null, annotations.getAnnotationMap());
	}

	private boolean badSelection(RefactoringStatus status) {
		return 	containsCode(status,RefactoringCoreMessages.StatementAnalyzer_doesNotCover) ||
				containsCode(status,RefactoringCoreMessages.StatementAnalyzer_end_of_selection) ||
				containsCode(status,RefactoringCoreMessages.StatementAnalyzer_beginning_of_selection) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodRefactoring_no_set_of_statements) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_parent_mismatch) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_only_method_body) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_single_expression_or_set) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_leftHandSideOfAssignment) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_after_do_keyword) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_super_or_this) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_variable_declaration_fragment) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_for_initializer) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_for_updater) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_variable_declaration) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_type_reference) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_method_name_reference) ||
				containsCode(status,RefactoringCoreMessages.ExtractMethodAnalyzer_cannot_extract_name_in_declaration) ||
				containsCode(status,RefactoringCoreMessages.StatementAnalyzer_try_statement) ||
				containsCode(status,RefactoringCoreMessages.StatementAnalyzer_catch_argument);
	}
	
	private boolean badSelection(ScopedControlFlowVisitor controlVisitor) {
		return !controlVisitor.isAStatementSelected();
	}
	
	/**
	 * @param status
	 * 
	 * @return		whether status is telling us that an early return was detected
	 */
	private boolean containsConditionalReturn(RefactoringStatus status) {
		//see refactoring.properties
		return containsCode(status, RefactoringCoreMessages.FlowAnalyzer_execution_flow);
	}

	private boolean containsCode(RefactoringStatus status, String message) {
		boolean badSelection = false;
		for(RefactoringStatusEntry entry : status.getEntries())
			badSelection |= entry.getMessage().contains(message);
		return badSelection;
	}


	/**
	 * @param selection
	 * 
	 * @return
	 */
	private ExtractMethodRefactoring extractMethodRefactoring(ITextSelection selection) 
																	throws CoreException {
		
		ExtractMethodRefactoring cheat;
		
			Object[] arguments = new Object[] {JDTUtils.getCompilationUnit(getEditor()), 
												selection.getOffset(), selection.getLength()};
			try{
				//for Eclipse 3.2 RC5
				Constructor constructor = ExtractMethodRefactoring.class.getConstructors()[0];
				constructor.setAccessible(true);
				cheat = (ExtractMethodRefactoring)constructor.newInstance(arguments);
			}catch(Exception e1){
					try{
						//for Eclipse 3.1.2
						Method method = ExtractMethodRefactoring.class.getMethod("create", new Class[]{ICompilationUnit.class,int.class,int.class});
						method.setAccessible(true);
						cheat = (ExtractMethodRefactoring)method.invoke(ExtractMethodRefactoring.class, arguments);
					}catch(Exception e2){
						Activator.logError(e1);
						Activator.logError(e2);
						cheat = null;
					}
			}
		
		return cheat;
	}


	private Map<AbstractTextEditor, AnnotationPainter> painters = 
		new HashMap<AbstractTextEditor, AnnotationPainter>();
	
	public AnnotationPainter getPainter() {
		if(!painters.containsKey(getEditor())){
			painters.put(getEditor(),new AnnotationPainter(EclipseHacks.getSourceViewer(getEditor())));
		}
		return painters.get(getEditor());
	}
	
	public void dispose(){
		super.dispose();
		for(AnnotationPainter p : painters.values())
			p.dispose();
	}
}