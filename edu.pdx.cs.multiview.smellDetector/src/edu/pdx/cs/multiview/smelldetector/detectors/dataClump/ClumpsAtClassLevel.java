package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;


public class ClumpsAtClassLevel {

	private IType iType;

	// We don't want to create too many clumps for methods that have a lot of
	// arguments, hence we have kept the limit as 10
	private static final int MAX_ARGUMENTS_TO_BE_CONSIDERED_FOR_CLUMPS = 10;
	private ClumpCollector clumpCollector;

	public ClumpsAtClassLevel(IType iType) {
		this.iType = iType;
		String projectName = iType.getJavaProject().getElementName();
		clumpCollector = ClumpCollector.createCumpCollector(projectName);
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

	public List<ClumpSignature> getClumpSignaturesForParameterNames(String[] names) {
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

	public List<ClumpSignature> combination(List<String> strings) {
		return combination(strings.subList(0, 0), strings);
	}

	public void createClumps() {
		try {
			getAllMethodsAndCreateClumps();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

	}

	private void getAllMethodsAndCreateClumps() throws JavaModelException {
		IMethod[] methods = iType.getMethods();
		for (IMethod iMethod : methods) {
			createClumpsForMethod(iMethod);
		}
	}

	private void createClumpsForMethod(IMethod iMethod) {
		try {
			List<ClumpSignature> sigs = getClumpSignaturesForParameterNames(iMethod.getParameterNames());
			addClumptSignaturesToCache(iMethod, sigs);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void addClumptSignaturesToCache(IMethod iMethod, List<ClumpSignature> sigs) {
		for (ClumpSignature sig : sigs) {
			clumpCollector.addToCache(sig, iMethod);
		}
	}

}