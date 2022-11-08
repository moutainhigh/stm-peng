$(function(){

	oc.ns('oc.profiletimeline.detail');
	
	var curStrategyIndex = -1;
	
	var resourceChildrenStrategyAccordion = null;
	
	var strategyDialog = null;
	
	//初始化策略信息弹出层
	function initDetail(profileData){
		
		var allData = profileData;
		
		if(resourceChildrenStrategyAccordion){
			$('#profile_timeline_children_strategy_id').parent().remove();
			$('#profile_timeline_detail_west_id').append('<div style="height:480px"><div id="profile_timeline_children_strategy_id" class="oc-window-leftmenu"></div></div>');
		}
		
		resourceChildrenStrategyAccordion = $('#profile_timeline_children_strategy_id');
		resourceChildrenStrategyAccordion.accordion({
			animate:true
		});
		
		//渲染主策略
		var allowAddHtml = '<a title="' + allData.profileInfo.profileName + '" class="text-over">' + allData.profileInfo.profileName + '</a>';

		$('#profile_timeline_strategy_id').append(allowAddHtml);
		
		$('#profile_timeline_strategy_id').unbind('click');
		$('#profile_timeline_strategy_id').on('click',function(e){
			
			if(curStrategyIndex == -1){
				//点击主策略已经展示
				return;
			}
			
			initAccrodion(allData,-1);
			
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
				
				var childStrategyTitle = '<div class="personalizeChildProfileTitle">' + resourceList[i];
				
				resourceChildrenStrategyAccordion.accordion('add',{
					title:childStrategyTitle + '</div>',
	    			content: navList,
	    			selected: false
				});
				
				var navSubList = oc.ui.navsublist({
					selector:navList,
					addRowed:function(li,data){
						li.find('.text:first').attr('title',data.name);
					},
					click:function(href,data,e){
						
						if(data.index == curStrategyIndex){
							//点击子策略已经展示
							return;
						}
						initAccrodion(allData,data.index);
						
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
			
		}
		
		
	}
	
	function initAccrodion(profileData,index){
		
		curStrategyIndex = index;
		
		var profileId = -1;
		var resourceId = '';
		if(index == -1){
			profileId = profileData.profileInfo.profileId;
			resourceId = profileData.profileInfo.resourceId;
		}else{
			profileId = profileData.children[index].profileInfo.profileId;
			resourceId = profileData.children[index].profileInfo.resourceId;
		}
		
		$('#profile_timeline_content_div').html('');
		oc.resource.loadScript('resource/module/resource-management/timeline/js/timeline.js',function(){
				oc.timeline.dialog.show($('#profile_timeline_content_div'),profileId,resourceId,profileData.profileInfo.createUser);
		}); 
		
	}
	
	oc.profiletimeline.detail = {
		show:function(strategyId){
			
			refreshDialog(strategyId);
			
		}
	};
	
	//重新加载dialog
	function refreshDialog(profileId){

		strategyDialog = $('<div/>').dialog({
			width:'1130px',
			height:'585px',//575
		    title: '基线设置',
		    href: oc.resource.getUrl('resource/module/resource-management/timeline/profileTimeline.html'),
		    onLoad:function(){
		    	
				//请求策略信息
		    	oc.util.ajax({
		    		  successMsg:null,
			  		  url: oc.resource.getUrl('portal/strategydetail/getStrategyById.htm'),
			  		  data : {strategyId:profileId},
			  		  success: function(data){
			  		  		
			  			    if(data.code == 200){
			  			    	
			  			    	initDetail(data.data);
			  			    	
			  			    	initAccrodion(data.data,-1);
			  			    	
			  					$('#profile_timeline_detail_layout').layout({
			  						fit:true
			  					});
			  					
			  					$("#profile_timeline_operator_button").append($('<a/>').linkbutton('RenderLB',{
			  						text:'应用',
			  						iconCls:"fa fa-check-circle",
			  						onClick:function(){
			  							
			  							var timelineForm = $('#profile_timeline_detail_layout').find('#timelineDialogForm');
			  				    		var unSaveTimeline=false;
			  				    		timelineForm.find(".editDiv").each(function(i,div){
			  				    			if($(this).css("display")!="none"){
			  				    				unSaveTimeline=true;
			  				    			}
			  				    		});
			  				    		
			  				    		
			  				    		if(unSaveTimeline){
			  						    	oc.ui.confirm('还有基线没有保存，是否关闭窗口？',function(){
			  						    		strategyDialog.panel('destroy');
			  						    	},function(){
			  						    		//
			  						    	});
			  				    		}else{
			  				    			strategyDialog.panel('destroy');
			  				    		}
			  							
			  						}
			  					})).append($('<a/>').linkbutton('RenderLB',{
			  						text:'取消',
			  						iconCls:"fa fa-times-circle",
			  						onClick:function(){
			  							strategyDialog.panel('destroy');
			  						}
			  					}));
			  			    	
			  			    }else{
			  			    	alert('获取策略信息失败！');
			  			    }
				    	
			  		  }
		    	
		    	});
		    	
		    	
		    }
		});
		
	}
	
});