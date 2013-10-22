package edu.pdx.cs.multiview.smelldetector.detectors.InstanceOf;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.InstanceofExpression;

import edu.pdx.cs.multiview.smelldetector.detectors.ClassSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.MethodSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class InstanceoftDetector extends SmellDetector<InstanceOfInstance>{

	private ClassInstanceOfRating ratings = new ClassInstanceOfRating();
	
	public InstanceoftDetector(Flower f) {	super(f);	}
	
	@Override
	public double obviousness() {	return 0.945;		}
	
	@Override
	public InstanceOfInstance calculateComplexity(List<IMethod> visibleMethods) {
		
		for (IMethod m : visibleMethods)
			ratings.cache(m);

		return new InstanceOfInstance(ratings,visibleMethods);
	}


	@Override
	public String getName() {
		return "Instance Of";
	}
	

	
	@Override
	public void showDetails() {
		//new FeatureEnvyExplanationWindow(currentSmell(),sourceViewer());
	}
}

class InstanceOfInstance extends ClassInstanceOfRating implements SmellInstance{
	
	private Collection<IMethod> visibleMethods;
	
	public InstanceOfInstance(ClassInstanceOfRating rating, Collection<IMethod> visibleMethods){
		super.rs = rating.rs;
		this.visibleMethods = visibleMethods;
	}

	public double calculateMagnitude() {
		int instOfExprCount = 0;
		for (MethodInstanceOfRating methodRating : ratings()) {
			instOfExprCount += methodRating.smells().size();
		}

		//we add 0.5 because 1 instanceof is worth pointing out
		//we multiply by 0.5 to extend the curve: about 7 or 8 
		//			instanceof expressions produces maximum wedge size
		return 0.5 * Math.log(instOfExprCount+0.5);
	}

	@Override
	protected Collection<MethodInstanceOfRating> ratings() {
		
		List<MethodInstanceOfRating> ratings = new LinkedList<MethodInstanceOfRating>();
		//collect values on key predicates
		for(Map.Entry<IMethod, MethodInstanceOfRating> r : rs.entrySet())
			if(visibleMethods.contains(r.getKey()))
				ratings.add(r.getValue());
		
		return ratings;
	}
}

class ClassInstanceOfRating extends ClassSmellRating<MethodInstanceOfRating, InstanceofExpression>{

	protected MethodInstanceOfRating createMethodRating(IMethod m) {
		return new MethodInstanceOfRating(m);
	}

	@Override
	public double calculateMagnitude() {
		return 0;
	}
}

class MethodInstanceOfRating extends MethodSmellRating<InstanceofExpression>{

	public MethodInstanceOfRating(IMethod m) {
		super(m);
	}

	protected ASTVisitor getVisitor() {
		return new ASTVisitor(){
			public boolean visit(InstanceofExpression expr){
				process(expr.resolveTypeBinding(),expr);
				return true;
			}
		};
	}
}