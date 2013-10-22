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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.pdx.cs.multiview.swt.geometry.Coordinate;

public class PieMenu extends Canvas implements IPieMenu {

	public static final int PIE_MENU_TYPE = 0;
	public static final int SEPARATE_MENU_TYPE = 1;
	public static final int TRANS_PIE_MENU_TYPE = 2;
	public static final int DOUGHNUT_MENU_TYPE = 3;

	public static final int MENU_ITEM_CIRCLE = 0;
	public static final int MENU_ITEM_RECT = 1;
	
	public static final String NULL_STRING ="No\nSuggestion";


	private int menuType;
	private int menuItemShape;
	

	/*
	 * called every time mouse button is down by PieMenuActivator.setParent
	 */
	public static PieMenu createPieMenu(final Composite parent,
			final Point point) {
		Shell shell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.ON_TOP
				| SWT.TOOL | SWT.TRANSPARENT);

		Point newPoint = parent.toDisplay(point.x, point.y);
		final PieMenu pieMenu = new PieMenu(shell, newPoint,
				DOUGHNUT_MENU_TYPE, MENU_ITEM_RECT);

		pieMenu.addKeyListenerTo(parent, shell);

		shell.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent _) {
				parent.forceFocus();
			}

			public void focusLost(FocusEvent arg0) {
			}

		});

		return pieMenu;
	}

	private void addKeyListenerTo(final Control... controls) {

		KeyListener listener = new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					for (Control c : controls)
						c.removeKeyListener(this);
					dispose();
				}
				// TODO: if buttons are arrows, activate menu item
			}

			public void keyReleased(KeyEvent e) {
			}
		};

		for (Control c : controls)
			c.addKeyListener(listener);
	}

	// these are currently specific, but in general could be any
	// kind of PieMenu
	private List<IPieMenu> items = new ArrayList<IPieMenu>(8);
	private String text = "Text Not Set";

	private Shell shell;
	private Region shellRegion;
	private PieMenuPainter painter;
	private PieMenu parent;

	private PieMenu(Shell myShell, Point point, int type, int shape) {
		super(myShell, SWT.TRANSPARENT);
		setMenuType(type);
		setMenuItemShape(shape);
		this.shell = myShell;

		setSize(getRadius() * 2, getRadius() * 2);
		painter = new PieMenuPainter(this);
		addPaintListener(painter);

		initShell(point);

		addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {
				handleMouseMoveIn(e.x, e.y);
			}
		});

		addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				try {
					Coordinate c = Coordinate.create(e.x, e.y, getRadius(),
							getRadius());

					forceActivateAt(c);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

		});
	}

	/*
	 * 
	 * Keeps getting called while mouse is hovering 
	 * 
	 */
	protected void handleMouseMoveIn(int x, int y) {
		int oldRegion = getSelectedItem();
		int selectedRegion = painter.getItemIndex(Coordinate.create(x, y,
				getRadius(), getRadius()));
		if (oldRegion != selectedRegion) {
			setSelectedItem(selectedRegion);
		}
	}

	private void initShell(Point point) {

		// shell.setSize(getRadius()*2,getRadius()*2);
		shell.setSize(point.x * 2, point.y * 2);

		shellRegion = new Region();

		if (getMenuType() == PIE_MENU_TYPE) {
			shellRegion.add(circle(getRadius(), point.x, point.y));
		} else if (getMenuType() == DOUGHNUT_MENU_TYPE) {
			shellRegion.add(circle(getRadius(), point.x, point.y));
			shellRegion.subtract(circle(50, point.x, point.y));

		} else if (getMenuType() == SEPARATE_MENU_TYPE) {
			if (getMenuItemShape() == MENU_ITEM_CIRCLE) {

				shellRegion.add(circle(50, point.x + 100, point.y));
				shellRegion.add(circle(50, point.x - 100, point.y));
				shellRegion.add(circle(50, point.x, point.y + 100));
				shellRegion.add(circle(50, point.x, point.y - 100));
			} else {// rectangle otherwise
				shellRegion.add(circle(20, point.x + 35, point.y));
				shellRegion.add(point.x + 30, point.y - 15, 153, 30);
				shellRegion.add(circle(16, point.x + 186, point.y));

				shellRegion.add(circle(20, point.x - 181, point.y));
				shellRegion.add(point.x - 195, point.y - 15, 150, 30);
				shellRegion.add(circle(16, point.x - 47, point.y));

				shellRegion.add(circle(20, point.x - 80, point.y + 115));
				shellRegion.add(point.x - 80, point.y + 100, 150, 30);
				shellRegion.add(circle(16, point.x + 73, point.y + 115));

				shellRegion.add(circle(20, point.x - 80, point.y - 115));
				shellRegion.add(point.x - 80, point.y - 130, 150, 30);
				shellRegion.add(circle(16, point.x + 73, point.y - 115));

			}
		} else if (getMenuType() == TRANS_PIE_MENU_TYPE) {
			transparentCircle(getRadius(), point.x, point.y, shellRegion);
			shellRegion.subtract(circle(25, point.x, point.y));
		} else { //regular Circle
			shellRegion.add(circle(getRadius(), point.x, point.y));

		}
		// shellRegion.add(circle(getRadius(),getRadius(),getRadius()));

		// shellRegion.add(circle(getRadius(),point.x+100,point.y+100));

		shell.setRegion(shellRegion);

	}

	/*
	 * Returns the radius of outer circle depending on menu's type 
	 */
	public int getRadius() {
		if (getMenuType() == PIE_MENU_TYPE
				|| getMenuType() == TRANS_PIE_MENU_TYPE)
			return 125;
		else if (getMenuType() == SEPARATE_MENU_TYPE && getMenuItemShape() == MENU_ITEM_RECT)
			return 202;
		else if (getMenuType() == DOUGHNUT_MENU_TYPE)
			return 155;
		else
			return 170;
	}

	@Override
	public void dispose() {
		shell.dispose();
		shellRegion.dispose();
		super.dispose();
		if (parent != null)
			parent.dispose();
	}

	public IPieMenu add(String menuItem) {
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
	public void setLocation(int x, int y) {
		shell.setLocation(x - getRadius(), y - getRadius());
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		shell.setVisible(b);
	}

	// from Snippet134
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

	private void transparentCircle(int radius, int offsetX, int offsetY,
			Region shellRegion) {
		Rectangle pixel = new Rectangle(0, 0, 1, 1);
		for (int y = -radius; y < radius + 1; y += 1) {
			for (int x = -radius; x < radius + 1; x += 2) {
				if (x * x + y * y <= radius * radius) {
					pixel.x = x + offsetX;
					pixel.y = y + offsetY;
					shellRegion.add(pixel);
				}
			}
		}
	}

	private void forceActivateAt(Coordinate c) {
		int index = painter.getItemIndex(c);
		if (index >= 0) {
			IPieMenu item = getItem(index);
			if (item.isEmpty()) {
				dispose();
			} else {
				PieMenu child = (PieMenu) item;
				child.setLocation(toScreen(c));
				child.setVisible(true);
			}
			item.fireSelectionEvent();
		}
	}

	private Point toScreen(Coordinate c) {

		Coordinate relativeCoord = c.toJavaCoordinate(Coordinate.create(
				getRadius(), getRadius()));

		return shell.toDisplay(relativeCoord.x(), relativeCoord.y());

	}

	public void forceActivate(int x, int y) {
		forceActivateAt(Coordinate.create(x, y));
	}

	public void addNull() {
		add(NULL_STRING);
	}
	
	

	private List<SelectionListener> listeners = new ArrayList<SelectionListener>(
			3);

	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	public void fireSelectionEvent() {
		for (SelectionListener l : listeners) {
			l.itemSelected();
		}
	}

	/**
	 * @param menuType
	 *            the menuType to set
	 */
	public void setMenuType(int menuType) {
		this.menuType = menuType;
	}

	/**
	 * @return the menuType
	 */
	public int getMenuType() {
		return menuType;
	}

	/**
	 * @param menuItemShape
	 *            the menuItemShape to set
	 */
	public void setMenuItemShape(int menuItemShape) {
		this.menuItemShape = menuItemShape;
	}

	/**
	 * @return the menuItemShape
	 */
	public int getMenuItemShape() {
		return menuItemShape;
	}
}
