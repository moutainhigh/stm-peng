package com.mainsteam.stm.portal.netflow.dao.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;
import com.mainsteam.stm.portal.netflow.bo.ProtocolBo;
import com.mainsteam.stm.portal.netflow.dao.IApplicationManagerDao;

@Repository("applicationManagerDao")
public class ApplicationManagerDaoImpl implements IApplicationManagerDao {

	@Resource(name = "portal_netflow_sqlSession")
	private SqlSessionTemplate session;

	@Resource(name = "stm_system_AuditlogSeq")
	private ISequence sequence;

	@Override
	public List<ProtocolBo> getAllProtocols() {
		return this.session.selectList("getAllProtocols");
	}

	@Override
	public int saveApplicationGroup(String name, String ports, String ips,
			int protocolId) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("ports", ports);
		map.put("ips", ips);
		map.put("protocolId", protocolId);
		this.session.insert("saveApplicationGroup", map);
		return Integer.parseInt((Long) map.get("id") + "");
	}

	@Override
	public int saveApplication(String name, int protocolId, String startIp,
			String endIp, int startPort, Integer endPort) {
		Map<String, Object> map = new HashMap<>();
		int id = 10000000 + (int) sequence.next();
		map.put("id", id);
		map.put("name", name);
		map.put("protocolId", protocolId);
		map.put("startIp", startIp);
		map.put("endIp", endIp);
		map.put("startPort", startPort);
		map.put("endPort", endPort);
		this.session.insert("saveApplication", map);
		return id;
	}

	@Override
	public int saveApplicationGroupMap(int groupId, int appId) {
		Map<String, Object> map = new HashMap<>();
		map.put("groupId", groupId);
		map.put("appId", appId);
		return this.session.insert("saveApplicationGroupMap", map);
	}

	@Override
	public Page<ApplicationBo, ApplicationBo> list(
			Page<ApplicationBo, ApplicationBo> page) {
		page.setDatas(this.session.<ApplicationBo> selectList(
				"queryApplicationGroup", page));
		return page;
	}

	@Override
	public int delApplicationGroupMap(int[] groupId) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", groupId);
		return this.session.delete("delApplicationGroupMap", map);
	}

	@Override
	public int delApplication(int[] groupId) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", groupId);
		return this.session.delete("delApplication", map);
	}

	@Override
	public int delApplicationGroup(int[] groupId) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", groupId);
		return this.session.delete("delApplicationGroup", map);
	}

	@Override
	public ApplicationBo get(int id) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		return this.session.selectOne("get_application_group", map);
	}

	@Override
	public boolean restoreDefault() {
		try {
			SqlSession sqlSession = this.session.getSqlSessionFactory()
					.openSession();
			Connection conn = sqlSession.getConnection();
			ScriptRunner runner = new ScriptRunner(conn);
			runner.setAutoCommit(false);
			runner.setErrorLogWriter(null);
			runner.setLogWriter(null);
			runner.runScript(Resources
					.getResourceAsReader("app_restoreDefault.sql"));
			runner.closeConnection();
			conn.close();
			sqlSession.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getCount(String name, Integer id) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("id", id);
		return this.session.selectOne("application_group_getCount", map);
	}
}
