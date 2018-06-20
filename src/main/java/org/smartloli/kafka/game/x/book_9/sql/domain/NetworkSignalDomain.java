package org.smartloli.kafka.game.x.book_9.sql.domain;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.smartloli.kafka.game.x.book_9.sql.util.JsonSerializable;

/**
 * 构建对象SQL查询实体.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkSignalDomain extends JsonSerializable {

	private static final long serialVersionUID = -6650766189688673925L;

	@QuerySqlField
	private String id;

	@QuerySqlField(index = true)
	private String deviceId;

	@QuerySqlField(index = true)
	private Long time;

	@QuerySqlField(index = true)
	private String networkType;

	@QuerySqlField
	private Double rxSpeed; // 接收速度

	@QuerySqlField
	private Double txSpeed; // 发送速度

	@QuerySqlField
	private Long rxData; // 接收数据

	@QuerySqlField
	private Long txData; // 发送数据

	@QuerySqlField
	private Double latitude; // 经度

	@QuerySqlField
	private Double longitude; // 纬度

	public String getId() {
		return id;
	}

	public NetworkSignalDomain setId(String id) {
		this.id = id;
		return this;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public NetworkSignalDomain setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public Long getTime() {
		return time;
	}

	public NetworkSignalDomain setTime(Long time) {
		this.time = time;
		return this;
	}

	public String getNetworkType() {
		return networkType;
	}

	public NetworkSignalDomain setNetworkType(String networkType) {
		this.networkType = networkType;
		return this;
	}

	public Double getRxSpeed() {
		return rxSpeed;
	}

	public NetworkSignalDomain setRxSpeed(Double rxSpeed) {
		this.rxSpeed = rxSpeed;
		return this;
	}

	public Double getTxSpeed() {
		return txSpeed;
	}

	public NetworkSignalDomain setTxSpeed(Double txSpeed) {
		this.txSpeed = txSpeed;
		return this;
	}

	public Long getRxData() {
		return rxData;
	}

	public NetworkSignalDomain setRxData(Long rxData) {
		this.rxData = rxData;
		return this;
	}

	public Long getTxData() {
		return txData;
	}

	public NetworkSignalDomain setTxData(Long txData) {
		this.txData = txData;
		return this;
	}

	public Double getLatitude() {
		return latitude;
	}

	public NetworkSignalDomain setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	public Double getLongitude() {
		return longitude;
	}

	public NetworkSignalDomain setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

}
