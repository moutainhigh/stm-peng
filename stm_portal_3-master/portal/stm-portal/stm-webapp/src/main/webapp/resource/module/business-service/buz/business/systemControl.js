buzdefine("bz/systemControl", ["bz/Base","bz/extend"], function (Base,extend) {
    return extend(function(args){
    	this.args=$.extend({
    		
    	},args);

        this.topoSVG = args.topoSVG;

    	this.init();
    },Base,{
    	init:function(){
            var _this = this;

    		var bizId = this.args.bizId;
    		
    		//判断是否有编辑权限
    		//var isAllowEdit = (oc.business.service.edit?oc.business.service.edit.permissions.checkEdit(this.args.bizId):false);
    		var isAllowEdit = this.topoSVG.args.editAble;
            
    		if(isAllowEdit){
    			
    			$('#b-build').show();
    			$('#b-define').show();
    			$('#b-warmset').show();
    			
    			$('#b-build').linkbutton('RenderLB',{
    				text:"业务构建",
    				iconCls:"fa fa-sitemap",
    				onClick:function(){
    					oc.resource.loadScript("resource/module/business-service/js/biz_build.js", function(){
    						new BusinessBuild({
    							bizId:bizId,
    							callBack:function(type,data){
    								if(data.code == 200){
    									if(type == 'auto'){
    										_this.topoSVG.loadRawData(data);
    									}else if(type == 'manul'){
    										_this.topoSVG.addRawData({nodes:data.data});
    									}
    								}
    							}
    						});
    					});
    				}
    			});
    			$('#b-define').linkbutton('RenderLB',{
    				text:"状态定义",
    				iconCls:"icon-stateye",
    				onClick:function(){
    					oc.resource.loadScript('resource/module/business-service/js/biz_status_define.js', function(){
    						oc.module.bizmanagement.status.define.open(bizId);
    					});
    					
    				}
    			});
    			$('#b-warmset').linkbutton('RenderLB',{
    				text:"告警设置",
    				iconCls:"icon-alarmset",
    				onClick:function(){
    					oc.resource.loadScript('resource/module/business-service/js/biz_warn_setting.js',function(){
    						oc.business.service.set.open({clickData:bizId,type:'edit',currentID:'summary'});
    					});
    				}
    			});
    			//加载右侧导航
        		oc.resource.loadScript("resource/module/business-service/js/biz_navigation.js",function(){
    				new BizNav({
    					parent:$("#navigation_hide"),	//右侧导航将加载在parent上
    					svgHolder:_this.topoSVG.args.holder,				//业务svg图层拥有者
    					//改变背景后回调(保存背景设置等操作……)
    					changeBgImg:function(data){
    						//data={fileId:212331}	//fileId：背景图片文件Id
    						_this.topoSVG.businessView.changeBackgroud(data.fileId);
    					},
    					//拖动图标结束后回调(渲染图标，保存节点，绑定事件等操作……)
    					dragPicStop:function(data){	//图片类型(0:业务,1:资源,2:基本形状)
    						//console.log(JSON.stringify(data))
    						if(data.type==0 || data.type==1){
    							var node = {};
    							node.id = -1;
    							node.nodeType = 6;
    							node.fileId = data.fileId;
    							node.showName = '';
    							var attr = {};
    							attr.h = data.height;
    							attr.w = data.width;
    							attr.x = data.x;
    							attr.y = data.y;
    							attr.type = 'net';
    							attr.src = '../platform/file/getFileInputStream.htm?fileId=' + data.fileId;
    							node.attr = attr;
    							//返回data={"type":0,"x":519,"y":211,"fileId":"756001","width":55,"height":154}
    							//console.debug(node);
    							_this.topoSVG.addNode(node);
    						}else if(data.type==2){	//shape：矩形、圆矩形、圆、椭圆
    							//data={"type":2,"x":1038,"y":212,"shape":"矩形"}
    							var node = {};
    							node.id = -1;
    							node.nodeType = 3;
    							var attr = {};
    							attr.h = 80;
    							attr.w = 80;
    							attr.x = data.x;
    							attr.y = data.y;
    							if(data.shape == '矩形'){
        							attr.type = 'rect';
        							attr.radius = 1;
    							}else if(data.shape == '圆矩形'){
        							attr.type = 'rect';
        							attr.radius = 10;
    							}else if(data.shape == '圆'){
        							attr.type = 'circle';
    							}else if(data.shape == '椭圆'){
        							attr.type = 'ellipse';
    							}
    							
    							node.attr = attr;
    							
    							//console.debug(node);
    							_this.topoSVG.addNode(node);
    						}
    					}
    				});
    			});
    		}else{
    			$('#b-build').hide();
    			$('#b-define').hide();
    			$('#b-warmset').hide();
    		}
    		
    		var user = oc.index.getUser();
    		if(user.userType == 2 || user.userType == 3 || user.userType == 4 || user.domainUser){
    			$('#b-powerset').show();
        		$('#b-powerset').linkbutton('RenderLB',{
                    text:"权限设置",
                    iconCls:"icon-user_photo",
    				onClick:function(){
    					oc.resource.loadScript('resource/module/business-service/js/biz_warn_setting.js',function(){
    						oc.resource.loadScript('resource/module/business-service/js/business_user.js', function(){
    							oc.module.resmanagement.biz.user.open();
    						});
    					});
    				}
    			});
    		}else{
    			$('#b-powerset').hide();
    		}

    		$('#b-return').linkbutton('RenderLB',{
                text:"返回",
                iconCls:"fa fa-reply",
                onClick:function(){
                	var ids = oc.index.indexLayout.data("bizIds");
                	ids.pop();
                	oc.index.indexLayout.data("bizIds",ids);
                	if(ids && ids.length > 0){
                		var previousId = ids[ids.length - 1];
                		if(ids.length == 1){
                			_this.topoSVG.setEditAble(true);
                		}
                		_this.topoSVG.businessView.clear();
                		_this.topoSVG.businessView.cancelSelected();
                		_this.topoSVG._refreshPanelSize();
                		_this.topoSVG._loadData(previousId);
                	}else{
                		if(oc.business.service.edit.permissions.getLastEnter() == 1){
                			oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
                		}else{
                			oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_summary_model.html'));
                		}
                	}
                }
			});
        }
    });
});