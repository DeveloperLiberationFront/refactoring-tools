package edu.pdx.cs.multiview.smelldetector;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.test.JavaTestProject;

public class BaseSmellDetectorTest {

	private JavaTestProject testProject;

	/**
	 * @return the testProject
	 */
	public JavaTestProject getTestProject() {
		return testProject;
	}

	/**
	 * Pass the path of the java file that needs to be added to the test
	 * project. We would like to have separate java files for different types of
	 * tests. Add the methods/fields in the file against which you want to write
	 * your tests.
	 * 
	 * @throws CoreException
	 * 
	 */
	public BaseSmellDetectorTest(String filePath) throws Exception {
		testProject = createTestProject();
		IPackageFragment frag = testProject.createPackage("com.testSmellDetector"); 
		frag.createCompilationUnit("SmellClass.java", readFileAsString(filePath), true, null);
	}

	private JavaTestProject createTestProject() throws CoreException {
		JavaTestProject testProject = new JavaTestProject();
		return testProject;
	}

	private String readFileAsString(String filePath) throws IOException {
		File file = new File(filePath);
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		try {
			long len = file.length();
			if (len > Integer.MAX_VALUE)
				throw new IOException("File " + file.getPath() + " too large, was " + len + " bytes.");
			byte[] bytes = new byte[(int) len];
			dis.readFully(bytes);
			return new String(bytes, "UTF-8");
		} finally {
			dis.close();
		}
	}

	public IMethod getMethod(String methodSignature) throws JavaModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		for (IProject project : projects) {
			for (IPackageFragment packageFragment : JavaCore.create(project).getPackageFragments()) {
				if (packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
					ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : compilationUnits) {
						for (IType iType : iCompilationUnit.getAllTypes()) {
							for (IMethod iMethod : iType.getMethods()) {
								if(iMethod.getElementName().equals(methodSignature)) return iMethod;	
							}
							
						}
					}
				}
			}
	
		}
		return null;
	}

}
