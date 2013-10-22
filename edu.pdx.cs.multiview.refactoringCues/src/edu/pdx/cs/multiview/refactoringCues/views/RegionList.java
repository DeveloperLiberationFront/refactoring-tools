package edu.pdx.cs.multiview.refactoringCues.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.ITextSelection;

/**
 *
 * Contains a sorted list of Regions
 * 
 * @author emerson
 *
 */
@SuppressWarnings("serial")
public abstract class RegionList extends ArrayList<ASTRegion> implements Regions{

		//TODO: this is a sorted list... can't I just reuse something... PLEASE?  
		//TreeBag? doesn't seem to work
	
	public static RegionList newSortedOnLength() {
		return new RegionList(){
			protected boolean shouldPlaceRegionAt(ASTRegion sr, int i) {
				return get(i).getLength() > sr.getLength();
			}
		};
	}
	
	public static RegionList newSortedOnOffset(){
		return new RegionList(){
			protected boolean shouldPlaceRegionAt(ASTRegion sr, int i) {
				return get(i).getOffset() > sr.getOffset();
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see edu.pdx.cs.multiview.refactoringCues.views.Regions#withOnlyActive()
	 */
	public RegionList withOnlyActive() {
		
		RegionList actives = newSortedOnLength();
		
		for(ASTRegion sr : this)
			if(sr.isSelected())
				actives.add(sr);
		
		return actives;
	}

	/* (non-Javadoc)
	 * @see edu.pdx.cs.multiview.refactoringCues.views.Regions#setSelectedIn(org.eclipse.jface.text.ITextSelection)
	 */
	public void setSelectedIn(ITextSelection selection) {

		List<ASTRegion> alreadySelected = new LinkedList<ASTRegion>();
		for(ASTRegion region : this){
			if(region.overlaps(selection) && 
					!region.overlapsAny(alreadySelected)){
				region.toggleActivation();
				alreadySelected.add(region);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see edu.pdx.cs.multiview.refactoringCues.views.Regions#first()
	 */
	public ASTRegion first() {
		return get(0);
	}
	
	public boolean add(ASTRegion sr){
		for(int i = 0; i<size(); i++)
			if(shouldPlaceRegionAt(sr, i)){
				add(i,sr);
				return true;
			}
		
		super.add(sr);
		return true;
	}
	
	protected abstract boolean shouldPlaceRegionAt(ASTRegion region, int i);
	
	/* (non-Javadoc)
	 * @see edu.pdx.cs.multiview.refactoringCues.views.Regions#last()
	 */
	public ASTRegion last() {
		return get(size()-1);
	}
	
}
