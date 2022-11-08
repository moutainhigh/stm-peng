<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html; charset=UTF-8" language="java"%>

<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@page
	import="com.mainsteam.stm.dataprocess.engine.MetricDataProcessEngineImpl"%>
<%@page
	import="com.mainsteam.stm.dataprocess.engine.MetricDataProcessor"%>
<html>
<head>
<%
	MetricDataProcessEngineImpl metricDataProcessEngine = (MetricDataProcessEngineImpl) SpringBeanUtil
	.getObject("metricDataProcessEngine");
	String operate = request.getParameter("operate");
	String filterName = request.getParameter("cName");
	if("filtered".equals(operate)){
		metricDataProcessEngine.filteredMetricDataProcessor(filterName);
	}else if("unFiltered".equals(operate)){
		metricDataProcessEngine.unFilteredMetricDataProcessor(filterName);
	}
	Map<String,MetricDataProcessor> filteredMap = metricDataProcessEngine.getFilteredMetricDataProcessor();
	List<MetricDataProcessor> metricDataProcessors = metricDataProcessEngine.getMetricDataProcessors();
%>
<title>MetricDataProcessor Managered</title>
<script type="text/javascript">
	function reloadPage() {
		window.location.href = window.location.href;
	}
	//setTimeout("reloadPage()", 5000);
</script>
<!-- Javascript goes in the document HEAD -->
<script type="text/javascript">
	function altRows(id) {
		if (document.getElementsByTagName) {

			var table = document.getElementById(id);
			var rows = table.getElementsByTagName("tr");

			for (i = 0; i < rows.length; i++) {
				if (i % 2 == 0) {
					rows[i].className = "evenrowcolor";
				} else {
					rows[i].className = "oddrowcolor";
				}
			}
		}
	}

	window.onload = function() {
		altRows('alternatecolor');
	}
	
	function manageFiltered(cName,isFiltered){
		var url = "dataProcessManager.jsp?cName="+cName+"&operate="+(isFiltered?"filtered":"unFiltered");
		document.location.href=url;
	}
</script>


<!-- CSS goes in the document HEAD or added to your external stylesheet -->
<style type="text/css">
table.altrowstable {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #a9c6c9;
	border-collapse: collapse;
}

table.altrowstable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #a9c6c9;
}

table.altrowstable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #a9c6c9;
}

.oddrowcolor {
	background-color: #d4e3e5;
}

.evenrowcolor {
	background-color: #c3dde0;
}
</style>
</head>
<body>
	<table class="altrowstable" id="alternatecolor">
		<thead>
			<tr>
				<th>实现类</th>
				<th>Order</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<%
				for (MetricDataProcessor metricDataProcessor : metricDataProcessors) {
					boolean isFiltered = filteredMap
							.containsKey(metricDataProcessor.getClass()
									.getSimpleName());
			%>
			<tr>
				<td><%=metricDataProcessor.getClass().getName()%></td>
				<td><%=metricDataProcessor.getOrder()%></td>
				<td><input type="button"
					value="<%=isFiltered ?  "取消过滤":"过滤" %>"
					onclick="manageFiltered('<%=metricDataProcessor.getClass().getSimpleName()%>',<%=!isFiltered%>)">
				</td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>