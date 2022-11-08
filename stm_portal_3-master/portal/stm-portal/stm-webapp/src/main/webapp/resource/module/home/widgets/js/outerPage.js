function outerPage(selector,opt){
	this.opt = {
		url:'https://my.oschina.net/xhload3d/blog/206993'
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var opp = outerPage.prototype;

opp.init = function(){
	this.dataObj = $("<iframe/>");
	this.dataObj.css({width:'100%',height:'100%',border:'0px'});
	this.dataObj.attr("frameborder","0").attr("allowtransparency","true");
	this.dataObj.css({filter:"chroma(color=#ffffff)"});
	this.opt.selector.html(this.dataObj);
	this.dataObj.attr("src",this.opt.url);

}

opp.refresh = function(){
	this.init();
}
