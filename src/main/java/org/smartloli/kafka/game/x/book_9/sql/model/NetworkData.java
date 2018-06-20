package org.smartloli.kafka.game.x.book_9.sql.model;

import java.util.List;

import org.smartloli.kafka.game.x.book_9.sql.util.JsonSerializable;

/**
 * 构建数据对象.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkData extends JsonSerializable {

	private static final long serialVersionUID = -7188517737272721658L;

	private String deviceId;
	private Long time;
	private List<NetworkSignal> signals;

	public String getDeviceId() {
		return deviceId;
	}

	public NetworkData setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public Long getTime() {
		return time;
	}

	public NetworkData setTime(Long time) {
		this.time = time;
		return this;
	}

	public List<NetworkSignal> getSignals() {
		return signals;
	}

	public NetworkData setSignals(List<NetworkSignal> signals) {
		this.signals = signals;
		return this;
	}

}
