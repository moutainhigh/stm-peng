package com.mainsteam.stm.portal.netflow.web.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.export.chart.CircularData;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowPageBo;
import com.mainsteam.stm.portal.netflow.bo.SessionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionPageBo;

public class CircularAdapter {

	public List<CircularData> adapter(String nameField, String sort,
			Collection<?> collections) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return this.adapter(new String[] { nameField }, sort, collections);
	}

	public List<CircularData> adapter(String[] nameFields, String sort,
			Collection<?> collections) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		List<CircularData> data = new ArrayList<>();
		if (collections != null) {
			String[] methodNames = new String[nameFields.length];
			for (int i = 0; i < nameFields.length; i++) {
				methodNames[i] = "get"
						+ nameFields[i].substring(0, 1).toUpperCase()
						+ nameFields[i].substring(1);
			}
			String valuefield = "get" + sort.substring(0, 1).toUpperCase()
					+ sort.substring(1);
			for (Object obj : collections) {
				CircularData circular = new CircularData();
				data.add(circular);
				Class<?> clzss = obj.getClass();
				Method method = null;
				StringBuilder name = new StringBuilder();
				for (int i = 0; i < methodNames.length; i++) {
					method = clzss.getMethod(methodNames[i]);
					if (i > 0) {
						name.append("-");
					}
					name.append((String) method.invoke(obj));
				}
				circular.setName(name.toString());
				method = clzss.getMethod(valuefield);
				String value = (String) method.invoke(obj);
				if (value != null) {
					circular.setValue(Double.parseDouble(value));
				} else {
					circular.setValue(0);
				}
			}
		}
		return data;
	}

	public List<CircularData> adapter(DeviceNetFlowPageBo bo) {
		List<CircularData> data = new ArrayList<>();
		if (bo != null && bo.getRows() != null) {
			for (Object obj : bo.getRows()) {
				DeviceNetFlowBo d = (DeviceNetFlowBo) obj;
				CircularData c = new CircularData();
				data.add(c);
				c.setName(d.getTerminal_Name());
				String v = "0.0";
				switch (bo.getSort()) {
				case "in_flows":
					v = d.getIn_flows();
					break;
				case "out_flows":
					v = d.getOut_flows();
					break;
				case "total_flows":
					v = d.getTotal_flows();
					break;
				case "in_packages":
					v = d.getIn_packages();
					break;
				case "out_packages":
					v = d.getOut_packages();
					break;
				case "total_package":
					v = d.getTotal_package();
					break;
				case "in_speed":
					v = d.getIn_speed();
					break;
				case "out_speed":
					v = d.getOut_speed();
					break;
				case "total_speed":
					v = d.getTotal_speed();
					break;
				case "flows_rate":
					v = d.getFlows_rate();
					break;
				}
				c.setValue(Double.parseDouble(v));
			}
		}
		return data;
	}

	public List<CircularData> adapter(SessionPageBo bo) {
		List<CircularData> data = new ArrayList<>();
		if (bo != null && bo.getRows() != null) {
			for (Object obj : bo.getRows()) {
				SessionBo d = (SessionBo) obj;
				CircularData c = new CircularData();
				data.add(c);
				c.setName(d.getProto_name());
				String v = "0.0";
				switch (bo.getSort()) {
				case "in_flows":
					v = d.getIn_flows();
					break;
				case "out_flows":
					v = d.getOut_flows();
					break;
				case "total_flows":
					v = d.getTotal_flows();
					break;
				case "in_packages":
					v = d.getIn_packages();
					break;
				case "out_packages":
					v = d.getOut_packages();
					break;
				case "total_package":
					v = d.getTotal_packages();
					break;
				case "in_speed":
					v = d.getIn_speed();
					break;
				case "out_speed":
					v = d.getOut_speed();
					break;
				case "total_speed":
					v = d.getTotal_speed();
					break;
				case "flows_rate":
					v = d.getFlows_rate();
					break;
				}
				c.setValue(Double.parseDouble(v));
			}
		}
		return data;
	}

}
