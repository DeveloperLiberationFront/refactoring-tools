package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;


public class ClumpCollector {
	
	private static Map<String, ClumpCollector> clumpCollectorsAtProjectLevel = new HashMap<String, ClumpCollector>();
	
	private String projectName;
	private Cache dataClumpsCache;
	
	private ClumpCollector(String projectName){
		this.projectName = projectName;
		initializeCacheForProject();
	}

	public static ClumpCollector createCumpCollector(String projectName) {
		ClumpCollector clumpCollector = new ClumpCollector(projectName);
		clumpCollectorsAtProjectLevel.put(projectName, clumpCollector);
		return clumpCollector;
	}
	
	public static ClumpCollector getClumpCollector(String projectName){
		return clumpCollectorsAtProjectLevel.get(projectName);
	}
	

	private void initializeCacheForProject(){
		dataClumpsCache = CacheManager.getInstance().getCache(projectName);
		String cacheName = projectName + "dataclumps";
		if (dataClumpsCache == null) {
			CacheManager.getInstance().addCache(cacheName);
			dataClumpsCache = CacheManager.getInstance().getCache(cacheName);
		}

		CacheConfiguration config = dataClumpsCache.getCacheConfiguration();
		// TODO : actual value can be decided after discussion with team and more profiling
		config.setMaxEntriesLocalHeap(500);
		// TODO : actual value can be decided after discussion with team and more profiling
		config.setTimeToLiveSeconds(1200000);
	}

	public void addToCache(ClumpSignature sig, IMethod m) {
		Element element = dataClumpsCache.get(sig);
		if (element == null || element.getObjectValue() == null) {
			ClumpGroup g = new ClumpGroup(sig, m);
			ClumpGroupHolder clumpGroupHolder = new ClumpGroupHolder(g);
			dataClumpsCache.put(new Element(sig, clumpGroupHolder));
			System.out.println(" Addig to cache " + sig);
		} else {
			ClumpGroupHolder existingGroup = (ClumpGroupHolder) element.getObjectValue();
			existingGroup.add(m);
		}
	}
	
	public List<ClumpGroup> inGroupOf(IMethod method){
		try {
			List<ClumpSignature> sigs = ClumpSignature.from(method.getParameterNames());
			List<ClumpGroup> groups = new LinkedList<ClumpGroup>();
			for(ClumpSignature sig : sigs){
				groups.add(getFromCache(sig));
			}
			return groups;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ClumpGroup getFromCache(ClumpSignature sig) {
		Element element = dataClumpsCache.get(sig);
		return (ClumpGroup) element.getObjectValue();
	}

}
