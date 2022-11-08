/**
 * 
 */
package com.mainsteam.stm.portal.netflow.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationModelBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceConditionBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.SessionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.SessionConditionBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalConditionBo;

/**
 * <li>文件名称: NetflowBaseService.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月17日
 * @author lil
 */
public class NetflowUtil implements Serializable {

	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 根据排序的列，返回对应的数据给highcharts显示
	 * 
	 * @param sort
	 * @param bos
	 * @return List<Double> 返回类型
	 */
	public static List<Double> filterSortResult(String sort,
			List<NetflowBo> bos, int listSize) {
		List<Double> rows = new ArrayList<Double>();
		if (null != bos && !bos.isEmpty()) {
			if ("flowIn".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getFlowIn() == null ? "0" : b
							.getFlowIn()));
				}
			} else if ("flowOut".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getFlowOut() == null ? "0"
							: b.getFlowOut()));
				}
			} else if ("flowTotal".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getFlowTotal() == null ? "0"
							: b.getFlowTotal()));
				}
			} else if ("packetIn".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketIn() == null ? "0"
							: b.getPacketIn()));
				}
			} else if ("packetOut".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketOut() == null ? "0"
							: b.getPacketOut()));
				}
			} else if ("packetTotal".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketTotal() == null ? "0"
							: b.getPacketOut()));
				}
			} else if ("speedIn".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getSpeedIn() == null ? "0"
							: b.getSpeedIn()));
				}
			} else if ("speedOut".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getSpeedOut() == null ? "0"
							: b.getSpeedOut()));
				}
			} else if ("speedTotal".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getSpeedTotal() == null ? "0"
							: b.getSpeedTotal()));
				}
			} else if ("flowInBwUsage".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getFlowInBwUsage() == null ? "0"
							: b.getFlowInBwUsage()));
				}
			} else if ("flowOutBwUsage".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getFlowOutBwUsage() == null ? "0"
							: b.getFlowOutBwUsage()));
				}
			} else if ("bwUsage".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getBwUsage() == null ? "0"
							: b.getBwUsage()));
				}
			} else if ("flowPctge".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getFlowPctge() == null ? "0"
							: b.getFlowPctge()));
				}
			} else if ("packetInSpeed".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketInSpeed() == null ? "0"
							: b.getPacketInSpeed()));
				}
			} else if ("packetOutSpeed".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketOutSpeed() == null ? "0"
							: b.getPacketOutSpeed()));
				}
			} else if ("packetTotalSpeed".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketTotalSpeed() == null ? "0"
							: b.getPacketTotalSpeed()));
				}
			} else if ("connectNumberIn".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getConnectNumberIn() == null ? "0"
							: b.getConnectNumberIn()));
				}
			} else if ("connectNumberOut".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getConnectNumberOut() == null ? "0"
							: b.getConnectNumberOut()));
				}
			} else if ("connectNumberTotal".equalsIgnoreCase(sort)) {
				for (NetflowBo bo : bos) {
					rows.add(Double
							.parseDouble(bo.getConnectNumberTotal() == null ? "0"
									: bo.getConnectNumberTotal()));
				}
			} else if ("connectNumberInSpeed".equalsIgnoreCase(sort)) {
				for (NetflowBo bo : bos) {
					rows.add(Double
							.parseDouble(bo.getConnectNumberInSpeed() == null ? "0"
									: bo.getConnectNumberInSpeed()));
				}
			} else if ("connectNumberOutSpeed".equalsIgnoreCase(sort)) {
				for (NetflowBo bo : bos) {
					rows.add(Double
							.parseDouble(bo.getConnectNumberOutSpeed() == null ? "0"
									: bo.getConnectNumberOutSpeed()));
				}
			} else if ("connectNumberTotalSpeed".equalsIgnoreCase(sort)) {
				for (NetflowBo bo : bos) {
					rows.add(Double
							.parseDouble(bo.getConnectNumberTotalSpeed() == null ? "0"
									: bo.getConnectNumberTotalSpeed()));
				}
			} else if ("packetPctge".equalsIgnoreCase(sort)) {
				for (NetflowBo b : bos) {
					rows.add(Double.parseDouble(b.getPacketPctge() == null ? "0"
							: b.getPacketPctge()));
				}
			} else if ("connectPctge".equalsIgnoreCase(sort)) {
				for (NetflowBo bo : bos) {
					rows.add(Double.parseDouble(bo.getConnectPctge() == null ? "0"
							: bo.getConnectPctge()));
				}
			}
			if (rows.isEmpty() || rows.size() < listSize) {
				int rowsSize = rows.size();
				for (int i = 0; i < listSize - rowsSize; i++) {
					rows.add(0.0);
				}
			}
		}
		return rows;
	}

	/**
	 * 根据排序的列，过滤highcharts要显示的标题和单位
	 * 
	 * @param sort
	 * @return Map<String,Object> 返回类型
	 */
	public static Map<String, Object> filterDisplayInfo4HC(String sort) {
		String sortColumn = "";
		String yAxisName = "";
		if ("flowIn".equalsIgnoreCase(sort)) {
			sortColumn = "MB";
			yAxisName = "流入流量";
		} else if ("flowOut".equalsIgnoreCase(sort)) {
			sortColumn = "MB";
			yAxisName = "流出流量";
		} else if ("flowTotal".equalsIgnoreCase(sort)) {
			yAxisName = "总流量";
			sortColumn = "MB";
		} else if ("packetIn".equalsIgnoreCase(sort)) {
			yAxisName = "流入包数";
			sortColumn = "MP";
		} else if ("packetOut".equalsIgnoreCase(sort)) {
			yAxisName = "流出包数";
			sortColumn = "MP";
		} else if ("packetTotal".equalsIgnoreCase(sort)) {
			yAxisName = "总包数";
			sortColumn = "MP";
		} else if ("speedIn".equalsIgnoreCase(sort)) {
			yAxisName = "流入速率";
			sortColumn = "Mbps";
		} else if ("speedOut".equalsIgnoreCase(sort)) {
			yAxisName = "流出速率";
			sortColumn = "Mbps";
		} else if ("speedTotal".equalsIgnoreCase(sort)) {
			yAxisName = "总速率";
			sortColumn = "Mbps";
		} else if ("flowInBwUsage".equalsIgnoreCase(sort)) {
			yAxisName = "流入带宽使用率";
			sortColumn = "%";
		} else if ("flowOutBwUsage".equalsIgnoreCase(sort)) {
			yAxisName = "流出带宽使用率";
			sortColumn = "%";
		} else if ("bwUsage".equalsIgnoreCase(sort)) {
			yAxisName = "带宽使用率";
			sortColumn = "%";
		} else if ("flowPctge".equalsIgnoreCase(sort)) {
			yAxisName = "占比";
			sortColumn = "%";
		} else if ("packetInSpeed".equalsIgnoreCase(sort)) {
			yAxisName = "流入包速率";
			sortColumn = "MPbps";
		} else if ("packetOutSpeed".equalsIgnoreCase(sort)) {
			yAxisName = "流出包速率";
			sortColumn = "MPbps";
		} else if ("packetTotalSpeed".equalsIgnoreCase(sort)) {
			yAxisName = "总包速率";
			sortColumn = "MPbps";
		} else if ("connectNumberIn".equalsIgnoreCase(sort)) {
			yAxisName = "流入连接数";
			sortColumn = "MF";
		} else if ("connectNumberOut".equalsIgnoreCase(sort)) {
			yAxisName = "流出连接数";
			sortColumn = "MF";
		} else if ("connectNumberTotal".equalsIgnoreCase(sort)) {
			yAxisName = "总连接数";
			sortColumn = "MF";
		} else if ("connectNumberInSpeed".equalsIgnoreCase(sort)) {
			yAxisName = "流入连接数速率";
			sortColumn = "MFbps";
		} else if ("connectNumberOutSpeed".equalsIgnoreCase(sort)) {
			yAxisName = "流出连接数速率";
			sortColumn = "MFbps";
		} else if ("connectNumberTotalSpeed".equalsIgnoreCase(sort)) {
			yAxisName = "总连接数速率";
			sortColumn = "MFbps";
		} else if ("packetPctge".equalsIgnoreCase(sort)) {
			yAxisName = "包占比";
			sortColumn = "%";
		} else if ("connectPctge".equalsIgnoreCase(sort)) {
			yAxisName = "连接数占比";
			sortColumn = "%";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sortColumn", sortColumn);
		map.put("yAxisName", yAxisName);
		return map;
	}

	public static String getIfIdsString(List<Long> list) {
		if (null == list || list.isEmpty())
			return null;
		StringBuffer sb = new StringBuffer();
		for (Long b : list) {
			sb.append(b).append(",");
		}
		sb = sb.deleteCharAt(sb.toString().length() - 1);
		return sb.toString();
	}

	public static List<Double> TerminalChartResultFilter(
			TerminalConditionBo tcBo, List<TerminalBo> bo, List<Double> record,
			TerminalChartDataBo tchartbo) {
		String flows = null;
		for (int loop = 0; loop < bo.size(); loop++) {
			TerminalBo sessionBo = bo.get(loop);
			switch (tcBo.getSort()) {
			case "in_flows":
				tchartbo.setSortColum("流入流量");
				flows = sessionBo.getIn_flows();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "out_flows":
				tchartbo.setSortColum("流出流量");
				flows = sessionBo.getOut_flows();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_packages":
				tchartbo.setSortColum("流入包数");
				flows = sessionBo.getIn_packages();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_packages":
				tchartbo.setSortColum("流出包数");
				flows = sessionBo.getOut_packages();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_speed":
				tchartbo.setSortColum("流入速率");
				flows = sessionBo.getIn_speed();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_speed":
				tchartbo.setSortColum("流出速率");
				flows = sessionBo.getOut_speed();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_flows":
				tchartbo.setSortColum("总流量");
				flows = sessionBo.getTotal_flows();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_speed":
				tchartbo.setSortColum("总流速");
				flows = sessionBo.getTotal_speed();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_packages":
				tchartbo.setSortColum("总包数");
				flows = sessionBo.getTotal_packages();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "flows_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getFlows_rate();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			}

		}
		return record;
	}

	public static List<Double> DeviceNetFlowChartResultFilter(
			DeviceConditionBo tcBo, List<DeviceNetFlowBo> bo,
			List<Double> record, DeviceNetFlowChartDataBo tchartbo) {
		String flows = null;
		for (int loop = 0; loop < bo.size(); loop++) {
			DeviceNetFlowBo sessionBo = bo.get(loop);
			switch (tcBo.getSort()) {
			case "in_flows":
				tchartbo.setSortColum("流入流量");
				flows = sessionBo.getIn_flows();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "out_flows":
				tchartbo.setSortColum("流出流量");
				flows = sessionBo.getOut_flows();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_packages":
				tchartbo.setSortColum("流入包数");
				flows = sessionBo.getIn_packages();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_packages":
				tchartbo.setSortColum("流出包数");
				flows = sessionBo.getOut_packages();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_speed":
				tchartbo.setSortColum("流入速率");
				flows = sessionBo.getIn_speed();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_speed":
				tchartbo.setSortColum("流出速率");
				flows = sessionBo.getOut_speed();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_flows":
				tchartbo.setSortColum("总流量");
				flows = sessionBo.getTotal_flows();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_speed":
				tchartbo.setSortColum("总流速");
				flows = sessionBo.getTotal_speed();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_packages":
				tchartbo.setSortColum("总包数");
				flows = sessionBo.getTotal_package();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "flows_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getFlows_rate();
				if (flows != null & !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			}

		}
		return record;
	}

	public static List<Double> ApplicationChartResultFilter(
			ApplicationConditionBo tcBo, List<ApplicationModelBo> bo,
			List<Double> record, ApplicationChartDataBo tchartbo) {
		String flows = null;
		for (int loop = 0; loop < bo.size(); loop++) {
			ApplicationModelBo sessionBo = bo.get(loop);
			switch (tcBo.getSort()) {
			case "in_flows":
				tchartbo.setSortColum("流入流量");
				flows = sessionBo.getIn_flows();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "out_flows":
				tchartbo.setSortColum("流出流量");
				flows = sessionBo.getOut_flows();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_packages":
				tchartbo.setSortColum("流入包数");
				flows = sessionBo.getIn_packages();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_packages":
				tchartbo.setSortColum("流出包数");
				flows = sessionBo.getOut_packages();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_speed":
				tchartbo.setSortColum("流入速率");
				flows = sessionBo.getIn_speed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_speed":
				tchartbo.setSortColum("流出速率");
				flows = sessionBo.getOut_speed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_flows":
				tchartbo.setSortColum("总流量");
				flows = sessionBo.getTotal_flows();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_speed":
				tchartbo.setSortColum("总流速");
				flows = sessionBo.getTotal_speed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_packages":
				tchartbo.setSortColum("总包数");
				flows = sessionBo.getTotal_packages();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "flows_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getFlows_rate();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "packets_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getPacketPctge();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "connects_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getConnectPctge();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "packetInSpeed":
				tchartbo.setSortColum("流入包数速率");
				flows = sessionBo.getPacketInSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "packetOutSpeed":
				tchartbo.setSortColum("流出包数速率");
				flows = sessionBo.getPacketOutSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "packetTotalSpeed":
				tchartbo.setSortColum("总包数速率");
				flows = sessionBo.getPacketTotalSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberIn":
				tchartbo.setSortColum("流入连接数");
				flows = sessionBo.getConnectNumberIn();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberOut":
				tchartbo.setSortColum("流出连接数");
				flows = sessionBo.getConnectNumberOut();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberTotal":
				tchartbo.setSortColum("总连接数");
				flows = sessionBo.getConnectNumberTotal();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberInSpeed":
				tchartbo.setSortColum("流入连接数速率");
				flows = sessionBo.getConnectNumberInSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberOutSpeed":
				tchartbo.setSortColum("流出连接数速率");
				flows = sessionBo.getConnectNumberOutSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberTotalSpeed":
				tchartbo.setSortColum("总连接数速率");
				flows = sessionBo.getConnectNumberTotalSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			}

		}
		return record;
	}

	public static List<Double> SessionChartResultFilter(
			SessionConditionBo tcBo, List<SessionBo> bo, List<Double> record,
			SessionChartDataBo tchartbo) {
		String flows = null;
		for (int loop = 0; loop < bo.size() && loop < record.size(); loop++) {
			SessionBo sessionBo = bo.get(loop);
			switch (tcBo.getSort()) {
			case "in_flows":
				tchartbo.setSortColum("流入流量");
				flows = sessionBo.getIn_flows();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "out_flows":
				tchartbo.setSortColum("流出流量");
				flows = sessionBo.getOut_flows();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_packages":
				tchartbo.setSortColum("流入包数");
				flows = sessionBo.getIn_packages();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_packages":
				tchartbo.setSortColum("流出包数");
				flows = sessionBo.getOut_packages();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "in_speed":
				tchartbo.setSortColum("流入速率");
				flows = sessionBo.getIn_speed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "out_speed":
				tchartbo.setSortColum("流出速率");
				flows = sessionBo.getOut_speed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_flows":
				tchartbo.setSortColum("总流量");
				flows = sessionBo.getTotal_flows();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_speed":
				tchartbo.setSortColum("总流速");
				flows = sessionBo.getTotal_speed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "total_packages":
				tchartbo.setSortColum("总包数");
				flows = sessionBo.getTotal_packages();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "flows_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getFlows_rate();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "packets_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getPackets_rate();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "connects_rate":
				tchartbo.setSortColum("占比");
				flows = sessionBo.getConnects_rate();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "packetInSpeed":
				tchartbo.setSortColum("流入包数速率");
				flows = sessionBo.getPacketInSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "packetOutSpeed":
				tchartbo.setSortColum("流出包数速率");
				flows = sessionBo.getPacketOutSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;
			case "packetTotalSpeed":
				tchartbo.setSortColum("总包数速率");
				flows = sessionBo.getPacketTotalSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberIn":
				tchartbo.setSortColum("流入连接数");
				flows = sessionBo.getConnectNumberIn();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberOut":
				tchartbo.setSortColum("流出连接数");
				flows = sessionBo.getConnectNumberOut();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberTotal":
				tchartbo.setSortColum("总连接数");
				flows = sessionBo.getConnectNumberTotal();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberInSpeed":
				tchartbo.setSortColum("流入连接数速率");
				flows = sessionBo.getConnectNumberInSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberOutSpeed":
				tchartbo.setSortColum("流出连接数速率");
				flows = sessionBo.getConnectNumberOutSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			case "connectNumberTotalSpeed":
				tchartbo.setSortColum("总连接数速率");
				flows = sessionBo.getConnectNumberTotalSpeed();
				if (flows != null && !"".equals(flows)) {
					record.set(loop, new Double(flows).doubleValue());
				}
				break;

			}

		}
		return record;
	}

	/**
	 * 如果设置的名字过长就截取显示...
	 */
	public static String getName(String srcName) {
		String dstName = (srcName == null ? "" : srcName);
		if (dstName.length() > 10) {
			dstName = dstName.substring(0, 10) + "...";
		}
		return dstName;
	}

}
