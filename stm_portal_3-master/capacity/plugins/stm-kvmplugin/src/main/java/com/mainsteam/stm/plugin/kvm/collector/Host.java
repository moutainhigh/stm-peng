package com.mainsteam.stm.plugin.kvm.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;

public class Host extends Hosts {
	private static final org.apache.commons.logging.Log log = LogFactory
			.getLog(Host.class);
	@SuppressWarnings("rawtypes")
	ArrayList vms = null;
	String hostname = "";
	String hostuuid = "";

	public Host(String ip, String username, String passwd) throws Exception {
		super(ip, username, passwd);
		log.error("ip--->>>" + ip + "---username--->>>" + username
				+ "---password--->>>" + passwd);
	}

	public String getHostName() throws IOException {
		String[] res = this.getUtil().getOut("virsh hostname");
		if (res != null)
			return res[res.length - 1];
		return null;

	}

	public void setHostName(String hostname) {
		this.hostname = hostname;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getTable(String[] ss) {
		ArrayList list = new ArrayList();
		int i = 0;

		for (; i < ss.length; i++) {
			if (ss[i].indexOf("------------------") >= 0)
				break;
		}

		for (; i < ss.length; i++) {
			String aa = ss[i].trim();
			if (aa != null && aa.length() > 2) {
				if (aa.indexOf("------------------") >= 0)
					continue;
				if (aa.length() <= 2)
					continue;
				String[] bb = aa.split(" ");
				String[] cc = new String[100];
				int jj = 0;
				for (int j = 0; j < bb.length; j++) {
					if (bb[j] != null && !"".equals(bb[j])
							&& bb[j].length() > 0) {
						// System.out.println(bb[j]);
						cc[jj++] = bb[j];
					}
				}
				list.add(cc);
			}
		}
		return list;
	}

	public String getProper(String[] ss, String prop) {
		int i = 0;

		for (; i < ss.length; i++) {
			String a = ss[i].trim();
			if (a.startsWith(prop)) {
				String[] b = a.split(":");
				if (b.length > 1)
					return b[1].trim();

				int n = a.indexOf(prop);
				if (n > 0)
					return a.substring(n + prop.length());

				return a;
			}
		}

		return null;
	}

	public String getLine(String[] ss, String prop) {
		int i = 0;

		for (; i < ss.length; i++) {
			String a = ss[i].trim();
			if (a.startsWith(prop)) {
				return a;
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getProperList(String[] ss, String prop) {
		int i = 0;
		ArrayList list = new ArrayList();
		for (; i < ss.length; i++) {
			String a = ss[i].trim();
			if (a.startsWith(prop)) {
				String[] b = a.split(":");
				if (b.length > 1)
					list.add(b[1].trim());
				else
					list.add(a);
			}
		}
		return list;
	}

	public String getline(String[] ss, int a) {
		if (a >= 0 && a < ss.length)
			return ss[a];

		if (a > ss.length) {
			return ss[ss.length - 1];
		}

		if (a < 0) {
			for (int j = ss.length - 1; j >= 0; j--) {
				if (ss[j] != null && !"".equals(ss[j].trim())) {
					return ss[j + a + 1];
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList getVms() throws Exception {
		// log.info("start to get VmsInfo by libvirt cmd1");
		if (vms != null) {
			return vms;
		}
		String[] vm = this.getUtil().getOut("virsh list");
		// log.info("start to get VmsInfo by libvirt cmd2");
		if (vm == null)
			return null;
		vms = this.getTable(vm);
		// log.info("start to get VmsInfo by libvirt cmd3  "+vms.size());
		return vms;
	}

	@SuppressWarnings("rawtypes")
	public String[] getVm(String a) throws Exception {
		ArrayList list = getVms();

		for (int i = 0; i < list.size(); i++) {
			String[] cc = (String[]) list.get(i);

			if (a.equals(cc[0]) || a.equals(cc[1]))
				return cc;
		}
		return null;
	}

	public ArrayList<Map<String, String>> getHostInfo() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			// log.info("start to get HostInfo by libvirt cmd2");
			String[] cap = getUtil().getOut("virsh capabilities");
			String[] hostname = getUtil().getOut("virsh hostname");

			String uuid = this.getProper(cap, "<uuid>").replace("<uuid>", "")
					.replace("</uuid>", "");
			map.put("uuid", uuid);

			map.put("namelabel", this.getline(hostname, -1));
			map.put("namedescription", map.get("namelabel"));
			map.put("hostname", map.get("namelabel"));
			// log.info("start to get HostInfo by libvirt cmd3");
		} catch (Exception e) {
			return null;
		}
		this.hostname = map.get("hostname");
		this.hostuuid = map.get("uuid");
		list.add(map);
		return list;
	}

	public ArrayList<Map<String, String>> getMemoryInfo() throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		list.add(map);
		String[] mems = getUtil().getOut("virsh nodememstats");

		String mem = this.getProper(mems, "total");
		// ++
		String freeMem = this.getProper(mems, "free");
		String buffersMem = this.getProper(mems, "buffers");
		String cachedMem = this.getProper(mems, "cached");

		String[] total = mem.split(" ");
		// ++
		String[] free = freeMem.split(" ");
		String[] buffers = buffersMem.split(" ");
		String[] cached = cachedMem.split(" ");

		map.put("memorytotal",
				String.valueOf((java.lang.Long.parseLong(total[0]) * 1024)));
		// ++
		map.put("memoryfree",
				String.valueOf((java.lang.Long.parseLong(free[0]) * 1024)));
		map.put("memorybuffers",
				String.valueOf((java.lang.Long.parseLong(buffers[0]) * 1024)));
		map.put("memorycached",
				String.valueOf((java.lang.Long.parseLong(cached[0]) * 1024)));

		map.put("hostuuid", this.hostuuid);
		map.put("hostname", this.hostname);
		return list;
	}

	@SuppressWarnings("unused")
	public ArrayList<Map<String, String>> getCpuInfo() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {

			Map<String, String> map = new HashMap<String, String>();
			list.add(map);

			String[] cpu = getUtil().getOut("virsh nodeinfo");

			String number = this.getProper(cpu, "CPU(s)");
			String numcell = this.getProper(cpu, "NUMA cell(s)");
			
			map.put("number", this.getProper(cpu, "CPU(s)"));
			map.put("numcell", this.getProper(cpu, "NUMA cell(s)"));
			map.put("num", this.getProper(cpu, "CPU(s)"));
			map.put("vendor", this.getProper(cpu, "CPU model"));
			map.put("speed", this.getProper(cpu, "CPU frequency"));
			map.put("modelname", this.getProper(cpu, "CPU model"));
			map.put("family", "");
			map.put("stepping", "");
			map.put("utilisation", "");
			map.put("model", this.getProper(cpu, "CPU model"));
			// System.out.println(map);
			map.put("hostuuid", this.hostuuid);
			map.put("hostname", this.hostname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Map<String, String>> getPIFInfo() throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String[] aa = getUtil().getOut("virsh iface-list");
		ArrayList net = this.getTable(aa);
		for (int i = 0; i < net.size(); i++) {
			String[] net1 = (String[]) net.get(i);
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			map.put("uuid", this.hostuuid + net1[2]);
			// map.put("ip",super.getIp());
			map.put("mac", net1[2]);
			map.put("mtu", "");
			map.put("currentlyattached", "");

			map.put("vendorid", "");
			map.put("vendorname", "");
			map.put("speed", "");
			map.put("duplex", "");
			map.put("networkuuid", "");
			map.put("namelabel", net1[0]);
			map.put("hostuuid", this.hostuuid);
			map.put("ipconfigurationmode", "");
			map.put("hostuuid", this.hostuuid);
			map.put("hostname", this.hostname);

			String ipline = this.getLine(
					this.getUtil().getOut("virsh iface-dumpxml " + net1[0]),
					"<ip address");
			System.out.println(ipline);
			if (ipline != null) {
				map.put("ip", this.getIPAddress(ipline, "address="));
				map.put("netmask", this.getIPAddress(ipline, "prefix="));
			}
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Map<String, String>> getDiyPDBAvailability(String uuid)
			throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String[] aa = this.getUtil().getOut("virsh pool-list");
		ArrayList pdb = this.getTable(aa);
		for (int i = 0; i < pdb.size(); i++) {
			String[] pdb1 = (String[]) pdb.get(i);

			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] pdbinfo = getUtil().getOut("virsh pool-info " + pdb1[0]);

			map.put("uuid", getProper(pdbinfo, "UUID"));
			map.put("device", pdb1[0]);
			map.put("location", "");
			map.put("type", "");
			map.put("legacy_mode", "");
			map.put("sruuid", "");
			map.put("namelabel", pdb1[0]);
			map.put("namedescription", pdb1[0]);
			map.put("virtualallocation", "");

			try {

				String State = getProper(pdbinfo, "State").trim();
				System.out.println("存储状态shift---->" + State);

				map.put("State", State);

			} catch (Exception e) {

			}
		}
		return list;
	}

	/**
	 * Allocation new add method 获取存储容量信息
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public String getDataStoreAllocation(String uuid) throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String[] aa = this.getUtil().getOut("virsh pool-list");
		String Allocation = null;
		ArrayList pdb = this.getTable(aa);
		for (int i = 0; i < pdb.size(); i++) {
			String[] pdb1 = (String[]) pdb.get(i);
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] pdbinfo = getUtil().getOut("virsh pool-info " + pdb1[0]);
			map.put("uuid", getProper(pdbinfo, "UUID"));
			map.put("device", pdb1[0]);
			map.put("location", "");
			map.put("type", "");
			map.put("legacy_mode", "");
			map.put("sruuid", "");
			map.put("namelabel", pdb1[0]);
			map.put("namedescription", pdb1[0]);
			map.put("virtualallocation", "");
			try {
				Allocation = getProper(pdbinfo, "Allocation")
						.replace("GiB", "").trim();
				Allocation = String.valueOf((long) (java.lang.Float
						.parseFloat(Allocation) * 1024 * 1024 * 1024));
				map.put("physicalutilisation", Allocation);
			} catch (Exception e) {

			}
		}
		return Allocation;
	}

	/**
	 * Capacity new add method 获取存储容量信息
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public String getDataStoreCapacity(String uuid) throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String[] aa = this.getUtil().getOut("virsh pool-list");
		// String Allocation = null;
		String Capacity = null;
		ArrayList pdb = this.getTable(aa);
		for (int i = 0; i < pdb.size(); i++) {
			String[] pdb1 = (String[]) pdb.get(i);

			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] pdbinfo = getUtil().getOut("virsh pool-info " + pdb1[0]);

			map.put("uuid", getProper(pdbinfo, "UUID"));
			map.put("device", pdb1[0]);
			map.put("location", "");
			map.put("type", "");
			map.put("legacy_mode", "");
			map.put("sruuid", "");
			map.put("namelabel", pdb1[0]);
			map.put("namedescription", pdb1[0]);
			map.put("virtualallocation", "");
			try {

				Capacity = getProper(pdbinfo, "Capacity").replace("GiB", "")
						.trim();
				Capacity = String.valueOf((long) (java.lang.Float
						.parseFloat(Capacity) * 1024 * 1024 * 1024));

				map.put("physicalsize", Capacity);
			} catch (Exception e) {

			}
		}
		return Capacity;
	}

	/**
	 * Capacity new add method 获取存储容量信息
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public String getDataStoreFreeSpace(String uuid) throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String[] aa = this.getUtil().getOut("virsh pool-list");
		String Available = null;
		ArrayList pdb = this.getTable(aa);
		for (int i = 0; i < pdb.size(); i++) {
			String[] pdb1 = (String[]) pdb.get(i);

			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] pdbinfo = getUtil().getOut("virsh pool-info " + pdb1[0]);

			map.put("uuid", getProper(pdbinfo, "UUID"));
			map.put("device", pdb1[0]);
			map.put("location", "");
			map.put("type", "");
			map.put("legacy_mode", "");
			map.put("sruuid", "");
			map.put("namelabel", pdb1[0]);
			map.put("namedescription", pdb1[0]);
			map.put("virtualallocation", "");

			try {
				Available = getProper(pdbinfo, "Available").replace("GiB", "")
						.trim();

				Available = String.valueOf((long) (java.lang.Float
						.parseFloat(Available) * 1024 * 1024 * 1024));
				map.put("freeSpace", Available);

			} catch (Exception e) {

			}
		}
		return Available;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Map<String, String>> getPDBInfo() throws IOException {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String[] aa = this.getUtil().getOut("virsh pool-list");
		ArrayList pdb = this.getTable(aa);
		for (int i = 0; i < pdb.size(); i++) {
			String[] pdb1 = (String[]) pdb.get(i);

			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			String[] pdbinfo = getUtil().getOut("virsh pool-info " + pdb1[0]);

			map.put("uuid", getProper(pdbinfo, "UUID"));
			map.put("device", pdb1[0]);
			map.put("location", "");
			map.put("type", "");
			map.put("legacy_mode", "");
			map.put("sruuid", "");
			map.put("namelabel", pdb1[0]);
			map.put("namedescription", pdb1[0]);
			map.put("virtualallocation", "");

			try {
				String Allocation = getProper(pdbinfo, "Allocation").replace(
						"GiB", "").trim();
				String Capacity = getProper(pdbinfo, "Capacity").replace("GiB",
						"").trim();
				String Available = getProper(pdbinfo, "Available").replace(
						"GiB", "").trim();

				Allocation = String.valueOf((long) (java.lang.Float
						.parseFloat(Allocation) * 1024 * 1024 * 1024));
				Capacity = String.valueOf((long) (java.lang.Float
						.parseFloat(Capacity) * 1024 * 1024 * 1024));
				Available = String.valueOf((long) (java.lang.Float
						.parseFloat(Available) * 1024 * 1024 * 1024));

				map.put("physicalutilisation", Allocation);
				map.put("physicalsize", Capacity);
				map.put("virtualsize", Capacity);
				map.put("freeSpace", Available);

			} catch (Exception e) {

			}
		}
		return list;
	}

	public String getIPAddress(String a, String start) {
		String ip = a;

		ip = ip.substring(ip.indexOf(start) + start.length() + 1);
		ip = ip.substring(0, ip.indexOf("'"));
		return ip;
	}

	public static void main(String[] args) throws Exception {
		// Host host = new Host("192.168.1.206", "root", "edi");

		Host host = new Host("192.168.1.186", "root", "123456");
		/***************** 信息指标 ******************/
		System.out.println("主机IP和name：" + host.getIp() + "/"
				+ host.getHostName() + "/libvirtUtils:" + host.getUtil());
		System.out.println("主机信息：");
		System.out.print("------->" + host.getHostInfo() + "\r");
		for (Map<String, String> hostInfo : host.getHostInfo()) {
			System.out.println("hostInfo---->" + hostInfo + "\r");
		}

		System.out.println("主机内存信息：");
		for (Map<String, String> memInfo : host.getMemoryInfo()) {
			System.out.println("memInfo---->" + memInfo + "\r");
		}
		System.out.println("主机CPU信息：");
		for (Map<String, String> cpuInfo : host.getCpuInfo()) {
			System.out.println("cpuInfo---->" + cpuInfo + "\r");
		}
		host.close();
	}

	// --------------------------------------------------new
	// ++++++++++++++-----------
	/*
	 * public ArrayList<Map<String, String>> getDiyAllVmInfo() {
	 * ArrayList<Map<String, String>> list = new ArrayList<Map<String,
	 * String>>(); try { Map<String, String> map = new HashMap<String,
	 * String>(); list.add(map); String[] vm =
	 * this.getUtil().getOut("virsh list");
	 * 
	 * String name = this.getProper(vm, "Name").trim().split(" ")[0] .trim();
	 * try { System.out.println("%---------->" + name); map.put("Name", name); }
	 * catch (Exception e) { e.printStackTrace(); } } catch (Exception e) {
	 * e.printStackTrace(); } System.out
	 * .println("list........................................................>"
	 * + list); return list; }
	 */
	public ArrayList<Map<String, String>> getDiyAllVmInfo() {
		// ArrayList<Map<String, String>> list = new ArrayList<Map<String,
		// String>>();
		// try {
		// Map<String, String> map = new HashMap<String, String>();
		// list.add(map);
		// String[] allvm = getUtil().getOut("virsh list");
		//
		// String number = this.getProper(allvm, "Id");
		// map.put("number", this.getProper(allvm, "Id"));
		// map.put("Name", this.getProper(allvm, "Name"));
		// map.put("State", this.getProper(allvm, "State"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return list;
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			// String[] info = getUtil().getOut("virsh domstate " +
			// this.vmname);
			String[] allvm = getUtil().getOut("virsh list");
			// String name = this.getProper(allvm,
			// "Name").trim().split(" ")[0].trim();
			for (String s : allvm) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + s);
			}
			try {
				// System.out.println("%------------------>"+name);
				// map.put("Name",name);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
