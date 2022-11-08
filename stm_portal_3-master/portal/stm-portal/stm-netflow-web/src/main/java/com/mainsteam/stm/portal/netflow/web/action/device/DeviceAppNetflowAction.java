/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.action.device;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.DownUtil;
import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.device.IDeviceAppApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.web.action.DviceNetFlowManagerAction;
import com.mainsteam.stm.portal.netflow.web.action.common.NetflowActionUtil;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.GenericNetflowVo;

/**
 * <li>文件名称: DeviceAppNetflowAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 处理设备应用流量查询controller
 * </li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月16日
 * @author lil
 */
@Controller
@RequestMapping("/netflow/device/app")
public class DeviceAppNetflowAction extends BaseAction {

	@Autowired
	private IDeviceAppApi deviceAppApi;

	/**
	 * 查询设备应用流量使用方法-datagrid
	 * 
	 * @param vo
	 * @return JSONObject 返回类型
	 */
	@RequestMapping("/deviceAppNetflowPageSelect")
	public JSONObject deviceAppNetflowPageSelect(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.toBo(vo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_DEVICE_TABLE_THREE, bo);
		NetflowPageBo result = this.deviceAppApi.deviceAppNetflowPageSelect(bo);
		result.setParamBo(bo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(DviceNetFlowManagerAction.APP_TABLE_TWO, result);
		return toSuccess(result);
	}

	/**
	 * 查询某一设备应用流量
	 * 
	 * @param vo
	 * @return JSONObject 返回类型
	 */
	@RequestMapping("/getDeviceAppChartData")
	public JSONObject getDeviceAppChartData(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.getBo(vo);
		NetflowPageBo page = (NetflowPageBo) BaseAction.getHttpServletRequest()
				.getSession()
				.getAttribute(DviceNetFlowManagerAction.APP_TABLE_TWO);
		NetflowCharWrapper appChartData = deviceAppApi.findDeviceAppChartData(
				bo, (List<NetflowBo>) page.getRows());
		appChartData.setSortColumn(bo.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(DviceNetFlowManagerAction.APP_CHART_TWO,
						appChartData);
		return toSuccess(appChartData);
	}

	private static final String APP_DEVICE_TABLE_THREE = "APP_DEVICE_TABLE_THREE";

	@RequestMapping("exportDevice")
	public void export(HttpServletResponse response, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowParamBo bo = (NetflowParamBo) BaseAction.getHttpServletRequest()
				.getSession().getAttribute(APP_DEVICE_TABLE_THREE);
		bo.setStartRow(0);
		bo.setRowCount(Long.MAX_VALUE);
		NetflowPageBo result = this.deviceAppApi.deviceAppNetflowPageSelect(bo);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addTable(new TableAdapter().adapter("应用", getColumn(type),
				result.getRows()));
		DownUtil.down(response, "device_app.pdf", pdf.generate());
	}

	private ShowColumn[] getColumn(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "设备名称", ShowColumnType.OTHER),
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
