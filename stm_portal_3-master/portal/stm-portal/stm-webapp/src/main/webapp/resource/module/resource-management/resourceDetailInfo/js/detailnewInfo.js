$(function() {
	
	
	var hand=false;
	var childResourceHtml = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html');
	var childResourceJs = 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js';

	function detailInfo(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	detailInfo.prototype = {
		constructor : detailInfo,
		cfg : undefined, // 外界传入的配置参数 json对象格式
		dlg : undefined,
		dlgDom : undefined,
		tabs : undefined,
		tabsDom : undefined,
		childrenType : undefined,
		timeId : undefined,
		resourceInfo : undefined,
		isHandle :false,
		_defaults : {},
		open : function() {
			var that = this;
			// 查询资源详细信息
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getResourceInfo.htm'),
				successMsg:null,
				data : {
					instanceId : this.cfg.instanceId
				},
				success : function(json) {
					if (json.code == 200) {
						that.resourceInfo = json.data;
						if(that.resourceInfo.lifeState == 'MONITORED'){
							if(that.resourceInfo.resourceType == 'StandardService'){
								that._initStandardServiceInfo(that.resourceInfo);
							}else{
								that._initResourceDetailInfo(that.resourceInfo);
							}
							//采集状态不可用
							var CollectState = that.resourceInfo.CollectState;
							if(CollectState && CollectState=='UNCOLLECTIBLE'){
//							if(true){
								that._cannotCollectDialog();
							}
							
						}else{
							alert('该资源当前不是监控状态');
						}
					} else {
						alert('没有查询到对应的资源信息');
						return false;
					}
				}
			});
		//	hand=	that._initIsHandle();
		//	console.info("this.isHandle"+hand);
		},
		_initIsHandle :function(){
			var isHandle=false;
			/*oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/resourceDetailInfo/isMetricHand.htm'),
				successMsg:null,
				data : {
					instanceId : this.cfg.instanceId
				},
				async:false,
				success : function(json) {
					if (json.code == 200) {
					if(json.data=='FALSE'){//未采集
						isHandle=false;
					}else{
						isHandle=true;
					}
						
					} 
				}
			});*/
			return isHandle;
		},
		_cannotCollectDialog : function(){

			var hintDlg = $("<div/>");
			hintDlg.append($("<div/>").css('top', '45px').css('text-align','center').addClass("oc-notice").html("所有采集方式不可用,设备暂时无法采集!"));
			hintDlg.dialog({
				title : ' ',
				width : '300px',
				height : '145px',
//				buttons : [],
				onClose : function(){
					hintDlg.dialog('destroy');
				}
			});
		
		},
		_initStandardServiceInfo : function(){
			var that = this;
			oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/dialog_standard_application.js',function(){
				oc.resource.management.dialog.standard.application.open({instanceId : that.resourceInfo.instanceId, callback:that.cfg.callback});
			 });
		},
		_initResourceDetailInfo : function(){
			var that = this;
			var detailType=this.cfg.type;//0資源 1虛擬化
//			console.info(detailType);
		
			// 子资源分类
			this.dlgDom = $("<div/>");
			this.tabsDom = $("<div/>").css("background","#1d4170").height("65px");//.height("70px");
			var content=$("<div/>").addClass("main-resource").height("86%");/*.append("<div class='tabPanel' id='all' onclick=showAndHide()></div>");*/
			content.css("height","86%");
			///content.css({width:'100%',height:'100%'});
			//			this.tabsDom.append('<div class="cediv"></div>')
			this.dlgDom.append(this.tabsDom);
			this.dlgDom.append(content); 
			// 新建窗口
			var windowHeight = $(window).height(), dlgHeight = 630, dlgWidth = 1128;
			var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
			var title=("<label class='titleshow'>"+that.resourceInfo.name.htmlspecialchars()+"</label>")+(this.resourceInfo.CollectState=="UNCOLLECTIBLE" ? "<span class='panel_pointsa'>（该设备无法采集，以下为快照信息！）</span>" : "");//+("<div class='titlePanel'></div>");
			
			this.dlg = this.dlgDom.dialog({
				title : title,
				width : dlgWidth,
				height : dlgHeight,
				top : dlgTop,
				onClose : function(){
		
					that.dlg.panel('destroy');
			
					if(typeof(that.cfg.callback) != 'undefined'){
						if(that.cfg.callbackType == '1'){
							that.cfg.callback.reLoadAll();
						}else{
							that.cfg.callback();
						}
					}
					clearInterval(that.timeId);
				}
			});
			$(".panel-header.panel-header-noborder.window-header").addClass("resource-panel-head");
			if(detailType==2){//业务
				$(".panel-body.panel-body-noborder.layout-body").css({"overflow":"hidden"});
			}else{
				$(".panel-body.panel-body-noborder.window-body").css({"padding":"0px","overflow":"hidden","width":"100%"});
			}
			
			// 新建tabs
			this._initTabs(this.dlg);
			//
			// 定时提示器
			this.setRefreshInterval();
		},
		_initTabs : function(dlg){
		var that=this;
			var	hand=	that._initIsHandle();
				var detailType=this.cfg.type;//0資源 1虛擬化
			//	console.info(detailType);
		this.childrenType = that.resourceInfo.childrenType;
		//<ul class="nav-list" id="menuList"></ul>
			var navehtml='<div class="oc-nav-menu" style="width:90%;float:left;height:70px;"><div>';
			var collectBtn='<div id="collectBtn" style="width:10%;float:left;background: #0F3566;"> <div id="refBtnBlues"  style="float:left;background: #0F3566;" ><a  id="refBtnBlue" group="" style="display:block;"><span class="l-btn-left l-btn-icon-left"><div class="btn-l">  <div class="btn-r">    <div class="btn-m"><span class="l-btn-icon icon-hand">&nbsp;</span><span class="l-btn-text">手动采集</span></div>  </div></div></span></a><button id="refBtn" style="display:none" class="btn_gray"><span class="fa fa-refresh paddingr-10"></span><span>手动采集</span></button></div></div>'
			this.tabsDom.append(navehtml);

				this.tabsDom.append(collectBtn);
		//	$(".titlePanel").append(navehtml);
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
				if(this.childrenType!=undefined){
					this.childrenType = this.childrenType.sort(function(a, b){
						return parseInt(a.No) - parseInt(b.No);
					});
				}
				
			}
			var obj={No:0,id:'resources',name:'资源总览',type:'resources',imgUrl:'',icon:'res-size-nav resources'};
			var alarmobj={No:20,id:'alarm',name:'告警',type:'alarm',imgUrl:'',icon:'res-size-nav alarm'};
			var controlPanelobj={No:21,id:'controlPanel',name:'面板图',type:'controlPanel',imgUrl:'',icon:'res-size-nav controlPanel'};
			var busobj={No:22,id:'business',name:'业务系统',type:'business',imgUrl:'',icon:'res-size-nav business'};
			var metricGroup={No:23,id:'metricGroup',name:'指标',type:'metricGroup',imgUrl:'',icon:'res-size-nav metricGroup'};
			var assetInformation={No:24,id:'assetInformation',name:'资产信息',type:'assetInformation',imgUrl:'',icon:'res-size-nav metricGroup'};
	    	//	datas[0]=obj;
	    		var dataArr=new Array();
	    		dataArr.push(obj);
				dataArr.push(metricGroup);//指标
				dataArr.push(alarmobj);//告警
				
	    		if(this.childrenType!=undefined){
	    		for(var i=0;i<this.childrenType.length;i++){
	    			dataArr.push(this.childrenType[i]);
	    		}
	    		}
	    		var isHaveBiz=that.isHaveBiz();
				if(isHaveBiz==true){//添加业务系统标签
					if(that.resourceInfo.resourceType=="Host"){//主机
						dataArr.push(busobj);
					}else if(that.resourceInfo.resourceType=='NetworkDevice'){//网络设备
							dataArr.push(busobj);
							dataArr.push(controlPanelobj);
					}else if(that.resourceInfo.resourceType=='VirtualCluster'){//虚拟化资源
						
							dataArr.push(busobj);
					}else{
						dataArr.push(busobj);
					}
				}else{
					if(that.resourceInfo.resourceType=='NetworkDevice'){//网络设备
						dataArr.push(controlPanelobj);
					}
				}
				
				//判断是否添加资产信息
				oc.util.ajax({
					url : oc.resource.getUrl('system/itsm/get.htm'),
					successMsg:null,
					async:false,
					success : function(resultData) {
						if(resultData.code == 200 && resultData.data.open == true){
							dataArr.push(assetInformation);
							that.cfg.itsmInfo = resultData.data;
							that.cfg.itsmInfo.instanceId = that.resourceInfo.instanceId;
							//查询WebServer  code
							oc.util.ajax({
								url : oc.resource.getUrl('system/itsm/getWebService.htm'),
								successMsg:null,
								async:false,
								success : function(resultData) {
									if(resultData.code == 200){
										var infoData = JSON.parse(resultData.data);
										that.cfg.itsmInfo.code = infoData.CODE;
									}
								} 
							});
						}
					} 
				});
	    		
	    	
	    		
	    		
			
			
			var nav=oc.index.nav=$(".oc-nav-menu").oc_nav({
		    	textField:'name',
		    	hrefFiled:'url',
		    	click:function(href,d,ds){
		    		//console.info($(".nav-actived"));
		    		$(".nav-actived").each(function(index,value){
		    			$(this).removeClass("nav-actived");
		    		});
		    		$(".oc-nav-menu").find("."+d.type).parent().parent().addClass("nav-actived");
		    		var contentDiv="";
		    		var url="";var jsFile="";
		    		if(d.type!='resources'){
		    			$(".tinytool").attr('style','display:none');
		    		}else{
		    			$(".tinytool").attr('style','display:block');
		    		}
	    			switch (d.type) {
		    		case 'resources' :       
		    		//	console.info("init all");
		    			url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/general.html');
		    					// 'resource/module/resource-management/resourceDetailInfo/js/general.js'
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/generalNew.js';
					case 'Process':
						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/ProcessMetricData.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/ProcessMetricData.js';
						break;
					case 'File':
						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/fileMetricData.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/fileMetricData.js';
						break;
					case 'LogicalVolume':
						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/logicalVolume.html');
					jsFile = 'resource/module/resource-management/resourceDetailInfo/js/logicalVolume.js';
						break;
					case 'VolumeGroup':
						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/volumeGroup.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/volumeGroup.js';
						break;
					case 'assetInformation':
						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/assetInformation.html');
						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/assetInformation.js';
//						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/volumeGroup.html');
//						jsFile = 'resource/module/resource-management/resourceDetailInfo/js/volumeGroup.js';
						break;
					default:
						url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html');
					    jsFile = 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js';
						break;
					}
		    		if(d.type=='resources'){
		    			url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/general.html');
    					// 'resource/module/resource-management/resourceDetailInfo/js/general.js'
		    			jsFile = 'resource/module/resource-management/resourceDetailInfo/js/generalNew.js';
		    			
		    		}else if(d.type=='metricGroup'){
		    			url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/metricGroup.html');
		    			jsFile = 'resource/module/resource-management/resourceDetailInfo/js/metricGroup.js';
		    		}
		    		
		    		else if(d.type=='alarm'){
		    			url = oc.resource.getUrl('resource/module/alarm-management/alarm-resource-list.html');
    					// 'resource/module/resource-management/resourceDetailInfo/js/general.js'
		    			jsFile = 'resource/module/alarm-management/js/alarm-resource-list.js';
		    		}
		    		
		    		else if(d.type=='business'){
		    			url = oc.resource.getUrl('resource/module/business-service/business_service_list.html');
		    			jsFile = 'resource/module/business-service/js/business_service_list.js';
		    		}
		    		
		    		else if(d.type=='controlPanel'){
		    			url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/controlNewPanel.html');
		    			jsFile = 'resource/module/resource-management/resourceDetailInfo/js/controlNewPanel.js';
		    		}
		    	
		    		var arg = {
							type : d.type,
							title : d.name,
							html : url,
							js : jsFile,
							instanceId : that.resourceInfo.instanceId,
							availability : that.resourceInfo.availability,
							content : $("div[id='"+d.type+"']"),
							isLoad : false,
							resourceType : that.resourceInfo.resourceType,
							resourceInfo:that.resourceInfo,
							instanceStatus:that.cfg.instanceStatus
						}
		    		$(".tabPanel").each(function(index,value){
						if($(this).attr("id")==d.type){
							$(this).css({"display":"block","width":"100%"});//attr("style","display:block;width:100%;");
							$(this).parent().css({"float":"left","width":"100%"})//attr("style","float:left;width:100%;");
				
							that.loadChildPanelSingle(arg, index);
			
						}else{
							$(this).css({"display":"none","width":"100%"});//attr("style","display:none;width:100%;");
							$(this).parent().css({"float":"left","width":"100%"})//attr("style","float:left;width:100%;");
					
						}
		
						
					});
		    		 $("div[id='"+d.type+"']").css({"display":"block","width":"100%"})//attr("style",'display: block;width:100%;height:100%;');
		    		
		    	},
		    	background:true,
		    	filter:function(datas){
		 
		    		var args=new Array();
		    		for(var i=0,len=datas.length,d;i<len;i++){
		    			d=datas[i];
		    			var classname="oc-panel-pics res-size-nav "+d.type;
		    			d.icon=classname;
		    			$(".main-resource").append("<div class='tabPanel' style='display:none;width:100%;height:100%;' id='"+d.type+"'></div>");	
		    			$(".main-resource").css({height:'86%'});
			
		    		}
		    
		    		return datas;
		    		
		    	},
		    	datas:dataArr
		    });
			nav._ul.find("li").each(function(){
				//$(this).css("float","left");
			});
			nav._ul.find('li:first').click();
			var spans=$(".nav-page span");
				spans.eq(0).addClass('fa fa-sort-up');
		//		
				spans.eq(1).addClass('fa fa-sort-down');
				
				$(".oc-nav").css('height','70px');
			
				if(nav._ul.find("li").length>12){
					$(".nav-page").css('display','block');
					spans.eq(0).removeClass('disabled');
					spans.eq(1).removeClass('disabled');
				}else{
					$(".nav-page").css('display','none');
				}
				spans.eq(0).click(function(){
					nav._ul.css('top','0px');
				});
				spans.eq(1).click(function(){
					nav._ul.css('top','-70px');
				});
		//	spans.eq(0).addClass('fa fa-sort-up');
		//	spans.eq(1).addClass('fa fa-sort-down');
				//$("#refBtnBlue").css("height",$(".oc-nav-menu").css("height"));
					$("#refBtnBlues").css("height",$(".oc-nav-menu").css("height"));
			//采集按钮
//		hand=true;
			if(hand==true){
				$('#refBtn').attr('style','display:block');
				$('#refBtnBlue').attr('style','display:none');
				}			
					$("#collectBtn").on('click',function(){
					
				if(hand==false){
					oc.util.ajax({
						  url: oc.resource.getUrl('portal/resource/resourceDetailInfo/getMetricHand.htm'),
						  data:{instanceId : that.resourceInfo.instanceId},
						  timeout:60000,
						  success:function(data){
							  if(data.data=="FALSE"){
								  alert("采集失败");
							  }else{
								  alert("正在采集指标，指标值将在3-5分钟后刷新，您可以离开此页面！");
									$('#refBtn').attr('style','display:block');
									$('#refBtnBlue').attr('style','display:none');
								  $('#collectBtn').unbind("click");
							  }
							  
						  }
					  });	
				
				}
			
			});
		},
	//onclick="collectResource()"
		setRefreshInterval : function(){

			var that = this;
			var hintNum = 0;
			if(that.timeId!=0){
				clearInterval(that.timeId);
			}
			this.timeId = setInterval(function(){
				if(hintNum != 0){
					var hintDlg = $("<div/>");
					hintDlg.append($("<div/>").addClass("messager-icon messager-warning").css('margin-top','20px').css('margin-left','10px'))
					.append($("<div/>").css('top', '23px').addClass("oc-notice").html("页面打开时间过长，是否刷新该页面？"));
					hintDlg.dialog({
						title : '温馨提示',
						width : '300px',
						height : '145px',
						buttons : [{
							text : '&nbsp;刷新&nbsp;',
							handler : function(){
							//	that.tabsDom = $("<div/>");
								that.tabsDom.empty();
								that.dlgDom.empty();
							/*	that.dlgDom.append(that.tabsDom);*/
								that.tabsDom = $("<div/>").css("background","#1d4170").height("65px");//.height("70px");
								var content=$("<div/>").addClass("main-resource").height("86%");/*.append("<div class='tabPanel' id='all' onclick=showAndHide()></div>");*/
								content.css("height","86%");
								///content.css({width:'100%',height:'100%'});
								//			this.tabsDom.append('<div class="cediv"></div>')
								that.dlgDom.append(that.tabsDom);
								that.dlgDom.append(content); 
								that._initTabs();
								//that._initResourceDetailInfo();
								hintDlg.dialog('close');
							}
						},{
							text : '&nbsp;取消&nbsp;',
							handler : function(){
								hintDlg.dialog('close');
							}
						}],
						onClose : function(){
							that.setRefreshInterval(that.resourceInfo);
						}
					});
					clearInterval(that.timeId);
				}
				hintNum ++ ;
			}, 50*60*1000);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(this.timeId);
			}else{
				tasks = new Array();
				tasks.push(this.timeId);
				oc.index.indexLayout.data("tasks", tasks);
			}
		
			
			
		},
		isHaveBiz:function(){
			var isHaveBizBl=false;
			oc.util.ajax({
				url : oc.resource.getUrl("portal/business/service/getBizListByInstanceId.htm"),
				data : {instanceId : this.cfg.instanceId},
				timeout : null,
				successMsg:null,
				async:false,
				success : function(d){
					if(d.data && d.data.length > 0){
						isHaveBizBl=true;
					}
				}
			});
			return isHaveBizBl;
		},
		loadChildPanelSingle : function(args, currentNo){//渲染子资源内容
			var that=this;
			var arg =args;//[currentNo];
			if(!arg.isLoad)
				arg.content.panel({
					height:'100%',
					href : arg.html,
					onLoad : function(){
					//	console.info(arg.js);
						oc.resource.loadScript(arg.js, function() {
							switch (arg.type) {
							case 'resources' :
							
								oc.module.resmanagement.resdeatilinfo.general.open(arg.resourceInfo,arg.instanceStatus);
								break;	
							case 'vm':
								oc.resourced.resource.detail.showmetric.openVMResource(arg.instanceId, arg.resourceId, arg.title ,arg.availability, arg.vminstanceIdList);
								break;
							case 'Process':
								oc.resourced.resource.detail.process.open(arg.instanceId, arg.availability);
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
								oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/controlNewPanel.js',function(){
									new controlNewPanelClass({node:arg.instanceId,downDeviceFlag:true});//downDeviceFlag:显示下联设备标志
								});
//								oc.resource.controlpanel.list.open(arg.instanceId,arg.resourceInfo);
								break;
							case 'metricGroup':
								oc.resource.metricgroup.list.open(arg.instanceId);
								break;
							case 'assetInformation':
								oc.resourced.resource.detail.asset.open(that.cfg.itsmInfo);
								break;
							default:
								oc.resourced.resource.detail.showmetric.open(arg.instanceId, arg.type, arg.title, arg.availability, arg.resourceType);
								break;
							}
							arg.isLoad = true;
						});
					}
				});
		
			
			
		},
		changeTabsHeadHeight : function(){},
		updateDlgTitle : function(newShowName){
			this.dlgDom.dialog('setTitle', "名称：" + newShowName.htmlspecialchars()+(this.resourceInfo.CollectState=="UNCOLLECTIBLE" ? "<span class='panel_pointsa'>（该设备无法采集，以下为快照信息！）</span>" : ""));
		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfonew');
	var detail = undefined;
	oc.module.resmanagement.resdeatilinfonew = {
		open : function(cfg) {
		//	detail = new detailInfo(cfg);
		//				detail.open();
			oc.util.ajax({
				url : oc.resource.getUrl("portal/resource/resourceDetailInfo/getDomainHave.htm"),
				data : {instanceid : cfg.instanceId},
				timeout : null,
				async:false,
				successMsg:null,
				success : function(json){
					if(json.data==1){
						detail = new detailInfo(cfg);
						detail.open();
					}else{
						alert("抱歉，没有资源权限！");
					}
				}
			});
		
		},
		updateDlgTitle : function(newShowName){
			if(detail != undefined){
				detail.updateDlgTitle(newShowName);
			}
		}
	}
});
function showAndHide(){
}