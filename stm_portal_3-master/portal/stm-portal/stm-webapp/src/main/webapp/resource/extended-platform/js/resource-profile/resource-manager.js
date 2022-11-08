/**
 * 
 */
$(function() {
	
	$(document).keydown(function(event){
		if(event.keyCode==13){
			search();
		}
	});
	
	$("#search-resource").linkbutton({
		iconCls : 'icon-search',
		text : '搜索',
		onClick : function() {
			search();
		}
	});

	
	function search(){
		var instanceId = $("#txt-instanceid").val();
		if (instanceId) {
			oc.util.ajax({
				url : baseUrl + "extendedplatform/instance/search.htm",
				data : {
					id : instanceId
				},
				success : function(data) {
					$("#instance-data-grid").datagrid({
						data : data,
						height : 400,
						title : '资源管理',
						singleSelect : true,
						toolbar : [ {
							iconCls : 'icon-remove',
							text : '清空',
							handler : function() {
								var instanceId = $("#txt-instanceid").val();
								$.messager.confirm("提示", "清空资源之前请先在资源管理中将资源删除再清空数据，否则在操作完成后需要重启系统服务！你确定要清空吗？", function(n){
									if(n){
										deleteInstance(instanceId);
									}
								});
							}
						} ],
						columns : [ [ {
							field : 'id',
							title : 'instanceId',
							width : 100
						}, {
							field : 'name',
							title : 'instanceName',
							width : 100
						}, {
							field : 'showName',
							title : 'instanceShowName',
							width : 100
						}, {
							field : 'parentId',
							title : 'parentId',
							width : 100
						}, {
							field : 'childType',
							title : 'instanceType',
							width : 100
						}, {
							field : 'resourceId',
							title : 'resourceId',
							width : 100
						}, {
							field : 'categoryId',
							title : 'categoryId',
							width : 100
						}, {
							field : 'discoverWay',
							title : 'discoverWay',
							width : 100
						}, {
							field : 'lifeState',
							title : 'lifeState',
							width : 100
						}, {
							field : 'discoverIP',
							title : 'discoverIP',
							width : 100
						}, {
							field : 'domainId',
							title : 'domainId',
							width : 100
						}, {
							field : 'isCore',
							title : 'isCore',
							width : 100
						} ] ],
						onClickRow : function(index, row) {
							initProfielInstanceGrid(row.id);
						}
					});
				}
			});
			initProfielInstanceGrid(instanceId);
		}
	}
	
	function initProfielInstanceGrid(instanceId) {
		oc.util.ajax({
			url : baseUrl
					+ "extendedplatform/instance/instanceProfileRelatione.htm",
			data : {
				instanceId : instanceId
			},
			success : function(data) {
				// var data = JSON.parse(data);
				if (data) {
					if (data.resourceInstance) {
						$("#instance-profile").datagrid({
							data : data.resourceInstance,
							title : "策略资源关系",
							height : 400,
							toolbar : [ {
								iconCls : 'icon-remove',
								text : '清空',
								handler : function() {
									var instanceId = $("#txt-instanceid").val();
									$.messager.confirm("提示", "清空资源与策略的关系之前请先在资源管理中将资源取消监控再清空数据，你确定要清空吗？", function(n){
										if(n){
											deleteInstanceProfileRel(instanceId);
										}
									});
								}
							} ],
							columns : [ [ {
								field : 'instanceId',
								title : 'instanceId',
								width : 100
							}, {
								field : 'parentInstanceId',
								title : 'parentInstanceId',
								width : 100
							}, {
								field : 'profileId',
								title : 'profileId',
								width : 100
							}, {
								field : 'profileName',
								title : 'profileName',
								width : 100
							} ] ]
						});
					}
					if (data.resourceInstanceLast) {
						$("#instance-profile-last").datagrid({
							data : data.resourceInstanceLast,
							title : "策略资源历史关系",
							height : 400,
							toolbar : [ {
								iconCls : 'icon-remove',
								text : '清空',
								handler : function() {
									var instanceId = $("#txt-instanceid").val();
									$.messager.confirm("提示", "清空资源与策略的历史关系之前请先在资源管理中将资源取消监控再清空数据，你确定要清空吗？", function(n){
										if(n){
											deleteInstanceProfileRel(instanceId);
										}
									});
								}
							} ],
							columns : [ [ {
								field : 'instanceId',
								title : 'instanceId',
								width : 100
							}, {
								field : 'parentInstanceId',
								title : 'parentInstanceId',
								width : 100
							}, {
								field : 'profileId',
								title : 'profileId',
								width : 100
							}, {
								field : 'profileName',
								title : 'profileName',
								width : 100
							} ] ]
						});
					}
				}
			}
		});
	}

	$("#delete-resource").linkbutton({
		iconCls : 'icon-cancel',
		text : '一键删除',
		onClick : function() {
			var instanceId = $("#txt-instanceid").val();
			$.messager.confirm("提示", "一键删除所有数据之前请先在资源管理中删除资源，否则在操作完成后需要重启系统服务！你确定要清空吗？", function(n){
				if(n){
					deleteInstanceProfileRel(instanceId);
					deleteInstanceProfileLastRel(instanceId);
					deleteInstance(instanceId);
				}
			});
		}
	});
	
	function deleteInstanceProfileRel(instanceId){
		oc.util.ajax({
			url : baseUrl + "extendedplatform/instance/deleteRelatione.htm",
			data : {instanceId : instanceId},
			success :function(data){
				initProfielInstanceGrid(instanceId);
			}
		});
	}
	
	function deleteInstanceProfileLastRel(instanceId){
		oc.util.ajax({
			url : baseUrl + "extendedplatform/instance/deleteLastRelatione.htm",
			data : {instanceId : instanceId},
			success :function(){
				initProfielInstanceGrid(instanceId);
			}
		});
	}
	
	function deleteInstance(instanceId){
		oc.util.ajax({
			url : baseUrl + "extendedplatform/instance/deleteInstance.htm",
			data : {instanceId : instanceId},
			success :function(){
				search();
			}
		});
	}
});