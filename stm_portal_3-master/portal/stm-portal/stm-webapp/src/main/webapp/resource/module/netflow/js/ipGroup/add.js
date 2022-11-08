function del_ip_addr(obj){
	$(obj).parent().remove();
}

function edit_ip_addr(obj){
	var obj = $(obj);
	obj.attr("id","select_li_ip");
	var ips = obj.text();
	var ip_array = ips.split("-");
	var ip_mask = ips.split("/");
	if(ip_array.length == 2){
		$("#ip_addr_type").combobox("setValue","2");
		$("#input_start_ip").val(ip_array[0]);
		$("#input_end_ip").val(ip_array[1]);

		$("#ip_li_ordinary_ip").hide();
		$("#ip_li_cidr").hide();
		$("#ip_li_netmask").hide();
		$("#ip_li_start_ip").show();
		$("#ip_li_end_ip").show();
	} else if(ip_mask.length == 3){
		$("#ip_addr_type").combobox("setValue","3");
		$("#ip").val(ip_mask[0]);
		$("#ip_addr_cidr").combobox("setValue",ip_mask[1]);
		$("#ip_span_netmask").html(ip_mask[2]);

		$("#ip_li_start_ip").hide();
		$("#ip_li_end_ip").hide();
		$("#ip_li_ordinary_ip").show();
		$("#ip_li_cidr").show();
		$("#ip_li_netmask").show();
	} else {
		$("#ip").val(ips);
		$("#ip_addr_type").combobox("setValue","1");

		$("#ip_li_start_ip").hide();
		$("#ip_li_end_ip").hide();
		$("#ip_li_cidr").hide();
		$("#ip_li_netmask").hide();
		$("#ip_li_ordinary_ip").show();
	}
}

$(function() {

	if(ip_group_id != null){
		oc.util.ajax({
			url : oc.resource.getUrl('netflow/ipGroup/get.htm'),
			data : {
				"id" :ip_group_id
			},
			success : function(data) {
				if(data && data.code == 200){
					$("#ip_group_id").val(data.data.id);
					$("#ip_group_name").val(data.data.name);
					$("#description").val(data.data.description);
					if(data.data.ips != null && data.data.ips != ""){
						var ips = data.data.ips.split(",");
						var html = "";
						for (var i = 0; i < ips.length; i++) {
							html += '<li><span class="li_value" onClick="edit_ip_addr(this)">'+ips[i]+'</span> <span class="ico ico-cancel" onClick="del_ip_addr(this)"></span></li>';
						};
						$(".r-content").html(html);
					}
				}
			}
		});
	}

	oc.ui.form({
		selector : $("#Batch"),
		combobox : [ {
			selector : '[id=ip_addr_type]',
			data : [ {
				id : '1',
				name : '单一地址'
			}, {
				id : '2',
				name : 'IP范围'
			}, {
				id : '3',
				name : '子网'
			} ],
			placeholder : false,
			onSelect : function(record){
				switch (record.id) {
				case "1":
					$("#ip_li_start_ip").hide();
					$("#ip_li_end_ip").hide();
					$("#ip_li_cidr").hide();
					$("#ip_li_netmask").hide();
					$("#ip").val("");
					$("#ip_li_ordinary_ip").show();
					break;
				case "2":
					$("#ip_li_ordinary_ip").hide();
					$("#ip_li_cidr").hide();
					$("#ip_li_netmask").hide();
					$("#input_start_ip").val("");
					$("#input_end_ip").val("");
					$("#ip_li_start_ip").show();
					$("#ip_li_end_ip").show();
					break;
				case "3":
					$("#ip_li_start_ip").hide();
					$("#ip_li_end_ip").hide();
					$("#ip").val("");
					$("#ip_addr_cidr").combobox("setValue","8");
					$("#ip_span_netmask").html("255.0.0.0");
					$("#ip_li_ordinary_ip").show();
					$("#ip_li_cidr").show();
					$("#ip_li_netmask").show();
					break;
				}
			}
		},
		{
			selector : '[id=ip_addr_cidr]',
			data : [{
				id : '1',
				name : '1'
			},{
				id : '2',
				name : '2'
			},{
				id : '3',
				name : '3'
			},{
				id : '4',
				name : '4'
			},{
				id : '5',
				name : '5'
			},{
				id : '6',
				name : '6'
			},{
				id : '7',
				name : '7'
			},{
				id : '8',
				name : '8'
			},{
				id : '9',
				name : '9'
			},{
				id : '10',
				name : '10'
			},{
				id : '11',
				name : '11'
			},{
				id : '12',
				name : '12'
			},{
				id : '13',
				name : '13'
			},{
				id : '14',
				name : '14'
			},{
				id : '15',
				name : '15'
			},{
				id : '16',
				name : '16'
			},{
				id : '17',
				name : '17'
			},{
				id : '18',
				name : '18'
			},{
				id : '19',
				name : '19'
			},{
				id : '20',
				name : '20'
			},{
				id : '21',
				name : '21'
			},{
				id : '22',
				name : '22'
			},{
				id : '23',
				name : '23'
			},{
				id : '24',
				name : '24'
			},{
				id : '25',
				name : '25'
			},{
				id : '26',
				name : '26'
			},{
				id : '27',
				name : '27'
			},{
				id : '28',
				name : '28'
			},{
				id : '29',
				name : '29'
			},{
				id : '30',
				name : '30'
			},{
				id : '31',
				name : '31'
			},{
				id : '32',
				name : '32'
			}],
			selected : '8',
			placeholder : false,
			onSelect : function(record){
				var html = "";
				switch (record.id) {
				case '0':
					html = "0.0.0.0";
					break;
				case '1':
					html = "128.0.0.0";
					break;
				case '2':
					html = "192.0.0.0";
					break;
				case '3':
					html = "224.0.0.0";
					break;
				case '4':
					html = "240.0.0.0";
					break;
				case '5':
					html = "248.0.0.0";
					break;
				case '6':
					html = "252.0.0.0";
					break;
				case '7':
					html = "254.0.0.0";
					break;
				case '8':
					html = "255.0.0.0";
					break;
				case '9':
					html = "255.128.0.0";
					break;
				case '10':
					html = "255.192.0.0";
					break;
				case '11':
					html = "255.224.0.0";
					break;
				case '12':
					html = "255.240.0.0";
					break;
				case '13':
					html = "255.248.0.0";
					break;
				case '14':
					html = "255.252.0.0";
					break;
				case '15':
					html = "255.254.0.0";
					break;
				case '16':
					html = "255.255.0.0";
					break;
				case '17':
					html = "255.255.128.0";
					break;
				case '18':
					html = "255.255.192.0";
					break;
				case '19':
					html = "255.255.224.0";
					break;
				case '20':
					html = "255.255.240.0";
					break;
				case '21':
					html = "255.255.248.0";
					break;
				case '22':
					html = "255.255.252.0";
					break;
				case '23':
					html = "255.255.254.0";
					break;
				case '24':
					html = "255.255.255.0";
					break;
				case '25':
					html = "255.255.255.128";
					break;
				case '26':
					html = "255.255.255.192";
					break;
				case '27':
					html = "255.255.255.224";
					break;
				case '28':
					html = "255.255.255.240";
					break;
				case '29':
					html = "255.255.255.248";
					break;
				case '30':
					html = "255.255.255.252";
					break;
				case '31':
					html = "255.255.255.254";
					break;
				case '32':
					html = "255.255.255.255";
					break;
				}
				$("#ip_span_netmask").html(html);
			}
		}]
	});

	$(".tree-arrow-right").click(function() {
		var ipObjs = $(".r-content>li>.li_value");
		var obj = $("#select_li_ip");
		var ip_type = $("#ip_addr_type").combobox("getValue");
		var ip_p = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
		var bol = true;
		switch(ip_type){
			case '1':
				var ip = $("#ip").val();
				if(ip == ""){
					alert('请输入IP地址!');
				} else if(!ip_p.test(ip)){
					alert('输入的IP不是有效的IP地址!');
				} else {
					if(ipObjs && ipObjs.length > 0){
						for (var i = 0; i < ipObjs.length; i++) {
							if(ip == $(ipObjs[i]).html()){
								bol = false;
								alert("不能添加重复IP地址!");
								return;
							}
						};
					}
					if(bol){
	 					if(obj[0]){
							obj.html(ip);
							obj.removeAttr("id");
						} else {
							$(".r-content").append('<li><span class="li_value" onClick="edit_ip_addr(this)">'+ip+'</span> <span class="ico ico-cancel" onClick="del_ip_addr(this)"></span></li>');
						}
						$("#ip").val("");
					}
				}
				break;
			case '2':
				var start_ip = $("#input_start_ip").val();
				var end_ip = $("#input_end_ip").val();
				if(start_ip == ""){
					alert('请输入开始IP地址!');
				} else if(!ip_p.test(start_ip)){
					alert('输入的开始IP不是有效的IP地址!');
				} else if(end_ip == ""){
					alert('请输入结束IP地址!');
				} else if(!ip_p.test(end_ip)){
					alert('输入的结束IP不是有效的IP地址!');
				} else {
					if(ipObjs && ipObjs.length > 0){
						for (var i = 0; i < ipObjs.length; i++) {
							if(ip == $(ipObjs[i]).html()){
								bol = false;
								alert("不能添加重复IP地址!");
								return;
							}
						};
					}
					if(bol){
						if(obj[0]){
							obj.html(start_ip + "-" + end_ip);
							obj.removeAttr("id");
						} else {
							$(".r-content").append('<li><span class="li_value" onClick="edit_ip_addr(this)">' + start_ip + "-" + end_ip + '</span> <span class="ico ico-cancel" onClick="del_ip_addr(this)"></span></li>');
						}
						$("#input_start_ip").val("");
						$("#input_end_ip").val("");
					}
				}
				break;
			case '3':
				var ip_mask = $("#ip").val();
				if(ip_mask == ""){
					alert('请输入IP地址!');
				} else if(!ip_p.test(ip_mask)){
					alert('输入的IP不是有效的IP地址!');
				} else {
					if(ipObjs && ipObjs.length > 0){
						for (var i = 0; i < ipObjs.length; i++) {
							if(ip == $(ipObjs[i]).html()){
								bol = false;
								alert("不能添加重复IP地址!");
								return;
							}
						};
					}
					if(bol){
						if(obj[0]){
							obj.html(ip_mask + "/" + $("#ip_addr_cidr").combobox("getValue") + "/" + $("#ip_span_netmask").html());
							obj.removeAttr("id");
						} else {
							$(".r-content").append('<li><span class="li_value" onClick="edit_ip_addr(this)">' + ip_mask + "/" + $("#ip_addr_cidr").combobox("getValue") + "/" + $("#ip_span_netmask").html() + '</span> <span class="ico ico-cancel" onClick="del_ip_addr(this)"></span></li>');
						}
						$("#ip").val("");
						$("#ip_addr_cidr").combobox("setValue","8");
						$("#ip_span_netmask").html("255.0.0.0");
					}
				}
				break;
		}
	});
});