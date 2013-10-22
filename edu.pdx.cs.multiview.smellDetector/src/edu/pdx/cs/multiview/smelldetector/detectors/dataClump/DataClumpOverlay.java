package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.jface.annotation.AnnTransaction;
import edu.pdx.cs.multiview.jface.annotation.Highlight;
import edu.pdx.cs.multiview.smelldetector.ColorManager;
import edu.pdx.cs.multiview.smelldetector.ColorManager.ColorIterator;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationOverlay;


public class DataClumpOverlay extends SmellExplanationOverlay<ClumpSpider>{

	private Map<ClumpGroup, Color> clumpsToColors = 
							new HashMap<ClumpGroup, Color>();
	
	public DataClumpOverlay(ClumpSpider inst, ISourceViewer sv) {
		super(inst,sv);
		init(inst);
	}
	
	public Map<ClumpGroup, Color> clumps(){
		return clumpsToColors;
	}

	private void init(ClumpSpider inst) {
		
		AnnTransaction annotations = new AnnTransaction();
		
		ICompilationUnit icu = inst.compilationUnit();
		Set<ClumpGroup> currentClumps = inst.currentClumps();
		
		ColorIterator colors = ColorManager.colorRange(currentClumps.size());
		
		for(ClumpGroup group : currentClumps){
			
			Color c = colors.next();
			
			Set<IMethod> methods = group.methodsIn(icu);
			for(IMethod m : methods){				
				Set<SingleVariableDeclaration> params = group.parametersOf(m);				
				for(SingleVariableDeclaration param : params){
					Highlight ann = new Highlight(c);
					annotations.add(ann, new Position(param.getStartPosition(),param.getLength()));					
				}
			}
			
			clumpsToColors.put(group, c);
		}
		
		
		allocateColors(clumpsToColors.values());
		addAnnotations(annotations);
	}
}