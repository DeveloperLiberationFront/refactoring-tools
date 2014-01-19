package edu.pdx.cs.multiview.smelldetector;

import java.util.List;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;

import edu.pdx.cs.multiview.jdt.util.ASTPool;

public class JavaCodeChangeListner implements IElementChangedListener {
	private List<ClassRatingsUpdateListner> classRatingsUpdateListners;

	public JavaCodeChangeListner(List<ClassRatingsUpdateListner> classRatingsUpdateListners) {
		this.classRatingsUpdateListners = classRatingsUpdateListners;
	}

	public void elementChanged(ElementChangedEvent event) {
		IJavaElementDelta delta = event.getDelta();
		if (delta != null) {
			if (delta.getElement().getElementType() == IJavaElement.COMPILATION_UNIT) {
				ASTPool.getDefaultCU().removeEntry(delta.getElement().getPath().toString());
				for (ClassRatingsUpdateListner listner : classRatingsUpdateListners) {
					ICompilationUnit compilationUnit = (ICompilationUnit) delta.getElement();
					listner.updateRatingsFor(compilationUnit);
				}

			}

		}
	}
}