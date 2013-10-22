package edu.pdx.cs.multiview.swt.pieMenu;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;


public class PieMenuActivator implements MouseListener, MouseMoveListener, KeyListener {
	
	private MenuBuilderThread thread;
	private Point mouseDownPoint;
	
	private PieMenuActivator(IPieMenuBuilder builder){
		thread = new MenuBuilderThread(builder);
	}
	
	public void mouseDown(final MouseEvent e) {
		
		if(e.button==2){
			
			mouseDownPoint = new Point(e.x,e.y);
			
			if(thread!=null && thread.isAlive()){
				try {
					//really, we don't want to join, we probably 
					//want to destroy
					thread.join();
				} catch (InterruptedException ignore) {}
			}				
			
			thread.setParent((Composite) e.widget);
			
			e.display.asyncExec(thread);
			
		}else{
			mouseDownPoint = null;
			if(thread.menu!=null)
				thread.menu.dispose();
		}
			
	}
	
	public void mouseUp(MouseEvent e) {
		if(mouseDownPoint!=null){
		
			int dx = e.x - mouseDownPoint.x;
			int dy = mouseDownPoint.y - e.y;			
			mouseDownPoint = null;
			
			try {
				thread.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			thread.forceActivate(dx,dy);
		}
	}

	public void mouseDoubleClick(MouseEvent e) {}
	
	public void keyPressed(KeyEvent e) {
		if(thread!=null && thread.menu!=null)
			thread.menu.dispose();
	}

	public void keyReleased(KeyEvent e) {}	
	
	public static void createPieMenuOn(Composite parent, IPieMenuBuilder builder){
		
		PieMenuActivator activator = getActivatorFor(parent,builder);
		parent.removeMouseListener(activator);
		parent.addMouseListener(activator);	
		parent.removeMouseMoveListener(activator);
		parent.addMouseMoveListener(activator);
		parent.removeKeyListener(activator);
		parent.addKeyListener(activator);
	}

	private static Map<Composite, PieMenuActivator> activators = 
		new HashMap<Composite, PieMenuActivator>();
	
	private static PieMenuActivator getActivatorFor(Composite parent, IPieMenuBuilder builder) {
		
		if(!activators.containsKey(parent))
			activators.put(parent, new PieMenuActivator(builder));
		
		
		PieMenuActivator activator = activators.get(parent);
		activator.thread.builder = builder;
		
		return activator;
	}





	class MenuBuilderThread extends Thread{
		
		public IPieMenuBuilder builder;
		private Composite parent;
		private PieMenu menu;
		
		public MenuBuilderThread(IPieMenuBuilder b){
			builder = b;
		}
		
		public void setParent(Composite c){
			
			parent = c;
			
			if(menu!=null && !menu.isDisposed())
				menu.dispose();
			
			menu = PieMenu.createPieMenu(c);
		}
		
		@Override
		public void run(){
			try {
				builder.build(menu);
				
				menu.setLocation(parent.toDisplay(mouseDownPoint));
				menu.setVisible(true);
				
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}

		public void forceActivate(int x, int y) {
			menu.forceActivate(x, y);
		}

		void handleMouseMoveInParent(MouseEvent e) {
			if(menu!=null && !menu.isDisposed() && parent!=null){				
				Point p = menu.toControl(parent.toDisplay(e.x,e.y));
				menu.handleMouseMoveIn(p.x, p.y);
			}
		}
	}

	public void mouseMove(MouseEvent e) {
		thread.handleMouseMoveInParent(e);
	}
}