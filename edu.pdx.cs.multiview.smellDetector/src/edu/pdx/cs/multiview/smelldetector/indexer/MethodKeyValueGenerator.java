package edu.pdx.cs.multiview.smelldetector.indexer;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class MethodKeyValueGenerator {

	private static MethodKeyValueGenerator keyValueGenerator = new MethodKeyValueGenerator();

	private MethodKeyValueGenerator() {
	}

	public static MethodKeyValueGenerator getInstance() {
		return keyValueGenerator;
	}

	public String[] getClassNameAndMethodSignature(IMethod iMethod) {
		String[] typeAndMethodSignature = new String[2];
		typeAndMethodSignature[0] = iMethod.getDeclaringType().getFullyQualifiedName();
		typeAndMethodSignature[1] = getMethodFullName(iMethod);
		return typeAndMethodSignature;
	}

	public IMethod getMethod(IJavaProject project, String[] methodKey) {
		IMethod method = null;
		try {
			IType type = project.findType(methodKey[0]);
			IMethod[] methods = type.getMethods();
			for (IMethod iMethod : methods) {
				if (getMethodFullName(iMethod).equals(methodKey[1])) {
					method = iMethod;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return method;
	}
	
	private  String getMethodFullName(IMethod iMethod){
	        StringBuilder name = new StringBuilder();
	        name.append(iMethod.getDeclaringType().getFullyQualifiedName());
	        name.append(".");
	        name.append(iMethod.getElementName());
	        name.append("(");

	        String comma = "";
	        for (String type : iMethod.getParameterTypes()) {
	                name.append(comma);
	                comma = ", ";
	                name.append(type);
	        }
	        name.append(")");

	        return name.toString();
	}

}
