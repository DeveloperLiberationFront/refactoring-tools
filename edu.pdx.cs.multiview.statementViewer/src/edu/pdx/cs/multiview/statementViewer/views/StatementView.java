package edu.pdx.cs.multiview.statementViewer.views;


import java.util.List;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.pdx.cs.multiview.jface.ComparisonTextSelection;
import edu.pdx.cs.multiview.jface.ICompilationUnitListener;
import edu.pdx.cs.multiview.statementViewer.Activator;
import edu.pdx.cs.multiview.statementViewer.editparts.ASTEditPart;
import edu.pdx.cs.multiview.statementViewer.editparts.EditPartFactory;
import edu.pdx.cs.multiview.statementViewer.models.CUModel;
import edu.pdx.cs.multiview.util.editor.CUEditorSelectionManager;

/**
 * I am a view that shows an outlines of the statements in
 * a text editor.
 * 
 * @author emerson
 */
public class StatementView extends ViewPart implements ICompilationUnitListener{

	//TODO: if/while tags
	//TODO: show anonymous classes
	//TODO:  flatten somewhat:
	//			for instance, "else if" is unecessarily nested
	
	//the main SWT component in the view
	private GraphicalViewerImpl viewer;
	
	protected CUEditorSelectionManager editorManager;

	private CUModel currentCU;

	public StatementView(){
		editorManager = new CUEditorSelectionManager(this);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		viewer = new ScrollingGraphicalViewer();
		viewer.setEditDomain(new DefaultEditDomain(null));
		viewer.createControl(parent);
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setEditPartFactory(new EditPartFactory());
		
		addSelectionListener();
		addKeyBindings();
		
		if(currentCU!=null){
			viewer.setContents(currentCU);
		}
	}

	private void addKeyBindings() {
		//we need this to get the standard key bindings
		GraphicalViewerKeyHandler keyHandler = 
			new GraphicalViewerKeyHandler(viewer);
		
		keyHandler.put(KeyStroke.getPressed(SWT.ARROW_LEFT, 0),new Action(){
			@Override
			public void run(){
				focusOnParent();
			}
		});
		
		keyHandler.put(KeyStroke.getPressed(SWT.ARROW_RIGHT, 0),new Action(){
			@Override
			public void run(){
				focusOnChild();
			}
		});
		
		viewer.setKeyHandler(keyHandler);
	}
	
	protected void focusOnParent() {
		select((ASTEditPart)viewer.getFocusEditPart().getParent());
	}
	
	protected void focusOnChild() {
		List children = viewer.getFocusEditPart().getChildren();
		if(!children.isEmpty())
			select((ASTEditPart)children.get(0));
	}

	private void select(ASTEditPart part) {
		int startPosition = part.getModel().getASTNode().getStartPosition();
		int length = part.getModel().getASTNode().getLength();
		
		editorManager.setSelection(startPosition, length);
		viewer.select(part);
	}

	private void addSelectionListener() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection() instanceof IStructuredSelection && 
					!event.getSelection().isEmpty()){
					Object[] sel = ((IStructuredSelection)event.getSelection()).toArray();
					Object firstSel = sel[0];
					Object lastSel = sel[sel.length-1];
					if(firstSel instanceof ASTEditPart && lastSel instanceof ASTEditPart){
						
						ASTEditPart firstPart = (ASTEditPart)firstSel;
						ASTEditPart lastPart = (ASTEditPart)lastSel;
						
						//no selection
						if(firstPart.getSelected()==EditPart.SELECTED_NONE)
							return;
						
						ASTNode firstNode = firstPart.getModel().getASTNode();
						ASTNode lastNode = lastPart.getModel().getASTNode();
						
						if(firstNode.getStartPosition()>lastNode.getStartPosition()){
							ASTNode swap = firstNode;
							firstNode = lastNode;
							lastNode = swap;
						}
							
						int start = firstNode.getStartPosition();						
						int end = lastNode.getStartPosition()+lastNode.getLength();
						
						editorManager.setSelection(start,end-start);
					}
				}
					
			}
		});
	}
	
	@Override
	public void setFocus() {
		//I'd like to activate the editor, but this doesn't seem to work
		//editorManager.getEditor().getEditorSite().a
//		IEditorPart p = editorManager.getEditor();
//		if(p!=null){
//			IWorkbenchPage page = p.getEditorSite().getPage();
//			page.activate(p);
//		}
		//editorManager.getEditor().setFocus();
		viewer.getControl().setFocus();
		
	}
	
	@Override
	public void init(IViewSite site) throws PartInitException{
		super.init(site);
		try{
			editorManager.listenToLater(site);
		}catch(Exception e){
			Activator.logError(e);
		}
	}
	
	@Override
	public void dispose(){
		super.dispose();
		editorManager.dispose();
	}
	
	public void compilationUnitChanged(String source) {
		
		if(source!=null)
			loadUnit(source);
		else
			viewer.setContents("This view will be populated when an editor is opened " +
					"on a java file.");
	}
	
	private void loadUnit(String source) {		
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		
		currentCU = new CUModel(astRoot);
		if(viewer!=null)
			viewer.setContents(currentCU);
		
		currentCU.calculateWhiteSpace(source);
	}

	//TODO: when view is deactivated, am I still listening?  I shouldn't
	
	public void selectionChanged(SelectionChangedEvent event) {
		
		ITextSelection selection = event.getSelection() instanceof ITextSelection ?
				((ITextSelection)event.getSelection()) : null;									

		if(selection!=null){			

			ASTEditPart primary = 
				getRoot().setSelected(new ComparisonTextSelection(selection));
			//TODO: also consider revealing the top of the method, if possible
			if(primary!=null)
				primary.reveal(viewer);
		}
	}

	private ASTEditPart getRoot() {
		ASTEditPart root = (ASTEditPart)viewer.getRootEditPart().getContents();
		return root;
	}
}