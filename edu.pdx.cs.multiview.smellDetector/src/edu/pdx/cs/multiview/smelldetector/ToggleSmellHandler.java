package edu.pdx.cs.multiview.smelldetector;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.indexer.SmellMetaDataIndexer;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;


@SuppressWarnings("restriction")
public class ToggleSmellHandler extends AbstractHandler {

	private boolean isActive = false;
	private Listener listener;
	private Flower flower = new Flower();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		try {
			if(isActive)	disableSmells();
			else			enableSmells();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		isActive = !isActive;
		
		return null;
	}

	public void enableSmells() {

		IWorkbenchWindow activeWindow = Activator.getDefault().
						getWorkbench().getActiveWorkbenchWindow();		
		
		if(listener==null){
			listener = new Listener(activeWindow);		
		}
		
		try {
			activateOn((JavaEditor)activeWindow.getActivePage().getActiveEditor());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	

	private SmellDetectorManager manager = 
		new SmellDetectorManager();
	
	private void activateOn(final JavaEditor activeEditor) {
		
		flower.moveTo(activeEditor.getViewer().getTextWidget());
		Map<SmellDetector<?>, Color> detectors = manager.smells(flower);
		flower.attachPetals(detectors);
		
		EditorViewportListener.listenTo(activeEditor, detectors.keySet());
		
		initializeClumpCreationJob(activeEditor);
		
	}

	private void initializeClumpCreationJob(final JavaEditor activeEditor) {
		Job job = new Job("My First Job") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				new SmellMetaDataIndexer(activeEditor);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
	
	public void disableSmells() {
		
		EditorViewportListener.removeListener();
		
		if(listener!=null)
			listener.dispose();
		listener = null;
		
		if(flower!=null)
			flower.dispose();
	}
	
	public boolean isActive(){
		return isActive;
	}

	public Flower activeFlower() {
		return flower;
	}
	
	private class Listener implements IWorkbenchListener, IPartListener{
		
		private IWorkbenchWindow window;
		
		public Listener(IWorkbenchWindow activeWindow){
			this.window = activeWindow;
			init();
		}

		private void init() {
			window.getActivePage().addPartListener(this);
			window.getWorkbench().addWorkbenchListener(this);
		}
		
		public void dispose(){
			window.getActivePage().removePartListener(this);
			window.getWorkbench().removeWorkbenchListener(this);
		}
		
		public boolean preShutdown(IWorkbench workbench, boolean forced) {
			disableSmells();
			return true;
		}
		
		public void partActivated(IWorkbenchPart part) {
			if(part instanceof JavaEditor)
				activateOn((JavaEditor)part);
		}
		
		public void postShutdown(IWorkbench workbench) {}		
		public void partBroughtToTop(IWorkbenchPart part) {	}
		public void partClosed(IWorkbenchPart part) {}
		public void partDeactivated(IWorkbenchPart part) {}
		public void partOpened(IWorkbenchPart part) {}
	}
}
