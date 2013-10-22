package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.RegionList;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

public abstract class MemberAction extends RefactoringAction<IMember>{

	private boolean includeFields;
	private boolean includeMethods;

	public MemberAction(boolean includeMethods, boolean includeFields){
		this.includeMethods = includeMethods;
		this.includeFields = includeFields;
	}
	
	@Override
	protected Regions calculateRegions(WrappedEditor e) {
		CompilationUnit cu = e.getCompilationUnit();
		
		RegionList regions = RegionList.newSortedOnLength();
		
		for(Object o : cu.types()){
			if(o instanceof TypeDeclaration){
				TypeDeclaration decl = (TypeDeclaration)o;
				
				if(includeMethods)
					for(MethodDeclaration mDecl : decl.getMethods()){
						regions.add(new ASTRegion(e,mDecl.getName()));
					}
				
				if(includeFields)
					for(FieldDeclaration fDecl : decl.getFields()){
						for(Object oFrag : fDecl.fragments()){
							VariableDeclarationFragment frag = (VariableDeclarationFragment)oFrag;
							regions.add(new ASTRegion(e,frag.getName()));	
						}
					}
			}
			
		}
		return regions;
	}

	@Override
	protected Collection<IMember> getSelectedItems(Regions regions) {
		
		ArrayList<IMember> members = new ArrayList<IMember>();
		
		for(ASTRegion astRegion : regions){
			try {
				ASTNode parent = astRegion.node.getParent();
				
				
				if(parent instanceof MethodDeclaration){
					MethodDeclaration md = (MethodDeclaration)parent;
					IMember member = (IMember)md.resolveBinding().getJavaElement();
					members.add(member);	
				}else if(parent instanceof VariableDeclarationFragment){
					//TODO: use that new IMemberBinding here to remove dups
					VariableDeclarationFragment vdf = (VariableDeclarationFragment)parent;
					IMember member = (IMember)vdf.resolveBinding().getJavaElement();
					members.add(member);	
				}
				
			} catch (RuntimeException e) {
				//this could happen for a lot of reasons, but
				//the above code should work for easy cases
				e.printStackTrace();
			}
		}
		
		return members;
	}

}
