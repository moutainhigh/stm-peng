package com.mainsteam.stm.common.sync;

import com.mainsteam.stm.common.sync.dao.DataSyncDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dataSyncService")
public class DataSyncServiceImpl implements DataSyncService {

    @Autowired
    @Qualifier("dataSyncDAO")
    private DataSyncDAO dataSyncDAO;

    @Override
    public void save(DataSyncPO dataSyncPO) {
        dataSyncDAO.save(dataSyncPO);
    }

    @Override
    public void delete(long id) {
        dataSyncDAO.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        dataSyncDAO.delete(ids);
    }

    @Override
    public DataSyncPO selectOne(DataSyncTypeEnum type) {
        return dataSyncDAO.catchOne(type);
    }

    @Override
    public List<DataSyncPO> selectBatch(DataSyncTypeEnum typeEnum) {
        return dataSyncDAO.catchList(typeEnum);
    }

    @Override
    public List<DataSyncPO> selectBatch(DataSyncTypeEnum typeEnum, int start, int limit) {
        return dataSyncDAO.catchList(typeEnum, start, limit);
    }

    @Override
    public List<DataSyncPO> selectBatch(List<DataSyncTypeEnum> typeEnums) {
        return null;
    }
}
