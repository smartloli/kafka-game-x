package org.smartloli.kafka.game.x.book_9.sql.util;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * 序列化对象工具类.
 * 
 * @author smartloli.
 *
 *         Created by Jun 20, 2018
 */
public abstract class JsonSerializable implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new Gson();

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

}
