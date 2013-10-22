package edu.pdx.cs.multiview.swt.pieMenu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.pdx.cs.multiview.swt.geometry.Coordinate;



public class PieMenu extends Canvas implements IPieMenu {

	public static PieMenu createPieMenu(final Composite parent) {
		Shell shell = new Shell(parent.getShell(),SWT.NO_TRIM | SWT.ON_TOP | SWT.TOOL);
		final PieMenu pieMenu = new PieMenu(shell);
		
		pieMenu.addKeyListenerTo(parent, shell);
		
		shell.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent _) {
				parent.forceFocus();
			}
			public void focusLost(FocusEvent arg0) {}
			
		});
		
		return pieMenu;
	}

	private void addKeyListenerTo(final Control ... controls) {
		
		KeyListener listener = new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if(e.keyCode==SWT.ESC){
					for(Control c : controls)
						c.removeKeyListener(this);
					dispose();						
				}
				//TODO: if buttons are arrows, activate menu item
			}

			public void keyReleased(KeyEvent e) {}
		};
		
		for(Control c : controls)
			c.addKeyListener(listener);
	}

	//these are currently specific, but in general could be any 
	//kind of PieMenu
	private List<IPieMenu> items = new ArrayList<IPieMenu>(8);
	private String text = "Text Not Set";
	
	private Shell shell;
	private Region shellRegion;
	private PieMenuPainter painter;
	private PieMenu parent;
	
	
	private PieMenu(Shell myShell) {
		super(myShell,SWT.NONE);
		this.shell = myShell;
		
		setSize(getRadius()*2, getRadius()*2);
		painter = new PieMenuPainter(this);
		addPaintListener(painter);
		
		initShell();
		
		addMouseMoveListener(new MouseMoveListener(){

			public void mouseMove(MouseEvent e) {
				handleMouseMoveIn(e.x,e.y);
			}
		});
		
		addMouseListener(new MouseListener(){

			public void mouseUp(MouseEvent e) {
				try {
					Coordinate c = Coordinate.create(e.x, e.y,
							PieMenuPainter.getRadius(),
							PieMenuPainter.getRadius());
					forceActivateAt(c);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
				}
			}
			
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {}
			
		});
	}
	
	protected void handleMouseMoveIn(int x, int y) {
		int oldRegion = getSelectedItem();
		int selectedRegion = painter.getItemIndex(
				Coordinate.create(x, y,PieMenuPainter.getRadius(),
									PieMenuPainter.getRadius()));
		if(oldRegion!=selectedRegion){
			setSelectedItem(selectedRegion);
		}
	}
	
	private void initShell(){
		
		shell.setSize(getRadius()*2,getRadius()*2);

		shellRegion = new Region();
		shellRegion.add(circle(getRadius(),getRadius(),getRadius()));
		shell.setRegion(shellRegion);
	}

	private int getRadius() {
		return PieMenuPainter.getRadius();
	}

	@Override
	public void dispose(){
		shell.dispose();
		shellRegion.dispose();
		super.dispose();
		if(parent!=null)
			parent.dispose();
	}
 
	
	public IPieMenu add(String menuItem){
		EmptyItem item = new EmptyItem(menuItem);
		items.add(item);
		return item;
	}
	
	public IPieMenu add(PieMenu item) {
		item.parent = this;
		items.add(item);
		return item;
	}

	private int selectedItem = -1; 
	
	int getSelectedItem() {
		return selectedItem;
	}
	
	private void setSelectedItem(int index) {
		selectedItem = index;
		redraw();
	}

	int getItemCount() {
		return items.size();
	}
	
	protected IPieMenu getItem(int i) {
		return items.get(i);
	}	

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public void setLocation(int x, int y){
		shell.setLocation(x-getRadius(),y-getRadius());
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		shell.setVisible(b);
	}
	
	//from Snippet134
	private static int[] circle(int r, int offsetX, int offsetY) {
	    int[] polygon = new int[8 * r + 4];
	    // x^2 + y^2 = r^2
	    for (int i = 0; i < 2 * r + 1; i++) {
	      int x = i - r;
	      int y = (int) Math.sqrt(r * r - x * x);
	      polygon[2 * i] = offsetX + x;
	      polygon[2 * i + 1] = offsetY + y;
	      polygon[8 * r - 2 * i - 2] = offsetX + x;
	      polygon[8 * r - 2 * i - 1] = offsetY - y;
	    }
	    return polygon;
	  }


	private void forceActivateAt(Coordinate c) {		
		int index = painter.getItemIndex(c);
		if(index>=0){
			IPieMenu item = getItem(index);
			if(item.isEmpty()){
				dispose();
			}else{
				PieMenu child = (PieMenu)item;
				child.setLocation(toScreen(c));
				child.setVisible(true);
			}
			item.fireSelectionEvent();
		}
	}

	private Point toScreen(Coordinate c) {
		
		Coordinate relativeCoord = 
			c.toJavaCoordinate(Coordinate.create(getRadius(), getRadius()));
		
		return shell.toDisplay(relativeCoord.x(), relativeCoord.y());
	}

	public void forceActivate(int x, int y) {
		forceActivateAt(Coordinate.create(x, y));		
	}

	public void addNull() {
		add("-");
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
