package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.List;

import org.eclipse.jdt.core.IMethod;

import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class DataClumpDetector extends SmellDetector<SmellInstance>{

	private ClumpSpider spider = new ClumpSpider();
	
	public DataClumpDetector(Flower f) {
		super(f);
	}

	@Override
	public double obviousness() {	return 0.2;		}

	@Override
	public SmellInstance calculateComplexity(List<IMethod> ms){		
		
		spider.spiderFrom(ms);

		return spider;
	}


	@Override
	public String getName() {
		return "Data Clumps";
	}
	
	@Override
	public void showDetails() {
		new DataClumpExplanationWindow(spider,sourceViewer());
	}
}