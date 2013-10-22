package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.refactoring.sef.SelfEncapsulateFieldRefactoring;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.RegionList;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

public class EncapsulateFieldAction extends RefactoringAction<IField>{

	@Override
	public String getName() {
		return "Encapsulate Field";
	}
	
	@Override
	protected Regions calculateRegions(WrappedEditor e) {
		CompilationUnit cu = e.getCompilationUnit();
		
		RegionList regions = RegionList.newSortedOnLength();
		
		for(Object o : cu.types()){
			if(o instanceof TypeDeclaration){
				TypeDeclaration decl = (TypeDeclaration)o;
				for(FieldDeclaration fDecl : decl.getFields()){
					for(Object oFrag : fDecl.fragments()){
						regions.add(new ASTRegion(e,((VariableDeclarationFragment)oFrag).getName()));
					}
				}
			}
		}
		return regions;
	}

	@Override
	protected Collection<IField> getSelectedItems(Regions regions) {
		
		ArrayList<IField> fields = new ArrayList<IField>();
		
		for(ASTRegion astRegion : regions){
			try {
				VariableDeclarationFragment frag = (VariableDeclarationFragment)astRegion.node.getParent();
				IField member = (IField)frag.resolveBinding().getJavaElement();
				fields.add(member);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
		return fields;
	}
	
	@Override
	protected RefactoringBundle getRefactoring(IField f) throws Exception {
		return new RefactoringBundle(new SelfEncapsulateFieldRefactoring(f));
	}
}
