package com.mainsteam.stm.platform.mybatis.plugin.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <li>文件名称: Page.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 
 * <pre>
 * Po：查询结果集实体对象
 * Qo：查询条件对象
 * </pre>
 * </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class Page<Po,Qo> implements Serializable {
	
	private static final long serialVersionUID = 2550746341197565736L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<Po> datas;
	private Qo condition;
	private String sort;
	private String order = "ASC";

	/**
	 * 根据指定的查询对象、开始的行、每页展示条数构建分页查询对象
	 */
	public Page(long startRow,long rowCount,Qo condition){
		this.condition=condition;
		this.setStartRow(startRow);
		this.setRowCount(rowCount);
	}
	
	/**
	 * 根据起始行、每页展示记录数构建分页查询对象
	 * @param startRow
	 * @param rowCount
	 */
	public Page(long startRow,long rowCount){
		this(startRow, rowCount, null);
	}
	
//	public Page(Qo qo){
//		this(0,15,qo);
//	}
//	
	public Page(){
		this(0,15, null);
	}
	
	/**
	 * 当前页第一行记录数
	 * startRow从0开始
	 */
	public void setStartRow(long startRow) {
		this.startRow=startRow<0?0:startRow;
	}

	/**
	 * 当前页第一行记录数
	 * startRow从0开始
	 */
	public long getStartRow() {
		return startRow;
	}
	
	/**
	 * 每页显示的记录数
	 */
	public void setRowCount(long rowCount) {
		this.rowCount=rowCount<0?0:rowCount;
	}

	/**
	 * 每页显示的记录数
	 */
	public long getRowCount() {
		return rowCount;
	}

	/**
	 * 总记录数
	 */
	public void setTotalRecord(long totalRecord) {
		this.totalRecord=totalRecord<0?0:totalRecord;
		if(startRow>this.totalRecord){
			long offset=this.totalRecord % this.getRowCount();
			startRow=this.totalRecord-(offset==0?rowCount:offset);
			this.setStartRow(startRow);
		}
	}

	/**
	 * 总记录数
	 */
	public long getTotalRecord() {
		return totalRecord;
	}
	
	public List<Po> emptyDatas(){
		return this.datas=new ArrayList<Po>(0);
	}

	/**
	 * 对应的当前页记录
	 */
	public List<Po> getDatas() {
		return datas;
	}

	/**
	 * 对应的当前页记录
	 */
	public void setDatas(List<Po> datas) {
		this.datas = datas;
	}

	public Qo getCondition() {
		return condition;
	}

	public void setCondition(Qo condition) {
		this.condition = condition;
	}
	
	/**
	 * 分页操作每页展示的结果集数据
	 * @return
	 */
	public List<Po> getRows(){
		return this.datas;
	}

	public String getSort() {
		return sort;
	}

	/**
	 * 需要排序的字段
	 * */
	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	/**
	 * 排序方式 默认asc
	 * */
	public void setOrder(String order) {
		String str = "DESC";
		if(null != order && (str.equals(order) || str.toLowerCase().equals(order))){
			this.order = str;
		}
	}

}
