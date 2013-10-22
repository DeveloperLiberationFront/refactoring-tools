package edu.pdx.cs.multiview.extractmethodannotations.ast;

import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.Statement;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.BreakAnnotation;

public class Break_Statement extends LocalCFStatement<BreakStatement>{

	public Break_Statement(BreakStatement node, Statement target) {
		super(node,target);
	}
	
	public static Transformer<Map.Entry<BreakStatement,Statement>, Break_Statement> breakTransform(){
		return new Transformer<Map.Entry<BreakStatement,Statement>, Break_Statement>(){
			public Break_Statement transform(Map.Entry<BreakStatement,Statement> input) {
				return new Break_Statement(input.getKey(),input.getValue());
			}
		};
	}

	@Override
	protected BreakAnnotation annotation() {
		return new BreakAnnotation();
	}
}
