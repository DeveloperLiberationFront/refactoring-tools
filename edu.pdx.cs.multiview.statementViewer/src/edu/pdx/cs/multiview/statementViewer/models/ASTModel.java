package edu.pdx.cs.multiview.statementViewer.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.jface.ComparisonTextSelection;
import edu.pdx.cs.multiview.jface.IComparableTextSelection;
import edu.pdx.cs.multiview.jface.IPropertyChangeSource;
import edu.pdx.cs.multiview.statementViewer.Activator;

/**
 * I am the generic model for all things put into this
 * particular GEF application.  All models should inherit 
 * from me.
 * 
 * @author emerson
 *
 * @param <T>	the ASTNode type that I wrap
 */
public abstract class ASTModel<T extends ASTNode> implements IPropertyChangeSource{

	//space between elements
	public static final int MARGIN = 2;
	private List<ASTModel> children;
	
	public ASTModel(T t, Point p){
		this.setASTNode(t);
		this.setLocation(p);
	}
	
	/**
	 * Property that says my children change
	 */
	public final static String 	P_CHILDREN = "_p_children",
								P_LOCATION = "_p_location";
	private PropertyChangeSupport changer = new PropertyChangeSupport(this);
	
	private T node;
	
	//the whitespace out in front of my node
	private int whitespace = 0;
	
	/**
	 * @return	the node that I wrap
	 */
	public T getASTNode(){
		return node;
	}
	
	/**
	 * @param aNode	the node that I wrap
	 */
	public void setASTNode(T aNode){
		if(aNode==null){
			Activator.logInfo("debug me: setting me to a null AST node");
		}
		this.node = aNode;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		changer.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		changer.removePropertyChangeListener(listener);
	} 
	
	/**
	 * @see PropertyChangeSupport#firePropertyChange(String, Object, Object)
	 * 
	 * @param listener
	 */
	protected void fireEvent(String id, Object oldVal, Object newVal){
		changer.firePropertyChange(id, oldVal, newVal);
	}
	
	private Point location;
	private Dimension dimension;
	
	private void setLocation(Point l){
		this.location = l.getCopy();
	}
	
	public Point getLocation(){
		return this.location.getCopy();
	}
	
	/**
	 * @return	the dimensions of this item
	 */
	public Dimension getDimensions(){
		if(dimension==null)
			dimension = calculateSize();
		return dimension.getCopy();
	}

	/**
	 * @return	the calculated dimensions of this item
	 */
	protected Dimension calculateSize() {
		
		Dimension d = getDefaultDimension();
		
		for(ASTModel<?> child : getChildren())
			d.union(child.getDimensions().expand(child.getLocation()));
		
		if(!getChildren().isEmpty())
			d.expand(MARGIN+1, MARGIN+1);
			
		return d;
	}
	
	/**
	 * @return	a constant representing the default dimension
	 */
	protected Dimension getDefaultDimension(){
		return new Dimension(200,10);
	}

	
	/**
	 * @return	new children of this model
	 */
	protected abstract List<ASTModel> buildChildren();

	/**
	 * 
	 * @return	the children of this model
	 */
	public List<ASTModel> getChildren(){
		if(children==null)
			children = buildChildren();
		
		return children;
	}
	
	/**
	 * @return	the text range this statement covers, including any
	 * 			whitespace out front
	 */
	public IComparableTextSelection getRangeWithWhiteSpace() {
		
		try{
		return new ComparisonTextSelection(getASTNode().getStartPosition()-getWhitespace(),
											getASTNode().getLength()+getWhitespace());
		}catch(Exception e){
			return new ComparisonTextSelection(0,0);
		}
	}
	
	public IComparableTextSelection getRange(){
		try{
			return new ComparisonTextSelection(getASTNode().getStartPosition(),
												getASTNode().getLength());
		}catch(Exception e){
			return new ComparisonTextSelection(0,0);
		}
	}

	public String toString(){
		return getASTNode().toString();
	}
	
	
	
	private int getWhitespace() {
		return whitespace;
	}
	
	/**
	 * Calculates and stores the whitespace before my node
	 * and my children node
	 * 
	 * @param source
	 */
	public void calculateWhiteSpace(String source){
		whitespace = JDTUtils.whiteSpaceBefore(getASTNode(),source);
		for(ASTModel child : getChildren())
			child.calculateWhiteSpace(source);
	}
}
