function QueryNode(args){
	this.args=$.extend({
		url:"topo/query.htm",
		node:null
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/QueryNode.html"),
		type:"get",
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
}
QueryNode.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		if(this.args.node){
			this.root.appendTo(this.args.node);
		}
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
	},
	getValue:function(){
		var retn = {};
		for(var key in this.fields){
			var val = this.fields[key].val();
			if(val && val != ""){
				retn[key]=val;
			}
		}
		return retn;
	},
	query:function(callBack){
		if(callBack){
			oc.util.ajax({
				url:oc.resource.getUrl(this.args.url),
				datType:"json",
				data:this.getValue(),
				success:function(data){
					callBack(data);
				}
			});
		}
	}
};