(function($) {
	function CustomMetricResource(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	CustomMetricResource.prototype={
			constructor : CustomMetricResource,
			selector:undefined,
			cfg:undefined,
			customMetricResourceForm:undefined,
			pickGrid:undefined,
			_mainDiv:undefined,
			pickTreeCfg:{},
			resourceIds:undefined,
			_loadDomainId:undefined,
			open : function() {
				var dlg = this._mainDiv = $('<div/>'), that = this, metricId = that.cfg.id, pluginId=that.cfg.pluginId;

				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/resource-management/customMetric/customMetricResource.html'),
					title : '关联资源',
					height : 420,
					width:780,//754,
					resizable : false,
					cache : false,
					onLoad : function() {
						
						var leftColumns = [[
						                    {field:'id',title:'资源ID',checkbox:true,sortable:true},
						                    {field:'resourceName',title:'资源名称',width:80,ellipsis:true},
						                    {field:'resourceIP',title:'IP地址',width:100,ellipsis:true},
						                    {field:'categoryName',title:'资源类型',width:80,ellipsis:true},
						                    {field:'domainName',title:'域',width:60,ellipsis:true}	,   
						                    {field:'categoryId',title:'计划ID',hidden:true},
						                    {field:'domainId',title:'计划ID',hidden:true}
						                  ]];
						var rightColumns = [[
						                    {field:'id',title:'资源ID',checkbox:true,sortable:true},
						                    {field:'resourceName',title:'资源名称',width:80,ellipsis:true},
						                    {field:'resourceIP',title:'IP地址',width:100,ellipsis:true},
						                    {field:'categoryName',title:'资源类型',width:80,ellipsis:true},
						                    {field:'domainName',title:'域',width:60,ellipsis:true}	 ,
						                    {field:'categoryId',title:'计划ID',hidden:true},
						                    {field:'domainId',title:'计划ID',hidden:true}
						                   ]];
						var cfg = {
								selector:"#metric_resource_pickgrid",
								leftColumns:leftColumns,
								rightColumns:rightColumns,
								isInteractive:true
						}
						
						pickGrid = oc.ui.pickgrid(cfg);
						
						var getLeftResources = function(){
							var domainId=dlg.find("#domainId").combobox("getValue");
							var categoryId=dlg.find("#categoryId").combobox("getValue");
							oc.util.ajax({
								  url: oc.resource.getUrl('portal/resource/customMetric/getUnBindResources.htm'),
								  data:{
									  metricId:metricId,
									  domainId:domainId,
									  categoryId:categoryId,
									  rightDatas:pickGrid.getRightRows(),
									  pluginId:pluginId
									  },
								  success:function(data){
									  if(!data.data){
										  pickGrid.loadData("left",[]); 
									  }else{
										  pickGrid.loadData("left",data.data); 
									  }
								  }
							});

						}
						
						var getRightResources = function(){
							oc.util.ajax({
								  url: oc.resource.getUrl('portal/resource/customMetric/getBindResources.htm'),
								  data:{metricId:metricId},
								  success:function(data){
									  if(!data.data){
										  pickGrid.loadData("right",[]); 
									  }else{
										  pickGrid.loadData("right",data.data);
									  }
								  }
							});

						}
						
						var domains = oc.index.getDomains();
						var firstDomain={
								id: 0,
								name: "请选择"	
						}
						domains.unshift(firstDomain);

						//判断pluginID确定类别
						var categoryData= [];
						if(pluginId=='JdbcPlugin'){
							categoryData = [
								      {id:'Database',name:'数据库',selected:true},
								      {id:'UniversalModel',name:'通用模型'}
						        ];
						}else if(pluginId=='UrlPlugin'){
							categoryData = [
								      {id:'UniversalModel',name:'通用模型',selected:true}
						        ];
						
						}else{
							categoryData = [
							      {id:'Host',name:'主机',selected:true},
							      {id:'NetworkDevice',name:'网络设备'}
					        ];
						}
						
						customMetricResourceForm = oc.ui.form({
							selector:dlg.find(".metric_resource_form"),
							combobox:[{
								selector:'[name=domainId]',
								placeholder:false,
								selected:false,
								data:domains,
								onSelect:function(r){
									getLeftResources();
								}
							},
							{
								selector:'[name=categoryId]',
								fit:false,
								placeholder:null,
								valueField:'id',    
								textField:'name',
								data:categoryData,
								onSelect:function(r){
									getLeftResources();
								}
							}]
						});

						//init
						getLeftResources();
						getRightResources();

						
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
							that.saveForm();
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
			},
			_initForm:function(){
				
			},
			search:function(){
				
			},
			saveForm:function(){

				oc.util.ajax({
					  url: oc.resource.getUrl('portal/resource/customMetric/bindResources.htm'),
					  data:{
						  metricId:this.cfg.id,
						  pluginId:this.cfg.pluginId,
						  rightDatas:pickGrid.getRightRows()
						  },
					  success:function(data){
//						  pickGrid.loadData("left",data.data); 
						  alert("资源绑定成功！");
					  }
				});
			}
	}

	oc.ns('oc.module.custom.metric.resource');

	oc.module.custom.metric.resource = {
		open : function(cfg) {
			new CustomMetricResource(cfg).open();
		}
	};
})(jQuery);