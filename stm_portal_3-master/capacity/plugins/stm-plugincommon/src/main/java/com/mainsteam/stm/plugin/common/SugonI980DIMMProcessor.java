package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.List;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class SugonI980DIMMProcessor implements PluginResultSetProcessor {

	private static final List<String> dimmIndex, dimmName;

	static {
		List<String> key = new ArrayList<String>();
		List<String> n = new ArrayList<String>();

		dimmIndex = new ArrayList<String>();
		dimmName = new ArrayList<String>();

		String cpu = "CPU";
		String dimm = " DIMM";

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 16; j++) {
				key.add(cpu + i + dimm);
			}
		}
		for (int j = 0; j < 8; j++) {
			for (char i = 'A'; i < 'A' + 8; i++) {

				for (int k = 1; k <= 2; k++) {
					n.add(i + "" + k);
				}
			}
		}

		for (int i = 0; i < key.size(); i++) {
			dimmName.add(key.get(i) + n.get(i));
		}

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				for (int l = 0; l < 2; l++) {
					dimmIndex.add(i + " " + j + " " + l);
				}
			}
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 16; j++) {
				System.out.print("{" + dimmIndex.get(i * 16 + j) + ","
						+ dimmName.get(i * 16 + j) + "},");
			}
			System.out.println();
		}
	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		for (int i = 0; i < 128; ++i) {
			resultSet.putValue(i, 0, dimmIndex.get(i));
			resultSet.putValue(i, 1, dimmName.get(i));
		}
	}

}
