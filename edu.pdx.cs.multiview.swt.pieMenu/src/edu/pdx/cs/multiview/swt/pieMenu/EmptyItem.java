package edu.pdx.cs.multiview.swt.pieMenu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public class EmptyItem implements IPieMenu{

	private String name;
	
	public EmptyItem(String menuItem) {
		name = menuItem;
	}

	public Image getIcon() {
		return null;
	}

	public String getText() {
		return name;
	}

	public boolean isEmpty() {
		return true;
	}
	

	private List<SelectionListener> listeners = 
		new ArrayList<SelectionListener>(3);
	
	public void addSelectionListener(SelectionListener listener){
		listeners.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener){
		listeners.remove(listener);
	}
	
	public void fireSelectionEvent(){
		for(SelectionListener l : listeners){						
			l.itemSelected();
		}
	}
}