package edu.pdx.cs.multiview.refactoring.piemenu.usability;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class RandomBuilder extends TestBuilder {
	
	@Override 
	protected void fillFieldMenu(SimpleName name, final IVariableBinding binding) {		
		addPushDown(binding);
		addNull();
		addEncapsulate(binding);
		addPullUp(binding);
	}

	@Override
	protected void fillTempMenu(VariableDeclaration parent) {
		addToInstance(parent);
		addNull();
		addNull();
		addInlineLocal(parent);		
	}

	@Override
	protected void fillMethodMenu(ASTNode node, IMethodBinding binding) {
		addInlineMethod(node);
		addPushDown(binding);
		addPullUp(binding);	
		addIndirection(binding);
	}
}
