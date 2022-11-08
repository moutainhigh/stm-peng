function BizBusinessView(args){
	this.args = $.extend({
		drawer:null
	},args);
	if(this.args.drawer){
		this.init();
	}else{
		throw "drawer can't be null";
	}
};
BizBusinessView.prototype = {
	init:function(){
		this.eles={
			nodes:{},
			links:{}
		};
		this.factory = new BizElementsFactory({
			drawer:this.args.drawer
		});
	},
	getNodeById:function(id){
		return this.eles.nodes[id];
	},
	setValue:function(eles){
		//绘制节点
		if(!eles) return ;
		if(eles.nodes){
			for(var i=0;i<eles.nodes.length;i++){
				var nodeInfo = eles.nodes[i];
				var node = this.factory.getNode(nodeInfo);
				//保存id和节点的关系，便于引用查找
				this.eles.nodes[nodeInfo.id]=node;
			}
		}
		//绘制连线
		if(eles.links){
			for(var i=0;i<eles.links.length;i++){
				var linkInfo = eles.links[i];
				var link = this.factory.getLink(linkInfo,this.getNodeById(linkInfo.from),this.getNodeById(linkInfo.to));
				//保存id和节点的关系，便于引用查找
				this.eles.links[linkInfo.id]=link;
			}
		}
	},
	getValue:function(){
		var result={
			nodes:[],
			links:[]
		};
		for(var k in this.eles.nodes){
			var node = this.eles.nodes[k].getValue();
			result.nodes.push(node);
		}
		for(var k in this.eles.links){
			var link = this.eles.links[k].getValue();
			result.links.push(link);
		}
		return result;
	}
};