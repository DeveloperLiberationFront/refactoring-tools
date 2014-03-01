package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.indexer.MethodKeyValueGenerator;
import edu.pdx.cs.multiview.smelldetector.indexer.MethodSmellMetadataCreator;

public class DuplicateCodeMetadataCreator implements MethodSmellMetadataCreator {

	
	private DuplicateCodeMetadataCollector collector;

	public DuplicateCodeMetadataCreator(IJavaProject project) {
		collector = DuplicateCodeMetadataCollector.getInstance(project);
	}

	@Override
	public void createSmellMetaData(IMethod method) {
		try {
			String[] lines = DuplicateCodeUtil.getMethodLinesWithoutTabsAndSpaces(method);
			for (String lineWithoutSpace : lines) {
				int hashCode = lineWithoutSpace.hashCode();
				getCollector().save(hashCode, getMethodKeyValueGenerator().getClassNameAndMethodSignature(method));
			}
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	public DuplicateCodeMetadataCollector getCollector() {
		return collector;
	}

	private MethodKeyValueGenerator getMethodKeyValueGenerator() {
		return MethodKeyValueGenerator.getInstance();
	}

}
