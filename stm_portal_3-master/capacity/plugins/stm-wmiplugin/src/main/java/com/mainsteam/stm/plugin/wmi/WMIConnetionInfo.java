package com.mainsteam.stm.plugin.wmi;

import java.net.InetSocketAddress;

public class WMIConnetionInfo {
	public final InetSocketAddress address;
	public final String type;

	public WMIConnetionInfo(String ip, int port, String type) {
		this.address = new InetSocketAddress(ip, port);
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WMIConnetionInfo other = (WMIConnetionInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WMIConnetionInfo [address=" + address + ", type=" + type + "]";
	}

}
