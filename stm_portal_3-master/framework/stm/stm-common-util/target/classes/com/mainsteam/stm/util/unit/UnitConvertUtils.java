package com.mainsteam.stm.util.unit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class UnitConvertUtils {
	 static final Map<String,ConvertCmd> cmdMap=new HashMap<>();
	 
	static {
		cmdMap.put("kbps",new ConvertCmd_Kbps());
		cmdMap.put("bps",new ConvertCmd_bps());
	 }
	 
	public static UnitResult convert(String value,String unit){
		if(StringUtils.isEmpty(unit)){
			return new UnitResult(value, unit);
		}
		ConvertCmd cmd=cmdMap.get(unit.toLowerCase());
		if(cmd!=null){
			return cmd.convert(value, unit);
		}
		return new UnitResult(value, unit);
	}

}
