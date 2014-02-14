package edu.pdx.cs.multiview.smelldetector.indexer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.smelldetector.detectors.dataClump.ClumpCollector;
import edu.pdx.cs.multiview.smelldetector.detectors.dataClump.ClumpsAtClassLevel;

@SuppressWarnings("restriction")
public class ClumpCreator {

	private ClumpCollector clumpCollector;

	public ClumpCreator(JavaEditor activeEditor) {
		IJavaProject project = JDTUtils.getCompilationUnit(activeEditor).getJavaProject();
		clumpCollector = ClumpCollector.getClumpCollector(project.getElementName());
		if (clumpCollector == null) {
			clumpCollector = ClumpCollector.createCumpCollector(project);
			createClumps(activeEditor);
		}
		clumpCollector.setInitialized(true);
	}

	private void createClumps(JavaEditor activeEditor) {
		try {
			ICompilationUnit compilationUnit = JDTUtils.getCompilationUnit(activeEditor);
			IPackageFragment[] buildPackages;
			buildPackages = compilationUnit.getJavaProject().getPackageFragments();
			createClumpsForMethodsInPackages(buildPackages);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void createClumpsForMethodsInPackages(IPackageFragment[] buildPackages) {
		try {
			for(IPackageFragment packageFragment : buildPackages){	
				if (packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
					createClumpsForMethodsInPackage(packageFragment);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	private void createClumpsForMethodsInPackage(IPackageFragment packageFragment) {
		try {
			ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
			for (ICompilationUnit iCompilationUnit : compilationUnits) {
				createClumpsForMethodsInFile(iCompilationUnit);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void createClumpsForMethodsInFile(ICompilationUnit iCompilationUnit) {
		try {
			IType[] allTypes = iCompilationUnit.getAllTypes();
			for (IType iType : allTypes) {
				ClumpsAtClassLevel clumpsAtClassLevel = new ClumpsAtClassLevel(iType);
				clumpsAtClassLevel.createClumps();
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}
}
