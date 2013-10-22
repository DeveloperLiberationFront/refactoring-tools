package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.Position;

import edu.pdx.cs.multiview.extractmethodannotations.ast.ControlFlowStatement;

public class BreakAnnotation extends ControlFlowAnnotation{
	
	@Override
	protected Position getTargetRange(int offset, int length) {
		return new Position(offset+length-1,1);
	}

	@Override
	protected Position getSourceRange(int offset, int length) {
		return new Position(offset,5);//5 = b-r-e-a-k
	}
	
	public void addTo(ControlFlowAnnotationCollection cfas, Position p) {
		cfas.addBreak(PAnnotation.create(this,p));
	}

	@Override
	protected void addTo(ControlFlowAnnotationCollection cfas, ControlFlowStatement brake, Statement parent) {
		Position p = new Position(brake.getStartPosition(),parent.getStartPosition()+parent.getLength()-brake.getStartPosition());
		cfas.addBreak(PAnnotation.create(this, p));
	}
}
