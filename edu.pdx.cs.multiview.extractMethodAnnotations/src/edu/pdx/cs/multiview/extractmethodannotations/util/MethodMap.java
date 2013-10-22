package edu.pdx.cs.multiview.extractmethodannotations.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.VariableAnnotationCollection;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference;
import edu.pdx.cs.multiview.jface.ComparisonTextSelection;
import edu.pdx.cs.multiview.jface.IComparableTextSelection;

/**
 * Associates methods with local variables
 * 
 * @author emerson
 */
@SuppressWarnings("serial") 
public class MethodMap extends HashMap<MethodDeclaration, List<TempVariableWithDependencies>>{

	private Stack<MethodDeclaration> methodStack = new Stack<MethodDeclaration>();
	
	/**
	 * Indicates that any reads or writes from here forward
	 * are within mDecl
	 * 
	 * @param mDecl
	 */
	public void start(MethodDeclaration mDecl){
		methodStack.push(mDecl);
		this.put(mDecl, new ArrayList<TempVariableWithDependencies>());
	}

	/**
	 * Indicates an end of method
	 * 
	 */
	public void end(){
		methodStack.pop();
	}
	
	public void add(TempVariableWithDependencies variable) {
		for(MethodDeclaration decl : methodStack)
			get(decl).add(variable);
	}
	
	public void refTo(Variable_Reference name) {
		TempVariable variable = getVariable(name);
		if(variable!=null)
			variable.refs.add(name);
	}
	
	private TempVariable getVariable(Variable_Reference name) {
		
		if(methodStack.isEmpty())
			return null;
		
		TempVariable variable = null;
		
		for(MethodDeclaration method : reverse(methodStack))
			if(variable==null)
				variable = CollectionUtils.find(reverse(get(method)), eqPred(name));
		
		return variable;
	}
	
	private Predicate<TempVariable> eqPred(final Variable_Reference name) {
		return new Predicate<TempVariable>(){
			public boolean evaluate(TempVariable variable) {
				return name.equals(variable);
			}};
	}

	private <T extends Object> List<T> reverse(List<T> l) {
		return edu.pdx.cs.multiview.util.CollectionUtils.reverse(l);
	}
	
	/**
	 * Adds an annotation to all scoped varibles in every method
	 * 
	 * @param region 
	 * @param annotationModel
	 */
	public VariableAnnotationCollection getAnnotations(ITextSelection selection) {
		

		if(selection==null)
			throw new IllegalArgumentException("Not allowed no mo'!");
		
		VariableAnnotationCollection allAnnotations = 
			new VariableAnnotationCollection();
		
		Enumeration<Color> colors = ColorManager.getColors();
		
		for(TempVariableWithDependencies var : variablesInSelection(selection)){
			VariableAnnotationCollection collection = 
				var.dependencyAnnotations(selection);
			if(collection.hasDependencies()){
				collection.addVariableAnnotations(var);
				collection.setColor(colors.nextElement());
				allAnnotations.merge(collection);
			}
		}
		
		//if there is more than one return value, set them as conflicting
		allAnnotations.checkForMultiReturn();
		
		return allAnnotations;
	}

	private Collection<TempVariableWithDependencies> variablesInSelection(ITextSelection selection) {
		Collection<Entry<MethodDeclaration, List<TempVariableWithDependencies>>> name = CollectionUtils.select(entrySet(), overlapsPredicate(selection));
		
		Set<TempVariableWithDependencies> deps = new HashSet<TempVariableWithDependencies>(10);
		for(Entry<MethodDeclaration, List<TempVariableWithDependencies>> e : name)
			deps.addAll(e.getValue());
		
		return deps;
	}

	
	private Predicate<Map.Entry<MethodDeclaration,?>> overlapsPredicate(final ITextSelection s) {
		return new Predicate<Map.Entry<MethodDeclaration,?>>(){
			public boolean evaluate(Map.Entry<MethodDeclaration, ?> entry) {
				return overlaps(entry.getKey(),s);
			}
			
			/**
			 * TODO: I think this is similar to a method in AST_Node, but they're not identical
			 * 
			 * @param decl
			 * @param region
			 * 
			 * @return	whether the region and decl's region in the source overlap, 
			 * 			or true if selection is null
			 */
			private boolean overlaps(MethodDeclaration decl, ITextSelection selection) {
				
				IComparableTextSelection declSelection = new ComparisonTextSelection(decl.getStartPosition(),decl.getLength());
				return !declSelection.compareTo(selection).equals(IComparableTextSelection.COMPARISON.NO_OVERLAP);
			}
		};
	}
}