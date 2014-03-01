package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.indexer.MethodKeyValueGenerator;

class ClumpGroupHolder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3079078584731861774L;
	private ClumpSignature signature;
	private Set<String[]> methodKeys = new HashSet<String[]>();

	public ClumpGroupHolder(ClumpGroup clumpGroup) {
		this.signature = clumpGroup.getSignature();
		Set<IMethod> methods = clumpGroup.getMethods();
		for (IMethod iMethod : methods) {
			String[] typeAndMethodSignature = getMethodKeyValueGenerator().getClassNameAndMethodSignature(iMethod);
			this.methodKeys.add(typeAndMethodSignature);
		}
	}

	private MethodKeyValueGenerator getMethodKeyValueGenerator() {
		return MethodKeyValueGenerator.getInstance();
	}

	public synchronized void add(IMethod m) {
		this.methodKeys.add(getMethodKeyValueGenerator().getClassNameAndMethodSignature(m));
	}

	public ClumpGroup getGroup(IJavaProject project) {
		Set<IMethod> methodsOfGroup = new HashSet<IMethod>();
		for (String[] classNameAndMethodSignature : methodKeys) {
			IMethod method = getMethodKeyValueGenerator().getMethod(project, classNameAndMethodSignature);
 			methodsOfGroup.add(method);
		}
		return new ClumpGroup(signature, methodsOfGroup);
	}

	@Override
	public String toString() {
		StringBuilder description = new StringBuilder("Signature : "+ signature+ "\n Methods : \n");
		for (String[] methodKey : methodKeys) {
			description.append("\t " + methodKey[1] +" \n");
		}
		return description.toString();
	}
	
}