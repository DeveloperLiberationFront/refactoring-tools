package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;

import edu.pdx.cs.multiview.jdt.util.ASTPool;

@SuppressWarnings("restriction")
class ClumpGroup{

	private ClumpSignature signature;
	private Set<IMethod> methods;
	
	public ClumpGroup(ClumpSignature signature, IMethod method) {
		this.signature = signature;
		this.methods = new HashSet<IMethod>();
		this.methods.add(method);
	}
	
	public ClumpGroup(ClumpSignature signature, Set<IMethod> methods) {
		this.signature = signature;
		this.methods= methods;
	}
	
	
	public ClumpGroup(ClumpSignature cs) {
		signature = cs;
		methods = new HashSet<IMethod>();
	}
	
	
	public int occurrences() {
		return methods.size();
	}

	public void mergeIfClumped(Set<ClumpGroup> group) {
		//add it if the signature contains two or more parameters
		//and there are two or more methods with that signature
		if(signatureSize()>1 && methods.size()>1)
			group.add(this);		
	}

	public int signatureSize() {
		return signature.size();
	}

	public void add(IMethod m){
		methods.add(m);
	}

	public Set<IMethod> methodsIn(ICompilationUnit icu) {
		HashSet<IMethod> result = new HashSet<IMethod>(methods);
		result.retainAll(allMethodsIn(icu));
		return result;
	}

	public static Set<IMethod> allMethodsIn(ICompilationUnit icu) {
		Set<IMethod> allMethodsInCU = new HashSet<IMethod>();
		//TODO: this does not include local types
		try {
			for(IType t : icu.getAllTypes()){
				try {
					for(IMethod m : t.getMethods()){
						allMethodsInCU.add(m);
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return allMethodsInCU;
	}

	
	public Set<SingleVariableDeclaration> parametersOf(IMethod m) {

		Set<SingleVariableDeclaration> params = 
			new HashSet<SingleVariableDeclaration>();
		CompilationUnit ast = ASTPool.getDefaultCU().getAST(m.getCompilationUnit());
		
		try {
			MethodDeclaration decl = 
				(MethodDeclaration)NodeFinder.perform(ast, m.getSourceRange());
			
			for(Object o : decl.parameters()){
				SingleVariableDeclaration param = (SingleVariableDeclaration) o;
				if(signature.contains(param.getName().getIdentifier()))
					params.add(param);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return params;
	}
	
	public String toString(){
		return signature.toString() + " ("+occurrences()+")";
	}

	public Iterable<IMethod> methods() {
		return methods;
	}
	
	Set<IMethod> getMethods() {
		return methods;
	}
	
	ClumpSignature getSignature() {
		return signature;
	}

}


class EmptyClumpGroup extends ClumpGroup{
	public EmptyClumpGroup(ClumpSignature cs) {
		super(cs, new HashSet<IMethod>());
	}
}