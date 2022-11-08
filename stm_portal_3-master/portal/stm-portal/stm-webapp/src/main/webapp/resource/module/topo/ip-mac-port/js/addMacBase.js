var form = null;
(function($){
	var mainDiv = $('#addStandardCcontainer');
	//mianDiv.css("padding","10px");//添加样式（Luo）
	var fm = mainDiv.find("form:first");
	form=oc.ui.form({
		selector:fm
	});
	//添加自定义验证规则：
	$.extend($.fn.validatebox.defaults.rules, {
		upDeviceInterface: {
        	validator : function(value) {
                return /^[a-zA-Z0-9\/ ]{1,}$/.test(value);
            },
            message : '请输入英文、数字、斜线'
        }
	});
})(jQuery);

//保存基准信息
function saveMacBase(datagrid,dia){
	var hostName = $.trim($("#hostName").val());
	var mac = $.trim($("#mac").val());
	var ip = $.trim($("#ip").val());
	var upDeviceName = $.trim($("#upDeviceName").val());
	var upDeviceIp = $.trim($("#upDeviceIp").val());
	var upDeviceInterface = $.trim($("#upDeviceInterface").val());
    var upRemarks = $.trim($("#upRemarks").val());

	if(form.validate()){
		oc.util.ajax({
			url : oc.resource.getUrl('topo/mac/base/save.htm'),
			failureMsg:'保存失败',
			successMsg:null,
			data : {
				"hostName" : hostName,
				"ip" : ip,
				"mac" : mac,
				"upDeviceName" : upDeviceName,
				"upDeviceIp":upDeviceIp,
                "upDeviceInterface": upDeviceInterface,
                "upRemarks": upRemarks
			},
			success : function(data) {
				if(data.code == 200){
					datagrid.reLoad();
					dia.dialog("close");
				}
			}
		});
	}
}