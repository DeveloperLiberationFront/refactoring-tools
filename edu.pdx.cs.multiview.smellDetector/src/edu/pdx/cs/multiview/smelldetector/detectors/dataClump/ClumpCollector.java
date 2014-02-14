package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class ClumpCollector {

	private static Map<String, ClumpCollector> clumpCollectorsAtProjectLevel = new HashMap<String, ClumpCollector>();

	private String projectName;
	private Cache dataClumpsCache;

	// boolean to track if all the clumps for a project has been created
	private boolean initialized;

	private IJavaProject project;

	private ClumpCollector(IJavaProject project) {
		String projectName = project.getElementName();
		this.project = project;
		this.projectName = projectName;
		initializeCacheForProject();
	}

	public static ClumpCollector createCumpCollector(IJavaProject project) {
		String projectName = project.getElementName();
		ClumpCollector clumpCollector = new ClumpCollector(project);
		clumpCollectorsAtProjectLevel.put(projectName, clumpCollector);
		return clumpCollector;
	}

	public static ClumpCollector getClumpCollector(String projectName) {
		return clumpCollectorsAtProjectLevel.get(projectName);
	}

	private void initializeCacheForProject() {
		String cacheName = projectName + "_dataclumps";
		dataClumpsCache = CacheManager.getInstance().getCache(cacheName);
		if (dataClumpsCache == null) {
			CacheManager.getInstance().addCache(cacheName);
			dataClumpsCache = CacheManager.getInstance().getCache(cacheName);
		}

		CacheConfiguration config = dataClumpsCache.getCacheConfiguration();
		// TODO : actual value can be decided after discussion with team and
		// more profiling
		config.setMaxEntriesLocalHeap(500);

	}

	public void addToCache(ClumpSignature sig, IMethod m) {
		Element element = dataClumpsCache.get(sig);
		if (element == null || element.getObjectValue() == null) {
			ClumpGroup g = new ClumpGroup(sig, m);
			ClumpGroupHolder clumpGroupHolder = new ClumpGroupHolder(g);
			System.out.println(" Saving Group \n " + clumpGroupHolder);
			dataClumpsCache.put(new Element(sig, clumpGroupHolder));
		} else {
			ClumpGroupHolder existingGroup = (ClumpGroupHolder) element.getObjectValue();
			existingGroup.add(m);
		}
	}

	public List<ClumpGroup> inGroupOf(IMethod method) {
		if (initialized) {
			try {
				List<ClumpSignature> sigs = ClumpSignature.from(method.getParameterNames());
				List<ClumpGroup> groups = new LinkedList<ClumpGroup>();
				for (ClumpSignature sig : sigs) {
					groups.add(getFromCache(sig));
				}
				return groups;
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<ClumpGroup>();
	}

	private ClumpGroup getFromCache(ClumpSignature sig) {
		Element element = dataClumpsCache.get(sig);
		if (element != null) {
			Object objectValue = element.getObjectValue();
			ClumpGroupHolder groupHolder = (ClumpGroupHolder) objectValue;
			System.out.println(" Found Clump Group \n" + groupHolder);
			ClumpGroup clumpGroup = groupHolder.getGroup(project);
			return clumpGroup;
		} else {
			System.out.println(" **** No Clump Found for Sig *** :" + sig);
			return new EmptyClumpGroup(sig);
		}
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

}
