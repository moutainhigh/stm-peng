package com.mainsteam.stm.portal.netflow.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;
import com.mainsteam.stm.portal.netflow.bo.ProtocolBo;

public interface IApplicationManagerApi {

	List<ProtocolBo> getAllProtocols();

	int save(String name, int protocolId, String ports, String ips);

	Page<ApplicationBo, ApplicationBo> list(
			Page<ApplicationBo, ApplicationBo> page);

	int del(int[] ids);

	ApplicationBo get(int id);

	int update(int id, String name, int protocolId, String ports, String ips);

	boolean restoreDefault();

	int getCount(String name, Integer id);
}
