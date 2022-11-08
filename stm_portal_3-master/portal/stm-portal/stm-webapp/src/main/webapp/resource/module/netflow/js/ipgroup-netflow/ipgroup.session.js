(function($){

//	var timeInterval = $('#timeInterval').combobox('getValue'),
//		timeObj = parseTime(timeInterval),
//		startTime=timeObj.startTime, 
//		endTime=timeObj.endTime;
//	var displayCount = $('#displayCount').combobox('getValue');
	
	//获取详细页面的参数：timeInterval,startTime,endTime,type
	var tab = $('.ipgroup-netflow-main-tabs').tabs('getSelected');
	
	var timeInterval = tab.find('.ipgroup-detail-timeinterval').combobox('getValue');
	var startTime = tab.find('.ipgroup-detail-custom-starttime').datetimebox('getValue');
	var endTime = tab.find('.ipgroup-detail-custom-endtime').datetimebox('getValue');
	var type = tab.find('.ipgroup-detail-indecator').combobox('getValue');
	var displayCount = 5;

	var ipgroup_id=$('.terminal-ipg').data('ipgroup_id');
	var ipGroupId = $('.netflow-main').data('ipGroupId');
	if(ipgroup_id!=null&&""!=ipgroup_id&&undefined!=ipgroup_id){
		ipGroupId=ipgroup_id;
	}
	
	/**
	 * 初始化datagrid
	 */
	var ipgSessoinDatagridDiv=tab.find('.ipgroup-session-datagrid-selector');
	datagridObj_netflow_ipg_session=oc.ui.datagrid({
			selector:ipgSessoinDatagridDiv,
			url:oc.resource.getUrl('netflow/ipgroup/session/ipGroupSessionPageSelect.htm'),
			queryParams: {
				startTime: startTime,
				endTime: endTime,
				timePerid: timeInterval,
				ipGroupId: ipGroupId,
				rowCount: displayCount,
				needPagination: false,
				querySize: displayCount
			},
			pagination: false,
			sortOrder: 'desc',
			sortName: 'flowIn',
			pageSize: displayCount,
			pageList: [5, 10, 20, 30, 40, 50],
			columns:[[
	         {field:'srcIp',title:'源IP',sortable:false,width:'15%',align:'center'},
	         {field:'dstIp',title:'目的IP',sortable:false,width:'15%',align:'center'},
	         {field:'flowIn',title:'流入流量',sortable:true, align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'flowOut',title:'流出流量',sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'flowTotal',title:'总流量',sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'packetIn',title:'流入包数',sortable:true,align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetOut',title:'流出包数',sortable:true,align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetTotal',title:'总包数',sortable:true,align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'speedIn',title:'流入速率',sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'speedOut',title:'流出速率',sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'speedTotal',title:'总速率',sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'flowPctge',title:'占比',sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return (value == null ? 0 : (value*100).toFixed(2)) + '%';
	         }}
	     ]],
	     onClickRow: function(rowIndex, rowData) {
	    	 $('<div class="device-session"></div>').dialog({    
	    		 	title: '流量分析 - 会话- ' + rowData.srcIp+"-"+rowData.dstIp, 
	    		    width: 1200,    
	    		    height: 580, 
	    		    top: '5%',
	    		    closed: false,    
	    		    cache: false,
	    		    position:'center',
	    		    draggable:false,
	    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToSessionDialog.html"),    
	    		    modal: true   
	    		});
	    	 $('.device-session').data('srcip',rowData.srcIp);
	    	 $('.device-session').data('dstip',rowData.dstIp);
	    	 
	    	 var timeInterval = tab.find('.ipgroup-detail-timeinterval').combobox('getValue');
	    	 var startTime = tab.find('.ipgroup-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = tab.find('.ipgroup-detail-custom-endtime').datetimebox('getValue');
	    	 var type = tab.find('.ipgroup-detail-indecator').combobox('getValue');
	    	 
	    	 $('.device-session').data('timeInterval', timeInterval);
	    	 $('.device-session').data('startTime', startTime);
	    	 $('.device-session').data('endTime', endTime);
	    	 $('.device-session').data('type', type);

	     },
	     onLoadSuccess: function(data) {
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/ipgroup/session/getIpGroupSessionChartData.htm'),
    			data: data.paramBo,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var ti = tab.find('.ipgroup-detail-timeinterval').combobox('getValue');
    				var step = getStep(ti);
    				genNetflowChart(tab.find('.ipgroup-session-hc'), 660, 135, data.data.timeLine, data.data.bos, data.data.unit, data.data.yAxisName, step, ti);
    			}
    		});
	     }
	});
	
	changeIndicatorDetail(ipgSessoinDatagridDiv, type, 'session', 1, true);

})(jQuery);

function showIpgroupSessions(){
	var rows = $(".ipgroup-session-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="ipgroup-app-allapp"></div>').dialog({    
		 	title: "会话",
		    width: 1200,    
		    height: 605, 
		    top: '5%',
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/ipgroup-netflow/ipgroup_session.html"),    
		    modal: true
		});
	}
}