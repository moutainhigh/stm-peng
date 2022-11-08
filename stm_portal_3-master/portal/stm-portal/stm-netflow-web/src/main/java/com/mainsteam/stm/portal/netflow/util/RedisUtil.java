package com.mainsteam.stm.portal.netflow.util;

import java.io.UnsupportedEncodingException;

import redis.clients.jedis.Jedis;

public class RedisUtil {

	public static void setLicense(String ip, int port, String license)
			throws UnsupportedEncodingException {
		Jedis jedis = new Jedis(ip, port);
		jedis.set("LicenseEncryptedKey".getBytes(),
				ChinaWiservEncrypt.TriDesEncrypt(license.getBytes("UTF-8")));
		jedis.close();
	}
}
