(function($) {
	function BusinessInfo() {
		this.selector = $("#oc-module-home-workbench-business").attr("id",oc.util.generateId());
		oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
//		this.init();
	}
	BusinessInfo.prototype = {
		selector : undefined,
		businessIds:undefined,
		constructor : BusinessInfo,
		_dlg : $("<div></div>"),
		businessSetGrid:undefined,
		businessInfoGrid:undefined,
		loadData:undefined,
		loadSetting:function(){
			var parentDiv = this.selector.parent("div");
			var workbenchExt= parentDiv.attr("data-workbench-ext");
			if(workbenchExt!=undefined && workbenchExt!="") {
				this.businessIds = workbenchExt;
			}
		},
		saveSetting:function(businessIds){
			this.businessIds = businessIds;
			var workbenchId = this.selector.parent("div").attr("data-workbench-id");
			var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
			this.selector.parent("div").attr("data-workbench-ext",this.businessIds);
			oc.home.workbench.setExt(workbenchId,workbenchSort,this.businessIds);
		},
		init : function() {
			var that = this;
			that.initdatagrid();
			oc.util.ajax({
				url:oc.resource.getUrl('home/business/getBusiness.htm'),
				data:{ids : that.businessIds},
				startProgress:false,
				stopProgress:false,
				success:function(d){
					that.loadData = d;
					that.loadDatagrid();
				}
			});
		},
		loadDatagrid:function(){
			this.selector.find("#oc_system_home_business").datagrid('loadData',this.loadData);
		},
		initdatagrid : function() {
			var that = this;
			that.loadSetting();
			that.businessInfoGrid = oc.ui.datagrid({
				selector : that.selector.find("#oc_system_home_business"),
//				url : oc.resource.getUrl('home/business/getBusiness.htm'),
				pagination : false,
//				queryParams : {ids : that.businessIds},
				columns : [ [
						{
							field : 'id',
							title : '序号',
							width : '13%'
						},
						 {
							field : 'businessName',
							title : '业务名称',
							width : '20%'
						}, {
							field : 'alarmCount',
							title : '告警',
							width : '13%'
						}, {
							field : 'hostCount',
							title : '主机',
							width : '13%'
						},{
							field : 'networkEquipmentCount',
							title : '网络设备',
							width : '13%'
						},{
							field : 'databaseCount',
							title : '数据库',
							width : '13%'
						},{
							field : 'middlewareCount',
							title : '中间件',
							width : '13%'
						}] ]

			});
		},
		openBusinessSetting : function() {
			var that = this;
			that._dlg.dialog({
				href:oc.resource.getUrl("resource/module/home/workbench/businessSetting.html"),
				title : '业务选择',
				width : 480,
				height : 300,
				buttons : [{
					text : "确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var businessIds = that.businessSetGrid.getSelectIds();
						if(businessIds==undefined || businessIds.length==0){
							alert("请至少选择一个业务",'danger');
						}else if(businessIds.length > 6){
							alert("最多只能选择六个业务",'danger');
						}else{
							that.saveSetting(businessIds.join());
							that.selector.find("#oc_system_home_business").datagrid('load',{
								ids:businessIds.join()
							});
							that._dlg.dialog("close");
						}
					}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						that._dlg.dialog("close");
					}
				}],
				onLoad:function(){
					that.businessSetGrid = oc.ui.datagrid({
						selector : "#businessSettingGrid",
						url : oc.resource.getUrl('home/business/getBusiness.htm'),
						pagination : false,
						columns : [[{
							field:'ck',
							title:'-',
							checkbox : true,
							width:'20'
						},
				 		{
							field : 'id',
							title : '序号',
							width : '160'
						}, {
							field : 'businessName',
							title : '业务名称',
							width : '300'
						}]]
					});
				}
			});
		},
		reLoad:function(){
			this.init();
		},
		render:function(){
			if(this.loadData!=undefined){
				this.loadDatagrid();
			}else{
				this.init();
			}
		}
	};
	
	var businessCharts =new BusinessInfo();

	oc.home.workbench.business = function() {
		return businessCharts;
	};
//	console.log(typeof(businessCharts));
	businessCharts.selector.find("#businessrefresh").click(function(){
		businessCharts.reLoad();
	});
	businessCharts.selector.find("#businesssetting").click(function(){
		businessCharts.openBusinessSetting();
	});
	businessCharts.selector.bind(oc.events.resize,function(){
		businessCharts.render();
	});
})(jQuery);