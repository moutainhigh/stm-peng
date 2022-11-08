$(function () {
//	$('#oc_resource_index').layout();
	// 命名空间
	oc.ns('oc.module.resourcemanagement.resource.west');
	//缓存第一个资源实例数据
	var lastClickIsShowInsance = false;
	
	var isFirstLoad = true;
	
	var isProgramOpenAccordion = false;
	
	var lastOpenAccordionName = null;
	
	var defaultSearchContent = '搜索资源名称|IP';
	//是否是普通用户
	var isCommonUser = false;
	//是否新添加自定义资源组
	var isJustAddResourceGroup = false;
	
	var accordionCategoryUlList = new Array();
	var treeMenuTitleName = $('#sidebar').find('#mainMenu').find('li.module-href.highlight').find('li.highlight:first').find('a:first').attr('rightmarkname');
	if(!treeMenuTitleName){
		treeMenuTitleName = '资源组';
	}
	
	var addButtonDom = treeMenuTitleName == '资源组' ? '<span id=\'addGroupButton\' class=\'fa fa-plus locate-right margin-top8 openbesinesswin\'></span>' : '';
	var westPanel = $('#resource_management').layout('panel','west');
	
	westPanel.panel('setTitle',treeMenuTitleName + addButtonDom);
	
	var treeUrl = oc.resource.getUrl('portal/resource/getAllCustomGroup.htm');
	if(treeMenuTitleName == '资源'){
		treeUrl = oc.resource.getUrl('resourceManagement/resourceCategory/getResourceCategorys.htm');
	}
	var customGroupEditBtnSpan = "<span class='customGroupTreeEditBtn mouse-btn-left' style='opacity:0; z-index:10;'></span>";
	
    //自定义资源组树结构封装
	var floor = 0;
    function convert2groupTree(data, arr ,n){
    	n++;
	   	for(var i = 0;i < data.length;i++){
	   		var node = data[i];
	   		var nameText = n < 5 ? customGroupEditBtnSpan : '';	//最多只能建5层
	   		arr.push({
	   			id: node.id,
	   			text: '<span class="customGroupTreeTitle" title="'+node.name+'">'+ node.name + '</span>' + nameText,
	   			state: node.childCustomGroupVo.length > 0 ? 'closed' : 'open',
	   			attributes: {
	   				name: node.name,
	   				description: node.description,
	   				groupType: node.groupType,
	   				entryId: node.entryId,
	   				entryDatetime: node.entryDatetime,
	   				resourceInstanceIds: node.resourceInstanceIds,
	   				pid: node.pid
	   			}
	   		});
	   		 
	   		if(node.childCustomGroupVo.length > 0){
	   			arr[i].children = convert2groupTree(node.childCustomGroupVo,[],n);
	   		}
	   	}
	   	return arr;
    }
    
    //资源类型树
	function convert2categoryTree(categoryArray, arr){
		for(var i = 0; i < categoryArray.length; i++){
			var category = categoryArray[i];
			if(category.resourceNumber == 0){
				continue;
			}
			var name = (category.childCategorys && category.childCategorys.length >0) ? category.name : ('<span class="oc-text-ellipsis" title="'+category.name+'">'+category.name+'</span>'+'<span class="s-digit">'+category.resourceNumber+'</span>');
			arr.push({
				id: category.id,
				text: name,
				state: category.childCategorys.length > 0 ? 'closed' : 'open',
				name: category.name,
			});
			if(category.childCategorys.length > 0){
				arr[i].children = convert2categoryTree(category.childCategorys, []);
			}
		}
		return arr;
	};
	
	//初始化右键menu
	$('#rightButtonMenu_resource').menu({
		onClick:function(item){
			var node = $("#rightButtonMenu_resource").data('node');
			var customGroupId = node.id;
			switch (item.text) {
			case '新增':
				showDialog('add', null, null,  customGroupId, null, customGroupId);
				break;
			case '编辑':
				var customGroupName = node.attributes.name;
				var resourceIds = node.attributes.resourceInstanceIds.join();
				showDialog('edit', customGroupName, customGroupId, null, resourceIds, customGroupId);
				break;
			case '删除':
				var customGroupName = node.attributes.name;
				delCustomGroup(customGroupId, customGroupName, customGroupId);
				break;
			case '向上移动':
				changeCustomGroupSort(customGroupId, 'up');
				break;
			case '向下移动':
				changeCustomGroupSort(customGroupId, 'down');
				break;
			default:
				break;
			}
		},
	});
    
	//资源管理菜单选项
	if(treeMenuTitleName == '资源组'){
		//自定义资源组树
		var customGroupTree = $('#customGroupTree').tree({
			fit:true,
			animate: true,
			lines: true,
			loader: function(param, success) {
				if(!param.id){
					oc.util.ajax({
						url: treeUrl,
						success: function(data){
							var jsonStr = [];
							if(data.data.length > 0){
								success(convert2groupTree(data.data,jsonStr,floor));
							}else{
								success([]);
							}
						}
					});
				}else{
					success();
				}
			},
			onClick: function(node){
				var titleInfo = {id : node.id, name : node.text};
				var queryData = {resourceId : node.attributes.resourceInstanceIds.join()};
				if(lastClickIsShowInsance){
					oc.resourced.manager.list.reloadGridData(queryData, titleInfo, 0);
				}else{
					createListGrid(queryData, titleInfo, 0);
				}
				lastClickIsShowInsance = true;
			},
			onLoadSuccess: function(node, data){
				if(data.length == 0){
					if(lastClickIsShowInsance){
						oc.resourced.manager.list.removeGroupDataGrid(0);
					}else{
						createListGrid(null,null,0);
					}
				}else{
					var firstNode = customGroupTree.tree('find',data[0].id);
					$(firstNode.target).click();
				}
				
//				var hasClickResourceMenu = $('#mainMenu').find('a[rightmarkname="资源组"]:first').attr('hasClickResourceMenu');
//				if(data.length == 0){
//					if(hasClickResourceMenu){
//						if(lastClickIsShowInsance){
//							oc.resourced.manager.list.removeGroupDataGrid(0);
//						}else{
//							createListGrid(null,null,0);
//						}
//					}else{
//						$('#mainMenu').find('a[rightmarkname="资源"]:first').click();
//					}
//				}else{
//					var firstNode = customGroupTree.tree('find',data[0].id);
//					$(firstNode.target).click();
//				}
				
				lastClickIsShowInsance = true;
				
				//绑定编辑图标事件
				$('#customGroupTree').find('.tree-node').each(function(){
					$(this).hover(function(e){
						$(this).parent().find('.customGroupTreeEditBtn:first').css("opacity","1");
					},function(e){
						$(this).parent().find('.customGroupTreeEditBtn:first').css("opacity","0");
					});
				});
				
				$(".customGroupTreeEditBtn").on('click', function(e){
					e.stopPropagation();
					var selectNodeTarget = $(this).parent().parent();
					customGroupTree.tree('select', selectNodeTarget);
					var node = customGroupTree.tree('getData', selectNodeTarget);
					
					//菜单赋值
					$('#rightButtonMenu_resource').data('node',node);
					$('#rightButtonMenu_resource').menu('show',{
						left:e.pageX,
						top:e.pageY
					});
					
					// 树节点展开后事件
					$(".customGroupTreeEditBtn").hover(function(e){
						e.stopPropagation();
					}, function(e){
						$(this).parent().find('.customGroupTreeEditBtn:first').css("opacity","0");
						e.stopPropagation();
					});
				});
			},
		});
	}else if(treeMenuTitleName == '资源'){
		//资源类别
		var customGroupTree = $('#customGroupTree').tree({
			fit:true,
			animate: true,
			lines: true,
			loader: function(param, success) {
				if(!param.id){
					oc.util.ajax({
						url: treeUrl,
						success: function(data){
							var jsonStr = [];
							if(data.data.length > 0){
								success(convert2categoryTree(data.data,jsonStr));
							}else{
								success([]);
							}
						}
					});
				}else{
					success();
				}
			},
			onClick: function(node){
				var titleInfo = {id : node.id, name : node.name};
				var queryData = {};
				if(node.children && node.children.length>0){	//一级资源类型
					queryData.categoryIds = node.id;
				}else{	//二级资源类型
					queryData.categoryId = node.id;
					var parentNode = $('#customGroupTree').tree('getParent',node.target);
					queryData.parentCategoryId = parentNode.id;
				}
				if(lastClickIsShowInsance){
					oc.resourced.manager.list.reloadGridData(queryData, titleInfo, 1);
				}else{
					createListGrid(queryData, titleInfo, 1);
				}
				lastClickIsShowInsance = true;
			},
			onLoadSuccess: function(node, data){
				$('#mainMenu').find('a[rightmarkname="资源组"]:first').attr('hasClickResourceMenu','true');
				if(data.length == 0){
					if(lastClickIsShowInsance){
						oc.resourced.manager.list.removeGroupDataGrid(0);
					}else{
						 createListGrid(null,null,1);
					}
				}else{
					var firstNode = customGroupTree.tree('find',data[0].id);
					$(firstNode.target).click();
				}
				lastClickIsShowInsance = true;
			}
		});
	}
	
	$('#addGroupButton').on('click',function(e){
		e.stopPropagation();
		showDialog('add');
	});
	
	//记录当前点击的资源组ID
	var curShowResourceGroupId = -1;
	var customContent = $('#customGroupAccordion');
	var customGroupAccordionParentPanel = $('.customGroupAccordionParentPanel');
	var categoryAccordion = $('#resourceCategroyAccordion');
	var resourceAccordionParentPanel = $(".resourceAccordionParentPanel");
	
	// 新需求新建自定义资源组
	var customGroupAccordion;
	function initCustomGroupAccordion(){
		customGroupAccordion = customContent.accordion({
			fit : true,
			onSelect : function(title, index){
				var panel = mainAccordion.accordion('getSelected');
				var index = mainAccordion.accordion('getPanelIndex', panel);
				if(index == 0){
					var id = $(title).attr('customGroupId');
					var name = $($(title).find("span").get(0)).html();
					var resourceIds = $(title).find(".s-digit").attr('value');
					clickCustomGroupTreeToShowGrid(id, name, resourceIds);
					// 取消所有树节点的选中状态
					customContent.find(".tree-node").removeClass("tree-node-selected");
				}
				// 先取消所有title的点击事件 再给title加上点击事件
				customContent.find(".panel-title").each(function(){
					$(this).unbind("click");
				});
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
	
	
	//最后一次点击的是显示资源实例，还是显示其他的
	var curIsHaveCustomGroup = false;
	//记录当前是否点击操作按钮
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
				 oc.resourced.manager.list.reloadGridData(queryData,titleInfo,1);
			 }else{
				 createListGrid(queryData,titleInfo,1);
			 }
		 }
		 lastClickIsShowInsance = true;
	}
	/** 改变自定义资源组的排序
	 * @param id 资源组id
	 * @param pid 资源组的父资源组id
	 * @param entryId 资源组的创建者id
	 * @param direction 移动方向
	 */
	function changeCustomGroupSort(id, direction){
		oc.util.ajax({
			url: oc.resource.getUrl('portal/resource/changeCustomGroupSort.htm'),
			data: {
				groupId : id,
				direction : direction
			},
			success : function(data) {
				var rtnData = data.data;
				if (rtnData.result) {
					$('#customGroupTree').tree('reload');
				} else {
					alert(rtnData.errorMsg);
				}
			}
		});
	}
	
	function delCustomGroup(id, name, rootGroupId){
		  oc.ui.confirm('确认删除' + name + '自定义资源组吗?',function(){
			  //发送删除请求
			  oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/delCustomGroup.htm'),
				  data:{id : id},
				  success:function(data){
					  if(data.code == 200){
						  alert('删除成功');
						  $('#customGroupTree').tree('reload');
					  }else{
						  alert('删除失败');
					  }
				  }
			  });
		  });
	}
	
	//修改或者添加展示dialog
	function showDialog(type, groupNameParam, groupId, parentGroupId, curGroupHaveResourceIds, rootGroupId){
		  var addWindow = $('<div/>');
		  addWindow.append('<form onkeydown="if(event.keyCode==13)return false;" class="oc-form col1" style="padding:0;"><div class="form-group" style="padding:0 5px; text-align:left;" ><span>资源组名称：</span><div><input type="text" id="cutomGroupNameInputId" maxlength=15 /></div></div></form>');
		  addWindow.append('<div id="groupPicktree"></div>');
			
		  var groupPicktree = null;
		  
		  var dialogTitle = '';
		  var pickTreeCfg = null;
		  
		  //获取树数据
		  var nowGroupName = '';
			
			//添加或者更新的url
		  var groupOperationUrl = '';
			
		  //添加或者更新的data
		  var transData = {};
		
			
		  pickTreeCfg = {
				  selector:"#groupPicktree",
				  dataType:'array',
				  isInteractive:true,
				  requestType:'sync',
				  search:{
					  searchText:defaultSearchContent,
					  onSearch:function(content){
						    //获取右边资源实例
							var rightData = groupPicktree.getRightTreeData();
							
							var checkedResourceIDs = '';
							
							for(var i = 0 ; i < rightData.length ; i++){
								
								var selectData = rightData[i];
								
								if(!selectData.isParent){
									checkedResourceIDs += selectData.id + ',';
								}
							}
							
							if(checkedResourceIDs){
								checkedResourceIDs = checkedResourceIDs.substring(0,checkedResourceIDs.length - 1);
							}
							
							//发送请求获取搜索结果
							oc.util.ajax({
								  url: oc.resource.getUrl('resourceManagement/resourceCategory/getLeftResourceDefBySearchContent.htm'),
								  data:{ids:checkedResourceIDs,searchContent:content},
								  successMsg:null,
								  success: function(data){
									  if(data.data){
										  groupPicktree.reload(data.data,'l');
										  if(content != ''){
											  groupPicktree.leftTreeObj.expandAll(true);
										  }
									  }		  	  	  
								  }
							});
					  }
				  }
		  };
		  
		  if(type == 'add'){
			  groupOperationUrl = 'portal/resource/addCustomGroup.htm';
			  dialogTitle = '添加自定义资源组';
			  pickTreeCfg.lUrl = oc.resource.getUrl('resourceManagement/resourceCategory/getAllResourceDef.htm');
			  transData.pid = parentGroupId;
		  }else if(type == 'edit'){
			  var curGroupHaveResourceIds = curGroupHaveResourceIds;
			  transData.id = groupId;
			  groupOperationUrl = 'portal/resource/editCustomGroup.htm';
			  dialogTitle = '修改自定义资源组';
			  
			  pickTreeCfg.lUrl = oc.resource.getUrl('resourceManagement/resourceCategory/getLeftResourceDef.htm');
			  pickTreeCfg.leftParam = {ids:curGroupHaveResourceIds};
			  pickTreeCfg.rUrl = oc.resource.getUrl('resourceManagement/resourceCategory/getRightResourceDef.htm');
			  pickTreeCfg.rightParam = {ids:curGroupHaveResourceIds};
			  nowGroupName = groupNameParam;
			  
		  }
		  
		  //构建dialog
		  addWindow.dialog({
			  title:dialogTitle,
			  width:800,
			  height:410,
			  modal:true,
			  buttons:[
					{
						text:'保存',
						iconCls:"fa fa-check-circle",
						handler:function(){
							
							var groupName = $('#cutomGroupNameInputId').val();
							
							if(groupName == null || groupName == undefined || groupName.trim() == ''){
								
								alert('请输入名称');
								return;
							}
							
							var checkData = groupPicktree.getRightTreeData();
							
							var resourceInstanceIdArray = new Array();
							
							var checkedResourceIDs = '';
							
							for(var i = 0 ; i < checkData.length ; i++){
								
								var selectData = checkData[i];
								
								if(!selectData.isParent){
									checkedResourceIDs += selectData.id + ',';
									resourceInstanceIdArray.push(selectData.id);
								}
							}
							checkedResourceIDs = checkedResourceIDs.substring(0,checkedResourceIDs.length - 1);
							
							if(type == 'edit' && curGroupHaveResourceIds == checkedResourceIDs && groupName == nowGroupName){
								alert('未做任何修改');
								return;
							}
							
							transData.name = groupName;
							transData.resourceInstanceIds = checkedResourceIDs;
							
							//发送添加请求
							oc.util.ajax({
								url: oc.resource.getUrl(groupOperationUrl),
								data:transData,
								failureMsg:null,
								successMsg:null,
								success: function(data){
									var result = eval(data);
									if(data.code == 201){
										alert('资源组名称已存在!');
										addWindow.dialog('destroy');
										return;
									}
									
									var loginUser = oc.index.getUser();
									if(result.data >= 1){
										var selectNode = $('#customGroupTree').tree('getSelected');
										
										if(type == 'add'){
											var newNode = {
													id: result.data,
													text: '<span class="customGroupTreeTitle">'+ groupName + '</span>' + customGroupEditBtnSpan,
													state: 'open',
													attributes: {
														name: groupName,
														description: null,
														groupType: 'USER',
														entryId: loginUser.id || 1,
														entryDatetime: null,
														resourceInstanceIds: resourceInstanceIdArray,
														pid: parentGroupId
													}
											};
											
											$('#customGroupTree').tree('append', {
												parent: parentGroupId ? selectNode.target : null,
												data: newNode
											});
										}else if(type == 'edit'){
											$('#customGroupTree').tree('update',{
												target: selectNode.target,
												text: '<span class="customGroupTreeTitle">'+ groupName + '</span>' + customGroupEditBtnSpan,
												attributes: {
													name: groupName,
													description: null,
													groupType: 'USER',
													entryId: loginUser.id || 1,
													entryDatetime: null,
													resourceInstanceIds: resourceInstanceIdArray,
													pid: parentGroupId
												}
											});
											
											$('.customGroupTreeEditBtn').unbind();
											$(".customGroupTreeEditBtn").on('click', function(e){
												e.stopPropagation();
												var selectNodeTarget = $(this).parent().parent();
												$('#customGroupTree').tree('select', selectNodeTarget);
												var node = $('#customGroupTree').tree('getData', selectNodeTarget);
												
												//菜单赋值
												$('#rightButtonMenu_resource').data('node',node);
												$('#rightButtonMenu_resource').menu('show',{
													left:e.pageX,
													top:e.pageY
												});
												
												// 树节点展开后事件
												$(".customGroupTreeEditBtn").hover(function(e){
													e.stopPropagation();
												}, function(e){
													$(this).parent().find('.customGroupTreeEditBtn:first').css("opacity","0");
													e.stopPropagation();
												});
											});
											
											$(selectNode.target).click();
										}
										
										addWindow.dialog('destroy');
									}else{
										alert('operation fail!');
									}
								}
							});
						}
					},{
						text:'取消',
						iconCls:"fa fa-times-circle",
						handler:function(){
							addWindow.dialog('destroy');
						}
					}
			  ]
		  });
		  
		  $('#cutomGroupNameInputId').val(nowGroupName);
		  
		  groupPicktree = oc.ui.picktree(pickTreeCfg);
		
	}
	
	//获取类别信息
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

	//通过li的jquery对象获取资源组相关数据
	function getLiData(liTag){
		var liInfo = {
			id:liTag.find('.text:first').attr('value'),
			name:liTag.find('.text:first').html(),
			resourceIds:liTag.find('.text:first').next().attr('value')
		};
		return liInfo;
	}
	
	//展示表格
	function clickCutomGroupToShowGrid(liTag){

		  var nowInfo = getLiData(liTag);
		  var queryData = {};
		  var titleInfo = {};
		  queryData.resourceId = nowInfo.resourceIds;
		  
		  titleInfo.id = nowInfo.id;
		  titleInfo.name = nowInfo.name;
		  /***
		   * BUG #12292 资源管理：只有1个资源组时，执行一次资源查询后，点不开该资源组包含的资源列表
		   *** 
		  if(curShowResourceGroupId == nowInfo.id){
			  return;
		  }
		  */
		  curShowResourceGroupId = nowInfo.id;
		  if(lastClickIsShowInsance){
			 oc.resourced.manager.list.reloadGridData(queryData,titleInfo,0);
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
			oc.resourced.manager.list.reloadGridData(queryData, titleInfo, 0);
		}else{
			createListGrid(queryData, titleInfo, 0);
		}
		lastClickIsShowInsance = true;
	}
	
	//刷新自定义资源组信息
	oc.module.resourcemanagement.resource.west = {
		updateCustomGroupInfo:function(selectIds,isDeleteTotal){
			var selectedNode = $('#customGroupTree').tree('getSelected');
			$('#customGroupTree').tree('reload');
			$(selectedNode.target).click();
			
//			var panel = customGroupAccordion.accordion('getSelected');
//			var rootGroupId = panel.find(".tree").attr('customGroupId');
//			var groupDiv = customContent.find(".customGroupAccordionTitle[customGroupId='" + rootGroupId + "']");
//			var resourceArray = groupDiv.find(".s-digit").attr('value').split(","), newResourceArray = [];
//			for(var i = 0; i < resourceArray.length; i++){
//				if($.inArray(parseFloat(resourceArray[i]), selectIds) == -1){
//					newResourceArray.push(resourceArray[i]);
//				}
//			}
//			groupDiv.find(".s-digit").attr('value', newResourceArray.join());
//			reLoadCustomGroupByGroupId(rootGroupId, curShowResourceGroupId);
		},
		updateGroupInfoForDeleteResource:function(){
			//删除资源刷新资源列表和自定义组列表
			//请求自定义资源数据
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/getAllCustomGroup.htm'),
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
			groupListWindow.append('<div class="oc-home-resource-select w-darkbg padding-bottom8" style="height: 90%; width: 288px;">' +
										'<div class="addResourceIntoGroupListIdOuter" style="height: 90%; width: 288px;">' +
										'<form class="oc-form oc-form_tan" style="width: 288px;">' + 
										'<div class="content" id="addResourceIntoGroupListId" style="overflow:auto;"></div>' +
										'</form>' +
										'</div>' +
									'</div>');
			groupListWindow.dialog({
				  width:300,
				  height:300,
				  title:'移入资源组',
				  modal:true,
				  buttons:[{
					  text:'确定',
					  iconCls:"fa fa-check-circle",
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
										  url: oc.resource.getUrl('portal/resource/getAllCustomGroup.htm'),
										  data:null,
										  successMsg:null,
										  success: function(data){
											  var result = eval(data);
											  $('#customGroupTree').tree('reload');
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
					  iconCls:"fa fa-times-circle",
					  handler:function(){
						  groupListWindow.dialog('close');
					  }
				  }]
			});
			//请求自定义资源数据
			oc.util.ajax({
				url: oc.resource.getUrl('portal/resource/getAllCustomGroup.htm'),
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
								return '<label title="' + node.name + '"><input type="checkbox" value="' + node.id + '" />' + node.name + '</label>';
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
	
	//移入资源组树
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
	
	function createListGrid(queryData,titleInfo,type){
		
		 $('#resource_management').layout('panel','center').panel({
				href : 'module/resource-management/resource_list_tabs.html',
				onLoad : function(){
					oc.resource.loadScript('resource/module/resource-management/js/resource_list.js',function(){
						//创建grid
						oc.resourced.manager.list.open(queryData,titleInfo,type);
						
					});
				}
		  });
	}
});