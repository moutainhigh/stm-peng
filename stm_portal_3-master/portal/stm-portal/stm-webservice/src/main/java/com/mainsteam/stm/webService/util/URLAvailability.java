package com.mainsteam.stm.webService.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 判断URL地址是否有效
 * 
 * @author Administrator
 *
 */
public class URLAvailability {
	private static HttpURLConnection con;
	private static int state = -1;
	/**
	 * 最多连接网络conCount次，如果conCount次不成功，则视为该地址不可用
	 */
	private static int conCount = 1;
	/**
	 * 检查当前URL连接是否可用
	 * 最多连接网络1次，如果1次不成功，则视为该地址不可用
	 * @param url 待检查的URL
	 * @return
	 */
	public synchronized static boolean isConnect(URL url) {
		int counts = 0;
		while (counts < conCount) {
			try {
				con = (HttpURLConnection) url.openConnection();
				//超时时间为3秒
				con.setConnectTimeout(3000);
				state = con.getResponseCode();
				if (state == 200) {
					return true;
				}
			} catch (IOException e) {
				counts++;
				continue;
			}
		}
		return false;
	}
}
