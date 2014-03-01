package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import java.util.List;

import org.eclipse.jdt.core.IMethod;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class DuplicateCodeDetector extends SmellDetector<DuplicateCodeInstace>{

	private static final double MODERATELY_OBVIOUS = 0.7;
	private static final String DUPLICATE_CODE_LABEL_TEXT = "Duplicate Code";
	
	
	public DuplicateCodeDetector(Flower f) {
		super(f);
	}

	@Override
	public String getName() {
		return DUPLICATE_CODE_LABEL_TEXT;
	}

	@Override
	public DuplicateCodeInstace calculateComplexity(List<IMethod> visibleMethods) {
		return new DuplicateCodeInstace(visibleMethods);
	}

	@Override
	public double obviousness() {
		return MODERATELY_OBVIOUS;
	}

	@Override
	public void showDetails() {
		// TODO Auto-generated method stub
		
	}
}
