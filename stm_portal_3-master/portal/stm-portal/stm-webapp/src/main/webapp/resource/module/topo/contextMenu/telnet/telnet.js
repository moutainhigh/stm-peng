function TopoTelnet(args,holder){
	this.holder=holder;
	this.args=args;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/telnet/telnet.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
TopoTelnet.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.find("iframe").attr("src","module/resource-management/resourceDetailInfo/tinytool/telnet.jsp?port=23&host="+this.args.node.d.ip);
		if(this.holder){
			this.root.appendTo(this.root);
		}
		//打开对话框
		this.root.dialog({
			width:800,height:468,
			title:"Telnet &nbsp;&nbsp;"+this.args.node.d.ip
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.regEvent();
	},
	regEvent:function(){
	}
};