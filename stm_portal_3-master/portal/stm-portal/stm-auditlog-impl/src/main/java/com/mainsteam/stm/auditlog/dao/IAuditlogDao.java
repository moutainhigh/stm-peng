package com.mainsteam.stm.auditlog.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.auditlog.bo.AuditlogQueryBo;
import com.mainsteam.stm.auditlog.bo.AuditlogRuleBo;

public interface IAuditlogDao {
	int insert(AuditlogBo auditlogBo);
	int del(long id);
	int batchDel(long[] ids);
	AuditlogBo get(long id);
	List<AuditlogBo> pageSelect(Page<AuditlogBo, AuditlogQueryBo> page);
	List<AuditlogBo> selectAllList(AuditlogQueryBo condition);
	int deleteSelect(long[] ids);
	int deleteAll();
	List<AuditlogBo> selectBuList(Page<AuditlogBo, AuditlogQueryBo> page);
	List<AuditlogQueryBo> queryLastTime();
	int queryBuCount(AuditlogBo auditlogBo);
	int insertBuAuditlog(List<AuditlogBo> auditlogBo);
	List<AuditlogBo> queryBulist(Page<AuditlogBo, AuditlogQueryBo> page);
	int updateAuditlog(AuditlogBo auditlogBo);
	int insertBuAuditlogRule(AuditlogRuleBo auditlogRuleBo);
	List<AuditlogRuleBo> selectAuditlogRule();
	int updateAuditlogRule(AuditlogRuleBo auditlogRuleBo);
}
