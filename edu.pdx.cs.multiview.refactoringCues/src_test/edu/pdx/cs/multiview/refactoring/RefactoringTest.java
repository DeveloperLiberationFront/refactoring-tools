package edu.pdx.cs.multiview.refactoring;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractTempRefactoring;
import org.eclipse.jdt.ui.JavaUI;

import edu.pdx.cs.multiview.test.JavaTestProject;

public class RefactoringTest extends TestCase{

	public void test1() throws Exception{race();}
	public void test2() throws Exception{race();}
	public void test3() throws Exception{race();}
	public void test4() throws Exception{race();}
	public void test5() throws Exception{race();}
	public void test6() throws Exception{race();}
	public void test7() throws Exception{race();}
	public void test8() throws Exception{race();}
	public void test9() throws Exception{race();}
	public void test0() throws Exception{race();}
	
public void race() throws Exception{
	
	String testClass = 
		"class Test{"+
		" void method(){"+
		"   int x = 0+1+2+3+4+5+6+7+8+9;"+
		" }"+
		"}";

	IPackageFragment fragment = createPackage();
	
	ICompilationUnit cu = 
		fragment.createCompilationUnit("Test.java", testClass, true, null);
	
	JavaUI.openInEditor(cu);
	
	IProgressMonitor pm = new NullProgressMonitor();
	
	for(int i = 0; i<10; i++){
		
			
			int varIndex = cu.getSource().lastIndexOf(""+i);
			System.out.print(cu.getSource().substring(0,varIndex));
			System.out.print("|");
			System.out.println(cu.getSource().substring(varIndex));
	
			ExtractTempRefactoring refactoring = 
				new ExtractTempRefactoring(cu,varIndex,1);
			refactoring.setTempName("a"+i);
				refactoring.checkAllConditions(pm);
		
			refactoring.createChange(pm).perform(pm);
			//System.out.println(i);
	}
	}
	
	private IPackageFragment createPackage() throws Exception {
		JavaTestProject p = new JavaTestProject();
		return p.createPackage("pkg");
	}
	
	int x(){
	  return 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9;
	}
}
