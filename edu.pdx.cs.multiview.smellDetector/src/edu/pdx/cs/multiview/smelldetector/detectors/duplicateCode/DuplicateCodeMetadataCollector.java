package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import edu.pdx.cs.multiview.smelldetector.indexer.EhcacheFactory;

public class DuplicateCodeMetadataCollector {

	private static Map<String, DuplicateCodeMetadataCollector> collectorsAtProjectLevel = new HashMap<String, DuplicateCodeMetadataCollector>();

	private Cache duplicateCodeCache;

	private DuplicateCodeMetadataCollector(IJavaProject project) {
		String projectName = project.getElementName();
		this.duplicateCodeCache = getEhcacheFactory().createCache(projectName + "_duplicateCode");
	}

	public EhcacheFactory getEhcacheFactory() {
		return EhcacheFactory.getInstance();
	}

	public static DuplicateCodeMetadataCollector getInstance(IJavaProject project) {
		String projectName = project.getElementName();
		DuplicateCodeMetadataCollector collector = collectorsAtProjectLevel.get(projectName);
		if (collector == null) {
			collector = new DuplicateCodeMetadataCollector(project);
			collectorsAtProjectLevel.put(projectName, collector);
		}
		return collector;
	}

	public void save(int hashOfCode, String[] classAndMethodName) {
		ClassAndMethodName methodName = new ClassAndMethodName(classAndMethodName);
		saveClassAndMethodForHashcode(hashOfCode, methodName);
	}

	public void saveClassAndMethodForHashcode(int hashOfCode, ClassAndMethodName methodName) {
		boolean isElementPresentForHashCode = getFromCache(hashOfCode) == null || getFromCache(hashOfCode).getObjectValue() == null;
		Set<ClassAndMethodName> methodNames = null; 
		
		if (isElementPresentForHashCode) {
			methodNames = new HashSet<ClassAndMethodName>();
		} else {
			methodNames = (Set<ClassAndMethodName>) getFromCache(hashOfCode).getObjectValue();
		}

		methodNames.add(methodName);
		putInCache(hashOfCode, methodNames);
	}

	public Set<ClassAndMethodName> getClassAndMethodNames(int hashcode){
		Element element = getFromCache(hashcode);
		return (Set<ClassAndMethodName>) element.getObjectValue();
	}
	
	Element getFromCache(int hashOfCode) {
		return duplicateCodeCache.get(hashOfCode);
	}
	
	

	void putInCache(int hashOfCode, Set<ClassAndMethodName> methodNames) {
		duplicateCodeCache.put(new Element(hashOfCode, methodNames));
	}

}