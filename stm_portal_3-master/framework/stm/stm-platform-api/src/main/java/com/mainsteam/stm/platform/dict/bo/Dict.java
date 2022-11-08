package com.mainsteam.stm.platform.dict.bo;

import java.io.Serializable;

/**
 * <li>文件名称: Dict.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月8日
 * @author   ziwenwen
 */
public class Dict implements Serializable {

	private static final long serialVersionUID = 7072654740830886072L;

	private String type;
	private String code;
	private String name;
	private String pcode;
	private int isValid;
	private String description;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPcode() {
		return pcode;
	}
	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	public int getIsValid() {
		return isValid;
	}
	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}


