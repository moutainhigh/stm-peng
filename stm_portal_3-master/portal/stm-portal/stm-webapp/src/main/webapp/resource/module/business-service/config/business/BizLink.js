window.BizLink=Bizextend(function(args){
	args=$.extend({
		fromNode:null,
		toNode:null
	},args);
	if(args.fromNode && args.toNode){
		this.attr({
			fromNode:args.fromNode,
			toNode:args.toNode
		});
		BizBase.apply(this,arguments);
	}else{
		throw "fromNode or toNode can't be null";
	}
},BizBase,{
	set_fromNode:function(node){
		this.fromNode=node;
	},
	get_fromNode:function(node){
		return this.fromNode;
	},
	set_toNode:function(node){
		this.toNode=node;
	},
	get_toNode:function(node){
		return this.toNode;
	}
});
