package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;

import edu.pdx.cs.multiview.smelldetector.BaseSmellDetectorTest;

public class DuplicateCodeInstaceITTest {
	private BaseSmellDetectorTest baseSmellDetectorTest;
	private DuplicateCodeMetadataCreator duplicateCodeMetadataCreator;
	final DuplicateCodeMetadataCollector collectorMock = mock(DuplicateCodeMetadataCollector.class);

	private DuplicateCodeInstace duplicateCodeInstace;
	
	@Before
	public void setup() throws Exception {
		String path = System.getProperty("user.dir")
				+ "/src_tests/edu/pdx/cs/multiview/smelldetector/detectors/duplicateCode/duplicate_method_class.java_file";
		System.out.println("File Path: " + path);
		baseSmellDetectorTest = new BaseSmellDetectorTest(path);
		duplicateCodeMetadataCreator = new DuplicateCodeMetadataCreator(baseSmellDetectorTest.getTestProject().getJavaProject());
		duplicateCodeMetadataCreator.createSmellMetaData(getTestMethod());
		duplicateCodeMetadataCreator.createSmellMetaData(getDuplicateOfTestMethod());
		duplicateCodeMetadataCreator.getCollector().setInitialized(true);
		List<IMethod> visibleMethods = new ArrayList<IMethod>();
		visibleMethods.add(getTestMethod());
		duplicateCodeInstace = new DuplicateCodeInstace(visibleMethods);
	}
	
	@Test
	public void shouldReturnCorrectNumberOfDuplicateLines() throws Exception {
		assertEquals(4, duplicateCodeInstace.getNumberOfDuplicateLinesForMethod(getTestMethod()));
	}
	
	@Test
	public void shouldReturnCorrectSmellMagnitude() throws Exception {
		assertEquals(.33, duplicateCodeInstace.magnitude(), 0.01);
	}
	
	
	private IMethod getTestMethod() throws Exception {
		String methodSignature = "testMethod";
		return baseSmellDetectorTest.getMethod(methodSignature);
	}
	
	
	private IMethod getDuplicateOfTestMethod() throws Exception {
		String methodSignature = "duplicateOfTestMethod";
		return baseSmellDetectorTest.getMethod(methodSignature);
	}
	
}
