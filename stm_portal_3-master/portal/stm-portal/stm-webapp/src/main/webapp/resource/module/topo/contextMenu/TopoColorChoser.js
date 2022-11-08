function TopoColorChoser(args){
	this.args=$.extend({
		
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoColorChoser.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
TopoColorChoser.prototype={
	init:function(html){
		this.root = $(html);
		this.root.css({
			position:"absolute"
		});
		this.root.appendTo($("body"));
		this.fields={};
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var $tmp = $(dom);
			ctx.fields[$tmp.attr("data-field")]=$tmp;
		});
		//初始化调色板
		var paper = Raphael(this.fields.colorPalette.get(0),160,170);
		this.cp = new ColorPalette({
			w:160,
			h:170,
			paper:paper
		});
		//扩展validatebox
		$.extend($.fn.validatebox.defaults.rules, {    
			color:{
				validator:function(v){
					var reg =/^#[0-9a-f]{6}$/;
					return reg.test(v);
				},
				message:'请输入正确的RGB(#00ffee)颜色值'
			}  
		}); 
		//初始化原色输入框
		this.fields.colorVl.validatebox({
			validType:"color",
			required:true
		});
		this.root.hide();
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		this.cp.on("over",function(color){
		});
		this.cp.on("selected",function(color){
			ctx.setValue(color);
			if(ctx.onselected){
				ctx.onselected(color);
				delete ctx.onselected;
			}
		});
		this.fields.colorVl.on("blur",function(){
			if(ctx.valid()){
				ctx.setValue(ctx.fields.colorVl.val());
				if(ctx.onselected){
					ctx.onselected();
					delete ctx.onselected;
				}
			}
		});
	},
	show:function(e){
		oc.util.showFloat(this.root,e);
	},
	hide:function(){
		this.root.hide();
	},
	valid:function(){
		var isValid = this.fields.colorVl.validatebox("isValid");
		if(!isValid){
			this.fields.colorVl.focus();
		}
		return isValid;
	},
	setValue:function(color){
		this.fields.colorBg.css("background-color",color);
		this.fields.colorVl.val(color);
	},
	getValue:function(){
		if(this.valid()){
			return this.fields.colorVl.val();
		}
	},
	onece:function(key,cb){
		this["on"+key]=cb;
	}
};