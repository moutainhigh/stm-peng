package com.mainsteam.stm.plugin.xen.collect;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.XmlRpcException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.xen.bo.ConnectionInfo;
import com.mainsteam.stm.plugin.xen.bo.XenResourceNode;
import com.mainsteam.stm.plugin.xen.dict.XenResourceID;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.PBD;
import com.xensource.xenapi.Pool;
import com.xensource.xenapi.SR;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.HostIsSlave;
import com.xensource.xenapi.Types.SessionAuthenticationFailed;
import com.xensource.xenapi.Types.VmPowerState;
import com.xensource.xenapi.Types.XenAPIException;
import com.xensource.xenapi.VM;

public class XenCollector {

	private static final String TRUE = "1";
	private static final String FALSE = "0";
	private static final int TIME_WINDOW = 180;

	private Connection connection;
	private ConnectionInfo connectionInfo;

	public XenCollector(ConnectionInfo connectionInfo) throws MalformedURLException, BadServerResponse, SessionAuthenticationFailed, HostIsSlave, XenAPIException,
			XmlRpcException {
		this.connectionInfo = connectionInfo;
		connection = new Connection(new URL("http://" + connectionInfo.host));
		Session.loginWithPassword(connection, connectionInfo.username, connectionInfo.password, APIVersion.latest().toString());
	}

	public String getResourceTree() throws BadServerResponse, XenAPIException, XmlRpcException {
		Collection<Pool.Record> poolRecords = Pool.getAllRecords(connection).values();
		Pool.Record poolRecord = (Pool.Record) poolRecords.toArray()[0];
		XenResourceNode root = new XenResourceNode(XenResourceID.XEN_POOL, poolRecord.uuid, null, poolRecord.nameLabel, XenResourceID.XEN_POOL);
		discoveryXen(root);
		return JSON.toJSONString(root);
	}

	private void discoveryXen(XenResourceNode currentNode) throws BadServerResponse, XenAPIException, XmlRpcException {
		switch (currentNode.getResourceId()) {
		case XenResourceID.XEN_POOL:
			Collection<Host.Record> hostRecords = Host.getAllRecords(connection).values();
			for (Host.Record hostRecord : hostRecords) {
				XenResourceNode hostNode = new XenResourceNode(XenResourceID.XEN_HOST, hostRecord.uuid, hostRecord.address, hostRecord.nameLabel, XenResourceID.XEN_HOST);
				currentNode.getChildTrees().add(hostNode);
				discoveryXen(hostNode);
			}
			Collection<SR.Record> srRecords = SR.getAllRecords(connection).values();
			for (SR.Record srRecord : srRecords) {
				if (srRecord.shared && srRecord.physicalSize >= 0) {
					XenResourceNode srNode = new XenResourceNode(XenResourceID.XEN_SR, srRecord.uuid, null, srRecord.nameLabel, XenResourceID.XEN_SR);
					currentNode.getChildTrees().add(srNode);
					discoveryXen(srNode);
				}
			}
			Collection<VM.Record> vmRecords = VM.getAllRecords(connection).values();
			for (VM.Record vmRecord : vmRecords) {
				if (!vmRecord.isATemplate && !vmRecord.isControlDomain && vmRecord.residentOn.isNull()) {
					XenResourceNode vmNode = new XenResourceNode(XenResourceID.XEN_VM, vmRecord.uuid, null, vmRecord.nameLabel, XenResourceID.XEN_VM);
					currentNode.getChildTrees().add(vmNode);
					discoveryXen(vmNode);
				}
			}
			break;
		case XenResourceID.XEN_HOST:
			Host.Record hostRecord = Host.getByUuid(connection, currentNode.getUuid()).getRecord(connection);
			// for (HostCpu hostCpu : hostRecord.hostCPUs) {
			// @SuppressWarnings("deprecation")
			// HostCpu.Record hostCpuRecord = hostCpu.getRecord(connection);
			// XenSubResourceNode hostCpuNode = new XenSubResourceNode(XenResourceID.XEN_HOST_CPU, hostCpuRecord.uuid, hostCpuRecord.modelname);
			// currentNode.getSubResources().add(hostCpuNode);
			// }
			// for (PIF pif : hostRecord.PIFs) {
			// PIF.Record pifRecord = pif.getRecord(connection);
			// XenSubResourceNode pifNode = new XenSubResourceNode(XenResourceID.XEN_PIF, pifRecord.uuid, pifRecord.device);
			// currentNode.getSubResources().add(pifNode);
			// }
			for (VM vm : hostRecord.residentVMs) {
				VM.Record vmRecord = vm.getRecord(connection);
				if (!vmRecord.isATemplate && !vmRecord.isControlDomain) {
					XenResourceNode vmNode = new XenResourceNode(XenResourceID.XEN_VM, vmRecord.uuid, null, vmRecord.nameLabel, XenResourceID.XEN_VM);
					currentNode.getChildTrees().add(vmNode);
					discoveryXen(vmNode);
				}
			}
			for (PBD pbd : hostRecord.PBDs) {
				SR.Record srRecord = pbd.getSR(connection).getRecord(connection);
				if (!srRecord.shared) {
					XenResourceNode srNode = new XenResourceNode(XenResourceID.XEN_SR, srRecord.uuid, null, srRecord.nameLabel, XenResourceID.XEN_SR);
					currentNode.getChildTrees().add(srNode);
					discoveryXen(srNode);
				}
			}
			break;
		case XenResourceID.XEN_SR:
			// SR.Record srRecord = SR.getByUuid(connection, currentNode.uuid).getRecord(connection);
			// for (VDI vdi : srRecord.VDIs) {
			// VDI.Record vdiRecord = vdi.getRecord(connection);
			// XenSubResourceNode vdiNode = new XenSubResourceNode(XenResourceID.XEN_VID, vdiRecord.uuid, vdiRecord.nameLabel);
			// currentNode.getSubResources().add(vdiNode);
			// System.out.println(vdiRecord);
			// }
			break;
		case XenResourceID.XEN_VM:
			// VM.Record vmRecord = VM.getByUuid(connection, currentNode.uuid).getRecord(connection);
			// TODO
		default:
			break;
		}
	}

	public String getPoolNameLabel() throws BadServerResponse, XenAPIException, XmlRpcException {
		Collection<Pool.Record> poolRecords = Pool.getAllRecords(connection).values();
		Pool.Record poolRecord = (Pool.Record) poolRecords.toArray()[0];
		return poolRecord.nameLabel;
	}

	public String getPoolUuid() throws BadServerResponse, XenAPIException, XmlRpcException {
		Collection<Pool.Record> poolRecords = Pool.getAllRecords(connection).values();
		Pool.Record poolRecord = (Pool.Record) poolRecords.toArray()[0];
		return poolRecord.uuid;
	}

	public String getHostAvailability(String uuid) {
		try {
			Host host = Host.getByUuid(connection, uuid);
			if (host.getEnabled(connection))
				return TRUE;
		} catch (Exception e) {
			return FALSE;
		}
		return FALSE;
	}

	public String getHostNameLabel(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		Host host = Host.getByUuid(connection, uuid);
		return host.getNameLabel(connection);
	}

	public String getHostUuid(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		Host host = Host.getByUuid(connection, uuid);
		return host.getUuid(connection);
	}

	public String getHostHostName(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		Host host = Host.getByUuid(connection, uuid);
		return host.getHostname(connection);
	}

	public String getHostAddress(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		Host host = Host.getByUuid(connection, uuid);
		return host.getAddress(connection);
	}

	public String getHostSysUpTime(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		Host host = Host.getByUuid(connection, uuid);
		return String.valueOf(System.currentTimeMillis() / 1000 - Double.valueOf(host.getOtherConfig(connection).get("boot_time")));
	}

	public String getHostCpuRate(String uuid) throws XmlRpcException, IOException, JDOMException {
		Host host = Host.getByUuid(connection, uuid);
		Map<String, String> perfMetrics = getUuidPerfMetrics(host).get(uuid);
		return String.valueOf((Double.valueOf(perfMetrics.get("cpu_avg")) * 100));
	}

	public String getHostMemRate(String uuid) throws XmlRpcException, IOException, JDOMException {
		Host host = Host.getByUuid(connection, uuid);
		Map<String, String> perfMetrics = getUuidPerfMetrics(host).get(uuid);
		double memoryTotal = Double.valueOf(perfMetrics.get("memory_total_kib"));
		double memoryFree = Double.valueOf(perfMetrics.get("memory_free_kib"));
		return String.valueOf((memoryTotal - memoryFree) / memoryTotal * 100);
	}

	public String getHostThroughput(String uuid) throws XmlRpcException, IOException, JDOMException {
		Host host = Host.getByUuid(connection, uuid);
		Map<String, String> perfMetrics = getUuidPerfMetrics(host).get(uuid);
		double rx = Double.valueOf(perfMetrics.get("pif_aggr_rx"));
		double tx = Double.valueOf(perfMetrics.get("pif_aggr_tx"));
		return String.valueOf(rx + tx);
	}

	public String getVMAvailability(String uuid) {
		try {
			VM vm = VM.getByUuid(connection, uuid);
			if (vm.getPowerState(connection) == VmPowerState.RUNNING)
				return TRUE;
		} catch (Exception e) {
			return FALSE;
		}
		return FALSE;
	}

	public String getVMNameLabel(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		VM vm = VM.getByUuid(connection, uuid);
		return vm.getNameLabel(connection);
	}

	public String getVMUuid(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		VM vm = VM.getByUuid(connection, uuid);
		return vm.getUuid(connection);
	}

	public String getVMSysUpTime(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		VM vm = VM.getByUuid(connection, uuid);
		return String.valueOf((System.currentTimeMillis() - vm.getMetrics(connection).getStartTime(connection).getTime()) / 1000);
	}

	public String getVMPowerState(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		VM vm = VM.getByUuid(connection, uuid);
		return vm.getPowerState(connection).toString();
	}

	public String getVMCpuRate(String uuid) throws XmlRpcException, IOException, JDOMException {
		Host host = VM.getByUuid(connection, uuid).getResidentOn(connection);
		Map<String, String> perfMetrics = getUuidPerfMetrics(host).get(uuid);
		Iterator<Entry<String, String>> iterator = perfMetrics.entrySet().iterator();
		double t = 0;
		int count = 0;
		while (iterator.hasNext()) {
			Entry<String, String> metric = iterator.next();
			if (metric.getKey().matches("cpu\\d")) {
				t += Double.valueOf(metric.getValue());
				count++;
			}
		}
		return String.valueOf(t / count * 100);
	}

	public String getVMMemRate(String uuid) throws XmlRpcException, IOException, JDOMException {
		Host host = VM.getByUuid(connection, uuid).getResidentOn(connection);
		Map<String, String> perfMetrics = getUuidPerfMetrics(host).get(uuid);
		double memoryTotal = Double.valueOf(perfMetrics.get("memory"));
		double memoryFree = Double.valueOf(perfMetrics.get("memory_internal_free"));
		return String.valueOf((memoryTotal - memoryFree) / memoryTotal * 100);
	}

	public String getVMThroughput(String uuid) throws XmlRpcException, IOException, JDOMException {
		Host host = VM.getByUuid(connection, uuid).getResidentOn(connection);
		Map<String, String> perfMetrics = getUuidPerfMetrics(host).get(uuid);
		Iterator<Entry<String, String>> iterator = perfMetrics.entrySet().iterator();
		double t = 0;
		while (iterator.hasNext()) {
			Entry<String, String> metric = iterator.next();
			if (metric.getKey().matches("vif_\\d_tx") || metric.getKey().matches("vif_\\d_rx")) {
				t += Double.valueOf(metric.getValue());
			}
		}
		return String.valueOf(t);
	}

	public String getSRNameLabel(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		return sr.getNameLabel(connection);
	}

	public String getSRUuid(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		return sr.getUuid(connection);
	}

	public String getSRType(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		return sr.getType(connection);
	}

	public String getSRShared(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		return sr.getShared(connection).toString();
	}

	public String getSRAddress(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		PBD pbd = (PBD) sr.getPBDs(connection).toArray()[0];
		if (sr.getShared(connection)) {
			switch (sr.getType(connection)) {
			case "iso":
				String target = pbd.getDeviceConfig(connection).get("location");
				Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
				Matcher matcher = pattern.matcher(target);
				if (matcher.find()) {
					return matcher.group(0);
				}
			case "lvmoiscsi":
				return pbd.getDeviceConfig(connection).get("target");
			case "nfs":
				return pbd.getDeviceConfig(connection).get("server");
			default:
				break;
			}
		} else {
			return pbd.getHost(connection).getAddress(connection);
		}
		return null;
	}

	public String getSRPhysicalSize(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		return sr.getPhysicalSize(connection).toString();
	}

	public String getSRPhysicalUtilisation(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		return sr.getPhysicalUtilisation(connection).toString();
	}

	public String getSRPhysicalRate(String uuid) throws BadServerResponse, XenAPIException, XmlRpcException {
		SR sr = SR.getByUuid(connection, uuid);
		long size = sr.getPhysicalSize(connection);
		if (size == 0)
			return null;
		return String.valueOf(((double) sr.getPhysicalUtilisation(connection) / size * 100));
	}

	public Map<String, Map<String, String>> getUuidPerfMetrics(Host host) throws IOException, JDOMException, XmlRpcException {
		SAXBuilder builder = new SAXBuilder();
		HashMap<String, Map<String, String>> uuidPerfMetrics = new HashMap<String, Map<String, String>>();
		Pattern p = Pattern.compile("AVERAGE:\\w+:([^:]+):(\\S+)");
		URL url = new URL("http://" + host.getAddress(connection) + "/rrd_updates?start=" + (host.getServertime(connection).getTime() / 1000 - TIME_WINDOW)
				+ "&host=true");
		URLConnection urlConnection = url.openConnection();
		String encoding = Base64.encodeBase64String((connectionInfo.username + ":" + connectionInfo.password).getBytes());
		urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
		Document doc = builder.build(urlConnection.getInputStream());
		ArrayList<String> metrics = new ArrayList<String>();
		for (Element entry : doc.getRootElement().getChild("meta").getChild("legend").getChildren("entry"))
			metrics.add(entry.getText());
		ArrayList<String> metricValues = new ArrayList<String>();
		for (Element value : doc.getRootElement().getChild("data").getChild("row").getChildren("v"))
			metricValues.add(value.getText());
		for (int i = 0; i < metrics.size(); ++i) {
			Matcher matcher = p.matcher(metrics.get(i));
			if (matcher.matches()) {
				String uuid = matcher.group(1);
				String metricName = matcher.group(2);
				String metricValue = metricValues.get(i);
				if (uuidPerfMetrics.get(uuid) == null) {
					uuidPerfMetrics.put(uuid, new HashMap<String, String>());
				}
				uuidPerfMetrics.get(uuid).put(metricName, metricValue);
			}
		}

		return uuidPerfMetrics;
	}

	public void dispose() {
		try {
			Session.logout(connection);
		} catch (Exception e) {
		}
	}

}
