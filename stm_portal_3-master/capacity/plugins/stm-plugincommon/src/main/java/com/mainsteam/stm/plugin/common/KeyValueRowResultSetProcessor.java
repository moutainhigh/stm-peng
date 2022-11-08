/**
 * 
 */
package com.mainsteam.stm.plugin.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/** 
 * 处理行为key，value类型的result数据，保留指定key的值。<br>
 * 格式为:<br>
 * key1,value11,key2,value21,key3,value31<br>
 * key1,value21,key2,value22,key3,value32<br>
 * 
 * @author 作者：ziw
 * @date 创建时间：2017年12月5日 下午3:26:36
 * @version 1.0
 */
/** 
 */
public class KeyValueRowResultSetProcessor implements PluginResultSetProcessor {

	/**
	 * 
	 */
	public KeyValueRowResultSetProcessor() {
	}

	@Override	
	public void process(ResultSet set, ProcessParameter parameter, PluginSessionContext context)
			throws PluginSessionRunException {
		ParameterValue isRemoveKey = parameter.getParameterValueByKey("removeKey");
		List<ParameterValue> keys = parameter.getParameterValuesByKey("searchKey");//需要保留的key的值
		Map<String,Integer> keysMap = new HashMap<>(keys.size());
		int index = 0;
		for (ParameterValue keyElement : keys) {
			keysMap.put(keyElement.getValue(), Integer.valueOf(index++));
		}
		/**
		 * 判定是否保留key到结果集。
		 */
		boolean leaveKey = !(isRemoveKey!=null && "true".equalsIgnoreCase(isRemoveKey.getValue()));
		
		for (int i = 0; i < set.getRowLength(); i++) {
			String[] row = set.getRow(i);
			String[] valueRow = null;
			if(leaveKey) {
				valueRow = new String[index*2];
			}else {
				valueRow = new String[index];
			}
			int count = 0;
			for (int j = 1; j < row.length; j+=2) {
				String key = row[j-1];
				if(keysMap.containsKey(key)) {
					String value = row[j];
					if(leaveKey) {
						valueRow[keysMap.get(key).intValue()] = key;
						valueRow[keysMap.get(key).intValue()+1] = value;
					}else {
						valueRow[keysMap.get(key).intValue()] = value;
					}
					count++;
				}
			}
			if(count>0) {
				set.replaceRow(i, valueRow);
			}else {
				set.removeRow(i);
			}
		}
	}
}
