package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.eclipse.jdt.core.IJavaProject;

import edu.pdx.cs.multiview.smelldetector.indexer.EhcacheFactory;

public class DuplicateCodeMetadataCollector {

	private static Map<String, DuplicateCodeMetadataCollector> collectorsAtProjectLevel = new HashMap<String, DuplicateCodeMetadataCollector>();

	private Cache duplicateCodeCache;
	private boolean initialized;

	private DuplicateCodeMetadataCollector(IJavaProject project) {
		String projectName = project.getElementName();
		this.duplicateCodeCache = getEhcacheFactory().createCache(projectName + "_duplicateCode");
	}

	public EhcacheFactory getEhcacheFactory() {
		return EhcacheFactory.getInstance();
	}

	public synchronized static DuplicateCodeMetadataCollector getInstance(IJavaProject project) {
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

	public Set<ClassAndMethodName> getClassAndMethodNames(int hashcode) {
		Element element = getFromCache(hashcode);
		if (initialized && element!= null && element.getObjectValue() != null) {
			return (Set<ClassAndMethodName>) element.getObjectValue();
		} else {
			return new HashSet<ClassAndMethodName>();
		}
	}
	
	synchronized Element getFromCache(int hashOfCode) {
		return duplicateCodeCache.get(hashOfCode);
	}
	
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	synchronized void  putInCache(int hashOfCode, Set<ClassAndMethodName> methodNames) {
		duplicateCodeCache.put(new Element(hashOfCode, methodNames));
	}

}