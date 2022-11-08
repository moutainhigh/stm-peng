function TopoToolbar(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.type=args.type||"normal";
	this.$root = $("#topoToolbar");
	this.topo=args.topo;
	this.init();
	this.regEvent();
}
TopoToolbar.prototype={
	init:function(){
		this.draw = this.topo.drawUtil;
		//初始化框选
		this.chbox=new Chooser({paper:this.paper,holder:this.holder,els:this.topo.els});
		this.chbox.setEditable(false);
		//初始化连线
		this.link = new Link({
			paper:this.paper,
			holder:this.holder
		});
	},
	regEvent:function(){
		var ctx = this;
		this.$root.find(".undo").click(function(){
			alert("撤销");
		});
		this.$root.find(".redo").click(function(){
			alert("重做");
		});
		this.$root.find(".textbox").click(function(){
			ctx.draw.setMode("textbox");
		});
		//字体大小
		this.$root.find("#fontSize").combobox({
			onSelect:function(v){
				var cur = ctx.draw.current;
				if(cur){
					if(cur instanceof TextArea){
						cur.fontSize=parseInt(v.value);
						cur.refresh(cur.getPos());
					}
				}
			}
		});
		//字体颜色
		this.$root.find(".fontcolor").colorpicker({
			success:function(obj,color){
				var cur = ctx.draw.current;
				if(cur){
					if(cur instanceof TextArea){
						cur.fontColor=color;
						cur.refresh(cur.getPos());
					}
				}
			}
		});
		//填充颜色
		this.$root.find(".wordart").colorpicker({
			success:function(obj,color){
				var cur = ctx.draw.current;
				if(cur){
					if(cur instanceof ZLine){
						cur.setColor(color);
					}else if(cur instanceof TextArea){
						cur.bg.attr("fill",color);
					}else if(cur instanceof Rect){
						cur.rect.attr("fill",color);
					}else if(cur instanceof Ellipse){
						cur.ellipse.attr("fill",color);
					}
				}
			}
		});
		//形状
		this.$root.find("#shape").combobox({
			onSelect:function(v){
				var value =ctx.$root.find('.shape .textbox-text').val();
				ctx.$root.find('#insertBg1').html($(value));
				if(v.value){
					ctx.draw.setMode(v.value);
				}
			}
		});
		//线宽
		this.$root.find("#lineweight").combobox({
			onSelect: function(v){ 
				var value =ctx.$root.find('.lineweights .textbox-text').val();
				ctx.$root.find('#insertBg2').html($(value));
				var cur = ctx.draw.current;
				if(cur){
					if(cur instanceof ZLine){
						cur.setWeight(v.weight);
					}else if(cur instanceof TextArea){
						cur.bg.attr("stroke-width",v.weight);
					}else if(cur instanceof Rect){
						cur.rect.attr("stroke-width",v.weight);
					}else if(cur instanceof Ellipse){
						cur.ellipse.attr("stroke-width",v.weight);
					}
				}
	        }
		});
		//线条样式
		this.$root.find("#linetype").combobox({
			onSelect: function(v){ 
				var value =ctx.$root.find('.linestyle .textbox-text').val();
				ctx.$root.find('#insertBg').html($(value));
				var cur = ctx.draw.current;
				if(cur){
					if(cur instanceof ZLine){
						cur.setStyle(v.type);
					}else if(cur instanceof TextArea){
						cur.bg.attr("stroke-dasharray",v.typeStr||"");
					}else if(cur instanceof Rect){
						cur.rect.attr("stroke-dasharray",v.typeStr||"");
					}else if(cur instanceof Ellipse){
						cur.ellipse.attr("stroke-dasharray",v.typeStr||"");
					}
				}
	        }
		});
		//框选按钮-单击进入框选状态
		this.$root.find(".choose").click(function(){
			ctx.chbox.setEditable(true);
		});
		//保存
		this.$root.find(".save").click(function(){
			var topoValue = ctx.topo.getValue();
			alert(JSON.stringify(topoValue));
		});
		//删除
		this.$root.find(".delete").click(function(){
			var tmp = ctx.draw.current;
			if(tmp){
				tmp.remove();
				if(tmp.d.id.indexOf("other")>=0){
					ctx.draw.removeById(tmp.id);
					if(tmp.connects){
						$.each(tmp.connects,function(idx,line){
							line.remove();
						});
					}
					//标记出待删除数据库中的项
					if(parseInt(tmp.id)>0){
						ctx.topo.deleteItems.others.push(tmp.id);
					}
				}
			}
		});
		//监听连线
		eve.on("topo_item_click",function(){
			ctx.link.add(this);
		});
		//监听连线完成
		this.link.onEnd=function(from,to){
			var con = ctx.topo.connect({
				conA:from,
				conB:to
			});
		};
	}
};