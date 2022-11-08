package com.mainsteam.stm.topo.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

public class TopoHelper {
	private static Logger logger = Logger.getLogger(TopoHelper.class);
	private static Map<String,Long> timelogs=new HashMap<String,Long>();
	public static ILoginUser getCurrentUser(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return (ILoginUser) request.getSession().getAttribute(ILoginUser.SESSION_LOGIN_USER);
	}
	public static void mixin(JSONObject src,JSONObject des){
		for(Map.Entry<String,Object> ele:des.entrySet()){
			src.put(ele.getKey(), ele.getValue());
		}
	}
	public static long[] toBaseLong(Long[] src){
		long[] retn = new long[src.length];
		for(int i=0;i<src.length;i++){
			retn[i]=src[i];
		}
		return retn;
	}
	public static String formatStringVal(Object src,String defaut,String suffix){
		if(src==null || src.toString().equals("null") || src.toString().equals("")){
			return defaut;
		}else{
			return src.toString()+suffix;
		}
	}
	public static Map<String, Integer> idToMap(Long[] ids) {
		Map<String,Integer> map = new HashMap<String,Integer>(Math.max(ids.length,1));
		for(Long id:ids){
			map.put(id.toString(),1);
		}
		return map;
	}
	public static Long[] toLongArray(Integer[] dbIds) {
		Long[] ids = new Long[dbIds.length];
		for(int i=0;i<dbIds.length;i++){
			ids[i]=new Long(dbIds[i]);
		}
		return ids;
	}
	public static void beginLog(String name){
		timelogs.put(name, System.currentTimeMillis());
	}
	public static void endLog(String name){
		if(timelogs.containsKey(name)){
			long begintime=timelogs.get(name);
			logger.error(String.format("%s total take %sms",name,(System.currentTimeMillis()-begintime)));
			timelogs.remove(name);
		}
	}
}
