<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.NumberFormat"%>
<%@page contentType="text/html; charset=UTF-8" language="java"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@page
	import="com.mainsteam.stm.common.metric.MetricDataBatchPersisterFactory"%>
<%@page
	import="com.mainsteam.stm.common.metric.MetricDataPersisterMonitor"%>
<%@page import="com.mainsteam.stm.caplib.dict.MetricTypeEnum"%>
<html>
<head>
<%
	MetricDataBatchPersisterFactory factory = (MetricDataBatchPersisterFactory) SpringBeanUtil
	.getObject("metricDataBatchPersisterFactory");
	NumberFormat numberFormat = NumberFormat.getInstance();
	numberFormat.setMaximumFractionDigits(2);
	SimpleDateFormat dateFormat = new SimpleDateFormat(
	"yyyy-MM-dd HH:mm:ss.SSS");
%>
<title>Metric Monitor</title>
<script type="text/javascript">
	function reloadPage() {
		window.location.href = window.location.href;
	}
	setTimeout("reloadPage()", 5000);
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
				<th>指标类别</th>
				<th>待处理指标(个)</th>
				<th>正在处理指标(个)</th>
				<th>最近指标处理时间</th>
				<th>最近指处理标个数</th>
				<th>最近指标处理耗时(ms)</th>
				<th>最近指标处理速度</th>
				<th>指标处理总个数(个)</th>
				<th>指标处理总耗时(ms)</th>
				<th>平均指标处理速度(ms/个)</th>
				<th>指标处理失败总个数(个)</th>
				<th>第一次最近指标处理时间</th>
			</tr>
		</thead>
		<tbody>
			<%
				Map<MetricTypeEnum, String> typeEnumNames = new HashMap<MetricTypeEnum, String>(
						5);
				typeEnumNames.put(MetricTypeEnum.AvailabilityMetric, "可用性指标");
				typeEnumNames.put(MetricTypeEnum.InformationMetric, "信息指标");
				typeEnumNames.put(MetricTypeEnum.PerformanceMetric, "性能指标");
				typeEnumNames.put(null, "自定义指标");
				List<MetricTypeEnum> typeEnumsList = new ArrayList<MetricTypeEnum>(
						5);
				for (MetricTypeEnum typeEnum : MetricTypeEnum.values()) {
					typeEnumsList.add(typeEnum);
				}
				typeEnumsList.add(null);
				for (MetricTypeEnum typeEnum : typeEnumsList) {
					MetricDataPersisterMonitor metricMonitor = (MetricDataPersisterMonitor) factory
							.getMetricDataBatchPersister(typeEnum);
					int remainSize = metricMonitor.getRemainingSize();
					int activeSize = metricMonitor.getActiveSize();
					long overSumSize = metricMonitor.getOverSumSize();
					Date batchDate = metricMonitor.getBatchDateTime();
					int batchCount = metricMonitor.getBatchMetricCount();
					long batchLossTime = metricMonitor.getBatchLossTime();
					Date startDate = metricMonitor.getStartDateTime();
					long lossTime = metricMonitor.getLossTime();
					long errCount = metricMonitor.getErrCount();
			%>
			<tr>
				<td><%=typeEnumNames.get(typeEnum)%></td>
				<td><%=remainSize%></td>
				<td><%=activeSize%></td>
				<td><%=batchDate == null ? "" : dateFormat.format(batchDate)%></td>
				<td><%=batchCount%></td>
				<td><%=batchLossTime%></td>
				<td><%=batchCount > 0 ? numberFormat
						.format((double) batchLossTime / batchCount) : -1%></td>
				<td><%=overSumSize%></td>
				<td><%=lossTime%></td>
				<td><%=overSumSize > 0 ? numberFormat
						.format((double) lossTime / overSumSize) : -1%></td>
				<td><%=errCount%></td>
				<td><%=startDate == null ? "" : dateFormat.format(startDate)%></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>