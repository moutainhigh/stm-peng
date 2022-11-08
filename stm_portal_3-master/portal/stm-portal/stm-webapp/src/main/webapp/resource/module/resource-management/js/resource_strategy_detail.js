$(function(){

	oc.ns('oc.resourced.strategy.detail');
	
	//记录当前策略页面数据
	var curProfileData = null;
	
	var nowStrategyId = null;
	
	var strategyInfoAccordion = null;
	
	var isFirstCreateAccordion = true;
	
	var indicatorsDefiendDiv = null;
	
	var strategyTitle = null;
	
	var strategyDialog = null;
	
	//输入框ID
	var strategy_base_info_input_name = null;
	
	var strategy_base_info_input_desc = null;
	
	var alarm_check_box = null;
	
	var monitor_check_box = null;
	
	var resource_strategy_detail_operator_button = null;
	
	//装载rangeSlider的数组
	var ionRangeSlider = null;
	
	//装载requencyCombox的数组
	var frequencyComboxs = null;
	
	//pick表格
	var pickGrid = null;
	
	//pick tree
	var strategyChildPicktree = null;
	
	//子策略左边表格
	var childProfileLeftGrid = null;
	
	//子策略右边表格
	var childProfileRightGird = null;
	
	var alarmFlappingArray = null;
	
	var alarmFlappingSelectId = null;
	
	var storeQuickSelectProfileList = null;
	
	var main_default_strategy_select = null;
	
	var quickSelectMainProfileSelect = null;
	
	var indicatorsDefiendGrid = null;
	
	//记录当前主策略ID
	var curMainProfileId = null;
	
	var curStrategyIndex = null;
	
	var curIsQuickSelectStrategy = null;
	
	var curStragetyType = null;
	
	var curMetricGridIsCreate = null;
	
	var curIsUsePersonalizedProfile = null;
	
	//快速指定方式的资源实例ID
	var curUseQuickSelectResourceInstanceId = null;
	
	var curMonitorUseProfileId = null;
	
	var lastSelectDefaultOrSpecialProfile = null;
	
	var curChildProfileNavList = null;
	
	var curIsComboboxSelectProfile = false;
	
	var curProfileDomainId = -1;
	
	//当前实例是否有过个性化策略
	var curInstanceIsHavePersonalizedProfile = null;
	
	var curInstanceHistroyPersonalizedProfileId = null;
	
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
	
	//记录当前主策略用户搜索内容
	var curUserSelectMainResourceContent = '';
	
	var childrenStrategy = null;
	
	var isAutoChecked = false;
	
	var pageSize = 12;
	
	var curPageStart = 0;
	
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
	
	//初始化策略信息弹出层
	function initDetail(){
		
		var stragetyType = curStragetyType;
		var allData = curProfileData;
		curChildProfileNavList = new Array();
		curRightResourceInstanceList = new Array();
		curUserCheckedResource = new Array();
		curUserSelectMainResourceState = 0;
		totalMainInstanceCount = 0;
		curUserSelectMainResourceContent = '';
		curUserCheckedResourceDomain = new Array();
		curUserUnCheckedResource = new Array();
		var isQuickSelectStrategy = curIsQuickSelectStrategy;
		strategy_base_info_input_name = 'strategy_base_info_input_name';
		
		strategy_base_info_input_desc = 'strategy_base_info_input_desc';
		
		main_default_strategy_select = 'main_default_strategy_select';
		
		alarm_check_box = 'alarm_check_box';
		
		monitor_check_box = 'montitor_check_box';
		
		alarmFlappingSelectId = 'alarmFlappingSelectId';
		
		resource_strategy_detail_operator_button = 'resource_strategy_detail_operator_button';
		
		strategyTitle = $('#resource_strategy_info_title_id');
		
		childrenStrategy = oc.ui.navsublist({
			selector:$('#resource_children_strategy_id'),
			addRowed:function(li,data){
				curChildProfileNavList.push(li);
				li.find('.text:first').attr('title',data.name);
				if(stragetyType != 0 && !(curIsQuickSelectStrategy && stragetyType == 2 && !curInstanceIsHavePersonalizedProfile)){
					
					//非默认策略  BUG #12310
					if(isAllowUpdateAllProfile && stragetyType != 2){
						li.append('<span class="fa fa-close oc-icomargin">&nbsp;</span>');
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
									  var isBySelect = null;
									  if(isQuickSelectStrategy){
										  isBySelect = $("input[name='default_and_personalized_strategy']:checked").attr('value');
									  }
									  refreshDialogDontClose(false,true,isBySelect,nowStrategyId);
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
					
					if(stragetyType == 2){
						
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
						
					}else{
						
						//检查是否修改
						isPass = checkAllInfoIsModifyForDefaultAndSpecial(isUpdateChildStrategy,-data.index,curStrategyIndex);
						
					}
					if(isPass){
						initAccrodion(true,false,-1,data.index);
						strategyTitle.text(allData.profileInfo.profileName);
					}
					
				}
				
				
			}
		});
		
		//渲染主策略
		var allowAddHtml = '<a id="main_proflie_name_id" title="' + allData.profileInfo.profileName + '" class="text-over">' + allData.profileInfo.profileName + '</a>';
		if(stragetyType == 0 || (curIsQuickSelectStrategy && stragetyType == 2 && !curInstanceIsHavePersonalizedProfile)){
			
			$('#resource_main_strategy_id').append(allowAddHtml);
			
		}else{
			
			if(isAllowUpdateAllProfile){
				// 个性化策略不能新增子策略 BUG #12310
				if(stragetyType == 2){
					$('#resource_main_strategy_id').append(allowAddHtml);
				}else{
					$('#resource_main_strategy_id').append(allowAddHtml + '<span class="l-btn-icon fa fa-plus" style="float: right; position: relative;right: -5px;"></span>');
				}
			}else{
				$('#resource_main_strategy_id').append(allowAddHtml);
			}
			
		}
		
		$('#resource_main_strategy_id').on('click',function(e){
			
			var clickTagClass = $(e.target).attr('class');
			
			if(clickTagClass == 'l-btn-icon fa fa-plus'){
				
				//点击添加
				//获取子策略类型
			    oc.util.ajax({
			      successMsg:null,
				  url: oc.resource.getUrl('portal/strategydetail/getChildProfileType.htm'),
				  data:{resourceId:allData.profileInfo.resourceId},
				  success:function(data){
					  
					  if(!data.data || data.data.length <= 0){
					  	 alert('该类型策略没有子策略!');
					  	 return;
					  }
					  // BUG #12310  不能添加已存在的子策略
					  if(allData.children && allData.children.length == data.data.length){
						  alert('该类型策略已有所有类型的子策略!');
						  return;
					  }
					  
					  var addChildStrategy = $('<div id="addChildStrategyForm"></div>');
					  
					  var addChildStrategyForm = $('<form class="oc-form col1"></form>');
					  
					  addChildStrategyForm.append('<div class="form-group"><label>子策略名称 : </label><div><input type="text" name="profileName" validType="maxLength[25]" required/></div></div>');
					  addChildStrategyForm.append('<div class="form-group"><label>子策略类型 : </label><div><select name="resourceId" required="required"></select></div></div>');
					  addChildStrategyForm.append('<div class="form-group"><label>备注 : </label><div><textarea style="resize: none;height:80px" name="profileDesc" validType="maxLength[50]" required></textarea></div></div>');
					  
					  addChildStrategy.append(addChildStrategyForm);
					  addChildStrategy.append('<div class="oc-toolbar margintop-20" align="right"></div>');
					  
					  var childStrategyType = new Array();
					  for(var i = 0 ; i < data.data.length ; i ++){
						  // BUG #12310  不能添加已存在的子策略
						  if(allData.children){
							  var repeatChildProfileFlag = false;
							  for(var j = 0 ; j < allData.children.length ; j ++){
								  if(allData.children[j].profileInfo.resourceId == data.data[i].id){
									  repeatChildProfileFlag = true;
									  break;
								  }
							  }
							  if(repeatChildProfileFlag){
								  continue;
							  }
						  }
						  childStrategyType.push({
							  id:data.data[i].id,
							  name:data.data[i].name
						  });
					  }

					  //添加子资源
					  var addChildStrategyDialog = $('<div/>').dialog({
						  width:'500px',
						  height:'250px',
						  title: '添加子策略',
						  content:addChildStrategy
					  });
					  
					  var addChildForm = oc.ui.form({
						  selector:$('#addChildStrategyForm'),
						  combobox:[{
							  placeholder:false,
							  selector:'[name=resourceId]',
							  data:childStrategyType
						  }]
					  });
					  
					  addChildStrategy.find('.oc-toolbar').append($('<a/>').linkbutton('RenderLB',{
						  text:'确定',
						  iconCls:"fa fa-check-circle",
						  onClick:function(){
							  if(addChildForm.validate()){
								  //验证表单
								  var addParameter = addChildForm.val();
								  addParameter.profileParentId = allData.profileInfo.profileId;
								  addParameter.profileTypeMapping = stragetyType;
								  
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
											  var isBySelect = null;
											  if(isQuickSelectStrategy){
												  isBySelect = $("input[name='default_and_personalized_strategy']:checked").attr('value');
											  }
											  refreshDialogDontClose(false,true,isBySelect,nowStrategyId);
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
					  
				  }
				  
			    });
				
				
			}else{
				
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
				if(stragetyType == 2){
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
					
				}else{
						
					//检查是否修改
					isPass = checkAllInfoIsModifyForDefaultAndSpecial(isUpdateChildStrategy,1,curStrategyIndex);
					
				}
				
				if(isPass){
					
					initAccrodion(false,false,-1,-1);
					childrenStrategy.clearCurActiveLi();
					strategyTitle.text("");
				}

			}
			
			
		});
		
		if(allData.children != null && allData.children.length > 0){
			
			for(var i = 0 ; i < allData.children.length ; i++){
				
				childrenStrategy.add({
					name:allData.children[i].profileInfo.profileName,
					index: i,
					profileId:allData.children[i].profileInfo.profileId
				});
				
			}
		}
		
		
	}
	
	//构建accrodion
	function initAccrodion(isChildStrategy,isSwitch,nowSelect,strategyIndex){
		
		curPageStart = 0;
		
		curUserCheckedForChildResourceInChildProfile = new Array();
		
		curUserCheckedForAllChildResourceInChildProfile = new Array();
		
		curUseStrategyResourceIDs = '';
		
		curRightResourceInstanceList = new Array();
		
		isNetWorkInterface = false;
		
		curUserUnCheckedResource = new Array();
		
		curUserCheckedResource = new Array();
		
		curUserSelectMainResourceState = 0;
		
		totalMainInstanceCount = 0;
		
		curUserSelectMainResourceContent = '';
		
		curUserCheckedResourceDomain = new Array();
		
		interForm = null;
		
		curMetricGridIsCreate = false;
		var stragetyType = curStragetyType;
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
		
		if(!isFirstCreateAccordion && !isSwitch){
	
			$('#resource_strategy_accordion_div').html('');
			$('#resource_strategy_accordion_div').append('<div id="resource_strategy_detail_center_id"></div>');
			
		}
		
		strategyInfoAccordion = $('#resource_strategy_detail_center_id');
		strategyInfoAccordion.accordion({
			animate:true,
			onSelect:function(title,index){
				if(index == 3){
					oc.resource.alarmrules.updateDomainId(getDomainListByMonitorInstance());
				}
				if(!curMetricGridIsCreate){
					if(stragetyType == 2){
						//个性化
						if(index == 0){
							showMetricGrid(singleStrategyInfo);
						}
						
					}else{
						
						if(index == 2){
							showMetricGrid(singleStrategyInfo);
						}
					}
				}
				
			},
			onBeforeSelect:function(title,index){
				
				if(!curMetricGridIsCreate){
					
					if(stragetyType == 2){
						//个性化
						if(index == 0){
							oc.ui.progress();
						}
						
					}else{
						
						if(index == 2){
							oc.ui.progress();
						}
					}
				}
				return true;
			}
		});
		var cfg = null;
		
		var childLeftCfg = null;
		var childRightCfg = null;
		
		var strategyNormalInfo = null;
		var resourceSelectDiv = null;
		
		var updateUserName = singleStrategyInfo.profileInfo.updateUser;
	
		if(stragetyType != 2){
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
			
			if(!isChildStrategy){
				
				if(stragetyType == 0 || !isAllowUpdateAllProfile){
					
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">策略名称</td><td><div>' + singleStrategyInfo.profileInfo.profileName + '</div></td></tr>');
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改人</td><td><div id="profileInfoBaseUpdateUserName">' + updateUserName + '</div></td></tr>');
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改时间</td><td><div id="profileInfoBaseUpdateTime">' + nowUpdateTime + '</div></td></tr>');
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">备注</td><td><div>' + nowProfileDesc + '</td></tr>');
				}else{
					
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">策略名称</td><td><input type="text" id="' + strategy_base_info_input_name + '" value="' + singleStrategyInfo.profileInfo.profileName + '" maxlength=25/></td></tr>');
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改人</td><td><div id="profileInfoBaseUpdateUserName">' + updateUserName + '</div></td></tr>');
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">修改时间</td><td><div id="profileInfoBaseUpdateTime">' + nowUpdateTime + '</div></td></tr>');
					strategyTable.append('<tr class="form-group h-pad-mar"><td class="td-bgcolor">备注</td><td><textarea style="resize: none;height:60px" id="' + strategy_base_info_input_desc + '" maxlength=50>' + nowProfileDesc + '</textarea></td></tr>');
				}
				
			}else{
			
				if(stragetyType == 0 || !isAllowUpdateAllProfile){
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
				
			}
			strategyForm.append(strategyTable);
			strategyNormalInfo.append(strategyForm);
			
			//资源选择
			resourceSelectDiv = $('<div style="height:300px"></div>');
			
			//循环遍历出使用当前策略的资源id
			
			if(isChildStrategy){
				
				for(var i = 0 ; i < singleStrategyInfo.profileInstanceRelations.instances.length ; i ++){
					
					curUseStrategyResourceIDs += singleStrategyInfo.profileInstanceRelations.instances[i].instanceId + ",";
					curUserCheckedForAllChildResourceInChildProfile.push(
							{
								id:singleStrategyInfo.profileInstanceRelations.instances[i].instanceId,
								pid:singleStrategyInfo.profileInstanceRelations.instances[i].parentInstanceId
							}
					);
					
				}

				curUseStrategyResourceIDs = curUseStrategyResourceIDs.substring(0,curUseStrategyResourceIDs.length - 1);

				var nowDomainId = -1;
				var interfaceState = null;
				if(curStragetyType == 1){
					nowDomainId = curProfileDomainId;
				}
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
						'<input type="text" id="searchResourceOrIpInputForChildProfile" placeholder="搜索资源名称|IP"/><a id="queryResourceButtonForChildProfile">查找</a>'+
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
							var childrenInstanceId = "";
							var dataGridRows = childProfileRightGrid.selector.datagrid('getRows');
							if(dataGridRows == null || dataGridRows.length == 0){
								alert("请选择资源");
								return;
							}
							oc.util.ajax({
								  url: oc.resource.getUrl('portal/strategydetail/getInterfaceState.htm'),
								  data:{mainInstanceId:curSelectMainResourceForChildProfile,childrenInstanceId:childrenInstanceId,type:type},
								  timeout:60000,
								  success:function(data){
									  var dataArr = data.data;
									  //var dataGridRows = childProfileRightGrid.selector.datagrid('getRows');
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
												  childProfileRightGrid.selector.datagrid('updateRow',{
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
								interfaceState = r.id;
								curChildPageStart = 0;
								oc.util.ajax({
									  url: oc.resource.getUrl('portal/strategydetail/getChildResourceForChildProfileInInterface.htm'),
									  async:true,
									  data:{mainInstanceId:curSelectMainResourceForChildProfile,resourceId:singleStrategyInfo.profileInfo.resourceId,
										  interfaceState:interfaceState,domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
									  success:function(data){
		          						  if(curChildPageStart + pageSize < data.data.total){
		          							  //未加载完
		          							  childResourceIsLoadCompele = false;
		          						  }else{
		          							  //加载完
		          							  childResourceIsLoadCompele = true;
		          						  }
										  showChildResource(data,singleStrategyInfo.profileInfo.resourceId,nowDomainId);
										  
										  if(data.data.resourceIds && data.data.resourceIds.length > 0){
											  var isSelectAll = true;
											  out : for(var i = 0 ; i < data.data.resourceIds.length ; i ++){
												  for(var j = 0 ; j < curUserCheckedForChildResourceInChildProfile.length ; j ++){
													  if(data.data.resourceIds[i] == curUserCheckedForChildResourceInChildProfile[j]){
														  continue out;
													  }
												  }
												  isSelectAll = false;
											  }
											  if(isSelectAll){
												  isAutoCheckAll = true;
												  childProfileRightGrid.selector.datagrid('checkAll');
											  }
										  }
										  

									  }
								  });
							}
						}]
					});
					
				}
				
				resourceSelectDiv.append('<div id="childResourceSelectLeftParentGrid" style="float:left;width:57%;height:290px"><div id="childResourceSelectLeftGrid"></div></div>' + 
						'<div id="childResourceSelectRightParentGrid" style="margin-left:30px;float:left;width:37%;height:290px"><div id="childResourceSelectRightGrid"></div></div>');
				
				childLeftCfg = {
						selector:'#childResourceSelectLeftGrid',
						pagination:false,
						fitColumns:false,
						checkOnSelect:false,
						columns:[[
				                    {field:'resourceId',hidden:true},
				                    {field:'childResourceSelectStatus',title:'',width:30},
				                    {field:'resourceShowName',title:'名称',width:150},
				                    {field:'resourceIp',title:'IP地址',ellipsis:true,width:110},
				                    {field:'strategyName',title:'当前策略',ellipsis:true,width:130},
				                    {field:'strategyID',title:'当前策略ID',hidden:true},
				                    {field:'chooseChildResource',title:'子资源',width:60,
				                    	formatter: function(value,row,index){
					        				return '<a class="selectChildResourceHref" style="font-weight:bold" selectStatus=' + value + ' parentId=' + row.resourceId + '>选择</a>' + 
					        					'<a class="selectChildResourceArrow" href="javascript:void(0)" parentId=' + row.resourceId + '></a>';
				                    	}
				                    }
	                    ]],
	                    onCheck:function(index,row){
//	                    	curSelectMainResourceForChildProfile = row.resourceId;
//                    		$('#childResourceSelectLeftParentGrid').find('.selectChildResourceHref[parentId="' + row.resourceId + '"]').trigger("click",["check"]);
	                    },
	                    onUncheck:function(index,row){
//	                    	curSelectMainResourceForChildProfile = row.resourceId;
//                    		$('#childResourceSelectLeftParentGrid').find('.selectChildResourceHref[parentId="' + row.resourceId + '"]').trigger("click",["unCheck"]);
	                    },
	                    onSelect:function(index,row){
	                    	$('#childResourceSelectLeftParentGrid').find('.datagrid-view2').find('tr[datagrid-row-index="' + index + '"]').removeClass('datagrid-row-selected');
	                    }
				};
				
				childRightCfg = {
						selector:'#childResourceSelectRightGrid',
						pagination:false,
						selectOnCheck:false,
						fitColumns:false,
						columns:[[
				                    {field:'resourceId',title:'资源ID',checkbox:true},
				                    {field:'resourceName',title:'子资源名称',width:200},
				                    {field:'interfaceState',title:'当前状态',hidden:true,width:120,
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
				                    {field:'strategyID',title:'当前策略ID',hidden:true}
	                    ]],
	                    onLoadSuccess:function(data){
	                    	curChildPageStart = data.rows.length;
	                    },
	                    onCheck:function(index,row){
	                    	curUserCheckedForChildResourceInChildProfile.push(row.resourceId);
	                    	curUserCheckedForAllChildResourceInChildProfile.push(
	                    			{
	                    				id:row.resourceId,
	                    				pid:curSelectMainResourceForChildProfile
	                    			}
	                    	);
	                    	
	                    	//查看当前主资源行选择状态
	                    	var curSelectMainResourceRow = $('#childResourceSelectLeftParentGrid')
	                    		.find('.selectChildResourceHref[parentid="' + curSelectMainResourceForChildProfile + '"]');
	                    	var status = curSelectMainResourceRow.attr('selectstatus');
	                    	//获取该子资源的总数
	                    	var childCount = curSelectMainResourceRow.attr('childcount');
	                    	if(status == 0){
	                    		//全未选中
	                    		if(childProfileRightGrid.selector.datagrid('getRows').length == 1 || 
	                    				(childResourceIsLoadCompele && curUserCheckedForChildResourceInChildProfile.length == childProfileRightGrid.selector.datagrid('getRows').length) || 
	                    				(!childResourceIsLoadCompele && childCount == curUserCheckedForChildResourceInChildProfile.length)){
	                    			//全选中
	                    			isAutoCheckAll = true;
	                    			childProfileRightGrid.selector.datagrid('checkAll');
	                    			curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectall');
	                    			curSelectMainResourceRow.attr('selectstatus',2);
	                    		}else{
	                    			curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectpart');
	                    			curSelectMainResourceRow.attr('selectstatus',1);
	                    		}
	                    		
	                    	}else{
	                    		
	                    		if(isNetWorkInterface && childProfileRightGrid.selector.datagrid('getRows').length == 1 &&
	                    				childCount == curUserCheckedForChildResourceInChildProfile.length){
	                    			curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectall');
	                    			curSelectMainResourceRow.attr('selectstatus',2);
	                    		}
	                    		
	                    		//部分选中
	                    		if((childResourceIsLoadCompele && curUserCheckedForChildResourceInChildProfile.length == childProfileRightGrid.selector.datagrid('getRows').length) || 
	                    				(!childResourceIsLoadCompele && childCount == curUserCheckedForChildResourceInChildProfile.length)){
	                    			isAutoCheckAll = true;
	                    			childProfileRightGrid.selector.datagrid('checkAll');
									curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectall');
									curSelectMainResourceRow.attr('selectstatus',2);
	                    		}
	                    	}
	                    	
	                    },
	                    onUncheck:function(index,row){
	                    	for(var i = 0 ; i < curUserCheckedForChildResourceInChildProfile.length ; i ++){
	                    		if(curUserCheckedForChildResourceInChildProfile[i] == row.resourceId){
	                    			curUserCheckedForChildResourceInChildProfile.splice(i,1);
	                    			break;
	                    		}
	                    	}
	                    	for(var i = 0 ; i < curUserCheckedForAllChildResourceInChildProfile.length ; i ++){
	                    		if(curUserCheckedForAllChildResourceInChildProfile[i].id == row.resourceId){
	                    			curUserCheckedForAllChildResourceInChildProfile.splice(i,1);
	                    			break;
	                    		}
	                    	}
	                    	
	                    	//查看当前主资源行选择状态
	                    	var curSelectMainResourceRow = $('#childResourceSelectLeftParentGrid')
	                    		.find('.selectChildResourceHref[parentid="' + curSelectMainResourceForChildProfile + '"]');
	                    	var status = curSelectMainResourceRow.attr('selectstatus');
	                    	if(status == 2){
	                    		//全选
	                    		if(curUserCheckedForChildResourceInChildProfile.length == 0){
	                    			curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectno');
	                    			curSelectMainResourceRow.attr('selectstatus',0);
	                    		}else{
	                    			curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectpart');
	                    			curSelectMainResourceRow.attr('selectstatus',1);
	                    		}
	                    		
	                    	}else{
	                    		//部分选中
	                    		if(curUserCheckedForChildResourceInChildProfile.length == 0){
	                    			curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectno');
	                    			curSelectMainResourceRow.attr('selectstatus',0);
	                    		}
	                    	}
	                    	
	                    },
	                    onCheckAll:function(rows){
	                    	if(isAutoCheckAll){
	                    		isAutoCheckAll = false;
	                    		return;
	                    	}
	                    	
	                		var nowDomainId = -1;
	                		if(curStragetyType == 1){
	                			nowDomainId = curProfileDomainId;
	                		}
	                    	
		          			//发送请求获取该主资源下的所有子资源进行全选操作
		          			oc.util.ajax({
		          				  successMsg:null,
		          				  url: oc.resource.getUrl('portal/strategydetail/getChildResourceIdForChildProfile.htm'),
		          				  data:{mainInstanceId:curSelectMainResourceForChildProfile,resourceId:curProfileData.children[strategyIndex].profileInfo.resourceId
		          					  ,interfaceState:"",domainId:nowDomainId},
		          				  success:function(data){
		          					  curUserCheckedForChildResourceInChildProfile = new Array();
		          					  for(var i = 0 ; i < data.data.length ; i ++){
		          						  curUserCheckedForChildResourceInChildProfile.push(data.data[i]);
		          						  var isAddToAllChecked = true;
		          						  for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
		          							  if(data.data[i] == curUserCheckedForAllChildResourceInChildProfile[j].id){
		          								  isAddToAllChecked = false;
		          								  break;
		          							  }
		          						  }
		          						  if(isAddToAllChecked){
		          							  curUserCheckedForAllChildResourceInChildProfile.push({
		          								  id:data.data[i],
		          								  pid:curSelectMainResourceForChildProfile
		          							  });
		          						  }
		          					  }
		          					  
			  	                      var curSelectMainResourceRow = $('#childResourceSelectLeftParentGrid')
			  	                    		.find('.selectChildResourceHref[parentid="' + curSelectMainResourceForChildProfile + '"]');
			          				  curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectall');
		                    		  curSelectMainResourceRow.attr('selectstatus',2);
		          					  
		          				  }
		          				  
		          			});
	                    },
	                    onUncheckAll:function(rows){
	                    	curUserCheckedForChildResourceInChildProfile = new Array();
	                    	for(var j = 0 ; j < rows.length ; j ++){
	                    		for(var i = 0 ; i < curUserCheckedForAllChildResourceInChildProfile.length ; i ++){
	                        		if(curUserCheckedForAllChildResourceInChildProfile[i].id == rows[j].resourceId){
	                        			curUserCheckedForAllChildResourceInChildProfile.splice(i,1);
	                        			break;
	                        		}
	                        	}
	                    	}
	                    	
	                    	var curSelectMainResourceRow = $('#childResourceSelectLeftParentGrid')
                    			.find('.selectChildResourceHref[parentid="' + curSelectMainResourceForChildProfile + '"]');
	                    	curSelectMainResourceRow.parents('tr').first().find('span').first().attr('class','ico ico_selectno');
                			curSelectMainResourceRow.attr('selectstatus',0);
	                    }
	                    
				};
				
			}else{
				resourceSelectDiv.append('<form id="filterInstanceStateFormId" class="oc-form col1 h-pad-mar"><div class="form-group h-pad-mar" style="text-align:left;line-height: 37px;"><lable style="position:relative; top:2px;">可选设备：</lable><div radio>' +
						'<label  style="position:relative; margin-left:5px;"><input id="allResourceState" name="monitorState" checked="checked" type="radio" value="0"/>全部</label>' +
						'<label  style="position:relative; margin-left:15px;"><input id="monitorResourceState" name="monitorState" type="radio" value="1" />' +
						'<span class="light-ico_resource res_monitor" style="width:25px;height:25px;position:relative; top:1px;left:3px;"></span>已监控</label>' + //tree-icon-width25
						'<label  style="position:relative; margin-left:15px;"><input id="notMonitorResourceState" name="monitorState"type="radio" value="2" />' +
						'<span class="light-ico_resource res_unknow" style="width:25px;height:25px;position:relative; top:1px;left:3px;"></span>未监控</label>' + //tree-icon-width25
						'</div><input type="text" id="searchResourceOrIpInput" placeholder="搜索资源名称|IP"/><a id="queryResourceButton" style="">查找</a></div></form>');
				resourceSelectDiv.append('<div id="resourceSelectGridParentDiv" style="height:85%"><div id="resource_select_pickGrid"></div></div>');
				
				cfg = {
					selector:'#resource_select_pickGrid',
					pagination:false,
					selectOnCheck:false,
					fitColumns:false,
					columns:[[
			                    {field:'resourceId',title:'资源ID',checkbox:true,sortable:true},
			                    {field:'resourceShowName',title:'名称',width:315},
			                    {field:'resourceIp',title:'IP地址',width:210,ellipsis:true},
			                    {field:'strategyName',title:'当前策略',width:300,ellipsis:true},
			                    {field:'strategyID',title:'当前策略ID',hidden:true}
                    ]],
                    onCheck:function(index,row){

                    	curUserCheckedResource.push(row.resourceId);
                    	curUserCheckedResourceDomain.push(row.domainId);
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
                    	for(var i = 0 ; i < curUserCheckedResource.length ; i ++){
                    		if(curUserCheckedResource[i] == row.resourceId){
                    			curUserCheckedResource.splice(i,1);
                    			break;
                    		}
                    	}
                    	for(var i = 0 ; i < curUserCheckedResourceDomain.length ; i ++){
                    		if(curUserCheckedResourceDomain[i] == row.domainId){
                    			curUserCheckedResourceDomain.splice(i,1);
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
                		if(curStragetyType == 1){
                			domainIdParam = curProfileDomainId;
                		}
                    	
                    	oc.util.ajax({
	              			  successMsg:null,
	              			  url: oc.resource.getUrl('portal/strategydetail/getMainProfileResourceInstancesId.htm'),
	              			  data:{resourceId:curProfileData.profileInfo.resourceId,state:curUserSelectMainResourceState,domainId:domainIdParam,searchContent:curUserSelectMainResourceContent},
	              			  success:function(data){
	              				  
	              				  if(data.code == 200 && data.data){
	              					  for(var i = 0 ; i < data.data.intancesIds.length ; i ++){
		                              		var isContain = false;
		                              		for(var j = 0 ; j < curUserCheckedResource.length ; j ++){
		                              			if(data.data.intancesIds[i] == curUserCheckedResource[j]){
		                              				isContain = true;
		                              				break;
		                              			}
		                              		}
		                              		if(!isContain){
		                              			curUserCheckedResource.push(data.data.intancesIds[i]);
		                              		}
		                              		for(var j = 0 ; j < curUserUnCheckedResource.length ; j++){
		                                  		if(curUserUnCheckedResource[j] == data.data.intancesIds[i]){
		                                  			curUserUnCheckedResource.splice(j,1);
		                                  			break;
		                                  		}
		                                  	}
	              					  }
	              					  for(var i = 0 ; i < data.data.domainIds.length ; i ++){
	              						  curUserCheckedResourceDomain.push(data.data.domainIds[i]);
	              					  }
	              				  }
	              				  
	              			  }
	              			  
	              		});
                    	
                    },
                    onUncheckAll:function(rows){
                    	
                    	for(var j = 0 ; j < rows.length ; j ++){
                    		curUserUnCheckedResource.push(rows[j].resourceId);
                    		for(var i = 0 ; i < curUserCheckedResource.length ; i ++){
                        		if(curUserCheckedResource[i] == rows[j].resourceId){
                        			curUserCheckedResource.splice(i,1);
                        			break;
                        		}
                        	}
                    		for(var i = 0 ; i < curUserCheckedResourceDomain.length ; i ++){
                        		if(curUserCheckedResourceDomain[i] == rows[j].domainId){
                        			curUserCheckedResourceDomain.splice(i,1);
                        			break;
                        		}
                        	}
                    	}
                    	
                    }
				};
				
				
			}
			
			
		}
		
		//指标定义
		indicatorsDefiendDiv = $('<div id="indicatorsDefiendGridId"></div>');
		if(stragetyType == 1 && !isChildStrategy){
			indicatorsDefiendDiv = $('<div><div class="oc-addlinecon"><a id="openTimeLineButtonId" class="locate-right" style="position:relative; right:8px;"></a><span id="timeLineCountId" class="top-num"></span></div><div style="height:270px"><div id="indicatorsDefiendGridId"></div></div></div>');
		}
		
		//告警规则
	    var accordionAddCfg1 = {
			title: '常规信息',
			content: strategyNormalInfo,
			selected: true
	    };
	    
	    
	    var accordionAddCfg2 = null;
	    if(isChildStrategy){
		    accordionAddCfg2 = {
					title: '选择' + singleStrategyInfo.profileInfo.profileResourceType,
					content: resourceSelectDiv,
					selected: false
			};
	    }else{
		    accordionAddCfg2 = {
					title: '资源选择',
					content: resourceSelectDiv,
					selected: false
			};
	    }

	    
	    var accordionAddCfg3 = null;
	    
	    if(stragetyType == 2){
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
	    }
	    
	    if(stragetyType == 2){
	    	strategyInfoAccordion.accordion('add', accordionAddCfg3);
	    	if(!isChildStrategy){
	    		strategyInfoAccordion.accordion('add', accordionAddCfg4);
	    	}
	    	
	    }else{
	    	
	    	strategyInfoAccordion.accordion('add', accordionAddCfg1);
	    	strategyInfoAccordion.accordion('add', accordionAddCfg2);
	    	strategyInfoAccordion.accordion('add', accordionAddCfg3);
	    	if(!isChildStrategy){
	    		strategyInfoAccordion.accordion('add', accordionAddCfg4);
	    		$('#queryResourceButton').linkbutton('RenderLB',{
	    			iconCls:'fa fa-search',
	    			onClick : function(){
	    				//先使滚动条回到顶部，防止二次请求
	    				$('#resourceSelectGridParentDiv').find('.datagrid-view2>.datagrid-body').scrollTop(0);
	    				showMainProfileResourceLeftGrid(singleStrategyInfo.profileInfo.resourceId,
	    						$('#filterInstanceStateFormId').find('input[name=monitorState]:checked').val(),$('#searchResourceOrIpInput').val(),0,pageSize);
	    			}
	    		});
	    	}else{
	    		$('#queryResourceButtonForChildProfile').linkbutton('RenderLB',{
	    			iconCls:'fa fa-search',
	    			onClick : function(){
	    				//先使滚动条回到顶部，防止二次请求
	    				$('#childResourceSelectLeftParentGrid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
					 	 //发送请求刷新主资源列表
						 showMainProfileResourceLeftGrid(curProfileData.profileInfo.resourceId,1,$('#searchResourceOrIpInputForChildProfile').val()
	    							,0,pageSize,curMainProfileId,singleStrategyInfo.profileInfo.resourceId);
	    			}
	    		});
	    	}
	    	
	    }
	    
	    if(alarmRulesDiv != null){
	    	alarmRulesDiv.panel({
	    		height:'302px',
	    		href:oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
	    		onLoad:function(){
	    			oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
	    				if(curStragetyType != 2){
	    					if(isAllowUpdateAllProfile){
		    					oc.resource.alarmrules.open(singleStrategyInfo.profileInfo.profileId,'model_profile',curProfileDomainId,curStragetyType);
	    					}else{
	    						oc.resource.alarmrules.openDataCheck(singleStrategyInfo.profileInfo.profileId,'model_profile',curProfileDomainId,curStragetyType);
	    					}
	    				}else{
	    					if(singleStrategyInfo.profileInfo.profileId <= 0){
	    						oc.resource.alarmrules.openDataCheck(lastSelectDefaultOrSpecialProfile,'model_profile',curUseQuickSelectResourceInstanceId,curStragetyType);
	    					}else{
		    					oc.resource.alarmrules.open(singleStrategyInfo.profileInfo.profileId,'model_profile',curUseQuickSelectResourceInstanceId,curStragetyType);
	    					}
	    				}
	    			});
	    		}
	    	});
	    }
	    
	    if($("#openTimeLineButtonId").length > 0){
	    	oc.util.ajax({
	    		successMsg:null,
	    		url: oc.resource.getUrl('portal/resource/timeline/getTimelinesCount.htm'),
	    		//有问题
	    		data:{profileId:singleStrategyInfo.profileInfo.profileId,resourceId:singleStrategyInfo.profileInfo.resourceId},
	    		success:function(data){
	    			$('#timeLineCountId').text(data.data);
	    			//基线按钮
	    			$("#openTimeLineButtonId").linkbutton('RenderLB',{
	    				text:'基线设置',
	    				onClick:function(e){
	    					oc.resource.loadScript('resource/module/resource-management/timeline/js/timeline.js',function(){
	    						oc.timeline.dialog.open(singleStrategyInfo.profileInfo.profileId,singleStrategyInfo.profileInfo.resourceId,singleStrategyInfo.profileInfo.createUser);
	    					}); 
	    				}
	    			
	    			});
	    		}
	    		
	    	});
	    }
	    
		$('#resource_strategy_detail_layout').layout({
			fit:true
		});
		
		if(isQuickSelectStrategy && !isChildStrategy && $('#resource_strategy_detail_layout').layout('panel','north').length <= 0){
			
			//创建快速指定策略的头部
			var northDiv = $('<div/>');
			
			if(nowSelect == 1){

				northDiv.append('<form id="quickSelectStrategySelectId" class="oc-form col1"><input type="radio" value="1" checked="true" name="default_and_personalized_strategy" />' + 
						'<select id="' + main_default_strategy_select + '"></select><input type="radio" value="2" name="default_and_personalized_strategy" /><lable>个性化策略</lable></form>');
				
			}else{
				
				
				northDiv.append('<form id="quickSelectStrategySelectId" class="oc-form col1"><input type="radio" value="1" name="default_and_personalized_strategy" />' + 
						'<select id="' + main_default_strategy_select + '"></select><input type="radio" checked="true" value="2" name="default_and_personalized_strategy" /><lable>个性化策略</lable></form>');
				
			}
			
			$('#resource_strategy_detail_layout').layout('add',{
				
				height:40,
				region:'north',
				content:northDiv
					
			});
			
			 $("input[name='default_and_personalized_strategy']").on("click",function(){ 
				 curIsComboboxSelectProfile = false;
				 switchDefaultOrPersonalizedStratety($(this).val());
			 });
			 
			 //获取全部默认策略
			 var strategyFormData = new Array();
			  
			 for(var i = 0 ; i < storeQuickSelectProfileList.length ; i ++){
				  strategyFormData.push({
					  id:storeQuickSelectProfileList[i].profileId,
					  name:storeQuickSelectProfileList[i].profileName
				  });
				  
			 }
			 
			 var profileListComboboxId = -1;
			 
			 if(curInstanceIsHavePersonalizedProfile && nowSelect == 2){
				 if(lastSelectDefaultOrSpecialProfile == null){
					 profileListComboboxId = storeQuickSelectProfileList[0].profileId;
					 lastSelectDefaultOrSpecialProfile = profileListComboboxId;
				 }else{
					 profileListComboboxId = lastSelectDefaultOrSpecialProfile;
				 }
			 }else if(nowSelect == 1 && curIsComboboxSelectProfile){
				 profileListComboboxId = nowStrategyId;
			 }else if(nowSelect == 1 && !curIsComboboxSelectProfile){
				 if(lastSelectDefaultOrSpecialProfile == null){
					 profileListComboboxId = nowStrategyId;
					 lastSelectDefaultOrSpecialProfile = profileListComboboxId;
				 }else{
					 profileListComboboxId = lastSelectDefaultOrSpecialProfile;
				 }
			 }else if(!curInstanceIsHavePersonalizedProfile && nowSelect == 2){
				 profileListComboboxId = nowStrategyId;
			 }else if(nowSelect == -1){
				 profileListComboboxId = lastSelectDefaultOrSpecialProfile;
			 }
			 
			 //快捷方式下主策略选择框
			 quickSelectMainProfileSelect = oc.ui.combobox({
				  selector:$('#' + main_default_strategy_select),
				  width:190,
				  placeholder:false,
				  selected:false,
				  value:profileListComboboxId,
				  data:strategyFormData,
				  onSelect:function(record){
					  //切换默认策略后刷新
					  nowStrategyId = record.id;
					  lastSelectDefaultOrSpecialProfile = nowStrategyId;
					  curIsComboboxSelectProfile = true;
					  switchDefaultOrPersonalizedStratety(1);
				  }
			  });
			 
			 oc.ui.form({
				  selector:$('#quickSelectStrategySelectId'),
				  combobox:[quickSelectMainProfileSelect]
			 });
			 
			 if(nowSelect == 2){
				 quickSelectMainProfileSelect.disable();
			 }
			 
		}
		
		if(stragetyType != 2){
			if(isChildStrategy){
				
				childProfileLeftGrid = oc.ui.datagrid(childLeftCfg);
				childProfileRightGrid = oc.ui.datagrid(childRightCfg);
				
				if(isNetWorkInterface){
					childProfileRightGrid.selector.datagrid('showColumn','interfaceState');
				}
				
				//加载左边表格数据
				oc.util.ajax({
					  successMsg:null,
					  url: oc.resource.getUrl('portal/strategydetail/getMainProfileResourceInstancesForChildProfile.htm'),
					  data:{state:1,searchContent:'',resourceId:curProfileData.profileInfo.resourceId,childResourceId:singleStrategyInfo.profileInfo.resourceId,
						  childProfileId:singleStrategyInfo.profileInfo.profileId,mainProfileId:curMainProfileId,start:curPageStart,pageSize:pageSize,domainId:nowDomainId},
					  success:function(data){
						  
						  	//去掉全选框
						  	$('#childResourceSelectLeftParentGrid').find('.datagrid-view2').find('.datagrid-header-check>input').css('display','none');
						  
						  	curPageStart = data.data.rows.length;
						  	
						  	for(var i = 0 ; i < data.data.rows.length ; i ++){
								if(data.data.rows[i].selectStatus == 0){
									//全部未选中
									data.data.rows[i].childResourceSelectStatus = '<span class="ico ico_selectno"></span>';
									data.data.rows[i].chooseChildResource = data.data.rows[i].selectStatus;
								}else if(data.data.rows[i].selectStatus == 2){
									//全部选中
									data.data.rows[i].childResourceSelectStatus = '<span class="ico ico_selectall"></span>';
									data.data.rows[i].chooseChildResource = data.data.rows[i].selectStatus;
								}else{
									//部分选中
									data.data.rows[i].childResourceSelectStatus = '<span class="ico ico_selectpart"></span>';
									data.data.rows[i].chooseChildResource = data.data.rows[i].selectStatus;
								}
						  	}
						  
						  	childProfileLeftGrid.selector.datagrid('loadData',data);
						  
						  	//注册子资源选择点击事件
	                    	$('.selectChildResourceHref').on('click',function(e,checkInfo){
	                    		
	                    		e.stopPropagation();
	                    		
	                    		$('#childResourceSelectRightParentGrid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
	                    		
	                    		$('#childResourceSelectLeftParentGrid').find('.datagrid-view2').find('tr').removeClass('datagrid-row-selected');
	                    		$(e.target).parents('tr').first().addClass('datagrid-row-selected');
	                    		
	                    		$('.selectChildResourceHref').css('font-style','normal');
                    			$(this).css('font-style','italic');
	                    		
	                    		$('.selectChildResourceArrow').removeClass('selected_arrow');
	                    		$(this).next().addClass('selected_arrow');
	                    		
	                    		var that = this;
	                    		
	                    		curChildPageStart = 0;
	                    		curSelectMainResourceForChildProfile = $(this).attr('parentId');
	                    		
	                    		var interfaceState = "";
	                    		
	                    		//判断当前是否网络设备接口
	                    		if(isNetWorkInterface){
	                    			interfaceState = "all";
	                    			if(interForm.ocui[0].jq.combobox('getValue') != 'all'){
	                    				interForm.ocui[0].jq.combobox('setValue','all');
	                    			}
	                    		}
	                    		
	                    		//发送请求获取子资源列表
	            				oc.util.ajax({
		          					  successMsg:null,
		          					  url: oc.resource.getUrl('portal/strategydetail/getChildResourceForChildProfile.htm'),
		          					  data:{mainInstanceId:$(this).attr('parentId'),resourceId:singleStrategyInfo.profileInfo.resourceId,
		          						  interfaceState:interfaceState,domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
		          					  success:function(data){
		          						  if(curChildPageStart + pageSize < data.data.total){
		          							  //未加载完
		          							  childResourceIsLoadCompele = false;
		          						  }else{
		          							  //加载完
		          							  childResourceIsLoadCompele = true;
		          						  }
	          							  showChildResource(data,singleStrategyInfo.profileInfo.resourceId,nowDomainId,checkInfo);
	          							  $(that).attr('childCount',data.data.total);
		      	                    	  if($(that).attr('selectStatus') == 2){
		      	                    		  	isAutoCheckAll = true;
		    	                    			childProfileRightGrid.selector.datagrid('checkAll');
		    	                    	  }
		          					  }
		          					  
		          				 });
	                    	});
						  	
	    				    //注册滚动条滚动到底部翻页事件
	    					var timeoutChildProfile;
	    					var scrollDivChildProfile = $('#childResourceSelectLeftParentGrid').find('.datagrid-view2>.datagrid-body');
	    					scrollDivChildProfile.on('scroll',function(){
	    						/*清除定时器，防止滚动滚动条时多次触发请求(IE)*/
	    						clearTimeout(timeoutChildProfile);
	    							/*当请求完成并且在页面上显示之后才能继续请求*/
	    							if(!isLoadding){
	    								 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
	    								 if($(this).get(0).scrollHeight - $(this).height() <= $(this).scrollTop()){
	    									 timeoutChildProfile = setTimeout(function(){
	    										 	 //发送请求刷新主资源列表
		    										 showMainProfileResourceLeftGrid(curProfileData.profileInfo.resourceId,1,$('#searchResourceOrIpInputForChildProfile').val()
		 						    							,curPageStart,pageSize,curMainProfileId,singleStrategyInfo.profileInfo.resourceId);
		    										 
	    									 },200);
	    								 }
	    							}
	    					});
						  
					  }
					  
				 });
				
				//保存和取消按钮
				if(isAllowUpdateAllProfile || curIsQuickSelectStrategy){
					//非个性化子策略的应用按钮
					$("#" + resource_strategy_detail_operator_button).append($('<a/>').linkbutton('RenderLB',{
						text:'应用',
						iconCls:"fa fa-check-circle",
						onClick:function(){
							var stragetyType = curStragetyType;
							var singleStrategyInfo = curProfileData.children[curStrategyIndex];
							
							var updateInfo = checkProfileUpdateInfo(stragetyType,singleStrategyInfo,false,true);
							
							if(updateInfo == -1){
								//有阈值修改未完成
								alert('有阈值未修改完成,请修改!');
								return;
							}
							
							if(updateInfo.modifyInfo || updateInfo.modifyResourceInfo || updateInfo.modifyMetricInfo){
								
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
				//获取数据
				pickGrid = oc.ui.datagrid(cfg);
				
				//展示表格数据
				showMainProfileResourceAllGrid(singleStrategyInfo.profileInfo.resourceId);
				
			}
			//渲染form
			baseInfoForm=oc.ui.form({
				selector:strategyForm
			});
		}else{
			
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
							
							var updateInfo = checkProfileUpdateInfo(null,singleStrategyInfo,true,isChildStrategy);
							
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
	
	//默认策略和个性化策略间的切换
	function switchDefaultOrPersonalizedStratety(nowSelect){
		if(nowSelect == 2){
			curStragetyType = 2;
			lastSelectDefaultOrSpecialProfile = nowStrategyId;
			if(curInstanceIsHavePersonalizedProfile){
				nowStrategyId = curInstanceHistroyPersonalizedProfileId;
			}
		}else{
			nowStrategyId = lastSelectDefaultOrSpecialProfile;
		}
		refreshDialogDontClose(false,true,nowSelect,nowStrategyId);
		
	}
	
	function createChildResourcePaging(resourceId,nowDomainId){
			//创建子策略子资源表格的分页
		    //注册滚动条滚动到底部翻页事件
			var timeoutChild;
			var scrollDivChild = $('#childResourceSelectRightParentGrid').find('.datagrid-view2>.datagrid-body');
			scrollDivChild.on('scroll',function(){
				/*清除定时器，防止滚动滚动条时多次触发请求(IE)*/
				clearTimeout(timeoutChild);
					/*当请求完成并且在页面上显示之后才能继续请求*/
					if(!isLoadding){
						 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
						 if($(this).get(0).scrollHeight - $(this).height() - 5 <= $(this).scrollTop()){
							 timeoutChild = setTimeout(function(){
		                    		//发送请求获取子资源列表
								 	isLoadding = true;
								 	
		                    		var interfaceState = "";
		                    		
		                    		//判断当前是否网络设备接口
		                    		if(isNetWorkInterface){
//		                    			interfaceState = "all";
//		                    			if(interForm.ocui[0].jq.combobox('getValue') != 'all'){
//		                    				interForm.ocui[0].jq.combobox('setValue','all');
//		                    			}
		                    			interfaceState = interForm.ocui[0].jq.combobox('getValue');
		                    		}
								 	
		            				oc.util.ajax({
			          					  successMsg:null,
			          					  url: oc.resource.getUrl('portal/strategydetail/getChildResourceForChildProfile.htm'),
			          					  data:{mainInstanceId:curSelectMainResourceForChildProfile,resourceId:resourceId,
			          						  interfaceState:interfaceState,domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
			          					  success:function(data){
			          						  
			          						   if(curChildPageStart + pageSize < data.data.total){
				          							  //未加载完
				          							  childResourceIsLoadCompele = false;
			          						   }else{
				          							  //加载完
				          							  childResourceIsLoadCompele = true;
			          						   }
			          						  
				          					    if(!data.data.rows || data.data.rows.length <= 0){
				          					    	alert('所有数据已加载完毕');
				          					    	isLoadding = false;
				          					    	return;
				          					    }else if(curChildPageStart > 0){
				          					    	curChildPageStart = curChildPageStart + data.data.rows.length;
				          					    }
				          					    for(var i = 0 ; i < data.data.rows.length ; i ++){
				          					    	  for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
				          					    		  if(data.data.rows[i].resourceId == curUserCheckedForAllChildResourceInChildProfile[j].id){
				          					    			  data.data.rows[i].checked = true;
//				          					    			  curUserCheckedForChildResourceInChildProfile.push(data.data.rows[i].resourceId);
				          					    			  break;
				          					    		  }
				          					    	  }
//					          						  if(data.data.rows[i].strategyID == curProfileData.children[curStrategyIndex].profileInfo.profileId){
//					          						  }
				          					    	  childProfileRightGrid.selector.datagrid('appendRow',data.data.rows[i]);
				          					    }
				          						isLoadding = false;
			          					  }
			          					  
			          				 });
									 
							 },200);
						 }
					}
			});
	}
	
	//展示子资源表格
	function showChildResource(data,resourceId,nowDomainId,checkInfo){

		  curUserCheckedForChildResourceInChildProfile = new Array();
		
		  $('#childResourceSelectRightParentGrid').find('.datagrid-view2>.datagrid-body').unbind('scroll');
//		  for(var i = 0 ; i < data.data.rows.length ; i ++){
//			  
//			  if(checkInfo == 'check'){
//				  data.data.rows[i].checked = true;
//			  }else if(checkInfo == 'unCheck'){
//				  
//			  }else{
//				  for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
//					  if(data.data.rows[i].resourceId == curUserCheckedForAllChildResourceInChildProfile[j].id){
//						  data.data.rows[i].checked = true;
//						  //记录用户当前子策略的子资源列表选中数据
//						  curUserCheckedForChildResourceInChildProfile.push(data.data.rows[i].resourceId);
//						  break;
//					  }
//				  }
//			  }
//			  
//			  
//		  }
		  out : for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
			  if(curSelectMainResourceForChildProfile == curUserCheckedForAllChildResourceInChildProfile[j].pid){
				  //记录用户当前子策略的子资源列表选中数据
				  curUserCheckedForChildResourceInChildProfile.push(curUserCheckedForAllChildResourceInChildProfile[j].id);
			  }
			  for(var i = 0 ; i < data.data.rows.length ; i ++){
				  if(data.data.rows[i].resourceId == curUserCheckedForAllChildResourceInChildProfile[j].id){
					  data.data.rows[i].checked = true;
					  //记录用户当前子策略的子资源列表选中数据
					  continue out;
				  }
			  }
		  }
		  
		  if(checkInfo == 'unCheck'){
			  curUserCheckedForChildResourceInChildProfile = new Array();
			  for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
				  if(curSelectMainResourceForChildProfile == curUserCheckedForAllChildResourceInChildProfile[j].id){
					  curUserCheckedForAllChildResourceInChildProfile.splice(j,1);
				  }
			  }
		  }else if(checkInfo == 'check'){
			  //发送请求获取该主资源下的所有子资源进行全选操作
			  oc.util.ajax({
				  successMsg:null,
				  url: oc.resource.getUrl('portal/strategydetail/getChildResourceIdForChildProfile.htm'),
				  data:{mainInstanceId:curSelectMainResourceForChildProfile,resourceId:resourceId,interfaceState:"",domainId:nowDomainId},
				  success:function(data){
					  for(var i = 0 ; i < data.data.length ; i ++){
						  curUserCheckedForChildResourceInChildProfile.push(data.data[i]);
						  var isAddToAllChecked = true;
						  for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
							  if(data.data[i] == curUserCheckedForAllChildResourceInChildProfile[j].id){
								  isAddToAllChecked = false;
								  break;
							  }
						  }
						  if(isAddToAllChecked){
							  curUserCheckedForAllChildResourceInChildProfile.push({
								  id:data.data[i],
								  pid:curSelectMainResourceForChildProfile
							  });
						  }
					  }
				  }
				  
			  });
		  }
		  
		  childProfileRightGrid.selector.datagrid('loadData',data);
		  createChildResourcePaging(resourceId,nowDomainId);
		  
	}
	
	oc.resourced.strategy.detail = {
		show:function(strategyId,stragetyType,isQuickSelectStrategy){
			clearCacheData();
			curIsQuickSelectStrategy = isQuickSelectStrategy;
			curStragetyType = stragetyType;
			curInstaceIsHavePersonalizedProfile = false;
			if(isQuickSelectStrategy){
				//若为快速制定策略，先查询默认策略
				//获取全部默认策略
				var isSwitch = false;
				if(strategyDialog != null){
					isSwitch = true;
					strategyDialog.dialog('clear');
				}
				
				oc.util.ajax({
					  successMsg:null,
					  url: oc.resource.getUrl('portal/strategydetail/getMainDefaultProfile.htm'),
					  data:{resourceId:strategyId},
					  success:function(data){
						  lastSelectDefaultOrSpecialProfile = null;
						  storeQuickSelectProfileList = $.extend(true,[],data.data);
						  var firstStrategyId = data.data[0].profileId;
						  curMonitorUseProfileId = firstStrategyId;
						  nowStrategyId = firstStrategyId;
						  curUseQuickSelectResourceInstanceId = strategyId;
						  if(data.data[0].profileType == 'PERSONALIZE'){
							  curStragetyType = 2;
							  curInstanceIsHavePersonalizedProfile = true;
							  curIsUsePersonalizedProfile = true;
							  curInstanceHistroyPersonalizedProfileId = data.data[0].profileId;
							  //当前策略为个性化策略
							  storeQuickSelectProfileList.splice(0,1);
							  refreshDialog(false,isSwitch,2,firstStrategyId);
						  }else if(data.data[0].profileType == 'DEFAULT'){
							  if(data.data.length > 1 && data.data[1].profileType == 'PERSONALIZE'){
								  curInstanceIsHavePersonalizedProfile = true;
								  curInstanceHistroyPersonalizedProfileId = data.data[1].profileId;
								  storeQuickSelectProfileList.splice(1,1);
							  }
							  curStragetyType = 0;
							  curIsUsePersonalizedProfile = false;
							  refreshDialog(false,isSwitch,1,firstStrategyId);
						  }else if(data.data[0].profileType == 'SPECIAL'){
							  if(data.data.length > 1 && data.data[1].profileType == 'PERSONALIZE'){
								  curInstanceIsHavePersonalizedProfile = true;
								  curInstanceHistroyPersonalizedProfileId = data.data[1].profileId;
								  storeQuickSelectProfileList.splice(1,1);
							  }
							  curStragetyType = 1;
							  curIsUsePersonalizedProfile = false;
							  refreshDialog(false,isSwitch,1,firstStrategyId);
						  }
					  
						  
					  }
					  
				 });
				
			}else{
				nowStrategyId = strategyId;
				var isSwitch = false;
				if(strategyDialog != null){
					isSwitch = true;
					strategyDialog.dialog('clear');
				}
				
				refreshDialog(false,isSwitch,1,strategyId);
				
			}
			
			
		},
		updateTimeLineCount:function(newCount){
			$('#timeLineCountId').text(newCount);
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
		var updateInfo = checkProfileUpdateInfo(null,singleStrategyInfo,true,isChildStrategy);
		
		if(updateInfo == -1){
			//有阈值修改未完成
			alert('有阈值未修改完成,请修改!');
			return;
		}
		
		if(updateInfo.modifyMetricInfo || updateInfo.modifyAlarmInfo){
			
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
	
	//检查自定义和默认是否有数据被修改
	function checkAllInfoIsModifyForDefaultAndSpecial(isChildStrategy,nextShowSelectIndex,strategyIndex){
		
		var stragetyType = curStragetyType;
		
		if(!isAllowUpdateAllProfile){
			//非系统管理员不可修改默认策略
			return true;
		}
		
		var singleStrategyInfo = null;
		if(isChildStrategy){
			singleStrategyInfo = curProfileData.children[strategyIndex];
		}else{
			singleStrategyInfo = curProfileData;
		}
		
		//检查是否修改
		var updateInfo = checkProfileUpdateInfo(stragetyType,singleStrategyInfo,false,isChildStrategy);
		
		if(updateInfo == -1){
			//有阈值修改未完成
			alert('有阈值未修改完成,请修改!');
			return;
		}
		
		if(updateInfo.modifyInfo || updateInfo.modifyResourceInfo || updateInfo.modifyMetricInfo || updateInfo.modifyAlarmInfo){
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
	function checkStrategyMainResourceIsModify(strategyInstance){

		if(curUserCheckedResource.length == 0 && pickGrid.selector.datagrid('getRows').length == 0 && $('#filterInstanceStateFormId').find('input[name=monitorState]:checked').val() == 0){
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
	
	//检查子策略的子资源选择是否修改
	function checkStrategyChildResourceIsModify(strategyInstance){
		
		var resourceInstanceIdArray = new Array();
		
		for(var x = 0 ; x < curUserCheckedForAllChildResourceInChildProfile.length ; x ++){
			
			resourceInstanceIdArray.push(curUserCheckedForAllChildResourceInChildProfile[x].id);
			
		}
		
		//原始数据
		//循环遍历出使用当前策略的资源id
		var curUseStrategyResourceIDsArray = new Array();
		
		if(curUseStrategyResourceIDs){
			curUseStrategyResourceIDsArray = curUseStrategyResourceIDs.split(',');
		}
		
		if(resourceInstanceIdArray.length == 0 && curUseStrategyResourceIDsArray.length == 0){
			return null;
		}
		
		//判断是否改变
		//找出右边多出的资源
		var rightAddResourceArray = resourceInstanceIdArray.concat();
		outFor : for(var index = 0 ; index < curUseStrategyResourceIDsArray.length ; index ++){
			
			var selectResourceId = curUseStrategyResourceIDsArray[index];
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
		var rightReduceResourceArray = curUseStrategyResourceIDsArray.concat();
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
			
			var oldThreshold1 = null;
			var oldThreshold2 = null;
			if(metrics.metricThresholds != null && metrics.metricThresholds.length > 0){
				metricThresholdsNum++;
				
				var nowAlarmFlapping = alarmFlappingArray[metricThresholdsNum].combobox('getValue');
				
				if(oldAlarmFlapping != nowAlarmFlapping){
					flappingValueMap += '"' +metricId + '":' + nowAlarmFlapping + ',';
				}
				
				var metricThresholdsArray = metrics.metricThresholds;
				
				for(var t = 0 ; t < metricThresholdsArray.length ; t++){
					if(metricThresholdsArray[t].perfMetricStateEnum == 'Minor'){
						oldThreshold1 =  metricThresholdsArray[t].thresholdValue;
					}else if(metricThresholdsArray[t].perfMetricStateEnum == 'Major'){
						oldThreshold2 =  metricThresholdsArray[t].thresholdValue;
					}
				}
				
				var ionValue = ionRangeSlider[metricThresholdsNum].getTargetValue();
				
				var nowThreshold1 = null;
				var nowThreshold2 = null;
				
				nowThreshold1 = ionValue[1];
				nowThreshold2 = ionValue[2];
				
				if(nowThreshold1 == undefined || nowThreshold2 == undefined){
					alert('获取阈值组件值失败!');
					return null;
				}
				
				if(oldThreshold1 != nowThreshold1){
					
					
					thresholdsMap += '{"threshold_mkId":' + metricThresholdsArray[1].threshold_mkId + ',"thresholdValue":"' + nowThreshold1 + '"},' +
						'{"threshold_mkId":' + metricThresholdsArray[2].threshold_mkId + ',"thresholdValue":"' + nowThreshold2 + '"},';
						
					
				}else if(oldThreshold2 != nowThreshold2){
					
						
					thresholdsMap += '{"threshold_mkId":' + metricThresholdsArray[1].threshold_mkId + ',"thresholdValue":"' + nowThreshold1 + '"},' +
						'{"threshold_mkId":' + metricThresholdsArray[2].threshold_mkId + ',"thresholdValue":"' + nowThreshold2 + '"},';
					
					
				}
					
				
			}
				
			//现在数据
			var nowIsAlarm = null;
			if($('#' + alarm_check_box + i).attr('checked') == 'checked'){
				nowIsAlarm = true;
			}else{
				nowIsAlarm = false;
			}
			
			if(isAlarm != nowIsAlarm){
				alarmsMap += '"' + metricId + '":' + nowIsAlarm + ',';
			}
			
			var nowIsMonitor = null;
			if($('#' + monitor_check_box + i).attr('checked') == 'checked'){
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
			
			var resultData = '{"profileId":' + strategyInstance.profileInfo.profileId + ',"monitorMap":{' + monitorMap + '},"alarmsMap":{' + alarmsMap + '},"frequencyValueMap":{' + frequencyValueMap + '},"flappingValueMap":{' + flappingValueMap + '},"thresholdsMap":[' + thresholdsMap + ']}';
			return resultData;
		}

		
		return null;
	}
	
	//重新加载dialog
	function refreshDialog(isChildStrategy,isSwitch,nowSelect,profileId){
		var nowHeight = '575px';
		if(curIsQuickSelectStrategy){
			nowHeight = '615px';
		}
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
			  		  		isAllowUpdateAllProfile = true;
			  			  	checkCurProfileType(data.data.profileInfo.profileType);
			  			  	if(curStragetyType == 2){
			  			  		curUseQuickSelectResourceInstanceId = data.data.profileInfo.personalize_instanceId;
			  			  	}
			  		  	    if((data.data.profileInfo.createUser && oc.index.getUser().id != data.data.profileInfo.createUser && curStragetyType == 1)
			  		  	    	|| (!oc.index.getUser().systemUser && curStragetyType == 0)){
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
				  			
					    	initAccrodion(isChildStrategy,isSwitch,nowSelect,-1);
				    	
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
				if(curStragetyType == 2){
					//个性化
					if(!curIsQuickSelectStrategy){
						//检查是否修改
						var isPass = checkAllInfoIsModifyForPersonalize(curIsChildStrategy,3,curStrategyIndex);
						if(!isPass){
							return false;
						}
						
					}
					
				}else{
					
					//检查是否修改
					var isPass = checkAllInfoIsModifyForDefaultAndSpecial(curIsChildStrategy,3,curStrategyIndex);
					if(!isPass){
						return false;
					}
					
				}
				
		    	return true;
		    },
		    onDestroy:function(){
		    	oc.resource.alarmrules.reSetPage();
		    }
		});
		
	}
	
	//重新加载没有关闭的dialog
	function refreshDialogDontClose(isChildStrategy,isSwitch,nowSelect,profileId){
		var nowHeight = '575px';
		if(curIsQuickSelectStrategy){
			nowHeight = '615px';
		}
		strategyDialog.panel('destroy');
		strategyDialog = $('<div/>').dialog({
			width:'1100px',
			height:nowHeight,
			title: '编辑监控策略',
		    href: oc.resource.getUrl('resource/module/resource-management/resource_strategy_detail.html'),
		    onLoad:function(){
		    	//判断当前资源实例是否有个性化策略
		    	if(curInstanceIsHavePersonalizedProfile && nowSelect == 2){
		    		
		    		//有默认个性化策略
		    		oc.util.ajax({
		    			successMsg:null,
		    			url: oc.resource.getUrl('portal/strategydetail/getStrategyById.htm'),
		    			data : {strategyId:curInstanceHistroyPersonalizedProfileId},
		    			success: function(data){
		    				checkCurProfileType(data.data.profileInfo.profileType);
			  		  	    if((data.data.profileInfo.createUser && oc.index.getUser().id != data.data.profileInfo.createUser && curStragetyType == 1)
			  		  	    	|| (!oc.index.getUser().systemUser && curStragetyType == 0)){
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
		    				initAccrodion(isChildStrategy,isSwitch,nowSelect,-1);
		    				
		    			}
		    			
		    		});
		    		
		    	}else{
		    		//没有个性化策略
		    		//请求策略信息
		    		if(nowSelect == 2){
		    			//复制修改本地数据
    					
		    			oc.util.ajax({
		    				successMsg:null,
		    				url: oc.resource.getUrl('portal/strategydetail/getEmptyPersonalizeProfile.htm'),
		    				data : {resourceId:curProfileData.profileInfo.resourceId,instanceId:curUseQuickSelectResourceInstanceId},
		    				success: function(data){
		    				
		    					curProfileData = data.data;
		    					initDetail();
		    					initAccrodion(isChildStrategy,isSwitch,nowSelect,-1);
		    					
		    				}
		    				
		    			});
    					
		    		}else{
		    			oc.util.ajax({
		    				successMsg:null,
		    				url: oc.resource.getUrl('portal/strategydetail/getStrategyById.htm'),
		    				data : {strategyId:profileId},
		    				success: function(data){
		    					checkCurProfileType(data.data.profileInfo.profileType);
    			  		  	    if((data.data.profileInfo.createUser && oc.index.getUser().id != data.data.profileInfo.createUser && curStragetyType == 1)
			  		  	    		|| (!oc.index.getUser().systemUser && curStragetyType == 0)){
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
		    					initAccrodion(isChildStrategy,isSwitch,nowSelect,-1);
		    					
		    				}
		    				
		    			});
		    		}
		    			
		    		
		    	}
		    	
		    	
		    	
		    },
		    onBeforeClose:function(){
		    	var curIsChildStrategy = null;
		    	if(curStrategyIndex == -1){
		    		curIsChildStrategy = false;
		    	}else{
		    		curIsChildStrategy = true;
		    	}
				if(curStragetyType == 2){
					
					//个性化
					if(!curIsQuickSelectStrategy){
						
						//检查是否修改
						var isPass = checkAllInfoIsModifyForPersonalize(curIsChildStrategy,3,curStrategyIndex);
						if(!isPass){
							return false;
						}
						
					}
					
				}else{
					
					//检查是否修改
					var isPass = checkAllInfoIsModifyForDefaultAndSpecial(curIsChildStrategy,3,curStrategyIndex);
					if(!isPass){
						return false;
					}
						
				}
				
				
		    	return true;
		    },
		    onDestroy:function(){
		    	oc.resource.alarmrules.reSetPage();
		    }
		});
		
	}
	
	//获取主策略的主资源的右边表格的数据
	function getRightGridResourceId(){
		
		var selectedRows = pickGrid.getRightRows();

		var checkedResourceIDs = '';
		
		for(var x = 0 ; x < selectedRows.length ; x++){
			
			var selectData = selectedRows[x];
				
			checkedResourceIDs += selectData.resourceId + ',';
			
		}
			
			
		checkedResourceIDs = checkedResourceIDs.substring(0,checkedResourceIDs.length - 1);
		
		return checkedResourceIDs;
	}

	//展示主资源左表格(resourceIds资源实例ID集合,resourceModelId资源ID,state过滤状态,searchContent过滤关键字)
	function showMainProfileResourceLeftGrid(resourceModelId,state,searchContent,start,pageSize,curMainProfileId,childResourceID){

		var nowDomainId = -1;
		if(curStragetyType == 1){
			nowDomainId = curProfileDomainId;
		}
		
		//判断主策略的资源列表还是子策略的资源列表
		var ajaxUrl,dataParam,loadGrid;
		if(curMainProfileId){
			//子策略的资源列表
			ajaxUrl = oc.resource.getUrl('portal/strategydetail/getMainProfileResourceInstancesForChildProfile.htm');
			dataParam = {resourceId:resourceModelId,state:state,domainId:nowDomainId,searchContent:searchContent,
					childResourceId:childResourceID,childProfileId:curProfileData.children[curStrategyIndex].profileInfo.profileId,start:start,pageSize:pageSize,mainProfileId:curMainProfileId};
			loadGrid = childProfileLeftGrid;
		}else{
			//主策略的资源列表
			var curUserCheckedResourceValue = curUserCheckedResource.join(",");
			ajaxUrl = oc.resource.getUrl('portal/strategydetail/getMainProfileResourceInstancesByCondion.htm');
			dataParam = {resourceId:resourceModelId,state:state,domainId:nowDomainId,searchContent:searchContent,
					profileId:curProfileData.profileInfo.profileId,curUserCheckedResource:curUserCheckedResourceValue,start:start,pageSize:pageSize};
			curUserSelectMainResourceState = state;
			curUserSelectMainResourceContent = searchContent;
			loadGrid = pickGrid;
		}
		
		oc.util.ajax({
			  successMsg:null,
			  url: ajaxUrl,
			  data:dataParam,
			  success:function(data){
				  	isLoadding = true;
				    if(start > 0 && (!data.data.rows || data.data.rows.length <= 0)){
				    	alert('所有数据已加载完毕');
				    	isLoadding = false;
				    	return;
				    }else if(start > 0){
				    	curPageStart = start + data.data.rows.length;
				    }else{
				    	curPageStart = data.data.rows.length;
				    }
				  
					for(var i = 0 ; i < data.data.rows.length ; i ++){
						
						if(!curMainProfileId){
							if(data.data.rows[i].lifeState == 'NOT_MONITORED'){
								//未监控
								data.data.rows[i].resourceShowName = '<span class="light-ico_resource res_unknow oc-datagrid-spanwdth" title="' + data.data.rows[i].resourceShowName + '">' + data.data.rows[i].resourceShowName + '</span>';
							}else if(data.data.rows[i].lifeState == 'MONITORED'){
								
								data.data.rows[i].resourceShowName = '<span class="light-ico_resource res_monitor oc-datagrid-spanwdth" title="' + data.data.rows[i].resourceShowName + '">'  + data.data.rows[i].resourceShowName + '</span>';
								
							}
							
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
						}else{
							if(data.data.rows[i].selectStatus == 0){
								//全部未选中
								data.data.rows[i].childResourceSelectStatus = '<span class="ico ico_selectno"></span>';
								data.data.rows[i].chooseChildResource = data.data.rows[i].selectStatus;
							}else if(data.data.rows[i].selectStatus == 2){
								//全部选中
								data.data.rows[i].childResourceSelectStatus = '<span class="ico ico_selectall"></span>';
								data.data.rows[i].chooseChildResource = data.data.rows[i].selectStatus;
							}else{
								//部分选中
								data.data.rows[i].childResourceSelectStatus = '<span class="ico ico_selectpart"></span>';
								data.data.rows[i].chooseChildResource = data.data.rows[i].selectStatus;
							}
						}
						
						//如果是翻页，则追到到表格最后
						if(start > 0){
							loadGrid.selector.datagrid('appendRow',data.data.rows[i]);
						}
						
					}
					if(start <= 0){
						loadGrid.selector.datagrid('loadData',data);
						if(data.data.selectAll){
							isAutoChecked = true;
							loadGrid.selector.datagrid('checkAll');
						}
					}
					
					if(curMainProfileId){
					  	 //注册子资源选择点击事件
						 $('.selectChildResourceHref').unbind('click');
	                   	 $('.selectChildResourceHref').on('click',function(e,checkInfo){
                    		
	                   		e.stopPropagation();
	                   		
	                   		$('#childResourceSelectRightParentGrid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
	                   		 
                    		$('#childResourceSelectLeftParentGrid').find('.datagrid-view2').find('tr').removeClass('datagrid-row-selected');
                    		$(e.target).parents('tr').first().addClass('datagrid-row-selected');
	                   		 
                    		$('.selectChildResourceHref').css('font-style','normal');
                    		$(this).css('font-style','italic');
                    		
                    		$('.selectChildResourceArrow').removeClass('selected_arrow');
                    		$(this).next().addClass('selected_arrow');
	                   		 
	                   		curChildPageStart = 0;
	                   		curSelectMainResourceForChildProfile = $(this).attr('parentId');
	                   		
                    		var interfaceState = "";
                    		
                    		//判断当前是否网络设备接口
                    		if(isNetWorkInterface){
                    			interfaceState = "all";
                    			if(interForm.ocui[0].jq.combobox('getValue') != 'all'){
                    				interForm.ocui[0].jq.combobox('setValue','all');
                    			}
                    		}
	                   		
	                   		//发送请求获取子资源列表
	           				oc.util.ajax({
		          					  successMsg:null,
		          					  url: oc.resource.getUrl('portal/strategydetail/getChildResourceForChildProfile.htm'),
		          					  data:{mainInstanceId:$(this).attr('parentId'),resourceId:childResourceID,
		          						  interfaceState:interfaceState,domainId:nowDomainId,start:curChildPageStart,pageSize:pageSize},
		          					  success:function(data){
		          						  if(curChildPageStart + pageSize < data.data.total){
		          							  //未加载完
		          							  childResourceIsLoadCompele = false;
		          						  }else{
		          							  //加载完
		          							  childResourceIsLoadCompele = true;
		          						  }
	          							  showChildResource(data,childResourceID,nowDomainId,checkInfo);
	          							  $(that).attr('childCount',data.data.total);
		      	                    	  if($(that).attr('selectStatus') == 2){
		      	                    		  	isAutoCheckAll = true;
		    	                    			childProfileRightGrid.selector.datagrid('checkAll');
		    	                    	  }
		          					  }
		          					  
		          				 });
	                   	 });
					}
					
					isLoadding = false;
				  
			  }
			  
		 });
		
	}
	
	//展示左右表格数据
	function showMainProfileResourceAllGrid(resourceModelId){
		
		var nowDomainId = -1;
		if(curStragetyType == 1){
			nowDomainId = curProfileDomainId;
		}

		oc.util.ajax({
			  successMsg:null,
			  url: oc.resource.getUrl('portal/strategydetail/getMainProfileResourceInstances.htm'),
			  data:{resourceId:resourceModelId,domainId:nowDomainId,profileId:curProfileData.profileInfo.profileId,start:0,pageSize:pageSize},
			  success:function(data){
				  
				  	curUserSelectMainResourceContent = '';
				  
				    curPageStart = data.data.rows.length;
				  
					for(var i = 0 ; i < data.data.rows.length ; i ++){
						
						if(data.data.rows[i].lifeState == 'NOT_MONITORED'){
							//未监控
							data.data.rows[i].resourceShowName = '<span class="light-ico_resource res_unknow oc-datagrid-spanwdth" style="text-overflow:ellipsis" title="' + data.data.rows[i].resourceShowName + '">' + data.data.rows[i].resourceShowName + '</span>';
						}else if(data.data.rows[i].lifeState == 'MONITORED'){
							
							data.data.rows[i].resourceShowName = '<span class="light-ico_resource res_monitor oc-datagrid-spanwdth" style="text-overflow:ellipsis" title="' + data.data.rows[i].resourceShowName + '">'  + data.data.rows[i].resourceShowName + '</span>';
							
						}
						
						//判断是否使用当前策略
						if(data.data.rows[i].strategyID == curProfileData.profileInfo.profileId){
							data.data.rows[i].checked = true;
						}
						
					}
					
					curRightResourceInstanceList = $.extend(true,[],data.data.instanceIds);
					curUserCheckedResource = $.extend(true,[],data.data.instanceIds);
					curUserCheckedResourceDomain = $.extend(true,[],data.data.domainIds);
					
					totalMainInstanceCount = data.data.totalRecord;
					
					pickGrid.selector.datagrid('loadData',data);
					if(data.data.selectAll){
						isAutoChecked = true;
						pickGrid.selector.datagrid('checkAll');
					}
					
					//保存和取消按钮
				    if(isAllowUpdateAllProfile || curIsQuickSelectStrategy){
						$("#" + resource_strategy_detail_operator_button).append($('<a/>').linkbutton('RenderLB',{
							text:'应用',
							iconCls:"fa fa-check-circle",
							onClick:function(){
								var stragetyType = curStragetyType;
								var singleStrategyInfo = curProfileData;
								
								//检查是否修改
								var updateInfo = checkProfileUpdateInfo(stragetyType,singleStrategyInfo,false,false);
								
								if(updateInfo == -1){
									//有阈值修改未完成
									alert('有阈值未修改完成,请修改!');
									return;
								}
								
								if(updateInfo.modifyInfo || updateInfo.modifyResourceInfo || updateInfo.modifyMetricInfo || updateInfo.modifyAlarmInfo){
									
									updateProfileTotalInfo(updateInfo,2,false,curStrategyIndex);
								}else{
									if(curIsQuickSelectStrategy){
										//指定策略
										setProfileForSingleInstance(false);
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
										 showMainProfileResourceLeftGrid(curProfileData.profileInfo.resourceId,
						    						$('#filterInstanceStateFormId').find('input[name=monitorState]:checked').val(),$('#searchResourceOrIpInput').val()
						    							,curPageStart,pageSize);
									 },200);
								 }
							}
					});
				  
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
			initAccrodion(false,false,-1,-1);
			childrenStrategy.clearCurActiveLi();
			strategyTitle.text("");
		}else if(code <= 0){
			//点击子策略
			initAccrodion(true,false,-1,-code);
			strategyTitle.text(curProfileData.profileInfo.profileName);
		}
		
	}
	
	//渲染指标表格
	function showMetricGrid(singleStrategyInfo){

		//判断是否域管理员,域管理员不能修改指标信息
		var isAllowUpdateMetricData = true;
		var inputDisabled = "";
		if(!isAllowUpdateAllProfile){
			//如果域管理员第一次创建个性化策略，允许修改指标
			if(curIsQuickSelectStrategy && !curIsUsePersonalizedProfile && curStragetyType == 2){
				isAllowUpdateMetricData = true;
			}else{
				isAllowUpdateMetricData = false;
			}
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
			         {field:'metricTypeEnum',title:'指标类型',width:84},
			         {field:'monitorFrequency',title:'监控频度',width:114,formatter:function(value,row,index,div){
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
			         {field:'metricThresholds',title:'阈值定义',width:222},
			         {field:'alarmFlapping',title:'告警Flapping',width:94,formatter:function(value,row,index,div){
			         		
			         		if(value){
			         		
			         			var isAllowUpdateFlapping = false;
			         			var isAddAvailiabelFlapping = true;
			         			
			         			if(row.metricTypeEnum == '可用性指标'){
			         				isAllowUpdateFlapping = true;
			         				isAddAvailiabelFlapping = false;
			         			}else{
			         				isAllowUpdateFlapping = !isAllowUpdateMetricData;
			         			}
			         		
								var alarmFlappingSingle = $("<select/>").appendTo(div).combobox({
									width:45,
									selected:false,
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
			         }
			     ]]
				
			};
		
		indicatorsDefiendGrid = oc.ui.datagrid(dataGridCfg);
		var rowsMetics = new Array();
		
		//初始化监控是否全部选中
		var initIsCheckAllMonitor = true;
		//初始化告警是否全部选中
		var initIsCheckAllAlarm = true;
		
		//初始化告警是否全部不可用
		var initIsDisabledAllAlarm = true;
		
		var thresholdsNum = 0;
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
			if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'InformationMetric'){
				alarmValue = "<input class='alarmCheckBoxClass' id='" + alarm_check_box + i + "' type='checkbox' value=1 disabled='true'>";
			}
			
			var metricNameAndUnit = null;
			if(singleStrategyInfo.metricSetting.metrics[i].unit == null || singleStrategyInfo.metricSetting.metrics[i].unit == ''){
				metricNameAndUnit = singleStrategyInfo.metricSetting.metrics[i].metricName;
			}else{
				metricNameAndUnit = singleStrategyInfo.metricSetting.metrics[i].metricName + '(' + singleStrategyInfo.metricSetting.metrics[i].unit + ')'
			}
			
			//指标类型
			var metricType = null;
			
			if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'AvailabilityMetric'){
				metricType = '可用性指标';
			}else if(singleStrategyInfo.metricSetting.metrics[i].metricTypeEnum == 'InformationMetric'){
				metricType = '信息指标';
			}else{
				metricType = '性能指标';
			}
			if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds == null || singleStrategyInfo.metricSetting.metrics[i].metricThresholds.length==0){
					
					rowsMetics.push({"monitor":monitorValue,"alarm":alarmValue,"metricTypeEnum":metricType,
						"meticsName":metricNameAndUnit,"monitorFrequency":singleStrategyInfo.metricSetting.metrics[i].dictFrequencyId,
						"metricThresholds":"","alarmFlapping":singleStrategyInfo.metricSetting.metrics[i].alarmFlapping,"supportFrequentList":singleStrategyInfo.metricSetting.metrics[i].supportFrequentList}); 
					
			}else{
					
					rowsMetics.push({"monitor":monitorValue,"alarm":alarmValue,"metricTypeEnum":metricType,
						"meticsName":metricNameAndUnit,"monitorFrequency":singleStrategyInfo.metricSetting.metrics[i].dictFrequencyId,
						"metricThresholds":"<div id='rangeSliderInput" + thresholdsNum + "' ></div>","alarmFlapping":singleStrategyInfo.metricSetting.metrics[i].alarmFlapping,
						"supportFrequentList":singleStrategyInfo.metricSetting.metrics[i].supportFrequentList}); 
					
					thresholdsNum++;
					
			}


		}
		
		var localData = {"data":{"total":0,"rows":rowsMetics},"code":200};
		indicatorsDefiendGrid.selector.datagrid('loadData',localData);
		
		$('#monitorTitleCheckBox').unbind();
		$('#alarmTitleCheckBox').unbind();
		//监听告警和监控列表头的checkbox点击事件
		$('#monitorTitleCheckBox').on('click',function(e){
			$('#alarmTitleCheckBox').removeAttr('disabled');
			if($(e.target).attr('checked') == 'checked'){
				$('.monitorCheckBoxClass').each(function(){
					var nowIndex = $(this).attr('id').replace(monitor_check_box,'');
					if(!$(this).attr('disabled')){
					    $(this).attr('checked',true);
					    //告警勾选,监控可用
					    $('#alarmTitleCheckBox').removeAttr('checked');
					    if(rowsMetics[nowIndex].metricTypeEnum!='信息指标'){
					    	$('#' + alarm_check_box + nowIndex).removeAttr('disabled');
					    }
					}
				});
			}else if($(e.target).attr('checked') == undefined){
				$('#alarmTitleCheckBox').attr('disabled',true);
				var isUnCheckAlarmTitle = false;
				$('.monitorCheckBoxClass').each(function(){
					var nowIndex = $(this).attr('id').replace(monitor_check_box,'');
					if(!$(this).attr('disabled')){
					    $(this).attr('checked',false);
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
			if($(e.target).attr('checked') == 'checked'){
				$('.alarmCheckBoxClass').each(function(){
					if(!$(this).attr('disabled')){
					    $(this).attr('checked',true);
					}
				});
			}else if($(e.target).attr('checked') == undefined){
				$('.alarmCheckBoxClass').each(function(){
					if(!$(this).attr('disabled')){
					    $(this).attr('checked',false);
					}
				});
			}
		});
		$('.monitorCheckBoxClass').on('click',function(e){
			
			var nowIndex = $(e.target).attr('id').replace(monitor_check_box,'');
			
			if($(e.target).attr('checked') == 'checked'){
				$('#alarmTitleCheckBox').removeAttr('disabled');
				$('#alarmTitleCheckBox').removeAttr('checked');
				var isCheckedAll = true;
				
				for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
					//查看是否全部选中
					if($('#' + monitor_check_box + i).attr('checked') == undefined){
						isCheckedAll = false;
						break;
					}
				}
				
				if(isCheckedAll){
					$('#monitorTitleCheckBox').attr('checked',true);
				}
				
				//监控勾选则联动告警可勾选
				$('#' + alarm_check_box + nowIndex).removeAttr('disabled');
				
			}else if($(e.target).attr('checked') == undefined){
				if($('#monitorTitleCheckBox').attr('checked') == 'checked'){
					$('#monitorTitleCheckBox').attr('checked',false);
				}
				//监控取消勾选则联动告警不可用
				$('#' + alarm_check_box + nowIndex).removeAttr('checked');
				$('#' + alarm_check_box + nowIndex).attr('disabled','true');
			}
			
		});
		$('.alarmCheckBoxClass').on('click',function(e){
			
			if($(e.target).attr('checked') == 'checked'){
				
				var isCheckedAll = true;
				
				for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
					//查看是否全部选中
					if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds == null){
						continue;
					}
					
					if(!$('#' + alarm_check_box + i).attr('checked') == undefined){
						isCheckedAll = false;
						break;
					}
				}
				
				if(isCheckedAll){
					$('#alarmTitleCheckBox').attr('checked',true);
				}
				
			}else if($(e.target).attr('checked') == undefined){
				if($('#alarmTitleCheckBox').attr('checked') == 'checked'){
					$('#alarmTitleCheckBox').attr('checked',false);
				}
			}
			
		});
		for(var i = 0 ; i < singleStrategyInfo.metricSetting.metrics.length ; i ++){
			
			if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds != null && singleStrategyInfo.metricSetting.metrics[i].metricThresholds.length > 0){
				var idName = '#rangeSliderInput' + nowIonSliderIndex;
				var singleIonRangeSlider = null;
				
				var value_1 = null;
				var value_2 = null;
				for(var t = 0 ; t < singleStrategyInfo.metricSetting.metrics[i].metricThresholds.length ; t++){
					if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].perfMetricStateEnum == 'Minor'){
						value_1 =  singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].thresholdValue;
					}else if(singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].perfMetricStateEnum == 'Major'){
						value_2 =  singleStrategyInfo.metricSetting.metrics[i].metricThresholds[t].thresholdValue;
					}
				}
				singleIonRangeSlider = $(idName).target([singleStrategyInfo.metricSetting.metrics[i].unit,value_1,value_2],!isAllowUpdateMetricData,true);
				
				ionRangeSlider.push(singleIonRangeSlider);
				
				nowIonSliderIndex ++;
				
			}
			
		}
				
		//判断监控是否全部选中
		if(initIsCheckAllMonitor){
			$('#monitorTitleCheckBox').attr('checked',true);
		}
		
		//判断告警是否全部选中
		if(initIsCheckAllAlarm){
			if(initIsDisabledAllAlarm){
				$('#alarmTitleCheckBox').attr('checked',false);
				$('#alarmTitleCheckBox').attr('disabled',true);
			}else{
				$('#alarmTitleCheckBox').attr('checked',true);
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
			  data:{profileInfo:updateInfo.modifyInfo,profileInfoVo:updateInfo.modifyResourceInfo,metricString:updateInfo.modifyMetricInfo,updateTime:nowTimestamp},
			  success:function(data){
				  
				  if(data.data){
					  alert('修改成功!');
					  
					  //判断告警策略是否需要修改
					  if(updateInfo.modifyAlarmInfo){
					  		oc.resource.alarmrules.saveAlarmSendSet();
					  }
					  
	    		  	  //在主策略中有资源移动,刷新资源列表
	    		  	  if(curIsQuickSelectStrategy && updateInfo.modifyResourceInfo && !isChildStrategy){
		    		  	  oc.resourced.manager.list.reLoadList();
	    		  	  }
					  
			    	  if(nextShowSelectIndex >= 2){
			    			showNextOperated(nextShowSelectIndex,isChildStrategy);
			    			return;
			    	  }
			    	  
			    	  //查看是有否修改人信息
			    	  if(isChildStrategy){
				    	   //修改基本信息的修改时间
				    	   curProfileData.children[strategyIndex].profileInfo.updateTime = nowTimestamp;
			    	  	   if(!curProfileData.children[strategyIndex].profileInfo.updateUser){
			    	  	   		curProfileData.children[strategyIndex].profileInfo.updateUser = oc.index.getUser().name;
			    	  	   		$('#profileInfoBaseUpdateUserName').text(oc.index.getUser().name);
			    	  	   }
			    	  }else{
			    	  	   //修改基本信息的修改时间
				    	   curProfileData.profileInfo.updateTime = nowTimestamp;
			    	  	   if(!curProfileData.profileInfo.updateUser){
			    	  	   		curProfileData.profileInfo.updateUser = oc.index.getUser().name;
			    	  	   		$('#profileInfoBaseUpdateUserName').text(oc.index.getUser().name);
			    	  	   }
			    	  }
			    	  
			    	  
			    	  //修改基本信息本地信息
			    	  if(updateInfo.modifyInfo){
			    		  
						  if(isChildStrategy){
							  //修改本地子策略数据
							  if(updateInfo.modifyInfo.profileName){
								  curChildProfileNavList[strategyIndex].find('.text:first').html(updateInfo.modifyInfo.profileName);
							  }
							  var newProfileInfo = curProfileData.children[strategyIndex].profileInfo;
							  newProfileInfo = $.extend(newProfileInfo,updateInfo.modifyInfo);
							  curProfileData.children[strategyIndex].profileInfo = newProfileInfo;
						  }else{
							  //修改本地父策略数据
							  if(updateInfo.modifyInfo.profileName){
								  $('#main_proflie_name_id').text(updateInfo.modifyInfo.profileName);
								  if(curStragetyType == 1 && !curIsQuickSelectStrategy){
									  oc.module.custom.strategy.querydatagrid();
								  }else if(curStragetyType == 2 && !curIsQuickSelectStrategy){
									  oc.module.personalize.profile.querydatagrid();
								  }
							  }
							  var newProfileInfo = curProfileData.profileInfo;
							  newProfileInfo = $.extend(newProfileInfo,updateInfo.modifyInfo);
							  curProfileData.profileInfo = newProfileInfo;
						  }
			    		  
			    	  }
			    	  
			    	  //修改本地资源选择信息
			    	  if(updateInfo.modifyResourceInfo != null){
			    		  
			    		  if(isChildStrategy){
			    			  //子策略
			    			  var oldInstanceRelation = $.extend(true,[],curProfileData.children[strategyIndex].profileInstanceRelations.instances);
			    			  
			    			  //修改当前右边缓存数据		
//			    			  curRightResourceInstanceList = $.extend(true,[],strategyChildPicktree.getRightTreeData());
							  
							  var reduceResources = new Array();
							  
							  if(updateInfo.modifyResourceInfo.reduceResources != null && updateInfo.modifyResourceInfo.reduceResources != ''){
								  
								  if(updateInfo.modifyResourceInfo.reduceResources.indexOf(',') == -1){
									  
									  //只有一个资源
									  reduceResources.push(updateInfo.modifyResourceInfo.reduceResources);
									  
								  }else{
									  
									  reduceResources = updateInfo.modifyResourceInfo.reduceResources.split(',');
									  
								  }
								  
								  //删除本地资源记录
								  for(var i = 0 ; i < reduceResources.length ; i++){
									  
									  for(var j = 0 ; j < oldInstanceRelation.length ; j++){
										  
										  if(oldInstanceRelation[j].instanceId == reduceResources[i]){
											  
											  oldInstanceRelation.splice(j,1);
											  
										  }
										  
									  }
									  
								  }
								  
							  }
							  
							  var addResources = new Array();
							  
							  if(updateInfo.modifyResourceInfo.resources != null && updateInfo.modifyResourceInfo.resources != ''){
								  
								  if(updateInfo.modifyResourceInfo.resources.indexOf(',') == -1){
									  
									  //只有一个资源
									  addResources.push(updateInfo.modifyResourceInfo.resources);
									  
								  }else{
									  
									  addResources = updateInfo.modifyResourceInfo.resources.split(',');
									  
								  }
								  
								  //判断相同类型的子策略是否监控改子资源,如果监控则从数据中移出
								  var updateResourceType = curProfileData.children[strategyIndex].profileInfo.resourceId;
								  var compareResourceChildProfile = new Array();
								  for(var i = 0 ; i < curProfileData.children.length ; i++){
								  		
								  		//遍历所有子策略,查找出所有和当前修改子策略类型相同的其他子策略
								  		if(i != strategyIndex && curProfileData.children[i].profileInfo.resourceId == updateResourceType){
								  			//记录类型相同的子策略下标
								  			compareResourceChildProfile.push(i);
								  		}
								  	
								  }
								  
								  //添加本地资源记录
								  for(var i = 0 ; i < addResources.length ; i++){
									  
									  //查找父ID
									  var parentId = -1;
									  for(var j = 0 ; j < curUserCheckedForAllChildResourceInChildProfile.length ; j ++){
									  	  if(addResources[i] == curUserCheckedForAllChildResourceInChildProfile[j].id){
										  	  parentId = curUserCheckedForAllChildResourceInChildProfile[j].pid;
										  	  break;
									  	  }
									  }

									  oldInstanceRelation.push({
										  parentInstanceId:parseInt(parentId),
										  instanceId:parseInt(addResources[i])
									  });
									  
									  //查看类型相同的子策略是否监控改资源，如果监控则移出
									  for(var x = 0 ; x < compareResourceChildProfile.length ; x ++){
									  		var index = compareResourceChildProfile[x];
									  		for(var y = 0 ; y < curProfileData.children[index].profileInstanceRelations.instances.length ; y ++){
									  			if(curProfileData.children[index].profileInstanceRelations.instances[y].instanceId == addResources[i]){
									  				//移出该子资源
									  				curProfileData.children[index].profileInstanceRelations.instances.splice(y,1);
									  			}
									  		}
									  		
									  }
									  
								  }
								  
							  }
							  
							  //替换旧本地数据
							  curProfileData.children[strategyIndex].profileInstanceRelations.instances = $.extend(true,[],oldInstanceRelation);
							  
			    		  }else{
			    		  	  			    		  
			    			  //主策略
			    			  var oldInstanceRelation = $.extend(true,[],curProfileData.profileInstanceRelations.instances);
							  //修改当前右边缓存数据			    			  
			    			  curRightResourceInstanceList = $.extend(true,[],curUserCheckedResource);
							  
							  var reduceResources = new Array();
							  
							  if(updateInfo.modifyResourceInfo.reduceResources != null && updateInfo.modifyResourceInfo.reduceResources != ''){
								  
								  if(updateInfo.modifyResourceInfo.reduceResources.indexOf(',') == -1){
									  
									  //只有一个资源
									  reduceResources.push(updateInfo.modifyResourceInfo.reduceResources);
									  
								  }else{
									  
									  reduceResources = updateInfo.modifyResourceInfo.reduceResources.split(',');
									  
								  }
								  
								  //删除本地资源记录
								  for(var i = 0 ; i < reduceResources.length ; i++){
									  
									  for(var j = 0 ; j < oldInstanceRelation.length ; j++){
										  
										  if(oldInstanceRelation[j].instanceId == reduceResources[i]){
											  
											  oldInstanceRelation.splice(j,1);
											  //取消监控主资源,子资源跟着取消监控
											  if(curProfileData.children){
											  	
											  		for(var x = 0 ; x < curProfileData.children.length ; x ++){
											  			if(curProfileData.children[x].profileInstanceRelations && curProfileData.children[x].profileInstanceRelations.instances){
											  				var instancesList = $.extend(true,[],curProfileData.children[x].profileInstanceRelations.instances);
											  				var newInstancesList = new Array();
											  				for(var y = 0 ; y < instancesList.length ; y ++){
											  					if(instancesList[y].parentInstanceId != reduceResources[i]){
											  						newInstancesList.push(instancesList[y]);
											  					}
											  					
											  				}
											  				curProfileData.children[x].profileInstanceRelations.instances = $.extend(true,[],newInstancesList);
											  			}
											  		}
											  	
								  			  }
											  
										  }
										  
									  }
									  
								  }
								  
							  }
							  
							  var addResources = new Array();
							  
							  if(updateInfo.modifyResourceInfo.resources != null && updateInfo.modifyResourceInfo.resources != ''){
								  
								  if(updateInfo.modifyResourceInfo.resources.indexOf(',') == -1){
									  
									  //只有一个资源
									  addResources.push(updateInfo.modifyResourceInfo.resources);
									  
								  }else{
									  
									  addResources = updateInfo.modifyResourceInfo.resources.split(',');
									  
								  }
								  
								  //添加本地资源记录
								  for(var i = 0 ; i < addResources.length ; i++){
										  
									  oldInstanceRelation.push({
										  parentInstanceId:0,
										  instanceId:parseInt(addResources[i])
									  });
									  
								  }
								  
							  }
							  //替换旧本地数据
							  curProfileData.profileInstanceRelations.instances = $.extend(true,[],oldInstanceRelation);
			    			  
			    		  }
			    		  
			    	  }
			    	  
			    	  //修改指标本地数据
			    	  if(updateInfo.modifyMetricInfo != null){
			    		  if(isChildStrategy){
			    			  //修改本地子策略指标数据
			    			  curProfileData.children[strategyIndex].metricSetting.metrics = getCurrentMetricInfo(curProfileData.children[strategyIndex]);
			    		  }else{
			    			  //修改本地父策略指标数据
			    			  curProfileData.metricSetting.metrics = getCurrentMetricInfo(curProfileData);
			    		  }
			    	  }
					  showNextOperated(nextShowSelectIndex);
				  }
				  
			  }
			  
		 });
		
	}
	
	function checkCurProfileType(type){
		
		  if(type == 'PERSONALIZE'){
			  curStragetyType = 2;
		  }else if(type == 'DEFAULT'){
			  curStragetyType = 0;
		  }else if(type == 'SPECIAL'){
			  curStragetyType = 1;
		  }
		
	}
	
	//获取当前指标信息
	function getCurrentMetricInfo(oldStrategyInstance){

		var strategyInstance = $.extend(true,{},oldStrategyInstance);
		
		if(curMetricGridIsCreate){
			var metricThresholdsNum = -1;
			for(var i = 0 ; i < strategyInstance.metricSetting.metrics.length ; i ++){
				//现在数据
				var nowIsMonitor = null;
				if($('#' + monitor_check_box + i).attr('checked') == 'checked'){
					nowIsMonitor = true;
				}else{
					nowIsMonitor = false;
				}
				
				var nowIsAlarm = null;
				if($('#' + alarm_check_box + i).attr('checked') == 'checked'){
					nowIsAlarm = true;
				}else{
					nowIsAlarm = false;
				}
				
				var nowFrequency = frequencyComboxs[i].combobox('getValue');
				
				var newMetric = {};
				newMetric.monitor = nowIsMonitor;
				newMetric.alarm = nowIsAlarm;
				newMetric.dictFrequencyId = nowFrequency;
				
				if(strategyInstance.metricSetting.metrics[i].metricThresholds != null && strategyInstance.metricSetting.metrics[i].metricThresholds.length > 0){
					metricThresholdsNum++;
					var ionValue = ionRangeSlider[metricThresholdsNum].getTargetValue();

					var nowThreshold1 = null;
					var nowThreshold2 = null;
					
					strategyInstance.metricSetting.metrics[i].metricThresholds[1].thresholdValue = ionValue[1] + "";
					strategyInstance.metricSetting.metrics[i].metricThresholds[2].thresholdValue = ionValue[2] + "";
					
					var nowAlarmFlapping = alarmFlappingArray[metricThresholdsNum].combobox('getValue');
					newMetric.alarmFlapping = nowAlarmFlapping;
			
				}
				
				strategyInstance.metricSetting.metrics[i] = $.extend(strategyInstance.metricSetting.metrics[i],newMetric);
				
			}
			
		}
		
		return strategyInstance.metricSetting.metrics;
	
		
	}
	
	function clearCacheData(){
			curRightResourceInstanceList = null;
			curUserCheckedForChildResourceInChildProfile = null;
			curUserCheckedForAllChildResourceInChildProfile = null;
			curInstanceIsHavePersonalizedProfile = null;
			curIsUsePersonalizedProfile = null;
			curMonitorUseProfileId = null;
	}
	
	//获取当前界面监控资源的域集合
	function getDomainListByMonitorInstance(){
		
	     var result = [], isRepeated;
	     for (var i = 0, len = curUserCheckedResourceDomain.length; i < len; i++) {
	        isRepeated = false;
	        for (var j = 0, len = result.length; j < len; j++) {
	              if (curUserCheckedResourceDomain[i] == result[j]) {   
	                  isRepeated = true;
	                  break;
	            }
	        }
	        if (!isRepeated) {
	           result.push(curUserCheckedResourceDomain[i]);
	        }
	     }
	      
	     return result;
	}
	
	//为资源快速指定策略
	function setProfileForSingleInstance(isChildStrategy){
	
		if(curStragetyType == 2){
							
				//应用个性化策略
				if(curIsUsePersonalizedProfile){
					//之前已使用性化策略
					alert('应用成功!');
					strategyDialog.panel('destroy');
				}else if(curInstanceIsHavePersonalizedProfile){
					//之前已有性化策略
					oc.util.ajax({
						successMsg:null,
						url: oc.resource.getUrl('portal/strategydetail/addInstanceIntoHistoryPersonalProfile.htm'),
						//有问题
						data:{profileId:curInstanceHistroyPersonalizedProfileId,instanceId:curUseQuickSelectResourceInstanceId},
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
							
							if(data.data){
								
								//添加成功
								alert('应用成功!');
								strategyDialog.panel('destroy');
								
							}else{
								alert('应用失败!');
							}
							
						}
						
					});
				}
				
			}else{
			
				//应用默认或者自定义策略
				if(curStragetyType == 0 && curMonitorUseProfileId != nowStrategyId){
					//应用默认
					oc.util.ajax({
						successMsg:null,
						url: oc.resource.getUrl('portal/strategydetail/addProfileIntoDefultProfile.htm'),
						data:{resourceInstanceId:curUseQuickSelectResourceInstanceId,profileId:nowStrategyId},
						success:function(data){
							//添加成功
							if(data.data){
								//添加成功
								alert('应用成功!');
								strategyDialog.panel('destroy');
								
							}
							
						}
						
					});
				}else if(curStragetyType == 1 && curMonitorUseProfileId != nowStrategyId){
					//应用自定义
					oc.util.ajax({
						successMsg:null,
						url: oc.resource.getUrl('portal/strategydetail/addProfileIntoSpecialProfile.htm'),
						data:{resourceInstanceId:curUseQuickSelectResourceInstanceId,profileId:nowStrategyId},
						success:function(data){
							
							if(data.data){
								
								//添加成功
								alert('应用成功!');
								strategyDialog.panel('destroy');
								
							}
							
						}
						
					});
				}else{
					alert('应用成功!');
					strategyDialog.panel('destroy');
				}
				
			}
	}
	
	function checkProfileUpdateInfo(stragetyType,info,isPersonalizeStrategy,isChildStrategy){
		
			//检查是否有为编辑完的阈值控件
			for(var i = 0 ; i < ionRangeSlider.length ; i ++){
				if(!ionRangeSlider[i].isValidate()){
					//未编辑完成
					return -1;
				}
			}
		
			var modifyInfo = null;
			
			var modifyResourceInfo = null;
			
			if(!isPersonalizeStrategy){
				//检查基本信息是否修改
				if(stragetyType != 0){
					modifyInfo = checkBaseInfoIsModify(info);
				}
				
				
				//检查策略的资源选择是否修改
				if(isChildStrategy){
					modifyResourceInfo = checkStrategyChildResourceIsModify(info);
				}else{
					modifyResourceInfo = checkStrategyMainResourceIsModify(info);
				}
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