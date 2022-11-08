package com.mainsteam.stm.state.obj;

import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

/**
 * Created by Xiaopf on 2016/6/13.
 */
public class ResourceInstanceState {

    private InstanceStateEnum instanceStateEnum;

    private CollectStateEnum collectStateEnum;

    public ResourceInstanceState(){}

    public ResourceInstanceState(InstanceStateEnum instanceStateEnum, CollectStateEnum collectStateEnum) {
        this.instanceStateEnum = instanceStateEnum;
        this.collectStateEnum = collectStateEnum;
    }

    public InstanceStateEnum getInstanceStateEnum() {
        return instanceStateEnum;
    }

    public void setInstanceStateEnum(InstanceStateEnum instanceStateEnum) {
        this.instanceStateEnum = instanceStateEnum;
    }

    public CollectStateEnum getCollectStateEnum() {
        return collectStateEnum;
    }

    public void setCollectStateEnum(CollectStateEnum collectStateEnum) {
        this.collectStateEnum = collectStateEnum;
    }
}
