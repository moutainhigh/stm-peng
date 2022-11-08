package com.mainsteam.stm.export.excel;

import java.io.InputStream;

/**
 * <li>文件名称: com.mainsteam.stm.export.excel.ReadExcelArgs.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年1月6日
 */
public class ReadExcelArgs {

	/******excel文件流（必须是文件流）*******/
	private InputStream in;
	
	/*****如果为-1表示不需要忽略行*******/
	private int ignoreRows = 0;
	
	/*****数据转换器*******/
	private IExcelConvert convert;
	
	/*****工作簿******/
	private int sheetIndex = 0;
	
	public ReadExcelArgs(){}
	
	public ReadExcelArgs(InputStream in, IExcelConvert convert){
		this.in = in;
		this.convert = convert;
	}
	public InputStream getIn() {
		return in;
	}
	public void setIn(InputStream in) {
		this.in = in;
	}
	public int getIgnoreRows() {
		return ignoreRows;
	}
	public void setIgnoreRows(int ignoreRows) {
		this.ignoreRows = ignoreRows;
	}
	public IExcelConvert getConvert() {
		return convert;
	}
	public void setConvert(IExcelConvert convert) {
		this.convert = convert;
	}
	public int getSheetIndex() {
		return sheetIndex;
	}
	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}
	
}
