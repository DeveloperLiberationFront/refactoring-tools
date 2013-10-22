/**
 * 
 */
package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections15.iterators.IteratorChain;
import org.apache.commons.collections15.iterators.SingletonIterator;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.ITextSelection;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.RegionList;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.refactoringCues.views.WrappedEditor;

abstract class ASTAction<T extends ASTNode> extends ASTLikeAction<T>{
	
	@Override
	final protected RefactoringBundle getRefactoring(T node) throws Exception{
		ICompilationUnit icu = getCU(node);
		T newNode = reparseForNode(icu, node);
		return getRefactoring(newNode,icu);
	}

	protected abstract RefactoringBundle getRefactoring(T node, 
								ICompilationUnit cu) throws Exception;
	
	@Override
	protected Collection<T> getSelectedItems(Regions regions) {
		
		List<T> nodes = new LinkedList<T>();
		for(ASTRegion region : regions)
			nodes.add((T)region.node);
		return nodes;
	}
}

abstract class ASTLikeAction<T> extends RefactoringAction<T>{
	
	@Override
	protected Regions calculateRegions(final WrappedEditor e) {
		CompilationUnit cu = e.getCompilationUnit();
		
		Converter action = new Converter(e);
		cu.accept(action);
		
		return action.roots;
	}
	
	protected abstract boolean isAcceptable(ASTNode node);
	
	private final class Converter extends ASTVisitor {
		private NodeList roots;
		private Stack<Node> parents = 
			new Stack<Node>();
		private WrappedEditor editor;
		
		public Converter(WrappedEditor e) {
			this.editor = e;
			roots = new NodeList();
		}

		public Regions getRoot(){
			return roots;
		}

		public void preVisit(ASTNode node){
						
			if(isAcceptable(node)){
				Node n = new Node(editor,node);
				if(!parents.isEmpty())
					parents.peek().add(n);
				else
					roots.add(n);
							
				parents.push(n);
			}
		}

		public void postVisit(ASTNode node){
			if(!parents.isEmpty() && parents.peek().node==node)
				parents.pop();
		}
	}
	
	class NodeList implements Regions{
		
		private List<Node> children = new LinkedList<Node>();
		
		public void add(Node n){
			children.add(0,n);
		}
		
		public void setSelectedIn(ITextSelection selection) {
			for(Node n : children)
				if(n.overlaps(selection))
					n.setSelectedIn(selection);
		}
		
		public boolean anyIn(ITextSelection selection) {
			for(Node n : children)
				if(n.overlaps(selection))
					return true;
			return false;
		}
		
		public ASTRegion first() {
			return children.get(0).first();
		}

		public boolean isEmpty() {
			return children.isEmpty();
		}

		public ASTRegion last() {
			return children.get(children.size()-1).last();
		}

		public Regions withOnlyActive() {
			//TODO: duplicated with RegionList
			RegionList actives = RegionList.newSortedOnOffset();
			
			for(ASTRegion sr : this)
				if(sr.isSelected())
					actives.add(sr);
			
			return actives;
		}

		public Iterator<ASTRegion> iterator() {
			IteratorChain<ASTRegion> iter = 
				new IteratorChain<ASTRegion>();
			
			for(Node n : children)
				iter.addIterator(n.iterator());
			
			return iter;
		}

		
	}

	class Node extends ASTRegion implements Regions{
		
		private Node parent;
		private NodeList children = new NodeList();

		public Node(WrappedEditor e, ASTNode node) {
			super(e,node);
		}
		
		public void add(Node n) {
			n.setParent(this);
			children.add(n);
		}
		
		public void setParent(Node n){
			this.parent = n;
		}

		public String toString(){
			return node.toString();
		}

		public Iterator<ASTRegion> iterator() {
						
			IteratorChain<ASTRegion> iter = new IteratorChain<ASTRegion>();
			iter.addIterator(children.iterator());
			if(this.node!=null)
				iter.addIterator(new SingletonIterator<ASTRegion>(this));
			
			return iter;
		}

		public ASTRegion first() {
			return isSelected() ? this : children.first();
		}

		public ASTRegion last() {
			return isSelected() ? this : children.last();
		}

		public void setSelectedIn(ITextSelection selection) {
			
			if(getEditor().similarToLastSelection(selection)){
				return;
			}
			
			if(leafInSelection(selection)){
				Node selectedAncestor = getSelecedAncestor();
				if(selectedAncestor==null){
					//if no parent is selected...
					if(isSelected() && parent!=null)
						parent.toggleActivation();
					toggleActivation();
				}else{
					//if some parent node is selected...
					if(selectedAncestor.parent!=null){
						//unselect that parent
						selectedAncestor.toggleActivation();
						//and select it's parent
						selectedAncestor.parent.toggleActivation();
					}else{
						//unselect the outermost parent
						selectedAncestor.toggleActivation();
					}
				}
			}else{
				//activate me if I'm totally within the selection
				if(containedBy(selection))
					toggleActivation();
				//otherwise, try activating a child
				else
					children.setSelectedIn(selection);
			}
		}

		private boolean leafInSelection(ITextSelection selection) {
			return !children.anyIn(selection);
		}

		private Node getSelecedAncestor() {
			return parent==null || parent.isSelected()?
					parent : parent.getSelecedAncestor();			
		}

		public Regions withOnlyActive() {
			//TODO: duplicated with RegionList
			RegionList actives = RegionList.newSortedOnLength();
			
			for(ASTRegion sr : this)
				if(sr.isSelected())
					actives.add(sr);
			
			return actives;
		}

		public boolean isEmpty() {
			return this.node!=null && !isSelected();
		}
	}
}