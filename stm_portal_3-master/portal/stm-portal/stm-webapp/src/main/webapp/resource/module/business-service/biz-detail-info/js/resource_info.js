$(function() {
	function resourceInfo(cfg){
		this.cfg = cfg;
		this.openType = cfg.openType;
		//节点id
		this.id = cfg.id;
	}
	resourceInfo.prototype={
			cfg : undefined,
			//parentRes childRes
			openType : undefined,
			ifParentRes : function(){
				return (this.openType=='parentRes')
			},
			init : function(){
				var that = this;
				if(that.ifParentRes()){
					that.renderMainDiv();
				}else{
					//关联子资源,先查询所有关联的子资源instanceid
					var childIdMap = {};
//					childIdMap[279002] = true;
//					that.childIdMap = childIdMap;
//					that.renderMainDiv();
					oc.util.ajax({
						url : oc.resource.getUrl('portal/business/canvas/getChildInsTypeInfoByNodeId.htm'),
						successMsg:null,
						data : {nodeId : that.id,parentInstanceId:that.cfg.instanceId},
						success : function(json) {
							if (json.code == 200) {
								var childInstances = json.data;
								
								var childTypeArr = new Array();
								if(childInstances){
									for(key in childInstances){
										var childs = childInstances[key];
										//如果这个类型下资源全为未监控,则过滤
										var isMonitorFlag = false;
										for(var i=0;i<childs.length;i++){
											var item = childs[i];
											if(item.isMonitor){
												isMonitorFlag = true;
											}
										}
										if(isMonitorFlag){
											childTypeArr.push(key);
										}
									}
								}
							}
							if(childTypeArr.length>0){
								that.childTypeArr = childTypeArr;
								that.childIdMap = childInstances;
								that.renderMainDiv();
							}else{
								alert('关联子资源未监控!');
							}
						}
					});
				}
			},
			renderMainDiv : function(){
				var that = this;
				// 查询资源详细信息
				oc.util.ajax({
					url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getResourceInfo.htm'),
					successMsg:null,
					data : {instanceId : this.cfg.instanceId},
					success : function(json) {
						if (json.code == 200) {
							that.resourceInfo = json.data;
							if(that.resourceInfo.lifeState == 'MONITORED'){
//							if(false){
								if(that.resourceInfo.resourceType == 'StandardService'){
									that._initStandardServiceInfo(that.resourceInfo);
								}else{
									that._initResourceDetailInfo(that.resourceInfo);
								}
								
							}else{
								alert('该资源当前不是监控状态');
								$(".tinytool").attr('style','display:none');
							}
						} else {
							alert('没有查询到对应的资源信息');
							$(".tinytool").attr('style','display:none');
							return false;
						}
					}
				});
			},
			_initStandardServiceInfo : function(){
					$(".tinytool").attr('style','display:none');
				var that = this;
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/dialog_standard_application.js',function(){
//					oc.resource.management.dialog.standard.application.open({instanceId : that.resourceInfo.instanceId, callback:null});
					oc.resource.management.dialog.standard.application.openByBiz({selector:that.cfg.selector,instanceId : that.resourceInfo.instanceId, callback:null});
				 });
			},
			_initResourceDetailInfo : function(){
				$(".tinytool").attr('style','display:none');
				var that = this;
				// 子资源分类
				this.dlgDom = that.cfg.selector;
				this.tabsDom = $("<div/>");
				this.dlgDom.append(this.tabsDom);

				// 新建tabs
				if(that.ifParentRes()){
					$(".tinytool").html("");
					this._initTabs();
					$(".tinytool").attr('style','display:block');
				}else{
					
					this._initOnlyChildTabs();
					$(".tinytool").attr('style','display:none');
				}
			},
			_initOnlyChildTabs : function(){
				var that = this;
				//过滤
				var childTypeArr = new Array();
				if(that.childTypeArr){
					for(var i=0;i<that.childTypeArr.length;i++){
						var type = that.childTypeArr[i];
						for(var x=0;x<that.resourceInfo.childrenType.length;x++){
							var childrenTypeObj = that.resourceInfo.childrenType[x];
							if(type==childrenTypeObj['type']){
								childTypeArr.push(childrenTypeObj);
								break;
							}
						}
					}
				}
				
				that.childrenType = childTypeArr;
				var instanceId=this.cfg.instanceId;//
				
				var instanceStatus= this.cfg.instanceStatus;
				// 新建tab页
				that.tabs = this.tabsDom.addClass("window-tabsbg").tabs({
					fit : true,
					tabWidth : 71,
					tabHeight : 33,
					scrollIncrement : 402//滚动长度
				});
				
				// 对主机的子资源进行排序操作
				if(that.childrenType == 'Host'){
					for(var index in that.childrenType){
						var child = that.childrenType[index];
						switch (child.type) {
						case 'CPU':
							child.No = 1;
							break;
						case 'Partition':
							child.No = 2;
							break;
						case 'HardDisk':
							child.No = 3;
							break;
						case 'NetInterface':
							child.No = 4;
							break;
						case 'Process':
							child.No = 5;
							break;
						case 'File':
							child.No = 6;
							break;
						case 'LogicalVolume':
							child.No = 7;
							break;
						case 'VolumeGroup':
							child.No = 8;
							break;
						default:
							child.No = 999;
							break;
						}
					}
					that.childrenType = that.childrenType.sort(function(a, b){
						return parseInt(a.No) - parseInt(b.No);
					});
				}
				var args = [];
				// 虚拟化要展示的资源
				var relationRes = that.resourceInfo.relationRes;
				for(var i = 0; relationRes != undefined && i < relationRes.length; i++){
					var resource = relationRes[i];
					var contentDiv = $("<div/>");
					this.tabs.tabs('add', {
						title : resource.name,
						selected : false,
						content : contentDiv
					});
					
					var arg = {
						type : 'vm',
						title : resource.name,
						html : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html'),
						js : 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js',
						instanceId : that.resourceInfo.instanceId,
						availability : that.resourceInfo.availability,
						content : contentDiv,
						resourceId : resource.resourceId,
						vminstanceIdList : resource.idList,
						isLoad : false,
						childIdMap : that.childIdMap
					};
					args.push(arg);
				}

				for(var i = 0; i < that.childrenType.length; i++){
					var child = that.childrenType[i];
					var htmlFile = undefined;
					var jsFile = undefined;
					var contentDiv = $("<div/>");
					switch (child.type) {
					case 'Process':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/ProcessMetricData.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/ProcessMetricData.js';
						break;
					case 'File':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/fileMetricData.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/fileMetricData.js';
						break;
					case 'LogicalVolume':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/logicalVolume.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/logicalVolume.js';
						break;
					case 'VolumeGroup':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/volumeGroup.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/volumeGroup.js';
						break;
					default:
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html');
					    jsFile = 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js';
						break;
					}
					
					this.tabs.tabs('add', {
						title : child.name,
						selected : false,
						content : contentDiv
					});
					
					var rinsArr = that.childIdMap[child.type];
					var instanceIds = {};
					for(var x=0;x<rinsArr.length;x++){
						var ri = rinsArr[x];
						instanceIds[ri.childInstanceId] = true;
					}
					var arg = {
						type : child.type,
						title : child.name,
						html : htmlFile,
						js : jsFile,
						instanceId : that.resourceInfo.instanceId,
						availability : that.resourceInfo.availability,
						content : contentDiv,
						isLoad : false,
						resourceType : that.resourceInfo.resourceType,
						childIdMap : instanceIds
					}
					args.push(arg);
				}
				// 把子资源加入到tabs里面
				this.tabs.tabs({
					onSelect : function(title, index){
						$("div.menu").css('left','1028px')
						if(index > 0){
							that.loadChildPanelSingle(args, index);
						}
					}
				});
				// 调整tabs头的问题一定放在这个函数的最后
				this.changeTabsHeadHeight();
				
				if(args.length==1){
					$("div.menu").css('left','1028px');
					
					
					this.tabsDom.find('.tabs-header').hide();
					this.tabsDom.find('.tabs-panels').height(595);
					this.tabsDom.find('.panel').height(595);
					
					that.loadChildPanelSingle(args, 0);
				}else if(args.length>1){
					$("div.menu").css('left','1028px');
					that.loadChildPanelSingle(args, 0);
				}else{
					alert('关联子资源未监控!');
				}
			},
			loadChildPanelSingle : function(args, currentNo){
				var that = this;
				//业务打开,一种类型子资源去除tabs
				var argsLengthFlag = false;
				if(args&&args.length==1){
					argsLengthFlag = true;
				}
				
				var arg = args[currentNo];
				if(!arg.isLoad)
					arg.content.panel({
						href : arg.html,
						onLoad : function(){
							oc.resource.loadScript(arg.js, function() {
								switch (arg.type) {
								case 'vm':
									oc.resourced.resource.detail.showmetric.openVMResource(arg.instanceId, arg.resourceId, arg.title 
											,arg.availability, arg.vminstanceIdList,{argsLengthFlag:argsLengthFlag});
									break;
								case 'Process':
									oc.resourced.resource.detail.process.open(arg.instanceId, arg.availability,{argsLengthFlag:argsLengthFlag});
									break;
								case 'File':
									oc.resourced.resource.detail.file.open(arg.instanceId, arg.availability);
									break;
								case 'LogicalVolume':
									oc.resourced.resource.detail.logicalvolume.open(arg.instanceId, arg.availability);
									break;
								case 'VolumeGroup':
									oc.resourced.resource.detail.volumegroup.open(arg.instanceId, arg.availability);
									break;
								case 'alarm':
									oc.alarm.resource.list.open(arg.instanceId);
									break;
								case 'business':
									oc.business.service.list.open(arg.instanceId);;
									break;
								case 'controlPanel':
									oc.resource.controlpanel.list.open(arg.instanceId,arg.resourceInfo);
									break;
								case 'metricGroup':
									oc.resource.metricgroup.list.open(arg.instanceId);
									break;
								default:
									oc.resourced.resource.detail.showmetric.openByBiz({instanceId:arg.instanceId, 
										metricType:arg.type, metricTypeName:arg.title,argsLengthFlag:argsLengthFlag,
										mainResourceType:arg.resourceType,childIdMap:arg.childIdMap,ifParentRes:that.ifParentRes()});
									break;
								}
								arg.isLoad = true;
							});
						}
					});
			},
			_initTabs : function(){
				var that = this;
				this.childrenType = that.resourceInfo.childrenType;
				var instanceId=this.cfg.instanceId;//
				
				// 主资源页面和JS
				var html = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/general.html');
				var js = 'resource/module/resource-management/resourceDetailInfo/js/generalNew.js';
				var instanceStatus= that.resourceInfo.availability;
				// 新建tab页
				that.tabs = this.tabsDom.addClass("window-tabsbg").tabs({
					fit : true,
					/*tabWidth : 136,
					tabHeight : 38,*/
					tabHeight : 33,
					scrollIncrement : 402//滚动长度
				}).tabs('add', {
						title : '资源总览',
						selected : true,
						href : html,
						onLoad : function(){
						
							oc.resource.loadScript(js, function(){
								oc.module.resmanagement.resdeatilinfo.general.open(that.resourceInfo,instanceStatus);
							});
						
						}
					});/*.tabs('add', {
						title : '指标',
						selected : false,
						href :  oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/metricGroup.html'),
						onLoad : function(){
			    			js = 'resource/module/resource-management/resourceDetailInfo/js/metricGroup.js';
							oc.resource.loadScript(js, function(){
								oc.resource.metricgroup.list.open(instanceId);
							});
						}
					}).tabs('add', {
						title : '告警',
						selected : false,
						href : oc.resource.getUrl('resource/module/alarm-management/alarm-resource-list.html'),
						onLoad : function(){
							//url = ;
							js = 'resource/module/alarm-management/js/alarm-resource-list.js';
							oc.resource.loadScript(js, function(){
								oc.alarm.resource.list.open(instanceId);
							});
						}
					});*/
				
				// 对主机的子资源进行排序操作
				if(that.resourceInfo.resourceType == 'Host'){
					for(var index in this.childrenType){
						var child = this.childrenType[index];
						switch (child.type) {
						case 'CPU':
							child.No = 1;
							break;
						case 'Partition':
							child.No = 2;
							break;
						case 'HardDisk':
							child.No = 3;
							break;
						case 'NetInterface':
							child.No = 4;
							break;
						case 'Process':
							child.No = 5;
							break;
						case 'File':
							child.No = 6;
							break;
						case 'LogicalVolume':
							child.No = 7;
							break;
						case 'VolumeGroup':
							child.No = 8;
							break;
						default:
							child.No = 999;
							break;
						}
					}
					this.childrenType = this.childrenType.sort(function(a, b){
						return parseInt(a.No) - parseInt(b.No);
					});
				}
				var args = [];
				// 虚拟化要展示的资源
				var relationRes = that.resourceInfo.relationRes;
				for(var i = 0; relationRes != undefined && i < relationRes.length; i++){
					var resource = relationRes[i];
					var contentDiv = $("<div/>");
					this.tabs.tabs('add', {
						title : resource.name,
						selected : false,
						content : contentDiv
					});
					var arg = {
						type : 'vm',
						title : resource.name,
						html : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html'),
						js : 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js',
						instanceId : that.resourceInfo.instanceId,
						availability : that.resourceInfo.availability,
						content : contentDiv,
						resourceId : resource.resourceId,
						vminstanceIdList : resource.idList,
						isLoad : false
					};
					args.push(arg);
				}
				var alarmobj={No:20,id:'alarm',name:'告警',type:'alarm'};
				var controlPanelobj={No:21,id:'controlPanel',name:'面板图',type:'controlPanel'};
				var busobj={No:22,id:'business',name:'业务系统',type:'business'};
				var metricGroup={No:23,id:'metricGroup',name:'指标',type:'metricGroup'};
		    	//	datas[0]=obj;
		    		var dataArr=new Array();
		    	
					dataArr.push(metricGroup);//指标
					dataArr.push(alarmobj);//告警
					
		    		if(this.childrenType!=undefined){
		    		for(var i=0;i<this.childrenType.length;i++){
		    			dataArr.push(this.childrenType[i]);
		    		}
		    		}
					
					if(that.resourceInfo.resourceType=="Host"){//主机
						dataArr.push(busobj);
					}else if(that.resourceInfo.resourceType=='NetworkDevice'){//网络设备
							dataArr.push(busobj);
							dataArr.push(controlPanelobj);
					}else if(that.resourceInfo.resourceType=='VirtualCluster'){//虚拟化资源
						
							dataArr.push(busobj);
					}
				
				// 把子资源加入到tabs里面
				
				for(var i = 0; i < dataArr.length; i++){
					var child = dataArr[i];
					var htmlFile = undefined;
					var jsFile = undefined;
					var contentDiv = $("<div/>");
					switch (child.type) {
					case 'Process':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/ProcessMetricData.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/ProcessMetricData.js';
						break;
					case 'File':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/fileMetricData.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/fileMetricData.js';
						break;
					case 'LogicalVolume':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/logicalVolume.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/logicalVolume.js';
						break;
					case 'VolumeGroup':
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/volumeGroup.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/volumeGroup.js';
						break;
					default:
						htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html');
					    jsFile = 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js';
						break;
					}
					 if(child.type=='metricGroup'){
						 htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/metricGroup.html');
		    			jsFile = 'resource/module/resource-management/resourceDetailInfo/js/metricGroup.js';
		    		}
		    		
		    		else if(child.type=='alarm'){
		    			htmlFile = oc.resource.getUrl('resource/module/alarm-management/alarm-resource-list.html');
    					// 'resource/module/resource-management/resourceDetailInfo/js/general.js'
		    			jsFile = 'resource/module/alarm-management/js/alarm-resource-list.js';
		    		}
		    		
		    		else if(child.type=='business'){
		    			htmlFile = oc.resource.getUrl('resource/module/business-service/business_service_list.html');
		    			jsFile = 'resource/module/business-service/js/business_service_list.js';
		    		}
		    		
		    		else if(child.type=='controlPanel'){
		    			htmlFile = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/controlPanel.html');
		    			jsFile = 'resource/module/resource-management/resourceDetailInfo/js/controlPanel.js';
		    		}
					this.tabs.tabs('add', {
						title : child.name,
						selected : false,
						content : contentDiv
					});
					var arg = {
						type : child.type,
						title : child.name,
						html : htmlFile,
						js : jsFile,
						instanceId : that.resourceInfo.instanceId,
						availability : that.resourceInfo.availability,
						content : contentDiv,
						isLoad : false,
						resourceInfo:that.resourceInfo,
						resourceType : that.resourceInfo.resourceType
					}
					args.push(arg);
				}
				this.tabs.tabs({
					onSelect : function(title, index){
						$("div.menu").css('left','1028px')
						if(index > 0){
							$(".tinytool").attr('style','display:none');
							that.loadChildPanelSingle(args, index - 1);
						}else{
							$(".tinytool").attr('style','display:block');
						}
					}
				});
				
				// 调整tabs头的问题一定放在这个函数的最后
				this.changeTabsHeadHeight();
			
			},
			changeTabsHeadHeight : function(){
//				var type=this.cfg.type;
				var childrenTypeCnt = this.childrenType.length;
				var oneTabsWidth = 151;
			
				var navWidth = 3;
				var navHeight = 44;
				var tabsHeight = this.tabsDom.height();
//				if(type==0){
//					navWidth=5;
//				}
				var tabsDefaultWith='1100px';
				var defaultHeight=(childrenTypeCnt + 3) * oneTabsWidth + navWidth;
				var tabsHeadHeight = (childrenTypeCnt + 3) * oneTabsWidth + navWidth + 'px';
				if(defaultHeight<1100){
					tabsHeadHeight='1100px';
				}
				if(childrenTypeCnt > 3){
					var tabsDom = this.tabsDom.find(".tabs-header > .tabs-wrap > .tabs").width(tabsHeadHeight);//
					this.tabsDom.find(".tabs-panels").height(tabsHeight - navHeight);
					this.tabsDom.find(".tabs-panels > .panel").height(tabsHeight - navHeight);
					this.tabsDom.find(".tabs-panels > .panel > .panel-body").height(tabsHeight - navHeight);
					this.tabsDom.find('.tabs-header > .tabs-scroller-left').css('display', 'none');
//					if(type==1){
//						this.tabsDom.find('.tabs-header > .tabs-scroller-right').css('display', 'none');
//					}else{
//						this.tabsDom.find('.tabs-header > .tabs-scroller-right').css('display', 'block');
//					}
				}
				
			}
	
	}

	oc.ns('oc.module.biz.resourceinfo');
	oc.module.biz.resourceinfo={
		//{id:data.id,selector:dlgItem,instanceId:data.instanceId}
		openByParentRes:function(cfg){
			oc.resource.loadScripts(['resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js'],function(){
				cfg['openType']='parentRes';
				var info = new resourceInfo(cfg);
				info.init();
			})
			
		},
		//{id:data.id,instanceId:data.instanceId}
		dialogByParentRes:function(cfg){
			oc.resource.loadScripts(['resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/generalNew.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/resourceCommonChart.js'],function(){
				var dlgItem = $('<div>');
				dlgItem.dialog({
				    title: '关联主资源',
				    width : 1100,
					height : 630,
				    onLoad:function(){
				    }
				});
				cfg['openType']='parentRes';
				cfg['selector']=dlgItem;
				var info = new resourceInfo(cfg);
				info.init();
			});
		},
		openByChildRes:function(cfg){
			oc.resource.loadScripts(['resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/generalNew.js'],function(){
				cfg['openType']='childRes';
				var info = new resourceInfo(cfg);
				info.init();
			})
		},
		dialogByChildRes:function(cfg){
			oc.resource.loadScripts(['resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/generalNew.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/resourceCommonChart.js'],function(){
				var dlgItem = $('<div>');
				dlgItem.dialog({
				    title: '关联子资源',
				    width : 1100,
					height : 630,
				    onLoad:function(){
				    }
				});
				cfg['openType']='childRes';
				cfg['selector']=dlgItem;
				var info = new resourceInfo(cfg);
				info.init();
			})
		}
	};
});