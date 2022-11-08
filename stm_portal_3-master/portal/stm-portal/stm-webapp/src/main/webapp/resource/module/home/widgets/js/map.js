//@ sourceURL=map.js
function map(selector,opt){
	this.opt = {
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var mapp = map.prototype;

mapp.init = function(){
	var _this = this;

	this.dataObj = $('<iframe scrolling="no"/>');
	this.dataObj.css({width:'100%',height:'100%',border:'0px'});
	this.dataObj.attr("frameborder","0").attr("allowtransparency","true");
	this.dataObj.css({filter:"chroma(color=#ffffff)"});
	this.opt.selector.html(this.dataObj);
	this.dataObj.attr("src",'module/home/widgets/js/map/nationMap.html?' + this.opt.id);
	
}

mapp.refresh = function(){
	this.init();
}
