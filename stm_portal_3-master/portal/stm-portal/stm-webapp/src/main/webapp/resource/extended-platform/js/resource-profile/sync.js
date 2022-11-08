/**
 * 
 */
$(function(){
	var resourceData,profileData,childResource,childProfile,resourceMetric,profileMetric,treeNodeId;
	var setting={
		check : {
			enable : false,
			chkboxType : {"Y" : "p","N" : "s"}
		},
		data:{
			key:{
				children:"childCategorys",
				name:"name",
				title:"name"
			}
		},
		async : {
			url:baseUrl+"extendedplatform/resourceprofile/resourceList.htm",
			enable : true,
			dataType : "json"
		},
		callback:{
			onAsyncSuccess:function(event,treeId){
				var treeObj = $.fn.zTree.getZTreeObj(treeId)
				treeObj.expandAll(true);
			},
			onClick:function(event, treeId, treeNode){
				if(treeNode.isParent){
					initResourceGrid();
					initProfileGrid();
				}else{
					$("#oc-profile-data-div,#oc-resource-data-div").empty();
					treeNodeId = treeNode.id;
					createResourceElement(treeNodeId);
					createProfileElement(treeNodeId);
				}
			}
		}
	};
	
	var resourceTree = $.fn.zTree.init($("#ztree"),setting);
	initResourceGrid();
	initProfileGrid();
	
	$('#zkall').linkbutton({
	    iconCls: 'icon-add',
	    text:'展开所有',
	    onClick:function(){
	    	resourceTree.expandAll(true);
	    	initResourceGrid();
			initProfileGrid();
			$("#zkall,#zdall").toggle();
	    }
	});
	$('#zdall').linkbutton({
	    iconCls: 'icon-remove',
	    text:'折叠所有',
	    onClick:function(){
	    	resourceTree.expandAll(false);
	    	initResourceGrid();
			initProfileGrid();
			$("#zkall,#zdall").toggle();
	    }
	});
	function createProfileElement(nodeId){
		oc.util.ajax({
			url:baseUrl+"extendedplatform/resourceprofile/profilelistByResource.htm",
			data:{resourceId:nodeId},
			success:function(data){
				profileData = data;
				childProfile = [];
				var html='<div id="profile_info_tab" style="width:100%;height:100%;">';
				if(data){
					for(var i=0;i<data.length;i++){
						var grid_id="profile-"+data[i].resourceId+"-grid";
						html+='<div id="profile-div-'+data[i].profileId+'" profileId="'+data[i].profileId+'" title="'+data[i].resourceId+'"><table id="'+grid_id+'"></table></div>'
						childProfile.push({id:data[i].resourceId,name:data[i].profileName});
					}
				}
				html+='</div>';
				$("#oc-profile-data-div").html(html);
				var isClose = false;
				$('#profile_info_tab').tabs({
				    border:false,
				    fit:true,
				    onSelect:function(title,index){
				    	if($('#resource_info_tab').tabs() && $('#resource_info_tab').tabs().length>0){
					    	var tab = $('#resource_info_tab').tabs("getTab",title);
					    	if(tab){
					    		$('#resource_info_tab').tabs("select",title);
					    	}else{
					    		$.messager.alert('提示','该策略不存在模型，请确认模型是否被删除或更名！');
					    	}
				    	}
				    },
				    onBeforeClose:function(title,index){
				    	if(isClose){
				    		return true;
				    	}else{
				    		$.messager.confirm('提示','删除模型策略将同步删除策略相关资源、指标数据，确认要删除吗？',function(r){
							    if (r){
							    	isClose=true;
							    	var tab = $('#profile_info_tab').tabs("getTab",index);
							    	oc.util.ajax({
							    		url:baseUrl+"extendedplatform/resourceprofile/removeProfile.htm",
							    		data:{profileId:$(tab[0]).attr("profileid")},
							    		async:false
							    	});
							    	$.messager.alert("策略删除完成！");
							    	$('#profile_info_tab').tabs("close",title);
							    }
							});
							return false;
				    	}
					}
				});
				
				for(var i=0;i<childProfile.length;i++){
					if(!contains(childResource, childProfile[i], "id", "id")){
						$("#profile_info_tab .tabs li").each(function(){
							if($(this).find("a span").html()==childProfile[i].id){
								$(this).find("a span").attr("style","color:red;");
							}
						});
						
						var tab = $('#profile_info_tab').tabs("getTab",childProfile[i].id);
						$('#profile_info_tab').tabs("update",{
							tab: tab,
							options:{
								closable:true
							}
						});
					}
				}
				
				if(data){
					profileMetric={};
					for(var i=0;i<data.length;i++){
						var metricKey=data[i].resourceId;
						var grid_id="profile-"+metricKey+"-grid";
						var profileId = data[i].profileId;
						oc.util.ajax({
							url:baseUrl+"extendedplatform/resourceprofile/profileMetrics.htm",
							data:{profileId:profileId},
							async:false,
							success:function(gridData){
								profileMetric[metricKey]=gridData;
								$("#"+grid_id).datagrid({
									data:gridData,
									fit:true,
									rownumbers:true,
									singleSelect:true,
									fitColumns:true,
								    columns:[[
								        {field:'mkId',title:'ID',width:100},
								        {field:'metricId',title:'指标ID',width:100},
								        {field:'profileId',title:'操作',width:100,formatter:function(value,row,index){
								        	if(!contains(resourceMetric[metricKey], row, "id", "metricId")){
								        		return "<a class='profile-opear' profileId='"+row.profileId+"' href='javascript:;' style='color:red;'>删除</a>"
								        	}
								        	return "";
								        }},
								    ]]
								});
							}
						});
					}
				}
				if(profileMetric){
					for(var metric in profileMetric){
						var gridId = "profile-"+metric+"-grid";
						var resourceMetricData = resourceMetric[metric];
						var options = $("#"+gridId).datagrid("options");
						options.rowStyler=function(index,row){
							var tab = $('#resource_info_tab').tabs("getTab",metric);
							if(tab){
								if(!contains(resourceMetricData,row,"id","metricId")){
					        		return 'background-color:#ffee00;color:red;';
								}
							}
						};
						options.columns=[[
					        {field:'mkId',title:'ID',width:100},
					        {field:'metricId',title:'指标ID',width:100},
					        {field:'profileId',title:'操作',width:100,formatter:function(value,row,index){
					        	var tab = $('#resource_info_tab').tabs("getTab",metric);
								if(tab){
						        	if(!contains(resourceMetric[metric], row, "id", "metricId")){
						        		return "<a class='remove-profile-"+metric+"' metricId='"+row.metricId+"' href='javascript:;' style='color:red;'>删除</a>"
						        	}
								}
					        	return "";
					        }}
					    ]];
						options.onLoadSuccess=function(){
							$(".remove-profile-"+metric).bind('click',function(){
								var metricId = $(this).attr("metricId");
								$.messager.confirm('提示','你确认要删除该策略指标吗？',function(r){
								    if (r){
								    	var tab = $('#profile_info_tab').tabs("getSelected");
								    	var profileId = $(tab[0]).attr("profileid");
								    	oc.util.ajax({
								    		url:baseUrl+"extendedplatform/resourceprofile/removeMetric.htm",
								    		data:{profileId:profileId,metricId:metricId},
								    		async:false
								    	});
								    	$.messager.alert("提示","指标删除完成！");
								    	createProfileElement(treeNodeId);
								    }
								});
							});
						}
						$("#"+gridId).datagrid(options);
					}
					if(resourceMetric){
						for(var metric in resourceMetric){
							var gridId = "resource-"+metric+"-grid";
							var profileMetridData = profileMetric[metric];
							var options = $("#"+gridId).datagrid("options");
							options.rowStyler=function(index,row){
								var tab = $('#profile_info_tab').tabs("getTab",metric);
								if(tab){
									if(!contains(profileMetridData,row,"metricId","id")){
						        		return 'background-color:#ffee00;color:red;';
									}
								}
							};
							options.columns=[[
						        {field:'id',title:'指标ID',width:100},
						        {field:'name',title:'指标名称',width:100},
						        {field:'t-id',title:'操作',width:100,formatter:function(value,row,index){
						        	var rowObj =JSON.stringify({resource:metric,metricId:row.id,metricName:row.name});
						        	var tab = $('#profile_info_tab').tabs("getTab",metric);
									if(tab){
							        	if(!contains(profileMetric[metric], row, "metricId", "id")){
							        		return "<a class='add-metric-"+metric+"' metric='"+rowObj+"' href='javascript:;' style='color:red;'>添加指标策略</a>"
							        	}
									}
						        	return "";
						        }}
						    ]];
							options.onLoadSuccess=function(){
								$(".add-metric-"+metric).bind('click',function(){
									var metricObj = JSON.parse($(this).attr("metric"));
									$.messager.confirm('提示','你确认要添加该模型指标到策略吗？',function(r){
									    if (r){
									    	var tab = $('#profile_info_tab').tabs("getSelected");
									    	var profileId = $(tab[0]).attr("profileid");
									    	oc.util.ajax({
									    		url:baseUrl+"extendedplatform/resourceprofile/addMetric.htm",
									    		data:{resourceId:metricObj.resource, metricId:metricObj.metricId, profileId:profileId},
									    		async:false
									    	});
									    	$.messager.alert("提示", "指标已经添加到策略中！");
									    	createResourceElement(treeNodeId);
											createProfileElement(treeNodeId);
									    }else{
									    }
									});
								});
							}
							
							$("#"+gridId).datagrid(options);
						}
					}
				}
			}
		});
		
	}
	
	function createResourceElement(nodeId){
		oc.util.ajax({
			url:baseUrl+"extendedplatform/resourceprofile/resource.htm",
			data:{id:nodeId},
			success:function(element){
				var html='<div id="resource_info_tab" style="width:100%;height:100%;">';
				if(element){
					childResource = [];
					for(var i=0;i<element.length;i++){
						var grid_id="resource-"+element[i].id+"-grid";
						html+='<div id="resource-div-'+element[i].id+'" title="'+element[i].id+'"><table id="'+grid_id+'"></table></div>'
						childResource.push({id:element[i].id,name:element[i].name});
					}
				}
				html+='</div>';
				$("#oc-resource-data-div").html(html);
				$('#resource_info_tab').tabs({
				    border:false,
				    fit:true,
				    onSelect:function(title,index){
				    	if($('#profile_info_tab').tabs() && $('#profile_info_tab').tabs().length>0){
				    		var tab = $('#profile_info_tab').tabs("getTab",title);
				    		if(tab){
				    			$('#profile_info_tab').tabs("select",title);
				    		}else{
				    			$.messager.alert('提示','该模型不存在策略，请发现新资源来创建策略！');
				    		}
				    	}
				    }
				});
				initChildResourceGrid(element);
			}
		});
	}
	
	function initChildResourceGrid(data){
		if(data){
			resourceMetric = {};
			for(var i=0;i<data.length;i++){
				var grid_id = "resource-"+data[i].id+"-grid";
				var grid_data = data[i].childCategorys
				resourceMetric[data[i].id]=grid_data;
				$("#"+grid_id).datagrid({
					fit:true,
					rownumbers:true,
					singleSelect:true,
					fitColumns:true,
					data:grid_data,
				    columns:[[
				        {field:'id',title:'指标ID',width:100},
				        {field:'name',title:'指标名称',width:100}
				    ]]
				});
			}
		}
	}
	
	function initProfileGrid(data){
		oc.util.ajax({
			url:baseUrl+"extendedplatform/resourceprofile/parentProfiles.htm",
			success:function(data){
				profileData = data;
				$("#oc-profile-data-div").html('<table id="profile-info-grid"></table>');
				$("#profile-info-grid").datagrid({
					data:data,
					fit:true,
					rownumbers:true,
					singleSelect:true,
					fitColumns:true,
					rowStyler:function(index,row){
						if(!contains(resourceData,row,"id","resourceId")){
			        		return 'background-color:#ffee00;color:red;';
						}
					},
				    columns:[[
				        {field:'profileId',title:'策略ID',width:100},
				        {field:'profileName',title:'策略名称',width:100},
				        {field:'resourceId',title:'resourceId',width:100},
				        {field:'id',title:'操作',width:50,formatter:function(value,row,index){
				        	if(!contains(resourceData,row,"id","resourceId")){
				        		return "<a class='profile-opear' profileId='"+row.profileId+"' href='javascript:;' style='color:red;'>删除</a>"
							}
				        }}
				    ]],
				    onLoadSuccess:function(){
				    	$("#oc-profile-data-div a.profile-opear").click(function(){
				    		alert($(this).attr('profileId'));
				    	});
				    }
				});
			}
		});
	}
	
	
	function initResourceGrid(){
		oc.util.ajax({
			url:baseUrl+"extendedplatform/resourceprofile/parentResourceList.htm",
			success:function(data){
				resourceData = data;
				$("#oc-resource-data-div").html('<table id="resource-info-grid"></table>');
				$("#resource-info-grid").datagrid({
					data:data,
					fit:true,
					rownumbers:true,
					singleSelect:true,
					fitColumns:true,
				    columns:[[
				        {field:'id',title:'模型ID',width:100},
				        {field:'name',title:'模型名称',width:100}
				    ]]
				});
			}
		});
	}
	
	
	function contains(arry,obj,arryKey,objKey){
		var flag = false;
		if(arry && arry.length>0){
			for(var i=0;i<arry.length;i++){
				if(arryKey && objKey){
					if(arry[i][arryKey]==obj[objKey]){
						flag = true;
						break;
					}
				}else if(arryKey && !objKey){
					if(arry[i][arryKey]==obj){
						flag = true;
						break;
					}
				}else if(!arryKey && objKey){
					if(arry[i]==obj[objKey]){
						flag = true;
						break;
					}
				}else{
					if(arry[i]==obj){
						flag = true;
						break;
					}
				}
				
			}
		}
		return flag;
	}
	
});