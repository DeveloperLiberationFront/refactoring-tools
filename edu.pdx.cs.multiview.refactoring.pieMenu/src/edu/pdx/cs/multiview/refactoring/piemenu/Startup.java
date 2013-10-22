package edu.pdx.cs.multiview.refactoring.piemenu;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.pdx.cs.multiview.refactoring.piemenu.usability.UsabilityTestActivator;
import edu.pdx.cs.multiview.swt.pieMenu.PieMenuActivator;
import edu.pdx.cs.multiview.util.editor.EditorSelectionManager;

@SuppressWarnings("restriction")
public class Startup implements IStartup, ISelectionChangedListener {

	private EditorSelectionManager manager = new EditorSelectionManager();
	
	public void earlyStartup() {
		  final IWorkbench workbench = PlatformUI.getWorkbench();		  
		  workbench.getDisplay().asyncExec(new Runnable() {
		    public void run() {
		      IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();		      
		      manager.listenToLater(window.getActivePage());
		      manager.addSelectionChangedListener(Startup.this);
		    }
		  });
	}

	public void selectionChanged(SelectionChangedEvent event) {
		AbstractTextEditor editor = manager.getEditor();
		if(editor.getSelectionProvider().equals(event.getSelectionProvider()) && 
				editor instanceof JavaEditor){
			
			StyledText styledText = manager.getStyledText();
			
			getBuilder().setEditor((JavaEditor)editor);
			PieMenuActivator.createPieMenuOn(styledText, getBuilder());
		}
	}

	
	private PieMenuBuilder getBuilder() {
		//this can be cleaned out by returning a singleton PieMenuBuilder
		return UsabilityTestActivator.getBuilder();
	}
}
