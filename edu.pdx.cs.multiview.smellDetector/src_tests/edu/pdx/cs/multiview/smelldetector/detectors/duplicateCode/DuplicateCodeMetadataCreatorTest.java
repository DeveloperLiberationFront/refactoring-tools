package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import edu.pdx.cs.multiview.smelldetector.BaseSmellDetectorTest;

public class DuplicateCodeMetadataCreatorTest {

	private BaseSmellDetectorTest baseSmellDetectorTest;
	private DuplicateCodeMetadataCreator duplicateCodeMetadataCreator;
	final DuplicateCodeMetadataCollector collectorMock = mock(DuplicateCodeMetadataCollector.class);

	@Before
	public void setup() throws Exception {
		String path = System.getProperty("user.dir")
				+ "/src_tests/edu/pdx/cs/multiview/smelldetector/detectors/duplicateCode/duplicate_method_class.java_file";
		System.out.println("File Path: " + path);
		baseSmellDetectorTest = new BaseSmellDetectorTest(path);

		duplicateCodeMetadataCreator = new DuplicateCodeMetadataCreator() {
			@Override
			DuplicateCodeMetadataCollector getCollector() {
				return collectorMock;
			}
		};
	}
	

	@Test
	public void shouldCreateMetaDataForTestMethod() throws Exception {
		IMethod testMethod = getTestMethod();
		String line1InMethod = "inta=5;";
		int hashCode = line1InMethod.hashCode();
		String[] classAndMethodName = {"com.testSmellDetector.DuplicateMethodClass", "com.testSmellDetector.DuplicateMethodClass.testMethod()"};
		IsClassNameAndMethodSame matcher = new IsClassNameAndMethodSame(classAndMethodName);

		duplicateCodeMetadataCreator.createSmellMetaData(testMethod);
		
		verify(collectorMock).save(eq(hashCode), argThat(matcher));
	}

	private IMethod getTestMethod() throws Exception {
		String methodSignature = "testMethod";
		return baseSmellDetectorTest.getMethod(methodSignature);
	}

	private class IsClassNameAndMethodSame extends ArgumentMatcher<String[]> {

		private final String[] classNameAndMethodName;
		public IsClassNameAndMethodSame(String[] classNameAndMethodName ) {
			this.classNameAndMethodName = classNameAndMethodName ;
		}
		@Override
		public boolean matches(Object argument) {
			String[] classAndMethodNameArg = (String[])argument;
			System.out.println("Checking argument ::" + Arrays.toString(classAndMethodNameArg));
			if(classNameAndMethodName[0].equals(classAndMethodNameArg[0]) && classNameAndMethodName[1].equals(classAndMethodNameArg[1])) 
				return true;
			return false;
		}

	}

}
