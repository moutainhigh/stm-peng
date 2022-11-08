/**
 * 轮廓控制实现类
 */
buzdefine("bz/ShapeControl", ["bz/Base","bz/extend"], function (Base,extend) {
	
	/*function getBoundingClientRect(obj){
		var _pos = obj.getBoundingClientRect();
		var bs = window.topoSVG.bizCfg.getBaseOffset();
		var pos = {};
		pos.top = _pos.top - bs.top;
		pos.left = _pos.left - bs.left;
		pos.height = _pos.height;
		pos.width = _pos.width;
		return pos;
	}//*/

    return extend(function(args){
    	args=$.extend({
            topoSVG:null,
    		ctx:null,//选中的节点元素信息
    		drawer:null,
            id:null,//全局唯一的shape_controls id
            controller:null,
            selectType:'single'
    	},args);
    	
    	if(args.ctx && args.id && args.topoSVG){
    		this.attr({
    			ctx:args.ctx,
    			id:args.id,
    			drawer:args.drawer,
                controller:args.controller,
    			selectType:args.selectType
    		});

            this.topoSVG = args.topoSVG;

            Base.apply(this,arguments);
    	}else{
    		throw "ctx can't be null";
    	}
    },Base,{
    	set_ctx:function(ctx){
    		this.ctx=ctx;
    	},
    	get_ctx:function(){
    		return this.ctx;
    	},
        get_id:function(){
            return this.id;
        },
        set_id:function(id){
            this.id=id;
        },
        get_selectType:function(){
            return this.selectType;
        },
        set_selectType:function(selectType){
            this.selectType=selectType;
        },
        set_drawer:function(drawer){
            this.drawer=drawer;
        },
        get_drawer:function(){
            return this.drawer;
        },
        set_controller:function(controller){
            this.controller = controller;
        },
        get_controller:function(){
            return this.controller;
        },
        setShowContour:function(b){
        	this.defalut.showContour = b;
    	},
    	setSelectType:function(type){
    		this.selectType = type;
    	},
    	init:function(){

    		this.defalut={
    			showContour:true
    		};
            if(this.drawer.shapeControls == undefined){
            	this.drawer.shapeControls = {};
            }

            this.drawer.shapeControls[this.id] = this;
            
            //指向选中的节点
            this.topoSVG.bizCfg.shapeControls = this.drawer.shapeControls;

    		this.conncect_pos = this.getBoundingClientRect(this.ctx.node.gdawer.node);
            this.outer_pos = this.getBoundingClientRect(this.ctx.node.node);

            this.regEvent();
    	},
    	showShapeControls:function(e){
    		 
    		if(e.ctrlKey)
            	this.selectType ='multiple';
            else
            	this.selectType ='single';

    		this._setSelect();
    		this.outer_pos = this.getBoundingClientRect(this.ctx.node.node);
    		this.plotShapeControls(this.outer_pos);
    		this.regEvent();
    	},
    	showShapeContour:function(e){
    		this.conncect_pos = this.getBoundingClientRect(this.ctx.node.gdawer.node);
    		if(this.ctx.node.txt){
    			var tbox= this.getBoundingClientRect(this.ctx.node.txt.node);
    			this.conncect_pos.height += tbox.height;
    			//this.conncect_pos.width += 10;
    		}

    		this.plotShapeContour(e,this.conncect_pos);
    		this.regEvent();
    	},
    	_setSelect:function(){
    		if(this.selectType == 'single'){
                var selectNodes = this.topoSVG.bizCfg.selected.nodes;
                //当前已选中，不作处理
                if(selectNodes[this.id]){
                    return;
                }

            	for(var i in this.drawer.shapeControls){
            		var sp = this.drawer.shapeControls[i];
            		if(i != this.id)
            			sp.destory();
            	}	
            }else if(this.selectType == 'multiple'){

            }
    	},
        _addMask:function(){
            var bz = this.topoSVG.args.holder;

            if(bz.find('.bz-mask').length==0)
                $('<div/>').addClass('bz-mask').attr("id","bz-mask").css({top:'0px',left:'0px','backgroud-img':'url(img/a.png)',position:'absolute',width:'100%',height:'100%'}).appendTo(bz); 
        },
        _removeMask:function(){
            var bz = this.topoSVG.args.holder;
            bz.find('.bz-mask').remove();
        },
    	regEvent:function(){

    			var _this = this;
    			var ctx = this.ctx;
    			var mouse={};
                var obj = undefined;
                var hander = "#shape_controls ";
                hander = '#' + this.id;
                
                var $panel =  this.topoSVG.args.root;

                var handerOBJ = $(hander + ' .shape_controller');
                handerOBJ.unbind().bind("mousedown",function(e){
                	if(e.which != 1){
                		return;
                	}

                    obj = this;
                    var rect = _this.getBoundingClientRect(ctx.node.node);
                    var panelBase = $(ctx.node.node.viewportElement).offset();

                    mouse.base = {};
                    var baseObj;
                    if($(obj).hasClass("n w")){//西北
                    	baseObj = handerOBJ.parent().find('.shape_controller.s.e');
                       	mouse.base.name = 'se';
                    } else if($(obj).hasClass("n e")){//东北
                    	baseObj = handerOBJ.parent().find('.shape_controller.s.w');
                       	mouse.base.name = 'sw';
                    } else if($(obj).hasClass("s w")){//西南
                    	baseObj = handerOBJ.parent().find('.shape_controller.n.e');
                       	mouse.base.name = 'ne'; 
                    } else if($(obj).hasClass("s e")){//东南
                    	baseObj = handerOBJ.parent().find('.shape_controller.n.w');
                       	mouse.base.name = 'nw'; 
                    }
                    
                    mouse.base.pleft = parseInt(baseObj.parent().css('left'));
                    mouse.base.ptop = parseInt(baseObj.parent().css('top'));

                    mouse.base.left = parseInt(baseObj.css('left'));
                    mouse.base.top = parseInt(baseObj.css('top'));
                   
                   	mouse.base.x = e.clientX;
                	mouse.base.y = e.clientY;
                   	var baseX  = e.clientX,baseY = e.clientY;
                    _this._addMask();
                    var minW=20,minH=20;
                    $panel.bind("mousemove.shape_controls",function(e){
                        stopEvent(e);
                        stopBubble(e);

                        var dx = e.clientX - mouse.base.x;
                        var dy = e.clientY - mouse.base.y;
                        
                        mouse.x = e.clientX;
                        mouse.y = e.clientY;
                        
                        var _pose = {};

                        if(mouse.base.name == 'se'){
                        	_pose.left = mouse.base.pleft + dx;
	                     	_pose.top = mouse.base.ptop + dy;

                        	var pw = rect.width - dx,
                        	ph = rect.height - dy;

	                        _pose.width = pw;
	                        _pose.height = ph;
                        }else if(mouse.base.name ==  'sw'){
                        	_pose.left = mouse.base.pleft;
	                     	_pose.top = mouse.base.ptop + dy;

                        	var pw = rect.width + dx,
                        	ph = rect.height - dy;

	                        _pose.width = pw;
	                        _pose.height = ph;
                        }else if(mouse.base.name == 'ne'){
                        	_pose.left = mouse.base.pleft + dx;
	                     	_pose.top = mouse.base.ptop ;

                        	var pw = rect.width - dx,
                        	ph = rect.height + dy;

	                        _pose.width = pw;
	                        _pose.height = ph;
                        }else if(mouse.base.name == 'nw'){
                        	_pose.left = mouse.base.pleft;
	                     	_pose.top = mouse.base.ptop;

                        	var pw = rect.width + dx,
                        	ph = rect.height + dy;

	                        _pose.width = pw;
	                        _pose.height = ph; 
                        }

                        var osf =$('#'+ _this.id).offset();
                        var d = ctx.getValue();

                        if( _pose.width >=minW){
                            d.attr.x = osf.left -panelBase.left;
                        }

                        if(_pose.height >= minH){
                            d.attr.y = osf.top - panelBase.top;
                        }
                        

                        _pose.width  =  _pose.width<minW?minW:_pose.width;
                        _pose.height =  _pose.height<minH?minH:_pose.height;

                        var rw = _pose.width/d.attr.w,rh=_pose.height/d.attr.h;
                        d.attr.w = _pose.width;
                        d.attr.h = _pose.height;

                        if(_pose.height == minH){
                            var trect = _this.getBoundingClientRect(ctx.node.node);
                            _pose.left = trect.x;
                            _pose.top = trect.y;
                        }
                        if( _pose.width == minW){
                            var trect = _this.getBoundingClientRect(ctx.node.node);
                            _pose.left = trect.x;
                            _pose.top = trect.y;
                        }
                        _this.plotShapeControls(_pose);  

                        _this.reSize(d,ctx);
                        
                        var selectNodes = _this.topoSVG.bizCfg.selected.nodes;
                        var shps = _this.drawer.shapeControls;
                        for(var o in selectNodes){
                            if(o == _this.id)
                                continue;
                            var sctx = selectNodes[o];
                            var tctx = sctx.ctx;
                            var d = tctx.getValue();
                            //d.attr.w = _pose.width;
                            //d.attr.h = _pose.height;
                            d.attr.w = d.attr.w*rw;
                            d.attr.h = d.attr.h*rh;

                            sctx.reSize(d,tctx);
                            sctx.refresh();
                        }
                        
                        _this.showShapeContour(e);
                    });
                    $panel.bind("mouseup.shape_controls",function(e){
                        obj = undefined;
                        $panel.unbind(".shape_controls");
                        _this._removeMask();
                        mouse={};
                        _this.refresh();
                        stopEvent(e);
                        stopBubble(e);
                    });
                });
                
                
                $(hander + ' #controls_bounding').unbind().bind("mouseover",function(e){
                    $panel.css("cursor","move");
                });

                $(hander + ' #controls_bounding').bind("mousedown",function(e1){
                	
                	stopEvent(e1);
                    stopBubble(e1);

                    if(e1.which == 3){
                        
                        document.oncontextmenu = function()
                        {   
                            _this.topoSVG.businessView.showRightMenu({mouse:e1,ctx:ctx,type:'node'});
                            document.oncontextmenu=undefined;
                            return false;
                        }
                        
                        return;
                    }

                    _this.dragable(e1);
                });
                $(hander + ' #controls_bounding').bind("mouseup",function(e){
                	
                	//_this.bindCancelSelect();
                });
                $(hander + ' #controls_bounding').bind("mouseout",function(e){
                        $panel.css("cursor","");
                });

                $(hander + ' #controls_bounding').bind("dblclick",function(e){
                	$panel.css("cursor","");
                    //文本结点编辑
                    //console.log(_this.ctx.data);
                    if(_this.ctx.data.attr.type == 'textNode'){
                        if(_this.controller){
                            _this.controller.showTextEditArea();
                        }
                        //console.log(_this);
                        return;
                    }

                    //查看节点的详情信息（或者进入子业务系统）
                    _this.topoSVG.showDetail(_this.args.ctx);

                });
                
    	},
        dragable:function(e1){//设置元素使其可以拖动,e是鼠标事件
            
            var _this = this;
            var obj = this;
            var mouse = {};
            mouse.lastX = e1.clientX;
            mouse.lastY = e1.clientY;
            
            var pal = _this.topoSVG.businessView.getPanel();
            pal = pal.attr;

            var $panel = this.topoSVG.args.root;
            
            $panel.bind(".node-moving").bind("mousemove.node-moving",function(e){
                _this.topoSVG.panel={};
                _this.topoSVG.panel.eventLocked = true;

                if(obj != undefined && e.which){

                    if(e.which == 1){

                        mouse.x = e.clientX - mouse.lastX ;
                        mouse.y = e.clientY - mouse.lastY;
                        mouse.which = e.which;
                        
                        if(!mouse.mousedown){
                            mouse.mousedown = true;
                            return ;
                        }
                        mouse.lastX = e.clientX;
                        mouse.lastY = e.clientY;

                        var selectNodes = _this.topoSVG.bizCfg.selected.nodes;
                        var shps = _this.drawer.shapeControls;
                        for(var o in selectNodes){

                            var ctx = selectNodes[o].ctx;
                            var vu = ctx.getValue();
                            vu.attr.x += mouse.x;
                            vu.attr.y += mouse.y;
                            if(vu.attr.x < 0){
                                vu.attr.x = 0;
                            }
                            if(vu.attr.x+vu.attr.w > pal.w){
                                vu.attr.x = pal.w - vu.attr.w;
                            }
                            
                            if(vu.attr.y < 0){
                                vu.attr.y = 0;
                            }
                            if(vu.attr.y+vu.attr.h > pal.h){
                                vu.attr.y = pal.h - vu.attr.h;
                            }


                            var _shp = shps[o];
                             _shp.reSize(vu,ctx);

                            _shp.showShapeControls(e);
                            _shp.showShapeContour(e);
                            
                            var links = _shp.args.ctx.links;
                            if(links){
                                for (var i = 0; i < links.length; i++) {
                                    var v = links[i].getValue();
                                    v.attr.trend={};
                                    links[i].setValue(v);
                                }
                            }

                        }
                    }
                }
                stopEvent(e);
                stopBubble(e);
            });

            $panel.bind("mouseup.node-moving",function(e){
                if(_this.topoSVG.panel)
                    _this.topoSVG.panel.eventLocked = false;
                
                obj = undefined;
                $panel.unbind(".node-moving");
                stopBubble(e);
                stopEvent(e);
            });
        },
    	plotShapeControls:function(_pos){
    		// if(!topoSVG.bizCfg.selected.nodes)
    		// 	topoSVG.bizCfg.selected.nodes={};
    		// topoSVG.bizCfg.selected.nodes[this.id] = this.id;
    		
    		this.topoSVG.bizCfg.addSelected('nodes',this.id,this);

            var newId = this.id;
            var obj = '#'+newId;

            if($(obj).length == 0){
                var oldOBj = "#shape_controls";
                var p = $(oldOBj).clone().attr("id",newId);
                $(oldOBj).parent().append(p);
            }
             //console.log(_pos);
            var sxy = this.getscrollXY();
            $(obj).css({left:(sxy.scrollX + _pos.left)+'px',top:(sxy.scrollY +_pos.top)+'px'});

            var w = _pos.width,h=_pos.height;
            $(obj + " .shape_controller.n.w").css({left:'-4px',top:'-4px'});
            $(obj + " .shape_controller.n.e").css({left:(w-4)+'px',top:'-4px'});
            $(obj + " .shape_controller.s.e").css({left:(w-4)+'px',top:(h-4)+'px'});
            $(obj + " .shape_controller.s.w").css({left:'-4px',top:(h-4)+'px'});
            var pth = ['M',0,0,'L',w,0,'L',w,h,'L',0,h,'L',0,0];
            $(obj + " #controls_bounding").css({width:w+'px',height:h+'px'});
            $(obj + " #controls_bounding path").attr("d",pth.join(' '));

            $(obj + " .shape_rotater").css({left:(w/2-20)+'px',display:'none'});
            if($(obj).is(":hidden")){
                $(obj).show();
            }
        },
        plotShapeContour:function(e,_pos){
        	if(!this.defalut.showContour)
    			return;
            //console.log(e);
            var _this = this;
            var eventType = e?e.type:undefined;
            var w = _pos.width,h=_pos.height;
            var cPos = [
                            [-4,h/2-4,'w'],//w
                            [w-4,h/2-4,'e'],//e
                            [w/2-4,-4,'n'],//n
                            [w/2-4,h-4,'s']//s
                        ];//控制点的位置

            var obj = ".shape_contour";

            var $panel = this.topoSVG.args.root;
           
            if(eventType == 'mouseover'){
                if($(".shape_contour_hovered").length ==0){
                    $panel.append($(".shape_contour").clone().addClass("shape_contour_hovered"));
                }
                obj += ".shape_contour_hovered";
            }

            var sxy = this.getscrollXY();
            $(obj).css({left:(sxy.scrollX + _pos.left)+'px',top:(sxy.scrollY +_pos.top)+'px'});
            //$(obj).css({left:_pos.left+'px',top:_pos.top+'px'});

            $(obj+" .shape_anchor:eq(0)").css({left:cPos[0][0]+'px',top:cPos[0][1]+'px'});
            $(obj+" .shape_anchor:eq(1)").css({left:cPos[1][0]+'px',top:cPos[1][1]+'px'});
            $(obj+" .shape_anchor:eq(2)").css({left:cPos[2][0]+'px',top:cPos[2][1]+'px'});
            $(obj+" .shape_anchor:eq(3)").css({left:cPos[3][0]+'px',top:cPos[3][1]+'px'});

            $(obj).show();
            if(obj != ".shape_contour.shape_contour_hovered")
            	$(".shape_contour.shape_contour_hovered").hide();

            buzrequire(["bz/LinkControl"],function(LinkControl){
                var lc = new LinkControl({topoSVG:_this.topoSVG,ctx:_this.args.ctx,drawer:_this.args.drawer});
                lc.setShapeAnchor(obj);
            });
        },
        refresh:function(){//重新绘制
            this.outer_pos = this.getBoundingClientRect(this.ctx.node.node);
            this.plotShapeControls(this.outer_pos);

            this.conncect_pos = this.getBoundingClientRect(this.ctx.node.gdawer.node);
            if(this.ctx.node.txt){
                var tbox= this.getBoundingClientRect(this.ctx.node.txt.node);
                this.conncect_pos.height += tbox.height;
            }
            this.plotShapeContour(null,this.conncect_pos);
        },
        reSize:function(info,ctx){//从新绘制形状
            ctx.setValue(info);
        },
        destory:function(){
        	$("#" + this.id).remove();
            $("#shape_contour").hide();
        	this.topoSVG.bizCfg.removeSelected('nodes',this.id);
        },
        getBoundingClientRect:function(obj){
            var _pos = obj.getBoundingClientRect();
            var bs = this.topoSVG.bizCfg.getBaseOffset();
            var pos = {};
            pos.top = _pos.top - bs.top;
            pos.left = _pos.left - bs.left;
            pos.height = _pos.height;
            pos.width = _pos.width;
            return pos;
        },
        /*获取页面滚动条的当前位置*/
        getscrollXY:function(){
            var supportPageOffset = window.pageXOffset !== undefined;
            var isCSS1Compat = ((document.compatMode || "") === "CSS1Compat");

            var x = supportPageOffset ? window.pageXOffset : isCSS1Compat ? document.documentElement.scrollLeft : document.body.scrollLeft;
            var y = supportPageOffset ? window.pageYOffset : isCSS1Compat ? document.documentElement.scrollTop : document.body.scrollTop;
            return {scrollX:x,scrollY:y};
        }
    });
});