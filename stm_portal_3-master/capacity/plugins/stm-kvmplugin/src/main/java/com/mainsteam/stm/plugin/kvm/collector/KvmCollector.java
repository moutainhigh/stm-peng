package com.mainsteam.stm.plugin.kvm.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.kvm.bo.ConnectionInfo;
import com.mainsteam.stm.plugin.kvm.bo.KvmResourceNode;
import com.mainsteam.stm.plugin.kvm.dict.KvmResourceID;
import com.mainsteam.stm.plugin.kvm.util.SSHUtil;

public class KvmCollector {
	private ConnectionInfo connectionInfo;
	private static SSHUtil sshUtil;
	public String globalName;
	public static KvmCollector kvmCollector;
	public Host host1;
	public VM globalVm;
	@SuppressWarnings("unused")
	private String globalHostUuid;
	private VM virtualMachine = null;
	@SuppressWarnings("unused")
	private String hostName;
	private static final Logger log = LogManager.getLogger(KvmCollector.class);

	public KvmCollector(ConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
		sshUtil = new SSHUtil(this.connectionInfo.vmname,
				this.connectionInfo.username, this.connectionInfo.password);
		try {
			sshUtil.login();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("连接-登录失败！原因：" + e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// conInfo = new ConnectionInfo("192.168.1.186", "root","123456");
		// conInfo= new Connect("192.168.1.186");
		String vmname = "192.168.1.186";
		String username = "root";
		String password = "123456";
		kvmCollector = new KvmCollector(new ConnectionInfo(vmname, username,
				password));
		kvmCollector.getResourceTree();
		// String vmUuid = "04c07717-cb4f-6e9f-d9b0-38380fe26726";// ubuntur 运行中
		String vmUuid = "8af5963d-eea2-3ff5-af44-cdda17214b88"; // xxxxlinux2
		// 停止
		// String hostUuid = "564de1cb-e56d-4629-0d53-be01f6ca845d";
//		System.out.println("主机内存利用率=====>" + kvmCollector.getHostMemRate());
//		 System.out.println("主机CPU利用率=====>" + kvmCollector.getHostUuid());

		System.out.println("vm=====>" + kvmCollector.getVMNameLabel(vmUuid));
		// System.out.println("主机=====>" + kvmCollector.getVMTotalMem(vmUuid));
		// System.out.println("HOST NAME===>" + kvmCollector.getHostName());
		// String hostUuid = "564de1cb-e56d-4629-0d53-be01f6ca845d";
		// String vmUuid = "04c07717-cb4f-6e9f-d9b0-38380fe26726";// ubuntur 运行中
		// String vmUuid = "8af5963d-eea2-3ff5-af44-cdda17214b88"; xxxxlinux2 停止
		// String vmUuid = "8674e104-cec0-32b7-9226-85b59e3e5b01";// xxxlinux3
		// 停止
		// System.out.println("虚拟机的：" + kvmCollector.getVMAvailability(vmUuid));
		// System.out.println("虚拟机的内存利用率：" + kvmCollector.getVmCpuRate(vmUuid));
		// System.out.println("虚拟机的总内存：" + kvmCollector.getVMTotalMem(vmUuid));
		// System.out.println("虚拟机的剩余内存：" + kvmCollector.getVMFreeMem(vmUuid));
		// System.out.println("虚拟机的已使用内存：" +
		// kvmCollector.getVMCachedMem(vmUuid));// 缓存?,已使用内存？
		// System.out.println("虚拟机的：" + kvmCollector.getVmMemRate(vmUuid));
		// System.out.println("主机UUID--->"+kvmCollector.getHostUuid());
		/*
		 * String uuid = "04c07717-cb4f-6e9f-d9b0-38380fe26726";
		 * System.out.println("kvmCollector.getVmAvailability()-------->" +
		 * kvmCollector.getVmAvailability(uuid));
		 * 
		 * 
		 * 
		 * 
		 * String dataStoreUuid = "5bb6d062-f9bc-bbd0-74cf-54e69add8900";
		 */
		// System.out
		// .println("kvmCollector.getDataStoreAvailability()----->"
		// + kvmCollector
		// .getDataStoreAvailability("5bb6d062-f9bc-bbd0-74cf-54e69add8900"));
		/*
		 * 
		 * String hostUuid = "564de1cb-e56d-4629-0d53-be01f6ca845d";
		 * System.out.println("kvmCollector.getHostMemRate()------>" +
		 * kvmCollector.getHostMemRate(hostUuid)); String hostUuid =
		 * "564de1cb-e56d-4629-0d53-be01f6ca845d";
		 * System.out.println("kvmCollector.getHostCpuRate()------>" +
		 * kvmCollector.getHostTotalCpu(hostUuid));
		 */
		// String dataStoreUuid = ("5bb6d062-f9bc-bbd0-74cf-54e69add8900");
		// System.out.println("存储总空间容量--->"
		// + kvmCollector.getDataStorePhysicalSize(dataStoreUuid));
		// System.out.println("存储已用容量--->"
		// + kvmCollector.getDataStorePhysicalUtilisation(dataStoreUuid));
		// System.out.println("存储可用容量--->"
		// + kvmCollector.getDataStoreActualFreeSizeGB(dataStoreUuid));

		// String vmUuid = "04c07717-cb4f-6e9f-d9b0-38380fe26726";
		// String vmUuid1 = "f3d012e2-4bdc-46b2-27e7-9fb9043254d4";
		// System.out.println("虚拟机处理器：--->" +
		// kvmCollector.getVmCpuRat(vmUuid1));
		// System.out.println("虚拟机内存--->" +
		// kvmCollector.getVMNameLabel(vmUuid1));
	}

	/**
	 * KVM发现
	 * 
	 * @param currentNode
	 * @throws IOException
	 */
	@SuppressWarnings({ "unused" })
	private void discoveryKvm(KvmResourceNode currentNode) throws IOException {
		switch (currentNode.getResourceId()) {
		case KvmResourceID.KvmHOST:
			String name = null;
			@SuppressWarnings("rawtypes")
			List list = new ArrayList<String>();
			try {
				virtualMachine = new VM(host1.getIp().toString(),
						sshUtil.username, sshUtil.password, sshUtil.vmname);
				String[] allVm1 = virtualMachine.getUtil().getOut(
						"virsh list --all");
				@SuppressWarnings("rawtypes")
				ArrayList vmList = null;
				vmList = getDiyTable(allVm1);
				for (int i = 0; i < vmList.size(); i++) {
					String[] vms = (String[]) vmList.get(i);
					for (int j = 0; j < vms.length; j++) {
						System.out
								.println("vms--------------------------------->"
										+ vms[j]);
						String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
						/**
						 * line和inffo要能直接匹配到
						 */
						String line = "\u0020" + vms[j];
						String pattern = zhengzeName;

						String uuidResult = null;
						String inffo = null;
						// 创建 Pattern 对象
						Pattern r = Pattern.compile(pattern);
						// 现在创建 matcher 对象
						Matcher m = r.matcher(line);
						if (m.find()) {
							virtualMachine = new VM(host1.getIp().toString(),
									sshUtil.username, sshUtil.password,
									m.group(1));
							globalVm = virtualMachine;
							String zijiVmUUID = virtualMachine.uuid;
							String vmip = "vmip";
							name = virtualMachine.getVmname();
							System.out.println("name--->" + name);
							// String regUuid = "\\**UUID:\\s+(\\S+)";
							String regUuid = "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+";
							// String regUuid = "\\S+:\\s(\\S+)\\s+\\S+";
							String[] vminfo = virtualMachine.getUtil().getOut(
									"virsh dominfo " + name);
							for (String info : vminfo) {
								inffo = info;
								// System.out.println(info);
							}
							// ////////////////////////////////////
							if (null != inffo) {
								uuidResult = getMyZhengZe(regUuid, inffo);
								// globalVmUuid = uuidResult;
								if (null != uuidResult) {
									int ss = uuidResult.lastIndexOf(":") + 10;
									int bb = uuidResult.length() - 12;
									uuidResult = uuidResult.substring(
											(uuidResult.lastIndexOf(":") + 10),
											(uuidResult.length() - 12));
									System.out.println("-------------->"
											+ uuidResult);
									// String uuid = createUuid(name);
									// System.out.println(uuid + "\r");
									// 主机.虚拟机
									KvmResourceNode vmNode = new KvmResourceNode(
											KvmResourceID.KvmVM, uuidResult,
											vmip, name, KvmResourceID.KvmVM);
									currentNode.getChildTrees().add(vmNode);
									discoveryKvm(vmNode);
								} else {
									System.out.println("没有找到匹配的正则!UUID将手动创建!");
									KvmResourceNode vmNode = new KvmResourceNode(
											KvmResourceID.KvmVM,
											createUuid(name), "vmip", name,
											KvmResourceID.KvmVM);
									currentNode.getChildTrees().add(vmNode);
									discoveryKvm(vmNode);
								}

							}
						}
					}
				}
				// 主机:存储
				ArrayList<Map<String, String>> storageVols = host1.getPDBInfo();
				for (Map<String, String> storageVol : storageVols) {
					// 5bb6d062-f9bc-bbd0-74cf-54e69add8900
					String uuid = storageVol.toString().substring(
							storageVol.toString().lastIndexOf("uuid=") + 5,
							storageVol.toString().length() - 8);
					// default
					String datastoreName = storageVol.toString()
							.substring(
									storageVol.toString().lastIndexOf(
											"namelabel=") + 10,
									storageVol.toString().length() - 51);
					// System.out.println(uuid+" "+name);
					KvmResourceNode storageVolNode = new KvmResourceNode(
							KvmResourceID.KvmDATASTORAGE, uuid, "datastoreIp",
							datastoreName, KvmResourceID.KvmDATASTORAGE);
					currentNode.getChildTrees().add(storageVolNode);
					discoveryKvm(storageVolNode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 树型结构
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getResourceTree() throws Exception {
		// root
		Host host = new Host(sshUtil.vmname, sshUtil.username, sshUtil.password);
		host1 = host;
		String ip = host.getIp();
		String uuid = null;
		for (Map<String, String> hostInfo : host.getHostInfo()) {
			String str = hostInfo.toString();
			System.out.println(str);
			// 主机UUID
			uuid = str
					.substring(str.lastIndexOf("uuid=") + 5, str.length() - 1);
		}
		String name = host.getHostName();
		hostName = name;
		KvmResourceNode root = new KvmResourceNode(KvmResourceID.KvmHOST, uuid,
				ip, name, KvmResourceID.KvmHOST);
		discoveryKvm(root);
		System.out.println("kvm tree:\r" + JSON.toJSONString(root) + "\r");
		return JSON.toJSONString(root);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getDiyTable(String[] ss) {
		ArrayList list = new ArrayList();
		int i = 0;

		for (; i < ss.length; i++) {
			if (ss[i].indexOf("------------------") >= 0)
				break;
		}

		for (; i < ss.length; i++) {
			String aa = ss[i].trim();
			if (aa != null && aa.length() > 5) {
				if (aa.indexOf("------------------") >= 0)
					continue;
				if (aa.length() <= 5)
					continue;
				String[] bb = aa.split(",");
				list.add(bb);
			}
		}
		return list;
	}

	/************************** hosts ******************************/
	// null
	/************************** end hosts ***************************/

	/************************** host ******************************/

	/**
	 * 主机UUID
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getHostUuid() throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			for (Map<String, String> hostInfo : hostEntity.getHostInfo()) {
				String str = hostInfo.toString();
				return str.substring(str.lastIndexOf("uuid=") + 5,
						str.length() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 主机名
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getHostName() throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			return hostEntity.getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 主机可用性
	 * 
	 * @param uuid
	 * @return
	 */
	public String getHostAvailability() {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			if (null != hostEntity) {
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 获取主机总CPU容量
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getHostTotalCpu() throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			// if (null != globalHostUuid && null != uuid
			// && globalHostUuid.equals(uuid)) {
			ArrayList<Map<String, String>> totalCpu = hostEntity.getCpuInfo();
			for (int i = 0; i < totalCpu.size(); i++) {
				// 通过KEY取集合里的值的方法，标准参考！
				if (null != totalCpu.get(i).get("speed")) {
					String doubleObjStr = totalCpu.get(i).get("speed");
					// System.out.println("总CPU兆赫--->" + doubleObj);
					return doubleObjStr.substring(0, doubleObjStr.length() - 4);
				}
			}
			// }
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取主机CPU利用率
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception 
	 */
	public int getHostCpuRate() throws Exception {
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		for (Map<String, String> hostMap : hostEntity.getCpuInfo()) {
			Double max = Double.valueOf(hostMap.get("number"));
			Double count= Double.valueOf(hostMap.get("numcell"));
		
			if (count > 0 && max > 0) {
				return (int) ((1-count/max)*100);
			}
		}
		return 0;
	}

	/**
	 * BUG001 主机内存利用率不准确:已解决
	 */
	/**
	 * 获取主机Mem利用率
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getHostMemRate() throws Exception {
		/**
		 * root@ubuntu:~# virsh nodememstats total : 4049868 kB free : 2048180
		 * kB buffers: 110184 kB cached : 365472 kB
		 */
		// CACHED/TOTAL *100
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		// {hostuuid=, memorytotal=4147064832, hostname=,
		// memorycached=395239424, memoryfree=2042560512,
		// memorybuffers=160452608}
		// (memorybuffers/memorytotal)*100%
		for (Map<String, String> vmMap : hostEntity.getMemoryInfo()) {
			Double memorytotal = Double.valueOf(vmMap.get("memorytotal")
					.substring(0, vmMap.get("memorytotal").length() - 3));
			Double memorybuffers = Double.valueOf(vmMap.get("memorybuffers")
					.substring(0, vmMap.get("memorybuffers").length() - 3));
			/*
			 * Double memorycached = Double.valueOf(vmMap.get("memorycached")
			 * .substring(0, vmMap.get("memorycached").length() - 3));
			 */
			if (memorybuffers >= 0 && memorytotal >= 0) {
				System.out.println("内存利用率"
						+ String.valueOf(formatDouble1(memorybuffers
								/ memorytotal * 1000)));
				return String.valueOf(formatDouble1(memorybuffers / memorytotal
						* 1000));
			}
		}
		return "0";
	}

	/**
	 * 获取主机总内存容量
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public double getHostTotalMemory() throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			// if (null != globalHostUuid && null != uuid
			// && globalHostUuid.equals(uuid)) {
			ArrayList<Map<String, String>> memRate = hostEntity.getMemoryInfo();
			for (int i = 0; i < memRate.size(); i++) {
				// 通过KEY取集合里的值的方法，标准参考！
				if (null != memRate.get(i).get("memorytotal")) {
					Double doubleObj = Double.parseDouble(memRate.get(i).get(
							"memorytotal"));
					// System.out.println("主机内存====>" + doubleObj / 1024 /
					// 1024);
					return doubleObj / 1024 / 1024;
				}
			}
			// }
			return 0.0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	/************************** end host ***************************/

	/*************************** start 存储模块 **********************/
	/**
	 * 存储可用性
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getDataStoreAvailability(String uuid) throws Exception {
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		ArrayList<Map<String, String>> storageVols = hostEntity
				.getDiyPDBAvailability(uuid);
		for (Map<String, String> storageVol : storageVols) {
			String datastoreUuid = storageVol.toString().substring(
					storageVol.toString().lastIndexOf("uuid=") + 5,
					storageVol.toString().length() - 8);
			if (datastoreUuid.equals(uuid)) {
				if (storageVol.get("State").equals("running")) {
					return "1";
				} else {
					return "0";
				}
			}
		}
		return null;
	}

	public String getDataStoreUuid(String uuid) throws Exception {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			ArrayList<Map<String, String>> storageVols = hostEntity
					.getDiyPDBAvailability(uuid);
			for (Map<String, String> storageVol : storageVols) {
				String datastoreUuid = storageVol.toString().substring(
						storageVol.toString().lastIndexOf("uuid=") + 5,
						storageVol.toString().length() - 8);
				if (datastoreUuid.equals(uuid)) {
					return storageVol.get("uuid");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取存储名
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getDataStoreNameLabel(String uuid) throws IOException {

		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			ArrayList<Map<String, String>> storageVols = hostEntity
					.getDiyPDBAvailability(uuid);
			for (Map<String, String> storageVol : storageVols) {
				String datastoreUuid = storageVol.toString().substring(
						storageVol.toString().lastIndexOf("uuid=") + 5,
						storageVol.toString().length() - 8);
				if (datastoreUuid.equals(uuid)) {
					return storageVol.get("namelabel");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取存储地址
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getDataStoreAddress(String uuid) throws IOException {

		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			ArrayList<Map<String, String>> storageVols = hostEntity
					.getDiyPDBAvailability(uuid);
			for (Map<String, String> storageVol : storageVols) {
				String datastoreUuid = storageVol.toString().substring(
						storageVol.toString().lastIndexOf("uuid=") + 5,
						storageVol.toString().length() - 8);
				if (datastoreUuid.equals(uuid)) {
					return storageVol.get("location");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取存储总容量 Allocation 已用空间大小
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getDataStorePhysicalUtilisation(String uuid)
			throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			String allocation = hostEntity.getDataStoreAllocation(uuid);
			return allocation.substring(0, allocation.length() - 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取存储总容量 Capacity 容量
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getDataStorePhysicalSize(String uuid) throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			String allocation = hostEntity.getDataStoreCapacity(uuid);
			return allocation.substring(0, allocation.length() - 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取存储总容量 Allocation 可用容量(实际空间大小)
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getDataStoreActualFreeSizeGB(String uuid) throws IOException {
		try {
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			String allocation = hostEntity.getDataStoreFreeSpace(uuid);
			return allocation.substring(0, allocation.length() - 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取存储空间利用率
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public String getDataStorePhysicalRate(String uuid) throws IOException {
		try {
			// 已用
			Double double1 = 0.0;
			Double double2 = 0.0;
			String rate;
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			String allocation = hostEntity.getDataStoreCapacity(uuid);
			if (null != allocation.substring(0, allocation.length() - 2)) {
				double1 = Double.parseDouble(allocation.substring(0,
						allocation.length() - 2));
			}
			// 总的
			String allocation1 = hostEntity.getDataStoreFreeSpace(uuid);
			if (null != allocation1.substring(0, allocation1.length() - 2)) {
				double2 = Double.parseDouble(allocation1.substring(0,
						allocation1.length() - 2));
			}

			if (double1 >= 0 && double2 >= 0) {
				Double double3 = (double1 / double2) * 10;
				rate = String.valueOf(double3);
				return rate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	public static String createUuid(final String... str) {
		final StringBuilder t_sb = new StringBuilder();
		for (final String t_s : str) {
			t_sb.append(t_s);
		}
		return UUID.nameUUIDFromBytes(t_sb.toString().getBytes()).toString();
	}

	/**
	 * 处理正则
	 * 
	 * @param guize
	 * @param str
	 * @return
	 */
	public String getMyZhengZe(String guize, String str) {
		System.out.println("处理的字符串：" + str);
		String pattern = guize;
		String line = str;
		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern);
		// 现在创建 matcher 对象
		Matcher m = r.matcher(line);
		if (m.find()) {
			return line;
		}
		/*
		 * else { System.out.println("没有找到匹配的正则!"); }
		 */
		return null;
	}

	/************************** vm *******************************/

	/**
	 * 虚拟机UUID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getVMUuid(String uuid) throws Exception {
		if (null != uuid) {
			return uuid;
		} else {
			return "";
		}
	}
	
	
	public String getVMNameLabel(String uuid) throws Exception {
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		VM virtualMachine = new VM(hostEntity.getIp().toString(),
				sshUtil.username, sshUtil.password, sshUtil.vmname);
//		virtualMachine.getVmInfo().
//		for (int i = 0; i < hostEntity.getVms().size(); i++) {
//			System.out.println("..............==============>"+hostEntity.getVms().get(i).toString());
//		}
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0;i < hostEntity.getVms().size(); i ++){
			for (Object listObject : hostEntity.getVms()) {
				System.out.println("!!!!!!!!!!!!!!-------->"+listObject);
				list.addAll((Collection<? extends String>) listObject);
			}
		}
		String bijiaodeuuid = virtualMachine.getVmInfo().get("uuid");
		if (null != bijiaodeuuid) {
			System.out.println("@1~~~~~~~~~~~~~~~~");
			for (int i = 0; i < virtualMachine.getVmInfo().get("uuid").length(); i++) {
				System.out.println("@3~~~~~~~~~~~~~~~~");
				bijiaodeuuid = virtualMachine.getVmInfo().get("uuid");
				if (uuid == bijiaodeuuid && uuid.equals(bijiaodeuuid)) {
					System.out.println("@4~~~~~~~~~~~~~~~~");
					System.out.println("@5-------------->"
							+ virtualMachine.getVmInfo().get("namelabel"));
					return virtualMachine.getVmInfo().get("namelabel");
				}
			}
		}
		return "";
	}
		
	/**
	 * 获取VM名字
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */

	/*@SuppressWarnings("unused")
	public String getVMNameLabel(String uuid) throws Exception {
		//通过UUID拿到对应的NAME
		System.out.println(("进入到了getVMNameLabel的方法1~~~~~~~~~~~~uuid--->"+uuid));
		log.error("进入到了getVMNameLabel的方法1~~~~~~~~~~~~uuid--->"+uuid);
		String name = null;
		@SuppressWarnings("rawtypes")
		List list = new ArrayList<String>();
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		VM virtualMachine = new VM(hostEntity.getIp().toString(),
				sshUtil.username, sshUtil.password, sshUtil.vmname);
		String[] allVm1 = virtualMachine.getUtil().getOut("virsh list --all");
		@SuppressWarnings("rawtypes")
		ArrayList vmList = null;
		vmList = getDiyTable(allVm1);
		for (int i = 0; i < vmList.size(); i++) {
			System.out.println(("进入到了vmList的方法2~~~~~~~~~~~~uuid--->"+uuid));
			log.error("进入到了vmList的方法2~~~~~~~~~~~~uuid--->"+uuid);
			
			String[] vms = (String[]) vmList.get(i);
			for (int j = 0; j < vms.length; j++) {
				System.out.println("vms--------------------------------->"
						+ vms[j]);
				String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
				*//**
				 * line和inffo要能直接匹配到
				 *//*
				String line = "\u0020" + vms[j];
				String pattern = zhengzeName;
				String uuidResult = null;
				String inffo = null;
				// 创建 Pattern 对象
				Pattern r = Pattern.compile(pattern);
				// 现在创建 matcher 对象
				Matcher m = r.matcher(line);
				if (m.find()) {
					
					System.out.println(("进入到了find的方法3~~~~~~~~~~~~uuid--->"+uuid));
					log.error("进入到了find的方法3~~~~~~~~~~~~uuid--->"+uuid);
					
					virtualMachine = new VM(hostEntity.getIp().toString(),
							sshUtil.username, sshUtil.password, m.group(1));
					globalVm = virtualMachine;
					String zijiVmUUID = virtualMachine.uuid;
				
					
					System.out.println(("进入到了zijiVmUUID的方法3~~~~~~~~~~~~zijiVmUUID--->"+zijiVmUUID));
					log.error("进入到了zijiVmUUID的方法3~~~~~~~~~~~~zijiVmUUID--->"+zijiVmUUID);
					
					String vmip = "vmip";
					name = virtualMachine.getVmname();
					log.error("虚拟机名称0---ip-->"+name+"   "+virtualMachine.getIp());
					System.out.println("name--->" + name);
					// String regUuid = "\\**UUID:\\s+(\\S+)";
					String regUuid = "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+";
					// String regUuid = "\\S+:\\s(\\S+)\\s+\\S+";
					String[] vminfo = virtualMachine.getUtil().getOut(
							"virsh dominfo " + name);
					for (String info : vminfo) {
						inffo = info;
						// System.out.println(info);
					}
					// ////////////////////////////////////
					if (null != inffo) {
						
						System.out.println(("进入到了if (null != inffo) {的方法4~~~~~~~~~~~~uuid--->"+uuid));
						log.error("进入到了if (null != inffo) {的方法4~~~~~~~~~~~~uuid--->"+uuid);
						
						uuidResult = getMyZhengZe(regUuid, inffo);
						
						System.out.println("进入到了if (null != inffo) {的方法4--->uuidResult------>"+uuidResult);
						
						if (null != uuidResult) {
							
							System.out.println(("进入到了uuidResult的方法5~~~~~~~~~~~~uuid--->"+uuid));
							log.error("进入到了if uuidResult的方法5~~~~~~~~~~~~uuid--->"+uuid);
							
							int ss = uuidResult.lastIndexOf(":") + 10;
							int bb = uuidResult.length() - 12;
							uuidResult = uuidResult.substring(
									(uuidResult.lastIndexOf(":") + 10),
									(uuidResult.length() - 12));
							if (uuid.equals(uuidResult)) {
								
								System.out.println(("进入到了(uuid.equals(uuidResult))的方法6~~~~~~~~~~~~uuid--->"+uuid));
								log.error("进入到了if (uuid.equals(uuidResult))的方法6~~~~~~~~~~~~uuid--->"+uuid);
							System.out.println("虚拟机名称1--->"+name); 
						 	
								log.error("虚拟机名称1--->"+name);
								return name;
							}
						}
						else
						{
							System.out.println(("进入到了uuidResult的方法7~~~~~~~~~~~~uuid--->"+uuid));
							log.error("进入到了if uuidResult的方法7~~~~~~~~~~~~uuid--->"+uuid);
							
							if (null!=uuid) {
								
								System.out.println(("进入到了(uuid.equals(uuidResult))的方法8~~~~~~~~~~~~uuid--->"+uuid));
								log.error("进入到了if (uuid.equals(uuidResult))的方法8~~~~~~~~~~~~uuid--->"+uuid);
						 	System.out.println("虚拟机名称1--->"+name); 
								log.error("虚拟机名称1--->"+name);
								
								return name;
							}
						}
					}
				}
			}
		}
		return "";
	}*/

	/**
	 * 虚拟机可用性
	 * 
	 * @return 0 -1
	 * @throws Exception
	 */
	/*
	 * public int getVmAvailability(String uuid) throws Exception { try { Host
	 * hostEntity = new Host(sshUtil.vmname, sshUtil.username,
	 * sshUtil.password); // VM virtualMachine = new
	 * VM(hostEntity.getIp().toString(), // sshUtil.username, sshUtil.password,
	 * sshUtil.vmname); if (null != uuid && !"".equals(uuid) && null != new
	 * VM(hostEntity.getIp().toString(), sshUtil.username, sshUtil.password,
	 * sshUtil.vmname)) { for (Map<String, String> iterable_element : new
	 * VM(hostEntity .getIp().toString(), sshUtil.username, sshUtil.password,
	 * sshUtil.vmname) .getVmAvailabilityImpl(uuid)) { if (null !=
	 * iterable_element) { if (iterable_element.containsValue("running")) {
	 * return 1; } else { return 0; } } else { return 0; } } } else { return 0;
	 * } } catch (Exception e) { return 0; } return 0; }
	 */

	public int getVMAvailability(String uuid) throws Exception {
		try {
			// log.error("王能的UUID----------------》"+uuid);
			Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
					sshUtil.password);
			// VM virtualMachine = new VM(hostEntity.getIp().toString(),
			// sshUtil.username, sshUtil.password, sshUtil.vmname);
			if (null != uuid
					&& !"".equals(uuid)
					&& null != new VM(hostEntity.getIp().toString(),
							sshUtil.username, sshUtil.password, sshUtil.vmname)) {
				for (Map<String, String> iterable_element : new VM(hostEntity
						.getIp().toString(), sshUtil.username,
						sshUtil.password, sshUtil.vmname)
						.getVmAvailabilityImpl(uuid)) {
					if (null != iterable_element) {
						if (iterable_element.containsValue("running")) {
							return 1;
						} else {
							return 0;
						}
					} else {
						return 0;
					}
				}
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

	/**
	 * 虚拟机CPU利用率
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getVMCpuRate(String uuid) throws Exception {
		return "0";
		/*
		 * String name = null;
		 * 
		 * @SuppressWarnings({ "rawtypes", "unused" }) List list = new
		 * ArrayList<String>(); Host hostEntity = new Host(sshUtil.vmname,
		 * sshUtil.username, sshUtil.password); virtualMachine = new
		 * VM(hostEntity.getIp().toString(), sshUtil.username, sshUtil.password,
		 * null); String[] allVm1 =
		 * virtualMachine.getUtil().getOut("virsh list --all");
		 * 
		 * @SuppressWarnings("rawtypes") ArrayList vmList = null; vmList =
		 * getDiyTable(allVm1); for (int i = 0; i < vmList.size(); i++) {
		 * String[] vms = (String[]) vmList.get(i); for (int j = 0; j <
		 * vms.length; j++) {
		 * System.out.println("vms--------------------------------->" + vms[j]);
		 * String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
		 *//**
		 * line和inffo要能直接匹配到
		 */
		/*
		 * String line = "\u0020" + vms[j]; String pattern = zhengzeName;
		 * 
		 * String uuidResult = null; String inffo = null; // 创建 Pattern 对象
		 * Pattern r = Pattern.compile(pattern); // 现在创建 matcher 对象 Matcher m =
		 * r.matcher(line); if (m.find()) { // System.out.println("Name:   " +
		 * m.group(1)); virtualMachine = new VM(hostEntity.getIp().toString(),
		 * sshUtil.username, sshUtil.password, m.group(1)); // globalVm =
		 * virtualMachine; // @SuppressWarnings("unused") // String zijiVmUUID =
		 * virtualMachine.uuid; // @SuppressWarnings("unused") // String vmip =
		 * "vmip"; name = virtualMachine.getVmname(); // String regUuid =
		 * "\\**UUID:\\s+(\\S+)"; String regUuid =
		 * "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+"; // String regUuid =
		 * "\\S+:\\s(\\S+)\\s+\\S+"; String[] vminfo =
		 * virtualMachine.getUtil().getOut( "virsh dominfo " + name); for
		 * (String info : vminfo) { inffo = info; // System.out.println(info); }
		 * // //////////////////////////////////// if (null != inffo) {
		 * uuidResult = getMyZhengZe(regUuid, inffo); if (null != uuidResult) {
		 * 
		 * @SuppressWarnings("unused") int ss = uuidResult.lastIndexOf(":") +
		 * 10;
		 * 
		 * @SuppressWarnings("unused") int bb = uuidResult.length() - 12;
		 * uuidResult = uuidResult.substring( (uuidResult.lastIndexOf(":") +
		 * 10), (uuidResult.length() - 12)); if (uuid.equals(uuidResult)) {
		 * ArrayList<Map<String, String>> vmCpuInfo = virtualMachine
		 * .getCpuInfo(); for (Map<String, String> vmMap : vmCpuInfo) { return
		 * vmMap.get("speed").substring(0, vmMap.get("speed").length() - 3); } }
		 * } } } } } return "";
		 */
	}

	/**
	 * 虚拟机内存利用率 GB
	 * 
	 * @return
	 * @throws Exception
	 */
	//用name拿
	public String getVMMemRate(String uuid) throws Exception {
		String name = null;
		@SuppressWarnings({ "rawtypes", "unused" })
		List list = new ArrayList<String>();
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		VM virtualMachine = new VM(hostEntity.getIp().toString(),
				sshUtil.username, sshUtil.password, sshUtil.vmname);
		String[] allVm1 = virtualMachine.getUtil().getOut("virsh list --all");
		@SuppressWarnings("rawtypes")
		ArrayList vmList = null;
		vmList = getDiyTable(allVm1);
		for (int i = 0; i < vmList.size(); i++) {
			String[] vms = (String[]) vmList.get(i);
			for (int j = 0; j < vms.length; j++) {
				System.out.println("vms--------------------------------->"
						+ vms[j]);
				String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
				/**
				 * line和inffo要能直接匹配到
				 */
				String line = "\u0020" + vms[j];
				String pattern = zhengzeName;

				String uuidResult = null;
				String inffo = null;
				// 创建 Pattern 对象
				Pattern r = Pattern.compile(pattern);
				// 现在创建 matcher 对象
				Matcher m = r.matcher(line);
				if (m.find()) {
					// System.out.println("Name:   " + m.group(1));
					virtualMachine = new VM(hostEntity.getIp().toString(),
							sshUtil.username, sshUtil.password, m.group(1));
					// globalVm = virtualMachine;
					// @SuppressWarnings("unused")
					// String zijiVmUUID = virtualMachine.uuid;
					// @SuppressWarnings("unused")
					// String vmip = "vmip";
					name = virtualMachine.getVmname();
					// String regUuid = "\\**UUID:\\s+(\\S+)";
					String regUuid = "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+";
					// String regUuid = "\\S+:\\s(\\S+)\\s+\\S+";
					String[] vminfo = virtualMachine.getUtil().getOut(
							"virsh dominfo " + name);
					for (String info : vminfo) {
						inffo = info;
						// System.out.println(info);
					}
					// ////////////////////////////////////
					if (null != inffo) {
						uuidResult = getMyZhengZe(regUuid, inffo);
						if (null != uuidResult) {
							@SuppressWarnings("unused")
							int ss = uuidResult.lastIndexOf(":") + 10;
							@SuppressWarnings("unused")
							int bb = uuidResult.length() - 12;
							uuidResult = uuidResult.substring(
									(uuidResult.lastIndexOf(":") + 10),
									(uuidResult.length() - 12));
							if (uuid.equals(uuidResult)) {
								ArrayList<Map<String, String>> vmMemInfo = virtualMachine
										.getMemoryInfo();
								for (Map<String, String> vmMap : vmMemInfo) {
									Double maxMem = Double.valueOf(vmMap.get(
											"memorytotal")
											.substring(
													0,
													vmMap.get("memorytotal")
															.length() - 3));
									System.out.println("MAX MEM====>" + maxMem);
									Double usedMem = Double.valueOf(vmMap.get(
											"memorycached")
											.substring(
													0,
													vmMap.get("memorycached")
															.length() - 3));
									System.out
											.println("MAX MEM====>" + usedMem);
									if (maxMem >= 0 && usedMem >= 0) {
										System.out
												.println("内存利用率"
														+ String.valueOf(formatDouble1(usedMem
																/ maxMem * 100)));
										return String
												.valueOf(formatDouble1(usedMem
														/ maxMem * 100));
									}
								}
							}
						}
					}
				}
			}
		}
		return "0";
	}

	/**
	 * 虚拟机.总内存
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getVMTotalMem(String uuid) throws Exception {
		String name = null;
		@SuppressWarnings({ "rawtypes", "unused" })
		List list = new ArrayList<String>();
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		VM virtualMachine = new VM(hostEntity.getIp().toString(),
				sshUtil.username, sshUtil.password, sshUtil.vmname);
		String[] allVm1 = virtualMachine.getUtil().getOut("virsh list --all");
		@SuppressWarnings("rawtypes")
		ArrayList vmList = null;
		vmList = getDiyTable(allVm1);
		for (int i = 0; i < vmList.size(); i++) {
			String[] vms = (String[]) vmList.get(i);
			for (int j = 0; j < vms.length; j++) {
				System.out.println("vms--------------------------------->"
						+ vms[j]);
				String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
				/**
				 * line和inffo要能直接匹配到
				 */
				String line = "\u0020" + vms[j];
				String pattern = zhengzeName;

				String uuidResult = null;
				String inffo = null;
				// 创建 Pattern 对象
				Pattern r = Pattern.compile(pattern);
				// 现在创建 matcher 对象
				Matcher m = r.matcher(line);
				if (m.find()) {
					// System.out.println("Name:   " + m.group(1));
					virtualMachine = new VM(hostEntity.getIp().toString(),
							sshUtil.username, sshUtil.password, m.group(1));
					// globalVm = virtualMachine;
					// @SuppressWarnings("unused")
					// String zijiVmUUID = virtualMachine.uuid;
					// @SuppressWarnings("unused")
					// String vmip = "vmip";
					name = virtualMachine.getVmname();
					// String regUuid = "\\**UUID:\\s+(\\S+)";
					String regUuid = "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+";
					// String regUuid = "\\S+:\\s(\\S+)\\s+\\S+";
					String[] vminfo = virtualMachine.getUtil().getOut(
							"virsh dominfo " + name);
					for (String info : vminfo) {
						inffo = info;
						// System.out.println(info);
					}
					// ////////////////////////////////////
					if (null != inffo) {
						uuidResult = getMyZhengZe(regUuid, inffo);
						if (null != uuidResult) {
							@SuppressWarnings("unused")
							int ss = uuidResult.lastIndexOf(":") + 10;
							@SuppressWarnings("unused")
							int bb = uuidResult.length() - 12;
							uuidResult = uuidResult.substring(
									(uuidResult.lastIndexOf(":") + 10),
									(uuidResult.length() - 12));
							if (uuid.equals(uuidResult)) {
								ArrayList<Map<String, String>> vmMemInfo = virtualMachine
										.getMemoryInfo();
								for (Map<String, String> vmMap : vmMemInfo) {
									Double maxMem = Double.valueOf(vmMap.get(
											"memorytotal")
											.substring(
													0,
													vmMap.get("memorytotal")
															.length() - 3));
									return String.valueOf(maxMem);
								}
							}
						}
					}
				}
			}
		}
		return "";
	}

	/**
	 * 虚拟机.剩余内存
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getVMFreeMem(String uuid) throws Exception {
		String name = null;
		@SuppressWarnings({ "rawtypes", "unused" })
		List list = new ArrayList<String>();
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		VM virtualMachine = new VM(hostEntity.getIp().toString(),
				sshUtil.username, sshUtil.password, sshUtil.vmname);
		String[] allVm1 = virtualMachine.getUtil().getOut("virsh list --all");
		@SuppressWarnings("rawtypes")
		ArrayList vmList = null;
		vmList = getDiyTable(allVm1);
		for (int i = 0; i < vmList.size(); i++) {
			String[] vms = (String[]) vmList.get(i);
			for (int j = 0; j < vms.length; j++) {
				System.out.println("vms--------------------------------->"
						+ vms[j]);
				String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
				/**
				 * line和inffo要能直接匹配到
				 */
				String line = "\u0020" + vms[j];
				String pattern = zhengzeName;

				String uuidResult = null;
				String inffo = null;
				// 创建 Pattern 对象
				Pattern r = Pattern.compile(pattern);
				// 现在创建 matcher 对象
				Matcher m = r.matcher(line);
				if (m.find()) {
					// System.out.println("Name:   " + m.group(1));
					virtualMachine = new VM(hostEntity.getIp().toString(),
							sshUtil.username, sshUtil.password, m.group(1));
					// globalVm = virtualMachine;
					// @SuppressWarnings("unused")
					// String zijiVmUUID = virtualMachine.uuid;
					// @SuppressWarnings("unused")
					// String vmip = "vmip";
					name = virtualMachine.getVmname();
					// String regUuid = "\\**UUID:\\s+(\\S+)";
					String regUuid = "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+";
					// String regUuid = "\\S+:\\s(\\S+)\\s+\\S+";
					String[] vminfo = virtualMachine.getUtil().getOut(
							"virsh dominfo " + name);
					for (String info : vminfo) {
						inffo = info;
						// System.out.println(info);
					}
					// ////////////////////////////////////
					if (null != inffo) {
						uuidResult = getMyZhengZe(regUuid, inffo);
						if (null != uuidResult) {
							@SuppressWarnings("unused")
							int ss = uuidResult.lastIndexOf(":") + 10;
							@SuppressWarnings("unused")
							int bb = uuidResult.length() - 12;
							uuidResult = uuidResult.substring(
									(uuidResult.lastIndexOf(":") + 10),
									(uuidResult.length() - 12));
							if (uuid.equals(uuidResult)) {
								ArrayList<Map<String, String>> vmMemInfo = virtualMachine
										.getMemoryInfo();
								for (Map<String, String> vmMap : vmMemInfo) {
									Double maxMem = Double.valueOf(vmMap.get(
											"memoryfree")
											.substring(
													0,
													vmMap.get("memoryfree")
															.length() - 3));
									return String.valueOf(maxMem);
								}
							}
						}
					}
				}
			}
		}
		return "";
	}

	/**
	 * 虚拟机.已使用内存
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getVMCachedMem(String uuid) throws Exception {
		String name = null;
		@SuppressWarnings({ "rawtypes", "unused" })
		List list = new ArrayList<String>();
		Host hostEntity = new Host(sshUtil.vmname, sshUtil.username,
				sshUtil.password);
		VM virtualMachine = new VM(hostEntity.getIp().toString(),
				sshUtil.username, sshUtil.password, sshUtil.vmname);
		String[] allVm1 = virtualMachine.getUtil().getOut("virsh list --all");
		@SuppressWarnings("rawtypes")
		ArrayList vmList = null;
		vmList = getDiyTable(allVm1);
		for (int i = 0; i < vmList.size(); i++) {
			String[] vms = (String[]) vmList.get(i);
			for (int j = 0; j < vms.length; j++) {
				System.out.println("vms--------------------------------->"
						+ vms[j]);
				String zhengzeName = "\u0020[\\d|-]+\\s+(\\S+)";
				/**
				 * line和inffo要能直接匹配到
				 */
				String line = "\u0020" + vms[j];
				String pattern = zhengzeName;

				String uuidResult = null;
				String inffo = null;
				// 创建 Pattern 对象
				Pattern r = Pattern.compile(pattern);
				// 现在创建 matcher 对象
				Matcher m = r.matcher(line);
				if (m.find()) {
					// System.out.println("Name:   " + m.group(1));
					virtualMachine = new VM(hostEntity.getIp().toString(),
							sshUtil.username, sshUtil.password, m.group(1));
					// globalVm = virtualMachine;
					// @SuppressWarnings("unused")
					// String zijiVmUUID = virtualMachine.uuid;
					// @SuppressWarnings("unused")
					// String vmip = "vmip";
					name = virtualMachine.getVmname();
					// String regUuid = "\\**UUID:\\s+(\\S+)";
					String regUuid = "\\S+\\s+\\S+:\\s(\\S+)\\s+\\S+";
					// String regUuid = "\\S+:\\s(\\S+)\\s+\\S+";
					String[] vminfo = virtualMachine.getUtil().getOut(
							"virsh dominfo " + name);
					for (String info : vminfo) {
						inffo = info;
						// System.out.println(info);
					}
					// ////////////////////////////////////
					if (null != inffo) {
						uuidResult = getMyZhengZe(regUuid, inffo);
						if (null != uuidResult) {
							@SuppressWarnings("unused")
							int ss = uuidResult.lastIndexOf(":") + 10;
							@SuppressWarnings("unused")
							int bb = uuidResult.length() - 12;
							uuidResult = uuidResult.substring(
									(uuidResult.lastIndexOf(":") + 10),
									(uuidResult.length() - 12));
							if (uuid.equals(uuidResult)) {
								ArrayList<Map<String, String>> vmMemInfo = virtualMachine
										.getMemoryInfo();
								for (Map<String, String> vmMap : vmMemInfo) {
									Double maxMem = Double.valueOf(vmMap.get(
											"memorycached")
											.substring(
													0,
													vmMap.get("memorycached")
															.length() - 3));
									return String.valueOf(maxMem);
								}
							}
						}
					}
				}
			}
		}
		return "";
	}

	/************************** end vm ***************************/

	public static double formatDouble1(double d) {
		return (double) Math.round(d * 100) / 100;
	}

	public void dispose() {
		try {
			sshUtil.getOut();
		} catch (Exception e) {
			if(log.isDebugEnabled())
			{
			log.error("empty connection failure:"+e);
		}}
	}
}
