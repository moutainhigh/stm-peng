package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;

public interface RecycleService {

	JSONArray list();

	void recover(Long[] ids);

	void del(Long[] ids);
	
}
