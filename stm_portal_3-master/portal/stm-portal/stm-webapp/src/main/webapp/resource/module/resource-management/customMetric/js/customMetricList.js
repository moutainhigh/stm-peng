(function($) {
	function CustomMetricList(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	CustomMetricList.prototype={
			constructor : CustomMetricList,
			selector:undefined,
			cfg:undefined,
			_mainDiv:undefined,
			open : function() {
				
				var dlg = this._mainDiv = $('<div/>'), that = this, instanceId=that.cfg.id;
				

				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/resource-management/customMetric/customMetricList.html'),
					title : '自定义指标',
					height : 500,
					width:800,
					resizable : false,
					cache : false,
					onLoad : function() {
					var mainDiv = $("#oc_module_resource_custom_metric_list").attr("id",oc.util.generateId());
						
					oc.ui.datagrid({
							selector : mainDiv.find("#oc_module_resource_custom_metric_list_datagrid"),
							url : oc.resource.getUrl('portal/resource/customMetric/getCustomMetricsByInstanceId.htm?instanceId='+instanceId),
							width : 'auto',
							height : 'auto',
							pagination:false,
						    checkOnSelect: false, 
						    selectOnCheck: false,
							columns : [ [
							         {field:'monitor',title:'是否监控',sortable:true,width:30,formatter: function(value,row,index){
							        	 return value ? "<a style='color:green'>是</a>" :"<a style='color:red'>否</a>" ;
							         }},
							         {field:'alert',title:'是否告警',sortable:true,width:30,formatter: function(value,row,index){
							        	 return value ? "<a style='color:green'>是</a>" :"<a style='color:red'>否</a>" ;
							         }},
						        	 {field:'metricType',title:'指标类型',sortable:true,width:40,formatter: function(value,row,index){
							        	 return value=="PerformanceMetric" ? "性能指标" : "信息指标";
							         }},
							         {field:'name',title:'指标名称',sortable:true,width:40},
						        	 {field:'discoverWay',title:'取值方式',sortable:true,formatter: function(value,row,index){
						        		 if(value=="SnmpPlugin"){
						        			 return "SNMP";
						        		 }else if(value=="TelnetPlugin"){
						        			 return "Telnet";
						        		 }else if(value=="SshPlugin"){
						        			 return "SSH";
						        		 }else{
						        			 return value;
						        		 }
						        	 }},
						        	 {field:'dateTime',title:'修改时间',sortable:true, formatter: function(value,row,index){
						        		 return value;
						        	 }}
						        	 ] ],
						             onClickCell:function(rowIndex, field, value,e){
						             },
						             onLoadSuccess : function() {

							}
						});
						
		

					},
					onClose:function(){
						if(that.cfg.callback){
							that.cfg.callback();
						}
						dlg.dialog('destroy');
					},
					buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							dlg.dialog('close');
						}
					},{
						text:"取消",
						iconCls:"fa fa-times-circle",
						handler:function(){
							dlg.dialog('close');
						}
					}]
				});
			}

	}

	oc.ns('oc.module.resource.custom.metric.list');

	oc.module.resource.custom.metric.list = {
		open : function(cfg) {
			new CustomMetricList(cfg).open();
		}
	};
})(jQuery);