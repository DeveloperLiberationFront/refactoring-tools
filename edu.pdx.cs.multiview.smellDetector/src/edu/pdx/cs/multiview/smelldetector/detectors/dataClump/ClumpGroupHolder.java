package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;


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
			// String keyForMethod = getKeyForMethod(iMethod);
			// ClumpCollector.allMethods.put(keyForMethod, iMethod);
			String[] typeAndMethodSignature = new String[2];
			typeAndMethodSignature[0] = iMethod.getDeclaringType().getFullyQualifiedName();
			try {
				typeAndMethodSignature[1] = iMethod.getSignature();
			} catch (JavaModelException e) {
				e.printStackTrace();
				continue;
			}
			this.methodKeys.add(typeAndMethodSignature);
		}
	}

	public synchronized void add(IMethod m) {
		String[] typeAndMethodSignature = new String[2];
		typeAndMethodSignature[0] = m.getDeclaringType().getFullyQualifiedName();
		try {
			typeAndMethodSignature[1] = m.getSignature();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return;
		}
		this.methodKeys.add(typeAndMethodSignature);
	}

	/*
	 * private String getKeyForMethod(IMethod iMethod) { StringBuilder key = new
	 * StringBuilder(); key.append(iMethod.getTypeRoot().getElementName());
	 * key.append(iMethod.getElementName()); String[] parameterTypes =
	 * iMethod.getParameterTypes(); for (String string : parameterTypes) {
	 * key.append(string); } return key.toString(); }
	 */

	// http://whileonefork.blogspot.in/2010/11/interrogating-java-model-in-eclipse-jdt.html
}