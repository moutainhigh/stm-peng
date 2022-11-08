function TopoMapFlowList(args){
	this.args=$.extend({
		holder:null
	},args);
	var ctx = this;
	if(this.args.holder){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/map/TopoMapFlowList.html"),
			success:function(html){
				ctx.init(html,args.key);
			},
			type:"get",
			dataType:"html"
		});
	}else{
		throw "bad parameters";
	}
};
TopoMapFlowList.prototype={
	init:function(htl,key){
		this.root = $(htl);
		this.root.appendTo(this.args.holder);
		this.fields ={};
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		var img = this.fields.flowItemTemplate.find("img");
		img.attr("src",oc.resource.getUrl(img.attr("src")));
		if(key){
			this.toMap(key);
			this.fields.alarmBox.removeClass("hide");
			this.fields.flowBox.addClass("hide");
			this.fields.alarmTitle.addClass("hover");
			this.fields.flowTitle.removeClass("hover");
		}
		this.regEvent();
	},
	regEvent:function(){
		var fd = this.fields,ctx=this;
		fd.flowTitle.on("click",function(){
			fd.flowBox.removeClass("hide");
			fd.alarmBox.addClass("hide");
			fd.flowTitle.addClass("hover");
			fd.alarmTitle.removeClass("hover");
		});
		fd.alarmTitle.on("click",function(){
			fd.alarmBox.removeClass("hide");
			fd.flowBox.addClass("hide");
			fd.alarmTitle.addClass("hover");
			fd.flowTitle.removeClass("hover");
		});
		fd.unavailableContainer.on("click",".unavailable_item",function(){
			var mapid = $(this).attr("data-next-mapid");
			var level = $(this).attr("data-next-level");
			if(mapid){
				oc.module.topo.map.loadMapData(parseInt(mapid),parseInt(level));
			}
		});
	},
	show:function(){
		this.root.show();
	},
	hide:function(){
		this.root.hide();
	},
	toMap:function(mapid,level){
		oc.topo.map.util.beginLog("TopoMapFlowList.toMap");
		if(!this.lastMapId||this.lastMapId!=mapid){
			var ctx = this;
			var url=oc.resource.getUrl("topo/map/graph/flowlist.htm");
			if(oc.topo.util.module=="GF"){
				url=oc.resource.getUrl("topo/gf/flowlist.htm");
			}else if(oc.topo.util.module=="FTMS"){
				url=oc.resource.getUrl("topo/ftms/flowlist.htm");
			}
			$.post(url,{
				mapid:mapid,
				level:level
			},function(result){
				if(result.status==200){
					ctx.setValue(result.info);
					oc.topo.map.util.endLog("TopoMapFlowList.toMap");
				}
			},"json");
		}
		this.lastMapId=mapid;
	},
	setAlarmListValue:function(v){
		var fd = this.fields;
		fd.monitorCount.text(v.monitorCount||0);
		fd.available.text(v.available||0);
		fd.unavailable.text(v.unavailable||0);
		//不可用节点
		if(v.unavailableNodes){
			fd.unavailableContainer.find(".unavailable_item").remove();
			for(var i=0;i<v.unavailableNodes.length;i++){
				var node = v.unavailableNodes[i];
				var template = fd.unavailableTemplate.clone();
				template.removeClass("hide");
				template.addClass("unavailable_item");
				template.find(".name").text(node.name);
				template.find(".ip").text(node.ip);
				template.attr("data-next-mapid",node.nextMapId);
				template.attr("data-next-level",node.level);
				fd.unavailableContainer.append(template);
			};
		}
	},
	setValue:function(v){
		var fd = this.fields;
		this.setAlarmListValue(v);
		//流量topo10
		fd.flowContainer.find(".flow_item").remove();
		for(var i=0;i<v.flowNodes.length;i++){
			var node = v.flowNodes[i];
			var template = fd.flowItemTemplate.clone();
			template.removeClass("hide");
			template.addClass("flow_item");
			template.find(".index").text(i+1);
			var srcName = template.find(".srcName");
			srcName.text(node.srcName||"- -");
			srcName.attr("title",node.srcName||"- -");
			var desName = template.find(".desName");
			desName.text(node.desName||"- -");
			desName.attr("title",node.desName||"- -");
			template.find(".flow_amount").text(node.flow);
			template.find(".flow_ratio").text(node.ratio);
			fd.flowContainer.append(template);
		}
	}
};