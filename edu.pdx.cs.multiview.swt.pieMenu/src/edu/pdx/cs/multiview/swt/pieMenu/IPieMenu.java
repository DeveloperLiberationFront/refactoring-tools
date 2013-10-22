package edu.pdx.cs.multiview.swt.pieMenu;


public interface IPieMenu {

	public String getText();

	/**
	 * @return	true if I have no submenus
	 */
	public boolean isEmpty();

	public void fireSelectionEvent();
	
	public void addSelectionListener(SelectionListener listener);
	public void removeSelectionListener(SelectionListener listener);
}
