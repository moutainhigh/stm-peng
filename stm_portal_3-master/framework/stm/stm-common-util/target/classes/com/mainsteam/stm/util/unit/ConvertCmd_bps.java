package com.mainsteam.stm.util.unit;

final class ConvertCmd_bps implements ConvertCmd {
	ConvertCmd_Kbps kbps=new ConvertCmd_Kbps();
	@Override
	public UnitResult convert(String value, String unit) {
		if(value.contains(".")){
			value=value.substring(0,value.indexOf("."));
		}
		int len=value.length();
		if(len>3){
			return kbps.convert(value.substring(0,len-3), unit);
		}
		return  new UnitResult(value,unit);
	}

}
