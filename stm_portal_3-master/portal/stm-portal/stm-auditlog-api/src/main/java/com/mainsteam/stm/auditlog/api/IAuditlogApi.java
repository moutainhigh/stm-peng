package com.mainsteam.stm.auditlog.api;


import java.util.List;


import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.auditlog.bo.AuditlogQueryBo;
import com.mainsteam.stm.auditlog.bo.AuditlogRuleBo;

public interface IAuditlogApi {
	
	/**
	* @Title: insert
	* @Description: 添加审计日志
	* @param auditlogBo
	* @return  int
	* @throws
	*/
	int insert(AuditlogBo auditlogBo);
	int del(long id);
	int batchDel(long[] ids);
	AuditlogBo get(long id);
	/**
	 * 日志条件查询分页显示
	 * @return
	 */
	List<AuditlogBo> pageSelect(Page<AuditlogBo, AuditlogQueryBo> page);
	
	List<AuditlogBo> selectAllList(AuditlogQueryBo condition);
	
	int deleteSelect(long[] ids);
	
	int deleteAll();
	
	List<AuditlogBo> selectBuList(Page<AuditlogBo, AuditlogQueryBo> page);
	
	List<AuditlogQueryBo> queryLastTime();
	
	int queryBuCount(AuditlogBo auditlogBo);
	
	void insertBuAuditlog();
	
	List<AuditlogBo> queryBuList(Page<AuditlogBo, AuditlogQueryBo> page);
	
	int updateAuditlog(AuditlogBo auditlogBo);
	
	int insertBuAuditlogRule(AuditlogRuleBo auditlogRuleBo);
	
	AuditlogRuleBo selectAuditlogRule();
	
	int updateAuditlogRule(AuditlogRuleBo auditlogRuleBo);
	
	int setLogByWebservice(String log);
	
}
