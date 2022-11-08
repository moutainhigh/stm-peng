package com.mainsteam.stm.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * <li>文件名称: UtilTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月25日
 * @author   ziwenwen
 */
public class UtilTest {

	@Test
	public void testIsEmpty() {
		assertTrue(Util.isEmpty(null));
		assertTrue(Util.isEmpty(""));
	}

	@Test
	public void testInitialUpeercase() {
		assertEquals("Aaaaa", Util.initialUpeercase("aaaaa"));
	}

	@Test
	public void testInitialLowercase() {
		assertEquals("aA", Util.initialLowercase("AA"));
	}

	@Test
	public void testToPercent() {
		System.out.println(Util.toPercent(55555, 12));
		System.out.println(Util.toPercent(5.5555,1));
		System.out.println(Util.toPercent(5.00000001, 3));
		System.out.println(Util.toPercent(5.001, 0));
		System.out.println(Util.toPercent(5, 2));
		System.out.println(Util.toPercent(5, 3));
	}

	@Test
	public void testJoin() {
		List<String> list=new ArrayList<String>();
		System.out.println(Util.join(list, ","));
		list.add("aaaa");
		System.out.println(Util.join(list, ","));
		list.add("b");
		System.out.println(Util.join(list, ","));
	}

	@Test
	public void testLong2Ip() {
		System.out.println(Util.long2Ip(0));
		System.out.println(Util.long2Ip(65536));
		System.out.println(Util.long2Ip(111111111));
	}

}


