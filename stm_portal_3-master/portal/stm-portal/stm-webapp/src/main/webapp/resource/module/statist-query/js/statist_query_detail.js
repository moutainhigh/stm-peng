$(function(){
	oc.ns('oc.statist.query.detail');
	function statistQueryDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	statistQueryDetail.prototype = {
		construtor : statistQueryDetail,
		_defaults : {},
		openType : undefined,
		dataOption : undefined,
		detailMainDiv : $("<div/>"),
		detailMainDlg : undefined,
		reportFrom : undefined,
		reportNameDom : undefined,
		mainResource : undefined,
		childResource : undefined,
		alternativeGrid : undefined,
		resourcePickgrid : undefined,
		metricDatagrid : undefined,
		checkedMetricIds : [],
		alternativeGridData : undefined,
		alternativeGridDataUnAppend : [],
		alternativeGridDataCheck : [],
		curMainResourceType : undefined,
		isFirstLoadAlternativeGrid4Edit : true,
		isFirstMainResourceLoadPickGrid : true,
		filterMainResources : ['Host', 'NetworkDevice', 'VM'],
		propertyCountMetric :[{id: "cpuRate", name: "CPU平均利用率(%)", unit: "%", metricSort: -1, metricExpectValue: null},
		                      {id: "memRate", name: "内存利用率(%)", unit: "%", metricSort: -1, metricExpectValue: null},
		                      {id: "DiskUsagePercentage", name: "磁盘使用百分比(%)", unit: "%", metricSort: -1, metricExpectValue: null}],
		open : function(){
			var that = this;
			that.openType = that.cfg.openType;
			that.dataOption = that.cfg.dataOption;
			that.openStaQDetailDlg();
		},
		openStaQDetailDlg : function(){
			var that = this;
			this.detailMainDlg = this.detailMainDiv.dialog({
				title : (that.openType == 'add' ? '新增统计报表' : '修改统计报表'),
				href : oc.resource.getUrl('resource/module/statist-query/statist_query_detail.html'),
				width : 990,
				height : 498,
				buttons : [{
					text:'保存',
			    	iconCls:"fa fa-check-circle",
					handler:function(){
						that.saveDetail();
					}
				}, {
					text:'关闭',
			    	iconCls:"fa fa-times-circle",
					handler:function(){
						that.detailMainDlg.dialog('close');
					}
				}],
				onClose : function(){
					that.detailMainDlg.panel('destroy');
				},
				onLoad : function() {
					that._init();
				}
			});
		},
		_init : function(){
			var that = this;
			this.createForm();
			this.createMainResource();
			this.createChildResource();
			this.createAlternativeGrid();
			this.createMetricGrid();
			this.addFilterAlternativeGridEvent();
		},
		createForm : function(){
			var that = this;
			this.reportFrom = oc.ui.form({
				selector : this.detailMainDiv.find("form[name='reportForm']")
			});
			this.reportNameDom = that.detailMainDiv.find("[name='reportName']");
			this.detailMainDiv.find("[name='statistType']").on('click', function(){
				if($(this).val() == 1){
					that.detailMainDiv.find(".childResourceTypeOuter").show();
				}else{
					that.detailMainDiv.find(".childResourceTypeOuter").hide();
				}
				that.mainResource.reLoad(oc.resource.getUrl('portal/statistQuery/detail/getResourceCategoryList.htm'));
				// 清空后续加载的组件数据
				that.childResource.load([]);
				that.setDatagridData(that.alternativeGrid, [], '');
				that.setDatagridData(that.metricDatagrid, [], '');
			});
		},
		createMainResource : function(){
			var that = this, isFirstLoadSuccess = true;
			this.mainResource = oc.ui.combotree({
				selector : this.detailMainDiv.find("[name='resourceType']"),
				width:'200px',
				placeholder:'请选择资源类型',
				url:oc.resource.getUrl('portal/statistQuery/detail/getResourceCategoryList.htm'),
				filter:function(data){
					var newData = data.data;
					var statistType = that.detailMainDiv.find("[name='statistType']:checked").val();
					if(statistType == '2' || (that.openType == 'edit' && that.dataOption.type == '2')){
						newData = that.filterMainResourceData(newData, that.filterMainResources);
					}
					return newData;
				},
				onChange : function(newValue, oldValue, newJson, oldJson){
					if(newJson != undefined && newValue != ''){
						var staticType = that.detailMainDiv.find("[name='statistType']:checked").val();
						if(staticType == 1){
							oc.util.ajax({
								successMsg:null,
								url: oc.resource.getUrl('portal/statistQuery/detail/getChildResourceListByMainResourceOrCategory.htm'),
								data:{id:newJson.id,type:newJson.type},
								success:function(data){
									if(data.data){
										that.childResource.load(data.data);
									}
								}
							});
						} else {
							that.childResource.load([]);
						}
						that.loadAlternativeGrid(newJson, 'mainResource');
						that.curMainResourceType = newJson.type;
					} else {
						that.childResource.load([]);
						that.setDatagridData(that.alternativeGrid, [], '');
						that.setDatagridData(that.metricDatagrid, [], '');
					}
				},
				onLoadSuccess : function(node, data){
					if(that.openType == 'edit' && isFirstLoadSuccess){
						isFirstLoadSuccess = false;
						that.setStatQDetailData4Edit();
					}
				}
			});
		},
		filterMainResourceData : function(data, filterList){
			var that = this, filteredData = [], newFilterList = [], copyData = $.extend([], data);;
			for(var i = 0; i < data.length; i++){
				var category = data[i];
				if($.inArray(category.pid, filterList) != -1 || (category.pid == 'Resource' && $.inArray(category.id, filterList) != -1)){
					filteredData.push(category);
					if(category.pid != 'Resource'){
						newFilterList.push(category.id);
					}
					$.grep(copyData,  function(n, i){
						return n.id != category.id;
					});
				}
			}
			if(newFilterList.length > 0){
				var childData = that.filterMainResourceData(copyData, newFilterList);
				for(var j = 0; j < childData.length; j++){
					filteredData.push(childData[j]);
				}
			}
			return filteredData;
		},
		createChildResource : function(){
			var that = this, isFirst = true;
			this.childResource = oc.ui.combobox({
				  selector:that.detailMainDiv.find('.childResourceType'),
				  width:'120px',
				  selected:false,
				  placeholder:'请选择子资源',
				  data:null,
				  onChange : function(newValue, oldValue){
					  var parameter = {};
					  if(newValue == '' || newValue == undefined || newValue == null){
						  parameter.id = that.mainResource.jq.combobox('getValue');
						  parameter.type = that.curMainResourceType;
					  }else{
						  parameter.id = newValue;
						  parameter.type = 2;
					  }
					  that.loadAlternativeGrid(parameter, 'childResource');
				  },
				  onLoadSuccess : function(node, data){
					  if(that.openType == 'edit' && isFirst && that.dataOption.subResourceId != "" && that.dataOption.type == '1'){
						  isFirst = false;
						  that.childResource.jq.combobox('setValue', that.dataOption.subResourceId);
					  }
				  }
			});
		},
		createAlternativeGrid : function(){
			var that = this;
			this.alternativeGrid = oc.ui.datagrid({
				selector : $('#parentResourceInstanceGrid').find(".selecdtInstanceGrid"),
				pagination:false,
				fitColumns:false,
				checkOnSelect:false,
				selectOnCheck:false,
				noDataMsg:'未选择数据',
				columns : [[
	                {field:'id',checkbox:true},
	                {field:'discoverIP',title:'IP地址',width:130,ellipsis:true},
	                {field:'showName',title:'名称',width:340,ellipsis:true},
	                {field:'resourceId',hidden:true},
	                {field:'resourceName',title:'资源类型',width:125,ellipsis:true}
	            ]],
				onCheck : function(rowIndex, rowData){
					for(var i = 0, alternativeGridDataCheckId = []; i < that.alternativeGridDataCheck.length; i++){
						alternativeGridDataCheckId.push(that.alternativeGridDataCheck[i].id);
					}
					if($.inArray(parseFloat(rowData.id), alternativeGridDataCheckId) == -1){
						that.alternativeGridDataCheck.push(rowData);
						that.loadMetricGrid();
					}
				},
				onUncheck : function(rowIndex, rowData){
					that.alternativeGridDataCheck = $.grep(that.alternativeGridDataCheck, function(n, i){
						return parseFloat(n.id) != parseFloat(rowData.id);
					});
					that.loadMetricGrid();
				},
				onCheckAll : function(rows){
					that.alternativeGridDataCheck = that.alternativeGridData;
					that.loadMetricGrid();
				},
				onUncheckAll : function(rows){
					that.alternativeGridDataCheck = [];
					that.loadMetricGrid();
				},
				onLoadSuccess:function(data){
					
				}
			});
			that.scrollDatagridBody = $('#parentResourceInstanceGrid').find('.datagrid-view2>.datagrid-body');
		},
		createMetricGrid : function(){
			var that = this, isFirst = true;
			this.metricDatagrid = oc.ui.datagrid({
				selector : that.detailMainDiv.find(".selectMetricGrid"),
				pagination:false,
				fitColumns:false,
				checkOnSelect:false,
				selectOnCheck:false,
				noDataMsg:'未选择数据',
				columns : [[{
					field:'id', checkbox:true
				}, {
					field:'name', title:'指标名称', width:240, ellipsis:true
				}]],
				onCheck : function(rowIndex, rowData){
					var rows = that.metricDatagrid.selector.datagrid('getChecked');
					if(rows.length > 5){
						that.metricDatagrid.selector.datagrid('uncheckRow', rowIndex);
						alert('选择指标不得多于5个');
					}else{
						if($.inArray(rowData.id, that.checkedMetricIds) == -1){
							that.checkedMetricIds.push(rowData.id);
						}
					}
				},
				onUncheck : function(rowIndex, rowData){
					that.checkedMetricIds = $.grep(that.checkedMetricIds, function(n, i){
						return n.id != rowData.id;
					});
				},
				onCheckAll : function(rows){
					var allMetricIds = [];
					for(var i = 0; i < rows.length; i++){
						allMetricIds.push(rows[i].id);
					}
					that.checkedMetricIds = allMetricIds;
				},
				onUncheckAll : function(rows){
					that.checkedMetricIds = [];
				},
				onLoadSuccess:function(data){
					var rows = data.rows, allCheckbox = that.detailMainDiv.find('.metricSelectGridDiv').find(".datagrid-view2 > .datagrid-header [type='checkbox']");
					if(rows.length > 5){
						allCheckbox.attr('disabled', true);
					}else{
						allCheckbox.attr('disabled', false);
					}
					// 修改时初次赋值
					if((that.openType == 'edit') && isFirst){
						that.checkedMetricIds = [];
						for(var i = 0; i < that.dataOption.statQMetricBoList.length; i++){
							that.checkedMetricIds.push(that.dataOption.statQMetricBoList[i].metricId);
						}
						isFirst = false;
					}
					var allMetricIds = [];
					for(var i = 0; i < rows.length; i++){
						if($.inArray(rows[i].id, that.checkedMetricIds) != -1){
							that.metricDatagrid.selector.datagrid('checkRow', i);
						}
						allMetricIds.push(rows[i].id);
					}
					// 如果加载的所有指标不包括已选择指标则从已选择指标中去掉
					that.checkedMetricIds = $.grep(that.checkedMetricIds, function(n, i){
						return $.inArray(n, allMetricIds) == -1;
					}, true);
				}
			});
		},
		loadAlternativeGrid : function(newJson, whickResource){
			var that = this, isFirst = true, isFirstLoadMetric = true;
			if(whickResource == 'mainResource' && that.openType == 'edit' && that.dataOption.subResourceId != "" && that.isFirstMainResourceLoadPickGrid){
				that.isFirstMainResourceLoadPickGrid = false;
				return;
			}
			oc.util.ajax({
				url: oc.resource.getUrl('portal/statistQuery/detail/getResourceInstanceList.htm'),
				  timeout:null,
				  data:{queryId : newJson.id , type : newJson.type , domainId : 0},
				  success:function(data){
					  // 清空列表加载的数据
					  that.setDatagridData(that.alternativeGrid, [], '');
					  that.setDatagridData(that.metricDatagrid, [], '');
					  // 初始化缓存数据
					  that.alternativeGridData = data.data;
					  that.alternativeGridDataUnAppend = $.extend([], data.data);
					  that.alternativeGridDataCheck = [];
					  // 编辑时 加载已选择资源
					  if(that.openType == 'edit' && that.isFirstLoadAlternativeGrid4Edit){
						  var selectInstIdList = [];
						  for(var i = 0; i < that.dataOption.statQInstBoList.length; i++){
							  selectInstIdList.push(that.dataOption.statQInstBoList[i].instanceId);
						  }
						  for(var i = 0; i < that.alternativeGridData.length; i++){
							  if($.inArray(parseFloat(that.alternativeGridData[i].id), selectInstIdList) != -1){
								  that.alternativeGridDataCheck.push(that.alternativeGridData[i]);
							  }
						  }
						  that.loadMetricGrid();
						  that.isFirstLoadAlternativeGrid4Edit = false;
					  }
					  // 先加载10条数据
					  that.appendAlternativeGrid(10, that.alternativeGridDataUnAppend);
				  }
			});
		},
		appendAlternativeGrid : function(maxAppendNum, appendRows){
			var that = this;
			// 取消滚动事件
			that.scrollDatagridBody.unbind('scroll');
			// 如果已经加载完毕则不再执行后面的操作
			if(appendRows.length <= 0){
				return;
			}
			var checkedInstIdList = [];
			for(var i = 0; i < that.alternativeGridDataCheck.length; i++){
				checkedInstIdList.push(that.alternativeGridDataCheck[i].id);
			}
			var copyGridData = $.extend([], appendRows);
			appendRows.reverse();
			for (var i = 0; i < copyGridData.length && i < maxAppendNum; i++) {
				that.alternativeGrid.selector.datagrid('appendRow', copyGridData[i]);
				if($.inArray(parseFloat(copyGridData[i].id), checkedInstIdList) != -1){
					that.alternativeGrid.selector.datagrid('checkRow', that.alternativeGrid.selector.datagrid('getRowIndex', copyGridData[i]));
				}
				appendRows.pop();
			}
			appendRows.reverse();
			// 添加滚动事件
			that.scrollDatagridBody.on('scroll', function(){
				if($(this).get(0).scrollHeight - $(this).height() <= $(this).scrollTop()){
					that.appendAlternativeGrid(10, that.alternativeGridDataUnAppend);
				}
			});
		},
		loadMetricGrid : function(){
			var that = this, resourceIdArray = "";
			var rightData = that.alternativeGridDataCheck;
			for(var i = 0 ; i < rightData.length ; i ++){
				resourceIdArray += rightData[i].resourceId + ",";
			}
			resourceIdArray = resourceIdArray.substring(0,resourceIdArray.length - 1);
			var statistType = that.detailMainDiv.find("[name='statistType']:checked").val();
			if(statistType == '2'){
				that.setDatagridData(that.metricDatagrid, that.propertyCountMetric, '所选资源无性能指标');
				return;
			}
			if(rightData == null || rightData.length == 0){
				that.setDatagridData(that.metricDatagrid, [], '');
				return;
			}
			oc.util.ajax({
				successMsg:null,
				url: oc.resource.getUrl('portal/statistQuery/detail/getMetricListByResourceId.htm'),
				data:{resourceIdList:resourceIdArray,instanceId:rightData[0].id,statQType:statistType},
				success:function(data){
					if(data.data){
						that.setDatagridData(that.metricDatagrid, data.data, '所选资源无性能指标');
					}else{
						alert('查询指标失败!');
					}
				}
			});
		},
		addFilterAlternativeGridEvent : function(){
			var that = this;
			that.detailMainDiv.find("#searchInstanceListButton").on('click', function(){
				that.filterAlternativeGrid();
			});
		},
		filterAlternativeGrid : function(){
			var that = this, unAppendInstIds = [], appendedData = [];
			var searchContent = that.detailMainDiv.find('#searchInstanceListInput').val().trim();
			// 没有输入条件时
			if(that.alternativeGridData != undefined && (searchContent == '' || searchContent == null)){
				for(var i = 0; i < that.alternativeGridDataUnAppend.length; i++){
					unAppendInstIds.push(that.alternativeGridDataUnAppend[i].id);
				}
				for(var i = 0; i < that.alternativeGridData.length; i++){
					if($.inArray(parseFloat(that.alternativeGridData[i].id), unAppendInstIds) == -1){
						appendedData.push(that.alternativeGridData[i]);
					}
				}
				that.loadAlternGridStayCheckIntMet4Filter(appendedData);
				that.loadMetricGrid();
				return;
			}
			if(that.alternativeGridData != undefined){
				for(var i = 0; i < that.alternativeGridData.length; i++){
					var rowData = that.alternativeGridData[i];
					if(rowData.discoverIP.indexOf(searchContent) != -1 || rowData.showName.indexOf(searchContent) != -1 || rowData.resourceName.indexOf(searchContent) != -1 ){
						appendedData.push(rowData);
					}
				}
				that.loadAlternGridStayCheckIntMet4Filter(appendedData);
				that.loadMetricGrid();
				return;
			}
		},
		// 加载待选资源保持已选择的资源和指标不变
		loadAlternGridStayCheckIntMet4Filter : function(appendedData){
			var that = this;
			var copyGridDataChecks = $.extend([], that.alternativeGridDataCheck), copyMetricIds = $.extend([], that.checkedMetricIds);
			that.setDatagridData(that.alternativeGrid, [], '');
			that.alternativeGridDataCheck = copyGridDataChecks;
			that.checkedMetricIds = copyMetricIds;
			that.appendAlternativeGrid(appendedData.length, appendedData);
		},
		submitData : function(){
			var that = this, data;
			if(!that.reportFrom.validate()){
				return false;
			}
			var reportName = that.reportNameDom.val();
			if(reportName == null || reportName == undefined || reportName.trim() == ''){
				alert('报表名称不能为空');
				return false;
			}
			var statistType = that.detailMainDiv.find("[name='statistType']:checked").val();
			// 类型类型
			var parentResourceId = this.mainResource.jq.combotree('getValue');
			if(parentResourceId == '' || parentResourceId == null || parentResourceId == undefined){
				alert('资源类型不能为空');
				return false;
			}
			var childResourceId = this.childResource.jq.combobox('getValue');
			// 所选资源
			var instaceList = this.alternativeGridDataCheck;
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
			data = {
				name : reportName.trim(),
				type : statistType,
				categoryId : parentResourceId,
				subResourceId : childResourceId,
				instanceIdList : instanceIds,
				metricIdList : metricIds
			};
			return data;
		},
		saveDetail : function(){
			var that = this, submitData = this.submitData();
			if(submitData == false){
				return;
			}
			var url = oc.resource.getUrl('portal/statistQuery/detail/addStatQueryDetail.htm');
			if(that.openType == 'edit'){
				url = oc.resource.getUrl('portal/statistQuery/detail/updateStatQueryDetail.htm');
				submitData.id = that.dataOption.id;
			}
			oc.util.ajax({
				url : url,
				data : {statQMain : JSON.stringify(submitData)},
				success : function(data){
					if(data.code == 200){
						that.detailMainDiv.dialog('close');
						if(that.openType == 'edit'){
							oc.statist.query.main.editStatQNavsublistItem(data.data);
						}else{
							oc.statist.query.main.addStatQNavsublistItem(data.data);
						}
						alert('保存成功');
					}else{
						if(data.code == 202){
							alert('报表名称已存在');
						}else{
							alert('保存失败');
						}
					}
				}
			});
		},
		setStatQDetailData4Edit : function(){
			var that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/statistQuery/detail/getSQMainByStatQId.htm'),
				data : {statQMainId : that.cfg.dataOption.id},
				timeout : null,
				success : function(data){
					if(data.code == 200){
						that.dataOption = data.data;
						that.reportNameDom.val(that.dataOption.name);
						that.detailMainDiv.find("[name='statistType'][value=" + that.dataOption.type + "]").attr("checked", "checked");
						that.detailMainDiv.find("[name='statistType']").attr("disabled", "disabled");;
						if(that.dataOption.type == '2'){
							that.detailMainDiv.find(".childResourceTypeOuter").hide();
						}
						that.mainResource.jq.combotree('setValue', that.dataOption.categoryId);
					}else{
						alert('获取数据错误');
					}
				}
			});
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
		}
	}
	oc.statist.query.detail = {
		open : function(cfg){
			var sqd = new statistQueryDetail(cfg);
			sqd.open();
		}
	}
});