package edu.pdx.cs.multiview.smelldetector;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class FlowerTest extends TestCase{

	private static  Display display = new Display();
	private Shell shell;
	private StyledText widget;
	private Flower flower;
	
	private int numLines = 100;
	
	public void setUp(){
		setUp(true);
	}
	
	public void setUp(boolean makeFlower){
		shell = new Shell(display);
		shell.setBounds(10, 10, 400, 400);		
		shell.setLayout(new FillLayout());
		
		
		widget = new StyledText(shell, SWT.BORDER | SWT.HORIZONTAL | SWT.VERTICAL);
		String s = "";
		for(int i = 0; i<numLines; i++){
			for(int j = i; j < numLines; j++)
				s += 0;
			s += "\n";
		}
		widget.setText(s);
		

		shell.open();
		
		if(makeFlower){
			flower = new Flower();
			flower.moveTo(widget);
		}
	}
	
	public void testResizeVertical(){

		Runnable r = new Runnable(){
			public void run(){
				shell.setSize(shell.getSize().x, shell.getSize().y*2);		
			}
		};
		
		doSizeChangeAndCheck(r,false);
	}
	
	public void testResizeHorizontal(){

		Runnable r = new Runnable(){
			public void run(){
				shell.setSize(shell.getSize().x*2, shell.getSize().y);		
			}
		};
		
		doSizeChangeAndCheck(r,false);
	}
	
	public void testScrollVertical(){

		Runnable r = new Runnable(){
			public void run(){
				widget.setTopIndex(2);		
			}
		};
		
		doSizeChangeAndCheck(r,true);
	}
	
	public void testScrollHorizontal(){

		Runnable r = new Runnable(){
			public void run(){
				widget.setHorizontalIndex(2);		
			}
		};
		
		doSizeChangeAndCheck(r,true);
	}
	
	public void testScrollReset(){
		
		Point oldSize = shell.getSize();
		
		Point startLocation = flower.getLocation();
		shell.setSize(oldSize.x+10,oldSize.y+10);		
		shell.setSize(oldSize);
		Point endLocation = flower.getLocation();

		assertEquals(startLocation,endLocation);
	}

	private void doSizeChangeAndCheck(Runnable r, boolean shouldBeEqual) {
		Point startLocation = flower.getLocation();
		r.run();
		Point endLocation = flower.getLocation();
		
		if(shouldBeEqual)
			assertEquals(startLocation,endLocation);
		else
			assertNotEquals(startLocation,endLocation);
	}
	
	public void testResizePerformance(){
		
		int iterations = 100;
		
		long startTime, lengthNoFlower, lengthWithFlower;
		
		setUp(false);
		
		startTime = System.currentTimeMillis();
		
		for(int i = 0; i<iterations; i++){
			shell.setSize(shell.getSize().x+1,shell.getSize().y+1);
		}
		
		lengthNoFlower = System.currentTimeMillis() - startTime;
		
		tearDown();
		setUp(true);
		
		startTime = System.currentTimeMillis();
		
		for(int i = 0; i<iterations; i++){
			shell.setSize(shell.getSize().x+1,shell.getSize().y+1);
		}
		
		lengthWithFlower = System.currentTimeMillis() - startTime;
		
		tearDown();
		
		double reducedPerformance = 1 - (double)lengthNoFlower / (double)lengthWithFlower;
		assertTrue(reducedPerformance < 0.25);		
	}
	
	interface Runnable{
		public void run(); 
	}
	
	
	public void tearDown(){
		shell.dispose();
	}
	
	static public void assertNotEquals(Object expected, Object actual) {
		if(expected.equals(actual))
			fail("expected:<"+expected+"> to be different from:<"+actual+">");
	}
	
	public static void main(String[] args){
		FlowerTest t = new FlowerTest();
		t.setUp();
		while (!t.shell.isDisposed ()) {
			 if (!display.readAndDispatch ()) 
				 display.sleep ();
		}
		 	
		display.dispose ();		
	}
}
