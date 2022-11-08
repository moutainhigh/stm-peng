/**
 * 添加网络设备（资源面板的网络设备拖拽到画布当中，完成网络设备的自定义添加）
 * 两种添加形式
 * 	1 添加节点
 * 	2 添加节点同时做后台发现
 */
function AddNetDevice(){
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/AddNetDevice.html"),
		type:"get",
		success:function(html){
			ctx.init(html);
		},
		dataType:"html"
	});
};
AddNetDevice.prototype={
	init:function(html){
		this.dia=$(html);
		var ctx = this;
		this.dia.dialog({
			width:350,height:280,
			title:"添加网络设备",
			buttons:[{
				text:"确定",handler:function(){
					if(ctx.onok){
						ctx.onok(ctx.getValue());
					}
					ctx.dia.dialog("close");
				}
			},{
				text:"取消",handler:function(){
					ctx.dia.dialog("close");
				}
			}]
		});
		//搜索绑定的域
		this.fields={};
		this.dia.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.fields.type.combobox({
			editable:false
		});
		//赋值
		if(this.node){
			this.setValue({
				manageIp:this.node.d.ip,
				type:this.node.d.type
			});
		}
		this.fields.manageIp.validatebox({
			validType:"ip"
		});
		this.fields.showName.validatebox({
			required:true
		});
		this.fields.commonBodyName.validatebox({
			required:false
		});
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this,
			fd	= this.fields;
		fd.isFind.on("change",function(nv,ov){
			var checked = $(this).prop("checked");
			if(checked){
				fd.manageIp.validatebox({
					required:true
				});
				fd.showName.validatebox({
					required:false
				});
				fd.commonBodyName.validatebox({
					required:true
				});
			}else{
				fd.manageIp.validatebox({
					required:false
				});
				fd.showName.validatebox({
					required:true
				});
				fd.commonBodyName.validatebox({
					required:false
				});
			}
		});
	},
	getValue:function(){
		if(this.fields.manageIp.validatebox("isValid") && this.fields.showName.validatebox("isValid") && this.fields.commonBodyName.validatebox("isValid")){
			var value={
				manageIp:this.fields.manageIp.val(),
				commonBodyName:this.fields.commonBodyName.val(),
				type:this.fields.type.combobox("getValue"),
				name:this.fields.showName.val(),
				isFind:this.fields.isFind.prop("checked")
			};
			return value;
		}else{
			if(!this.fields.manageIp.validatebox("isValid")){
				this.fields.manageIp.focus();
			}else if(!this.fields.showName.validatebox("isValid")){
				this.fields.showName.focus();
			}else if(!this.fields.commonBodyName.validatebox("isValid")){
				this.fields.commonBodyName.focus();
			}
			throw "manageIp must not be null";
		}
	},
	setValue:function(value){
		for(var key in this.fields){
			this.fields[key].val(value[key]);
		}
		this.fields.type.combobox('setValue',value.type);
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	}
};