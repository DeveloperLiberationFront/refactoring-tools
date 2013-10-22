package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;

@SuppressWarnings("restriction")
public class ClumpSpider implements SmellInstance{

	private List<IMethod> currentMethods = new ArrayList<IMethod>();
	private Set<IMethod> visitedMethods = new HashSet<IMethod>();
	private DataClumpCollection clumpColl = new DataClumpCollection();
	
	public Set<ClumpGroup> currentClumps(){
		Set<ClumpGroup> currentClumps = new HashSet<ClumpGroup>();
		
		for(IMethod m : currentMethods){
			for(ClumpGroup cg : clumpColl.inGroupOf(m)){
				cg.mergeIfClumped(currentClumps);			
			}
		}
		
		return currentClumps;
	}
	
	public double magnitude() {		
		Set<ClumpGroup> clumps = currentClumps();
		double magnitude = 0;
		for (ClumpGroup clump : clumps) {
			int clumpSize = clump.signatureSize();
			int clumpOccurences = clump.occurrences();
			magnitude += clumpSize * Math.pow(clumpOccurences,1.75); 
		}
		return Math.log(magnitude) / 4;
	}
	
	

	public void spiderFrom(List<IMethod> methods) {
		this.currentMethods = methods;		
		
		if(compilationUnit()==null)
			return;
		
		for(IMethod m : ClumpGroup.allMethodsIn(compilationUnit())){
			try {
				process(m);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	private void process(IMethod m) throws JavaModelException {
		boolean doVisit = visitedMethods.add(m);
		if(!doVisit)
			return;
		
		clumpColl.addClump(m);
	}

	public ICompilationUnit compilationUnit() {	
		
		if(currentMethods.size()<1)
			return null;
		
		return currentMethods.get(0).getCompilationUnit();
	}	
}

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

class ClumpSignature{

	private final int signature;
	private final Set<String> names;
	
	private ClumpSignature(List<String> ns) {		
		int s = 0;
		for(String name : ns)
			s += name.hashCode();
		signature = s;
		names = new HashSet<String>(ns);
	}

	public static List<ClumpSignature> from(String[] names) {
		LinkedList<String> strings = new LinkedList<String>();
		for(String name : names){
			strings.add(name);
		}
		if(names.length < 2){
			List<ClumpSignature> sigs = new ArrayList<ClumpSignature>(1);
			if(names.length == 1)
				sigs.add(new ClumpSignature(strings));
			return sigs;
		}
		
		return combination(strings);
	}
	
    // print all subsets of the characters in s
    public static List<ClumpSignature> combination(List<String> strings) { 
    	return combination(strings.subList(0, 0),strings); 
    }
 
    private static List<ClumpSignature> combination(List<String> prefix, List<String> rest) {
    	List<ClumpSignature> sigs = new LinkedList<ClumpSignature>();
    	if (rest.size() > 0) {
    		List<String> newPrefix = new LinkedList<String>(prefix);
    		newPrefix.add(rest.get(0));
    		if(newPrefix.size()>1){//we're not interested in prefixes of length 1	    		
	    		sigs.add(new ClumpSignature(newPrefix));
    		}
            sigs.addAll(combination(newPrefix, rest.subList(1, rest.size())));
            sigs.addAll(combination(prefix,    rest.subList(1, rest.size())));
        }
    	return sigs;
    }  

	@Override
	public boolean equals(Object o){
		if(!(o instanceof ClumpSignature)){
			return false;
		}
		
		return ((ClumpSignature)o).signature==this.signature;
	}
	
	@Override
	public String toString(){
		return names.toString();
	}
	
	@Override
	public int hashCode(){
		return signature;
	}

	public boolean contains(String identifier) {
		return names.contains(identifier);
	}

	public int size() {
		return names.size();
	}
}


@SuppressWarnings("restriction")
class ClumpGroup{

	private ClumpSignature signature;
	private Set<IMethod> methods;
	
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
}
