package org.smartloli.kafka.game.x.book_9.sql;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_9.sql.ignite.NetworkSignalIgniteRepository;

/**
 * Kafka流处理SQL处理.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class IgniteStreamingSQLQuery implements Runnable {

	private static final long POLL_TIMEOUT = 3000;
	private final Logger LOG = LoggerFactory.getLogger(IgniteStreamingSQLQuery.class);

	private static final List<String> QUERIES = Arrays.asList("SELECT deviceId, SUM(rxData) AS rxTotal, " + "SUM(txData) AS txTOTAL FROM NetworkSignalDomain " + "GROUP BY deviceId ORDER BY rxTotal DESC, txTotal DESC LIMIT 5",
			"SELECT networkType, SUM(rxData) AS rxTotal, SUM(txData) AS txTotal " + "FROM NetworkSignalDomain GROUP BY networkType",
			"SELECT networkType, AVG(rxSpeed) AS avgRxSpeed, AVG(txSpeed) AS avgTxSpeed" + " FROM NetworkSignalDomain GROUP BY networkType");

	@Override
	public void run() {
		LOG.info("Initializing Ignite queries...");
		NetworkSignalIgniteRepository networkSignalRepository = new NetworkSignalIgniteRepository();
		try {
			List<List<?>> rows = null;
			do {
				// 等待数据写入
				try {
					Thread.sleep(POLL_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (String sql : QUERIES) {
					rows = networkSignalRepository.sqlQuery(sql);
					LOG.info("**********************************");
					for (List<?> row : rows) {
						LOG.info("Row: " + row.toString());
					}
				}
			} while (rows != null && rows.size() > 0);
		} finally {
			networkSignalRepository.close();
		}
	}
}
