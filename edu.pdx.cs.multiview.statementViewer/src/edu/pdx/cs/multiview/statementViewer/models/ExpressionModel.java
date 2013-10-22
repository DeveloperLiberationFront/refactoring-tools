package edu.pdx.cs.multiview.statementViewer.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.Expression;

public class ExpressionModel<Exp extends Expression> extends ASTModel<Exp>{
	
	public ExpressionModel(Exp e, Point p) {
		super(e,p);
	}

	public static <Exp extends Expression> ExpressionModel<Exp> 
			getExpressionFor(Exp expression, Point p) {
		
		return new ExpressionModel<Exp>(expression,p);
	}

	@Override
	public List<ASTModel> buildChildren() {
		return new ArrayList<ASTModel>(0);
	}

}
