var device_group_id = null;
function save(dia, datagrid) {
	var name = $("#interface_name").val();
	if (name.length <= 0) {
		alert('请输入【设备组名称】！');
		return;
	}
	var jsonArray = device_group_tree.getRightTreeData();
	if (jsonArray.length <= 0) {
		alert('请将要分组的设备添加到右边窗口！');
		return;
	}
	var ids = "";
	if (jsonArray != null) {
		for (var i = 0; i < jsonArray.length; i++) {
			ids += jsonArray[i].id + ",";
		}
	}
	if (ids.length > 0) {
		ids = ids.substring(0, ids.length - 1);
	}
	oc.util.ajax({
		url : oc.resource.getUrl('netflow/deviceGroup/getCount.htm'),
		data : {
			"name" : $("#interface_name").val(),
			"id" : $("#device_group_id").val()
		},
		success : function(data){
			if(data && data.code == 200 && data.data == 0){
				oc.util.ajax({
					url : oc.resource.getUrl('netflow/deviceGroup/save.htm'),
					data : {
						"id" : $("#device_group_id").val(),
						"name" : $("#interface_name").val(),
						"deviceIds" : ids,
						"description" : $("#description").val()
					},
					success : function(data) {
						alert("保存成功！");
						datagrid.reLoad();
						dia.dialog("close");
					}
				});
			} else {
				alert("【设备组名称】已经存在!");
			}
		}
	});
}

function device_group_edit(id){
	device_group_id = id;
	dia = $("<div/>").dialog({
		title : '设备组设置',
		href : oc.resource.getUrl('resource/module/netflow/deviceGroup/add.html'),
		resizable : false,
		width : 800,
		height : 497,
		cache : false,
		buttons : [{
				text : "确定",
				iconCls : "fa fa-check-circle",
				handler : function() {
					save(
							dia,
							datagrid);
				}
			},
			{
				text : '关闭',
				handler : function() {
					dia.dialog("close");
				}
		}]
	});
}
var datagrid = null;
var device_group_tree = null;
$(function() {

	tree = null;
	var mainId = oc.util.generateId();
	var mainDiv = $('#interface_group_list_div').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	});
	var user = oc.index.getUser();
	var datagridtoolbar = mainDiv.find("#netflow-devicegroup-managment");
	if(user.systemUser){
		datagrid = oc.ui.datagrid({
			selector : mainDiv.find('#interface_group_list'),
			hideSearch:true,
			hideReset:true,
			url : oc.resource.getUrl("netflow/deviceGroup/list.htm"),
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							deviceGroupName:$('#netflow-device-managment-devicegroup').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-devicegroup').val('');
						}
					}],
				right : [
						{
							text : '添加',
							iconCls : 'fa fa-plus',
							onClick : function() {
								device_group_edit(null);
							}
						},
						{
							text : '删除',
							iconCls : 'fa fa-trash-o',
							onClick : function() {
								var ids = datagrid.getSelectIds();
								if (ids.length == 0) {
									alert('请至少选择一条数据！');
									return;
								} else {
									oc.ui
											.confirm(
													'确认删除所选择的数据？',
													function() {
														oc.util
																.ajax({
																	url : oc.resource
																			.getUrl('netflow/deviceGroup/del.htm'),
																	data : "ids="
																			+ ids
																					.join(","),
																	success : function(
																			data) {
																		alert("删除成功！");
																		datagrid
																				.reLoad();
																	}
																});
													});
								}

							}
						} ]
			},
			width : '100%',
			columns : [ [ {
				field : 'id',
				title : '-',
				checkbox : true,
			}, {
				field : 'name',
				title : '设备组名称',
				sortable:true,
				width : "33%",
				formatter: function(value, row, index) {
					return '<span onClick="device_group_edit('+row.id+')">'+value+"</span>";
				}
			}, {
				field : 'deviceIds',
				title : '设备组成员',
				width : "33%"
			}, {
				field : 'description',
				title : '设备组描述',
				width : "33%"
			} ] ],
		});
	}else{
		datagrid = oc.ui.datagrid({
			selector : mainDiv.find('#interface_group_list'),
			hideSearch:true,
			hideReset:true,
			url : oc.resource.getUrl("netflow/deviceGroup/list.htm"),
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							deviceGroupName:$('#netflow-device-managment-devicegroup').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-devicegroup').val('');
						}
					}]
			},
			width : '100%',
			columns : [ [{
				field : 'name',
				title : '设备组名称',
				sortable:true,
				width : "33%"
			}, {
				field : 'deviceIds',
				title : '设备组成员',
				width : "33%"
			}, {
				field : 'description',
				title : '设备组描述',
				width : "33%"
			} ] ],
		});
	}
	

});