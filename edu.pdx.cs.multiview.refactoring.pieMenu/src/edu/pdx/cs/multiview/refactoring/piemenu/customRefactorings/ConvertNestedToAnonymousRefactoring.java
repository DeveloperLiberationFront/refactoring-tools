package edu.pdx.cs.multiview.refactoring.piemenu.customRefactorings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

/**
 * http://rickyclarkson.blogspot.com/2007/06/missing-refactor-convert-to-anonymous.html
 * 
 *  This refactoring not yet available in Eclipse, but apparently is in IDEA 7.0
 *  
 * @author emerson
 *
 */
public class ConvertNestedToAnonymousRefactoring extends Refactoring {

	private IType type;
	private TypeDeclaration typeNode;
	
	public ConvertNestedToAnonymousRefactoring(TypeDeclaration typeNode, IType type) {
		this.type = type;
		this.typeNode = typeNode;
	}
	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		//only one superclass or implemented interface that is used externally
		//no references to non-overriding/implementing methods
		//all instance variables used in implementing methods must be passed in to
		//		every constructor
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		//TODO: should really do precondition checking...
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		ASTRewrite rewrite = ASTRewrite.create(typeNode.getAST());
		
		final TypeDeclaration body = (TypeDeclaration)ASTNode.copySubtree(typeNode.getAST(), typeNode);
		
		deleteConstructorsAndFieldsIn(body);
		
		ClassInstanceCreation nuType = createAnonymousType(body);
		
		
		TextEditGroup group = new TextEditGroup("xxx");
		
		hangAnonymousClasslFromConstructor(rewrite, body, nuType, group);
		
		
		//delete the type
		rewrite.remove(typeNode, group);		
		
		return createChange(rewrite);
	}

	private void hangAnonymousClasslFromConstructor(ASTRewrite rewrite,
			final TypeDeclaration body, ClassInstanceCreation nuType,
			TextEditGroup group) {
		//hang empty constructor c to the calling node with name Ts
		final List<ClassInstanceCreation> constructors = new LinkedList<ClassInstanceCreation>();
		typeNode.getRoot().accept(new ASTVisitor(){
			public boolean visit(ClassInstanceCreation constructorCall){
				
				if(constructorCall.getType().toString().equals(body.getName().toString())){
					constructors.add(constructorCall);
				}
				
				return false;
			}
		});
		
		//hang c from constructors
		
		for(ClassInstanceCreation c : constructors)
			rewrite.replace(c, ASTNode.copySubtree(typeNode.getAST(),nuType), group);
	}

	private TextFileChange createChange(ASTRewrite rewrite)
			throws JavaModelException {
		TextEdit astEdit = rewrite.rewriteAST();
		
		
		TextFileChange change = new TextFileChange(type.getCompilationUnit().getElementName(),
												(IFile)type.getCompilationUnit().getResource());
		change.setTextType("java");
		change.setEdit(astEdit);
		return change;
	}

	private ClassInstanceCreation createAnonymousType(final TypeDeclaration body) {
		//let superType be the implementing interface or superclass of type
		
		final Type superType = getSupertype(body);
		
		AnonymousClassDeclaration anonClass = typeNode.getAST().newAnonymousClassDeclaration();
		
		List bodyDecl = ASTNode.copySubtrees(typeNode.getAST(), body.bodyDeclarations());
		anonClass.bodyDeclarations().addAll(bodyDecl);
		
		ClassInstanceCreation nuType = typeNode.getAST().newClassInstanceCreation();
		nuType.setType(superType);
		
		nuType.setAnonymousClassDeclaration(anonClass);
		return nuType;
	}

	private Type getSupertype(final TypeDeclaration newType) {
		Type superType = newType.getSuperclassType();
		
		if(superType==null){
			List superInterfaceTypes = newType.superInterfaceTypes();
			if(superInterfaceTypes.isEmpty()){
				SimpleName objectName = newType.getAST().newSimpleName("Object");
				superType = newType.getAST().newSimpleType(objectName);
			}else
				superType = (Type) superInterfaceTypes.get(0);
		}
		
		superType = (Type) ASTNode.copySubtree(newType.getAST(), superType);
		return superType;
	}

	private void deleteConstructorsAndFieldsIn(final TypeDeclaration newType) {
		final List<ASTNode> toDelete = 
			new ArrayList<ASTNode>(3);
		newType.accept(new ASTVisitor(){
			public boolean visit(MethodDeclaration md){				
				if(md.isConstructor()){//TODO: and it must be top level
					toDelete.add(md);
				}
				return false;
			}
			
			//TODO: should really just delete the ones that are set
			//			in constructors
			public boolean visit(FieldDeclaration vd) {
				
				if(vd.getParent()==newType)
					toDelete.add(vd);
				return false;
			}
		});
		
		
		
		for(ASTNode node : toDelete){
			node.delete();
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
