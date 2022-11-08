package com.mainsteam.stm.ct.api;

import java.util.List;
import com.mainsteam.stm.ct.bo.MsCtResourceinfo;

public interface IResourceinfoService {
	
	List<MsCtResourceinfo> selectById(String id);

	void addInfo(MsCtResourceinfo resourceinfo);
}
