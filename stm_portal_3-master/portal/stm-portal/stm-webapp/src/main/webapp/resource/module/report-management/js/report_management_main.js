$(function () {
	oc.ns('oc.report.management.main');
	//定义左侧菜单栏,用于动态添加元素
	var performances,alarmCounts,TOPNReports,availableReports,multipleReports,trendReports,analyseReports,businessReports;
	var menuDiv,centerDiv,indexDiv,reportMenuTree;
	var isCreateMenu = false;
	
	var busRep;
	oc.util.ajax({
		url:oc.resource.getUrl('portal/report/reportTemplateList/busRep.htm'),
		success:function(data){
			busRep = data.data;
			init();
		}
	});
	
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
		
		//初始化右键menu
		$('#rightButtonMenu_report').menu({
			onClick:function(item){
				var node = $(item.target).data('node');
				switch (item.text) {
				case '编辑':
					oc.resource.loadScript('resource/module/report-management/js/report_template_detail.js', function(){
						  oc.module.reportmanagement.report.template.detail.update(node.id);
					});
					break;
				case '删除':
					var dialogSetIds = $('<div/>');
					dialogSetIds.dialog({
					    title: '确认删除',
					    width : 300,
						height : 140,
						buttons:[{
					    	text: '确定',
					    	handler:function(){
					    		isCreateMenu = false;
					    		var value = dialogSetIds.find(":checked").attr('value');
					    		
								oc.util.ajax({
									url:oc.resource.getUrl('portal/report/reportTemplateList/delReportTemplateById.htm'),
									data:{reportTemplateId:node.id,removeType:value},
									success:function(data){
										reportMenuTree.tree('reload');
									}
								})
								
					    		dialogSetIds.dialog('destroy');
					    	}
					    },{
					    	text: '取消',
					    	handler:function(){
					    		dialogSetIds.dialog('destroy');
					    	}
					    }],
					});
					var checkBoxDiv = $('<div/>').append('<table><tr><td style="padding-top:10px;padding-left:10px;"><input style="vertical-align:middle;" type="radio" checked="true" name="removeReport" value="1"/></td><td style="padding-top:10px;padding-left:10px;">只删除模板</tr></td>'+
							'<tr><td style="padding-top:10px;padding-left:10px;"><input style="vertical-align:middle;" type="radio" name="removeReport" value="2"/></td><td style="padding-top:10px;padding-left:10px;">删除模板及所属报告</td></tr></table>');
					
					dialogSetIds.append(checkBoxDiv);
					dialogSetIds.find(':checkbox').on('click',function(e){
						dialogSetIds.find(':checkbox').each(function(){
							$(this).attr('checked',false);
						})
						$(this).attr('checked',true);
					})
					break;
				default:
					break;
				}
			},
		});
		
		//让顶部 报表管理标签高亮   性能报告高亮
		indexDiv.find('#addGroupButton').parent().parent().addClass('oc-accordion-selected oc-accordion-selected-report');
		
		centerDiv=indexDiv.layout('panel','center');
		
		indexDiv.find('#addGroupButton').on('click', function(){ 
			oc.resource.loadScript('resource/module/report-management/js/report_template_detail.js', function(){
				  oc.module.reportmanagement.report.template.detail.open();
			  });
	     });
		
		//菜单树
		reportMenuTree = $('#reportMainMenu').tree({
			url: oc.resource.getUrl('portal/report/reportTemplateList/getReportList.htm?dataType=json'),
			fit: true,
			animate: true,
			lines: true,
			loadFilter: function(data){
				if(isCreateMenu){
					return data;
				}
				
				var dataArray = null;
				if(Array.isArray(data)){
					dataArray = data;
				}else{
					dataArray = data.data;
				}
				
				var clickDetailUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_detail.html');
				var clickDownloadUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_download.html');
				var performanceArr={id:1,text:'性能报告',state:'open',attributes:{href:clickDetailUrl}},
					alarmCountArr={id:2,text:'告警统计',state:'open',attributes:{href:clickDetailUrl}},
					TOPNReportArr={id:3,text:'TOPN报告',state:'open',attributes:{href:clickDetailUrl}},
					availableReportArr={id:4,text:'可用性报告',state:'open',attributes:{href:clickDetailUrl}},
					trendReportArr={id:5,text:'趋势报告',state:'open',attributes:{href:clickDetailUrl}},
					analyseReportArr={id:6,text:'分析报告',state:'open',attributes:{href:clickDetailUrl}},
					businessReportArr={id:7,text:'业务报告',state:'open',attributes:{href:clickDetailUrl}},
					multipleReportArr={id:100,text:'综合报告',state:'open',attributes:{href:clickDetailUrl}},
					arr = [];
				
				for(var i=0;i<dataArray.length;i++){
					var attributes = {      
							reportTemplateId : dataArray[i].reportTemplateId,
							reportTemplateCreateUser : dataArray[i].reportTemplateCreateUserName,
							reportTemplateCreateTime : dataArray[i].reportTemplateCreateTime,
							reportTemplateName : dataArray[i].reportTemplateName,
							reportTemplateType : dataArray[i].reportTemplateType,
							reportTemplateCycle : dataArray[i].reportTemplateCycle,
							reportTemplateStatus : dataArray[i].reportTemplateStatus,
							href: clickDownloadUrl
					};
					var node = {
							id:dataArray[i].reportTemplateId,
							text:dataArray[i].reportTemplateName,
							attributes:attributes,
					};
					
					var reportTemplateNameStr = dataArray[i].reportTemplateName;
					
					switch (dataArray[i].reportTemplateType) {
					case 1:
						performanceArr.state = 'closed';
						if(!performanceArr.children){
							performanceArr.children = [];
						}
						performanceArr.children.push(node);
						break;
					case 2:
						alarmCountArr.state = 'closed';
						if(!alarmCountArr.children){
							alarmCountArr.children = [];
						}
						alarmCountArr.children.push(node);
						break;
					case 3:
						TOPNReportArr.state = 'closed';
						if(!TOPNReportArr.children){
							TOPNReportArr.children = [];
						}
						TOPNReportArr.children.push(node);
						break;
					case 4:
						availableReportArr.state = 'closed';
						if(!availableReportArr.children){
							availableReportArr.children = [];
						}
						availableReportArr.children.push(node);
						break;
					case 5:
						trendReportArr.state = 'closed';
						if(!trendReportArr.children){
							trendReportArr.children = [];
						}
						trendReportArr.children.push(node);
						break;
					case 6:
						analyseReportArr.state = 'closed';
						if(!analyseReportArr.children){
							analyseReportArr.children = [];
						}
						analyseReportArr.children.push(node);
						break;
					case 7:
						businessReportArr.state = 'closed';
						if(!businessReportArr.children){
							businessReportArr.children = [];
						}
						businessReportArr.children.push(node);
						break;
					case 100:
						multipleReportArr.state = 'closed';
						if(!multipleReportArr.children){
							multipleReportArr.children = [];
						}
						multipleReportArr.children.push(node);
						break;
					}
				}
				arr.push(performanceArr);
				arr.push(alarmCountArr);
				arr.push(TOPNReportArr);
				arr.push(availableReportArr);
				arr.push(trendReportArr);
				arr.push(analyseReportArr);
				if(busRep!=0){	//判断是否应该包含业务报表
					arr.push(businessReportArr);
				}
				arr.push(multipleReportArr);
				isCreateMenu = true;
				return arr;
			},
			onLoadSuccess: function(node,data){
				var firstNode = reportMenuTree.tree('find',data[0].id);
				$(firstNode.target).click();
			},
			onClick: function(node){
				var id = node.id;
				var dataTemp ;
				switch (id) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 100:
					var TemplateIdArr = getTemplateIdArrByType(node);
					dataTemp = {name:node.text, type:node.id, tempIdArr:TemplateIdArr};
					reportMainMenuClick(node.attributes.href,dataTemp);
					break;
				default:	//二级菜单
					clickItem(node.attributes,node.attributes.href);
					break;
				}
			},
			onContextMenu: function(e, node){
				//修改选中状态
				var selectedNode = $('#reportMainMenu').tree('getSelected');
				$(selectedNode.target).removeClass('tree-node-selected');
				$(node.target).addClass('tree-node-selected');
				
				//取消浏览器右键菜单
				e.preventDefault();
				
				//右键菜单赋值
				$('#rightButtonMenu_report').find('#menu_edit').data('node',node);
				$('#rightButtonMenu_report').find('#menu_del').data('node',node);
				
				//只有二级菜单才有右键菜单
				if(node.attributes.reportTemplateId){
					$('#rightButtonMenu_report').menu('show',{
						left:e.pageX,
						top:e.pageY
					});
//					$(document).unbind(".interface-eq");
				}
			}
		});
		
		//获取templateIdArr以便通过templateType查询
		function getTemplateIdArrByType(infoArr){
			var templateIdArr = new Array();
			for(var index = 0;infoArr.children && index<infoArr.children.length;index++){
				templateIdArr.push(infoArr.children[index].id);
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
			title:data.reportTemplateName,
			href:data.href,
			onLoad:function(){
				oc.resource.loadScript('resource/module/report-management/js/report_main_center_download.js',function(){
					oc.report.main.center.download.open(data);
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
			var reportTemplateNameStr = reportTemplateName;
			
			var node = {
					id:reportTemplateId,
					text:reportTemplateName,
					attributes:{reportTemplateId:reportTemplateId,reportTemplateName:reportTemplateName,reportTemplateType:type,href: clickDownloadUrl},
			}
			
			switch (type) {
			case 1:
				var dom = $('#reportMainMenu').tree('find', 1);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 2:
				var dom = $('#reportMainMenu').tree('find', 2);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 3:
				var dom = $('#reportMainMenu').tree('find', 3);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 4:
				var dom = $('#reportMainMenu').tree('find', 4);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 5:
				var dom = $('#reportMainMenu').tree('find', 5);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 6:
				var dom = $('#reportMainMenu').tree('find', 6);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 7:
				var dom = $('#reportMainMenu').tree('find', 7);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			case 100:
				var dom = $('#reportMainMenu').tree('find', 100);
				$('#reportMainMenu').tree('append', {
					parent: dom.target,
					data:node
				});
				break;
			}
		},
		editNavsublistItem:function(reportTemplateId,reportTemplateName){
			isCreateMenu = false;
			reportMenuTree.tree('reload');
		},
		reportMainMenuClick:function(href,data){
			reportMainMenuClick(href,data);
		}
	};
	
});