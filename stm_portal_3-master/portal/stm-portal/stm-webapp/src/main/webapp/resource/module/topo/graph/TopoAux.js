function TopoAux(topo){
	this.topo = topo;
}
TopoAux.prototype={
	init:function(){
		/*//初始化框选
		this.chbox=new Chooser({paper:this.topo.paper,holder:this.topo.holder,els:this.topo.els});
		//初始化连线
		this.link = new Link({
			paper:this.topo.paper,
			holder:this.topo.holder
		});
		//初始化拓扑上下文菜单
		this.menu=new TopoMenu({
			topo:this.topo
		},this.topo.holder);
		//启动事件
		new SvgEvent({
			paper:this.topo.paper,
			holder:this.topo.holder
		});
		//加载全局拓扑配置
		this.loadTopoCfg();*/
		this.regEvent();
	},
	//加载全局拓扑配置
	loadTopoCfg:function(){
		var ctx = this;
		$.get(oc.resource.getUrl("topo/setting/get/globalSetting.htm"),function(cfg){
			ctx.topo.cfg.nodeTitle=cfg.device.tagField;
			ctx.topo.cfg.lineTitle=cfg.link.tagField;
			ctx.topo.cfg.showLineTitle=(cfg.link.hasTag && cfg.link.hasTag == "true");
			ctx.topo.cfg.showLineArrow=(cfg.link.showDownDirection && cfg.link.showDownDirection == "true");
			ctx.topo.cfg.fontSize=cfg.topo.fontSize;
			ctx.topo.setCfg(ctx.topo.cfg);
		},"json");
	},
	regEvent:function(){
		var ctx = this;
		/*//监听更换拓扑图事件
		eve.on("oc_topo_subtopo",function(id){
			ctx.topo.id=id;
			ctx.topo.reset();
			if(!id && id!=0){
				id="";
			}
			ctx.topo.loadData(oc.resource.getUrl("topo/getSubTopo/"+id+".htm"));
		});*/
		//定时刷新拓扑状态
		//获取资源实例id
		var ids = [];
		$.each(this.topo.els,function(key,el){
			if(el.d && el.d.instanceId){
				ids.push(el.d.instanceId);
			}
		});
		var refreshStateInterval = setInterval(function(){
			$.post(oc.resource.getUrl("topo/refreshState.htm"),{
				instIds:ids.join(",")
			},function(states){
//				console.log(state);
			},"json");
		},2000);
		var tasks = oc.index.indexLayout.data("tasks");
		if(tasks && tasks.length > 0){
			oc.index.indexLayout.data("tasks").push(refreshStateInterval);
		}else{
			tasks = new Array();
			tasks.push(refreshStateInterval);
			oc.index.indexLayout.data("tasks", tasks);
		}
	},
	//保存
	save:function(){
		var ctx = this;
		var vals = this.topo.getValue();
		//保存
		oc.util.ajax({
			url:oc.resource.getUrl("topo/save.htm"),
			data:{
				groups:JSON.stringify(vals.groups),
				nodes:JSON.stringify(vals.nodes),
				links:JSON.stringify(vals.links),
				rgroups:JSON.stringify(this.topo.deleteItems.groups),
				rnodes:JSON.stringify(this.topo.deleteItems.nodes),
				rlinks:JSON.stringify(this.topo.deleteItems.links),
				rothers:this.topo.deleteItems.others.join(","),
				others:JSON.stringify(vals.others)
			},
			success:function(){
				alert("保存成功");
			}
		});
	}
};