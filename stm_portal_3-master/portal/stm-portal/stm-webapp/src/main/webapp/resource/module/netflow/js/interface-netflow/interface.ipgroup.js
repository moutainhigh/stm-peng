(function($){

	var curTab = $('.interface-netflow-main-tabs').tabs('getSelected');

	var displayCount = 5;
	var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.interface-detail-indecator').combobox('getValue');
	var startTime = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
	var endTime = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
	
	/**
	 * 初始化datagrid
	 */
	var datagridDiv=curTab.find('.interface-ipgroup-datagrid'),
		ifId = $('.netflow-main').data('ifId');
		datagridObj_netflow_ipg=oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('netflow/if/ipgroup/ifIpgroupPageSelect.htm'),
			queryParams: {
				startTime: startTime,
				endTime: endTime,
				timePerid: timeInterval,
				ifId: ifId,
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
	         {field:'name',title:'IP组名称',sortable:false,width:'20%',align:'center'},
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
	    	 $('<div class="terminal-ipg"></div>').dialog({    
	    		 title: '流量分析 - IP分组 - ' + rowData.name,    
	    		 width: 1200,    
	    		    height: 620,    
	    		    top:'5%',
	    		    closed: false,    
	    		    cache: false,
	    		    position:'center',
	    		    draggable:true,
	    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToIPGDialog.html"),    
	    		    modal: true   
	    		});
	    	 $('.terminal-ipg').data('ipgroup_id',rowData.ipGroupId);
	    	 
	    	 //传值：timeInterval, type, startTime, endTime
	    	 var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue')
	    	 var type = curTab.find('.interface-detail-indecator').combobox('getValue');
	    	 var startTime = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
	    	 $('.terminal-ipg').data('startTime', startTime);
	    	 $('.terminal-ipg').data('endTime', endTime);
	    	 $('.terminal-ipg').data('timeInterval', timeInterval);
	    	 $('.terminal-ipg').data('type', type);

	     },
	     onLoadSuccess: function(data) {
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/if/ipgroup/getIfIpgroupChartData.htm'),
    			data: data.paramBo,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
       				var ti = curTab.find('.interface-detail-timeinterval').combobox('getValue');
    				var step = getStep(ti, data.data.timeLine);
    				var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
    				var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');

    				genNetflowChart(curTab.find('.interface-ipgroup-hc'), 660, 135, data.data.timeLine, data.data.bos, data.data.unit, data.data.yAxisName, step, ti, st, et);
    			}
    		});
	     }
	});
		
	changeIndicatorDetail(datagridDiv, type, 'ipg', 1, false);

})(jQuery);