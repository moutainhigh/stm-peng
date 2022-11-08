package com.mainsteam.stm.caplib.dict;

/**
 * 
 * 文件名称: CapbilityKMType
 * 
 * 文件描述: 采集接口
 * 
 * 版权所有: 版权所有(C)2005-2014
 * 
 * 公司: 美新翔盛
 * 
 * 内容摘要:监控模型知识分类
 * 
 * 其他说明:
 * 
 * 完成日期：2019年11月7日
 * 
 * 修改记录1: 创建
 * 
 * @version 3
 * @author sunsht
 * 
 */
public class CapbilityKMType implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7075140681535176586L;
	/**
	 * 父id
	 */
	private String parentId;
	/**
	 * 级别，根据赵总Word文档里面的定义
	 * 
	 * 故障分类2-6级分别代表：分类、子分类、主模型、子模型、指标
	 * 
	 * 监控模型分类是3级代表分类，4级代表子分类
	 */
	private int level;
	/**
	 * id
	 */
	private String id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 描述，没有定义描述的，调用getDescription会返回name
	 */
	private String description;

	private KMTypeEnum kmTypeEnum;

	public CapbilityKMType(String parentId, int selfLevel, String selfId,
			String selfName, KMTypeEnum kmTypeEnum) {
		this.parentId = parentId;
		this.level = selfLevel;
		this.id = selfId;
		this.name = selfName;
		this.kmTypeEnum = kmTypeEnum;
	}

	public KMTypeEnum getKmTypeEnum() {
		return kmTypeEnum;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		if (null == description || description.isEmpty()) {
			return this.name;
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + level;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
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
		CapbilityKMType other = (CapbilityKMType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (level != other.level)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[parentId=");
		builder.append(parentId);
		builder.append(", level=");
		builder.append(level);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(this.getDescription());
		builder.append(", kmTypeEnum=");
		builder.append(kmTypeEnum);
		builder.append("]");
		return builder.toString();
	}

}
