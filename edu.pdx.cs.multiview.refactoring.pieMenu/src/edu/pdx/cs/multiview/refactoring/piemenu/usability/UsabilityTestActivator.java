package edu.pdx.cs.multiview.refactoring.piemenu.usability;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

import edu.pdx.cs.multiview.refactoring.piemenu.PieMenuBuilder;

public class UsabilityTestActivator implements IWorkbenchWindowPulldownDelegate2  {
	
	public void init(IWorkbenchWindow window) {}
	public void run(IAction action) {}
	public void selectionChanged(IAction action, ISelection selection) {}	
	public void dispose() {}

	private Menu fMenu;
	
	public Menu getMenu(Menu parent) {
		setMenu(new Menu(parent.getParent(),SWT.CHECK));
		fillMenu(fMenu);
		return fMenu;
	}
	
	private abstract class MyAction extends Action{
		
		public boolean isChecked(){return builder()==current;}
		public int getStyle(){return AS_CHECK_BOX;}
		public void run(){current = builder();}
		
		public abstract String getText(); 
		protected abstract PieMenuBuilder builder();
	}

	private void fillMenu(final Menu menu) {
		
		MyAction standard = new MyAction(){
			public String getText(){return "Standard";}
			protected PieMenuBuilder builder() {return standardBuilder;}	
		};
		
		MyAction logical = new MyAction(){
			public String getText(){return "Test: Logical";}
			protected PieMenuBuilder builder() {return logicalBuilder;}
		};
		
		MyAction random = new MyAction(){
			public String getText(){	return "Test: Random";}
			protected PieMenuBuilder builder() { return randomBuilder;}
		};
		
		for(Action a : new Action[] {standard,logical,random}){
			new ActionContributionItem(a).fill(menu, -1);	
		}
		
		menu.setDefaultItem(menu.getItem(0));
	}

	public Menu getMenu(Control parent) {
		setMenu(new Menu(parent));
		fillMenu(fMenu);
		return fMenu;
	}
	
	private void setMenu(Menu menu) {
		if (fMenu != null) {
			fMenu.dispose();
		}
		fMenu = menu;
	}
	
	public static PieMenuBuilder getBuilder(){
		return current;
	}

	private static PieMenuBuilder standardBuilder = new PieMenuBuilder();
	private static PieMenuBuilder logicalBuilder = new LogicalBuilder();
	private static PieMenuBuilder randomBuilder = new RandomBuilder();
	private static PieMenuBuilder current = standardBuilder;
}
