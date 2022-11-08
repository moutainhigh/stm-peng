$(function() {
	if(interface_group_id != null){
		oc.util.ajax({
			url : oc.resource.getUrl('netflow/interfaceGroup/get.htm'),
			data : {
				"id" :interface_group_id
			},
			success : function(data) {
				if(data && data.code == 200){
					$("#interface_group_id").val(data.data.id);
					$("#interface_name").val(data.data.name);
					$("#description").val(data.data.description);
					interface_group_tree = oc.ui.picktree({
						selector : "#interface_group_add",
						lUrl : oc.resource.getUrl("netflow/interfaceGroup/deviceInterface.htm?notIds=" + data.data.interfaceIds),
						rUrl : oc.resource.getUrl("netflow/interfaceGroup/deviceInterface.htm?f=r&ids=" + data.data.interfaceIds),
						dataType : 'json',
						requestType:'sync',
						isInteractive : true,
					});
				}
			}
		});
	}

	if(interface_group_id == null){
		interface_group_tree = oc.ui
				.picktree({
					selector : "#interface_group_add",
					lUrl : oc.resource.getUrl("netflow/interfaceGroup/deviceInterface.htm"),
					dataType : 'json',
					requestType:'sync',
					isInteractive : true,
				});
	}

	$("#interface_group_add_query")
			.click(
					function() {
						var jsonArray = interface_group_tree.getRightTreeData();
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
						oc.util
								.ajax({
									url : oc.resource
											.getUrl('netflow/interfaceGroup/deviceInterface.htm'),
									data : {
										"name" : $(
												"#interface_group_add_query_name")
												.val(),
										"notIds" : ids,
									},
									success : function(data) {
										interface_group_tree.reload(data.data,
												"l");
									}
								});
					});
});