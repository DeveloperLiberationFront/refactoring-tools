package edu.pdx.cs.multiview.smelldetector.detectors.dataClump.clumps;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;

public class ClumpGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7805315175123528803L;
	
	private ClumpSignature signature;
	private Set<IMethod> methods;

	public ClumpGroup(ClumpSignature signature, Set<IMethod> methods) {
		this.signature = signature;
		this.methods = methods;
	}

	public ClumpGroup(ClumpSignature signature, IMethod method) {
		this.signature = signature;
		this.methods = new HashSet<IMethod>();
		this.methods.add(method);
	}

	public void add(IMethod m) {
		methods.add(m);
	}

	ClumpSignature getSignature() {
		return signature;
	}


	Set<IMethod> getMethods() {
		return methods;
	}

	
	

}