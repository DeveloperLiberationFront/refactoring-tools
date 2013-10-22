package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.jface.text.LinkModeManager;
import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.AstMatchingNodeFinder;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

public abstract class RefactoringAction<T>{
	
	public boolean isOutstanding() {
		return regions!=null && !regions.isEmpty();
	}

	public void deActivateSelectionCue() {
			
		if(regions==null)
			return;
		
		WrappedEditor editor = null;
		
		Map<WrappedEditor,List<Annotation>> map = 
			new HashMap<WrappedEditor, List<Annotation>>();
		
		for(ASTRegion region : regions){
			editor = region.getEditor();
			if(!map.containsKey(editor))
				map.put(editor, new ArrayList<Annotation>());
			region.addTo(map.get(editor));
		}
		
		for (Map.Entry<WrappedEditor, List<Annotation>> entry : map
				.entrySet()) {

			editor = entry.getKey();
			List<Annotation> regions = entry.getValue();
			editor.removeAnnotations(regions.toArray(new Annotation[regions.size()]));
			editor.clearListeners();
		}	
		
		regions = null;
		
		editor.restoreDoubleClickStrategy();
	}

	public void executeRefactoring(){
		executeRefactoring(regions.withOnlyActive());
	}
	
	private void executeRefactoring(Regions regions){
		final Collection<T> ts = getSelectedItems(regions);
		if(ts.isEmpty())
			return;
		
		WrappedEditor editor = regions.first().getEditor();
		IProgressMonitor monitor = editor.getProgressMonitor();
		
		monitor.beginTask("Performing " + getName(), ts.size()*3);
		
		LinkModeManager links = new LinkModeManager(editor);
		
		//TODO: to get faster, a bunch of these might be able to be grouped together, either
		//			by aggregating changes, or by aggregating elements (e.g., pull up)
		for(T t : ts){
			try {
				
				RefactoringBundle bundle = getRefactoring(t);
				Refactoring r = bundle.getRefactoring();
				
				RefactoringStatus result = r.checkAllConditions(new SubProgressMonitor(monitor,1));
				if(!result.isOK())
					throw new Exception(result.toString());
				
				r.	createChange(new SubProgressMonitor(monitor,1)).
					perform(new SubProgressMonitor(monitor,1));				
				
				links.add(bundle);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		links.activateLinks();
		
		monitor.done();
	}
	

	 //TODO: add "repurpose hotkeys" button to intercept usual 
	//		refactoring keys to activate selection modes
	
	protected abstract RefactoringBundle getRefactoring(T t) throws Exception;

	protected abstract Collection<T> getSelectedItems(Regions regions2);

	public abstract String getName();

	public void activateSelectionCue(WrappedEditor activeEditor) {
		
		if(activeEditor!=null){
			showRegions(activeEditor);
			activeEditor.setSelectionListeners(regions);
		}
		
		activeEditor.disableDoubleClickStrategy();
	}

	private void showRegions(WrappedEditor e) {
		
		e.addAnnotations(getSelectionAnnotations(e));
	}

	private Map<Annotation, Position> 
				getSelectionAnnotations(WrappedEditor e) {
		
			
		Map<Annotation, Position> annotations = new HashMap<Annotation, Position>();
			
		for(ASTRegion region : getSelectionRegions(e)){
			region.addTo(annotations);
		}
		
		return annotations;
	}

	private Regions regions;
	
	public Iterable<ASTRegion> getSelectionRegions(WrappedEditor activeEditor){
		if(regions==null)
			regions = calculateRegions(activeEditor);
		return regions;
	}

	protected abstract Regions calculateRegions(WrappedEditor activeEditor);

	protected ICompilationUnit getCU(ASTNode node) {
		
		if(node instanceof CompilationUnit)
			return (ICompilationUnit)((CompilationUnit)node).getJavaElement();
		else
			return getCU(node.getParent());
	}

	protected <NodeType extends ASTNode> NodeType reparseForNode(ICompilationUnit cu, NodeType original) {
		
		CompilationUnit newAST = 
			JDTUtils.parseCompilationUnit(cu, true);
		
		ASTNode[] nodes = pruneNodes(original,AstMatchingNodeFinder.findMatchingNodes(newAST, original));
		
		if(nodes.length!=1)
			System.err.println("Didn't find right node on re-parse!");
		
		return (NodeType)nodes[0];
	}

	/**
	 * Remove nodes that are clearly not equal.  Subclasses may choose to
	 * override if the type of T is low-level (such as SimpleNames).
	 */
	protected <NodeType extends ASTNode> ASTNode[] pruneNodes(NodeType original, ASTNode[] nodes) {
		return nodes;
	}

	/**
	 * Create and return the visual component for configuring this 
	 * refactoring.
	 * 
	 * @param parent
	 * @return
	 */
	public Composite initConfigurationGUI(Composite parent) {
		Composite c = new Composite(parent,SWT.NONE);
		c.setLayout(new FillLayout());
		Label l = new Label(c,SWT.NONE);
		l.setText("No configuration options.");
		l.setAlignment(SWT.CENTER);
		return c;
	}
}