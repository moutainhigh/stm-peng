oc.resource.loadScripts([
    "resource/module/business-service/config/business/BizLink.js",
    "resource/module/business-service/config/business/Bizextend.js"
],function(){
	window.BizNormalLink=Bizextend(function(args){
		BizLink.apply(this,arguments);
	},BizLink,{
		init:function(){
			with(this.args){
				this.node=drawer.group();
				this.node.line=this.node.line(0,0,1,1).attr({stroke:"red"});
				this.regEvent();
			}
		},
		set_data:function(data){
			this.data=data;
		},
		get_data:function(){
			return this.data;
		},
		regEvent:function(){
			var ctx=this;
			this.fromNode.on("dragmove",function(){
				ctx.refresh();
			});
			this.toNode.on("dragmove",function(){
				ctx.refresh();
			});
		},
		getValue:function(){
			return this.get_data();
		},
		setValue:function(info){
			this.set_data(info);
			this.refresh();
		},
		refresh:function(attr){
			var fbox=this.fromNode.attr("rbox"),
				tbox=this.toNode.attr("rbox");
			this.node.line.plot(fbox.cx,fbox.cy,tbox.cx,tbox.cy);
		}
	});
});