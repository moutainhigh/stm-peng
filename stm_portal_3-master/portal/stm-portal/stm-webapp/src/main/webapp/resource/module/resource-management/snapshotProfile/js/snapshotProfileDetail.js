$(function(){
	function snapshotDetail(){
		
	}
	snapshotDetail.prototype = {
		constructor : snapshotDetail,
		mainDiv : undefined,
		addDetailDiv : undefined,
		mainResource : undefined,
		childResource : undefined,
		resourcePickgrid : undefined,
		metricDatagrid : undefined,
		snapshotScript : undefined,
		resumScript : undefined,
		leftPickgridData : undefined,
		domainCombobox : undefined,
		isEdit : false,
		isCopy : false,
		profile : undefined,
		instanceRelation : undefined,
		metricRelation : undefined,
		profileDatagrid : undefined,
		open : function(profileDatagrid){
			var that = this;
			this.profileDatagrid = profileDatagrid;
			this.addDetailDiv = $("<div/>");
			this.addDetailDiv.dialog({
				href : oc.resource.getUrl('resource/module/resource-management/snapshotProfile/snapshotProfileDetail.html'),
				title : '编辑策略',
				height : 575,
				width : 1000,
				onLoad : function(){
					that.init();
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						that.saveSnapshotProfile();
					}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						that.addDetailDiv.dialog('close');
					}
				}]
			});
		},
		edit : function(profile, profileDatagrid){
			var that = this;
			this.isEdit = true;
			this.profile = profile;
			this.profileDatagrid = profileDatagrid;
			this.addDetailDiv = $("<div/>");
			this.addDetailDiv.dialog({
				href : oc.resource.getUrl('resource/module/resource-management/snapshotProfile/snapshotProfileDetail.html'),
				title : '编辑策略',
				height : 575,
				width : 1000,
				onLoad : function(){
					that.init();
					that.setBasicInfo();
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						that.editSnapshotProfile();
					}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						that.addDetailDiv.dialog('close');
					}
				}]
			});
		},
		copy : function(profile, profileDatagrid){
			var that = this;
			this.isCopy = true;
			this.profile = profile;
			this.profileDatagrid = profileDatagrid;
			this.addDetailDiv = $("<div/>");
			this.addDetailDiv.dialog({
				href : oc.resource.getUrl('resource/module/resource-management/snapshotProfile/snapshotProfileDetail.html'),
				title : '编辑策略',
				height : 575,
				width : 1000,
				onLoad : function(){
					that.init();
					that.setBasicInfo();
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						that.saveSnapshotProfile();
					}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						that.addDetailDiv.dialog('close');
					}
				}]
			});
		},
		init : function(){
			this.createAccordion();
			this.createBaseInfo();
			this.createMainResource();
			this.createChildResource();
			this.createFilterLeftGrid();
			this.createPickGrid();
			this.createMetricGrid();
			this.createSnapshotScript();
			this.createResumScript();
		},
		createAccordion : function(){
			var that = this, flag = true;
			var id = oc.util.generateId();
			this.mainDiv = $("#oc_module_resource_management_snapshotProfile_detail").attr("id", id);
			this.mainDiv.accordion({
				onSelect : function(title, index){
//					if((that.isEdit || that.isCopy) && index == 1 && flag){
//						that.setResourceInfo();
//						flag = false;
//					}
				}
			});
		},
		createBaseInfo : function(){
			var that = this, flag = false;
			var domains = oc.index.getDomains();
			var firstDomainId = typeof(domains[0]) == "undefined" ? '' : domains[0].id;
			this.domainCombobox = oc.ui.combobox({
				selector : this.mainDiv.find("[name='domainId']"),
				placeholder : false,
				data : domains,
				value : firstDomainId,
				onChange : function(newValue, oldValue){
					that.mainResource.jq.combotree('setValue', '');
					if(flag){
						alert('选择资源信息清空');
					}
					flag = true;
				}
			});
		},
		createMainResource : function(){
			var that = this, mainResourceDiv = that.mainDiv.find(".mainResourceSelect"), flag = true;
			this.mainResource = oc.ui.combotree({
				  selector : mainResourceDiv,
				  width:'200px',
				  placeholder:'请选择资源类型',
				  url:oc.resource.getUrl('portal/resource/snapshotProfile/getResourceCategoryList.htm'),
				  filter:function(data){
					  return data.data;
				  },
				  onChange : function(newValue, oldValue, newJson, oldJson){
					  if(newValue != ''){
						  if(newJson.children != undefined){
							  //that.mainResource.jq.combotree('setValue', oldValue);
							  that.childResource.load([]);
							  alert('请选择具体的资源模型');
						  } else {
							  oc.util.ajax({
								  successMsg:null,
								  url: oc.resource.getUrl('portal/resource/snapshotProfile/getChildResourceListByMainResourceOrCategory.htm'),
								  data:{id:newJson.id,type:newJson.type},
								  success:function(data){
									  if(data.data){
										  that.childResource.load(data.data);
									  }
								  }
							  });
							  if((!that.isEdit && !that.isCopy) || !flag || !that.profile.resourceId){
								  that.loadPickGrid(newJson);
							  }
							  flag = false;
						  }
					  } else {
						  that.childResource.load([]);
						  that.setPickgridLeftData([]);
						  that.setPickgridRightData([]);
						  that.setDatagridData(that.metricDatagrid, [], '');
					  }
				  }
			});
		},
		createChildResource : function(){
			var that = this, flag = true;
			this.childResource = oc.ui.combobox({
				  selector:that.mainDiv.find('.childResourceSelect'),
				  width:'120px',
				  selected:false,
				  placeholder:'请选择子资源',
				  data:null,
				  onChange : function(newValue, oldValue){
					  var parameter = {};
					  if(newValue == '' || newValue == undefined || newValue == null){
						  parameter.id = that.mainResource.jq.combobox('getValue');
					  }else{
						  parameter.id = newValue;
					  }
					  parameter.type = 2;
					  that.loadPickGrid(parameter);
				  },
				  onLoadSuccess : function(){
					  if((that.isEdit || that.isCopy) && flag){
						  that.childResource.jq.combobox('setValue', that.profile.resourceId);
						  flag = false;
					  }
				  }
			});
		},
		createPickGrid : function(){
			var that = this, flag = true;
			var instanceColumns = [[
                {field:'id',checkbox:true},
                {field:'discoverIP',title:'IP地址',width:90,ellipsis:true},
                {field:'showName',title:'名称',width:85,ellipsis:true},
                {field:'resourceId',hidden:true},
                {field:'resourceName',title:'资源类型',width:80,ellipsis:true}
            ]];
			this.resourcePickgrid = oc.ui.pickgrid({
				selector:'#parentResourceInstanceGrid',
				leftColumns:instanceColumns,
				rightColumns:instanceColumns,
				isInteractive:true,
				moveBeforeEvent:function(rows,direction){
					return rows;
				},
				onMoveSuccess:function(srcGridData, targetGridData){
					that.filterLeftGrid();
					if(that.resourcePickgrid.getRightRows().length == 0){
						that.deleteAllGridData(that.metricDatagrid);
					}else{
						that.loadMetricGrid();
					}
				},
				leftOptions : {
					onLoadSuccess:function(data){
						if((that.isEdit || that.isCopy) && flag){
							that.getProfileRelation();
							flag = false;
						}
					}
				}
			});
		},
		createMetricGrid : function(){
			var that = this, flag = true;
			this.metricDatagrid = oc.ui.datagrid({
				selector : that.mainDiv.find(".selectMetricGrid"),
				pagination:false,
				fitColumns:false,
				noDataMsg:'未选择数据',
				columns : [[{
					field:'id',checkbox:true
				},{
					field:'name',title:'指标名称',width:235,ellipsis:true
				}]],
				onLoadSuccess : function(data){
					if((that.isEdit || that.isCopy) && flag){
						var rows = data.rows;
						for(var i = 0; i < rows.length; i++){
							for(var j = 0; j < that.metricRelation.length; j++){
								if(rows[i].id == that.metricRelation[j]){
									that.metricDatagrid.selector.datagrid('checkRow', i);
									break;
								}
							}
						}
					}
				}
			});
		},
		createSnapshotScript : function(){
			var that = this, flag = true;
			this.snapshotScript = oc.ui.datagrid({
				selector : that.mainDiv.find(".snapshotScript > div"),
				pagination:false,
				fitColumns:false,
				url : oc.resource.getUrl('portal/resource/snapshotProfile/getSnapshotScript.htm'),
				columns : [[{
					field:'scriptId',checkbox:true
				},{
					field:'docName',title:'快照脚本名称',width:160,ellipsis:true
				},{
					field:'discription',title:'描述',width:245,ellipsis:true
				}]],
				loadFilter : function(data){
					var dataFiltered = data.data;
					if(data.code != 200){
						dataFiltered = data;
					}
					return dataFiltered;
				},
				onLoadSuccess:function(data){
					if((that.isEdit || that.isCopy) && flag){
						var rows = data.rows;
						for(var i = 0; i < rows.length; i++){
							if(rows[i].scriptId == that.profile.snapshotScriptId){
								that.snapshotScript.selector.datagrid('checkRow', i);
								break;
							}
						}
					}
				},
				onCheck : function(rowIndex, rowData){
					var rows = that.snapshotScript.selector.datagrid('getChecked');
					for(var i = 0; i < rows.length; i++){
						if(rows[i].scriptId != rowData.scriptId){
							that.snapshotScript.selector.datagrid('uncheckRow', that.snapshotScript.selector.datagrid('getRowIndex', rows[i]));
						}
					}
				}
			});
		},
		createResumScript : function(){
			var that = this, flag = true;
			this.resumScript = oc.ui.datagrid({
				selector : that.mainDiv.find(".resumScript > div"),
				pagination:false,
				fitColumns:false,
				url : oc.resource.getUrl('portal/resource/snapshotProfile/getResumScript.htm'),
				columns : [[{
					field:'scriptId',checkbox:true
				},{
					field:'docName',title:'恢复脚本名称',width:160,ellipsis:true
				},{
					field:'discription',title:'描述',width:245,ellipsis:true
				}]],
				loadFilter : function(data){
					var dataFiltered = data.data;
					if(data.code != 200){
						dataFiltered = data;
					}
					return dataFiltered;
				},
				onLoadSuccess:function(data){
					if((that.isEdit || that.isCopy) && flag){
						var rows = data.rows;
						for(var i = 0; i < rows.length; i++){
							if(rows[i].scriptId == that.profile.recoveryScriptId){
								that.resumScript.selector.datagrid('checkRow', i);
								break;
							}
						}
					}
					// 加载资源与指标 放这个弹窗的最后不然加载会有问题
					if((that.isEdit || that.isCopy) && flag){
						that.setResourceInfo();
						flag = false;
					}
					that.resumScript.selector.find(".datagrid-header-row [type='checkbox']").css('display', 'none');
				},
				onCheck : function(rowIndex, rowData){
					var rows = that.resumScript.selector.datagrid('getChecked');
					for(var i = 0; i < rows.length; i++){
						if(rows[i].scriptId != rowData.scriptId){
							that.resumScript.selector.datagrid('uncheckRow', that.resumScript.selector.datagrid('getRowIndex', rows[i]));
						}
					}
				}
			});
		},
		createFilterLeftGrid : function(){
			var that = this;
			that.mainDiv.find("#searchInstanceListButton").on('click', function(){
				that.filterLeftGrid();
			});
		},
		filterLeftGrid : function(){
			var that = this, newData = [];
			var searchContent = that.mainDiv.find('#searchInstanceListInput').val().trim();
			var leftData = that.resourcePickgrid.getLeftRows();
			var rightData = that.resourcePickgrid.getRightRows();
			if(that.leftPickgridData != undefined && (searchContent == '' || searchContent == undefined) && (rightData.length + leftData.length) == that.leftPickgridData.length){
				return ;
			}
			for(var i = 0; that.leftPickgridData != undefined && i < that.leftPickgridData.length; i++){
				var isRight = false;
				for(var j = 0; j < rightData.length; j++){
					if(that.leftPickgridData[i].id == rightData[j].id){
						isRight = true;
						break;
					}
				}
				if(!isRight){
					newData.push(that.leftPickgridData[i]);
				}
			}
			var newLeftData = $.extend(true,[], newData);
			if(newLeftData == null || newLeftData == undefined || newLeftData.length <= 0){
				return;
			}
			leftData = [];
			for(var i = 0 ; i < newLeftData.length ; i ++){
				if(newLeftData[i].discoverIP.indexOf(searchContent) != -1 || newLeftData[i].showName.indexOf(searchContent) != -1 || newLeftData[i].resourceName.indexOf(searchContent) != -1 ){
					leftData.push(newLeftData[i]);
				}
			}
			that.setPickgridLeftData(leftData);
		},
		loadPickGrid : function(newJson){
			var domainId = this.domainCombobox.jq.combobox('getValue');
			if(domainId == null || domainId == undefined || domainId == ''){
				alert('域不能为空');
				return false;
			}
			var that = this;
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/snapshotProfile/getResourceInstanceList.htm'),
				  timeout:null,
				  data:{queryId:newJson.id,type:newJson.type,domainId:domainId},
				  success:function(data){
					  that.setPickgridLeftData(data.data);
					  that.setPickgridRightData([]);
					  that.deleteAllGridData(that.metricDatagrid);
					  that.leftPickgridData = data.data;
				  }
			});
		},
		loadMetricGrid : function(){
			var that = this, resourceIdArray = new Array(), instanceIdArr = new Array();
			var rightData = that.resourcePickgrid.getRightRows();
			for(var i = 0 ; i < rightData.length ; i ++){
				resourceIdArray.push(rightData[i].resourceId);
				instanceIdArr.push(rightData[i].id);
			}
			
			oc.util.ajax({
				successMsg:null,
				url: oc.resource.getUrl('portal/resource/snapshotProfile/getMetricListByResourceId.htm'),
				data:{resourceIdArr:resourceIdArray.join(","),instanceIdArr:instanceIdArr.join(",")},
				success:function(data){
					if(data.data){
						that.setDatagridData(that.metricDatagrid,data.data,'所选资源无性能指标');
					}else{
						alert('查询指标失败!');
					}
				}
			});
		},
		setPickgridLeftData : function(nowData) {
			var that = this;
			var data = $.extend(true, [], nowData);
			if (!data || data.length <= 0) {
				that.deleteAllGridData(that.resourcePickgrid.leftGrid);
			} else {
				that.resourcePickgrid.loadData("left", {
					"code" : 200,
					"data" : {
						"total" : 0,
						"rows" : data
					}
				});
			}
		},
		setPickgridRightData : function(nowData) {
			var that = this;
			var data = $.extend(true, [], nowData);
			if (!data || data.length <= 0) {
				that.deleteAllGridData(that.resourcePickgrid.rightGrid);
			} else {
				that.resourcePickgrid.loadData("right", {
					"code" : 200,
					"data" : {
						"total" : 0,
						"rows" : data
					}
				});
			}
		},
		setDatagridData : function(grid, nowData, newMsg){
			var that = this;
			var data = $.extend(true,[],nowData);
			if(!data || data.length <= 0){
				if(newMsg){
					grid.updateNoDataMsg(newMsg);
				}else{
					grid.updateNoDataMsg('未选择数据');
				}
				that.deleteAllGridData(grid);
			}else{
				grid.selector.datagrid('loadData',{"code":200,"data":{"total":0,"rows":data}});
			}
		},
		deleteAllGridData : function(grid) {
			var item = grid.selector.datagrid('getRows');
			if (item) {
				for (var i = item.length - 1; i >= 0; i--) {
					var index = grid.selector.datagrid('getRowIndex', item[i]);
					grid.selector.datagrid('deleteRow', index);
				}
				grid.selector.datagrid('uncheckAll');
			}
		},
		submitData : function(){
			var that = this;
			// 策略名称
			var profileName = this.mainDiv.find("[name=profileName]").val();
			if(profileName == '' || profileName == null || profileName == undefined){
				alert('策略名称不能为空');
				return false;
			}
			// 策略描述
			var profileDesc = this.mainDiv.find("[name=profileDesc]").val();
			var domainId = this.domainCombobox.jq.combobox('getValue');
			if(domainId == '' || domainId == null || domainId == undefined){
				alert('域不能为空');
				return false;
			}
			// 类型类型
			var parentResourceId = this.mainResource.jq.combotree('getValue');
			if(parentResourceId == '' || parentResourceId == null || parentResourceId == undefined){
				alert('资源类型不能为空');
				return false;
			}
			// 子资源类型
			var resourceId = this.childResource.jq.combotree('getValue');
			// 所选资源
			var instaceList = this.resourcePickgrid.getRightRows();
			if(instaceList == null || instaceList == undefined || instaceList.length == 0){
				alert('选择的资源不能为空');
				return false;
			}
			var instanceIds = new Array();
			for(var i = 0; i < instaceList.length; i ++){
				instanceIds.push(instaceList[i].id);
			}
			// 所选指标
			var metricList = this.metricDatagrid.selector.datagrid('getChecked');
			if(metricList == null || metricList == undefined || metricList.length == 0){
				alert('选择指标至少勾选一项');
				return false;
			}
			var metricIds = new Array();
			for(var i = 0; i < metricList.length; i ++){
				metricIds.push(metricList[i].id);
			}
			// 告警级别
			var alarmLevel = new Array();
			this.mainDiv.find("[name=alarmLevel]").each(function(){
				if($(this).prop("checked"))
					alarmLevel.push($(this).val());
					
			});
			if(alarmLevel.length == 0){
				alert('告警级别不能为空');
				return false;
			}
			// 快照脚本和恢复脚本
			var snapshotScriptList = this.snapshotScript.selector.datagrid('getChecked');
			var resumScriptList = this.resumScript.selector.datagrid('getChecked');
			if((snapshotScriptList == null || snapshotScriptList == undefined || snapshotScriptList.length == 0)
					&& (resumScriptList == null || resumScriptList == undefined || resumScriptList.length == 0)){
				alert('快照脚本和恢复脚本至少要勾选一个');
				return false;
			}
			var snapshotScript = snapshotScriptList != undefined && snapshotScriptList.length > 0 ? snapshotScriptList[0].scriptId : '';
			var resumScript = resumScriptList != undefined && resumScriptList.length > 0 ? resumScriptList[0].scriptId : '';
			var data = {
					'profileName' : profileName,
					'profileDesc' : profileDesc,
					'parentResourceId' : parentResourceId,
					'resourceId' : resourceId,
					'alarmLevel' : alarmLevel.join(),
					'snapshotScriptId' : snapshotScript,
					'recoveryScriptId' : resumScript,
					'domainId' : domainId,
					'instanceIds' : instanceIds.join(),
					'metricIds' : metricIds.join()
				};
			return data;
		},
		saveSnapshotProfile : function(){
			var that = this, data = this.submitData();
			if(that.isCopy){
				data.isUse = that.profile.isUse;
			}else{
				data.isUse = 1;
			}
			if(data){
				oc.util.ajax({
					url : oc.resource.getUrl('portal/resource/snapshotProfile/addSnapshotProfile.htm'),
					data : data,
					success : function(data){
						that.addDetailDiv.dialog('close');
						that.profileDatagrid.reLoad();
					}
				});
			}
		},
		editSnapshotProfile : function(){
			var that = this, data = this.submitData();
			if(data){
				data.profileId = this.profile.profileId;
				data.isUse = this.profile.isUse;
				oc.util.ajax({
					url : oc.resource.getUrl('portal/resource/snapshotProfile/editSnapshotProfile.htm'),
					data : data,
					success : function(data){
						that.addDetailDiv.dialog('close');
						that.profileDatagrid.reLoad();
					}
				});
			}
		},
		setBasicInfo : function(){
			var that = this;
			if(that.isCopy){
				this.mainDiv.find("[name=profileName]").val('复制' + this.profile.profileName);
			}else{
				this.mainDiv.find("[name=profileName]").val(this.profile.profileName);
			}
			this.mainDiv.find('.createUser').html(this.profile.createUser);
			var createTime = this.profile.createTime;
			createTime = createTime != undefined && createTime != null && createTime != '' ? (new Date(createTime)).stringify('yyyy-mm-dd hh:MM:ss') : '';
			this.mainDiv.find('.createTime').html(createTime);
			this.mainDiv.find('.updateUser').html(this.profile.updateUser);
			var updateTime = this.profile.updateTime;
			updateTime = updateTime != undefined && updateTime != null && updateTime != '' ? (new Date(updateTime)).stringify('yyyy-mm-dd hh:MM:ss') : '';
			this.mainDiv.find('.updateTime').html(updateTime);
			this.mainDiv.find("[name=profileDesc]").val(this.profile.profileDesc);
			var alarmLevel = that.profile.alarmLevel.split(",");
			this.mainDiv.find("[name=alarmLevel]").each(function(){
				for(var i = 0; i < alarmLevel.length; i++){
					if($(this).val() == alarmLevel[i]){
						$(this).prop('checked', true);
						break;
					}
				}
			});
		},
		setResourceInfo : function(){
			this.domainCombobox.jq.combobox('setValue', this.profile.domainId);
			this.mainResource.jq.combotree('setValue', this.profile.parentResourceId);
		},
		getProfileRelation : function(){
			var that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/snapshotProfile/getSnapshotProfileRelation.htm'),
				data : {
					profileId : that.profile.profileId
				},
				success : function(data){
					if(data.data){
						that.instanceRelation = data.data.instance;
						that.metricRelation = data.data.metric;
						var leftData = that.resourcePickgrid.getLeftRows(), newLeftData = new Array();
						var rightData = new Array();
						for(var i = 0; leftData != null && leftData != undefined && i < leftData.length; i++){
							var flag = true;
							for(var j = 0; that.instanceRelation != null && that.instanceRelation != undefined && j < that.instanceRelation.length; j++){
								if(leftData[i].id == that.instanceRelation[j]){
									rightData.push(leftData[i]);
									flag = false;
									break;
								}
							}
							if(flag){
								newLeftData.push(leftData[i]);
							}
						}
						that.setPickgridLeftData(newLeftData);
						that.setPickgridRightData(rightData);
						that.loadMetricGrid();
					}
				}
			});
		}
	};

	oc.ns('oc.resource.snapshotdetail');
	oc.resource.snapshotdetail = {
		open : function(profileDatagrid){
			new snapshotDetail().open(profileDatagrid);
		},
		edit : function(profile, profileDatagrid){
			new snapshotDetail().edit(profile, profileDatagrid);
		},
		copy : function(profile, profileDatagrid){
			new snapshotDetail().copy(profile, profileDatagrid);
		}
	}
});