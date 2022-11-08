$(function(){

	oc.ns('oc.quick.strategy.detail');
	
	var nowStrategyId = null;
	
	var strategyTitle = null;
	
	var strategyDialog = null;
	
	var storeQuickSelectProfileList = null;
	
	var main_default_strategy_select = null;
	
	var quickSelectMainProfileSelect = null;
	
	var curIsUsePersonalizedProfile = null;
	
	//记录当前主策略ID
	var curMainProfileId = null;
	
	var curIsQuickSelectStrategy = null;
	
	var curStragetyType = null;
	
	//快速指定方式的资源实例ID
	var curUseQuickSelectResourceInstanceId = null;
	
	var curUseQuickSelectResourceId = null;
	
	var curMonitorUseProfileId = null;
	
	var lastSelectDefaultOrSpecialProfile = null;
	
	var curIsComboboxSelectProfile = false;
	
	//当前实例是否有过个性化策略
	var curInstanceIsHavePersonalizedProfile = null;
	
	var curInstanceHistroyPersonalizedProfileId = null;
	
	//判断是否允许修改整个策略
	var isAllowUpdateAllProfile = null;
	
	//构建accrodion
	function initQuickStrategyHead(isSwitch,nowSelect){
		
		var stragetyType = curStragetyType;
		var isQuickSelectStrategy = curIsQuickSelectStrategy;
		
		$('#resource_strategy_detail_layout').layout({
			fit:true
		});
		
		if(isQuickSelectStrategy && $('#resource_strategy_detail_layout').layout('panel','north').length <= 0){
			
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
					  name:storeQuickSelectProfileList[i].profileName,
					  type:storeQuickSelectProfileList[i].profileType
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
				 quickSelectMainProfileSelect.jq.combo('readonly',true);
			 }
			 
		}
		
	}
	
	//默认策略和个性化策略间的切换
	function switchDefaultOrPersonalizedStratety(nowSelect){
		if(nowSelect == 2){
			curStragetyType = 2;
			quickSelectMainProfileSelect.jq.combo('readonly',true);
			lastSelectDefaultOrSpecialProfile = nowStrategyId;
			if(curInstanceIsHavePersonalizedProfile){
				nowStrategyId = curInstanceHistroyPersonalizedProfileId;
			}
		}else{
			nowStrategyId = lastSelectDefaultOrSpecialProfile;
			quickSelectMainProfileSelect.jq.combo('readonly',false);
			
			for(var i = 0 ; i < storeQuickSelectProfileList.length ; i ++){
				
				if(storeQuickSelectProfileList[i].profileId == nowStrategyId){
					checkCurProfileType(storeQuickSelectProfileList[i].profileType);
					break;
				}
				
			}
			
		}
		 
		oc.quick.strategy.detail.clearDialogHtml();
		
		$('#resource_children_strategy_id').parent().remove();
		$('#resource_strategy_detail_west_id').append('<div style="height:480px"><div id="resource_children_strategy_id" class="oc-window-leftmenu"></div></div>');
		
	   	if(curStragetyType == 0){
			 oc.resource.loadScript('resource/module/resource-management/js/default_strategy_detail.js',function(){
				 oc.defaultprofile.strategy.detail.showInQuickSelect(nowStrategyId,strategyDialog,curUseQuickSelectResourceInstanceId);
			 });
		}else if(curStragetyType == 1){
			 oc.resource.loadScript('resource/module/resource-management/js/custom_strategy_detail.js',function(){
				 oc.customprofile.strategy.detail.showInQuickSelect(nowStrategyId,strategyDialog,curUseQuickSelectResourceInstanceId);
			 });
		}else if(curStragetyType == 2){
			 oc.resource.loadScript('resource/module/resource-management/js/personalize_strategy_detail.js',function(){
				 oc.personalizeprofile.strategy.detail.clearCurSelectChildProfileResourceIndex();
				 oc.personalizeprofile.strategy.detail.showInQuickSelect(nowStrategyId,strategyDialog,
						 curInstanceIsHavePersonalizedProfile,curUseQuickSelectResourceId,curUseQuickSelectResourceInstanceId);
			 });
		}
		
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
	
	oc.quick.strategy.detail = {
		show:function(instanceId){
			clearCacheData();
			curIsQuickSelectStrategy = true;
			curInstaceIsHavePersonalizedProfile = false;

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
				  data:{resourceId:instanceId},
				  success:function(data){
					  if(data.data.length>0 || data.data==null){
						  lastSelectDefaultOrSpecialProfile = null;
						  storeQuickSelectProfileList = $.extend(true,[],data.data);
						  var firstStrategyId = data.data[0].profileId;
						  curMonitorUseProfileId = firstStrategyId;
						  nowStrategyId = firstStrategyId;
						  curUseQuickSelectResourceInstanceId = instanceId;
						  curUseQuickSelectResourceId = data.data[0].resourceId;
						  if(data.data[0].profileType == 'PERSONALIZE'){
							  curStragetyType = 2;
							  curInstanceIsHavePersonalizedProfile = true;
							  curIsUsePersonalizedProfile = true;
							  curInstanceHistroyPersonalizedProfileId = data.data[0].profileId;
							  //当前策略为个性化策略
							  storeQuickSelectProfileList.splice(0,1);
							  refreshDialog(isSwitch,2,firstStrategyId);
						  }else if(data.data[0].profileType == 'DEFAULT'){
							  if(data.data.length > 1 && data.data[1].profileType == 'PERSONALIZE'){
								  curInstanceIsHavePersonalizedProfile = true;
								  curInstanceHistroyPersonalizedProfileId = data.data[1].profileId;
								  storeQuickSelectProfileList.splice(1,1);
							  }
							  curStragetyType = 0;
							  curIsUsePersonalizedProfile = false;
							  refreshDialog(isSwitch,1,firstStrategyId);
						  }else if(data.data[0].profileType == 'SPECIAL'){
							  if(data.data.length > 1 && data.data[1].profileType == 'PERSONALIZE'){
								  curInstanceIsHavePersonalizedProfile = true;
								  curInstanceHistroyPersonalizedProfileId = data.data[1].profileId;
								  storeQuickSelectProfileList.splice(1,1);
							  }
							  curStragetyType = 1;
							  curIsUsePersonalizedProfile = false;
							  refreshDialog(isSwitch,1,firstStrategyId);
						  }
					  }else{
						  alert('未成功绑定策略，请重新加入监控');
					  }
				  }
			 });
			
		},
		getCurMonitorUseProfileId:function(){
			return curMonitorUseProfileId;
		},
		getCurInstanceHistroyPersonalizedProfileId:function(){
			return curInstanceHistroyPersonalizedProfileId;
		},
		getCurIsUsePersonalizedProfile:function(){
			return curIsUsePersonalizedProfile;
		},
		clearDialogHtml:function(){
			$('#resource_strategy_info_title_id').html('');
			$('#resource_main_strategy_id').html('');
			$('#resource_children_strategy_id').html('');
			$('#resource_strategy_accordion_div').html('');
			$('#resource_strategy_accordion_div').append('<div id="resource_strategy_detail_center_id"></div>');
		}
	};
	
	//重新加载dialog
	function refreshDialog(isSwitch,nowSelect,profileId){
		var nowHeight = '615px';
		strategyDialog = $('<div/>').dialog({
			width:'1100px',
			height:nowHeight,
		    title: '编辑监控策略',
		    href: oc.resource.getUrl('resource/module/resource-management/resource_strategy_detail.html'),
		    onLoad:function(){
		    	 initQuickStrategyHead(isSwitch,nowSelect);
		    	 if(curStragetyType == 0){
		   			 oc.resource.loadScript('resource/module/resource-management/js/default_strategy_detail.js',function(){
						 oc.defaultprofile.strategy.detail.showInQuickSelect(profileId,strategyDialog,curUseQuickSelectResourceInstanceId);
					 });
		    	 }else if(curStragetyType == 1){
		   			 oc.resource.loadScript('resource/module/resource-management/js/custom_strategy_detail.js',function(){
						 oc.customprofile.strategy.detail.showInQuickSelect(profileId,strategyDialog,curUseQuickSelectResourceInstanceId);
					 });
		    	 }else if(curStragetyType == 2){
		   			 oc.resource.loadScript('resource/module/resource-management/js/personalize_strategy_detail.js',function(){
		   				 oc.personalizeprofile.strategy.detail.clearCurSelectChildProfileResourceIndex();
						 oc.personalizeprofile.strategy.detail.showInQuickSelect(profileId,strategyDialog,true,curUseQuickSelectResourceId,curUseQuickSelectResourceInstanceId);
					 });
		    	 }
		    	
		    },
		    onBeforeClose:function(){
		    	 if(curStragetyType == 0){
					 return oc.defaultprofile.strategy.detail.beforeColse();
		    	 }else if(curStragetyType == 1){
		    		 return oc.customprofile.strategy.detail.beforeColse();
		    	 }else if(curStragetyType == 2){
					 return oc.personalizeprofile.strategy.detail.beforeColse();
		    	 }
		    },
		    onDestroy:function(){
		    	oc.resource.alarmrules.reSetPage();
		    }
		});
		
	}
	
	function clearCacheData(){
			curRightResourceInstanceList = null;
			curUserCheckedForChildResourceInChildProfile = null;
			curUserCheckedForAllChildResourceInChildProfile = null;
			curInstanceIsHavePersonalizedProfile = null;
			curIsUsePersonalizedProfile = null;
			curMonitorUseProfileId = null;
	}
	
});