package edu.pdx.cs.multiview.extractmethodannotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.TogglePresentationAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.BadSelectionAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.BreakAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.ContinueAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.DependencyAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.FlowAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.ReturnAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.SelectionAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.TempVariableAnnotation;
import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.jface.annotation.AnnotationPainter;
import edu.pdx.cs.multiview.test.JavaTestProject;

@SuppressWarnings("restriction")
public class TestSwitchMode extends TestCase {

	private static final String CONTINUE = "continue", BREAK = "break";
	private SwitchModeAction action;
	private IType type;
	private IEditorPart editor;
	
	private final String START = "/*START*/", END = "/*STOP*/";
	
	public void setUp() throws CoreException{
		JavaTestProject  jtp = new JavaTestProject();
		
		IPackageFragment pkg = jtp.createPackage("pkg");
		type = jtp.createType(pkg,"TestClass.java","class TestClass{}");
		
		action = new SwitchModeAction();
		editor = JDTUtils.openElementInEditor(type);
		action.init(editor.getEditorSite().getWorkbenchWindow());	
	}
	
	public void tearDown(){
		editor.doSave(new NullProgressMonitor());
	}
	
	
	public void testVars1() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int z = a;" +
							END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 1, 0, 2, 0, 0, 1, 0);
	}

	public void testVars2() throws Exception{
		
		String methodBody = "void x(int a, int b){"+
							START +
							"int z = a+b;" +
							END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 2, 0, 4, 0, 0, 1, 0);
	}
	
	public void testVars3() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int b = 1;"+
							"int z = a+b;" +
							END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 1, 0, 2, 0, 0, 1, 0);
	}
	
	public void testVars4() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int b = 1;"+
							END + 
							"int z = a+b;" +
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 1, 2, 0, 0, 1, 0);
	}
	
	public void testVars5() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int b = 1;"+
							"int c = 1;"+
							END + 
							"int z = a+b;" +
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 1, 2, 0, 0, 1, 0);
	}
	
	public void testVars6() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int b = 1;"+
							"int c = 1;"+
							END + 
							"int z = b+c;" +
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 2, 4, 0, 0, 1, 0);
	}
	
	public void testVars7() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int b = 1;"+
							"int c = 1;"+
							END + 
							"int z = 3;" +
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 0, 0, 0, 0, 1, 0);
	}
	
	
	/*
	 * TODO: control flow not yet implemented
	 */
	public void testVars8() throws Exception{
		
		String methodBody = "void x(int a){"+
							"int b;"+
							"if(a!=1) " + 
								START +"b=3;"+END + 
							"else "+
								"b=4;"+
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 1, 0, 3, 0, 0, 1, 0);
	}
	
	/*
	 * TODO: innerclasses not yet handled
	 */
	public void testVars9() throws Exception{
		
		String methodBody = "Object x(final String a){"+
								START+
								"Object o = new Object(){" +
									"public String toString(){" +
										"return a;" +
									"}"+
								"};" +
								END+
								"return o;"+
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 1, 1, 4, 0, 0, 1, 0);
	}
	
	public void testVars10() throws Exception{
		
		String methodBody = "void x(int a){"+
								"if(a>10){" +
								START +
								"	int b = 1;" +
								"	System.out.println(b);" +
								END +
								"}else{" +
								"	int b = 2;" +
								"	System.out.println(b);" +
								"}" +
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 0, 0, 0, 0, 1, 0);
	}
	
	public void testVars11() throws Exception{
		String methodBody = "void x(int a){"+
							START +
							"a++;" +
							END +
							"System.out.println(a);" +
						"}";
					
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 1, 1, 3, 0, 0, 1, 0);
	}
	
	public void testVars12() throws Exception{
		String methodBody =   "void x() {"+
						      "Object a = null;"+
						      "for (;;) {"+
						      START+
						      "    a = getClass();"+
						      "    if (a != null) {}"+
						      END+
						      "}"+
						      "}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 1, 0, 3, 0, 0, 1, 0);
	}
	
	public void testVars13() throws Exception{
		String methodBody = "void foo(){"+
								"final int x = 1;"+
								"new Object(){"+
									"int run(){"+
										"int y = 2;"+
										START+
										"return x+y;"+
										END+
									"}"+
								"};"+
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(1, 2, 0, 4, 0, 0, 0, 0);
	}

	public void testBreak1() throws Exception {
		bc1(BREAK);
		assertAnnotationCounts(0, 0, 0, 0, 1, 0, 1, 0);
	}

	public void testContinue1() throws Exception {
		bc1(CONTINUE);
		assertAnnotationCounts(0, 0, 0, 0, 0, 1, 1, 0);
	}
	
	public void bc1(String bc) throws Exception{
		
		String methodBody = "void x(int a){"+
							"while(a!=1) " + 
								START +bc+";"+END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
	}
	
	public void testBreak2() throws Exception {
		bc2(BREAK);
		assertAnnotationCounts(0, 1, 0, 2, 0, 0, 1, 0);
	}

	public void testContinue2() throws Exception {
		bc2(CONTINUE);
		assertAnnotationCounts(0, 1, 0, 2, 0, 0, 1, 0);
	}
	
	public void bc2(String bc) throws Exception{
		
		String methodBody = "void x(int a){"+
							START+"while(a!=1) " + 
								bc+";"+END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
	}
	
	public void testBreak3() throws Exception {
		bc3(BREAK);
		assertAnnotationCounts(0, 1, 0, 3, 0, 0, 1, 0);
	}

	public void testContinue3() throws Exception {
		bc3(CONTINUE);
		assertAnnotationCounts(0, 1, 0, 3, 0, 0, 1, 0);
	}
	
	public void bc3(String bc) throws Exception{
		
		String methodBody = "void x(int a){"+
							"while(a!=2)"+
							START+"while(a!=1) " + 
								bc+";"+END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
	}
	
	public void testBreak4() throws Exception {
		bc4(BREAK);
		assertAnnotationCounts(0, 1, 0, 3, 1, 0, 1, 0);
	}

	public void testContinue4() throws Exception {
		bc4(CONTINUE);
		assertAnnotationCounts(0, 1, 0, 3, 0, 1, 1, 0);
	}
	
	public void bc4(String bc) throws Exception{
		
		String methodBody = "void x(int a){"+
							"tag:while(a!=2)"+
							START+"while(a!=1) " + 
								bc+" tag;"+END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
	}
	
	public void testBreak5() throws Exception {
		bc5(BREAK);
		assertAnnotationCounts(0, 0, 0, 0, 1, 0, 1, 0);
	}

	public void testContinue5() throws Exception {
		bc5(CONTINUE);
		assertAnnotationCounts(0, 0, 0, 0, 0, 1, 1, 0);
	}
	
	public void bc5(String bc) throws Exception{
		
		String methodBody = "void x(int a){"+
							"do{ " + 
								START +bc+";"+END + 
							"}while(b!=0);" +
							"}";
		
		createMethodAndAnnotate(methodBody);
	}
	
	public void testBreak6() throws Exception{ 
		bc6(BREAK); 
		assertAnnotationCounts(0, 0, 0, 0, 1, 0, 1, 0);
	}
	
	public void testContinue6() throws Exception{ 
		bc6(CONTINUE); 
		assertAnnotationCounts(0, 0, 0, 0, 0, 1, 1, 0);
	}

	public void bc6(String bc) throws JavaModelException, Exception {
		String methodBody = "void x(int a){"+
							"for(;;){ " + 
								START +bc+";"+END + 
							"}" +
							"}";
		
		createMethodAndAnnotate(methodBody);
	}
	
	public void testReturn1() throws JavaModelException, Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"return;" +
							END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(1, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public void testReturn2() throws JavaModelException, Exception{
		
		String methodBody = "void x(int a){"+							
							START +
							"int b = 3;"+
							"if(b==1) return;" +
							"else if(b==2) return;" +
							END + 
							"}System.out.println(\"a bit more\");"+
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(2, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public void testReturn3() throws JavaModelException, Exception{
		
		String methodBody = "void x(int a){"+							
							"if(a!=2){"+
							START +
							"int b = 3;"+
							"if(b==1) return;" +
							"else return;" +
							END + 
							"}System.out.println(\"a bit more\");"+
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(2, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public void testColors() throws JavaModelException, Exception{
		
		String methodBody = "void x(int a, int b, int c, int d, int e, int f, int g, int h){"+							
							START +
							"System.out.println(a+b+c+d+e+f+g+h);"+
							END + 
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		List<TempVariableAnnotation> anns = 
			contains(action.getPainter(),TempVariableAnnotation.class,8*2);
				
		assertSameNameIsSameColor(anns);
		assertColors(anns,8);
	}
	
	public void testBadSelection() throws JavaModelException, Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int z = " +
							END +
							"a;" +
							"}";

		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 0, 0, 0, 0, 0, 1);
	}

	private void assertColors(List<TempVariableAnnotation> anns, int expectedCount) {
		Set<Color> colors = new HashSet<Color>();
		for(TempVariableAnnotation ann1 : anns){
			colors.add(ann1.getColor());
		}
		assertEquals(colors.size(),expectedCount);
	}

	private void assertSameNameIsSameColor(List<TempVariableAnnotation> anns) {
		for(TempVariableAnnotation ann1 : anns){
			for(TempVariableAnnotation ann2 : anns){
				assertTrue(!ann1.getIdentifier().equals(ann2.getIdentifier()) || 
							ann1.getColor().equals(ann2.getColor()));
			}	
		}
	}
	
	
	
	private void createMethodAndAnnotate(String methodBody) throws JavaModelException, Exception {
		
		IMethod method = type.createMethod(methodBody, null, true, null);	
		
		assertTrue("Oops!  Method does not parse: " + method,method.isStructureKnown());
		
		action.annotateEditor(getSelection(), new NullProgressMonitor());
	}


	private void assertAnnotationCounts(int returnAnnotations, 
										int parameterAnnotations, int returnParameterAnnotations, 
										int tempVariableAnnotations, int breakAnnotations, 
										int continueAnnotations, int flowAnnotations, int badSelectionAnns) {
		
		AnnotationPainter painter = action.getPainter();
		
		contains(painter,BadSelectionAnnotation.class,badSelectionAnns);
		contains(painter,SelectionAnnotation.class,1);
		contains(painter,ReturnAnnotation.class,returnAnnotations);
		contains(painter,TempVariableAnnotation.class,tempVariableAnnotations);
		contains(painter,BreakAnnotation.class,breakAnnotations);
		contains(painter,ContinueAnnotation.class,continueAnnotations);
		contains(painter,FlowAnnotation.class,flowAnnotations);
		
		List<DependencyAnnotation> dependencies = contains(painter,DependencyAnnotation.class,
													parameterAnnotations+returnParameterAnnotations);
		
		int returnCount = 0, paramCount = 0;
		for(DependencyAnnotation ann : dependencies){
			if(ann.isParameter)
				paramCount++;
			else
				returnCount++;
		}
		assertEquals("Incorrect number of parameters", parameterAnnotations, paramCount);
		assertEquals("Incorrect number of return parameters", returnParameterAnnotations, returnCount);
	}

	@SuppressWarnings("unchecked")
	private <C extends Annotation> List<C> contains(AnnotationPainter annotationModel, 
													Class<C> annotationClass, 
													int occurences) {
		
		List<C> typedAnns = new ArrayList<C>();
		
		
		Iterator anns = annotationModel.getAnnotationIterator();
		while(anns.hasNext()){
			Annotation ann = (Annotation)anns.next();
			if(annotationClass.isInstance(ann))
				typedAnns.add((C)ann);
		}
		
		assertEquals("Wrong number of " + annotationClass.getSimpleName() + 
					 "s -", occurences, typedAnns.size());
		
		return typedAnns;
	}

	private ITextSelection getSelection() throws JavaModelException {
		int start = type.getCompilationUnit().getSource().indexOf(START);
		int end = type.getCompilationUnit().getSource().indexOf(END);
		ITextSelection selection = new TextSelection(start,end-start);
		return selection;
	}
	
	public void testShowSelectedElementOnly() throws Exception{
		
		String methodBody = "void x(int a){"+
							START +
							"int z = a;" +
							END + 
							"}";

		type.createMethod(methodBody, null, true, null);	
		
		ITextEditor e = (ITextEditor)editor;
		e.getSelectionProvider().setSelection(getSelection());
		
		TogglePresentationAction tpa = new TogglePresentationAction();
		tpa.setEditor(e);
		
		
		tpa.run();
		
		action.annotateEditor(getSelection(), new NullProgressMonitor());
		assertAnnotationCounts(0, 1, 0, 2, 0, 0, 1, 0);
		
		//undo
		tpa.run();
	}
	
	/*
	 * TODO: make sure that when you select just an expression, you somehow indicate that there is a return value
	 */
	public void testExpression() throws Exception{
		
		
		String methodBody = "void x(int a){"+
							"System.out.println();"+
							START +
							"System.out" +
							END + 
							".println();"+
							"}";
		
		createMethodAndAnnotate(methodBody);
		
		assertAnnotationCounts(0, 0, 0, 0, 0, 0, 0, 1);
	}
}
