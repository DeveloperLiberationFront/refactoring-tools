package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import static org.mockito.Mockito.*;
import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DuplicateCodeInstaceTest {
	private LinkedHashMap<Integer, Set<ClassAndMethodName>> lineToMethodsMapping = new LinkedHashMap<Integer, Set<ClassAndMethodName>>();
	private DuplicateCodeInstace duplicateCodeInstace ;
	
	
	ClassAndMethodName classAandTestMethod1 = new ClassAndMethodName(new String[]{"ClassA", "method1"});
	ClassAndMethodName classAandTestMethod2 = new ClassAndMethodName(new String[]{"ClassA", "method2"});
	ClassAndMethodName classBandTestMethod1 = new ClassAndMethodName(new String[]{"ClassB", "method1"});
	ClassAndMethodName classCandTestMethod2 = new ClassAndMethodName(new String[]{"ClassC", "method2"});
	ClassAndMethodName classDandTestMethod2 = new ClassAndMethodName(new String[]{"ClassD", "method2"});
	
	
	private Set<ClassAndMethodName> methodsThatContainLine1;
	private Set<ClassAndMethodName> methodsThatContainLine2;
	private Set<ClassAndMethodName> methodsThatContainLine3;
	private Set<ClassAndMethodName> methodsThatContainLine4;
	private Set<ClassAndMethodName> methodsThatContainLine5;
	private Set<ClassAndMethodName> methodsThatContainLine6;
	private Set<ClassAndMethodName> methodsThatContainLine7;
	
	@Before
	public void setup(){
		
		String line1 = "int a = 5";
		String line2 = "int b = 4";
		String line3 = "int c = 6";
		String line4 = "int d = 19";
		String line5 = "int e = a+b+c";
		String line6 = "int f = c+d";
		String line7 = "System.out.println(f)";
		
		
		methodsThatContainLine1 = new ClassMethodMap().addElement(classAandTestMethod2).addElement(classBandTestMethod1).addElement(classDandTestMethod2);
		methodsThatContainLine2 = new ClassMethodMap().addElement(classAandTestMethod1).addElement(classBandTestMethod1).addElement(classCandTestMethod2);
		methodsThatContainLine3 = new ClassMethodMap().addElement(classBandTestMethod1).addElement(classCandTestMethod2);
		methodsThatContainLine4 = new ClassMethodMap().addElement(classCandTestMethod2);
		methodsThatContainLine5 = new ClassMethodMap().addElement(classDandTestMethod2).addElement(classAandTestMethod2).addElement(classCandTestMethod2);
		methodsThatContainLine6 = new ClassMethodMap().addElement(classDandTestMethod2).addElement(classAandTestMethod2);
		methodsThatContainLine7 = new ClassMethodMap().addElement(classDandTestMethod2).addElement(classAandTestMethod2);
		
		lineToMethodsMapping.put(line1.hashCode(), methodsThatContainLine1);
		lineToMethodsMapping.put(line2.hashCode(), methodsThatContainLine2);
		lineToMethodsMapping.put(line3.hashCode(), methodsThatContainLine3);
		lineToMethodsMapping.put(line4.hashCode(), methodsThatContainLine4);
		lineToMethodsMapping.put(line5.hashCode(), methodsThatContainLine5);
		lineToMethodsMapping.put(line6.hashCode(), methodsThatContainLine6);
		lineToMethodsMapping.put(line7.hashCode(), methodsThatContainLine7);
		
		IMethod visibleMethod = mock(IMethod.class);
		ArrayList<IMethod> visibleMethods = new ArrayList<IMethod>();
		visibleMethods.add(visibleMethod);
		duplicateCodeInstace = new DuplicateCodeInstace(visibleMethods);
	}

	@Test
	public void testGetMethodsToRepeatedLinesMap() {
			HashMap<ClassAndMethodName, Integer> methodsToRepeatedLinesMap = duplicateCodeInstace.getMethodsToRepeatedLinesMap(lineToMethodsMapping);
			for (Entry<ClassAndMethodName, Integer> entry : methodsToRepeatedLinesMap.entrySet()) {
				System.out.println(entry.getKey()+":::"+entry.getValue());
			}
			assertEquals(new Integer(4), methodsToRepeatedLinesMap.get(classCandTestMethod2));
			assertEquals(new Integer(3), methodsToRepeatedLinesMap.get(classDandTestMethod2));
			assertEquals(new Integer(3), methodsToRepeatedLinesMap.get(classAandTestMethod2));
	}

	class ClassMethodMap extends HashSet<ClassAndMethodName> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ClassMethodMap addElement(ClassAndMethodName e) {
			super.add(e);
			return this;
		}
	}

}
