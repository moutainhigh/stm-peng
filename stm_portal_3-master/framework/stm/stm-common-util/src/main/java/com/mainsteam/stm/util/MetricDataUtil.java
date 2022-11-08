package com.mainsteam.stm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public class MetricDataUtil {
	
	/** 将使用TableResultSetConvert转换的指标的值转换为二维数组 */
	public static List<Map<String, String>> parseTableResultSet(String[] metricData) {
		List<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
		List<String> array2D = JSONArray.parseArray(metricData[0], String.class);
		if(array2D.size() > 1) {
			JSONArray headArray = JSONArray.parseArray(array2D.get(0));
			int headSize = headArray.size();
			for(int i = 1; i < array2D.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONArray bodyArray = JSONArray.parseArray(array2D.get(i));
				int bodySize = bodyArray.size();
				for(int j = 0; j < headSize; j++) {
					if(bodySize >= j + 1) {
						String bodyString = bodyArray.getString(j) == null 
								? "" : bodyArray.getString(j);
						map.put(headArray.getString(j), bodyString);
					} else {
						map.put(headArray.getString(j), "");
					}
				}
				resultList.add(map);
			}
		}
		return resultList;
	}
	
	/** 将使用TableResultSetConvert转换的指标的值转换为二维数组 */
	public static String[][] parseTableResultSet2Array2D(String[] metricData) {
		List<String> array2D = JSONArray.parseArray(metricData[0], String.class);
		String[][] resultArray = null;
		if (array2D.size() > 1) {
			JSONArray headArray = JSONArray.parseArray(array2D.get(0));
			int headSize = headArray.size();
			resultArray = new String[array2D.size()][headSize];
			for (int i = 0; i < array2D.size(); i++) {
				JSONArray bodyArray = JSONArray.parseArray(array2D.get(i));
				int bodySize = bodyArray.size();
				for (int j = 0; j < headSize; j++) {
					if (bodySize >= j + 1) {
						String bodyString = bodyArray.getString(j) == null 
								? "" : bodyArray.getString(j);
						resultArray[i][j] = bodyString;
					} else {
						resultArray[i][j] = "";
					}
				}
			}
		}
		return resultArray;
	}
	
	@Deprecated
	public static List<Map<String, String>> parseTableResultSet4Branch(String[] metricData) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		if (metricData.length > 1) {
			String[] columnName = metricData[0].split(",", -1);
			for (int i = 1; i < metricData.length; i++) {
				Map<String, String> map = new HashMap<String, String>();
				String[] columnValue = metricData[i].split(",", -1);
				for (int j = 0; j < columnName.length; j++) {
					map.put(columnName[j], columnValue[j]);
				}
				resultList.add(map);
			}
		}
		return resultList;
	}
	
}
