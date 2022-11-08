(function(){

	//设备详细页面的参数
//	var timeInterval = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();
	
	var startTime = $('.device-app-dlg').data('startTime');
	var endTime = $('.device-app-dlg').data('endTime');
	var timeInterval = $('.device-app-dlg').data('timeInterval');
	var type = $('.device-app-dlg').data('type');
	var displayCount = 5;
	
	var appid = $('.netflow-main').data('appid');
	var app_ip = $('.device-app-dlg').data('app_id');
	if(app_ip != null && ''!=app_ip&&undefined!=app_ip){
		appid = app_ip;
	}
	
	var tarDiv = $('.application-proto-datagrid');
	datagridObj_netflow_proto = oc.ui.datagrid({
		selector:tarDiv,
		width:500,
		queryParams :{
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			app_id : appid
		},
		pagination: false,
		sortOrder : 'desc',
		url:oc.resource.getUrl('netflow/application/getprotocolbyapp.htm'),
		columns:[[
	          
	         {field:'proto_name',title:'协议名称',sortable:false},
	       
	         {field:'in_flows',title:'流入流量',sortable:true,order: 'desc', formatter:function(value,rowData,rowIndex){
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
	    	 var series = parseForJRPie(rows, sort, 'proto');
	    	
    	    $('.application-proto-hc').highcharts({
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
	
	changeIndicatorDetail(tarDiv, type, 'proto', 2, false);

})();
function showApplicationProtos(){
	var rows = $(".Application-proto-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="application-proto-allproto"></div>').dialog({    
		 	title: "协议",
		    width:1100,    
		    height: 570,    
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/application-netflow/application_proto.html"),    
		    modal: true
		});
	}
}