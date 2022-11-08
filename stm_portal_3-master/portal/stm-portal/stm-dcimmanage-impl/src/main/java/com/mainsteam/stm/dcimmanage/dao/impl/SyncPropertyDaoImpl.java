package com.mainsteam.stm.dcimmanage.dao.impl;

import com.mainsteam.stm.dcimmanage.dao.ISyncPropertyDao;
import com.mainsteam.stm.dcimmanage.po.DcimResourcePo;
import com.mainsteam.stm.dcimmanage.vo.DcimResourceVo;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SyncPropertyDaoImpl extends BaseDao<DcimResourcePo> implements ISyncPropertyDao {


    public SyncPropertyDaoImpl(SqlSessionTemplate session) {
        super(session, SyncPropertyDaoImpl.class.getName());
    }

    @Override
    public List<DcimResourceVo> getAll() {
        return getSession().selectList("getDcimList");
    }

    @Override
    public int batchInsert(List<DcimResourcePo> list) {
        return super.batchInsert(list);
    }

    @Override
    public void getDcimPage(Page<DcimResourceVo, DcimResourceVo> dcimPage) {
//        this.select("queryList", dcimPage);
        getSession().selectList("getDcimPage", dcimPage);
    }

    @Override
    public List countRelationByDcim(Long dcimId) {
        return getSession().selectList("getDcimList", dcimId);
    }

    @Override
    public int updateRelation(Long dcimId, Long stmId) {
        Map map = new HashMap();
        map.put("dcimId", dcimId);
        map.put("stmId", stmId);
        return getSession().update("updateRelation", map);
    }

    @Override
    public int insertRelation(Long dcimId, Long stmId) {
        Map map = new HashMap();
        map.put("dcimId", dcimId);
        map.put("stmId", stmId);
        return getSession().insert("insertRelation", map);
    }
}
