package edu.pdx.cs.multiview.smelldetector.indexer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

public class EhcacheFactory {

	private static EhcacheFactory factory = new EhcacheFactory();
	
	private EhcacheFactory() {
	}
	
	public Cache createCache(String cacheName) {
		Cache cache = CacheManager.getInstance().getCache(cacheName);
		if (cache == null) {
			CacheManager.getInstance().addCache(cacheName);
			cache = CacheManager.getInstance().getCache(cacheName);
		}
	
		CacheConfiguration config = cache.getCacheConfiguration();
		// TODO : actual value can be decided after discussion with team and
		// more profiling
		config.setMaxEntriesLocalHeap(500);
		config.setCopyOnRead(true);
		return cache;
	}

	public static EhcacheFactory getInstance() {
		return factory;
	}

}
