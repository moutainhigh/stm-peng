$(function() {
	oc.util.ajax({
		url : oc.resource.getUrl('netflow/license/getConf.htm'),
		success : function(data) {
			if(data && data.code == 200 && data.data && data.data != null){
				$("#license_ip").val(data.data.ip);
				$("#license_port").val(data.data.port == null ? "6379" : data.data.port);
			}
		}
	});
	var user = oc.index.getUser();
	if(user.systemUser){
		$("#license_submitForm").click(function() {
			var ip_p = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
			var ip = $("#license_ip").val();
			var port = $("#license_port").val();
			if (ip == "") {
				alert('请输入IP地址!');
			} else if (!ip_p.test(ip)) {
				alert('不是有效的IP地址！');
			} else if(port == ""){
				alert('请输入端口号!');
			} else if (!(/^\d+$/.test(port) && port >= 1 && port <= 65535)){
				alert('不是有效端口号(有效端口号范围:1-65535)');
			} else {
				oc.util.ajax({
					url : oc.resource.getUrl('netflow/license/register.htm'),
					data : {
						"ip" : ip,
						"port" : port
					},
					success : function(data) {
						if (data && data.code == 200 && data.data == 1) {
							alert("配置成功！");
						} else {
							alert("配置失败！请检查IP，端口是否正确.");
						}
					}
				});
			}
		});
	}else{
		$("#license_ip").attr("disabled","true");
		$("#license_port").attr("disabled","true");
		$("#license_submitForm").css("display","none");
	}
	
});
