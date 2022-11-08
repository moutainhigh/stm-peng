/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.action.inf;

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
import com.mainsteam.stm.portal.netflow.api.inf.IIfTosApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.web.action.common.NetflowActionUtil;
import com.mainsteam.stm.portal.netflow.web.action.two.TwoExportAction;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.GenericNetflowVo;

/**
 * <li>文件名称: InterfaceTosAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 接口流量统计</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月15日
 * @author lil
 */
@Controller
@RequestMapping("/netflow/if/tos")
public class InterfaceTosAction extends BaseAction {

	@Autowired
	private IIfTosApi ifTosApi;

	/**
	 * 处理接口tos的datagrid数据查询请求
	 */
	@RequestMapping("/ifTosPageSelect")
	public JSONObject ifTosPageSelect(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.toBo(vo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(INTERFACE_TOS_TABLE_THREE, bo);
		NetflowPageBo ret = ifTosApi.ifTosPageSelect(bo);
		ret.setParamBo(bo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TwoExportAction.INTERFACE_TOS_TABLE_TWO, ret);
		return toSuccess(ret);
	}

	/**
	 * 处理查询tos图表数据的请求
	 */
	@RequestMapping("/getIfTosChartData")
	public JSONObject getIfTosChartData(GenericNetflowVo vo) {
		NetflowParamBo bo = NetflowActionUtil.getBo(vo);
		NetflowCharWrapper ret = ifTosApi.getIfTosChartData(bo);
		ret.setSortColumn(bo.getSort());
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TwoExportAction.INTERFACE_TOS_CHART_TWO, ret);
		return toSuccess(ret);
	}

	private static final String INTERFACE_TOS_TABLE_THREE = "INTERFACE_TOS_TABLE_THREE";

	@RequestMapping("interfaceTos")
	public void interfaceTos(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		NetflowParamBo bo = (NetflowParamBo) BaseAction.getHttpServletRequest()
				.getSession().getAttribute(INTERFACE_TOS_TABLE_THREE);
		bo.setStartRow(0);
		bo.setRowCount(Long.MAX_VALUE);
		NetflowPageBo ret = ifTosApi.ifTosPageSelect(bo);
		PDFHelper pdf = new PDFHelper("接口流量");
		pdf.addTable(new TableAdapter().adapter("tos", getColumnTos(type),
				ret.getRows()));
		DownUtil.down(resp, "interface_tos.pdf", pdf.generate());
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
}
