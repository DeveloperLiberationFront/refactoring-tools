package edu.pdx.cs.multiview.statementViewer.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.jface.IComparableTextSelection;
import edu.pdx.cs.multiview.statementViewer.models.ASTModel;

public class ASTEditPart 	extends AbstractGraphicalEditPart  
									implements PropertyChangeListener{

	//determines if the inner nodes are colored, as well as the leaves
	public static final boolean colorInnerNodes = true;
	
	private final static Color borderColor = ColorConstants.lightBlue,
								borderColor2 = ColorConstants.black;
	protected final static int borderWidth = 1;
	
	public void setModel(Object model){
		super.setModel(model);
		getModel().addPropertyChangeListener(this);
	}
	
	public ASTModel getModel(){
		return (ASTModel)super.getModel();
	}

	protected Label createFigure() {
		
		Label l = new Label();
		l.setText(getText());
		
		l.setTextAlignment(PositionConstants.TOP);
		l.setLabelAlignment(PositionConstants.LEFT);
		
		LineBorder lBorder = new LineBorder();
		lBorder.setColor(borderColor);
		lBorder.setWidth(borderWidth);
		
		MarginBorder mBorder = new MarginBorder(new Insets(5,5,0,0));
		l.setBorder(new CompoundBorder(lBorder, mBorder));
		
		l.setBackgroundColor(ColorConstants.white);
		l.setOpaque(true);
		
		l.setLayoutManager(new XYLayout());
		
		l.addMouseMotionListener(new MouseMotionListener(){
			public void mouseEntered(MouseEvent me) {setMouseEnter(true);}
			public void mouseExited(MouseEvent me) {setMouseEnter(false);}
			public void mouseDragged(MouseEvent me) {}
			public void mouseHover(MouseEvent me) {}
			public void mouseMoved(MouseEvent me) {}
		});
		
		return l;
	}
	
	protected String getText(){
		return "";
	}



	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropagationId()==ASTModel.P_CHILDREN)
			this.refreshChildren();
	}

	@Override
	protected void createEditPolicies() {/*none*/}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		super.addChildVisual(childEditPart, index);
		placeChildVisual(childEditPart);
	}

	/**
	 * Sets the bounds of the child
	 * 
	 * @param childEditPart
	 */
	protected void placeChildVisual(EditPart childEditPart) {
		ASTEditPart childPart = (ASTEditPart)childEditPart;
		Point myLocation = this.getFigure().getBounds().getLocation();
		Rectangle bounds = new Rectangle(childPart.getLocation().translate(myLocation),
										childPart.getDimensions());
		childPart.getFigure().setBounds(bounds);
	}
	
	protected Dimension getDimensions(){
		return getModel().getDimensions();
	}
	
	protected Point getLocation(){
		return getModel().getLocation();
	}
	
	protected void scaleFromRoot(){
		if(getParent() instanceof ASTEditPart)
			((ASTEditPart)getParent()).scaleFromRoot();
	}
	
	protected List getModelChildren(){
		return getModel().getChildren();
	}
	
	public ASTEditPart setSelected(IComparableTextSelection selection) {
		
		ASTEditPart temp, coloredChild = null;

		//try to color the children
		for(Object o : getChildren())
			if(o instanceof ASTEditPart){
				temp = ((ASTEditPart)o).setSelected(selection);
				if(coloredChild==null)
					coloredChild = temp;
			}
		
		boolean imColored = tryToColorSelf(selection,coloredChild!=null);
		
		return imColored ?  this : coloredChild;
	}

	/**
	 * Colors myself, if I am within the argument
	 * 
	 * @param selection		a selection
	 * @param coloredChild 	whether one of my children has been colored
	 * 
	 * @return	if I am colored
	 */
	protected boolean tryToColorSelf(IComparableTextSelection selection, 
										boolean coloredChild) {
		
		switch(getModel().getRange().compareTo(selection)){
			case FULL_CONTAINMENT:
				colorFigure(ColorConstants.green);
				break;
			case SOME_OVERLAP:
			case FULL_CONTAINER:
				if(okToColorSelfOrange(coloredChild)){
					colorFigure(ColorConstants.orange);
					break;
				}
			default:
				if(IComparableTextSelection.COMPARISON.FULL_CONTAINER == 
					(getModel().getRangeWithWhiteSpace().compareTo(selection)) &&
					okToColorSelfOrange(coloredChild)){
					
					colorFigure(ColorConstants.orange);
					break;
				}
				colorFigure(ColorConstants.white);
				return false;
				
		}
		
		return true;
	}

	protected void colorFigure(Color color) {
		getFigure().setBackgroundColor(color);
	}
	
	protected void setMouseEnter(boolean enter){
		int width = enter ? borderWidth+1 : borderWidth; 
		Color c = enter ? borderColor2 : borderColor;
		CompoundBorder border = (CompoundBorder)getFigure().getBorder();
		LineBorder outerBorder = (LineBorder)border.getOuterBorder();
		outerBorder.setColor(c);
		outerBorder.setWidth(width);
		getFigure().repaint();
	}

	protected boolean okToColorSelfOrange(boolean hasColoredChild) {
		return (colorInnerNodes && !hasColoredChild) || getChildren().isEmpty();
	}
	
	public String toString(){
		return "EP>"+getModel().toString();
	}
	
	//TODO: this exists elsewhere - make GEFUtils
	class DottedLineBorder extends LineBorder{
		public void paint(IFigure aFigure, Graphics graphics, Insets insets){
			tempRect.setBounds(getPaintRectangle(aFigure, insets));
			if (getWidth() % 2 == 1) {
				tempRect.width--;
				tempRect.height--;
			}
			tempRect.shrink(getWidth() / 2, getWidth() / 2);
			graphics.setLineWidth(getWidth());
			if (getColor() != null)
				graphics.setForegroundColor(getColor());
			
			graphics.setLineStyle(Graphics.LINE_DASH);
			graphics.drawRectangle(tempRect);
		}
	}

	/**
	 * Reveal myself on the parameter.  If I reside within a method,
	 * then reveal that first
	 * 
	 * @param viewer
	 */
	public void reveal(GraphicalViewerImpl viewer) {
		
		//TODO: hmm... this should reveal the method, if possible, but that
		//			doesn't loook like it's happening
		ASTEditPart revealParent = getRevealParent();
		if(revealParent!=null)
			viewer.reveal(revealParent);
		
		viewer.reveal(this);
	}
	
	/**
	 * Returns my nearest parent who wants to be revealed
	 * (subclasses should override if they wish to be revealed)
	 * 
	 * @return
	 */
	protected ASTEditPart getRevealParent(){
		if(this.getParent() instanceof ASTEditPart)
			return ((ASTEditPart)this.getParent()).getRevealParent();
		else
			return null;
	}
}
