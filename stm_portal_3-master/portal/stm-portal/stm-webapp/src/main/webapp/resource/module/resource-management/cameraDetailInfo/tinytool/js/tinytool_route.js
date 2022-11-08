$(function() {
	function tinytoolRouteTable(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolRouteTable.prototype = {
		constructor : tinytoolRouteTable,
		cfg : undefined,
		routeDiv : undefined,
		_defaults : {},
		open : function(){
			var that = this;
			this.routeDiv = $("<div/>");
			this.routeDiv.dialog({
				title : '路由表',
				width : '800px',
				height : '600px',
				modal: true,
				href : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool_route.html'),
				onLoad : function(){
					that.loadHtmlSuccess();
				}
			});
		},
		loadHtmlSuccess : function(){
			var that = this;
			var instanceId=this.cfg.instanceId;

			var routeDatagridDiv=this.routeDiv.find("#route_table_data")
			
			oc.ui.datagrid({
				selector:routeDatagridDiv,
				fit : true,
				pagination:false,
				octoolbar:{
					right:[
					    {
					    	iconCls: 'ico ico_export_excel',
							text:"导出",
							onClick: function(){
								var iframeHtml = $("<iframe style='display:none' src='"+oc.resource.getUrl('portal/resource/tinyTools/routetableExportExcel.htm?instanceId='+ instanceId +"'/>"));
								iframeHtml.appendTo("body");
							}
					    }
					]
				},
				columns:[[
				    {field:'distIPAddress',title:'目的地',width:'20%',align:'center'},
				    {field:'subnetMask',title:'掩码',width:'20%',align:'center'},
				    {field:'routeProtocol',title:'路由协议',width:'20%',align:'center'},
				    {field:'nextHop',title:'下一跳',width:'20%',align:'center'} ,
				    {field:'routeType',title:'类型',width:'20%',align:'center'}  
				]]
			});
			
			  oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/tinyTools/routetable.htm?instanceId='+instanceId),
				  timeout:null,
				  success:function(data){
					  routeDatagridDiv.datagrid('loadData',data); 
				  }
			  });

		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolRouteTable = {
		open : function(cfg) {
			var ttnetstat = new tinytoolRouteTable(cfg);
			ttnetstat.open();
		}
	}
});