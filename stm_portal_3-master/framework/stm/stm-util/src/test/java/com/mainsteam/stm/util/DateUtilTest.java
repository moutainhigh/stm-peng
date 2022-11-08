package com.mainsteam.stm.util;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * <li>文件名称: DateUtilTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月25日
 * @author   ziwenwen
 */
public class DateUtilTest {
	private Date date;
	private Calendar calendar;
	
	@Before
	public void initCalendar(){
		date=new Date();
		calendar=DateUtil.getCalendar(date);
	}
	
	@Test()
	public void testReset() {
		System.out.println(DateUtil.format(DateUtil.reset(calendar, 0,0,0)));
	}

	@Test
	public void testFormatWithSimple() {
		System.out.println(DateUtil.formatWithSimple(date, DateUtil.format_yyyycnMMcnddcn));
	}
}


