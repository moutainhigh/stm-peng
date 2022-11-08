(function(){
	
	//设备详细页面的参数
//	var timeInterval = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();

	var startTime = $('.device-terminal').data('startTime');
	var endTime = $('.device-terminal').data('endTime');
	var timeInterval = $('.device-terminal').data('timeInterval');
	var type = $('.device-terminal').data('type');
	var displayCount = 5;

	var terminal_name = $('.netflow-main').data('terminal_name');
	var terminal_ip = $('.device-terminal').data('terminal_ip');
	if(terminal_ip != null && ''!= terminal_ip && undefined != terminal_ip){
		terminal_name = terminal_ip;
	}
	var tarDiv = $('.terminal-app-datagrid');
	datagridObj_netflow_terminal_app = oc.ui.datagrid({
		selector:tarDiv,
		queryParams :{
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			terminal_name : terminal_name
		},
		pagination: false,
		sortOrder : 'desc',
		url:oc.resource.getUrl('netflow/terminal/getappbyterminal.htm'),
		columns:[[
	          
	         {field:'app_name',title:'应用名称',sortable:false},
	       
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
	    	 
	    	    $('.terminal-app-hc').highcharts({
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

function showTerminalApps(){
	var rows = $(".terminal-app-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="devicegroup-app-allapp"></div>').dialog({    
		 	title: "应用",
		 	width: 1100,    
		    height: 570,    
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/terminal-netflow/terminal_app.html"),    
		    modal: true
		});
	}
}