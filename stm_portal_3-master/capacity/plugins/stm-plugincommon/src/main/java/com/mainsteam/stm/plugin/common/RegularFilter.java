package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

public class RegularFilter implements PluginResultSetProcessor {

	private static final String OPTIONAL = "Optional";

	private static final String REGULAR = "regular";
	
	private static final Log logger = LogFactory.getLog(RegularFilter.class);

	public static void main(String[] argv) {

		Pattern p = Pattern.compile("(?:\\[??root@.+){0,1}[$#]$");
		Matcher m = p
				.matcher("[root@ghs] ./#");
		// while (m.find()) {
		// System.out.println(m.group());
		// }

		while (m.find()) {
			System.out.println(m.group(1));
		}

	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,PluginSessionContext context) {
		if(logger.isDebugEnabled()) {
			logger.debug("RegularFilter init resultSet : " + resultSet.toString());
		}
		
		ParameterValue[] params = parameter.listParameterValues();
		List<String> regularParams = new ArrayList<String>(2);
		Map<String, String> replaces = new HashMap<String, String>(2); //正则表达式中参数替换的值
		Map<String, String> optionalRegulars = new HashMap<String, String>(4);//可选的正则表达式
		for(ParameterValue value : params) {
			if(StringUtils.equalsIgnoreCase(value.getKey(), REGULAR)){//选择出正则表达式
				regularParams.add(value.getValue());
			}else if(StringUtils.equalsIgnoreCase(value.getType(), OPTIONAL)){//可选表达式
				optionalRegulars.put(value.getKey(), value.getValue());
			}else{
				replaces.put(value.getKey(), value.getValue());
			}
		}
		
		if(!optionalRegulars.isEmpty()) {//如果备选正则表达式不为空的话，则需要从备选中挑出指定的表达式
			String regular = null;
			String removeKey = null;
			if(!replaces.isEmpty()) {
				Set<String> keySet = replaces.keySet();
				Iterator<String> iterator = keySet.iterator();
				while(iterator.hasNext()) {
					String key = iterator.next();
					String optionalKey = replaces.get(key);
					regular = optionalRegulars.get(optionalKey);
					if(StringUtils.isNotBlank(regular)) {
						removeKey = key;
						break;
					}
				}
				
			}
			if(StringUtils.isNotBlank(regular)) {//如果匹配到可选表达式，则将默认的表达式清除
				regularParams.clear();
				regularParams.add(regular);
				replaces.remove(removeKey); //删除表达式指定参数，不用于参数替换
			}
			
		}
		
		boolean isRow = true; //标志按行匹配还是按列匹配
		if(regularParams != null){
			//临时保存正则表达式匹配的结果集
			int rowSize = resultSet.getRowLength();
			int columnSize = 1;//MetaInfo中配置的列数
			ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
			if(metaInfo != null){
				columnSize = metaInfo.getColumnLength();
			}
			List<List<String>> resultList = new ArrayList<List<String>>(columnSize);
			
			//获取resultSet的实际列数
			int colSize = 1;
			for(int i = 0; i < rowSize; i++){
				if(colSize < resultSet.getRow(i).length)
					colSize = resultSet.getRow(i).length;
			}
			//按照顺序依次截取值，当前列数
			int columnIndex = 0;
			//如果多个正则表达式，则需要按列进行匹配，如果列数与正则表达式的个数不相同的话，则按将结果集和全部正则依次匹配，这样则不保证数据的准确性
			for(String regex : regularParams){
				if(!replaces.isEmpty()) {
					Set<String> keySet = replaces.keySet();
					Iterator<String> iterator = keySet.iterator();
					while(iterator.hasNext()) {
						String key = iterator.next();
						regex = StringUtils.replace(regex, "${" + key + "}", replaces.get(key));
					}
				}
				Pattern p = Pattern.compile(regex);
				if(regularParams.size() > 1 && regularParams.size() == columnSize){
					isRow = false;
					List<String> columnValues = new ArrayList<String>(rowSize);
					for (int i = 0; i < rowSize; i++) {
						String str = resultSet.getValue(i, columnIndex);
						if(str == null)
							break;
						Matcher matcher = p.matcher(str);
						while(matcher.find()){
							columnValues.add(matcher.group(1));
						}
					}
					columnIndex++;
					if(!columnValues.isEmpty()){
						resultList.add(columnValues); //按列取值
					}
					
				}else{
					
					for (int i = 0; i < rowSize; i++) {
						for (int j = 0; j < colSize; j++) {
							String val = resultSet.getValue(i, j);
							if(val == null)
								continue;
							Matcher m = p.matcher(val);
							while(m.find()){
								List<String> strs = new ArrayList<String>(m.groupCount());
								for(int k = 0; k < m.groupCount(); k++){
									strs.add(m.group(k+1));
								}
								resultList.add(strs);
							}
							
						}
					}
					
				}
				
			}
			
			resultSet.clearRows();
			if(!resultList.isEmpty()) {
				for(int i = 0; i < resultList.size(); i++){
					List<String> values = resultList.get(i);
					for(int j = 0; j < values.size(); j++){
						if(!isRow) {
							resultSet.putValue(j, i, values.get(j));
						}else{
							resultSet.putValue(i, j, values.get(j));
						}
					}
				}
			}
			
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("RegularFilter dealed resultSet : " + resultSet.toString());
		}
		
	}

}
