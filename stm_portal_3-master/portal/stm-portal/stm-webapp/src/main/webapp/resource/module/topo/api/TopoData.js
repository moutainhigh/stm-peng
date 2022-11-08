function TopoData(args){
	this.args = $.extend({
		shower:null,
		topoAttr:null
	},args);
	if(this.args.shower && this.args.shower instanceof TopoShower && this.args.topoAttr && this.args.topoAttr instanceof TopoAttr){
		this.init();
	}else{
		throw "bad arguments";
	}
};
TopoData.prototype={
	init:function(){
		this.refreshNodeData();
	},
	//刷新节点cpu利用率，ram利用率，厂商型号，显示名称
	refreshNodeData:function(){
		var ta = this.args.topoAttr;
		//获取厂商型号
		$.post(oc.resource.getUrl("topo/refreshVendorName.htm"),{
			ids:ta.state.nodesIds.join(",")
		},function(info){
			for(var i=0;i<info.length;++i){
				var t = info[i];
				var el = ta.state.nodesIdMap[t.id];
				if(el){
					el.d.vendorName=t.vendorName;
					el.d.series=t.series;
				}
			}
			ta.refreshAttr();
		},"json");
		//获取显示名称等链路数据
		$.post(oc.resource.getUrl("topo/refreshLifeState.htm"), {
			ids : ta.state.nodesInstanceIds.join(",")
		}, function(result) {
			var info = result.data;
			for (var i = 0; i < info.length; ++i) {
				var item = info[i];
				var els = ta.state.instanceMap[item.instanceId];
				if (els) {
					for (var j = 0; j < els.length; j++) {
						var el = els[j];
						if(item.showName){
							el.d.showName = item.showName;
						}
						if(item.lifeState){
							el.d.lifeState=item.lifeState;
							el.setLifeState(el.d.lifeState);
						}
					}
				}
			}
			//刷新属性
			ta.refreshAttr();
		}, "json");
	}
};