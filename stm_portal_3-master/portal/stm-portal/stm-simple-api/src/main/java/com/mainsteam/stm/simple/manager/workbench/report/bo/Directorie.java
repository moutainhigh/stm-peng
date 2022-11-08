package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: Directorie</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 报表章节目录</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月20日 下午3:19:06
 * @author   俊峰
 */
public class Directorie implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5149477908215753068L;

	/**
	 *章节名称
	 */
	private String name;
	
	/**
	 * 章节里的业务数据集合（一个章节会包含多种数据）
	 */
	private List<List<BusinessData>> businessDatas;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<List<BusinessData>> getBusinessDatas() {
		return businessDatas;
	}

	public void setBusinessDatas(List<List<BusinessData>> businessDatas) {
		this.businessDatas = businessDatas;
	}
	
	
}
