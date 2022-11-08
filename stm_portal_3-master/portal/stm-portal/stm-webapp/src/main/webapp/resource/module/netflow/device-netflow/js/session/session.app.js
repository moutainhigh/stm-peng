(function(){
	
/*	var timeInterval = $('#timeInterval').combobox('getValue'),
	timeObj = parseTime(timeInterval),
	startTime=timeObj.startTime, 
	endTime=timeObj.endTime;
	var displayCount = $('#displayCount').combobox('getValue');*/

	//设备详细页面的参数
//	var timeInterval = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();
	
	var timeInterval = $('.device-session').data('timeInterval');
	var type = $('.device-session').data('type');
	var startTime = $('.device-session').data('startTime');
	var endTime = $('.device-session').data('endTime');

	var displayCount = 5;

	var src_ip = $('.netflow-main').data('src_ip');
	var dst_ip = $('.netflow-main').data('dst_ip');
	var srcip = $('.device-session').data('srcip');
	var dstip = $('.device-session').data('dstip');
	if(srcip != null && " "!=srcip&&undefined!=srcip){
		src_ip=srcip;
		dst_ip=dstip;
	}
	
	var tarDiv = $('.session-app-datagrid');
	datagridObj_netflow_session_app=oc.ui.datagrid({
		selector: tarDiv,
		pagination: false,
		sortOrder : 'desc',
		queryParams :{
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			currentSrcIp : src_ip,
			currentDstIp : dst_ip
		},
		url:oc.resource.getUrl('netflow/session/getappbysession.htm'),
		columns:[[
	       
	         {field:'app_name',title:'应用名称',width:'20%',sortable:false},
	         {field:'in_flows',title:'流入流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'out_flows',title:'流出流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'total_flows',title:'总流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
//	         {field:'in_packages',title:'流入包数',sortable:true},
//	         {field:'out_packages',title:'流出包数',sortable:true},
//	         {field:'total_packages',title:'总包数',sortable:true},
	         {field:'in_speed',title:'流入速率',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'out_speed',title:'流出速率',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'total_speed',title:'总速率',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'flows_rate',title:'占比',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return pctgeFormatter(value);
	         }}
	     ]],
	     onLoadSuccess : function(data){
	    	 var sort = data.sort;
	    	 var rows = data.rows;
	    	 var series = parseForJRPie(rows, sort, 'app');
	    	 
	    	    $('.session-app-hc').highcharts({
	    	        chart: {
	    	            type: 'pie',
	    	            backgroundColor: '#111718',
	    	            options3d: {
	    	                alpha: 45
	    	            }
	    	        },
	    	        title: {
	    	            text: ''
	    	        },
	    	        subtitle: {
	    	            text: ''
	    	        },
	    	        tooltip: {
	    	        	pointFormat: '{point.percentage:.1f}%</b>'
	    	        },
	    	        plotOptions: {
	    	            pie: {
	    	                innerSize: 30,
	    	                depth: 45,
	    	                showInLegend: true,
	    	                dataLabels: {
	    	                	formatter: function() {
	    	                		return Highcharts.numberFormat(this.percentage, 2) +'%';
	    	                	}
	    	                }
	    	            }
	    	        },
	    	        legend: {
	    	        	itemStyle: {
	    	        		"color": "white"
	    	        	},
	    	        	x: -20,
	    	        	y: -30,
	    	        	borderColor: '#172D17',
	    	        	align: 'right',
	    	        	layout: 'vertical'
	    	        },
	    	        credits: {
	    	        	enabled: false
	    	        },
	    	        exporting: {
	    	       		enabled: false
	    	        },
	    	        series: [{
	    	            name: '协议',
	    	            data:series
	    	        }]
	    	    });
	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'app', 2, false);

})();
function showSessionApps(){
	var rows = $(".session-app-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="session-app-allapp"></div>').dialog({    
		 	title: "应用",
		    width: '90%',    
		    height: '90%', 
		    top: '5%',
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:true,
		    href: oc.resource.getUrl("resource/module/netflow/session-netflow/session_app.html"),    
		    modal: true
		});
	}
}