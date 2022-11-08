function TopoHLJMenu(args){
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/heiLongJiang/TopoHLJMenu.html"),
		success:function(htl){
			ctx.init(htl);
		},
		type:"get",
		dataType:"html"
	});
	
};
TopoHLJMenu.prototype={
	init:function(htl){
		this.root=$(htl);
		this.root.appendTo($("body"));
		this.root.menu();
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		this.root.menu({
			onClick:function(item){
				switch(item.text){
				case "关联资源实例":
					oc.resource.loadScript("resource/module/topo/heiLongJiang/HLJRelateResourceInstance.js",function(){
						var node = ctx.getNode();
						var rr = new HLJRelateResourceInstance({
							singleSelect:false,
							subTopoId:-1,
							id:-1,
							areas:oc.topo.hlj.main.getAreas(),
							onConfirm:function(){
								var resIds=[];
								$.each(this.getRows(),function(idx,row){
									resIds.push(row.instanceId);
								});
								var areaInfo=this.getAreaInfo();
								var info = {
										nodeId:node.data("id"),
										instanceIds:resIds.join(","),
										nextMapId:areaInfo.key||node.attr("key"),
										area:areaInfo.name||node.attr("data-courtname"),
										mapId:oc.topo.hlj.main.key,
										parentMapId:oc.topo.hlj.main.getParentKey()
								};
								if(node.attr("dbid")){
									info.id=node.attr("dbid");
								}
								oc.util.ajax({
									url:oc.resource.getUrl("topo/hlj/relateInst.htm"),
									data:info,
									success:function(result){
										if(result.status==200){
											oc.topo.hlj.main.refreshState();
											alert(result.msg);
										}else{
											alert(result.msg,"warning");
										}
									}
								});
								this.root.dialog("close");
							}
						});
						if(node.attr("dbid")){
							$.post(oc.resource.getUrl("topo/hlj/getRelateInfo.htm"),{
									id:node.attr("dbid")
								},
								function(resinfo){
									if(resinfo.status==200){
										rr.setChecked(resinfo.info.instanceIds.split(","));
										rr.select(resinfo.info.nextMapId);
									}
								},
								"json"
							);
						}
						
					});
					break;
				}
			}
		});
	},
	setNode:function(node){
		this.node=node;
	},
	getNode:function(){
		if(this.node){
			return this.node;
		}else{
			throw "node is undefined";
		}
	},
	show:function(x,y){
		this.root.menu("show",{
			left:x,
			top:y
		});
	}
};