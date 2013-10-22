package edu.pdx.cs.multiview.smelldetector.detectors.featureEnvy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.jdt.util.MemberReference;
import edu.pdx.cs.multiview.jface.annotation.AnnTransaction;
import edu.pdx.cs.multiview.jface.annotation.Highlight;
import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;
import edu.pdx.cs.multiview.smelldetector.ColorManager;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationOverlay;
import edu.pdx.cs.multiview.util.CollectionUtils2;

class FeatureEnvyOverlay extends SmellExplanationOverlay<EnvyInstance>{

	private Color thisColor;
	
	private MemberStore store;
	
	private Map<MemberReference, Highlight> annotations = 
		new IdentityHashMap<MemberReference, Highlight>();

	public FeatureEnvyOverlay(EnvyInstance i, ISourceViewer sv){
		super(i,sv);
		store = new MemberStore(instance.uniqueClassesReferenced());
		init();
	}

	private void init() {		
		
		AnnTransaction annotations = new AnnTransaction();
		
		//allocate and assign colors to classes
		for(MemberReference ref : instance.items()){
			Color color = store.addIfNotPresent(ref.referencedClass());
			annotations.add(getAnnotation(color, ref), ref.getPosition());
		}

		store.assignReferences(instance.items());
		
		allocateColors(memberColors());
		
		thisColor = new Color(null,50,50,50);
		allocateColor(thisColor);
					
		for(MemberReference mr : instance.thisReferences()){				
			annotations.add(getAnnotation(thisColor, mr), mr.getPosition());	
		}
		
		addAnnotations(annotations);
	}

	private Highlight getAnnotation(Color aColor, MemberReference mr) {
		Highlight ann = new Highlight(aColor);
		this.annotations.put(mr, ann);
		return ann;
	}
		
	public Map<ITypeBinding, Color> typeColors(){
		return store.typeColors();
	}
	
	public Collection<Color> memberColors(){
		return store.colors();
	}

	public Color thisColor() {
		return thisColor;
	}
	
	public Set<MemberReference> uniqueThisReferences(){
		return instance.uniqueThisReferences();
	}

	public SortedSet<MemberReferenceDuplicate> referencesForColor(Color c) {
		return store.referencesForColor(c);
	}

	public void highlight(MemberReference ref, boolean applyToAllInClass, boolean enable) {

		List<ISelfDrawingAnnotation> anns = new ArrayList<ISelfDrawingAnnotation>();
		
		for(MemberReference someRef : instance.references()){
			
			Highlight highlight = annotations.get(someRef);
			
			if(applyToAllInClass &&  someRef.referencedClass().equals(ref.referencedClass())){
				anns.add(highlight);
				highlight.setEmphasized(enable);	
			}
			else if(!applyToAllInClass &&  someRef.equals(ref)){
				anns.add(highlight);
				highlight.setEmphasized(enable);
			}
		}		
		
		refreshAnnotations(anns);
	}

	public boolean hasReadOnlyReferencedClasses() {
		for(MemberReference ref : store.members())
			if(ref.refersToReadOnlyClass())
				return true;
		
		return false;
	}
}

class MemberStore{
	
	private Iterator<Color> colorRange;
	
	private Map<ITypeBinding, Color> typeColors = 
		new HashMap<ITypeBinding, Color>();
	private Map<MemberReferenceDuplicate, Color> memberColors = 
		new HashMap<MemberReferenceDuplicate, Color>();
	
	public MemberStore(int typeCount){
		colorRange = ColorManager.colorRange(typeCount);
	}
	
	public Collection<? extends MemberReference> members() {
		return memberColors.keySet();
	}

	public Map<ITypeBinding, Color> typeColors() {
		return typeColors;
	}

	public void assignReferences(Collection<MemberReference> items) {
		for(MemberReference mr : items){
			memberColors.put(new MemberReferenceDuplicate(mr), 
					typeColors.get(mr.referencedClass()));
		}
		
		for(MemberReferenceDuplicate orig : memberColors.keySet()){
			for(MemberReference other : items){
				if(orig.equals(other))
					orig.dups++;
			}
		}
	}
	
	/**
	 * @return colors, ranked by how many references are made using that color
	 */
	public List<Color> colors(){
		
		//an ordered map with a reverse compare (greatest to least)
		Map<Integer, List<Color>> rankedColors = new TreeMap<Integer, List<Color>>(
				new Comparator<Integer>(){
					public int compare(Integer i1, Integer i2) {						
						int originalCompare = i1.compareTo(i2);
						return originalCompare < 0 ? Math.abs(originalCompare) : -originalCompare;
					}});
		
        for(Color color : typeColors.values()){
            int refCount = referencesForColor(color).size();
            if(!rankedColors.containsKey(refCount)){
            	rankedColors.put(refCount, new LinkedList<Color>());
            }
            rankedColors.get(refCount).add(color);
        }
        
        return CollectionUtils2.flatten(rankedColors.values());
		
	}

	public Color addIfNotPresent(ITypeBinding binding){
		if(!typeColors.containsKey(binding)){
			typeColors.put(binding, colorRange.next());
		}
		return typeColors.get(binding);
	}
	
	public SortedSet<MemberReferenceDuplicate> referencesForColor(Color c) {
		SortedSet<MemberReferenceDuplicate> refs = 
			new TreeSet<MemberReferenceDuplicate>();
		
		for(Map.Entry<MemberReferenceDuplicate, Color> e : memberColors.entrySet()){
			if(e.getValue().equals(c))
				refs.add(e.getKey());
		}
		
		return refs;
	}
}

class MemberReferenceDuplicate extends MemberReference
implements Comparable<MemberReferenceDuplicate>{

	private MemberReference ref;
	public int dups = 0;

	public MemberReferenceDuplicate(MemberReference mr) {
		super(mr);
		this.ref = mr;
	}

	@Override
	public IBinding getBinding() {
		return ref.getBinding();
	}

	@Override
	public ITypeBinding referencedClass() {
		return ref.referencedClass();
	}

	public int compareTo(MemberReferenceDuplicate o) {
		int diff = o.dups - this.dups;
		//can't return zero, cause if we do
		//one will be eliminated when putting into
		//a TreeSet
		return diff==0 ? getPosition().offset - o.getPosition().offset : diff;								
	}
	
	@Override
	public String toString(){
		return ref.toString();
	}
}