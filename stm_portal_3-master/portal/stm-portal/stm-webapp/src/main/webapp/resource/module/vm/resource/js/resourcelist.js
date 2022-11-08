$(function(){
	var quanGrid=null;
	function resourceList(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this.init();
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
					quanGrid.monitorDatagrid.selector.datagrid("reload");
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
		scanContent = $("<div><div><div class='oc-toolbar datagrid-toolbar insert-btn'><input id='queryManagerUserInput' style='margin-right:40px;width:200px;' type='text' placeholder='输入用户姓名'/>" +
				"<button id='searchManagerUserByName'><span class='l-btn-icon icon-search poas_re' style='top:5px;'></span>查询</button>" +
				"</div><div style='height:235px'><div id='resibilityGrid'/></div></div>");
		dialogTitle = '选择责任人';
		// 构建dialog
		addWindow.dialog({
			title : dialogTitle,
			width:410,
			height:340,
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
											quanGrid.monitorDatagrid.selector.datagrid("reload");
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
						iconCls : "fa fa-times-circle",
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
	
	resourceList.prototype = {
		constructor : resourceList,
		_defaults : {},
		cfg : undefined,
		currentVmType : undefined,
		currentModule : undefined,
		accordion : undefined,
		tabsDiv : undefined,
		freshBtn : undefined,
		monitorForm : undefined,
		unMonitorForm : undefined,
		vmDatagridToolBar : undefined,
		monitorDatagrid : undefined,
		unMonitorDatagrid : undefined,
		resourceStatus : undefined,
		categoryCountList : undefined,
		monitorDataGridLoadFlag : undefined,
		unMonitorDataGridLoadFlag : undefined,
		init : function(){
			this.currentVmType = this.cfg.vmType;
			this.currentModule = this.cfg.module;
			this.accordion = this.cfg.accordion;
			
			var id = oc.util.generateId();
			this.tabsDiv = $("#vmResourceList").attr('id', id);
			this.vmDatagridToolBar = this.tabsDiv.find(".vmDatagridToolBar");
			this.addFreshBtn();
			this.createResourceStatusLight();
			this.createMonitorForm();
			this.createUnMonitorForm();
			this.createMonitorDatagrid();
			this.createUnMonitorDatagrid();
//			this.collapse_btn();
		},
//		collapse_btn: function(){
//			//左侧菜单折叠按钮
//			$("#collapse_btn").click(function(){
//				if($(this).hasClass('fa-angle-double-left')){
//					var gs = $('.oc_vm_index');
//					var gt = $('.oc_vm_index').layout('panel','west');
//					$('.oc_vm_index').layout('collapse','west');
//					$('.oc_vm_index').layout('panel','center').parent().css('left','0px');
//					$(this).removeClass('fa-angle-double-left');
//					$(this).addClass('fa-angle-double-right');
//				}else{
//					$('.oc_vm_index').layout('expand','west');
//					$(this).removeClass('fa-angle-double-right');
//					$(this).addClass('fa-angle-double-left');
//				}
//			});
//		},
		addFreshBtn : function(){
			var that = this;
			//tabs头上加一个刷新按钮
			var tabsWrap = this.tabsDiv.find(".tabs-header > .tabs-wrap");
			var tabs = tabsWrap.find(".tabs").css("float", "left");
			this.freshBtn = $("<span/>").css({
				height : '33px',
				float : "right",
				"margin-right" : "10px"
			}).append($("<span>").addClass('fa fa-refresh').css("margin-top", "9px").attr("title", "刷新"));
			tabsWrap.append(this.freshBtn);
			this.freshBtn.find("span").on('click', function(){
				that.updateWestGroupInfo();
				that.loadAll();
			});
		},
		createMonitorForm : function(){
			var that = this;
			var domains = oc.index.getDomains();
			this.monitorForm = oc.ui.form({
				selector : $("#vm_monitor_form").attr('id', oc.util.generateId()),
				combobox : [ {
					selector : '[name=domainId]',
					data : domains,
					width : 90,
					placeholder : '${oc.local.ui.select.placeholder}'
				} ]
			});
			
			this.monitorForm.selector.find("[name='categoryId']").val(this.currentVmType);
			
			this.monitorForm.selector.find(".queryBtn").linkbutton('RenderLB',{
				iconCls:'icon-search',
				onClick : function(){
					that.monitorDatagrid.load();
				}
			});
			this.monitorForm.selector.find(".resetBtn").linkbutton('RenderLB',{
				iconCls:'icon-reset',
				onClick : function(){
					that.removeStatusLightActive();
					that.monitorForm.reset();
				}
			});
			
			this.resourceStatus = this.monitorForm.selector.find("[name='resourceStatus']");
		},
		createUnMonitorForm : function(){
			var that = this;
			var domains = oc.index.getDomains();
			this.unMonitorForm = oc.ui.form({
				selector : $("#vm_unMonitor_form").attr('id', oc.util.generateId()),
				combobox : [ {
					selector : '[name=domainId]',
					data : domains,
					width : 90,
					placeholder : '${oc.local.ui.select.placeholder}'
				} ]
			});

			this.unMonitorForm.selector.find("[name='categoryId']").val(this.currentVmType);
			
			this.unMonitorForm.selector.find(".queryBtn").linkbutton('RenderLB',{
				iconCls:'icon-search',
				onClick : function(){
					that.unMonitorDatagrid.load();
				}
			});
			this.unMonitorForm.selector.find(".resetBtn").linkbutton('RenderLB',{
				iconCls:'icon-reset',
				onClick : function(){
					that.unMonitorForm.reset();
				}
			});
		},
		createResourceStatusLight : function(){
			var that = this;
			this.vmDatagridToolBar.find('.light').each(function(i){
				var light = $(this);
				if (light.hasClass('all')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('all');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('available')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('available');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('notavailable')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('notavailable');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('resunknown')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('resunknown');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('critical')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('critical');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('serious')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('serious');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('warn')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('warn');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('normal')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('normal');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('unknown')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('unknown');
						that.monitorDatagrid.load();
					});
				}
			});
		},
		removeStatusLightActive : function(){
			this.vmDatagridToolBar.find('.light span').removeClass("active");
		},
		addStatusLightActive : function(light){
			$(light).parent().parent().find("span").removeClass('active');
			$(light).find("span").addClass('active');
		},
		createMonitorDatagrid : function(){
			quanGrid=this;
			var that = this;
			// 根据vm类型不同显示和隐藏不同的列，并且调整不同列的宽度
			var showNameWidth ='25%',sourceNameWidth = '12%', cpuRateWidth = '15%', memRateWidth = '15%', allCpuWidth = '9%',
				allMemWidth = '7%', datastoreRateWidth = '16%', allDatastoreWidth = '7%', freeDatastoreWidth = '10%',
				exsiTotalWidth = '8%', vmTotalWidth = '8%',vmTotalPoolWidth = '25%',clusterWidth = '10%', vmOSWidth = '11%',
				exsiWidth = '10%', dataCenterWidth = '7%', vCenterWidth = '10%', cpuPercentWidth = '12%', memPercentWidth = '12%',
				addressWidth = '10%', physicalSizeWidth = '10%', physicalUtilisationWidth = '10%', physicalRateWidth = '15%',
				runtimeWidth = '10%', typeWidth = '10%',cpRateWidth='8%', ipWidth='8%',liablePersonWidth='6%',
				cpuNumWidth = '20%', memSizeWidth = '20%', regeionWidth = '20%',
				projectNameWidth = '10%', createTimeWidth = '10%', configWidth = '10%';
			
			var showNameHidden = true , sourceNameHidden = false, cpuRateHidden = true, memRateHidden = true, allCpuHidden = false,
				allMemHidden = false, datastoreRateHidden = true, allDatastoreHidden = false, freeDatastoreHidden = true,
				exsiTotalHidden = false, vmTotalHidden = false, clusterHidden = true, vmOSHidden = true,
				exsiHidden = true, dataCenterHidden = false, vCenterHidden = false, cpuPercentHidden = false, memPercentHidden = false,
				addressHidden = true, physicalSizeHidden = true, physicalUtilisationHidden = true, physicalRateHidden = true,
				runtimeHidden = true, typeHidden = true,cpRateHidden=false,ipHidden=true,vmTotalPoolHidden = true,liablePersonHidden=true,
				cpuNumHidden = true, memSizeHidden = true, regeionHidden = true,
				projectNameHidden = true, createTimeHidden = true, configHidden = true;
				
			
			if(this.currentVmType == 'VirtualHost'){
				sourceNameWidth = '15%', cpuRateWidth = '15%', memRateWidth = '15%', vmTotalWidth = '10%',
				clusterWidth = '10%', dataCenterWidth = '15%', vCenterWidth = '10%';
				cpuRateHidden = false, memRateHidden = false, allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true,
				exsiTotalHidden = true, clusterHidden = false, cpuPercentHidden = true, memPercentHidden = true,cpRateHidden=true,liablePersonHidden=false;
			} else if(this.currentVmType == 'VirtualStorage'){
				sourceNameWidth = '15%', allDatastoreWidth = '15%', exsiTotalWidth = '10%', vmTotalWidth = '10%',
				dataCenterWidth = '10%', vCenterWidth = '10%';
				cpuRateHidden = true, memRateHidden = true, allCpuHidden = true, allMemHidden = true,
				datastoreRateHidden = false, freeDatastoreHidden = false, cpuPercentHidden = true, memPercentHidden = true,cpRateHidden=true;
			} else if(this.currentVmType == 'ResourcePool'){
				vmTotalPoolWidth = '35%', exsiWidth = '35%';//clusterWidth = '25%',
				showNameHidden = false ,sourceNameHidden = true,cpuRateHidden = true, memRateHidden = true, allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true,
				exsiTotalHidden = true,vmTotalHidden = true, vmTotalPoolHidden = false, vmOSHidden = true, exsiHidden = true, cpuPercentHidden = true,
				memPercentHidden = true,cpRateHidden=true,ipHidden=true,exsiHidden = false,clusterHidden = true,dataCenterHidden = true, vCenterHidden = true;
			} else if(this.currentVmType == 'VirtualVM'){
				sourceNameWidth = '15%', cpuRateWidth = '14%', memRateWidth = '14%', vmOSWidth = '11%', dataCenterWidth = '10%',
				vCenterWidth = '10%';
				cpuRateHidden = false, memRateHidden = false, allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true,
				exsiTotalHidden = true, vmTotalHidden = true, vmOSHidden = false, exsiHidden = false, cpuPercentHidden = true,
				memPercentHidden = true,cpRateHidden=true,ipHidden=false,liablePersonHidden=false;
			} else if(this.currentVmType == 'XenHosts'){
				sourceNameWidth = '27%', addressWidth = '20%', cpuRateWidth = '17%', memRateWidth = '15%', runtimeWidth = '10%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = false
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = false,cpRateHidden=true,liablePersonHidden=false;
			} else if(this.currentVmType == 'KvmHosts'){
				sourceNameWidth = '40%', cpuRateWidth = '25%', memRateWidth = '25%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = true
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = true,cpRateHidden=true,liablePersonHidden=false;
			} else if(this.currentVmType == 'XenVMs'){
				sourceNameWidth = '34%', cpuRateWidth = '17%', memRateWidth = '17%', runtimeWidth = '15%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true,
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = false,cpRateHidden=true,ipHidden=false,liablePersonHidden=false;
			} else if(this.currentVmType == 'KvmVMs'){
				sourceNameWidth = '40%', cpuRateWidth = '20%', memRateWidth = '20%',liablePersonWidth='15%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true,
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = true,cpRateHidden=true,ipHidden=true,liablePersonHidden=false;
			} else if(this.currentVmType == 'XenSRs'){
				sourceNameWidth = '25%', physicalRateWidth = '20%', physicalSizeWidth = '15%', physicalUtilisationWidth = '15%',  typeWidth = '10%',
				addressWidth = '10%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = false,
				physicalSizeHidden = false, physicalUtilisationHidden = false, physicalRateHidden = false, typeHidden = false,cpRateHidden=true;
			} else if(this.currentVmType == 'KvmDataStores'){
				sourceNameWidth = '35%', physicalRateWidth = '20%', physicalSizeWidth = '20%', physicalUtilisationWidth = '20%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = true,
				physicalSizeHidden = false, physicalUtilisationHidden = false, physicalRateHidden = false, typeHidden = true,cpRateHidden=true;
			} else if(this.currentVmType == 'FusionComputeHosts'){
				sourceNameWidth = '27%', addressWidth = '20%', cpuRateWidth = '17%', memRateWidth = '20%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = false
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = true,cpRateHidden=true,liablePersonHidden=false;
			} else if(this.currentVmType == 'FusionComputeVMs'){
				sourceNameWidth = '34%', cpuRateWidth = '17%', memRateWidth = '17%',ipWidth='15%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true,
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = true,cpRateHidden=true,ipHidden=false,liablePersonHidden=false;
			} else if(this.currentVmType == 'FusionComputeDataStores'){
				sourceNameWidth = '30%', physicalRateWidth = '25%', physicalSizeWidth = '15%', physicalUtilisationWidth = '15%',  typeWidth = '10%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = false,
				physicalSizeHidden = false, physicalUtilisationHidden = false, physicalRateHidden = false, addressHidden = true, typeHidden = false,cpRateHidden=true;
			} else if(this.currentVmType == 'FusionComputeClusters'){
				sourceNameWidth = '30%',cpuRateWidth = '30%', memRateWidth = '35%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, addressHidden = true
				cpuRateHidden = false, memRateHidden = false, runtimeHidden = true,cpRateHidden=true;
			} else if(this.currentVmType == 'DTCenterECSs'){
				sourceNameWidth = '30%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, cpRateHidden=true,
				ipHidden=false, cpuNumHidden = false, memSizeHidden = false, regeionHidden = false;
			} else if(this.currentVmType == 'KyLinVms'){
				sourceNameWidth = '30%', projectNameWidth = '20%',configWidth = '20%', createTimeWidth = '30%';
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, cpuPercentHidden = true, memPercentHidden = true, cpRateHidden=true,
				ipHidden=true, projectNameHidden = false, configHidden = false, createTimeHidden = false;
			}
			
			var isDTCenterECSs = this.currentVmType == 'DTCenterECSs';
			var isKyLinVms = this.currentVmType == 'KyLinVms';
			
			// 是否为虚拟化模块
			var user = oc.index.getUser();
			var octoolbarRight = [];
			if(user.domainUser || user.systemUser){
				//资源池特殊处理
				if(that.currentVmType && "ResourcePool"==that.currentVmType){
					octoolbarRight = [{
						iconCls: 'fa fa-trash-o',
						text:"删除资源",
						onClick: function(){
							that.deleteResource(that.monitorDatagrid);
						}
					}].concat(octoolbarRight);
				}
				else if(that.currentVmType && ("VirtualHost"==that.currentVmType 
						|| "VirtualVM"==that.currentVmType 
						|| "XenHosts"==that.currentVmType 
						|| "XenVMs"==that.currentVmType 
						|| "FusionComputeHosts"==that.currentVmType 
						|| "FusionComputeVMs"==that.currentVmType
						|| "DTCenterECSs"==that.currentVmType
						|| "KyLinServers"==that.currentVmType
						|| "KyLinVms"==that.currentVmType
						|| "KvmHosts"==that.currentVmType 
						|| "KvmVMs"==that.currentVmType)){
					octoolbarRight = [{
						iconCls: 'fa fa-trash-o',
						text:"删除资源",
						onClick: function(){
							that.deleteResource(that.monitorDatagrid);
						}
					}, {
						iconCls: 'fa fa-times-circle',
						text:"取消监控",
						onClick: function(){
							that.cancelMonitor();
						}
					},{
						id : "moreOpBtnVm",
						iconCls: 'fa fa-th-large',
						text:"更多操作",
						onClick: function(e){
							that.moreOperation(e);
						}						
					}].concat(octoolbarRight);				
				}
				else{
					octoolbarRight = [{
						iconCls: 'fa fa-trash-o',
						text:"删除资源",
						onClick: function(){
							that.deleteResource(that.monitorDatagrid);
						}
					}, {
						iconCls: 'fa fa-times-circle',
						text:"取消监控",
						onClick: function(){
							that.cancelMonitor();
						}
					}].concat(octoolbarRight);
				}
				
				
				// 如果是资源管理 加入资源发现按钮
				if(this.currentModule == 'resourceModule'){
					octoolbarRight = [{
						iconCls: 'fa fa-eye',
						text:"资源发现",
						onClick: function(){
							oc.resource.loadScript('resource/module/resource-management/discresource/js/discresource.js',function(){
								oc.module.resmanagement.discresource.disc.open({
									resourceListObject : that
								});
							});
						}
					}].concat(octoolbarRight);
				}
			}
			// 如果是资源管理  加入移入资源组按钮
			if(this.currentModule == 'resourceModule'){
				octoolbarRight.push({
					iconCls: 'icon-movein',
					text:"移入资源组",
					onClick: function(){
						that.moveInResourceGroup(this.monitorDatagrid );
					}
				});
			}
			// 初始化列表
			this.monitorDatagrid = oc.ui.datagrid({
				selector : this.tabsDiv.find(".monitorDatagrid"),
				pageSize:$.cookie('pageSize_vm_resourcelist')==null ? 15 : $.cookie('pageSize_vm_resourcelist'),
				fitColumns : false,
				hideReset:true,
				hideSearch:true,
				queryForm : this.monitorForm,
				url : oc.resource.getUrl('portal/vm/vmResourceList/getMonitorList.htm'),
				columns : [[{
					field:'id',
					checkbox:true
				}, {
					field:'showName',
					title:'资源名称',
					sortable:true,
					width:showNameWidth,
					hidden:showNameHidden,
					formatter:function(value, row, rowIndex){ 
						// 加入手形样式
						var statusLabel = $("<label/>").addClass("oc-pointer-operate resourcePoolShowVM");
						if(value != null){
							statusLabel.attr('title', value).attr('uuid',row.uuid).html(value);
					 	}
						return statusLabel;
					}
				}, {
					field:'sourceName',
					title:isDTCenterECSs ? '实例ID/实例名称' : '资源名称',
					sortable:true,
					width:sourceNameWidth,
					hidden:sourceNameHidden,
					formatter:function(value, row, rowIndex){ 
						// 加入手形样式
						var statusLabel = $("<label/>").addClass("light-ico_resource " + row.resourceStatus + " oc-pointer-operate");
						statusLabel.attr('rowIndex', rowIndex).addClass('quickSelectDetailInfo');
						if(row.categoryId == 'DTCenterECSs'){
							valueAtEcs = row.uuid + '/' + value;
							if(value != null){
								statusLabel.attr('title', valueAtEcs).html(valueAtEcs.htmlspecialchars());
						 	}
							return statusLabel;
						} else {
							if(value != null){
								statusLabel.attr('title', value).html(value.htmlspecialchars());
						 	}
							return statusLabel;
						}
					}
				}, 
				{
					field:'projectName',
					title:'项目名称',
					sortable:false,
					width:projectNameWidth,
					hidden:projectNameHidden,
					ellipsis:true
				},
				{
					field:'ip',
					title:'IP地址',
					sortable:true,
					width:ipWidth,
					hidden:ipHidden
				}, {
					field:'address',
					title:'地址',
					sortable:true,
					width:addressWidth,
					hidden:addressHidden
				}, {
					field:'memSize',
					title:'内存容量',
					sortable:false,
					width:memSizeWidth,
					hidden:memSizeHidden,
					align:'center',
				}, {
					field:'cpuNum',
					title:'CPU内核数',
					sortable:false,
					width:cpuNumWidth,
					hidden:cpuNumHidden,
					align:'center',
				}, {
					field:'cpuRate',
					title:'CPU利用率',
					sortable:true,
					width:cpuRateWidth,
					hidden:cpuRateHidden,
					formatter:function(value,row,rowIndex){
						
						if(row.cpuRateIsAlarm == false){
							row.cpuRateState = 'green';
						}
						var cpuRate = row.cpuRate;
						if(cpuRate == null || cpuRate == 'N/A'){
							return 'N/A';
						}else if(cpuRate == '--'){
							return '--';
						}
						var cpuRateState = row.cpuRateState;
						// 主资源为致命或未知则指标显示为灰色
						var resourceStatus = row.resourceStatus;
						if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==cpuRateState){
							return '--';
							// cpuRateState = 'gray';
						}
						return '<div class="rate rate-t-'+cpuRateState+'"><span class="rate-'+cpuRateState+'" style="width:'+cpuRate+'"></span></div><span class="rt">'+cpuRate+'</span>';
					}
				}, {
					field:'cpuPercent',
					title:'CPU百分比',
					sortable:true,
					width:cpuPercentWidth,
					hidden:cpuPercentHidden,
					formatter:function(value,row,rowIndex){
						if(row.cpuPercentIsAlarm == false){
							row.cpuPercentState = 'green';
						}
						var cpuPercent = row.cpuPercent;
						if(cpuPercent == null || cpuPercent == 'N/A'){
							return 'N/A';
						}else if(cpuPercent == '--'){
							return '--';
						}
						var cpuPercentState = row.cpuPercentState;
						// 主资源为致命或未知则指标显示为灰色
						var resourceStatus = row.resourceStatus;
						if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==cpuPercentState){
							return '--';
							// cpuPercentState = 'gray';
						}
						return '<div class="rate rate-t-'+cpuPercentState+'"><span class="rate-'+cpuPercentState+'" style="width:'+cpuPercent+'"></span></div><span class="rt">'+cpuPercent+'</span>';
					}
				}, {
					field:'memPercent',
					title:'内存百分比',
					sortable:true,
					width:memPercentWidth,
					hidden:memPercentHidden,
					formatter:function(value,row,rowIndex){
						if(row.memPercentIsAlarm == false){
							row.memPercentState = 'green';
						}
						var memPercent = row.memPercent;
						if(memPercent == null || memPercent == 'N/A'){
							return 'N/A';
						}else if(memPercent == '--'){
							return '--';
						}
						var memPercentState = row.memPercentState;
						// 主资源为致命或未知则指标显示为灰色
						var resourceStatus = row.resourceStatus;
						if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==memPercentState){
							return '--';
							// memPercentState = 'gray';
						}
						return '<div class="rate rate-t-'+memPercentState+'"><span class="rate-'+memPercentState+'" style="width:'+memPercent+'"></span></div><span class="rt">'+memPercent+'</span>';
					}
				}
				,{
				field:'memUsedRate',
					title:'磁盘使用率',
					sortable:true,
					width:cpRateWidth,
					hidden:cpRateHidden,
					formatter:function(value,row,rowIndex){
						return '<span class="rt">'+row.memUsedRate+'</span>';
						
					}
				}
				, {
					field:'memRate',
					title:'内存利用率',
					sortable:true,
					width:memRateWidth,
					hidden:memRateHidden,
					formatter:function(value,row,rowIndex){
						if(row.memRateIsAlarm == false){
							row.memRateState = 'green';
						}
						var memRate = row.memRate;
						if(memRate == null || memRate == 'N/A'){
							return 'N/A';
						}else if(memRate == '--'){
							return '--';
						}
						var memRateState = row.memRateState;
						// 主资源为致命或未知则指标显示为灰色
						var resourceStatus = row.resourceStatus;
						if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==memRateState){
							return '--';
							// memRateState = 'gray';
						}
						return '<div class="rate rate-t-'+memRateState+'"><span class="rate-'+memRateState+'" style="width:'+memRate+'"></span></div><span class="rt">'+memRate+'</span>';
					}
				}, {
					field:'regeion',
					title:'区域',
					sortable:false,
					width:regeionWidth,
					hidden:regeionHidden,
				}, {
					field:'allCpu',
					title:'总CPU资源',
					sortable:true,
					width:allCpuWidth,
					hidden:allCpuHidden
				}, {
					field:'allMem',
					title:'总内存',
					sortable:true,
					width:allMemWidth,
					hidden:allMemHidden
				}, {
					field:'datastoreRate',
					title:'空间使用情况',
					sortable:true,
					width:datastoreRateWidth,
					hidden:datastoreRateHidden,
					formatter:function(value,row,rowIndex){
						if(row.datastoreRateIsAlarm == false){
							row.datastoreRateState = 'green';
						}
						var datastoreRate = row.datastoreRate;
						if(datastoreRate == null || datastoreRate == 'N/A'){
							return 'N/A';
						}else if(datastoreRate == '--'){
							return '--';
						}
						var datastoreRateState = row.datastoreRateState;
						// 主资源为致命或未知则指标显示为灰色
						var resourceStatus = row.resourceStatus;
						if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==datastoreRateState){
							return '--';
							// datastoreRateState = 'gray';
						}
						return '<div class="rate rate-t-'+datastoreRateState+'"><span class="rate-'+datastoreRateState+'" style="width:'+datastoreRate+'"></span></div><span class="rt">'+datastoreRate+'</span>';
					}
				}, {
					field:'physicalRate',
					title:'空间利用率',
					sortable:true,
					width:physicalRateWidth,
					hidden:physicalRateHidden,
					formatter:function(value,row,rowIndex){
						if(row.physicalRateIsAlarm == false){
							row.physicalRateState = 'green';
						}
						var physicalRate = row.physicalRate;
						if(physicalRate == null || physicalRate == 'N/A'){
							return 'N/A';
						}else if(physicalRate == '--'){
							return '--';
						}
						var physicalRateState = row.physicalRateState;
						// 主资源为致命或未知则指标显示为灰色
						var resourceStatus = row.resourceStatus;
						if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==physicalRateState){
							return '--';
							// physicalRateState = 'gray';
						}
						return '<div class="rate rate-t-'+physicalRateState+'"><span class="rate-'+physicalRateState+'" style="width:'+physicalRate+'"></span></div><span class="rt">'+physicalRate+'</span>';
					}
				}, {
					field:'allDatastore',
					title:'总存储',
					sortable:true,
					width:allDatastoreWidth,
					hidden:allDatastoreHidden
				}, {
					field:'freeDatastore',
					title:'可用空间',
					sortable:true,
					width:freeDatastoreWidth,
					hidden:freeDatastoreHidden
				}, {
					field:'physicalSize',
					title:'总空间',
					sortable:true,
					width:physicalSizeWidth,
					hidden:physicalSizeHidden
				}, {
					field:'physicalUtilisation',
					title:'已用空间',
					sortable:true,
					width:physicalUtilisationWidth,
					hidden:physicalUtilisationHidden
				}, {
					field:'exsiTotal',
					title:'主机数',
					sortable:true,
					width:exsiTotalWidth,
					hidden:exsiTotalHidden
				}, {
					field:'vmTotal',
					title:'虚拟机数',
					sortable:true,
					width:vmTotalWidth,
					hidden:vmTotalHidden
				}, {
					field:'vmTotalForPool',
					title:'虚拟机数',
					sortable:true,
					width:vmTotalPoolWidth,
					hidden:vmTotalPoolHidden,
					formatter:function(value, row, rowIndex){ 
						// 加入手形样式
						var statusLabel = $("<label/>").addClass("oc-pointer-operate resourcePoolShowVM");
						if(value != null){
							statusLabel.attr('title', value).attr('uuid',row.uuid).html(value);
					 	}
						return statusLabel;
					}
				}, {
					field:'vmOS',
					title:'客户机操作系统',
					sortable:true,
					width:vmOSWidth,
					hidden:vmOSHidden,
					ellipsis:true
				}, {
					field:'runtime',
					title:'运行时间',
					sortable:true,
					width:runtimeWidth,
					hidden:runtimeHidden,
					ellipsis:true
				}, {
					field:'type',
					title:'存储类型',
					sortable:true,
					width:typeWidth,
					hidden:typeHidden,
					ellipsis:true
				}, {
					field:'exsi',
					title:'主机',
					sortable:true,
					width:exsiWidth,
					hidden:exsiHidden,
					ellipsis:true
				}, {
					field:'cluster',
					title:'Cluster',
					sortable:true,
					width:clusterWidth,
					hidden:clusterHidden,
					ellipsis:true
				}, {
					field:'dataCenter',
					title:'数据中心',
					sortable:true,
					width:dataCenterWidth,
					hidden:dataCenterHidden,
					ellipsis:true
				}, 
				 {
					field:'liablePerson',
					title:'责任人',
					sortable:true,
					width:liablePersonWidth,
					hidden:liablePersonHidden,
					ellipsis:true
				}, 
				{
					field:'vCenter',
					title:'vCenter',
					sortable:true,
					width:vCenterWidth,
					hidden:vCenterHidden,
					ellipsis:true
				},
				{
					field:'config',
					title:'配置',
					sortable:false,
					width:configWidth,
					hidden:configHidden,
					formatter:function(value,row,index){
						//return row.cpuNum + '/' + row.memSize;
						return row.memSize == null ? "" : row.memSize;
					}
				},
				{
					field:'createTime',
					title:'创建时间',
					sortable:false,
					width:createTimeWidth,
					hidden:createTimeHidden,
				}]],
				octoolbar : {
					left : [this.monitorForm.selector],
					right : octoolbarRight
				},
				loadFilter : function(data){
					var myData = data.data;
					that.tabsDiv.find('.tabs-header .tabs-wrap .tabs-title:eq(0)').html('已监控('+myData.totalRecord+')');
					return myData;
				},
				onLoadSuccess : function(data){
					//资源池特殊处理
					if(that.currentVmType && "ResourcePool"==that.currentVmType){
						that.tabsDiv.find('#selectDomainId').hide();
					}
					
					that.monitorDataGridLoadFlag = true;
					that.categoryCountList = data.resourceCategoryBos;
					// 查看资源详情
					that.tabsDiv.find(".quickSelectDetailInfo").on('click', function(e){
		        		 var rowIndex = $(this).attr('rowIndex');
				    	 var row = that.monitorDatagrid.selector.datagrid('getRows')[rowIndex];
				    	 var instanceId = row.id;
		    			 oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
		    				 oc.module.resmanagement.resdeatilinfonew.open({instanceId:instanceId,callback:that,callbackType:'1',type:'1'});
		    			 });
		        		 e.stopPropagation();
		        	 });
					
					that.tabsDiv.find(".resourcePoolShowVM").on('click', function(){
						var thisObj = $(this);
						
						var dlg = $('<div/>').attr('id',"resourcePoolShowVMDiv");
						var from = $('<form/>').attr('id',"resourcePoolShowVMForm").attr('class','oc-form float').html('<div class="form-group">'+
								'名称：<input type="text" name="iPorName" value="" style="width:80px;"></div>'+
								'<div class="form-group">'+
								'<a class="queryBtn">查询</a>'+
							'</div>'+
							'<div class="form-group">'+
							'	<a class="resetBtn">重置</a>'+
							'</div>'+
						'<input type="hidden" name="resourcePoolUuid" value="'+thisObj.attr('uuid')+'" >');
						var datagridDiv = $('<div/>').attr('style','width:100%;height: 550px;').html("<div id='resourcePoolShowVMDatagrid' style='width:100%;height: 100%;'>");
						
						dlg.append(from);
						dlg.append(datagridDiv);
						
						var queryForm=oc.ui.form({selector:dlg.find('#resourcePoolShowVMForm')});
						var toolBarTemp = dlg.find(".oc-toolbar");
						
						dlg.find('#resourcePoolShowVMForm').find(".queryBtn").linkbutton('RenderLB',{
							iconCls:'icon-search',
							onClick : function(){
								datagridPool.load();
							}
						});
						dlg.find('#resourcePoolShowVMForm').find(".resetBtn").linkbutton('RenderLB',{
							iconCls:'icon-reset',
							onClick : function(){
								queryForm.reset();
							}
						});
						
						var datagridPool = oc.ui.datagrid({
				    		selector:dlg.find("#resourcePoolShowVMDatagrid"),
				    		pagination:true,
				    		url:oc.resource.getUrl('portal/vm/vmResourceList/getVMachineMonitorList.htm'),
				    		queryForm:queryForm,
				    		hideReset : true,
							hideSearch : true,
				    		octoolbar:{
								left : [queryForm.selector]
							},
				    		columns:[[{
										field:'sourceName',
										title:'资源名称',
										sortable:true,
										width:25,
										formatter:function(value, row, rowIndex){ 
											// 加入手形样式
											var statusLabel = $("<label/>").addClass("light-ico_resource " + row.resourceStatus + " oc-pointer-operate");
											if(value != null){
												statusLabel.attr('title', value).html(value.htmlspecialchars());
										 	}
											return statusLabel;
										}
									}, {
										field:'ip',
										title:'IP地址',
										sortable:true,
										width:15
									}, {
										field:'cpuRate',
										title:'CPU利用率',
										sortable:true,
										width:20,
										formatter:function(value,row,rowIndex){
											
											if(row.cpuRateIsAlarm == false){
												row.cpuRateState = 'green';
											}
											var cpuRate = row.cpuRate;
											if(cpuRate == null || cpuRate == 'N/A'){
												return 'N/A';
											}else if(cpuRate == '--'){
												return '--';
											}
											var cpuRateState = row.cpuRateState;
											// 主资源为致命或未知则指标显示为灰色
											var resourceStatus = row.resourceStatus;
											if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==cpuRateState){
												return '--';
												// cpuRateState = 'gray';
											}
											return '<div class="rate rate-t-'+cpuRateState+'"><span class="rate-'+cpuRateState+'" style="width:'+cpuRate+'"></span></div><span class="rt">'+cpuRate+'</span>';
										}
									}, {
										field:'memRate',
										title:'内存利用率',
										sortable:true,
										width:20,
										formatter:function(value,row,rowIndex){
											if(row.memRateIsAlarm == false){
												row.memRateState = 'green';
											}
											var memRate = row.memRate;
											if(memRate == null || memRate == 'N/A'){
												return 'N/A';
											}else if(memRate == '--'){
												return '--';
											}
											var memRateState = row.memRateState;
											// 主资源为致命或未知则指标显示为灰色
											var resourceStatus = row.resourceStatus;
											
											if(null==resourceStatus || resourceStatus == 'res_critical' || resourceStatus == 'res_critical_nothing' || resourceStatus == 'res_unknown_nothing' || resourceStatus == 'res_unkown' || null==memRateState){
												return '--';
												// memRateState = 'gray';
											}
											return '<div class="rate rate-t-'+memRateState+'"><span class="rate-'+memRateState+'" style="width:'+memRate+'"></span></div><span class="rt">'+memRate+'</span>';
										}
									}, {
										field:'vmOS',
										title:'客户机操作系统',
										sortable:true,
										width:20,
										ellipsis:true
									}
				    	     ]]
				    	     
				    	});
						
						dlg.dialog({
						    title: "虚拟机列表",
						    width : 900,
							height : 590,
						    onLoad:function(){
						    	
						    }
						});
						
					})
					
					// 取消进度条
					that.disableGlobalProgress();
				}
			});
			
			//cookie记录pagesize
			var paginationObject = this.tabsDiv.find(".monitorDatagrid").datagrid('getPager');
			if(paginationObject){
				paginationObject.pagination({
					onChangePageSize:function(pageSize){
						$.cookie('pageSize_vm_resourcelist',pageSize);
					}
				});
			}
		},
		createUnMonitorDatagrid : function(){
			var that = this;
			var sourceNameWidth = 0.10, allCpuWidth = 0.16, allMemWidth = 0.1, allDatastoreWidth = 0.1,
				freeDatastoreWidth = 0.1, exsiTotalWidth = 0.1, vmTotalWidth = 0.15, clusterWidth = 0.2,
				vmOSWidth = 0.2, exsiWidth = 0.2, dataCenterWidth = 0.15, vCenterWidth = 0.1, addressWidth = 0.1,
				ipWidth = 0.10, projectNameWidth = 0.25;
			
			var sourceNameHidden = false, allCpuHidden = false, allMemHidden = false, allDatastoreHidden = false,
				freeDatastoreHidden = true, exsiTotalHidden = false, vmTotalHidden = false, clusterHidden = true,
				vmOSHidden = true, exsiHidden = true, dataCenterHidden = false, vCenterHidden = false, addressHidden = true,
				ipHidden = true, projectNameHidden = true;
			if(this.currentVmType == 'VirtualHost'){
				sourceNameWidth = 0.15, vmTotalWidth = 0.2, clusterWidth = 0.2, dataCenterWidth = 0.2, vCenterWidth = 0.2;
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true,
				clusterHidden = false;
			} else if(this.currentVmType == 'VirtualStorage'){
				sourceNameWidth = 0.15, allDatastoreWidth = 0.15, exsiTotalWidth = 0.1, vmTotalWidth = 0.15,
				dataCenterWidth = 0.15, vCenterWidth = 0.15;
				allCpuHidden = true, allMemHidden = true, datastoreRateHidden = false, freeDatastoreHidden = false;
			} else if(this.currentVmType == 'VirtualVM'){
				sourceNameWidth = 0.15, dataCenterWidth = 0.2, vCenterWidth = 0.2;
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true,
				vmTotalHidden = true, vmOSHidden = false, exsiHidden = false;
			} else if(this.currentVmType == 'XenHosts'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, addressHidden = false;
				sourceNameWidth = 0.4;
			} else if(this.currentVmType == 'XenVMs'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true;
				sourceNameWidth = 0.4;
			} else if(this.currentVmType == 'XenSRs'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true;
				sourceNameWidth = 0.4;
			} else if(this.currentVmType == 'FusionComputeHosts'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, addressHidden = false;
				sourceNameWidth = 0.4;
			} else if(this.currentVmType == 'FusionComputeVMs'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true;
				sourceNameWidth = 0.4;
			} else if(this.currentVmType == 'FusionComputeDataStores'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true;
				sourceNameWidth = 0.4;
			} else if(this.currentVmType == 'FusionComputeClusters'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true, exsiTotalHidden = true, vmTotalHidden = true,
				dataCenterHidden = true, vCenterHidden = true, addressHidden = true;
				sourceNameWidth = 1.0;
			} else if(this.currentVmType == 'DTCenterECSs'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true,
				exsiTotalHidden = true, vmTotalHidden = true, dataCenterHidden = true,
				vCenterHidden = true, ipHidden = false;
				sourceNameWidth = 0.5, ipWidth = 0.5;
			} else if(this.currentVmType == 'KyLinVms'){
				allCpuHidden = true, allMemHidden = true, allDatastoreHidden = true,
				exsiTotalHidden = true, vmTotalHidden = true, dataCenterHidden = true,
				vCenterHidden = true, ipHidden = true, projectNameHidden = false, hostNameHidden = true;
				sourceNameWidth = 0.5, projectNameWidth = 0.5;
			}
			
			var tabsWidth = this.tabsDiv.width();
			sourceNameWidth = sourceNameWidth * tabsWidth + 'px', allCpuWidth = allCpuWidth * tabsWidth + 'px',
			allMemWidth = allMemWidth * tabsWidth + 'px', allDatastoreWidth = allDatastoreWidth * tabsWidth + 'px',
			freeDatastoreWidth = freeDatastoreWidth * tabsWidth + 'px', exsiTotalWidth = exsiTotalWidth * tabsWidth + 'px',
			vmTotalWidth = vmTotalWidth * tabsWidth + 'px', clusterWidth = clusterWidth * tabsWidth + 'px',
			vmOSWidth = vmOSWidth * tabsWidth + 'px', exsiWidth = exsiWidth * tabsWidth + 'px',
			dataCenterWidth = dataCenterWidth * tabsWidth + 'px', vCenterWidth = vCenterWidth * tabsWidth + 'px',
			ipWidth = ipWidth * tabsWidth + 'px',projectNameWidth = projectNameWidth * tabsWidth + 'px';
			
			var isDTCenterECSs = this.currentVmType == 'DTCenterECSs';
			var isKyLinVms = this.currentVmType == 'KyLinVms';
			// 是否为虚拟化模块
			var octoolbarRight = [];
			
			var user = oc.index.getUser();
			if(user.domainUser || user.systemUser){
				octoolbarRight = [{
					iconCls: 'fa fa-trash-o',
					text:"删除资源",
					onClick: function(){
						that.deleteResource(that.unMonitorDatagrid);
					}
				}, {
					iconCls: 'fa fa-plus',
					text:"开启监控",
					onClick: function(){
						that.openMonitor();
					}
				}].concat(octoolbarRight);
			}
			// 如果是资源管理化加入资源组按钮
			if(this.currentModule == 'resourceModule'){
				octoolbarRight.push({
					iconCls: 'icon-movein',
					text:"移入资源组",
					onClick: function(){
						that.moveInResourceGroup(that.unMonitorDatagrid);
					}
				});
			}
			// 初始化列表
			this.unMonitorDatagrid = oc.ui.datagrid({
				selector : this.tabsDiv.find(".unMonitorDatagrid"),
				pageSize:$.cookie('pageSize_vm_resourcelist')==null ? 15 : $.cookie('pageSize_vm_resourcelist'),
				fitColumns : false,
				hideReset:true,
				hideSearch:true,
				queryForm : this.unMonitorForm,
				url : oc.resource.getUrl('portal/vm/vmResourceList/getUnMonitorList.htm'),
				columns : [[{
					field:'id',
					checkbox:true
				}, {
					field:'sourceName',
					title: isDTCenterECSs ? '实例ID/实例名称' : '资源名称',
					sortable:true,
					width:sourceNameWidth,
					hidden:sourceNameHidden,
					formatter:function(value, row, rowIndex){ 
						if(row.categoryId == 'DTCenterECSs'){
							valueAtEcs = row.uuid + '/' + value;
							return '<label title="' + valueAtEcs + '">' + valueAtEcs.htmlspecialchars() + '</label>';
						} else {
							return '<label title="' + value + '">' + value.htmlspecialchars() + '</label>';
						}
					}
				}, {
					field:'address',
					title:'地址',
					sortable:true,
					width:addressWidth,
					hidden:addressHidden
				}, {
					field:'allCpu',
					title:'总CPU资源',
					sortable:true,
					width:allCpuWidth,
					hidden:allCpuHidden
				}, {
					field:'allMem',
					title:'总内存',
					sortable:true,
					width:allMemWidth,
					hidden:allMemHidden
				}, {
					field:'allDatastore',
					title:'总存储',
					sortable:true,
					width:allDatastoreWidth,
					hidden:allDatastoreHidden
				}, {
					field:'freeDatastore',
					title:'可用空间',
					sortable:true,
					width:freeDatastoreWidth,
					hidden:freeDatastoreHidden
				}, {
					field:'exsiTotal',
					title:'主机数',
					sortable:true,
					width:exsiTotalWidth,
					hidden:exsiTotalHidden
				}, {
					field:'vmTotal',
					title:'虚拟机数',
					sortable:true,
					width:vmTotalWidth,
					hidden:vmTotalHidden
				}, {
					field:'cluster',
					title:'Cluster',
					sortable:true,
					width:clusterWidth,
					hidden:clusterHidden
				}, {
					field:'vmOS',
					title:'客户机操作系统',
					sortable:true,
					width:vmOSWidth,
					hidden:vmOSHidden
				}, {
					field:'exsi',
					title:'主机',
					sortable:true,
					width:exsiWidth,
					hidden:exsiHidden
				}, {
					field:'dataCenter',
					title:'数据中心',
					sortable:true,
					width:dataCenterWidth,
					hidden:dataCenterHidden
				}, {
					field:'projectName',
					title:'项目名称',
					sortable:false,
					width:projectNameWidth,
					hidden:projectNameHidden,
				}, {
					field:'ip',
					title:'IP地址',
					sortable:false,
					width:ipWidth,
					hidden:ipHidden,
				}, {
					field:'vCenter',
					title:'vCenter',
					sortable:true,
					width:vCenterWidth,
					hidden:vCenterHidden
				}]],
				octoolbar : {
					left : [this.unMonitorForm.selector],
					right : octoolbarRight
				},
				loadFilter : function(data){
					 var myData = data.data;
					 that.tabsDiv.find('.tabs-header .tabs-wrap .tabs-title:eq(1)').html('未监控('+myData.totalRecord+')');
					 return myData;
				},
				onLoadSuccess : function(data){
					that.unMonitorDataGridLoadFlag = true;
					that.categoryCountList = data.resourceCategoryBos;
					// 取消进度条
					that.disableGlobalProgress();
				}
			});
			
			//cookie记录pagesize
			var paginationObject = this.tabsDiv.find(".unMonitorDatagrid").datagrid('getPager');
			if(paginationObject){
				paginationObject.pagination({
					onChangePageSize:function(pageSize){
						$.cookie('pageSize_vm_resourcelist',pageSize);
					}
				});
			}
		},
		open : function(){
			
		},
		enableGlobalProgress : function(){
			this.monitorDataGridLoadFlag = false;
			this.unMonitorDataGridLoadFlag = false;
			oc.ui.progress();
		},
		disableGlobalProgress : function(){
			var that = this;
			if(this.monitorDataGridLoadFlag && this.unMonitorDataGridLoadFlag){
				if(this.currentModule == 'resourceModule'){
					oc.module.resourcemanagement.resource.west.refreshCategoryCount(that.categoryCountList);
				}else{
					var hasVMCategory = false;
					for(var i = 0; i < that.categoryCountList.length; i++){
						if(that.categoryCountList[i].id == 'VM'){
							this.updateCategory([that.categoryCountList[i]]);
							hasVMCategory = true;
							break;
						}
					}
					if(!hasVMCategory){
						this.accordion.find(".s-digit").html('0');
					}
				}
				$.messager.progress('close');
			}else{
				oc.ui.progress();
			}
		},
		updateCategory : function(categoryCountList){
			if(categoryCountList) {
				var vmwareNum = 0, vmwareclusterNum = 0, vmwarehostNum = 0, vmwarevmNum = 0, vmwarestorageNum = 0;
				for(var i = 0; i < categoryCountList.length; i++){
					var category = categoryCountList[i];
					if(category.id == 'VMware' || category.id == 'VMware5.5' || category.id == 'VMware6' || category.id == 'VMware6.5'){
						vmwareNum = vmwareNum + category.resourceNumber;
					} else if(category.id == 'VirtualCluster' || category.id == 'VirtualCluster5.5' 
						|| category.id == 'VirtualCluster6' || category.id == 'VirtualCluster6.5'){
						vmwareclusterNum = vmwareclusterNum + category.resourceNumber;
					} else if(category.id == 'VirtualHost' || category.id == 'VirtualHost5.5' 
						|| category.id == 'VirtualHost6' || category.id == 'VirtualHost6.5'){
						vmwarehostNum = vmwarehostNum + category.resourceNumber;
					} else if(category.id == 'VirtualVM' || category.id == 'VirtualVM5.5'
						|| category.id == 'VirtualVM6' || category.id == 'VirtualVM6.5'){
						vmwarevmNum = vmwarevmNum + category.resourceNumber;
					} else if(category.id == 'VirtualStorage' || category.id == 'VirtualStorage5.5'
						|| category.id == 'VirtualStorage6' || category.id == 'VirtualStorage6.5'){
						vmwarestorageNum = vmwarestorageNum + category.resourceNumber;
					} else {
						this.accordion.find('span[value='+category.id+']').html(category.resourceNumber);
					} 
					if(category.childCategorys == undefined || category.childCategorys.length == 0) {
						switch (category.id) {
						case 'VMware':
						case 'VMware5.5':
						case 'VMware6':
						case 'VMware6.5':
							this.accordion.find('span[value=VMware]').html(vmwareNum);
							break;
						case 'VirtualCluster':
						case 'VirtualCluster5.5':
						case 'VirtualCluster6':
						case 'VirtualCluster6.5':
							this.accordion.find('span[value=VirtualCluster]').html(vmwareclusterNum);
							break;
						case 'VirtualHost':
						case 'VirtualHost5.5':
						case 'VirtualHost6':
						case 'VirtualHost6.5':
							this.accordion.find('span[value=VirtualHost]').html(vmwarehostNum);
							break;
						case 'VirtualVM':
						case 'VirtualVM5.5':
						case 'VirtualVM6':
						case 'VirtualVM6.5':
							this.accordion.find('span[value=VirtualVM]').html(vmwarevmNum);
							break;
						case 'VirtualStorage':
						case 'VirtualStorage5.5':
						case 'VirtualStorage6':
						case 'VirtualStorage6.5':
							this.accordion.find('span[value=VirtualStorage]').html(vmwarestorageNum);
							break;
						default:
							break;
						}
					}
					this.updateCategory(category.childCategorys);
				}
			}
		},
		loadAll : function(){
			this.enableGlobalProgress();
			this.monitorDatagrid.load();
			this.unMonitorDatagrid.load();
		},
		reLoadAll : function(){
			this.enableGlobalProgress();
			this.monitorDatagrid.reLoad();
			this.unMonitorDatagrid.reLoad();
		},
		updateWestGroupInfo : function(){
			if(this.currentModule == 'resourceModule'){
				oc.module.resourcemanagement.resource.west.updateGroupInfoForDeleteResource();
			}
		},
		reLoadWestAndList: function(){
			$(oc.index.activeContent[0]).load(oc.index.activeHref);
		},
		getDatagridSelectIds : function(datagrid){
			return this.getSelectIds(datagrid);
		},
		getMonitorDatagridSelectIds : function(){
			return this.getSelectIds(this.monitorDatagrid);
		},
		getUnMonitorDatagridSelectIds : function(){
			return this.getSelectIds(this.unMonitorDatagrid);
		},
		getSelectIds : function(dataGrid){
			var objs=dataGrid.selector.datagrid('getChecked'), ids=[];
			for(var i=0,len=objs.length;i<len;i++){
				var obj = objs[i];
				ids.push(obj.id);
			}
			return ids;
		},
		deleteResource : function(datagrid){
			var that = this;
			 if(that.currentVmType && "ResourcePool"==that.currentVmType){
				 var objs=datagrid.selector.datagrid('getChecked'), uuids=[];
					for(var i=0,len=objs.length;i<len;i++){
						var obj = objs[i];
						uuids.push(obj.uuid);
					}
					
					if(uuids == undefined||uuids == ""){
						 alert("请选择需要删除的资源");
					 }
					
		    		 //资源池删除
					 oc.ui.confirm("是否确认删除资源？",function() {
			    		 oc.util.ajax({
						     url:oc.resource.getUrl('portal/vm/vmResourceList/delResourcePools.htm'),
						     timeout : null,
						     data:{uuids:uuids.join()},
						     successMsg:null,
						     success:function(d){
						    	 that.loadAll();
						    	 that.updateWestGroupInfo();
						     }
				         });
					 });
	    	 }else{
	    		 var ids = that.getDatagridSelectIds(datagrid);
				 if(ids == undefined||ids == ""){
					 alert("请选择需要删除的资源");
				 }else{
		             oc.ui.confirm("是否确认删除资源？",function() {
		            		 oc.util.ajax({
							     url:oc.resource.getUrl('portal/vm/vmResourceList/batchDelResource.htm'),
							     timeout : null,
							     data:{ids:ids.join()},
							     successMsg:null,
							     success:function(d){
							    	 that.loadAll();
							    	 that.updateWestGroupInfo();
							     }
					         }); 
		             });
				}
	    	 }
			 
		},
		moveInResourceGroup : function(datagrid){
			var that = this;
			var ids = that.getDatagridSelectIds(datagrid);
			if(!!ids && ids.length > 0){
				oc.module.resourcemanagement.resource.west.addResourceIntoGroup(ids.join());
			}else{
				alert('请选择需要移入资源组的资源');
			}
		},
		openMonitor : function(){
			var that = this;
			var ids = that.getUnMonitorDatagridSelectIds();
			if(!!ids && ids.length > 0){
				oc.util.ajax({
					url:oc.resource.getUrl('resource/resourceMonitor/batchOpenMonitor.htm'),
					type: 'POST',
					dataType: "json", 
					data:{ids:ids.join()},
					successMsg:null,
					success:function(json){
						if(json.code == 200){
							if(json.data == 1){
								alert('加入监控成功');
							}else if(json.data == 0){
								alert('加入监控失败');
							}else{
								alert('加入监控失败,监控数量超过购买监控数量');
							}
						}else{
							alert('加入监控失败');
						}
						that.loadAll();
					}
				});
			}else{
				alert('请选择需要开启监控的资源');
			}
		},
		cancelMonitor : function(){
			var that = this;
			var ids = that.getMonitorDatagridSelectIds();
			 if(!!ids && ids.length > 0){
				 oc.util.ajax({
					 url:oc.resource.getUrl('resource/resourceMonitor/batchCloseMonitor.htm'),			            
					 data:{ids:ids.join()},
					 successMsg:null,
					 async:false,
					 success:function(d){
						 that.loadAll();
					 }
				 });
			 }else{
				 alert('请选择需要取消监控的资源');
			 }
		},
		moreOperation: function(e){
			var that = this;
			e.stopPropagation();
			var top = $("#moreOpBtnVm").offset().top + 18;
			var left = $("#moreOpBtnVm").offset().left - 5;
			$('body').find('.cgtOper').remove();
			cgtOperDiv = $("<div class='cgtOper sh-window' style='top:"
					+ top + "px;left:" + left + "px'>");
			tWindowDiv = $("<div class='t-window'><div class='b-window'><div class='m-window'>"
					+ "<span class='cgtEdit'><a class='ico ico ico-Person'></a>编辑责任人</span>"
					+ "<span class='cgtDel'><a class='ico ico-clearp'></a>清除责任人</span>"
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
			$(cgtOperDiv).hover(function(e) {
				e.stopPropagation();
			}, function(e) {
				e.stopPropagation();
				$('body').find('.cgtOper').remove();
			});
		}
	};
	oc.ns('oc.vm.resource.resourcelist');
	oc.vm.resource.resourcelist = {
		open : function(cfg){
			var vmResourceList = $('#vmResourceList');
			if(cfg['vmType'] && "ResourcePool"==cfg['vmType']){
				vmResourceList.find('.unMonitorVmResource').hide();
				vmResourceList.find('.vmDatagridToolBar').hide();
				vmResourceList.find('#monitorDatagrid').height('93%');
				
				vmResourceList.find('.tabs-title').each(function(){
					var obj = $(this);
					
					if(obj.html().indexOf('未监控')>-1){
						obj.parent().hide();
					}
				});
			}else{
				vmResourceList.find('#monitorDatagrid').height('88%');
				vmResourceList.find('.unMonitorVmResource').show();
				vmResourceList.find('.vmDatagridToolBar').show();
				
				vmResourceList.find('.tabs-title').each(function(){
					var obj = $(this);
					
					if(obj.html().indexOf('未监控')>-1){
						obj.parent().show();
					}
				});
			}
			new resourceList(cfg)
		}
	};
});