package edu.pdx.cs.multiview.extractmethodannotations;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.ui.IEditorPart;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.test.JavaTestProject;


@SuppressWarnings("restriction")
public class TestActivePageListener extends TestCase {

	private JavaTestProject jtp;
	private IType type, type2;
	private ActivePageListener listener;
	
	
	
	public void setUp() throws CoreException{
		jtp = new JavaTestProject();
		
		IPackageFragment pkg = jtp.createPackage("pkg");
		type = jtp.createType(pkg,"TestClass.java","package pkg; class TestClass{}");
		type2 = jtp.createType(pkg,"TestClass2.java","package pkg; class TestClass2{}");
		
		listener = new ActivePageListener();
		
		//precondition
		assertNotNull(JavaPlugin.getActiveWorkbenchWindow());
	}
	
	public void tearDown() throws CoreException{
		jtp.dispose();
	}
	
	public void testNoEditorOpen(){
		
		listener.init(JavaPlugin.getActiveWorkbenchWindow());
		
		assertNull(listener.getEditor());
	}
	
	public void testAttatch_Open(){
		
		listener.init(JavaPlugin.getActiveWorkbenchWindow());
		IEditorPart opened = JDTUtils.openElementInEditor(type);
		
		assertEquals(opened,listener.getEditor());
	}
	
	public void testOpen_Attach(){
		
		IEditorPart opened = JDTUtils.openElementInEditor(type);
		listener.init(JavaPlugin.getActiveWorkbenchWindow());
		
		assertEquals(opened,listener.getEditor());
	}
	
	public void testChangeEditor(){
		
		listener.init(JavaPlugin.getActiveWorkbenchWindow());
		
		JDTUtils.openElementInEditor(type);
		IEditorPart opened = JDTUtils.openElementInEditor(type2);
		
		assertEquals(opened,listener.getEditor());
	}
	
	//TODO: test is broken
//	public void testChangeEditorBack() throws InterruptedException{
//		
//		listener.init(JavaPlugin.getActiveWorkbenchWindow());
//		
//		IEditorPart opened = JDTUtils.openElementInEditor(type);
//		IEditorPart part = JDTUtils.openElementInEditor(type2);
//		
//		//assertNotSame(opened.getEditorSite().getPage(),part.getEditorSite().getPage());
//		opened.getEditorSite().getActionBarContributor().setActiveEditor(opened);
//		//opened.getSite().getWorkbenchWindow().setActivePage(opened.getEditorSite().getPage());
//		
//		assertEquals(opened,listener.getEditor());
//	}
	
	public void testCloseEditor(){
		
		listener.init(JavaPlugin.getActiveWorkbenchWindow());
		
		IEditorPart opened1 = JDTUtils.openElementInEditor(type);
		IEditorPart opened2 = JDTUtils.openElementInEditor(type2);
		
		opened2.getSite().getPage().closeEditor(opened2, false);
		
		assertEquals(opened1,listener.getEditor());
	}
	
	public void testCloseEditors() throws InterruptedException{
		
		listener.init(JavaPlugin.getActiveWorkbenchWindow());
		
		IEditorPart opened1 = JDTUtils.openElementInEditor(type);
		IEditorPart opened2 = JDTUtils.openElementInEditor(type2);
		
		opened2.getSite().getPage().closeEditor(opened2, false);
		opened1.getSite().getPage().closeEditor(opened1, false);
		
		assertNotSame(opened1,listener.getEditor());
		assertNotSame(opened2,listener.getEditor());
	}
	
}
