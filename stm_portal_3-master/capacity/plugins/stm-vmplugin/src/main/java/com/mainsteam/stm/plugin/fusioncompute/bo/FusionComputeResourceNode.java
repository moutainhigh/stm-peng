package com.mainsteam.stm.plugin.fusioncompute.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree structure design class
 * 
 * @author yuanlb
 * @2016年1月14日 上午10:57:46
 */
public class FusionComputeResourceNode {

    private String resourceId, uuid, ip, name, type;

    private List<FusionComputeResourceNode> childTrees;

    private List<FusionComputeSubResourceNode> subResources;

    public FusionComputeResourceNode(String resourceID, String uuid, String ip,
            String name, String type) {
        super();
        this.resourceId = resourceID;
        this.uuid = uuid;
        this.ip = ip;
        this.name = name;
        this.type = type;
        this.childTrees = new ArrayList<FusionComputeResourceNode>();
        this.subResources = new ArrayList<FusionComputeSubResourceNode>();
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FusionComputeResourceNode> getChildTrees() {
        return childTrees;
    }

    public void setChildTrees(List<FusionComputeResourceNode> childTrees) {
        this.childTrees = childTrees;
    }

    public List<FusionComputeSubResourceNode> getSubResources() {
        return subResources;
    }

    public void setSubResources(List<FusionComputeSubResourceNode> subResources) {
        this.subResources = subResources;
    }

    @Override
    public String toString() {
        return "FusionComputeResourceNode [resourceId=" + resourceId
                + ", uuid=" + uuid + ", ip=" + ip + ", name=" + name
                + ", childTrees=" + childTrees + ", subResources="
                + subResources + "]";
    }
}
