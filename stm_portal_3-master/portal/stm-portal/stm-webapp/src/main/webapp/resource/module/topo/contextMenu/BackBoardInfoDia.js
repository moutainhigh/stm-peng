function BackBoardInfoDia(args){
	this.args=$.extend({
		callBack:function(){},
		node:null,
		onLoad:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/BackBoardInfoDia.html"),
		type:"get",
		dataType:"html",
		success:function(html){
			oc.resource.loadScripts(["resource/module/topo/backboard/BB.js","resource/module/topo/api/TopoScale.js"], function(){
				ctx.init(html);
			});
		}
	});
};
BackBoardInfoDia.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化menu
		this.menu = this.fields.menu.menu();
		this.root.dialog({
			width:800,height:560,//500
			title:"面板信息"
		});
		this.bb = new BB({
			paper:Raphael("bbInfo",778,394),
			holder:"#bbInfo",
			iterator:function(item){
				ctx.regItemEvent(item);
			},
			onLoad:function(){
				ctx.bb=this;
				ctx._initData();
				ctx.args.onLoad.call(ctx);
			}
		});
		this.regEvent();
	},
	//初始化背板信息
	_initData:function(){
		var ctx = this;
		this.scale=new TopoScale({
			holder:$("#bbInfo")
		});
		if(this.args.value){
			this.bb.instanceId=this.args.instanceId;
			this.bb.setValue(this.args.value);
			this.bb.setEditable(false);
			this.bb.refreshState();
			this.scale.selfAdapt(this.bb.getRange(),true);
			ctx.bb.hideImageSetFg();
		}
		if(this.args.node){
			this.bb.load(this.args.node.d.instanceId,function(){
				ctx.bb.setEditable(false);
				ctx.bb.refreshState();
				ctx.scale.selfAdapt(ctx.bb.getRange(),true);
				ctx.bb.hideImageSetFg();
			});
		}
	},
	regItemEvent:function(ele){
		var ctx = this;
		if(ele && ele.img){
			$(ele.img.node).unbind("contextmenu");
		}
		if(ele.dataType=="port"){
			//tooltip
			ele.img.mouseover(function(e){
				if(ctx.tipTimeId){
					clearTimeout(ctx.tipTimeId);
				}
				ctx.tipTimeId=null;
				ctx.tipTimeId=setTimeout(function(){
					oc.topo.tips.show({
						type:"port",
						x:e.pageX,
						y:e.pageY,
						value:{
							instanceId:ele.rawItem.instanceId,
							ifIndex:ele.rawItem.ifIndex
						}
					});
					ctx.tipTimeId=null;
				},1000);
			});
			ele.img.mouseout(function(){
				if(ctx.tipTimeId){
					clearTimeout(ctx.tipTimeId);
					ctx.tipTimeId=null;
				}
				oc.topo.tips.hide();
			});
			//右键
			$(ele.img.node).on("contextmenu",function(e){
				if(ctx.args.downDeviceFlag && true == ctx.args.downDeviceFlag){	//显示下联设备标志
					clearTimeout(ctx.tipTimeId);
					ctx.tipTimeId=null;
					oc.topo.tips.hide();
					ctx.menu.menu("show",{
						left:e.pageX,
						top:e.pageY
					});
					ctx.currentEle=ele;
				}
				e.stopPropagation(); 
				return false;
			});
		}
	},
	regEvent:function(){
		var ctx = this;
		this.menu.menu({
			onClick:function(item){
				switch(item.text){
				case "查看下联设备":
					if(ctx.currentEle && ctx.currentEle.rawItem && ctx.currentEle.rawItem.instanceId){
						oc.resource.loadScript("resource/module/topo/backboard/DownDeviceDia.js",function(){
							$.post(oc.resource.getUrl("topo/backboard/downinfo.htm"),{
								subInstanceId:ctx.currentEle.rawItem.instanceId,
								mainInstanceId:ctx.args.node.d.instanceId
							},function(result){
								new DownDeviceDia({
									ip:result.ip,		//上联 设备ip
									intface:result.IfName,//上联设备接口
									deviceName:result.deviceShowName,//上联设备名称
									intfaceIndex:ctx.currentEle.rawItem.ifIndex	//接口索引
								});
							},"json");
						});
					}
					break;
				}
			}
		});
	}
};