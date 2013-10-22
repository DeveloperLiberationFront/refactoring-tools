package edu.pdx.cs.multiview.extractmethodannotations;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * I handle events that show dependencies for the current selection
 * 
 * @author emerson
 */
public class ShowDependencyHandler extends AbstractHandler implements IWorkbenchWindowActionDelegate{	

	private static SwitchModeAction switchModeAction = new SwitchModeAction();
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Object trigger = event.getTrigger();
		if(trigger instanceof Event){
			Event evt = (Event)trigger;
			if(evt.widget instanceof StyledText){
				handleEvent((StyledText)evt.widget);
			}
		}
		
		return null;
	}
	
	/*
	 * Handle a mouse down on the parameter
	 */
	private void handleEvent(StyledText st) {
		//add listener for mouse up if we're toggling
		if(!Settings.toggleMode){
			st.removeKeyListener(listener);
			st.addKeyListener(listener);
		}
		activate();
	}

	/*
	 * Activate the action
	 */
	private void activate() {
		getAction().init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		getAction().toggle();
	}
	
	/*
	 * Deactivate the action
	 */
	private void deActivate(){
		switchModeAction.toggleOff();
	}
	
	public void dispose() {
		if(switchModeAction!=null){
			switchModeAction.dispose();
			switchModeAction = null;
		}
	}

	public void init(IWorkbenchWindow window) {
		getAction().init(window);
	}

	public void run(IAction action) {
		getAction().run(action);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		getAction().selectionChanged(action,selection);
	}

	private SwitchModeAction getAction() {

		if(switchModeAction==null){
			switchModeAction = new SwitchModeAction();
		}
		
		return switchModeAction;
	}
	
	private KeyListener listener = new KeyListener(){
		public void keyPressed(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {
			deActivate();
			Control text = (Control)e.getSource();
			text.removeKeyListener(this);
		}
	};
}
