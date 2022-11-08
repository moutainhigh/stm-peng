var interface_group_id = null;
var datagrid = null;
function save(dia, datagrid) {
		var name = $("#interface_name").val();
		if (name.length <= 0) {
			alert('请输入【接口组名称】！');
			return;
		}else{
			for(var i = 0; i<name.lenght;i++){
				var char = name[i];
				if(char=="/"||char=="@"||char=="#"||char=="￥"||char=="%"||char=="'"||char=="&"
					||char=="'"||char=='"'||char=="*"||char==":"||char==";"||char=="<"||char==">"||char=="?"||cahr=="/"
						||char=='+'||char=='='||char=='`'||char=='~'||char=='\\'){
					oc.ui.alert("标题含有非法字符"+char);
					return;
				}
			}
		}
		var jsonArray = interface_group_tree.getRightTreeData();
		if (jsonArray.length <= 0) {
			alert('请将要分组的接口添加到右边窗口！');
			return;
		}
		var ids = "";
		if (jsonArray != null) {
			for (var i = 0; i < jsonArray.length; i++) {
				var c_j = jsonArray[i].children;
				for (var j = 0; j < c_j.length; j++) {
					ids += c_j[j].id + ",";
				}
			}
		}
		if (ids.length > 0) {
			ids = ids.substring(0, ids.length - 1);
		}
		oc.util.ajax({
			url : oc.resource.getUrl('netflow/interfaceGroup/getCount.htm'),
			data : {
				"name" : $("#interface_name").val(),
				"id" : $("#interface_group_id").val()
			},
			success : function(data) {
				if(data && data.code == 200 && data.data == 0){
					oc.util.ajax({
						url : oc.resource.getUrl('netflow/interfaceGroup/save.htm'),
						data : {
							"id" : $("#interface_group_id").val(),
							"name" : $("#interface_name").val(),
							"interfaceIds" : ids,
							"description" : $("#description").val()
						},
						success : function(data) {
							alert("保存成功！");
							datagrid.reLoad();
							dia.dialog("close");
						}
					});
				} else {
					alert("【接口组名称】已经存在!");
				}
			}
		});
	}

function interface_group_edit(id){
	interface_group_id = id;
	dia = $("<div/>").dialog({
		title : '接口组设置',
		href : oc.resource
				.getUrl('resource/module/netflow/interfaceGroup/add.html'),
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

var interface_group_tree = null;
$(function() {
	
	tree = null;
	var mainId = oc.util.generateId();
	var mainDiv = $('#interface_group_list_div1').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	});
	var user = oc.index.getUser();
	var datagridtoolbar = mainDiv.find("#netflow-interfacegroup-managment");
	if(user.systemUser){
		datagrid = oc.ui
		.datagrid({
			selector : mainDiv.find('#interface_group_list1'),
			url : oc.resource.getUrl("netflow/interfaceGroup/list.htm"),
			hideSearch:true,
			hideReset:true,
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							interfaceGroupName:$('#netflow-device-managment-interfacegroup').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-interfacegroup').val('');
						}
					}],
				right : [
						{
							text : '添加',
							iconCls : 'fa fa-plus',
							onClick : function() {
								interface_group_edit(null);
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
																			.getUrl('netflow/interfaceGroup/del.htm'),
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
				title : '接口组名称',
				sortable:true,
				width : "33%",
				formatter : function(value,row,index){
					return '<span onClick="interface_group_edit('+row.id+')">'+value+"</span>";
				}
			}, {
				field : 'interfaceNames',
				title : '接口组成员',
				width : "33%"
			}, {
				field : 'description',
				title : '接口组描述',
				width : "33%"
			} ] ],
		});
	}else{
		datagrid = oc.ui
		.datagrid({
			selector : mainDiv.find('#interface_group_list1'),
			url : oc.resource.getUrl("netflow/interfaceGroup/list.htm"),
			hideSearch:true,
			hideReset:true,
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							interfaceGroupName:$('#netflow-device-managment-interfacegroup').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-interfacegroup').val('');
						}
					}]
			},
			width : '100%',
			columns : [ [ {
				field : 'name',
				title : '接口组名称',
				sortable:true,
				width : "33%"
			}, {
				field : 'interfaceNames',
				title : '接口组成员',
				width : "33%"
			}, {
				field : 'description',
				title : '接口组描述',
				width : "33%"
			} ] ],
		});
	}


});