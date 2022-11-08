function TopoZIndex(args){
	this.args = $.extend({
		shower:null
	},args);
	if(this.args.shower && this.args.shower instanceof TopoShower){
		this.init();
	}else{
		throw "bad arguments";
	}
};
TopoZIndex.prototype={
	init:function(){
		var holder = this.args.shower.args.holder;
		this.svg = holder.find("svg");
	},
	setZIndex:function(){
		var children = this.svg.children();
		$.each(children,function(idx,dom){
			dom.setAttribute("z-index",idx);
		});
	}
};