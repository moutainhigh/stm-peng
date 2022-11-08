package com.mainsteam.stm.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IMemcacheTest {

	@Test
	public void test() throws Exception {
		IMemcache<String> cacheL = MemCacheFactory
				.getLocalMemCache(String.class);
		IMemcache<String> cacheR = MemCacheFactory
				.getRemoteMemCache(String.class);
		System.out.println(cacheR.isActivate());
		cacheR.set("aaa11", "AAA");
		System.out.println(cacheR.get("aaa11"));
		cacheR.delete("aaa11");
		System.out.println(cacheR.get("aaa11"));
		cacheR.set("aaa11", "AAA");
		System.out.println(cacheR.get("aaa11"));
		Thread.sleep(10000);
		System.out.println(cacheR.get("aaa11"));
		System.out.println(cacheR.isActivate());
	}

	@Test
	public void test1() {
		IMemcache<String> cacheL = MemCacheFactory
				.getRemoteMemCache(String.class);
		byte[] content = new byte[1024];
		do {
			String readValue = null;
			try {
				int length = System.in.read(content);
				readValue = new String(content, 0, length).trim();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cacheL.set("test", readValue);
			System.out.println("put value " + cacheL.get("test"));
		} while (true);
	}

	@Test
	public void test2() {
		IMemcache<String> cacheL = MemCacheFactory
				.getRemoteMemCache(String.class);
		String value = "a";
		cacheL.set("test",value);
		List<String> values = new ArrayList<>();
		cacheL.update("test", value);
		String readValue =null;
		while (true) {
			 readValue = cacheL.get("test");
			 values.add(readValue);
//			if (!(value == null && readValue == value || value != null
//					&& value.equals(readValue))) {
				System.out.println("read value " +readValue);
				value = readValue;
//			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
