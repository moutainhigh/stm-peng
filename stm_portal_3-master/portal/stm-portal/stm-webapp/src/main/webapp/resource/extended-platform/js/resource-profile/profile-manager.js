/**
 * 
 */
$(function(){
	$("#search-profile").linkbutton({
	    iconCls: 'icon-search',
	    text:'搜索',
	    onClick:function(){
	    	searchProfileById();
	    }
	});
	
	$("#delete-profile").linkbutton({
	    iconCls: 'icon-cancel',
	    text:'一键删除',
	    onClick:function(){
	    	$.messager.confirm("提示", "清空数据之前请先在资源管理中将相关资源取消监控再清空数据，你确定要清空吗？", function(n){
				if(n){
					deleteLastRel();
					deleteRel();
					deleteProfile();
				}
			});
	    }
	});
	
	$("#search-profile-resource").linkbutton({
	    iconCls: 'icon-search',
	    text:'搜索',
	    onClick:function(){
	    	searchProfileByResource();
	    }
	});
	
	$("#delete-profile-resource").linkbutton({
	    iconCls: 'icon-cancel',
	    text:'一键删除',
	    onClick:function(){
	    }
	});
	
	function searchProfileById(){
		$("#txt_resourceId").val("")
		var profileId = $("#txt_profileId").val();
		oc.util.ajax({
			url : baseUrl + "extendedplatform/resourceprofile/searchByProfileId.htm",
			data : {profileId : profileId},
			success : function(data) {
				if(data){
					if(data.profileInfos){
						initProfileGrid(data.profileInfos);
					}
					if(data.profileInstanceRel){
						initProfileInstanceRelGrid(data.profileInstanceRel);
					}
					
					if(data.profileInstanceLastRel){
						initProfileInstanceLastRelGrid(data.profileInstanceLastRel);
					}
				}
			}
		});
	}
	
	function searchProfileByResource(){
		$("#txt_profileId").val("");
		var resourceId = $("#txt_resourceId").val();
		oc.util.ajax({
			url : baseUrl + "extendedplatform/resourceprofile/searchByResourceId.htm",
			data : {resourceId : resourceId},
			success : function(data) {
				if(data){
					if(data.profileInfos){
						initProfileGrid(data.profileInfos);
					}
					if(data.profileInstanceRel){
						initProfileInstanceRelGrid(data.profileInstanceRel);
					}
					
					if(data.profileInstanceLastRel){
						initProfileInstanceLastRelGrid(data.profileInstanceLastRel);
					}
				}
			}
		});	
	}
	
	function initProfileGrid(data){
		$("#table_profile_info_grid").datagrid({
			data : data,
			height : 400,
			title : '策略管理',
			singleSelect : true,
			toolbar : [ {
				iconCls : 'icon-remove',
				text : '清空',
				handler : function() {
					$.messager.confirm("提示", "清空策略之前请先在资源管理中将相关资源取消监控再清空数据，你确定要清空吗？", function(n){
						if(n){
							deleteProfile();
						}
					});
				}
			} ],
			columns : [ [ 
			              {field : 'profileId',title : 'profileId'},
			              {field : 'profileName',title : 'profileName'},
			              {field : 'profileDesc',title : 'profileDesc'},
			              {field : 'isUse',title : 'isUse'},
			              {field : 'resourceId',title : 'resourceId'},
			              {field : 'parentProfileId',title : 'parentProfileId'},
			              {field : 'profileType',title : 'profileType'}
			] ],
			onClickRow : function(index, row) {
			}
		});
	}
	
	
	function initProfileInstanceRelGrid(data){
		$("#instance-profile").datagrid({
			data : data,
			title : "策略资源关系",
			height : 400,
			toolbar : [ {
				iconCls : 'icon-remove',
				text : '清空',
				handler : function() {
					$.messager.confirm("提示", "清空策略与资源的关系之前请先在资源管理中将资源取消监控再清空数据，你确定要清空吗？", function(n){
						if(n){
							deleteRel();
						}
					});
				}
			} ],
			columns : [ [ {
				field : 'instanceId',
				title : 'instanceId',
				width : 100
			}, {
				field : 'parentInstanceId',
				title : 'parentInstanceId',
				width : 100
			}, {
				field : 'profileId',
				title : 'profileId',
				width : 100
			}, {
				field : 'profileName',
				title : 'profileName',
				width : 100
			} ] ]
		});
	}
	
	function initProfileInstanceLastRelGrid(data){
		$("#instance-profile-last").datagrid({
			data : data,
			title : "策略资源历史关系",
			height : 400,
			toolbar : [ {
				iconCls : 'icon-remove',
				text : '清空',
				handler : function() {
					$.messager.confirm("提示", "清空策略与资源的关系之前请先在资源管理中将资源取消监控再清空数据，你确定要清空吗？", function(n){
						if(n){
							deleteLastRel();
						}
					});
				}
			} ],
			columns : [ [ {
				field : 'instanceId',
				title : 'instanceId',
				width : 100
			}, {
				field : 'parentInstanceId',
				title : 'parentInstanceId',
				width : 100
			}, {
				field : 'profileId',
				title : 'profileId',
				width : 100
			}, {
				field : 'profileName',
				title : 'profileName',
				width : 100
			} ] ]
		});
	}
	
	function deleteRel(){
		var profileId = $("#txt_profileId").val();
		var resourceId = $("#txt_resourceId").val();
		var url=baseUrl + "extendedplatform/resourceprofile/";
		var data;
		var status;
		if(profileId && !resourceId){
			url+="deleteRel.htm";
			data={profileId:profileId};
			status = 1;
		}else if(!profileId && resourceId){
			url+="deleteRelByResource.htm";
			data={resourceId:resourceId};
			status = 2;
		}
		oc.util.ajax({
			url:url,
			data:data,
			success:function(data){
				if(status == 1){
					searchProfileById();
				}else{
					searchProfileByResource();
				}
			}
		});
	}
	
	
	function deleteLastRel(){
		var profileId = $("#txt_profileId").val();
		var resourceId = $("#txt_resourceId").val();
		var url=baseUrl + "extendedplatform/resourceprofile/";
		var data;
		var status;
		if(profileId && !resourceId){
			url+="deleteLastRel.htm";
			data={profileId:profileId};
			status = 1;
		}else if(!profileId && resourceId){
			url+="deleteLastRelByResource.htm";
			data={resourceId:resourceId};
			status = 2;
		}
		oc.util.ajax({
			url:url,
			data:data,
			success:function(data){
				if(status == 1){
					searchProfileById();
				}else{
					searchProfileByResource();
				}
			}
		});
	}
	
	function deleteProfile(){
		var rows = $("#table_profile_info_grid").datagrid("getRows");
		var profileIds = [];
		if(rows){
			for(var i=0;i<rows.length;i++){
				profileIds.push(rows[i].profileId);
			}
		}
		oc.util.ajax({
			url:baseUrl + "extendedplatform/resourceprofile/deleteProfileInfos.htm",
			data:{ids:JSON.stringify(profileIds)},
			success:function(data){
				var profileId = $("#txt_profileId").val();
				var resourceId = $("#txt_resourceId").val();
				if(profileId && !resourceId){
					searchProfileById();
				}else if(!profileId && resourceId){
					searchProfileByResource();
				}
			}
		});
	}
});