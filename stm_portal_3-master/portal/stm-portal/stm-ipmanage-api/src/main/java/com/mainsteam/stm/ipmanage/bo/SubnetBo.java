package com.mainsteam.stm.ipmanage.bo;

import java.util.List;

public class SubnetBo {
    private Integer ip1;
    private Integer ip2;
    private Integer ip3;
    private Integer ip4;
    private Integer mask1;
    private Integer mask2;
    private Integer mask3;
    private Integer mask4;
    private Character netClass;
    private String selectClass;
    private Integer subnetNum;
    private Integer nodeNum;
    private Integer subnetBit;
    private Boolean hasSubnets;
    private List<Segment> segments;
    private Integer node_id;


	public Integer getNode_id() {
		return node_id;
	}

	public void setNode_id(Integer node_id) {
		this.node_id = node_id;
	}

	public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public Boolean getHasSubnets() {
        return hasSubnets;
    }

    public void setHasSubnets(Boolean hasSubnets) {
        this.hasSubnets = hasSubnets;
    }

    public Integer getSubnetBit() {
        return subnetBit;
    }

    public void setSubnetBit(Integer subnetBit) {
        this.subnetBit = subnetBit;
    }

    public Integer getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(Integer nodeNum) {
        this.nodeNum = nodeNum;
    }

    public Integer getSubnetNum() {
        return subnetNum;
    }

    public void setSubnetNum(Integer subnetNum) {
        this.subnetNum = subnetNum;
    }

    public String getSelectClass() {
        return selectClass;
    }

    public void setSelectClass(String selectClass) {
        this.selectClass = selectClass;
    }

    public Character getNetClass() {
        return netClass;
    }

    public void setNetClass(Character netClass) {
        this.netClass = netClass;
    }

    public Integer getIp1() {
        return ip1;
    }

    public void setIp1(Integer ip1) {
        this.ip1 = ip1;
    }

    @Override
    public String toString() {
        return "SubnetBo{" +
                "ip1=" + ip1 +
                ", ip2=" + ip2 +
                ", ip3=" + ip3 +
                ", ip4=" + ip4 +
                ", mask1=" + mask1 +
                ", mask2=" + mask2 +
                ", mask3=" + mask3 +
                ", mask4=" + mask4 +
                ", netClass=" + netClass +
                ", selectClass='" + selectClass + '\'' +
                ", subnetNum=" + subnetNum +
                ", nodeNum=" + nodeNum +
                ", subnetBit=" + subnetBit +
                ", hasSubnets=" + hasSubnets +
                ", segments=" + segments +
                '}';
    }

    public Integer getIp2() {
        return ip2;
    }

    public void setIp2(Integer ip2) {
        this.ip2 = ip2;
    }

    public Integer getIp3() {
        return ip3;
    }

    public void setIp3(Integer ip3) {
        this.ip3 = ip3;
    }

    public Integer getIp4() {
        return ip4;
    }

    public void setIp4(Integer ip4) {
        this.ip4 = ip4;
    }

    public Integer getMask1() {
        return mask1;
    }

    public void setMask1(Integer mask1) {
        this.mask1 = mask1;
    }

    public Integer getMask2() {
        return mask2;
    }

    public void setMask2(Integer mask2) {
        this.mask2 = mask2;
    }

    public Integer getMask3() {
        return mask3;
    }

    public void setMask3(Integer mask3) {
        this.mask3 = mask3;
    }

    public Integer getMask4() {
        return mask4;
    }

    public void setMask4(Integer mask4) {
        this.mask4 = mask4;
    }
}
