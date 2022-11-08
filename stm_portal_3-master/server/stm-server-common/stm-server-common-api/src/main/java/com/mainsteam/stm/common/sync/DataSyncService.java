package com.mainsteam.stm.common.sync;

import java.util.List;

public interface DataSyncService {

    void save(DataSyncPO dataSyncPO);

    void delete(long id);

    void delete(List<Long> ids);

    DataSyncPO selectOne(DataSyncTypeEnum type);

    List<DataSyncPO> selectBatch(DataSyncTypeEnum typeEnum);

    List<DataSyncPO> selectBatch(List<DataSyncTypeEnum> typeEnums);

    List<DataSyncPO> selectBatch(DataSyncTypeEnum typeEnum, int start, int limit);
}
