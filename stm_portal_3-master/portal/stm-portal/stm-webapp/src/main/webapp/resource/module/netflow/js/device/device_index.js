var device_add_pickgrid = null;
$(function() {
	// oc.util.ajax({
	// url : oc.resource.getUrl('netflow/device/list.htm'),
	// data : {
	// "startRecord" : '0',
	// "pageSize" : '10',
	// },
	// success : function(data) {
	// }
	// });

	var mainId = oc.util.generateId(),
	// 模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
	mainDiv = $('#div_device').attr('id', mainId).panel({
		// title:'用户管理',
		fit : true,
		isOcAutoWidth : true
	})
	// 表格文档对象
	, datagridDiv = mainDiv.find('.device_data');

	// , queryForm = oc.ui.form({
	// selector : mainDiv.find(".oc-form")

	// combobox:[{
	// selector:'[name=domainId]',
	// filter:function(data){
	// return data.data;
	// },
	// url:oc.resource.getUrl('system/user/getDomains')
	// }]
	// });
	// 表格实例
	datagrid = null;
	// var userTypes = oc.util.getDict('user_type');
	var user = oc.index.getUser();
	var deviceQueryForm = oc.ui.form({
		selector: $('#netflow-device-managment-form')
	});
    var inputsearchbar = mainDiv.find('#netflow-device-managment');
	if(user.systemUser){
		datagrid = oc.ui.datagrid({
			selector : datagridDiv,
			hideSearch:true,
			hideReset:true,
			queryForm: deviceQueryForm,
			url : oc.resource.getUrl('netflow/device/list.htm'),
			octoolbar : {
				left  : [inputsearchbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							deviceSearchname:	$('#netflow-device-managment-devicename').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-devicename').val('');
						}
					}],
				right : [
						{
							text : '添加',
							iconCls : 'fa fa-plus',
							onClick : function() {
								_open('add');
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
																			.getUrl('netflow/device/delDevice.htm'),
																	data : "ids="
																			+ ids
																					.join(","),
																	success : function(
																			data) {
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
			columns : [ [
					{
						field : 'dbId',
						title : '-',
						checkbox : true,
					},
					{
						field : 'name',
						title : '设备名称',
						sortable:true,
						width : "20%",
					},
					{
						field : 'ip',
						title : 'IP地址',
						sortable:true,
						width : "20%",
					},
					{
						field : 'type',
						title : '设备类型',
						width : "20%",
						formatter : function(value,row,index){
							switch(value){
								case "Switch" :
									return "交换机";
								case "Router" :
									return "路由器";
								case "Firewall" :
									return "防火墙";
								case "WirelessAP" :
									return "无线ap";
							}
							return value;
						}
					},
					{
						field : 'manufacturers',
						title : '厂商',
						width : "20%",
					},
					{
						field : 'opt',
						title : '接口配置',
						width : "19%",
						formatter : function(value, rec) {
							return '<span class="ico-edit report-detail-edit"  onclick="device_manager(\''
									+ rec.id
									+ '\',\''
									+ rec.dbId
									+ '\',\''
									+ rec.type + '\')" />';
						}
					} ] ],
			onLoadSuccess : function(data) {
				$('.ico-edit').linkbutton({
					plain : true,
					iconCls : 'icon-edit'
				});
			}
		});
	}
	else{
		datagrid = oc.ui.datagrid({
			selector : datagridDiv,
			queryForm: deviceQueryForm,
			url : oc.resource.getUrl('netflow/device/list.htm'),
			width : '100%',
			hideReset:true,
			hideSearch:true,
			octoolbar : {
				left  : [inputsearchbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						datagrid.selector.datagrid("load", {
							deviceSearchname:	$('#netflow-device-managment-devicename').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-device-managment-devicename').val('');
						}
					}]
					},	
			columns : [ [
					{
						field : 'name',
						title : '设备名称',
						sortable:true,
						width : "25%",
					},
					{
						field : 'ip',
						title : 'IP地址',
						sortable:true,
						width : "25%",
					},
					{
						field : 'type',
						title : '设备类型',
						width : "25%",
						formatter : function(value,row,index){
							switch(value){
								case "Switch" :
									return "交换机";
								case "Router" :
									return "路由器";
								case "Firewall" :
									return "防火墙";
								case "WirelessAP" :
									return "无线ap";
							}
							return value;
						}
					},
					{
						field : 'manufacturers',
						title : '厂商',
						width : "25%",
					}] ],
		});
	}
	

	function save(dia) {
		var ids = '';
		var selectedRows = device_add_pickgrid.getRightRows();
		if(selectedRows && selectedRows.length > 0){
			for (var i = 0; i < selectedRows.length; i++) {
				ids += selectedRows[i].info + ",";
			}
		}
		if (ids.length > 0) {
			ids = ids.substring(0, ids.length - 1);
			oc.util.ajax({
				url : oc.resource.getUrl('netflow/device/saveCheck.htm'),
				data : "infos=" + ids,
				success : function(data) {
					if (data.code == 220) {
						alert(data.data);
						datagrid.load();
					} else {
						alert("保存成功!");
						dia.dialog("close");
						datagrid.load();
					}
				},
			});
		} else {
			alert('请至少选择一条数据！');
		}
	}

	function _open(type) {
		var dia = $('<div/>')
				.dialog(
						{
							title : '添加设备',
							href : oc.resource
									.getUrl('resource/module/netflow/device/device_add.html'),
							resizable : false,
							width : 800,
							height : 455,
							cache : false,
							buttons : [ {
								text : "确定",
								iconCls : "fa fa-check-circle",
								handler : function() {
									save(dia);
								}
							}, {
								text : '关闭',
								handler : function() {
									/*oc.ui.confirm('否保存当前操作？', function() {
										save(dia);
									}, function() {
										
									});*/
									dia.dialog("close");
								}
							} ]
						});
	}

});

var resourceId;
var resource_type;
var device_id;
function device_manager(id, dbId, type) {
	device_id = dbId;
	resourceId = id;
	resource_type = type;
	var dia_interface = $('<div/>')
			.dialog(
					{
						title : '接口管理',
						href : oc.resource
								.getUrl('resource/module/netflow/device/dialog_interface.html'),
						// onLoad : function() {
						// _init();
						// },
						resizable : false,
						width : 800,
						height : 455,
						cache : false,
					// buttons : [ {
					// text : "确定",
					// iconCls : "fa fa-check-circle",
					// handler : function() {
					// save_interface(dia_interface);
					//
					// }
					// }, {
					// text : '关闭',
					// handler : function() {
					// oc.ui.confirm('否保存当前操作？', function() {
					// save_interface(dia_interface);
					// });
					// }
					// } ]
					});
}