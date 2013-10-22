package edu.pdx.cs.multiview.smelldetector.detectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.pdx.cs.multiview.smelldetector.ui.CloseButton;
import edu.pdx.cs.multiview.smelldetector.ui.StapleButton;

public abstract class SmellExplanationWindow extends Composite{

	private final Cursor arrowCursor;
	private DragListener dragListener;
	
	//static so it persists between invocations, but
	//this will be a problem if there are multiple windows open
	private static boolean canMove = false;

	public SmellExplanationWindow(StyledText st) {
		super(st, SWT.BORDER);
		this.setCursor(arrowCursor = new Cursor(this.getDisplay(),SWT.CURSOR_ARROW));
		addPageUpDownHack(st);
	}

	/**
	 * this corrects for the problem that if you page down one a scroll bar
	 * (click in the gutter), none of the children move
	 */
	private void addPageUpDownHack(final StyledText st) {
		st.getVerticalBar().addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				//TODO: this isn't quite right...
				//we don't want to move it a full screen if the page down didn't go
				//a full screen (e.g., we're almost at the end and we finish it off)
				if(e.detail == SWT.PAGE_DOWN || e.detail == SWT.PAGE_UP){
					Point size = st.getSize();
					Point oldLocation = getLocation();
					int dy = e.detail==SWT.PAGE_DOWN ? -size.y : size.y;
					setLocation(oldLocation.x,oldLocation.y+dy);
				}
			}});
	}
	
	@Override
	public StyledText getParent(){
		return (StyledText) super.getParent();
	}
	
	public void fillMain(Composite parent) {
		
		GridLayout thisLayout = new GridLayout(1,true);
		thisLayout.marginWidth = 0;
		setLayout(thisLayout);
		setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		Composite titleBar = new Composite(this,SWT.NONE);
		GridLayout titleLayout = new GridLayout(3,false);
		titleLayout.marginWidth = 10;
		titleBar.setLayout(titleLayout);
		titleBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fillTitleBar(titleBar);		
		
		Composite mainBar = new Composite(this,SWT.NONE);		
		fill(mainBar);

		pack();			
		setLocationPrivate(parent.getClientArea().width - getBounds().width, 0);			
		setVisible(true);
	}

	private void fillTitleBar(Composite titleBar) {
		
		final StapleButton pinButton = new StapleButton(titleBar,canMove);
		pinButton.addMouseListener(new MouseListener(){
			public void mouseUp(MouseEvent e) {
				canMove = pinButton.togglePinned();
			}
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {}
		});
		
		Label title = new Label(titleBar,SWT.CENTER);
		title.setText(getText());
		makeBold(title);
		title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dragListener = new DragListener(this, title);
		
		CloseButton closeButton = new CloseButton(titleBar);
		
		closeButton.addMouseListener(new MouseListener(){
			public void mouseUp(MouseEvent e) {dispose();}						
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {}
		});
	}

	protected abstract String getText();
	
	protected abstract void fill(Composite parent);
	
	protected abstract SmellExplanationOverlay<?> getOverlay();
	
	private static void makeBold(Control c) {
		Font f = c.getFont();
		FontData[] fds = f.getFontData();
		for(FontData fd : fds)
			fd.setStyle(SWT.BOLD);
		c.setFont(new Font(f.getDevice(),fds));
	}
	
	public void dispose(){
		arrowCursor.dispose();
		dragListener.dispose();
		getOverlay().dispose();
		super.dispose();
	}
	
	@Override
	public void setLocation (int x, int y){
		
		if(getLocation().x!=x || getLocation().y!=y)
			if(!canMove)
				return;

		setLocationPrivate(x, y);
	}

	protected void setLocationPrivate(int x, int y) {
		super.setLocation(x, y);
	}
	
	public void enableMoving(boolean enable){
		canMove = enable;
	}
	
	public boolean canMove(){
		return canMove;
	}
}
