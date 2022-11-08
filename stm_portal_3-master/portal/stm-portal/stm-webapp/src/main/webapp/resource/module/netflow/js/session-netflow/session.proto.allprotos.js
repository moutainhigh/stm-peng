(function(){

//	var timeInterval = $('#timeInterval').combobox('getValue');

	var mainDiv = $('.sessionsingle-detail-proto-more-main');
	
	var startTime = $('.session-detail-custom-starttime').datetimebox('getValue');
	var endTime = $('.session-detail-custom-endtime').datetimebox('getValue');
	var timeInterval = $('.session-detail-timeinterval').combobox('getValue');
	var type = $('.session-detail-indecator').combobox('getValue');
	var rowCount = 20;

	 var src_ip = $('.netflow-main').data('src_ip');
	 var dst_ip = $('.netflow-main').data('dst_ip');
	
	//timeinterval
	oc.ui.combobox({
		placeholder: null,
		selector: mainDiv.find('.sessionsingle-detail-proto-more-timeinterval'),
		data:[
			{id:'1hour',name:'最近一小时'},
			{id:'6hour',name:'最近六小时'},
			{id:'1day',name:'最近一天'},
			{id:'7day',name:'最近一周'},
			{id: '30day', name:'最近一个月'},
			{id:'custom',name:'自定义'}
		],
		selected: '1hour',
		onSelect: function(d) {
			
			if(d.id == 'custom') {
				mainDiv.find('.sessionsingle-detail-proto-more-custom-date').removeClass('hide');
				mainDiv.find('#sessionsingle_detail_proto_more_search').removeClass('hide');
			} else {
				mainDiv.find('.sessionsingle-detail-proto-more-custom-date').addClass('hide');
				mainDiv.find('#sessionsingle_detail_proto_more_search').addClass('hide');

				var data = {
					'onePageRows': rowCount,
					'sort': "in_flows",
					'order': 'desc',
					'showpagination': false,
					'recordCount': rowCount,
					'rowCount': rowCount,
					'timePerid': d.id,
					'currentSrcIp': src_ip,
					'currentDstIp': dst_ip
				};
				tarDiv.datagrid('load', data);
			}
		}
	});
	
	//type
	oc.ui.combobox({
		selector: mainDiv.find('.sessionsingle-detail-proto-more-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		placeholder: null,
		onSelect: function(d) {
			changeIndicatorDetail(tarDiv, d.id, 'proto', 2, false);
		}
	});

	//datetimebox
	mainDiv.find('.sessionsingle-detail-proto-more-custom-starttime').datetimebox();
	mainDiv.find('.sessionsingle-detail-proto-more-custom-endtime').datetimebox();

	//search button
	mainDiv.find('#sessionsingle_detail_proto_more_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			
			var t = mainDiv.find('.sessionsingle-detail-proto-more-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.sessionsingle-detail-proto-more-custom-starttime').datetimebox('getValue');
			var endTime = mainDiv.find('.sessionsingle-detail-proto-more-custom-endtime').datetimebox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'starttime': startTime,
						'endtime': endTime,
						'onePageRows': rowCount,
						'sort': "in_flows",
						'order': 'desc',
						'showpagination': false,
						'recordCount': rowCount,
						'rowCount': rowCount,
						'timePerid': t,
						'currentSrcIp': src_ip,
						'currentDstIp': dst_ip
				};
				
				tarDiv.datagrid('load', data);
			}
		}
	});
	
	//export button
	mainDiv.find('#sessionsingle_detail_proto_more_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.sessionsingle-detail-proto-more-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/session/exportProto.htm?type="+type);
		}
	});
	
	//init status
	mainDiv.find('.sessionsingle-detail-proto-more-custom-date').addClass('hide');
	mainDiv.find('#sessionsingle_detail_proto_more_search').addClass('hide');

	if(timeInterval) {
		mainDiv.find('.sessionsingle-detail-proto-more-timeinterval').combobox('setValue', timeInterval);
		
		if(timeInterval == 'custom') {
			mainDiv.find('.sessionsingle-detail-proto-more-custom-date').removeClass('hide');
			mainDiv.find('#sessionsingle_detail_proto_more_search').removeClass('hide');

			mainDiv.find('.sessionsingle-detail-proto-more-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.sessionsingle-detail-proto-more-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	
	if(type) {
		mainDiv.find('.sessionsingle-detail-proto-more-indecator').combobox('setValue', type);
	}
	
	var tarDiv = mainDiv.find('.sessionsingle-proto-allprotos');
	oc.ui.datagrid({
		selector:tarDiv,
		queryParams: {		
			starttime: startTime,
			endtime: endTime,
			rowCount: 20,
			recordCount: 20,
			timePerid: timeInterval,
			sort: "in_flows",
			order: 'desc',
			showpagination: true,
			currentSrcIp: src_ip,
			currentDstIp:dst_ip
		},
		pagination: true,
		sortOrder : 'desc',
		sortName: 'in_flows',
		pageSize: 20,
		url: oc.resource.getUrl('/netflow/session/getprotocolbysession.htm'),
		columns: [[
	          
	         {field:'proto_name',title:'协议名称',sortable:false,width:'10%'},
	       
	         {field:'in_flows',title:'流入流量',sortable:true,width:'10%',order: 'desc',formatter:function(value,rowData,rowIndex){
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
	         {field:'out_flows',title:'流出流量',sortable:true,width:'10%',order: 'desc',formatter:function(value,rowData,rowIndex){
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
	         {field:'total_flows',title:'总流量',sortable:true,width:'10%',order: 'desc',formatter:function(value,rowData,rowIndex){
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
	         {field:'in_packages',title:'流入包数',sortable:true,width:'8%',order: 'desc'},
	         {field:'out_packages',title:'流出包数',sortable:true,width:'8%',order: 'desc'},
	         {field:'total_packages',title:'总包数',sortable:true,width:'8%',order: 'desc'},
	         {field:'in_speed',title:'流入速率',sortable:true,width:'9%',order: 'desc',formatter:function(val,rowData,rowIndex){
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
	         {field:'out_speed',title:'流出速率',sortable:true,width:'10%',order: 'desc',formatter:function(val,rowData,rowIndex){
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
	         {field:'total_speed',title:'总速率',sortable:true,width:'10%',order: 'desc',formatter:function(val,rowData,rowIndex){
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
	         {field:'flows_rate',title:'占比',sortable:true,width:'9%',order: 'desc',formatter:function(val,rowData,rowIndex){
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
 			var p = tarDiv.datagrid('getPager');
 			$(p).pagination({
 				showPageList: false
 			});
 	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'proto', 2, false);

})();