var app_group_id = null;
function save(dia, datagrid) {
	var portLis = $(".port_data>li>.li_value");
	var appName = $("#app_name").val();
	var type = $('#protocol_type').combobox('getValue');
	if (appName == "") {
		alert('【应用名称】不能为空！');
	} else if (type == "") {
		alert('【协议】必须选择！');
	} else if (portLis.length <= 0) {
		alert('【端口】必须添加！');
	} else {
		var ports = "";
		for (var i = 0; i < portLis.length; i++) {
			ports += "," + $.trim($(portLis[i]).html());
		}
		ports = ports.substring(1, ports.length);
		var ipLis = $(".ip_data>li>.li_value");
		var ips = "";
		if (ipLis.length > 0) {
			for (var i = 0; i < ipLis.length; i++) {
				ips += "," + $.trim($(ipLis[i]).html());
			}
			ips = ips.substring(1, ips.length);
		}
		oc.util.ajax({
			url : oc.resource.getUrl("netflow/applicationManager/getCount.htm"),
			data : {
				"name" : appName,
				"id" : $("#app_group_id").val()
			},
			success : function(data) {
				if(data && data.code == 200 && data.data == 0){
					oc.util.ajax({
						url : oc.resource.getUrl("netflow/applicationManager/save.htm"),
						data : {
							"id" : $("#app_group_id").val(),
							"name" : appName,
							"protocolId" : $('#protocol_type').combobox('getValue'),
							"ips" : ips,
							"ports" : ports
						},
						success : function(data) {
							if(data && data.code == 200 && data.data == 1){
								alert("保存成功！");
								datagrid.reLoad();
								dia.dialog("close");
							} else {
								alert("保存失败,端口与IP包含重复IP段！");
							}
						}
					});
				}else{
					alert("【应用名称】已经存在!");
				}
			}
		});
	}
}


function app_group_edit(id){
	app_group_id = id;
	dia = $("<div/>").dialog({
				title : '应用添加',
				href : oc.resource
						.getUrl('resource/module/netflow/applicationManager/add.html'),
				resizable : false,
				width : 800,
				height : 497,
				cache : false,
				buttons : [
						{
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
						} ]
			});
}
var datagrid = null;

function reladDatagrid(name){
	datagrid.selector.datagrid("load", {
		applicationFirstName:name,
	});
}

$(function() {
	tree = null;
	var mainId = oc.util.generateId();
	var mainDiv = $('#netflow-application-main').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	});
	var user = oc.index.getUser();
	var datagridtoolbar = mainDiv.find("#netflow-application-managment");
	if(user.systemUser){
		datagrid = oc.ui.datagrid({
			selector : $("#interface_group_list"),
			hideSearch:true,
			hideReset:true,
			url : oc.resource.getUrl("netflow/applicationManager/list.htm"),
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							applicationName:$('#netflow-device-managment-application').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-application').val('');
						}
					}],
				right : [
						
							{
							text : '添加',
							iconCls : 'fa fa-plus',
							onClick : function() {
								app_group_edit(null);
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
																			.getUrl('netflow/applicationManager/del.htm'),
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
						},{
							text : '恢复默认',
							iconCls : 'fa fa-plus',
							onClick : function() {
								oc.ui.confirm('确认要恢复到默认应用？',function() {
									oc.util.ajax({
										url : oc.resource.getUrl('netflow/applicationManager/restoreDefault.htm'),
										success : function(data) {
											if(data && data.code == 200 && data.data == true){
												alert("恢复成功！");
												datagrid.reLoad();
											} else {
												alert("恢复失败！");
											}
										}
									});
								});
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
				title : '应用名称',
				sortable:true,
				width : "24%",
				formatter : function(value,row,index){
					return '<span onClick="app_group_edit('+row.id+')">'+value+"</span>";
				}
			}, {
				field : 'protocolName',
				title : '协议',
				width : "24%"
			}, {
				field : 'ports',
				title : '端口',
				width : "24%"
			}, {
				field : 'ips',
				title : 'IP地址',
				width : "24%"
			} ] ],
		});
	}else{
		datagrid = oc.ui.datagrid({
			selector : $("#interface_group_list"),
			hideSearch:true,
			hideReset:true,
			url : oc.resource.getUrl("netflow/applicationManager/list.htm"),
			octoolbar : {
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							applicationName:$('#netflow-device-managment-application').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-application').val('');
						}
					}],
			},
			width : '100%',
			columns : [ [ {
				field : 'name',
				title : '应用名称',
				sortable:true,
				width : "24%"
			}, {
				field : 'protocolName',
				title : '协议',
				width : "24%"
			}, {
				field : 'ports',
				title : '端口',
				width : "24%"
			}, {
				field : 'ips',
				title : 'IP地址',
				width : "24%"
			} ] ],
		});
	}
	

});