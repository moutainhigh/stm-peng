buzdefine("bz/LinkControl", ["bz/Base","bz/extend","bz/NormalLink"], function (Base,extend,NormalLink) {
    return extend(function(args){
    	args=$.extend({
    		 ctx:null,
             drawer:null
    	},args);
    	if(args.ctx && args.drawer && args.topoSVG){
    		this.attr({
    			ctx:args.ctx,
    			drawer:args.drawer
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
    	get_ctx:function(ctx){
    		return this.ctx;
    	},
        set_drawer:function(drawer){
            this.drawer=drawer;
        },
        get_drawer:function(drawer){
            return this.ctx;
        },
        init:function(){

        },
        setShapeAnchor:function(obj){
            this.shapeAnchorObj = obj;
            this.regEvent();
        },
        regEvent:function(){
            var obj = this.shapeAnchorObj;
            var _this = this;
            var _anchor = undefined;

            var $bz = this.topoSVG.args.holder;
            var $panel =  this.topoSVG.args.root;

            $(obj + " .shape_anchor").unbind();
            $(obj + " .shape_anchor").bind("mousedown",function(e){
                $bz.css({cursor: 'crosshair'});
                if(_anchor)
                    return;

                var _anchor = this;
                var mouse = {};

                mouse.lastX = e.clientX;
                mouse.lastY = e.clientY;
                
                var node = _this.args.ctx.node;
                var img = _this.args.ctx.node.img;
                var dr = _this.args.drawer;
                //console.log(dr);
                if(dr.anchor==undefined)
                    dr.anchor = {};

                imgTb = img.tbox();
                if($(this).index()==0){
                    mouse.startX = imgTb.x;
                    mouse.startY = imgTb.cy;
                    dr.anchor.pose_mousedown = 'w';
                }else if($(this).index()==1){
                    mouse.startX = imgTb.x2;
                    mouse.startY = imgTb.cy;
                    dr.anchor.pose_mousedown = 'e';
                }else if($(this).index()==2){
                    mouse.startX = imgTb.cx;
                    mouse.startY = imgTb.y;
                    dr.anchor.pose_mousedown = 'n';
                }else if($(this).index()==3){
                    mouse.startX = imgTb.cx;
                    mouse.startY = imgTb.y2;
                    dr.anchor.pose_mousedown = 's';

                }
                
                dr.anchor.mousedown_node = _this.args.ctx;
                delete dr.anchor.mouseover_node;
                delete dr.anchor.pose_mouseover;
                
                stopBubble(e);

                $panel.bind("mousemove.draw-line",function(e){
                        if(_anchor != undefined && e.which){
                            //console.log("--+--");
                            var bs = $(node.node.viewportElement).offset()
                            mouse.x = e.clientX ;
                            mouse.y = e.clientY;
                            mouse.which = e.which;
                            var dx = mouse.x - bs.left;
                            var dy = mouse.y - bs.top;

                            var from = {x:mouse.startX,y:mouse.startY};
                            var to = {x:dx,y:dy};
                            var dranchar = dr.anchor;
                           
                            if(dranchar.mousedown_node){
                                var pnode = dranchar.mousedown_node;
                                from.pose = dranchar.pose_mousedown;
                                from.id = pnode.data.id;
                                fid = from.id;
                            }
                            
                            if(dranchar.mouseover_node){
                                var pnode = dranchar.mouseover_node;
                                to.pose = dranchar.pose_mouseover;
                                to.id = pnode.data.id;
                                
                                to.pose = _this._getNodePos([dx,dy],pnode);
                                
                                //用于处理from和to都在同一个节点的情况,此时不画线
                                if(to.id == from.id){
                                    delete dr.anchor.mouseover_node;
                                    delete dr.anchor.pose_mouseover;
                                    return;
                                }
                            }
                            
                            if(!mouse.line){
                                var link = {
                                    id:undefined,
                                    fromNode:from.id,
                                    toNode:to.id,
                                    attr:{
                                        from:from,
                                        to:to,
                                        attr:{},
                                        type:'line'
                                    }
                                }
                                link = $.extend(true,link,_this.topoSVG.bizCfg.get("link"));
                                delete link.attr.trend;
                                mouse.line = _this.drawLine(link);
                            }else{
                                var link = mouse.line.getValue();
                                link.attr.from = from;
                                link.attr.to = to;
                                link.fromNode = from.id,
                                link.toNode = to.id,
                               _this.refreshLine(mouse.line,link);
                            }

                            if(e.which == 1){
                                mouse.mousedown = true;
                            }
                        }
                        stopEvent(e);
                });
                $panel.bind("mouseup.draw-line",function(e){
                    mouse.mousedown = false;
                    $panel.unbind(".draw-line");
                    _anchor = undefined;
                    $bz.css({cursor: ''});
                    _this.topoSVG.businessView.updateLink(mouse.line);
                });
            });
            
            $(obj + " .shape_anchor").bind("mouseover",function(e){
                var dr = _this.args.drawer;
                if(!dr.anchor){
                    dr.anchor = {};
                }
                dr.anchor.mouseover_node = _this.ctx;
                dr.anchor.pose_mouseover = $(this).attr("direct");
                //$bz.css({cursor: 'crosshair'});
                $(this).addClass("shape-anchor-selected");
            });
            
            $(obj + " .shape_anchor").bind("mouseout",function(e){
                var dr = _this.args.drawer;
               
                $(this).removeClass("shape-anchor-selected");
                
                if(dr.anchor){
                    delete dr.anchor.mouseover_node;
                    delete dr.anchor.pose_mouseover;
                }
            });
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

            /*获取两点间的距离*/
            function getDistance(p1,p2){
                var dx = p2[0] - p1[0];
                var dy = p2[1] - p1[1];
                return Math.sqrt(dx*dx + dy*dy)
            }

            return pos;
        },
        drawLine:function(link){
            var lk = this.topoSVG.businessView.addLink(link);
            return lk;
        },
        refreshLine:function(line,link){
            line.setValue(link);
        }
    });

    function stopEvent (evt) {
        var evt = evt || window.event; 
        if (evt.preventDefault) { 
            evt.preventDefault(); 
        } else { 
            evt.returnValue = false; 
        } 
    } 
});