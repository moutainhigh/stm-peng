package com.mainsteam.stm.plugin.resin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ResinJmxUtil {
	public static Map<String, String> getAttributes(MBeanServerConnection conn, ObjectName objName, String... attrNames) 
			throws InstanceNotFoundException, ReflectionException, IOException {
		AttributeList list = conn.getAttributes(objName, attrNames);
		Map<String, String> result = new HashMap<String, String>();
		for(Object attrObj : list) {
			Attribute attr = (Attribute)attrObj;
			result.put(attr.getName(), attr.getValue().toString());
		}
		return result;
	}
	
	public static String attrMapsToString(List<Map<String, String>> maps, String[] keys) {
		if(maps.size() == 0)
			return "";
		StringBuilder result = new StringBuilder();
		for(String key : keys) {
			result.append(key + "\t");
		}
		result.deleteCharAt(result.length()-1);
		result.append("\n");
		
		for(Map<String, String> row : maps) {
			for(String key : keys) {
				result.append(row.get(key)+"\t");
			}
			result.deleteCharAt(result.length()-1);
			result.append("\n");
		}
		result.deleteCharAt(result.length()-1);
		
		return result.toString();
	}

}
