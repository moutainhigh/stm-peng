$(function() {
	function tinytoolRollback(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolRollback.prototype = {
		constructor : tinytoolRollback,
		cfg : undefined,
		arpDiv : undefined,
		_defaults : {},
		open : function(){
			var that = this;
			this.arpDiv = $("<div/>");
			this.arpDiv.dialog({
				title : '回滚',
				width : '1000px',
				height : '600px',
				modal: false,
				href : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool_rollback.html'),
				onLoad : function(){
					that.loadHtmlSuccess();
				}
			});
		},
		loadHtmlSuccess : function(){
			var that = this;
			var instanceId=this.cfg.instanceId;
			var arpDatagridDiv=this.arpDiv.find("#rollback_data");
			var grid = oc.ui.datagrid({
				selector:arpDatagridDiv,
				url : oc.resource.getUrl('portal/resource/tinyTools/rollback.htm?instanceId='+instanceId),
				fit : true,
				columns:[[
				          {field:'segmentName',title:'段名',width:'10%',align:'center',
				        	  formatter : function(value, row, rowIndex) {
								
									return "<span title="+value+">"+value+"</span>";
								}},
				          {field:'segmentId',title:'段ID',width:'10%',align:'center'},
				          {field:'owner',title:'所有者',width:'20%',align:'center',ellipsis : true},
				          {field:'tablespaceName',title:'所属表空间名',width:'10%',align:'center'},
				          {field:'status',title:'状态',width:'10%',align:'center'},
				          {field:'extents',title:'extent数量',width:'10%',align:'center'},
				          {field:'xacts',title:'当前活动事务数',width:'12%',align:'center'},
				          {field:'curext',title:'当前区',width:'10%',align:'center'},
				          {field:'curblk',title:'当前块',width:'8%',align:'center'}
				]]
			});
		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolRollback = {
		open : function(cfg) {
			var ttr = new tinytoolRollback(cfg);
			ttr.open();
		}
	}
});