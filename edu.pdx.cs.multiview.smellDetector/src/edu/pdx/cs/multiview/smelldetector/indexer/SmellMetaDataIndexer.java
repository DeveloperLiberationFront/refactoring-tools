package edu.pdx.cs.multiview.smelldetector.indexer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.smelldetector.detectors.dataClump.DataClumpCreator;

/**
 * @author robin
 * 
 *         This class is used for reading the code of entire project and then
 *         creating smell meta data for it. This class is not responsible for
 *         updating the metadata whenever there is change in the code.
 * 
 */
@SuppressWarnings("restriction")
public class SmellMetaDataIndexer {

	List<MethodSmellMetadataCreator> methodSmellMetadataCreators = new ArrayList<MethodSmellMetadataCreator>();

	public SmellMetaDataIndexer(JavaEditor activeEditor) {
		IJavaProject project = JDTUtils.getCompilationUnit(activeEditor).getJavaProject();
		MethodSmellMetadataCreator dataCumpCreator = new DataClumpCreator(project);
		methodSmellMetadataCreators.add(dataCumpCreator);
		createSmellMetaData(activeEditor);
	}

	private void createSmellMetaData(JavaEditor activeEditor) {
		try {
			ICompilationUnit compilationUnit = JDTUtils.getCompilationUnit(activeEditor);
			IPackageFragment[] buildPackages;
			buildPackages = compilationUnit.getJavaProject().getPackageFragments();
			createSmellMetaDataForPackages(buildPackages);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void createSmellMetaDataForPackages(IPackageFragment[] buildPackages) {
		try {
			for (IPackageFragment packageFragment : buildPackages) {
				if (packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
					createSmellMetaDataForPackage(packageFragment);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	private void createSmellMetaDataForPackage(IPackageFragment packageFragment) {
		try {
			ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
			for (ICompilationUnit iCompilationUnit : compilationUnits) {
				createSmellMetaDataForFile(iCompilationUnit);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void createSmellMetaDataForFile(ICompilationUnit iCompilationUnit) {
		try {
			for (IType iType : iCompilationUnit.getAllTypes()) {
				createSmellMetaDataForClass(iType);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	private void createSmellMetaDataForClass(IType iType) {
		try {
			for (IMethod iMethod : iType.getMethods()) {
				for (MethodSmellMetadataCreator metadataCreator : methodSmellMetadataCreators) {
					metadataCreator.createSmellMetaData(iMethod);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

}
