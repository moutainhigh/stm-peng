package com.mainsteam.stm.simple.engineer.workbench.bo;

import java.io.Serializable;

/**
 * <li>文件名称: RepairResultBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 故障修复结果对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class RepairResultBo implements Serializable{

	private static final long serialVersionUID = -6765145498324483987L;

	/**
	 * 是否修复成功 成功为1 失败为0
	 */
	private int isSuccess;
	
	/**
	 * 修复结果描述
	 */
	private String description;

	public int getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}


