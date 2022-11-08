package com.mainsteam.stm.topo.api;

import java.awt.image.BufferedImage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.HLJNode;

public interface HLJService {

	BufferedImage getBadge(String type, Integer size, Integer badge);

	JSONArray refreshState(Integer key);

	void relateInstance(HLJNode node);

	JSONObject mapInfo(Integer mapId,Integer level);

	JSONObject getRelateInfo(Long id);

}
