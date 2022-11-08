package com.mainsteam.stm.plugin.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginResultSet;

public class DefaultResultSetConverterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testConvert() {
		String str1 = "00:01:01:01:01:01";
		String str2 = "00:01:01:01:01:02";
		String str3 = "00:01:01:01:01:03";
		List<String> list = new ArrayList<String>();
		list.add(str1);
		list.add(str2);
		list.add(str3);

		DefaultResultSetConverter converter = new DefaultResultSetConverter();
		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.addRow(new String[] { "1", str1 });
		pluginResultSet.addRow(new String[] { "2", str2 });
		pluginResultSet.addRow(new String[] { "3", str3 });
		ResultSet resultSet = new ResultSet(pluginResultSet, null);
		ConverterResult[] vals = converter.convert(resultSet, null);
		for (ConverterResult val : vals) {
			String[] datas = val.getResultData();
			System.out.println(val.getResultIndex());
			if (null != datas) {
				for (String data : datas) {
					System.out.println(data);
				}
			}
			assertTrue(list.contains(val.getResultData()[0]));
		}

	}

}
