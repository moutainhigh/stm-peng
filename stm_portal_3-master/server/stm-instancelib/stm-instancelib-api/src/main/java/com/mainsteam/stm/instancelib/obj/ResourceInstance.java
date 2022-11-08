package com.mainsteam.stm.instancelib.obj;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

/**
 * 资源实例类
 * 
 * @author xiaoruqiang
 */
public class ResourceInstance extends Instance implements Cloneable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6918464950208942344L;

	/**
	 * 模型ID(window，linux,huawei,h3c等主机)
	 */
	private String resourceId;
	/**
	 * 资源分类
	 */
	private String categoryId;
	
	/**
	 * 资源分类（主机，网络设备等分类）
	 */
	private String parentCategoryId;
	/**
	 * 模型配置属性
	 */
	private List<ModuleProp> moduleProps;
	/**
	 * 发现输入属性
	 */
	private List<DiscoverProp> discoverProps;
	/**
	 * 自定义属性
	 */
	private List<CustomProp> customProps;
	/**
	 * 扩展模型属性
	 */
	private List<ExtendProp> extendProps;
	/**
	 * 发现时使用ip
	 */
	private String showIP;
	/**
	 * 发现时使用节点
	 */
	private String discoverNode;

	/**
	 * 发现方式（snmp,telnet,ssh方式）
	 */
	private DiscoverWayEnum discoverWay;

	/**
	 * 生命周期状态
	 */
	private InstanceLifeStateEnum lifeState = InstanceLifeStateEnum.INITIALIZE;

	/**
	 * 父实例ID
	 */
	private long parentId;

	/**
	 * cpu，磁盘，网络接口等
	 */
	private String childType;

	/**
	 * 域ID
	 */
	private long domainId;

	/**
	 * 展示名称
	 */
	private String showName;

	/**
	 * 子实例
	 */
	private List<ResourceInstance> children;

	/**
	 * 是否检验资源重复 true 检验 false 不检验
	 */
	private boolean isRepeatValidate = true;

	/**
	 * 父实例对象
	 */
	private transient ResourceInstance parentInstance;

	/**
	 * 核心节点
	 */
	private boolean isCore = false;

	/**
	 * 是否需要后台生成主键
	 */
	private boolean generatePrimaryKey = true;
	
	/**
	 * 资源实例第一次发现生成，后边不能被修改
	 */
	private String createUserAccount;
	
	//是否刷新当前资源
	private boolean autoRefresh = true;
	
	public ResourceInstance() {
	}

	public ResourceInstance(ResourceInstance parentResourceInstance) {
		if (parentResourceInstance != null && parentResourceInstance != this) {
			if (ResourceInstance.class
					.equals(parentResourceInstance.getClass())) {
				this.parentInstance = parentResourceInstance;
			}
		}
	}
	
	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}
	
	public DiscoverWayEnum getDiscoverWay() {
		return discoverWay;
	}

	public void setDiscoverWay(DiscoverWayEnum discoverWay) {
		this.discoverWay = discoverWay;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}

	public void setLifeState(InstanceLifeStateEnum lifeState) {
		this.lifeState = lifeState;
	}

	public List<ModuleProp> getModuleProps() {
		return moduleProps;
	}

	public void setModuleProps(List<ModuleProp> moduleProps) {
		this.moduleProps = moduleProps;
	}

	public List<DiscoverProp> getDiscoverProps() {
		/**
		 * 如果是子资源实例，则返回父资源实例的发现属性(采集器使用)
		 */
		if (this.discoverProps == null && this.parentInstance != null) {
			return this.parentInstance.getDiscoverProps();
		}
		return discoverProps;
	}

	public void setDiscoverProps(List<DiscoverProp> discoverProps) {
		this.discoverProps = discoverProps;
	}

	public List<CustomProp> getCustomProps() {
		return customProps;
	}

	public void setCustomProps(List<CustomProp> customProps) {
		this.customProps = customProps;
	}

	public List<ExtendProp> getExtendProps() {
		return extendProps;
	}

	public void setExtendProps(List<ExtendProp> extendProps) {
		this.extendProps = extendProps;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getShowIP() {
		return showIP;
	}

	public void setShowIP(String showIP) {
		this.showIP = showIP;
	}

	public String getDiscoverNode() {
		return discoverNode;
	}

	public void setDiscoverNode(String discoverNode) {
		this.discoverNode = discoverNode;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}

	public List<ResourceInstance> getChildren() {
		return children;
	}

	public void setParentInstance(ResourceInstance parentInstance) {
		if (parentInstance != null && parentInstance != this && parentInstance.getId() != getId()) {
			this.parentInstance = parentInstance;
		}
	}

	public void setChildren(List<ResourceInstance> children) {
		this.children = children;
	}

	public boolean isRepeatValidate() {
		return isRepeatValidate;
	}

	public boolean isCore() {
		return isCore;
	}

	/**
	 * @return the parentInstance
	 */
	public ResourceInstance getParentInstance() {
		return parentInstance;
	}

	public void setRepeatValidate(boolean isRepeatValidate) {
		this.isRepeatValidate = isRepeatValidate;
	}

	@Override
	public ResourceInstance clone() throws CloneNotSupportedException {
		return (ResourceInstance) super.clone();
	}

	public String[] getModulePropBykey(String key) {
		if (key == null || "".equals(key.trim())) {
			return null;
		}
		if (this.moduleProps != null) {
			for (ModuleProp moduleProp : moduleProps) {
				if (key.equals(moduleProp.getKey())) {
					return moduleProp.getValues();
				}
			}
		}
		return null;
	}

	public String[] getDiscoverPropBykey(String key) {
		if (key == null || "".equals(key.trim())) {
			return null;
		}
		List<DiscoverProp> props = getDiscoverProps();
		if (props != null) {
			for (DiscoverProp discoverProp : props) {
				if (key.equals(discoverProp.getKey())) {
					return discoverProp.getValues();
				}
			}
		}
		return null;
	}

	public String[] getExtendPropBykey(String key) {
		if (key == null || "".equals(key.trim())) {
			return null;
		}
		if (this.extendProps != null) {
			for (ExtendProp extendProp : extendProps) {
				if (key.equals(extendProp.getKey())) {
					return extendProp.getValues();
				}
			}
		}
		return null;
	}

	public String[] getCustomPropBykey(String key) {
		if (key == null || "".equals(key.trim())) {
			return null;
		}
		if (this.customProps != null) {
			for (CustomProp customProp : customProps) {
				if (key.equals(customProp.getKey())) {
					return customProp.getValues();
				}
			}
		}
		return null;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public void setCore(boolean isCore) {
		this.isCore = isCore;
	}

	public String getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(String parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public boolean getGeneratePrimaryKey() {
		return generatePrimaryKey;
	}

	public void setGeneratePrimaryKey(boolean generatePrimaryKey) {
		this.generatePrimaryKey = generatePrimaryKey;
	}

	public String getCreateUserAccount() {
		return createUserAccount;
	}

	public void setCreateUserAccount(String createUserAccount) {
		this.createUserAccount = createUserAccount;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(" [instanceName:").append(this.getName());
		b.append(",instanceType:").append(this.getChildType());
		b.append("]");
		return b.toString();
	}
	
	
}
