function AttrTip(args){
	this.args=$.extend({
		pos:{
			x:0,y:0
		},
		holder:$("body"),
		show:true,
		onLoad:function(){},
		value:[]
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/backboard/smallWindow/AttrTip.html"),
		type:"get",
		success:function(html){
			ctx.init(html);
			ctx.args.onLoad.call(ctx);
		},
		dataType:"html"
	});
};
AttrTip.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.css({
			position:"absolute",
			"z-index":1
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.root.appendTo(this.args.holder);
		this.template=this.fields.template;
		this.regEvent();
		if(this.args.show){
			this.show(this.args.pos);
		}
		this.w=parseInt(this.root.css("width"));
		this.h=parseInt(this.root.css("height"));
	},
	regEvent:function(){
		var ctx = this;
		this.fields.closeBtn.on("click",function(){
			ctx.hide();
		});
		this.root.on("mouseleave",function(){
			ctx.hide();
		});
	},
	setInstanceId:function(){
		
	},
	setValue:function(items){
		this.root.find("tr.item").remove();
		for(var i=0;i<items.length;++i){
			var item = items[i];
			this.fields.label.text(item.text);
			this.fields.value.text(item.value||" - ");
			var tmp = this.fields.template.clone();
			tmp.addClass("item");
			tmp.removeClass("hide");
			tmp.removeAttr("data-field");
			this.fields.table.append(tmp);
		}
	},
	hide:function(){
		this.root.fadeOut();
	},
	show:function(e){
		oc.util.showFloat(this.root,e);
		/*var off = this.args.holder.offset();
		var w = parseInt(this.root.css("width")),h=parseInt(this.root.css("height"));
		this.root.css({
			top:pos.y-off.top-h/2,
			left:pos.x-off.left-w/2
		});
		this.root.fadeIn();*/
	}
};