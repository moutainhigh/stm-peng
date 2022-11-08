package com.mainsteam.stm.state.util;

import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.state.obj.InstanceStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by Xiaopf on 2017/1/20.
 * 可采集状态处理类
 */
public class CollectionStateUtils {

    private static final Log logger = LogFactory.getLog(CollectionStateUtils.class);

    private InstanceStateDataCacheUtil instanceStateDataCacheUtil;

    public CollectionStateUtils(InstanceStateDataCacheUtil instanceStateDataCacheUtil) {
        super();
        this.instanceStateDataCacheUtil = instanceStateDataCacheUtil;
    }

    public void saveCollectionStateUtils(long instanceID, Date collectionTime, CollectStateEnum collectStateEnum) {

        InstanceStateData preInstanceStateData = instanceStateDataCacheUtil.getInstanceState(instanceID);
        if(null != preInstanceStateData || (preInstanceStateData.getCollectStateEnum() != collectStateEnum)) {
            preInstanceStateData.setUpdateTime(new Date());
            preInstanceStateData.setCollectTime(collectionTime);
            preInstanceStateData.setCollectStateEnum(collectStateEnum);
            instanceStateDataCacheUtil.saveInstanceState(preInstanceStateData);
        }

    }

}
