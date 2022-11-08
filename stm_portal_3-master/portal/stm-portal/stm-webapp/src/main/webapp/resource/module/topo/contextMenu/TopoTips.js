function TopoTips(args){
	this.args=$.extend({
		holder:$("body")
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoTips.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
TopoTips.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.appendTo(this.args.holder);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.paper = Raphael("topo_dashboard_holder",199,80);
		this.cpu = new Dashbord({
			paper:this.paper,
			pos:{x:44,y:42,r:30,rr:8,cr:6}
		});
		var cpuText = this.paper.text(44,60,"");
		cpuText.attr({
			"fill":"white",
			"font-size":12,
			"text":"CPU"
		});
		$(cpuText.node).find("tspan").removeAttr("dy");
		this.ram = new Dashbord({
			paper:this.paper,
			pos:{x:150,y:42,r:30,rr:8,cr:6}
		});
		var ramText = this.paper.text(150,0,"内存").attr({
			"fill":"white",
			"font-size":12,
			y:60
		});
		$(ramText.node).find("tspan").removeAttr("dy");
		this.changeTheme();
		this.regEvent();
	},
	changeTheme:function(){
		var ctx = this;
		$.post(oc.resource.getUrl("topo/help/currentTheme.htm"),function(result){
			if(result.status==200){
				ctx.cpu.changeTheme(result.theme);
				ctx.ram.changeTheme(result.theme);
			}
		},"json");
	},
	regEvent:function(){
		var ctx = this;
		this.root.draggable({
			handler:this.fields.handler,
			cursor:'move',
			onDrag:function(e){
				var d = e.data;
				if (d.left < 0){d.left = 0;};
				if (d.top < 0){d.top = 0;};
				if (d.left +199 > $(d.parent).width()){
					d.left = $(d.parent).width() - 199;
				}
				if (d.top+74 > $(d.parent).height()){
					d.top = $(d.parent).height() - 74;
				}
			}
		});
		//监听隐藏
		eve.on("topo.topotip.hide",function(){
			ctx.root.hide();
		});
		//监听点击事件
		eve.on("element.click",function(e){
			var _ctx = this;
			if(this.d && !this.d.instanceId){
				ctx.root.hide();
				return ;
			}else if(!this.d){
				ctx.root.hide();
				ctx.setValue({});
			}else{
				if(this.d.multiNumber > 1){		//TODO:多链路暂时不显示任何提示
					ctx.root.hide();
					return;
				}
				ctx.root.show();
				if(this.d && this.d.id && this.d.id.indexOf("node")>=0){//是设备
					ctx.switchView("dashview");
					if(this.d.lifeState=="not_monitored" || !this.d.instanceId){
						ctx.setValue({});
					}else{
						$.post(oc.resource.getUrl("topo/metricinfo.htm"),{
							instanceId:this.d.instanceId,
							type:"node"
						},function(result){
							if(result.state==200){
								ctx.setValue(result.info);
							}else{
								alert(result.msg,"warning");
							}
						},"json");
					}
				}else if(this.d.type=="link"){//是链路
					ctx.switchView("linkview");
					if(this.d.instanceId){
						$.post(oc.resource.getUrl("topo/metricinfo.htm"),{
							instanceId:this.d.instanceId,
							type:"link",
							unit:"Mbps"
						},function(result){
							_ctx.d=$.extend(_ctx.d,result.info);
							if(result.state==200){
								ctx.setValue(result.info);
							}else{
								alert(result.msg,"warning");
							}
						},"json");
					}else{
						ctx.setValue({});
					}
				}
			}
		});
		//监听点击事件
		$(document).on("click",function(){
			ctx.root.hide();
		});
	},
	//切换视图
	switchView:function(viewName){
		this.root.find(".viewbox").addClass("hide");
		var view = this.fields[viewName];
		if(view){
			view.removeClass("hide");
		}
	},
	setValue:function(info){
		this.ram.setState(info.ramState);
		this.ram.setValue(info.ramRate);
		this.cpu.setValue(info.cpuRate);
		this.cpu.setState(info.cpuState);
		this.fields.upSpeed.text(info.upSpeed||"N/A");
		this.fields.downSpeed.text(info.downSpeed||"N/A");
		this.fields.totalSpeed.text(info.totalSpeed||"N/A");
	}
};