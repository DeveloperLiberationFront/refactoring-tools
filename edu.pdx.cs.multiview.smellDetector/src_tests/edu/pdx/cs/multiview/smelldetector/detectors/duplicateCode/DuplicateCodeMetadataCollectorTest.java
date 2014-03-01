package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Element;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class DuplicateCodeMetadataCollectorTest {

	private DuplicateCodeMetadataCollector collector;

	@Before
	public void setup() throws Exception {

		IJavaProject javaProject = mock(IJavaProject.class);
		when(javaProject.getElementName()).thenReturn("test_project");
		
		collector = mock(DuplicateCodeMetadataCollector.class);
	}

	@Test
	public void shouldCreateNewSetIfDataNotPresentCorrespondingToHashcode() {
		Integer hashOfCode = 3445554;
		String[] classAndMethodName = {"com.testSmellDetector.DuplicateMethodClass", "com.testSmellDetector.DuplicateMethodClass.testMethod()"};
		
		// call actual method under test
		doCallRealMethod().when(collector).save(any(Integer.class), any(String[].class));
		collector.save(hashOfCode, classAndMethodName);

		// verify that an element was added to the set
		IsNewSetCreated matcher = new IsNewSetCreated(classAndMethodName);
		verify(collector).putInCache(eq(hashOfCode), argThat(matcher));
	}
	
	
	@Test
	public void shouldAddToExisteingSetThatCorrespondsToHashcode() {
		Integer hashOfCode = 3445554;
		String[] classAndMethodName = {"com.testSmellDetector.DuplicateMethodClass", "com.testSmellDetector.DuplicateMethodClass.testMethod()"};
		String[] anotherClassAndMethodName = {"com.testSmellDetector.AnotherDuplicateMethodClass", "com.testSmellDetector.DuplicateMethodClass.anotherTestMethod()"};
	
		Element value = createElement(hashOfCode, classAndMethodName);

		///should return the dummy element for hashcode
		when(collector.getFromCache(eq(hashOfCode))).thenReturn(value);
		
		
		// call actual method under test
		doCallRealMethod().when(collector).save(any(Integer.class), any(String[].class));
		
		collector.save(hashOfCode, anotherClassAndMethodName);

		// verify that an element was added to the set
		IsSetOfMultipleElements matcher = new IsSetOfMultipleElements(classAndMethodName , anotherClassAndMethodName);
		verify(collector).putInCache(eq(hashOfCode), argThat(matcher));
	}	
	

	private Element createElement(Integer hashOfCode, String[] classAndMethodName) {
		ClassAndMethodName classAndMethodElement1 = new ClassAndMethodName(classAndMethodName);
		Set<ClassAndMethodName> elementValue =  new HashSet<ClassAndMethodName>();
		elementValue.add(classAndMethodElement1);
		Element value = new Element(hashOfCode, elementValue);
		return value;
	}

	class IsNewSetCreated extends ArgumentMatcher<Set<ClassAndMethodName>> {
		final String[] elementContents;

		public IsNewSetCreated(String[] elementContents) {
			this.elementContents = elementContents;
		}

		@Override
		public boolean matches(Object argument) {
			Set<ClassAndMethodName> methodNames = (Set<ClassAndMethodName>) argument;
			ClassAndMethodName classAndMethod = new ClassAndMethodName(elementContents);
			return (methodNames.size() == 1 && methodNames.contains(classAndMethod));
		}
	}
	

	class IsSetOfMultipleElements extends ArgumentMatcher<Set<ClassAndMethodName>> {
		final String[][] elements;

		public IsSetOfMultipleElements(String[]... elementContents) {
			this.elements = elementContents;
		}

		@Override
		public boolean matches(Object argument) {
			Set<ClassAndMethodName> methodNames = (Set<ClassAndMethodName>) argument;
			boolean matches = true;
			for (String[] elementContent : elements) {

				ClassAndMethodName classAndMethod = new ClassAndMethodName(elementContent);
				matches = matches && methodNames.contains(classAndMethod);
			}
			matches = methodNames.size() == elements.length;
			return matches;
		}
	}

}
