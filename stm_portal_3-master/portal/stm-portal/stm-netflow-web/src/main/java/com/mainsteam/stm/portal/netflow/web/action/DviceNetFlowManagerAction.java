package com.mainsteam.stm.portal.netflow.web.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IDeviceNetFlowApi;
import com.mainsteam.stm.portal.netflow.bo.DeviceConditionBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.common.NetFlowUtil;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.DeviceConditionVo;
import com.mainsteam.stm.util.IPUtil;
import com.mainsteam.stm.util.StringUtil;

/**
 * <li>文件名称: User.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
@Controller
@RequestMapping("/netflow/devices")
public class DviceNetFlowManagerAction extends BaseAction {

	@Autowired
	private IDeviceNetFlowApi ideviceApi;

	/**
	 * 增加一个用户
	 * 
	 * @param userVo
	 * @return 插入成功的条数
	 */
	@RequestMapping("/findterminalbydevice")
	public JSONObject getTerminalByDevice(DeviceConditionVo deviceConditionVo) {
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_TABLE_THREE, dcBo);
		DeviceNetFlowPageBo dnfPageBo = ideviceApi.findTerminalsByDevice(dcBo);
		List<String> terminalsIp = new ArrayList<String>();
		List<DeviceNetFlowBo> dnfs = (List<DeviceNetFlowBo>) dnfPageBo
				.getRows();
		for (DeviceNetFlowBo bo : dnfs) {
			terminalsIp.add(bo.getTerminal_Name());
		}
		dcBo.setTerminalsIp(terminalsIp);
		dnfPageBo.setDcBo(dcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_TABLE_TWO, dnfPageBo);
		return toSuccess(dnfPageBo);
	}

	@RequestMapping("/terminalchartoncolum")
	public JSONObject terminalChartOnColumByDevice(
			DeviceConditionVo deviceConditionVo) {
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		DeviceNetFlowChartDataBo dnfChartDataBo = ideviceApi
				.termianlChartonCloumByDevice(dcBo);
		return toSuccess(dnfChartDataBo);
	}

	@RequestMapping("/findsessionbydevice")
	public JSONObject getsessionByDevice(DeviceConditionVo deviceConditionVo) {
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(DEVICE_SESSION_TABLE_THREE, dcBo);
		DeviceNetFlowPageBo dnfPageBo = ideviceApi.findSessionsByDevice(dcBo);
		List<DeviceNetFlowBo> sessions = (List<DeviceNetFlowBo>) dnfPageBo
				.getRows();
		List<Map<String, String>> sessionips = new ArrayList<Map<String, String>>();
		for (DeviceNetFlowBo bo : sessions) {
			Map<String, String> currentSession = new HashMap<String, String>();
			currentSession.put(bo.getSrc_ip(), bo.getDst_ip());
			sessionips.add(currentSession);
		}
		dcBo.setSessionips(sessionips);
		dnfPageBo.setDcBo(dcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_TABLE_TWO, dnfPageBo);
		return toSuccess(dnfPageBo);
	}

	@RequestMapping("/sessionchartoncolum")
	public JSONObject getsessionChartOnColumByDevice(String deviceCondition) {
		DeviceConditionVo deviceConditionVo = JSON.parseObject(deviceCondition,
				DeviceConditionVo.class);
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		DeviceNetFlowChartDataBo SessionChartDataBo = ideviceApi
				.sessionChartonCloumByDevice(dcBo);
		SessionChartDataBo.setSortColum(dcBo.getSort());
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_CHART_TWO, SessionChartDataBo);
		return toSuccess(SessionChartDataBo);
	}

	@RequestMapping("/findnexthopbydevice")
	public JSONObject getNextHopByDevice(DeviceConditionVo deviceConditionVo) {
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(DEVICE_NEXT_TABLE_THREE, dcBo);
		DeviceNetFlowPageBo DeviceNetFlowPageBo = ideviceApi
				.findNextHopsByDevice(dcBo);
		List<String> nextHopsIp = new ArrayList<String>();
		List<DeviceNetFlowBo> nextHops = (List<DeviceNetFlowBo>) DeviceNetFlowPageBo
				.getRows();
		for (DeviceNetFlowBo bo : nextHops) {
			nextHopsIp.add(bo.getNext_hop());
		}
		dcBo.setNextHopsIp(nextHopsIp);
		DeviceNetFlowPageBo.setDcBo(dcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(NEXT_TABLE_TWO, DeviceNetFlowPageBo);
		return toSuccess(DeviceNetFlowPageBo);
	}

	@RequestMapping("/nexthopchartoncolum")
	public JSONObject nextHopChartOnColumByDevice(String deviceCondition) {
		DeviceConditionVo deviceConditionVo = JSON.parseObject(deviceCondition,
				DeviceConditionVo.class);
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		DeviceNetFlowChartDataBo deviceNetFlowChartDataBo = ideviceApi
				.nextHopsChartonCloumByDevice(dcBo);
		deviceNetFlowChartDataBo.setSortColum(dcBo.getSort());
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(NEXT_CHART_TWO, deviceNetFlowChartDataBo);
		return toSuccess(deviceNetFlowChartDataBo);
	}

	@RequestMapping("/findipgbydevice")
	public JSONObject getIPGByDevice(DeviceConditionVo deviceConditionVo) {
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(DEVICE_IP_GROUP_TABLE_THREE, dcBo);
		DeviceNetFlowPageBo DeviceNetFlowPageBo = ideviceApi
				.findIPGsByDevice(dcBo);
		List<String> ipgsIp = new ArrayList<String>();
		List<DeviceNetFlowBo> ipgs = (List<DeviceNetFlowBo>) DeviceNetFlowPageBo
				.getRows();
		for (DeviceNetFlowBo bo : ipgs) {
			ipgsIp.add(bo.getIpgroup_id());
		}
		dcBo.setIpgsIp(ipgsIp);
		DeviceNetFlowPageBo.setDcBo(dcBo);
		DeviceNetFlowPageBo.setSort(dcBo.getSort());
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(IP_GROUP_TABLE_TWO, DeviceNetFlowPageBo);
		return toSuccess(DeviceNetFlowPageBo);
	}

	@RequestMapping("/ipgchartoncolum")
	public JSONObject terminalChartOnColumByDevice(String deviceCondition) {
		DeviceConditionVo deviceConditionVo = JSON.parseObject(deviceCondition,
				DeviceConditionVo.class);
		DeviceConditionBo dcBo = toDeviceConditionBo(deviceConditionVo);
		DeviceNetFlowChartDataBo deviceNetFlowChartDataBo = ideviceApi
				.ipgChartonCloumByDevice(dcBo);
		deviceNetFlowChartDataBo.setSortColum(dcBo.getSort());
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(IP_GROUP_CHART_TWO, deviceNetFlowChartDataBo);
		return toSuccess(deviceNetFlowChartDataBo);
	}

	public DeviceConditionBo toDeviceConditionBo(
			DeviceConditionVo deviceConditionVo) {
		if (deviceConditionVo == null)
			return null;
		DeviceConditionBo deviceConditionBo = new DeviceConditionBo();
		if (null != deviceConditionVo.getTimePerid()) {
			deviceConditionBo.setTimePerid(deviceConditionVo.getTimePerid()
					.trim());
		}
		deviceConditionBo.setDeviceIp(deviceConditionVo.getDeviceIp());
		deviceConditionBo.setEndtime(deviceConditionVo.getEndtime());
		if (deviceConditionVo.getRecordCount() != null) {
			deviceConditionBo.setRecordCount(new Long(deviceConditionVo
					.getRecordCount()).longValue());
		} else {
			deviceConditionBo.setRecordCount(new Long(0).longValue());
		}
		if (deviceConditionVo.getRowCount() != null) {
			deviceConditionBo.setRowCount(new Long(deviceConditionVo
					.getRowCount()).longValue());
		} else {
			deviceConditionBo.setRowCount(new Long(0).longValue());
		}
		deviceConditionBo.setSort(deviceConditionVo.getSort());
		deviceConditionBo.setOrder(deviceConditionVo.getOrder());
		deviceConditionBo.setStartRow(new Long(deviceConditionVo.getStartRow())
				.longValue());
		deviceConditionBo.setStarttime(deviceConditionVo.getStarttime());
		deviceConditionBo.setShowpagination(deviceConditionVo
				.getShowpagination());
		deviceConditionBo.setSessionips(deviceConditionVo.getSessionips());
		deviceConditionBo.setAllsessionFlows(deviceConditionVo
				.getAllsessionFlows());
		deviceConditionBo.setAllterminalsFlows(deviceConditionVo
				.getAllterminalsFlows());
		deviceConditionBo.setEtime(deviceConditionVo.getEtime());
		deviceConditionBo.setStime(deviceConditionVo.getStime());
		deviceConditionBo.setSessionips(deviceConditionVo.getSessionips());
		deviceConditionBo.setTableSubfixTime(deviceConditionVo
				.getTableSubfixTime());
		deviceConditionBo.setTerminalsIp(deviceConditionVo.getTerminalsIp());
		deviceConditionBo.setTimepart(deviceConditionVo.getTimepart());
		deviceConditionBo.setAllNextHopsFlows(deviceConditionVo
				.getAllNextHopsFlows());
		deviceConditionBo.setNextHopsIp(deviceConditionVo.getNextHopsIp());
		deviceConditionBo.setAllipgsFlows(deviceConditionVo.getAllipgsFlows());
		deviceConditionBo.setIpgsIp(deviceConditionVo.getIpgsIp());
		return deviceConditionBo;
	}

	private static final String TERMINAL_TABLE_TWO = "TERMINAL_TABLE_TWO";
	public static final String APP_TABLE_TWO = "APP_TABLE_TWO";
	public static final String APP_CHART_TWO = "APP_CHART_TWO";
	private static final String SESSION_TABLE_TWO = "SESSION_TABLE_TWO";
	private static final String SESSION_CHART_TWO = "SESSION_CHART_TWO";
	public static final String PROTOCOL_TABLE_TWO = "PROTOCOL_TABLE_TWO";
	public static final String PROTOCOL_CHART_TWO = "PROTOCOL_CHART_TWO";
	private static final String NEXT_CHART_TWO = "NEXT_CHART_TWO";
	private static final String NEXT_TABLE_TWO = "NEXT_TABLE_TWO";
	public static final String TOS_TABLE_TWO = "TOS_TABLE_TWO";
	public static final String TOS_CHART_TWO = "TOS_CHART_TWO";
	private static final String IP_GROUP_TABLE_TWO = "IP_GROUP_TABLE_TWO";
	private static final String IP_GROUP_CHART_TWO = "IP_GROUP_CHART_TWO";
	private static final String TERMINAL_TABLE_THREE = "TERMINAL_TABLE_THREE";
	private static final String DEVICE_SESSION_TABLE_THREE = "DEVICE_SESSION_TABLE_THREE";
	private static final String DEVICE_NEXT_TABLE_THREE = "DEVICE_NEXT_TABLE_THREE";
	private static final String DEVICE_IP_GROUP_TABLE_THREE = "DEVICE_IP_GROUP_TABLE_THREE";

	@RequestMapping("exportTwo")
	public void export(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		DeviceNetFlowPageBo bo = (DeviceNetFlowPageBo) session
				.getAttribute(TERMINAL_TABLE_TWO);
		PDFHelper pdf = new PDFHelper("设备("
				+ IPUtil.inetNtoa(bo.getDcBo().getDeviceIp()) + ")流量");
		// 终端
		pdf.addCircular(
				"终端环形图("
						+ NetFlowUtil.getSortName(StringUtil.isNull(bo
								.getSort()) ? bo.getDcBo().getSort() : bo
								.getSort()) + ")",
				new CircularAdapter().adapter("terminal_Name",
						StringUtil.isNull(bo.getSort()) ? bo.getDcBo()
								.getSort() : bo.getSort(), bo.getRows()));
		pdf.addTable(new TableAdapter().adapter("终端流量数据",
				getColumnTerminal(type), bo.getRows()));
		// 应用
		NetflowCharWrapper appChart = (NetflowCharWrapper) session
				.getAttribute(APP_CHART_TWO);
		NetflowPageBo appBo = (NetflowPageBo) session
				.getAttribute(APP_TABLE_TWO);
		pdf.addLine(
				"应用折线图("
						+ NetFlowUtil.getSortName(appBo.getParamBo().getSort())
						+ ")", "时间", "流量(MB)",
				new LineAdapter().adapter(appChart));
		pdf.addTable(new TableAdapter().adapter("应用流量数据", getColumnApp(type),
				appBo.getRows()));
		// 会话
		DeviceNetFlowChartDataBo sessionChart = (DeviceNetFlowChartDataBo) session
				.getAttribute(SESSION_CHART_TWO);
		DeviceNetFlowPageBo sessionBo = (DeviceNetFlowPageBo) session
				.getAttribute(SESSION_TABLE_TWO);
		pdf.addLine(
				"会话折线图("
						+ NetFlowUtil.getSortName(StringUtil.isNull(sessionBo
								.getSort()) ? sessionBo.getDcBo().getSort()
								: sessionBo.getSort()) + ")", "时间", "流量(MB)",
				new LineAdapter().adapter(sessionChart));
		pdf.addTable(new TableAdapter().adapter("会话流量数据",
				getColumnSession(type), sessionBo.getRows()));
		// 协议
		// NetflowCharWrapper protocolChart = (NetflowCharWrapper) session
		// .getAttribute(PROTOCOL_CHART_TWO);
		NetflowPageBo protocolBo = (NetflowPageBo) session
				.getAttribute(PROTOCOL_TABLE_TWO);
		pdf.addCircular(
				"协议环形图("
						+ NetFlowUtil.getSortName(protocolBo.getParamBo()
								.getSort()) + ")", new CircularAdapter()
						.adapter("name", protocolBo.getParamBo().getSort(),
								protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("协议流量数据",
				getColumnProtocol(type), protocolBo.getRows()));
		// 下一跳
		DeviceNetFlowChartDataBo nextChart = (DeviceNetFlowChartDataBo) session
				.getAttribute(NEXT_CHART_TWO);
		DeviceNetFlowPageBo nextBo = (DeviceNetFlowPageBo) session
				.getAttribute(NEXT_TABLE_TWO);
		pdf.addLine(
				"下一跳折线图("
						+ NetFlowUtil.getSortName(StringUtil.isNull(nextBo
								.getSort()) ? nextBo.getDcBo().getSort()
								: nextBo.getSort()) + ")", "时间", "流量(MB)",
				new LineAdapter().adapter(nextChart));
		pdf.addTable(new TableAdapter().adapter("下一跳流量数据", getColumnNext(type),
				nextBo.getRows()));
		// tos
		NetflowCharWrapper tosChart = (NetflowCharWrapper) session
				.getAttribute(TOS_CHART_TWO);
		NetflowPageBo tosBo = (NetflowPageBo) session
				.getAttribute(TOS_TABLE_TWO);
		pdf.addLine(
				"下一跳折线图("
						+ NetFlowUtil.getSortName(tosBo.getParamBo().getSort())
						+ ")", "时间", "流量(MB)",
				new LineAdapter().adapter(tosChart));
		pdf.addTable(new TableAdapter().adapter("tos流量数据", getColumnTos(type),
				tosBo.getRows()));
		// ip分组
		DeviceNetFlowChartDataBo ipGroupChart = (DeviceNetFlowChartDataBo) session
				.getAttribute(IP_GROUP_CHART_TWO);
		DeviceNetFlowPageBo ipGroupBo = (DeviceNetFlowPageBo) session
				.getAttribute(IP_GROUP_TABLE_TWO);
		pdf.addLine(
				"IP分组折线图("
						+ NetFlowUtil.getSortName(StringUtil.isNull(ipGroupBo
								.getSort()) ? ipGroupBo.getDcBo().getSort()
								: ipGroupBo.getSort()) + ")", "时间", "流量(MB)",
				new LineAdapter().adapter(ipGroupChart));
		pdf.addTable(new TableAdapter().adapter("IP分组流量数据",
				getColumnIPGroup(type), ipGroupBo.getRows()));
		DownUtil.down(resp, "device.pdf", pdf.generate());
	}

	@RequestMapping("exportTerminal")
	public void exportTerminal(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		DeviceConditionBo dcBo = (DeviceConditionBo) session
				.getAttribute(TERMINAL_TABLE_THREE);
		dcBo.setStartRow(0);
		dcBo.setRowCount(Long.MAX_VALUE);
		DeviceNetFlowPageBo dnfPageBo = ideviceApi.findTerminalsByDevice(dcBo);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addTable(new TableAdapter().adapter("终端", getColumnTerminal(type),
				dnfPageBo.getRows()));
		DownUtil.down(resp, "device_terminal.pdf", pdf.generate());
	}

	@RequestMapping("exportSession")
	public void exportSession(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		DeviceConditionBo dcBo = (DeviceConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(DEVICE_SESSION_TABLE_THREE);
		dcBo.setStartRow(0);
		dcBo.setRowCount(Long.MAX_VALUE);
		DeviceNetFlowPageBo dnfPageBo = ideviceApi.findSessionsByDevice(dcBo);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addTable(new TableAdapter().adapter("会话", getColumnSession(type),
				dnfPageBo.getRows()));
		DownUtil.down(resp, "device_session.pdf", pdf.generate());
	}

	@RequestMapping("exportNext")
	public void exportNext(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		DeviceConditionBo dcBo = (DeviceConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(DEVICE_NEXT_TABLE_THREE);
		dcBo.setStartRow(0);
		dcBo.setRowCount(Long.MAX_VALUE);
		DeviceNetFlowPageBo page = ideviceApi.findNextHopsByDevice(dcBo);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addTable(new TableAdapter().adapter("下一跳", getColumnNext(type),
				page.getRows()));
		DownUtil.down(resp, "device_next.pdf", pdf.generate());
	}

	@RequestMapping("exportIpGroup")
	public void exportIpGroup(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		DeviceConditionBo dcBo = (DeviceConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(DEVICE_IP_GROUP_TABLE_THREE);
		DeviceNetFlowPageBo page = ideviceApi.findIPGsByDevice(dcBo);
		PDFHelper pdf = new PDFHelper("设备流量");
		pdf.addTable(new TableAdapter().adapter("ip分组", getColumnIPGroup(type),
				page.getRows()));
		DownUtil.down(resp, "device_ip_group.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnTerminal(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("terminal_Name", "终端名称",
							ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("terminal_Name", "终端名称",
							ShowColumnType.OTHER),
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
					new ShowColumn("terminal_Name", "终端名称",
							ShowColumnType.OTHER),
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
		}
		return null;
	}

	private ShowColumn[] getColumnApp(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "应用名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "应用名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "应用名称", ShowColumnType.OTHER),
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
		}
		return null;
	}

	private ShowColumn[] getColumnSession(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "目的IP", ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "目的IP", ShowColumnType.OTHER),
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
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "目的IP", ShowColumnType.OTHER),
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
		}
		return null;
	}

	private ShowColumn[] getColumnProtocol(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "协议名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "协议名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "协议名称", ShowColumnType.OTHER),
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
		}
		return null;
	}

	private ShowColumn[] getColumnNext(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("next_hop", "下一跳IP", ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("terminal_Name", "终端名称",
							ShowColumnType.OTHER),
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
					new ShowColumn("terminal_Name", "终端名称",
							ShowColumnType.OTHER),
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
		}
		return null;
	}

	private ShowColumn[] getColumnTos(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "协议名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "协议名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "协议名称", ShowColumnType.OTHER),
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
		}
		return null;
	}

	private ShowColumn[] getColumnIPGroup(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("ipgroup_name", "IP分组名称",
							ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("ipgroup_name", "IP分组名称",
							ShowColumnType.OTHER),
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
					new ShowColumn("ipgroup_name", "IP分组名称",
							ShowColumnType.OTHER),
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
		}
		return null;
	}

}
