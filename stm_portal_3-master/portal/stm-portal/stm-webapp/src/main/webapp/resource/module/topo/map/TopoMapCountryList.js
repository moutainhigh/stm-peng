function TopoMapCountryList(args){
	var ctx = this;
	this.args=$.extend({
		onLoad:function(){},
		holder:null
	},args);
	if(this.args.holder){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/map/TopoMapCountryList.html"),
			success:function(htl){
				ctx.init(htl);
			},
			dataType:"html",
			type:"get"
		});
	}else{
		throw "bad arguments";
	}
};
TopoMapCountryList.prototype={
	init:function(htl){
		this.root=$(htl);
		this.root.appendTo(this.args.holder);
		this.fields={};
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.regEvent();
		var img = this.fields.template.find("img");
		img.attr("src",oc.resource.getUrl(img.attr("src")));
		this.args.onLoad.call(this);
	},
	regEvent:function(){
		var fd = this.fields,
			ctx = this;
	},
	show:function(){
		this.root.show();
	},
	hide:function(){
		this.root.hide();
	},
	setValue:function(value){
		var fd = this.fields;
		fd.title.text(value.title);
		fd.container.find(".list_item").remove();
		if(value.nodes){
			for(var i=0;i<value.nodes.length;i++){
				var node = value.nodes[i];
				var tmp = fd.template.clone();
				tmp.removeClass("hide");
				tmp.addClass("list_item");
				tmp.find(".srcName").text(node.srcName);
				tmp.find(".srcName").attr({
					title:node.srcName
				});
				tmp.find(".desName").text(node.desName);
				tmp.find(".desName").attr({
					title:node.desName
				});
				tmp.find(".des_flag").addClass(node.desState);
				tmp.find(".src_flag").addClass(node.srcState);
				tmp.appendTo(fd.container);
			}
		}
	}
};