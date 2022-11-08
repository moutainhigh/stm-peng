/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.action.device;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.DownUtil;
import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.device.IDeviceFlowApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.web.action.common.NetflowActionUtil;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.GenericNetflowVo;

/**
 * <li>文件名称: DeviceAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 设备流量请求处理controller</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月25日
 * @author lil
 */
@Controller
@RequestMapping("/netflow/device/")
public class DeviceFlowAction extends BaseAction {

	@Autowired
	private IDeviceFlowApi deviceFlowApi;

	/**
	 * 查询受管理的设备流量的请求处理
	 * 
	 * @param vo
	 * @return JSONObject 返回类型
	 */
	@RequestMapping("/findManagedDeviceList")
	public JSONObject findManagedDeviceList(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.toBo(vo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_DEVICE_TABLE_ALL, bo);
		NetflowPageBo pageBo = deviceFlowApi.deviceListPageSelect(bo);
		pageBo.setParamBo(bo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_DEVICE_TABLE, pageBo);
		return toSuccess(pageBo);
	}

	/**
	 * 生成设备流量图表的请求处理
	 * 
	 * @return JSONObject 返回类型
	 */
	@RequestMapping("/getDeviceChartData")
	public JSONObject getDeviceChartData(GenericNetflowVo vo,
			HttpServletRequest req) {
		NetflowParamBo bo = NetflowActionUtil.getBo(vo);
		NetflowCharWrapper deviceFlowChartData = deviceFlowApi
				.deviceFlowChartPageSelect(bo);
		req.getSession().setAttribute(EXPORT_DEVICE_CHART, deviceFlowChartData);
		return toSuccess(deviceFlowChartData);
	}

	private static final String EXPORT_DEVICE_CHART = "EXPORT_DEVICE_CHART";
	private static final String EXPORT_DEVICE_TABLE = "EXPORT_DEVICE_TABLE";
	private static final String EXPORT_DEVICE_TABLE_ALL = "EXPORT_DEVICE_TABLE_ALL";

	@RequestMapping("exportAll")
	public void exportAll(HttpServletRequest request,
			HttpServletResponse response, String type)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {
		NetflowParamBo bo = (NetflowParamBo) BaseAction.getHttpServletRequest()
				.getSession().getAttribute(EXPORT_DEVICE_TABLE_ALL);
		bo.setStartRow(0);
		bo.setRowCount(Long.MAX_VALUE);
		NetflowPageBo pageBo = deviceFlowApi.deviceListPageSelect(bo);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addTable(new TableAdapter().adapter("设备流量数据", this.getColumn(type),
				pageBo.getRows()));
		DownUtil.down(response, "device.pdf", pdf.generate());
	}

	@RequestMapping("export")
	public void export(HttpServletResponse response, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowCharWrapper chart = (NetflowCharWrapper) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(EXPORT_DEVICE_CHART);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addLine("设备流量数据折线图", "时间", "流量(KB)",
				new LineAdapter().adapter(chart));
		NetflowPageBo pageBo = (NetflowPageBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(EXPORT_DEVICE_TABLE);
		pdf.addCircular("设备流量数据环行图", new CircularAdapter().adapter("name",
				pageBo.getParamBo().getSort(), pageBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("设备流量数据", this.getColumn(type),
				pageBo.getRows()));
		DownUtil.down(response, "device.pdf", pdf.generate());
	}

	private ShowColumn[] getColumn(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "设备名称", ShowColumnType.OTHER),
					new ShowColumn("ipAddr", "IP地址", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "总流量", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "流出速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("name", "设备名称", ShowColumnType.OTHER),
					new ShowColumn("ipAddr", "IP地址", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "流入包数", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "流出包数", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "总包数", ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "流入速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "流出速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "总速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("name", "设备名称", ShowColumnType.OTHER),
					new ShowColumn("ipAddr", "IP地址", ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "流入连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "流出连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "总连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "流入速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "流出速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "总速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data3;
		case "4":
			ShowColumn[] data4 = {
					new ShowColumn("name", "设备名称", ShowColumnType.OTHER),
					new ShowColumn("ipAddr", "IP地址", ShowColumnType.OTHER),
					new ShowColumn("flowInBwUsage", "流入带宽使用率",
							ShowColumnType.PERCENTAGE),
					new ShowColumn("flowOutBwUsage", "流出带宽使用率",
							ShowColumnType.PERCENTAGE),
					new ShowColumn("bwUsage", "带宽使用率",
							ShowColumnType.PERCENTAGE) };
			return data4;
		}
		return null;
	}
}
