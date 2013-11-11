package edu.pdx.cs.multiview.smelldetector.detectors;

public interface SmellInstance{
	
	public double magnitude();
}

abstract class CachedSmellInstance implements SmellInstance
{
	private double cachedMagnitude = -1; 
	
	public abstract double calculateMagnitude();
	
	@Override
	public final double magnitude(){
		if(cachedMagnitude<0){
			cachedMagnitude = calculateMagnitude();
		}
		return cachedMagnitude;
	}
}
