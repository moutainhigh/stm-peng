package com.mainsteam.stm.plugin.vmware.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.plugin.vmware.collector.VMWareBaseCollector;

/**
 * ResourceTree指标
 * 
 * @author Xiaopf
 *
 */
public class ResourceTree {

	private String name = "";

	private String type = "";

	private String uuid = "";

	private String ip = "";

	private String fullName = "";

	//资源
	private List<SubResource> subResources;

	//子树
	private List<ResourceTree> childTrees;

	ResourceTree() {
		this.childTrees = new ArrayList<ResourceTree>();
		this.subResources = new ArrayList<SubResource>();
	}

	ResourceTree(String name, String type, String uuid, String ip) {
		this();
		this.name = name;
		this.type = type;
		this.uuid = uuid;
		this.ip = ip;
	}

	public ResourceTree(String name, String type, String uuid, String ip,
			String fullName) {
		this(name, type, uuid, ip);
		this.fullName = fullName;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<ResourceTree> getChildTrees() {
		return childTrees;
	}

	public void setChildTrees(List<ResourceTree> childTrees) {
		this.childTrees = childTrees;
	}

	public boolean addResource(ResourceTree resource) {
		try {
			this.childTrees.add(resource);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean addSubResource(SubResource subResource) {
		try {
			this.subResources.add(subResource);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public List<SubResource> getSubResources() {
		return subResources;
	}

	public void setSubResources(List<SubResource> subResources) {
		this.subResources = subResources;
	}

	public String getResourceId() {
		switch (this.type) {
		
		
	/*	case VMWareBaseCollector.C_VCENTER:
			return "VMWareVCenter";
		case VMWareBaseCollector.C_CLUSTER:
			return "VMWareCluster";
		case VMWareBaseCollector.C_HOST:
			return "vmESXi";
		case VMWareBaseCollector.C_VM:
			return "VMWareVM";
		case VMWareBaseCollector.C_DATASTORE:
			return "VMWareDatastore";
		case VMWareBaseCollector.C_CPU:
			return "esxCPU";
		case VMWareBaseCollector.C_NET_INTERFACE:
			return "ESXiInterface";*/
		case VMWareBaseCollector.C_VCENTER:
			return "VMWareVCenter6";
		case VMWareBaseCollector.C_CLUSTER:
			return "VMWareCluster6";
		case VMWareBaseCollector.C_HOST:
			return "vmESXi6";
		case VMWareBaseCollector.C_VM:
			return "VMWareVM6";
		case VMWareBaseCollector.C_DATASTORE:
			return "VMWareDatastore6";
		case VMWareBaseCollector.C_CPU:
			return "esxCPU";
		case VMWareBaseCollector.C_NET_INTERFACE:
			return "ESXiInterface";
		default:
			return "";
		}
	}

	/**
	 * 子子资源实例化树
	 * 
	 * @author Xiaopf
	 *
	 */
	public class SubResource {
		// 实例化Id
		private String id = "";
		// 实例化名称
		private String name = "";

		private String type = "";

		private String resourceId = "";
		
		private String subIndex = "";
		
		private String uuid ="";//hostSystemUuid

		private Map<String,String> properties = new HashMap<String,String>();
		
		SubResource() {

		}
		
		public void addProperty(String key,String value){
			properties.put(key, value);	
		}

		SubResource(String id, String name, String type,String subIndex,String uuid) {
			super();
			this.id = id;
			this.name = name;
			this.type = type;
			this.subIndex = subIndex;
			this.uuid = uuid;
		}

		public SubResource(String id, String name, String type,
				String resourceId,String subIndex,String uuid) {
			this(id, name, type,subIndex,uuid);
			this.resourceId = resourceId;
		}
		
		public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSubIndex() {
			return subIndex;
		}

		public void setSubIndex(String subIndex) {
			this.subIndex = subIndex;
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

		public String getResourceId() {
			return resourceId;
		}

		public void setResourceId(String resourceId) {
			this.resourceId = resourceId;
		}

	}

}
