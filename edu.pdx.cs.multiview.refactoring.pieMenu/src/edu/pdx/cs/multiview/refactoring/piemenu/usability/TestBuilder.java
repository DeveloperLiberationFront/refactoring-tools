package edu.pdx.cs.multiview.refactoring.piemenu.usability;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import edu.pdx.cs.multiview.refactoring.piemenu.PieMenuBuilder;

public abstract class TestBuilder extends PieMenuBuilder {

	public TestBuilder() {
		super();
	}

	@Override
	protected void fillForExpression(Expression node) {}

	@Override
	protected void fillForModifier(Modifier node) {}

	@Override
	protected void fillForStatement(ASTNode[] nodes) {}

}