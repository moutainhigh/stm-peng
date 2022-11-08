package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class SolarisNetStatProcessor implements PluginResultSetProcessor {

	private static final String N = "\\n";
	private static final String N_N = "\\n\\n";
	private static final String STRING = "-";
	private static final String LOCAL_ADDRESS = "Local Address";
	private static final String TCP = "TCP";
	private static final String UDP = "UDP";

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		// TODO Auto-generated method stub
		if(resultSet != null) {
			String result = resultSet.getValue(0, 0);
			String[] strs = result.split(N_N);
			resultSet.clearRows();
			
			if(strs != null) {
				Pattern udpPattern = Pattern.compile("(\\S+)\\s+(.*)\\s+(\\w+)");
				Pattern tcpPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+\\w+\\s+\\d+\\s+\\d+\\s+\\d+\\s+(\\w+)");
				for(String str : strs) {
					if(StringUtils.isNotBlank(str)) {
						str = str.trim();
						String[] tmps = str.split(N);
						if(tmps != null) {
							String protocol = "";
							boolean isAvail = false;
							for(int i  = 0; i < tmps.length; i++) {
								List<String> tmpList = new ArrayList<String>();
								if(StringUtils.startsWithAny(tmps[i], new String[]{UDP, TCP})){
									protocol = tmps[i];
									isAvail = true;
									continue;
								}
								
								if(isAvail) {
									if(!StringUtils.startsWithAny(StringUtils.trim(tmps[i]), new String[]{LOCAL_ADDRESS, STRING})){
										Matcher matcher = null;
										if(StringUtils.startsWith(protocol, UDP)){ //UDP处理
											matcher = udpPattern.matcher(tmps[i]);
										}else if(StringUtils.startsWith(protocol, TCP)){//TCP处理
											matcher = tcpPattern.matcher(tmps[i]);
										}
										if(null != matcher && matcher.find()) {
											tmpList.add(protocol);
											for(int j = 1; j <= matcher.groupCount(); j++) {
												tmpList.add(matcher.group(j));
											}
											
										}
										if(tmpList.size() > 0){
											String[] tmpArray = new String[tmpList.size()];
											resultSet.addRow(tmpList.toArray(tmpArray));
										}
										
									}else
										continue;
								}else {
									break;
								}
							}
						}
					}
					
				}
			}
			
		}

	}

}
