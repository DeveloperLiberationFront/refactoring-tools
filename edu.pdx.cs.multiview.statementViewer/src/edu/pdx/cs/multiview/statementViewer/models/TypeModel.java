package edu.pdx.cs.multiview.statementViewer.models;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeModel extends ASTModel<TypeDeclaration>{

	public TypeModel(TypeDeclaration t, Point p) {
		super(t, p);
	}

	@Override
	public List<ASTModel> buildChildren() {

		//TODO: make getASTChildren and refactor all these flat getChlidrens...
		
		List<ASTModel> children = new LinkedList<ASTModel>();
		
		Point currentChildLocation =  getFirstChildLocation();
		for(MethodDeclaration mDecl : getASTNode().getMethods()){
			MethodModel method = new MethodModel(mDecl,currentChildLocation);
			Dimension size = method.getDimensions();
			currentChildLocation.translate(0,size.height+MARGIN);
			children.add(method);
			fireEvent(P_CHILDREN, null, method);
		}
		
		for(TypeDeclaration innerClass : getASTNode().getTypes()){
			TypeModel classModel = new TypeModel(innerClass,currentChildLocation);
			Dimension size = classModel.getDimensions();
			currentChildLocation.translate(0, size.height+MARGIN);
			children.add(classModel);
			fireEvent(P_CHILDREN, null, classModel);
		}
		
		return children;
	}

	private Point getFirstChildLocation() {
		return new Point(10,25);
	}

}
