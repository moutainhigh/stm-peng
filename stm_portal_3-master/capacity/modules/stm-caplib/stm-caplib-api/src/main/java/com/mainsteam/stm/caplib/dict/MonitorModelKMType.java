package com.mainsteam.stm.caplib.dict;

/**
 * 
 * 文件名称: MonitorModelKMType
 * 
 * 文件描述: 采集接口
 * 
 * 版权所有: 版权所有(C)2005-2014
 * 
 * 公司: 美新翔盛
 * 
 * 内容摘要:监控模型知识分类：提供3级和4级
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
public class MonitorModelKMType extends CapbilityKMType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7634181461293523011L;

	public MonitorModelKMType(String parentId, int selfLevel, String selfId,
			String selfName, KMTypeEnum kmTypeEnum) {
		super(parentId, selfLevel, selfId, selfName, kmTypeEnum);
	}

	public MonitorModelKMType(AccidentKMType ckmType) {
		super(ckmType.getParentId(), ckmType.getLevel(), ckmType.getId(),
				ckmType.getName(), ckmType.getKmTypeEnum());
		super.setDescription(ckmType.getDescription());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MonitorModelKMType [toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
