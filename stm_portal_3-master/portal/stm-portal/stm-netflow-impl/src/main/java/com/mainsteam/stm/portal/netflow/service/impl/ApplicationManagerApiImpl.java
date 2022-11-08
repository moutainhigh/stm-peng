package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.IApplicationManagerApi;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;
import com.mainsteam.stm.portal.netflow.bo.ProtocolBo;
import com.mainsteam.stm.portal.netflow.dao.IApplicationManagerDao;

@Service("applicationManagerApi")
public class ApplicationManagerApiImpl implements IApplicationManagerApi {

	@Autowired
	private IApplicationManagerDao applicationManagerDao;

	@Override
	public List<ProtocolBo> getAllProtocols() {
		return this.applicationManagerDao.getAllProtocols();
	}

	@Override
	public int save(String name, int protocolId, String ports, String ips) {
		int groupId = this.applicationManagerDao.saveApplicationGroup(name,
				ports, ips, protocolId);
		String[] ipArray = "".equals(ips.trim()) ? null : ips.split(",");
		String[] portArray = ports.split(",");
		List<Integer> appIds = new ArrayList<>();
		if (portArray != null) {
			for (String port : portArray) {
				String[] portTmp = port.split("-");
				int startPort = -1;
				Integer endPort = null;
				if (portTmp.length == 2) {
					startPort = Integer.parseInt(portTmp[0]);
					endPort = Integer.parseInt(portTmp[1]);
				} else {
					startPort = Integer.parseInt(port);
					endPort = startPort;
				}
				if (ipArray != null) {
					for (String ip : ipArray) {
						String startIp = null;
						String endIp = null;
						String[] ipTmp = ip.split("-");
						if (ipTmp.length == 2) {
							startIp = ipTmp[0];
							endIp = ipTmp[1];
						} else {
							startIp = ip;
						}
						appIds.add(this.applicationManagerDao.saveApplication(
								name, protocolId, startIp, endIp, startPort,
								endPort));
					}
				} else {
					appIds.add(this.applicationManagerDao.saveApplication(name,
							protocolId, null, null, startPort, endPort));
				}
			}
			for (Integer appId : appIds) {
				this.applicationManagerDao.saveApplicationGroupMap(groupId,
						appId);
			}
		}
		return 1;
	}

	@Override
	public Page<ApplicationBo, ApplicationBo> list(
			Page<ApplicationBo, ApplicationBo> page) {
		return this.applicationManagerDao.list(page);
	}

	@Override
	public int del(int[] ids) {
		this.applicationManagerDao.delApplication(ids);
		this.applicationManagerDao.delApplicationGroup(ids);
		this.applicationManagerDao.delApplicationGroupMap(ids);
		return 1;
	}

	@Override
	public ApplicationBo get(int id) {
		return this.applicationManagerDao.get(id);
	}

	@Override
	public int update(int id, String name, int protocolId, String ports,
			String ips) {
		this.del(new int[] { id });
		return this.save(name, protocolId, ports, ips);
	}

	@Override
	public boolean restoreDefault() {
		return this.applicationManagerDao.restoreDefault();
	}

	@Override
	public int getCount(String name, Integer id) {
		return this.applicationManagerDao.getCount(name, id);
	}
}
