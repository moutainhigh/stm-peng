$(function() {
	var quanGrid=null;
	var quanGridUn=null;
	function resourceGrid(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		if (cfg && cfg.id) {
			this.gourpId = cfg.id;
		}
		this.init();
		this.createGroupDelCfg();
	}
	
	// 清除维修模式
	function showDialogMaintainClear(resourceIds,flag){		
		oc.util.ajax({
			url : oc.resource.getUrl('resource/resourceMonitor/clearMaintain.htm'),
			data : {
				instanceIds :resourceIds.join(),
				executeFlag:flag
			},
			success : function(d) {
				if(d.code==200){
					if(flag==2){// 已监控资源
						quanGrid.dataGrid1.selector.datagrid("reload");
					}
					else{//未监控资源
						quanGridUn.loadAll();
						//quanGridUn.dataGrid2.selector.datagrid("reload");
					}
					alert("保存成功");
				}
				else{
					alert("保存失败");
				}
			}
			});					
	
	}
	// 未监控设备批量设置维修模式或者单个编辑，只能延迟结束时间
	function showDialogMaintainOver(resourceIds,timeStart,timeEnd) {		
		var addWindow = $('<div />');
		var scanContent;
		if(resourceIds.length==1 && timeStart!=""){
			    scanContent =  "<div id='mainTainId'>" 
	    		+'<form style="padding:26px 50px;">'
			    +  '<div style="margin-bottom:10px;">'
			    + '<label>维修开始时间：</label>'
			    +'<input id="startTime"/>'
			    +'</div>'
			    +  '<div>'
			    + '<label>维修结束时间：</label>'
			    +'<input id="endTime" />'
			    +'</div>'
			    +'</form>'
			    +"</div>";  			
		}
		else{
			 scanContent =  "<div id='mainTainId'>" 
		    		+'<form style="padding:50px 50px;">'		   
				    +  '<div>'
				    + '<label>维修结束时间：</label>'
				    +'<input id="endTime" />'
				    +'</div>'
				    +'</form>'
				    +"</div>";  
		}		
		dialogTitle = '维修模式设置';
		// 构建dialog
		addWindow.dialog({
			title : dialogTitle,
			width : 400,
			height : 200,
			content : scanContent,
			modal : true,
			buttons : [
					{
						text : '保存',
						iconCls : "fa fa-check-circle",
						handler : function() {
							var endTime=$('#endTime').datetimebox('getValue');
							var dtEnd = new Date(endTime);
							var nowDate=new Date();								
							if(dtEnd<nowDate){
								alert("维修结束时间不能小于当前时间");
								return;
							}
							oc.util.ajax({
								url : oc.resource.getUrl('resource/resourceMonitor/saveMaintain.htm'),
								data : {
									instanceIds :resourceIds.join(),
									startTime:'',
									endTime:endTime
								},
								success : function(d) {
									if(d.code==200){
										quanGridUn.dataGrid2.selector.datagrid("reload");
										alert("保存成功");
									}
									else{
										alert("保存失败");
									}									
								}
								});		
							addWindow.dialog('close');
						}

					}, {
						text : '取消',
						iconCls:"fa fa-times-circle",
						handler : function() {
							addWindow.dialog('close');
						}
					} ]
		});	
		if(resourceIds.length==1 && timeStart!=""){
			$('#startTime').datetimebox({
				width : '188px',
				showSeconds : false,
				required: true,			
				disabled:true,
				value : timeStart
			});			
		}
		
		$('#endTime').datetimebox({
			width : '188px',
			showSeconds : false,
			required: true,			
			value : timeEnd
		});		
	}
	// 已监控设备批量设置维修模式或者单个编辑
	function showDialogMaintain(resourceIds,timeStart,timeEnd) {		
		var addWindow = $('<div />');
	    var scanContent =  "<div id='mainTainId'>" 
    		+'<form style="padding:26px 50px;">'
		    +  '<div style="margin-bottom:10px;">'
		    + '<label>维修开始时间：</label>'
		    +'<input id="startTime" />'
		    +'</div>'
		    +  '<div>'
		    + '<label>维修结束时间：</label>'
		    +'<input id="endTime" />'
		    +'</div>'
		    +'</form>'
		    +"</div>";  			
		
		dialogTitle = '维修模式设置';
		// 构建dialog
		addWindow.dialog({
			title : dialogTitle,
			width : 400,
			height : 200,
			content : scanContent,
			modal : true,
			buttons : [
					{
						text : '保存',
						iconCls : "fa fa-check-circle",
						handler : function() {					
							var startTime=$('#startTime').datetimebox('getValue');
							var endTime=$('#endTime').datetimebox('getValue');
							if(startTime>endTime){
								alert("维修开始时间不能大于结束时间");
								return;
							}	
							var dtStart = new Date(startTime);
							var nowDate=new Date();								
							if(dtStart<nowDate){
								alert("维修开始时间不能小于当前时间");
								return;
							}
							oc.util.ajax({
								url : oc.resource.getUrl('resource/resourceMonitor/saveMaintain.htm'),
								data : {
									instanceIds :resourceIds.join(),
									startTime:startTime,
									endTime:endTime
								},
								success : function(d) {
									if(d.code==200){
										quanGrid.dataGrid1.selector.datagrid("reload");
										alert("保存成功");
									}
									else{
										alert("保存失败");
									}
								}
								});		
							addWindow.dialog('close');
						}

					}, {
						text : '取消',
						iconCls : "fa fa-times-circle",
						handler : function() {
							addWindow.dialog('close');
						}
					} ]
		});		
		$('#startTime').datetimebox({
			width : '188px',
			showSeconds : false,
			required: true,
			value : timeStart
		});
		$('#endTime').datetimebox({
			width : '188px',
			showSeconds : false,
			required: true,
			value :timeEnd
		});

	}
	// 清除责任人
	function showDialogPersonClear(resourceIds){		
		oc.util.ajax({
			url : oc.resource.getUrl('resource/resourceMonitor/clearLiablePerson.htm'),
			data : {
				instanceIds :resourceIds.join()				
			},
			success : function(d) {
				if(d.code==200){
					quanGrid.dataGrid1.selector.datagrid("reload");
					alert("保存成功");
				}
				else{
					alert("保存失败");
				}
			}
			});					
	}

	// 编辑责任人
	function showDialogPerson(resourceIds) {		
		var addWindow = $('<div />');
		//var scanContent = $('<div id="resibilityGrid"/>');
		scanContent = $("<div><div><div class='oc-toolbar datagrid-toolbar insert-btn'><input id='queryManagerUserInput' style='margin-right:40px;width:200px;' type='text' placeholder='输入用户名'/>" +
				"<button id='searchManagerUserByName'><span class='l-btn-icon fa fa-search poas_re' style='top:5px;'></span>查询</button>" +
				"</div><div style='height:235px'><div id='resibilityGrid'/></div></div>");
		dialogTitle = '选择责任人';
		// 构建dialog
		addWindow.dialog({
			title : dialogTitle,
			width:410,
			height:350,
			content : scanContent,
			modal : true,
			buttons : [
					{
						text : '保存',
						iconCls : "fa fa-check-circle",
						handler : function() {
							var selectRow = $('#resibilityGrid').datagrid('getChecked');
						//	var data = $("input[type='radio']:Checked");
							if (selectRow && selectRow.length > 0) {								
								oc.util.ajax({
									url : oc.resource.getUrl('resource/resourceMonitor/saveLiablePerson.htm'),
									data : {
										instanceIds :resourceIds.join(),
										userId:selectRow[0].id
									},
									success : function(d) {
										if(d.code==200){
											quanGrid.dataGrid1.selector.datagrid("reload");
											alert("保存成功");
										}
										else{
											alert("保存失败");
										}
									}
									});										
								addWindow.dialog('close');
							} else {							
									alert('请选择责任人');								
							}
						}

					}, {
						text : '取消',
						iconCls:"fa fa-times-circle",
						handler : function() {
							addWindow.dialog('close');
						}
					} ]
		});	
		
		var resbonGrid = oc.ui.datagrid({
					selector : $('#resibilityGrid'),
					url : oc.resource.getUrl('portal/business/service/getManagerUsers.htm'),
					queryParams:{searchContent:''},
					fitColumns : true,
					singleSelect : true,
					pagination : false,
					columns : [ [
							{
								field : 'id',
								title : '<input type="radio" style="display:none;"/>',
								width : 20,
								formatter : function(value, rowData, rowIndex) {
									return '<input type="radio" name="selectRadio" id="selectRadio'
											+ rowIndex
											+ '" value="'
											+ rowData.oid
											+ '" rid="'
											+ rowData.id
											+ '" rname="'
											+ rowData.name + '" />';
								}
							}, {
								field : 'account',
								title : '用户名',
								sortable : true,
								width : 80
							}, {
								field : 'name',
								title : '姓名',
								sortable : true,
								width : 80
							} ] ],
					onLoadSuccess : function(data) {
						
					},
					onSelect : function(index, row) {
						$('#selectRadio' + index).attr("checked", true);
					}

				});
		$('#searchManagerUserByName').on('click',function(){				
			resbonGrid.selector.datagrid('options').queryParams = {searchContent:$('#queryManagerUserInput').val()};
			resbonGrid.reLoad();
	});
	}

	// 编辑域
	function showDialogDomin(resourceIds) {		
		var addWindow = $('<div />');
		var scanContent = $('<div id="dominGrid"/>');
		dialogTitle = '选择域';		
		// 构建dialog
		addWindow.dialog({
			title : dialogTitle,
			width : 400,
			height : 400,
			content : scanContent,
			modal : true,
			buttons : [
					{
						text : '保存',
						iconCls : "fa fa-check-circle",
						handler : function() {							
							var selectRow = $('#dominGrid').datagrid('getChecked');
						//	var data = $("input[type='radio']:Checked");
							if (selectRow && selectRow.length > 0) {															
										oc.util.ajax({
											url : oc.resource.getUrl('resource/resourceMonitor/update_resource_domain_batch.htm'),
											timeout : 5000,
											data : {
												instanceIds :resourceIds.join(),
												dominId:selectRow[0].id
											},
											success : function(d) {
												if (d.code == 200) {														
													alert("保存成功！");
													quanGrid.dataGrid1.selector.datagrid("reload");
												} else {
													alert("保存失败");
												}
											}										
										});
										addWindow.dialog('close');
							} else {								
									alert('请选择域');								
							}
						}

					}, {
						text : '取消',
						iconCls:"fa fa-times-circle",
						handler : function() {
							addWindow.dialog('close');
						}
					} ]
		});
		
		var dominsGrid = oc.ui.datagrid({
			selector : $('#dominGrid'),
			url : oc.resource.getUrl('system/domain/domainPage.htm'),
			fitColumns : true,
			singleSelect : true,
			pagination : false,
			columns : [ [
					{
						field : 'id',
						title : '<input type="radio" style="display:none;"/>',
						width : 20,
						formatter : function(value, rowData, rowIndex) {
							return '<input type="radio" name="selectRadio" id="selectRadio'
									+ rowIndex
									+ '" value="'
									+ rowData.oid
									+ '" rid="'
									+ rowData.id
									+ '" rname="'
									+ rowData.name + '" />';
						}
					}, {
						field : 'name',
						title : '域',
						sortable : true,
						width : 80
					}, {
						field : 'description',
						title : '备注',
						sortable : true,
						width : 80
					} ] ],
			onLoadSuccess : function(data) {
				var item = $('#dominGrid').datagrid('getRows'), index;
				if (item) {
					for (var i = item.length - 1; i >= 0; i--) {
						if (item[i].id == $("input[name='managerId']").val()) {
							index = $('#resibilityGrid').datagrid(
									'getRowIndex', item[i]);
							($("input[type='radio']")[index + 1]).checked = true;
							break;
						}
					}
				}
			},
			onSelect : function(index, row) {
				$('#selectRadio' + index).attr("checked", true);
			}

		});
	}
	
//	//左侧菜单折叠按钮
//	function collapse_btn(){
//		$("#collapse_btn").click(function(){
//			if($(this).hasClass('fa-angle-double-left')){
//				$('#sidebar').find('#mainMenu').find('.fa-globe').parent().parent().attr('collapse',true);
//				$('#resource_management').layout('panel','west').parent().hide();
//				$('#resource_management').layout('collapse','west');
//				var wid = $('#mainMenuContent').width();
//				$('#resource_management').layout('panel','center').parent().css('left','0px');
//				$(this).removeClass('fa-angle-double-left');
//				$(this).addClass('fa-angle-double-right');
//				setTimeout(function(){
//					quanGrid.dataGrid1.selector.datagrid();
//				},300);
////				quanGrid.dataGrid1.selector.datagrid();
//			}else{
//				$('#sidebar').find('#mainMenu').find('.fa-globe').parent().parent().removeAttr('collapse');
//				$('#resource_management').layout('expand','west');
//				$(this).removeClass('fa-angle-double-right');
//				$(this).addClass('fa-angle-double-left');
//				setTimeout(function(){
//					quanGrid.dataGrid1.selector.datagrid();
//				},300);
////				quanGrid.dataGrid1.selector.datagrid();
//			}
//		});
//	}

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
			this.createUnMonitorDatagrid();
			this.createDiscoverBtn();
			this.showOrHideRemoveCustomGroupBtn();	
		},
		init : function() {
			var that = this;
			var domains = oc.index.getDomains();
			// 表格查询表单对象
			this.queryForm = oc.ui.form({
				selector : $('#show_resource_instance_form'),
				combobox : [ {
					selector : '[name=domainId]',
					data : domains,
					width : 90,
					placeholder : '${oc.local.ui.select.placeholder}'
				} ]
			});
			this.queryFormUnmonitor = oc.ui.form({
				selector : $('#show_resource_instance_unmonitor_form'),
				combobox : [ {
					selector : '[name=domainId]',
					data : domains,
					width : 90,
					placeholder : '${oc.local.ui.select.placeholder}'
				} ]
			});

			$('#show_resource_instance_form').find(".queryBtn").linkbutton(
					'RenderLB', {
						iconCls : 'fa fa-search',
						onClick : function() {
							that.dataGrid1.load();
						}
					});
			$('#show_resource_instance_form').find(".resetBtn").linkbutton(
					'RenderLB', {
						iconCls : 'icon-reset',
						onClick : function() {
							that.removeStatusLightActive();
							that.queryForm.reset();
						}
					});
			$('#show_resource_instance_form').find('#iPorNameId').keyup(
					function(e) {
						if (e.keyCode == 13) {
							that.dataGrid1.load();
						}
					});

			$('#show_resource_instance_unmonitor_form').find(".queryBtn")
					.linkbutton('RenderLB', {
						iconCls : 'fa fa-search',
						onClick : function() {
							that.dataGrid2.load();
						}
					});
			$('#show_resource_instance_unmonitor_form').find(".resetBtn")
					.linkbutton('RenderLB', {
						iconCls : 'icon-reset',
						onClick : function() {
							that.queryFormUnmonitor.reset();
						}
					});
			$('#show_resource_instance_unmonitor_form').find('#iPorNameId')
					.keyup(function(e) {
						if (e.keyCode == 13) {
							that.dataGrid2.load();
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
			$('#show_resource_instance_form_instanceStatus_monitor_unMonitor').val('all');
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
			this.addFreshBtn();
		},
		removeStatusLightActive : function() {
			$('#show_resource_instance_form_instanceStatus').val('all');
			$('.light').find("a").removeClass("active");
		},
		addStatusLightActive : function(light) {
			$(light).parent().parent().find("a").removeClass('active');
			$(light).find("a").addClass('active');
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
					$("<span>").addClass('fa fa-refresh').css({"margin-top":
							"9px","color":"#49a1e3"}).attr("title", "刷新"));
			tabsWrap.append(this.freshBtn);
			this.freshBtn.find("span").on(
					'click',
					function() {
//						oc.module.resourcemanagement.resource.west
//								.updateGroupInfoForDeleteResource();
						that.loadAll();
					});
		},
		createGroupDelCfg : function() {			
			var that = this;
			// datagridgroup
			// 自定义资源组的删除操作
			this.groupDelCfg = {
				url : oc.resource.getUrl('portal/resource/delResourceFromCustomGroup.htm'),
				id : 'monitorRemoveResGroupBtn',
                /*BUG #38476 资源管理：【更多操作】按钮位置需要移动 huangping 2017/7/5 start*/
                idx: 8,
                /*BUG #38476 资源管理：【更多操作】按钮位置需要移动 huangping 2017/7/5 end*/
				iconCls : 'fa fa-arrows',
				remoteField : 'resourceInstanceIds',
				direction : 'right',
				text : '移出资源组',
				confirmMsg : '是否移出选中资源',
				delMsg : '至少选择一条资源',
				successMsg : '移出成功',
				data : {
					id : that.gourpId
				},
				success : function() {
					var isDeleteTotal = false;
					var nowResourceIds = that.resourceInput.val().split(',');
					var newResourceIds = new Array(), selectIdsString = new Array(), currentDatagridSelectIds = that.getCurrentDatagridSelectIds();
					for (var i = 0; i < nowResourceIds.length; i++) {
						if ($.inArray(parseFloat(nowResourceIds[i]),currentDatagridSelectIds) == -1) {
							newResourceIds.push(nowResourceIds[i]);
						}
					}
					for (var i = 0; i < currentDatagridSelectIds.length; i++) {
						selectIdsString.push(currentDatagridSelectIds[i] + "");
					}
					// 删除全部资源
					if (selectIdsString.join() == that.curResourceIDs.join()) {
						isDeleteTotal = true;
					}
					that.curResourceIDs = newResourceIds;
					that.resourceInput.val(newResourceIds.join());
					that.resourceInputUnmonitor.val(newResourceIds.join());

					oc.module.resourcemanagement.resource.west
							.updateCustomGroupInfo(currentDatagridSelectIds,
									isDeleteTotal);
					that.curDataGrid.reLoad();
				}
			};
			this.showDelCfg = $.extend({}, this.groupDelCfg);
			this.showDelCfg.id = 'unMonitorRemoveResGroupBtn';
		},
		createMonitorDatagrid : function() {
			quanGrid=this;
			var that = this;
			var hiddenProfile = true;
			var ipAddressWidth = '23%';
			var user = oc.index.getUser();
			if (user.domainUser || user.systemUser) {
				hiddenProfile = false;
				ipAddressWidth = '15%';
			}

			var dataGridCfg1 = {
				selector : this.tab.tabs('getTab', 0).find('.oc-datagrid'),
				pageSize : $.cookie('pageSize_resource_list') == null ? 15 : $
						.cookie('pageSize_resource_list'),
				fitColumns : false,
				queryForm : this.queryForm,
				url : oc.resource.getUrl('resource/resourceMonitor/getHaveMonitor.htm'),
				delCfg : this.groupDelCfg,
				hideReset : true,
				hideSearch : true,
				columns : [ [
						{
							field : 'id',
							checkbox : true,
							isDisabled : function(value, row, index) {
								return !row.hasRight;
							}
						},
						{
							field : 'sourceName',
							title : '名称',
							sortable : true,
							align : 'left',
							width : '21%',
							formatter : function(value, row, rowIndex) {
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
							title : 'IP地址/域名',
							sortable : true,
							width : ipAddressWidth,
							ellipsis : false,
							formatter : function(value, row, rowIndex) {
								// 加入手形样式							
								if (row.resourceId == 'GENEURL'
										&& value != '--' && value != ''
										&& value != null && value != undefined) {
									return "<span style='cursor:pointer;' title='"
											+ value + "'>" + value + "</span>";
								} else {
									return "<span title='" + value + "'>"
											+ value + "</span>";
								}
							}
						},
						{
							field : 'sourceIp',
							title : '源IP地址/域名',
							sortable : true,
							width : '15%',
							ellipsis : false,

						},
						{
							field : 'distIP',
							title : '目的IP地址/域名',
							sortable : true,
							width : '15%',
							ellipsis : false,
						},
						{
							field : 'monitorType',
							title : '监控类型',
							sortable : true,
							width : '12%',
							ellipsis : true
						},
						{
							field : 'cpuAvailability',
							title : 'CPU利用率',
							sortable : true,
							align : 'left',
							width : '15%',
							formatter : function(value, row, rowIndex) {
								if (row.cpuIsAlarm == false) {
									row.cpuStatus = 'green';
								}
								var cpuRate = row.cpuAvailability;
								if (cpuRate == null || cpuRate == 'N/A') {
									return 'N/A';
								} else if (cpuRate == '--') {
									return '--';
								}
								var cpuStatus = row.cpuStatus;
								// 主资源为致命或未知则指标显示为灰色
								var instanceStatus = row.instanceStatus;
								if (null == instanceStatus
										|| instanceStatus == 'res_critical'
										|| instanceStatus == 'res_critical_nothing'
										|| instanceStatus == 'res_unknown_nothing'
										|| instanceStatus == 'res_unkown'
										|| null == cpuStatus) {
									return '--';
									// cpuStatus = 'gray';
								}
								return '<div class="rate rate-t-' + cpuStatus
										+ '"><span class="rate-' + cpuStatus
										+ '" style="width:' + cpuRate
										+ '"></span></div><span class="rt">'
										+ cpuRate + '</span>';
							}
						},
						{
							field : 'memoryAvailability',
							title : '内存利用率',
							sortable : true,
							align : 'left',
							width : '15%',
							formatter : function(value, row, rowIndex) {
								if (row.memoryIsAlarm == false) {
									row.memoryStatus = 'green';
								}
								var memRate = row.memoryAvailability;
								if (memRate == null || memRate == 'N/A') {
									return 'N/A';
								} else if (memRate == '--') {
									return '--';
								}
								var memStatus = row.memoryStatus;
								// 主资源为致命或未知则指标显示为灰色
								var instanceStatus = row.instanceStatus;
								if (null == instanceStatus
										|| instanceStatus == 'res_critical'
										|| instanceStatus == 'res_critical_nothing'
										|| instanceStatus == 'res_unknown_nothing'
										|| instanceStatus == 'res_unkown'
										|| null == memStatus) {
									return '--';
									// memStatus = 'gray';
								}
								return '<div class="rate rate-t-' + memStatus
										+ '"><span class="rate-' + memStatus
										+ '" style="width:' + memRate
										+ '"></span></div><span class="rt">'
										+ memRate + '</span>';
							}
						},
						{
							field : 'domainName',
							title : '域名称',
							sortable : true,
							ellipsis : true,
							width : '6%'
						},
						{
							field : 'liablePerson',
							title : '责任人',
							sortable : false,
							ellipsis : true,				
							width : '6%'
						},					
						{
							field : 'resourceId',
							title : '操作',
							width : '8%',
							hidden : hiddenProfile,
							formatter : function(value, row, rowIndex) {// 将三个操作合为一列
								// return html;
								return '<a data-index="'
										+ rowIndex
										+ '" class="light_blue icon-field a_domain_edit_btn" title="域编辑"></a><a rowIndex="'
										+ rowIndex
										+ '" class="light_blue icon-tactics quickSelectProfile" title="编辑策略"></a><a rowIndex="'
										+ rowIndex
										+ '" class="light_blue icon-edit quickSelectDiscoverParamter" title="编辑发现信息"></a>';
							}
						} ] ],
				loadFilter : function(data) {
					var myData = data.data;
					// 这种方式比较耗时 改为jquery直接修改
					/*
					 * var cuTab = $("#"+that.domId).tabs("getTab",0);
					 * $("#"+that.domId).tabs("update",{ tab:cuTab, options:{
					 * title:'已监控('+myData.totalRecord+')' } });
					 */
					$("#" + that.domId).find(
							'.tabs-header .tabs-wrap .tabs-title:eq(0)').html(
							'已监控(' + myData.totalRecord + ')');
					return myData;
				},
				onLoadSuccess : function(data) {			
			 	   
					that.monitorDataGridLoadFlag = true;
					that.categoryCountList = data.resourceCategoryBos;
					// 调整列宽
					if (that.ifStandardService) {
						that.dataGrid1.selector.datagrid('hideColumn',
								'cpuAvailability');
						that.dataGrid1.selector.datagrid('hideColumn',
								'memoryAvailability');
						var datagridView2 = that.dataGrid1.selector.parent()
								.find('div[class=datagrid-view2]');
						var datagridView2Width = datagridView2.width();
						//console.log('view2 width:'+datagridView2Width+'px');

						if(data.condition.categoryId == 'RemotePings'){
							//源IP
							if (that.dataGrid1.selector.datagrid('getColumnOption',
									'sourceIp').hidden) {
								that.dataGrid1.selector.datagrid('showColumn',
										'sourceIp');
							}
							//目的IP
							if (that.dataGrid1.selector.datagrid('getColumnOption',
									'distIP').hidden) {
								that.dataGrid1.selector.datagrid('showColumn',
										'distIP');
							}
						}else{
							that.dataGrid1.selector.datagrid('hideColumn',
								'sourceIp');
							that.dataGrid1.selector.datagrid('hideColumn',
								'distIP');
							datagridView2.find('table[class=datagrid-htable]').css(
								'width', (datagridView2Width - 27) + 'px');
							datagridView2.find('table[class=datagrid-btable]').css(
								'width', (datagridView2Width - 27) + 'px');
						}
					} else {
						that.dataGrid1.selector.datagrid('hideColumn',
								'sourceIp');
						that.dataGrid1.selector.datagrid('hideColumn',
								'distIP');
						if (that.dataGrid1.selector.datagrid('getColumnOption',
								'cpuAvailability').hidden) {
							that.dataGrid1.selector.datagrid('showColumn',
									'cpuAvailability');
						}
						if (that.dataGrid1.selector.datagrid('getColumnOption',
								'memoryAvailability').hidden) {
							that.dataGrid1.selector.datagrid('showColumn',
									'memoryAvailability');
						}
						var datagridView2 = that.dataGrid1.selector.parent()
								.find('div[class=datagrid-view2]');
						var datagridView2Width = datagridView2.width();
						datagridView2.find('table[class=datagrid-htable]').css(
								'width', (datagridView2Width - 27) + 'px');
						datagridView2.find('table[class=datagrid-btable]').css(
								'width', (datagridView2Width - 27) + 'px');
					}
					// 绑定点击事件	
					//编辑维修模式fa fa-trash-o
					that.tab.find(".editMaintain").on('click',function(e) {
						var rowIndex = $(this).attr('rowIndex');
						var row = that.dataGrid1.selector.datagrid('getRows')[rowIndex];
						var instanceId =[row.id];		
						var timeStart=row.maintainStartTime;
						var timeEnd=row.maintainEndTime;
						showDialogMaintain(instanceId,timeStart,timeEnd)
						});
					
					// 查看资源详情
					that.tab.find(".quickSelectDetailInfo").on('click',function(e) {
						var isHaveDomain=false;
										var rowIndex = $(this).attr('rowIndex');
										var row = that.dataGrid1.selector.datagrid('getRows')[rowIndex];
										var instanceId = row.id;
										var instanceStatus = row.instanceStatus;
										var hasRight = row.hasRight;
									
									
										if (hasRight ) {
											oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js',
															function() {
																oc.module.resmanagement.resdeatilinfonew
																		.open({
																			instanceId : instanceId,
																			callback : that,
																			callbackType : '1',
																			type : '0',
																			instanceStatus : instanceStatus
																		});
															});
										} else {
											alert('没有该资源的权限');
										}
										e.stopPropagation();
									});
					// 编辑策略信息
					that.tab
							.find(".quickSelectProfile")
							.on(
									'click',
									function(e) {
										var rowIndex = $(this).attr('rowIndex');
										var row = that.dataGrid1.selector
												.datagrid('getRows')[rowIndex];
										var instanceId = row.id;
										var hasRight = row.hasRight;
										if (hasRight) {
											oc.resource
													.loadScript(
															'resource/module/resource-management/js/quick_strategy_select.js',
															function() {
																oc.quick.strategy.detail
																		.show(instanceId);
															});
										} else {
											alert('没有该资源的权限');
										}
										e.stopPropagation();
									});
					// 编辑发现参数
					that.tab.find(".quickSelectDiscoverParamter").on('click',
							function(e) {
								var rowIndex = $(this).attr('rowIndex');
								var row = that.dataGrid1.selector
										.datagrid('getRows')[rowIndex];
								var instanceId = row.id;
								var hasRight = row.hasRight;
								if (hasRight) {
									that.editDiscoverParamter(row);
								} else {
									alert('没有该资源的权限');
								}
								e.stopPropagation();
							});
					/** ********修改资源所属域start******** */
					resourceGrid.prototype.editdomain = {};
					var user = oc.index.getUser();
					that.tab.find(".a_domain_edit_btn").on(	'click',function() {
										var index = $(this).data('index');
										that.editdomain.row = that.dataGrid1.selector
												.datagrid('getRows')[index];

										that.editdomain.render = function() {
											this.mainDiv = this.dlg
													.find(
															"#edit_resource_domain_main")
													.attr(
															"id",
															oc.util
																	.generateId());
											this.form = oc.ui
													.form({
														selector : this.mainDiv
																.find(
																		".edit_resource_domain_form")
																.first(),
														combobox : [ {
															selector : '[name=domainId]',
															data : oc.index	.getDomains(),
															value : that.editdomain.row.domainId,
															placeholder : false,
															onSelect : function(
																	record) {
																oc.util
																		.ajax({
																			url : oc.resource.getUrl('resource/resourceMonitor/getDomainDcs.htm'),
																			data : {
																				domainId : record.id
																			},
																			success : function(d) {
																				if (d.data) {
																					var dcsInfo = d.data;
																					var flag = true;
																					for (var i = 0; i < dcsInfo.length; i++) {
																						var dcsItem = dcsInfo[i];
																						if (dcsItem.isChecked
																								&& dcsItem.isChecked == 1) {
																							that.editdomain.dlg
																									.find(
																											'input[name=dcsGroupName]')
																									.val(
																											dcsItem.dcsName);
																							flag = false;
																							break;
																						}
																					}
																					if (flag) {
																						that.editdomain.dlg
																								.find(
																										'input[name=dcsGroupName]')
																								.val(
																										"");
																					}

																				}
																			}
																		})
															}
														} ]
													});

											this.domainId = this.row.domainId;
											this.form.load(this.row);
											if (!user.systemUser) {
												this.form.disable();
												this.form.coms.domainId.jq
														.parent()
														.find(
																":input[type=text]")
														.attr("disabled", true);
											}
										};
										that.editdomain.save = function() {
											var target = this, data = target.form
													.val();
											if (target.domainId == data.domainId) {
												target.dlg.window("close");
												return;
											}
											if (!data || !data.dcsGroupName) {
												alert('请确认参数');
												return;
											}
											oc.util
													.ajax({
														url : oc.resource
																.getUrl('resource/resourceMonitor/update_resource_domain.htm'),
														timeout : 5000,
														data : data,
														success : function(d) {
															if (d.code == 200) {
																target.row.domainId = data.domainId;
																target.dlg
																		.window("close");
																that.dataGrid1.selector
																		.datagrid("reload");
																alert("修改成功！");
															} else {
																if (d.code == 202) {
																	log("参数为空",
																			data);
																}
															}
														},
														error : function(e) {
															alert("修改失败....");
														}
													});
										}
										var buttons = [
												{
													text : "确定",
													iconCls : "fa fa-check-circle",
													handler : function() {
														that.editdomain.save();
													}
												},
												{
													text : '取消',
													iconCls:"fa fa-times-circle",
													handler : function() {
														that.editdomain.dlg
																.window("destroy");
													}
												} ];
										that.editdomain.dlg = $("<div/>")
												.dialog(
														{
															content : "<div class='edit_resource_domain'></div>",
															title : '编辑域信息',
															height : 255,
															width : 400,
															cache : false,
															modal : true,
															_onOpen : function() {
																$
																		.ajax({
																			url : oc.resource
																					.getUrl('resource/module/resource-management/edit_resource_domain.html'),
																			dataType : 'html',
																			success : function(
																					data) {
																				var reg = /<body[^>]*>((.|[\n\r])*)<\/body>/im;
																				var body = reg
																						.exec(data);
																				var page = $(body ? body[1]
																						: data);
																				that.editdomain.dlg
																						.find(
																								".edit_resource_domain")
																						.append(
																								page);
																				that.editdomain
																						.render();
																			},
																			error : function(
																					e) {
																				alert("页面被管理员给搞丢了");
																			}
																		});
															},
															buttons : !user.systemUser ? []
																	: [
																			{
																				text : "确定",
																				iconCls : "fa fa-check-circle",
																				handler : function() {
																					that.editdomain
																							.save();
																				}
																			},
																			{
																				text : '取消',
																				iconCls:"fa fa-times-circle",
																				handler : function() {
																					that.editdomain.dlg
																							.window("destroy");
																				}
																			} ]
														});

									});
					/** *******修改资源所属域end********* */
					// 取消进度条
					that.disableGlobalProgress();
				},
				onClickCell : function(rowIndex, field, value) {
					if (field == 'ipAddress') {
						var row = $(this).datagrid('getRows')[rowIndex];
						if (row.resourceId == 'GENEURL' && value != '--'
								&& value != '' && value != undefined
								&& value != null) {
							var newValue = value;
							if (newValue.search(/http:\/\//) == -1) {
								newValue = 'http://' + newValue;
							}
							window.open(newValue, field);
						}
					}
				},
				octoolbar : {
					left : [ this.queryForm.selector ],
					right : [
							{
								id : "discoverBtn",
								iconCls : 'fa fa-eye',
								text : "资源发现",
								onClick : function() {
								}
							},
							'&nbsp;',
							{
								id : "deleteResurce",
								iconCls : 'fa fa-trash-o',
								text : "删除资源",
								onClick : function() {
									var ids = that
											.getMonitorDatagridSelectIds();
									if (ids == undefined || ids == "") {
										alert("请选择需要删除的资源");
									} else {
										oc.ui
												.confirm(
														"是否确认删除资源？",
														function() {
															oc.util
																	.ajax({
																		url : oc.resource
																				.getUrl('resource/resourceMonitor/batchDelResource.htm'),
																		timeout : null,
																		data : {
																			ids : ids.join()
																		},
																		successMsg : null,
																		success : function(d) {
																			that.loadAll();
																			oc.module.resourcemanagement.resource.west.updateGroupInfoForDeleteResource();
																		}
																	});
														});
									}
								}
							},
							'&nbsp;',
							{
								id : "cancleMonitor",
								iconCls : ' fa fa-times-circle',
								text : "取消监控",
								onClick : function() {
									var ids = that.getMonitorDatagridSelectIds();
									if (!!ids && ids.length > 0) {
										var item =quanGrid.dataGrid1.selector.datagrid("getSelections");
										var maintainIndexs=[];
										for (var i =0;i< item.length ; i++) {
											if (item[i].maintainStaus =="1") {
												maintainIndexs.push(i);													
											}
										}
										if(maintainIndexs.length>0){//选择了在正处于维修阶段的资源'
											oc.ui	.confirm("您确定要操作设置了维修模式的资源？",	function() {
												// 取消维修模式
												showDialogMaintainClear(ids,2);
												oc.util.ajax({
													url : oc.resource.getUrl('resource/resourceMonitor/batchCloseMonitor.htm'),
													data : {
														ids : ids.join()
													},
													successMsg : null,
													async : false,
													success : function(d) {
														that.loadAll();
													}
												});
											});
										}
										else{
											oc.util.ajax({
												url : oc.resource.getUrl('resource/resourceMonitor/batchCloseMonitor.htm'),
												data : {
													ids : ids.join()
												},
												successMsg : null,
												async : false,
												success : function(d) {
													that.loadAll();
												}
											});
										}								
									} else {
										alert('请选择需要取消监控的资源');
									}
								}
							},
							'&nbsp;',
							{
								id : "monitorAddResGroupBtn",
								iconCls : 'icon-movein',
								text : "移入资源组",
								onClick : function() {
									var ids = that.getMonitorDatagridSelectIds();
									if (!!ids && ids.length > 0) {
										oc.module.resourcemanagement.resource.west.addResourceIntoGroup(ids.join());
									} else {
										alert('请选择需要移入资源组的资源');
									}
								}
							},
							'&nbsp;',
							{
								id : "moreOpBtn",
								iconCls : 'fa fa-th-large',
								text : "更多操作",
								onClick : function(e) {
									e.stopPropagation();
									var top = $("#moreOpBtn").offset().top + 18;
									var left = $("#moreOpBtn").offset().left - 5;
									$('body').find('.cgtOper').remove();
									cgtOperDiv = $("<div class='cgtOper sh-window' style='top:"
											+ top + "px;left:" + left + "px'>");
									tWindowDiv = $("<div class='t-window'><div class='b-window'><div class='m-window'>"
											+ "<span class='cgtEdit'><a class='icon-Person'></a>编辑责任人</span>"
											+ "<span class='cgtDel'><a class='icon-clearp'></a>清除责任人</span>"
											+ "<span id='cgtEditDominId' class='cgtEditDomin'><a class='icon-field'></a>编辑域</span>"	
											+ "</div></div></div>");
									$('body').append(cgtOperDiv.append(tWindowDiv));
									// 编辑责任人
									$(cgtOperDiv).find(".cgtEdit").on('click',function(e) {
														var ids = that.getMonitorDatagridSelectIds();													
														if (!!ids	&& ids.length > 0) {
															showDialogPerson(ids);
															e.stopPropagation();
														} else {
															alert('请选择需要操作的资源');
														}

													});
									// 清除责任人
									$(cgtOperDiv).find(".cgtDel").on('click',function(e) {
										var ids = that.getMonitorDatagridSelectIds();	
										if (!!ids	&& ids.length > 0) {
											showDialogPersonClear(ids);
											e.stopPropagation();
										} else {
											alert('请选择需要操作的资源');
										}
											});
									// 编辑域
									if(!user.systemUser){ //域用户不具有编辑域权限
										$('#cgtEditDominId').css("color","#6f6f6f");
									}
									else{
										$(cgtOperDiv).find(".cgtEditDomin").on('click', function(e) {
											var ids = that.getMonitorDatagridSelectIds();
											if (!!ids	&& ids.length > 0) {
												showDialogDomin(ids);
												e.stopPropagation();
											} else {
												alert('请选择需要操作的资源');
											}	
										});										
									}																			
									
									// 已监控资源批量编辑维修模式
									$(cgtOperDiv).find(".cgtWeixiu").on('click', function(e) {
										var ids = that.getMonitorDatagridSelectIds();
										if (!!ids	&& ids.length > 0) {
											showDialogMaintain(ids,"","");
											e.stopPropagation();
										} else {
											alert('请选择需要操作的资源');
										}												
									});

									// 已监控资源批量清除维修模式
									$(cgtOperDiv).find(".cgtWeixiuQx").on('click', function(e) {								
										var ids = that.getMonitorDatagridSelectIds();
										if (!!ids	&& ids.length > 0) {
											var item =quanGrid.dataGrid1.selector.datagrid("getSelections");
											for (var i =0;i< item.length ; i++) {
												if (item[i].maintainStaus !="1") {
													alert('您选择了未设置维修模式的资源');
													return;
												}
											}
											showDialogMaintainClear(ids,2);
											e.stopPropagation();
										} else {
											alert('请选择需要操作的资源');
										}												
									});
									$(cgtOperDiv).hover(function(e) {
										e.stopPropagation();
									}, function(e) {
										e.stopPropagation();
										$('body').find('.cgtOper').remove();
									});
								}
							}]
				}
			};

			// 绑定事件
			$('.light')
					.each(
							function(i) {
								var instanceStatus = $('#show_resource_instance_form_instanceStatus');
								var iPorNameId = $('#iPorNameId');
								switch ($(this).attr('id')) {
								case 'all':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('all');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'resource_availability':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('availability');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'resource_unavailability':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('unavailability');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'resource_unknown':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('unknown');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'down':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('down');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'metric_error':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('metric_error');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'metric_warn':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('metric_warn');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'metric_recover':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('metric_recover');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								case 'metric_unkwon':
									$(this).on('click', function() {
										that.addStatusLightActive(this);
										instanceStatus.val('metric_unkwon');
										// iPorNameId.val("");
										that.dataGrid1.reLoad();
									});
									break;
								}
							});
			this.dataGrid1 = oc.ui.datagrid(dataGridCfg1);
			this.curDataGrid = this.dataGrid1;

			// cookie记录pagesize
			var paginationObject = this.tab.tabs('getTab', 0).find(
					'.oc-datagrid').datagrid('getPager');
			if (paginationObject) {
				paginationObject.pagination({
					onChangePageSize : function(pageSize) {
						$.cookie('pageSize_resource_list', pageSize);
					}
				});
			}
		},
		createUnMonitorDatagrid : function() {
			quanGridUn=this;
			var that = this;
			var tabsWidth = $("#" + that.domId).width();
			var colWidth = tabsWidth / 4;
			this.dataGrid2 = oc.ui
					.datagrid({
						selector : this.tab.tabs('getTab', 1).find('.oc-datagrid'),
						pageSize : $.cookie('pageSize_resource_list') == null ? 15
								: $.cookie('pageSize_resource_list'),
						fitColumns : false,
						queryForm : this.queryFormUnmonitor,
						delCfg : this.showDelCfg,
						hideReset : true,
						hideSearch : true,
						// checkOnSelect : false,
						url : oc.resource
								.getUrl('resource/resourceMonitor/getNotMonitor.htm'),
						loadFilter : function(data) {
							var myData = data.data;
							// 这种方式比较耗时 改为jquery直接修改
							/*
							 * var cuTab = $("#"+that.domId).tabs("getTab",1);
							 * $("#"+that.domId).tabs("update",{ tab:cuTab,
							 * options:{ title:'未监控('+myData.totalRecord+')' }
							 * });
							 */
							$("#" + that.domId)
									.find(
											'.tabs-header .tabs-wrap .tabs-title:eq(1)')
									.html('未监控(' + myData.totalRecord + ')');
							return myData;
						},
						onLoadSuccess : function(data) {
							// 绑定点击事件	
							//编辑维修模式fa fa-trash-o 单个编辑
							that.tab.find(".editMaintainUn").on('click',function(e) {
								var rowIndex = $(this).attr('rowIndex');
								var row = that.dataGrid2.selector.datagrid('getRows')[rowIndex];
								var instanceId =[row.id];		
								var timeStart=row.maintainStartTime;
								var timeEnd=row.maintainEndTime;								
								showDialogMaintainOver(instanceId,timeStart,timeEnd)
								});
							that.unMonitorDataGridLoadFlag = true;
							that.categoryCountList = data.resourceCategoryBos;
							that.disableGlobalProgress();
						},
						columns : [ [
								{
									field : 'id',
									checkbox : true,
									isDisabled : function(value, row, index) {
										return !row.hasRight;
									}
								},
								{
									field : 'sourceName',
									title : '名称',
									sortable : true,
									width : colWidth + 'px',
									formatter : function(value, row, rowIndex) {
										if(value){
											return '<label title="' + value + '">'
											+ value.htmlspecialchars()
											+ '</label>';
										}else{
											return '';
										}
										
									}
								}, {
									field : 'ipAddress',
									title : 'IP地址/域名',
									sortable : true,
									width : colWidth + 'px'									
									}, {
									field : 'monitorType',
									title : '监控类型',
									sortable : true,
									width : colWidth + 'px'
								}, {
									field : 'domainName',
									title : '域名称',
									ellipsis : true,
									sortable : true,
									width : (colWidth - 50) + 'px'
								} ] ],
						octoolbar : {
							left : [ this.queryFormUnmonitor.selector ],
							right : [
									{
										id : "addMonitor",
										iconCls : 'fa fa-plus',
										text : "开启监控",
										onClick : function() {
											var ids = that.getUnMonitorDatagridSelectIds();
											if (!!ids && ids.length > 0) {
												var item =quanGridUn.dataGrid2.selector.datagrid("getSelections");
												var maintainIndexs=[];
												for (var i =0;i< item.length ; i++) {
													if (item[i].maintainStaus =="1") {
														maintainIndexs.push(i);													
													}
												}
												if(maintainIndexs.length>0){//选择了在正处于维修阶段的资源'
													oc.ui	.confirm("您确定要操作设置了维修模式的资源？",	function() {
														// 取消维修模式
														showDialogMaintainClear(ids,1);
														oc.util.ajax({
															url : oc.resource.getUrl('resource/resourceMonitor/batchOpenMonitor.htm'),
															type : 'POST',
															dataType : "json",
															data : {
																ids : ids.join()
															},
															successMsg : null,
															success : function(
																	json) {
																if (json.code == 200) {
																	if (json.data == 1) {
																		alert('加入监控成功');
																	} else if (json.data == 0) {
																		alert('加入监控失败');
																	} else {
																		alert('加入监控失败,监控数量超过购买监控数量');
																	}
																} else {
																	alert('加入监控失败');
																}
																that.loadAll();
															}
														});
													});
												}
												else{
													oc.util.ajax({
														url : oc.resource.getUrl('resource/resourceMonitor/batchOpenMonitor.htm'),
														type : 'POST',
														dataType : "json",
														data : {
															ids : ids
																	.join()
														},
														successMsg : null,
														success : function(
																json) {
															if (json.code == 200) {
																if (json.data == 1) {
																	alert('加入监控成功');
																} else if (json.data == 0) {
																	alert('加入监控失败');
																} else {
																	alert('加入监控失败,监控数量超过购买监控数量');
																}
															} else {
																alert('加入监控失败');
															}
															that.loadAll();
														}
													});
												}								
											} else {
												alert('请选择需要开启监控的资源');
											}
										}
									},
									'&nbsp;',
									{
										id : "deleteResurceUnmonitor",
										iconCls : 'fa fa-trash-o',
										text : "删除资源",
										onClick : function() {
											var ids = that.getUnMonitorDatagridSelectIds();
											if (ids == undefined || ids == "") {
												alert("请选择需要删除的资源");
											} else {
												oc.ui	.confirm("是否确认删除资源？",	function() {
													oc.util.ajax({
																				url : oc.resource.getUrl('resource/resourceMonitor/batchDelResource.htm'),
																				data : {
																					ids : ids.join()
																				},
																				successMsg : null,
																				success : function(
																						d) {
																					that
																							.loadAll();
																					oc.module.resourcemanagement.resource.west
																							.updateGroupInfoForDeleteResource();
																				}
																			});
																});
											}
										}
									},
									'&nbsp;',
									{
										id : "notMonitorAddResGroupBtn",
										iconCls : 'icon-movein',
										text : "移入资源组",
										onClick : function() {
											var ids = that
													.getUnMonitorDatagridSelectIds();
											if (!!ids && ids.length > 0) {
												oc.module.resourcemanagement.resource.west
														.addResourceIntoGroup(ids
																.join());
											} else {
												alert('请选择需要移入资源组的资源');
											}
										}
									}]
						}
					});

			// cookie记录pagesize
			var paginationObject = this.tab.tabs('getTab', 1).find(
					'.oc-datagrid').datagrid('getPager');
			if (paginationObject) {
				paginationObject.pagination({
					onChangePageSize : function(pageSize) {
						$.cookie('pageSize_resource_list', pageSize);
					}
				});
			}
		},
		getCurrentDatagridSelectIds : function() {
			return this.getSelectIds(this.curDataGrid);
		},
		getMonitorDatagridSelectIds : function() {
			return this.getSelectIds(this.dataGrid1);
		},
		getUnMonitorDatagridSelectIds : function() {
			return this.getSelectIds(this.dataGrid2);
		},
		getSelectIds : function(dataGrid) {
			var objs = dataGrid.selector.datagrid('getChecked'), ids = [];
			for (var i = 0, len = objs.length; i < len; i++) {
				var obj = objs[i];
				if (obj.hasRight) {
					ids.push(obj.id);
				}
			}
			return ids;
		},
		createDiscoverBtn : function() {
			var that = this;
			var user = oc.index.getUser();
			if (user.domainUser || user.systemUser) {				
				$("#discoverBtn")
						.on(
								'click',
								function() {
									oc.resource.loadScript('resource/module/resource-management/discresource/js/discresource.js',
													function() {
														oc.module.resmanagement.discresource.disc
																.open({
																	resourceListObject : that
																});
													});
								});
			} else {
				$("#discoverBtn").hide();
				$("#addMonitor").hide();
				$('#moreOpBtn').hide();//更多操作隐藏
				$("#deleteResurce").remove();
				$("#deleteResurceUnmonitor").remove();
				$("#cancleMonitor").hide();
			}
		},
		reLoadWestAndList : function() {
			$('#customGroupTree').tree('reload');
		},
		loadAll : function() {
			this.enableGlobalProgress();
			this.dataGrid1.load();
			this.dataGrid2.load();
		},
		reLoadAll : function() {
			this.enableGlobalProgress();
			this.dataGrid1.reLoad();
			this.dataGrid2.reLoad();
		},
		enableGlobalProgress : function() {
			this.monitorDataGridLoadFlag = false;
			this.unMonitorDataGridLoadFlag = false;
			oc.ui.progress();
		},
		disableGlobalProgress : function() {
			var that = this;
			if (this.monitorDataGridLoadFlag && this.unMonitorDataGridLoadFlag) {
//				oc.module.resourcemanagement.resource.west
//						.refreshCategoryCount(that.categoryCountList);
				$.messager.progress('close');
			} else {
				oc.ui.progress();
			}
		},
		editDiscoverParamter : function(data) {
			var that = this;
			var domId = oc.util.generateId();
			var dlg = $("<div/>");
			var panel = $("<div/>").attr('id', domId).css({
				'width' : '100%',
				'height' : '100%',
				'overflow-y' : 'auto'
			});
			panel.append($("<div/>").addClass("singleDisc_discPara_content"));
			dlg.append(panel).dialog({
				title : '编辑发现信息',
				width : '502px',
				height : '400px',
				buttons : [ {
					text : '连接测试',
					handler : function() {
						that.testDiscover(dlg, panel, data.id);
					}
				}, {
					text : '保存发现信息',
					handler : function() {
						that.saveDiscoverParamter(dlg, panel, data.id);
					}
				}, {
					text : '重新发现',
					handler : function() {
						that.reDiscover(dlg, panel, data.id);
					}
				} ],
				onClose : function() {
					that.dataGrid1.reLoad();

				}
			});
			oc.util
					.ajax({
						url : oc.resource
								.getUrl("portal/resource/discoverResource/getpluginInitParameter.htm"),
						data : {
							resourceId : data.resourceId
						},
						success : function(json) {

							if (json.data) {
								oc.resource
										.loadScript(
												'resource/module/resource-management/discresource/js/disresource_singlediscover_paramter.js',
												function() {
													oc.module.resmanagement.discresource.singlediscparam
															.open({
																id : domId,
																data : json.data,
																resourceId : data.resourceId
															});
													panel
															.find(
																	"fieldset[name='domainfieldset'],fieldset[name='DCS']")
															.hide();
													panel
															.find(
																	"[name='discoverBtn']")
															.parent().remove();
													panel
															.find(
																	"[name='usernameBtn']")
															.parent()
															.attr(
																	'isDiscovery',
																	'not_discovery');
													var v = panel
															.find("form")
															.find(
																	"select[textboxname='securityLevel'] option:selected")
															.val();
													var jsonData = json.data;

													// Telnet/SSH连接信息
													if (data.hasTelSSHParams) {
														that
																.createTelSSHView(panel
																		.find("form"));
													}
													that.setDiscoverParamter(
															panel, data.id,
															jsonData);
												});
							}
						}
					});
		},
		createTelSSHView : function(form) {
			var fieldset = $("<fieldset class='singdiscparamfieldset telSSHFieldset'>"
					+ "<legend>Telnet/SSH连接信息<span class='telSSHBtn'><span class='telSSHBtnImg begina'></span><span class='telSSHBtnTxt'>展开</span></span></legend>"
					+ "<div class='telSSHParam'></div>" + "</fieldset>");
			fieldset
					.find(".telSSHParam")
					.append(
							"<div class='form-group' style='text-align:left;'>"
									+ "<label style='width:150px;'>登录方式：</label>"
									+ "<div>"
									+ "<select name='loginType'></select>"
									+ "<a title='登录方式' href='javascript:void(0);' style='display:inline-block; zoom:1; *display:inline; position: relative;top:7px;'>"
									+ "<span class='r-h-ico r-h-help'></span>"
									+ "</a>" + "</div>" + "</div>");
			var params = [ {
				id : 'loginPort',
				name : '登陆端口',
				type : 'text'
			}, {
				id : 'username',
				name : '用户名',
				type : 'text'
			}, {
				id : 'password',
				name : '密码',
				type : 'password'
			}, {
				id : 'enableUserName',
				name : '特权模式用户名',
				type : 'text'
			}, {
				id : 'enablePassword',
				name : '特权模式密码',
				type : 'password'
			} ];
			for (var i = 0; i < params.length; i++) {
				var param = params[i];
				fieldset
						.find(".telSSHParam")
						.append(
								"<div class='form-group' style='text-align:left;'>"
										+ "<label style='width:150px;'>"
										+ param.name
										+ "：</label>"
										+ "<div>"
										+ "<input type='"
										+ param.type
										+ "' name='"
										+ param.id
										+ "'>"
										+ "<a title='"
										+ param.name
										+ "' href='javascript:void(0);' style='display:inline-block; zoom:1; *display:inline; position: relative;top:7px;'>"
										+ "<span class='r-h-ico r-h-help'></span>"
										+ "</a>" + "</div>" + "</div>");
			}
			$(form).append(fieldset);
			oc.ui.combobox({
				selector : fieldset.find("[name='loginType']"),
				placeholder : false,
				data : [ {
					id : 'Telnet',
					name : 'Telnet'
				}, {
					id : 'SSH',
					name : 'SSH'
				} ],
				value : 'Telnet'
			});
			fieldset.find(".telSSHParam").hide();
			fieldset.find(".telSSHBtn").on('click',function(e){
				if($(this).find('.telSSHBtnImg').hasClass('begina')){
					fieldset.find(".telSSHParam").show();
					fieldset.find(".telSSHBtnImg").removeClass('begina').addClass('beginb');
					fieldset.find(".telSSHBtnTxt").html('收起');
					fieldset.removeClass('telSSHFieldset');
				}else{
					fieldset.find(".telSSHParam").hide();
					fieldset.find(".telSSHBtnImg").removeClass('beginb').addClass('begina');
					fieldset.find(".telSSHBtnTxt").html('展开');
					fieldset.addClass('telSSHFieldset');
				}
			});
		},
		setDiscoverParamter : function(panel, instanceId, jsonData) {
			var that = this;
			oc.util
					.ajax({
						url : oc.resource
								.getUrl("resource/resourceMonitor/getInstanceDiscoverParamter.htm"),
						data : {
							instanceId : instanceId
						},
						success : function(json) {
							if (json.data) {
								var props = json.data;
								var isnetwork = false;
								var securityLevel = '';
								var securityLevel1 = '';
								var snmpVersion = '';
								var snmpVersion1 = '';
								for (var i = 0; i < props.length; i++) {
									if (props[i].key == 'securityLevel') {
										securityLevel1 = props[i].value;
									}
									if (props[i].key == 'snmpVersion') {
										snmpVersion1 = props[i].value;
									}
								}
								for (var i = 0; i < props.length; i++) {
									var prop = props[i];
									if (prop.key == 'collectType') {
										var propDom = panel.find("input[name='"
												+ prop.key + "']");
										if (propDom.parent().is(".combo")) {
											propDom = propDom.parent().parent()
													.find('select');
											var collectTypeDatas = propDom
													.combobox('getData');
											for (var j = 0; j < collectTypeDatas.length; j++) {
												var collectTypeData = collectTypeDatas[j];
												if (collectTypeData.name == prop.value) {
													propDom.combobox(
															'setValue',
															collectTypeData.id);
													break;
												}
											}
											propDom.combobox('readonly');
										}
										break;
									}
									if (prop.key == 'securityLevel') {
										securityLevel = prop.value;
									}
									if (prop.key == 'snmpVersion') {
										snmpVersion = prop.value;
									}
								}
								// 调整nodeGroupId和domainId的赋值顺序
								props = props.sort(function(a, b) {
									if (a.key == 'nodeGroupId'
											&& b.key == 'domainId') {
										return 1;
									} else if (a.key == 'domainId'
											&& b.key == 'nodeGroupId') {
										return -1;
									} else {
										return 0;
									}
								});
								for (var i = 0; i < props.length; i++) {
									var prop = props[i];
									if (prop.key == 'collectType') {
										continue;
									}
									var propDom = panel.find("input[name='"
											+ prop.key + "']");
									if (propDom.length > 1) {
										propDom.each(function() {
											if ($(this).val() == prop.value) {
												$(this).attr('checked',
														'checked');
												$(this).trigger('click');
											} else {
												$(this).removeAttr('checked');
											}
										});
									} else if (propDom.length > 0) {
										if (propDom.parent().is(".combo")) {
											propDom = propDom.parent().parent()
													.find('select');
											propDom.combobox('setValue',
													prop.value);
										} else {
											propDom.val(prop.value);
										}
									}
								}
								if (snmpVersion == 3 || snmpVersion1 == 3) {
									that.showAndHideObj(jsonData, panel,
											securityLevel, securityLevel1, 0);
								} else {
									that.showAndHideObj(jsonData, panel,
											snmpVersion, snmpVersion1, 1);
								}

							}
						}
					});
		},
		showAndHideObj : function(jsonData, panel, securityLevel,
				securityLevel1, bl) {
			var hideGroup = new Array();
			var showGroup = new Array();
			var isOthers = false;
			// 根据下拉框选中值隐藏或显示对应信息
			for ( var pluginId in jsonData) {
				var plugin = jsonData[pluginId];
				for (var i = 0; i < plugin.length; i++) {
					if (plugin[i].boxStyle == 'OptionBox'
							|| plugin[i].boxStyle == 'RadioBox') {
						var supportValues = plugin[i].supportValues;
						for (var k = 0; k < supportValues.length; k++) {
							if (supportValues[k].value == securityLevel
									|| supportValues[k].value == securityLevel1) {
								hideGroup = supportValues[k].hideGroups ? supportValues[k].hideGroups
										: null;
								showGroup = supportValues[k].showGroups ? supportValues[k].showGroups
										: null;

							}
						}

					}
				}
			}

			if (bl == 0) {// v3发现方式
				if (securityLevel == 1 || securityLevel1 == 1) {// 级别1
					hideGroup = [ "v3groupAuth", "v3groupPriv" ];
				} else if (securityLevel == 2 || securityLevel1 == 2) {
					hideGroup = [ "v3groupPriv" ];
					showGroup = [ "v3groupAuth" ];
				} else if (securityLevel == 3 || securityLevel1 == 3) {
					showGroup = [ "v3groupAuth", "v3groupPriv" ];
				}
			}
			if (hideGroup != null) {
				for (var i = 0; i < hideGroup.length; i++) {
					panel.find("form div[group='" + hideGroup[i] + "']").hide()
							.val('');
				}
			}
			if (showGroup != null) {
				for (var i = 0; i < showGroup.length; i++) {
					panel.find("form")
							.find("div[group='" + showGroup[i] + "']").show();
				}
			}

		},
		saveDiscoverParamter : function(dlg, panel, instanceId) {
			var formDiv = panel.find("form");
			var flag = oc.module.resmanagement.discresource.singlediscparam
					.validateForm(formDiv);
			if (flag) {
				var dataJson = oc.module.resmanagement.discresource.singlediscparam
						.constructDiscoverParamter(formDiv);
				oc.util
						.ajax({
							url : oc.resource
									.getUrl("portal/resource/discoverResource/updateDiscoverParamter.htm"),
							data : {
								"jsonData" : dataJson,
								"instanceId" : instanceId
							},
							timeout : null,
							success : function(json) {
								if (json.code == 200) {
									if (json.data == 1) {
										alert('更新成功');
										dlg.dialog('close');
									} else if (json.data == 2) {
										alert('发现参数不正确');
									} else {
										alert('更新失败');
									}
								}
							}
						});
			}
		},
		reDiscover : function(dlg, panel, instanceId) {
			var formDiv = panel.find("form");
			var flag = oc.module.resmanagement.discresource.singlediscparam
					.validateForm(formDiv);
			if (flag) {
				var dataJson = oc.module.resmanagement.discresource.singlediscparam
						.constructDiscoverParamter(formDiv);
				oc.util
						.ajax({
							url : oc.resource.getUrl("portal/resource/discoverResource/refreshDiscover.htm"),
							// .getUrl("portal/resource/discoverResource/reDiscover.htm"),
							data : {
								"jsonData" : dataJson,
								"instanceId" : instanceId
							},
							timeout : null,
							success : function(json) {
								if (json.code == 200) {
									if (json.data.result == 1) {
										alert('重新发现成功');
										var reg = $('<div/>');
										reg.dialog({
											href : oc.resource.getUrl('resource/module/resource-management/discresource/disresource_refresh_success.html'),
											title : '发现结果',
											// width:550,
											// height : 500,
											resizable : false,
											cache : false,
											onLoad : function() {
												$(".panel-tool-close",$(this).parent()).hide();
												oc.module.resmanagement.discresource.refreshdiscresource.open({
													data : json.data
												});
												dlg.dialog('close');
											},
											buttons : [ {
												text : '完成',
												handler : function() {
													oc.module.resmanagement.discresource.refreshdiscresource.refreshDis({
														reg : this.reg
													});
													reg.dialog('destroy');
												}
											} /*
												 * ,{ text : '取消',
												 * handler : function(){
												 * oc.module.resmanagement.discresource.refreshdiscresource.refreshDis({reg:this.reg});
												 * reg.dialog('destroy'); } }
												 */
											]
										});
									} else if (json.data.result == 2) {
										alert('刷新资源失败');
									} else {
										alert('重新发现失败');
									}
								}
							}
						});
			}
		},
		testDiscover : function(dlg, panel, instanceId) {
			var formDiv = panel.find("form");
			var flag = oc.module.resmanagement.discresource.singlediscparam
					.validateForm(formDiv);
			if (flag) {
				var dataJson = oc.module.resmanagement.discresource.singlediscparam
						.constructDiscoverParamter(formDiv);
				oc.util
						.ajax({
							url : oc.resource
									.getUrl("portal/resource/discoverResource/testDiscover.htm"),
							data : {
								"jsonData" : dataJson,
								"instanceId" : instanceId
							},
							timeout : null,
							success : function(json) {
								if (json.code == 200) {
									if (json.data == 1) {
										alert('测试连接成功');
									} else if (json.data == 0) {
										alert('测试连接失败');
									} else {
										var content = oc.local.module.resource.discovery['code_'
												+ json.data];
										alert('测试连接失败，'
												+ (content == undefined ? ('失败代码:' + json.data)
														: content));
									}
								}
							}
						});
			}
		},
		modifyGridCfg : function(queryData, titleInfo, modifyResourceNumber) {
			this.isCustomResGroupInput.val(modifyResourceNumber);
			this.isCustomResGroupInputUnmonitor.val(modifyResourceNumber);

			this.showOrHideRemoveCustomGroupBtn();
			this.removeStatusLightActive();
			if (queryData) {
				// 如果是自定义资源组并且资源组没有资源
				if (modifyResourceNumber == 0
						&& (queryData.resourceId == null
								|| queryData.resourceId == undefined || queryData.resourceId == "")) {
					this.resourceInput.val("");
					this.resourceInputUnmonitor.val("");
				}
				// 判断是否为标准服务
				if (queryData.categoryIds == 'StandardService'
						|| queryData.parentCategoryId == 'StandardService') {
					this.ifStandardService = true;
				} else if (queryData.categoryIds == 'SnmpOthers'
						|| queryData.parentCategoryId == 'SnmpOthers') {
					this.ifStandardService = true;
				} else if (queryData.categoryIds == 'Storage'
						|| queryData.parentCategoryId == 'Storage') {// 存储去掉cpu和内存利用率
					this.ifStandardService = true;
				} else {
					this.ifStandardService = false;
				}
				// 为queryForm赋值查询条件
				if (queryData.categoryId != null
						&& queryData.categoryId != undefined
						&& queryData.categoryId.trim() != "") {
					this.categoryInput.val(queryData.categoryId.trim());
					this.categoryInputUnmonitor
							.val(queryData.categoryId.trim());

					this.categorysInput.val('');
					this.categorysInputUnmonitor.val('');

					this.resourceInput.val('');
					this.resourceInputUnmonitor.val('');
				} else if (queryData.resourceId != null
						&& queryData.resourceId != undefined
						&& queryData.resourceId.trim() != "") {
					this.categoryInput.val('');
					this.categoryInputUnmonitor.val('');

					this.resourceInput.val(queryData.resourceId.trim());
					this.resourceInputUnmonitor
							.val(queryData.resourceId.trim());

					this.categorysInput.val('');
					this.categorysInputUnmonitor.val('');

					this.curResourceIDs = queryData.resourceId.trim()
							.split(',');
					if (this.dataGrid1) {
						this.dataGrid1.cfg.delCfg.data.id = titleInfo.id;
					}
					if (this.dataGrid2) {
						this.dataGrid2.cfg.delCfg.data.id = titleInfo.id;
					}
				} else if (queryData.categoryIds != null
						&& queryData.categoryIds != undefined
						&& queryData.categoryIds.trim() != "") {
					this.categoryInput.val('');
					this.categoryInputUnmonitor.val('');

					this.categorysInput.val(queryData.categoryIds.trim());
					this.categorysInputUnmonitor.val(queryData.categoryIds
							.trim());

					this.resourceInput.val('');
					this.resourceInputUnmonitor.val('');
				}
			}
		},
		showOrHideRemoveCustomGroupBtn : function() {
			if (this.isCustomResGroupInput.val() == 0) {
				$('#notMonitorAddResGroupBtn').hide();
				$('#monitorAddResGroupBtn').hide();
				$("#" + this.groupDelCfg.id).show();
				$("#" + this.showDelCfg.id).show();
			} else {
				$('#notMonitorAddResGroupBtn').show();
				$('#monitorAddResGroupBtn').show();
				$("#" + this.groupDelCfg.id).hide();
				$("#" + this.showDelCfg.id).hide();
			}
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
		deleteResurceButtonCheckMethod : function() {
			var that = this;
			// 自定义资源组不显示,删除资源按钮
			if (that.isCustomResGroupCheck()) {
				// alert('切换');
				that.deleteResurceButtonTurn('none');
			} else {
				// alert('恢复');
				that.deleteResurceButtonTurn('show');
			}
		},
		emptyDataGrid : function() {
			this.dataGrid1.selector.datagrid('loadData', {
				"data" : {
					"startRow" : 0,
					"rowCount" : 10,
					"totalRecord" : 0,
					"total" : 1,
					"rows" : []
				},
				"code" : 200
			});
			this.dataGrid2.selector.datagrid('loadData', {
				"data" : {
					"startRow" : 0,
					"rowCount" : 10,
					"totalRecord" : 0,
					"total" : 1,
					"rows" : []
				},
				"code" : 200
			});
		},
		ipOrNameFixPlaceHolder : function(){
		  $('#show_resource_instance_unmonitor_form').find('#iPorNameId').fixPlaceHolder();
		  $('#show_resource_instance_form').find('#iPorNameId').fixPlaceHolder();
		}
	};

	// 对外提供入口方法
	oc.ns('oc.resourced.manager.list');
	var resourceList = undefined;
	oc.resourced.manager.list = {
		removeGroupDataGrid : function(modifyResourceNumber) {
			// 后台查询自定义资源组没有resourceId直接返回空
			resourceList.isCustomResGroupInput.val('0');
			resourceList.isCustomResGroupInputUnmonitor.val('0');

			resourceList.resourceInput.val('');
			resourceList.resourceInputUnmonitor.val('');
           
			resourceList.loadAll();
			resourceList.ipOrNameFixPlaceHolder();
		},
		reloadGridData : function(queryData, titleInfo, modifyResourceNumber) {
			resourceList.queryForm.reset();
			resourceList.queryFormUnmonitor.reset();

			resourceList.modifyGridCfg(queryData, titleInfo,
					modifyResourceNumber);
			resourceList.loadAll();
			// 自定义资源组不显示,删除资源按钮
			resourceList.deleteResurceButtonCheckMethod();
			resourceList.ipOrNameFixPlaceHolder();
		},
		open : function(queryData, titleInfo, modifyResourceNumber) {
			resourceList = new resourceGrid(titleInfo);
			resourceList.modifyGridCfg(queryData, titleInfo,
					modifyResourceNumber);
			resourceList.open();
			// 自定义资源组不显示,删除资源按钮
			resourceList.deleteResurceButtonCheckMethod();
			resourceList.ipOrNameFixPlaceHolder();
		},
		reLoadList : function() {
			resourceList.loadAll();
			resourceList.ipOrNameFixPlaceHolder();
		}
	};
});
