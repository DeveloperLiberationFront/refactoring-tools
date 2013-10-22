package edu.pdx.cs.multiview.smelldetector.detectors.largeMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.jface.annotation.AnnTransaction;
import edu.pdx.cs.multiview.jface.annotation.Highlight;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationOverlay;


public class LargeMethodOverlay extends SmellExplanationOverlay<LargeMethodInstance>{

	private Map<IMethod, Color> methodsToColors = new HashMap<IMethod, Color>();
	
	public LargeMethodOverlay(LargeMethodInstance inst, ISourceViewer sv) {
		super(inst,sv);
		init(inst);
	}

	private void init(LargeMethodInstance inst) {

		int shortest = inst.shortestLength();
		int longest = inst.longestLength();
		int mLength;
		int red;
		
		AnnTransaction annotations = new AnnTransaction();
		
		for(Entry<IMethod, Integer> e : inst.sortedPairs()){
			mLength = e.getValue();
			red = (int)(((double)(mLength - shortest) / (double)(longest - shortest)) * 255);
			final Color c = new Color(null,red,255-red,0);
			IMethod m = e.getKey();	
			
			methodsToColors.put(m,c);
			
			try {
				ISourceRange range = m.getSourceRange();			
				Position p  = new Position(range.getOffset(),range.getLength());
				
				annotations.add(new Highlight(c), p);
				
			} catch (JavaModelException e1) {
				e1.printStackTrace();
			}
		}
		
		allocateColors(methodsToColors.values());
		addAnnotations(annotations);
	}

	public Color colorFor(IMethod m){
		return methodsToColors.get(m);
	}
}