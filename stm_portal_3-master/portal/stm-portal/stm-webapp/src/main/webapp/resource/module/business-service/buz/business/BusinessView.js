/**
 * 拓扑展示类
 */
buzdefine("bz/BusinessView", ["bz/ElementsFactory","bz/rightMenu"], function (ElementsFactory,rightMenu) {
    var tmp = function (args) {
    	this.args = $.extend({
            topoSVG:null,
    		drawer:null,
            db:null
    	},args);

    	if(args.drawer && args.db && args.topoSVG){
            this.topoSVG = args.topoSVG;
            this.bizCfg = args.bizCfg;
            this.fdrawer = args.drawer.group();
            this.holder = args.holder;
            this.db = args.db;

    		this.init();

    	}else{
    		throw "drawer can't be null";
    	}
    };
    tmp.prototype = {
    	init:function(){
    		this.eles={
    			nodes:{},
    			links:{},
                panel:{attr:{}}
    		};
    		this.factory = new ElementsFactory({
                topoSVG:this.topoSVG,
                bizCfg:this.bizCfg,
    			drawer:this.fdrawer
    		});
            this._regEvent();
    	},
        _regEvent:function(){
            var _this = this;
            var rt =  this.holder;
            $(rt).bind('mousedown.panel-mouse-event',function(e){
                if(e.which == 1){
                    //alert("点击！");
                    _this.cancelSelected();
                }else if(e.which ==3){
                    document.oncontextmenu = function()
                    {   
                        try{
                            _this.showRightMenu({mouse:e,ctx:null,type:'white'});
                        }catch(e){ }
                        document.oncontextmenu=undefined;
                        return false;
                    }
                    stopBubble(e);
                    return;
                }
                //console.log("tehis");
            });
            _this._regPanelSlectEvent();
        },
        _regPanelSlectEvent:function() {
            var ctx = this;
            if(!ctx.panelSlectDrawer)
                ctx.panelSlectDrawer = this.args.drawer.group();
            var np= ctx.panelSlectDrawer;
            var tpf=undefined;
            var move = undefined;
            var panel = $(this.holder).parent();
            panel.unbind(".panle-select");
            panel.bind("mousedown.panle-select",function(e){
                if(e.which != 1)
                    return;

                //编辑权限控制
                var isAllowEdit = ctx.topoSVG.bizCfg.get("editAble");
                if(!isAllowEdit)
                    return;


                //用于避免和其它鼠标绑定到panel的鼠标事件起冲突
                if(panel.data('events') && panel.data('events').mouseup){
                    var countMouseup = panel.data('events').mouseup.length;
                    if(countMouseup>1)
                        return;
                }

                var base = $(np.node.viewportElement).offset();
                move = {};
                move.base = base;
                move.begin={
                    x:e.clientX-base.left,
                    y:e.clientY-base.top
                };

                if(tpf){//用于处理鼠标超出控制区域释放后导致的状态未还原
                    tpf.remove();
                    tpf =undefined;
                    $('#bz #bz-mask').remove();
                }
                // console.log(move);
                $(document).unbind(".panle-selecting");
                $(document).bind("mousemove.panle-selecting",function(e){

                    if($('#bz #bz-mask').length==0)
                        $('<div/>').attr("id","bz-mask").css({top:'0px',left:'0px','backgroud-img':'url(img/a.png)',position:'absolute',width:'100%',height:'100%'}).appendTo('#bz');
                
                    move.end={
                        x:e.clientX-base.left,
                        y:e.clientY-base.top
                    };
                    if(!tpf){
                        tpf=np.rect(Math.abs(move.end.x-move.begin.x),Math.abs(move.end.y-move.begin.y));
                        tpf.attr({fill: 'none',stroke: 'blue'});
                    }
                    var w = move.end.x-move.begin.x;
                    var h = move.end.y-move.begin.y;
                    tpf.size(Math.abs(w),Math.abs(h));
                    var mx= move.begin.x,my=move.begin.y;
                    if(w<0)
                        mx = move.end.x;
                    if(h<0)
                        my = move.end.y;
                    tpf.move(mx,my);
                    move.rect = {x:mx,y:my,w:Math.abs(w),h:Math.abs(h)};

                    //console.log(move);
                    stopEvent(e);
                    stopBubble(e);
                    /*$(document).bind("mouseup.panle-selecting",function(){
                        $(document).unbind(".panle-selecting");
                        $('#bz #bz-mask').remove();
                        
                        stopEvent(e);
                        stopBubble(e);
                    });//*/

                    //这部分主要用于屏蔽处理IE自身的选中功能
                    try{
                          if(document.selection){//IE ,Opera
                                if(document.selection.empty)
                                    document.selection.empty();//IE
                                else{//Opera
                                    document.selection = null;
                              }
                          }else if(window.getSelection){//FF,Safari
                               window.getSelection().removeAllRanges();
                          }
                    }catch(e){}

                });
                //stopBubble(e);
            });
            panel.bind("mouseup.panle-select",function(e){
                if(e.which != 1)
                    return;
                if(panel.data('events') && panel.data('events').mouseup){
                    var countMouseup=panel.data('events').mouseup.length;
                    if(countMouseup>1)
                        return;
                }

                $(document).unbind(".panle-selecting");
                $('#bz #bz-mask').remove();
                if(tpf){

                    //文本框绘制事件
                    if(ctx.topoSVG.panelControl.isDrawTextBox()){
                            var data ={};
                            data.attr = $.extend(true,{},move.rect);
                            data.attr.w = move.rect.w>120?move.rect.w:120;
                            data.attr.h = move.rect.h>20?move.rect.h:20;

                            data.attr.bgStyle={'fill-opacity': 0};

                            data.attr.text = "文本框，点击输入文字";
                            //data.id = "text" + (+new Date);
                            data.attr.type ='textNode';
                            data.type ='textNode';

                            ctx.topoSVG.addNode(data);
                            ctx.topoSVG.panelControl.setDrawTextBox(false);

                       
                    }else{//鼠标框选事件
                        //console.log(move);
                        var es = ctx.eles;
                        var nodes = es.nodes;
                        var links = es.links;
                        var rct  = move.rect;
                        for(var o in nodes){
                            var nd = nodes[o];
                            var rbox = nd.node.rbox();

                            if(rbox.x >= rct.x && rbox.y >= rct.y &&
                               rbox.x2 <= rct.x+rct.w && rbox.y2 <= rct.y+rct.h ){
                                if(nd.handler && nd.handler.ShapeControl && nd.handler.ShapeControl.showShapeControls)
                                    nd.handler.ShapeControl.showShapeControls({ctrlKey:true});
                            }
                        }
                    }

                    move = undefined;
                    tpf.remove();
                    tpf = undefined;
                    
                }
            });

            //键盘事件处理
            $(document).bind('keydown.bz-panel-edit',function(e){
                //通过上下左右键微调结点位置
                var nodes = ctx.topoSVG.bizCfg.selected.nodes;
                if(nodes){
                    var dx=0,dy=0;
                    if(e.keyCode==37){//left
                        dx = -1;
                    }else if(e.keyCode==38){//top
                        dy = -1;
                    }else if(e.keyCode==39){//right
                         dx = 1;
                    }else if(e.keyCode==40){//bottom
                        dy = 1;
                    }else{
                        return;
                    }

                    for(var o in nodes){
                        var nctx = nodes[o].ctx;
                        var dt = nctx.getValue();
                        dt.attr.x = dt.attr.x  + dx;
                        dt.attr.y = dt.attr.y  + dy;
                        nctx.setValue(dt);
                        nodes[o].refresh();
                    }
                 return;
                }

                //其它
            });

        },
        cancelSelected:function(){//取消选中的当前对象
            var nds= this.topoSVG.bizCfg.selected.nodes;
            for(var o in nds){
                nds[o].destory();
            }

            var lks= this.topoSVG.bizCfg.selected.links;
            if(lks){
                SVG.select('path.path-selected').removeClass('path-selected');
                this.topoSVG.bizCfg.removeSelected('links');
            }
            if(this.topoSVG.bizCfg.panelTools)
                this.topoSVG.bizCfg.panelTools.updateStatus();
        },
    	getNodeById:function(id){
    		return this.eles.nodes[id];
    	},
        clear:function(){
            this.factory.clearALL();
            this.init();
        },
    	setValue:function(eles){
    		//绘制节点
    		if(!eles) return ;
    		if(eles.nodes){
    			for(var i=0;i<eles.nodes.length;i++){
    				var nodeInfo = eles.nodes[i];
                    if(!nodeInfo.attr)
                        continue;
    				var node = this.factory.getNode(nodeInfo);
    				//保存id和节点的关系，便于引用查找
    				this.eles.nodes[nodeInfo.id]=node;
                    node.store = this.eles.nodes;
    			}
    		}
    		//绘制连线
    		if(eles.links){
    			for(var i=0;i<eles.links.length;i++){
    				var linkInfo = eles.links[i];
                   if(!linkInfo.attr)
                        continue;
                    var link = this.factory.getLink(linkInfo,this.eles.nodes);

    				//保存id和节点的关系，便于引用查找
    				this.eles.links[linkInfo.id]=link;
    			}
                this.eles.links_len = eles.links.length;
    		}
            if(eles.panel){
                var p = $.extend(true,{attr:null},eles.panel);
                this.setPanel(p);
            }
            for(var o in this.eles.nodes){
                var nd = this.eles.nodes[o];
                this._changEleZindex(nd);
            }

            for(var o in this.eles.links){
                var nd = this.eles.links[o];
                this._changEleZindex(nd);
            }

    	},
        _changEleZindex:function(nd){
            var zdx = nd.data.attr.zIndex;
            if(isNaN(zdx))
                return;
            zdx = parseInt(zdx);
            zdx = zdx>=0?zdx:0;

            var cp = nd.node.position();
             while(cp > zdx){
                nd.node.backward();
                cp = nd.node.position();
                var t = nd.node.previous();
                if(!t)
                    break;
            }
            //console.log(cp + "->" + zdx);
            while(cp < zdx){
                nd.node.forward();
                cp = nd.node.position();
                var t = nd.node.next();
                if(!t)
                    break;
            }
        },
        addLink:function(linkInfo){
            var _this = this;
            if(linkInfo.id == undefined)
                linkInfo.id = "link-" + (+new Date);//需要去动态获取link的唯一标识Id
            var link = _this.factory.getLink(linkInfo,this.eles.nodes);
            _this.eles.links[link.data.id] = link;
            //console.log("add-id :" + link.data.id);
            return link;
        },
        updateLink:function(obj){
            if(!obj)
                return;
            _this = this;
            _this.db.addLink(obj.getValue(),function(d){
                //console.log(d);
                if(d.code != 200){
                    alert("添加连线失败！");
                    return;
                }

                var id = d.data;
                var _tobj = $.extend(true,{},obj.getValue());
                var oLink = _this.eles.links[_tobj.id];
                _this._removeLink(oLink);
                
                
                _tobj.id = id;
                //console.log(_tobj);
                _this.addLink(_tobj);
            });
        },
        removeLink:function(link){
            _this = this;
            var id = link.data.id;

            _this.confirm("确认删除？",function(){
                _this.db.deleteLink(id,function(d){
                     if(!d.data){
                        alert("删除连线失败");
                        return;
                    }
                    _this._removeLink(link);
                    _this.cancelSelected();

                });
            });
        },
        removeLinks:function(links){
            _this = this;
            var ids = "";
            for(var o in links){
                ids += links[o].data.id + ',';
            }
            if(ids == "")
                return;
            else
                ids = ids.substring(0,ids.length-1);

            //_this.confirm("确认删除？",function(){
                _this.db.deleteLink(ids,function(d){
                     if(!d.data){
                        alert("删除连线失败");
                        return;
                    }
                    for(var o in links){
                        var link = links[o];
                        _this._removeLink(link);
                    }
                    _this.cancelSelected();
                });
            //});
        },
        _removeLink:function(link){
            link.deleted = true;
            var id = link.data.id;

            var node = link.from.node;
            if(node)
                this.removeLinkFromNode(node,id);

            node = link.to.node;
            if(node)
                this.removeLinkFromNode(node,id);
            link.node.remove();
            delete this.eles.links[id];
        },
        removeLinkFromNode:function(node,linkId){
            var nlinks = [];
            for(var i=0;i<node.links.length;i++){
                var tl = node.links[i];
                if(tl.data.id != linkId){
                    nlinks.push(tl);
                }
            }
            node.links = nlinks;
        },
        addNode:function(node){///添加包含结点布局信息的节点
        	if(!node.attr.fontStyle){
        		node.attr.fontStyle = this.topoSVG.bizCfg.get('node').attr.fontStyle;
        	}
        	node.attr.fontStyle['fill'] = '#fff';
            var _this = this;
            
            /*
            this.db.addNode(node,function(d){
                var nd = _this.factory.getNode(d);
               
                //保存id和节点的关系，便于引用查找
                _this.eles.nodes[nd.data.id]=nd;
                nd.store = _this.eles.nodes;

            });//*/

            /*
            node.id = "add-" + (+new Date);
            node.attr.x = 100;
            node.attr.y = 200;  //*/
            var nd = _this.factory.getNode(node);
               
            //保存id和节点的关系，便于引用查找
            _this.eles.nodes[nd.data.id]=nd;
            nd.store = _this.eles.nodes;
          

        },
        updateNode:function(obj){

        },
        removeNode:function(node){
            var _this = this;
            _this.confirm("确认删除？",function(){
                _this.db.deleteNode(node.data.id,function(d){
                    if(!d.data){
                        alert("删除节点失败");
                        return;
                    }
                    _this.topoSVG.bizCfg.removeSelected('nodes',node.data.id);
                    node.node.remove();
                    delete _this.eles.nodes[node.data.id];
                    _this.cancelSelected();
                    alert("删除节点成功");
                });
            });
        },
        removeNodes:function(nodes){
            var _this = this;
            var ids = "";
            for(var o in nodes){
                ids += nodes[o].data.id + ',';
            }
            if(ids == "")
                return;
            else
                ids = ids.substring(0,ids.length-1);
            
            _this.db.deleteNode(ids,function(d){
                if(!d.data){
                    alert("删除节点失败");
                    return;
                }
                for(var o in nodes){
                    var node = nodes[o];
                    _this.topoSVG.bizCfg.removeSelected('nodes',node.data.id);
                    node.node.remove();
                    delete _this.eles.nodes[node.data.id];
                }
                _this.cancelSelected();
                alert("删除节点成功");
                
            });
           

        },
        showRightMenu:function(e){
            var _this = this;
            var p = _this.topoSVG.businessView.getPanel();
            _this.topoSVG.db.getRightMenuStatus(p.bizId,function(data){
                e.showBusinessSpeed = (data!=undefined && data instanceof  Array && data.length >0);
                _this.rightMenu = new rightMenu({
                    topoSVG:_this.topoSVG,
                    e:e}); 
            });
        },
    	getValue:function(){
    		var result={
    			nodes:[],
    			links:[]
    		};
    		for(var k in this.eles.nodes){
    			var node = this.eles.nodes[k].getValue();
    			result.nodes.push(node);
    		}
    		for(var k in this.eles.links){
    			var link = this.eles.links[k].getValue();
                result.links.push(link);
    		}
            result.panel=this.eles.panel;
    		return result;
    	},
        getPanel:function() {
          return this.eles['panel'];
        },
        setPanel:function(panel) {
            this.eles['panel'] = panel;
            if(panel.attr.style){
                $(this.holder).css(panel.attr.style);
            }
        },
        changeBackgroud:function(filedId){
            var panel = this.getPanel();

            if(!panel.attr.style)
                panel.attr.style = {};
            panel.attr.style['background-image']='url(../platform/file/getFileInputStream.htm?fileId=' + filedId +')';
            this.setPanel(panel);
        },
        confirm:function(msg,callback){
            oc.ui.confirm(msg,function() {
                if(typeof(callback)=='function')
                    callback();
            });
        }
    };
    return tmp;
});