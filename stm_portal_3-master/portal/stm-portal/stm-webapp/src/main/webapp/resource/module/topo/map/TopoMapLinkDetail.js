function TopoMapLinkDetail(args){
	this.args=$.extend({
		instanceId:null,
		metricId:null,
		dialog:true
	},args);
	with(this){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/map/TopoMapLinkDetail.html"),
			type:"get",
			dataType:"html",
			success:function(htl){
				oc.resource.loadScripts(["resource/module/resource-management/resourceDetailInfo/js/resourceCommonChart.js","resource/module/topo/contextMenu/TopoLinkInfo.js"],function(){
					init(htl);
				});
			}
		});
	}
};
TopoMapLinkDetail.prototype={
	init:function(htl){
		this.root=$(htl);
		with(this){
			this.fields={};
			root.find("[data-field]").each(function(idx,dom){
				var tmp=$(dom);
				fields[tmp.attr("data-field")]=tmp;
			});
			if(args.dialog){
				this.root.dialog({
					title:"链路详细信息",
					width:1260,height:420,
					buttons:[{
						text:"关闭",handler:function(){
							root.dialog("close");
						}
					}]
				});
			}
			//初始化曲线图
			this.chart=new chartObj(fields.chart,1,args.metricId);
			oc.util.ajax({
				url:oc.resource.getUrl("topo/link/getValueInstId.htm"),
				data:{
					instId:args.instanceId
				},
				success:function(result){
					if(result.status==200){
						chart.setIds(args.metricId,result.instanceId);
					}else{
						alert(result.msg,"warning");
					}
				}
			});
			//初始化表格
			new TopoLinkInfo({
				dialog:false,
				parent:fields.grid,
				type:"map",
				onLoad:function(){
					var _ctx = this;
					this.fields.grid.parent().css("height","100px");
					this.root.on("click",".topo_link_cell",function(){
						var tmp = $(this);
						chart.setIds(tmp.attr("metricId"),tmp.attr("instanceId"));
						tmp.addClass("ico-chartred");
						tmp.removeClass("ico-chart");
						if(_ctx.lastTmp){
							_ctx.lastTmp.addClass("ico-chart");
							_ctx.lastTmp.removeClass("ico-chartred");
						}
						_ctx.lastTmp=tmp;
					});
					this.load(args.instanceId,"map");
				}
			});
			regEvent();
		}
	},
	regEvent:function(){
		
	}
};