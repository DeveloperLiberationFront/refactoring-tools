package edu.pdx.cs.multiview.extractmethodannotations.visitors;


import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.ITextSelection;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.VariableAnnotationCollection;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Declaration;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference;
import edu.pdx.cs.multiview.extractmethodannotations.ast.Variable_Reference.Type;
import edu.pdx.cs.multiview.extractmethodannotations.util.MethodMap;
import edu.pdx.cs.multiview.extractmethodannotations.util.TempVariableWithDependencies;

/**
 * 
 * Finds temporary variables
 * 
 * @author emerson
 */
public class LocalVariableVisitor extends ASTVisitor{
	
	
	private MethodMap methods;
	
	public LocalVariableVisitor(){
		methods = new MethodMap();
	}
	
	@Override
	public boolean visit(MethodDeclaration mDecl){
		methods.start(mDecl);
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration mDecl){
		methods.end();
	}
	
	@Override
	public boolean visit(SingleVariableDeclaration decl){
		return visit((VariableDeclaration)decl);//lousy upcast...
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment decl){
		return visit((VariableDeclaration)decl);
	}

	private boolean visit(VariableDeclaration decl) {
		methods.add(new TempVariableWithDependencies(new Variable_Declaration(decl)));
		
		//traverse right...
		if(decl.getInitializer()!=null)
			decl.getInitializer().accept(this);
		//but not left.
		return false;
	}
	
	@Override
	public boolean visit(SimpleName name){
		methods.refTo(new Variable_Reference(name,Type.read));
		return true;
	}	
	
	@Override
	public boolean visit(Assignment assign){
		
		if(assign.getLeftHandSide() instanceof SimpleName){
			methods.refTo(new Variable_Reference((SimpleName)assign.getLeftHandSide(),Type.write));
		
			//traverse right...
			assign.getRightHandSide().accept(this);
			//but not left.
			return false;
		}
		
		//may be qualified assignment, like object.field = ...
		return true;
	}
	
	@Override
	public boolean visit(PrefixExpression exp){
		
		if(exp.getOperand() instanceof SimpleName){
			Variable_Reference op;
			if(isWriter(exp.getOperator())){
				op = new Variable_Reference((SimpleName)exp.getOperand(),Type.readAndWrite);
				methods.refTo(op);
			}else{
				op = new Variable_Reference((SimpleName)exp.getOperand(),Type.read);
				methods.refTo(op);
			}
				
			return false;
		}
		
		return true;
	}
	
	private boolean isWriter(PrefixExpression.Operator operator) {
		return 	operator.equals(PrefixExpression.Operator.INCREMENT) || 
				operator.equals(PrefixExpression.Operator.DECREMENT);
	}

	@Override
	public boolean visit(PostfixExpression exp){
		
		if(exp.getOperand() instanceof SimpleName){
			methods.refTo(new Variable_Reference((SimpleName)exp.getOperand(),Type.readAndWrite));
			return false;
		}
		
		return true;
	}


	@Override
	public boolean visit(QualifiedName name){
		
		/*
		 * If we already visited a temp variable named out, 
		 * we do want to visit
		 * 		out.foo()
		 * but we don't want to visit 
		 *		System.out;
		 */
		name.getQualifier().accept(this);
		return false;
	}
	
	/**
	 * Returns all my annotations
	 * 
	 * @param selection
	 */
	public VariableAnnotationCollection getAnnotations(ITextSelection selection) {
		return methods.getAnnotations(selection);
	}
}
