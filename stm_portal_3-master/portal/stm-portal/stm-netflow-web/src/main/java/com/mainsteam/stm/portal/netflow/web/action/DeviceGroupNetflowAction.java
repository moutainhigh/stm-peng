package com.mainsteam.stm.portal.netflow.web.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.DownUtil;
import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IDeviceGroupNetflowApi;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowQueryBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.web.action.common.NetflowActionUtil;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.GenericNetflowVo;
import com.mainsteam.stm.util.NetFlowDataUtil;

@Controller
@RequestMapping("netflow/deviceGroupNetflow")
public class DeviceGroupNetflowAction extends BaseAction {

	@Autowired
	private IDeviceGroupNetflowApi deviceGroupNetflowApi;

	@RequestMapping("list")
	public JSONObject list(int startRow, int displayCount, String time) {
		Date end = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(1407924600000l);
		end = cal.getTime();
		Date start;
		switch (time) {
		case "1hour":
			start = NetFlowDataUtil.addHour(end, -1);
			break;
		case "6hour":
			start = NetFlowDataUtil.addHour(end, -6);
			break;
		case "1day":
			start = NetFlowDataUtil.addDay(end, -1);
			break;
		case "7day":
			start = NetFlowDataUtil.addDay(end, -7);
			break;
		default:
			start = NetFlowDataUtil.addHour(end, -1);
			break;
		}
		Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> pageQuery = new Page<>(
				startRow, displayCount);
		DeviceGroupNetflowQueryBo query = new DeviceGroupNetflowQueryBo();
		query.setStart(start.getTime() / 1000);
		query.setEnd(end.getTime() / 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		query.setTableSuffix(TimeUtil.getTableSuffix(sdf.format(start),
				sdf.format(end)));
		pageQuery.setCondition(query);
		Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page = this.deviceGroupNetflowApi
				.list(pageQuery);
		return super.toSuccess(page);
	}

	@RequestMapping("/findManagedDeviceList")
	public JSONObject findManagedDeviceList(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.toBo(vo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_NETFLOW_DEVICE_GROUP_TABLE_QUERY, bo);
		NetflowPageBo pageBo = deviceGroupNetflowApi.deviceListPageSelect(bo);
		if (pageBo.getRows() != null) {
			for (Object obj : pageBo.getRows()) {
				NetflowBo n = ((NetflowBo) obj);
				if (n.getPacketIn() == null || "".equals(n.getPacketIn())) {
					n.setPacketIn("0");
				}
				if (n.getPacketOut() == null || "".equals(n.getPacketOut())) {
					n.setPacketOut("0");
				}
				if (n.getPacketTotal() == null || "".equals(n.getPacketTotal())) {
					n.setPacketTotal("0");
				}
			}
		}
		pageBo.setParamBo(bo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_NETFLOW_DEVICE_GROUP_TABLE, pageBo);
		return toSuccess(pageBo);
	}

	@RequestMapping("/getDeviceChartData")
	public JSONObject getDeviceChartData(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.getBo(vo);
		NetflowCharWrapper deviceFlowChartData = this.deviceGroupNetflowApi
				.deviceFlowChartPageSelect(bo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(EXPORT_NETFLOW_DEVICE_GROUP_CHART,
						deviceFlowChartData);
		return toSuccess(deviceFlowChartData);
	}

	private static final String EXPORT_NETFLOW_DEVICE_GROUP_CHART = "EXPORT_NETFLOW_DEVICE_GROUP_CHART";
	private static final String EXPORT_NETFLOW_DEVICE_GROUP_TABLE = "EXPORT_NETFLOW_DEVICE_GROUP_TABLE";
	private static final String EXPORT_NETFLOW_DEVICE_GROUP_TABLE_QUERY = "EXPORT_NETFLOW_DEVICE_GROUP_TABLE_QUERY";

	@RequestMapping("export")
	public void export(HttpServletResponse response, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowCharWrapper chart = (NetflowCharWrapper) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(EXPORT_NETFLOW_DEVICE_GROUP_CHART);
		PDFHelper pdf = new PDFHelper("???????????????");
		pdf.addLine("??????????????????????????????", "??????", "??????(MB)",
				new LineAdapter().adapter(chart));
		NetflowPageBo page = (NetflowPageBo) BaseAction.getHttpServletRequest()
				.getSession().getAttribute(EXPORT_NETFLOW_DEVICE_GROUP_TABLE);
		pdf.addCircular("??????????????????????????????", new CircularAdapter().adapter("name",
				page.getParamBo().getSort(), page.getRows()));
		pdf.addTable(new TableAdapter().adapter("?????????????????????", getColumn(type),
				page.getRows()));
		DownUtil.down(response, "device_group.pdf", pdf.generate());
	}

	@RequestMapping("exportAll")
	public void exportAll(HttpServletResponse response, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowParamBo bo = (NetflowParamBo) BaseAction.getHttpServletRequest()
				.getSession()
				.getAttribute(EXPORT_NETFLOW_DEVICE_GROUP_TABLE_QUERY);
		bo.setStartRow(0);
		bo.setRowCount(Long.MAX_VALUE);
		NetflowPageBo pageBo = deviceGroupNetflowApi.deviceListPageSelect(bo);
		if (pageBo.getRows() != null) {
			for (Object obj : pageBo.getRows()) {
				NetflowBo n = ((NetflowBo) obj);
				if (n.getPacketIn() == null || "".equals(n.getPacketIn())) {
					n.setPacketIn("0");
				}
				if (n.getPacketOut() == null || "".equals(n.getPacketOut())) {
					n.setPacketOut("0");
				}
				if (n.getPacketTotal() == null || "".equals(n.getPacketTotal())) {
					n.setPacketTotal("0");
				}
			}
		}
		pageBo.setParamBo(bo);
		PDFHelper pdf = new PDFHelper("???????????????");
		pdf.addTable(new TableAdapter().adapter("?????????????????????", getColumn(type),
				pageBo.getRows()));
		DownUtil.down(response, "device_group.pdf", pdf.generate());
	}

	@RequestMapping("getDeviceIdsByGroupId")
	public JSONObject getDeviceIdsByGroupId(Long deviceGroupId) {
		String ret = deviceGroupNetflowApi.getDeviceIdsByGroupId(deviceGroupId);
		if (null != ret) {
			ret = ret.trim();
		}
		return toSuccess(ret);
	}

	private ShowColumn[] getColumn(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "???????????????", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("name", "???????????????", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "?????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("name", "???????????????", ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "?????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data3;
		case "4":
			ShowColumn[] data4 = {
					new ShowColumn("name", "???????????????", ShowColumnType.OTHER),
					new ShowColumn("flowInBwUsage", "?????????????????????",
							ShowColumnType.PERCENTAGE),
					new ShowColumn("flowOutBwUsage", "?????????????????????",
							ShowColumnType.PERCENTAGE),
					new ShowColumn("bwUsage", "???????????????",
							ShowColumnType.PERCENTAGE) };
			return data4;
		}
		return null;
	}
}
