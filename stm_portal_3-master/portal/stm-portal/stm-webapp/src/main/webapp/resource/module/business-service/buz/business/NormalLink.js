buzdefine("bz/NormalLink",["bz/extend","bz/Link"],function(extend,Link){
	return extend(function(args){
		args=$.extend({
             drawer:null
    	},args);
		if(!args.drawer || !args.bizCfg || !args.topoSVG){
    		throw "drawer can't be null";
    	}

    	this.topoSVG = args.topoSVG;
    	this.bizCfg=args.bizCfg;
		
		Link.apply(this,arguments);


	},Link,{
		init:function(){

			this.node=this.args.drawer.group();
			//this.node.line=this.node.line(0,0,1,1).attr({stroke:"red"});
			this.node.bgLine=this.node.path([['M',0,0], ['L',0,0]]).attr({stroke:"red",'stroke-width':12,'stroke-opacity':0});
			this.node.line=this.node.path([['M',0,0], ['L',0,0]]);

			this.from = {};
			this.to = {};

			this.regEvent();
			
		},
		set_data:function(data){
			this.data=data;
			
			delete this.from;
			this.from = $.extend(true,{},data.attr.from);
			this.from.id = data.fromNode;
			
			delete this.to;
			this.to = $.extend(true,{},data.attr.to);
			this.to.id = data.toNode;
			
			

			if(data.fromNode != undefined ){
				this.from.node = this.nodes[data.fromNode];
				if(this.from.node){
					this.from.node.combineLink(this);
				}
			}

			if(data.toNode != undefined){
				this.to.node = this.nodes[data.toNode];
				if(this.to.node){
					this.to.node.combineLink(this);
				}
			}
			
			//console.log("set-pose:" + this.to.pose);
			//console.log(this.to);
		},
		get_data:function(){
			this.data.attr.zIndex = this.node.position();

			return this.data;
		},
		regEvent:function(){
			if(this.bizCfg){
				var editAble = this.bizCfg.get("editAble");
				if(!editAble)
					return;
			}

			var ctx=this;
			var drag = this.node;
			var ct= {};//控制点集合
			drag.on('mousedown', function(e){
				e.preventDefault();
				stopBubble(e);

				if(e.which == 3){
					document.oncontextmenu = function()
                    {
						
						//右键连线也需选中连线
						/************************/
						if(!e.ctrlKey){
							SVG.select('path.path-selected').removeClass('path-selected');
							ctx.topoSVG.businessView.cancelSelected();
						}

						ctx.node.line.addClass("path-selected");
						if(ctx.node.stArrow)
							ctx.node.stArrow.addClass("path-selected");
						if(ctx.node.edArrow)
							ctx.node.edArrow.addClass("path-selected");

						ctx.topoSVG.bizCfg.addSelected('links',ctx.data.id,ctx);
						/************************/
						
                        ctx.topoSVG.businessView.showRightMenu({mouse:e,ctx:ctx,type:'link'});
                        document.oncontextmenu=undefined;
                        return false;
                    }
					stopBubble(e);
					return;
				}

			  	var cObj = ctx.topoSVG.args.root;;
				cObj.on('mousemove.lineMoving', function(e){
					e.preventDefault();
					ct.mousemoving = true;
					var bs = $(ctx.node.node.viewportElement).offset();
			  		var p = {x:e.clientX-bs.left,y:e.clientY-bs.top};
					if(!ct.start || getDistance([ct.start.x,ct.start.y],[p.x,p.y])<2){
						return ;
					}

					//线条内部调整
					if(ct.inside){
						var cti = ct.inside;
						window.cti = cti;
						var idx = cti.index;
						var s = cti.path[idx];
						var e = cti.path[idx+1];
						var bd = {x:cti.path[0][1],y:cti.path[0][2],
										x1:cti.path[cti.path.length-1][1],y1:cti.path[cti.path.length-1][2]
									}
						if(s[0] == 'M' || idx+1 == cti.path.length-1){
							return;
						}
						if(s[1] == e[1]){//变化x轴的位置
							
							s[1] = e[1] =p.x;
							cti.dat = {axis:'x',v:p.x};
						}else if(s[2] == e[2]){//变化y轴的位置
							s[2] = e[2] =p.y;
							cti.dat = {axis:'y',v:p.y};
						}
						cti.dat.index = cti.index; 
						//console.log(cti.dat);

						if(!ctx.data.attr.trend)
							ctx.data.attr.trend = {};
						ctx.data.attr.trend[cti.index]=cti.dat;//更新线路的走向*/
						ctx.refresh();
						return;
					}


					//改变连接的节点
					if(ct.point == 'from' || ct.point == 'to'){
						//重新调节连接点的时候则删除trend，防止其对新连线产生干扰
						ctx.data.attr.trend = {};
					}

					var arc = ctx.args.drawer.anchor;
					//console.log(arc);
					var tdata = ctx.data;
					var updateObj = {};
					if(ct.point == 'from'){
				  		var nd = tdata.attr.from;
				  		if(arc && arc.mouseover_node){

				  			nd.pose = ctx._getNodePos([p.x,p.y],arc.mouseover_node);
				  			nd.id = arc.mouseover_node.data.id;
				  			delete nd.x;
				  			delete nd.y;
				  			tdata.fromNode = nd.id;
				  			updateObj.newNode = nd.node;
				  		}else{
				  			updateObj.oldNode = nd.node;
				  			delete nd.pose;
				  			delete nd.id;
				  			tdata.fromNode = undefined;
				  			nd.x =p.x;
				  			nd.y =p.y;
				  		}
				  		tdata.attr.from = nd;
				 	}else if(ct.point == 'to'){
				  		var nd = tdata.attr.to;
				  		if(arc && arc.mouseover_node){
				  			nd.pose = ctx._getNodePos([p.x,p.y],arc.mouseover_node);
				  			nd.id = arc.mouseover_node.data.id;
				  			delete nd.x;
				  			delete nd.y;
				  			tdata.toNode=nd.id;
				  			updateObj.newNode = nd.node ;
				  		}else{
				  			updateObj.oldNode = nd.node;
				  			delete nd.pose;
				  			delete nd.id;
				  			tdata.toNode = undefined;
				  			nd.x =p.x;
				  			nd.y =p.y;
				  		}
				  		tdata.attr.to = nd;
					}

					ctx.setValue(tdata);
					updateObj.link = ctx;
					
					if(updateObj.oldNode){
						updateObj.oldNode.detachLink(ctx.data.id);
					}

					if(updateObj.newNode){
						updateObj.newNode.combineLink(ctx);
					}
				});

				cObj.on('mouseup.lineMoving', function(e){
					e.preventDefault()
					cObj.unbind(".lineMoving");
					$('body').css("cursor", "");
					ct = {};
					cObj.unbind('.lineMoving');
					var arc = ctx.args.drawer.anchor;
					if(arc){
						delete arc.pose_mouseover;
                    	delete arc.mouseover_node;
                	}
				});
			});


			drag.on("mouseover",function(e){
				e.preventDefault();
				if(ct.mousemoving)
					return;
			  	var pth = this.line.array().value;
			  	var oft =$(this.node.viewportElement).offset();
			  	var p = {x:e.clientX-oft.left,y:e.clientY-oft.top};
			  	var d = getDistance([pth[0][1],pth[0][2]],[p.x,p.y]);
			  	var d2 = getDistance([pth[pth.length-1][1],pth[pth.length-1][2]],[p.x,p.y]);
				ct = {};
				ct.start = p;

				if(d<10 || d2 < 10){
					$(this.node).css("cursor", " move");
					$('body').css("cursor", " move");
					if(d<10){
						ct.point = 'from';
					}else if(d2 < 10){
						ct.point = 'to';
					}					
				}else{
					ct.point = 'none';
					var pi=0,i=0,min;
					var s1 = pth[i],s2=pth[i+1];
					min = getDistP0toP1P2([p.x,p.y],[s1[1],s1[2]],[s2[1],s2[2]]);
					for(i=0;i<pth.length-1;i++){
						s1 = pth[i],s2=pth[i+1];
						var det = getDistP0toP1P2([p.x,p.y],[s1[1],s1[2]],[s2[1],s2[2]]);
						if(det < min){
							min = det;
							pi=i;
						}

					}
					if(min<10 && pth[pi][0] != 'M' && pi+1 != pth.length-1){
						if(pth[pi][1] == pth[pi+1][1]){
							$(this.node).css("cursor", "ew-resize");
							$('body').css("cursor", "ew-resize");
						}else if(pth[pi][2] == pth[pi+1][2]){
							$(this.node).css("cursor", "ns-resize");
							$('body').css("cursor", "ns-resize");
						}
						ct.inside ={
							index:pi,
							path:$.extend(true,[],pth)
						}
					}else{
						delete ct.inside;
						$(this.node).css("cursor", "pointer");
					}
				}

				stopEvent(e);
				document.oncontextmenu = function ()
				{	
					return false;
				}
			});
			drag.on("mouseout",function(e1){
				document.oncontextmenu = null;
				var $panel =  ctx.topoSVG.args.root;
				
				$panel.unbind(".link-selected");
				if(!e1.ctrlKey){
					$panel.bind('click.link-selected',function(){
			           	SVG.select('path.path-selected').removeClass('path-selected');
			        	ctx.topoSVG.bizCfg.removeSelected('links');
			    	});
				}
            	$(this.node).css("cursor", "");
            	$('body').css("cursor", "");
			});

			drag.on("click",function(e) {
				e.preventDefault();

				$(document).unbind(".link-edit");
			 	$(document).bind("keydown.link-edit",function(e){
					if(e.keyCode ==46){
						ctx.topoSVG.businessView.removeLink(ctx);
						$(document).unbind(".link-edit");
					}
				});
				if(!e.ctrlKey){
					SVG.select('path.path-selected').removeClass('path-selected');
					ctx.topoSVG.businessView.cancelSelected();
				}

				ctx.node.line.addClass("path-selected");
				if(ctx.node.stArrow)
					ctx.node.stArrow.addClass("path-selected");
				if(ctx.node.edArrow)
					ctx.node.edArrow.addClass("path-selected");

				ctx.topoSVG.bizCfg.addSelected('links',ctx.data.id,ctx);
				
				stopBubble(e);
				stopEvent(e);
			});

			drag.on('dblclick',function(e){
				stopBubble();
			});
		},
		getValue:function(){
			return this.get_data();
		},
		setValue:function(info){
			this.set_data(info);
			this.refresh();
		},
		refresh:function(){
			//这里可以采用this.calPath();方法来计算路径，这是旧方法 
			var pth = this.calPath2();
			
			var arrowColor  = "black";
			var tdata = this.data.attr; 
			if(tdata.attr){
				if(tdata.attr.stroke){
					arrowColor = tdata.attr.stroke;
				}
				this.node.line.attr(tdata.attr);
			}

			if(tdata.arrow == 'double-arrow'){
				var lineWidth = tdata.attr['stroke-width'];
				var color = tdata.attr['stroke'];
				//绘制起始箭头
				var st1 = pth[1];
				var st2 = pth[3];
				var drect = [st2[0]-st1[0],st2[1]-st1[1]];
				var arrow = this._drawArrow(drect,st1,lineWidth,0);
				var p1 = arrow[3];
				var p2 = arrow[5];
				var e = [(p1[0]+p2[0])/2,(p1[1]+p2[1])/2];
				pth[1]=e;

				if(!this.node.stArrow)
					this.node.stArrow = this.node.path(arrow.join(' ')).attr({'stroke':color,fill:color});
				else
					this.node.stArrow.plot(arrow.join(' ')).attr({'stroke':color,fill:color});
			}else{
				if(this.node.stArrow)
					this.node.stArrow.remove();
				delete this.node.stArrow;
			}

			if(tdata.arrow == 'double-arrow' || tdata.arrow == 'single-arrow' ){
				var lineWidth = tdata.attr['stroke-width'];
				var color = tdata.attr['stroke'];
				//绘制结束箭头
				var ed1 = pth[pth.length-1];
				var ed2 = pth[pth.length-3];
				drect = [ed1[0]-ed2[0],ed1[1]-ed2[1]];
				arrow = this._drawArrow(drect,ed1,lineWidth,Math.PI);
				
				var p1 = arrow[3];
				var p2 = arrow[5];
				var e = [(p1[0]+p2[0])/2,(p1[1]+p2[1])/2];
				pth[pth.length-1]=e;
	
				if(!this.node.edArrow)
					this.node.edArrow = this.node.path(arrow.join(' ')).attr({'stroke':color,fill:color});
				else
					this.node.edArrow.plot(arrow.join(' ')).attr({'stroke':color,fill:color});
			}

			if(!tdata.arrow || tdata.arrow=='none' ){
				if(this.node.edArrow)
					this.node.edArrow.remove();
				if(this.node.stArrow)
					this.node.stArrow.remove();
				delete this.node.edArrow;
				delete this.node.stArrow;
			}
			this.node.bgLine.plot(pth.join(' ')).fill('none').attr("bgLine","bg");
			this.node.line.plot(pth.join(' ')).fill('none');
		},
		calPath:function(){//生成绘制路径
			var type = this.data.attr.type;
			var st,ed;
			
			//取两结点间的最短距离
			var tg = this.getMinPath(this.from,this.to);
			st=tg.from;
			ed=tg.to;

			if(this.data.attr.from.pose && this.data.attr.from.pose !='min'){//取指定的结点位置
				st = this.getPos(this.from);
			}else{
				this.from.pose = tg.fromPose;

			}
			if(this.data.attr.to.pose && this.data.attr.to.pose != 'min'){//取指定的结点位置
				ed = this.getPos(this.to);
			}else{
				this.to.pose = tg.toPose;
			}
			//console.log(this.from);
			//console.log(this);
			//console.log(this.to);
			//console.log(st + " " + ed);
			
			//if(this.to.node)
			//	console.log(this.to.node.node.bbox());


			//线路不存在走势限制的情况
			var line =['M'];
			line.push(st);
			if(type == 'poly'){//折线绘制
				var edP = [];
				var endLin = [];
				var sdetY = Math.abs(st[1]-ed[1])/10;//起始结束y方向偏移距离
				var sdetX = Math.abs(st[0]-ed[0])/10;//起始结束x方向偏移距离

				if(this.to.pose){
					if(this.to.pose == 's'){
						edP = [ed[0],ed[1]+sdetY];
					}else if(this.to.pose == 'n'){
						edP = [ed[0],ed[1]-sdetY];
					}else if(this.to.pose == 'w'){
						edP = [ed[0]-sdetX,ed[1]];
					}else if(this.to.pose == 'e'){
						edP = [ed[0]+sdetX,ed[1]];
					}else if(this.to.pose == 'min'){
						edP = [ed[0],ed[1]];
					}else{
						edP = [ed[0],ed[1]];
					}
					
				}else{
					edP=ed;
				}

				//起始点位置判断
				if(this.from.pose == 's'){
					line.push('L');
					line.push([st[0],st[1]+sdetY]);
					
					line.push('L');
					line.push([edP[0],st[1]+sdetY]);
					
				}else if(this.from.pose == 'n'){
					line.push('L');
					line.push([st[0],st[1]-sdetY]);
					
					line.push('L');
					line.push([edP[0],st[1]-sdetY]);
					
				}else if(this.from.pose == 'w'){
					line.push('L');
					line.push([st[0]-sdetX,st[1]]);
					
					line.push('L');
					line.push([st[0]-sdetX,edP[1]]);
				}else if(this.from.pose == 'e'){
					line.push('L');
					line.push([st[0]+sdetX,st[1]]);
					
					line.push('L');
					line.push([st[0]+sdetX,edP[1]]);
				}
				
				//结束点位置判断
				if(!(edP[0]==ed[0] && edP[1]==ed[1])){
					line.push('L');
					line.push([edP[0],edP[1]]);
				}
			}

			line.push('L');
			line.push(ed);

			//线路存在走势限制的情况
			var trend = this.data.attr.trend;
			if(type == 'poly' && trend){		
				
				for(var i in trend){
					var o = trend[i];
					var idx = parseInt(i)*2+1; 

					if(o.axis == 'x') {
						line[idx][0] =o.v;
						line[idx+2][0] =o.v;
					}else if(o.axis == 'y') {
						line[idx][1] =o.v;
						line[idx+2][1] =o.v;
					}
				}
				
			}
			return line;
		},
		calPath2:function(){
			var type = this.data.attr.type;
			var st,ed;
			
			//取两结点间的最短距离
			var tg = this.getMinPath(this.from,this.to);
			st=tg.from;
			ed=tg.to;
			if(this.data.attr.from.pose && this.data.attr.from.pose !='min'){//取指定的结点位置
				st = this.getPos(this.from);
			}else{
				this.from.pose = tg.fromPose;

			}
			if(this.data.attr.to.pose && this.data.attr.to.pose != 'min'){//取指定的结点位置
				ed = this.getPos(this.to);
			}else{
				this.to.pose = tg.toPose;
			}

			if(type == 'line'){
				var line =['M'];
				line.push(st);
				line.push('L');
				line.push(ed);
				return line;
			}
			//绘制折线
			if(type == 'poly'){
				return this._genPolyLine(st,ed);
			}
		},
		_genPolyLine:function(st,ed){
			
			var stRect = {x:st[0],y:st[1],x2:st[0],y2:st[1]};//起始节点的长宽及位置
			var edRect = {x:ed[0],y:ed[1],x2:ed[0],y2:ed[1]};//结束节点的长宽及位置

			var homeScrollTop = 0;
			if(this.topoSVG.args.selector.closest('div.home-content') && this.topoSVG.args.selector.closest('div.home-content').length > 0){
				homeScrollTop = $('.home-content').scrollTop();
			}
			//获取当前浏览器滚动高度
			var scrollY =  window.scrollY || window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || homeScrollTop || 0;

			if(this.from.id){
				var nd = this.nodes[this.from.id];
				stRect = nd.node.rbox();
			}
			
			if(this.to.id){
				var nd = this.nodes[this.to.id];
				edRect = nd.node.rbox();
			}
			
			edRect.y += scrollY;
			edRect.y2 += scrollY;
			stRect.y += scrollY;
			stRect.y2 += scrollY;
			
			var m ={
				x:stRect.x,
				y:stRect.y,
				x2:stRect.x2,
				y2:stRect.y2
			}
			for(o in edRect){
				var v = edRect[o];
				if(o == 'x' || o == 'y'){
					if(v < m[o]){
						m[o]=v;
					}
				}else if(o == 'x2' || o == 'y2'){
					if(v > m[o]){
						m[o]=v;
					}
				}
			}

			var edPth =[];
			var edP = [ed[0],ed[1]];
			stRect.w = stRect.x2 - stRect.x;
			stRect.h = stRect.y2 - stRect.y;
			stRect.cw = stRect.w /2;
			stRect.ch = stRect.h /2;

			edRect.w = edRect.x2 - edRect.x;
			edRect.h = edRect.y2 - edRect.y;
			edRect.cw = edRect.w /2;
			edRect.ch = edRect.h /2;

			

			var line =['M'];
			line.push(st);


			var dct = [ed[0]-st[0],ed[1]-st[1]];
			var ske = 20; //skewing,偏移量

			if(!this.to.pose || !this.from.pose){
				var tx = st[0];
				var ty = st[1];
				tx = st[0] + dct[0]; 
				line.push('L');
				line.push([tx,ty]);

				line.push('L');
				line.push(ed);
					
				return line;
			}

			//起始点位置判断
			if(this.from.pose == 'n'){
				var tx = st[0];
				var ty = stRect.y-ske;
				if(edRect.y2<=stRect.y){//起始块上半区域
					if(this.to.pose == 'n'){
						ty = edRect.y -ske;
						
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');
						line.push([tx,ty]);
					}else if(this.to.pose == 's'){
						ty = ed[1]+ske;
						var d = stRect.y-edRect.y2;
						if( d > 0){
							ty = edRect.y2 + d/2;
						}
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');
						line.push([tx,ty]);
					}else if(this.to.pose == 'w'){
						var d = stRect.y-edRect.y2;
						if(dct[0]<0){//在右侧区域
							if( d > 2*ske){
								ty = edRect.y2 + d/2;
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0]-ske;
								line.push('L');
								line.push([tx,ty]);
								
								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
							}else{
								ty = edRect.y -ske;
								line.push('L');
								line.push([tx,ty]);

								tx = edRect.x - ske;
								line.push('L');
								line.push([tx,ty]);

								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
							}
						}else{//左侧区域
							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);

						}
					}else if(this.to.pose == 'e'){
						var d = stRect.y-edRect.y2;
						if(dct[0]<0){//在右侧区域
							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
						}else{//左侧区域
							if( d > 2*ske){
								ty = edRect.y2 + d/2;
								line.push('L');
								line.push([tx,ty]);

								tx = edRect.x2 + ske;
								line.push('L');
								line.push([tx,ty]);
								
								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
							}else{
								ty = edRect.y -ske;
								line.push('L');
								line.push([tx,ty]);

								tx = edRect.x2 + ske;
								line.push('L');
								line.push([tx,ty]);

								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
							}	
						}
					}
				}else{//起始块下半区域
					if(this.to.pose == 'n'){
						ty = stRect.y -ske;
							
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');
						line.push([tx,ty]);
					}else if(this.to.pose == 's'){
						ty = stRect.y -ske;
						var d = stRect.x2-edRect.x;
						line.push('L');
						line.push([tx,ty]);

						tx = edRect.x2 + ske;
						if(d<=0 && d < -2*ske){
							tx = stRect.x2 - d/2;
						}else{
							
							if(stRect.x-edRect.x2 < 2*ske){
								tx = stRect.x2 + ske;
							}else{
								tx = edRect.x + d/2;
							}

						}
						
						line.push('L');
						line.push([tx,ty]);
						
						ty = edRect.y2 + ske;
						line.push('L');
						line.push([tx,ty]);
						
						tx = ed[0]; 
						line.push('L');
						line.push([tx,ty]);
					}else if(this.to.pose == 'w'){
						ty = stRect.y -ske;
						
						var d = edRect.x -stRect.x2;
						
						if(d>0){
							line.push('L');
							line.push([tx,ty]);
							
							tx = stRect.x2 + d/2;
							
							line.push('L');
							line.push([tx,ty]);	

							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);

						}else{
							
							if(stRect.y>edRect.y){
								
								ty = edRect.y -ske;	
								line.push('L');
								line.push([tx,ty]);
								
								if(stRect.x<edRect.x)
									tx = stRect.x-ske;
								else 
									tx = edRect.x-ske;

								line.push('L');
								line.push([tx,ty]);

								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
							}else{
								line.push('L');
								line.push([tx,ty]);

								if(stRect.x<edRect.x)
									tx = stRect.x-ske;
								else 
									tx = edRect.x-ske;
								line.push('L');
								line.push([tx,ty]);

								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
							}
						}
					}else if(this.to.pose == 'e'){
						ty = stRect.y -ske;
						var d = edRect.x -stRect.x2;
						
						if(stRect.y>edRect.y && dct[0]>0){
							ty = edRect.y -ske;
						}

						line.push('L');
						line.push([tx,ty]);
						
						tx = edRect.x2 + ske;
						if(d<0){
							//tx = edRect.x2 + d/2;
						}
						line.push('L');
						line.push([tx,ty]);

						ty = ed[1];
						line.push('L');
						line.push([tx,ty]);

							
					}
				}
			}else if(this.from.pose == 's'){
				var tx = st[0];
				var ty = stRect.y2+ske;
				if(stRect.y2<=edRect.y){//起始块上半区域
					if(this.to.pose == 'n'){
						if(stRect.x2 < ed[0]){ //起始块位于左侧
							var d = edRect.y - stRect.y2;
							if(d<= 0){
								line.push('L');
								line.push([tx,ty]);

								tx = stRect.x - ske;
								line.push('L');
								line.push([tx,ty]);

								ty = stRect.y - ske;
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);

							}else{

								ty = stRect.y2 + d/2;
								line.push('L');
								line.push([tx,ty]);
								
								tx = ed[0];	
								line.push('L');
								line.push([tx,ty]);
							}

						}else{//起始块位于右侧
							var d = edRect.y - stRect.y2;
							if(d<= 0){
								line.push('L');
								line.push([tx,ty]);

								tx = stRect.x2 + ske;
								line.push('L');
								line.push([tx,ty]);

								ty = stRect.y - ske;
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);

							}else{

								ty = stRect.y2 + d/2;
								line.push('L');
								line.push([tx,ty]);
								
								tx = ed[0];	
								line.push('L');
								line.push([tx,ty]);


							}
						}
					}else if(this.to.pose == 's'){
						ty = edRect.y2 + ske; 
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');
						line.push([tx,ty]);
					}else if(this.to.pose == 'w'){
						if(stRect.x2 < ed[0]){//起始节点在左侧区域
							ty = ed[1]; 
							line.push('L');
							line.push([tx,ty]);

						}else if(stRect.x < edRect.x2){
							ty = stRect.y2;
							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x-ske; 
							line.push('L');
							line.push([tx,ty]);

							ty = ed[1]; 
							line.push('L');
							line.push([tx,ty]);
						}else{
							
							ty = edRect.y2 + ske;
							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x-ske; 
							line.push('L');
							line.push([tx,ty]);

							ty = ed[1]; 
							line.push('L');
							line.push([tx,ty]);
						}
					}else if(this.to.pose == 'e'){
						if(stRect.x2 < edRect.x){
							ty = edRect.y2 + ske;
							var d = edRect.y - stRect.y2;
							if(d>0){
								ty = stRect.y2 + d/2;	
							}
							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x2 + ske;
							line.push('L');
							line.push([tx,ty]);

							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
						}else if(stRect.x < edRect.x2){
							var d = ed[1] - stRect.y2;
							ty = stRect.y2 + d/2;
							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x2 + ske;
							line.push('L');
							line.push([tx,ty]);
							
							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
						}else{
							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
						}
					}
				}else{//起始块下半区域
					if(this.to.pose == 'n'){
						if(stRect.x2 < edRect.x2){ //起始块位于左侧
							
							var d = edRect.x - stRect.x2;
							if(d<= 2*ske){
								line.push('L');
								line.push([tx,ty]);

								tx =  stRect.x - ske;
								line.push('L');
								line.push([tx,ty]);

								ty = edRect.y - ske;
								if(stRect.y < edRect.y)
									ty = stRect.y -ske;
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);

							}else{

								ty = stRect.y2 + ske;
								line.push('L');
								line.push([tx,ty]);

								tx = stRect.x2 + d/2;
								line.push('L');
								line.push([tx,ty]);
								
								ty = edRect.y - ske;
								line.push('L');
								line.push([tx,ty]);


								tx = ed[0];	
								line.push('L');
								line.push([tx,ty]);

							}
						}else{//起始块位于右侧
							var d = stRect.x - edRect.x2;
							if(d<= 2*ske){
								line.push('L');
								line.push([tx,ty]);

								tx =  stRect.x2 + ske;
								
								line.push('L');
								line.push([tx,ty]);

								ty = edRect.y - ske;
								if(stRect.y < edRect.y){
									ty =  stRect.y - ske;
								}
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);

							}else{
								ty = stRect.y2 + ske;
								line.push('L');
								line.push([tx,ty]);

								tx = stRect.x - d/2;
								line.push('L');
								line.push([tx,ty]);
								
								ty = edRect.y - ske;
								line.push('L');
								line.push([tx,ty]);


								tx = ed[0];	
								line.push('L');
								line.push([tx,ty]);

							}
						}
					}else if(this.to.pose == 's'){
						var d = edRect.y2 - stRect.y;
						ty = edRect.y2 + ske; 
						if(edRect.y2 < stRect.y2){
							ty = stRect.y2 + ske; 
						}
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');
						line.push([tx,ty]);
					}else if(this.to.pose == 'w'){
						if(stRect.x2 < ed[0]){//起始节点在左侧区域
							if(stRect.y2 <ed[1]){
								ty = ed[1];
								line.push('L');
								line.push([tx,ty]);
								
							}else{
								var d = ed[0] - stRect.x2;
								line.push('L');
								line.push([tx,ty]);
								if(d>0){
									tx = stRect.x2+d/2;
									line.push('L');
									line.push([tx,ty]);

									ty = ed[1];
									line.push('L');
									line.push([tx,ty]);
								}else{
									line.push('L');
									line.push([tx,ty]);

									tx = ed[0];
									line.push('L');
									line.push([tx,ty]);
								}

							}
							
						}else {
							if(edRect.y2>stRect.y && edRect.y<stRect.y2){
								ty = edRect.y2 + ske;
								if(stRect.y2 > edRect.y2){
									ty = stRect.y2 + ske;
								}
							}
							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x - ske;

							line.push('L');
							line.push([tx,ty]);
							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
						}
					}else if(this.to.pose == 'e'){
						ty = stRect.y2 + ske;
						if(stRect.y2<edRect.y2){
							ty = edRect.y2 + ske;
							if(st[0]> ed[0])
								ty=ed[1];
							
						}
						line.push('L');
						line.push([tx,ty]);

						tx = edRect.x2 + ske;
				
						if(st[0]>ed[0])
							tx = ed[0] + ske;
						

						line.push('L');
						line.push([tx,ty]);

						ty = ed[1];
						line.push('L');
						line.push([tx,ty]);
					}
				}

			}else if(this.from.pose == 'w'){
				var tx = st[0]-ske;
				var ty = st[1];
				if(this.to.pose == 'n'){
					if(stRect.y2 >= edRect.y){
						line.push('L');
						line.push([tx,ty]);
						
						ty = stRect.y - ske; 
						if(stRect.y > edRect.y){
							ty = edRect.y - ske; 
						}
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');
						line.push([tx,ty]);

					}else{
						line.push('L');
						line.push([tx,ty]);

						var d = edRect.y - stRect.y2;
						ty = stRect.y2 + d/2;
						line.push('L');
						line.push([tx,ty]);
						
						tx =ed[0];
						line.push('L');
						line.push([tx,ty]);
					}
				}else if(this.to.pose == 's'){
					if(edRect.x2 < stRect.x2){
						if(edRect.y2 < st[1]){
							if(edRect.x2 < stRect.x){
								tx = ed[0]; 
								line.push('L');
								line.push([tx,ty]);
							}else{

								line.push('L');
								line.push([tx,ty]);

								var d = stRect.y - edRect.y2;
								if(d>0)
									ty = edRect.y2 + d/2;
								else
									ty = edRect.y2 + ske;
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);
							}
						}else{
							var d = stRect.x - edRect.x2;
							tx = edRect.x - ske; 
							if(d>0){
								tx = edRect.x2+d/2;	
							}
							line.push('L');
							line.push([tx,ty]);

							ty = edRect.y2 + ske;
							line.push('L');
							line.push([tx,ty]);

							tx = ed[0]; 
							line.push('L');
							line.push([tx,ty]);

						}

					}else{
						tx = stRect.x - ske; 
						line.push('L');
						line.push([tx,ty]);

						ty = edRect.y2 + ske;
						line.push('L');
						line.push([tx,ty]);

						tx = ed[0]; 
						line.push('L');
						line.push([tx,ty]);
					}
				}else if(this.to.pose == 'w'){
					if(edRect.x < stRect.x)
						tx = edRect.x -ske;
					line.push('L');
					line.push([tx,ty]);
					
					ty = ed[1];
					line.push('L');
					line.push([tx,ty]);
				}else if(this.to.pose == 'e'){
					if(stRect.y > edRect.y2){//上半区域
						var d =  stRect.y - edRect.y2;
						//console.log(d);
						if(d <= 0){
							line.push('L');
							line.push([tx,ty]);
							
							ty = ed[1];
							if( stRect.x2 < edRect.x){
								ty = edRect.y -ske;
								var td = stRect.y2-edRect.y;
								if(td>0)
									ty = edRect.y+td/2;
									
							}

							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x2 + ske;
							if( stRect.x >= edRect.x2){
								var d = stRect.x - edRect.x2;
								tx = edRect.x2 + d/2;
							}
							line.push('L');
							line.push([tx,ty]);

							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);

						}else{
						
							line.push('L');
							line.push([tx,ty]);

							ty = edRect.y2 + d/2;
							if(stRect.x-edRect.x2>2*ske)
								ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
							
							tx = edRect.x2+ske;
							line.push('L');
							line.push([tx,ty]);

							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);

						}
					}else{
						if(stRect.x > edRect.x2){
						
							var d = stRect.x - edRect.x2;
							tx = edRect.x2 + d/2;
							line.push('L');
							line.push([tx,ty]);
							
							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);

						}else{

							line.push('L');
							line.push([tx,ty]);

							var d = edRect.y - stRect.y2;
							ty = stRect.y2 + d/2;
							if(stRect.y2 > edRect.y){
								ty = stRect.y2+ske;
								if(stRect.y2 < edRect.y2)
								 	ty = edRect.y2 + ske;
							}
							line.push('L');
							line.push([tx,ty]);

							tx = edRect.x2 + ske;
							line.push('L');
							line.push([tx,ty]);

							ty = ed[1];
							line.push('L');
							line.push([tx,ty]);
						}
					}
				}
			}else if(this.from.pose == 'e'){
				var tx = st[0]+ske;
				var ty = st[1];
				if(this.to.pose == 'n'){
					if(stRect.x2 < edRect.x){
						var d = edRect.x - stRect.x2;
						if(d>0){
							if(stRect.y>edRect.y){
								tx = stRect.x2 + d/2;
								line.push('L');
								line.push([tx,ty]);

								ty = edRect.y - ske;
								line.push('L');
								line.push([tx,ty]);

								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);
							}else{
								tx = ed[0];
								line.push('L');
								line.push([tx,ty]);
							}
						}
					}else{
						tx = stRect.x2 + ske;
						if(stRect.x2 < edRect.x2){
							tx = edRect.x2 + ske;
						}
						line.push('L');	
						line.push([tx,ty]);

						ty = edRect.y - ske;
						line.push('L');	
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');	
						line.push([tx,ty]);
					}
				}else if(this.to.pose == 's'){
					if(stRect.x2 < ed[0]){
						var d = edRect.x - stRect.x2;
						if(d<0)
							d =0;
						tx = stRect.x2 + d/2;
						line.push('L');	
						line.push([tx,ty]);

						ty = edRect.y2 + ske;
						line.push('L');	
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');	
						line.push([tx,ty]);
					}else{
						
						tx = stRect.x2 + ske;
						line.push('L');	
						line.push([tx,ty]);

						ty = edRect.y2 + ske;
						line.push('L');	
						line.push([tx,ty]);

						tx = ed[0];
						line.push('L');	
						line.push([tx,ty]);
					}
				}else if(this.to.pose == 'w'){
					if(stRect.x2 < edRect.x){
						tx = st[0] + (ed[0]-st[0])/2;
						line.push('L');	
						line.push([tx,ty]);

						ty = ed[1];
						line.push('L');	
						line.push([tx,ty]);
					}else{
						tx = stRect.x2 + ske;
						line.push('L');	
						line.push([tx,ty]);
						
						if(stRect.y < edRect.y){
							var d = edRect.y - stRect.y2;
							ty = edRect.y -ske;
							if(d > 2*ske){
								ty = stRect.y2 + d/2;
							}

							line.push('L');	
							line.push([tx,ty]);
						}else{
							var d = stRect.y - edRect.y2;
							ty = stRect.y+ske;
							if(d >2 * ske)
								 ty = stRect.y - d/2; 

							line.push('L');	
							line.push([tx,ty]);
						}
						tx = edRect.x -ske;
						line.push('L');	
						line.push([tx,ty]);

						ty = ed[1];
						line.push('L');	
						line.push([tx,ty]);						
					}
				}else if(this.to.pose == 'e'){
					if(stRect.x2 < ed[0]){
						
						tx = edRect.x2 + ske;

						line.push('L');	
						line.push([tx,ty]);

						ty=ed[1];
						line.push('L');	
						line.push([tx,ty]);
					}else{
						line.push('L');	
						line.push([tx,ty]);

						ty = ed[1];
						line.push('L');	
						line.push([tx,ty]);
					}
				}
			}
			

			line.push('L');
			line.push(ed);

			//线路存在走势限制的情况
			var trend = this.data.attr.trend;
			if(trend){		
				for(var i in trend){
					var o = trend[i];
					var idx = parseInt(i)*2+1; 

					if(o.axis == 'x') {
						line[idx][0] =o.v;
						line[idx+2][0] =o.v;
					}else if(o.axis == 'y') {
						line[idx][1] =o.v;
						line[idx+2][1] =o.v;
					}
				}
			}

			return line;
		},
		getMinPath:function(from,to){
			
			var f = [];
			var t = [];
			if(!from.node){
				var pushX = from.x;
				var pushY = from.y;
				if(oc.business.radiox){
					pushX = pushX / oc.business.radiox;
				}
				if(oc.business.radioy){
					pushY = pushY / oc.business.radioy;
				}
				f.push([pushX,pushY,'']);
			}else{
				var box = from.node.node.gdawer.tbox();
				var txtBox = from.node.node.txt.tbox();
				box.cy = box.y + (box.h+txtBox.h)/2;
				f.push([box.x,box.cy,'w']);
				f.push([box.x2,box.cy,'e']);
				f.push([box.cx,box.y,'n']);
				f.push([box.cx,box.y2+txtBox.h,'s']);
			}

			if(!to.node){
				var pushX = to.x;
				var pushY = to.y;
				if(oc.business.radiox){
					pushX = pushX / oc.business.radiox;
				}
				if(oc.business.radioy){
					pushY = pushY / oc.business.radioy;
				}
				t.push([pushX,pushY,'']);
			}else{
				var box = to.node.node.gdawer.tbox();
				var txtBox = to.node.node.txt.tbox();
				box.cy = box.y + (box.h+txtBox.h)/2;

				t.push([box.x,box.cy,'w']);
				t.push([box.x2,box.cy,'e']);
				t.push([box.cx,box.y,'n']);
				t.push([box.cx,box.y2+txtBox.h,'s']);
			}

			var min ={
				m:getDistance(f[0],t[0]),
				from:[f[0][0],f[0][1]],
				to:[t[0][0],t[0][1]],
				fromPose:'e',
				toPose:'w'
			}
			min.fromPose = f[0][2];
			min.toPose = t[0][2];
			for (var i = 0; i < f.length; i++){
				for (var j = 0; j <t.length; j++){
					var m = getDistance(f[i],t[j]);
					if(m<min.m){
						min.m = m;
						min.from = [f[i][0],f[i][1]];
						min.to = [t[j][0],t[j][1]];
						//from.pose = f[i][2];
						//to.pose = t[j][2];
						min.fromPose = f[i][2];
						min.toPose = t[j][2];
					}
				}
			}
			return min;
		},
		getPos:function(node){//根据d获取node节点对应的坐标
			var d = node.pose;
			if(!node.node){
				return [node.x,node.y];
			}
			node = node.node;
			var box = node.node.gdawer.tbox();
			var txtBox = node.node.txt.tbox();
			box.cy = box.y + (box.h+txtBox.h)/2;

			//console.log(txtBox);
			switch(d){
				case 'w':
					return [box.x,box.cy];
				case 'e':
					return [box.x2,box.cy];
				case 'n':
					return [box.cx,box.y];
				case 's':
					return [box.cx,box.y2+txtBox.h];
				default:
					return [box.cx,box.cy];
				break;
			}
		},
		/**
		* 画带有箭头的直线
		* @param d 方向向量，确定绘制箭头方向,如d=[-90,380]
		* @param p 箭头的坐标位置,如p=[10,390]
		* @param w 直线的宽度,大于0的数
		* @param deg 设置箭头绕指定方向旋转的度数
		*
		*/
		_drawArrow:function (d,p,w,deg){
			var slopy,cosy,siny;
			var Par=8.0 + 10*(w-1)/5;//控制箭头的长度
			var scale = 4;//控制箭头的宽度
		    var x3,y3;
		    slopy=Math.atan2(d[1],d[0]);
		    slopy += deg;
		    cosy=Math.cos(slopy);   
		    siny=Math.sin(slopy);
		    
		    x3=Number(p[0]);
			y3=Number(p[1]);

			var p0 =[(Number(x3)+Number(Par*cosy-(Par/scale*siny))),(Number(y3)+Number(Par*siny+(Par/scale*cosy)))];
			var p1 = [Number(x3)+Number(Par*cosy+Par/scale*siny),Number(y3)-Number(Par/scale*cosy-Par*siny)];
			var ph =['M',p,'L',p0,'L',p1,'L',p];
			return ph;
		},
        /*
        * 计算当前鼠标离节点的那个位置比较近
        * m 当前鼠标的位置
        * ctx 鼠标所在的节点 
        */
        _getNodePos:function(m,ctx){
            var box = ctx.node.rbox();
            var min = getDistance(m,[box.cx,box.y]);
                pos = 'n';
            var t = getDistance(m,[box.cx,box.y2]);
            if(t<min){
                min = t;
                pos = 's';
            }
            t = getDistance(m,[box.x,box.cy]);
            if(t<min){
                min = t;
                pos = 'w';
            }
            t = getDistance(m,[box.x2,box.cy]);
            if(t<min){
                min = t;
                pos = 'e';
            }
            return pos;
        }
	});
	
	/*获取两点间的距离*/
	function getDistance(p1,p2){
		var dx = p2[0] - p1[0];
		var dy = p2[1] - p1[1];
		return Math.sqrt(dx*dx + dy*dy)
	}

	/*点p0到p1、p2确定的直线线的距离*/
	function getDistP0toP1P2(p0,p1,p2){
		//直线方程y=kx+b,k=dety/detx;
		//-y+kx+b=0
		//var p0=[309,473]; 
		//var p1=[94,218];
		//var p2=[309,473];
		var detx = p2[0]-p1[0];
		var dety = p2[1]-p1[1];
		var a = -1;
		var k= 0;
		var b = 0;
		if(detx !=0){
			k = dety/detx;
			b = p1[1]- k*p1[0];
		}else{//垂直于x轴的情形
			a = 0;
			k = 1;
			b = -p1[0];
		}
		var d = Math.abs(a*p0[1]+k*p0[0]+b)/(Math.sqrt(a*a+k*k));
		return d;
	}

});