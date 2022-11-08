package com.mainsteam.stm.dcimmanage.dao;

import com.mainsteam.stm.dcimmanage.po.DcimResourcePo;
import com.mainsteam.stm.dcimmanage.vo.DcimResourceVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

import java.util.List;

public interface ISyncPropertyDao {

    public List<DcimResourceVo> getAll();

    public int batchInsert(List<DcimResourcePo> list);

    public void getDcimPage(Page<DcimResourceVo, DcimResourceVo> dcimPage);

    public List countRelationByDcim(Long dcimId);

    public int updateRelation(Long dcimId, Long stmId);

    public int insertRelation(Long dcimId, Long stmId);

}
