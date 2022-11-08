package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;
import com.mainsteam.stm.portal.netflow.bo.ProtocolBo;

public interface IApplicationManagerDao {

	List<ProtocolBo> getAllProtocols();

	/**
	 * 
	 * @param name
	 * @return 返回插入主键 id
	 */
	int saveApplicationGroup(String name, String ports, String ips,
			int protocolId);

	/**
	 * 
	 * @param name
	 * @param protocolId
	 * @param startIp
	 * @param endIp
	 * @param startPort
	 * @param endPort
	 * @return 返回插入主键 id
	 */
	int saveApplication(String name, int protocolId, String startIp,
			String endIp, int startPort, Integer endPort);

	int saveApplicationGroupMap(int groupId, int appId);

	Page<ApplicationBo, ApplicationBo> list(
			Page<ApplicationBo, ApplicationBo> page);

	int delApplicationGroupMap(int[] groupId);

	int delApplication(int[] groupId);

	int delApplicationGroup(int[] groupId);

	ApplicationBo get(int id);

	boolean restoreDefault();

	int getCount(String name, Integer id);
}
