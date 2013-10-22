package edu.pdx.cs.multiview.smelldetector.detectors;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.source.ISourceViewer;

import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public abstract class SmellDetector<Smell extends SmellInstance>{
	
	private Flower flower;
	private Smell currentSmell;
	private ISourceViewer sourceViewer;
	
	public SmellDetector(Flower f){
		this.flower = f;
	}
	
	public final double size(){
		return severity();
	}
	
	public double order(){
		return obviousness();
	}
	
	public abstract String getName();
	
	/**
	 * @return	how bad the smell is, 1 being worst
	 */
	private double severity(){
		
		double severity = currentSmellMagnitude();
		
		if(severity>1.0)
			severity = 1.0;
		else if(severity<0.0)
			severity = 0.0;
		
		return severity;
	}
	
	protected Smell currentSmell(){
		return currentSmell;
	}
	
	/**
	 * @param newSmell
	 * 
	 * @return	true if the severity has changed
	 */
	protected boolean setSeverity(Smell newSmell){
	
		double oldMag = currentSmellMagnitude();
		double newMag = newSmell.magnitude();
		
		currentSmell = newSmell;
		
		return oldMag!=newMag;
	}

	private double currentSmellMagnitude() {
		return currentSmell()==null ? -1 : currentSmell().magnitude();
	}
	
	/**
	 * This message should be sent whenever new smells need
	 * to be recomputed
	 * 
	 * @param e	the current editor in focus
	 * 
	 * @return	if the severity has changed
	 */
	public boolean recompute(ISourceViewer viewer, List<IMethod> methods) {

		try {
			this.sourceViewer = viewer;
			return setSeverity(calculateComplexity(methods));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return false;
	}
	
	public void redrawUI(){
		flower.redraw();
	}

	protected ISourceViewer sourceViewer(){
		return sourceViewer;
	}
	
	public abstract Smell calculateComplexity(List<IMethod> visibleMethods);
	
	/**
	 * @return	how obvious the smells is, 1 being very obvious
	 */
	public abstract double obviousness();

	public static final double LOG2 = Math.log(2);

	public abstract void showDetails();
}

