//@ sourceURL=inspect_list.js
(function($) {
    //console.info(oc.util);
	var curDataGrid = null;
	var reportDatagrid;
	var planDatagrid;
	var queryReportForm = null;
	 queryReportForm = oc.ui.form({
		selector: $('#inspect_report_main_form'),
		datetimebox : [{
			selector:'[name=inspectReportProduceStartTime]',
			width: 145,
			editable:false,
			showSeconds: false,
			value:oc.util.dateAddDay(new Date(), -30).stringify(),
			onSelect: function(date) {
			}
		}, {
			selector:'[name=inspectReportProduceEndTime]',
			editable:false,
			showSeconds: false,
			value:oc.util.dateAddDay(new Date(), +0).stringify(),
			onSelect: function(date) {
			},
			onChange: function(){
				var startStr = $('#inspect_report_main_form').find('#startTime').datetimebox('getValue').trim();
				var endStr = $('#inspect_report_main_form').find('#endTime').datetimebox('getValue').trim();
				var start=new Date(startStr.replace(/-/g,"/"));  
				var end=new Date(endStr.replace(/-/g,"/")); 
				 /* console.info(end);
				  console.info(start);
				  console.info();*/
				if(end<start){
					alert('开始日期应在结束日期之前！');	
				}
				
			}
		}]
	/*	combobox:[{
			selector: '#report_list_inspector_domain',
			width: 120,
			data: oc.index.getDomains(),
			onChange: function(id) {
				console.log();
				queryReportForm.ocui[1].reLoad(oc.resource.getUrl('inspect/plan/getUser.htm?domainId='+id));
				var data = $("#report_list_inspector_domain").combobox('getData');
				var text = '';
				for(var i = 0; i<data.length;i++){
					if(id == data[i].id){
						text = data[i].name;
						$("#report_list_inspector_domain1").val(text);
					}
				}
			}
		}, {
			selector: '#report_list_inspector_name',
			width: 120,
			fit: false,
			filter: function(d) {
				return d.data;
			}
		}]*/
	});
//	$("#startTime").datetimebox('setValue','');
//	$("#endTime").datetimebox('setValue','');
	var inspectObj = [];
	var domains = [];
	d= oc.index.getDomains();
	if(d && d.length > 0) {
		for(var i=0,len=d.length; i<len; i++) {
			var id = d[i].id;
			if(id && id != '') {
				domains[id] = d[i].name;
			}
		}
	}
	var queryPlanForm = oc.ui.form({
		selector: $('#inspect_plan_main_form'),
		/*combobox:[{
			selector: '#inspector_plan_type',
			data:[
			      {id:1, name: '手动'},
			      {id:2, name: '每天(自动)'},
			      {id:3, name: '每周(自动)'},
			      {id:4, name: '每月(自动)'},
			      {id:5, name: '自定义时间'}
			      ]
			}, {
				selector: '#inspector_plan_status',
				data:[
				      {id: 1, name: '启用'},
				      {id: 2, name: '停用'}
				]
			}, {
				selector: '#plan_list_inspector_domain',
				data: oc.index.getDomains(),
				filter: function(d) {
					if(d && d.length > 0) {
						for(var i=0,len=d.length; i<len; i++) {
							var id = d[i].id;
							if(id && id != '') {
								domains[id] = d[i].name;
							}
						}
					}
				},
				onChange: function(id) {
					//queryPlanForm.ocui[3].reload(oc.resource.getUrl('inspect/plan/getUser.htm?domainId='+id));
				}
			}, {
				selector: '[name=inspectPlanInspector]',
				fit: false,
				filter: function(d) {
					var data = d.data || [];
					for(var i=0,len=data.length; i<len; i++) {
						var id = data[i].id;
						if(id && id != '') {
							inspectObj[id] = data[i].name;
						}
					}
					return data;
				},
				url: oc.resource.getUrl('inspect/plan/getDomainsAllUser.htm'),
			}
		]*/
	});

	var domId=oc.util.generateId();
	var tab=$('#inspect_management_tabs').attr('id',domId).tabs({
		onSelect: function(title,index){
			if(index == 0){
				curDataGrid = reportDatagrid;
			}else{
				curDataGrid = planDatagrid;
			}
			if(curDataGrid){
				curDataGrid.selector.datagrid("reload");
			}
		}
	});
	
	var reportCondtion = tab.find("#china-oc-inspect-report-condition");
	var reportDatagrid = oc.ui.datagrid({
		selector:tab.tabs('getTab',0).find('.oc-datagrid'),
		pageSize:$.cookie('pageSize_inspect_list')==null ? 15 : $.cookie('pageSize_inspect_list'),
		queryForm: queryReportForm,
		url:oc.resource.getUrl('inspect/report/list.htm'),
		fit : true,
		hideReset:true,
		hideSearch:true,
		pageList: [15, 20, 30, 40],
		columns:
		[[ 
		    {
				field : 'id',
				title : '',
				hidden : true
			},{
				field : 'inspectReportStatus',
				title : '状态<span name="inspectReportStatus" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01/2),
					formatter:function(value,row,rowIndex){
						var status = '待提交';
						if(value == 0){
							status = '待提交'
						} else if(value==1){
							status = '已提交';
						}
						return '<label style="margin-left: 5px;">'+status+'</label>';
					}
			}, {
				field : 'inspectReportName',
				title : '巡检报告名称<span name="inspectReportName" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01*2),
				formatter: function(value, row, index) {
					var title = '';
					       
					if(row.inspectReportStatus) {
						title = "查看巡检报告";
					} else {
						title = "编辑巡检报告";
					}
					return '<span inspectorid="'+row.inspectorId+'" title="'+title+'" class="report-detail-edit oc-pointer-operate" reportdetailid="'+row.id+'" reportstatus="'+row.inspectReportStatus+'">'+value+'</span>'
				}
 			}, {
				field : 'inspectReportInspector',
				title : '巡检人 <span name="inspectReportInspector" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01)
			}, {
				field : 'inspectReportDomain',
				title : '域 <span name="inspectReportDomain" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				ellipsis:true,
				width : fixWidth(0.01)
			}, {
				field : 'inspectReportSubmitTime',
				title : '报告提交时间 ',
				sortable : false,
				width : fixWidth(0.01),
				formatter:function(value,row,index){
					if(row.inspectReportStatus==true){
						return row.inspectReportSubmitTime;
					}else{
						return '';
					}
				}
			}, {
				field : 'inspectReportTaskCreator',
				title : '计划创建人 <span name="inspectReportTaskCreator" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01),
	        	formatter:function(value,row,index){
	        		return value;
	        	}
	        }, {
				field : 'inspectReportPlanName',
				title : '巡检计划名称<span name="inspectReportPlanName" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01), 
				formatter:function(value,row,rowIndex){ 	         
	        		return value; 
        	    }
			}, {
				field : 'inspectReportProduceTime',
				title : '巡检时间',
				sortable : false,
				width : fixWidth(0.01), 
				formatter: function(value,row,rowIndex){ 	         
	        		 return value.substring(0,17); 
        	    }
			},{
				field : ' ',
				title : '操作',
				sortable : false,
				width : fixWidth(0.01), 
				formatter:function(value,row,rowIndex){
					var curUser = oc.index.getUser();					
					
					if(row.inspectReportStatus) { //已提交
						return '<span title="导出EXCEL" planid="'+row.id+'" class="fa fa-file-excel-o light_blue  export-to-excel"></span>' +
								'<span title="导出WORD" planid="'+row.id+'" class=" fa fa-file-word-o light_blue export-to-word" style="margin-left: 10px;"></span>' +
								'<span title="导出PDF" planid="'+row.id+'" class="fa fa-file-pdf-o light_blue export-to-pdf" style="margin-left: 10px;" ></span>';
					} else { //未提交
						if(curUser.id*1 == row.inspectorId*1) { //当前是巡检人
							return '<span title="提交巡检报告"  class="fa fa-check-circle light_blue   report-detail-submit" editTime="'+row.editTime+'" reportsubmitid="'+row.id+'"></span>';
						} else {
							return '<span title="提交巡检报告" class="fa fa-check-circle blue_gray  not-anth-user"></span>';
						}
					}
					
					
				/*	if(row.userType!=''&&row.userType!=null&&row.userType!=undefined&&row.userType=='creator'){
						return '<span class="icon-edit report-detail-edit" reportdetailid="'+row.id+'" reportstatus="true">';
					}
					if(row.userType!=''&&row.userType!=null&&row.userType!=undefined&&row.userType=='inspector'){
						if(row.inspectReportStatus == false){*/
			        		 //return '<span style="margin-left: 5px;" class="ico ico-submit report-detail-submit" reportsubmitid="'+row.id+'"></span>'; 
							/*}
							if(row.inspectReportStatus == true){
								return '<span class="ico ico-search report-detail-preview" reportpreviewid="'+row.id+'">';
							}
					}*/
					
        	     }
			} 
		]],
		onLoadSuccess: function(data){
			//非巡检人登录，如系统管理员。不能提交巡检人的报告
			tab.find('.not-anth-user').bind('click', function(e) {
				alert('你没有提交权限');
				e.stopPropagation();
			});
			
			tab.find(".report-detail-edit").bind('click',function(e){
				var id = $(this).attr("reportdetailid");
				var reportstatus = $(this).attr("reportstatus");
				var inspectorId = $(this).attr('inspectorid');
				
				var curUser = oc.index.getUser();
				if(inspectorId*1 != curUser.id*1) {//当前登录用户不是巡检人，只能查看报告
					_previewReport(id,reportstatus);
					e.stopPropagation();
				} else { //当前登录用户是巡检人
					if(reportstatus=="false"){ //如果报告没有提交，可以编辑报告
						_editReport(id, inspectorId);
						e.stopPropagation();
					}
					if(reportstatus=="true"){ //报告已经提交，只能查看报告
						_previewReport(id,reportstatus);
						e.stopPropagation();
					}
				}
			});
			var submit_id = null;
			var inspect_e = null;
			tab.find(".report-detail-submit").bind('click',function(e){
				inspect_e = e;
				submit_id = $(this).attr("reportsubmitid");
				oc.util.ajax({
								url: oc.resource.getUrl('inspect/report/loadBasic.htm'),
								data: {
									"id" : submit_id
								},
								async: false,
								successMsg: null,
								success: function(data) {
									var r = data.data;
									if(r && r.editTime && r.editTime != null && r.editTime != ""){
										_submitReport(submit_id);
									} else {
										oc.ui.confirm('这份报告你没做编辑，是否确认提交？', function() {
											_submitReport(submit_id);
										}, function() {
										});
									}
									inspect_e.stopPropagation();
								}
							});
			});
			
			tab.find('.export-to-excel').bind('click', function(e) {
				var id = $(this).attr("planid");
				_export(id, 'excel');
				e.stopPropagation();
			});
			
			tab.find('.export-to-word').bind('click', function(e) {
				var id = $(this).attr("planid");
				_export(id, 'word');
				e.stopPropagation();
			});

			tab.find('.export-to-pdf').bind('click', function(e) {
				var id = $(this).attr("planid");
				_export(id, 'pdf');
				e.stopPropagation();
			});
		},
		octoolbar:{
			left: [queryReportForm.selector],
			right:["",{
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){
					var startStr = $('#inspect_report_main_form').find('#startTime').datetimebox('getValue').trim();
					var endStr = $('#inspect_report_main_form').find('#endTime').datetimebox('getValue').trim();
					
					var start=new Date(startStr.replace(/-/g,"/"));  
					var end=new Date(endStr.replace(/-/g,"/"));  
					//console.info(end=statr);
					if(end<start){
						alert('开始日期应在结束日期之前！');	
					}else{
						$(reportDatagrid.selector).datagrid('reload');
					}
				}
				
				
				
			},{
				iconCls: 'icon-reset',
				text:"重置",
				onClick: function(){
					$("#startTime").datetimebox('setValue',oc.util.dateAddDay(new Date(), -365).stringify());
					$("#endTime").datetimebox('setValue',oc.util.dateAddDay(new Date(), +1).stringify());
				}
			}]
		},
		onClickCell: function(rowIndex, field, value) {
			
		}
	});
	
	//cookie记录pagesize
	var paginationObject_tab0 = tab.tabs('getTab',0).find('.oc-datagrid').datagrid('getPager');
	if(paginationObject_tab0){
		paginationObject_tab0.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_inspect_list');
				$.cookie('pageSize_inspect_list',pageSize);
			}
		});
	}
	
	var inspectPlan = tab.find('#china-oc-inspect-plan-condition');
	//用于查询过滤的隐藏域
	
	var planDatagrid = oc.ui.datagrid({
		selector: tab.tabs('getTab',1).find('.oc-datagrid'),
		queryForm: queryPlanForm,
		url: oc.resource.getUrl('inspect/plan/list.htm'),
		hideReset:true,
		hideSearch:true,
		octoolbar:{
			left:[inspectPlan,{
				iconCls: 'icon-search',
				text:"搜索",
				onClick: function(){
					$(planDatagrid.selector).datagrid('reload');
				}
			}]
		},
		pageSize:$.cookie('pageSize_inspect_list')==null ? 15 : $.cookie('pageSize_inspect_list'),
		pageList: [15, 20, 30, 40],
		columns:
		[[
			 {
				field : 'inspectPlanStatus',
				title : '状态<span name="inspectStatus" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01/2),
				formatter:function(value,row,rowIndex){
					var curUser = oc.index.getUser();
					if(curUser.id*1 == row.inspectPlanCreator*1) { //是否是创建人
						return "<span planstatus='"+value+"' clickFlag='enable' planid='"+row.id+"' class='oc-top0 locate-left plan-status-ope status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
        		 	} else if(curUser.systemUser) { //是否系统管理员
        		 		return "<span planstatus='"+value+"' clickFlag='enable' planid='"+row.id+"' class='oc-top0 locate-left plan-status-ope status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
        		 	}
        		 	//add by sunhailiang on 20170626
       		 		return "<span planstatus='"+value+"' clickFlag='disabled' planid='"+row.id+"' class='oc-top0 locate-left plan-status-ope status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"' style='cursor:auto;'></span>";
				}
			}, {
				field : 'insepctPalnTaskName',
				title : '巡检计划名称<span name="inspectName" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01*2),
				ellipsis:true
			}, {
				field : 'createUserName',
				title : '创建人<span name="InsepctCreator" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01/2),
				formatter: function(value, row, index) {
					return value;
				}
			}, {
				field : 'domainName',
				title : '域<span name="inspectDomain" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01/2),
			}, {
				field : 'inspectorName',
				title : '巡检人<span name="inspector" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.01/2),
			}, {
				field : 'inspectPlanLastEditTime',
				title : '最近一次编辑时间',
				sortable : false,
				width : fixWidth(0.01)
			}, {
				field : 'inspectPlanType',
				title : '巡检周期<span name="inspectType" class="datagrid-title-ico">&nbsp;</span>',
				sortable : false,
				width : fixWidth(0.015/2),
	        	formatter:function(value,row,index){
	        		switch(value){
	        		case 1:
	        			return "手动执行";
	        		case 2:
	        			return '自动执行(每天)';
	        		case 3:
	        			return '自动执行(每周)';
	        		case 4:
	        			return '自动执行(每月)';
	        		case 5:
	        			return '自动执行(自定义)';
	        		}
	        		return value;
	        	}
	        }, {
				field : 'inspectPlanLastExecTime',
				title : '最近一次执行时间',
				sortable : false,
				width : fixWidth(0.01), 
				formatter:function(value,row,rowIndex){ 	         
	        		return value; 
        	    }
			}, {
				field : 'inspectPlanDescrible',
				title : '描述',
				sortable : false,
				width : fixWidth(0.01), 
				formatter: function(value,row,rowIndex){ 	         
//	        		 return value; 
	        		 var inspectPlanDescrible = (value != null && value != '') ? value : '';
	        		 if(inspectPlanDescrible!=''&&inspectPlanDescrible.length > 10){
        				 var check = inspectPlanDescrible.substring(0,9)+'...';
        				 return '<span title="'+inspectPlanDescrible+'">'+check+'</span>';
        			 }else{
        				 return inspectPlanDescrible;
        			 }
        	    },
        	    ellipsis:true
			},{
				field : ' ',
				title : '操作',
				sortable : false,
				width : fixWidth(0.01), 
				formatter:function(value,row,rowIndex){
        			var curUser = oc.index.getUser();
					
					var editPlan = '<span class="icon-edit light_blue thiscompile edit-inspect-plan" planId="'+row.id+'" onclick="javascript:;" title="编辑"></span>';
					var commitPlan='';
					if(row.inspectPlanStatus==1){//0是停用，1是启用
						commitPlan = '<span class="fa fa-play-circle light_blue commit-inspect-plan-now" planId="'+row.id+'" planstatus="'+row.inspectPlanStatus+'" title="立即执行" style="margin-left:10px;margin-right:0px;"></span>';
					}
        		 	var copyPlan = '<span class="icon-copy light_blue copy-inspect-plan" srcplanid="'+row.id+'" title="复制" style="margin-left:10px"></span>';
        		 	var deletePlan = '<span planId="'+row.id+'" class="fa fa-trash-o light_blue del_plan" title="删除" style="margin-left:10px"/>';

        		 	if(curUser.id*1 == row.inspectPlanCreator*1) { //是否是创建人
        		 		return editPlan + commitPlan + copyPlan + deletePlan;
        		 	} else if(curUser.systemUser) { //是否系统管理员
        		 		return commitPlan + copyPlan + deletePlan;
        		 	} else { //普通用户
        		 		return '';
        		 	}
				}
			}
		]],
		onClickCell: function(rowIndex, field, value){
	       	 var row = planDatagrid.selector.datagrid("getRows")[rowIndex];
	       	 if(field=='inspectPlanStatus'){
	       		var planStatus = $('.oc-top0[planid="'+row.id+'"]'); 
	       		//add by sunhailiang on 20170626
				 if(planStatus.attr('clickFlag') == 'disabled'){
				   return false;
				 } 

	       		 var status = (value + 1)%2 == 0 ? false : true;
	       		_updatePlanStatus(row.id, status, planDatagrid); 

				if(status){
					planStatus.removeClass("close").addClass("open");
				}else{
					planStatus.removeClass("open").addClass("close");
				}

	       		row.inspectPlanStatus = value == 1 ? 0 : 1;
	       		planDatagrid.selector.datagrid("updateRow",{
					index : rowIndex,
					row : row
    			});
	       	 }
        },
		onLoadSuccess: function(data){
			
			// 查看巡检计划
//			tab.find('.view-inspect-plan').bind('click', function(e) {
//				var id = $(this).attr('planId');
//				_viewPlan(id);
//				e.stopPropagation();
//			});
			
			tab.find(".status").each(function(){
        		var dom = $(this),
        		data = dom.data("data");
        		data!=1&&dom.parent().parent().parent().addClass("grey_color");
        	 });
			
			//复制巡检计划
			tab.find('.copy-inspect-plan').bind('click', function(e) {
				var srcPlanId = $(this).attr('srcplanid');
				var dstPlanId = _copyPlan(srcPlanId);
				oc.resource.loadScript('resource/module/inspect-management/js/inspect_plan_edit.js', function() {
					oc.module.inspect.plan.open({
						type: 'edit',
						id: dstPlanId,
						refresh: function(flagId) {
							//复制询价计划第一个页面不能保存，所以删掉
							if(!flagId){
								_delcopyPlan(dstPlanId);
							}
							planDatagrid.selector.datagrid('reload');
						}
					});
				});
				e.stopPropagation();
			});
			// 删除巡检计划
			//planDatagrid.selector.find(".datagrid-toolbar .right").eq(0).hide();
			tab.find('.del_plan').bind('click', function(e) {
				var id = $(this).attr('planId');
				oc.ui.confirm('确认删除该计划？', function() {
					oc.util.ajax({
						url : oc.resource.getUrl('inspect/plan/delPlan.htm'),
						data : {
							"planIds": id
						},
						success : function(data) {
							planDatagrid.selector.datagrid('reload');
						}
					});
				});
				e.stopPropagation();
			});
			// 编辑巡检计划
			tab.find('.edit-inspect-plan').bind('click', function(e) {
				var id = $(this).attr('planId');
				_editPlan(id);
				e.stopPropagation();
			});
			// 执行巡检计划
			tab.find('.commit-inspect-plan-now').bind('click', function(e) {
				var id = $(this).attr('planId');
				var status = $(this).attr('planstatus');
				_executePlan(id, status);
				e.stopPropagation();
			});
			// 启用巡检计划
			tab.find('.enable-plan-status').bind('click', function(e) {
				var id = $(this).attr('id');
				_updatePlanStatus(id, true, planDatagrid);
				e.stopPropagation();
			});
			// 停用巡检计划
			tab.find('.disable-plan-status').bind('click', function(e) {
				var id = $(this).attr('id');
				_updatePlanStatus(id, false, planDatagrid);
				e.stopPropagation();
			});
		},
		octoolbar:{
			left:[queryPlanForm.selector],
			right:[{
				iconCls: 'fa fa-plus inspect-plan-add-btn',
				text: "添加",
				onClick: function() {
					oc.resource.loadScript('resource/module/inspect-management/js/inspect_plan_edit.js', function() {
						oc.module.inspect.plan.open({
							type: 'add',
							refresh: function() {
								planDatagrid.load();
							}
						});
					});
				}
			}]
		}
	});
	
	//cookie记录pagesize
	var paginationObject_tab1 = tab.tabs('getTab',1).find('.oc-datagrid').datagrid('getPager');
	if(paginationObject_tab1){
		paginationObject_tab1.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_inspect_list');
				$.cookie('pageSize_inspect_list',pageSize);
			}
		});
	}
		
	//巡检计划只有域管理员与超极管理员可见
	checkPlanAuth(tab);

	function _export(planId, type) {
		switch (type) {
		case "excel":
			top.location = oc.resource.getUrl('inspect/report/excel.htm?id=' + planId);
			break;
		case "word":
			top.location = oc.resource.getUrl('inspect/report/word.htm?id=' + planId);
			break;
		case "pdf":
			top.location = oc.resource.getUrl('inspect/report/pdf.htm?id=' + planId);
			break;
		}
	}
	
	function _copyPlan(id) {
		var newId = 0;
		oc.util.ajax({
			url : oc.resource.getUrl('inspect/plan/copy.htm'),
			data : {
				id: id
			},
			async: false,
			success : function(data) {
				newId = data.data;
				return newId;
			}
		});
		return newId;
	}
	
	function _delcopyPlan(id) {
		var newId = 0;
		oc.util.ajax({
			url : oc.resource.getUrl('inspect/plan/delPlan.htm'),
			data : {
				"planIds": id
			},
			async: false,
			success : function(data) {
				newId = data.data;
				return newId;
			}
		});
		return newId;
	}
	
	function bindMethod(){
		 //绑定过滤弹出框 
	   	 $('.datagrid-title-ico').each(function(){ 
	   		var filterMenu ;
	   		switch ($(this).attr('name')) {
				case 'inspectStatus':
					filterMenu = $('<div ></div>');
					filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter statusFilter"  value="1" type="checkbox" />&nbsp;启用</label></div>');
		    		filterMenu.append('<div><label class="datagridFilter" ><input class="datagridFilter statusFilter" value="0" type="checkbox" />&nbsp;停用</label></div>');	
					break;
				case 'inspectType':
					filterMenu = $('<div ></div>');
					filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter inspectTypeFilter" value="1" type="checkbox" />&nbsp;手动执行</label></div>');
		    		filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter inspectTypeFilter" value="2" type="checkbox" />&nbsp;自动执行(每天)</label></div>');	
		    		filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter inspectTypeFilter" value="3" type="checkbox" />&nbsp;自动执行(每周)</label></div>');	
		    		filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter inspectTypeFilter" value="4" type="checkbox" />&nbsp;自动执行(每月)</label></div>');	
		    		filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter inspectTypeFilter" value="5" type="checkbox" />&nbsp;自动执行(自定义)</label></div>');	
					break;
				case 'inspectDomain':
					filterMenu = $('<div ></div>');
					data = oc.index.getDomains();
					for(var i = 0; i< data.length;i++){
						filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter inspectDomainFilter" value="'+data[i].id+'" type="checkbox" />&nbsp;'+data[i].name+'</label></div>');

					}
					break;
				case 'inspectName':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter"><input class="datagridFilter planNameFilter text-box" type="text" ></input></label></div>');
					break;	
				case 'InsepctCreator':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter createUserFilter text-box" type="text" ></input></label></div>');
					break;
				case 'inspector':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter inspectorFilter text-box" type="text" ></input></label></div>');
					break;
			 	case 'inspectReportStatus':
			 		filterMenu = $('<div ></div>');
			 		filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter reportstatusFilter"  value="1" type="checkbox" />&nbsp;已提交</label></div>');
			 		filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter reportstatusFilter" value="0" type="checkbox" />&nbsp;待提交</label></div>');	
					break;
				case 'inspectReportDomain':
					filterMenu = $('<div ></div>');
					data = oc.index.getDomains();
					for(var i = 0; i< data.length;i++){
						filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter reportDomainFilter" value="'+data[i].name+'" type="checkbox" />&nbsp;'+data[i].name+'</label></div>');
					}
					break;
				case 'inspectReportName':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter"><input class="datagridFilter reportNameFilter text-box" type="text" ></input></label></div>');
					break;	
				case 'inspectReportInspector':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter reportinspectorFilter text-box" type="text" ></input></label></div>');
					break;
				case 'inspectReportTaskCreator':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter reportcreatorFilter text-box" type="text" ></input></label></div>');
					break;
				case 'inspectReportPlanName':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter reportplannameFilter text-box" type="text" ></input></label></div>');
					break;
				}
	   		$(this).menubutton({
				   menu: filterMenu
			});
	   		
	   		filterMenu.find('.datagridFilter').on('click',function(e){
	   			e.stopPropagation();
	   		});
	   		filterMenu.find('.planNameFilter').on('change',function(){
	   			tab.find('[name="insepctPalnTaskName"]').val($(this).val());
	   			$(planDatagrid.selector).datagrid('reload');
	   		})
	   		filterMenu.find('.createUserFilter').on('change',function(){
	   			tab.find('[name="createUserName"]').val($(this).val());
	   			$(planDatagrid.selector).datagrid('reload');
	   		})
	   		filterMenu.find('.inspectorFilter').on('change',function(){
	   			tab.find('[name="inspectPlanInspector"]').val($(this).val());
	   			$(planDatagrid.selector).datagrid('reload');
	   		})
	   		
	   		filterMenu.find('.reportNameFilter').on('change',function(){
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportName').val($(this).val());
	   			
	   			$(reportDatagrid.selector).datagrid('reload');
	   		})
	   		filterMenu.find('.reportinspectorFilter').on('change',function(){
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportInspector').val($(this).val());
	   			$(reportDatagrid.selector).datagrid('reload');
	   		})
	   		filterMenu.find('.reportcreatorFilter').on('change',function(){
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportTaskCreator').val($(this).val());
	   			$(reportDatagrid.selector).datagrid('reload');
	   		})
   			filterMenu.find('.reportplannameFilter').on('change',function(){
   			tab.find('#china-oc-inspcet-report-condition-inspectReportPlanName').val($(this).val());
   			$(reportDatagrid.selector).datagrid('reload');
	   		})
	   		
			//支持ie回车事件 add by sunhailiang on 20170628
            if(oc.util.isIE()){
			   filterMenu.find('input.text-box').keypress(function(e){
			   	  e = e || window.event;
				  e.stopPropagation();
				  if(e.keyCode == 13){ //绑定回车 
					  $(this).trigger('change');
				  }
			   });
			}

	   		filterMenu.find('.datagridFilter').on('change',function(e){
	   			switch ($(this).attr('class')) {
					case 'datagridFilter statusFilter':
						var statusQueryIdArr = null;
		    			filterMenu.find('.datagridFilter.statusFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==statusQueryIdArr){
		    						statusQueryIdArr=valueTemp;
		    					}else{
		    						statusQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			tab.find('[name="inspectPlanStatus"]').attr('value',statusQueryIdArr);
		    			$(planDatagrid.selector).datagrid('reload');
						break;
					case 'datagridFilter inspectTypeFilter':
						var cycleQueryIdArr = null;
		    			filterMenu.find('.datagridFilter.inspectTypeFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==cycleQueryIdArr){
		    						cycleQueryIdArr=valueTemp;
		    					}else{
		    						cycleQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			tab.find('[name="inspectPlanType"]').attr('value',cycleQueryIdArr);		
		    			$(planDatagrid.selector).datagrid('reload');
						break;
			   		case 'datagridFilter inspectDomainFilter':
						var cycleQueryIdArr = null;
		    			filterMenu.find('.datagridFilter.inspectDomainFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==cycleQueryIdArr){
		    						cycleQueryIdArr=valueTemp;
		    					}else{
		    						cycleQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			tab.find('[name="inspectPlanDomain"]').attr('value',cycleQueryIdArr);		
		    			$(planDatagrid.selector).datagrid('reload');
						break;
						
			   		case 'datagridFilter reportstatusFilter':
						var statusQueryIdArr = null;
						filterMenu.find('.datagridFilter.reportstatusFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==statusQueryIdArr){
		    						statusQueryIdArr=valueTemp;
		    					}else{
		    						statusQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			tab.find('#china-oc-inspcet-report-condition-inspectReportStatus').attr('value',statusQueryIdArr);
						$(reportDatagrid.selector).datagrid('reload');
						break;
					case 'datagridFilter reportDomainFilter':
						var cycleQueryIdArr = null;
						filterMenu.find('.datagridFilter.reportDomainFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==cycleQueryIdArr){
		    						cycleQueryIdArr=valueTemp;
		    					}else{
		    						cycleQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			tab.find('#china-oc-inspcet-report-condition-inspectReportDomain').attr('value',cycleQueryIdArr);		
						$(reportDatagrid.selector).datagrid('reload');
						break;
					}
	   			
	   		});
	   	    //为重置添加input,checkbox清空 
	   		tab.find('.l-btn-text').on('click',function(){
	   			filterMenu.find('.datagridFilter').each(function(i){
	   				var clearCheckBox = $(this);
	   				if(clearCheckBox.attr('type')=='checkbox'){
	   					clearCheckBox.attr('checked',false);
	   				}
	   			})
	   			//隐藏域清除
	   			tab.find('[name="insepctPalnTaskName"]').val('');
	   			tab.find('[name="createUserName"]').val('');
	   			tab.find('[name="inspectPlanInspector"]').val('');
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportName').val('');
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportInspector').val('');
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportTaskCreator').val('');
	   			tab.find('#china-oc-inspcet-report-condition-inspectReportPlanName').val('');
	   		})
	   	 });
	}
	bindMethod();
	
	var reLoadAll = function() {
		reportDatagrid.load();
		planDatagrid.load();
	};
	
	function _viewPlan(id) {
		
	}
	
	function _editPlan(id) {
		oc.resource.loadScript('resource/module/inspect-management/js/inspect_plan_edit.js', function() {
			oc.module.inspect.plan.open({
				type: 'edit',
				id: id,
				refresh: function() {
					planDatagrid.selector.datagrid('reload');
				}
			});
		});
	}
	
	function _executePlan(id, status) {
		if(status != undefined) {
			if(status*1 == 0) {
				alert('请启用该计划');
			} else {
				oc.util.ajax({
					url : oc.resource.getUrl('inspect/plan/exec.htm'),
					data : {
						"id": id
					},
					success : function(data) {
						alert("正在执行，请等待几秒！");
						setTimeout(function(){
						alert("执行成功!");
						planDatagrid.selector.datagrid('load');
						},5000);
					}
				});
			}
		}
	}
	
	function checkPlanAuth(tab) {
		//如果不是系统管理员或域管理员，则不显示巡检计划tab标签
		var user = oc.index.getUser();
		if(!user.systemUser && !user.domainUser) {
			tab.tabs('close', 1);
		}
	}
	
	function _updatePlanStatus(id, status) {
		if(id) {
			oc.util.ajax({
				url : oc.resource.getUrl('inspect/plan/updateState.htm'),
				data : {
					id: id,
					"state": status
				},
				async:false,
				successMsg : null,
				success : function(data) {
					if(data.code == 200 && data.data > 0) {
						if(status) {
							
							alert('启用成功!');
						} else {
							alert("停用成功!");
						}
//						planDatagrid.selector.datagrid('reload');
					} else {
						alert('更新状态失败!');
					}
				}
			});
		}
	}
	
	//打开巡检页面
	function _editReport(id, inspectorId) {
		oc.resource.loadScript('resource/module/inspect-management/js/inspect_report_detail.js', function() {
			oc.module.inspect.report.detail.open({
				id: id,
				inspectorId: inspectorId,
				refresh: function() {
					reportDatagrid.selector.datagrid('reload');
				}
			});
		});
	}
 //提交巡检报告
	function _submitReport(id){
		oc.util.ajax({
			url : oc.resource.getUrl('inspect/report/updateState.htm'),
			data : {
				id: id
			},
			async:false,
			successMsg : null,
			success : function(data) {
				if(data.code == 200 && data.data > 0) {
					alert('提交成功!');
					reportDatagrid.selector.datagrid('reload');
				} else {
					alert('提交失败!');
				}
			}
		});
	}
	//提交了以后预览报告
	function _previewReport(id,reportstatus){
		oc.resource.loadScript('resource/module/inspect-management/js/inspect_report_preview.js', function() {
			oc.module.inspect.report.preview.open({
				id: id,//id是巡检报告的id
				reportstatus:reportstatus
			});
		});
	}
	
	function fixWidth(percent)  
	{  
	    return document.body.clientWidth * percent ; //这里你可以自己做调整  
	}  
})(jQuery);