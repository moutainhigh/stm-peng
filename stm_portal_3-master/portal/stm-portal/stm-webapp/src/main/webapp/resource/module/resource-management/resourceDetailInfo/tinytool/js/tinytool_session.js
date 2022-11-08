$(function() {
	function tinytoolSession(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolSession.prototype = {
		constructor : tinytoolSession,
		cfg : undefined,
		arpDiv : undefined,
		_defaults : {},
		open : function(){
			var that = this;
			this.arpDiv = $("<div/>");
			this.arpDiv.dialog({
				title : 'Session',
				width : '1000px',
				height : '600px',
				modal: false,
				href : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool_session.html'),
				onLoad : function(){
					that.loadHtmlSuccess();
				}
			});
		},
		loadHtmlSuccess : function(){
			var that = this;
			var instanceId=this.cfg.instanceId;
			var arpDatagridDiv=this.arpDiv.find("#session_data");
			oc.ui.datagrid({
				selector:arpDatagridDiv,
				fit : true,
				pagination:false,
				columns:[[
				          {field:'sid',title:'会话ID',width:'5%',align:'center'},
				          {field:'seq',title:'会话序列',width:'10%',align:'center'},
				          {field:'event',title:'非空闲等待事件',width:'27%',align:'center',ellipsis : true},
				          {field:'waitTime',title:'等待时间',width:'10%',align:'center'},
				          {field:'p1',title:'p1',width:'10%',align:'center'},
				          {field:'p2',title:'p2',width:'5%',align:'center'},
				          {field:'p3',title:'p3',width:'5%',align:'center'},
				          {field:'p1text',title:'p1text',width:'10%',align:'center'},
				          {field:'p2text',title:'p2text',width:'8%',align:'center'},
				          {field:'p3text',title:'p3text',width:'10%',align:'center',ellipsis : true}  
				      ]]
			});

			  oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/tinyTools/session.htm?instanceId='+instanceId),
				  timeout:null,
				  success:function(data){
					  arpDatagridDiv.datagrid('loadData',data); 
				  }
			  });
			


		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolSession = {
		open : function(cfg) {
			var tts = new tinytoolSession(cfg);
			tts.open();
		}
	}
});