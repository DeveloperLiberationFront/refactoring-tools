package edu.pdx.cs.multiview.smelldetector.detectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.pdx.cs.multiview.jdt.util.ASTPool;
import edu.pdx.cs.multiview.jdt.util.JavaElementFinder;

public abstract class MethodSmellRating<SmellType>{
	
	public MethodSmellRating(IMethod m) {
		initRating(m);
	}

	private HashMap<ITypeBinding, List<SmellType>> expressions;
	
	public List<SmellType> smells() {
		
		List<SmellType> members = new LinkedList<SmellType>();
		for(List<SmellType> er : expressions.values())
			members.addAll(er);
		
		return members;
	}
	

	public List<SmellType> smells(ITypeBinding binding){
		return expressions.get(binding);
	}
	
	public Set<ITypeBinding> classesReferenced(){
		return expressions.keySet();
	}

	private void initRating(IMethod m) {
		ASTNode root = getNode(m);
		
		//when m has been deleted...
		if(root == null){
			return;
		}
		
		expressions = 
			new HashMap<ITypeBinding, List<SmellType>>();
		
		root.accept(getVisitor());
	}
	
	protected void process(ITypeBinding clazz, SmellType e) {
		if(!expressions.containsKey(clazz))
			expressions.put(clazz, new ArrayList<SmellType>(20));
		expressions.get(clazz).add(e);
	}
	
	protected abstract ASTVisitor getVisitor();
	
	private static ASTPool<ICompilationUnit> astPool = ASTPool.getDefaultCU();
	
	public MethodDeclaration getNode(IMethod m) {		
		CompilationUnit icu = astPool.getAST(m.getCompilationUnit());
		return JavaElementFinder.findMethodDeclaration(m,icu);
	}
}
