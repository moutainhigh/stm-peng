package com.mainsteam.stm.platform.foreign.api;

/**
 * <li>文件名称: IForeignApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月6日
 * @author   ziwenwen
 */
public interface IForeignApi {
	
	/**
	 * 检查一个表中的某个字段的某个值是否已经被引用，如果被引用了返回true
	 * @param reledTable 被引用表名
	 * @param reledField 被引用表字段
	 * @param val 被引用字段值
	 */
	boolean delCheck(String reledTable,String reledField,Object val);
}


