package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
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

	public ClumpGroup getGroup(IJavaProject project) {
		Set<IMethod> methodsOfGroup = new HashSet<IMethod>();
		for (String[] methodKey : methodKeys) {
			try {
				IType type = project.findType(methodKey[0]);
				IMethod[] methods = type.getMethods();
				for (IMethod iMethod : methods) {
					if (iMethod.getSignature().equals(methodKey[1])) {
						methodsOfGroup.add(iMethod);
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return new ClumpGroup(signature, methodsOfGroup);
	}

	@Override
	public String toString() {
		StringBuilder description = new StringBuilder("Signature : "+ signature+ "\n Methods :");
		for (String[] methodKey : methodKeys) {
			description.append("\t " + Arrays.toString(methodKey));
		}
		return description.toString();
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