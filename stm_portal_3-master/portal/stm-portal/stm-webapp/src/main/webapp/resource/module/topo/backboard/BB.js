function BB(args){
	var ctx = this;
	this.paper=args.paper;
	this.holder = args.holder;
	this.args=args;
	this.$svg = $(this.holder).find("svg");
	this.els = [];
	this.cs = new Chooser({
		paper:this.paper,
		holder:this.holder
	});
	oc.resource.loadScript("resource/module/topo/backboard/smallWindow/PortAttrDia.js", function(){
		ctx.init();
		if(ctx.args.onLoad){
			ctx.args.onLoad.call(ctx);
		}
	});
};
BB.prototype={
	init:function(){
		this.cs.setEls(this.els);
		//放大器
		this.sizer = new Sizer({
			paper:this.paper,
			holder:this.holder
		});
		this.sizer.hide();
		this.regEvent();
	},
	align:function(type){
		var nodes = this.cs.chosed;
		if(nodes.length>1){
			var rp = nodes[0].getPos();
			rp.x2=rp.x+rp.w;
			rp.y2=rp.y+rp.h;
			if(type == "hmargin"){
				nodes = nodes.sort(function(a,b){
					var p1 = a.getPos();
					var p2 = b.getPos();
					return p2.x>p1.x;
				});
				var pa = nodes[0].getPos(),pb=nodes[nodes.length-1].getPos();
				rp.pw = Math.floor((pb.x-pa.x)/(nodes.length-1));
			}
			if(type == "vmargin"){
				nodes = nodes.sort(function(a,b){
					var p1 = a.getPos();
					var p2 = b.getPos();
					return p2.y>p1.y;
				});
				var pa = nodes[0].getPos(),pb=nodes[nodes.length-1].getPos();
				rp.ph = Math.floor((pb.y-pa.y)/(nodes.length-1));
			}
			$.each(nodes,function(idx,node){
				var tp = node.getPos();
				if(rp.x>tp.x) rp.x=tp.x;
				if(rp.x2<(tp.x+tp.w)){
					rp.x2=tp.x+tp.w;
				}
				if(rp.y>tp.y) rp.y=tp.y;
				if(rp.y2<(tp.y+tp.h)) {
					rp.y2=tp.y+tp.h;
				}
			});
			$.each(nodes,function(idx,node){
				var ttp = node.getPos();
				switch(type){
				case "top":
					ttp.y=rp.y;
					break;
				case "left":
					ttp.x=rp.x;
					break;
				case "bottom":
					ttp.y=rp.y2-ttp.h;
					break;
				case "right":
					ttp.x=rp.x2-ttp.w;
					break;
				case "vmiddle":
					ttp.x=(rp.x+rp.w/2)-ttp.w/2;
					break;
				case "hmiddle":
					ttp.y=(rp.y+rp.h/2)-ttp.h/2;
					break;
				case "vmargin":
					ttp.y=rp.y+idx*rp.ph;
					break;
				case "hmargin":
					ttp.x=rp.x+idx*rp.pw;
					break;
				}
				node.refresh(ttp);
			});
		}
	},
	removeChosed:function(){
		$.each(this.cs.chosed,function(idx,c){
			c.remove();
		});
		if(this.cs.chosed.length==0 && this.currentElement){
			this.currentElement.remove();
		}
	},
	getRange:function(){
		var range = {x:1000000,y:100000,w:0,h:0};
		$.each(this.els,function(idx,el){
			if(el.getPos){
				var p = el.getPos();
				try{
					if(p.x<range.x){
						range.x=p.x;
					}
					if((p.x+p.w)>range.w){
						range.w=(p.x+p.w);
					}
					if(p.y<range.y){
						range.y=p.y;
					}
					if((p.h+p.y)>range.h){
						range.h=(p.h+p.y);
					}
				}catch(e){
					// console.log(e);
				}
			}
		});
		return range;
	},
	save:function(isBatch){
		if(this.instanceId){
			var info = this.getValue();
			var infoTmp = JSON.stringify(info);
			var re = new RegExp("src","g");
			infoTmp = infoTmp.replace(re,"imgUrl");
			oc.util.ajax({
				url:oc.resource.getUrl("topo/backboard/save.htm"),
				data:{
					isBatch:isBatch,
					info:infoTmp,
					instanceId:this.instanceId
				},
				type:"POST",
				success:function(){
					alert("面板保存成功");
				}
			});
		}
	},
	//加载面板信息
	load:function(instanceId,callBack){
		this.instanceId=instanceId;
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/backboard/get.htm"),
			data:{
				instanceId:instanceId
			},
			type:"get",
			success:function(info){
				//绘制面板
				var vals = info.info = JSON.parse(info.info);
				ctx.setTitle(info);
				ctx.setValue(vals);
				if(callBack){
					callBack(info);
				}
			}
		});
	},
	//刷新状态
	refreshState:function(){
		if(this.instanceId){
			var ctx = this;
			oc.util.ajax({
				url:oc.resource.getUrl("topo/backboard/allInfs.htm?instanceId="+this.instanceId),
				dataType:"json",
				success:function(info){
					//过滤出port
					var ports=[];
					for(var i=0;i<ctx.els.length;++i){
						var el = ctx.els[i];
						if(el.dataType && el.dataType=="port"){
							if(el instanceof ImageSet){
								$.each(el.list,function(idx,item){
									ports.push(item);
								});
							}else{
								ports.push(el);
							}
						}
					}
					//更新状态
					var i=0;
					for(;(i<info.length&&i<ports.length);++i){
						var item = info[i];
						var el = ports[i];
						if(el && el instanceof Image){
							var tmpValue = el.getValue();
							var src = tmpValue.src;
							var parts = src.split(".");
						
							//图标颜色状态更换
							if(item.state.toLowerCase()=="normal"){
								tmpValue.src = parts[0]+"."+parts[1];
							}else if(item.state.toLowerCase()=="adminormal_opercritical"){
								var state = "danger";
								tmpValue.src = parts[0].split("_")[0]+"_"+state+"."+parts[1];
							}else{
								var state = (item.state == "CRITICAL")?"danger":item.state;
								tmpValue.src = parts[0].split("_")[0]+"_"+state+"."+parts[1];
							}
							el.rawItem=$.extend(el.rawItem,item);
							el.setValue(tmpValue);
						}
					}
					//处理未知的接口
					if(i<=ports.length){
						for(;i<ports.length;++i){
							var el = ports[i];
							if(el&&el.getValue){
								var tmpValue = el.getValue();
								var src = tmpValue.src;
								var parts = src.split(".");
								tmpValue.src = parts[0].split("_")[0]+"_nodata."+parts[1];
								el.setValue(tmpValue);
							}
						}
					}
				},
				type:"post"
			});
		}
	},
	//加锁
	lock:function(){
		$.each(this.cs.chosed,function(idx,c){
			c.setEditable(false);
			c.setState("normal");
		});
		if(this.clickedImage){
			this.clickedImage.setEditable(false);
		}
	},
	//解锁指定image
	unlock:function(){
		if(this.clickedImage){
			this.clickedImage.setEditable(true);
		}
	},
	//开始框选
	choose:function(flag){
		this.selfAdapt();
	},
	//旋转
	rotation:function(){
		$.each(this.cs.chosed,function(idx,c){
			c.rotage((c.angle+90)%360);
		});
	},
	setValue:function(vs){
		var ctx = this;
		this.reset();
		$.each(vs,function(key,items){
			var dataType=(key+"").replace("s","");
			if(dataType == "background"){
				console.log(items);
				items[0].width = 624;
//				dataType="card";
			}
			if(! items instanceof Array){
				items=[items];
			}
			$.each(items,function(idx,item){
				switch(item.type||"image"){
				case "image":
					item = $.extend({
						w:26,h:26,x:100,y:100,width:26,height:26
					},item);
					if(dataType=="port"){
						item.src="themes/default/images/topo/backboard/port/port.png";
					}else{
						if(item.imgUrl){
							item.src=item.imgUrl;
						}else{
							item.src=item.src;
						}
					}
					item.w=parseInt(item.width);item.h=parseInt(item.height);
					item.x=parseInt(item.x);item.y=parseInt(item.y);
					var img = new Image({
						paper:ctx.paper,
						holder:ctx.holder,
						attr:item,
						noCtxMenu:true
					});
					$(img.img.node).unbind("contextmenu");
					img.rawItem=item;
					img.setValue(item);
					img.dataType=dataType;
					ctx.els.push(img);
					ctx.iterator(img);
					break;
				case "imageSet":
					var imgset = new ImageSet({
						paper:ctx.paper
					});
					imgset.setValue(item,function(it){
						var tmp = new Image({
							paper:ctx.paper,
							holder:ctx.holder,
							attr:it,
							noCtxMenu:true
						});
						tmp.rawItem=it;
						it.src=it.imgUrl;
						tmp.setValue(it);
						tmp.dataType="port";
						ctx.iterator(tmp);
						return tmp;
					});
					imgset.dataType="port";
					ctx.els.push(imgset);
					break;
				}
			});
		});
		this.paper.setViewBox(0,0,$(this.holder).width(),$(this.holder).height());
	},
	hideImageSetFg:function(){
		for(var i=0;i<this.els.length;++i){
			var item = this.els[i];
			if(item instanceof ImageSet){
				item.fg.hide();
			}
		}
	},
	selfAdapt:function(){
		var ctx = this;
		if(!this.scale){
			oc.resource.loadScript("resource/module/topo/api/TopoScale.js",function(){
				ctx.scale=new TopoScale({
					holder:$(ctx.holder)
				});
				ctx.scale.selfAdapt(ctx.getRange(),true);
			});
		}else{
			ctx.scale.selfAdapt(ctx.getRange(),true);
		}
	},
	iterator:function(ele){
		if(this.args.iterator){
			this.args.iterator.call(this,ele);
		}
	},
	setTitle:function(data){//设置面板显示标题信息
		//设备名称：core 设备IP：172.12.2.12 设备类型：三层交换机 设备型号：catalyst3524XL 设备厂商：cisco 设备OID:1.3.6.1.4.1.9.248
		if(data){
			var name = "设备名称："+(data.name?data.name:"--");
			var ip = "  设备IP:"+(data.ip?data.ip:"--");
			var type = "  设备类型："+(data.typeName?data.typeName:"--");
			var vender = "  设备厂商："+(data.venderName?data.venderName:"--");
//			var oid = "  设备OID:"+(data.oid?data.oid:"--");
			$("#bbTitle").text(name+ip+type+vender);
		}
	},
	getValue:function(){
		var retn = {
			backgrounds:[],
			cards:[],
			ports:[],
			powers:[],
			logos:[],
			others:[],
			imageSets:[]
		};
		$.each(this.els,function(idx,el){
			if(!el) return ;
			var value = el.getValue();
			if(value.type=="image"){
				value.width=value.w;
				value.height=value.h;
				delete value.w;
				delete value.h;
			}
			retn[el.dataType+"s"].push(value);
		});
		return retn;
	},
	//重置画布
	reset:function(){
		$.each(this.els,function(idx,el){
			if(el){
				el.remove();
				el=null;
			}
		});
		this.els=new Array();
		this.cs.setEls(this.els);
	},
	setEditable:function(flag){
		this.editable=!!flag;
		var ctx = this;
		$.each(this.els,function(idx,el){
			el.setEditable(ctx.editable);
		});
		this.sizer.setEditable(this.editable);
	},
	regEvent:function(){
		var ctx = this;
		$(this.holder).droppable({
			accept:".icon",
			onDrop:function(e,dom){
				var $dom = $(dom).draggable("proxy");
				var $img = $dom.find("img");
				var offset = $(this).offset();
				var pos = {
					x:$dom.offset().left-offset.left,
					y:$dom.offset().top-offset.top,
					w:parseInt($img.css("width"))||16,
					h:parseInt($img.css("height"))||16
				};
				var dataType=$dom.attr("data-type");
				if(dataType=="port"){
					setTimeout(function(){
						var pd = new PortAttrDia();
						pd.on("load",function(){
							pd.setValue({
								"width":pos.w,"height":pos.h
							});
							pd.on("ok",function(cfg){
								//绘制接口
								var w=parseInt(cfg.width),h=parseInt(cfg.height),rw=2,rh=2;
								//方向
								var angle=parseInt(cfg.angle);
								var imgs = [];
								for(var i=0;i<parseInt(cfg.rows);++i){
									for(var j=0;j<parseInt(cfg.cols);++j){
										var img = new Image({
											paper:ctx.paper,
											holder:ctx.holder,
											attr:{
												x:pos.x+j*(w+rw),y:pos.y+i*(h+rh),w:w,h:h,src:$img.attr("src")
											},
											noCtxMenu:true
										});
										img.rotage(angle);
										if(cfg.reverse){
											if(i%2>0){
												img.rotage(angle+180);
											}
										}
										img.dataType=dataType;
										imgs.push(img);
									}
								}
								if(imgs.length==1){
									ctx.els.push(imgs[0]);
								}else{
									//创建一个imageSet
									var imageSet = new ImageSet({
										paper:ctx.paper,
										list:imgs
									});
									imageSet.dataType=dataType;	
									ctx.els.push(imageSet);
								}
							});
						});
					},20);
				}else{
					var img = new Image({
						paper:ctx.paper,
						holder:ctx.holder,
						attr:{
							x:pos.x,y:pos.y,w:pos.w,h:pos.h,src:$img.attr("src")
						},
						noCtxMenu:true
					});
					img.dataType=dataType;
					ctx.els.push(img);
				}
			}
		});
		eve.on("element.click",function(){
			if(this.paper==ctx.paper){
				ctx.sizer.setNode(this);
				ctx.sizer.show();
				ctx.sizer.toFront();
				ctx.currentElement = this;
			}
		});
		eve.on("element.remove",function(){
			if(this.paper==ctx.paper){
				ctx.sizer.hide();
				for(var i=0;i<ctx.els.length;++i){
					if(ctx.els[i]==this){
						delete ctx.els[i];
					}
				}
			}
		});
	}
};