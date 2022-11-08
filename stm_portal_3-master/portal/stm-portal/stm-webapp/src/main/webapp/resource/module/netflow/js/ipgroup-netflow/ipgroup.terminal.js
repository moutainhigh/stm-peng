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
	
	var ipGroupId = $('.netflow-main').data('ipGroupId');
	var ipgroup_id=$('.terminal-ipg').data('ipgroup_id');
	if(ipgroup_id!=null&&""!=ipgroup_id&&undefined!=ipgroup_id){
		ipGroupId=ipgroup_id;
	}
	/**
	 * 初始化datagrid
	 */
	var ipgTermDatagridDiv = tab.find('.ipgroup-terminal-datagrid-selector');
		datagridObj_netflow_ipg_terminal=oc.ui.datagrid({
			selector: ipgTermDatagridDiv,
			url:oc.resource.getUrl('netflow/ipgroup/terminal/ipGroupTerminalPageSelect.htm'),
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
			columns:[[
	         {field:'terminalIp',title:'终端名称',sortable:false,width:'20%',align:'center'},
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
	    	 $('<div class="device-terminal"></div>').dialog({    
	    		 	title: '流量分析 - 终端 - ' + rowData.terminalIp,   
	    		    width: 1200,    
	    		    height: 580, 
	    		    top: '5%',
	    		    closed: false,    
	    		    cache: false,
	    		    position:'center',
	    		    draggable:false,
	    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToTerminalDialog.html"),    
	    		    modal: true   
	    		});
	    	 $('.device-terminal').data('terminal_ip',rowData.terminal_Name);
	    	 
	    	 var timeInterval = tab.find('.ipgroup-detail-timeinterval').combobox('getValue');
	    	 var startTime = tab.find('.ipgroup-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = tab.find('.ipgroup-detail-custom-endtime').datetimebox('getValue');
	    	 var type = tab.find('.ipgroup-detail-indecator').combobox('getValue');
	    	 
	    	 $('.device-terminal').data('timeInterval', timeInterval);
	    	 $('.device-terminal').data('startTime', startTime);
	    	 $('.device-terminal').data('endTime', endTime);
	    	 $('.device-terminal').data('type', type);

	     },
	     onLoadSuccess: function(data) {
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/ipgroup/terminal/getIpGroupTerminalChartData.htm'),
    			data: data.paramBo,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var dst = [];
    				if(data.data.bos) {
    					dst = dataTransfer(data.data.bos);
    				}
    				genIpgroupTerminalChart(dst, tab);
    			}
    		});
	     }
	});
		
	changeIndicatorDetail(ipgTermDatagridDiv, type, 'terminal', 1, false);

})(jQuery);

function dataTransfer(src) {
	var dst = [];
	if(src) {
		for(var i=0; i<src.length; i++) {
			
			var dataList = src[i].data;
			var name = src[i].name;

			var ele = [];
			var data = 0;

			if(dataList && dataList.length > 0) {
				for(var j=0; j<dataList.length; j++) {
					data += dataList[j];
				}
			}
			ele.push(name);
			ele.push(data);
			
			dst.push(ele);
		}
	}
	return dst;
}

function genIpgroupTerminalChart(flowData, tab) {
	tab.find('.ipgroup-terminal-hc').empty();
	tab.find('.ipgroup-terminal-hc').highcharts({
        chart: {
        	backgroundColor: '#111718',
        	type: 'pie'
        },
        title: {
        	text: ''
        },
        tooltip: {
        	pointFormat: '{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                innerSize: 30,
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
        	type: 'pie',
        	data: flowData
        }]
    });
}
function showIpgroupTerminals(){
	var rows = $(".ipgroup-terminal-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="ipgroup-terminal-allterminal"></div>').dialog({    
		 	title: "终端",
		    width: 1200, 
		    height: 605, 
		    top: '5%',
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/ipgroup-netflow/ipgroup_terminal.html"),    
		    modal: true
		});
	}
}