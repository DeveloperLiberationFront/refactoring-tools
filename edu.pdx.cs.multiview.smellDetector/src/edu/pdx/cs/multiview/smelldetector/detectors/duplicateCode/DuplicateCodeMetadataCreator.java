package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.indexer.MethodSmellMetadataCreator;

public class DuplicateCodeMetadataCreator implements MethodSmellMetadataCreator{

	private DuplicateCodeMetadataCollector collector;

	public DuplicateCodeMetadataCreator() {
		collector = DuplicateCodeMetadataCollector.getInstance();
	}
	
	@Override
	public void createSmellMetaData(IMethod method) {
		try {
			String source = method.getSource();
			
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}	
	}
	
	DuplicateCodeMetadataCollector getCollector(){
		return collector;
	}

}
