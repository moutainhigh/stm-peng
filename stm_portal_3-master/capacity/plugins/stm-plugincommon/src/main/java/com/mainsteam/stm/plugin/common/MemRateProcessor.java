package com.mainsteam.stm.plugin.common;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class MemRateProcessor implements PluginResultSetProcessor{
	
	private static final String TOTAL_MEM_SIZE = "totalMemSize";
	private static final String RESOURCE_PROPERTY = "ResourceProperty";
	private static final String[] HOST_TYPES = new String[]{"UnixWare","SCO_SV","HP-UX","Linux"};
	private static final String NON_MEMORY_USAGE = "0";
	private static final Log logger = LogFactory.getLog(MemRateProcessor.class);

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		
		if(resultSet != null){
			try{
				
				ParameterValue[] pValues = parameter.listParameterValues();
				String totalMemSize = null;
				if(pValues != null) {
					for(ParameterValue pValue : pValues) {
						if(StringUtils.equalsIgnoreCase(RESOURCE_PROPERTY, pValue.getType())
								&& StringUtils.equalsIgnoreCase(TOTAL_MEM_SIZE, pValue.getKey())){
							totalMemSize = pValue.getValue();
						}
					}
				}
				
				String result = resultSet.getValue(0, 0);
				String hostType = resultSet.getValue(0, 1);
				Matcher matcher = Pattern.compile("(\\S+)").matcher(result);
				double sumUsedMemory = 0;
				while(matcher.find()) {
					for(int k = 0; k < matcher.groupCount(); k++){
						try{
							sumUsedMemory += Double.parseDouble(matcher.group(k+1));
						}catch(Exception e) {
							if(logger.isWarnEnabled()) {
								logger.warn("AppMemRate add used memory error." + e.getMessage(), e);
							}
							continue;
						}
					}
				}
				
				resultSet.clearRows();
				if(StringUtils.endsWithAny(hostType, HOST_TYPES)) {
					if(totalMemSize != null) {
						Double memorySize = Double.parseDouble(totalMemSize);
						double memoryUsage = (sumUsedMemory/memorySize)*100;
						try{
							BigDecimal bg = new BigDecimal(memoryUsage);
							resultSet.putValue(0, 0, bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
						}catch(NumberFormatException e){
							logger.warn("AppMemRate format error." + e.getMessage(), e);
							resultSet.putValue(0, 0, NON_MEMORY_USAGE);
						}
					}else {
						if(logger.isWarnEnabled()) {
							logger.warn("AppMemRate can get total memory.");
						}
						resultSet.putValue(0, 0, NON_MEMORY_USAGE);
					}
				}else {
					resultSet.putValue(0, 0, sumUsedMemory+"");
				}
				
			}catch(Exception e) {
				if(logger.isWarnEnabled()) {
					logger.warn(e.getMessage(), e);
				}
				resultSet.clearRows();
				resultSet.putValue(0, 0, NON_MEMORY_USAGE);
				
			}
			
		}else {
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLVALUES, "AppMemRate resultset is null.");
		}
		
	}

}
