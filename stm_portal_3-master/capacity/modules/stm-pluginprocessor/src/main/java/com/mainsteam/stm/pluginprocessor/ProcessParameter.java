/**
 * 
 */
package com.mainsteam.stm.pluginprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziw
 * 
 */
public class ProcessParameter {

	private Map<String, List<ParameterValue>> parametersMap;

	public ProcessParameter() {
		parametersMap = new HashMap<>();
	}

	public void addParameter(ParameterValue value) {
		if (null == value.getKey()) {
			value.setKey("");
		}
		if (parametersMap.containsKey(value.getKey())) {
			parametersMap.get(value.getKey()).add((value));
		} else {
			List<ParameterValue> list = new ArrayList<ParameterValue>();
			list.add(value);
			this.parametersMap.put(value.getKey(), list);
		}
	}

	public List<ParameterValue> getParameterValuesByKey(String key) {
		return this.parametersMap.get(key);
	}

	public ParameterValue getParameterValueByKey(String key) {
		List<ParameterValue> array = this.parametersMap.get(key);
		if(array == null)
			return null;
		if (array.size() > 0) {
			return array.get(0);
		}
		return null;
	}

	public ParameterValue[] listParameterValues() {
		List<ParameterValue> valueAll = new ArrayList<ParameterValue>();
		for (List<ParameterValue> valueList : parametersMap.values()) {
			valueAll.addAll(valueList);
		}
		return valueAll.toArray(new ParameterValue[valueAll.size()]);
	}

	public List<ParameterValue> getParameterValuesByEmptyKey() {
		return this.parametersMap.get("");
	}
}
