package edu.pdx.cs.multiview.smelldetector.detectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import edu.pdx.cs.multiview.jface.annotation.AnnTransaction;
import edu.pdx.cs.multiview.jface.annotation.AnnotationPainter;
import edu.pdx.cs.multiview.jface.annotation.ISelfDrawingAnnotation;

public class SmellExplanationOverlay<SI extends SmellInstance> {

	private SourceViewer viewer;
	//TODO: not clear that instance should be here... probablyin Window
	protected SI instance;
	private AnnotationPainter painter;
	private List<Color> allocatedColors = new ArrayList<Color>();
	
	public SmellExplanationOverlay(SI si, ISourceViewer sourceViewer){
		viewer = (SourceViewer) sourceViewer;
		this.instance = si;
		this.painter = new AnnotationPainter(viewer);
	}

	public StyledText textWidget(){
		return viewer.getTextWidget();
	}

	public SI getInstance() {
		return instance;
	}
	
	public void dispose(){

		for(Color c : allocatedColors)
			c.dispose();
		
		painter.deactivate(false);
		painter.dispose();
		painter = null;
	}
	
	public void clear(){
		painter.removeAllAnnotations();
	}
	
	protected void allocateColors(Collection<Color> colors) {
		allocatedColors.addAll(colors);
	}
	
	protected void allocateColor(Color c){
		allocatedColors.add(c);
	}

	protected void addAnnotations(AnnTransaction at) {
		painter.replaceAnnotations(at);
	}
	
	protected void refreshAnnotations(Collection<ISelfDrawingAnnotation> someAnnotations){
		painter.refresh(someAnnotations);
	}
}
