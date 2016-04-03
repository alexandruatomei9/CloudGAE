package info.cloud.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

import info.cloud.util.GoogleResult;

public class GoogleMemcache {
	public static final Logger _log = Logger.getLogger(GoogleMemcache.class.getName());

	private static GoogleMemcache _instance;
	private Cache cache;

	private GoogleMemcache() {
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			Map<Object, Object> properties = new HashMap<>();
			properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.HOURS.toSeconds(1));
			cache = cacheFactory.createCache(properties);
		} catch (CacheException e) {
			_log.warning("Error in creating the Cache");
		}
	}

	public static synchronized GoogleMemcache getInstance() {
		if (_instance == null) {
			_instance = new GoogleMemcache();
		}
		return _instance;
	}

	@SuppressWarnings("unchecked")
	public List<GoogleResult> findInCache(String key) {
		if (cache.containsKey(key)) {
			return (List<GoogleResult>) cache.get(key);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void putInCache(String key, List<GoogleResult> resluts) {
		cache.put(key, resluts);
	}
}
