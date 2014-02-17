package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import edu.pdx.cs.multiview.smelldetector.BaseSmellDetectorTest;

public class DuplicateCodeMetadataCreatorTest {

	private BaseSmellDetectorTest baseSmellDetectorTest;
	private DuplicateCodeMetadataCreator duplicateCodeMetadataCreator;
	final DuplicateCodeMetadataCollector collectorMock = mock(DuplicateCodeMetadataCollector.class);
	@Before
	public void setup() throws Exception {
		String path = System.getProperty("user.dir")+"/src_tests/edu/pdx/cs/multiview/smelldetector/detectors/duplicateCode/duplicate_method_class.java_file";
		System.out.println("File Path: "+ path);
		baseSmellDetectorTest = new BaseSmellDetectorTest(path);
		
		duplicateCodeMetadataCreator = new DuplicateCodeMetadataCreator(){
			@Override
			DuplicateCodeMetadataCollector getCollector() {
				return collectorMock;
			}
		};
	}

	@Test
	public void shouldCreateMetaDataForTestMethod() throws Exception {
		
		IMethod testMethod = getTestMethod();
		duplicateCodeMetadataCreator.createSmellMetaData(testMethod);
		String line1InMethod = "inta=5";
		verify(collectorMock).save(eq(line1InMethod.hashCode()), eq(testMethod.getElementName()));
	}


	private IMethod getTestMethod() throws Exception{
		String methodSignature = "testMethod";
		return baseSmellDetectorTest.getMethod(methodSignature);
	}
	
	

}
