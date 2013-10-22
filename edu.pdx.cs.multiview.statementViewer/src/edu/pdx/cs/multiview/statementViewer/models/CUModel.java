package edu.pdx.cs.multiview.statementViewer.models;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CUModel extends ASTModel<CompilationUnit>{

	public CUModel(CompilationUnit t) {
		super(t, new Point(0,0));
	}

	@Override
	public List<ASTModel> buildChildren() {
		
		List<ASTModel> types = new LinkedList<ASTModel>();
		
		Point currentChildLocation =  getFirstChildLocation();
		for(Object o : getASTNode().types()){
			TypeDeclaration td  = (TypeDeclaration)o;
			TypeModel t = new TypeModel(td,currentChildLocation);
			Dimension size = t.getDimensions();
			currentChildLocation.translate(0,size.height+MARGIN);
			types.add(t);
			fireEvent(P_CHILDREN, null, t);
		}
		
		return types;
	}
	
	@Override
	public Point getLocation(){
		return new Point(0,0);
	}
	
	private Point getFirstChildLocation() {
		return new Point(10,10);
	}
}
