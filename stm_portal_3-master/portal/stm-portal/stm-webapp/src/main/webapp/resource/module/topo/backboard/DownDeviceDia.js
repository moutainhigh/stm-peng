function DownDeviceDia(args){
	this.args=$.extend({},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/backboard/DownDevice.html"),
		type:"get",
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
};
DownDeviceDia.prototype={
	init:function(html){
		this.root=$(html);
		var ctx = this;
		ctx.intDatagrid();
		this.root.dialog({
			width:800,height:560,
			title:"<span style='margin-right: 20px;'>设备名称："+ctx.args.deviceName+",</span><span style='margin-right: 20px;'>端口索引："+ctx.args.intfaceIndex+",</span><span style='margin-right: 20px;'>端口名称："+ctx.args.intface + "</span>"
		});
	},
	intDatagrid:function(){
		var ctx = this;
		oc.ui.datagrid({
			selector:ctx.root.find('#downDeviceDiv'),
			url : oc.resource.getUrl("topo/mac/updevice/infos.htm?ip="+ctx.args.ip+"&intface="+ctx.args.intface),
			singleSelect:false,
			pagination:false,
			title:'下联设备列表:',
			columns:[[
		         {field:'hostName',title:"设备名称",width:100},
		         {field:'ip',title:'IP地址',width:100},
		         {field:'mac',title:'MAC信息',width:100}
	         ]]
		});
	}
};