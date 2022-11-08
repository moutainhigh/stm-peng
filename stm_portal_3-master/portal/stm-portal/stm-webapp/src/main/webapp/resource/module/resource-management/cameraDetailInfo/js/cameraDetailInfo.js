//@ sourceURL=cameraDetailInfo.js
$(function() {
	var hand=false;
	//var childResourceHtml = oc.resource.getUrl('resource/module/resource-management/cameraDetailInfo/generalHostShowMetric.html');
	//var childResourceJs = 'resource/module/resource-management/cameraDetailInfo/js/generalHostShowMetric.js';

	function cameraDetailInfo(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
    cameraDetailInfo.prototype = {
		constructor : cameraDetailInfo,
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
			oc.util.ajax({
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
			});
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
			oc.resource.loadScript('resource/module/resource-management/cameraDetailInfo/js/dialog_standard_application.js',function(){
				oc.resource.management.dialog.standard.application.open({instanceId : that.resourceInfo.instanceId, callback:that.cfg.callback});
			 });
		},
		_initResourceDetailInfo : function(){
			var that = this;
			// 子资源分类
			this.dlgDom = $("<div/>");
			this.tabsDom = $("<div/>");
//			this.tabsDom.append('<div class="cediv"></div>')
			this.dlgDom.append(this.tabsDom);
			// 新建窗口
			var windowHeight = $(window).height(), dlgHeight = 632, dlgWidth = 1040;
			var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
			this.dlg = this.dlgDom.dialog({
				title : "名称：" + that.resourceInfo.name.htmlspecialchars()+(this.resourceInfo.CollectState=="UNCOLLECTIBLE" ? "<span class='panel_pointsa'>（该设备无法采集，以下为快照信息！）</span>" : ""),
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
			// 新建tabs
			this._initTabs();
			//
			// 定时提示器
			this.setRefreshInterval();
		},
		_initTabs : function(){
			var that = this;
			this.childrenType = that.resourceInfo.childrenType;
		var	hand=	that._initIsHandle();
			var detailType=this.cfg.type;//0資源 1虛擬化
			var instanceId=this.cfg.instanceId;//
			// 主资源页面和JS
			var html = oc.resource.getUrl('resource/module/resource-management/cameraDetailInfo/general.html');
			var js = 'resource/module/resource-management/cameraDetailInfo/js/general.js';
			var instanceStatus= this.cfg.instanceStatus;
			// 新建tab页
			var title = '';
			if(that.resourceInfo.resourceType == 'SignalDetection'){
				title = '信号检测';
			}
			this.tabs = this.tabsDom.addClass("window-tabsbg").tabs({
				fit : true,
				tabWidth : 71,
				tabHeight : 38,
				showHeader:false,
				scrollIncrement : 402//滚动长度
			//	display: none
			}).tabs('add', {
				title : title,
				selected : true,
				href : html,
				onLoad : function(){
				
					that.resourceInfo.callback = that.cfg.callback;
					oc.resource.loadScript(js, function(){
						oc.module.resmanagement.resdeatilinfom.general.open(that.resourceInfo);
					});
				}
			});
			// 调整tabs头的问题一定放在这个函数的最后
			this.changeTabsHeadHeight();//tabs-header
			//}
		},
	//onclick="collectResource()"
		setRefreshInterval : function(){
			var that = this;
			var hintNum = 0;
			this.timeId = setInterval(function(){
				if(hintNum != 0){
					var hintDlg = $("<div/>");
					hintDlg.append($("<div/>").addClass("messager-icon messager-warning"))
					.append($("<div/>").css('top', '23px').addClass("oc-notice").html("页面打开时间过长是否刷新该页面？"));
					hintDlg.dialog({
						title : '温馨提示',
						width : '300px',
						height : '145px',
						buttons : [{
							text : '&nbsp;刷新&nbsp;',
							handler : function(){
								that.tabsDom = $("<div/>");
								that.dlgDom.empty();
								that.dlgDom.append(that.tabsDom);
								that._initTabs();
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
			}, 5*60*1000);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(this.timeId);
			}else{
				tasks = new Array();
				tasks.push(this.timeId);
				oc.index.indexLayout.data("tasks", tasks);
			}
		},
		loadChildPanelSingle : function(args, currentNo){
			var arg = args[currentNo];
			if(!arg.isLoad)
				arg.content.panel({
					href : arg.html,
					onLoad : function(){
						oc.resource.loadScript(arg.js, function() {
							switch (arg.type) {
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
							default:
								oc.resourced.resource.detail.showmetric.open(arg.instanceId, arg.type, arg.title, arg.availability, arg.resourceType);
								break;
							}
							arg.isLoad = true;
						});
					}
				});
		},
		changeTabsHeadHeight : function(){
			var type=this.cfg.type;
			var childrenTypeCnt = this.childrenType.length;
			var oneTabsWidth = 151;
		
			var navWidth = 6;
			var navHeight = 44;
			var tabsHeight = this.tabsDom.height();
			if(type==0){
				navWidth=5;
			}
			var tabsHeadHeight = (childrenTypeCnt + 1) * oneTabsWidth + navWidth + 'px';
			if(childrenTypeCnt > 5){
				var tabsDom = this.tabsDom.find(".tabs-header > .tabs-wrap > .tabs").width(tabsHeadHeight);//
				this.tabsDom.find(".tabs-panels").height(tabsHeight - navHeight);
				this.tabsDom.find(".tabs-panels > .panel").height(tabsHeight - navHeight);
				this.tabsDom.find(".tabs-panels > .panel > .panel-body").height(tabsHeight - navHeight);
				this.tabsDom.find('.tabs-header > .tabs-scroller-left').css('display', 'none');
				if(type==1){
					this.tabsDom.find('.tabs-header > .tabs-scroller-right').css('display', 'none');
				}else{
					this.tabsDom.find('.tabs-header > .tabs-scroller-right').css('display', 'block');
				}
			}
			
		},
		updateDlgTitle : function(newShowName){
			this.dlgDom.dialog('setTitle', "名称：" + newShowName.htmlspecialchars()+(this.resourceInfo.CollectState=="UNCOLLECTIBLE" ? "<span class='panel_pointsa'>（该设备无法采集，以下为快照信息！）</span>" : ""));
		}
	}
	oc.ns('oc.module.resmanagement.cameradeatilinfo');
	var detail = undefined;
	oc.module.resmanagement.cameradeatilinfo = {
		open : function(cfg) {
			detail = new cameraDetailInfo(cfg);
			detail.open();
		},
		updateDlgTitle : function(newShowName){
			if(detail != undefined){
				detail.updateDlgTitle(newShowName);
			}
		}
	}
});