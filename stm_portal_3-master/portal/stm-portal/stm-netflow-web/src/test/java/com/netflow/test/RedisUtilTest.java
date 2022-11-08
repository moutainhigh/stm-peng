package com.netflow.test;

import java.io.UnsupportedEncodingException;

import com.mainsteam.stm.portal.netflow.util.ChinaWiservEncrypt;

public class RedisUtilTest {
	public static void main(String[] args) throws UnsupportedEncodingException {
		// Jedis jedis = new Jedis("172.16.7.252", 6379);
		// jedis.set("foo", "bar");
		// String value = jedis.get("foo");
		// System.out.println(value);
		// jedis.close();

		// RedisUtil.addLicense("172.16.7.252", 6379, "");

		String license = "20150227#80:c1:6e:67:e2:26##[NTAS]NF_ANA:ON#NF_DEV_NUMBER:10#NF_IF_NUMBER:100#NAT_LOG:OFF#NAT_LOG_FPS:10000#NAT_LOG_SIZE:20[/NTAS]";
		byte[] sd = ChinaWiservEncrypt.TriDesEncrypt(license.getBytes("UTF-8"));
		System.out.println(sd);
	}
}
