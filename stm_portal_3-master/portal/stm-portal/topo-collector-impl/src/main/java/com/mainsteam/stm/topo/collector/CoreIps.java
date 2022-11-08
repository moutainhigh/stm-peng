package com.mainsteam.stm.topo.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class CoreIps implements IpConfigs {
	private static final long serialVersionUID = 8162307398525090669L;
	private long startIp;
	private long endIp;

	public String getStartIp() {
//		return TopologyTools.iPv4String(startIp);
		return null;
	}

	public String getEndIp() {
//		return TopologyTools.iPv4String(endIp);
		return null;
	}

	public void setSubnetIps(String subnet, String netmask) {
//		this.startIp = TopologyTools.str2long(subnet) + 1;
//		this.endIp = TopologyTools.getHostNumbers(netmask) + TopologyTools.str2long(subnet);
		check();
	}

	public void setSegmentIps(String startIp, String endIp) {
		this.startIp = TopologyTools.str2long(startIp);
		this.endIp = TopologyTools.str2long(endIp);
		check();
	}

	private void check() {
		if (this.endIp < this.startIp || this.startIp <= 0) {
			throw new IllegalArgumentException("网段不合法:" + this.startIp + "---" + this.endIp);
		}
	}

	public List<String> intersectionIps(Collection<String> ips) {
		List<String> intersectionIps = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(ips)) {
			for (String ip : ips) {
				if (contains(ip)) {
					intersectionIps.add(ip);
				}
			}
		}
		return intersectionIps;
	}

	public List<String> excludeIps(Collection<String> ips) {
		List<String> excludeIps = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(ips)) {
			for (String ip : ips) {
				if (!contains(ip)) {
					excludeIps.add(ip);
				}
			}
		}
		return excludeIps;
	}

	public boolean hasIntersectionIps(Collection<String> ips) {
		if (!CollectionUtils.isEmpty(ips)) {
			for (String ip : ips) {
				if (contains(ip)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSubset(Collection<String> ips) {
		if (!CollectionUtils.isEmpty(ips)) {
			for (String ip : ips) {
				if (!contains(ip)) {
					return false;
				}
			}
		}
		return true;
	}

	public Iterator<String> iterator() {
		Iterator<String> it = new Iterator<String>() {
			private long current = startIp;

			public void remove() {
				throw new RuntimeException("网段方式不支持删除");
			}

			public String next() {
//				String val = TopologyTools.iPv4String(current);
//				++current;
//				return val;
				return null;
			}

			public boolean hasNext() {
				return current <= endIp;
			}
		};
		return it;
	}

	public boolean contains(String ip) {
//		long ipVal = TopologyTools.str2long(ip);
//		if (ipVal >= startIp && ipVal <= endIp) {
//			return true;
//		}
		return false;
	}

	public Set<String> getAllIps() {
		Set<String> allIps = new HashSet<String>();
		for (long i = startIp; i <= endIp; ++i) {
			allIps.add(TopologyTools.iPv4String(i));
		}
		return allIps;
	}

	public long getIpCount() {
		return endIp - startIp + 1;
	}

	public String getIpsStr() {
//		return getStartIp() + "-" + getEndIp();
		return null;
	}

	public void setIpsStr(String ipStr) {
//		if (ipStr.indexOf("-") > 0) {
//			String[] str = ipStr.split("-");
//			setSegmentIps(str[0], str[1]);
//		} else if (ipStr.indexOf("/") > 0) {
//			String[] str = ipStr.split("/");
//			setSubnetIps(str[0], str[1]);
//		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof CoreIps) {
			boolean startIpEqual = (this.startIp == ((CoreIps) obj).startIp);
			boolean endIpEqual = (this.endIp == ((CoreIps) obj).endIp);
			return startIpEqual && endIpEqual;
		}
		return super.equals(obj);
	}

	public int hashCode() {
		return (int) (startIp + endIp / 2);
	}

	public String toString() {
		return getIpsStr() + " (共" + getIpCount() + "个)";
	}
}
