package com.mainsteam.stm.plugin.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class DateTimeProcessor implements PluginResultSetProcessor {
	private static final Log logger = LogFactory
			.getLog(DateTimeProcessor.class);

	@SuppressWarnings("unused")
	private static final String TYPE_OVERRIDE = "override";
	private static final String SELECT = "SELECT";
	private static final String AS = "AS";
	private static final String FROM = "FROM";
	private static final String SPLIT = ",";
	
	private String[] vars;
	private Expression expression;
	private Pattern pattern;
	
	private static final JexlEngine jexl = new JexlEngine();
	static {
		jexl.setCache(512);
		jexl.setLenient(false);	//null isn't treated as zero
		jexl.setSilent(false);		//throw exception
	}

	public static void main(String[] args) {
		String source = "FROM 20141216091438.677610+480 TO 20141218091438.677610+480, TEST END.";
		String value = "SELECT yy,mm,dd,hh,mm,ss AS 'yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mm + ':' + ss' FROM '(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})\\.\\d{6}\\+\\d{3}'";
		if (!value.startsWith(SELECT)){
			return;
		}
		int ASPos = value.indexOf(AS);
		if (ASPos < 0){
			return;
		}
		int FROMPos = value.indexOf(FROM, ASPos + 2);
		if (FROMPos < 0){
			return;
		}
		String varString = value.substring(SELECT.length(), ASPos).trim();
		String target = value.substring(ASPos + AS.length(), FROMPos).trim();
		String regex = value.substring(FROMPos + FROM.length()).trim();
		if (!target.startsWith("'") || !target.endsWith("'") || !regex.endsWith("'") || !regex.endsWith("'")){
			return;
		}
		target = target.substring(1, target.length() - 1);
		regex = regex.substring(1, regex.length() - 1);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		String[] vars = varString.split(",");
		JexlContext jexlContext = new MapContext();
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				String var = vars[i - 1].trim();
				jexlContext.set(var, matcher.group(i));
			}
			Expression expression = jexl.createExpression(target);
			matcher.appendReplacement(sb, expression.evaluate(jexlContext).toString());
		}
		matcher.appendTail(sb);
		System.out.println(sb.toString());
	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		if (logger.isDebugEnabled()) {
			logger.debug("DateTimeResultSetProcessor Process Starts.");
		}
		ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
		ParameterValue[] parameterValues = parameter.listParameterValues();
		for (ParameterValue parameterValue : parameterValues) {
			String value = parameterValue.getValue();
			String parseRet = parseValue(value);
			if (parseRet != null){
				if (logger.isErrorEnabled()){
					logger.error(parseRet);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, parseRet);
			}
			//get target column
			for (int colunmIndex = 0; colunmIndex < metaInfo.getColumnLength(); ++colunmIndex){
				if (metaInfo.getColumnName(colunmIndex).equals(parameterValue.getKey().trim())){
					for (int rowIndex = 0; rowIndex < resultSet.getRowLength(); ++rowIndex){
						String source = resultSet.getValue(rowIndex, colunmIndex);
						JexlContext jexlContext = new MapContext();
						StringBuffer sb = new StringBuffer();
						Matcher matcher = pattern.matcher(source);
						while (matcher.find()){
							if (matcher.groupCount() != vars.length){
								break;
							}
							for (int i = 1; i <= matcher.groupCount(); ++i) {
								jexlContext.set(vars[i - 1], matcher.group(i));
							}
							 matcher.appendReplacement(sb, expression.evaluate(jexlContext).toString());
						}
						matcher.appendTail(sb);
						resultSet.putValue(rowIndex, colunmIndex, sb.toString());
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("DateTimeResultSetProcessor Process Finished.");
		}
	}
	
	//parseValue to generate vars, targetFormat, regex
	private String parseValue(String value) {
		if (!value.startsWith(SELECT)){
			return "Keyword <" + SELECT + "> NOT FOUND.";
		}
		int ASPos = value.indexOf(AS);
		if (ASPos < 0){
			return "Keyword <" + AS + "> NOT FOUND.";
		}
		int FROMPos = value.indexOf(FROM, ASPos + 2);
		if (FROMPos < 0){
			return "Keyword <" + FROM + "> NOT FOUND.";
		}
		String varString = value.substring(SELECT.length(), ASPos).trim();
		String targetFormat = value.substring(ASPos + AS.length(), FROMPos).trim();
		String regex = value.substring(FROMPos + FROM.length()).trim();
		if (!targetFormat.startsWith("'") || !targetFormat.endsWith("'") || !regex.endsWith("'") || !regex.endsWith("'")){
			return "Target format and regular expression should surround with single quotes (').";
		}
		targetFormat = targetFormat.substring(1, targetFormat.length() - 1);
		regex = regex.substring(1, regex.length() - 1);
		try{
			pattern = Pattern.compile(regex);
			vars = varString.split(SPLIT);
			JexlContext context = new MapContext();
			for (String var : vars) {
				var = var.trim();
				if (context.has(var)){
					return "Repeated Variable : " + var;
				}
				context.set(var, var);
			}
			expression = jexl.createExpression(targetFormat);
			String dummyResult = expression.evaluate(context).toString();
			if (logger.isDebugEnabled()){
				logger.debug("Dummy Result of <" + value + "> is : " + dummyResult);
			}
		} catch (PatternSyntaxException e) {
			return "Invalid Regular Expression.";
		} catch (JexlException e){
			return "Invalid Target Format.";
		}
		return null;
	}

}
