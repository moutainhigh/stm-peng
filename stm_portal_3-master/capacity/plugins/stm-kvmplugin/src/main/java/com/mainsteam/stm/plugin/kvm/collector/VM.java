package com.mainsteam.stm.plugin.kvm.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;

public class VM extends Host {

	@SuppressWarnings("unused")
	private static final org.apache.commons.logging.Log log = LogFactory
			.getLog(VM.class);

	private String vmname = null;
	private String[] vm = null;
	public String uuid = "";

	public String getVmname() {
		return vmname;
	}

	public void setVmname(String vmname) {
		this.vmname = vmname;
		vm = null;
	}

	public VM(String ip, String username, String passwd, String vmname)
			throws Exception {
		super(ip, username, passwd);
		this.vmname = vmname;
	}

	public String[] getVm() throws Exception {
		if (vm != null)
			return vm;
		vm = super.getVm(this.vmname);
		return vm;
	}

	public ArrayList<Map<String, String>> getVmAvailabilityImpl(String uuid) {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] info = getUtil().getOut("virsh dominfo " + uuid);
			String state = this.getProper(info, "State").trim().split(" ")[0]
					.trim();
			try {
				map.put("State", state);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, String> getVmInfo() {
		Map<String, String> map = new HashMap<String, String>();
		if (this.vm == null) {
			try {
				this.getVm();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String[] info = null;
		String[] ipinfo = null;
		try {
			info = getUtil().getOut("virsh dominfo " + this.vmname);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			ipinfo = getUtil().getOut("virsh dumpxml " + this.vmname);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if (null != info && info.length > 0) {
				String mem = this.getProper(info, "Max memory").trim()
						.split(" ")[0].trim();
				mem = String.valueOf(java.lang.Long.parseLong(mem) * 1024);
				map.put("uuid", this.getProper(info, "UUID"));
				map.put("namelabel", this.getProper(info, "Name"));
				map.put("powerstate", this.getProper(info, "State"));
				map.put("namedescription", this.getProper(info, "Name"));
				map.put("memoryactual", mem);
				map.put("memory", mem);
				map.put("vcpusnumber", this.getProper(info, "CPU(s)"));
				map.put("cpusnumber", this.getProper(info, "CPU(s)"));

				map.put("vcpusutilisation", "");
				map.put("utilisation", "");
				map.put("hostuuid", this.hostuuid);
				map.put("devicename", this.vmname);
				this.uuid = map.get("uuid");

				map.put("ip", "");

				String ip = this.getProper(ipinfo, "<parameter name='IP'");
				ip = ip.substring(ip.indexOf("value=") + 7);
				ip = ip.substring(0, ip.indexOf("'"));
				map.put("ip", ip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	@SuppressWarnings("unused")
	public ArrayList<Map<String, String>> getVmMemory() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] info = getUtil().getOut("virsh dommemstat " + this.vmname);
			try {
				map.put("vmuuid", this.uuid);
				map.put("hostuuid", this.hostuuid);
				map.put("memorytotal", this.getVmInfo().get("memory"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public ArrayList<Map<String, String>> getVmCpu() {

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		try {
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] info = getUtil().getOut("virsh vcpuinfo " + this.vmname);

			String[] dominfo = getUtil().getOut("virsh dominfo " + this.vmname);

			try {
				map.put("uuid", "");
				map.put("vmuuid", this.uuid);
				map.put("hostuuid", this.hostuuid);
				map.put("cpusnumber", this.getProper(info, "VCPU"));
				map.put("number", this.getProper(info, "VCPU"));
				map.put("num", this.getProper(dominfo, "CPU(s)"));
				map.put("vcpusutilisation", "");
				map.put("utilisation", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Map<String, String>> getVmVBDInfo() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			String[] info = getUtil().getOut("virsh domblklist " + this.vmname);

			ArrayList disks = this.getTable(info);
			for (int i = 0; i < disks.size(); i++) {
				String[] disk = (String[]) disks.get(i);
				String[] diskinfo = getUtil().getOut(
						"virsh domblkinfo " + this.vmname + " " + disk[0]);
				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				try {
					map.put("vmuuid", this.uuid);
					map.put("hostuuid", this.hostuuid);
					map.put("uuid", disk[0]);
					map.put("device", disk[0]);
					map.put("bootable", "");
					map.put("unpluggable", "");
					map.put("vmuuid", this.uuid);
					map.put("namelabel", disk[0]);
					map.put("namedescription", disk[1]);
					String size = this.getProper(diskinfo, "Capacity").replace(
							"GB", "");
					String used = this.getProper(diskinfo, "Allocation")
							.replace("GB", "");

					size = String.valueOf((long) (java.lang.Float
							.parseFloat(size)));
					used = String.valueOf((long) (java.lang.Float
							.parseFloat(used)));
					map.put("physicalutilisation", used);
					map.put("physicalsize", size);
					map.put("virtualsize", size);
					map.put("vdiuuid", "");
					map.put("sruuid", "");
					map.put("type", "");

				} catch (Exception e) {
					System.out.println(":VBDInfo of " + this.vmname
							+ " is not found......");
				}
			}
		} catch (Exception e) {
			System.out.println(":VBDInfo of " + this.vmname
					+ " is not found......");
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Map<String, String>> getVmVIFInfo() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			String[] info = getUtil().getOut("virsh domiflist " + this.vmname);
			ArrayList nets = this.getTable(info);
			for (int i = 0; i < nets.size(); i++) {
				String[] vif = (String[]) nets.get(i);
				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				try {
					map.put("vmuuid", this.uuid);
					map.put("hostuuid", this.hostuuid);
					map.put("uuid", vif[0]);
					map.put("device", vif[0]);
					map.put("mac", vif[4]);
					map.put("mtu", "");
					map.put("vmuuid", this.uuid);
					map.put("objectid", "");
					map.put("networkuuid", vif[0]);
					map.put("namelabel", vif[0]);
					String[] netinfo = getUtil().getOut(
							"virsh dumpxml " + this.vmname);
					map.put("ipaddr", this.getIPAddress(
							this.getVnetIPLine(netinfo, vif[0]), "value="));
					map.put("ip", map.get("ipaddr"));
					map.put("netmask", this.getIPAddress(
							this.getVnetMaskLine(netinfo, vif[0]), "value="));
					map.put("lastupdated", "");
				} catch (Exception e) {
					System.out.println(":VIFInfo of " + this.vmname
							+ " is not found......");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unused")
	public String getVnetIPLine(String[] info, String name) {
		int i = 0, j = 0;
		for (i = 0; i < info.length; i++) {
			if (info[i].trim().startsWith("<target dev='" + name + "'/>")) {
				for (; i < info.length; i++) {
					if (info[i].trim().startsWith("</interface>"))
						break;
					if (info[i].trim().startsWith(
							"<parameter name='IP' value='")) {
						return info[i];
					}
				}
			}
		}
		return "";
	}

	@SuppressWarnings("unused")
	public String getVnetMaskLine(String[] info, String name) {
		int i = 0, j = 0;
		for (i = 0; i < info.length; i++) {
			if (info[i].trim().startsWith("<target dev='" + name + "'/>")) {
				for (; i < info.length; i++) {
					if (info[i].trim().startsWith("</interface>"))
						break;
					if (info[i].trim().startsWith("<parameter name='PROJMASK'")) {
						return info[i];
					}
				}
			}
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		// VM vm = new VM("192.168.1.186", "root", "123456", "6" );
		VM vm = new VM("192.168.1.186", "root", "123456", "192.168.1.186");
		System.out.println("虚拟机所属主机信息:" + vm.getHostInfo());
		System.out.println("虚拟机CPU:" + vm.getCpuInfo());
		System.out.println("虚拟机内存:" + vm.getMemoryInfo());
		System.out.println("虚拟机PDB信息:" + vm.getPDBInfo());
		System.out.println("虚拟机PIF信息:" + vm.getPIFInfo());
		System.out.println("-----------------------------");
		System.out.println("虚拟机信息:" + vm.getVmInfo());
		System.out.println("虚拟机VMCPU:" + vm.getVmCpu());
		System.out.println("虚拟机VMMem:" + vm.getVmMemory());
		System.out.println("虚拟机VBD信息:" + vm.getVmVBDInfo());
		System.out.println("虚拟机VmVIF信息:" + vm.getVmVIFInfo());
		vm.close();
	}
}
