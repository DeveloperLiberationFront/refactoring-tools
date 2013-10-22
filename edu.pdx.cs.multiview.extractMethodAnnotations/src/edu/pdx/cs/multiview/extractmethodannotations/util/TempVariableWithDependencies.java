package edu.pdx.cs.multiview.extractmethodannotations.util;

import java.util.Collection;
import java.util.SortedSet;

import org.apache.commons.collections15.CollectionUtils;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;

import edu.pdx.cs.multiview.extractmethodannotations.Settings;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.DependencyAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.PAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.VariableAnnotationCollection;
import edu.pdx.cs.multiview.extractmethodannotations.annotations.DependencyAnnotation.VectorDependencyAnnotation;
import edu.pdx.cs.multiview.extractmethodannotations.ast.AST_Node;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Declaration;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference;
import edu.pdx.cs.multiview.util.CollectionUtils2;

/**
 * I am a temp variable from which you can ask dependency annotations
 * 
 * @author emerson
 *
 */
public class TempVariableWithDependencies extends TempVariable{
	
	public TempVariableWithDependencies(Variable_Declaration decl) {
		super(decl);
	}

	public VariableAnnotationCollection dependencyAnnotations(ITextSelection scope) {
		
		VariableAnnotationCollection answer = new VariableAnnotationCollection();
		
		collectParameters(scope, answer);
		collectReturns(scope, answer);
		
		return answer;
	}

	private void collectReturns(ITextSelection scope, VariableAnnotationCollection answer) {
		//return values
		SortedSet<Variable_Reference> writesInSelection = referencesWithin(scope,true,false,true);
		if(!writesInSelection.isEmpty()){
			Variable_Reference lastWrite = writesInSelection.last();
			ITextSelection postSelectionScope = variableScopeAfter(scope);
			
			//connect the last write to the first read after the selection
			for(Variable_Reference postRef : referencesWithin(postSelectionScope,false,true, true)){
				
				answer.addReturn(getAnnotation(scope, lastWrite, postRef));
				break;
			}
			
			//return values due to references above in a loop
			Position loopPosition = lastWrite.outermostLoopRangeBefore(scope.getOffset());
			
			//if (the write is in a loop) and (the declaration is made outside the loop (naive...))
			if(loopPosition!=null && !AST_Node.within(scope,decl)){
				ITextSelection preWriteScope = new TextSelection(loopPosition.offset,
						scope.getOffset()+scope.getLength()-loopPosition.offset);
				
				Variable_Reference firstRefInLoop = referencesWithin(preWriteScope,false,true,true).first();
				
				if(!firstRefInLoop.isWrite())
					answer.putBackwardReturn(getAnnotation(scope, lastWrite, firstRefInLoop));
			}
		}
	}


	private void collectParameters(ITextSelection scope, VariableAnnotationCollection answer) {
		int lineTarget = scope.getOffset();
		Variable_Reference lastWrite = writeBefore(lineTarget);
		if(lastWrite!=null){
			for(Variable_Reference referenceInScope : referencesWithin(scope,false,true,true)){
				
					PAnnotation<? extends DependencyAnnotation> ann = 
						getAnnotation(lineTarget, true, lastWrite, referenceInScope);
		
					ann.annotation.setVisible(Settings.showParameterDependencies);
					answer.addParameter(ann);
					
					break;
			}
		}
	}
	
	private PAnnotation<? extends DependencyAnnotation> getAnnotation(ITextSelection scope, Variable_Reference lastWrite, Variable_Reference postRef) {
		
		return getAnnotation(scope.getOffset()+scope.getLength(), false, lastWrite, postRef);
	}
	
	private PAnnotation<? extends DependencyAnnotation> getAnnotation(int lineTarget, boolean isParameter, Variable_Reference source, Variable_Reference target) {

		return VectorDependencyAnnotation.getAnnotation(target, source, isParameter, lineTarget);
	}

	/**
	 * @param scope
	 * 
	 * @return	a range that starts at the end of scope and ends where my last 
	 * 			reference ends
	 */
	private ITextSelection variableScopeAfter(ITextSelection scope) {
		int postSelectionStart = scope.getOffset()+scope.getLength();
		
		Position position = fullScope();
		int postSelectionEnd = position.length + position.offset;
		ITextSelection postSelectionScope = new TextSelection(postSelectionStart,
												Math.max(0, postSelectionEnd-postSelectionStart));
		return postSelectionScope;
	}

	/**
	 * @return	a range covering all annotations
	 */
	@SuppressWarnings("unchecked")
	private Position fullScope() {
		
		int start = decl.getStartPosition();
		int end = AST_Node.lastNode(CollectionUtils2.flatten(decl.getName(),refs)).getEndPosition(); 
		
		Position p = new Position(start,Math.max(decl.getLength(), end-start));
		
		return p;
	}
	
	/**
	 * @param position
	 * 
	 * @return	the last occuring write before the given position
	 */
	private Variable_Reference writeBefore(int position) {
		
		ITextSelection scope = new TextSelection(decl.getName().getStartPosition()-1,
									position-decl.getName().getStartPosition());
		
		Collection<Variable_Reference> allWrites = CollectionUtils.select( 
														refs,withinPredicate(scope,false,true)
													);
		
		if(!allWrites.isEmpty()){
			return AST_Node.lastNode(allWrites);
		}
		Variable_Reference r = decl.getName();
		
		if(AST_Node.within(scope,r))
			return decl.getName();
		
		return null;
	}
	
}
