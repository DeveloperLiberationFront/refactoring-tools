package edu.pdx.cs.multiview.statementViewer.editparts;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;

import edu.pdx.cs.multiview.draw2d.FreeformLabel;


public class RootEditPart extends UnSelectableEditPart implements FigureListener{

	@Override
	protected FreeformLabel createFigure() {
		
		FreeformLabel fig = new FreeformLabel(getText());
		fig.addFigureListener(this);
		
		fig.setTextAlignment(PositionConstants.TOP);
		fig.setLabelAlignment(PositionConstants.LEFT);
		
		return fig;
	}
	
	public void figureMoved(IFigure source) {
		scaleChildren(source);
	}
	
	@Override
	protected void scaleFromRoot(){
		scaleChildren(getFigure());
	}

	private void scaleChildren(IFigure figure) {
		for(Object o : figure.getChildren()){
			IFigure child = (IFigure)o;
			int newWidth = //the old width
							figure.getBounds().width -
							//minus the left indent
							(child.getBounds().x-figure.getBounds().x) - 
							//minus the right indent
							borderWidth;//this seems quite important
			child.setSize(newWidth, child.getBounds().height);
			scaleChildren(child);
		}
	}
}
