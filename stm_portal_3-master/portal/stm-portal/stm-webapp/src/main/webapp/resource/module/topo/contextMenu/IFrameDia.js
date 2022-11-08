function IFrameDia(args){
	this.args=$.extend({
		w:700,
		h:400,
		title:"百度首页",
		url:"http://www.baidu.com",
		buttons:[],
		isShow:true
	},args);
	var iframe = $("<iframe/>");
	iframe.prop("src",this.args.url);
	iframe.css({
		width:"100%",height:"100%","border":"none"
	});
	var div = $("<div/>");
	div.append(iframe);
	div.dialog({
		title:this.args.title,
		width:this.args.w,
		height:this.args.h,
		buttons:this.args.buttons
	});
	if(!this.args.isShow){
		div.dialog("close");
	}
	this.root=div;
};
IFrameDia.prototype={
	show:function(){
		this.root.dialog("open");
	}
};