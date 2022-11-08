package com.mainsteam.stm.platform.sequence.service;

/**
 * <li>文件名称: ISequence.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: <strong>用于数据库主键的生成，建议序列名称命名为相应表名称</strong></li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
public interface ISequence {
	
	/**
	 * 根据序列名称获取下一个序列值
	 * 不存在该序列名称时抛出异常
	 * @return
	 */
	long next();
	
	/**
	 * <pre>
	 * 根据序列名称获取一个序列值<br/>
	 * 不存在该序列名称时 ：
	 * 若force为true 将新建以该名称命名并且起始值为1的序列的序列
	 * 若force为false 抛出异常
	 * </pre> 
	 * @param count 一次获取序列值的个数
	 * @return
	 */
//	long next(boolean force);
	
	/**
	 * 获取当前序列最新值
	 * @return
	 */
	long current();
}
