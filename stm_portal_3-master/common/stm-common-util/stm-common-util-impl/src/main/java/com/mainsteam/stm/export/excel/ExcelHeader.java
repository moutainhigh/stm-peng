package com.mainsteam.stm.export.excel;

/**
 * <li>文件名称: ExcelHeader</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: Excel表头设置</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月24日 上午10:30:49
 * @author   俊峰
 */
public class ExcelHeader {

	/**
	 * 对象属性名称
	 * */
	private String objField;
	
	/**
	 * 对象属性对应的表头名称
	 * */
	private String headerName;

	public String getObjField() {
		return objField;
	}

	public void setObjField(String objField) {
		this.objField = objField;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public ExcelHeader(String objField, String headerName) {
		super();
		this.objField = objField;
		this.headerName = headerName;
	}

	public ExcelHeader() {
		super();
	}
	
	
}
