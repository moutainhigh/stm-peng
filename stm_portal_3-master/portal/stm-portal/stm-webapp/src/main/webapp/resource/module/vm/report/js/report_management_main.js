$(function () {
	oc.ns('oc.report.management.main');
	//定义左侧菜单栏,用于动态添加元素
	var performances,alarmCounts,TOPNReports,availableReports,multipleReports,trendReports,analyseReports,businessReports;
	var menuDiv,centerDiv,indexDiv;
	init();
	
function init(){
	var id=oc.util.generateId();
	indexDiv=$('.report_management').attr('id',id);
	var curWillOpenIndex = -1;
	
	menuDiv = indexDiv.layout().find('.easyui-accordion').accordion({
		//设置menu展开规则
		onBeforeUnSelect:function(title,index){
			
			if(curWillOpenIndex != index && curWillOpenIndex != -1){
				curWillOpenIndex = -1;
				return true;
			}else if(curWillOpenIndex == -1){
				var pp = indexDiv.find('.easyui-accordion').accordion('getSelected');
				var selectIndex;
				if(pp){
					selectIndex = indexDiv.find('.easyui-accordion').accordion('getPanelIndex', pp);
				}
				if(selectIndex == index){
					return false;
				}
			}
			return true;
		   
		},
		onBeforeSelect:function(title,index){
			curWillOpenIndex = index;
			
			return true;
		},
		onSelect:function(title,index){
			return true;
		}
	});
	
	//让顶部 报表管理标签高亮   性能报告高亮
	indexDiv.find('#addGroupButton').parent().parent().addClass('oc-accordion-selected');
	indexDiv.find('.panel-title').each(function(){
		var target = $(this);
		if(target.html()=='性能报告'){
			target.parent().addClass('accordion-header-selected');
		}
	})
	
	centerDiv=indexDiv.layout('panel','center');
	
	indexDiv.find('#addGroupButton').on('click', function(){ 
		oc.resource.loadScript('resource/module/report-management/js/report_template_detail.js', function(){
			  oc.module.reportmanagement.report.template.detail.open();
		  });
     });
	
	indexDiv.find('.l-btn-icon').hide();
	indexDiv.find('.ico-set').on('click',function(){
		if(menuDiv.find('.l-btn-icon').css('display')=='block'){
			menuDiv.find('.l-btn-icon').hide();
		}else{
			menuDiv.find('.l-btn-icon').show();
		}
	})
	
	function _click(href,data,e){
//		event.stopPropagation();
//		centerDiv.children().remove();
		var clickTagClass = $(e.target).attr('class');
		var templateThisId = $(e.target).attr('name');
		var type = $(e.target).attr('type');
		
		
		switch (clickTagClass) {
		case "l-btn-icon fa fa-trash-o oc-icomargin":
			oc.ui.confirm('确认删除报表模板?',function(){
				var clickUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_detail.html');
				oc.util.ajax({
					url:oc.resource.getUrl('portal/report/reportTemplateList/delReportTemplateById.htm'),
					data:{reportTemplateId:templateThisId},
					success:function(data){
						//在菜单中去除该栏,右侧菜单变更,刷新
						var detailCenterTemplateIdArr;
						switch (type) {
						case "1":
							reportMainMenuClick(clickUrl,{name:"性能报告",type:1});
							break;
						case "2":
							reportMainMenuClick(clickUrl,{name:"告警统计",type:2});
							break;
						case "3":
							reportMainMenuClick(clickUrl,{name:"TOPN报告",type:3});
							break;
						case "4":
							reportMainMenuClick(clickUrl,{name:"可用性报告",type:4});
							break;
						case "5":
							reportMainMenuClick(clickUrl,{name:"趋势报告",type:5});
							break;
						case "6":
							reportMainMenuClick(clickUrl,{name:"分析报告",type:6});
							break;
						case "7":
							reportMainMenuClick(clickUrl,{name:"业务报告",type:7});
							break;
						case "100":
							reportMainMenuClick(clickUrl,{name:"综合报告",type:100});
							break;
						}
						$(e.target).parent().parent().remove();
					}
				})
			},function(){})
			break;
		case "l-btn-icon icon-edit oc-icomargin":
			oc.resource.loadScript('resource/module/report-management/js/report_template_detail.js', function(){
				  oc.module.reportmanagement.report.template.detail.update(templateThisId);
			  });
			break;
		case "l-btn-icon icon-mark oc-icomargin":
			var reportTypeNameTemp;
			switch (type) {
			case "1":
				reportTypeNameTemp = "性能报告";
				break;
			case "2":
				reportTypeNameTemp = "告警统计";
				break;
			case "3":
				reportTypeNameTemp = "TOPN报告";
				break;
			case "4":
				reportTypeNameTemp = "可用性报告";
				break;
			case "5":
				reportTypeNameTemp = "趋势报告";
				break;
			case "6":
				reportTypeNameTemp = "分析报告";
				break;
			case "7":
				reportTypeNameTemp = "业务报告";
				break;
			case "100":
				reportTypeNameTemp = "综合报告";
				break;
			}
			var reportNameTemp ; 
			$(e.target).parent().parent().find('[name="titleName-'+templateThisId+'"]').each(function(){
				reportNameTemp = $(this).html();
			});
			oc.resource.loadScript('resource/module/report-management/js/resourcesOutlook.js',function(){
				oc.module.reportmanagement.report.rol
						.openPreview({
							reportTemplateId : templateThisId,
							reportName : reportNameTemp,
							reportTypeCode : type,
							reportTypeName : reportTypeNameTemp
						});
			});

			break;
	
		default:
			clickItem(data,href);
			break;
		}

		
	}
	
	
	
	var performanceArr=new Array(),
		alarmCountArr=new Array(),
		TOPNReportArr=new Array(),
		availableReportArr=new Array(),
		multipleReportArr=new Array(),
		trendReportArr=new Array(),
		analyseReportArr=new Array(),
		businessReportArr=new Array();
	
	var clickDetailUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_detail.html');
	var clickDownloadUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_download.html');
	oc.util.ajax({
		url:oc.resource.getUrl('portal/report/reportTemplateList/getReportList.htm'),
		data:{type:1},
		success:function(data){
			for(var i=0;i<data.data.length;i++){
				
				var dataOption = {      
						reportTemplateId : data.data[i].reportTemplateId,
						reportTemplateCreateUser : data.data[i].reportTemplateCreateUserName,
						reportTemplateCreateTime : data.data[i].reportTemplateCreateTime,
						reportTemplateName : data.data[i].reportTemplateName,
						reportTemplateType : data.data[i].reportTemplateType,
						reportTemplateCycle : data.data[i].reportTemplateCycle,
						reportTemplateStatus : data.data[i].reportTemplateStatus
				};
				
				var reportTemplateNameStr = data.data[i].reportTemplateName;
//				if(getStrLength(data.data[i].reportTemplateName)>24){
//					reportTemplateNameStr = data.data[i].reportTemplateName.substring(0,12)+'...';
//				}else{
//					reportTemplateNameStr = data.data[i].reportTemplateName;
//				}
				//<span type="'+data.data[i].reportTemplateType+'" name="'+data.data[i].reportTemplateId+'" class="l-btn-icon fa fa-trash-o oc-icomargin" style="display:none;"></span><span type="'+data.data[i].reportTemplateType+'" name="'+data.data[i].reportTemplateId+'" class="l-btn-icon icon-edit oc-icomargin" style="display:none;"></span><span type="'+data.data[i].reportTemplateType+'" name="'+data.data[i].reportTemplateId+'" class="l-btn-icon icon-mark oc-icomargin" style="display:none;"></span>',
				var option = {
						href:clickDownloadUrl,
						name:'<span class="oc-text-ellipsis" style="width:85%;" name="titleName-'+data.data[i].reportTemplateId+'" title="'+data.data[i].reportTemplateName+'" type='+data.data[i].reportTemplateType+' >'+reportTemplateNameStr+'</span>',//title="'+data.data[i].reportTemplateName+'"
						dataOption:dataOption
					};
				
				switch (data.data[i].reportTemplateType) {
				case 1:
					performanceArr.push(option);
					break;
				case 2:
					alarmCountArr.push(option);
					break;
				case 3:
					TOPNReportArr.push(option);
					break;
				case 4:
					availableReportArr.push(option);
					break;
				case 5:
					trendReportArr.push(option);
					break;
				case 6:
					analyseReportArr.push(option);
					break;
				case 7:
					businessReportArr.push(option);
					break;
				case 100:
					multipleReportArr.push(option);
					break;
				}
			}
			
			performances=oc.ui.navsublist({
				selector:indexDiv.find('.performance'),
				data:performanceArr,
				click:_click
			});
			
			alarmCounts=oc.ui.navsublist({
				selector:indexDiv.find('.alarmCount'),
				data:alarmCountArr,
				click:_click
			});
			
			TOPNReports=oc.ui.navsublist({
				selector:indexDiv.find('.TOPNReport'),
				data:TOPNReportArr,
				click:_click
			});
			
			availableReports=oc.ui.navsublist({
				selector:indexDiv.find('.availableReport'),
				data:availableReportArr,
				click:_click
			});
			
			multipleReports=oc.ui.navsublist({
				selector:indexDiv.find('.multipleReport'),
				data:multipleReportArr,
				click:_click
			});
			
			trendReports=oc.ui.navsublist({
				selector:indexDiv.find('.trendReport'),
				data:trendReportArr,
				click:_click
			});
			
			analyseReports=oc.ui.navsublist({
				selector:indexDiv.find('.analyseReport'),
				data:analyseReportArr,
				click:_click
			});
			
			businessReports=oc.ui.navsublist({
				selector:indexDiv.find('.businessReport'),
				data:businessReportArr,
				click:_click
			});
			
			//绑定报表模板操作列
//			menuDiv.find('.l-btn-icon').on('click',function(event){
//				event.stopPropagation();
//				var templateTemp = $(this);
//				var templateThisId = templateTemp.attr('name');
//				switch (templateTemp.attr('class')) {
//				case "l-btn-icon fa fa-trash-o oc-icomargin":
//					
//					oc.util.ajax({
//						url:oc.resource.getUrl('portal/report/reportTemplateList/delReportTemplateById.htm'),
//						data:{reportTemplateId:templateThisId},
//						success:function(data){
//							//在菜单中去除该栏,右侧菜单变更
//							var detailCenterTemplateIdArr;
//							switch (templateTemp.attr('type')) {
//							case "1":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(performanceArr);
//								reportMainMenuClick(clickUrl,{name:"性能报告",type:1,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							case "2":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(alarmCountArr);
//								reportMainMenuClick(clickUrl,{name:"告警统计",type:2,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							case "3":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(TOPNReportArr);
//								reportMainMenuClick(clickUrl,{name:"TOPN报告",type:3,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							case "4":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(availableReportArr);
//								reportMainMenuClick(clickUrl,{name:"可用性报告",type:4,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							case "5":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(trendReportArr);
//								reportMainMenuClick(clickUrl,{name:"趋势报告",type:5,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							case "6":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(analyseReportArr);
//								reportMainMenuClick(clickUrl,{name:"分析报告",type:6,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							case "7":
//								detailCenterTemplateIdArr =  getTemplateIdArrByType(multipleReportArr);
//								reportMainMenuClick(clickUrl,{name:"综合报告",type:7,tempIdArr:detailCenterTemplateIdArr});
//								break;
//							}
//							templateTemp.parent().parent().remove();
//						}
//					})
//					break;
//				case "l-btn-icon icon-edit oc-icomargin":
//					oc.resource.loadScript('resource/module/report-management/js/report_template_detail.js', function(){
//						  oc.module.reportmanagement.report.template.detail.update(templateThisId);
//					  });
//					break;
//				case "l-btn-icon icon-mark oc-icomargin":
//					oc.resource.loadScript('resource/module/report-management/js/resourcesOutlook.js',function(){
//						oc.module.reportmanagement.report.rol
//								.openPreview({
//									report_list_id : 7162612,
//									reportName : '集团总部交换机性能概况',
//									reportType : '性能报告'
//								});
//					});
//
//					break;
//				}
//			})
			
			//默认加载center页
			var detailCenterTemplateIdArr =  getTemplateIdArrByType(performanceArr);
			reportMainMenuClick(clickDetailUrl,{name:"性能报告",type:1,tempIdArr:detailCenterTemplateIdArr});
		}
	});
	
	
	
	indexDiv.find('.accordion-header').on('click',function(){
		var str = $(this).find('.panel-title').html();
		var dataTemp ;
		
		switch (str) {
		case "性能报告":
			var perforArr = getTemplateIdArrByType(performanceArr);
			dataTemp = {name:str,type:1,tempIdArr:perforArr}; 
			break;
		case "告警统计":
			var alarArr = getTemplateIdArrByType(alarmCountArr);
			dataTemp = {name:str,type:2,tempIdArr:alarArr}; 
			break;
		case "TOPN报告":
			var topArr = getTemplateIdArrByType(TOPNReportArr);
			dataTemp = {name:str,type:3,tempIdArr:topArr}; 
			break;
		case "可用性报告":
			var avaiArr = getTemplateIdArrByType(availableReportArr);
			dataTemp = {name:str,type:4,tempIdArr:avaiArr}; 
			break;
		case "趋势报告":
			var treArr = getTemplateIdArrByType(trendReportArr);
			dataTemp = {name:str,type:5,tempIdArr:treArr}; 
			break;
		case "分析报告":
			var analyArr = getTemplateIdArrByType(analyseReportArr);
			dataTemp = {name:str,type:6,tempIdArr:analyArr}; 
			break;
		case "业务报告":
			var bussArr = getTemplateIdArrByType(businessReportArr);
			dataTemp = {name:str,type:7,tempIdArr:bussArr}; 
			break;
		case "综合报告":
			var multiArr = getTemplateIdArrByType(multipleReportArr);
			dataTemp = {name:str,type:100,tempIdArr:multiArr}; 
			break;
		}
		reportMainMenuClick(clickDetailUrl,dataTemp);
	})
	
	
	//获取templateIdArr以便通过templateType查询
	function getTemplateIdArrByType(infoArr){
		var templateIdArr = new Array();
		for(var index = 0;index<infoArr.length;index++){
			templateIdArr.push(infoArr[index].dataOption.reportTemplateId);
		}
		return templateIdArr;
	}
	}

	//将字符串内中文换算为两个长度
	function getStrLength(str){
		var strNew = str.replace(/([^\u0000-\u00FF])/g, function ($) { return escape('--'); });
		return strNew.length;
	}
	function reportMainMenuClick(href,data){
		//去除选中状态的标签
		menuDiv.find('li[class="active"]').each(function(){
			$(this).removeClass('active');
		});
		
		centerDiv.children().remove();
		$('<div style="width:100%;height:100%;"></div>').appendTo(centerDiv).panel('RenderP',{
		title:data.name,
		href:href,
		onLoad:function(){
			oc.resource.loadScript('resource/module/report-management/js/report_main_center_detail.js',function(){
					oc.report.main.center.detail.openWithTemplateType(data);
				});
			}
		});
	}
	function clickItem(data,href){
		centerDiv.children().html('');
		centerDiv.children().remove();
		$('<div style="width:100%;height:100%;"></div>').appendTo(centerDiv).panel('RenderP',{
			title:menuDiv.find('[name="titleName-'+data.dataOption.reportTemplateId+'"]').attr('title'),
			href:href,
			onLoad:function(){
				oc.resource.loadScript('resource/module/report-management/js/report_main_center_download.js',function(){
//				oc.resource.loadScript('resource/module/report-management/js/report_main_center_detail.js',function(){
					oc.report.main.center.download.open(data.dataOption);
//					oc.report.main.center.detail.open(data.dataOption);
				});
			}
		 });
	}

	/**
	 * 提供给外部引用的接口
	 */
	oc.report.management.main={
		open:function(){
			init();
		},
		addNavsublistItem:function(type,reportTemplateId,reportTemplateName){
			var clickDetailUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_detail.html');
			var clickDownloadUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_download.html');
			//<span type="'+type+'" name="'+reportTemplateId+'" class="l-btn-icon fa fa-trash-o oc-icomargin" style="display:none;"></span><span type="'+type+'" name="'+reportTemplateId+'" class="l-btn-icon icon-edit oc-icomargin" style="display:none;"></span><span type="'+type+'" name="'+reportTemplateId+'" class="l-btn-icon icon-mark oc-icomargin" style="display:none;"></span>',
			var reportTemplateNameStr = reportTemplateName;
//			if(getStrLength(reportTemplateName)>24){
//				reportTemplateNameStr = reportTemplateName.substring(0,12)+'...';
//			}else{
//				reportTemplateNameStr = reportTemplateName;
//			}
			var liData = {
					href:clickDownloadUrl,
					name:'<span class="oc-text-ellipsis" style="width:85%;" name="titleName-'+reportTemplateId+'" title="'+reportTemplateName+'">'+reportTemplateNameStr+'</span>',
					dataOption:{reportTemplateId:reportTemplateId,reportTemplateName:reportTemplateName,reportTemplateType:type}
			};
			switch (type) {
			case 1:
				performances.add(liData);
				break;
			case 2:
				alarmCounts.add(liData);
				break;
			case 3:
				TOPNReports.add(liData);
				break;
			case 4:
				availableReports.add(liData);
				break;
			case 5:
				trendReports.add(liData);
				break;
			case 6:
				analyseReports.add(liData);
				break;
			case 7:
				businessReports.add(liData);
				break;
			case 100:
				multipleReports.add(liData);
				break;
			}
		},
		editNavsublistItem:function(reportTemplateId,reportTemplateName){
			var reportTemplateType;
			menuDiv.find('[name="titleName-'+reportTemplateId+'"]').each(function(){
				$(this).html(reportTemplateName);
				$(this).attr('title',reportTemplateName)
				reportTemplateType = $(this).attr('type');
			});
			
			var dataOption = {
					reportTemplateId : reportTemplateId,
					reportTemplateName : reportTemplateName,
					reportTemplateType : parseInt(reportTemplateType)
			};
			var data = {dataOption:dataOption};
			var clickDownloadUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_download.html');
			clickItem(data,clickDownloadUrl);
			
		},
		reportMainMenuClick:function(href,data){
			reportMainMenuClick(href,data);
		}
	};
	
});