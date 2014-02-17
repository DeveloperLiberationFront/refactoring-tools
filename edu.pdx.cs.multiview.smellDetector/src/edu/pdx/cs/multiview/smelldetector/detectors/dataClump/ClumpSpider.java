package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;

public class ClumpSpider implements SmellInstance {

	private List<IMethod> currentMethods = new ArrayList<IMethod>();

	public double magnitude() {
		Set<ClumpGroup> clumps = currentClumps();
		double magnitude = 0;
		for (ClumpGroup clump : clumps) {
			int clumpSize = clump.signatureSize();
			int clumpOccurences = clump.occurrences();
			magnitude += clumpSize * Math.pow(clumpOccurences, 1.75);
		}
		return Math.log(magnitude) / 4;
	}

	Set<ClumpGroup> currentClumps() {
		Set<ClumpGroup> currentClumps = new HashSet<ClumpGroup>();

		for (IMethod m : currentMethods) {
			ClumpCollector clumpColl = ClumpCollector.getClumpCollector(m.getJavaProject());
			for (ClumpGroup cg : clumpColl.inGroupOf(m)) {
				cg.mergeIfClumped(currentClumps);
			}
		}

		return currentClumps;
	}

	ICompilationUnit compilationUnit() {

		if (currentMethods.size() < 1)
			return null;

		return currentMethods.get(0).getCompilationUnit();
	}

	void spiderFrom(List<IMethod> methods) {
		this.currentMethods = methods;

		if (compilationUnit() == null)
			return;

	}
}
