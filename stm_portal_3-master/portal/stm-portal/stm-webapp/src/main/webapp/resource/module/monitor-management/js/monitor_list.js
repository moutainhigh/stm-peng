$(function() {
	var quanGrid=null;
	var quanGridUn=null;
	function resourceGrid(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		if (cfg && cfg.id) {
			this.gourpId = cfg.id;
		}
		this.init();
	}
	


	resourceGrid.prototype = {
		constructor : resourceGrid,
		curDataGrid : undefined,
		monitorDataGridLoadFlag : undefined,
		unMonitorDataGridLoadFlag : undefined,
		dataGrid1 : undefined,
		dataGrid2 : undefined,
		isCustomResGroupInput : undefined,
		categoryInput : undefined,
		categorysInput : undefined,
		resourceInput : undefined,
		// 是否 标准服务的查询，如果是则隐藏cpu，内存使用率
		ifStandardService : false,

		isCustomResGroupInputUnmonitor : undefined,
		categoryInputUnmonitor : undefined,
		categorysInputUnmonitor : undefined,
		resourceInputUnmonitor : undefined,

		categoryCountList : undefined,
		curResourceIDs : undefined,
		queryForm : undefined,
		queryFormUnmonitor : undefined,
		tab : undefined,
		freshBtn : undefined,
		gourpId : undefined,
		groupDelCfg : undefined,
		domId : undefined,
		showDelCfg : undefined,
		_defaults : {},		
		open : function() {
			this.enableGlobalProgress();
			this.createMonitorDatagrid();
			/*this.enableGlobalProgress();
			this.createMonitorDatagrid();
			this.createDiscoverBtn();
			this.showOrHideRemoveCustomGroupBtn();	*/
		},
		init : function() {
			var that = this;
			// 表格查询表单对象
			this.queryForm = oc.ui.form({
				selector : $('#show_management_instance_form')
			});
			
			
			this.queryForm.find(":input[name=iPorName]").keyup(function(e){
				/*e.keyCode==13&&(datagrid.octoolbar.jq.find(".icon-search").trigger('click'));*/
				if (e.keyCode == 13) {
					that.loadAll();
				}
			});
			
			
			
			$('#show_management_instance_form').on("change","#onlineStatusAll",function(){
				$('#show_management_instance_form').find(":checkbox[name='status']").prop("checked",$(this).prop("checked"));
				
			});
			
			$('#show_management_instance_form').on("change","#diagnoseResultAll",function(){
				$('#show_management_instance_form').find(":checkbox[name='results']").prop("checked",$(this).prop("checked"));
				
			});
			
			$('#show_management_instance_form2').on("change","#allDignoseIndex",function(){
				$('#show_management_instance_form2').find(":checkbox[name='dignoseIndex']").prop("checked",$(this).prop("checked"));
				
			});
			
			//在线状态
			$('#show_management_instance_form').on("change",".onlineClass",function(){
				if(!$(this).prop("checked")){
					$('#onlineStatusAll').prop("checked",$(this).prop("checked"));
				}
				else{
					var check_flag=true;
					var checkbox_length=$('#show_management_instance_form').find(":checkbox[class='onlineClass']").length;
					for(var i=0;i<checkbox_length;i++){
						if(!$('#show_management_instance_form').find(":checkbox[class='onlineClass']").eq(i).prop("checked")){
							check_flag=false;
							break;
						}
					}
					if(check_flag){
						$('#onlineStatusAll').prop("checked",$(this).prop("checked"));
					}
				}
				
			});
			
			//诊断结果
			$('#show_management_instance_form').on("change",".diagnoseResultClass",function(){
				if(!$(this).prop("checked")){
					$('#diagnoseResultAll').prop("checked",$(this).prop("checked"));
				}
				else{
					var check_flag=true;
					var checkbox_length=$('#show_management_instance_form').find(":checkbox[class='diagnoseResultClass']").length;
					for(var i=0;i<checkbox_length;i++){
						if(!$('#show_management_instance_form').find(":checkbox[class='diagnoseResultClass']").eq(i).prop("checked")){
							check_flag=false;
							break;
						}
					}
					if(check_flag){
						$('#diagnoseResultAll').prop("checked",$(this).prop("checked"));
					}
				}
				
			});
			
			//诊断指标
			$('#show_management_instance_form2').on("change",".dignoseIndexClass",function(){
				if(!$(this).prop("checked")){
					$('#allDignoseIndex').prop("checked",$(this).prop("checked"));
				}
				else{
					var check_flag=true;
					var checkbox_length=$('#show_management_instance_form2').find(":checkbox[class='dignoseIndexClass']").length;
					for(var i=0;i<checkbox_length;i++){
						if(!$('#show_management_instance_form2').find(":checkbox[class='dignoseIndexClass']").eq(i).prop("checked")){
							check_flag=false;
							break;
						}
					}
					if(check_flag){
						$('#allDignoseIndex').prop("checked",$(this).prop("checked"));
					}
				}
				
			});

			this.isCustomResGroupInput = $('#show_resource_instance_form_isCustomResGroup');
			this.categoryInput = $('#show_resource_instance_form_categoryId');
			this.categorysInput = $('#show_resource_instance_form_categoryIds');
			this.resourceInput = $('#show_resource_instance_form_resourceId');
			$('#show_resource_instance_form_instanceStatus').val('all');

			this.isCustomResGroupInputUnmonitor = $('#show_resource_instance_form_isCustomResGroup_unMonitor');
			this.categoryInputUnmonitor = $('#show_resource_instance_form_categoryId_unMonitor');
			this.categorysInputUnmonitor = $('#show_resource_instance_form_categoryIds_unMonitor');
			this.resourceInputUnmonitor = $('#show_resource_instance_form_resourceId_unMonitor');
			$('#show_resource_instance_form_instanceStatus_monitor_unMonitor')
					.val('all');
			// tabs
			this.domId = oc.util.generateId();
			this.tab = $('#resourceList').attr('id', that.domId).tabs({
				onSelect : function(title, index) {
					if (index == 0) {
						that.curDataGrid = that.dataGrid1;
						if (!!that.freshBtn) {
							that.freshBtn.find("span").show();
						}
					} else {
						that.curDataGrid = that.dataGrid2;
						if (!!that.freshBtn) {
							that.freshBtn.find("span").hide();
						}
					}
				}
			});
			$(".tabs-selected").removeClass("tabs-selected");
			$(".tabs-inner").removeClass("tabs-inner");
			
			//this.addFreshBtn();
		},
		addFreshBtn : function() {
			var that = this;
			// tabs头上加一个刷新按钮
			var tabsWrap = $("#" + this.domId)
					.find(".tabs-header > .tabs-wrap");
			var tabs = tabsWrap.find(".tabs").css("float", "left");
			this.freshBtn = $("<span/>").css({
				height : '33px',
				float : "right",
				"margin-right" : "10px"
			}).append(
					$("<span>").addClass('ico ico-refrash').css("margin-top",
							"9px").attr("title", "刷新"));
			tabsWrap.append(this.freshBtn);
			this.freshBtn.find("span").on(
					'click',
					function() {
						that.loadAll();
					});
		},
		
		
		
		
		createMonitorDatagrid : function() {
			quanGrid=this;
			var that = this;
			var hiddenProfile = true;
			var ipAddressWidth = '20%';
			var user = oc.index.getUser();
			if (user.domainUser || user.systemUser) {
				hiddenProfile = false;
				ipAddressWidth = '15%';
			}
			var dataGridCfg1 = {
				selector : $('.monitor-oc-datagrid'),
				pageSize : $.cookie('pageSize_monitor_list') == null ? 15 : $.cookie('pageSize_monitor_list'),
				fitColumns : false,
				queryForm : this.queryForm,
				// url : oc.resource.getUrl('portal/resource/cameraqualitydetails/getHaveMonitorNew.htm'),
                url : oc.resource.getUrl('portal/resource/cameraqualitydetails/getVQDList.htm'),
				hideReset : true,
				hideSearch : true,
				columns : [ [
						{
							field : 'sourceName',
							title : '摄像头名称',
							sortable : true,
							ellipsis : true,
							align : 'left',
							width : '10%',
							formatter : function(value, row, rowIndex) {
								/*if(row.alarmTips=="SERIOUS")
									return "<span data-index='"+ rowIndex+"' id='detailInfoPage' title='"+value+"'><img src='"+oc.resource.getUrl('resource/themes/blue/images/icons/red.png')+"'/>&nbsp;&nbsp;"+value+"</span>";
								else
									return "<span data-index='"+ rowIndex+"' id='detailInfoPage' title='"+value+"'><img src='"+oc.resource.getUrl('resource/themes/blue/images/icons/green.png')+"'/>&nbsp;&nbsp;"+value+"</span>";*/
								// 加入手形样式
								var statusLabel = $("<label/>").addClass("light-ico_resource "+ row.instanceStatus
												+ " oc-pointer-operate");
								statusLabel.attr('rowIndex', rowIndex)
										.addClass('quickSelectDetailInfo');
								if (value != null) {
									statusLabel.attr('title', value).html(
											value.htmlspecialchars());
								}
								return statusLabel;
							}
						},
						{
							field : 'ipAddress',
							title : 'IP',
							sortable : false,
							width : '10%',
							align : 'left',
							ellipsis : false,
							formatter:function(value){
			                	if(value)
			                		return value;
			                	else
			                		return '-';
			                } 
						
						},
						{
							field : 'monitorType',
							title : '类型',
							sortable : false,
							align : 'center',
							width : '8%',
							ellipsis : false
						},
						{
							field : 'availability',
							title : '在线状态',
							sortable : true,
							align : 'center',
							width : '8%',
							ellipsis : false,
							formatter:function(value){
			                	if(value=='NORMAL')
			                		return '是';
                                else if(value=='SERIOUS')
                                    return '是';
			                	else if(value=='CRITICAL' || value == 'WARN')
			                		return '<label style="color:red">否</label>';
			                	else
			                		return '-';
			                }
						},
						{
							field : 'brightness',
							title : '亮度',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
							formatter:function(value,row){
							    if(row.availability == "CRITICAL"){
							        return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'legibility',
							title : '清晰度',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'screenFreezed',
							title : '冻结',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},	
						{
							field : 'colourCast',
							title : '偏色',
							sortable : false,
							width : '8%',
							ellipsis : false,
							align : 'center',
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'lostSignal',
							title : '信号缺失',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'sightChange',
							title : '场景变换',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'ptzSpeed',
							title : 'PTZ速度',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						
						{
							field : 'keepOut',
							title : '遮挡',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						
						{
							field : 'streakDisturb',
							title : '条纹干扰',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'ptzdegree',
							title : 'PTZ角度',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
						{
							field : 'snowflakeDisturb',
							title : '雪花干扰',
							sortable : false,
							width : '8%',
							align : 'center',
							ellipsis : false,
                            formatter:function(value,row){
                                if(row.availability == "CRITICAL"){
                                    return "-";
                                }
			                	if(value=='NORMAL')
			                		return '正常';
			                	else if(value=='SERIOUS' || value == 'WARN')
			                		return '<label style="color:orange">异常</label>';
			                	else
			                		return '-';
			                } 
						},
					
						{
							field : 'dignoseTime',
							title : '诊断时间',
							sortable : false,
							width : '15%',
							align : 'center'
						}
						/*{
							field : 'id',
							title : '查看详情',
							width : '8%',
							align : 'left',
							formatter : function(value, row, rowIndex) {
								return '<a data-index="'
										+ rowIndex
										+ '" class="domainUser" id="'+value+'"  title="查看详情"></a>';
							}
						}*/
						
						 ] ],
				loadFilter : function(data) {
					var myData = data.data;
					return myData;
				},
				onLoadSuccess : function(data) {			
					that.monitorDataGridLoadFlag = true;
					
					/** ********修改资源所属域start******** */
					/** *******修改资源所属域end********* */
					// 取消进度条 
					//创建grid
					var span_text="<div style='font-weight: bold'><label>摄像头总数:"+data.totalRecord+"&nbsp;&nbsp;</label>   <label style='color:red'>离线数:"+data.offlineNumber+
					"        &nbsp;&nbsp;<label style='color:orange'>故障数:"+data.abnormalNumber+"</label></div>";
					$(".tabs-title").html(span_text);
					that.disableGlobalProgress();
					/*that.tab.find(".domainUser").on('click',function(e) {
						var rowIndex = $(this).attr('data-index');
						var row = that.dataGrid1.selector.datagrid('getRows')[rowIndex];
						var instanceId=row.id;
						var instanceStatus = row.instanceStatus;
						var hasRight = row.hasRight;
						oc.resource.loadScript('resource/module/resource-management/cameraDetailInfo/js/cameraDetailInfo.js',
										function() {
											oc.module.resmanagement.cameradeatilinfo
													.open({
														instanceId : instanceId,
														callback : that,
														callbackType : '1',
														type : '0',
														instanceStatus : instanceStatus
													});
										});
						e.stopPropagation();
					});*/
					
					
					that.tab.find(".quickSelectDetailInfo").on('click',function(e) {
						var rowIndex = $(this).attr('rowIndex');
						var row = that.dataGrid1.selector.datagrid('getRows')[rowIndex];
						var instanceId=row.id;
						var instanceStatus = row.instanceStatus;
						var hasRight = row.hasRight;
						oc.resource.loadScript('resource/module/resource-management/cameraDetailInfo/js/cameraDetailInfo.js',
										function() {
											oc.module.resmanagement.cameradeatilinfo
													.open({
														instanceId : instanceId,
														callback : that,
														callbackType : '1',
														type : '0',
														instanceStatus : instanceStatus
													});
										});
						e.stopPropagation();
					});
					
					
					
				},
				octoolbar : {
					left : [ $("#show_management_instance_form2") ],
					right : [
							{
								id : "queryBtn",
								iconCls : 'icon-search',
								text : "查询",
								onClick : function() {
									var length=$('#show_management_instance_form').find("input:checkbox[name='status']:checked").length;
									var status_str="";
									$('#show_management_instance_form').find("input:checkbox[name='status']:checked").each(function(i){
										if($(this).val()!='0'){
											status_str+=$(this).val();
											if(i!=length-1){
												status_str+=",";
											}
										}
										
									});
									
									var length4Result=$('#show_management_instance_form').find("input:checkbox[name='results']:checked").length;
									var result_str="";
									$('#show_management_instance_form').find("input:checkbox[name='results']:checked").each(function(i){
										if($(this).val()!='0'){
											result_str+=$(this).val();
											if(i!=length4Result-1){
												result_str+=",";
											}
										}
										
									});
									
									var length4Metriec=$('#show_management_instance_form2').find("input:checkbox[name='dignoseIndex']:checked").length;
									var metriec_str="";
									$('#show_management_instance_form2').find("input:checkbox[name='dignoseIndex']:checked").each(function(i){
										if($(this).val()!='0'){
											metriec_str+=$(this).val();
											if(i!=length4Metriec-1){
												metriec_str+=",";
											}
										}
										
									});
									
									$('#show_resource_instance_form2_onlineStatus').val(status_str);
									
									$('#show_resource_instance_form2_diagnoseResult').val(result_str);
									
									$('#show_resource_instance_form2_dignoseMetrics').val(metriec_str);
									
									that.loadAll();
								}
							},
							'&nbsp;',
							{
								id : "resetBtn",
								iconCls : 'icon-reset',
								text : "重置",
								onClick : function() {
									$('#show_management_instance_form2').form("reset");
									$('#show_management_instance_form').form("reset");
								}
							},
							'&nbsp;',
							{
								id : "cancleMonitor",
								iconCls : 'l-btn-icon fa fa-sign-out',
								text : "导出",
								onClick : function() {
									oc.util.downloadFromUrl(oc.resource.getUrl('portal/resource/cameraqualitydetails/downloadCameras.htm'));
									
									
								}
							}
							
								
							
							]
				},
				
			};

			// 绑定事件
			this.dataGrid1 = oc.ui.datagrid(dataGridCfg1);
			this.curDataGrid = this.dataGrid1;
			

			// cookie记录pagesize
			var paginationObject = $('.monitor-oc-datagrid').datagrid('getPager');
			
			if (paginationObject) {
				paginationObject.pagination({
					onChangePageSize : function(pageSize) {
						$.cookie('pageSize_monitor_list', pageSize);
					}
				});
			}
		},
		reLoadWestAndList : function() {
			$(oc.index.activeContent[0]).load(oc.index.activeHref);
		},
		loadAll : function() {
			this.enableGlobalProgress();
			this.dataGrid1.load();
		},
		reLoadAll : function() {
			this.enableGlobalProgress();
			this.dataGrid1.reLoad();
		},
		enableGlobalProgress : function() {
			oc.ui.progress();
		},
		disableGlobalProgress : function() {
			var that = this;
			$.messager.progress('close');
			this.updateTableWidth(this.dataGrid1);
		},
		updateTableWidth : function(grid) {
			var dataGrid = grid.selector;
			var viewObj = dataGrid.parent().find('div[class=datagrid-view2]');
			var resourceObj = viewObj.find('table[class=datagrid-htable]');
			var targetObj = viewObj.find('table[class=datagrid-btable]');
			targetObj.css('width', resourceObj.css('width'));
		},
		isCustomResGroupCheck : function() {
			if (this.isCustomResGroupInput.val() == 0) {
				return true;
			} else {
				return false;
			}
		},
		
		
		
		deleteResurceButtonTurn : function(type) {
			var that = this;
			// 将删除资源按钮获取，以便控制显示
			if (!that.deleteResurceMonitorButton) {
				// deleteResurce 来自datagrid octoolbar配置
				that.deleteResurceMonitorButton = $('#monitorResourceDiv')
						.find('#deleteResurce');
			}
			if (!that.deleteResurceUnmonitorButton) {
				that.deleteResurceUnmonitorButton = $('#unMonitorResourceDiv')
						.find('#deleteResurceUnmonitor');
			}

			if (that.deleteResurceMonitorButton) {
				var str = that.deleteResurceMonitorButton.css('display');
				if (type == 'show') {
					if ('none' == str) {
						that.deleteResurceMonitorButton.css('display',
								'inline-block');
					}
				} else if (type == 'none') {
					if ('inline-block' == str) {
						that.deleteResurceMonitorButton.css('display', 'none');
					}
				}
			}
			if (that.deleteResurceUnmonitorButton) {
				var str = that.deleteResurceUnmonitorButton.css('display');
				if (type == 'show') {
					if ('none' == str) {
						that.deleteResurceUnmonitorButton.css('display',
								'inline-block');
					}
				} else if (type == 'none') {
					if ('inline-block' == str) {
						that.deleteResurceUnmonitorButton
								.css('display', 'none');
					}
				}
			}
		},
	};

	// 对外提供入口方法
	oc.ns('oc.monitor.manager.list');
	var resourceList;
	oc.monitor.manager.list = {
		removeGroupDataGrid : function(modifyResourceNumber) {
			// 后台查询自定义资源组没有resourceId直接返回空
			resourceList.isCustomResGroupInput.val('0');
			resourceList.isCustomResGroupInputUnmonitor.val('0');

			resourceList.resourceInput.val('');
			resourceList.resourceInputUnmonitor.val('');
			resourceList.loadAll();
		},
		reloadGridData : function(queryData, titleInfo, modifyResourceNumber) {
			resourceList.queryForm.reset();
			$('#show_resource_instance_form_resourceId').val(queryData.resourceId);
			resourceList.loadAll();
		},
		open : function(queryData, titleInfo, modifyResourceNumber) {
			//$('#show_resource_instance_form_resourceId').val(queryData.resourceId);
			resourceList = new resourceGrid(titleInfo);
			resourceList.open();
			// 自定义资源组不显示,删除资源按钮
			//resourceList.deleteResurceButtonCheckMethod();
		},
		reLoadList : function() {
			resourceList.loadAll();
		}
	};
	
});
