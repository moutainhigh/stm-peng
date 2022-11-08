function TopoApiFacade(args){
	this.args = $.extend({
		topoName:null,
		holder:null,
		topoId:null
	},args);
	var ctx = this;
	if(this.args.topoName && this.args.holder){
		$.post(oc.resource.getUrl("topo/subtopo/getSubTopoIdBySubTopoName.htm"),{name:this.args.topoName},function(result){
			if(result.status==200){
				ctx.init(result.id);
			}else{
				alert(result.msg,"warning");
			}
		},"json");
	}else if(this.args.topoId && this.args.holder){
		this.init(this.args.topoId);
	}else{
		throw "topoName || holder be null";
	}
};
TopoApiFacade.prototype={
	init:function(id){
		var ctx = this;
		$.post(oc.resource.getUrl("topo/getSubTopo/"+id+".htm"),function(result){
			ctx.shower = new TopoShower({
				holder:ctx.args.holder,
				data:result,
				onLoad:function(){
					var ta = new TopoAttr({
						topoId:id,
						topoShower:this
					});
					
					//刷新数据
					var td = new TopoData({
						shower:this,
						topoAttr:ta,
					});
					ctx.topoScale = new TopoScale({
						holder:this.args.holder
					});
					ctx.topoRange=ta.getRange();
					//自适应
					ctx.topoScale.selfAdapt(ctx.topoRange);
					
					var tz = new TopoZIndex({
						shower:this
					});
					tz.setZIndex();
				}
			});
		},"json");
	},
	refresh:function(){
		
	},
	fullScreen:function(){
		this.topoScale.fullScreen(this.topoRange);
		//测试getValue接口
		/*var v = this.shower.getValue();
		console.log(v);*/
	},
	exitFullScreen:function(){
		this.topoScale.exitFullScreen(this.topoRange);
	}
};