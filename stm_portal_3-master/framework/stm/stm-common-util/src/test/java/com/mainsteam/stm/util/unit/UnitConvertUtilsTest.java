package com.mainsteam.stm.util.unit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.util.unit.UnitConvertUtils;
import com.mainsteam.stm.util.unit.UnitResult;

public class UnitConvertUtilsTest {
	Logger logger=LoggerFactory.getLogger(UnitConvertUtilsTest.class);
	
	@Test
	public void convertbps(){
		UnitResult rst=UnitConvertUtils.convert("12345667", "bps");
		logger.info(JSON.toJSONString(rst));
	}
	
	@Test
	public void converbps2(){
		UnitResult rst=UnitConvertUtils.convert("1234567", "bps");
		logger.info(JSON.toJSONString(rst));
	}
	
	@Test
	public void convertkbps(){
		UnitResult rst=UnitConvertUtils.convert("12345667", "kbps");
		logger.info(JSON.toJSONString(rst));
	}
	@Test
	public void convertkbps2(){
		UnitResult rst=UnitConvertUtils.convert("1234567", "kbps");
		logger.info(JSON.toJSONString(rst));
	}
}
