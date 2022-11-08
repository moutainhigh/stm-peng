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
	var ipgProtoDatagrid = tab.find('.ipgroup-proto-datagrid-selector');
		datagridObj_netflow_ipg_proto=oc.ui.datagrid({
			selector:ipgProtoDatagrid,
			url:oc.resource.getUrl('netflow/ipgroup/proto/ipGroupProtoPageSelect.htm'),
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
	         {field:'name',title:'协议名称',sortable:false,width:'20%',align:'center'},
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
	     onLoadSuccess: function(data) {
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/ipgroup/proto/getIpGroupProtoChartData.htm'),
    			data:data.paramBo,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var dst = [];
    				if(data.data.bos) {
    					dst = dataTransfer(data.data.bos);
    				}
    				genIpgroupProtoChart(dst, tab);
    			}
    		});
	     }
	});
		
	changeIndicatorDetail(ipgProtoDatagrid, type, 'proto', 1, false);

})(jQuery);

function dataTransfer(src) {
	var dst = [];
	if(src) {
		for(var i=0; i<src.length; i++) {
			
			var dataList = src[i].data;
			var ele = [];

			var data = 0;
			var name = src[i].name;
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

function genIpgroupProtoChart(flowData, tab) {
	tab.find('.ipgroup-proto-hc').empty();
	tab.find('.ipgroup-proto-hc').highcharts({
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
function showIpgroupProtos(){
	var rows = $(".ipgroup-proto-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="ipgroup-proto-allproto"></div>').dialog({    
		 	title: "协议",
		    width: 1200,
		    height: 605, 
		    top: '5%',
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/ipgroup-netflow/ipgroup_proto.html"),    
		    modal: true
		});
	}
}