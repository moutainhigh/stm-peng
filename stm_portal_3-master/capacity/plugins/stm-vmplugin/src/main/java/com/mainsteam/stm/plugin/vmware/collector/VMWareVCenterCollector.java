package com.mainsteam.stm.plugin.vmware.collector;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.vmware.constants.CommonConstants;
import com.mainsteam.stm.plugin.vmware.util.VSphereUtil;
import com.mainsteam.stm.plugin.vmware.vo.ResourceTree;
import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostHostBusAdapter;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PhysicalNic;
import com.vmware.vim25.ScsiLun;
import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.util.MorUtil;

/**
 * 子资源+资源池（单层）合并版
 * 
 * @author yuanlb
 * @startTime 2016年8月10日下午9:14:36
 * @description TODO
 * @editTime 2016年8月10日下午9:14:36
 */
public class VMWareVCenterCollector extends VMWareBaseCollector {
	private static final Log LOGGER = LogFactory
			.getLog(VMWareVCenterCollector.class);

	private static final String SUMMARY_CONFIG_INSTANCE_UUID = "summary.config.instanceUuid";

	public int getAvailability(JBrokerParameter parameter) {
		try {
			ServiceInstance si = (ServiceInstance) parameter.getConnection();
			if (null != si && null != si.getServerClock()) {
				return CommonConstants.AVAIL;
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return CommonConstants.UN_AVAIL;
	}

	public String getUUID(JBrokerParameter parameter) {
		final String t_name = parameter.getIp();
		final String t_fullName = "/" + t_name;
		final String t_uuid = VSphereUtil.createUuid(t_fullName);
		return t_uuid;
	}

	public static void main(String[] args) throws Exception {
		JBrokerParameter parameter = new JBrokerParameter();
		parameter.setIp("192.168.1.235");
		parameter.setUsername("administrator");
		parameter.setPassword("root");
		URL url = new URL("https", parameter.getIp(), "/sdk");
		ServiceInstance serviceInstance = new ServiceInstance(url,
				parameter.getUsername(), parameter.getPassword(), true);
		parameter.setConnection(serviceInstance);
		VMWareVCenterCollector vMWareVCenterCollector = new VMWareVCenterCollector();
		System.out.println("tree:");
		String str4 = vMWareVCenterCollector.getResourceTree(parameter);
		System.out.println(str4);
	}

	public String getResourceTree(JBrokerParameter parameter) {

		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			final Folder t_root = si.getRootFolder();
			final String t_name = parameter.getIp();
			final String t_fullName = "/" + t_name;
			final String t_uuid = VSphereUtil.createUuid(t_fullName);
			ResourceTree rootResource = createTree(t_name, C_VCENTER, t_uuid,
					"", t_fullName);
			try {
				System.out.println("||||||||||"+rootResource+"\r"+si+"\r"+t_root+"\r"+t_fullName+"|||||");
				createTreeEntity(rootResource, si, t_root, t_fullName);
			} catch (final Exception t_e) {
				LOGGER.error("ResourceTree: " + t_e.getMessage(), t_e);
			}
			String rr = JSON.toJSONString(rootResource);
			return rr;
		}
		return null;
	}

	public static String getResourcePoolUuid(ResourcePool pool) {
		String url = getResourcePoolUrl(pool);
		return createResourcePoolUUID(new String[] { url });
	}

	public static String getResourcePoolUrl(ResourcePool pool) {
		return pool.getSummary().getName();
	}

	public static String createResourcePoolUUID(String[] str) {
		String x = str[0];
		int uIdx = StringUtils.lastIndexOf(x, '/', x.length() - 2);
		String uuid = "";
		if (StringUtils.endsWith(x, "/"))
			uuid = x.substring(uIdx + 1, x.length() - 1);
		else {
			uuid = x.substring(uIdx + 1);
		}
		if ((StringUtils.contains(uuid, "-")) && (uuid.length() == 35)) {
			return uuid;
		}
		return createUuid(new String[] { uuid });
	}

	public static String createUuid(String[] str) {
		StringBuilder t_sb = new StringBuilder();
		for (String t_s : str) {
			t_sb.append(t_s);
		}
		return UUID.nameUUIDFromBytes(t_sb.toString().getBytes()).toString();
	}

	private ResourceTree createTreeEntity(final ResourceTree childRT,
			final ServiceInstance si, final Folder parent,
			final String parentFullName) throws Exception {
		final ManagedObjectReference[] t_mors = (ManagedObjectReference[]) parent
				.getPropertyByPath("childEntity");

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("createTreeEntity parentFullName="+parentFullName+" child.size="+(null == t_mors ? 0:t_mors.length));
		}
		if (null != t_mors && t_mors.length > 0) {
			for (final ManagedObjectReference t_mor : t_mors) {
				final ManagedEntity t_me = MorUtil.createExactManagedEntity(
						si.getServerConnection(), t_mor);
				final String t_name = t_me.getName();
				final String t_fullName = parentFullName + "/" + t_name;
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("createTreeEntity begin deal with "+t_fullName);
				}
				if (t_me instanceof Datacenter) {
					final String t_uuid = VSphereUtil.createUuid(t_fullName);
					ResourceTree dcTree = createTree(t_name, C_DATACENTER,
							t_uuid, "", t_fullName);
					childRT.addResource(dcTree);
					final Datacenter t_dc = (Datacenter) t_me;
					final Folder t_hsFolder = t_dc.getHostFolder();
					final Folder t_dsFolder = t_dc.getDatastoreFolder();
					// 主机文件夹
					createTreeEntity(dcTree, si, t_hsFolder, t_fullName);
					// 数据存储文件夹
					createTreeEntity(dcTree, si, t_dsFolder, t_fullName);
				} else if (t_me instanceof ClusterComputeResource) {
					// 群集
					final String t_uuid = VSphereUtil.createUuid(t_name);
					ResourceTree clusterRT = createTree(t_name, C_CLUSTER,
							t_uuid, "", t_fullName);
					childRT.addResource(clusterRT);
					final ManagedEntity[] t_hMes = getManagedEntity(si, t_me,
							C_HOST);
					for (final ManagedEntity t_hMe : t_hMes) {
						ResourceTree hostRT = createTreeByME(C_HOST,
								t_fullName, t_hMe);
						if (null != hostRT) {
							clusterRT.addResource(hostRT);
						}

						// 新增资源池
						ManagedEntity[] t_clusterPoolMes = getManagedEntity(si,
								t_me, "ResourcePool");
						for (ManagedEntity t_clusterPoolMe : t_clusterPoolMes) {
							ResourceTree clusterResourcepoolRT = createTreeByME(
									"ResourcePool", t_fullName, t_clusterPoolMe);
							if (!clusterResourcepoolRT.getName().equals(
									"Resources")) {
								System.out.println("poolRT"
										+ clusterResourcepoolRT.getName());
								hostRT.addResource(clusterResourcepoolRT);
							}
							if (null != t_clusterPoolMe) {
								ResourcePool pool = (ResourcePool) t_clusterPoolMe;
								for (ManagedEntity t_vm : pool.getVMs()) {
									// if ((t_vm.getName().contains("cdzw-"))
									// || (t_vm.getName()
									// .contains("CDZW-"))
									// || (t_vm.getName().contains("资源池-"))
									// || (t_vm.getName()
									// .contains("ResoucePool-"))
									// || (t_vm.getName()
									// .contains("ziyuanchi-"))
									// || (t_vm.getName()
									// .contains("resourcepool-"))) {
									ResourceTree vmRT = createTreeByME(
											"VirtualMachine", t_fullName, t_vm);
									if (pool.getParent().getName()
											.equals("Resources")) {
										System.out.println(new StringBuilder()
												.append("集群下.资源池.虚拟机：")
												.append(vmRT.getName())
												.toString());
									}
									if (null == vmRT)
										continue;
									clusterResourcepoolRT.addResource(vmRT);
									// }
								}
							}
						}
						// //////////////////////////////////

						final ManagedEntity[] t_vmMes = getManagedEntity(si,
								t_hMe, C_VM);
						for (final ManagedEntity t_vmMe : t_vmMes) {
							ResourceTree vmRT = createTreeByME(C_VM,
									t_fullName, t_vmMe);
							if (null != vmRT) {
								hostRT.addResource(vmRT);
							}
						}
						/*
						 * 屏蔽子资源相关
						 * 
						 * //cpu HostSystem hs = (HostSystem)t_hMe;
						 * HostCpuPackage[] cpuPkgs =
						 * hs.getHardware().getCpuPkg(); if (cpuPkgs != null) {
						 * for (HostCpuPackage hostCpu : cpuPkgs) { String
						 * cpuName = C_CPU + hostCpu.getIndex(); String cpuUUID
						 * = createUuid(t_uuid,
						 * String.valueOf(hostCpu.getIndex()));
						 * ResourceTree.SubResource subResource = hostRT.new
						 * SubResource( cpuUUID, cpuName, C_CPU,
						 * "esxCPU",cpuName);
						 * hostRT.addSubResource(subResource); } }
						 * 
						 * // 网卡列表 PhysicalNic[] nics =
						 * hs.getConfig().getNetwork() .getPnic(); for
						 * (PhysicalNic nic : nics) { String nicName =
						 * nic.getDevice(); // 网卡名称 String nicUUID =
						 * createUuid(t_uuid, nicName);// 网卡ID
						 * ResourceTree.SubResource subResource = hostRT.new
						 * SubResource( nicUUID, nicName, C_NET_INTERFACE,
						 * "esxHostInterface",nicName);
						 * hostRT.addSubResource(subResource); }
						 */
						// 集群下的
						if (t_me instanceof ComputeResource) {
							// 主机.存储适配器
							String esxiUuid = VSphereUtil
									.getHostUuid((HostSystem) t_hMe);
							HostHostBusAdapter[] hostHostBusAdapters = ((HostSystem) t_hMe)
									.getConfig().getStorageDevice()
									.getHostBusAdapter();
							if (null != hostHostBusAdapters) {
								for (HostHostBusAdapter dtAdapter : hostHostBusAdapters) {
									String storageAdapterName = dtAdapter
											.getDevice();
									String storageAdapterUuid = VSphereUtil
											.createUuid(storageAdapterName);
									String storageAdapterId = dtAdapter
											.getKey();
									/******************************************************/
									// 第三层
									HostConfigInfo hostConfigManager = ((HostSystem) t_hMe)
											.getConfig();
									for (ScsiLun sonSon : hostConfigManager
											.getStorageDevice().getScsiLun()) {
										if (sonSon.getDeviceName()
												.substring(24, 31)
												.equals(storageAdapterName)
												|| sonSon
														.getDeviceName()
														.substring(24, 30)
														.equals(storageAdapterName)
												|| sonSon
														.getDeviceName()
														.substring(24, 32)
														.equals(storageAdapterName)) {
											String singleStorageAdapterUuid = storageAdapterUuid
													+ "-"
													+ sonSon.getDeviceName();
											System.out
													.println("集群下适配器的结构中的第三层的组合ID："
															+ singleStorageAdapterUuid);// 20
											LOGGER.error("集群下适配器的结构中的第三层的组合ID："
													+ singleStorageAdapterUuid);
											// 第三层独有的拼接ID的结构
											ResourceTree.SubResource subResource2 = hostRT.new SubResource(
													storageAdapterId,
													storageAdapterName,
													C_ESXSTORAGEADAPTER,
													C_ESXSTORAGEADAPTER,
													singleStorageAdapterUuid,
													t_uuid);
											hostRT.addSubResource(subResource2);
											break;
										}
										ResourceTree.SubResource subResource = hostRT.new SubResource(
												storageAdapterId,
												storageAdapterName,
												C_ESXSTORAGEADAPTER,
												C_ESXSTORAGEADAPTER,
												storageAdapterUuid, esxiUuid);
										hostRT.addSubResource(subResource);
										break;
									}
								}
								/**********************************************************************************************/

								// 主机.网络适配器
								PhysicalNic[] physicalNics = ((HostSystem) t_hMe)
										.getConfig().getNetwork().getPnic();
								for (PhysicalNic physicalNic : physicalNics) {
									String netWorkAdapterName = physicalNic
											.getDevice();
									String netWorkAdapterId = physicalNic
											.getKey();
									String netWorkAdapterUuid = VSphereUtil
											.createUuid(netWorkAdapterName);
									ResourceTree.SubResource subResource = hostRT.new SubResource(
											netWorkAdapterId,
											netWorkAdapterName,
											C_ESXNETWORKADAPTER,
											C_ESXNETWORKADAPTER,
											netWorkAdapterUuid, esxiUuid);
									hostRT.addSubResource(subResource);
								}
							}

						}
						// //////////////////////////////////////////////////////////////////
					}
				} else if (t_me instanceof Datastore) {
					// 存储
					ResourceTree dsRT = createTreeByME(C_DATASTORE,
							parentFullName, t_me);
					if (null != dsRT) {
						childRT.addResource(dsRT);
					}
				} else if (t_me instanceof Folder) {
					// 文件夹
					final String t_uuid = VSphereUtil.createUuid(t_fullName);
					ResourceTree rt = createTree(t_name, "Folder", t_uuid, "",
							t_fullName);
					createTreeEntity(rt, si, (Folder) t_me, t_fullName);
					childRT.addResource(rt);
				} else if (t_me instanceof ComputeResource) {
					// 主机
					// ///////////// 资源池/////////////////
					ManagedEntity[] t_poolMes = getManagedEntity(si, t_me,
							"ResourcePool");
					List<ResourceTree> poolTrees = new ArrayList<>(t_poolMes.length);
					for (ManagedEntity t_poolMe : t_poolMes) {
						ResourceTree poolRT = createTreeByME("ResourcePool",
								t_fullName, t_poolMe);
						if (null != poolRT) {
							String jiequStr = poolRT.getUuid().substring(0,
									(poolRT.getUuid().length() - 5));
							poolRT.setUuid(jiequStr + "jason");
							System.out
									.println("lasjdlfjalksjfljaslkdjf=--------->"
											+ poolRT.getUuid());
							if (!poolRT.getName().equals("Resources")) {
								System.out.println("poolRT" + poolRT.getName());
								poolTrees.add(poolRT);
							}
						}
						if (null != t_poolMe) {
							ResourcePool pool = (ResourcePool) t_poolMe;
							// 控制虚拟机
							for (ManagedEntity t_vm : pool.getVMs()) {
								// if ((t_vm.getName().contains("cdzw-"))
								// || (t_vm.getName().contains("CDZW-"))
								// || (t_vm.getName().contains("资源池-"))
								// || (t_vm.getName()
								// .contains("ResoucePool-"))
								// || (t_vm.getName()
								// .contains("ziyuanchi-"))
								// || (t_vm.getName()
								// .contains("resourcepool-"))) {

								ResourceTree vmRT = createTreeByME(
										"VirtualMachine", t_fullName, t_vm);
								// 屏蔽resourcepool同级的主机下的虚拟机
								// System.out.println("~~~~~~~~~>"+pool.getParent().getName());
								if (pool.getParent().getName()
										.equals("Resources")) {
									System.out.println(new StringBuilder()
											.append("主机下.资源池.虚拟机：")
											.append(vmRT.getName()).toString());
								}
								if (null == vmRT)
									continue;
								poolRT.addResource(vmRT);
								// }
							}
						}
					}
					
					HostSystem[] hosts = ((ComputeResource)t_me).getHosts();
					for (HostSystem t_hs : hosts) {
						final String t_uuid = VSphereUtil.getHostUuid(t_hs);
						final String hostIp = VSphereUtil.getHostIp(t_hs);
						ResourceTree hostRT = createTree(t_name, C_HOST, t_uuid,
								hostIp, t_fullName);
						// /////////////////////////////////////
						// 主机下的VM
						final ManagedEntity[] t_vmMes = getManagedEntity(si, t_me,
								C_VM);
						for (final ManagedEntity t_vmMe : t_vmMes) {
							ResourceTree vmRT = createTreeByME(C_VM, t_fullName,
									t_vmMe);
							if (null != vmRT) {
								hostRT.addResource(vmRT);
							}
						}
						
						// HostCpuPackage[] cpuPkgs =
						// t_hs.getHardware().getCpuPkg();
						// if (cpuPkgs != null) {
						// for (HostCpuPackage hostCpu : cpuPkgs) {
						// String cpuName = C_CPU + hostCpu.getIndex();
						// String cpuUUID = createUuid(t_uuid,
						// String.valueOf(hostCpu.getIndex()));
						// ResourceTree.SubResource subResource = hostRT.new
						// SubResource(
						// cpuUUID, cpuName, C_CPU, "esxCPU",cpuName);
						// hostRT.addSubResource(subResource);
						// }
						// }
						//
						// // 网卡列表
						// PhysicalNic[] nics = t_hs.getConfig().getNetwork()
						// .getPnic();
						// for (PhysicalNic nic : nics) {
						// String nicName = nic.getDevice(); // 网卡名称
						// String nicUUID = createUuid(t_uuid, nicName);// 网卡ID
						// ResourceTree.SubResource subResource = hostRT.new
						// SubResource(
						// nicUUID, nicName, C_NET_INTERFACE,
						// "esxHostInterface",nicName);
						// hostRT.addSubResource(subResource);
						// }
						// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+t_hs.getName()+"\r");
						// String esxiUuid = VSphereUtil.getHostUuid((HostSystem)
						// t_me);
						// ///////主机下的 主机.存储适配器////////
						HostHostBusAdapter[] hostHostBusAdapters = t_hs.getConfig()
								.getStorageDevice().getHostBusAdapter();
						System.out.println("t_uuid==================>" + t_uuid);
						if (null != hostHostBusAdapters) {
							for (HostHostBusAdapter dtAdapter : hostHostBusAdapters) {
								String storageAdapterId = dtAdapter.getKey();
								String storageAdapterName = dtAdapter.getDevice();
								String storageAdapterUuid = VSphereUtil
										.createUuid(storageAdapterName);

								/*******************************************************************************************/
								// 第三层
								HostConfigInfo hostConfigManager = t_hs.getConfig();
								for (ScsiLun sonSon : hostConfigManager
										.getStorageDevice().getScsiLun()) {
									if (sonSon.getDeviceName().substring(24, 31)
											.equals(storageAdapterName)
											|| sonSon.getDeviceName()
													.substring(24, 30)
													.equals(storageAdapterName)
											|| sonSon.getDeviceName()
													.substring(24, 32)
													.equals(storageAdapterName)) {
										System.out
												.println("sonSon.getDeviceName()~~~~~~~~~~~~~~~~~~~~~~~>"
														+ sonSon.getDeviceName());
										String singleStorageAdapterUuid = storageAdapterUuid
												+ "-" + sonSon.getDeviceName();
										System.out.println("主机下的适配器的结构中的第三层的组合ID："
												+ singleStorageAdapterUuid);// 194
										LOGGER.error("主机下的适配器的结构中的第三层的组合ID："
												+ singleStorageAdapterUuid);
										// 第三层独有的拼接ID的结构
										ResourceTree.SubResource subResource2 = hostRT.new SubResource(
												storageAdapterId,
												storageAdapterName,
												C_ESXSTORAGEADAPTER,
												C_ESXSTORAGEADAPTER,
												singleStorageAdapterUuid, t_uuid);
										hostRT.addSubResource(subResource2);
										break;
									}

									ResourceTree.SubResource subResource = hostRT.new SubResource(
											storageAdapterId, storageAdapterName,
											C_ESXSTORAGEADAPTER,
											C_ESXSTORAGEADAPTER,
											storageAdapterUuid, t_uuid);
									hostRT.addSubResource(subResource);
									break;
								}
								/*****************************************************/
							}

							// ///////////主机.网络适配器///////////
							PhysicalNic[] physicalNics = t_hs.getConfig()
									.getNetwork().getPnic();
							for (PhysicalNic physicalNic : physicalNics) {
								String netWorkAdapterId = physicalNic.getKey();
								String netWorkAdapterName = physicalNic.getDevice();
								String netWorkAdapterUuid = VSphereUtil
										.createUuid(netWorkAdapterName);
								ResourceTree.SubResource subResource = hostRT.new SubResource(
										netWorkAdapterId, netWorkAdapterName,
										C_ESXNETWORKADAPTER, C_ESXNETWORKADAPTER,
										netWorkAdapterUuid, t_uuid);

								hostRT.addSubResource(subResource);
							}
						}
						childRT.addResource(hostRT);
					}
				}
			}
		}
		return childRT;
	}

	private ResourceTree createTreeByME(final String type,
			final String parentFullName, final ManagedEntity me) {

		final String t_name = me.getName();
		final String t_fullName = parentFullName + "/" + t_name;
		// 资源池
		if (type.equals("ResourcePool")) {
			ResourcePool pool = (ResourcePool) me;

			String t_uuid = VSphereUtil.createUuid(new String[] { pool
					.getName() });

			if (StringUtils.isEmpty(t_uuid)) {
				return null;
			}
			ResourceTree rt = createTree(t_name, "ResourcePool", t_uuid,
					"poolIp", "poolFullName");
			return rt;
		}
		if (type.equals(C_VM)) {
			VirtualMachine vm = (VirtualMachine) me;
			final String t_uuid = (String) vm
					.getPropertyByPath(SUMMARY_CONFIG_INSTANCE_UUID);
			if (StringUtils.isEmpty(t_uuid)) {
				return null;
			}
			String ip = VSphereUtil.getVMIP(vm);
			ResourceTree rt = createTree(t_name, C_VM, t_uuid, ip, t_fullName);
			return rt;
		} else if (type.equals(C_HOST)) {
			HostSystem hs = (HostSystem) me;
			final String t_uuid = VSphereUtil.getHostUuid(hs);
			if (StringUtils.isEmpty(t_uuid)) {
				return null;
			}
			final String hostIp = VSphereUtil.getHostIp(hs);
			ResourceTree rt = createTree(t_name, C_HOST, t_uuid, hostIp,
					t_fullName);
			return rt;
		} else if (type.equals(C_DATASTORE)) {
			Datastore ds = (Datastore) me;
			final String uuidDS = VSphereUtil.getDatastoreUUID(ds);
			if (StringUtils.isEmpty(uuidDS)) {
				return null;
			}
			ResourceTree rt = createTree(t_name, C_DATASTORE, uuidDS, "",
					t_fullName);
			return rt;
		}
		return null;
	}

	private ResourceTree createTree(final String name, final String type,
			final String uuid, final String ip, final String fullName) {
		return new ResourceTree(name, type, uuid, ip, fullName);
	}

	public String getName(final JBrokerParameter parameter) {
		String ip = parameter.getIp();
		if (null != ip) {
			return ip;
		}
		return "";
	}
}
