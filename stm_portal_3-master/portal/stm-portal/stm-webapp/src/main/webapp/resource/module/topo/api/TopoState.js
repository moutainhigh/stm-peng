function TopoState(args){
	this.args = $.extend({
		topoShower:null,
		cfg:null
	},args);
	if(this.args.topoShower && this.args.topoShower instanceof TopoShower){
		this.init();
	}else{
		throw "bad arguments";
	}
};
TopoState.prototype={
	init:function(){
		//处理对应关系
		this.handleRelation();
	},
	setCfg:function(cfg){
		this.cfg=$.extend({
			auto:true,
			interval:60000,//一分钟刷新一次
			linkMetricId:"device",//链路所使用的颜色指标
			nodeMetricId:"device"//节点使用的颜色指标
		},cfg);
		//刷新状态
		this.refreshState();
		if(this.cfg.auto){
			var ctx = this;
			if(this.refreshTimeId){
				clearTimeout(this.refreshTimeId);
			}
			this.refreshTimeId = setInterval(function(){
				ctx.refreshState();
			},this.cfg.interval);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(this.refreshTimeId);
			}else{
				tasks = new Array();
				tasks.push(this.refreshTimeId);
				oc.index.indexLayout.data("tasks", tasks);
			}
		}
	},
	refreshState:function(){
		var ctx = this;
		//获取配置信息
		$.post(oc.resource.getUrl("topo/refreshState.htm"),{
			nodeInstIds:this.nodesInstanceIds.join(","),
			linkInstIds:this.linksInstanceIds.join(","),
			linkMetricId:this.cfg.linkMetricId,
			nodeMetricId:this.cfg.nodeMetricId
		},function(result){
			if(result && result.data){
				for(var i=0;i<result.data.length;i++){
					var itm = result.data[i];
					if(itm.instanceId){
						var tmp = ctx.instanceMap[itm.instanceId];
						if(tmp){
							for(var j=0;j<tmp.length;j++){
								tmp[j].setState(itm.state);
							}
						}
					}
				}
			}
		},"json");
	},
	handleRelation:function(){
		if(!this.instanceMap){
			this.instanceMap={};
		}
		if(!this.nodesInstanceIds){
			this.nodesInstanceIds=[];
		}
		if(!this.linksInstanceIds){
			this.linksInstanceIds=[];
		}
		if(!this.nodesIds){
			this.nodesIds=[];
		}
		if(!this.nodesIdMap){
			this.nodesIdMap={};
		}
		var ts = this.args.topoShower;
		//链路
		for(var key in ts.els.links){
			var link = ts.els.links[key];
			if(link && link.d && link.d.instanceId){
				if(this.instanceMap[link.d.instanceId]){
					this.instanceMap[link.d.instanceId].push(link);
				}else{
					this.instanceMap[link.d.instanceId]=[link];
				}
				this.linksInstanceIds.push(link.d.instanceId);
			}
		}
		//节点
		for(var key in ts.els.nodes){
			var node = ts.els.nodes[key];
			if(node && node.d){
				if(node.d.instanceId){
					if(this.instanceMap[node.d.instanceId]){
						this.instanceMap[node.d.instanceId].push(node);
					}else{
						this.instanceMap[node.d.instanceId]=[node];
					}
					this.nodesInstanceIds.push(node.d.instanceId);
				}
				if(node.d.id){
					this.nodesIds.push(node.d.id);
					this.nodesIdMap[node.d.id]=node;
				}
			}
		}
	}
};