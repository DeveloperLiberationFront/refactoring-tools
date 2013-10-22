package edu.pdx.cs.multiview.extractmethodannotations.util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.eclipse.jface.text.ITextSelection;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.PAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.TempVariableAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.ast.AST_Node;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Declaration;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference;

/**
 * Represents a variable local to a method
 * 
 * @author emerson
 */
public class TempVariable{
	
	public final Variable_Declaration decl;
	
	protected final List<Variable_Reference> refs;
	
	/**
	 * Creates a {@link TempVariable} based on a declared
	 * variable.
	 * 
	 * @param decl
	 */
	public TempVariable(Variable_Declaration decl){
		
		this.decl = decl;
		refs = new ArrayList<Variable_Reference>(3);
	}
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof TempVariable))
			return false;
		
		return ((TempVariable)other).decl.equals(this.decl);
	}

	/**
	 * @return my annotations
	 */
	public List<PAnnotation<TempVariableAnnotation>> getAnnotations() {
		
		ArrayList<PAnnotation<TempVariableAnnotation>> l = new ArrayList<PAnnotation<TempVariableAnnotation>>();

		l.add(toAnnotation.transform(decl.getName()));
		CollectionUtils.collect(refs, toAnnotation, l);
		
		return l;
	}
	
	private final NodeToAnnotationTransformer toAnnotation = new NodeToAnnotationTransformer();
	
	private class NodeToAnnotationTransformer implements Transformer<Variable_Reference, PAnnotation<TempVariableAnnotation>	>{
		public PAnnotation<TempVariableAnnotation> transform(Variable_Reference input) {
			return TempVariableAnnotation.getAnnotation(input);
		}
	}


	/**
	 * @param scope
	 * @param includeDecl	whether to include the declaration in the result
	 * @param includeReads	whether to include reads in the result
	 * @param includeWrites whether to include writes in the result
	 * 
	 * @return	all variables that occur within the argument, ordered by thier offsets
	 * 			in the file in which they occur
	 * 			
	 */
	protected SortedSet<Variable_Reference> referencesWithin(final ITextSelection scope, 
															 final boolean includeDecl, 
															 final boolean includeReads, 
															 final boolean includeWrites) {
		
		SortedSet<Variable_Reference> variables = new TreeSet<Variable_Reference>(AST_Node.nodeComparator());
		Predicate<Variable_Reference> within = withinPredicate(scope, includeReads, includeWrites);
		
		if(includeDecl && within.evaluate(decl.getName()))
			variables.add(decl.getName());
		
		CollectionUtils.select(refs, within, variables);
	
		return variables;
	}
	
	/**
	 * @param scope
	 * @return
	 */
	public static Predicate<Variable_Reference> withinPredicate(final ITextSelection scope, 
																final boolean includeReads, 
																final boolean includeWrites){
		return new Predicate<Variable_Reference>(){
			public boolean evaluate(Variable_Reference e) {
				return ((includeReads==e.isRead()) || (includeWrites==e.isWrite())) && AST_Node.within(scope,e);
			}
		};
	}
}