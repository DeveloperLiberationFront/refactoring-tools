package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.indexer.MethodKeyValueGenerator;
import edu.pdx.cs.multiview.smelldetector.indexer.MethodSmellMetadataCreator;

public class DuplicateCodeMetadataCreator implements MethodSmellMetadataCreator {

	
	private DuplicateCodeMetadataCollector collector;

	public DuplicateCodeMetadataCreator() {
		collector = DuplicateCodeMetadataCollector.getInstance();
	}

	@Override
	public void createSmellMetaData(IMethod method) {
		try {
			String source = method.getSource();
			String newLineSeparator = System.getProperty( "line.separator" );
			String[] lines = source.split(newLineSeparator);
			String[] linesWithoutSpace = new String[lines.length];
			for (int i = 0; i < lines.length; i++) {
				// remove tabs and white spaces
				linesWithoutSpace[i] = lines[i].replaceAll("\\s","");
			}
			
			for (String lineWithoutSpace : linesWithoutSpace) {
				int hashCode = lineWithoutSpace.hashCode();
				getCollector().save(hashCode, getMethodKeyValueGenerator().getClassNameAndMethodSignature(method));
			}
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	DuplicateCodeMetadataCollector getCollector() {
		return collector;
	}

	private MethodKeyValueGenerator getMethodKeyValueGenerator() {
		return MethodKeyValueGenerator.getInstance();
	}

}
