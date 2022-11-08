package com.mainsteam.stm.portal.netflow.web.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import com.mainsteam.stm.export.data.DataRow;
import com.mainsteam.stm.export.data.DataTable;
import com.mainsteam.stm.portal.netflow.web.action.common.NumberFormatUtil;

public class TableAdapter {
	public static class ShowColumn {
		private String field;
		private String showName;
		private ShowColumnType type;

		public enum ShowColumnType {
			FLOW, FLOW_RATE, PACKET, PACKET_ROTE, CONNECT, CONNECT_ROTE, PERCENTAGE, OTHER
		}

		public ShowColumn() {
		}

		public ShowColumn(String field, String showName, ShowColumnType type) {
			this.setField(field);
			this.setShowName(showName);
			this.setType(type);
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getShowName() {
			return showName;
		}

		public void setShowName(String showName) {
			this.showName = showName;
		}

		public ShowColumnType getType() {
			return type;
		}

		public void setType(ShowColumnType type) {
			this.type = type;
		}

	}

	public DataTable adapter(String title, ShowColumn[] showColumns,
			Collection<?> data) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (data != null) {
			DataRow hand = new DataRow();
			for (ShowColumn sc : showColumns) {
				hand.addColumn(sc.getShowName());
			}
			DataTable dataTable = new DataTable(title, hand);
			if (data != null) {
				for (Object obj : data) {
					DataRow row = new DataRow();
					dataTable.addRow(row);
					Class<?> clzss = obj.getClass();
					for (ShowColumn sc : showColumns) {
						Method method = clzss.getMethod("get"
								+ sc.getField().substring(0, 1).toUpperCase()
								+ sc.getField().substring(1));
						String value = String.valueOf(method.invoke(obj));
						if (sc.getType() != null) {
							switch (sc.getType()) {
							case CONNECT:
								value = NumberFormatUtil.autoConectUnit(value);
								break;
							case CONNECT_ROTE:
								value = NumberFormatUtil
										.autoConectRoteUnit(value);
								break;
							case FLOW:
								value = NumberFormatUtil.autoFlowUnit(value);
								break;
							case FLOW_RATE:
								value = NumberFormatUtil
										.autoFlowRoteUnit(value);
								break;
							case PACKET:
								value = NumberFormatUtil.autoPacketUnit(value);
								break;
							case PACKET_ROTE:
								value = NumberFormatUtil
										.autoPacketRoteUnit(value);
								break;
							case PERCENTAGE:
								value = NumberFormatUtil.per(value);
								break;
							case OTHER:
								break;
							default:
								break;
							}
						}
						row.addColumn(value);
					}
				}
			}
			return dataTable;
		}
		return null;
	}

}
