package edu.pdx.cs.multiview.smelldetector.detectors.tooManyArguments;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

/**
 * Adding more arguments decreases the readability of the code and it can be an indirect sign of low cohesion.
 * One can use "Introduce Parameter Object" refactoring to avoid this code smell in some situations
 * 
 * Below are Uncle Bob's views about it:
 * "Functions should have a small number of arguments. No argument is best,followed by one, two, and three. 
 *  More than three is very questionable and should be avoided with prejudice."
 *  	
 * @author robin
 *
 */
public class TooManyArgumentsDetector extends SmellDetector<TooManyArgumentsClassInstance> {

	public static final String TOO_MANY_ARGUMENTS_LABEL_TEXT = "Too Many Arguments";
	private static final double HIGHLY_OBVIOUS = 0.95;

	public TooManyArgumentsDetector(Flower f) {
		super(f);
	}

	/**
	 *  We think that code smell related to number of arguments is quite obvious as number of arguments can be easily counted. 
	 *  However, we think that its obviousness is less that large methods and large class as its generally neglected by the developers. 
	 *  
	 */
	@Override
	public double obviousness() {
		return HIGHLY_OBVIOUS;
	}

	@Override
	public TooManyArgumentsClassInstance calculateComplexity(List<IMethod> visibleMethods) {
		TooManyArgumentsClassInstance inst = new TooManyArgumentsClassInstance();
		for (IMethod m : visibleMethods) {
			inst.put(m, inst.numberOfArguments(m));
		}
		return inst;
	}

	@Override
	public void showDetails() {
		new TooManyArgumentsExplanationWindow(currentSmell(), sourceViewer());
	}

	@Override
	public String getName() {
		return TOO_MANY_ARGUMENTS_LABEL_TEXT;
	}
}

class TooManyArgumentsClassInstance implements SmellInstance {
	private static final int MAXIMUM_CODE_SMELL_VALUE = 1;
	private static final int LEVELS_OF_SMELL = 3;
	private static final int TWO_NUMBER_OF_ARGUMENTS = 2;
	private Map<IMethod, Integer> methodToNumberOfArguments = new HashMap<IMethod, Integer>();

	

	public void put(IMethod m, int i) {
		getMethodToNumberOfArguments().put(m, i);
	}

	public int size() {
		return getMethodToNumberOfArguments().size();
	}


	/**
	 * 
	 * Two arguments are considered fine. But as the number of arguments increases more that two, it can be
	 * considered as a sign of code smell.  So, we divided the smells into three levels 
	 * 1) number of arguments <= 2  : no smell
	 * 2) number of arguments == 3  : can be a code smell, but can be a valid scenarios as well
	 * 3) number of arguments == 4  : is a code smell 
	 * 4) number of arguments >= 5  : prominent code smell
	 *  
	 */
	@Override
	public double magnitude() {
		double numberOfArgumentsExceedingThresholdOfTwo = 0;
		for (IMethod m : methodToNumberOfArguments.keySet()) {
			numberOfArgumentsExceedingThresholdOfTwo = Math.max((numberOfArguments(m)-TWO_NUMBER_OF_ARGUMENTS), numberOfArgumentsExceedingThresholdOfTwo);
		}

		double normalizedSeverity = numberOfArgumentsExceedingThresholdOfTwo / LEVELS_OF_SMELL;
		double severityValue = normalizedSeverity > MAXIMUM_CODE_SMELL_VALUE ? MAXIMUM_CODE_SMELL_VALUE : normalizedSeverity;
		return severityValue ;
	}

	public int numberOfArguments(IMethod m) {
		return m.getNumberOfParameters();
	}

	public Map<IMethod, Integer> getMethodToNumberOfArguments() {
		return methodToNumberOfArguments;
	}
	
	public Integer getMaxNumberOfArguments(){
		Collection<Integer> values = methodToNumberOfArguments.values();
		return Collections.max(values);
	}
	

}