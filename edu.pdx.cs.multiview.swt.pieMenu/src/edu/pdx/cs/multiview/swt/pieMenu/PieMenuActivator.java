package edu.pdx.cs.multiview.swt.pieMenu;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class PieMenuActivator implements MouseListener, MouseMoveListener,
		KeyListener {

	public static int LEFT_MOUSE_BUTTON = 1;
	public static int MIDDLE_MOUSE_BUTTON = 2;
	public static int RIGHT_MOUSE_BUTTON = 3;

	public static int SINGLE_CLICK = 1;
	public static int DOUBLE_CLICK = 2;

	private MenuBuilderThread thread;
	private Point mouseDownPoint;

	private PieMenuActivator(IPieMenuBuilder builder) {
		thread = new MenuBuilderThread(builder);
	}

	// mouse button down
	public void mouseDown(final MouseEvent e) {

		if (e.button == LEFT_MOUSE_BUTTON && e.count == DOUBLE_CLICK) {
			// if (e.button == LEFT_MOUSE_BUTTON && e.stateMask == SWT.CONTROL){

			// start loading menu
			mouseDownPoint = new Point(e.x, e.y);

			if (thread != null && thread.isAlive()) {
				try {
					// really, we don't want to join, we probably
					// want to destroy
					thread.join();
				} catch (InterruptedException ignore) {
				}
			}

			// create new menu
			thread.setParent((Composite) e.widget, mouseDownPoint);

			// run method of the MenuBuilderThread will be invoked by the
			// user-interface thread
			e.display.asyncExec(thread);

		} else {
			mouseDownPoint = null;
			if (thread.menu != null)
				thread.menu.dispose();
		}

	}

	// TODO: we should probably move stuff from mouseDown to mouseUp 
	public void mouseUp(MouseEvent e) {
		if (mouseDownPoint != null) {

			int dx = 0;// e.x - mouseDownPoint.x;
			int dy = 0;// mouseDownPoint.y - e.y;
			// mouseDownPoint = null;

			try {
				thread.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			thread.forceActivate(dx, dy);
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// if(thread!=null && thread.menu!=null)
		// thread.menu.dispose();

		if (e.keyCode == SWT.F3 && e.stateMask == SWT.ALT) {

			if (thread != null && thread.isAlive()) {
				try {
					// really, we don't want to join, we probably
					// want to destroy
					thread.join();
				} catch (InterruptedException ignore) {
				}
			}

			thread.setParent((Composite) e.widget, new Point(100, 100));

			e.display.asyncExec(thread);

		} else {
			// mouseDownPoint = null;
			if (thread.menu != null)
				thread.menu.dispose();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public static void createPieMenuOn(Composite parent, IPieMenuBuilder builder) {

		PieMenuActivator activator = getActivatorFor(parent, builder);
		parent.removeMouseListener(activator);
		parent.addMouseListener(activator);
		parent.removeMouseMoveListener(activator);
		parent.addMouseMoveListener(activator);
		parent.removeKeyListener(activator);
		parent.addKeyListener(activator);
	}

	private static Map<Composite, PieMenuActivator> activators = new HashMap<Composite, PieMenuActivator>();

	private static PieMenuActivator getActivatorFor(Composite parent,
			IPieMenuBuilder builder) {

		if (!activators.containsKey(parent))
			activators.put(parent, new PieMenuActivator(builder));

		PieMenuActivator activator = activators.get(parent);
		activator.thread.builder = builder;

		return activator;
	}

	class MenuBuilderThread extends Thread {

		public IPieMenuBuilder builder;
		private Composite parent;
		private PieMenu menu;

		public MenuBuilderThread(IPieMenuBuilder b) {
			builder = b;
		}

		public void setParent(Composite c, Point point) {

			parent = c;

			// dispose the previous menu
			if (menu != null && !menu.isDisposed())
				menu.dispose();

			//

			// create a new menu
			menu = PieMenu.createPieMenu(c, point);
		}

		@Override
		public void run() {
			try {
				builder.build(menu);

				Point newPoint = parent.toDisplay(mouseDownPoint);
				menu.setLocation(newPoint);

				// bug fixed
				//set bounds for menu to be drawn on shell
				menu.setBounds(newPoint.x - menu.getRadius(),
						newPoint.y - menu.getRadius(), menu.getSize().x,
						menu.getSize().y);
				menu.setVisible(true);

			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}

		public void forceActivate(int x, int y) {
			menu.forceActivate(x, y);
			// menu.forceActivate(100, 100);
		}

		void handleMouseMoveInParent(MouseEvent e) {
			if (menu != null && !menu.isDisposed() && parent != null) {
				Point p = menu.toControl(parent.toDisplay(e.x, e.y));
				menu.handleMouseMoveIn(p.x, p.y);
			}
		}
	}

	public void mouseMove(MouseEvent e) {

		mouseDownPoint = new Point(e.x, e.y);
		thread.handleMouseMoveInParent(e);
	}
}