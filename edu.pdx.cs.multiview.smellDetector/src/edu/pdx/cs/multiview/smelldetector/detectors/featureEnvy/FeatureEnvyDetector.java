package edu.pdx.cs.multiview.smelldetector.detectors.featureEnvy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.pdx.cs.multiview.jdt.util.MemberReference;
import edu.pdx.cs.multiview.smelldetector.detectors.ClassSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.MethodSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class FeatureEnvyDetector extends SmellDetector<EnvyInstance>{

	private ClassEnvyRating ratings = new ClassEnvyRating();
	
	public FeatureEnvyDetector(Flower f) {
		super(f);
	}

	@Override
	public double obviousness() {	return 0.5;		}

	@Override
	public EnvyInstance calculateComplexity(List<IMethod> visibleMethods) {
		for (IMethod m : visibleMethods)
			ratings.cache(m);

		return new EnvyInstance(ratings,visibleMethods);
	}
	
	/*
	 * for debugging purposes only - clears the cache
	 */
	@SuppressWarnings("unused")
	private void clear(){
		ratings.rs.clear();
	}


	@Override
	public String getName() {
		return "Feature Envy";
	}
	

	
	@Override
	public void showDetails() {
		new FeatureEnvyExplanationWindow(currentSmell(),sourceViewer());
	}
}

/**
 * I represent a set of feature envy smells for
 * a region of an {@link ICompilationUnit}
 */
class EnvyInstance extends ClassEnvyRating implements SmellInstance{
	
	private Collection<IMethod> visibleMethods;
	
	public EnvyInstance(ClassEnvyRating rating, Collection<IMethod> visibleMethods){
		super.rs = rating.rs;
		this.visibleMethods = visibleMethods;
	}

	/*
	 * Magnitudes are aggregations of the per-method magnitudes.
	 */
	public double calculateMagnitude() {
		double maxMagnitude = 0;
		for (MethodEnvyRating methodRating : ratings()) {
			maxMagnitude = Math.max(methodRating.magnitude(),maxMagnitude);
		}

		return Math.min(maxMagnitude,1.0);
	}

	@Override
	protected Collection<MethodEnvyRating> ratings() {
		
		List<MethodEnvyRating> ratings = new LinkedList<MethodEnvyRating>();
		//collect values on key predicates
		for(Map.Entry<IMethod, MethodEnvyRating> r : rs.entrySet())
			if(visibleMethods.contains(r.getKey()))
				ratings.add(r.getValue());
		
		return ratings;
	}
	
	@SuppressWarnings("serial")
	public Set<MemberReference> uniqueThisReferences(){ 
		//this code sort of duplicates FeatureEnvyOverlay>>assignReferences
		Collection<MemberReference> thisRefs = 
			thisReferences(new HashSet<MemberReference>(){
				public boolean add(MemberReference ref){
					for(MemberReference otherRef : this){
						if(otherRef.equals(ref)){
							((MemberReferenceDuplicate)otherRef).dups++;
							return false;
						}
					}
					return super.add(new MemberReferenceDuplicate(ref));
				}
		});
		return new TreeSet<MemberReference>(thisRefs);
	}
	
	public List<MemberReference> thisReferences(){
		return thisReferences(new ArrayList<MemberReference>());
	}

	private <C extends Collection<MemberReference>> C thisReferences(C coll) {
		
		for(MethodEnvyRating rating : ratings()){
			coll.addAll(rating.thisReferences());
		}
		
		return coll;
	}
	

	@Override
	public int uniqueClassesReferenced() {	
		return super.uniqueClassesReferenced() +
			(uniqueThisReferences().isEmpty() ? 0 : 1);
	}
	
	@Override
	public int uniqueItemsReferenced() {
		return super.uniqueItemsReferenced() +
					uniqueThisReferences().size();
	}
	
	public List<MemberReference> references(){
		List<MemberReference> refs = new ArrayList<MemberReference>();

		refs.addAll(items());
		refs.addAll(thisReferences());
		
		return refs;
	}
}

/**
 *	I represent a set of lazily initialized feature envy smells
 *	for an entire {@link ICompilationUnit}
 */
class ClassEnvyRating extends ClassSmellRating<MethodEnvyRating, MemberReference>{

	@Override
	protected MethodEnvyRating createMethodRating(IMethod m) {
		return new MethodEnvyRating(m);
	}

	@Override
	public double calculateMagnitude() {
		return 0;
	}
}

/**
 * I'm an envy rating for one method
 */
class MethodEnvyRating extends MethodSmellRating<MemberReference>{

	private List<MemberReference> thisReferences;
	
	public MethodEnvyRating(IMethod m) {
		super(m);
	}

	@Override
	protected ASTVisitor getVisitor() {
		return new ASTVisitor(){
			
			public boolean visit(MethodInvocation inv){
				proces(MemberReference.with(inv));
				return true;
			}
			
			public boolean visit(SimpleName name){
				
				IBinding binding = name.resolveBinding();
				if(binding != null && binding instanceof IVariableBinding){
					IVariableBinding vbind = (IVariableBinding)binding;
					if(vbind.isField()){
						proces(MemberReference.with(name,vbind));
						return false;
					}
				}
				
				return true;
			}
			
			public boolean visit(ClassInstanceCreation constructor){				
				proces(MemberReference.with(constructor));				
				return true;
			}

			private void proces(MemberReference mb) {
				if(mb==null)
					return;
				
				//if referenced class is null (typically an array.length), skip it
				if(mb.referencedClass()==null)
					return;
				
				process(mb.referencedClass(),mb);
			}
		};
	}
	
	protected void process(ITypeBinding clazz, MemberReference e) {
		if(e.declaringClass().resolveBinding().equals(clazz)){
			thisReferences().add(e);
		}else{
			super.process(clazz, e);
		}
	}
	
	public List<MemberReference> thisReferences(){
		if(thisReferences==null)
			thisReferences = new ArrayList<MemberReference>();
		return thisReferences;
	}

	/**
	 * @return a value between 0 and 1
	 */
	public double magnitude() {
		
		int classRefs = classesReferenced().size();
		
		if(classRefs==0)
			return 0.0;
		
		int thisMemberRefs = thisReferences().size();
			
		int remainder = 0;
		for (ITypeBinding clazz : classesReferenced()) {
			List<MemberReference> refs = super.smells(clazz);
			remainder += Math.max(0,refs.size() - thisMemberRefs);		
		}
		
		return Math.min(1.0,Math.log(remainder) / 4);
	}
}