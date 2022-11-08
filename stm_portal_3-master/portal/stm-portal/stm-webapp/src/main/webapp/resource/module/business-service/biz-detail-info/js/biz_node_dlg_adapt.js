(function(){
	
	oc.ns('oc.module.biz.biznodedlgadapt');
	oc.module.biz.biznodedlgadapt={
		dialog:function(cfg){
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/canvas/getSingleInstanceNodeType.htm'),
				data:{nodeId:cfg.nodeId},
				successMsg:null,
				success:function(data){
					if(data.data){
						var nodeObj = data.data;
						
						switch(nodeObj['nodeType']){
						case 1:
							var nodeStatus = nodeObj['nodeStatus'];
							if(nodeStatus==1){
								//'parentResource';
								oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/resource_info.js', function(){
				    				oc.module.biz.resourceinfo.dialogByParentRes({id:nodeObj.id,instanceId:nodeObj.instanceId});
				    			});
							}else if(nodeStatus==2){
								//'childResource';
								oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/resource_info.js', function(){
				    				oc.module.biz.resourceinfo.dialogByChildRes({id:nodeObj.id,instanceId:nodeObj.instanceId});
				    			});
							}else{
								//'metricNode';
								oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/metric_info.js', function(){
				    				oc.module.biz.bizmetricinfo.dialog({nodeId:nodeObj.id,parentInstanceId:nodeObj.instanceId});
				    				
				    			});
							}
							break;
						case 2:
							//'childBiz';
							oc.resource.loadScript('resource/module/business-service/biz-detail-info/js/business_info.js', function(){
				    			oc.module.biz.businessinfo.dialog({bizId:nodeObj.bizId,bizName:nodeObj.showName});
			    			});
							break;
						}
				}
			}
		})
		}
	};
	
})()