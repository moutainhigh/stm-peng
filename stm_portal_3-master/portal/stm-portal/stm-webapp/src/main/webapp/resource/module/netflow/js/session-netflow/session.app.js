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
	
//	var timeInterval = $('.device-session').data('timeInterval');
//	var type = $('.device-session').data('type');
//	var startTime = $('.device-session').data('startTime');
//	var endTime = $('.device-session').data('endTime');
	
	var tab = $('.sessionsingle-netflow-main-tabs').tabs('getSelected');

	var startTime = tab.find('.session-detail-custom-starttime').datetimebox('getValue');
	var endTime = tab.find('.session-detail-custom-endtime').datetimebox('getValue');
	var timeInterval = tab.find('.session-detail-timeinterval').combobox('getValue');
	var type = tab.find('.session-detail-indecator').combobox('getValue');
	var displayCount = 5;

	var src_ip = $('.netflow-main').data('src_ip');
	var dst_ip = $('.netflow-main').data('dst_ip');
	var srcip = $('.device-session').data('srcip');
	var dstip = $('.device-session').data('dstip');
	if(srcip != null && " "!=srcip&&undefined!=srcip){
		src_ip=srcip;
		dst_ip=dstip;
	}
	
	var tarDiv = tab.find('.session-app-datagrid');
	datagridObj_netflow_session_app=oc.ui.datagrid({
		selector: tarDiv,
		pagination: false,
		sortOrder : 'desc',
		sortName: 'in_flows',
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
//		columns:[[
//	       
//	         {field:'app_name',title:'应用名称',width:'20%',sortable:false},
//	         {field:'in_flows',title:'流入流量',sortable:true,formatter:function(value,rowData,rowIndex){
//	        	 var GB = 1024*1024*1024;
//	        	 var MB = 1024*1024 ;
//	        	 if(value == null || value == 0){
//	        		 return '0MB';
//	        	 }
//	        	 else{
//	        		 if(value >= GB ){
//	        			 value = value/GB;
//	        			 value = value.toFixed(2);
//	        			 return value + 'GB';
//	        		 }
//	        		 value=value/MB;
//	        		 value = value.toFixed(2);
//	        		 return value + 'MB';
//	        	 }
//	         }},
//	         {field:'out_flows',title:'流出流量',sortable:true,formatter:function(value,rowData,rowIndex){
//	        	 var GB = 1024*1024*1024;
//	        	 var MB = 1024*1024 ;
//	        	 if(value == null || value == 0){
//	        		 return '0MB';
//	        	 }
//	        	 else{
//	        		 if(value >= GB ){
//	        			 value = value/GB;
//	        			 value = value.toFixed(2);
//	        			 return value + 'GB';
//	        		 }
//	        		 value=value/MB;
//	        		 value = value.toFixed(2);
//	        		 return value + 'MB';
//	        	 }
//	         }},
//	         {field:'total_flows',title:'总流量',sortable:true,formatter:function(value,rowData,rowIndex){
//	        	 var GB = 1024*1024*1024;
//	        	 var MB = 1024*1024 ;
//	        	 if(value == null || value == 0){
//	        		 return '0MB';
//	        	 }
//	        	 else{
//	        		 if(value >= GB ){
//	        			 value = value/GB;
//	        			 value = value.toFixed(2);
//	        			 return value + 'GB';
//	        		 }
//	        		 value=value/MB;
//	        		 value = value.toFixed(2);
//	        		 return value + 'MB';
//	        	 }
//	         }},
//	         {field:'in_packages',title:'流入包数',sortable:true},
//	         {field:'out_packages',title:'流出包数',sortable:true},
//	         {field:'total_packages',title:'总包数',sortable:true},
//	         {field:'in_speed',title:'流入速率',sortable:true,formatter:function(val,rowData,rowIndex){
//	        	 var GB = parseFloat(1024*1024*1024);
//	        	 var MB = parseFloat(1024*1024); 
//	        	 var KB = parseFloat(1024); 	 
//	        	 if(val == null || val == 0){
//	        		 return '0KBps';
//	        	 }
//	        	 else{	        		 
//	        		 var speed = parseFloat(val);
//	        		 if(speed >= GB ){
//	        			 speed = speed/GB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'GBps';
//	        		 } else if(speed >= MB ){
//	        			 speed = speed/MB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'MBps';
//	        		 } else if(speed > KB){
//	        			 speed = speed / KB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'KBps';
//	        		 }else{
//	        			 return speed+"Bps";
//	        		 }
//	        	 }
//	         }},
//	         {field:'out_speed',title:'流出速率',sortable:true,formatter:function(val,rowData,rowIndex){
//	        	 var GB = parseFloat(1024*1024*1024);
//	        	 var MB = parseFloat(1024*1024); 
//	        	 var KB = parseFloat(1024); 	 
//	        	 if(val == null || val == 0){
//	        		 return '0KBps';
//	        	 }
//	        	 else{
//	        		 var speed = parseFloat(val);
//	        		 if(speed >= GB ){
//	        			 speed = speed/GB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'GBps';
//	        		 } else if(speed >= MB ){
//	        			 speed = speed/MB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'MBps';
//	        		 } else if(speed > KB){
//	        			 speed = speed / KB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'KBps';
//	        		 }else{
//	        			 return speed+"Bps";
//	        		 }
//	        	 }
//	         }},
//	         {field:'total_speed',title:'总速率',sortable:true,formatter:function(val,rowData,rowIndex){
//	        	 var GB = parseFloat(1024*1024*1024);
//	        	 var MB = parseFloat(1024*1024); 
//	        	 var KB = parseFloat(1024); 	 
//	        	 if(val == null || val == 0){
//	        		 return '0KBps';
//	        	 }
//	        	 else{   		 
//	        		 var speed = parseFloat(val);
//	        		 if(speed >= GB ){
//	        			 speed = speed/GB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'GBps';
//	        		 } else if(speed >= MB ){
//	        			 speed = speed/MB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'MBps';
//	        		 } else if(speed > KB){
//	        			 speed = speed / KB;
//	        			 speed = speed.toFixed(2);
//	        			 return speed + 'KBps';
//	        		 }else{
//	        			 return speed+"Bps";
//	        		 }
//	        	 }
//	         }},
//	         {field:'flows_rate',title:'占比',sortable:true,formatter:function(val,rowData,rowIndex){
//	        	if(val == null || val == 0){
//	        		return '0%';
//	        	}else{
//	        		var rate = parseFloat(val);
//	        		rate = rate*100;
//	        		rate = rate.toFixed(2);
//	        		return rate+'%';
//	        	}
//	        	
//	        	
//	         }}
//	     ]],
	     onLoadSuccess : function(data){
	    	 var sort = data.sort;
	    	 var rows = data.rows;
	    	 var series = parseForJRPie(rows, sort, 'app');
	    	 
//	    	 for(var i = 0; i < rows.length; i++){
//	    		 var element = [];
//	    		 var name = rows[i].app_name;
//	    		 var data = "";
//	    		 switch(sort){
//	    		 case "in_flows":  data = parseFloat(rows[i].in_flows);break;
//	    		 case "out_flows":  data =parseFloat(rows[i].out_flows);break;
//	    		 case "total_flows":  data =parseFloat(rows[i].total_flows) ;break;
//	    		 case "in_packages":  data =parseFloat(rows[i].in_packages);break;
//	    		 case "out_packages":  data =parseFloat(rows[i].out_packages);break;
//	    		 case "total_packages":  data =parseFloat(rows[i].total_packages);break;
//	    		 case "in_speed":  data=parseFloat(rows[i].in_speed);break;
//	    		 case "out_speed":  data=parseFloat(rows[i].out_speed);break;
//	    		 case "total_speed":  data=parseFloat(rows[i].total_speed);break;
//	    		 case "flows_rate":  data=parseFloat(rows[i].flows_rate);break;
//	    		 }
//	    		 element.push(name);
//	    		 element.push(data);
//	    		 series.push(element);
//	    		
//	    	 }
	    	 
	    	    tab.find('.session-app-hc').highcharts({
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
		    width: 1200,
		    height: 605, 
		    top: '5%',
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/session-netflow/session_app.html"),    
		    modal: true
		});
	}
}