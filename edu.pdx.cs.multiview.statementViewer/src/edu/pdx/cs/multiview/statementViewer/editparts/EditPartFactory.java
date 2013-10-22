package edu.pdx.cs.multiview.statementViewer.editparts;

import org.eclipse.gef.EditPart;

import edu.pdx.cs.multiview.statementViewer.models.ASTModel;
import edu.pdx.cs.multiview.statementViewer.models.CUModel;
import edu.pdx.cs.multiview.statementViewer.models.MethodModel;
import edu.pdx.cs.multiview.statementViewer.models.TypeModel;

/**
 * I associate models with EditParts
 * 
 * @author emerson
 */
public class EditPartFactory implements org.eclipse.gef.EditPartFactory{
	
	public EditPart createEditPart(EditPart context, Object model) {
		
		EditPart part = null;

		if (model instanceof CUModel)
			part = new RootEditPart();
		else if (model instanceof MethodModel)
			part = new MethodEditPart();
		else if (model instanceof TypeModel)
			part = new TypeEditPart();
		else if (model instanceof ASTModel)
			part = new ASTEditPart(); 
		else
			part = new UnknownEditPart();

		part.setModel(model);
		return part;
	}
}
