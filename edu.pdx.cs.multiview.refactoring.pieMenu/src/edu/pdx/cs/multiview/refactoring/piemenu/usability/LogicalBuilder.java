package edu.pdx.cs.multiview.refactoring.piemenu.usability;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class LogicalBuilder extends TestBuilder {

	@Override 
	protected void fillFieldMenu(SimpleName name, final IVariableBinding binding) {		
		addPullUp(binding);
		addNull();
		addPushDown(binding);
		addEncapsulate(binding);
	}

	@Override
	protected void fillTempMenu(VariableDeclaration parent) {

		addNull();
		addInlineLocal(parent);		
		addNull();	
		addToInstance(parent);
	}

	@Override
	protected void fillMethodMenu(ASTNode node, IMethodBinding binding) {
		addPullUp(binding);		
		addInlineMethod(node);
		addPushDown(binding);		
		addIndirection(binding);
	}
}
