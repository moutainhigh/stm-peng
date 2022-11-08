$(function() {
	function del(ids) {
		oc.util.ajax({
			url : oc.resource.getUrl("netflow/device/delInterface.htm"),
			data : {
				"interfaceIds" : ids,
			},
			success : function(data) {
				alert("取消成功!");
			}
		});
	}

	function save_interfaces(deviceId, infos) {
		oc.util.ajax({
			url : oc.resource.getUrl("netflow/device/addInterface.htm"),
			data : {
				"deviceId" : device_id,
				"infos" : JSON.stringify(infos),
			},
			success : function(data) {
				switch (data.data) {
				case 1:
					alert("添加成功!");
					break;
				case -1:
					alert("没有License，添加接口失败!");
					break;
				case -2:
					alert("没有License，部分接口添加成功!");
					break;
				case -3:
					alert("添加接口已到上限!");
					break;
				case -4:
					alert("添加成功,有重复接口!");
					break;
				}
			}
		});
	}

	oc.ui.datagrid({
		selector : $("#dialogFormDemo"),
		queryForm : null,
		url : oc.resource.getUrl('netflow/device/confInterface.htm?resourceId='
				+ resourceId + "&resourceType=" + resource_type),
		width : '100%',
		onSelect : function(rowIndex, rowData) {
			save_interfaces(device_id, [ rowData ]);
		},
		onSelectAll : function(rows) {
			if (rows != null && rows.length > 0) {
				save_interfaces(device_id, rows);
			}
		},
		onUnselect : function(rowIndex, rowData) {
			del(rowData.id);
		},
		onUnselectAll : function(rows) {
			if (rows != null) {
				var ids = "";
				for (var i = 0; i < rows.length; i++) {
					if (i != 0) {
						ids += ",";
					}
					ids += rows[i].id;
				}
				if (ids != "") {
					del(ids);
				}
			}
		},
		columns : [ [ {
			field : 'info',
			title : '是否管理',
			styler : function(value, row, index) {
				return {
					class : 'd_interface_cbox'
				};
			},
			checkbox : true,
		}, {
			field : 'name',
			title : '接口名称',
			width : "25%",
		}, {
			field : 'index',
			title : '接口索引',
			width : "25%",
		}, {
			field : 'ifSpeed',
			title : '带宽',
			width : "25%",
		}, {
			field : 'available',
			title : '状态',
			width : "24%",
			formatter : function(value,row,index){
				switch(value){
					case true:
						return '可用';
					case false:
						return '不可用';
				}
			}
		} ] ]
	});
});