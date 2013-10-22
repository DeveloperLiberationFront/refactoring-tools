package edu.pdx.cs.multiview.extractmethodannotations.ast;

import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.ContinueAnnotation;

public class Continue_Statement extends LocalCFStatement<ContinueStatement>{

	public Continue_Statement(ContinueStatement node, Statement s) {
		super(node,s);
	}
	
	public static Transformer<Map.Entry<ContinueStatement,Statement>, Continue_Statement> continueTransform(){
		return new Transformer<Map.Entry<ContinueStatement,Statement>, Continue_Statement>(){
			public Continue_Statement transform(Map.Entry<ContinueStatement,Statement> input) {
				return new Continue_Statement(input.getKey(),input.getValue());
			}
		};
	}

	@Override
	protected ContinueAnnotation annotation() {
		return new ContinueAnnotation();
	}
	
	public int loopNameLength() {
		if(target instanceof ForStatement)
			return 3;
		else if(target instanceof WhileStatement)
			return 5;
		else if(target instanceof EnhancedForStatement)
			return 3;
		else if(target instanceof DoStatement)
			return 2;
		else if(target instanceof SwitchStatement)
			return 6;
		else
			return 1;
	}
}