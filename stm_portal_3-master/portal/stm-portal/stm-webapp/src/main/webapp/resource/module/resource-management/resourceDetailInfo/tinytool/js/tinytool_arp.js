$(function() {
	function tinytoolArpTable(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolArpTable.prototype = {
		constructor : tinytoolArpTable,
		cfg : undefined,
		arpDiv : undefined,
		_defaults : {},
		open : function(){
			var that = this;
			this.arpDiv = $("<div/>");
			this.arpDiv.dialog({
				title : 'ARP 表',
				width : '800px',
				height : '600px',
				modal: false,
				href : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool_arp.html'),
				onLoad : function(){
					that.loadHtmlSuccess();
				}
			});
		},
		loadHtmlSuccess : function(){
			var that = this;
			var instanceId=this.cfg.instanceId;
			var arpDatagridDiv=this.arpDiv.find("#arp_table_data");
			oc.ui.datagrid({
				selector:arpDatagridDiv,
				fit : true,
				pagination:false,
				octoolbar:{
					right:[
					    {
					    	iconCls: 'ico ico_export_excel',
							text:"导出",
							onClick: function(){
								var iframeHtml = $("<iframe style='display:none' src='"+oc.resource.getUrl('portal/resource/tinyTools/arptableExportExcel.htm?instanceId='+ instanceId +"'/>"));
								iframeHtml.appendTo("body");
							}
					    }
					]
				},
				columns:[[
				    {field:'iPAddress',title:'IP地址',width:'25%',align:'center'},
				    {field:'macAddress',title:'MAC地址',width:'25%',align:'center'},
				    {field:'ifIndex',title:'接口索引',width:'25%',align:'center'},
				    {field:'arpType',title:'接口类型',width:'25%',align:'center'}  
				]]
			});
			oc.util.ajax({
				url: oc.resource.getUrl('portal/resource/tinyTools/arptable.htm?instanceId='+instanceId),
				timeout:null,
				success:function(data){
					arpDatagridDiv.datagrid('loadData',data); 
				}
			});
		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolArpTable = {
		open : function(cfg) {
			var ttnetstat = new tinytoolArpTable(cfg);
			ttnetstat.open();
		}
	}
});