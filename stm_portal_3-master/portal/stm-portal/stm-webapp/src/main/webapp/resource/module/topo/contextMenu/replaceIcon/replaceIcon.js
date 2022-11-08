function ReplaceIconDia(node){
	this.node=node;
	this.nodeVal=this.node.getValue();
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/replaceIcon/replaceIcon.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
ReplaceIconDia.prototype={
	init:function(html){
		var ctx = this;
		this.dia = $(html);
		this.dia.dialog({
			width:700,
			height:430,
			title:"替换图标",
			buttons:[{
				text:"恢复默认",handler:function(){
					ctx.src=ctx.fields.defaultIcon.attr("src");
					ctx.save();
				}
			},{
				text:"保存",handler:function(){
					ctx.save();
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
		this.initUi();
		this.regEvent();
	},
	//绑定事件
	regEvent:function(){
		var ctx = this;
		this.fields.sysDefaultIcon.find("img").click(function(){
			ctx.replaceIcon($(this));
		});
		this.fields.customIcon.find("img").click(function(){
			ctx.replaceIcon($(this));
		});
	},
	//替换图标
	replaceIcon:function($img){
		this.fields.previewIcon.removeClass("hide");
		this.fields.previewIcon.attr("src",$img.attr("src"));
	},
	//初始化ui包括它们的数据
	initUi:function(){
		var ctx = this;
		this.dia.find(".easyui-linkButton").linkbutton("RenderLB");
		this.dia.find(".easyui-panel").panel();
		//修改当前使用的图标
		this.fields.currentIcon.attr("src",this.nodeVal.icon.replace("resource/",""));
		//加载自定义图标
		$.get(oc.resource.getUrl("topo/image/getByType/net.htm"),function(icons){
			for(var i=0;i<icons.length;++i){
				var icon = icons[i];
				var img = $("<img/>");
				img.attr("src",oc.resource.getUrl("topo/image/change.htm?path="+icon.fileId));
				ctx.fields.customIcon.append(img);
				img.click(function(){
					ctx.replaceIcon($(this));
				});
			}
		},"json");
		//获取默认图标
		$.get(oc.resource.getUrl("topo/image/"+this.node.d.type+"/default.htm"),function(icon){
			ctx.fields.defaultIcon.attr("src",icon);
			ctx.fields.defaultIcon.removeClass("hide");
		},"text");
	},
	//对话框保存
	save:function(){
		var src = this.src||this.fields.previewIcon.attr("src");
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/replaceIcon.htm"),
			data:{
				id:this.node.d.rawId,
				src:src
			},
			success:function(){
				ctx.node.img.attr("src",src);
				ctx.node.setSrc(src);
				ctx.node.setState();
				ctx.dia.dialog("close");
			}
		});
	}
};