function TopoScreenApi(args){
	this.args = $.extend({
		id:null,
		holder:null,
		onOver:function(){}
	},args);
	if(this.args.id!=undefined && this.args.holder){
		this.init(this.args.id);
	}else{
		throw "id(topo) can't be null";
	}
};
TopoScreenApi.prototype={
	init:function(id){
		var ctx = this;
		$.post(oc.resource.getUrl("topo/getSubTopo/"+id+".htm"),function(result){
			ctx.shower = new TopoShower({
				holder:ctx.args.holder,
				data:result,
				onLoad:function(){
					new TopoAttr({
						topoId:id,
						topoShower:this,
						onReady:function(){
							//刷新数据
							new TopoData({
								shower:ctx.shower,
								topoAttr:this,
							});
							ctx.topoScale = new TopoScale({
								holder:ctx.args.holder
							});
							ctx.topoRange=this.getRange();
							//自适应
							ctx.topoScale.selfAdapt(ctx.topoRange);
							//回掉
							ctx.args.onOver.call(ctx);
						}
					});
				}
			});
		},"json");
	}
};