package edu.pdx.cs.multiview.smelldetector.detectors.typecast;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;

import edu.pdx.cs.multiview.smelldetector.detectors.ClassSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.MethodSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

//TODO: this class is more or lessa  copy of FeatureEnvyDetector
public class TypecastDetector extends SmellDetector<TypeCastInstance>{

	private ClassTypeCastRating ratings = new ClassTypeCastRating();
	
	public TypecastDetector(Flower f) {	super(f);	}
	
	@Override
	public double obviousness() {	return 0.94;		}

	@Override
	public TypeCastInstance calculateComplexity(List<IMethod> visibleMethods) {
		
		for (IMethod m : visibleMethods)
			ratings.cache(m);

		return new TypeCastInstance(ratings,visibleMethods);
	}


	@Override
	public String getName() {
		return "Typecast";
	}
	

	
	@Override
	public void showDetails() {
		//new FeatureEnvyExplanationWindow(currentSmell(),sourceViewer());
	}
}

class TypeCastInstance extends ClassTypeCastRating implements SmellInstance{
	
	private Collection<IMethod> visibleMethods;
	
	public TypeCastInstance(ClassTypeCastRating rating, Collection<IMethod> visibleMethods){
		super.rs = rating.rs;
		this.visibleMethods = visibleMethods;
	}

	public double calculateMagnitude() {
		int severity = 0;
		for (MethodTypeCastRating methodRating : ratings()) {
			severity += methodRating.classesReferenced().size();
		}

		return Math.log(severity) / (8 * SmellDetector.LOG2);
	}

	@Override
	protected Collection<MethodTypeCastRating> ratings() {
		
		List<MethodTypeCastRating> ratings = new LinkedList<MethodTypeCastRating>();
		//collect values on key predicates
		for(Map.Entry<IMethod, MethodTypeCastRating> r : rs.entrySet())
			if(visibleMethods.contains(r.getKey()))
				ratings.add(r.getValue());
		
		return ratings;
	}
}

class ClassTypeCastRating extends ClassSmellRating<MethodTypeCastRating, CastExpression>{

	protected MethodTypeCastRating createMethodRating(IMethod m) {
		return new MethodTypeCastRating(m);
	}

	@Override
	public double calculateMagnitude() {
		return 0;
	}
}

class MethodTypeCastRating extends MethodSmellRating<CastExpression>{

	public MethodTypeCastRating(IMethod m) {
		super(m);
	}

	protected ASTVisitor getVisitor() {
		return new ASTVisitor(){
			public boolean visit(CastExpression expr){
				process(expr.resolveTypeBinding(),expr);
				return true;
			}
		};
	}
}