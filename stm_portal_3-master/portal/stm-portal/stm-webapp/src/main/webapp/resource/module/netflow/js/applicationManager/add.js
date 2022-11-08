function del_app_info(obj){
	$(obj).parent().remove();
}

function edit_app_ip(obj){
	var obj = $(obj);
	obj.attr("id","select_li_app_ip");
	var ips = obj.text();
	var ip_array = ips.split("-");
	$("#start_ip").val(ip_array[0]);
	if(ip_array.length == 2){
		$("#end_ip").val(ip_array[1]);
	}
}

function edit_app_port(obj){
	var obj = $(obj);
	obj.attr("id","select_li_app_port");
	var ports = obj.text();
	var port_array = ports.split("-");
	$("#start_port").val(port_array[0]);
	if(port_array.length == 2){
		$("#end_port").val(port_array[1]);
	}
}

$(function() {
	if(app_group_id != null){
		oc.util.ajax({
			url : oc.resource.getUrl("netflow/applicationManager/get.htm"),
			data : {
				"id" : app_group_id
			},
			success : function(data) {
				if(data && data.code == 200){
					$("#app_group_id").val(data.data.id);
					$("#app_name").val(data.data.name);
					if(data.data.ports){
						var html = "";
						var ports = data.data.ports.split(",");
						for (var i = 0; i < ports.length; i++) {
							html += '<li><span class="li_value" onClick="edit_app_port(this)">' + ports[i] + '</span> <span class="ico ico-cancel" onClick="del_app_info(this)"></span></li>';
						};
						$(".port_data").html(html);
					}
					if(data.data.ips){
						var html = "";
						var ips = data.data.ips.split(",");
						for (var i = 0; i < ips.length; i++) {
							html += '<li><span class="li_value" onClick="edit_app_ip(this)">' + ips[i] + '</span> <span class="ico ico-cancel" onClick="del_app_info(this)"></span></li>';
						};
						$(".ip_data").html(html);
					}
					oc.ui.form({
						selector : $(".fixedvaluefromul"),
						combobox : [ {
							selector : '[id=protocol_type]',
							filter : function(data) {
								return data.data;
							},
							url : oc.resource.getUrl('netflow/applicationManager/protocols.htm'),
							placeholder : false,
							selected : data.data.protocolName
						} ]
					});
				}
			}
		});
	}

	if(app_group_id == null){
		oc.ui.form({
			selector : $(".fixedvaluefromul"),
			combobox : [ {
				selector : '[id=protocol_type]',
				filter : function(data) {
					return data.data;
				},
				url : oc.resource
						.getUrl('netflow/applicationManager/protocols.htm'),
				placeholder : false
			} ]
		});
	}


	$("#port_add")
			.click(
					function() {
						var start_port = $("#start_port").val();
						var end_port = $("#end_port").val();
						var port_p = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
						if (!port_p.test(start_port)) {
							alert('【起始端口号】不能为空,且必须是0到65535之间的数！');
						} else if (end_port != "" && !port_p.test(end_port)) {
							alert('【结束端口号】必须是0到65535之间的数！');
						} else if (end_port != ""
								&& parseInt(start_port) > parseInt(end_port)) {
							alert('【结束端口号】必须大于或等于【起始端口号】！');
						} else {
							var obj = $("#select_li_app_port");
							var info = start_port
										+ (end_port != "" ? "-" + end_port : "");
							var bol = true;
							var portLis = $(".port_data>li>.li_value");
							if(portLis && portLis.length > 0){
								for (var i = 0; i < portLis.length; i++) {
									if(info == $(portLis[i]).html()){
										bol = false;
										alert("不能添加重复端口!");
										return;
									}
								};
							}
							if(bol){
								if(obj[0]){
									obj.html(info);
									obj.removeAttr("id");
								} else {
									$(".port_data").append('<li><span class="li_value" onClick="edit_app_port(this)">'
											+ info
											+ '</span> <span class="ico ico-cancel" onClick="del_app_info(this)"></span></li>');
								}
								$("#start_port").val("");
								$("#end_port").val("");
							}
						}
					});

	$("#ip_add")
			.click(
					function() {
						var start_ip = $("#start_ip").val();
						var end_ip = $("#end_ip").val();
						var ip_p = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
						if (!ip_p.test(start_ip)) {
							alert('【起始IP地址】不能为空,且必须填写正确IP地址!');
						} else if (end_ip != "" && !ip_p.test(end_ip)) {
							alert('【结束IP地址】必须填写正确的IP地址!');
						} else {
							var obj = $("#select_li_app_ip");
							var info =  start_ip + (end_ip != "" ? "-" + end_ip : "");
							var bol = true;
							var ipLis = $(".ip_data>li>.li_value");
							if(ipLis && ipLis.length > 0){
								for (var i = 0; i < ipLis.length; i++) {
									if(info == $(ipLis[i]).html()){
										bol = false;
										alert("不能添加重复IP地址!");
										return;
									}
								};
							}
							if(bol){
								if(obj[0]){
									obj.html(info);
									obj.removeAttr("id");
								} else {
									$(".ip_data").append('<li><span class="li_value" onClick="edit_app_ip(this)">'+info+'</span> <span class="ico ico-cancel" onClick="del_app_info(this)"></span></li>');
								}
								$("#start_ip").val("");
								$("#end_ip").val("");
							}
						}
					});
});