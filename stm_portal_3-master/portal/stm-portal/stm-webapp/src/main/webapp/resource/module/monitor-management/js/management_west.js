$(function () {
	$('#oc_resource_index').layout();
	// 命名空间
	oc.ns('oc.module.monitormanagement.resource.west');
	
	// 缓存第一个资源实例数据
	var lastClickIsShowInsance = false;
	
	var isFirstLoad = true;
	
	var isProgramOpenAccordion = false;
	
	var lastOpenAccordionName = null;
	
	var defaultSearchContent = '搜索资源名称|IP';
	
	var cameraTotalCount;//摄像头总数
	
	// 是否是普通用户
	var isCommonUser = false;
	
	// 是否新添加自定义资源组
	var isJustAddResourceGroup = false;
	
	var accordionCategoryUlList = new Array();
	
	// 记录当前点击的资源组ID
	var curShowResourceGroupId = -1;
	var customContent = $('#customGroupAccordion');
	var categoryAccordion = $('#resourceCategroyAccordion');
	var resourceAccordionParentPanel = $(".resourceAccordionParentPanel");
	
	var mainAccordion = $('#mainAccordionDivId').accordion({
		onBeforeSelect:function(title, index){
			if(index != 0){
				curShowResourceGroupId = -1;
			}
			if(index == 0){
				// 点击资源组
				if(!isFirstLoad){
					if(customGroupAccordion.accordion('panels').length > 0){
						customContent.find(".tree-node").removeClass("tree-node-selected");
						var selectPanel = customGroupAccordion.accordion('getSelected');
						
						if(selectPanel){
							var panelTitle = selectPanel.parent().find(".panel-title");
							clickCustomGroupTreeToShowGrid(panelTitle.find("> div").attr('customGroupId'), panelTitle.find(".custom-widthno").html(), panelTitle.find(".s-digit").attr('value'));
						}else{
							if(lastClickIsShowInsance){
								oc.monitor.manager.list.removeGroupDataGrid(0);
							}else{
								createListGrid(null,null,0);
							}
						}
					}else{
						if(lastClickIsShowInsance){
							oc.monitor.manager.list.removeGroupDataGrid(0);
						}else{
							createListGrid(null,null,0);
						}
					}
					lastClickIsShowInsance = true;
				}else{
					isFirstLoad = false;
				}
			}
			 if(index == 1){
				//点击资源组
				var selectPanel = categoryAccordion.accordion('getSelected');
				if(selectPanel){
					var nowSelectIndex = categoryAccordion.accordion('getPanelIndex', selectPanel);
					if(nowSelectIndex == 0){
						categoryAccordion.find('li').removeClass('active');
						queryResourceInstanceByCategory(selectPanel.find('ul').attr('categoryname'));
					}else{
						selectPanel.css('display','none');
						categoryAccordion.accordion('select',0);
					}
				}else{
					if(categoryAccordion.accordion('panels').length > 0){
						categoryAccordion.accordion('select',1);
					}else{
						createListGrid(null,null,1);
					}
				}
			}
			this.selectedIdx=index;
			
			return true;
		},
		onBeforeUnSelect:function(title,index){
			if((typeof this.selectedIdx=='number')&&index==this.selectedIdx){
				return false;
			}
			return true;
		}
	});
	
	if(oc.index.getUser().commonUser && !oc.index.getUser().domainUser){
		//普通用户登录,没有策略页签
		mainAccordion.accordion('remove',2);
		isCommonUser = true;
	}
	
	
	// 新需求新建自定义资源组
	var customGroupAccordion;
	function initCustomGroupAccordion(){
		customGroupAccordion = customContent.accordion({
			fit : true,
			onSelect : function(title, index){
				var panel = mainAccordion.accordion('getSelected');
				var index = mainAccordion.accordion('getPanelIndex', panel);
				if(index == 0){
					//var id =$(".customGroupTreeTitle").eq(index).attr('customGroupId');
					var id = $(title).attr('customGroupId');
					//var id = "999999";
					var name = $($(title).find("span").get(0)).html();
					//var name = "视频质量诊断";
					var resourceIds = $(title).find(".s-digit").attr('value');
					//var resourceIds =$(".customGroupTreeTitle").eq(index).attr('value');
					//$(".tree-node").eq(index).addClass('tree-node-selected');
					clickCustomGroupTreeToShowGrid(id, name, resourceIds);
					// 取消所有树节点的选中状态
					//customContent.find(".tree-node").removeClass("tree-node-selected");
				}
				// 先取消所有title的点击事件 再给title加上点击事件
				/*customContent.find(".panel-title").each(function(){
					$(this).unbind("click");
				});*/
				$(title).parent().on('click', function(e){
					customContent.find(".tree-node").removeClass("tree-node-selected");
					var id = $(title).attr('customGroupId');
					var name = $($(title).find("span").get(0)).html();
					var resourceIds = $(title).find(".s-digit").attr('value');
					clickCustomGroupTreeToShowGrid(id, name, resourceIds);
				});
			}
		});
	}

	initCustomGroupAccordion();
	
	
	// 最后一次点击的是显示资源实例，还是显示其他的
	var curIsHaveCustomGroup = false;
	// 记录当前是否点击操作按钮
	var isOpenOperation = false;
	
	categoryAccordion.accordion({
		
		animate:true,
		onBeforeSelect:function(title,index){
			//发送请求获取资源类别
			categoryAccordion.find('li').removeClass('active');
			queryResourceInstanceByCategory(title);
			this.selectedIdx=index;
			return true;
		},
		onBeforeUnSelect:function(title,index){
			if((typeof this.selectedIdx=='number')&&index==this.selectedIdx){
				//发送请求获取资源类别
				categoryAccordion.find('li').removeClass('active');
				queryResourceInstanceByCategory(title);
				return false;
			}
			return true;
		}
	});
	
	function queryResourceInstanceByCategory(title){
		
		if(isProgramOpenAccordion){
			return;
		}
		lastOpenAccordionName = title;
		 var categoryId = categoryAccordion.find('ul[categoryname="' + title + '"]').attr('categoryid');
		 var queryData = {};
	     var titleInfo = {};
	     queryData.categoryIds = categoryId;
		 titleInfo.id = categoryId;
		 titleInfo.name = title;
		 if(!curIsHaveCustomGroup){
			 createListGrid(queryData,titleInfo,1);
			 curIsHaveCustomGroup = true;
		 }else{
			 if(lastClickIsShowInsance){
				 oc.monitor.manager.list.reloadGridData(queryData,titleInfo,1);
			 }else{
				 createListGrid(queryData,titleInfo,1);
			 }
		 }
		 lastClickIsShowInsance = true;
	}
	
	// 请求自定义资源数据
	oc.util.ajax({
		  url: oc.resource.getUrl('portal/resource/cameradetail/getAllCustomGroup.htm'),
		  data:null,
		  successMsg:null,
		  success: function(data){
			  var result = eval(data);
			  createOrRefreshGroup(result, true);
			  //点击添加按钮
			  $('#addGroupButton').on('click',function(e){
				  e.stopPropagation();
			  });
			  if(result.data != null && result.data != "" && result.data.length > 0){
				 //有自定义资源组才加载表格
				 curIsHaveCustomGroup = true;
				 lastClickIsShowInsance = false;
			  }
			  //加载资源类别
			  getCategoryInfo();
		  }
	});
	
	function delCustomGroup(id, name, rootGroupId, title){
		  oc.ui.confirm('确认删除' + name + '自定义资源组吗?',function(){
			  //发送删除请求
			  oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/delCustomGroup.htm'),
				  data:{id : id},
				  success:function(data){
					  if(data.code == 200){
						  alert('删除成功');
						  if(rootGroupId){
							  if(id == rootGroupId){
								  customGroupAccordion.accordion('remove', title);
							  }else{
								  reLoadCustomGroupByGroupId(rootGroupId, rootGroupId);
							  }
						  }
					  }else{
						  alert('删除失败');
					  }
				  }
			  });
		  });
	}
	
	
	// 获取类别信息
	function getCategoryInfo(){
		  //发送请求获取资源类别
		oc.util.ajax({
			  url: oc.resource.getUrl('resourceManagement/resourceCategory/getResourceCategorys.htm'),
			  data:null,
			  successMsg:null,
			  success: function(data){
				  var result = eval(data);  
				  var categoryArray = result.data;
				  if(categoryArray != undefined && categoryArray != null && categoryArray.length > 0){
					  createOrRefreshResourceInstance(categoryArray,false);
				  }else{
					  //资源组和资源都没有,显示空表格
					  createListGrid(null,null,1);
					  lastClickIsShowInsance = true;
				  }				  	  	  
			  }
		});
	}

	// 通过li的jquery对象获取资源组相关数据
	function getLiData(liTag){
		var liInfo = {
			id:liTag.find('.text:first').attr('value'),
			name:liTag.find('.text:first').html(),
			resourceIds:liTag.find('.text:first').next().attr('value')
		};
		return liInfo;
	}
	
	// 展示表格
	function clickCutomGroupToShowGrid(liTag){

		  var nowInfo = getLiData(liTag);
		  var queryData = {};
		  var titleInfo = {};
		  queryData.resourceId = nowInfo.resourceIds;
		  
		  titleInfo.id = nowInfo.id;
		  titleInfo.name = nowInfo.name;

		  curShowResourceGroupId = nowInfo.id;
		  if(lastClickIsShowInsance){
			 oc.monitor.manager.list.reloadGridData(queryData,titleInfo,0);
		  }else{
			 createListGrid(queryData,titleInfo,0);
		  }
		  lastClickIsShowInsance = true;
		
	}

	// 新增需求
	function clickCustomGroupTreeToShowGrid(id, name, resourceIds){
		var allResourceId = findAllResourceId(id);
		if(resourceIds){
			var resourceIdArray = resourceIds.split(",");
			for (var i = 0; i < resourceIdArray.length; i++) {
				if($.inArray(resourceIdArray[i], allResourceId) == -1){
					allResourceId.push(resourceIdArray[i]);
				}
			}
		}
		var titleInfo = {id : id, name : name};
		var queryData = {resourceId : allResourceId.join()};
		curShowResourceGroupId = id;
		if(lastClickIsShowInsance){
			oc.monitor.manager.list.reloadGridData(queryData, titleInfo, 0);
		}else{
			createListGrid(queryData, titleInfo, 0);
		}
		lastClickIsShowInsance = true;
	}
	
	// 刷新自定义资源组信息
	oc.module.monitormanagement.resource.west = {
		updateCustomGroupInfo:function(selectIds,isDeleteTotal){
			var panel = customGroupAccordion.accordion('getSelected');
			var rootGroupId = panel.find(".tree").attr('customGroupId');
			var groupDiv = customContent.find(".customGroupAccordionTitle[customGroupId='" + rootGroupId + "']");
			var resourceArray = groupDiv.find(".s-digit").attr('value').split(","), newResourceArray = [];
			for(var i = 0; i < resourceArray.length; i++){
				if($.inArray(parseFloat(resourceArray[i]), selectIds) == -1){
					newResourceArray.push(resourceArray[i]);
				}
			}
			groupDiv.find(".s-digit").attr('value', newResourceArray.join());
			reLoadCustomGroupByGroupId(rootGroupId, curShowResourceGroupId);
		},
		updateGroupInfoForDeleteResource:function(){
			//删除资源刷新资源列表和自定义组列表
			//请求自定义资源数据
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/cameradetail/getAllCustomGroup.htm'),
				  data:null,
				  successMsg:null,
				  success: function(data){
					  var result = eval(data);
					  createOrRefreshGroup(result, false);
				  }
			});
		},
		addResourceIntoGroup:function(instanceIds){
			//将资源加入自定义资源组
			var groupListWindow = $('<div/>');
			groupListWindow.append('<div style="height:8%" class="sel_resource_name"><span class="ico ico-help"></span><span>请选择需移入资源组名称</span></div>');
			groupListWindow.append('<div class="oc-home-resource-select w-darkbg padding-bottom8" style="height: 299px; width: 288px;">' +
										'<div class="addResourceIntoGroupListIdOuter" style="height: 299px; width: 288px;">' +
										'<form class="oc-form oc-form_tan" style="width: 288px;">' + 
										'<div class="content" id="addResourceIntoGroupListId"></div>' +
										'</form>' +
										'</div>' +
									'</div>');
			groupListWindow.dialog({
				  width:300,
				  height:400,
				  title:'移入资源组',
				  modal:true,
				  buttons:[{
					  text:'确定',
					  iconCls:"icon-ok",
					  handler:function(){
						  
						  //获取选中资源组
						  var groupIdArray = new Array();
						  $('#addResourceIntoGroupListId').find('input[type="checkbox"]:checked').each(function(index,e){
							  var groupId = $(this).attr('value');
							  groupIdArray.push(groupId);
						  });
						  
						  if(!groupIdArray || groupIdArray.length <= 0){
							  alert('至少选择一个自定义资源组');
							  return;
						  }
						  
						  //发送请求移入资源组
						  oc.util.ajax({
							  url: oc.resource.getUrl('portal/resource/insertIntoGroupForInstanceList.htm'),
							  data:{instanceIds:instanceIds,groupIds:groupIdArray.join()},
							  successMsg:null,
							  success: function(data){
								  if(data.data){
									  alert('移入资源组成功');
									  //请求自定义资源数据
									  oc.util.ajax({
										  url: oc.resource.getUrl('portal/resource/cameradetail/getAllCustomGroup.htm'),
										  data:null,
										  successMsg:null,
										  success: function(data){
											  var result = eval(data);
											  createOrRefreshGroup(result, false);
										  }
									  });
									  groupListWindow.dialog('close');
								  }else{
									  alert('移入资源组失败');
								  }
							  }
						  });
					  }
				  },{
					  text:'取消',
					  iconCls:"icon-cancel",
					  handler:function(){
						  groupListWindow.dialog('close');
					  }
				  }]
			});
			//请求自定义资源数据
			oc.util.ajax({
				url: oc.resource.getUrl('portal/resource/cameradetail/getAllCustomGroup.htm'),
				data:null,
				successMsg:null,
				success: function(data){
					if(data.data){
						var customAccordion = $('#addResourceIntoGroupListId'), selected = true;
						if(data.data.length > 5){
							groupListWindow.find(".oc-home-resource-select").css({
								'overflow-y':'auto',
								'overflow-x':'hidden'
							});
							groupListWindow.find(".addResourceIntoGroupListIdOuter").height((56 * data.data.length) + 'px');
						}
						var parentData = {
								childCustomGroupVo:data.data
						};
						//构建树数据
						var treeData = createCustomGroupTreeData(parentData).children;
						//关闭根节点
						if(treeData){
							for(var i = 0 ; i < treeData.length ; i ++){
								treeData[i].state = "closed";
							}
						}
						customAccordion.tree({
							fit : true,
							data : treeData,
							formatter : function(node){
								return '<label><input type="checkbox" value="' + node.id + '" title="' + node.name + '"/>' + node.name + '</label>';
							}
						});
					}else{
						alert('获取自定义资源组失败');
					}
				}
			});
		},
		refreshCategoryCount:function(categoryCountList){
			//刷新类别数字
			var allPanels = categoryAccordion.accordion('panels');
			if(!categoryCountList){
				categoryCountList = new Array();
			}
			for(var x = 0 ; x < allPanels.length ; x++){
				var categoryName = allPanels[x].find('ul').attr('categoryname');
				//判断大类是否存在
				var firstCategoryIsExsit = false;
				for(var y = 0 ; y < categoryCountList.length ; y++){
					if(categoryCountList[y].name == categoryName){
						firstCategoryIsExsit = true;
						break;
					}
				}
				if(!firstCategoryIsExsit){
					categoryAccordion.accordion('remove',categoryName);
				}
			}
			var categoryIndex = -1;
			for(var i = 0 ; i < categoryCountList.length ; i ++){
				var secondCategoryCountList = categoryCountList[i].childCategorys;
				if(secondCategoryCountList && secondCategoryCountList.length > 0
						&& categoryAccordion.find('ul[categoryid=' + categoryCountList[i].id + ']').length > 0){
					
					categoryIndex++;
					for(var j = 0 ; j < secondCategoryCountList.length ; j ++){
						var countSpan = categoryAccordion.find('ul[categoryid=' + categoryCountList[i].id + ']').find('span[class="s-digit"][value=' + secondCategoryCountList[j].id + ']');
						if(!countSpan || countSpan.length <= 0){
							if(secondCategoryCountList[j].resourceNumber > 0){
								accordionCategoryUlList[categoryIndex].add({										  
									name:secondCategoryCountList[j].name,
									info:secondCategoryCountList[j].id,
									number:secondCategoryCountList[j].resourceNumber
								});
							}
							continue;
						}
						if(secondCategoryCountList[j].resourceNumber == 0){
							//该二级类别没有了
							countSpan.parent().remove();
							continue;
						}
						if(countSpan.text() != secondCategoryCountList[j].resourceNumber){
							countSpan.text(secondCategoryCountList[j].resourceNumber);
						}
					}
				}
			}
			changeResAccordionHight(categoryCountList.length);
		}
	};


	
	
	function createListGrid(queryData,titleInfo,type){
		 $('#monitor_management').layout('panel','center').panel({
				href : 'module/monitor-management/monitor_list_tabs.html',
				onLoad : function(){
					oc.resource.loadScript('resource/module/monitor-management/js/monitor_list.js',function(){
						
						//$("#firstCount").attr("title","摄像头总数:"+cameraTotalCount);
						//$("#resourceList").panel({title: "摄像头总数:"+cameraTotalCount});
						oc.monitor.manager.list.open(queryData,titleInfo,type);
					});
				}
		  });
	}

	function findAllResourceId(groupId){
		var groupDiv = customContent.find("div[customGroupId='" + groupId + "']"), allResourceId = [];
		if(groupDiv.hasClass('customGroupAccordionTitle')){
			groupDiv.parent().parent().parent().find(".customGroupTreeTitle").each(function(){
				if($(this).attr('value') && $.inArray($(this).attr('value'), allResourceId) == -1){
					allResourceId.push($(this).attr('value'));
				}
			});
		}else{
			groupDiv.parent().parent().parent().find(".customGroupTreeTitle[customGroupId!='" + groupId + "']").each(function(){
				if($(this).attr('value') && $.inArray($(this).attr('value'), allResourceId) == -1){
					allResourceId.push($(this).attr('value'));
				}
			});
		}
		return allResourceId;
	}

	function reLoadCustomGroupByGroupId(rootGroupId, clickTreeGroupId){
		var contentDiv = customContent.find("ul[customGroupId='" + rootGroupId + "']");
		contentDiv.empty();
		oc.util.ajax({
			url: oc.resource.getUrl('portal/resource/getCustomGroupById.htm'),
			data:{id : rootGroupId},
			success:function(data){
				createCustomGroupTree(contentDiv, data.data);
				// 取消所有树节点的选中状态
				customContent.find(".tree-node").removeClass("tree-node-selected");
				// 刷新树后加载右侧列表
				var clickTreeDiv = customContent.find("[customGroupId='" + clickTreeGroupId + "']");
				if(clickTreeDiv.hasClass("customGroupTreeTitle")){
					clickTreeDiv.parent().parent().addClass("tree-node-selected");
					clickCustomGroupTreeToShowGrid(clickTreeGroupId, clickTreeDiv.find(".custom-widthno").html(), clickTreeDiv.attr('value'));
				}else{
					clickCustomGroupTreeToShowGrid(clickTreeGroupId, clickTreeDiv.find(".custom-widthno").html(), clickTreeDiv.find(".s-digit").attr('value'));
				}
			}
		});
	}

	function createOrRefreshGroup(result, selected){
		// 清空所有内容重新新建
		var allPanels = customGroupAccordion.accordion('panels');
		for(var i = allPanels.length - 1; i >= 0; i--){
			customGroupAccordion.accordion('remove', i);
		}
		for(var i = 0; i < result.data.length; i++){
			var data = result.data[i];
			if(data.id=="999999"){
				customGroupAddAccordion(data, selected);
				changeCustomGroupAccordionHeight();
				selected = false;
			}
			
		}
		
	}

	// 自定义accordion的title和tree的node
	function createCustomGroupTitle(id, name, resourceIds, titleClass){
		var titleDiv="";
			// 样式调整、结构的区别处理
			if(titleClass == 'customGroupAccordionTitle'){
					 titleDiv = $("<div class='" + titleClass + "' customGroupId='" + id + "' />"), groupNameSpan = $("<span class='custom-widthno' title='" + name + "'>" + name + "</span>"),
						sDigitSpan = $("<span class='s-digit' value='" + resourceIds.join() + "'></span>"), outerCgtOperDiv = $("<span class='outerCgtOper' />");
					var customGroupEditBtnSpan = $("<span class=''></span>");
					//customGroupEditBtnSpan.addClass('customGroupTreeRootEditBtn');
					titleDiv.append(groupNameSpan).append(sDigitSpan).append(outerCgtOperDiv.append(customGroupEditBtnSpan));
				
			}else{
				 titleDiv = $("<div class='" + titleClass + "' customGroupId='" + id + "' />"), groupNameSpan = $("<span class='custom-widthno' title='" + name + "'>" + name + "</span>"),
				sDigitSpan = $("<span class='s-digit' value='" + resourceIds.join() + "'></span>"), outerCgtOperDiv = $("<span class='outerCgtOper' />");
				titleDiv.attr('value', resourceIds.join()).css({'height':'18px'});
				groupNameSpan.css({'text-overflow':'ellipsis','white-space':'nowrap','overflow':'hidden','display':'block','float':'left'});
				var customGroupEditBtnSpan = $("<span class=''></span>");
				customGroupEditBtnSpan.addClass('customGroupTreeEditBtn').css({'display':'none','margin-top':'2px'});
				titleDiv.append(groupNameSpan).append(outerCgtOperDiv.append(customGroupEditBtnSpan));
				return titleDiv[0].outerHTML;
			}
	
		return titleDiv;
	}

	// 创建自定义资源组Accordion
	function customGroupAddAccordion(data, selected){
		var titleDiv = createCustomGroupTitle(data.id, data.name, data.resourceInstanceIds, 'customGroupAccordionTitle')
		var contentDiv = $("<ul  customGroupId='" + data.id + "'/>");
		customGroupAccordion.accordion('add',{
			selected : selected,
			title : titleDiv,
			content : contentDiv
		});
		// 新增按钮事件
		var customGroupId = titleDiv.attr('customGroupId');
		titleDiv.find('.customGroupTreeRootEditBtn').on('click', function(e){
			e.stopPropagation();
			var top  = $(e.target).offset().top + 5;
			var left = $(e.target).offset().left + 8;
			$('body').find('.cgtOper').remove();
			cgtOperDiv = $("<div class='cgtOper sh-window' style='top:" + top + "px;left:" + left + "px'>");
			tWindowDiv = $("<div class='t-window'><div class='b-window'><div class='m-window'>" +
									"<span class='cgtAdd'><a class='ico ico-addpage'></a>添加子资源组</span>" +
									"<span class='cgtEdit'><a class='ico ico-edit'></a>编辑</span>" +
									"<span class='cgtDel'><a class='ico ico-del'></a>删除</span>" +
							"</div></div></div>");
			$('body').append(cgtOperDiv.append(tWindowDiv));
			
			$(cgtOperDiv).find(".cgtAdd").on('click', function(e){
				e.stopPropagation();
			});
			$(cgtOperDiv).find(".cgtEdit").on('click', function(e){
				var customGroupName = titleDiv.find('.custom-widthno').html();
				var resourceIds = titleDiv.find(".s-digit").attr('value');
				e.stopPropagation();
			});
			$(cgtOperDiv).find(".cgtDel").on('click', function(e){
				var customGroupName = titleDiv.find('.custom-widthno').html();
				delCustomGroup(customGroupId, customGroupName, customGroupId, titleDiv);
				changeCustomGroupAccordionHeight();
				e.stopPropagation();
			});
			
			$(cgtOperDiv).hover(function(e){
				e.stopPropagation();
			},function(e){
				e.stopPropagation();
				$('body').find('.cgtOper').remove();
			});
		});

		// 创建tree
		createCustomGroupTree(contentDiv, data);
	}

	// 新需求创建自定义资源组树
	function createCustomGroupTree($TreeDiv, data){
		$TreeDiv.tree({
			fit : true,
			data : createCustomGroupTreeData(data).children,
			formatter : function(node){
				var titleDiv = createCustomGroupTitle(node.id, node.name, node.resourceIds, 'customGroupTreeTitle');
				return titleDiv;
			},
			onClick : function(node){
				// 因为这里资源id和资源名称都有可能改变了，而树还没有更新
				var $TreeTitle = $TreeDiv.find(".customGroupTreeTitle[customGroupId='" + node.id + "']");
				clickCustomGroupTreeToShowGrid(node.id, $TreeTitle.find(".custom-widthno").html(), $TreeTitle.attr('value'));
			},
			onLoadSuccess : function(node, data){
				$TreeDiv.find(".tree-node").each(function(){
					var treeNodeWidth = 195;
					var treeIndentWidth = 0, treeTitleWidth = 0, treeEditBtnWidth = 36;
					$(this).find("> span").each(function(){
						if($(this).hasClass("tree-title")){
							treeTitleWidth += $(this).width();
						}else{
							treeIndentWidth += $(this).width();
						}
					});
					$(this).find(".tree-title").width(treeNodeWidth - treeIndentWidth);

					// 最多新建四层
					if($(this).find("> .tree-indent").length >= 4){
						$(this).find(".cgtAdd").remove();
					}
				});
			}
		});
		
		$TreeDiv.find(".customGroupTreeTitle").each(function(){
			var $TreeTitle = $(this);
			// 树节点事件
			$TreeTitle.hover(function(e){
				$(this).find(".customGroupTreeEditBtn").show();
			}, function(e){
				$(this).find(".customGroupTreeEditBtn").hide();
			});
			// 树节点编辑按钮事件
			$TreeTitle.find(".customGroupTreeEditBtn").on('click', function(e){
				e.stopPropagation();
				var top  = $(e.target).offset().top + 5;
				var left = $(e.target).offset().left + 6;
				$('body').find('.cgtOper').remove();
				cgtOperDiv = $("<div class='cgtOper sh-window' style='top:" + top + "px;left:" + left + "px'>");
				tWindowDiv = $("<div class='t-window'><div class='b-window'><div class='m-window'>" +
										"<span class='cgtAdd'><a class='ico ico-addpage'></a>添加子资源组</span>" +
										"<span class='cgtEdit'><a class='ico ico-edit'></a>编辑</span>" +
										"<span class='cgtDel'><a class='ico ico-del'></a>删除</span>" +
								"</div></div></div>");
				$('body').append(cgtOperDiv.append(tWindowDiv));
				
				// 树节点具体编辑按钮事件
				var customGroupId = $TreeTitle.attr('customGroupId');
				var customGroupName = $TreeTitle.find('.custom-widthno').html();
				var resourceIds = $TreeTitle.attr('value');
				$(cgtOperDiv).find(".cgtAdd").on('click', function(e){
					$('body').find('.cgtOper').remove();
					e.stopPropagation();
				});
				$(cgtOperDiv).find(".cgtEdit").on('click', function(e){
					$('body').find('.cgtOper').remove();
					e.stopPropagation();
				});
				$(cgtOperDiv).find(".cgtDel").on('click', function(e){
					delCustomGroup(customGroupId, customGroupName, $TreeDiv.attr('customGroupId'));
					$('body').find('.cgtOper').remove();
					e.stopPropagation();
				});
				
				// 树节点展开后事件
				$(cgtOperDiv).hover(function(e){
					e.stopPropagation();
				}, function(e){
					$TreeTitle.find(".customGroupTreeEditBtn").hide();
					$('body').find('.cgtOper').remove();
					e.stopPropagation();
				});
			});

		});
	}

	// 创建Tree的数据
	function createCustomGroupTreeData(data){
		var parentData = {
			id : data.id,
			name : data.name,
			text : data.name,
			pid : data.pid,
			resourceIds : data.resourceInstanceIds,
			children : []
		};
		for(var i = 0; data.childCustomGroupVo && i < data.childCustomGroupVo.length; i++){
			var customGroupVo = data.childCustomGroupVo[i];
			parentData.children.push(createCustomGroupTreeData(customGroupVo));
		}
		return parentData;
	}
	
	function createOrRefreshResourceInstance(categoryArray,isRefresh,lastOpenAccordionName){
		for(var i = 0 ; i < categoryArray.length ; i++){
			  var category = categoryArray[i]; 
			  var categorySecondArray = category.childCategorys;
			  if(categorySecondArray != undefined && categorySecondArray != null){
				  var accordionCategoryUl = oc.ui.navsublist({
					  addRowed:function(li,data){
						  if(data.name=='摄像头'){
							  li.find('.text:first').attr('value',data.info).attr('title',data.name).addClass('oc-text-ellipsis');
							  //li.append('<span class="s-digit" value=' + data.info + '>' + data.number + '</span>');			
						  }
						 						 								 
					  },
					  click:function(href,data,e){
						  curShowResourceGroupId = -1;
						  var queryData = {};
						  var titleInfo = {};
						  queryData.categoryId = data.info;
						  queryData.parentCategoryId = $(e.target).parent().parent().attr('categoryid');
						  titleInfo.id = data.info;
						  titleInfo.name = data.name;
						  if(lastClickIsShowInsance){
							 oc.monitor.manager.list.reloadGridData(queryData,titleInfo,1);
						  }else{
							 createListGrid(queryData,titleInfo,1);
						  }
						  lastClickIsShowInsance = true;
					  }
				  }); 
				  for(var j = 0 ; j < categorySecondArray.length ; j++){
					  var categorySecond = categorySecondArray[j];
					  if(categorySecond.resourceNumber  != 0){
						  if(categorySecond.name=='摄像头'){
							  var liData = {										  
									  name:categorySecond.name,
									  info:categorySecond.id,
									  number:categorySecond.resourceNumber
							  };
							  cameraTotalCount=categorySecond.resourceNumber;
							  accordionCategoryUl.add(liData);
						  }
					
					  }
				  }
				  accordionCategoryUlList.push(accordionCategoryUl);
			  }
			  var selected = false;
			  if(lastOpenAccordionName && category.name == lastOpenAccordionName){
				  selected = true;
			  }
			  accordionCategoryUl.ul.attr('categoryname',category.name).attr('categoryid',category.id);
			  categoryAccordion.accordion('add', {
					title: category.name,
					content: accordionCategoryUl.ul,
					selected: selected
			  });			
 	    }
		
		if(!curIsHaveCustomGroup && !isRefresh){
			//没有自定义资源组,默认显示第一个类别
			//跳转到资源
			mainAccordion.accordion('select',1);
		}
		
		isProgramOpenAccordion = false;
	}

	// 计算自定义资源组的高度
	function changeCustomGroupAccordionHeight(){
		var panels = customGroupAccordion.accordion('panels');
		var customGroupCount = 0;
		if(panels && panels.length > 0){
			customGroupCount = panels.length;
		}
		customGroupAccordion.accordion('panels').length
		var groupTitleHeight = 34, groupPanelMinHeight = 200;
		var accordionPanelHeight = $('#mainAccordionDivId').height() - (groupTitleHeight * 4);
		if(accordionPanelHeight - (customGroupCount * groupTitleHeight) < groupPanelMinHeight){
			customGroupAccordion.accordion('resize');
		}else{
			customGroupAccordion.accordion('resize');
		}
	}

	// 计算资源组高度
	function changeResAccordionHight(categoryCount){
		var categoryTitleHeight = 34, categoryPanelMinHeight = 200;
		var accordionPanelHeight = $('#mainAccordionDivId').height() - (categoryTitleHeight * 4);
		if(accordionPanelHeight - (categoryCount * categoryTitleHeight) < categoryPanelMinHeight){
			resourceAccordionParentPanel.height(categoryCount * categoryTitleHeight + categoryPanelMinHeight);
			categoryAccordion.accordion('resize');
		}else{
			resourceAccordionParentPanel.height('100%');
			categoryAccordion.accordion('resize');
		}
	}

});