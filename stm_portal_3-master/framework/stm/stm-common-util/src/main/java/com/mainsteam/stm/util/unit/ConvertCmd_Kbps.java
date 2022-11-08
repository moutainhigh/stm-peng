package com.mainsteam.stm.util.unit;

final class ConvertCmd_Kbps implements ConvertCmd {
	
	@Override
	public UnitResult convert(String value, String unit) {
		if(value.contains(".")){
			value=value.substring(0,value.indexOf("."));
		}
		int len=value.length();
		if(len>7){
			return new UnitResult(value.substring(0,len-6),"Gbps");
		}
		if(len>6){
			String tmp=value.substring(0,len-5);
			tmp=tmp.substring(0,1)+"."+tmp.substring(1);
			return new UnitResult(tmp,"Gbps");
		}
		if(len>4){
			return new UnitResult(value.substring(0,len-3),"Mbps");
		}
		if(len>3){
			String tmp=value.substring(0,len-2);
			tmp=tmp.substring(0,1)+"."+tmp.substring(1);
			return new UnitResult(tmp,"Mbps");
		}
		return  new UnitResult(value,unit);
	}

}
