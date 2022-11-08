var ip_group_id = null;
var datagrid = null;
function save(dia, datagrid) {
	var name = $("#ip_group_name").val();
	var lis = $(".r-content>li>.li_value");
	var ips = "";
	if (lis != null) {
		for (var i = 0; i < lis.length; i++) {
			ips += $(lis[i]).html() + ",";
		}
	}
	if (ips.length > 0) {
		ips = ips.substring(0, ips.length - 1);
	}
	if (name == "") {
		alert('【IP组名称】不能为空!');
	} else if (ips == "") {
		alert('【IP组】不能为空!');
	} else {
		oc.util.ajax({
			url : oc.resource.getUrl('netflow/ipGroup/getCount.htm'),
			data : {
				"name" : $("#ip_group_name").val(),
				"id" : $("#ip_group_id").val()
			},
			success : function(data) {
				if(data && data.code == 200 && data.data == 0){
					oc.util.ajax({
						url : oc.resource.getUrl('netflow/ipGroup/save.htm'),
						data : {
							"id" : $("#ip_group_id").val(),
							"name" : $("#ip_group_name").val(),
							"ips" : ips,
							"description" : $("#description").val()
						},
						success : function(data) {
							alert("保存成功！");
							datagrid.reLoad();
							dia.dialog("close");
						}
					});
				}else{
					alert("【IP组名称】已经存在!");
				}
			}
		});
	}
}


function ip_group_edit(id){
	ip_group_id = id;
	dia = $("<div/>").dialog({
		title : '接口组设置',
		href : oc.resource
				.getUrl('resource/module/netflow/ipGroup/add.html'),
		resizable : false,
		width : 800,
		height : 497,
		cache : false,
		buttons : [
				{
					text : "确定",
					iconCls : "fa fa-check-circle",
					handler : function() {
						save(dia,datagrid);
					}
				},
				{
					text : '关闭',
					handler : function() {
						dia.dialog("close");
					}
				} ]
	});
}

$(function() {
	tree = null;
	
	var mainId = oc.util.generateId();
	var mainDiv = $('#ip_group_list_div').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	});
	var user = oc.index.getUser();
	var datagridtoolbar = mainDiv.find("#netflow-ipgroup-managment");
	if(user.systemUser){
		datagrid = oc.ui.datagrid({
			selector : mainDiv.find('#ip_group_list'),
			url : oc.resource.getUrl("netflow/ipGroup/list.htm"),
			hideSearch:true,
			hideReset:true,
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							ipGroupName:$('#netflow-device-managment-ipgroup').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-ipgroup').val('');
						}
					}],
				right : [
						{
							text : '添加',
							iconCls : 'fa fa-plus',
							onClick : function() {
								ip_group_edit(null);
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
																			.getUrl('netflow/ipGroup/del.htm'),
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
				title : 'IP组名称',
				sortable:true,
				width : "33%",
				formatter : function(value,row,index){
					return '<span onClick="ip_group_edit('+row.id+')">'+value+"</span>";
				}
			}, {
				field : 'ips',
				title : 'IP组成员',
				width : "33%"
			}, {
				field : 'description',
				title : 'IP组描述',
				width : "33%"
			} ] ],
		});
	}else{
		datagrid = oc.ui.datagrid({
			selector : mainDiv.find('#ip_group_list'),
			url : oc.resource.getUrl("netflow/ipGroup/list.htm"),
			hideSearch:true,
			hideReset:true,
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							ipGroupName:$('#netflow-device-managment-ipgroup').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-ipgroup').val('');
						}
					}]
			},
			width : '100%',
			columns : [ [ {
				field : 'name',
				title : 'IP组名称',
				sortable:true,
				width : "33%"
			}, {
				field : 'ips',
				title : 'IP组成员',
				width : "33%"
			}, {
				field : 'description',
				title : 'IP组描述',
				width : "33%"
			} ] ],
		});
	}


});