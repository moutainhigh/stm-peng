package com.mainsteam.stm.webService.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.mainsteam.stm.webService.resource.BeanMapConvertor.MapEntry;

public class BeanMapAdapter extends XmlAdapter<BeanMapConvertor, Map<Long,List<ThresholdSyncBean>>>{

	@Override
	public Map<Long, List<ThresholdSyncBean>> unmarshal(BeanMapConvertor convertor)
			throws Exception {
		List<MapEntry> entries = convertor.getEntries();
		if (entries != null && entries.size() > 0) {
			Map<Long, List<ThresholdSyncBean>> map = new HashMap<Long, List<ThresholdSyncBean>>();
			for (MapEntry mapEntry : entries) {
				map.put(mapEntry.getKey(), mapEntry.getValue());
			}
			return map;
		}
		return null;
	}

	@Override
	public BeanMapConvertor marshal(Map<Long, List<ThresholdSyncBean>> map) throws Exception {
		// TODO Auto-generated method stub
		BeanMapConvertor convertor = new BeanMapConvertor();
		for (Map.Entry<Long, List<ThresholdSyncBean>> entry : map.entrySet()) {
			convertor.addEntry(new BeanMapConvertor.MapEntry(entry));
		}
		return convertor;
	}

}
