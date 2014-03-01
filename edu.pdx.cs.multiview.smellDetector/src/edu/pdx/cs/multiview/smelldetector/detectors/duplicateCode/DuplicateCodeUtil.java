package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class DuplicateCodeUtil {
	public static String getLineWithoutTabsAndSpaces(String line) {
		return line.replaceAll("\\s","");
	}

	public static String[] getMethodLines(IMethod method) throws JavaModelException {
		String source = method.getSource();
		String newLineSeparator = System.getProperty( "line.separator" );
		String[] lines = source.split(newLineSeparator);
		return lines;
	}

	public static String[] getMethodLinesWithoutTabsAndSpaces(IMethod method) throws JavaModelException {
		String[] lines = getMethodLines(method);
		String[] linesWithoutSpace = new String[lines.length];
		for (int i = 0; i < lines.length; i++) {
			linesWithoutSpace[i] = getLineWithoutTabsAndSpaces(lines[i]);
		}
		return linesWithoutSpace;
	}
}
