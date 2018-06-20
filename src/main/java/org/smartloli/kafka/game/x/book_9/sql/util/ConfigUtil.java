package org.smartloli.kafka.game.x.book_9.sql.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取工具类.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public final class ConfigUtil {

	public static InputStream getResource(String resourceName) {
		return ConfigUtil.class.getClassLoader().getResourceAsStream(resourceName);
	}

	public static Properties getConfig(String configName) {
		try {
			Properties properties = new Properties();
			properties.load(getResource(configName + ".properties"));
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
