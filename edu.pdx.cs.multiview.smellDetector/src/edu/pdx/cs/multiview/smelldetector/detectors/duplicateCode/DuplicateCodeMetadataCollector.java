package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

public class DuplicateCodeMetadataCollector {
	
	private static DuplicateCodeMetadataCollector collector  = new DuplicateCodeMetadataCollector();
	private DuplicateCodeMetadataCollector() {
		
	}
	
	public static DuplicateCodeMetadataCollector getInstance(){
		return collector;
	}
	
	public void save(int hashOfCode, String[] classAndMethodName){
		
	}
}
