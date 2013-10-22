package edu.pdx.cs.multiview.smelldetector;

import junit.framework.TestCase;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;

import edu.pdx.cs.multiview.test.JavaTestProject;

@SuppressWarnings("restriction")
public class ToggleSmellHandlerTest extends TestCase{

	private ToggleSmellHandler handler;
	
	public void setUp(){
		handler = new ToggleSmellHandler();
	}
	
	public void testSimpleToggle() throws Exception{
				
		handler.execute(null);
		
		assertTrue(handler.isActive());
		
		handler.execute(null);
		
		assertFalse(handler.isActive());
	}
	
	public void testImageDisposed() throws Exception{
		
		JavaEditor e = openEditor();
		
		assertNull(getBackground(e));
		
		handler.execute(null);//on
		
		assertNotNull(getBackground(e));
		
		handler.execute(null);//off
		
		assertNull(getBackground(e));
	}
	
	

	public void testScrollPerformance() throws Exception{
		

		IPackageFragment pkg = createPackage();
		
		long timeToScroll = testScrollPerformance(false,pkg);
		System.out.println(timeToScroll);
		
		timeToScroll = testScrollPerformance(true,pkg);
		System.out.println(timeToScroll);
	}

	private long testScrollPerformance(boolean showFlower, IPackageFragment pkg) throws Exception{
		String methods = "";
		for(int i = 0; i<100; i++){
			methods += "void m"+i+"(){\n";
			for(int j = 0; j < i; j++)
				methods += "System.out.println();\n";
			methods += "}\n";
		}
		
		JavaEditor e = openEditorOn("Foo"+(int)(Math.random()*100),methods,pkg);
		
		
		handler.execute(null);
		
		long start = System.currentTimeMillis();
		
		scrollABit(e);
		
		long stop = System.currentTimeMillis();
		
		long timeToScroll = stop-start;
		return timeToScroll;
	}

	private void scrollABit(JavaEditor e) {
		int scrollIterations = 100;
		int currentTop = e.getViewer().getTopIndex();
		for(int i = 0; i<scrollIterations; i++){
			e.getViewer().setTopIndex(++currentTop);
		}
	}
	

	private Image getBackground(JavaEditor e) {
		return e.getViewer().getTextWidget().getParent().getBackgroundImage();
	}
	
	private JavaEditor openEditor() throws Exception{
		
		String className = "TestClass";
		
		String methods = "void method(){"+
					"   int x = 0+1+2+3+4+5+6+7+8+9;"+
					" }";
		
		IPackageFragment fragment = createPackage();
		
		return openEditorOn(className, methods, fragment);	
	}

	private JavaEditor openEditorOn(String className, String methods,
			IPackageFragment fragment) throws JavaModelException,
			PartInitException {
		
		String classBody = 
			"class " + className + "{ " + methods + " }";
		ICompilationUnit cu = 
			fragment.createCompilationUnit(className+".java", classBody, true, null);
		
		return (JavaEditor) JavaUI.openInEditor(cu);
	}
	
	private IPackageFragment createPackage() throws Exception {
		JavaTestProject p = new JavaTestProject();
		return p.createPackage("pkg");
	}
}
