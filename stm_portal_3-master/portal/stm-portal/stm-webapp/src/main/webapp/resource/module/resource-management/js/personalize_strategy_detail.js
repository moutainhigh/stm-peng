$(function(){

	oc.ns('oc.personalizeprofile.strategy.detail');
	
	//记录当前策略页面数据
	var curProfileData = null;
	
	var nowStrategyId = null;
	
	var strategyInfoAccordion = null;
	
	var isFirstCreateAccordion = true;
	
	var indicatorsDefiendDiv = null;
	
	var curInstanceIsHavePersonalizedProfile = null;
	
	var strategy_base_info_input_name = null;
	
	var strategy_base_info_input_desc = null;
	
	var curIsQuickSelectStrategy = null;
	
	var strategyTitle = null;
	
	var strategyDialog = null;
	
	var alarm_check_box = null;
	
	var monitor_check_box = null;
	
	var resource_strategy_detail_operator_button = null;
	
	var curInstanceDomainId = null;
	
	//装载rangeSlider的数组
	var ionRangeSlider = null;
	
	//装载requencyCombox的数组
	var frequencyComboxs = null;
	
	//pick表格
	var pickGrid = null;
	
	var alarmFlappingArray = null;
	
	var alarmFlappingSelectId = null;
	
	var indicatorsDefiendGrid = null;
	
	//记录当前主策略ID
	var curMainProfileId = null;
	
	var curStrategyIndex = null;
	
	var curMetricGridIsCreate = null;
	
	var curIsComboboxSelectProfile = false;
	
	var curProfileDomainId = -1;
	
	//判断是否允许修改整个策略
	var isAllowUpdateAllProfile = null;
	
	//记录右边表格的资源实例列表
	var curRightResourceInstanceList = null;
	
	//记录用户在主策略上对主资源的勾选操作
	var curUserCheckedResource = null;
	
	//记录用户在主策略上对主资源勾选的所有域
	var curUserCheckedResourceDomain = null;
	
	//记录用户在主策略上对主资源的取消勾选操作
	var curUserUnCheckedResource = null;
	
	//记录子策略时当前选中的主资源
	var curSelectMainResourceForChildProfile = null;
	
	//记录当前主策略用户所选择的状态
	var curUserSelectMainResourceState = 0;
	
	var childrenStrategy = null;
	
	var isAutoChecked = false;
	
	var pageSize = 12;
	
	var curChildPageStart = 0;
	
	var isLoadding = false;
	
	//记录子资源是否加载完毕
	var childResourceIsLoadCompele = false;
	
	var isAutoCheckAll = false;
	
	var totalMainInstanceCount = 0;
	
	//记录用户在子策略中当前子资源表格列表选中行
	var curUserCheckedForChildResourceInChildProfile = new Array();
	
	//记录子策略中子资源用户的选择情况
	var curUserCheckedForAllChildResourceInChildProfile = new Array();
	
	//记录当前子策略是否网络设备接口
	var isNetWorkInterface = false;
	
	var curUseStrategyResourceIDs = '';
	
	var interForm = null;
	
	var resourceChildrenStrategyAccordion = null;
	
	var childProfileMaxCount = 0;
	
	var curInterfaceState = '';
	
	var curUseQuickSelectResourceId = null;
	
	var curSelectChildProfileResourceIndex = null;
	
	//初始化策略信息弹出层
	function initDetail(){
		
		var allData = curProfileData;
		curRightResourceInstanceList = new Array();
		curUserCheckedResource = new Array();
		curUserSelectMainResourceState = 0;
		totalMainInstanceCount = 0;
		curUserCheckedResourceDomain = new Array();
		curUserUnCheckedResource = new Array();
		curInstanceDomainId = null;
		
		main_default_strategy_select = 'main_default_strategy_select';
		
		alarm_check_box = 'alarm_check_box';
		
		monitor_check_box = 'montitor_check_box';
		
		alarmFlappingSelectId = 'alarmFlappingSelectId';
		
		strategy_base_info_input_name = 'strategy_base_info_input_name';
		
		strategy_base_info_input_desc = 'strategy_base_info_input_desc';
		
		resource_strategy_detail_operator_button = 'resource_strategy_detail_operator_button';
		
		strategyTitle = $('#resource_strategy_info_title_id');
		
		strategyTitle.text(allData.profileInfo.profileName);
		
		if(resourceChildrenStrategyAccordion){
			$('#resource_children_strategy_id').parent().remove();
			$('#resource_strategy_detail_west_id').append('<div style="height:480px"><div id="resource_children_strategy_id" class="oc-window-leftmenu"></div></div>');
		}
		
		resourceChildrenStrategyAccordion = $('#resource_children_strategy_id');
		resourceChildrenStrategyAccordion.accordion({
			animate:true,
			onSelect:function(title,index){
				curSelectChildProfileResourceIndex = index;
			}
		});
		
		//渲染主策略
		var allowAddHtml = '<a id="main_proflie_name_id" title="' + allData.profileInfo.profileName + '" class="text-over">' + allData.profileInfo.profileName + '</a>';

		$('#resource_main_strategy_id').append(allowAddHtml);
		
		$('#resource_main_strategy_id').unbind('click');
		$('#resource_main_strategy_id').on('click',function(e){
			
			if(curStrategyIndex == -1){
				//点击主策略已经展示
				return;
			}
			
			var isUpdateChildStrategy = null;
			if(curStrategyIndex == -1){
				isUpdateChildStrategy = false;
			}else{
				isUpdateChildStrategy = true;
			}
			var isPass = true;
			
			//个性化
			if(curIsQuickSelectStrategy && !curInstanceIsHavePersonalizedProfile){
				
				if(curStrategyIndex != -1){
					var newMetricInfo = getCurrentMetricInfo(allData.children[curStrategyIndex]);
					curProfileData.children[curStrategyIndex].metricSetting.metrics = newMetricInfo;
				}else{
					var newMetricInfo = getCurrentMetricInfo(allData);
					curProfileData.metricSetting.metrics = newMetricInfo;
				}
				
			}else{
				isPass = checkAllInfoIsModifyForPersonalize(isUpdateChildStrategy,1,curStrategyIndex);
			}
			
			if(isPass){
				
				initAccrodion(false,-1);
			}
			
			
		});
		
		if(allData.children != null && allData.children.length > 0){
			
			var resourceList = new Array();
			var resourceIdList = new Array();
			
			out : for(var i = 0 ; i < allData.children.length ; i++){
				
				for(var j = 0 ; j < resourceList.length ; j++){
					
					if(resourceList[j] == allData.children[i].profileInfo.profileResourceType){
						
						continue out;
						
					}
					
				}
				
				resourceList.push(allData.children[i].profileInfo.profileResourceType);
				resourceIdList.push(allData.children[i].profileInfo.resourceId);
				
			}
			
			for(var i = 0 ; i < resourceList.length ; i++){
				
				var navList = $('<div/>');
				
				var childStrategyTitle = '<div class="personalizeChildProfileTitle">个性化子策略' + resourceList[i];
				
				if(isAllowUpdateAllProfile || (curIsQuickSelectStrategy && !curInstanceIsHavePersonalizedProfile)){
					childStrategyTitle += '<span class="l-btn-icon fa fa-plus" resourceId="' + resourceIdList[i] + '" style="float: right; position: relative;right: 5px;"></span>';//margin-top:2px;
				}
				
				resourceChildrenStrategyAccordion.accordion('add',{
					title:childStrategyTitle + '</div>',
	    			content: navList,
	    			selected: false
				});
				
				var isFirstChildProfile = true;
				
				var navSubList = oc.ui.navsublist({
					selector:navList,
					addRowed:function(li,data){
						li.find('.text:first').attr('title',data.name);
						
						if(isAllowUpdateAllProfile){
							if(!isFirstChildProfile){
								li.append('<span class="fa fa-close oc-icomargin">&nbsp;</span>');
							}else{
								isFirstChildProfile = false;
							}
						}
							
					},
					click:function(href,data,e){
						var clickTagClass = $(e.target).attr('class');
						
						var childStrategyName = $(e.target).parent().find('.text:first').html();
						
						if(clickTagClass == 'fa fa-close oc-icomargin'){
							
							//删除子资源
							oc.ui.confirm('确认删除' + childStrategyName + '子策略吗?',function(){
							  
								  //发送删除请求
								  oc.util.ajax({
									  successMsg:null,
									  url: oc.resource.getUrl('portal/strategydetail/removeProfileById.htm'),
									  data:{profileId:data.profileId},
									  success:function(data){
										  
										  if(data.data){
											  if(curIsQuickSelectStrategy){
												  
												  oc.quick.strategy.detail.clearDialogHtml();
												  
												  oc.personalizeprofile.strategy.detail.showInQuickSelect(nowStrategyId,strategyDialog,curInstanceIsHavePersonalizedProfile,curUseQuickSelectResourceId,curUseQuickSelectResourceInstanceId);
												  
											  }else{
												  refreshDialogDontClose(false,nowStrategyId);
											  }
										  }else{
											  alert('删除失败!');
										  }
										  
									  }
									  
								  });
							  
							});
							
						}else{
							if(data.index == curStrategyIndex){
								//点击子策略已经展示
								return;
							}
							//点击的子策略
							var isUpdateChildStrategy = null;
							if(curStrategyIndex == -1){
								isUpdateChildStrategy = false;
							}else{
								isUpdateChildStrategy = true;
							}
							var isPass = true;
							
							//个性化
							if(!curInstanceIsHavePersonalizedProfile && curIsQuickSelectStrategy){
								
								if(curStrategyIndex != -1){
									var newMetricInfo = getCurrentMetricInfo(allData.children[curStrategyIndex]);
									curProfileData.children[curStrategyIndex].metricSetting.metrics = $.extend(true,[],newMetricInfo);
								}else{
									var newMetricInfo = getCurrentMetricInfo(allData);
									curProfileData.metricSetting.metrics = $.extend(true,[],newMetricInfo);
								}
								
							}else{
								isPass = checkAllInfoIsModifyForPersonalize(isUpdateChildStrategy,-data.index,curStrategyIndex);
							}
							
							if(isPass){
								initAccrodion(true,data.index);
							}
							
						}
						
						
					}
				});
				
				for(var j = 0 ; j < allData.children.length ; j++){
					
					if(allData.children[j].profileInfo.resourceId == resourceIdList[i]){
						navSubList.add({
							name:allData.children[j].profileInfo.profileName,
							index: j,
							profileId:allData.children[j].profileInfo.profileId
						});
					}
					
				}
				
				
			}
			
			if(curSelectChildProfileResourceIndex != null){
				//展开最后一次打开的子策略类型
				resourceChildrenStrategyAccordion.accordion('select',curSelectChildProfileResourceIndex);
			}
			
			if(isAllowUpdateAllProfile || (curIsQuickSelectStrategy && !curInstanceIsHavePersonalizedProfile)){
				
				//注册添加子策略按钮事件
				$('#resource_children_strategy_id').find('.fa-plus').on('click',function(e){
					
					e.stopPropagation();
					
					if(curIsQuickSelectStrategy && !curInstanceIsHavePersonalizedProfile){
						alert('请先创建个性化策略！');
						return;
					}
					
					var nowAddResourceId = $(e.target).attr('resourceId');
					
					var count = 0;
					for(var j = 0 ; j < allData.children.length ; j++){
						
						if(allData.children[j].profileInfo.resourceId == nowAddResourceId){
							count++;
						}
						
					}
					
					if(count >= (childProfileMaxCount + 1)){
						alert('同一类型子策略不能超过' + childProfileMaxCount + '个');
						return;
					}
					
					var addChildStrategy = $('<div id="addChildStrategyForm"></div>');
					
					var addChildStrategyForm = $('<form class="oc-form col1"></form>');
					
					addChildStrategyForm.append('<div class="form-group"><label>子策略名称 : </label><div><input type="text" name="profileName" validType="maxLength[25]" required/></div></div>');
					addChildStrategyForm.append('<div class="form-group"><label>备注 : </label><div><textarea style="resize: none;height:80px" name="profileDesc" validType="maxLength[50]"></textarea></div></div>');
					
					addChildStrategy.append(addChildStrategyForm);
					addChildStrategy.append('<div class="oc-toolbar" align="right"></div>');
					
					//添加子资源
					var addChildStrategyDialog = $('<div/>').dialog({
						width:'500px',
						height:'220px',
						title: '添加子策略',
						content:addChildStrategy
					});
					
					var addChildForm = oc.ui.form({
						selector:$('#addChildStrategyForm')
					});
					
					addChildStrategy.find('.oc-toolbar').append($('<a/>').linkbutton('RenderLB',{
						text:'应用',
						iconCls:"fa fa-check-circle",
						onClick:function(){
							if(addChildForm.validate()){
								//验证表单
								var addParameter = addChildForm.val();
								addParameter.profileParentId = allData.profileInfo.profileId;
								addParameter.resourceId = nowAddResourceId;
								addParameter.profileTypeMapping = 2;
								
								if(addParameter.profileName.length > 11){
									alert('子策略名称过长，请修改');
									return;
								}
								
								if(allData.children){
									//检查子策略名字是否重复
									for(var i = 0 ; i < allData.children.length ; i ++){
										if(addParameter.profileName == allData.children[i].profileInfo.profileName){
											//重复
											alert('子策略名重复!');
											return;
										}
									}
								}
								
								//添加子策略
								oc.util.ajax({
									successMsg:null,
									url: oc.resource.getUrl('portal/strategydetail/addChildProfile.htm'),
									data:addParameter,
									success:function(data){
										
										if(data.data){
											addChildStrategyDialog.dialog('close');
											if(curIsQuickSelectStrategy){

												oc.quick.strategy.detail.clearDialogHtml();
												
												oc.personalizeprofile.strategy.detail.showInQuickSelect(nowStrategyId,strategyDialog,curInstanceIsHavePersonalizedProfile,curUseQuickSelectResourceId,curUseQuickSelectResourceInstanceId);
												
											}else{

												refreshDialogDontClose(false,nowStrategyId);
											}
										}else{
											alert('添加失败!');
										}
										
									}
								
								});
							}
							
							
						}
					})).append($('<a/>').linkbutton('RenderLB',{
						text:'取消',
						iconCls:"fa fa-times-circle",
						onClick:function(){
							
							addChildStrategyDialog.dialog('close');
						}
					}));
					
				});
			}
			
		}
		
		
	}
	
	//构建accrodion
	function initAccrodion(isChildStrategy,strategyIndex){
		
		curChildPageStart = 0;
		
		curUserCheckedForChildResourceInChildProfile = new Array();
		
		curUserCheckedForAllChildResourceInChildProfile = new Array();
		
		curUseStrategyResourceIDs = '';
		
		curRightResourceInstanceList = new Array();
		
		isNetWorkInterface = false;
		
		curUserUnCheckedResource = new Array();
		
		curUserCheckedResource = new Array();
		
		curUserSelectMainResourceState = 0;
		
		totalMainInstanceCount = 0;
		
		curUserCheckedResourceDomain = new Array();
		
		interForm = null;
		
		curInterfaceState = '';
		
		curMetricGridIsCreate = false;
		var isQuickSelectStrategy = curIsQuickSelectStrategy;
		curStrategyIndex = strategyIndex;
		
		$("#" + resource_strategy_detail_operator_button).html('');
		
		ionRangeSlider = new Array();
		
		var singleStrategyInfo = null;
		if(isChildStrategy){
			singleStrategyInfo = curProfileData.children[strategyIndex];
		}else{
			singleStrategyInfo = curProfileData;
		}
		
		frequencyComboxs = new Array();
		
		alarmFlappingArray = new Array();
		
		var strategyForm = $('<form class="oc-form col1 h-pad-mar" onkeydown="if(event.keyCode==13)return false;"></form>');
		
		var strategyTable = $('<table style=" width:100%;" class="tab-border"/>');
		
		if(!isFirstCreateAccordion){
	
			$('#resource_strategy_accordion_div').html('');
			$('#resource_strategy_accordion_div').append('<div id="resource_strategy_detail_center_id"></div>');
			
		}
		
		strategyInfoAccordion = $('#resource_strategy_detail_center_id');
		strategyInfoAccordion.accordion({
			animate:true,
			onSelect:function(title,index){
				if(!isChildStrategy && index == 1){
					var updateDomainArray = new Array();
					if(curInstanceDomainId){
						updateDomainArray.push(curInstanceDomainId);
					}
					oc.resource.alarmrules.updateDomainId(curInstanceDomainId);
				}
				if(!curMetricGridIsCreate){
					//个性化
					if(title.indexOf('指标') != -1){
						showMetricGrid(singleStrategyInfo);
					}
				}
				
			},
			onBeforeSelect:function(title,index){
				
				if(!curMetricGridIsCreate){
					
					//个性化
					if(title.indexOf('指标') != -1){
						oc.ui.progress();
					}
				}
				return true;
			}
		});
		var cfg = null;
		
		var resourceSelectDiv = null;
		
		if(isChildStrategy){
			var strategyNormalInfo = null;
			
			var strategyForm = $('<form class="oc-form col1 h-pad-mar" onkeydown="if(event.keyCode==13)return false;"></form>');
			
			var strategyTable = $('<table style=" width:100%;" class="tab-border"/>');
			
			var updateUserName = singleStrategyInfo.profileInfo.updateUser;
			
			//常规信息
			strategyNormalInfo = $('<div/>');
			if(null == updateUserName){
				updateUserName = '';
			}
			
			var nowProfileDesc = '';
			if(singleStrategyInfo.profileInfo.profileDesc){
				nowProfileDesc = singleStrategyInfo.profileInfo.profileDesc;
			}
			
			var nowUpdateTime = null;
			
			if(singleStrategyInfo.profileInfo.updateTime){
				nowUpdateTime = new Date(parseInt(singleStrategyInfo.profileInfo.updateTime)).toLocaleString().substr(0,22);
			}else{
				nowUpdateTime = '';
			}
			
			if(!isAllowUpdateAllProfile){
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">策略名称</td><td><div>' + singleStrategyInfo.profileInfo.profileName + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">策略类型</td><td><div>' + singleStrategyInfo.profileInfo.profileResourceType + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改人</td><td><div id="profileInfoBaseUpdateUserName">' + updateUserName + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改时间</td><td><div id="profileInfoBaseUpdateTime">' + nowUpdateTime + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">备注</td><td><div>' + nowProfileDesc + '</td></tr>');
				
			}else{
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">策略名称</td><td><input type="text" id="' + strategy_base_info_input_name + '" value="' + singleStrategyInfo.profileInfo.profileName + '" maxlength=25/></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">策略类型</td><td><div>' + singleStrategyInfo.profileInfo.profileResourceType + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改人</td><td><div id="profileInfoBaseUpdateUserName">' + updateUserName + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改时间</td><td><div id="profileInfoBaseUpdateTime">' + nowUpdateTime + '</div></td></tr>');
				strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">备注</td><td><textarea style="resize: none;height:60px" id="' + strategy_base_info_input_desc + '" maxlength=50>' + nowProfileDesc + '</textarea></td></tr>');
				
			}
			
			strategyForm.append(strategyTable);
			strategyNormalInfo.append(strategyForm);
		}

		//资源选择
		resourceSelectDiv = $('<div style="height:300px"></div>');
		
		//循环遍历出使用当前策略的资源id
		
		if(isChildStrategy && (!curIsQuickSelectStrategy || curInstanceIsHavePersonalizedProfile)){

			var nowDomainId = -1;
			var hostInterface = singleStrategyInfo.profileInfo.resourceId.indexOf("HostInterface");
			var type = "";
			if(singleStrategyInfo.profileInfo.profileResourceType == "接口"){
				if(hostInterface < 0) {//网络设备接口
					type="netWorkInterface";
					isNetWorkInterface = true;
				}else{
					//主机接口
					type="hostInterface";
				}
			}else if(singleStrategyInfo.profileInfo.profileResourceType == "服务"){
				type="service";
			}
			
			resourceSelectDiv.append('<form class="oc-form col1">'+
					'<div class="form-group" style="text-align:left">'+
					'<input type="text" id="searchResourceOrIpInputForChildProfile" placeholder="搜索资源名称"/><a id="queryResourceButtonForChildProfile">查找</a>'+
					'</div></form>');
			
			if(isNetWorkInterface) {
				resourceSelectDiv.find('.form-group').append('<label class="form_fontstan">状态：</label>'+
						'<select name="interfaceState" required="required"></select>'+
						'<a class="refreshStateBtn xing_stan" href="javascript:void(0);"></a>');
				resourceSelectDiv.find(".refreshStateBtn").first().linkbutton('RenderLB', {
					iconCls : 'fa fa-refresh',
					text : '采集'+singleStrategyInfo.profileInfo.profileResourceType+'状态',
					onClick : function(){
						/*
						if(curUserCheckedForChildResourceInChildProfile.length <= 0) {
							alert("请选择接口");
							return;
						}
						var childrenInstanceId = "";
						for(var i = 0; i < curUserCheckedForChildResourceInChildProfile.length; i++) {
							childrenInstanceId += curUserCheckedForChildResourceInChildProfile[i] + ",";
						}
						*/
						// 子资源ID改用后台查询
						oc.util.ajax({
							  url: oc.resource.getUrl('portal/strategydetail/getInterfaceState.htm'),
							  data:{mainInstanceId:curUseQuickSelectResourceInstanceId,type:type},
							  timeout:60000,
							  success:function(data){
								  var dataArr = data.data;
								  var dataGridRows = pickGrid.selector.datagrid('getRows');
								  for(var i = 0; i < dataGridRows.length; i++) {
									  var singleRow = dataGridRows[i];
									  for(var j = 0; j < dataArr.length; j++) {
										  //是否可用指标
										  var ifAvailabilityArr = dataArr[j].data;
										  if(singleRow.resourceId == dataArr[j].resourceInstanceId){//判断是哪个节点的可用性
											  if(ifAvailabilityArr[0] == 0){//如果为不可用，则标红
												  singleRow.interfaceState = '不可用';
											  }else if(ifAvailabilityArr[0] == 1){//如果为可用
												  singleRow.interfaceState = '可用';
											  }
											  pickGrid.selector.datagrid('updateRow',{
												  index:i,
												  row:singleRow
											  });
										  }
									  }
								  }
							  }
						  });
					}
				});
				interForm = oc.ui.form({
					selector:resourceSelectDiv,
					combobox:[{
						selector:'[name=interfaceState]',
						data:[{id:'all',name:'全部'},{id:'up',name:'可用'},{id:'down',name:'不可用'}],
						placeholder:null,
						onSelect:function(r){
							//先使滚动条回到顶部，防止二次请求
							$('#resourceSelectGridParentDiv').find('.datagrid-view2>.datagrid-body').scrollTop(0);
							curInterfaceState = r.id;
							curChildPageStart = 0;
							oc.util.ajax({
								  url: oc.resource.getUrl('portal/strategydetail/getChildResourceForPersonalizeChildProfileInInterface.htm'),
								  async:true,
								  data:{childProfileId:singleStrategyInfo.profileInfo.profileId,mainInstanceId:curUseQuickSelectResourceInstanceId,resourceId:singleStrategyInfo.profileInfo.resourceId,
									  interfaceState:curInterfaceState,domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
								  success:function(data){
									  
									  	isLoadding = true;
								    	curChildPageStart = data.data.rows.length;
									  
										for(var i = 0 ; i < data.data.rows.length ; i ++){
											
											//判断是否是用户操作勾选资源
											for(var j = 0 ; j < curUserCheckedResource.length ; j ++){
												if(curUserCheckedResource[j] == data.data.rows[i].resourceId){
													data.data.rows[i].checked = true;
													break;
												}
											}
											
											//判断是否使用当前策略和是否被用户取消勾选
											if(data.data.rows[i].strategyID == curProfileData.profileInfo.profileId && !data.data.rows[i].checked){
												var isAdd = true;
												for(var j = 0 ; j < curUserUnCheckedResource.length ; j ++){
													if(data.data.rows[i].resourceId == curUserUnCheckedResource[j]){
														//被用户取消勾选
														isAdd = false;
														break;
													}
												}
												if(isAdd){
													data.data.rows[i].checked = true;
												}
											}
											
										}
										
										pickGrid.selector.datagrid('loadData',data);
										if(data.data.selectStatus == 2){
											isAutoChecked = true;
											pickGrid.selector.datagrid('checkAll');
										}
										
										isLoadding = false;
									  

								  }
							  });
						}
					}]
				});
				
			}
			
			resourceSelectDiv.append('<div id="resourceSelectGridParentDiv" style="height:95%"><div id="resource_select_pickGrid"></div></div>');
			
			cfg = {
				selector:'#resource_select_pickGrid',
				pagination:false,
				selectOnCheck:false,
				fitColumns:false,
				columns:[[
		                    {field:'resourceId',title:'资源ID',checkbox:true,sortable:true},
		                    {field:'resourceName',title:'子资源名称',width:'32%'},
		                    {field:'interfaceState',title:'当前状态',hidden:true,width:'32%',
		                    	formatter: function(value,row,index){
		                    		if(value == '0'){
		                    			return '不可用';
		                    		}else if(value == '1'){
		                    			return '可用';
		                    		}else{
		                    			return value;
		                    		}
		                    	}
		                    },
		                    {field:'strategyName',title:'当前策略',width:'32%',ellipsis:true},
		                    {field:'strategyID',title:'当前策略ID',hidden:true}
                ]],
                onCheck:function(index,row){
                	
                	if(isAutoChecked){
                		isAutoChecked = false;
                		return;
                	}
                	
                	curUserCheckedResource.push(row.resourceId);
                	for(var i = 0 ; i < curUserUnCheckedResource.length ; i ++){
                		if(curUserUnCheckedResource[i] == row.resourceId){
                			curUserUnCheckedResource.splice(i,1);
                			break;
                		}
                	}
                	
                	//全选操作
                	if(totalMainInstanceCount == curUserCheckedResource.length){
                		isAutoChecked = true;
                		pickGrid.selector.datagrid('checkAll');
                	}
                	
                },
                onUncheck:function(index,row){
                	
                	if(isAutoChecked){
                		isAutoChecked = false;
                		return;
                	}
                	
                	for(var i = 0 ; i < curUserCheckedResource.length ; i ++){
                		if(curUserCheckedResource[i] == row.resourceId){
                			curUserCheckedResource.splice(i,1);
                			break;
                		}
                	}
                	curUserUnCheckedResource.push(row.resourceId);
                },
                onCheckAll:function(rows){
                	
                	if(isAutoChecked){
                		isAutoChecked = false;
                		return;
                	}
                	
            		var domainIdParam = -1;
                	
                	oc.util.ajax({
              			  successMsg:null,
          				  url: oc.resource.getUrl('portal/strategydetail/getChildResourceIdForChildProfile.htm'),
          				  data:{mainInstanceId:curUseQuickSelectResourceInstanceId,resourceId:singleStrategyInfo.profileInfo.resourceId
          					  ,interfaceState:curInterfaceState,domainId:domainIdParam},
              			  success:function(data){
              				  
              				  if(data.code == 200 && data.data){
              					  curUserUnCheckedResource = new Array();
              					  for(var i = 0 ; i < data.data.length ; i ++){
	                              		var isContain = false;
	                              		for(var j = 0 ; j < curUserCheckedResource.length ; j ++){
	                              			if(data.data[i] == curUserCheckedResource[j]){
	                              				isContain = true;
	                              				break;
	                              			}
	                              		}
	                              		if(!isContain){
	                              			curUserCheckedResource.push(data.data[i]);
	                              		}
              					  }
              				  }
              				  
              			  }
              			  
              		});
                	
                },
                onUncheckAll:function(rows){
                	
                	if(isAutoChecked){
                		isAutoChecked = false;
                		return;
                	}
                	
//                	for(var j = 0 ; j < rows.length ; j ++){
//                		curUserUnCheckedResource.push(rows[j].resourceId);
//                		for(var i = 0 ; i < curUserCheckedResource.length ; i ++){
//                    		if(curUserCheckedResource[i] == rows[j].resourceId){
//                    			curUserCheckedResource.splice(i,1);
//                    			break;
//                    		}
//                    	}
//                	}
                	
                	var domainIdParam = -1;
                	
                   	oc.util.ajax({
            			  successMsg:null,
        				  url: oc.resource.getUrl('portal/strategydetail/getChildResourceIdForChildProfile.htm'),
        				  data:{mainInstanceId:curUseQuickSelectResourceInstanceId,resourceId:singleStrategyInfo.profileInfo.resourceId
        					  ,interfaceState:curInterfaceState,domainId:domainIdParam},
            			  success:function(data){
            				  
            				  if(data.code == 200 && data.data){
            					  curUserCheckedResource = new Array();
              					  for(var i = 0 ; i < data.data.length ; i ++){
	                              		var isContain = false;
	                              		for(var j = 0 ; j < curUserUnCheckedResource.length ; j ++){
	                              			if(data.data[i] == curUserUnCheckedResource[j]){
	                              				isContain = true;
	                              				break;
	                              			}
	                              		}
	                              		if(!isContain){
	                              			curUserUnCheckedResource.push(data.data[i]);
	                              		}
              					  }
            				  }
            				  
            			  }
            			  
            		});
                	
                }
			};
			
			
		
		}
		
		//指标定义
		indicatorsDefiendDiv = $('<div id="indicatorsDefiendGridId"></div>');
		
	    var accordionAddCfg2 = null;
	    if(isChildStrategy && (!curIsQuickSelectStrategy || curInstanceIsHavePersonalizedProfile)){
		    accordionAddCfg2 = {
					title: '选择' + singleStrategyInfo.profileInfo.profileResourceType,
					content: resourceSelectDiv,
					selected: true
			};
	    }
	    
	    var accordionAddCfg3 = null;
	    
	    if(!isChildStrategy || (curIsQuickSelectStrategy && !curInstanceIsHavePersonalizedProfile)){
		    accordionAddCfg3 = {
					title: '指标定义',
					content: indicatorsDefiendDiv,
					selected: true
			};
	    }else{
		    accordionAddCfg3 = {
					title: '指标定义',
					content: indicatorsDefiendDiv,
					selected: false
			};
	    }
	    
	    var accordionAddCfg4 = null;
	    
	    var alarmRulesDiv = null;
	    if(!isChildStrategy){
	    	
	    	alarmRulesDiv = $('<div/>');
	    	accordionAddCfg4 = {
	    			title: '告警规则',
	    			content: alarmRulesDiv,
	    			selected: false
	    	};
	    }else{
		    var accordionAddCfg1 = {
					title: '常规信息',
					content: strategyNormalInfo,
					selected: false
		    };
		    strategyInfoAccordion.accordion('add', accordionAddCfg1);
	    }
	    
    	if(isChildStrategy && (!curIsQuickSelectStrategy || curInstanceIsHavePersonalizedProfile)){
    		strategyInfoAccordion.accordion('add', accordionAddCfg2);
    		$('#queryResourceButtonForChildProfile').linkbutton('RenderLB',{
    			iconCls:'fa fa-search',
    			onClick : function(){
    				//先使滚动条回到顶部，防止二次请求
					$('#resourceSelectGridParentDiv').find('.datagrid-view2>.datagrid-body').scrollTop(0);
				 	 //发送请求刷新主资源列表
    				curChildPageStart = 0;
					showMainProfileResourceLeftGrid(singleStrategyInfo);
    			}
    		});
    	}
    	strategyInfoAccordion.accordion('add', accordionAddCfg3);
    	if(!isChildStrategy){
    		strategyInfoAccordion.accordion('add', accordionAddCfg4);
    	}
	    
	    if(alarmRulesDiv != null){
	    	alarmRulesDiv.panel({
	    		height:'302px',
	    		href:oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
	    		onLoad:function(){
	    			oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
    					if(singleStrategyInfo.profileInfo.profileId <= 0){
    						oc.resource.alarmrules.openDataCheck(lastSelectDefaultOrSpecialProfile,'model_profile',curUseQuickSelectResourceInstanceId,2);
    					}else{
	    					oc.resource.alarmrules.open(singleStrategyInfo.profileInfo.profileId,'model_profile',curUseQuickSelectResourceInstanceId,2);
    					}
	    			});
	    		}
	    	});
	    }
	    
		$('#resource_strategy_detail_layout').layout({
			fit:true
		});
		
		if(isChildStrategy && (!curIsQuickSelectStrategy || curInstanceIsHavePersonalizedProfile)){
			
			//获取数据
			pickGrid = oc.ui.datagrid(cfg);
			
    		//判断当前是否网络设备接口
    		if(isNetWorkInterface){
    			curInterfaceState = 'all';
    			if(interForm.ocui[0].jq.combobox('getValue') != 'all'){
    				interForm.ocui[0].jq.combobox('setValue','all');
    			}
    			pickGrid.selector.datagrid('showColumn','interfaceState');
    		}
    		
			//展示表格数据
			showMainProfileResourceAllGrid(singleStrategyInfo);

			//个性化策略
			//保存和取消按钮
			if(isAllowUpdateAllProfile || curIsQuickSelectStrategy){
				
				$("#" + resource_strategy_detail_operator_button).append($('<a/>').linkbutton('RenderLB',{
					text:'应用',
					iconCls:"fa fa-check-circle",
					onClick:function(){

						if(!curIsQuickSelectStrategy || (curIsQuickSelectStrategy && curInstanceIsHavePersonalizedProfile)){
							var singleStrategyInfo = null;
							
							if(isChildStrategy){
								singleStrategyInfo = curProfileData.children[curStrategyIndex];
							}else{
								singleStrategyInfo = curProfileData;
							}
							
							var updateInfo = checkProfileUpdateInfo(singleStrategyInfo,isChildStrategy);
							
							if(updateInfo == -1){
								//有阈值修改未完成
								alert('有阈值未修改完成,请修改!');
								return;
							}
							
							if(updateInfo.modifyInfo || updateInfo.modifyMetricInfo || updateInfo.modifyResourceInfo){
								updateProfileTotalInfo(updateInfo,2,true,curStrategyIndex);
							}else{
								if(curIsQuickSelectStrategy){
									//指定策略
									setProfileForSingleInstance(isChildStrategy);
								}else{
									alert('应用成功!');
									strategyDialog.panel('destroy');
								}
							}
						}else{
							if(curIsQuickSelectStrategy){
								//指定策略
								setProfileForSingleInstance(isChildStrategy);
							}else{
								alert('应用成功!');
								strategyDialog.panel('destroy');
							}
						}
						
						
					}
				})).append($('<a/>').linkbutton('RenderLB',{
					text:'取消',
					iconCls:"fa fa-times-circle",
					onClick:function(){
						strategyDialog.panel('destroy');
					}
				}));
			}
		}else{
			//个性化策略
			//保存和取消按钮
			var buttonText = '应用';
			if(curIsQuickSelectStrategy && !curInstanceIsHavePersonalizedProfile){
				buttonText = '创建';
			}
			
			if(isAllowUpdateAllProfile || curIsQuickSelectStrategy){
				
				$("#" + resource_strategy_detail_operator_button).append($('<a/>').linkbutton('RenderLB',{
					text:buttonText,
					iconCls:"fa fa-check-circle",
					onClick:function(){

						if(!curIsQuickSelectStrategy || (curIsQuickSelectStrategy && curInstanceIsHavePersonalizedProfile)){
							var singleStrategyInfo = null;
							
							if(isChildStrategy){
								singleStrategyInfo = curProfileData.children[curStrategyIndex];
							}else{
								singleStrategyInfo = curProfileData;
							}
							
							var updateInfo = checkProfileUpdateInfo(singleStrategyInfo,isChildStrategy);
							
							if(updateInfo == -1){
								//有阈值修改未完成
								alert('有阈值未修改完成,请修改!');
								return;
							}
							
							if(updateInfo.modifyMetricInfo || updateInfo.modifyAlarmInfo){
								updateProfileTotalInfo(updateInfo,2,false,curStrategyIndex);
							}else{
								if(curIsQuickSelectStrategy){
									//指定策略
									setProfileForSingleInstance(isChildStrategy);
								}else{
									alert('应用成功!');
									strategyDialog.panel('destroy');
								}
							}
						}else{
							if(curIsQuickSelectStrategy){
								//指定策略
								setProfileForSingleInstance(isChildStrategy);
							}else{
								alert('应用成功!');
								strategyDialog.panel('destroy');
							}
						}
						
						
					}
				})).append($('<a/>').linkbutton('RenderLB',{
					text:'取消',
					iconCls:"fa fa-times-circle",
					onClick:function(){
						strategyDialog.panel('destroy');
					}
				}));
			}
		}
		
		isFirstCreateAccordion = false;
	}
	
	//展示左右表格数据
	function showMainProfileResourceAllGrid(singleStrategyInfo){
		
		var nowDomainId = -1;

		oc.util.ajax({
			  successMsg:null,
			  url: oc.resource.getUrl('portal/strategydetail/getChildResourceForPersonalizeChildProfile.htm'),
			  data:{childProfileId:singleStrategyInfo.profileInfo.profileId,mainInstanceId:curUseQuickSelectResourceInstanceId,resourceId:singleStrategyInfo.profileInfo.resourceId,
				  interfaceState:curInterfaceState,searchContent:$('#searchResourceOrIpInputForChildProfile').val(),
				  domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
			  success:function(data){
				  
				  	curChildPageStart = data.data.rows.length;
				  
					for(var i = 0 ; i < data.data.rows.length ; i ++){
						
						//判断是否使用当前策略
						if(data.data.rows[i].strategyID == singleStrategyInfo.profileInfo.profileId){
							data.data.rows[i].checked = true;
						}
						
					}
					
					curRightResourceInstanceList = $.extend(true,[],data.data.resourceIds);
					curUserCheckedResource = $.extend(true,[],data.data.resourceIds);
					
					totalMainInstanceCount = data.data.totalRecord;
					
					pickGrid.selector.datagrid('loadData',data);
					
					if(data.data.selectStatus == 2){
						isAutoChecked = true;
						pickGrid.selector.datagrid('checkAll');
					}
					
				    //注册滚动条滚动到底部翻页事件
					var timeout;
    				
					var scrollDiv = $('#resourceSelectGridParentDiv').find('.datagrid-view2>.datagrid-body');
					scrollDiv.on('scroll',function(){
						/*清除定时器，防止滚动滚动条时多次触发请求(IE)*/
						clearTimeout(timeout);
							/*当请求完成并且在页面上显示之后才能继续请求*/
							if(!isLoadding){
								 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
								 if($(this).get(0).scrollHeight - $(this).height() <= $(this).scrollTop()){
									 timeout = setTimeout(function(){
										 showMainProfileResourceLeftGrid(singleStrategyInfo);
									 },200);
								 }
							}
					});
				  
			  }
			  
		 });
		
	}
	
	oc.personalizeprofile.strategy.detail = {
		show:function(strategyId){

			clearCacheData();
			curIsQuickSelectStrategy = false;

			nowStrategyId = strategyId;
			curInstanceIsHavePersonalizedProfile = true;
			if(strategyDialog != null){
				strategyDialog.dialog('clear');
			}
			
			refreshDialog(false,strategyId);
				
		},
		showInQuickSelect:function(profileId,quickStrategyDialog,isHavePersonalizeProfile,resourceId,instanceId){
			
			clearCacheData();
			
			strategyDialog = quickStrategyDialog;
			
			curUseQuickSelectResourceId = resourceId;
			
			curUseQuickSelectResourceInstanceId = instanceId;
			curInstanceIsHavePersonalizedProfile = isHavePersonalizeProfile; 
			curIsQuickSelectStrategy = true;
			
	    	if(curInstanceIsHavePersonalizedProfile){
	    		nowStrategyId = profileId;
	    		
	    		//有默认个性化策略
	    		oc.util.ajax({
	    			successMsg:null,
	    			url: oc.resource.getUrl('portal/strategydetail/getStrategyById.htm'),
	    			data : {strategyId:nowStrategyId},
	    			success: function(data){
	    				
	    				childProfileMaxCount = data.data.childProfileMaxCount;
	    				curInstanceDomainId = data.data.personalize_instanceDomainId;
	    				
	    				curUseQuickSelectResourceInstanceId = data.data.profileInfo.personalize_instanceId;
	    				
	    				if((data.data.profileInfo.createUser && oc.index.getUser().id != data.data.profileInfo.createUser)){
		  		  	    	//自定义策略当前用户不是创建者，不允许修改
		  		  	    	//默认策略是不是系统管理员，不允许修改
		  		  	    	isAllowUpdateAllProfile = false;
		  		  	    }else{
		  		  	    	isAllowUpdateAllProfile = true;
		  		  	    }
	    				curProfileDomainId = data.data.profileInfo.domainId;
	    				curMainProfileId = profileId;
	    				curProfileData = data.data;
	    				initDetail();
	    				initAccrodion(false,-1);
	    				
	    			}
	    			
	    		});
	    		
	    	}else{
	    		
	    		nowStrategyId = -1;
	    		lastSelectDefaultOrSpecialProfile = profileId;
	    		isAllowUpdateAllProfile = false;
	    		
	    		//复制修改本地数据
	    		oc.util.ajax({
	    			successMsg:null,
	    			url: oc.resource.getUrl('portal/strategydetail/getEmptyPersonalizeProfile.htm'),
	    			data : {resourceId:resourceId,instanceId:curUseQuickSelectResourceInstanceId},
	    			success: function(data){
	    				
	    				curProfileData = data.data;
	    				initDetail();
	    				initAccrodion(false,-1);
	    				
	    			}
	    			
	    		});
	    		
	    	}
		},
		clearCurSelectChildProfileResourceIndex : function(){
			curSelectChildProfileResourceIndex = null;
		},
		beforeColse:function(){
	    	var curIsChildStrategy = null;
	    	if(curStrategyIndex == -1){
	    		curIsChildStrategy = false;
	    	}else{
	    		curIsChildStrategy = true;
	    	}

	    	//检查是否修改
			var isPass = checkAllInfoIsModifyForPersonalize(curIsChildStrategy,3,curStrategyIndex);
			if(!isPass){
				return false;
			}
			
	    	return true;
		}
	};
	
	//检查个性化是否有数据被修改
	function checkAllInfoIsModifyForPersonalize(isChildStrategy,nextShowSelectIndex,strategyIndex){
		var singleStrategyInfo = null;
		if(isChildStrategy){
			singleStrategyInfo = curProfileData.children[strategyIndex];
		}else{
			singleStrategyInfo = curProfileData;
		}
		
		//检查是否修改
		var updateInfo = checkProfileUpdateInfo(singleStrategyInfo,isChildStrategy);
		
		if(updateInfo == -1){
			//有阈值修改未完成
			alert('有阈值未修改完成,请修改!');
			return;
		}
		
		if(updateInfo.modifyInfo || updateInfo.modifyMetricInfo || updateInfo.modifyAlarmInfo || updateInfo.modifyResourceInfo){
			
			var comfirmDialog = $('<div/>').dialog({
			    title: '确认消息',
			    width: 220,
			    height: 120,
			    content:'策略信息已被修改，是否保存？',
			    modal: true,
			    closable:false,
			    buttons:[{
			    	text:'确定',
			    	iconCls:"fa fa-check-circle",
					handler:function(){
						
						updateProfileTotalInfo(updateInfo,nextShowSelectIndex,isChildStrategy,strategyIndex);
						
						comfirmDialog.panel('destroy');
						
					}
			    },{
			    	text:'取消',
			    	iconCls:"fa fa-times-circle",
			    	handler:function(){
			    		//取消
						showNextOperated(nextShowSelectIndex);
						oc.resource.alarmrules.reSetPage();
						comfirmDialog.panel('destroy');
			    	}
			    }]
			});
			
			return false;
		}else{
			return true;
		}
		
		return true;
		
	}
	
	//检查非默认策略的基本信息是否修改
	function checkBaseInfoIsModify(strategyInstance){
		//检查子策略名称
		var isModify = false;
		var updateInfo = {};
		if($('#' + strategy_base_info_input_name).val() && strategyInstance.profileInfo.profileName.trim() != $('#' + strategy_base_info_input_name).val().trim()){
			isModify = true;
			updateInfo.profileName = $('#' + strategy_base_info_input_name).val().trim();
		}
		//检查策略备注
		if($('#' + strategy_base_info_input_desc).val()){
			var nowBaseDescInfo = $('#' + strategy_base_info_input_desc).val().trim();
			if(strategyInstance.profileInfo.profileDesc != nowBaseDescInfo && !(!strategyInstance.profileInfo.profileDesc && nowBaseDescInfo == '')){
				isModify = true;
				updateInfo.profileDesc = nowBaseDescInfo;
			}
		}
		
		if(isModify){
			//已经修改
			updateInfo.profileId = strategyInstance.profileInfo.profileId;
			return updateInfo;
			
		}else{
			return null;
		}
		
		
		
	}
	
	//检查主策略的主资源选择是否修改
	function checkStrategyChildResourceIsModify(strategyInstance){

		if(curUserCheckedResource.length == 0 && pickGrid.selector.datagrid('getRows').length == 0){
			//没有改变
			return null;
		}

		var resourceInstanceIdArray = new Array();
		
		var checkedResourceIDs = '';
		
		for(var x = 0 ; x < curUserCheckedResource.length ; x++){
			
			checkedResourceIDs += curUserCheckedResource[x] + ',';
			resourceInstanceIdArray.push(curUserCheckedResource[x]);
			
		}
			
		checkedResourceIDs = checkedResourceIDs.substring(0,checkedResourceIDs.length - 1);
		
		if(curRightResourceInstanceList.length == 0 && resourceInstanceIdArray.length == 0){
			return null;
		}
		
		//找出右边多出的资源
		var rightAddResourceArray = resourceInstanceIdArray.concat();
		outFor : for(var index = 0 ; index < curRightResourceInstanceList.length ; index ++){
			
			var selectResourceId = curRightResourceInstanceList[index];
			for(var j = 0 ; j < rightAddResourceArray.length ; j ++){
				
				if(selectResourceId == rightAddResourceArray[j]){
					
					rightAddResourceArray.splice(j,1);
					
				}
				
			}
			
			if(rightAddResourceArray.length == 0){
				break outFor;
			}
			
		}
		
		//找出右边减少的资源
		var rightReduceResourceArray = curRightResourceInstanceList.concat();
		outFor : for(var index2 = 0 ; index2 < resourceInstanceIdArray.length ; index2 ++){
			
			var selectResourceId2 = resourceInstanceIdArray[index2];
			for(var j2 = 0 ; j2 < rightReduceResourceArray.length ; j2 ++){
				
				if(selectResourceId2 == rightReduceResourceArray[j2]){
					
					rightReduceResourceArray.splice(j2,1);
					
				}
				
			}
			
			if(rightReduceResourceArray.length == 0){
				break outFor;
			}
			
		}
		
		if(rightReduceResourceArray.length <= 0 && rightAddResourceArray.length <= 0){
			//没有改变
			return null;
		}
		
		var resourcesParameter = {};
		
		resourcesParameter.profileId = strategyInstance.profileInfo.profileId;
		resourcesParameter.resources = rightAddResourceArray.join();
		resourcesParameter.reduceResources = rightReduceResourceArray.join();
		return resourcesParameter;
		
	}
	
	//检查指标是否修改
	function checkStrategyMetricIsModify(strategyInstance){
		
		var monitorMap = '';
		var alarmsMap = '';
		var frequencyValueMap = '';
		var thresholdsMap = '';
		var flappingValueMap = '';
		
		//确定
		var metricThresholdsNum = -1;
		for(var i = 0 ; i < strategyInstance.metricSetting.metrics.length ; i ++){
			var metricId = strategyInstance.metricSetting.metrics[i].metricId;
			//原始数据
			var metrics = strategyInstance.metricSetting.metrics[i];
			var isMonitor = metrics.monitor;
			var isAlarm = metrics.alarm;
			var oldDictFrequencyId = metrics.dictFrequencyId;
			var oldAlarmFlapping = metrics.alarmFlapping;
			
			var nowAlarmFlapping = alarmFlappingArray[i].combobox('getValue');
			
			if(oldAlarmFlapping != nowAlarmFlapping){
				flappingValueMap += '"' +metricId + '":' + nowAlarmFlapping + ',';
			}
//			var oldThreshold1 = null;
//			var oldThreshold2 = null;
//			if(metrics.metricThresholds != null && metrics.metricThresholds.length > 0){
//				metricThresholdsNum++;
//				
//				var metricThresholdsArray = metrics.metricThresholds;
//				var thresholdMK_Id1 = null;
//				var thresholdMK_Id2 = null;
//				
//				for(var t = 0 ; t < metricThresholdsArray.length ; t++){
//					if(metricThresholdsArray[t].perfMetricStateEnum == 'Minor'){
//						thresholdMK_Id1 = metricThresholdsArray[t].threshold_mkId;
//						oldThreshold1 =  metricThresholdsArray[t].thresholdValue;
//					}else if(metricThresholdsArray[t].perfMetricStateEnum == 'Major'){
//						thresholdMK_Id2 = metricThresholdsArray[t].threshold_mkId;
//						oldThreshold2 =  metricThresholdsArray[t].thresholdValue;
//					}
//				}
//				
//				var ionValue = ionRangeSlider[metricThresholdsNum].getTargetValue();
//				
//				var nowThreshold1 = null;
//				var nowThreshold2 = null;
//				
//				nowThreshold1 = ionValue[1];
//				nowThreshold2 = ionValue[2];
//				
//				if(nowThreshold1 == undefined || nowThreshold2 == undefined){
//					alert('获取阈值组件值失败!');
//					return null;
//				}
//				
//				if(oldThreshold1 != nowThreshold1){
//					
//					
//					thresholdsMap += '{"threshold_mkId":' + thresholdMK_Id1 + ',"thresholdValue":"' + nowThreshold1 + '"},' +
//						'{"threshold_mkId":' + thresholdMK_Id2 + ',"thresholdValue":"' + nowThreshold2 + '"},';
//						
//					
//				}else if(oldThreshold2 != nowThreshold2){
//					
//						
//					thresholdsMap += '{"threshold_mkId":' + thresholdMK_Id1 + ',"thresholdValue":"' + nowThreshold1 + '"},' +
//						'{"threshold_mkId":' + thresholdMK_Id2 + ',"thresholdValue":"' + nowThreshold2 + '"},';
//					
//					
//				}
//					
//				
//			}
				
			//现在数据
			var nowIsAlarm = null;
			if($('#' + alarm_check_box + i).prop('checked')){
				nowIsAlarm = true;
			}else{
				nowIsAlarm = false;
			}
			
			if(isAlarm != nowIsAlarm){
				alarmsMap += '"' + metricId + '":' + nowIsAlarm + ',';
			}
			
			var nowIsMonitor = null;
			if($('#' + monitor_check_box + i).prop('checked')){
				nowIsMonitor = true;
			}else{
				nowIsMonitor = false;
			}
			if(isMonitor != nowIsMonitor){
				monitorMap += '"' + metricId + '":' + nowIsMonitor + ',';
			}
			
			
			var nowFrequency = frequencyComboxs[i].combobox('getValue');
			
			if(oldDictFrequencyId != nowFrequency){
				frequencyValueMap += '"' + metricId + '":"' + nowFrequency + '",';
			}
			
		}
		
		if(thresholdsMap != '' || monitorMap != '' || alarmsMap != '' || frequencyValueMap != '' || flappingValueMap != ''){
			
			if(thresholdsMap.indexOf(',') != -1){
				thresholdsMap = thresholdsMap.substr(0,thresholdsMap.length - 1);
			}
			
			if(monitorMap.indexOf(',') != -1){
				monitorMap = monitorMap.substr(0,monitorMap.length - 1);
			}
			
			if(alarmsMap.indexOf(',') != -1){
				alarmsMap = alarmsMap.substr(0,alarmsMap.length - 1);
			}
			
			if(frequencyValueMap.indexOf(',') != -1){
				frequencyValueMap = frequencyValueMap.substr(0,frequencyValueMap.length - 1);
			}
			
			if(flappingValueMap.indexOf(',') != -1){
				flappingValueMap = flappingValueMap.substr(0,flappingValueMap.length - 1);
			}
			
			var resultData = '{"profileId":' + strategyInstance.profileInfo.profileId + ',"monitorMap":{' + monitorMap + '},"alarmsMap":{' + alarmsMap + '},"frequencyValueMap":{' + frequencyValueMap + '},"flappingValueMap":{' + flappingValueMap + '}}';
			return resultData;
		}

		
		return null;
	}
	
	//重新加载dialog
	function refreshDialog(isChildStrategy,profileId){
		curSelectChildProfileResourceIndex = null;
		var nowHeight = '575px';
		strategyDialog = $('<div/>').dialog({
			width:'1100px',
			height:nowHeight,
		    title: '编辑监控策略',
		    href: oc.resource.getUrl('resource/module/resource-management/resource_strategy_detail.html'),
		    onLoad:function(){
		    	
				//请求策略信息
		    	oc.util.ajax({
		    		  successMsg:null,
			  		  url: oc.resource.getUrl('portal/strategydetail/getStrategyById.htm'),
			  		  data : {strategyId:profileId},
			  		  success: function(data){
			  			  
			  			  	childProfileMaxCount = data.data.childProfileMaxCount;
			  			  curInstanceDomainId = data.data.personalize_instanceDomainId;
			  			  
			  		  		isAllowUpdateAllProfile = true;
		  			  		curUseQuickSelectResourceInstanceId = data.data.profileInfo.personalize_instanceId;
		  			  		
			  		  	    if((data.data.profileInfo.createUser && oc.index.getUser().id != data.data.profileInfo.createUser)){
			  		  	    	//自定义策略当前用户不是创建者，不允许修改
			  		  	    	//默认策略是不是系统管理员，不允许修改
			  		  	    	isAllowUpdateAllProfile = false;
			  		  	    }else{
			  		  	    	isAllowUpdateAllProfile = true;
			  		  	    }
			  			    curProfileDomainId = data.data.profileInfo.domainId;
			  			    curMainProfileId = profileId;
			  			  	curProfileData = data.data;
				  			initDetail();
				  			
					    	initAccrodion(isChildStrategy,-1);
				    	
			  		  }
		    	
		    	});
		    	
		    	
		    },
		    onBeforeClose:function(){
		    	var curIsChildStrategy = null;
		    	if(curStrategyIndex == -1){
		    		curIsChildStrategy = false;
		    	}else{
		    		curIsChildStrategy = true;
		    	}

		    	//检查是否修改
				var isPass = checkAllInfoIsModifyForPersonalize(curIsChildStrategy,3,curStrategyIndex);
				if(!isPass){
					return false;
				}
				
		    	return true;
		    },
		    onDestroy:function(){
		    	oc.resource.alarmrules.reSetPage();
		    }
		});
		
	}
	
	//重新加载没有关闭的dialog
	function refreshDialogDontClose(isChildStrategy,profileId){
		var nowHeight = '575px';
		strategyDialog.panel('destroy');
		strategyDialog = $('<div/>').dialog({
			width:'1100px',
			height:nowHeight,
			title: '编辑监控策略',
		    href: oc.resource.getUrl('resource/module/resource-management/resource_strategy_detail.html'),
		    onLoad:function(){
		    	//判断当前资源实例是否有个性化策略
		    	if(curInstanceIsHavePersonalizedProfile){
		    		
		    		//有默认个性化策略
		    		oc.util.ajax({
		    			successMsg:null,
		    			url: oc.resource.getUrl('portal/strategydetail/getStrategyById.htm'),
		    			data : {strategyId:nowStrategyId},
		    			success: function(data){
		    				
		    				childProfileMaxCount = data.data.childProfileMaxCount;
		    				curInstanceDomainId = data.data.personalize_instanceDomainId;
		    					
		    				curUseQuickSelectResourceInstanceId = data.data.profileInfo.personalize_instanceId;
		    				
		    				if((data.data.profileInfo.createUser && oc.index.getUser().id != data.data.profileInfo.createUser)){
			  		  	    	//自定义策略当前用户不是创建者，不允许修改
			  		  	    	//默认策略是不是系统管理员，不允许修改
			  		  	    	isAllowUpdateAllProfile = false;
			  		  	    }else{
			  		  	    	isAllowUpdateAllProfile = true;
			  		  	    }
		    				curProfileDomainId = data.data.profileInfo.domainId;
		    				curMainProfileId = profileId;
		    				curProfileData = data.data;
		    				initDetail();
		    				initAccrodion(isChildStrategy,-1);
		    				
		    			}
		    			
		    		});
		    		
		    	}else{
		    		
		    		//复制修改本地数据
		    		oc.util.ajax({
		    			successMsg:null,
		    			url: oc.resource.getUrl('portal/strategydetail/getEmptyPersonalizeProfile.htm'),
		    			data : {resourceId:curProfileData.profileInfo.resourceId,instanceId:curUseQuickSelectResourceInstanceId},
		    			success: function(data){
		    				
		    				curProfileData = data.data;
		    				initDetail();
		    				initAccrodion(isChildStrategy,-1);
		    				
		    			}
		    			
		    		});
		    		
		    	}
		    	
		    },
		    onBeforeClose:function(){
		    	var curIsChildStrategy = null;
		    	if(curStrategyIndex == -1){
		    		curIsChildStrategy = false;
		    	}else{
		    		curIsChildStrategy = true;
		    	}
		    	
		    	//检查是否修改
		    	var isPass = checkAllInfoIsModifyForPersonalize(curIsChildStrategy,3,curStrategyIndex);
		    	if(!isPass){
		    		return false;
		    	}
		    	
		    	return true;
		    },
		    onDestroy:function(){
		    	oc.resource.alarmrules.reSetPage();
		    }
		});
		
	}
	
	//展示主资源左表格
	function showMainProfileResourceLeftGrid(singleStrategyInfo){

		var nowDomainId = -1;
		
		//判断主策略的资源列表还是子策略的资源列表
		var ajaxUrl,dataParam,loadGrid;
		//子策略的资源列表
		ajaxUrl = oc.resource.getUrl('portal/strategydetail/getChildResourceForPersonalizeChildProfile.htm'),
		dataParam = {childProfileId:singleStrategyInfo.profileInfo.profileId,mainInstanceId:curUseQuickSelectResourceInstanceId,resourceId:singleStrategyInfo.profileInfo.resourceId,
			  interfaceState:curInterfaceState,searchContent:$('#searchResourceOrIpInputForChildProfile').val(),
			  domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
		loadGrid = pickGrid;
		
		oc.util.ajax({
			  successMsg:null,
			  url: ajaxUrl,
			  data:dataParam,
			  success:function(data){
				    var nowStart = curChildPageStart;
				  	isLoadding = true;
				    if(curChildPageStart > 0 && (!data.data.rows || data.data.rows.length <= 0)){
				    	alert('所有数据已加载完毕');
				    	isLoadding = false;
				    	return;
				    }else if(curChildPageStart > 0){
				    	curChildPageStart = curChildPageStart + data.data.rows.length;
				    }else{
				    	curChildPageStart = data.data.rows.length;
				    }
				  
					for(var i = 0 ; i < data.data.rows.length ; i ++){
						
						//判断是否是用户操作勾选资源
						for(var j = 0 ; j < curUserCheckedResource.length ; j ++){
							if(curUserCheckedResource[j] == data.data.rows[i].resourceId){
								data.data.rows[i].checked = true;
								break;
							}
						}
						
						//判断是否使用当前策略和是否被用户取消勾选
						if(data.data.rows[i].strategyID == curProfileData.profileInfo.profileId && !data.data.rows[i].checked){
							var isAdd = true;
							for(var j = 0 ; j < curUserUnCheckedResource.length ; j ++){
								if(data.data.rows[i].resourceId == curUserUnCheckedResource[j]){
									//被用户取消勾选
									isAdd = false;
									break;
								}
							}
							if(isAdd){
								data.data.rows[i].checked = true;
							}
						}
						
						//如果是翻页，则追到到表格最后
						if(nowStart > 0){
							loadGrid.selector.datagrid('appendRow',data.data.rows[i]);
					    	if(data.data.rows[i].checked){
  								isAutoChecked = true;
  								loadGrid.selector.datagrid('checkRow',loadGrid.selector.datagrid('getRowIndex',data.data.rows[i]));
					    	}
						}
						
					}
					if(nowStart <= 0){
						loadGrid.selector.datagrid('loadData',data);
						
						if(data.data.selectStatus == 2){
							isAutoChecked = true;
							loadGrid.selector.datagrid('checkAll');
						}
					}
					
					isLoadding = false;
				  
			  }
			  
		 });
		
	}
	
	//继续下一步操作
	function showNextOperated(code,isChildStrategy){

		if(code == 3){
			//关闭dialog或者点击保存按钮
			strategyDialog.panel('destroy');
		}else if(code == 2){
			//应用按钮判断是否需要为资源指定策略
			if(curIsQuickSelectStrategy){
				setProfileForSingleInstance(isChildStrategy);
			}else{
				strategyDialog.panel('destroy');
			}
		}else if(code == 1){
			//点击主策略
			initAccrodion(false,-1);
			strategyTitle.text("");
		}else if(code <= 0){
			//点击子策略
			initAccrodion(true,-code);
			strategyTitle.text(curProfileData.profileInfo.profileName);
		}
		
	}
	
	//渲染指标表格
	function showMetricGrid(singleStrategyInfo){

		//判断是否域管理员,域管理员不能修改指标信息
		var isAllowUpdateMetricData = true;
		var inputDisabled = "";
		if(!isAllowUpdateAllProfile){
			isAllowUpdateMetricData = false;
		}
		
		
		var dataGridCfg = {
				selector:$('#indicatorsDefiendGridId'),
				pagination:false,
				checkOnSelect:false,
				fitColumns:false,
				noDataMsg:'该子资源只有信息指标进行监控',
				columns:[[
			         {field:'monitor',title:'<input type="checkbox" id="monitorTitleCheckBox" name="monitorCheckBoxName" value=2>监控',width:64},
			         {field:'alarm',title:'<input type="checkbox" id="alarmTitleCheckBox" name="alarmCheckBoxName" value=3>告警',width:64},
			         {field:'meticsName',title:'指标',width:223,ellipsis:true},
			         {field:'metricTypeEnum',title:'指标类型',width:114},
			         {field:'monitorFrequency',title:'监控频度',width:149,formatter:function(value,row,index,div){
	         					//渲染combox
								var frequencyCombox = $("<select/>").appendTo(div).combobox({
									 width:75,
									 placeholder:false,
									 selected:false,
									 valueField:'id',    
		    			    		 textField:'name' ,
									 disabled:!isAllowUpdateMetricData,
									 value:value,
									 data:row.supportFrequentList
								});
								
								frequencyComboxs.push(frequencyCombox);
			         	}
			         },
			         {field:'alarmFlapping',title:'告警Flapping',width:129,formatter:function(value,row,index,div){
			         		
			         		if(value){
			         		
			         			var isAllowUpdateFlapping = false;
			         			var isAddAvailiabelFlapping = true;
			         			
			         			isAllowUpdateFlapping = !isAllowUpdateMetricData;
//			         			if(row.metricTypeEnum == '信息指标'){
//			         				isAllowUpdateFlapping = true;
//			         			}
			         		
								var alarmFlappingSingle = $("<select/>").appendTo(div).combobox({
									width:45,
									selected:false,
									editable:false,
									readonly:isAllowUpdateFlapping,
									placeholder:false,
									value:value,
									valueField:'id',    
		    			    		textField:'name' ,
									data:[{'id':1,'name':'1'},{'id':2,'name':'2'},{'id':3,'name':'3'},{'id':4,'name':'4'},{'id':5,'name':'5'}]
								});
								
								if(isAddAvailiabelFlapping){
									alarmFlappingArray.push(alarmFlappingSingle);
								}
			         		}
			         		
			         	}
			         },
			         {field:'metricThresholds',title:'阈值定义',width:122}
			     ]],
			     onClickCell:function(index,field,value){
			    	 if(!isAllowUpdateMetricData){
			    		 if((curProfileData.profileInfo.createUser && oc.index.getUser().id != curProfileData.profileInfo.createUser)){
			    			 alert('没有对该个性化策略的修改权限！');
			    		 }else{
			    			 alert('个性化策略未创建！');
			    		 }
			    	 }
			     }
				
			};
		
		indicatorsDefiendGrid = oc.ui.datagrid(dataGridCfg);
		var rowsMetics = new Array();
		
		//初始化监控是否全部选中
		var initIsCheckAllMonitor = true;
		//初始化告警是否全部选中
		var initIsCheckAllAlarm = true;
		
		//初始化告警是否全部不可用
		var initIsDisabledAllAlarm = true;
		
		//展示指标信息
		var nowIonSliderIndex = 0;

		for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
			
			inputDisabled = '';
			
			if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'AvailabilityMetric' || !isAllowUpdateMetricData){
				//域管理员和默认策略不能修改，或者可用性指标不能修改
				inputDisabled = "disabled='true'";
			}else{
				inputDisabled = '';
			}
			
			var monitorValue = "<input class='monitorCheckBoxClass' id='"+ monitor_check_box + i + "' type='checkbox' value=1 " + inputDisabled + ">";
			
			if(singleStrategyInfo.metricSetting.metrics[i].monitor){
				
				//需要监控
				monitorValue = "<input class='monitorCheckBoxClass'  id='" + monitor_check_box + i + "' type='checkbox' checked='checked' value=1 " + inputDisabled + ">";
				
			}else{
				//没有监控,告警框不可用
				initIsCheckAllMonitor = false;
				inputDisabled = "disabled='true'";
			}
			
			
			if(curStrategyIndex >= 0 && isAllowUpdateMetricData && singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'AvailabilityMetric'){
				//子策略，有修改权限，可应用指标，允许修改告警
				inputDisabled = '';
			}
			
			var alarmValue = "<input class='alarmCheckBoxClass' id='" + alarm_check_box + i + "' type='checkbox' value=1 " + inputDisabled + ">";
			
			if(!inputDisabled){
				//有一个告警框可用
				initIsDisabledAllAlarm = false;
			}

			if(singleStrategyInfo.metricSetting.metrics[i].alarm){
				
				//需要告警
				alarmValue = "<input class='alarmCheckBoxClass' id='" + alarm_check_box + i + "' type='checkbox' checked='checked' value=1 " + inputDisabled + ">";
				
			}else{
				if(!inputDisabled){
					initIsCheckAllAlarm = false;
				}
			}
			//信息指标不勾选，不可编辑
//			if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'InformationMetric'){
//				alarmValue = "<input class='alarmCheckBoxClass' id='" + alarm_check_box + i + "' type='checkbox' value=1 disabled='true'>";
//			}
			
			var metricNameAndUnit = null;
			if(singleStrategyInfo.metricSetting.metrics[i].unit == null || singleStrategyInfo.metricSetting.metrics[i].unit == ''){
				metricNameAndUnit = singleStrategyInfo.metricSetting.metrics[i].metricName;
			}else{
				metricNameAndUnit = singleStrategyInfo.metricSetting.metrics[i].metricName + '(' + singleStrategyInfo.metricSetting.metrics[i].unit + ')'
			}
			
			//指标类型
			var metricType = null;
			
			if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'AvailabilityMetric'){
				//可用性指标也展示为信息指标
				metricType = '信息指标';
			}else if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'InformationMetric'){
				metricType = '信息指标';
			}else{
				metricType = '性能指标';
			}
//			if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds == null || singleStrategyInfo.metricSetting.metrics[i].metricThresholds.length==0){
//					
//					rowsMetics.push({"monitor":monitorValue,"alarm":alarmValue,"metricTypeEnum":metricType,
//						"meticsName":metricNameAndUnit,"monitorFrequency":singleStrategyInfo.metricSetting.metrics[i].dictFrequencyId,
//						"metricThresholds":"","alarmFlapping":singleStrategyInfo.metricSetting.metrics[i].alarmFlapping,"supportFrequentList":singleStrategyInfo.metricSetting.metrics[i].supportFrequentList}); 
//					
//			}else{
//					
//					rowsMetics.push({"monitor":monitorValue,"alarm":alarmValue,"metricTypeEnum":metricType,
//						"meticsName":metricNameAndUnit,"monitorFrequency":singleStrategyInfo.metricSetting.metrics[i].dictFrequencyId,
//						"metricThresholds":"<div id='rangeSliderInput" + thresholdsNum + "' ></div>","alarmFlapping":singleStrategyInfo.metricSetting.metrics[i].alarmFlapping,
//						"supportFrequentList":singleStrategyInfo.metricSetting.metrics[i].supportFrequentList}); 
//					
//					thresholdsNum++;
//					
//			}
			
			rowsMetics.push({"monitor":monitorValue,"alarm":alarmValue,"metricTypeEnum":metricType,
				"meticsName":metricNameAndUnit,"monitorFrequency":singleStrategyInfo.metricSetting.metrics[i].dictFrequencyId,
				"metricThresholds":"<div id='rangeSliderInput" + i + "' ></div>","alarmFlapping":singleStrategyInfo.metricSetting.metrics[i].alarmFlapping,
				"supportFrequentList":singleStrategyInfo.metricSetting.metrics[i].supportFrequentList}); 



		}
		
		var localData = {"data":{"total":0,"rows":rowsMetics},"code":200};
		indicatorsDefiendGrid.selector.datagrid('loadData',localData);
		
		$('#monitorTitleCheckBox').unbind();
		$('#alarmTitleCheckBox').unbind();
		//监听告警和监控列表头的checkbox点击事件
		$('#monitorTitleCheckBox').on('click',function(e){
			$('#alarmTitleCheckBox').removeAttr('disabled');
			if($(e.target).prop('checked')){
				$('.monitorCheckBoxClass').each(function(){
					var nowIndex = $(this).attr('id').replace(monitor_check_box,'');
					if(!$(this).attr('disabled')){
					    $(this).prop('checked',true);
					    //告警勾选,监控可用
					    $('#alarmTitleCheckBox').removeAttr('checked');
//					    if(rowsMetics[nowIndex].metricTypeEnum!='信息指标'){
					    	$('#' + alarm_check_box + nowIndex).removeAttr('disabled');
//					    }
					}
				});
			}else if(!$(e.target).prop('checked')){
				$('#alarmTitleCheckBox').attr('disabled',true);
				var isUnCheckAlarmTitle = false;
				$('.monitorCheckBoxClass').each(function(){
					var nowIndex = $(this).attr('id').replace(monitor_check_box,'');
					if(!$(this).attr('disabled')){
					    $(this).prop('checked',false);
					    //告警取消勾选,监控不可用
						$('#' + alarm_check_box + nowIndex).removeAttr('checked');
						$('#' + alarm_check_box + nowIndex).attr('disabled','true');
						isUnCheckAlarmTitle = true;
					}
				});
				if(isUnCheckAlarmTitle){
					$('#alarmTitleCheckBox').removeAttr('checked');
				}
			}
		});
		
		$('#alarmTitleCheckBox').on('click',function(e){
			if($(e.target).prop('checked')){
				$('.alarmCheckBoxClass').each(function(){
					if(!$(this).attr('disabled')){
					    $(this).prop('checked',true);
					}
				});
			}else if(!$(e.target).prop('checked')){
				$('.alarmCheckBoxClass').each(function(){
					if(!$(this).attr('disabled')){
					    $(this).prop('checked',false);
					}
				});
			}
		});
		$('.monitorCheckBoxClass').on('click',function(e){
			
			var nowIndex = $(e.target).attr('id').replace(monitor_check_box,'');
			
			if($(e.target).prop('checked')){
				$('#alarmTitleCheckBox').removeAttr('disabled');
				$('#alarmTitleCheckBox').removeAttr('checked');
				var isCheckedAll = true;
				
				for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
					//查看是否全部选中
					if(!$('#' + monitor_check_box + i).prop('checked')){
						isCheckedAll = false;
						break;
					}
				}
				
				if(isCheckedAll){
					$('#monitorTitleCheckBox').prop('checked',true);
				}
				
				//监控勾选则联动告警可勾选
				$('#' + alarm_check_box + nowIndex).removeAttr('disabled');
				
			}else if(!$(e.target).prop('checked')){
				if($('#monitorTitleCheckBox').prop('checked')){
					$('#monitorTitleCheckBox').prop('checked',false);
				}
				//监控取消勾选则联动告警不可用
				$('#' + alarm_check_box + nowIndex).removeAttr('checked');
				$('#' + alarm_check_box + nowIndex).attr('disabled','true');
			}
			
		});
		$('.alarmCheckBoxClass').on('click',function(e){
			
			if($(e.target).prop('checked')){
				
				var isCheckedAll = true;
				
				for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
					//查看是否全部选中
					if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds == null){
						continue;
					}
					
					if(!$('#' + alarm_check_box + i).prop('checked')){
						isCheckedAll = false;
						break;
					}
				}
				
				if(isCheckedAll){
					$('#alarmTitleCheckBox').prop('checked',true);
				}
				
			}else if(!$(e.target).prop('checked')){
				if($('#alarmTitleCheckBox').prop('checked')){
					$('#alarmTitleCheckBox').prop('checked',false);
				}
			}
			
		});
		for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
			
//			if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds != null && singleStrategyInfo.metricSetting.metrics[i].metricThresholds.length > 0){
//				var idName = '#rangeSliderInput' + nowIonSliderIndex;
//				var singleIonRangeSlider = null;
//				
//				var value_1 = null;
//				var value_2 = null;
//				for(var t = 0 ; t < singleStrategyInfo.metricSetting.metrics[i].metricThresholds.length ; t++){
//					if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].perfMetricStateEnum == 'Minor'){
//						value_1 =  singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].thresholdValue;
//					}else if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].perfMetricStateEnum == 'Major'){
//						value_2 =  singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].thresholdValue;
//					}
//				}
//				singleIonRangeSlider = $(idName).target([singleStrategyInfo.metricSetting.metrics[i].unit,value_1,value_2],!isAllowUpdateMetricData,true);
//				
//				ionRangeSlider.push(singleIonRangeSlider);
//				
//				nowIonSliderIndex ++;
//				
//			}
			
			if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum != 'AvailabilityMetric'){
				var idName = '#rangeSliderInput' + i;
				var metricShowName = singleStrategyInfo.metricSetting.metrics[i].metricName;
				if(singleStrategyInfo.metricSetting.metrics[i].unit){
					metricShowName += '(' + singleStrategyInfo.metricSetting.metrics[i].unit + ')';
				}
				$(idName).append('<a metricname="' + metricShowName + '" metricid="' + singleStrategyInfo.metricSetting.metrics[i].metricId + '" class="light_blue icon-threshold updateThresholdClass" title="编辑阈值"></a>');
			}
			
		}
		
		
		if(isAllowUpdateMetricData){
			$('.updateThresholdClass').click(function(){
				var metricInfo = $(this).attr('metricid');
				var metricName = $(this).attr('metricname');
				oc.resource.loadScript('resource/module/resource-management/js/strategy_threshold_setting.js',function(){
					oc.module.resourcemanagement.strategythresholdsetting.open({profileId:singleStrategyInfo.profileInfo.profileId,metricName:metricName,metricId:metricInfo,readOnly:!isAllowUpdateMetricData});
				});
			});
		}
				
		//判断监控是否全部选中
		if(initIsCheckAllMonitor){
			$('#monitorTitleCheckBox').prop('checked',true);
		}
		
		//判断告警是否全部选中
		if(initIsCheckAllAlarm){
			if(initIsDisabledAllAlarm){
				$('#alarmTitleCheckBox').prop('checked',false);
				$('#alarmTitleCheckBox').attr('disabled',true);
			}else{
				$('#alarmTitleCheckBox').prop('checked',true);
			}
		}
		
		$.messager.progress('close');
		curMetricGridIsCreate = true;
	}
	
	function updateProfileTotalInfo(updateInfo,nextShowSelectIndex,isChildStrategy,strategyIndex){
	
		var nowTimestamp = Date.parse(new Date());
		//确定
		oc.util.ajax({
			  successMsg:null,
			  url: oc.resource.getUrl('portal/strategydetail/updateProfile.htm'),
			  data:{mainProfileId:nowStrategyId,profileInfo:updateInfo.modifyInfo,profileInfoVo:updateInfo.modifyResourceInfo,metricString:updateInfo.modifyMetricInfo,updateTime:nowTimestamp},
			  success:function(data){
				  
				  if(data.data){
					  alert('修改成功!');
					  
					  //判断告警策略是否需要修改
					  if(updateInfo.modifyAlarmInfo){
					  		oc.resource.alarmrules.saveAlarmSendSet();
					  }
					  
			    	  if(nextShowSelectIndex >= 2){
			    			showNextOperated(nextShowSelectIndex,isChildStrategy);
			    			return;
			    	  }
			    	  
			    	  curProfileData = data.data;
			    	  
					  showNextOperated(nextShowSelectIndex);
				  }else{
					  
					  alert('修改失败!');
					  
				  }
				  
			  }
			  
		 });
		
	}
	
	//获取当前指标信息
	function getCurrentMetricInfo(oldStrategyInstance){

		var strategyInstance = $.extend(true,{},oldStrategyInstance);
		
		if(curMetricGridIsCreate){
			var metricThresholdsNum = -1;
			for(var i = 0 ; i < strategyInstance.metricSetting.metrics.length ; i ++){
				//现在数据
				var nowIsMonitor = null;
				if($('#' + monitor_check_box + i).prop('checked')){
					nowIsMonitor = true;
				}else{
					nowIsMonitor = false;
				}
				
				var nowIsAlarm = null;
				if($('#' + alarm_check_box + i).prop('checked')){
					nowIsAlarm = true;
				}else{
					nowIsAlarm = false;
				}
				
				var nowFrequency = frequencyComboxs[i].combobox('getValue');
				
				var newMetric = {};
				newMetric.monitor = nowIsMonitor;
				newMetric.alarm = nowIsAlarm;
				newMetric.dictFrequencyId = nowFrequency;
				
				var nowAlarmFlapping = alarmFlappingArray[i].combobox('getValue');
				newMetric.alarmFlapping = nowAlarmFlapping;
				
//				if(strategyInstance.metricSetting.metrics[i].metricThresholds != null && strategyInstance.metricSetting.metrics[i].metricThresholds.length > 0){
//					metricThresholdsNum++;
//					var ionValue = ionRangeSlider[metricThresholdsNum].getTargetValue();
//
//					var nowThreshold1 = null;
//					var nowThreshold2 = null;
//					
//					strategyInstance.metricSetting.metrics[i].metricThresholds[1].thresholdValue = ionValue[1] + "";
//					strategyInstance.metricSetting.metrics[i].metricThresholds[2].thresholdValue = ionValue[2] + "";
//					
//			
//				}
				
				strategyInstance.metricSetting.metrics[i] = $.extend(strategyInstance.metricSetting.metrics[i],newMetric);
				
			}
			
		}
		
		return strategyInstance.metricSetting.metrics;
	
		
	}
	
	function clearCacheData(){
			curIsQuickSelectStrategy = null;
			curRightResourceInstanceList = null;
			curUserCheckedForChildResourceInChildProfile = null;
			curUserCheckedForAllChildResourceInChildProfile = null;
			curInstanceIsHavePersonalizedProfile = null;
			curIsUsePersonalizedProfile = null;
			curUseQuickSelectResourceId = null;
			curInstanceDomainId = null;
	}
	
	//为资源快速指定策略
	function setProfileForSingleInstance(isChildStrategy){
		
		//应用个性化策略
		if(oc.quick.strategy.detail.getCurIsUsePersonalizedProfile()){
			//之前已使用性化策略
			alert('应用成功!');
			strategyDialog.panel('destroy');
		}else if(curInstanceIsHavePersonalizedProfile){
			//之前已有性化策略
			oc.util.ajax({
				successMsg:null,
				url: oc.resource.getUrl('portal/strategydetail/addInstanceIntoHistoryPersonalProfile.htm'),
				//有问题
				data:{profileId:oc.quick.strategy.detail.getCurInstanceHistroyPersonalizedProfileId(),instanceId:curUseQuickSelectResourceInstanceId},
				success:function(data){
					
					if(data.data){
						
						//添加成功
						alert('应用成功!');
						strategyDialog.panel('destroy');
						
					}
					
				}
				
			});
		}else{
			//之前没有个性化策略
			if(isChildStrategy){
				var newMetricInfo = getCurrentMetricInfo(curProfileData.children[curStrategyIndex]);
				curProfileData.children[curStrategyIndex].metricSetting.metrics = newMetricInfo;
			}else{
				var newMetricInfo = getCurrentMetricInfo(curProfileData);
				curProfileData.metricSetting.metrics = newMetricInfo;
			}
			oc.util.ajax({
				successMsg:null,
				url: oc.resource.getUrl('portal/strategydetail/addPersonalizeProfileAndMonitor.htm'),
				//有问题
				data:{profile:curProfileData},
				success:function(data){
					
					if(data.data && data.data > 0){
						
						//添加成功
						alert('创建成功!');
						
						strategyDialog.panel('destroy');
						
						oc.quick.strategy.detail.show(curUseQuickSelectResourceInstanceId);
						
					}else{
						alert('创建失败!');
					}
					
				}
				
			});
		}
		
	}
	
	function checkProfileUpdateInfo(info,isChildStrategy){
		
			//检查是否有为编辑完的阈值控件
			for(var i = 0 ; i < ionRangeSlider.length ; i ++){
				if(!ionRangeSlider[i].isValidate()){
					//未编辑完成
					return -1;
				}
			}
		
			var modifyResourceInfo = null;
			
			var modifyInfo = null;

			//检查策略的资源选择是否修改
			if(isChildStrategy){
				modifyResourceInfo = checkStrategyChildResourceIsModify(info);
				modifyInfo = checkBaseInfoIsModify(info);
			}
			
			
			//检查指标是否修改
			var modifyMetricInfo = null;
			if(curMetricGridIsCreate){
				modifyMetricInfo = checkStrategyMetricIsModify(info);
			}
			
			//告警规则是否修改
			var modifyAlarmInfo = null;
			if(!isChildStrategy && oc.resource.alarmrules.ifAlarmRuleSendEnableChange()){
				modifyAlarmInfo = true;
			}
			
			var updateInfo = {};
			updateInfo.modifyInfo = modifyInfo;
			updateInfo.modifyResourceInfo = modifyResourceInfo;
			updateInfo.modifyMetricInfo = modifyMetricInfo;
			updateInfo.modifyAlarmInfo = modifyAlarmInfo;
			
			return updateInfo;
	}
	
});