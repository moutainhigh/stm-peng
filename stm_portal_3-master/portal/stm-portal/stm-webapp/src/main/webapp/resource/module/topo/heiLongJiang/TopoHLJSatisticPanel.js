function TopoHLJSatisticPanel(args){
	this.args=$.extend({
		onLoad:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/heiLongJiang/TopoHLJSatisticPanel.html"),
		type:"get",
		dataType:"html",
		success:function(htl){
			ctx.init(htl);
		}
	});
};
TopoHLJSatisticPanel.prototype={
	init:function(htl){
		this.root=$(htl);
		this.args.onLoad.call(this);
		this.fields={};
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
	},
	toMap:function(mapId,his,cb,level){
		var ctx = this;
		oc.util.ajax({
			data:{
				mapId:mapId,
				level:level
			},
			url:oc.resource.getUrl("topo/hlj/mapInfo.htm"),
			success:function(result){
				if(result.status==200){
					result.info.title=his.name;
					ctx.setValue(result.info);
					if(cb){
						cb.call(ctx);
					}
				}
			}
		});
	},
	setValue:function(info){
		if(!info) return;
		this.lastValue=info;
		with(this.fields){
			monitorCount.text(info.amount);
			available.text(info.available);
			unavailable.text(info.unavailable);
			title.text(info.title);
			unavailableContainer.find(".info_item").remove();
			for(var i=0;i<info.items.length;i++){
				var item = info.items[i];
				var template = unavailableTemplate.clone();
				template.attr("title",item.showName+"("+item.ip+")");
				template.find(".name").text(item.showName);
				template.find(".ip").text(item.ip);
				template.removeClass("hide");
				template.addClass("info_item");
				unavailableContainer.append(template);
			}
		}
	}
};