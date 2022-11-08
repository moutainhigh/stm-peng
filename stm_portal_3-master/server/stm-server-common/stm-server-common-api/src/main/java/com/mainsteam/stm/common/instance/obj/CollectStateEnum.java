package com.mainsteam.stm.common.instance.obj;

/**
 * Resource collecting state, depends on capacity service
 * Created by Xiaopf on 2016/6/13.
 */
public enum CollectStateEnum {
    COLLECTIBLE(1),
    UNCOLLECTIBLE(0);

    private int stateValue;

    private CollectStateEnum(int value){
        this.stateValue = value;
    }

    public int getStateValue() {
        return this.stateValue;
    }


}
