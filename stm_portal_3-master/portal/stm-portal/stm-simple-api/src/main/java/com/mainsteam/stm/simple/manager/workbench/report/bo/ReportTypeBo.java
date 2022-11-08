package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;

/**
 * <li>文件名称: ReportTypeBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月18日
 * @author   ziwenwen
 */
public class ReportTypeBo implements Serializable{
	private static final long serialVersionUID = 4581892954944585876L;

	private Long id;
	
	private String name;
	
	/**
	 * 报表周期类型
	 */
	private int cycleType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCycleType() {
		return cycleType;
	}

	public void setCycleType(int cycleType) {
		this.cycleType = cycleType;
	}
}


