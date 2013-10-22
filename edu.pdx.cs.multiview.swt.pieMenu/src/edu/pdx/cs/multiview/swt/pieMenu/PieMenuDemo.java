package edu.pdx.cs.multiview.swt.pieMenu;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

public class PieMenuDemo implements IPieMenuBuilder {
	private static Shell shell;

	public static void main(String[] args){
		Display display = new Display();
	    shell = new Shell(display);
		shell.open();
	    
	    PieMenuActivator.createPieMenuOn(shell, new PieMenuDemo());
	    
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch())
	          display.sleep();
	      }
	      display.dispose();
	}

	public void build(PieMenu menu) {
		
		for(int i = 0; i<4; i++){
			PieMenu upMenu = PieMenu.createPieMenu(shell);
			upMenu.setText("Parent "+i);
			for(int j = 0; j < 8; j++){				
				final String label = "Child "+j;
				upMenu.add(label).addSelectionListener(
						new SelectionListener(){
							public void itemSelected() {								
								System.out.println(label +  " selected");
							}
						});
			}
			menu.add(upMenu);
		}
		
	}
}
