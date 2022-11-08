/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.action.ipgroup.netflow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.DownUtil;
import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.ipgroup.netflow.IIpGroupNetflowApi;
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
 * <li>文件名称: IpGroupAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 接口流量统计</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月16日
 * @author lil
 */
@Controller
@RequestMapping("/netflow/ipgroup")
public class IpGroupNetflowAction extends BaseAction {

	@Autowired
	private IIpGroupNetflowApi ipGroupNetflowApi;

	@RequestMapping("/ipGroupPageSelect")
	public JSONObject ipGroupPageSelect(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.toBo(vo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_NETFLOW_IP_GROUP_TABLE_QUERY, bo);
		NetflowPageBo ret = ipGroupNetflowApi.ipGroupNetflowPageSelect(bo);
		ret.setParamBo(bo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_NETFLOW_IP_GROUP_TABLE, ret);
		return toSuccess(ret);
	}

	@RequestMapping("getIpGroupChartData")
	public JSONObject getIpGroupChartData(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.getBo(vo);
		NetflowCharWrapper ret = ipGroupNetflowApi
				.getIfGroupNetflowChartData(bo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(EXPORT_NETFLOW_IP_GROUP_CHART, ret);
		return toSuccess(ret);
	}

	private static final String EXPORT_NETFLOW_IP_GROUP_CHART = "EXPORT_NETFLOW_IP_GROUP_CHART";
	private static final String EXPORT_NETFLOW_IP_GROUP_TABLE = "EXPORT_NETFLOW_IP_GROUP_TABLE";
	private static final String EXPORT_NETFLOW_IP_GROUP_TABLE_QUERY = "EXPORT_NETFLOW_IP_GROUP_TABLE_QUERY";

	@RequestMapping("export")
	public void export(HttpServletResponse response, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowCharWrapper chart = (NetflowCharWrapper) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(EXPORT_NETFLOW_IP_GROUP_CHART);
		PDFHelper pdf = new PDFHelper("IP组流量");
		pdf.addLine("IP组流量数据折线图", "时间", "流量(MB)",
				new LineAdapter().adapter(chart));
		NetflowPageBo page = (NetflowPageBo) BaseAction.getHttpServletRequest()
				.getSession().getAttribute(EXPORT_NETFLOW_IP_GROUP_TABLE);
		pdf.addCircular("IP组流量数据折线图", new CircularAdapter().adapter("name",
				page.getParamBo().getSort(), page.getRows()));
		pdf.addTable(new TableAdapter().adapter("IP组流量数据",
				getColumnIpGroup(type), page.getRows()));
		DownUtil.down(response, "ip_group.pdf", pdf.generate());
	}

	@RequestMapping("exportAll")
	public void exportAll(HttpServletResponse response, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowParamBo bo = (NetflowParamBo) BaseAction.getHttpServletRequest()
				.getSession().getAttribute(EXPORT_NETFLOW_IP_GROUP_TABLE_QUERY);
		bo.setStartRow(0);
		bo.setRowCount(Long.MAX_VALUE);
		NetflowPageBo ret = ipGroupNetflowApi.ipGroupNetflowPageSelect(bo);
		ret.setParamBo(bo);
		PDFHelper pdf = new PDFHelper("IP组流量");
		pdf.addTable(new TableAdapter().adapter("IP组流量数据",
				getColumnIpGroup(type), ret.getRows()));
		DownUtil.down(response, "ip_group.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnIpGroup(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "IP分组名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "IP分组名称", ShowColumnType.OTHER),
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
					new ShowColumn("name", "IP分组名称", ShowColumnType.OTHER),
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
