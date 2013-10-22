package edu.pdx.cs.multiview.extractmethodannotations.annotations;

import edu.pdx.cs.multiview.extractmethodannotations.ast.ControlFlowStatement;

public interface IControlFlowAnnotation {
	public abstract void addTo(ControlFlowAnnotationCollection cfas, ControlFlowStatement statement);
}
