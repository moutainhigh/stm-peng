var datagridObj_netflow_ipgroup_more;
(function($){

//	var timeInterval = $('.device-detail-timeinterval').combobox('getValue');
//	var type = $('.device-detail-indecator').combobox('getValue');
	var timeInterval = $('.device-ipgroup-more-wrapper').data('timeInterval');
	var type = $('.device-ipgroup-more-wrapper').data('type');
	var displayCount = 20;
	var startTime = $('.device-ipgroup-more-wrapper').data('starttime');
	var endTime = $('.device-ipgroup-more-wrapper').data('endtime');

	/**
	 * 初始化datagrid
	 */
	var datagridDiv=$('#oc_module_netflow_device_ipgroup_more_datagrid');
	var	deviceIp = $('.netflow-main').data('deviceIp');

	datagridObj_netflow_ipgroup_more = oc.ui.datagrid({
		selector:datagridDiv,
		queryParams :{
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: 20,
			recordCount : 20,
			deviceIp:deviceIp ,
			sort : "in_flows",
			order : 'desc',
			showpagination : true
		},
		pageSize: 20,
		pageList: [5, 10, 20, 30, 40, 50],
		pagination: true,
		sortOrder : 'desc',
		sortName: 'in_flows',
		url:oc.resource.getUrl('netflow/devices/findipgbydevice.htm'),
		columns:[[
	          
	         {field:'ipgroup_name',title:'IP分组名称',width:'10%',sortable:false},
	       
	         {field:'in_flows',title:'流入流量',sortable:true,width:'10%',order:'desc',formatter:function(value,rowData,rowIndex){
	        	 var GB = 1024*1024*1024;
	        	 var MB = 1024*1024 ;
	        	 if(value == null || value == 0){
	        		 return '0MB';
	        	 }
	        	 else{
	        		 if(value >= GB ){
	        			 value = value/GB;
	        			 value = value.toFixed(2);
	        			 return value + 'GB';
	        		 }
	        		 value=value/MB;
	        		 value = value.toFixed(2);
	        		 return value + 'MB';
	        	 }
	         }},
	         {field:'out_flows',title:'流出流量',sortable:true,width:'10%',order:'desc',formatter:function(value,rowData,rowIndex){
	        	 var GB = 1024*1024*1024;
	        	 var MB = 1024*1024 ;
	        	 if(value == null || value == 0){
	        		 return '0MB';
	        	 }
	        	 else{
	        		 if(value >= GB ){
	        			 value = value/GB;
	        			 value = value.toFixed(2);
	        			 return value + 'GB';
	        		 }
	        		 value=value/MB;
	        		 value = value.toFixed(2);
	        		 return value + 'MB';
	        	 }
	         }},
	         {field:'total_flows',title:'总流量',sortable:true,width:'10%',order:'desc',formatter:function(value,rowData,rowIndex){
	        	 var GB = 1024*1024*1024;
	        	 var MB = 1024*1024 ;
	        	 if(value == null || value == 0){
	        		 return '0MB';
	        	 }
	        	 else{
	        		 if(value >= GB ){
	        			 value = value/GB;
	        			 value = value.toFixed(2);
	        			 return value + 'GB';
	        		 }
	        		 value=value/MB;
	        		 value = value.toFixed(2);
	        		 return value + 'MB';
	        	 }
	         }},
	         {field:'in_packages',title:'流入包数',sortable:true,width:'8%',order:'desc'},
	         {field:'out_packages',title:'流出包数',sortable:true,width:'8%',order:'desc'},
	         {field:'total_package',title:'总包数',sortable:true,width:'9%',order:'desc'},
	         {field:'in_speed',title:'流入速率',sortable:true,width:'9%',order:'desc',formatter:function(val,rowData,rowIndex){
	        	 var GB = parseFloat(1024*1024*1024);
	        	 var MB = parseFloat(1024*1024); 
	        	 var KB = parseFloat(1024); 	 
	        	 if(val == null || val == 0){
	        		 return '0KBps';
	        	 }
	        	 else{	        		 
	        		 var speed = parseFloat(val);
	        		 if(speed >= GB ){
	        			 speed = speed/GB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'GBps';
	        		 } else if(speed >= MB ){
	        			 speed = speed/MB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'MBps';
	        		 } else if(speed > KB){
	        			 speed = speed / KB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'KBps';
	        		 }else{
	        			 return speed+"Bps";
	        		 }
	        	 }
	         }},
	         {field:'out_speed',title:'流出速率',sortable:true,width:'10%',order:'desc',formatter:function(val,rowData,rowIndex){
	        	 var GB = parseFloat(1024*1024*1024);
	        	 var MB = parseFloat(1024*1024); 
	        	 var KB = parseFloat(1024); 	 
	        	 if(val == null || val == 0){
	        		 return '0KBps';
	        	 }
	        	 else{
	        		 var speed = parseFloat(val);
	        		 if(speed >= GB ){
	        			 speed = speed/GB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'GBps';
	        		 } else if(speed >= MB ){
	        			 speed = speed/MB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'MBps';
	        		 } else if(speed > KB){
	        			 speed = speed / KB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'KBps';
	        		 }else{
	        			 return speed+"Bps";
	        		 }
	        	 }
	         }},
	         {field:'total_speed',title:'总速率',sortable:true,width:'10%',order:'desc',formatter:function(val,rowData,rowIndex){
	        	 var GB = parseFloat(1024*1024*1024);
	        	 var MB = parseFloat(1024*1024); 
	        	 var KB = parseFloat(1024); 	 
	        	 if(val == null || val == 0){
	        		 return '0KBps';
	        	 }
	        	 else{   		 
	        		 var speed = parseFloat(val);
	        		 if(speed >= GB ){
	        			 speed = speed/GB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'GBps';
	        		 } else if(speed >= MB ){
	        			 speed = speed/MB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'MBps';
	        		 } else if(speed > KB){
	        			 speed = speed / KB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'KBps';
	        		 }else{
	        			 return speed+"Bps";
	        		 }
	        	 }
	         }},
	         {field:'flows_rate',title:'占比',sortable:true,width:'9%',order:'desc',formatter:function(val,rowData,rowIndex){
	        	if(val == null || val == 0){
	        		return '0%';
	        	}else{
	        		var rate = parseFloat(val);
	        		rate = rate*100;
	        		rate = rate.toFixed(2);
	        		return rate+'%';
	        	}
	         }}
	     ]],
	     onLoadSuccess: function(data) {
	    	 //不显示每页条数列表
	    	 var p = datagridDiv.datagrid('getPager');
	    	 $(p).pagination({
	    		 showPageList: false
	    	 });
	     }
	});
	
	initMoreTimeInterval('.device-detail-ipgroup-more-timeinterval', '.device-detail-ipgroup-more-custom-date', '#device_detail_ipgroup_more_search', '.device-detail-ipgroup-more-indecator', datagridDiv);
	
	initMoreType('.device-detail-ipgroup-more-indecator', datagridDiv, 'ipg', 2, false);
	
	initExportBtn('#device_detail_ipgroup_more_export', function() {
		var type = $('.device-detail-ipgroup-more-indecator').combobox('getValue');
		top.location = oc.resource.getUrl("netflow/devices/exportIpGroup.htm?type="+type);
	});
	
	initSearchBtn('#device_detail_ipgroup_more_search', '.device-detail-ipgroup-more-custom-starttime', 
			'.device-detail-ipgroup-more-custom-endtime', function() {
		var startTime = $('.device-detail-ipgroup-more-custom-starttime').datetimebox('getValue');
		var endTime = $('.device-detail-ipgroup-more-custom-endtime').datetimebox('getValue');
		var timeInterval = $('.device-detail-ipgroup-more-timeinterval').combobox('getValue');
		var checkMsg = checkCustomTime(startTime, endTime);
		if('' != checkMsg) {
			alert(checkMsg);
		} else {
			datagridDiv.datagrid('load', {
				starttime: startTime,
				endtime: endTime,
				timePerid: timeInterval,
				rowCount: 20,
				recordCount : 20,
				deviceIp:deviceIp ,
				sort : "in_flows",
				order : 'desc',
				showpagination : true
			});
		}
	});
	
	initCustomDateStatus('.device-detail-ipgroup-more-indecator', '.device-detail-ipgroup-more-custom-starttime', '.device-detail-ipgroup-more-custom-endtime', 
			'.device-detail-ipgroup-more-custom-date', '#device_detail_ipgroup_more_search', '.device-detail-ipgroup-more-timeinterval', 
			'.device-ipgroup-more-wrapper', '.device-detail-timeinterval', '.device-detail-indecator');

	changeIndicatorDetail(datagridDiv, type, 'ipg', 2, false);

})(jQuery);