package com.mainsteam.stm.caplib.dict;

/**
 * 
 * 文件名称: AccidentKMType
 * 
 * 文件描述: 故障知识分类
 * 
 * 版权所有: 版权所有(C)2005-2014
 * 
 * 公司: 美新翔盛
 * 
 * 内容摘要:故障知识分类共分6级，能力库提供2-6级
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
public class AccidentKMType extends CapbilityKMType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4498459578301675825L;

	public AccidentKMType(String parentId, int selfLevel, String selfId,
			String selfName, KMTypeEnum kmTypeEnum) {
		super(parentId, selfLevel, selfId, selfName, kmTypeEnum);
	}

	public AccidentKMType(AccidentKMType ckmType) {
		super(ckmType.getParentId(), ckmType.getLevel(), ckmType.getId(),
				ckmType.getName(), ckmType.getKmTypeEnum());
		super.setDescription(ckmType.getDescription());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccidentKMType [toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
