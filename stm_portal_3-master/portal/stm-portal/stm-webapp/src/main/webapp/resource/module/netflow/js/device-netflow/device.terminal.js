(function($){

	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');

	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	
	var deviceIp = $('.netflow-main').data('deviceIp');
	var tarDiv = curTab.find('.device-terminal-datagrid');
	
	
	datagridObj_netflow_terminal = oc.ui.datagrid({
		selector:tarDiv,
		queryParams :{
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			deviceIp:deviceIp ,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			starttime: stime,
			endtime: etime
		},
		pageSize: displayCount,
		pageList: [5],
		pagination: false,
		sortOrder : 'desc',
		sortName: 'in_flows',
		url:oc.resource.getUrl('netflow/devices/findterminalbydevice.htm'),
		columns:[[
	          
	         {field:'terminal_name',title:'终端名称',sortable:false},
	       
	         {field:'in_flows',title:'流入流量',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'out_flows',title:'流出流量',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'total_flows',title:'总流量',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
//	         {field:'in_packages',title:'流入包数',sortable:true},
//	         {field:'out_packages',title:'流出包数',sortable:true},
//	         {field:'total_package',title:'总包数',sortable:true},
	         {field:'in_speed',title:'流入速率',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'out_speed',title:'流出速率',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'total_speed',title:'总速率',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	return flowFormatter(value, 'speed');
	         }},
	         {field:'flows_rate',title:'占比',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return pctgeFormatter(value);
	         }}
	     ]],
	     onClickRow: function(rowIndex, rowData) {
	    	 $('<div class="device-terminal"></div>').dialog({    
	    		 	title: '流量分析 - 终端 - ' + rowData.terminal_Name,   
	    		    width: 1200,
	    		    height: 580, 
	    		    top: '5%',
	    		    closed: false,    
	    		    cache: false,
	    		    position:'center',
	    		    draggable:true,
	    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToTerminalDialog.html"),    
	    		    modal: true   
	    		});
	    	 $('.device-terminal').data('terminal_ip',rowData.terminal_Name);
	    	 
	    	 var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue')
	    	 var type = curTab.find('.device-detail-indecator').combobox('getValue');
	    	 var startTime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	    	 $('.device-terminal').data('startTime', startTime);
	    	 $('.device-terminal').data('endTime', endTime);
	    	 $('.device-terminal').data('timeInterval', timeInterval);
	    	 $('.device-terminal').data('type', type);
	     },
	     onLoadSuccess : function(data){
	    	 var sort = data.sort;
	    	 var rows = data.rows;
	    	 var series = parseForJRPie(rows, sort, 'terminal');
	    	 	/**
	    		 * 初始化报表
	    		 * 
	    		 * */
	    	 	curTab.find('.device-terminal-hc').highcharts({
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
	    	            name: '终端',
	    	            data:series
	    	        }]
	    	    });
	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'terminal', 2, false);

})(jQuery);