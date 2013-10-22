package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.Position;

import edu.pdx.cs.multiview.extractmethodannotations.ast.Continue_Statement;
import edu.pdx.cs.multiview.extractmethodannotations.ast.ControlFlowStatement;

public class ContinueAnnotation extends ControlFlowAnnotation {
	
	private int loopNameLength, continueLength;
	
	public ContinueAnnotation(){ loopNameLength = 3;}
	
	@Override
	public Position getTargetRange(int offset, int length){
		return new Position(offset,loopNameLength);
	}


	@Override
	protected Position getSourceRange(int offset, int length) {
		return new Position(offset+length-continueLength,continueLength);
	}
	
	@Override
	protected void addTo(ControlFlowAnnotationCollection cfas, ControlFlowStatement statement, Statement parent) {
		loopNameLength = ((Continue_Statement)statement).loopNameLength();//TODO: try to remove this cast
		continueLength = ((Continue_Statement)statement).getLength();
		Position p = new Position(parent.getStartPosition(),statement.getEndPosition()-parent.getStartPosition());
		cfas.addContinue(PAnnotation.create(this, p));
	}

}
