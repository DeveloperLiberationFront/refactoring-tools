package edu.pdx.cs.multiview.refactoringCues.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.pdx.cs.multiview.refactoringCues.actions.ActivateRefactoring;
import edu.pdx.cs.multiview.refactoringCues.refactorings.RefactoringAction;
import edu.pdx.cs.multiview.refactoringCues.refactorings.RefactoringActionManager;


public class RefactoringCueView extends ViewPart implements ExpandListener {
	
	private ExpandBar bar;
	private static RefactoringCueView view;				

	public RefactoringCueView() {
		view = this;
	}

	@Override
	public void createPartControl(Composite parent) {
		bar = new ExpandBar(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		for(RefactoringAction<?> action : RefactoringActionManager.getActions()){
			Composite c = action.initConfigurationGUI(bar);
			createExpandItem(action, c);
		}
		
		bar.addExpandListener(this);
		addButtons();
	}

	private void addButtons() {
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		Action doRefactoring = new Action(){
			public void run() {
				ActivateRefactoring.execute();
			}
		};
		doRefactoring.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK));
		doRefactoring.setToolTipText("Executes outstanding refactorings");
		manager.add(doRefactoring);
	}

	private void createExpandItem(RefactoringAction<?> action, Composite c) {
		ExpandItem item = new ExpandItem(bar,SWT.NONE,0);
		item.setText(action.getName());
		item.setControl(c);
//		item.setImage(PlatformUI.getWorkbench().
//				getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
		item.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item.setData(action);
	}

	public void setFocus() {
		bar.setFocus();
	}
	
	public void itemCollapsed(ExpandEvent event) {
	
		Widget item = event.item;
		deActivateCue(item);
	}

	private void deActivateCue(Widget item) {
		RefactoringAction<?> action = (RefactoringAction<?>)item.getData();
		action.deActivateSelectionCue();
	}

	public void itemExpanded(ExpandEvent event) {
		
		Widget selectedItem = event.item;
		
		for(ExpandItem someItem : bar.getItems())
			if(someItem.getExpanded() && someItem!=selectedItem){
				unExpandAndDeActivate(someItem);
			}
		
		RefactoringAction<?> action = (RefactoringAction<?>)selectedItem.getData();
		action.activateSelectionCue(getEditor());
	}

	private void unExpandAndDeActivate(ExpandItem someItem) {
		someItem.setExpanded(false);//no event generated by default...
		deActivateCue(someItem);//...must force
				
		IEditorPart e = getViewSite().getPage().getActiveEditor();
		getViewSite().getPage().activate(e);
	}

	private WrappedEditor getEditor() {
		return WrappedEditor.getActiveEditor(getSite().getWorkbenchWindow());
	}

	public static void collapse(RefactoringAction<?> action) {
		
		if(view!=null)
			for(ExpandItem item : view.bar.getItems())
				if(item.getData()==action && item.getExpanded()){
					view.unExpandAndDeActivate(item);					
				}	
	}
}
