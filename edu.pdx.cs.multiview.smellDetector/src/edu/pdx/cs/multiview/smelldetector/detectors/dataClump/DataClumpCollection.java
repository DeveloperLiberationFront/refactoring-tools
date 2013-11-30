package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

class DataClumpCollection{
	
	private Map<ClumpSignature,ClumpGroup> clumps = 
		new HashMap<ClumpSignature, ClumpGroup>();
	
	public void addClump(IMethod m) throws JavaModelException {

		String[] names = m.getParameterNames();
		List<ClumpSignature> sigs = ClumpSignature.from(names);
		for (ClumpSignature sig : sigs) {
			ClumpGroup existingGroup = clumps.get(sig);
			if(existingGroup==null){
				ClumpGroup g = new ClumpGroup(sig);
				g.add(m);
				clumps.put(sig, g);
			}else{
				existingGroup.add(m);
			}	
		}		
	}

	public List<ClumpGroup> inGroupOf(IMethod method){
		try {
			List<ClumpSignature> sigs = ClumpSignature.from(method.getParameterNames());
			List<ClumpGroup> groups = new LinkedList<ClumpGroup>();
			for(ClumpSignature sig : sigs){
				groups.add(clumps.get(sig));
			}
			return groups;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
}