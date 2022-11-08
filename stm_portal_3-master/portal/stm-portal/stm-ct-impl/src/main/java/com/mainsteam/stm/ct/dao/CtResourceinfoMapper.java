package com.mainsteam.stm.ct.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ct.bo.MsCtResourceinfo;

public interface CtResourceinfoMapper {
	List<MsCtResourceinfo> selectById(@Param("id")String id);

	void addInfo(MsCtResourceinfo resourceinfo);
}
