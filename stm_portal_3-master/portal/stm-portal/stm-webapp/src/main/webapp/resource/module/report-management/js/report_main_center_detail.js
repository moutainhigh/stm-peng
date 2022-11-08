(function($){
	oc.ns('oc.report.main.center.detail');
	//showType 0表示点击报表类型 ,1表示点击具体报表名称
	var dataOption ,tempIdArr,showType,datagrid,reportMainCenterDiv;
	
	function init(){
		//1.性能报告2.告警统计3.TOPN报告4.可用性报告5.综合性报告
//		var templateType ;
//		switch (dataOption.reportTemplateType) {
//		case 1:
//			templateType = '性能报告';
//			break;
//		case 2:
//			templateType = '告警统计';
//			break;
//		case 3:
//			templateType = 'TOPN报告';
//			break;
//		case 4:
//			templateType = '可用性报告';
//			break;
//		case 5:
//			templateType = '趋势报告';
//			break;
//		case 6:
//			templateType = '分析报告';
//			break;
//			
//		case 7:
//			templateType = '综合性报告';
//			break;
//		}
		reportMainCenterDiv = $('#reportMainCenter');
		var reportMainCenterTop = reportMainCenterDiv.find('#reportMainCenterTop');
		//用于查询过滤的隐藏域
		var reportQueryStatus = $('<input />').attr('name','reportQueryStatus').attr('type','text').css('display','none');
		var reportTemplateQueryCycle = $('<input />').attr('name','reportTemplateQueryCycle').attr('type','text').css('display','none');
		var reportQueryCreateUserName = $('<input />').attr('name','reportQueryCreateUserName').attr('type','text').css('display','none');
		var reportQueryName = $('<input/>').attr('name','reportName').attr('type','text').addClass('text-box').addClass('reportQueryName').css('display','none');
		var reportQueryDomain = $('<input/>').attr('name','reportQueryDomain').attr('type','text').addClass('text-box').addClass('reportQueryDomain').css('display','none');
		reportMainCenterTop.append(reportQueryDomain);
		reportMainCenterTop.append(reportQueryName);
		reportMainCenterTop.append(reportQueryStatus);
		reportMainCenterTop.append(reportTemplateQueryCycle);
		reportMainCenterTop.append(reportQueryCreateUserName);
		
		if(showType==1){
			//是否启用:0.启用1.停用
//			var templateStatus;
//			switch (dataOption.reportTemplateStatus) {
//			case 0:
//				templateStatus = '启用';
//				break;
//			case 1:
//				templateStatus = '停用';
//				break;
//			}
//			//报表周期：1.日报2.周报3.月报
//			var templateCycle;
//			switch (dataOption.reportTemplateCycle) {
//			case 1:
//				templateCycle = '日报';
//				break;
//			case 2:
//				templateCycle = '周报';
//				break;
//			case 3:
//				templateCycle = '月报';
//				break;
//			}
			
			var reportMainCenterInfo = reportMainCenterDiv.find('#reportMainCenterInfo').attr('style','height: 20px;');
			reportMainCenterDiv.find('.reportMainCenterDatagridParent').attr('style','height: 96%;');
//			var dateCreate = new Date(dataOption.reportTemplateCreateTime);
//			var dateStr = formatDate(dateCreate);
			
			oc.util.ajax({
				successMsg:null,
				url:oc.resource.getUrl('portal/report/reportTemplateList/getSimpleReportTemplateById.htm'),
				data:{reportTemplateId:dataOption.reportTemplateId},
				success:function(data){
					var reportTemplateInfoData = formatReportTemplateInfo(data.data);
					
					var labelTop = $('<label/>').html('创建人： '+reportTemplateInfoData.reportTemplateCreateUserName+'  创建时间： '+reportTemplateInfoData.reportTemplateCreateTime+'  报告周期： '+reportTemplateInfoData.reportTemplateCycle+'  状态： '+reportTemplateInfoData.reportTemplateStatus);
					reportMainCenterInfo.append(labelTop);
				}
			})
			
		}else{
			reportMainCenterDiv.find('#reportMainCenterInfo').attr('style','height: 0px;');
			reportMainCenterDiv.find('.reportMainCenterDatagridParent').attr('style','height: 100%;');
		}
		
		var reportMainCenterOctoolbar = $('<div/>');
		var label1 = $('<label/>').html('生成时间：从 ');
		var dateStartSelect = $('<select/>').attr('name','reportDateStartSelect').attr('id','reportDateStartSelect').addClass('oc-toolBar-dateSelect');
		var label2 = $('<label/>').html(' 到 ');
		var dateEndSelect = $('<select/>').attr('name','reportDateEndSelect').attr('id','reportDateEndSelect').addClass('oc-toolBar-dateSelect');
		
		reportMainCenterOctoolbar.append(label1);
		reportMainCenterOctoolbar.append(dateStartSelect);
		reportMainCenterOctoolbar.append(label2);
		reportMainCenterOctoolbar.append(dateEndSelect);
		reportMainCenterTop.append(reportMainCenterOctoolbar);
		//去除空格
//		reportMainCenterTop.find("[name='reportName']").blur(function(){
//			$(this).val($(this).val().replace(/\ /g,""));
//		})
		var initTime = new Date().stringify();
		var reportMainCenterFormObj = reportMainCenterDiv.find('#reportMainCenterForm');
		oc.ui.form({
			selector:reportMainCenterFormObj,
			datetimebox:[{
				value:initTime,
				selector:'.oc-toolBar-dateSelect',
				editable :false
			}]
		});
		
		//表格查询表单对象
		var queryForm=oc.ui.form({selector:reportMainCenterFormObj});
		
		var datagridUrl;
		if(showType==1){
			datagridUrl = oc.resource.getUrl('portal/report/reportTemplateList/getReportListByTemplateId.htm?reportTemplateId='+dataOption.reportTemplateId);
		}else{
			datagridUrl = oc.resource.getUrl('portal/report/reportTemplateList/getReportListByTemplateId.htm?reportType='+dataOption.reportTemplateType);
		}
		
		//业务报表不显示域列
		var datagridClumns;
		if(dataOption.reportTemplateType == 7){
			datagridClumns = [
			                  {field:'reportListId',title:'id',hidden:true,checkbox:false},
			                  {field:'reportStatus',title:'状态<span name="status" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:60,formatter:function(value,row,rowIndex){
			                 	//0.未阅1.已阅
			                 	switch (value) {
			         			case 0:
			         				return '未阅';
			         			case 1:
			         				return '已阅';
			         			}
			                  }},
			                  {field:'reportName',title:'报表名称<span name="rtName" class="datagrid-title-ico">&nbsp;</span>',width:100 , formatter:function(value,row,rowIndex,div){
			                 	// 加入手形样式
			         			div.parent().addClass('oc-pointer-operate');
			         			return value;
			                  }},
			                  {field:'reportTemplateCycle',title:'周期<span name="cycle" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:100,formatter:function(value,row,rowIndex){
			                 	var thisTemplateCycle;
			                 	switch (value) {
			              		case "1":
			              			thisTemplateCycle = '日报';
			              			break;
			              		case "2":
			              			thisTemplateCycle = '周报';
			              			break;
			              		case "3":
			              			thisTemplateCycle = '月报';
			              			break;
			              		}
			                 	 return thisTemplateCycle;
			                  }},
			                  {field:'reportTemplateCreateUser',title:'创建人<span name="createUser" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:100},
			                  {field:'reportGenerateTime',title:'生成时间',align:'left',width:100,sortable:true,formatter:function(value,row,rowIndex){
			                 	 return formatDate(new Date(value),null);
			                  }},
			                  {field: 'op',title:'操作',width:100,align:'left',formatter:function(value,row,rowIndex){
			                 	 if(row.removeAble){
			                 		 return '<a name="'+rowIndex+'" title="导出EXCEL" class="fa fa-file-excel-o light_blue"></a><a name="'+rowIndex+'" title="导出WORD" class="fa fa-file-word-o light_blue"></a><a name="'+rowIndex+'" title="导出PDF" class="fa fa-file-pdf-o light_blue"></a><a name="'+rowIndex+'" title="删除" class="fa fa-trash-o light_blue"></a>';
			                 	 }else{
			                 		 return '<a name="'+rowIndex+'" title="导出EXCEL" class="fa fa-file-excel-o light_blue"></a><a name="'+rowIndex+'" title="导出WORD" class="fa fa-file-word-o light_blue"></a><a name="'+rowIndex+'" title="导出PDF" class="fa fa-file-pdf-o light_blue"></a>';
			                 	 }
			                 	 
			                  }}
			              ];
		}else{
			datagridClumns = [
			                  {field:'reportListId',title:'id',hidden:true,checkbox:false},
			                  {field:'reportStatus',title:'状态<span name="status" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:60,formatter:function(value,row,rowIndex){
			                 	//0.未阅1.已阅
			                 	switch (value) {
			         			case 0:
			         				return '未阅';
			         			case 1:
			         				return '已阅';
			         			}
			                  }},
			                  {field:'reportName',title:'报表名称<span name="rtName" class="datagrid-title-ico">&nbsp;</span>',width:100 , formatter:function(value,row,rowIndex,div){
			                 	// 加入手形样式
			         			div.parent().addClass('oc-pointer-operate');
			         			return value;
			                  }},
			                  {field:'reportTemplateCycle',title:'周期<span name="cycle" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:100,formatter:function(value,row,rowIndex){
			                 	var thisTemplateCycle;
			                 	switch (value) {
			              		case "1":
			              			thisTemplateCycle = '日报';
			              			break;
			              		case "2":
			              			thisTemplateCycle = '周报';
			              			break;
			              		case "3":
			              			thisTemplateCycle = '月报';
			              			break;
			              		}
			                 	 return thisTemplateCycle;
			                  }},
			                  {field:'reportTemplateCreateUser',title:'创建人<span name="createUser" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:100},
			                  {field:'reportTemplateDomainName',title:'域<span name="domain" class="datagrid-title-ico">&nbsp;</span>',align:'left',width:100},
			                  {field:'reportGenerateTime',title:'生成时间',align:'left',width:100,sortable:true,formatter:function(value,row,rowIndex){
			                 	 return formatDate(new Date(value),null);
			                  }},
			                  {field: 'op',title:'操作',width:100,align:'left',formatter:function(value,row,rowIndex){
			                 	 if(row.removeAble){
			                 		 return '<a name="'+rowIndex+'" title="导出EXCEL" class="fa fa-file-excel-o light_blue"></a><a name="'+rowIndex+'" title="导出WORD" class="fa fa-file-word-o light_blue"></a><a name="'+rowIndex+'" title="导出PDF" class="fa fa-file-pdf-o light_blue"></a><a name="'+rowIndex+'" title="删除" class="fa fa-trash-o light_blue"></a>';
			                 	 }else{
			                 		 return '<a name="'+rowIndex+'" title="导出EXCEL" class="fa fa-file-excel-o light_blue"></a><a name="'+rowIndex+'" title="导出WORD" class="fa fa-file-word-o light_blue"></a><a name="'+rowIndex+'" title="导出PDF" class="fa fa-file-pdf-o light_blue"></a>';
			                 	 }
			                 	 
			                  }}
			              ];
		}
		
		datagrid = oc.ui.datagrid({
			selector:reportMainCenterDiv.find('#reportMainCenterDatagrid'),
			pageSize:$.cookie('pageSize_report_main')==null ? 15 : $.cookie('pageSize_report_main'),
			url:datagridUrl,  
			queryForm:queryForm,
			queryConditionPrefix:'',
			fitColumns:true,
			hideReset:true,
			hideSearch:true,
			octoolbar:{
				left:[reportMainCenterTop,{
					iconCls: 'icon-search',
					text:"查询",
					onClick: function(){
						var dateStartStr = reportMainCenterDiv.find('#reportDateStartSelect').datetimebox('getValue');
						var dateEndStr = reportMainCenterDiv.find('#reportDateEndSelect').datetimebox('getValue');	
						if(queryDateCheckMethod(dateStartStr,dateEndStr)){
							datagrid.reLoad();
						}
						
					}
				},{
					 id : 'resetBtn',
						iconCls:'icon-reset',
						text : '重置',
						onClick : function(){
							reportMainCenterDiv.find('.datagridFilter').each(function(i){
								var clearCheckBox = $(this);
								if(clearCheckBox.attr('type')=='checkbox'){
									clearCheckBox.attr('checked',false);
								}
							});
							
							//重置搜索时间
							reportMainCenterDiv.find('#reportDateStartSelect').datetimebox('setValue',initTime);
							reportMainCenterDiv.find('#reportDateEndSelect').datetimebox('setValue',initTime);
							
							$('.datagridFilter.reportNameFilter').val('');
							$('.datagridFilter.createUserFilter').val('');
							$('.datagridFilter.cycleFilter').prop('checked',false);
							$('.datagridFilter.domainFilter').prop('checked',false);
							$('.datagridFilter.statusFilter').prop('checked',false);
							//隐藏域清除
							reportMainCenterDiv.find('.reportQueryName').val('');
							reportMainCenterDiv.find('[name="reportQueryStatus"]').val('');
							reportMainCenterDiv.find('[name="reportTemplateQueryCycle"]').val('');
							reportMainCenterDiv.find('[name="reportQueryCreateUserName"]').val('');
							reportMainCenterDiv.find('[name="reportQueryDomain"]').val('');
							datagrid.reLoad();
						 }
					 }]
			},
			columns:[datagridClumns],
//		     onClickRow: function (rowIndex, rowData) {
//                 // 列表点击
//		    	 oc.resource.loadScript('resource/module/report-management/js/resourcesOutlook.js',function(){
//		 			oc.module.reportmanagement.report.rol.open({reportXmlDataId:rowData.reportXmlData,callback:function(){datagrid.reLoad();}});
//		 		});
//		     },
		     onClickCell : function(rowIndex, field, value){
		    	 if(field=='reportName'){
		    		 var rows = datagrid.selector.datagrid('getRows');
			    	 var rowData = rows[rowIndex];
			    	// 列表点击
			    	 oc.resource.loadScript('resource/module/report-management/js/resourcesOutlook.js',function(){
			 			oc.module.reportmanagement.report.rol.open({reportXmlDataId:rowData.reportXmlData,callback:function(){datagrid.reLoad();}});
			 		});
		    	 }
		     },
		     onLoadSuccess:function(data){
		    	 var rows = datagrid.selector.datagrid('getRows');
		    	 reportMainCenterDiv.find('.reportTemplateCreateUser').on('click',function(){
		    		 var rowIndex = $(this).attr('name');
	        		 var row = rows[rowIndex];
		    	 })

		    	 //删除方法
		    	 reportMainCenterDiv.find('.fa-trash-o').on('click',function(event){
		    		 event.stopPropagation();
		    		 var rowIndex = $(this).attr('name');
	        		 var row = rows[rowIndex];
		    		 if($(this).attr('class') == "fa fa-trash-o light_blue"){
		    			 oc.ui.confirm('确认删除该报告?',function(){
		    			 oc.util.ajax({
								url:oc.resource.getUrl('portal/report/reportTemplateList/delReportListById.htm'),
								data:{reportListId:row.reportListId,reportTemplateCreateUserId:row.createUserId,domainId:row.reportTemplateDomainId},
								success:function(data){
									if(!data.data){
										alert('删除失败!');
									}else if("permissionFalse" == data.data){
										alert('您没有权限删除该报告!');
									}
									datagrid.reLoad();
								}
						 })
		    			})
		    		 }
		    	 })
		    	 reportMainCenterDiv.find('.fa').each(function(){
		    		 
		    		 var rowIndex = $(this).attr('name');
	        		 var row = rows[rowIndex];
		    		 
		    		 switch ($(this).attr('class')) {
					case "fa fa-file-excel-o light_blue":
						$(this).click(function(event){
							event.stopPropagation();
							$("<iframe style='display:none;' src='"+oc.resource.getUrl('portal/report/reportTemplateXmlInfo/getFileDownload.htm?xmlFileID='+
									row.reportXmlData+'&modelFileId='+row.reportModelName+'&reportTemplateId='+row.reportTemplateId+'&reportCreateTimeStr='+formatDate(new Date(row.reportGenerateTime),'day')+'&type=excel'
									)+"'/>").appendTo("body");
//							window.open(oc.resource.getUrl('portal/report/reportTemplateXmlInfo/getFileDownload.htm?xmlFileID='+
//									row.reportXmlData+'&modelFileId='+row.reportModelName+'&type=excel'
//									));
						});
//						$(this).attr('href',oc.resource.getUrl('portal/report/reportTemplateXmlInfo/getFileDownload.htm?xmlFileID='+
//								row.reportXmlData+'&modelFileId='+row.reportModelName+'&type=excel'
//						));
						break;
					case "fa fa-file-word-o light_blue":
						$(this).click(function(event){
							event.stopPropagation();
							$("<iframe style='display:none;' src='"+oc.resource.getUrl('portal/report/reportTemplateXmlInfo/getFileDownload.htm?xmlFileID='+
									row.reportXmlData+'&modelFileId='+row.reportModelName+'&reportTemplateId='+row.reportTemplateId+'&reportCreateTimeStr='+formatDate(new Date(row.reportGenerateTime),'day')+'&type=word'
									)+"'/>").appendTo("body");
						});
						break;
					case "fa fa-file-pdf-o light_blue":
						$(this).click(function(event){
							event.stopPropagation();
							$("<iframe style='display:none;' src='"+oc.resource.getUrl('portal/report/reportTemplateXmlInfo/getFileDownload.htm?xmlFileID='+
									row.reportXmlData+'&modelFileId='+row.reportModelName+'&reportTemplateId='+row.reportTemplateId+'&reportCreateTimeStr='+formatDate(new Date(row.reportGenerateTime),'day')+'&type=pdf'
									)+"'/>").appendTo("body");
						});
						break;
					}
		    		 
		    	 })
		    	 
		     }
		});
		
		//cookie记录pagesize
		var paginationObject = reportMainCenterDiv.find('#reportMainCenterDatagrid').datagrid('getPager');
		if(paginationObject){
			paginationObject.pagination({
				onChangePageSize:function(pageSize){
					var before_cookie = $.cookie('pageSize_report_main');
					$.cookie('pageSize_report_main',pageSize);
				}
			});
		}
		
	}
//	//左侧菜单折叠按钮
//	function collapse_btn(){
//		$("#collapse_btn").click(function(){
//			if($(this).hasClass('fa-angle-double-left')){
//				$('.report_management').layout('collapse','west');
//				$('.report_management').layout('panel','center').parent().css('left','0px');
//				$(this).removeClass('fa-angle-double-left');
//				$(this).addClass('fa-angle-double-right');
//			}else{
//				$('.report_management').layout('expand','west');
//				$(this).removeClass('fa-angle-double-right');
//				$(this).addClass('fa-angle-double-left');
//			}
//		});
//	}
	
	function queryDateCheckMethod(dateStartStrSource,dateEndStrSource){
		var dateStartStr = dateStartStrSource.trim();
		var dateEndStr = dateEndStrSource.trim();
		if(dateStartStr=='' || dateEndStr==''){
			alert('日期输入不能为空！');
			return false;
		}
		var dateStartStrFormmat = dateStartStr.replace(/-/g,"/");
		var dateEndStrFormmat = dateEndStr.replace(/-/g,"/");
		var dateStart,dateEnd;
		if(isNaN(dateStartStrFormmat)){
			dateStart = new Date(dateStartStrFormmat);
		}else{
			alert('请输入正确的日期格式！');
			return false;
		}
		if(isNaN(dateEndStrFormmat)){
			dateEnd = new Date(dateEndStrFormmat);
		}else{
			alert('请输入正确的日期格式！');
			return false;
		}
		
		var timeSub ;
		if(isNaN(dateEnd.getTime()) || isNaN(dateStart.getTime())){
			alert('请输入正确的日期格式！');
			return false;
		}else{
			timeSub = dateEnd.getTime() - dateStart.getTime();
			if(timeSub<0 ){
				alert('开始日期应在结束日期之前！');
				return false;
			}else{
				return true;
			}
		}
	}
	
	function bindMethod(){
		 //绑定过滤弹出框 
		reportMainCenterDiv.find('.datagrid-title-ico').each(function(){
	   		var menuElement = $(this);
	   		var filterMenu ;
	   		switch (menuElement.attr('name')) {
				case 'status':
					filterMenu = $('<div ></div>');
					filterMenu.append('<div><label class="datagridFilter" ><input class="datagridFilter statusFilter" value="1" type="checkbox" />&nbsp;已阅</label></div>');
		    		filterMenu.append('<div><label class="datagridFilter" ><input class="datagridFilter statusFilter" value="0" type="checkbox" />&nbsp;未阅</label></div>');	
					
		    		bindDatagridFilter(menuElement,filterMenu);
		    		break;
				case 'cycle':
					filterMenu = $('<div ></div>');
					filterMenu.append('<div><label class="datagridFilter" ><input class="datagridFilter cycleFilter" value="1" type="checkbox" />&nbsp;日报</label></div>');
		    		filterMenu.append('<div><label class="datagridFilter" ><input class="datagridFilter cycleFilter" value="2" type="checkbox" />&nbsp;周报</label></div>');	
		    		filterMenu.append('<div><label class="datagridFilter" ><input class="datagridFilter cycleFilter" value="3" type="checkbox" />&nbsp;月报</label></div>');	

		    		bindDatagridFilter(menuElement,filterMenu);
		    		break;
				case 'rtName':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div height="35px"><label class="datagridFilter" ><input onChangeFlag="true" class="datagridFilter reportNameFilter text-box min140" type="text" ></input></label></div>');

		    		bindDatagridFilter(menuElement,filterMenu);
					break;	
				case 'createUser':
					filterMenu = $('<div style="width:175px;"></div>');
					filterMenu.append('<div ><label class="datagridFilter" ><input onChangeFlag="true" class="datagridFilter createUserFilter text-box" type="text" ></input></label></div>');

		    		bindDatagridFilter(menuElement,filterMenu);
					break;
				case 'domain':
					oc.util.ajax({
						successMsg:null,
						url:oc.resource.getUrl('portal/report/reportTemplateList/getUserDomain.htm'),
						success:function(data){
							if(data.data.length>0){
								filterMenu = $('<div ></div>');
								for(var i=0;i<data.data.length;i++){
									filterMenu.append('<div><label class="datagridFilter" ><input  class="datagridFilter domainFilter" value="'+data.data[i].domainId+'" type="checkbox" />&nbsp;'+data.data[i].domainName+'</label></div>');
								}

					    		bindDatagridFilter(menuElement,filterMenu);
							}
						}
					})
					break;
				}
	   		
	   	 });
		
		
		
	}
	
	function bindDatagridFilter(menuElement,filterMenu){
		menuElement.menubutton({
			   menu: filterMenu
		});
		
		filterMenu.find('.datagridFilter').on('click',function(e){
			e.stopPropagation();
		});
		filterMenu.find('.reportNameFilter').on('change',function(){
			reportMainCenterDiv.find('[name="reportName"]').val($(this).val());
			if($(this).attr('onChangeFlag')=='true'){
				datagrid.reLoad();
			}
			$(this).attr('onChangeFlag','true');
		})
		filterMenu.find('.createUserFilter').on('change',function(){
			reportMainCenterDiv.find('[name="reportQueryCreateUserName"]').val($(this).val());
			if($(this).attr('onChangeFlag')=='true'){
				datagrid.reLoad();
			}
			$(this).attr('onChangeFlag','true');
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
		    			reportMainCenterDiv.find('[name="reportQueryStatus"]').attr('value',statusQueryIdArr);
		    			datagrid.reLoad();
		    			
						break;
					case 'datagridFilter cycleFilter':
						var cycleQueryIdArr = null;
		    			filterMenu.find('.datagridFilter.cycleFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==cycleQueryIdArr){
		    						cycleQueryIdArr=valueTemp;
		    					}else{
		    						cycleQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			reportMainCenterDiv.find('[name="reportTemplateQueryCycle"]').attr('value',cycleQueryIdArr);
		    			
		    			datagrid.reLoad();
						break;
					case 'datagridFilter domainFilter':
						var domainQueryIdArr = null;
		    			filterMenu.find('.datagridFilter.domainFilter').each(function(i){
		    				var valueTemp = $(this).val();
		    				if($(this).prop('checked')){
		    					if(null==domainQueryIdArr){
		    						domainQueryIdArr=valueTemp;
		    					}else{
		    						domainQueryIdArr+=','+valueTemp;
		    					}
		    				}
		    			})
		    			reportMainCenterDiv.find('[name="reportQueryDomain"]').attr('value',domainQueryIdArr);
		    			
		    			datagrid.reLoad();
						break;
					}
				
			});
		
		
		
		
//		filterMenu.find('.reportNameFilterQueryButton').unbind('click');
//		filterMenu.find('.reportNameFilterQueryButton').on('click',function(e){
//			$('.reportQueryName').val($('.reportNameFilter').val());
//			datagrid.reLoad();
//		});
//		<a class="reportNameFilterQueryButton" >确定</a>
		
	}
	
	
	function formatDate(date,type){
		switch(type){
		case 'day':
			
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
			break;
		default:
			
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
			break;
		}
	}
function formatReportTemplateInfo(reportTemplateInfoData){
		
		var reportTemplateName,
		reportTemplateCycle,
		reportTemplateStatus,
		reportTemplateCreateTime,
		reportTemplateCreateUserName;
		
		reportTemplateName = reportTemplateInfoData.reportTemplateName;
		reportTemplateCycle = fromatTemplateCycle(reportTemplateInfoData.reportTemplateCycle);
		reportTemplateStatus = formatTemplateStatus(reportTemplateInfoData.reportTemplateStatus);
		reportTemplateCreateUserName = reportTemplateInfoData.reportTemplateCreateUserName;
		reportTemplateCreateTime = formatCreateTime(reportTemplateInfoData.reportTemplateCreateTime,null);
		
		return {reportTemplateName:reportTemplateName,reportTemplateCycle:reportTemplateCycle,reportTemplateStatus:reportTemplateStatus,
			reportTemplateCreateUserName:reportTemplateCreateUserName,reportTemplateCreateTime:reportTemplateCreateTime};
	}
	function formatCreateTime(templateCreateTime,type){
		var date = new Date(parseFloat(templateCreateTime));
		if(type=='day'){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
		}else{
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+' '+(date.getHours()==0?'00':String(date.getHours()))+':'+(date.getMinutes()==0?'00':String(date.getMinutes()))+':'+(date.getSeconds()==0?'00':String(date.getSeconds()));	
		}
	}

	function formatTemplateStatus(templateStatus){
		switch (templateStatus) {
		case 0:
			return '启用';
		case 1:
			return '停用';
		}
	}

	function fromatTemplateCycle(templateCycle){
		var reportTemplateCycle;
		switch (templateCycle) {
		case 1:
			reportTemplateCycle = '日报';
			break;
		case 2:
			reportTemplateCycle = '周报';
			break;
		case 3:
			reportTemplateCycle = '月报';
			break;
		}
		return reportTemplateCycle;
	}
	
	/**
	 * 提供给外部引用的接口
	 */
	oc.report.main.center.detail={
		open:function(data){
//			tempIdArr = data.tempIdArr;
			dataOption = data;//{reportTemplateType : data.type};
			showType = 1;
			
			$('#reportMainCenterInfo').html('');
			$('#reportMainCenterTop').html('');
			init();
			bindMethod();
//			collapse_btn();
		},
		openWithTemplateType:function(data){
//			tempIdArr = data.tempIdArr;
			dataOption = {reportTemplateType : data.type};
			showType = 0;
			
			$('#reportMainCenterInfo').html('');
			$('#reportMainCenterTop').html('');
			init();
			bindMethod();
//			collapse_btn();
		}
	};
})(jQuery);