function TopoNodeDblClick(args){
	this.args = $.extend({
		dialog:true,
		title:"双击操作设置",
		key:null
	},args);
	var ctx = this;
	//加载视图模板
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoNodeDblClick.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
TopoNodeDblClick.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		if(this.args.dialog){
			this.root.dialog({
				title:this.args.title,
				width:370,height:200,
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
		//url
		this.fields.url.validatebox({
			validType:"url"
		});
		//map
		oc.util.ajax({
			url:oc.resource.getUrl("topo/0/subTopos.htm"),
			type:"get",
			success:function(results){
				var topos = [{
					text:"二层拓扑",
					id:0,
					children:results
				}];
				ctx.fields.map.combotree({
					data:topos,
					editable:false
				});
			}
		});
		this.form = new FormUtil("#topo_node_dblclick_setting");
		//获取初始值
		if(this.args.key){
			$.post(oc.resource.getUrl("topo/setting/get/"+this.args.key+".htm"),function(info){
				if(info){
					ctx.setValue(info);
				}
			},"json");
		}
	},
	getValue:function(){
		if(this.fields.url.validatebox("isValid")){
			var retn = this.form.getValue();
			retn.map=this.fields.map.combobox("getValue");
			retn.url=this.fields.url.val();
			return retn;
		}else{
			this.fields.url.focus();
			return null;
		}
	},
	setValue:function(info){
		this.form.setValue(info);
		if(info.map){
			this.fields.map.combotree("setValue",parseInt(info.map)||0);
		}
		this.fields.url.val(info.url);
	},
	save:function(){
		var ctx = this;
		if(this.args.key){
			var info = this.getValue();
			if(info){
				$.post(oc.resource.getUrl("topo/setting/save.htm"),{
					key:this.args.key,
					value:JSON.stringify(info)
				},function(){
					alert("配置保存成功");
					ctx.root.dialog("close");
				},"json");
			}
		}
	}
};