$(function() {
	if(device_group_id != null){
		oc.util.ajax({
			url : oc.resource.getUrl('netflow/deviceGroup/get.htm'),
			data : {
				"id" : device_group_id
			},
			success : function(data) {
				if (data && data.code == 200) {
					$("#device_group_id").val(data.data.id);
					$("#interface_name").val(data.data.name);
					$("#description").val(data.data.description);
					device_group_tree = oc.ui.picktree({
						selector : $("#interface_group_add"),
						lUrl : oc.resource.getUrl("netflow/deviceGroup/getAllDevice.htm?notIds="+data.data.deviceIds),
						rUrl : oc.resource.getUrl("netflow/deviceGroup/getRDevice.htm?ids="+data.data.deviceIds),
						dataType : 'json',
						requestType:'sync',
						isInteractive : true,
					});
				};
			}
		});
	}

	if(device_group_id == null){
			device_group_tree = oc.ui.picktree({
			selector : $("#interface_group_add"),
			lUrl : oc.resource.getUrl("netflow/deviceGroup/getAllDevice.htm"),
			dataType : 'json',
			requestType:'sync',
			isInteractive : true,
		});
	}

	$("#interface_group_add_query").click(function() {
		var jsonArray = device_group_tree.getRightTreeData();
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
			url : oc.resource.getUrl('netflow/deviceGroup/getAllDevice.htm'),
			data : {
				"name" : $("#interface_group_add_query_name").val(),
				"notIds" : ids,
			},
			success : function(data) {
				device_group_tree.reload(data.data, "l");
			}
		});
	});
});