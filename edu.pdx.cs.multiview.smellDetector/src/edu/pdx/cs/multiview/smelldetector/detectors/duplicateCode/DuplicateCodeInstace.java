package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;

public class DuplicateCodeInstace implements SmellInstance {
	List<IMethod> visibleMethods;

	public DuplicateCodeInstace(List<IMethod> visibleMethods) {
		this.visibleMethods = visibleMethods;
	}

	@Override
	public double magnitude() {
		int numberOfCommonLines = 0;
		for (IMethod method : visibleMethods) {
			numberOfCommonLines +=getNumberOfDuplicateLinesForMethod(method);
		}
		return (numberOfCommonLines-2)/4;
	}
	
	int getNumberOfDuplicateLinesForMethod(IMethod method){
		int numberOfDuplicateLines = 0;
		DuplicateCodeMetadataCollector collector = getCodeMetadataCollector(method.getJavaProject());
		try {
			String[] methodLines = DuplicateCodeUtil.getMethodLinesWithoutTabsAndSpaces(method);
			LinkedHashMap<Integer, Set<ClassAndMethodName>>  lineByLineMapping = new LinkedHashMap<Integer, Set<ClassAndMethodName>>(); 
			for (String line : methodLines) {
				// only consider the lines that has number of alphabets  more that 2 
				// We dont want to consider the last line which has "}".  May we we can come up with a better handling for this
				if(line.length() > 2)
					lineByLineMapping.put(line.hashCode(), collector.getClassAndMethodNames(line.hashCode()));
			}
			 HashMap<ClassAndMethodName, Integer> methodsToRepeatedLinesMap = getMethodsToRepeatedLinesMap(lineByLineMapping);
			 numberOfDuplicateLines = getMaxNumberOfRepeatedLines(methodsToRepeatedLinesMap);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return numberOfDuplicateLines;
	}
	
	

	private int getMaxNumberOfRepeatedLines(HashMap<ClassAndMethodName, Integer> methodsToRepeatedLinesMap) {
		int max = 0;
		for(Entry<ClassAndMethodName, Integer> entry: methodsToRepeatedLinesMap.entrySet()){
			if(max < entry.getValue()){
				max = entry.getValue();
			}
		}
		return max;
	}

	
	/*
	 * hash1 -> [method1, method2 , method3]
	 * hash2 -> [method1, method4]
	 * hash3 -> [method1, method5]
	 * hash4 -> [method1, method2, method5]
	 * hash5 -> [method2]
	 * hash6 -> [method2]
	 * 
	 * answer is 
	 * [method1 -> 4]
	 * [method2 -> 3]
	 * [method5 -> 2]
	 * [method3 -> 1]
	 * [method4 -> 1]
	 * 
	 */
	
	/**
	 * For each entry in list
	 * 		For each string in entry
	 * 		Remove all pairs from HashMap that weren't in the last entry and are present in current entry
	 * 			If string in HashMap, increment the value
	 * 			Else add to HashMap with value 1
	 *  Update longest chain length
	 *  
	 */
	HashMap<ClassAndMethodName, Integer> getMethodsToRepeatedLinesMap(
			LinkedHashMap<Integer, Set<ClassAndMethodName>> lineToMethodMapping) {
		HashMap<ClassAndMethodName, Integer> linesMatchCountMap = new HashMap<ClassAndMethodName, Integer>();

		Entry<Integer, Set<ClassAndMethodName>> lastEntry = null;
		Entry<Integer, Set<ClassAndMethodName>> currentEntry = null;
		Iterator<Entry<Integer, Set<ClassAndMethodName>>> iterator = lineToMethodMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			currentEntry = iterator.next();
			// Remove all pairs from HashMap that weren't in the last entry and
			// are present in current entry
			if (lastEntry != null) {
				for (ClassAndMethodName method : currentEntry.getValue()) {
					if (currentEntry.getValue().contains(method) && !lastEntry.getValue().contains(method)) {
						linesMatchCountMap.remove(method);
					}
				}
			}
			Set<ClassAndMethodName> methodsThatContainLine = currentEntry.getValue();
			for (ClassAndMethodName classAndMethod : methodsThatContainLine) {
				if (linesMatchCountMap.containsKey(classAndMethod)) {
					linesMatchCountMap.put(classAndMethod, linesMatchCountMap.get(classAndMethod) + 1);
				} else {
					linesMatchCountMap.put(classAndMethod, 1);
				}
			}
			lastEntry = currentEntry;
		}
		return linesMatchCountMap;
	}

	DuplicateCodeMetadataCollector getCodeMetadataCollector(IJavaProject project) {
		return DuplicateCodeMetadataCollector.getInstance(project);
	}

}
