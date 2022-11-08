package com.mainsteam.stm.portal.netflow.web.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.export.chart.LineData;
import com.mainsteam.stm.export.chart.Point;
import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.SessionChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.SessionChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataModel;
import com.mainsteam.stm.portal.netflow.web.action.common.NumberFormatUtil;

public class LineAdapter {

	public List<LineData> adapter(NetflowCharWrapper chart) {
		if (chart != null) {
			List<LineData> data = new ArrayList<>();
			for (int i = 0; i < chart.getBos().size(); i++) {
				NetflowChartBo bo = chart.getBos().get(i);
				LineData ld = new LineData();
				data.add(ld);
				ld.setName(bo.getName());
				ld.setPoints(new ArrayList<Point>());
				for (int j = 0; j < bo.getData().size(); j++) {
					Point p = new Point();
					ld.getPoints().add(p);
					p.setName(chart.getTimeLine().get(j));
					p.setValue(NumberFormatUtil.byteToMB(bo.getData().get(j)));
				}
			}
			return data;
		}
		return null;
	}

	public List<LineData> adapter(TerminalChartDataBo chart) {
		if (chart != null) {
			List<LineData> data = new ArrayList<>();
			for (int i = 0; i < chart.getTerminalChartDataModel().size(); i++) {
				TerminalChartDataModel bo = chart.getTerminalChartDataModel()
						.get(i);
				LineData ld = new LineData();
				data.add(ld);
				ld.setName(bo.getName());
				ld.setPoints(new ArrayList<Point>());
				for (int j = 0; j < bo.getData().size(); j++) {
					Point p = new Point();
					ld.getPoints().add(p);
					p.setName(chart.getTimepoints().get(j));
					p.setValue(NumberFormatUtil.byteToMB(bo.getData().get(j)));
				}
			}
			return data;
		}
		return null;
	}

	public List<LineData> adapter(SessionChartDataBo chart) {
		if (chart != null) {
			List<LineData> data = new ArrayList<>();
			for (int i = 0; i < chart.getSessionChartDataModel().size(); i++) {
				SessionChartDataModel bo = chart.getSessionChartDataModel()
						.get(i);
				LineData ld = new LineData();
				data.add(ld);
				ld.setName(bo.getName());
				ld.setPoints(new ArrayList<Point>());
				for (int j = 0; j < bo.getData().size(); j++) {
					Point p = new Point();
					ld.getPoints().add(p);
					p.setName(chart.getTimepoints().get(j));
					p.setValue(NumberFormatUtil.byteToMB(bo.getData().get(j)));
				}
			}
			return data;
		}
		return null;
	}

	public List<LineData> adapter(ApplicationChartDataBo chart) {
		if (chart != null) {
			List<LineData> data = new ArrayList<>();
			for (int i = 0; i < chart.getApplicationChartDataModel().size(); i++) {
				ApplicationChartDataModel bo = chart
						.getApplicationChartDataModel().get(i);
				LineData ld = new LineData();
				data.add(ld);
				ld.setName(bo.getName());
				ld.setPoints(new ArrayList<Point>());
				for (int j = 0; j < bo.getData().size(); j++) {
					Point p = new Point();
					ld.getPoints().add(p);
					p.setName(chart.getTimepoints().get(j));
					p.setValue(NumberFormatUtil.byteToMB(bo.getData().get(j)));
				}
			}
			return data;
		}
		return null;
	}

	public List<LineData> adapter(DeviceNetFlowChartDataBo chart) {
		if (chart != null) {
			List<LineData> data = new ArrayList<>();
			for (int i = 0; i < chart.getDeviceNetFlowChartDataModel().size(); i++) {
				DeviceNetFlowChartDataModel bo = chart
						.getDeviceNetFlowChartDataModel().get(i);
				LineData ld = new LineData();
				data.add(ld);
				ld.setName(bo.getName());
				ld.setPoints(new ArrayList<Point>());
				for (int j = 0; j < bo.getData().size(); j++) {
					Point p = new Point();
					ld.getPoints().add(p);
					p.setName(chart.getTimepoints().get(j));
					p.setValue(NumberFormatUtil.byteToMB(bo.getData().get(j)));
				}
			}
			return data;
		}
		return null;
	}

}
