package edu.pdx.cs.multiview.refactoringCues.views;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.ui.ProgressMonitorUtil;
import edu.pdx.cs.multiview.util.editor.FastSelectionManager;


@SuppressWarnings("restriction")
public class WrappedEditor extends edu.pdx.cs.multiview.jface.text.WrappedEditor
			implements ISelectionChangedListener{
	
	private ICompilationUnit iCompilationUnit;
	private CompilationUnit compilationUnit;
	
	private Regions interestedRegions;
	
	private FastSelectionManager selManager;
	private ITextSelection lastSelection;
	
	public WrappedEditor(IEditorPart part){
		super((JavaEditor)part);
		selManager = new FastSelectionManager();
		selManager.listenToLater(part.getSite().getPage());
	}
	
	public void addAnnotations(Map<Annotation, Position> map) {			
				
		ASTRegion.prep(editor);				
		getModel().replaceAnnotations(new Annotation[0],map);
		refresh();
	}

	private void refresh() {
		UIJob j = new UIJob("Refreshing annotations"){
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				getViewer().getTextWidget().redraw();
				return Status.OK_STATUS;
			}
		};
		
		j.schedule();
	}

	public void removeAnnotations(Annotation[] annotations){
		getModel().replaceAnnotations(annotations, new HashMap<Object,Object>());
		refresh();
	}

	private IAnnotationModelExtension getModel() {
		return (IAnnotationModelExtension) editor.getViewer().getAnnotationModel();
	}

	public CompilationUnit getCompilationUnit() {
		
		if(compilationUnit==null){
			compilationUnit = ASTProvider.getASTProvider().
				getAST(getICompilationUnit(), ASTProvider.WAIT_YES, 
						new NullProgressMonitor());
		}
		
		return compilationUnit;
	}

	public ICompilationUnit getICompilationUnit() {
		
		if(iCompilationUnit==null){
			iCompilationUnit = JDTUtils.getCompilationUnit(editor);
		}
		
		return iCompilationUnit;
	}

	public static WrappedEditor getActiveEditor(IWorkbenchWindow ww) {
		return new WrappedEditor(ww.getActivePage().getActiveEditor());
	}
	
	public void setSelectionListeners(Regions regions){
		interestedRegions = regions;
		//editor.getSelectionProvider().addSelectionChangedListener(this);
		selManager.addSelectionChangedListener(this);
	}
	
	public void clearListeners(){
		//editor.getSelectionProvider().removeSelectionChangedListener(this);
		selManager.removeSelectionChangedListener(this);
		interestedRegions = null;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		
		if(!(event.getSelection() instanceof ITextSelection))
			return;
		
		ITextSelection selection = (ITextSelection)event.getSelection();
		
		interestedRegions.setSelectedIn(selection);
		lastSelection = selection;
	}

	public IProgressMonitor getProgressMonitor() {
		return ProgressMonitorUtil.getBackgroundMonitor(editor);
	}

	public boolean similarToLastSelection(ITextSelection selection) {
		
		if(selection==null || lastSelection==null)
			return false;
		
		if(selection.getLength()==0 && lastSelection.getLength()==0)
			return false;
		
		return selection.getOffset() == lastSelection.getOffset() ||
				selection.getOffset()+selection.getLength() == lastSelection.getOffset()+lastSelection.getLength();
	}
	
	public void disableDoubleClickStrategy() {
		editor.getViewer().getTextWidget().setDoubleClickEnabled(false);
	}

	public void restoreDoubleClickStrategy() {
		editor.getViewer().getTextWidget().setDoubleClickEnabled(true);
	}
}