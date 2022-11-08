$(function() {
	function tinytoolOrcTable(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolOrcTable.prototype = {
		constructor : tinytoolOrcTable,
		cfg : undefined,
		arpDiv : undefined,
		_defaults : {},
		open : function(cfg){
			var resourceMain = $('#oracle_resource_list_id').attr('id', oc.util.generateId());
			var that = this;
			that.loadHtmlSuccess(0);
			resourceMain.find('#oracle_resource_tabs').attr('id',resourceMain.domId).addClass("window-tabsbg").tabs({
				fit : false,
				width : "100%",
				onSelect : function(title,index) {
					that.loadHtmlSuccess(index);
				}
			});
		},
		loadHtmlSuccess : function(index){
			var that = this;
			var instanceId=this.cfg.instanceId;
			var resourceId=this.cfg.resourceId;
			$("#oracle_resource_table").html("");
			oc.util.ajax({
				url: oc.resource.getUrl('portal/resource/resourceApply/getDatagridData0ByResourceId.htm'),
				data:{instanceId:instanceId,resourceId:resourceId,type:index},
				timeout:null,
				success:function(data){
					if(data.code==200){
						var datas=data.data.metricData;
						if(datas!=null){
							for(var i=1;i<datas.length; i++){
								var d=datas[i].toLowerCase();
								if(d.length>160){
									d=d.substr(0, 180);
								}
								$("#oracle_resource_table").append('<tr class="datagrid-row" title="'+datas[i]+'"><td ><span style="font-weight:bold">TOP'+(i)+':</span>'+d+'</td></tr>');
							}
						}else{
							$("#oracle_resource_table").append('<tr class="datagrid-row" title=""><td ><span style="font-weight:bold"></span><div class="table-dataRemind">抱歉，没有可展示的数据！</div></td></tr>');
						}
						
					}
				
				}
			});
		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolOrcTable = {
		open : function(cfg) {
			var ttnetstat = new tinytoolOrcTable(cfg);
			ttnetstat.open();
		}
	}
});