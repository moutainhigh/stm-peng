package com.mainsteam.stm.plugin.fusioncompute.utils;

/**
 * SET GET RESOURCE NODE PARENT CLASS
 * 
 * @author yuanlb
 * @2016年1月14日 上午11:42:35
 */
public class FusionComputeSubResourceNode {
    private String resourceID, uuid, name;

    public FusionComputeSubResourceNode(String resourceID, String uuid,
            String name) {
        super();
        this.resourceID = resourceID;
        this.uuid = uuid;
        this.name = name;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FCSubResourceNode [resourceID=" + resourceID + ", uuid=" + uuid
                + ", name=" + name + "]";
    }
}
