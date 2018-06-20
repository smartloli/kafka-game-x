package org.smartloli.kafka.game.x.book_9.sql.parser;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.game.x.book_9.sql.domain.NetworkSignalDomain;
import org.smartloli.kafka.game.x.book_9.sql.model.NetworkData;
import org.smartloli.kafka.game.x.book_9.sql.model.NetworkSignal;
import org.smartloli.kafka.game.x.book_9.sql.util.HashCodeUtil;
import org.smartloli.kafka.game.x.book_9.sql.validation.NetworkSignalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将JSON数据转换成实体对象.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkDataParser {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkDataParser.class);

	private Gson gson = new Gson();

	private NetworkSignalValidator validator;

	public NetworkDataParser(NetworkSignalValidator validator) {
		this.validator = validator;
	}

	public List<NetworkSignalDomain> parse(String json) throws Exception {
		// 转换JSON数据
		NetworkData networkData = gson.fromJson(json, NetworkData.class);

		// 数据为空时进行补充并过滤, 最后返回
		if (networkData == null || networkData.getSignals() == null) {
			return new ArrayList<>(0);
		}
		// 转换对象&添加唯一ID
		return networkData.getSignals().stream().map(networkSignal -> convert(networkData.getDeviceId(), networkSignal)).map(entity -> entity.setId(generateUniqueId(entity))).filter(entity -> validator.isValid(entity))
				.collect(Collectors.toList());
	}

	/** 数据实体转换. */
	private NetworkSignalDomain convert(String deviceId, NetworkSignal networkSignal) {
		return new NetworkSignalDomain().setDeviceId(deviceId).setTime(networkSignal.getTime()).setLatitude(networkSignal.getLatitude()).setLongitude(networkSignal.getLongitude()).setNetworkType(networkSignal.getNetworkType())
				.setRxSpeed(networkSignal.getRxSpeed()).setTxSpeed(networkSignal.getTxSpeed()).setRxData(networkSignal.getRxData()).setTxData(networkSignal.getTxData());
	}

	/** 生产唯一的Hash值. */
	private String generateUniqueId(NetworkSignalDomain entity) {
		try {
			return HashCodeUtil.getHashString(entity.getDeviceId(), entity.getTime(), entity.getNetworkType(), entity.getRxData(), entity.getTxData(), entity.getRxSpeed(), entity.getTxSpeed(), entity.getLatitude(), entity.getLongitude());
		} catch (Exception e) {
			LOG.error("Error generating unique id", e);
			return null;
		}
	}

}
