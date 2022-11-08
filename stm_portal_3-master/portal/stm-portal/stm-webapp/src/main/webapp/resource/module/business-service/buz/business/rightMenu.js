buzdefine("bz/rightMenu", ["bz/Base","bz/extend"], function (Base,extend) {
    var f = extend(function(args){
    	this.args=$.extend({
    		e:null,
            topoSVG:null,
            showBusinessSpeed:true
    	},args);
        if(args.e && args.topoSVG){
    		this.attr({
    			e:args.e
    		});
            this.topoSVG = args.topoSVG;
    		//Base.apply(this,arguments);
            this.init();
    	}else{
    		throw "event can't be null";
    	}
    },Base,{
        init:function(){
            var e = this.e.mouse;
            var type = this.e.type;//判断是从什么地方传过来的事件
            var isAllowEdit = this.topoSVG.bizCfg.get("editAble");

            var mainArea = this.topoSVG.args.selector;
            //console.log(mainArea); 
            var $rightMenu = mainArea.find('.right-menu');
            var $subUl = $rightMenu.find(".sub-ul");

            //console.log($rightMenu);

            //显示1级菜单
            $rightMenu.find("ul:eq(0)").show();

            //隐藏2级菜单
            $subUl.hide();

            if(!this.e.showBusinessSpeed)
                $subUl.find("li span.bus-spend").parent().removeClass('show').addClass("hide");

            $rightMenu.find("ul li span").parent().hide();

            if(this.e.ctx){
                $rightMenu.find("ul li span.copy").parent().show();
                $rightMenu.find("ul li span.past").parent().hide();
                $rightMenu.find("ul li span.delete").parent().show();

                $rightMenu.find("ul li span.rmsave").parent().hide();
                $rightMenu.find("ul li span.rmback").parent().hide();
            }else{
                $rightMenu.find("ul li span.copy").parent().hide();
                $rightMenu.find("ul li span.delete").parent().hide();
               
                //当有数据的时候才允许粘贴
                if(this.topoSVG.coyData)
                    $rightMenu.find("ul li span.past").parent().show();
                else
                    $rightMenu.find("ul li span.past").parent().hide();

                $rightMenu.find("ul li span.rmsave").parent().show();
                $rightMenu.find("ul li span.rmback").parent().show();
            }
            
            if(this.e.ctx){
            	
            	var nodeData = this.e.ctx.data;
            	//判断是否有编辑权限
        		//var isAllowEdit = oc.business.service.edit.permissions.checkEdit(nodeData.bizId);
            	//console.debug(nodeData);
            	//控制节点右键菜单内容
            	if(nodeData.nodeType == 1){
            		//资源节点
            		if(isAllowEdit){
            			$rightMenu.find("ul li span.edit").parent().show();
            			$rightMenu.find("ul li span.delete").parent().show();
            			$rightMenu.find("ul li span.copy").parent().show();
            		}else{
            			$rightMenu.find("ul li span.edit").parent().hide();
            			$rightMenu.find("ul li span.delete").parent().hide();
            			$rightMenu.find("ul li span.copy").parent().hide();
            		}
            		$rightMenu.find("ul li span.past").parent().hide();
            		//$rightMenu.find("ul#bus-set-sub-1").hide();
            		$rightMenu.find("ul li span.statedefine").parent().hide();
            		$rightMenu.find("ul li span.bus-set").parent().hide();
            		$rightMenu.find("ul li span.checkwarm").parent().show();
            		$rightMenu.find("ul li span.checkdetial").parent().show();
            	}else if(nodeData.nodeType == 2){
            		//业务节点
            		if(nodeData.instanceId == nodeData.bizId){
            			//主业务
            			if(isAllowEdit){
            				$rightMenu.find("ul li span.edit").parent().show();
            				$rightMenu.find("ul li span.statedefine").parent().show();
            				//$rightMenu.find("ul#bus-set-sub-1").show();
            				$rightMenu.find("ul li span.bus-set").parent().show();
            			}else{
            				$rightMenu.find("ul li span.edit").parent().hide();
            				$rightMenu.find("ul li span.statedefine").parent().hide();
            				//$rightMenu.find("ul#bus-set-sub-1").hide();
            				$rightMenu.find("ul li span.bus-set").parent().hide();
            			}
            			$rightMenu.find("ul li span.delete").parent().hide();
            			$rightMenu.find("ul li span.copy").parent().hide();
            			$rightMenu.find("ul li span.past").parent().hide();
            			$rightMenu.find("ul li span.checkwarm").parent().show();
            			$rightMenu.find("ul li span.checkdetial").parent().show();
            		}else{
            			//子业务
            			if(isAllowEdit){
            				$rightMenu.find("ul li span.delete").parent().show();
            			}else{
            				$rightMenu.find("ul li span.delete").parent().hide();
            			}
            			$rightMenu.find("ul li span.edit").parent().hide();
            			$rightMenu.find("ul li span.copy").parent().hide();
            			$rightMenu.find("ul li span.past").parent().hide();
            			$rightMenu.find("ul li span.checkwarm").parent().show();
            			$rightMenu.find("ul li span.checkdetial").parent().show();
            			//$rightMenu.find("ul#bus-set-sub-1").hide();
            			$rightMenu.find("ul li span.statedefine").parent().hide();
            			$rightMenu.find("ul li span.bus-set").parent().hide();
            			
            		}
            	}else{
        			if(isAllowEdit){
        				$rightMenu.find("ul li span.delete").parent().show();
        				$rightMenu.find("ul li span.copy").parent().show();
        				$rightMenu.find("ul li span.edit").parent().hide();
        				$rightMenu.find("ul li span.past").parent().hide();
        				$rightMenu.find("ul li span.checkwarm").parent().hide();
        				$rightMenu.find("ul li span.checkdetial").parent().hide();
        				//$rightMenu.find("ul#bus-set-sub-1").hide();
        				$rightMenu.find("ul li span.statedefine").parent().hide();
        				$rightMenu.find("ul li span.bus-set").parent().hide();
        			}else{
        				$rightMenu.hide();
                        return;
        			}
            	}

                if(isAllowEdit){
                        $rightMenu.find("ul li span.right-menu-zindex-first").parent().show();
                        $rightMenu.find("ul li span.right-menu-zindex-up").parent().show();
                        $rightMenu.find("ul li span.right-menu-zindex-last").parent().show();
                        $rightMenu.find("ul li span.right-menu-zindex-down").parent().show();
                    }else{
                        $rightMenu.find("ul li span.right-menu-zindex-first").parent().hide();
                        $rightMenu.find("ul li span.right-menu-zindex-up").parent().hide();
                        $rightMenu.find("ul li span.right-menu-zindex-last").parent().hide();
                        $rightMenu.find("ul li span.right-menu-zindex-down").parent().hide();
                    }
            }else{//空白处右键权限处理
                
               if(isAllowEdit){
                    $rightMenu.find("ul li span.rmsave").parent().show();
                     $rightMenu.find("ul li span.rmback").parent().show();
                }else{
                    $rightMenu.find("ul li span.rmsave").parent().hide();
                    $rightMenu.find("ul li span.rmback").parent().hide();
                    $rightMenu.find("ul:eq(0)").hide();
                }

            }
            

            //处理当前是多选的情况
            if(isAllowEdit && type == 'node'){
                var nds = this.topoSVG.bizCfg.selected.nodes;
                var i = 0;
                var isMulit = false;
                
                for(var o in nds){
                    i++;
                    if(i>1){
                        isMulit = true;
                        break;
                    }
                }

                if(isMulit){
                    $rightMenu.find("ul li span").parent().hide();
                    $rightMenu.find("ul li span.copy").parent().show();
                    $rightMenu.find("ul li span.delete").parent().show();
                }
            }

            

            if(isAllowEdit && type == 'link'){
                $rightMenu.find("ul li span").parent().hide();
                $rightMenu.find("ul li span.delete").parent().show();
            }

            //计算menu显示的位置
            var pbs = this.topoSVG.args.holder.offset();            
            var x=e.clientX-pbs.left+10,y=e.clientY-pbs.top+10;
            var basObj =  $rightMenu.parent().parent();
            var mw = basObj.width();
            var mh = basObj.height();
            var w =  $rightMenu.width();
            var h =  $rightMenu.height();
            if(x+w >mw)
              x = mw - w -10;
            if(y+h>mh)
              y = mh - h - 10;
            $rightMenu.css({top:y+'px',left:x+'px'}).show().css("display","inline-block");

            this.regEvent();
        },
        regEvent:function(){
            var _this = this;
            var mainArea = this.topoSVG.args.selector;
            var $rightMenu = mainArea.find('.right-menu');
            var $subUl = $rightMenu.find(".sub-ul");

            var unid =Math.round(Math.random()*99999999999);
            $(document).bind('mousedown.right-menu'+unid,function(e){
                if(e.which ==1){
                    $rightMenu.hide();
                    $(document).unbind('.right-menu'+unid);
                    stopEvent(e);
                    stopBubble(e);
                }
            });
            $rightMenu.find("ul li").unbind('.right-menu-event');
            $rightMenu.find("ul li").bind('mousedown.right-menu-event',function(e){
                if(e.which ==3){
                    stopEvent(e);
                    stopBubble(e); 
                    return;
                }

                e.targetName = $(this).find('span').attr('class');
                _this.procssEvent(e);
                $rightMenu.hide();
                stopEvent(e);
                stopBubble(e);
            });

            $rightMenu.find("ul:eq(0) li").bind('mouseover.right-menu-event',function(e){
                if(e.which ==3){
                    stopEvent(e);
                    stopBubble(e); 
                    return;
                }

                e.targetName = $(this).find('span').attr('class');
                if(e.targetName.indexOf('bus-set') > -1){
                    var basObj =  $(".right-menu").parent().parent();
                    var mw = basObj.width();
                    var foset  = $(".right-menu-ico.bus-set").offset();
                    var left = '108px';
                    if(mw - foset.left < 110){
                        left = '-108px';
                    }
                    $subUl.css({display:'block','position':'absolute',top:'100px',left:left,width:'130px'});


                    $subUl.show();
                    $subUl.find("li").show();
                    $subUl.find("li.hide").hide();

                }else{
                    $subUl.hide();
                }

                stopEvent(e);
                stopBubble(e);
            });
        },
        procssEvent:function(e){
            var _this = this;
            var ctx = this.e.ctx;
            var eType = this.e.type;
            if(e.targetName.indexOf('copy') >-1 ){//处理复制操作
                if(ctx != null){
                    _this.topoSVG.coyData ={};
                    _this.topoSVG.coyData.nodes ={};
                    var sets = _this.topoSVG.bizCfg.selected;
                    var i = 0;
                    for(var o in sets.nodes){
                        var ctx = sets.nodes[o].ctx;
                        
                        if(ctx.data.nodeType  == 2 && ctx.data.instanceId != ctx.data.bizId ){
                            continue;
                        }

                        var dt = $.extend(true,{}, ctx.getValue());
                        //console.log(dt);
                        dt.id = 'copy' + (+new Date) + (i++);
                        dt.attr.x = dt.attr.x  +5;
                        dt.attr.y = dt.attr.y  +5;
                        _this.topoSVG.coyData.nodes[dt.id] = dt;
                    }
                }
                return;
            }

            if(e.targetName.indexOf('past') >-1 ){//处理复制操作
                if(!_this.topoSVG.coyData)
                    return;
                var sets = _this.topoSVG.coyData;
                for(var o in sets.nodes){
                    var ctx = $.extend(true,{},sets.nodes[o]);
                    _this.topoSVG.addNode(ctx);
                    ctx.attr.x = ctx.attr.x  +5;
                    ctx.attr.y = ctx.attr.y  +5;
                }
                return;
            }

            if(e.targetName.indexOf('delete') >-1 ){//处理删除操作

                _this.topoSVG.confirm("确认删除？",function(){ 

                    var sets = _this.topoSVG.bizCfg.selected;
                    var links= sets.links,nodes = {};
                    for(var o in sets.nodes){
                        var ctx = sets.nodes[o].ctx;
                        
                        if(ctx.data.nodeType  == 2 && ctx.data.instanceId == ctx.data.bizId){
                            continue;
                        }

                        if(ctx.links != undefined && ctx.links.length > 0){
                           for(var i=0; i<ctx.links.length; i++ ){
                                links[ctx.links[i].data.id] = ctx.links[i];
                            }
                        }
                        nodes[ctx.data.id] = ctx;
                    }
                    _this.topoSVG.businessView.removeLinks(links);
                    _this.topoSVG.businessView.removeNodes(nodes);
                });


                return;
            }

            if(e.targetName.indexOf('edit') > -1){//处理结点编辑事件
                if(ctx == null)
                    return;
                if(eType == 'node'){
                    var id = ctx.data.id;
                    //var iId = ctx.data.instanceId;
                    //var bizId = ctx.data.bizId;
                    var name = ctx.data.showName;
                    var nodeType = ctx.data.nodeType;
                    var bizId = ctx.data.bizId;
                    if(nodeType == 1){//资源节点
                        oc.resource.loadScript("resource/module/business-service/js/biz_node_edit.js",function(){
                            var args = {
	                    		canvasNodeId:id,
	                    		saveCallBack:function(){	//保存成功后回调
                                    _this.topoSVG.saveData(function(){
                                        _this.topoSVG.refresh();
                                    });
	                    		}
	                    	};   
                            new BizNodeEdit(args);
                        });
                    }else if(nodeType == 2){//业务节点
                        oc.resource.loadScript('resource/module/business-service/js/biz_base_info.js',function(){
                             oc.module.biz.baseinfo.open({
                                type:'edit',
                                id:bizId,
                                name:name,
                                saveCallBack:function(){    //保存成功后回调
                                    _this.topoSVG.saveData(function(){
                                        _this.topoSVG.refresh();
                                    });
                                }
                            });
                         });
                    }
                 }
                return;
            }

            if(e.targetName.indexOf('checkwarm') > -1){//处理查看告警事件
                if(ctx == null)
                    return;
                if(eType == 'node'){
                   _this.topoSVG.showWarm(ctx); 
                 }
                return;
            }

            if(e.targetName.indexOf('checkdetial') > -1){//处理结点查看详情事件
                if(ctx == null)
                    return;
                if(eType == 'node'){
                    _this.topoSVG.showDetail(ctx);
                }

                return;
            }

            if(e.targetName.indexOf('statedefine') > -1){//处理结点状态定义事件
                if(ctx == null)
                    return;
                if(eType == 'node'){
                    var iId = ctx.data.instanceId;
                    var bizId = ctx.data.bizId;
                    oc.resource.loadScript('resource/module/business-service/js/biz_status_define.js', function(){
                        oc.module.bizmanagement.status.define.open(bizId);
                    });
                }
                return;
            }

            if(e.targetName.indexOf('bus-spend') > -1){//处理结点业务响应速度事件
                if(ctx == null)
                    return;
                if(eType == 'node'){
                    var bizId = ctx.data.bizId;
                    oc.resource.loadScript('resource/module/business-service/js/bizResponeTimeMetric.js',function(){
                        oc.business.service.resp.open({
                            bizId : bizId
                        });
                    });
                }
                return;
            }

            if(e.targetName.indexOf('bus-capacity') > -1){//处理结点业务容量事件
                if(ctx == null)
                    return;
                if(eType == 'node'){
                   var bizId = ctx.data.bizId;
                    oc.resource.loadScript("resource/module/business-service/js/biz_cap.js",function(){
                        oc.business.service.cap.open({bizId:bizId});
                    });
                }
                return;
            }

            if(e.targetName.indexOf('zindex-first') > -1){//图元z轴顺序调整
                if(ctx == null)
                    return;
                ctx.node.front();
                return;
            }

            if(e.targetName.indexOf('zindex-last') > -1){//图元z轴顺序调整
                if(ctx == null)
                    return;
                ctx.node.back();
                return;
            }
            if(e.targetName.indexOf('zindex-up') > -1){//图元z轴顺序调整
                if(ctx == null)
                    return;
                ctx.node.forward();
                return;
            }
            if(e.targetName.indexOf('zindex-down') > -1){//图元z轴顺序调整
                if(ctx == null)
                    return;
                ctx.node.backward();
                return;
            }


            if(e.targetName.indexOf('save') > -1){
                _this.topoSVG.saveData();
                return;
            }

            if(e.targetName.indexOf('back') > -1){
                _this.topoSVG.back();
                return;
            }

        },
    	set_e:function(e){
    		this.e=e;
    	},
    	get_e:function(e){
    		return this.e;
    	}
    });

    return f;
});