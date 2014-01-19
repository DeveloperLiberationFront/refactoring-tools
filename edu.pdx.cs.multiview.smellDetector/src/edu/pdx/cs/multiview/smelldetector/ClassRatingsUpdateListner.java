package edu.pdx.cs.multiview.smelldetector;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.detectors.ClassSmellRating;
import edu.pdx.cs.multiview.smelldetector.detectors.MethodSmellRating;

public class ClassRatingsUpdateListner  {

	
	@SuppressWarnings("rawtypes")
	private ClassSmellRating  ratings;

	
	
	public <MethodRating extends MethodSmellRating<T>,T> ClassRatingsUpdateListner(ClassSmellRating<MethodRating, T> ratings) {
		this.ratings = ratings;
	}



	public void updateRatingsFor(ICompilationUnit compilationUnit){
		try {
			IType[] allTypes = compilationUnit.getAllTypes();
			for (IType iType : allTypes) {
				IMethod[] methods = iType.getMethods();
				for (IMethod iMethod : methods) {
					ratings.removeFromCache(iMethod);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}	
	}
	
}
