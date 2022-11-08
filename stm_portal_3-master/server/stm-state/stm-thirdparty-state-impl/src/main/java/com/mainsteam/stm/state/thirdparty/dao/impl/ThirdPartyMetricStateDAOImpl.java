package com.mainsteam.stm.state.thirdparty.dao.impl;

import com.mainsteam.stm.state.thirdparty.dao.ThirdPartyMetricStateDAO;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by Xiaopf on 6/15/2016.
 */
public class ThirdPartyMetricStateDAOImpl implements ThirdPartyMetricStateDAO {

    private SqlSession session;

    public void setSession(SqlSession session) {
        this.session = session;
    }

    @Override
    public void save(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        session.insert("saveStateData", thirdPartyMetricStateData);
    }

    @Override
    public void updateIfExistsOrAdd(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        session.update("updateIfExistsOrAdd", thirdPartyMetricStateData);
    }

    @Override
    public void delete(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        session.delete("deleteStateData", thirdPartyMetricStateData);
    }

    @Override
    public ThirdPartyMetricStateData findOne(ThirdPartyMetricStateData thirdPartyMetricStateData) {

        return session.selectOne("findRecord", thirdPartyMetricStateData);
    }

    @Override
    public List<ThirdPartyMetricStateData> findList(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        return session.selectList("findRecord", thirdPartyMetricStateData);
    }
}
