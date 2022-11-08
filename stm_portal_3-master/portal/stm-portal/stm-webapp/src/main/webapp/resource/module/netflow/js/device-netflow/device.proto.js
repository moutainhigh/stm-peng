(function($){

	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	
	var datagridDiv=curTab.find('.device-proto-datagrid');
	var deviceIp = $('.netflow-main').data('deviceIp');
	
	datagridObj_netflow_proto = oc.ui.datagrid({
		selector:datagridDiv,
		url:oc.resource.getUrl('netflow/device/proto/deviceProtoNetflowPageSelect.htm'),
		queryParams: {
			timePerid: timeInterval,
			deviceIp: deviceIp,
			rowCount: displayCount,
			needPagination: false,
			querySize: displayCount,
			startTime: stime,
			endTime: etime
		},
		pagination: false,
		sortOrder: 'desc',
		sortName: 'flowIn',
		pageSize: displayCount,
		pageList: [5, 10, 20, 30, 40, 50],
		columns:[[
         {field:'name',title:'协议名称',sortable:false,width:'20%',align:'center'},
         {field:'flowIn',title:'流入流量',sortable:true, align:'center',order:'desc',formatter:function(value,row,index) {
        	 return flowFormatter(value, null);
         }},
         {field:'flowOut',title:'流出流量',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
        	 return flowFormatter(value, null);
         }},
         {field:'flowTotal',title:'总流量',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
        	 return flowFormatter(value, null);
         }},
//         {field:'packetIn',title:'流入包数',sortable:true,align:'center',formatter:function(value, row, index) {
//        	 if(value == null) {
//        		 return 0;
//        	 } else {
//        		 return value;
//        	 }
//         }},
//         {field:'packetOut',title:'流出包数',sortable:true,align:'center',formatter:function(value, row, index) {
//        	 if(value == null) {
//        		 return 0;
//        	 } else {
//        		 return value;
//        	 }
//         }},
//         {field:'packetTotal',title:'总包数',sortable:true,align:'center',formatter:function(value, row, index) {
//        	 if(value == null) {
//        		 return 0;
//        	 } else {
//        		 return value;
//        	 }
//         }},
         {field:'speedIn',title:'流入速率',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
        	 return flowFormatter(value, 'speed');
         }},
         {field:'speedOut',title:'流出速率',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
        	 return flowFormatter(value, 'speed');
         }},
         {field:'speedTotal',title:'总速率',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
        	 return flowFormatter(value, 'speed');
         }},
         {field:'flowPctge',title:'占比',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
        	 return pctgeFormatter(value);
         }}
     ]],
     onLoadSuccess: function(data) {
		oc.util.ajax({
			url:oc.resource.getUrl('netflow/device/proto/getDeviceProtoChartData.htm'),
			data: data.paramBo,
			failureMsg:'加载用户域数据失败！',
			async:false,
			success:function(data){
				var dst = [];
				if(data.data.bos) {
					dst = dataTransfer(data.data.bos);
				}
				genDeviceProtoChart(curTab, dst);
			}
		});
     }
});
	
	changeIndicatorDetail(curTab.find('.device-proto-datagrid'), type, 'proto' , 1, false);

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

function genDeviceProtoChart(curTab, flowData) {
	curTab.find('.device-proto-hc').empty();
	curTab.find('.device-proto-hc').highcharts({
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