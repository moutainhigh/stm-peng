function TopoNodeAttr(args){
	this.args = $.extend({
		dialog:true,
		title:"属性",
		node:null,
		w:300,
		h:200
	},args);
	if(this.args.node){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoNodeAttr.html"),
			success:function(html){
				ctx.init(html);
			},
			type:"get",
			dataType:"html"
		});
	}else{
		throw "bad arguments!";
	}
};
TopoNodeAttr.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		if(this.args.dialog){
			this.root.dialog({
				title:this.args.title,
				width:this.args.w,
				height:this.args.h,
				buttons:[{
					text:"取消",handler:function(){
						ctx.root.dialog("close");
					}
				},{
					text:"确定",handler:function(){
						ctx.save();
						ctx.root.dialog("close");
					}
				}]
			});
		}
		//设备发光级别
		this.fields.glow.combobox({
			width:40,
			data:[{
				value:0
			},{
				value:1
			},{
				value:2
			},{
				value:3
			},{
				value:4
			},{
				value:5
			}],
			textField:"value",
			valueField:"value"
		});
		this.fields.glow.combobox("setValue",this.args.node.d.glow||this.args.node.d.rx||1);
	},
	getValue:function(){
		var glow = this.fields.glow.combobox("getValue");
		return {glow:parseInt(glow)};
	},
	save:function(){
		var v = this.getValue();
		if(v){
			this.args.node.d.rx=v.glow;
			this.args.node.playGlow(v.glow);
		}
	}
};