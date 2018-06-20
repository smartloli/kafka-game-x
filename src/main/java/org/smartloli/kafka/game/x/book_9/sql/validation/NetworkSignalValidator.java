package org.smartloli.kafka.game.x.book_9.sql.validation;

import org.smartloli.kafka.game.x.book_9.sql.domain.NetworkSignalDomain;

/**
 * 校验实体对象.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public class NetworkSignalValidator {

	public boolean isValid(NetworkSignalDomain entity) {
		return entity.getId() != null && entity.getDeviceId() != null && entity.getTime() != null && entity.getRxSpeed() != null && entity.getTxSpeed() != null && entity.getRxData() != null && entity.getTxData() != null
				&& entity.getRxSpeed() > 0 && entity.getTxSpeed() > 0 && entity.getRxData() > 0 && entity.getTxData() > 0;
	}

}
