package com.mainsteam.stm.portal.business.report.dao;

import java.util.List;

import com.mainsteam.stm.portal.business.report.obj.BizSerReport;

/**
 * <li>文件名称: IBizSerReportDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: 业务应用报表dao</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月18日
 * @author   caoyong
 */
public interface IBizSerReportDao {
	/**
	 * 根据业务名称/负责人模糊查询业务报表集合
	 * @param searchKey 查询关键字(业务名称/负责人)
	 * @return
	 */
	public List<BizSerReport> getBizSerReports(String searchKey);
	/**
	 * 根据业务id查询返回记录
	 * @param id 主id
	 * @return
	 */
	public BizSerReport getBizSerReport(Long id);
	/**
	 * 根据ids数组查询业务集合对象
	 * @param ids
	 * @return
	 */
	public List<BizSerReport> getBizSerReports(Long[] ids);
}
