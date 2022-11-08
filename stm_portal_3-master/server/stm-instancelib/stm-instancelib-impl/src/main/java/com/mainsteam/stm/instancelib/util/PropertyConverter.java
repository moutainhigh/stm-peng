/**
 * 
 */
package com.mainsteam.stm.instancelib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;

/**
 * @author ziw
 * 
 */
public class PropertyConverter {

	/**
	 * 
	 */
	public PropertyConverter() {
	}

	public static Map<Long, List<? extends InstanceProp>>[] convertProps(
			List<PropDO> propDos) {
		Map<Long, List<? extends InstanceProp>> modulePropsMap = new HashMap<>();
		Map<Long, List<? extends InstanceProp>> discoveryPropsMap = new HashMap<>();

		Map<Long, Map<String, List<PropDO>>> modulePropsDoMap = new HashMap<>();
		Map<Long, Map<String, List<PropDO>>> discoveryPropsDoMap = new HashMap<>();
		for (PropDO tdo : propDos) {
			Map<Long, Map<String, List<PropDO>>> map = null;
			if (PropTypeEnum.MODULE.name().equals(tdo.getPropType())) {
				map = modulePropsDoMap;
			} else {
				map = discoveryPropsDoMap;
			}

			Map<String, List<PropDO>> instancePropMap = null;
			Long instanceId = tdo.getInstanceId();
			if (map.containsKey(instanceId)) {
				instancePropMap = map.get(instanceId);
			} else {
				instancePropMap = new HashMap<>(50);
				map.put(instanceId, instancePropMap);
			}
			if (instancePropMap.containsKey(tdo.getPropKey())) {
				instancePropMap.get(tdo.getPropKey()).add(tdo);
			} else {
				List<PropDO> list = new ArrayList<>();
				list.add(tdo);
				instancePropMap.put(tdo.getPropKey(), list);
			}
		}
		for (Iterator<Entry<Long, Map<String, List<PropDO>>>> iterator = modulePropsDoMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<Long, Map<String, List<PropDO>>> instanceEntry = iterator
					.next();
			Map<String, List<PropDO>> propsMap = instanceEntry.getValue();
			long instanceId = instanceEntry.getKey();

			List<ModuleProp> defs = new ArrayList<>(propsMap.size());
			for (Iterator<Entry<String, List<PropDO>>> propIterator = propsMap
					.entrySet().iterator(); propIterator.hasNext();) {
				Entry<String, List<PropDO>> propEntry = propIterator.next();
				List<PropDO> props = propEntry.getValue();
				String key = propEntry.getKey();

				String[] values = new String[props.size()];
				for (int i = 0; i < values.length; i++) {
					values[i] = props.get(i).getPropValue();
				}
				ModuleProp def = new ModuleProp();
				def.setInstanceId(instanceId);
				def.setKey(key);
				def.setValues(values);
				defs.add(def);
			}
			modulePropsMap.put(instanceEntry.getKey(), defs);
		}

		for (Iterator<Entry<Long, Map<String, List<PropDO>>>> iterator = discoveryPropsDoMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<Long, Map<String, List<PropDO>>> instanceEntry = iterator
					.next();
			Map<String, List<PropDO>> propsMap = instanceEntry.getValue();
			long instanceId = instanceEntry.getKey();

			List<DiscoverProp> defs = new ArrayList<>(propsMap.size());
			for (Iterator<Entry<String, List<PropDO>>> propIterator = propsMap
					.entrySet().iterator(); propIterator.hasNext();) {
				Entry<String, List<PropDO>> propEntry = propIterator.next();
				List<PropDO> props = propEntry.getValue();
				String key = propEntry.getKey();

				String[] values = new String[props.size()];
				for (int i = 0; i < values.length; i++) {
					values[i] = props.get(i).getPropValue();
				}
				DiscoverProp def = new DiscoverProp();
				def.setInstanceId(instanceId);
				def.setKey(key);
				def.setValues(values);
				defs.add(def);
			}
			discoveryPropsMap.put(instanceEntry.getKey(), defs);
		}
		
		@SuppressWarnings("unchecked")
		Map<Long, List<? extends InstanceProp>>[] result = new Map[2];
		result[0] = modulePropsMap;
		result[1] = discoveryPropsMap;
		return result;
	}
}
