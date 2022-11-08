package com.mainsteam.stm.topo.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface IpConfigs extends Serializable {

	public String getIpsStr();

	public void setIpsStr(String ipStr);

	// 获取交集
	public List<String> intersectionIps(Collection<String> ips);

	// 获取不属于参数范围的集合
	public List<String> excludeIps(Collection<String> ips);

	public boolean hasIntersectionIps(Collection<String> ips);

	public boolean isSubset(Collection<String> ips);

	public boolean contains(String ip);

	public Iterator<String> iterator();

	// 此方法慎用
	public Set<String> getAllIps();

	public long getIpCount();

}
