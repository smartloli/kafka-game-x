package org.smartloli.kafka.game.x.book_9.sql.ignite;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_9.sql.domain.NetworkSignalDomain;
import org.smartloli.kafka.game.x.book_9.sql.model.NetworkSignal;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存及窗口大小设置 & 处理Sql业务逻辑.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkSignalIgniteRepository {

	private static final String CACHE = "NetworkSignal";

	private static final Duration DURATION = new Duration(TimeUnit.MINUTES, 15); // 15 minutes window

	private static final Logger LOG = LoggerFactory.getLogger(NetworkSignalIgniteRepository.class);

	private IgniteRepository igniteRepository;

	private IgniteCache<String, NetworkSignalDomain> cache;

	@SuppressWarnings("unchecked")
	public NetworkSignalIgniteRepository() {
		this.igniteRepository = IgniteRepositoryFactory.getInstance();

		CacheConfiguration<String, NetworkSignal> cacheConfig = new CacheConfiguration<>(CACHE);
		cacheConfig.setCacheMode(CacheMode.REPLICATED);
		cacheConfig.setIndexedTypes(String.class, NetworkSignalDomain.class);
		this.cache = igniteRepository.getCache(cacheConfig).withExpiryPolicy(new CreatedExpiryPolicy(DURATION));
	}

	public void save(NetworkSignalDomain entity) {
		cache.put(entity.getId(), entity);
		LOG.info("Saved in ignite: " + entity);
	}

	public List<List<?>> sqlQuery(String query) {
		SqlFieldsQuery sql = new SqlFieldsQuery(query);
		try (QueryCursor<List<?>> cursor = cache.query(sql)) {
			return cursor.getAll();
		}
	}

	public void close() {
		igniteRepository.close();
	}

}
