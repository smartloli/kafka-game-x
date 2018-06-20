package org.smartloli.kafka.game.x.book_9.sql.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.smartloli.kafka.game.x.book_9.sql.util.ConfigUtil;

/**
 * Ignite初始化配置文件.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class IgniteRepository {

	private Ignite ignite;

	IgniteRepository() {

	}

	void init() {
		ignite = Ignition.start(ConfigUtil.getResource("ignite.xml"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	IgniteCache getCache(CacheConfiguration config) {
		return ignite.getOrCreateCache(config);
	}

	void close() {
		if (ignite != null) {
			ignite.close();
		}
	}

}
