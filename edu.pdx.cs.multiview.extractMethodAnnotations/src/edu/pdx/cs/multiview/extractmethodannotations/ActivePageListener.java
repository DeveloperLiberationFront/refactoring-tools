package edu.pdx.cs.multiview.extractmethodannotations;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

/**
 * 
 * I listen to the current editor.  Clients must first call my 
 * init method, then getEditor will always return the active editor.
 * 
 * @author emerson
 */
public class ActivePageListener{

	private IWorkbenchWindow window;
	
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	protected AbstractDecoratedTextEditor getEditor() {
		return (AbstractDecoratedTextEditor)window.getActivePage().getActiveEditor();
	}
}