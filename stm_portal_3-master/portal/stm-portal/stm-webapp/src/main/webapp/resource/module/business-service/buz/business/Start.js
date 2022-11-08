/**
 create by xfq
 2015/7/18 0018
 */
buzdefine("bz/Start", ["bz/Cfg","bz/DataHelper","bz/BusinessView","bz/panelControl","bz/systemControl"], 
            function (cfg,DataHelper,BusinessView,panelControl,systemControl) {

    var tmp = function (args) {
    	this.args=$.extend({
            selector:null,
            showControlPanel:undefined,
            editAble:undefined
    	},args);
    	if(args.selector){
    		this.init();
    	}else{
    		throw "holder can't be null";
    	}
    };
    tmp.prototype = {
    	init:function(){
            //window.topoSVG = this;

            var panelId = this.genId();
            var bzId = this.genId();
            
            var $area = this.args.selector;//整个绘图区域对象


            this.args.root = $area.find('.panel').attr('id',panelId);
            this.args.holder = $area.find('.bz').attr('id',bzId);

            this.db = new DataHelper({
                topoSVG:this
            });
			
            //this.drawGrid();//网格绘制，没有引用，暂时注释
            if(this.args.editAble == undefined){
                this.args.editAble = (oc.business.service.edit?oc.business.service.edit.permissions.checkEdit(this.args.bizId):false);
            }
            if(this.args.showControlPanel == undefined){
                this.args.showControlPanel = true;
            }
            //console.log(this.args.editAble + " " + this.args.showControlPanel);
            
            this.bizCfg = new cfg({
                topoSVG:this,
                root:this.args.root,
                showControlPanel:this.args.showControlPanel,
                editAble:this.args.editAble
            });


            this.panelControl = new panelControl({
                    topoSVG:this,
                    bizCfg:this.bizCfg,
                    bizId:this.args.bizId});
            this.systemControl = new systemControl({
                topoSVG:this,
                bizId:this.args.bizId
            });

            
            var box = this.getBox();
            this.drawer = SVG(this.args.holder.get(0),box.w,box.h);
            
            this.businessView = new BusinessView({
                topoSVG:this,
                bizCfg:this.bizCfg,
    			drawer:this.drawer,
                holder:this.args.holder,
                db:this.db
    		});


            if(this.args.bizId)
                this._loadData(this.args.bizId);

            this._refreshPanelSize();
    	},
        _refreshPanelSize:function(){
            //var ph = ($("body").height() - $(".header").height() - $("#panel-control").height() ) + "px";
            var $panel = this.args.root;
            if($panel && $panel.length > 0){
            	$panel = $panel.find('.yw-panel');
            }
            var $pc = this.args.selector.find('.panel-control');
            
            var h = $pc.is(":hidden")?0:$pc.height();
            var ph = (this.args.selector.height()-h) + 'px';

            $panel.css({width:"100%",height:ph});

            var box = this.getBox();
            if(this.drawer)
                this.drawer.size(box.w,box.h);
        },
        setEditAble:function(able){
            this.bizCfg.set('editAble',able);
            this.panelControl.setEditAble(able);
        },
        refresh:function(){
            var bizId = this.businessView.eles.panel.bizId;

            this.businessView.clear();
            this.businessView.cancelSelected();
            this._refreshPanelSize();
            this._loadData(bizId);
        },
       /* loadData:function(businessId){
            var _this = this;
            var isAllowEdit = (oc.business.service.edit?oc.business.service.edit.permissions.checkEdit(this.args.bizId):false);
            _this.bizCfg.set("editAble",isAllowEdit);
            _this._loadData(businessId);
        },//*/
    	_loadData:function(businessId){
    		var _this = this;
            var box = this.getBox();
            _this.db.getBusiness(businessId,box,function(d){
                _this.businessView.setValue(d);
            });
    	},
        loadRawData:function(data){
            if(!data){
                alert("数据为空");
                return;
            }
            with(this){
                var box = this.getBox();
                var view = {};
                view.w = box.w;
                view.h = box.h
                view.isEmptyPanel = (businessView.getValue().nodes.length<0);
                view.data = businessView.getValue();
                view.panel = businessView.getPanel();
                businessView.setValue(db.generateTopo(view,data));
                saveData();
            }
        },
        addRawData:function(data){
            var box = this.getBox();
            var view = {};
            view.w = box.w;
            view.h = box.h

            data = this.db.convert(view,data);
            for(var i=0;i<data.nodes.length;i++){
                this.businessView.addNode(data.nodes[i]);
            }
            this.saveData();
        },
        addNode:function(node){//添加结点

            var _this = this;
            if(isNaN(node.id) || node.id <1){
                this.db.addNode(node,function(d){
                    _this.businessView.addNode(d);
                });
            }else{
                 this.businessView.addNode(node);
            }
        },
        deleteNode:function(id){//删除结点
            var _this = this;
            _this.confirm("确认删除？",function(){
                _this.db.deleteNode(id,function(d){
                  alert("删除节点成功");
                });
            });
        },
    	saveData:function(callback){
    		var v=this.businessView.getValue();
    		//console.log(JSON.stringify(v));
            this.db.update(v,function(d){
                if(typeof(callback)=='function'){
                        callback(d);
                }else{
                     if(d.data)
                        alert("保存成功");
                    else
                        alert("保存失败");
                }
            });
    	},
        autoSaveData:function(){//自动保存数据
            var v=this.businessView.getValue();
            this.db.update(v,function(d){});
        },
    	getBox:function(){
    		return {
    			w:this.args.holder.width(),
    			h:this.args.holder.height()
    		};
    	},
        drawGrid:function(){

            var drawer = this.drawer;
            if(!drawer)
                return;

            var pth="";
            var pth2 = "";

            var box = drawer.rbox();
            var max = box.w>box.h?box.w:box.h;

            for(var i=80; i<max;i=i+80){
                var p = ['M',i,0,'L',i,box.h];
                pth += p.join(' ');

                var p2 = ['M',0,i,'L',box.w,i];
                pth2 += p2.join(' ');
            }
            var g = drawer.group();
            g.path(pth + pth2).attr({'stroke':'#E5E5E5','stroke-width':'1','opacity':1});

            pth="";
            pth2 = ""; 
            for(var i=20; i<max;i=i+20){
                var p = ['M',i,0,'L',i,box.h];
                pth += p.join(' ');

                var p2 = ['M',0,i,'L',box.w,i];
                pth2 += p2.join(' ');
            }
            g.path(pth + pth2).attr({'stroke':'#F2F2F2','stroke-width':'1','opacity':1});
        },
        confirm:function(msg,callback){
            oc.ui.confirm(msg,function() {
                if(typeof(callback)=='function')
                    callback();
            });
        },
        showHealth:function(){
            var biz = this.businessView.getPanel();
            if(!biz)
                return;
            bizId = biz.bizId;
            oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/biz_health_dlg.js', function(){
                oc.module.biz.healthdlg.open({bizId:bizId});
            });
        },
        _checkBuzAuthority:function(ctx,bizId,callback){
            var usr = oc.index.getUser();
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/user/checkUserView.htm'),
                data:{
                    user_id:usr.id,
                    biz_id:bizId
                },
                successMsg:null,
                success:function(d){
                    if(!d.data){
                         alert("没有查看权限!");
                        return;
                    }
                    if(typeof(callback)=='function')
                        callback(ctx);
                }
            });

        },
        showWarm:function(ctx){
            var _this = this;
            var d = ctx.data;
            var bizId = d.bizId;
            if(d.nodeType == 1){
                bizId = d.instanceId;
            }else if(d.nodeType == 2){
               if(d.bizId != d.instanceId){
                    bizId = d.instanceId;
               }
            }
            _this._checkBuzAuthority(ctx,bizId,function(ctx){
                _this._showWarm(ctx);
            });
        },
        _showWarm:function(ctx){
            var iId = ctx.data.instanceId;
            var bizId = ctx.data.bizId;
            var name = ctx.data.showName;
            var nodeType = ctx.data.nodeType;
            var addWindow = $('<div/>');
            if(nodeType == 1){//资源节点
                if(ctx.data.nodeStatus == -1){
                    alert('节点未监控');
                    return;
                }
                addWindow.dialog({
                  title:"告警信息",
                  href:oc.resource.getUrl('resource/module/business-service/alarm.html'),
                  width:800,
                  height:510,
                  modal:true,
                  onLoad:function(){
                    oc.resource.loadScript('resource/module/business-service/js/alarm.js',function(){
                        oc.module.biz.ser.alarm.open({id:iId,name:name,nodeId:ctx.data.id});
                    });
                  }
                });
            }else if(nodeType == 2){//业务节点

                if(iId == bizId){//主业务
                    addWindow.dialog({
                      title:"告警信息",
                      href:oc.resource.getUrl('resource/module/business-service/alarm.html'),
                      width:800,
                      height:510,
                      modal:true,
                      onLoad:function(){
                        oc.resource.loadScript('resource/module/business-service/js/alarm.js',function(){
                            oc.module.biz.ser.alarm.open({id:iId,name:name});
                        });
                      }
                    });
                }else{
                   addWindow.dialog({
                      title:"告警信息",
                      href:oc.resource.getUrl('resource/module/business-service/alarm.html'),
                      width:800,
                      height:510,
                      modal:true,
                      onLoad:function(){
                        oc.resource.loadScript('resource/module/business-service/js/alarm.js',function(){
                            oc.module.biz.ser.alarm.open({id:iId,name:name});
                        });
                      }
                    }); 
                }
            }       
        },
        showDetail:function(ctx){
            var _this = this;
            var d = ctx.data;
            var bizId = d.bizId;
             
            if(d.nodeType == 2){
               if(d.bizId != d.instanceId){
                    var usr = oc.index.getUser();
                    oc.util.ajax({
                    url:oc.resource.getUrl('portal/business/user/checkUserView.htm'),
                        data:{
                            user_id:usr.id,
                            biz_id:d.instanceId
                        },
                        successMsg:null,
                        success:function(d){
                            //console.log(d);
                            if(d.data){
                                _this.openSubBusSys(ctx);
                                
                            }else{
                                alert("没有查看权限!");
                            }

                        }
                    });
                    return;
               }
            }
            if(ctx.data.nodeType == 2){
                if(ctx.data.bizId == ctx.data.instanceId){
                    oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
                        oc.module.biz.detailinfo.open({bizId:bizId,bizName:ctx.data.showName});
                    });
                }else{
                    oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/business_info.js', function(){
                        oc.module.biz.businessinfo.dialog({bizId:ctx.data.instanceId,bizName:ctx.data.showName});
                    });
                }
            }else if(ctx.data.nodeType == 1){
                if(ctx.data.nodeStatus == -1){
                    alert('节点未监控');
                    return;
                }
                oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/biz_node_dlg_adapt.js', function(){
                    oc.module.biz.biznodedlgadapt.dialog({nodeId:ctx.data.id});
                });
            }
        },
        openSubBusSys:function(ctx){
            //进入子业务系统
            var d = ctx.data;
            if(d.nodeType != 2)
                return;
            if(d.bizId == d.instanceId)
                return ;
            var p = this.businessView.getPanel();
            p.bizId = d.instanceId;
    		var ids = oc.index.indexLayout.data("bizIds");
            if (ids && ids.length > 0) {
            	oc.index.indexLayout.data("bizIds").push(d.instanceId);
            } else {
            	ids = new Array();
            	ids.push(d.instanceId);
            	oc.index.indexLayout.data("bizIds", ids);
            }
            //禁止其可编辑
            this.setEditAble(false);
            this.refresh();
        },
        showToolTip:function(ctx,e){
            if(ctx.data.nodeType == 2 && ctx.data.bizId == ctx.data.instanceId){
                    if(!ctx.nodemouseON){
                        ctx.nodemouseON = {};
                        ctx.nodemouseON.mouseout = false;
                    }

                    if(!ctx.nodemouseON.event){
                        ctx.nodemouseON.mouseout = false;
                        ctx.nodemouseON.event =setTimeout(function(){
                            //console.log('setTimeout is mouseout-:' + ctx.nodemouseON.mouseout);
                            if(!ctx.nodemouseON.mouseout){
                                oc.resource.loadScript("resource/module/business-service/buz/business/NodeTip.js",function(){
                                    oc.business.canvas.nodetip.open({bizId:ctx.data.bizId,x:e.clientX,y:e.clientY});
                                    ctx.nodemouseON.opend = true;
                                    delete ctx.nodemouseON.event;
                                });
                            }else{
                                delete ctx.nodemouseON.event; 
                            }
                        },1000);
                    }
                }
        },
        closeTooltip:function(ctx,e){
            if(!ctx.nodemouseON){
                ctx.nodemouseON = {};
            }
            ctx.nodemouseON.mouseout = true;
            setTimeout(function(){
                oc.resource.loadScript("resource/module/business-service/buz/business/NodeTip.js",function(){
                    try{
                        oc.business.canvas.nodetip.close();
                    }catch(e){}
                });
            },10);
            //console.log('is mouseout-:' + ctx.nodemouseON.mouseout);
        },
        back:function(){//返回上一级
           
            if(oc.business.service.edit.permissions.getLastEnter() == 1){
            	oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
            }else{
            	oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_summary_model.html'));
            }
        },
        genId:function(){//生成Id
           return 'buz-'+Math.round(Math.random()*99999999999);
        }
    };
    return tmp;
});