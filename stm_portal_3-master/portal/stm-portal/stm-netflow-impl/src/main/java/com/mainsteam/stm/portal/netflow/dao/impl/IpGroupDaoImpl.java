package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.IpGroupBo;
import com.mainsteam.stm.portal.netflow.dao.IIpGroupDao;
import com.mainsteam.stm.portal.netflow.po.IpGroupPo;
import com.mainsteam.stm.util.IPUtil;
import com.mainsteam.stm.util.Nets;

public class IpGroupDaoImpl extends BaseDao<IpGroupPo> implements IIpGroupDao {

	public IpGroupDaoImpl(SqlSessionTemplate session) {
		super(session, IIpGroupDao.class.getName());
	}

	@Override
	public int save(String name, String ips, String description) {
		List<Integer> queryIds = super.getSession()
				.selectList("getIpGroupById");
		Map<Integer, Object> ids = new HashMap<>();
		for (int i = 1; i <= 63; i++) {
			ids.put(i, null);
		}
		if (queryIds != null) {
			for (Integer id : queryIds) {
				ids.remove(id);
			}
		}
		Set<Integer> idsSet = ids.keySet();
		if (idsSet != null && idsSet.size() > 0) {
			Iterator<Integer> idsIterator = idsSet.iterator();
			Map<String, Object> map = new HashMap<>();
			if (idsIterator.hasNext()) {
				int id = idsIterator.next();
				map.put("id", id);
				map.put("name", name);
				map.put("description", description);
				map.put("ips", ips);
				super.getSession().insert("saveIpGroup", map);
				map.clear();
				map.put("groupId", id);
				super.getSession().insert("saveIpGroupByAid", map);
				String[] ipsArray = ips.split(",");
				if (ipsArray != null) {
					for (String ipStr : ipsArray) {
						String startIp = null;
						String endIp = null;
						String ip = null;
						String mask = null;
						if (ipStr != null && !"".equals(ipStr.trim())) {
							String[] ipD = ipStr.split("-");
							String[] tmp = ipStr.split("/");
							String[] mp = ipStr.split(":");
							if (ipD.length == 2) {
								startIp = ipD[0];
								endIp = ipD[1];
							} else if (tmp.length > 1) {
								Nets nets = IPUtil.getEndIP(tmp[0],
										Integer.parseInt(tmp[1]));
								startIp = nets.getStartIP();
								endIp = nets.getEndIP();
								ip = tmp[0];
								mask = nets.getNetMask();
							} else if (mp.length == 2) {
								Nets nets = IPUtil.getEndIP(mp[0], mp[1]);
								startIp = nets.getStartIP();
								endIp = nets.getEndIP();
								ip = mp[0];
								mask = mp[1];
							} else {
								startIp = ipStr;
							}
						}
						Map<String, Object> dataMap = new HashMap<>();
						dataMap.put("startIp", startIp);
						dataMap.put("endIp", endIp);
						dataMap.put("ip", ip);
						dataMap.put("mask", mask);
						dataMap.put("groupId", id);
						super.getSession().insert("saveIpGroupByIp", dataMap);
					}
				}
			}
		}
		return 1;
	}

	@Override
	public List<IpGroupBo> list(Page<IpGroupBo, IpGroupBo> page) {
		return super.getSession().selectList("query_ip_group", page);
	}

	@Override
	public int del(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		super.getSession().delete("del_ip_group_ByAid", map);
		super.getSession().delete("del_ip_group_iprange", map);
		return super.getSession().delete("del_ip_group", map);
	}

	@Override
	public IpGroupBo get(int id) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		return super.getSession().selectOne("get_ip_group", map);
	}

	@Override
	public int getCount(String name, Integer id) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("id", id);
		return super.getSession().selectOne("ip_group_getCount", map);
	}
}
