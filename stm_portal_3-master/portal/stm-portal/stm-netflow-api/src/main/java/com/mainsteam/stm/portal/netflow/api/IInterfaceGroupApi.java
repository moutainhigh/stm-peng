package com.mainsteam.stm.portal.netflow.api;

import java.util.List;

import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupBo;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupPageBo;
import com.mainsteam.stm.portal.netflow.bo.zTreeBo;

public interface IInterfaceGroupApi {

	List<zTreeBo> queryDeviceInterface(String name, int[] notIds, int[] ids);

	int save(String name, String interfaceIds, String description);

	List<InterfaceGroupBo> list();

	InterfaceGroupPageBo pageSelect(long startRecord, long pageSize,
			String interfaceGroupName, String order);

	int del(int[] ids);

	InterfaceGroupBo get(int id);

	int update(int id, String name, String interfaceIds, String description);

	int getCount(String name, Integer id);
}
