<%@page import="java.util.Arrays"%>
<%@page import="com.mainsteam.stm.common.metric.obj.MetricData"%>
<%@page import="com.mainsteam.stm.pluginprocessor.ParameterValue"%>
<%@page
	import="com.mainsteam.stm.executor.obj.CustomMetricPluginParameter"%>
<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@page import="com.mainsteam.stm.executor.MetricExecutor"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>调度器监控</title>
<script type="text/javascript">
	
</script>
</head>
<body>
	<%
		MetricData data = null;
		long instanceId = -1;
		String metricId = "customMetricId";
		String pluginId = request.getParameter("pluginId");
		if (pluginId != null) {
			MetricExecutor metricExecutor = (MetricExecutor) SpringBeanUtil
					.getBean(MetricExecutor.class);
			CustomMetricPluginParameter parameter = new CustomMetricPluginParameter();
			parameter.setCustomMetricId(metricId);

			instanceId = Long.parseLong(request.getParameter("instanceId"));
			parameter.setInstanceId(instanceId);

			parameter.setPluginId(pluginId);
			String executeParameterKey = request
					.getParameter("executeParameterKey");
			String[] executeParameterKeys = request
						.getParameterValues("executeParameterKey");
			String[] executeParameterValues = request
						.getParameterValues("executeParameterValue");
			ParameterValue[] values = null;
			if (executeParameterKeys == null) {
				String executeParameterValue = request
						.getParameter("executeParameterValue");
				values = new ParameterValue[1];
				ParameterValue p = new ParameterValue();
				p.setKey(executeParameterKey);
				p.setValue(executeParameterValue);
				values[0] = p;
			} else {
				values = new ParameterValue[executeParameterKeys.length];
				for (int i = 0; i < values.length; i++) {
					ParameterValue p = new ParameterValue();
					p.setKey(executeParameterKeys[i]);
					p.setValue(executeParameterValues[i]);
					values[i] = p;
				}
			}
			parameter.setParameters(values);
			data = metricExecutor
					.catchMetricDataWithCustomPlugin(parameter);
		}
	%>
	<form action="customMetricQuery.jsp" method="post" target="_self">
		<li><span>取值方式：</span><span><input type="text"
				name="pluginId" value="SnmpPlugin" /></span></li>
		<li><span>执行参数取值-方法：</span> <span> <input type="text"
				name="executeParameterKey" value="method" /><input type="text"
				name="executeParameterValue" value="walk" /></span></li>
		<li><span>执行参数oid：</span><span><input type="text"
				name="executeParameterKey" value="oid" /><input type="text"
				name="executeParameterValue" value="1.3.6.1.2.1.2.2.1.6" /> </span></li>
		<li><span>资源实例id：</span> <span><input type="text"
				name="instanceId" value="501" /> </span></li>
		<li><input type="submit" name="submit" value="提交"></li>
	</form>
	<div>
		<%
			if (data != null) {
		%>
		资源实例id：<%=instanceId%><br> 指标id：<%=metricId%><br> 获取值为：<%=Arrays.toString(data.getData())%><br>
		<%
			}
		%>
	</div>
</body>
</html>
