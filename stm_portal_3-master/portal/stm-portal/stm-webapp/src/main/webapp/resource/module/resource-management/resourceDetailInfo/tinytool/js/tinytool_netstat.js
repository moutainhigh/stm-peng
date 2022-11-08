$(function() {
	function tinytoolNetstat(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolNetstat.prototype = {
		constructor : tinytoolNetstat,
		cfg : undefined,
		netstatDiv : undefined,
		_defaults : {},
		open : function(){
			var that = this;
			this.netstatDiv = $("<div/>");
			var netstatDialog=this.netstatDiv.dialog({
				title : 'Netstat',
				width : '800px',
				height : '600px',
				modal: true,
				href : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool_netstat.html'),
				onLoad : function(){
					  that.loadHtmlSuccess();
				}
			});
			return netstatDialog;
		},
		loadHtmlSuccess : function(){
			var that = this;
			var instanceId=this.cfg.instanceId;
			var netstatDivObj=this.netstatDiv.find("#netstat_data");
			
			oc.ui.datagrid({
				selector:netstatDivObj,
				fit : true,
				pagination:false,
				columns:[[
				          {field:'protocol',title:'协议',width:'25%',align:'center'},
				          {field:'foreignAddress',title:'外部地址',width:'25%',align:'center'},
				          {field:'localAddress',title:'本地地址',width:'25%',align:'center'},
				          {field:'state',title:'状态',width:'25%',align:'center'}  
				      ]]
			});

			  oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/tinyTools/netstat.htm?instanceId='+instanceId),
				  timeout:null,
				  success:function(data){
					  netstatDivObj.datagrid('loadData',data); 
				  }
				  
			  });

		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolNetstat = {
		open : function(cfg) {
			var ttnetstat = new tinytoolNetstat(cfg);
			ttnetstat.open();
		}
	}
});