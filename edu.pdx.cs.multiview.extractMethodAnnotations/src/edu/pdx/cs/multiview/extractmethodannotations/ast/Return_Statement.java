package edu.pdx.cs.multiview.extractmethodannotations.ast;

import org.apache.commons.collections15.Transformer;
import org.eclipse.jdt.core.dom.ReturnStatement;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.ReturnAnnotation;

public class Return_Statement extends ControlFlowStatement<ReturnStatement>{

	public Return_Statement(ReturnStatement node) {
		super(node);
	}
	
	public static Transformer<ReturnStatement, Return_Statement> returnTransform(){
		return new Transformer<ReturnStatement, Return_Statement>(){
			public Return_Statement transform(ReturnStatement input) {
				return new Return_Statement(input);
			}
		};
	}
	

	@Override
	protected ReturnAnnotation annotation() {
		return new ReturnAnnotation();
	}
	
	
}
