package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.indexer.MethodSmellMetadataCreator;

public class DataClumpCreator implements MethodSmellMetadataCreator {
	private static final int MAX_ARGUMENTS_TO_BE_CONSIDERED_FOR_CLUMPS = 10;
	private ClumpCollector clumpCollector;

	public DataClumpCreator(IJavaProject project) {
		clumpCollector = ClumpCollector.getClumpCollector(project);
	}

	@Override
	public void createSmellMetaData(IMethod iMethod) {
		try {
			List<ClumpSignature> sigs = getClumpSignaturesForParameterNames(iMethod.getParameterNames());
			addClumptSignaturesToCache(iMethod, sigs);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates various combinations of the Clump Signatures(combination
	 * parameter names)
	 * 
	 * 
	 * @param names
	 * @return
	 */
	private List<ClumpSignature> getClumpSignaturesForParameterNames(String[] names) {
		if (names.length > MAX_ARGUMENTS_TO_BE_CONSIDERED_FOR_CLUMPS) {
			return new ArrayList<ClumpSignature>();
		}
		List<String> parameters = Arrays.asList(names);
		if (parameters.size() < 2) {
			List<ClumpSignature> sigs = new ArrayList<ClumpSignature>(1);
			if (parameters.size() == 1)
				sigs.add(new ClumpSignature(parameters));
			return sigs;
		}

		return combination(parameters.subList(0, 0), parameters);
	}
	
	private List<ClumpSignature> combination(List<String> prefix, List<String> rest) {
		List<ClumpSignature> sigs = new LinkedList<ClumpSignature>();
		if (rest.size() > 0) {
			List<String> newPrefix = new LinkedList<String>(prefix);
			newPrefix.add(rest.get(0));
			if (newPrefix.size() > 1) {
				// we're not interested in prefixes of length 1
				sigs.add(new ClumpSignature(newPrefix));
			}
			sigs.addAll(combination(newPrefix, rest.subList(1, rest.size())));
			sigs.addAll(combination(prefix, rest.subList(1, rest.size())));
		}
		return sigs;
	}

	private void addClumptSignaturesToCache(IMethod iMethod, List<ClumpSignature> sigs) {
		for (ClumpSignature sig : sigs) {
			clumpCollector.addToCache(sig, iMethod);
		}
	}

	
	public ClumpCollector getCollector() {
		return clumpCollector;
	}
	
	

}
