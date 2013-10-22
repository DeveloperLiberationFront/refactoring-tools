package edu.pdx.cs.multiview.refactoringCues.refactorings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.refactoringCues.refactorings.ExtractMethodFromExpressionAction.ExtractMethodGUI;
import edu.pdx.cs.multiview.refactoringCues.views.ASTRegion;
import edu.pdx.cs.multiview.refactoringCues.views.RegionList;
import edu.pdx.cs.multiview.refactoringCues.views.Regions;
import edu.pdx.cs.multiview.util.Pair;

@SuppressWarnings("restriction")
public class ExtractMethodFromStatementsAction extends ASTLikeAction<ExtractRegion> {

	//TODO: need way to split contiguous statements into multiple extractions
	
	private ExtractMethodGUI extractMethodGUI;

	
	@Override
	public String getName() {
		return "Extract Method (Statment(s))";
	}
	
	protected boolean isAcceptable(ASTNode node) {
		return (node instanceof Statement) && 
				node.getParent().getNodeType()!=ASTNode.METHOD_DECLARATION;
	}

	@Override
	protected RefactoringBundle getRefactoring(ExtractRegion r) throws Exception {
		
		ICompilationUnit cu = getCU(r.first);
		r = reparse(r,cu);
		
		int startPosition = r.first.getStartPosition();
		int length = (r.second.getStartPosition()+r.second.getLength())-r.first.getStartPosition();
		
		ExtractMethodRefactoring refactoring = new ExtractMethodRefactoring(cu,
				startPosition,length);
		
		RefactoringBundle rb = new RefactoringBundle(refactoring);
		refactoring.setMethodName(rb.generateIdName(cu.getSource()));
		refactoring.setGenerateJavadoc(extractMethodGUI.generateComment);
		refactoring.setThrowRuntimeExceptions(extractMethodGUI.declareRuntimeExceptions);
		
		
		return rb;
	}

	private ExtractRegion reparse(ExtractRegion r, ICompilationUnit cu) {
		
		Statement newFirst = reparseForNode(cu, r.first);
		Statement newSecond = reparseForNode(cu, r.second);
		return new ExtractRegion(newFirst,newSecond);
	}

	protected Collection<ExtractRegion> getSelectedItems(Regions regions) {
		return toRegions(contiguousStatements(pairStatementsWithParents(regions)));
	}

	private Collection<ExtractRegion> toRegions(List<RegionList> contiguousStatements) {
		
		sortByOffset(contiguousStatements);
		Collection<ExtractRegion> collect = CollectionUtils.collect(contiguousStatements, 
				new Transformer<RegionList, ExtractRegion>(){
					public ExtractRegion transform(RegionList r) {						
						return new ExtractRegion(
								(Statement)r.first().node,
								(Statement)r.last().node);
					}});
		return collect;
	}

	private List<RegionList> contiguousStatements(
								Map<Block, RegionList> blocks) {
		
		
		
		List<RegionList> contiguousStatements = new ArrayList<RegionList>();
		
		for(Map.Entry<Block, RegionList> entry : blocks.entrySet()){
			Block block = entry.getKey();
			Regions statements = entry.getValue();
			
			int lastIndex = Integer.MIN_VALUE;
			RegionList lastContiguousSet = RegionList.newSortedOnLength();
			
			
			for(ASTRegion r : statements){
				int indexOfR = block.statements().indexOf(r.node);
				if(indexOfR != lastIndex + 1){
					if(!lastContiguousSet.isEmpty()){
						contiguousStatements.add(lastContiguousSet);
						lastContiguousSet = RegionList.newSortedOnLength();
					}
				}
				lastContiguousSet.add(r);
				lastIndex = indexOfR;
			}
			
			if(!lastContiguousSet.isEmpty())
				contiguousStatements.add(lastContiguousSet);
		}
		return contiguousStatements;
	}

	/**
	 * @param regions
	 * 
	 * @return	the value is a Block, and the key is a collection of that block's 
	 * 			children, each an element of the argument
	 */
	private Map<Block, RegionList> pairStatementsWithParents(Regions regions) {
		Map<Block, RegionList> blocks = new HashMap<Block, RegionList>();
		
		for(ASTRegion r : regions){
			ASTNode parent = r.node.getParent();
			if(r.node.getParent() instanceof Block){
				if(!blocks.containsKey(parent)){
					blocks.put((Block)parent, RegionList.newSortedOnLength());
				}
				blocks.get(parent).add(r);
				
			}else{
				System.err.println("Parent not a block?->"+r.node);
			}
		}
		
		sortByOffset(blocks.values());
		
		return blocks;
	}

	private void sortByOffset(Collection<RegionList> collection) {
		for(RegionList list : collection){
			Collections.sort(list,new Comparator<ASTRegion>(){
				public int compare(ASTRegion r1, ASTRegion r2) {
					return r1.getOffset()-r2.getOffset();
				}});
		}
	}
	
	@Override
	public Composite initConfigurationGUI(Composite parent) {
		return extractMethodGUI = new ExtractMethodGUI(parent);
	}
}

class ExtractRegion extends Pair<Statement, Statement>{
	
	public ExtractRegion(Statement first, Statement second) {
		super(first, second);
	}
}	

