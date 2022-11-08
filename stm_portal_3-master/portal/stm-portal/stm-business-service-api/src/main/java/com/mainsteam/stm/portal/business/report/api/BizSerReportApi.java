package com.mainsteam.stm.portal.business.report.api;

import java.util.List;

import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;

/**
 * <li>文件名称: BizSerReportApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: 业务报表api</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明:  指标计算规则说明
 * 				1、可用率
				资源可用率=资源可用时长/总时长，系统默认时，业务可用率取值为资源可用率的最低值。
				如业务的告警规则计算为或计算，则业务可用率取资源可用率中的最大值。如业务的告警规则计算为与计算，取值与系统默认时一致。
				2、MTTR
				平均故障恢复时间，业务的MTTR为资源的MTTR中的最大值，
				如业务的告警规则计算为或计算，则业务MTTR为资源MTTR中的最小值。如业务的告警规则计算为与计算，取值与系统默认时一致。
				3、MTBF
				平均连续运行时间，业务的MTBF为资源的MTBF中的最小值。
				如业务的告警规则计算为或计算，则业务MTBF为资源MTBF中的最大值。如业务的告警规则计算为与计算，取值与系统默认时一致。
				4、宕机次数
				业务自身进行计算
				5、宕机时长
				业务自身进行计算
				6、产生告警的数量
				业务自身进行计算
				7、未恢复的告知警数量
				业务自身进行计算.
 *</li>
 * @version  ms.stm
 * @since    2019年12月18日
 * @author   caoyong
 */
public interface BizSerReportApi {
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
	 * 根据开始时间～结束时间段查询业务报表数据集合
	 * @param ids业务应用ids集合
	 * @param metricIds 指标枚举index集合
	 * @param timePeriods 时间区间数据(每个timePeriod包含开始时间和结束时间)
	 * @return
	 */
	public List<BizSerReport> getBizSerReports(List<Long> ids,List<String> metricIds, List<TimePeriod> timePeriods);
	/**
	 * 根据ids数组查询业务集合对象
	 * @param ids
	 * @return
	 */
	public List<BizSerReport> getBizSerReports(Long[] ids);
	/**
	 * 获取指标列表数据
	 * @return
	 */
	public List<BizSerMetric> getBizSerMetrics();
}
