(function($) {
	function CustomMetricDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	CustomMetricDetail.prototype={
			constructor : CustomMetricDetail,
			selector:undefined,
			cfg:undefined,
			customMetricForm:undefined,
			_mainDiv:undefined,
			_thresholdObject:undefined,
			open : function() {
				
				var dlg = this._mainDiv = $('<div/>'), that = this, type = that.cfg.type , currentMetric=that.cfg.metric;
				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/resource-management/customMetric/customMetricDetail.html'),
					title : '自定义指标',
					height : 610,
					width: 785,
					resizable : false,
					cache : false,
					onLoad : function() {
						that.selector = $("#oc_module_custom_metric_detail").attr("id",oc.util.generateId());
						
						that.customMetricForm = oc.ui.form({
							selector:that.selector.find(".custom_metric_form")
						});
						
						//采集频度
						oc.ui.combobox({
							selector:dlg.find(".frequent"),
							fit:false,
							width:80,
							placeholder:null,
							panelHeight : 'auto',
							valueField:'id',    
							textField:'name',
							data:[
							      {id:'min1',name:'1分钟'},
							      {id:'min5',name:'5分钟'},
							      {id:'min10',name:'10分钟'},
							      {id:'min30',name:'30分钟'},
							      {id:'hour1',name:'1小时'},
							      {id:'day1',name:'1天'}
					        ]
						});
						
						//数据处理：
						oc.ui.combobox({
							selector:dlg.find(".dataProcessWay"),
							fit:false,
							width:160,
							placeholder:null,
							panelHeight : 'auto',
							valueField:'id',    
							textField:'name',
							data:[
							      {id:'NONE',name:'无',selected:true},
							      {id:'MAX',name:'最大值'},
							      {id:'MIN',name:'最小值'},
							      {id:'AVG',name:'平均值'}
					        ]
						});
						

						//加载指标数据
						if(type=="edit"){
							
							//渲染阈值设置界面
							oc.resource.loadScript('resource/module/resource-management/js/strategy_threshold_setting.js',function(){
								that._thresholdObject = oc.module.resourcemanagement.strategythresholdsetting.updateWithCumstom({selector:$('.custom_metric_threshold_setting'),thresholdData:currentMetric.thresholdsMap});
							});
							
							//固定指标类型，和发现类型
							dlg.find("input[type='radio']").attr("disabled","disabled");
							dlg.find('input[type=hidden][name=id]').attr("value",currentMetric.id);
							dlg.find("#name").attr("value",currentMetric.name);
							if(currentMetric.metricType=="PerformanceMetric"){
								dlg.find('input:radio[value="PerformanceMetric"]').attr("checked","checked");
							}else if(currentMetric.metricType=="InformationMetric"){
								dlg.find('input:radio[value="InformationMetric"]').attr("checked","checked");
							}else if(currentMetric.metricType=="AvailabilityMetric"){
								dlg.find('input:radio[value="AvailabilityMetric"]').attr("checked","checked");
							}
							
							dlg.find('.frequent').combobox('setValue', currentMetric.frequent);
							
							if(currentMetric.unit){
								dlg.find("#unit").attr("value",currentMetric.unit);
							}
							
							if(currentMetric.discoverWay=="SnmpPlugin"){
								dlg.find('input:radio[value="SnmpPlugin"]').attr("checked","checked");
								dlg.find('input:radio[value="SshPlugin"]').removeAttr("checked");
								dlg.find('input:radio[value="TelnetPlugin"]').removeAttr("checked");
								dlg.find('input:radio[value="JdbcPlugin"]').removeAttr("checked");	
								
								dlg.find("#oid").attr("value",currentMetric.oid);
								
								dlg.find('.dataProcessWay').combobox('setValue', currentMetric.dataProcessWay);
								
							}else if(currentMetric.discoverWay=="SshPlugin"){
								dlg.find('input:radio[value="SshPlugin"]').attr("checked","checked");
								dlg.find('input:radio[value="SnmpPlugin"]').removeAttr("checked");	
								dlg.find('input:radio[value="TelnetPlugin"]').removeAttr("checked");
								dlg.find('input:radio[value="JdbcPlugin"]').removeAttr("checked");	
								dlg.find('#command').val(currentMetric.command);
							}else if(currentMetric.discoverWay=="TelnetPlugin"){
								dlg.find('input:radio[value="TelnetPlugin"]').attr("checked","checked");
								dlg.find('input:radio[value="SshPlugin"]').removeAttr("checked");	
								dlg.find('input:radio[value="SnmpPlugin"]').removeAttr("checked");
								dlg.find('input:radio[value="JdbcPlugin"]').removeAttr("checked");	
								dlg.find('#command').val(currentMetric.command);
							}else if(currentMetric.discoverWay=="JdbcPlugin"){
								dlg.find('input:radio[value="JdbcPlugin"]').attr("checked","checked");
								dlg.find('input:radio[value="SshPlugin"]').removeAttr("checked");	
								dlg.find('input:radio[value="SnmpPlugin"]').removeAttr("checked");
								dlg.find('input:radio[value="TelnetPlugin"]').removeAttr("checked");	
								dlg.find('#command').val(currentMetric.command);
							}else{
								dlg.find('#command').val(currentMetric.command);
							}
							
						}else{
							//渲染阈值设置界面
							oc.resource.loadScript('resource/module/resource-management/js/strategy_threshold_setting.js',function(){
								that._thresholdObject = oc.module.resourcemanagement.strategythresholdsetting.addWithCumstom({selector:$('.custom_metric_threshold_setting')});
							});
						}

						var metricType=dlg.find('input:radio[name="metricType"]:checked').val();
						if(metricType=="PerformanceMetric"){
							that.customMetricForm.enableValidation(['greenValue','yellowValue','redValue']);
							dlg.find("#discoverWayForAvailability").hide();
							dlg.find(".custom_metric_threshold").show();
							dlg.find("#discoverWayForCommon").show();
							dlg.find("#commandOrUrl").text('采集命令：');
							that.showOrHideCollectionWay(dlg.find('input:radio[name="discoverWay"]:checked').val());
						}else if(metricType=="InformationMetric"){
							that.customMetricForm.disableValidation(['greenValue','yellowValue','redValue']);
							dlg.find(".custom_metric_threshold").hide();
							if(dlg.find('input:radio[name="discoverWay"]:checked').val() == "JdbcPlugin"){
								dlg.find("#custom_metric_unit_id").show();
							}
							dlg.find("#discoverWayForAvailability").hide();
							dlg.find("#discoverWayForCommon").show();
							dlg.find("#commandOrUrl").text('采集命令：');
							that.showOrHideCollectionWay(dlg.find('input:radio[name="discoverWay"]:checked').val());
						}else {
							that.customMetricForm.disableValidation(['greenValue','yellowValue','redValue']);
							dlg.find(".custom_metric_threshold").hide();
							dlg.find("#discoverWayForCommon").hide();
							dlg.find("#discoverWayForAvailability").show();
							
							that.customMetricForm.disableValidation(['oid']);
							that.customMetricForm.enableValidation(['command']);
							dlg.find(".SnmpPlugin").hide();
							dlg.find(".SshPlugin").show();
							dlg.find("#commandOrUrl").text('Url：');
							
						}
						dlg.find(".metricType").bind("click",function(){
							var type=$(this).val();
							if(type=="PerformanceMetric"){
								if(that.cfg.type == 'add'){
									oc.util.ajax({
										  successMsg:null,
										  url: oc.resource.getUrl('portal/resource/customMetric/getDefaultAlarmContent.htm'),
										  data:{type:'PerformanceMetric'},
										  success:function(data){
											  
											  if(data.code == 200){
												  
												  that._thresholdObject.updateAlarmContent(data.data);
												  
											  }else{
												  alert('获取信息失败!');
											  }
											  
										  }
										  
									});
								}
								dlg.find("#discoverWayForAvailability").hide();
								dlg.find(".custom_metric_threshold").show();
								dlg.find("#discoverWayForCommon").show();
								dlg.find("#commandOrUrl").text('采集命令：');
								that.showOrHideCollectionWay(dlg.find('input:radio[name="discoverWay"]:checked').val());
								
								if(dlg.find("#command").text().indexOf('http') == 0){
									dlg.find("#command").text('');
								}
								
							}else if(type=="InformationMetric"){
								if(that.cfg.type == 'add'){
									oc.util.ajax({
										  successMsg:null,
										  url: oc.resource.getUrl('portal/resource/customMetric/getDefaultAlarmContent.htm'),
										  data:{type:'InformationMetric'},
										  success:function(data){
											  
											  if(data.code == 200){
												  
												  that._thresholdObject.updateAlarmContent(data.data);
												  
											  }else{
												  alert('获取信息失败!');
											  }
											  
										  }
										  
									});
								}
								dlg.find(".custom_metric_threshold").hide();
								if(dlg.find('input:radio[name="discoverWay"]:checked').val() == "JdbcPlugin"){
									dlg.find("#custom_metric_unit_id").show();
								}
								dlg.find("#discoverWayForAvailability").hide();
								dlg.find("#discoverWayForCommon").show();
								dlg.find("#commandOrUrl").text('采集命令：');
								that.showOrHideCollectionWay(dlg.find('input:radio[name="discoverWay"]:checked').val());
								
								if(dlg.find("#command").text().indexOf('http') == 0){
									dlg.find("#command").text('');
								}
								
							}else {
								if(that.cfg.type == 'add'){
									oc.util.ajax({
										  successMsg:null,
										  url: oc.resource.getUrl('portal/resource/customMetric/getDefaultAlarmContent.htm'),
										  data:{type:'AvailabilityMetric'},
										  success:function(data){
											  
											  if(data.code == 200){
												  
												  that._thresholdObject.updateAlarmContent(data.data);
												  
											  }else{
												  alert('获取信息失败!');
											  }
											  
										  }
										  
									});
								}
								dlg.find(".custom_metric_threshold").hide();
								dlg.find("#discoverWayForCommon").hide();
								dlg.find("#discoverWayForAvailability").show();
								
								that.customMetricForm.disableValidation(['oid']);
								that.customMetricForm.enableValidation(['command']);
								dlg.find(".SnmpPlugin").hide();
								dlg.find(".SshPlugin").show();
								dlg.find("#commandOrUrl").text('Url：');
								dlg.find("#command").text('http://');
								
							}
						});

						if(metricType!="AvailabilityMetric"){
							
							var discoverWay=dlg.find('input:radio[name="discoverWay"]:checked').val();
							if(discoverWay=="SnmpPlugin"){
								that.customMetricForm.disableValidation(['command']);
								that.customMetricForm.enableValidation(['oid']);
								dlg.find(".SnmpPlugin").show();
								dlg.find(".SshPlugin").hide();
							}else{
								that.customMetricForm.disableValidation(['oid']);
								that.customMetricForm.enableValidation(['command']);
								dlg.find(".SnmpPlugin").hide();
								dlg.find(".SshPlugin").show();
							}
							
							if(discoverWay=="JdbcPlugin" && dlg.find('input:radio[name="metricType"]:checked').val() == "InformationMetric"){
								dlg.find("#custom_metric_unit_id").show();
							}else if(discoverWay!="JdbcPlugin" && dlg.find('input:radio[name="metricType"]:checked').val() == "InformationMetric"){
								dlg.find("#custom_metric_unit_id").hide();
							}
							
						}
						
						
						that.customMetricForm.disableValidation(['command']);
						dlg.find(".discoverWay").bind("click",function(){
							var way=$(this).val();
							if(way=="SnmpPlugin"){
								that.customMetricForm.disableValidation(['command']);
								that.customMetricForm.enableValidation(['oid']);
								dlg.find(".SnmpPlugin").show();
								dlg.find(".SshPlugin").hide();
							}else{
								that.customMetricForm.disableValidation(['oid']);
								that.customMetricForm.enableValidation(['command']);
								dlg.find(".SnmpPlugin").hide();
								dlg.find(".SshPlugin").show();
							}
							if(way=="JdbcPlugin" && dlg.find('input:radio[name="metricType"]:checked').val() == "InformationMetric"){
								dlg.find("#custom_metric_unit_id").show();
							}else if(way!="JdbcPlugin" && dlg.find('input:radio[name="metricType"]:checked').val() == "InformationMetric"){
								dlg.find("#custom_metric_unit_id").hide();
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
							if(that.saveForm()){
								dlg.dialog('destroy');
							}
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
			saveForm:function(){
				var that = this, type = that.cfg.type;
				var data = that.customMetricForm.val();
				
				if(type!="add"){
					data.alert=that.cfg.metric.alert;
					data.monitor=that.cfg.metric.monitor;
					data.metricType=that.cfg.metric.metricType;
					data.discoverWay=that.cfg.metric.discoverWay;
				}
				
				var thresholdData = that._thresholdObject.getThresholdValues(that.cfg.placehodler);
				if(!thresholdData){
					return;
				}
				data.thresholdsMap = thresholdData;
				
				if(data.metricType == "AvailabilityMetric" && type=="add"){
					data.discoverWay = data.discoverWayForVailability;
				}
				
				if(data.discoverWay=="JdbcPlugin"){
					var command = data.command.toUpperCase();
					var patrn=/^(INSERT|UPDATE|DELETE|CREATE|DROP|ALTER){1}.*$/;
					//校验采集命令内容
					if(patrn.exec(command)){
						//不合法
						alert('jdbc采集命令不合法！');
						return;
					}
					
				}
				
				if(data.discoverWay=="UrlPlugin"){
//					var patrn=/^(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/g;
					var patrn=/^(http:\/\/|https:\/\/){1}.+/g;
					//校验采集命令内容
					if(!patrn.exec(data.command)){
						//不合法
						alert('url采集命令不合法！');
						return;
					}
					
				}
				
				if(that.customMetricForm.validate()){
					oc.util.ajax({
						url:oc.resource.getUrl(type=="add"?"portal/resource/customMetric/addCustomMetric.htm":"portal/resource/customMetric/updateCustomMetric.htm"),
						data:{customMetricString:JSON.stringify(data)},
						success:function(data){
							
							if(data.data=="DuplicateName"){
								alert("指标名称已存在，请重命名！");
							}else{
								if(data.data){
									alert("指标保存成功！");
								}else{
									alert("指标保存失败！");
								}
								if(that.cfg.callback){
									that.cfg.callback();
								}
							}
							
						}
					});
					return true;
				}else{
					alert('请检查必填项');
					return false;
				}
			},
			showOrHideCollectionWay:function(pluginWay){
				var that = this;
				var dlg = this._mainDiv;
				if(pluginWay=="SnmpPlugin"){
					that.customMetricForm.disableValidation(['command']);
					that.customMetricForm.enableValidation(['oid']);
					dlg.find(".SnmpPlugin").show();
					dlg.find(".SshPlugin").hide();
				}else{
					that.customMetricForm.disableValidation(['oid']);
					that.customMetricForm.enableValidation(['command']);
					dlg.find(".SnmpPlugin").hide();
					dlg.find(".SshPlugin").show();
				}
			}

	}

	oc.ns('oc.module.resource.custom.metric');

	oc.module.resource.custom.metric = {
		open : function(cfg) {
			new CustomMetricDetail(cfg).open();
		}
	};
})(jQuery);