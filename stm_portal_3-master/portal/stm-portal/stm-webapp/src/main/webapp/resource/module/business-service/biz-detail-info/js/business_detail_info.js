(function(){
	
	function businessDetailInfo(cfg){
		this.cfg = cfg;
	}
	businessDetailInfo.prototype={
		cfg : undefined,
		init : function(){
			var that = this;

			var dlg = $('<div style="width:1250px;height:630px;"><div id="mainInfolayout" style="height:578px;overflow:hidden"></div></div>');//height:592px;

			that.bizId = that.cfg.bizId;
			
			//查询业务节点数
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/canvas/getNodeListByBiz.htm'),
				data:{bizId:that.bizId},
				successMsg:null,
				success:function(data){
					dlg.dialog({
					    title: '业务详情',
					    width : 1250,
						height : 620,
					    onLoad:function(){
					    }
					});
					var bizName = that.cfg.bizName;
					var layout = dlg.find('#mainInfolayout').layout();
					var westDiv = layout.layout('add',{
					    region: 'west',     
					    width: 140
					    //background-color:#BDD0EE;
					    ,content : "<div id='mainInfolayoutWest' class='over-x' style='width:100%;height:542px;overflow-y: auto;background: #184580;' >"+
					    	"<div id='accordionTest' ><div class='oc-window-lefttitle clickTarget'><div class='generalShowNameForBiz'><a class='text-over'>"+bizName+"</a></div></div><div class='oc-window-leftmenu'><ul class='oc-nav-sub-list' id='vmResources'></ul></div></div></div>"
					}); 
					var centerDiv = layout.layout('add',{
					    region: 'center',    
					    width: 1100
					    ,content : '<div id="mainInfolayoutCenter" style="width:1100px;height:100%;"></div>'
					});
					layout.find('#mainInfolayoutWest').parent().addClass('resource_strategy_detail_west').css("height","542px");
					
					var nodeArr = data.data;
					var nodeNavsubList = new Array();
					var ulJQ = layout.find("#vmResources");
					if(nodeArr){
						for(var i=0;i<nodeArr.length;i++){
							var nodeObj = nodeArr[i];
							var menuType , showName;
							switch(nodeObj['nodeType']){
							case 1:
								var nodeStatus = nodeObj['nodeStatus'];
								if(nodeStatus==1){
									menuType = 'parentResource';
								}else if(nodeStatus==2){
									menuType = 'childResource';
								}else{
									menuType = 'metricNode';
								}
								break;
							case 2:
								menuType = 'childBiz';
								break;
							}
							
							var div = $('<div>').addClass('generalShowNameForBiz')
							.html(nodeObj['showName']).attr('title',nodeObj['showName']);
							var li = $('<li>').append(div).attr('name','bizMenuLi').attr('type',menuType)
							.attr('showName',nodeObj['showName']).attr('bizId',nodeObj['bizId'])
							.attr('nodeId',nodeObj.id).attr('instanceId',nodeObj.instanceId);
							ulJQ.append(li);
							
//							nodeNavsubList.push({
//								type : menuType,
//						    	name : nodeObj['showName'],
//						    	id : nodeObj.id,
//						    	instanceId : nodeObj.instanceId,
//						    	href : ''
//						    });
						}
					}
					
					var centerDiv = dlg.find("#mainInfolayoutCenter");
					
					var bizInfoTarget = dlg.find('.clickTarget');
					bizInfoTarget.on('click',function(){
						ulJQ.find('li[name=bizMenuLi]').removeClass('active');
						centerDiv.html('');
						centerDiv.load('module/business-service/biz-detail-info/biz_info.html',function(){
							var businessInfo = centerDiv.find('.business_info');
							oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/business_info.js', function(){
								
				    			oc.module.biz.businessinfo.open({selector:businessInfo,bizId:that.bizId,bizName:bizName});
//								oc.module.biz.detailinfo.businessinfo.dialog({bizId:628501,bizName:bizName});
			    			});
						});
					})
					
					ulJQ.find('li[name=bizMenuLi]').on('click',function(){
						var target = $(this);
						ulJQ.find('li[name=bizMenuLi]').each(function(){
							$(this).removeClass('active');
						});
						target.addClass('active');
						
						var type = target.attr('type');
				    	var name = target.attr('showName');
				    	var id = target.attr('nodeId');
				    	var instanceId = target.attr('instanceId');
				    	var bizId = target.attr('bizId');
				    	
				    	centerDiv.html('');
			    		switch(type){
			    		case 'parentResource':
			    			//dlgItem centerDiv ,callback:null,callbackType:'1',type:'0',instanceStatus:"res_normal_nothing"
			    			oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/resource_info.js', function(){
				    			oc.module.biz.resourceinfo.openByParentRes({id:id,selector:centerDiv,instanceId:instanceId});
			    			});
			    			break;
			    		case 'childResource':
			    			oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/resource_info.js', function(){
				    			oc.module.biz.resourceinfo.openByChildRes({id:id,selector:centerDiv,instanceId:instanceId});
			    			});
			    			break;
			    		case 'childBiz':
			    			centerDiv.html('');
							centerDiv.load('module/business-service/biz-detail-info/biz_info.html',function(){
								var businessInfo = centerDiv.find('.business_info');
								oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/business_info.js', function(){
									//当打开子资源业务时instanceId为bizId
					    			oc.module.biz.businessinfo.open({selector:businessInfo,bizId:instanceId,bizName:name});
				    			});
							});
			    			break;
			    		case 'metricNode':
			    			oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/metric_info.js', function(){
			    				oc.module.biz.bizmetricinfo.open({nodeId:id,parentInstanceId:instanceId,selector:centerDiv});
			    				
			    			});
			    			break;
			    		}
					})
					
//					oc.ui.navsublist({
//				    	selector: layout.find("#vmResources"),
//				    	data: nodeNavsubList,
//				    	addRowed:function(li,data){
//							  li.find('.text:first').attr('value',data.info).attr('title',data.name).addClass('oc-text-ellipsis');
////							  li.append('<span class="s-digit" value=' + data.info + '>' + data.number + '</span>');	
//						},
//				    	click: function(href, data, e) {
//				    		centerDiv.html('');
//				    		switch(data.type){
//				    		case 'parentResource':
//				    			//dlgItem centerDiv ,callback:null,callbackType:'1',type:'0',instanceStatus:"res_normal_nothing"
//				    			oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/resource_info.js', function(){
//					    			oc.module.biz.detailinfo.resourceinfo.openByParentRes({id:data.id,selector:centerDiv,instanceId:data.instanceId});
////				    				oc.module.biz.detailinfo.resourceinfo.dialogByParentRes({id:data.id,instanceId:data.instanceId});
//				    			});
//				    			break;
//				    		case 'childResource':
//				    			oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/resource_info.js', function(){
//					    			oc.module.biz.detailinfo.resourceinfo.openByChildRes({id:data.id,selector:centerDiv,instanceId:data.instanceId});
////				    				oc.module.biz.detailinfo.resourceinfo.dialogByChildRes({id:data.id,instanceId:data.instanceId});
//				    			});
//				    			break;
//				    		case 'childBiz':
//				    			centerDiv.html('');
//								centerDiv.load('module/business-service/biz-detail-info/biz_info.html',function(){
//									var businessInfo = centerDiv.find('.business_info');
//									oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/business_info.js', function(){
//						    			oc.module.biz.detailinfo.businessinfo.open({selector:businessInfo,bizId:data.id,bizName:data.name});
//					    			});
//								});
//				    			break;
//				    		case 'metricNode':
//				    			oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/metric_info.js', function(){
//				    				oc.module.biz.detailinfo.bizmetricinfo.open({nodeId:data.id,parentInstanceId:data.instanceId,selector:centerDiv});
////				    				oc.module.biz.detailinfo.bizmetricinfo.dialog({bizId:data.id,parentInstanceId:data.instanceId});
//				    				
//				    			});
//				    			break;
//				    		}
//				    	}
//				    });
					
					bizInfoTarget.trigger('click');
				}
			})
		}
	};
	
	
	oc.ns('oc.module.biz.detailinfo');
	oc.module.biz.detailinfo={
		open:function(cfg){
			var detailInfo = new businessDetailInfo(cfg);
			detailInfo.init();
		}
	};
	
})()