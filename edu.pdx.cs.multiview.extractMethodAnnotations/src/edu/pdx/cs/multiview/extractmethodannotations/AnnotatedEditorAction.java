package edu.pdx.cs.multiview.extractmethodannotations;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * An abstract editor that manages an annotated editor.  This action toggles.
 * 
 * @author emerson
 *
 */
public abstract class AnnotatedEditorAction extends ActivePageListener 
						implements IWorkbenchWindowActionDelegate{


	private boolean isActive = false;
	
	public void selectionChanged(IAction action, ISelection selection) {}
	
	public void toggle(){
		run(null);
	}
	
	public void run(IAction action) {
		
		boolean toggled = true;
		
		try{
			if(isActive)
				toggleOff();
			else
				toggled = toggleOn();
			
		}catch(Exception e){
			Activator.logError(e);
		}finally{
			if(toggled)
				isActive = !isActive;
		}
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	protected abstract boolean toggleOn();
	protected abstract void toggleOff();
	
	
	public void dispose() {
		try{
			toggleOff();
		}catch(Exception ignore){}
	}
}