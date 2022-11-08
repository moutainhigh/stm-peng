function NodeAttr(args){
	this.args=$.extend({
		dialog:true,
		w:800,h:430,title:"编辑属性",
		load:function(){},
		topoId:0,
		node:null//required
	},args);
	var ctx = this;
	if(this.args.node){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/NodeAttr.html"),
			type:"get",
			success:function(html){
				ctx.init(html);
				ctx.args.load();
			},
			dataType:"html"
		});
	}
};
NodeAttr.prototype={
	init:function(html){
		var ctx = this;
		ctx.root=$(html);
		if(this.args.dialog){
			ctx.root.dialog({
				title:this.args.title,
				width:this.args.w,
				height:this.args.h,
				buttons:[{
					text:"确定",handler:function(){
						ctx.exe("ok",{value:ctx.getValue()});
						ctx.save();
						ctx.root.dialog("destroy");
					}
				},{
					text:"取消",handler:function(){
						ctx.root.dialog("destroy");
					}
				}]
			});
		}
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化
		this.fields.map.combobox({
			url:oc.resource.getUrl("topo/allTopos.htm"),
			textField:"name",
			valueField:"id",
			method:"post",
			value:0,
			editable:false
		});
		//初始化tab
		this.fields.tabCon.tabs({});
		
		//初始化静态文本输入看
		this.fields.textIpt.validatebox({
			required:true
		});
		this.fields.url.validatebox({
			validType:"url"
		});
		//初始化数据
//		$.post(oc.resource.getUrl("topo/setting/get/"+this.args.node.linktype+this.args.node.d.id+".htm"),function(data){
		$.post(oc.resource.getUrl("topo/setting/get/"+this.args.node.linktype+this.args.node.id+".htm"),function(data){
			ctx.setValue(data);
		},"json");
		this.regEvent();
	},
	//添加分组
	addGroup:function(title,callBack){
		var ctx = this;
		if(title && !this.newSubTopoDia){
			oc.resource.loadScript("resource/module/topo/contextMenu/NewSubTopoDia.js", function(){
				ctx.gridCon = $("<div/>");
				ctx.fields.tabCon.tabs("add",{
					title:title,
					content:ctx.gridCon,
					selected:true
				});
				ctx.newSubTopoDia = new NewSubTopoDia({
					dialog:false,
					topoId:ctx.args.topoId,
					load:function(nst){
						nst.root.appendTo(ctx.gridCon);
						nst.fields.name.val(title);
						nst.fields.subTopoTitleCon.hide();
						nst.root.css({
							width:782,height:320
						});
					}
				});
			});
		}
	},
	regEvent:function(){
		var fd = this.fields;
		fd.url.on("change",function(){
			if($(this).validatebox("isValid")){
				fd.openTypeUrl.prop("checked",true);
			}
		});
		fd.map.combobox({
			onChange:function(){
				fd.openTypeMap.prop("checked",true);
			}
		});
	},
	getValue:function(){
		var fd	 = this.fields;
		if(fd.url.validatebox("isValid")){
			var retn = {
				map:fd.map.combobox("getValue"),
				url:fd.url.val(),
				text:fd.textIpt.val()
			};
			if(fd.openTypeUrl.prop("checked")){
				retn.openType=fd.openTypeUrl.val();
			}else{
				retn.openType=fd.openTypeMap.val();
			}
			return retn;
		}else{
			throw "url not valid";
		}
	},
	setValue:function(v){
		var fd	 = this.fields;
		fd.url.val(v.url);
		fd.textIpt.val(v.text);
		this.fields.map.combobox("setValue",v.map);
		if(v.openType=="url"){
			fd.openTypeUrl.prop("checked",true);
		}else{
			fd.openTypeMap.prop("checked",true);
		}
	},
	//保存
	save:function(){
		var ctx = this;
		try{
			var value=this.getValue();
			//节点的值
			$.post(oc.resource.getUrl("topo/setting/save.htm"),{
//				key:ctx.args.node.linktype+ctx.args.node.d.id,
				key:ctx.args.node.linktype+ctx.args.node.id,
				value:JSON.stringify(value)
			},function(){
				alert("保存成功");
			},"json");
		}catch(e){
			alert(e,"warning");
		}
	},
	on:function(key,callBack){
		if(key.indexOf("on")<0){
			key="on"+key;
		}
		if(this.key){
			this.key.push(callBack);
		}else{
			this.key=[callBack];
		}
	},
	exe:function(key,params){
		if(this.key){
			for(var i=0;i<this.key.length;++i){
				var cb = this.key[i];
				cb.call(this,params);
			}
		}
	}
}