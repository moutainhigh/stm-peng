function TopoToolbarNew(args){
	this.holder = args.holder;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/graph/toolbar.html"),
		type:"get",
		success:function(html){
			ctx.init(html);
		},
		dataType:"html"
	});
};
TopoToolbarNew.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.appendTo(this.holder);
		//搜索域
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.initUi();
		this.regEvent();
	},
	show:function(){
		this.root.fadeIn();
	},
	hide:function(){
		this.root.fadeOut();
	},
	initUi:function(){
		var ctx = this;
		//将工具栏定位到左上角
		this.root.css({
			top:0,left:0
		});
		//初始化一堆的combobox
		var fontSizes = [];
		for(var i=0;i<=5;i++){
			fontSizes.push({
				id:i,
				text:10+2*i
			});
		}
		//新增30号和40号字体
		for(var i=6;i<=7;i++){
			fontSizes.push({
				id:i,
				text:10+10*(i-4)
			});
		}
		this.fields.fontSizeSelector.combobox({
			disabled:true,
			width:50,
			valueField:'id',
			textField:'text',
			data:fontSizes
		});
		
		this.fields.lineStyleSelector.combobox({
			width:130,
			valueField:'id',
			textField:'text',
			disabled:true,
			data:[{    
			    "id":1,    
			    "text":"<div class='linetype lt1'></div>",
			    type:"solid",
			    typeStr:""
			},{    
			    "id":2,    
			    "text":"<div class='linetype lt2'></div>",
			    type:"dash",
			    typeStr:". "
			},{  
			    "id":3,    
			    "text":"<div class='linetype lt3'></div>",    
			    type:"interval",
			    typeStr:"-"
			},{    
			    "id":4,    
			    "text":"<div class='linetype lt4'></div>",
			    type:"intervalLong",
			    typeStr:"--"
			}]
		});
		this.fields.lineStyleSelector.mask=$('<div id="insertBg"><div style="padding:1px;">线条类型设置</div></div>').insertAfter(this.fields.lineStyleSelector.parent().find('.textbox-text'));
		
		this.fields.lineWeightSelector.combobox({
			width:130,
			valueField:'id',
			textField:'text',
			value:null,
			disabled:true,
			data:[{    
			    "id":1,    
			    "text":"<div class='lineweight lw1'></div>",
			    weight:1
			},{    
			    "id":2,    
			    "text":"<div class='lineweight lw2'></div>",
			    weight:2
			},{    
			    "id":3,    
			    "text":"<div class='lineweight lw3'></div>",    
			    weight:4  
			},{    
			    "id":4,    
			    "text":"<div class='lineweight lw4'></div>",
			    weight:8
			}] 
		});
		this.fields.lineWeightSelector.mask = $('<div id="insertBg2"><div style="padding:1px;">线条粗细设置</div></div>').insertAfter(this.fields.lineWeightSelector.parent().find('.textbox-text'));
		
		this.fields.shapeSelector.combobox({
			width:55,
			value:"line",
			valueField:'value',
			textField:'text',
			data:[{    
				"text":"<div class='shapeBg sh4'></div>",  
				"value":"ellipse"
			},{    
				 "value":"rect",    
			    "text":"<div class='shapeBg sh3'></div>"    
			},{    
				 "value":"circle",
			    "text":"<div class='shapeBg sh6'></div>"
			}] 
		});
		this.fields.shapeSelector.mask = $('<div id="insertBg1"><div style="padding:1px;">形状</div></div>').insertAfter(this.fields.shapeSelector.parent().find('.textbox-text'));
		oc.resource.loadScript("resource/module/topo/contextMenu/ColorPalette.js", function(){
			ctx.colorChoser = new ColorPalette();
			oc.topo.colorChoser=ctx.colorChoser;
		});
		this.width=this.root.width();
		this.fields.holder.css("overflow","hidden");
		this.bg=this.fields.holder.css("background");
	},
	enableTool:function(isFontColor,isFontSize,isLineStyle,isLineWeight,isFillColor){
		var fd = this.fields;
		fd.fontSizeSelector.combobox({
			disabled:!!!isFontSize
		});
		fd.fontColorSelector.disabled=!!!isFontColor;
		fd.lineStyleSelector.combobox({
			disabled:!!!isLineStyle
		});
		fd.fillColorBtn.disabled=!!!isFillColor;
		fd.lineWeightSelector.combobox({
			disabled:!!!isLineWeight
		});
	},
	shrink:function(){
		this.fields.holder.css("background","url()");
		this.root.css("width","36px");
		this.fields.holder.css("width","36px");
	},
	expand:function(){
		this.fields.holder.css("background",this.bg);
		this.fields.holder.css("width",this.width);
		this.root.css("width",this.width);
	},
	regEvent:function(){
		var ctx = this;
		this.fields.dragArea.on("mousedown",function(){
			ctx.mouseDownTime=new Date().getTime();
		});
		//拖动区域
		this.fields.dragArea.on("mouseup",function(){
			var currentTime = new Date().getTime();
			var remain = currentTime-ctx.mouseDownTime;
			if(remain>0 && remain<150){
				var tmp = $(this);
				var shrinkFlag = tmp.attr("data-shrink");
				if(!shrinkFlag || shrinkFlag=="1"){
					ctx.shrink();
					shrinkFlag=0;
				}else{
					ctx.expand();
					shrinkFlag=1;
				}
				tmp.attr("data-shrink",shrinkFlag);
			}
		});
		//防止乱拖动
		var toolbarWidth = parseInt(this.root.css("width")),toolbarHeight = parseInt(this.root.css("height"));
		this.root.draggable({handle:this.fields.dragArea,cursor:'move',onDrag:function(e){
			var d = e.data;
			if (d.left < 0){d.left = 0;};
			if (d.top < 0){d.top = 0;};
			if (d.left +toolbarWidth > $(d.parent).width()){
				d.left = $(d.parent).width() - toolbarWidth;
			}
			if (d.top+toolbarHeight > $(d.parent).height()){
				d.top = $(d.parent).height() - toolbarHeight;
			}
			ctx.isDraging=true;
		}});
		//形状
		this.fields.shapeSelector.combobox({
			onSelect:function(row){
				ctx.fields.shapeSelector.mask.html($(row.text));
				ctx.fields.shapeSelector.row=row;
				eve("topo.draw.command",this,"shape",row);
			}
		});
		this.fields.shapeSelector.next().on("click",function(){
			if(ctx.fields.shapeSelector.row){
				eve("topo.draw.command",this,"shape",ctx.fields.shapeSelector.row);
			}
		});
		//字体大小
		this.fields.fontSizeSelector.combobox({
			onSelect:function(record){
				ctx.fields.fontSizeSelector.record=record;
				ctx.updateAttr({fontsize:record}, function(){
					eve("topo.draw.command",this,"fontSize",record);
				});
			}
		});
		this.fields.shapeSelector.next().on("click",function(){
			if(ctx.fields.fontSizeSelector.record){
				eve("topo.draw.command",this,"shape",ctx.fields.fontSizeSelector.record);
			}
		});
		//字体颜色
		this.fields.fontColorSelector.on("click",function(e){
			if(ctx.fields.fontColorSelector.disabled) return;
			var _ctx = this;
			ctx.colorChoser.show(e);
			ctx.colorChoser.onece("selected",function(color){
				var vl = ctx.colorChoser.getValue();
				if(vl){
					_ctx.oldColor=vl.color;
					ctx.updateAttr({fontColor:vl.color}, function(){
						eve("topo.draw.command",this,"fontColor",vl.color);
					});
					ctx.colorChoser.hide();
				}
			});
		});
		//线条样式
		this.fields.lineStyleSelector.combobox({
			onSelect:function(row){
				ctx.fields.lineStyleSelector.mask.html($(row.text));
				ctx.updateAttr({style:row.typeStr}, function(){
					eve("topo.draw.command",this,"lineStyle",row);
				});
			}
		});
		//线条宽度
		this.fields.lineWeightSelector.combobox({
			onSelect:function(row){
				ctx.fields.lineWeightSelector.mask.html($(row.text));
				ctx.updateAttr({weight:row.weight}, function(){
					eve("topo.draw.command",this,"lineWeight",row);
				});
			}
		});
		//边框色
		this.fields.borderColorBtn.on("click",function(e){
			if(ctx.fields.fillColorBtn.disabled) return;
			var _ctx = this;
			ctx.colorChoser.show(e);
			var attr = ctx.getCurrentColorPaletteAttr();
			if(attr){
				ctx.colorChoser.setValue({
					color:attr.scolor,
					opacity:attr.sopacity
				});
			}
			ctx.colorChoser.onece("selected",function(color){
				var vl = ctx.colorChoser.getValue();
				if(vl){
					ctx.updateAttr({
						"stroke":vl.color,
						"stroke-opacity":vl.opacity
					},function(){
						eve("topo.draw.command",this,"borderColor",vl.color);
						eve("topo.draw.command",this,"stroke-opacity",vl.opacity);
					});
					ctx.colorChoser.hide();
				}
			});
		});
		//填充颜色
		this.fields.fillColorBtn.on("click",function(e){
			if(ctx.fields.fillColorBtn.disabled) return;
			var _ctx = this;
			ctx.colorChoser.show(e);
			var attr = ctx.getCurrentColorPaletteAttr();
			if(attr){
				ctx.colorChoser.setValue({
					color:attr.fcolor,
					opacity:attr.fopacity
				});
			}
			ctx.colorChoser.onece("selected",function(color){
				var vl = ctx.colorChoser.getValue();
				if(vl){
					_ctx.oldColor=vl.color;
					ctx.updateAttr({
						"stroke":vl.color,
						"fill-opacity":vl.opacity
					},function(){
						eve("topo.draw.command",this,"fillColor",vl.color);
						eve("topo.draw.command",this,"fill-opacity",vl.opacity);
					});
					ctx.colorChoser.hide();
				}
			});
		});
		//文本框
		this.fields.textBoxBtn.on("click",function(){
			eve("topo.draw.command",this,"textBox",null);
		});
		//创建区域
		/*this.fields.areaBtn.on("click",function(){
			eve("topo.drawgroup.command",this);
		});*/
		//全屏
		this.fields.boxChooseBtn.on("click",function(){
			oc.resource.loadScript("resource/module/topo/api/TopoScale.js",function(){
				var ts = new TopoScale({
					holder:$("#topo_holder")
				});
				ts.fullScreen();
			});
			/*eve("oc_topo_box_chose",this);
			oc.topo.tips.show("node",{},{x:100,y:100});*/
		});
		//保存
		this.fields.saveBtn.on("click",function(){
			eve("topo.save",this,function(){
				eve("topo.refresh");
			});
		});
		//删除
		this.fields.removeBtn.on("click",function(){
			eve("topo.remove.command");
		});
		//topo发现结果
		this.fields.findResultBtn.on("click",function(){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/setting/get/topo_find_result.htm"),
				success:function(result){
					oc.resource.loadScript("resource/module/topo/findResult/findResult.js", function(){
						new FindResultDia({
							show:true,
							onLoad:function(){
								this.setValue(result);
								this.fields.closeBtn.show();
								this.fields.resBtn.hide();
								this.fields.topoBtn.hide();
								this.fields.backBtn.hide();
								this.fields.cancelBtn.hide();
							}
						});
					});
				}
			});
		});
		//刷新
		this.fields.refreshBtn.on("click",function(){
			eve("topo.refresh");
		});
		//连线
		this.fields.linkBtn.on("click",function(){
			eve("topo.enablelink");
		});
		//监听文本框设置文字
		eve.on("element.textarea.*",function(){
			ctx.updateAttr();
		});
		//监听拓扑图选择
		eve.on("element.click.*",function(){
			ctx.current=this;
			if(this instanceof TopoTextArea){
				ctx.enableTool(true, true, false, false, true);
			}else if(this instanceof Ellipse || this instanceof Rect){
				ctx.enableTool(false, false, true, true, true);
			}else if(this instanceof Connect && this.d && !this.d.instanceId && this.d.type=="line"){
				ctx.enableTool(false, false, true, true, true);
			}else{
				ctx.enableTool(false, false, false, false, false);
			}
		});
		eve.on("topo.toolbar.hide",function(){
			ctx.root.hide();
		});
		eve.on("topo.toolbar.show",function(){
			ctx.root.show();
		});
	},
	getCurrentColorPaletteAttr:function(){
		var current = this.current;
		if(current instanceof Ellipse || current instanceof Rect){
			var node = current.ellipse || current.rect;
			return {
				fopacity:node.attr("fill-opacity"),
				fcolor:node.attr("fill"),
				sopacity:node.attr("stroke-opacity"),
				scolor:node.attr("stroke")
			};
		}else{
			return null;
		}
	},
	updateAttr:function(attr,callback){
		var ctx = this;
		var current = this.current;
		if(current instanceof Ellipse || current instanceof Rect || current instanceof TopoTextArea){
			if(callback){
				callback.call(ctx);
			}
			oc.util.ajax({
				url:oc.resource.getUrl("topo/other/updateAttr.htm"),
				data:{
					id:current.d.rawId||current.d.id,
					attr:JSON.stringify(current.getValue()),
					subTopoId:current.d.subTopoId
				},
				success:function(info){
					if(info.state==200){
						alert(info.msg);
					}else{
						alert(info.msg,"warning");
					}
					if(callback){
						callback.call(ctx);
					}
				}
			});
		}else if(current instanceof Connect && current.d && !current.d.instanceId){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/link/updateAttr.htm"),
				data:{
					id:current.d.rawId||current.d.id,
					attr:JSON.stringify(attr),
					subTopoId:current.d.subTopoId
				},
				success:function(info){
					alert(info.msg,info.state==200?"normal":"warning");
					if(callback){
						callback.apply(ctx);
					}
				}
			});
		}
		return false;
	}
};