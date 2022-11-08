$(function() {
	var bizSerIndexDivDatas = oc.business.service.index.datas();
	var chooseRowData = bizSerIndexDivDatas.chooseRowData,
		bizSerIndexDiv = bizSerIndexDivDatas.bizSerIndexDiv,
		bizSerIndexCenterDiv = bizSerIndexDivDatas.bizSerIndexCenterDiv,
		selectedTitle = null,clickData = null;
	var user = oc.index.getUser();
	$('#businessServiceAccordion_index').layout();
	var bizMainAccordionDiv = $("#bizMainAccordionDiv").accordion();
	var bizDepAccordion = buildAccordion($("#bizDepAccordion"));
	var bizSerAccordion = buildAccordion($("#bizSerAccordion"));
	function buildAccordion(jq){
		return oc.ui.navsublist({
			selector:jq,
			click:function(href,data,e){
				e.stopPropagation();
				oc.resource.loadScript('resource/module/business-service/js/graph.js?t'+new Date(),function(){
					oc.module.biz.ser.graph.portal(bizSerIndexCenterDiv,data);
				});
			},
			addRowed:function(li,data){
				li.find('.text:first').attr('value',data.id).attr('title',data.name).addClass('oc-datagrid-spanwdth');
				if(li.find('.text:first').html().length>6) li.find('.text:first').html(li.find('.text:first').html().substring(0,6)+"...");
				if(!user.systemUser==true) return;
				li.append($('<span id=\"sb_bizserdepser_'+ data.id+'\" onclick="" class="oc-thridico-place ico ico-rightarrow locate-right"></span>')
				  .on('click',function(e){
					  e.stopPropagation();
					  if(!(user.id==data.createrId) && !(user.id==data.createrId || user.systemUser==true)){
						  return;
					  }	
					  $("#biz-ser-t-window").css({display:'block',top:$(this).offset().top-136+'px',
						  left:$(this).offset().left+15+'px',position:'absolute'});
					  //权限控制start=========
					  if(user.systemUser==true){//系统管理员（编辑：谁创建的，谁编辑；删除：系统管理员和创建人）
						  $("#biz-ser-edit-record").show();
						  $("#biz-ser-edit-del,#biz-ser-edit-alarm,#biz-ser-edit-status").hide();
					  }
					  (function(){
						  $("#biz-ser-edit-record").unbind('click').on('click',function(e){
							  oc.resource.loadScript('resource/module/business-service/js/business_service_dep_detail.js',function(){
								  oc.module.biz.service.dep.open({
									  type:'edit',
									  row:data,
									  div:li
								  });
							  });
						  });
					  })();
				  }));
			}
		});
	}
	var bizAccordion = $('#businessServiceAccordion').accordion({
		animate:false,
		fit:true,
		onBeforeSelect:function(title,index){
			//请求业务服务记录详情数据
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/business/service/get.htm?id='+$(title).attr('value')),
				  data:null,
				  successMsg:null,
				  success: function(data){
					  $('#businessServiceAccordion').find(".accordion-header-selected").each(function(){
						  $(this).removeClass("accordion-header-selected");
					  });
					  $(title).parent().parent().addClass("accordion-header-selected");
					  chooseRowData = eval(data).data;
					  selectedTitle = title;
					  //重新定义选中chooseRowData，load右侧画图页面
					  bizSerIndexCenterDiv.load(oc.resource.getUrl('resource/module/business-service/business_list_tabs.html'),
		    			function(){
						  	oc.resource.loadScript('resource/module/business-service/js/tabs.js',function(){
						  		oc.business.service.tabs.open(chooseRowData.id);
						  	});
					  });
				  }
			});
			return false;
	}}),bizAccordionPanels = bizAccordion.accordion('panels'); 
	$("#biz-ser-t-window").on('mouseover',function(){
		$(this).show();
	}).on('mouseout',function(e){
		$(this).hide();
	});
	$("#bizSerBackBtn").on('click',function(e){
		e.stopPropagation();
		oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/business_service_main.html'),function(){});
	});
	//点击添加按钮
	$('#addBizServicebtn').on('click',function(e){
	  e.stopPropagation();
	  oc.resource.loadScript('resource/module/business-service/js/business_service_detail.js',function(){
		  oc.module.biz.service.open({
			  type:'add',
			  row:undefined,
			  ul:bizAccordion
		  });
	  });
	});
	
	//命名空间
	oc.ns('oc.business.service.west');
	function refreshBizAccordion(){
		//请求业务应用数据
		oc.util.ajax({
			  url: oc.resource.getUrl('portal/business/service/getList.htm'),
			  data:null,
			  successMsg:null,
			  success: function(data){
				  bizAccordion.empty();
				  var rows = eval(data).data.rows;
				  for(var i=0;i<rows.length;i++){
					  var row = rows[i];
					  oc.business.service.west.appendBizAccordionCfg(row,bizAccordion,user,false);
					  if(rows[i].id == chooseRowData.id){
						  bizAccordion.find("span[value="+row.id+"]").parent().parent().addClass("accordion-header-selected");
					  }
				  }
			  }
		});
	}
	refreshBizAccordion();
	function getBizDepOrBizSerDatas(type){
		//请求业务单位数据
		oc.util.ajax({
			url: oc.resource.getUrl('portal/business/service/dep/getList.htm'),
			data:{type:type},
			successMsg:null,
			success: function(data){
				var rows = data.data;
				for(var i=0;i<rows.length;i++){
					var row = rows[i];
					type==0?bizDepAccordion.add(row):bizSerAccordion.add(row);
				}
			}
		});
	}
	getBizDepOrBizSerDatas(0);
	getBizDepOrBizSerDatas(1);
	oc.business.service.west.datas = function(){
		return {
			chooseRowData:chooseRowData,
			bizSerIndexDiv:bizSerIndexDiv,
			bizSerIndexCenterDiv:bizSerIndexCenterDiv,
			bizAccordion:bizAccordion
		}
	}
	oc.business.service.west.clickData = function(){
		return clickData;
	}
	bizSerIndexCenterDiv.load(oc.resource.getUrl('resource/module/business-service/business_list_tabs.html'),
		function(){
		oc.resource.loadScript('resource/module/business-service/js/tabs.js',function(){
	  		oc.business.service.tabs.open(chooseRowData.id);
	  	});
 	});
	oc.business.service.west.refreshBizAccordion = function(){
		return refreshBizAccordion();
	}
	oc.business.service.west.appendBizAccordionCfg = function(row,bizAccordion,user,add){
		var status;
		if(!row.status){
			status = "<span class='light-ico greenlight oc-datagrid-spanwdth' title='"+row.name+"' style='width:70%'>"+row.name+"</span>";
	   	}else if(row.status =="CRITICAL"){
	   		status = "<span class='light-ico redlight oc-datagrid-spanwdth' title='"+row.name+"' style='width:70%'>"+row.name+"</span>";
	   	}else if(row.status =="SERIOUS"){
	   		status = "<span class='light-ico orangelight oc-datagrid-spanwdth' title='"+row.name+"' style='width:70%'>"+row.name+"</span>";
	   	}else if(row.status =="WARN"){
	   		status = "<span class='light-ico yellowlight oc-datagrid-spanwdth' title='"+row.name+"' style='width:70%'>"+row.name+"</span>";
	   	}else if(row.status =="NORMAL"){
	   		status = "<span class='light-ico greenlight oc-datagrid-spanwdth' title='"+row.name+"' style='width:70%'>"+row.name+"</span>";
	   	}
		var row_title = $('<span id=\"span_'+row.id+'\" value='+row.id+' ></span>')
		  .append('<span>'+status+'</span>')
		  .append($('<span id=\"sb_'+ row.id+'\" onclick="" class="ico ico-rightarrow locate-right"></span>')
				  .on('click',function(e){
					  e.stopPropagation();
					  if(!(user.id==row.createrId || add==true) && !(user.id==row.createrId || user.systemUser==true || add==true)){
						  return;
					  }	
					  var rowid= $(this).parent().attr('value');
					  clickData = rowid;
					  $("#biz-ser-t-window").css({display:'block',top:$(this).offset().top-136+'px',
						  left:$(this).offset().left+15+'px',position:'absolute'});
					  //权限控制start=========
					  if(user.systemUser==true){//系统管理员（编辑：谁创建的，谁编辑；删除：系统管理员和创建人）
						  if(user.id==row.createrId || add==true){//编辑
							  $("#biz-ser-edit-record,#biz-ser-edit-alarm,#biz-ser-edit-status,#biz-ser-edit-alarm").show();
						  }
						  if(user.id==row.createrId || user.systemUser==true || add==true){//删除
							  $("#biz-ser-edit-del").show();
						  }else{
							  $("#biz-ser-edit-del").hide();
						  }
				  	  }
					  //权限控制end===========
					  (function(d,a){
						  $("#biz-ser-edit-record").unbind('click').on('click',function(e){
							  oc.resource.loadScript('resource/module/business-service/js/business_service_detail.js',function(){
								  oc.module.biz.service.open({
									  type:'edit',
									  row:row,
									  ul:bizAccordion,
									  rowDiv:a
								  });
							  });
						  });
						  $("#biz-ser-edit-del").unbind('click').on('click',function(e){
							  $.messager.confirm('提示','确认要删除该条业务应用？',function(r){
								  if(r){
									oc.util.ajax({
										  url: oc.resource.getUrl('portal/business/service/del.htm?id='+row.id),
										  data:null,
										  successMsg:null,
										  success: function(data){
											  if(data.code && data.code==200){
												  bizAccordion.accordion('remove', row_title);
												  alert("删除成功");
												  if(bizAccordion.find(".panel").length==0){
													  bizSerIndexCenterDiv.empty();
												  }
											  }else if(data.code==299){
												  alert(data.data);
											  }
										  }
									});
								  }
							  })
						  });
					  })(rowid,$(this).parent());
				  }))
		  .append($('<span id=\"sb1_'+ row.id+'\" class="ico ico-bell locate-right margin-top8"></span>')
				  .on('click',function(e){
			  		e.stopPropagation();
		  			var addWindow = $('<div/>');
		  			var alarmDiv = $('<div/>');
		  			//构建dialog
		  			addWindow.dialog({
		  			  title:"告警信息",
		  			  href:oc.resource.getUrl('resource/module/business-service/alarm.html'),
		  			  width:800,
		  			  height:510,
		  			  modal:true,
		  			  onLoad:function(){
		  				oc.resource.loadScript('resource/module/business-service/js/alarm.js',function(){
		  					oc.module.biz.ser.alarm.open(row);
		  				});
		  			  }
		  			});
		  }));
		var accordionAddCfg = {
		  title: row_title,
		  selected: false
		};
		bizAccordion.accordion('add', accordionAddCfg);
		//权限控制start=========
		if(user.systemUser==true){//系统管理员
			$("#sb_"+row.id).show();
		}else{
			$("#sb_"+row.id).hide();
		}
		//权限控制end===========
	}
	oc.business.service.west.appendBizDepOrBizSerAccordionCfg = function(row,type){
		type==0?bizDepAccordion.add(row):bizSerAccordion.add(row)
	}
	oc.business.service.west.removeBizDepOrBizSerAccordionCfg = function(id){
		$("span[value='"+id+"'][class='text']").parent().remove();
	}
	
	$("#biz-ser-edit-alarm").on('click',function(e){
		var addWindow = $('<div/>');
		var alarmRulesDiv = $('<div/>');
		//构建dialog
		addWindow.dialog({
		  title:"告警设置",
		  content: alarmRulesDiv,
		  width:600,
		  height:470,
		  modal:true
		});
		alarmRulesDiv.panel({
			height:'430px',
			href:oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
			onLoad:function(){
				oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
					oc.resource.alarmrules.bizOpen(clickData);
				});
			}
		});
			
	});
	$("#biz-ser-edit-status").on('click',function(e){
		var addWindow = $('<div/>');
		//构建dialog
		addWindow.dialog({
		  title:"状态定义",
		  href:oc.resource.getUrl('resource/module/business-service/business_service_status.html'),
		  width:900,
		  height:500,
		  modal:true,
		  buttons:[{
				text:'确定',
				handler:function(){
					var statusType,deathRelation,seriousRelation,warnRelation,selectNullFlag = false; 
					addWindow.find($("input[name='status_radio']")).each(function(){
						if($(this).is(":checked"))
							statusType = $(this).val();
					});
					addWindow.find($("input[name='deathRelation']")).each(function(){
						if($(this).is(":checked"))
							deathRelation = $(this).val();
					});
					addWindow.find($("input[name='seriousRelation']")).each(function(){
						if($(this).is(":checked"))
							seriousRelation = $(this).val();
					});
					addWindow.find($("input[name='warnRelation']")).each(function(){
						if($(this).is(":checked"))
							warnRelation = $(this).val();
					});
					if(statusType == 1){
						addWindow.find(".bizSerStatusSelfDefine").find("select").each(function(){
							if($(this).combobox("getValue") == ""){
								selectNullFlag = true;
							}
						});
						if(selectNullFlag){
							alert("请选择相关下拉列表数据");
							return;
						}else{
							var statusData = {};
							//组装保存到后台的json数据
							if(addWindow.find("#deathGroupDiv").find("input[type=checkbox]").length==0){
								alert("请添加致命规则");
								return;
							}else{
								var deathGroupData = buildStatusJsonData(addWindow,0);
								statusData.deathGroupData = {
									death_relation:deathRelation,
									rows:deathGroupData
								};
							}
							if(addWindow.find("#seriousGroupDiv").find("input[type=checkbox]").length==0){
								alert("请添加严重规则");
								return;
							}else{
								var seriousGroupData = buildStatusJsonData(addWindow,1);
								statusData.seriousGroupData ={
									serious_relation:seriousRelation,
									rows:seriousGroupData
								};
							}
							if(addWindow.find("#warningGroupDiv").find("input[type=checkbox]").length==0){
								alert("请添加告警规则");
								return;
							}else{
								var warningGroupData = buildStatusJsonData(addWindow,2);
								statusData.warningGroupData = {
									warn_relation:warnRelation,
									rows:warningGroupData	
								};
							}
							statusData.bizMainId = clickData;
							oc.util.ajax({
								url: oc.resource.getUrl('portal/business/service/status/updateState.htm'),
								  data:{data:statusData},
								  successMsg:null,
								  success: function(data){
									  if(data.code&&data.code==200){
										  if(data.data.status!=null && data.data.status!=undefined && data.data.status!=""){
											  var statusObj = $("#business-service_index").find("span[value="+clickData+"]").find(".light-ico").removeClass();
										  }
										  if(data.data.status =="CRITICAL"){
											  statusObj.addClass('light-ico redlight');
										  }else if(data.data.status =="SERIOUS"){
											  statusObj.addClass('light-ico orangelight');
										  }else if(data.data.status =="WARN"){
											  statusObj.addClass('light-ico yellowlight');
										  }else if(data.data.status =="NORMAL"){
											  statusObj.addClass('light-ico greenlight');
										  }
										  statusObj.click();
										  alert("保存成功");
									  }else if(data.code==299){
										  alert(data.data);
									  }
									  addWindow.dialog('close');
								  }
							});
						}
					}else if(statusType == 0){
						oc.util.ajax({
							url: oc.resource.getUrl('portal/business/service/updateStatusType.htm'),
							data:{status_type:statusType,id:clickData},
								successMsg:null,
								success: function(data){
									addWindow.dialog('close');
								}
						});
					}
				}
			},{
				text:'取消',
				handler:function(){
					addWindow.dialog('close');
				}
			}]
		});
	});
	
	//权限控制start=========
	if(user.systemUser==true){//系统管理员
		$("#addBizServicebtn").show();
	}else{
		$("#addBizServicebtn").hide();
	}
	//权限控制end===========
});

function buildStatusJsonData(addWindow,type){
	var deathGroupData = [],groupDiv;
	if(type==0){
		groupDiv = addWindow.find("#deathGroupDiv");
	}else if(type==1){
		groupDiv = addWindow.find("#seriousGroupDiv");
	}else if(type==2){
		groupDiv = addWindow.find("#warningGroupDiv");
	}
	groupDiv.find("input[type=checkbox]").each(function(){
		var selectDiv = $(this).parent().next(".selectDiv"),rowData = {};
		rowData.id = selectDiv.find("input[name=id]").val();
		selectDiv.find("select").each(function(){
			var me = $(this);
			if(me.attr("id")=="resource_death"){
				rowData.resource_death=me.combobox("getValue");
			}else if(me.attr("id")=="intanceState"){
				rowData.intanceState=me.combobox("getValue");
			}else if(me.attr("id")=="childResourceDef"){
				rowData.childResourceDef=me.combobox("getValue");
			}else if(me.attr("id")=="metricDef"){
				rowData.metricDef=me.combobox("getValue");
			}else if(me.attr("id")=="metricDefState"){
				rowData.metricDefState=me.combobox("getValue");
			}
		});
		deathGroupData.push(rowData);
	});
	var groupData=[];
	for(var i=0;i<deathGroupData.length;i++){
		var rowData={};
		rowData.id = deathGroupData[i].id;
		rowData.biz_main_id = addWindow.find("#bizMainId").val();
		rowData.type = type;
		if(deathGroupData[i].childResourceDef){
			rowData.instance_id = deathGroupData[i].childResourceDef;
		}else{
			rowData.instance_id = deathGroupData[i].resource_death;
		}
		if(deathGroupData[i].metricDef){
			rowData.metric_id = deathGroupData[i].metricDef;
		}
		if(deathGroupData[i].intanceState){
			rowData.state = deathGroupData[i].intanceState;
		}else if(deathGroupData[i].metricDefState){
			rowData.state = deathGroupData[i].metricDefState;
		}
		groupData.push(rowData);
	}
	return groupData;
}