package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;

public abstract class TempAction extends ASTAction<VariableDeclaration>{

	@Override
	protected boolean isAcceptable(ASTNode node) {
		if(node.getNodeType()==ASTNode.SIMPLE_NAME && 
				node.getParent() instanceof VariableDeclaration){
			IBinding binding = ((SimpleName)node).resolveBinding();
			if(binding instanceof IVariableBinding){
				IVariableBinding vb = (IVariableBinding)binding;
				return !vb.isParameter() && !vb.isField();
			}
		}
		return false;
	}

	@Override
	protected Collection<VariableDeclaration> getSelectedItems(Regions regions) {
		
		Map<IBinding, SimpleName> bindingsToNames =
			new HashMap<IBinding, SimpleName>();
		
		for(ASTRegion region : regions){
			SimpleName name = (SimpleName)((ASTRegion)region).node;
			IBinding binding = name.resolveBinding();
			if(!bindingsToNames.containsKey(binding))
				bindingsToNames.put(binding, name);
		}
		
		List<VariableDeclaration> assignments = new ArrayList<VariableDeclaration>();
		
		for(SimpleName name : new HashSet<SimpleName>(bindingsToNames.values())){
		
			if(name.getParent() instanceof VariableDeclaration){
				assignments.add((VariableDeclaration) name.getParent());
			}		
		}
		
		return assignments;
	}
}