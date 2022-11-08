function CreateRoom(args){
	this.args = $.extend({
		dialog:true,
		w:400,h:116,
		title:"新建机房",
		parentId:2,
		value:{},
		onOk:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/CreateRoom.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
CreateRoom.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		if(this.args.dialog){
			this.root.dialog({
				width:this.args.w,
				height:this.args.h,
				title:this.args.title,
				buttons:[{
					text:"确定",handler:function(){
						ctx.save();
					}
				},{
					text:"取消",handler:function(){
						ctx.root.dialog("close");
					}
				}]
			});
		}
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var $tmp = $(dom);
			ctx.fields[$tmp.attr("data-field")]=$tmp;
		});
		//验证
		this.fields.name.validatebox({
			required:true
		});
		//设置值
		this.setValue(this.args.value);
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		this.fields.name.on("keyup",function(e){
			if(e.keyCode==13){
				ctx.save();
			}
		});
	},
	setValue:function(v){
		var fd = this.fields;
		if(v.name){
			fd.name.val(v.name);
		}
		this.id=v.id;
	},
	getValue:function(){
		var retn = null;
		var fd = this.fields;
		if(fd.name.validatebox("isValid")){
			retn={
				name:fd.name.val()
			};
		}
		return retn;
	},
	on:function(key,cb){
		this["on"+key]=cb;
	},
	save:function(){
		var v = this.getValue();
		if(v){
			var ctx = this; 
			oc.util.ajax({
				url:oc.resource.getUrl("topo/subtopo/createOrUpdateSubtopo.htm"),
				data:{
					id:this.id,
					name:v.name,
					parentId:ctx.args.parentId,
					attr:JSON.stringify({
						type:"room"
					})
				},
				success:function(result){
					ctx.root.dialog("close");
					if(result.status==200){
						ctx.args.onOk.call(ctx,result.data);
						alert(result.msg);
					}else{
						alert(result.msg,"warning");
					}
				}
			});
		}
	}
};