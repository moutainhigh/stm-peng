(function($) {
	
	
	var loginUser = oc.index.getUser();
	var mainId = oc.util.generateId(), _thresholdOperation = null,thresholdPlaceHolder = null,
	// 模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
	mainDiv = $('#oc_module_resource_custom_metric_main').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	}),
	// 自定义指标列表文档对象
	datagridDiv = mainDiv.find('.oc_module_resource_custom_metric_main_datagrid'),
	// 自定义指标列表实例
	datagrid = null;
	var conditionForm= oc.ui.form({
		selector:mainDiv.find('#show_metric_form'),
		combobox:[{
			selector:mainDiv.find(".metricType"),
			fit:false,
			width:100,
			placeholder:'--请选择--',
//			panelHeight : 'auto',
			valueField:'id',    
			textField:'name',
			data:[
			      {id:'PerformanceMetric',name:'性能指标'},
			      {id:'InformationMetric',name:'信息指标'},
			      {id:'AvailabilityMetric',name:'可用性指标'}
	        ]
		},{
			selector:mainDiv.find(".discoverWay"),
			fit:false,
			width:80,
			placeholder:'--请选择--',
//			panelHeight : 'auto',
			valueField:'id',    
			textField:'name',
			data:[
			      {id:'JdbcPlugin',name:'JDBC'},
			      {id:'SshPlugin',name:'SSH'},
			      {id:'SnmpPlugin',name:'SNMP'},
			      {id:'TelnetPlugin',name:'Telnet'},
			      {id:'UrlPlugin',name:'URL'}
	        ]}
		]
	});
	
	  //获取告警内容参数
	  oc.util.ajax({
		  successMsg:null,
		  url: oc.resource.getUrl('portal/strategydetail/getProfileMetricThresholdOperation.htm'),
		  async:false,
		  success:function(data){
			  if(data.code == 200 && data.data){
				  _thresholdOperation = data.data.binaryComparisonOperators.concat(data.data.unaryComparisonOperators).concat(data.data.logicalOperators);
			  }else{
				  alert('获取阈值参数失败');
			  }
		  }
	  });
	
	var octoolbar = null;
	if(loginUser.systemUser){
		octoolbar={
			left:[conditionForm.selector],
			right : [ {
				text : '添加',
				iconCls : 'fa fa-plus',
				onClick : function() {
					_open('add',undefined);
				}
			},{
				text : '删除',
				iconCls : "fa fa-trash-o",
				onClick : function() {
					delCustomMetric();
				}
			}]
		};
	}else{
		octoolbar={
				left:[conditionForm.selector]
			};
	}
	var dlg = $("<div class = 'oc-dialog-float'/>");
	
	
	
	// 初始化自定义指标列表
	datagrid = oc.ui.datagrid({
				selector : datagridDiv,
				pageSize:$.cookie('pageSize_customMetric')==null ? 15 : $.cookie('pageSize_customMetric'),
				url : oc.resource.getUrl('portal/resource/customMetric/getCustomMetrics.htm'),
				queryForm : conditionForm,
				width : 'auto',
				height : 'auto',
				hideReset : true,
				hideSearch : true,
				checkOnSelect : false,
				idField:"id",
				octoolbar :octoolbar,
				columns : [ [
				         {field:'id',title:'-',checkbox:true,width:20},
				         {field:'monitor',title:'是否监控',sortable:true,width:30,formatter: function(value,row,index){
				        	 return "<span class='oc-top0 locate-left status oc-switch "+(value ? "open" : "close")+"' data-data='"+value+"'></span>";
				         }},
				         {field:'alert',title:'是否告警',sortable:true,width:30,formatter: function(value,row,index){
			        		 return "<span class='oc-top0 locate-left status oc-switch "+(value ? "open" : "close")+"' data-data='"+value+"'></span>";
				         }},
				         {field:'name',title:'指标名称',sortable:true,width:40,formatter: function(value,row,index){
				        	 	return "<span class='oc-pointer-operate' style='text-decoration: underline;' title='点我编辑详情'>"+value+"</span>";
			        	 }},
			        	 {field:'metricType',title:'指标类型',sortable:true,width:40,formatter: function(value,row,index){
//				        	 	return "<span class='oc-pointer-operate' style='text-decoration: underline;' title='点我编辑详情'>"+value+"</span>";
			        		if(value=="PerformanceMetric"){
				        		value="性能指标";
				        	}
				        	if(value=="InformationMetric"){
				        		value="信息指标";
				        	}
							if(value=="AvailabilityMetric"){
								value="可用性指标";
							}
				        	return value;
			        	 }},
			        	 {field:'discoverWay',title:'采集方式',sortable:true, width:30,formatter: function(value,row,index){
			        		if(value=="SnmpPlugin"){
			        			value="SNMP";
			        		}
			        		if(value=="SshPlugin"){
			        			value="SSH";
			        		}
							if(value=="TelnetPlugin"){
								value="Telnet";
							}
							if(value=="UrlPlugin"){
								value="URL";
							}
							if(value=="JdbcPlugin"){
								value="JDBC";
							}
			        		return value;
			        	 }},
			        	 {field:'frequent',title:'监控频度',sortable:true,width:30,formatter: function(value,row,index){
//			        		return "<span class='oc-pointer-operate' style='text-decoration: underline;' title='点我编辑详情'>"+value+"</span>";
			        		 if(value=="min1"){
					        		value="1分钟";
					        }
			        		if(value=="min5"){
				        		value="5分钟";
				        	}
				        	if(value=="min10"){
				        		value="10分钟";
				        	}
							if(value=="min30"){
								value="30分钟";
							}
							if(value=="hour1"){
				        		value="1小时";
				        	}
							if(value=="day1"){
								value="1天";
							}
				        	return value;
			        	 }},
			        	/* {field:'greenThreshold',title:'绿色阈值',sortable:true, width:30,formatter: function(value,row,index){
			        		 if(row.metricType=="PerformanceMetric"){
			        			 var greenValue = row.greenOprateChar+row.greenValue;
			        			 if(row.unit){
			        				 greenValue += row.unit
			        			 }
			        			 return greenValue;
			        		 }else{
			        			 return "N/A";
			        		 }
			        		 
			        	 }},*/
			        	 {field:'resource',title:'关联资源',sortable:true,width:40,formatter: function(value,row,index){
				        	 	return "<span class='ico ico-relevance ' style='text-decoration: underline;' title='点我绑定资源'></span>";
			        	 }}
			        	 ] ],
			             onClickCell:function(rowIndex, field, value,e){
			            	 var row = datagrid.selector.datagrid("getRows")[rowIndex];
			            	 if(field=='name'){
			            		 _open('edit',row);
			            	 }else if(field=='resource'){
			            		 _bindResource({
			            			 id:row.id,
			            			 pluginId:row.discoverWay
			            		 });
			            	 }else if(field=='monitor'){
			            		 if(value){
			            			 row.monitor = false;
			            			 row.alert = false;
			            		 }else{
			            			 row.monitor = true;
			            		 }
			            		 var queryData = $.extend(true,{},row);
			            		 delete queryData.thresholdsMap;
			            		oc.util.ajax({
			            			url:oc.resource.getUrl('portal/resource/customMetric/updateCustomMetricSetting.htm'),
			          	    		data:queryData,
			          	    		failureMsg:'操作失败！',
			          	    		success:function(data){
			          	    			if(data.code==200){
					 		    			datagrid.selector.datagrid("updateRow",{
					 		    				index : rowIndex,
					 		    				row : row
					     		    		});
					 		    			alert("操作成功！");
			          	    			}
			          	    		}
			          	    	});

			            	 }else if(field=='alert'){
			            		 if(row.monitor == false){
			            			 //如果未监控，无法操作是否告警
			            			 alert('指标未监控，不能开启告警！');
			            			 return;
			            		 }
			            		 if(value){
			            			 row.alert = false;
			            		 }else{
			            			 row.alert = true;
			            		 }
			            		 var queryData = $.extend(true,{},row);
			            		 delete queryData.thresholdsMap;
			            		oc.util.ajax({
			            			url:oc.resource.getUrl('portal/resource/customMetric/updateCustomMetricSetting.htm'),
			          	    		data:queryData,
			          	    		failureMsg:'操作失败！',
			          	    		success:function(data){
			          	    			if(data.code==200){
					 		    			datagrid.selector.datagrid("updateRow",{
					 		    				index : rowIndex,
					 		    				row : row
					     		    		});
					 		    			alert("操作成功！");
			          	    			}
			          	    		}
			          	    	});
		 		    			
			            	 }
			             },
			             onLoadSuccess : function(data) {
			            	 thresholdPlaceHolder = data.placeHolder;
							var execution = function(index) {
								datagridDiv.datagrid('unselectAll');
								datagridDiv.datagrid('selectRow', index);
								var currentRow = datagridDiv.datagrid("getSelected");
								_open('edit',currentRow);
							};
							mainDiv.find("span.icon-group-detail").bind('click',
								function() {
									execution($(this).attr("index"));
						}
					);
				}
			});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_customMetric');
				$.cookie('pageSize_customMetric',pageSize);
			}
		});
	}
	
	$('#show_metric_form').find(".metricName").keydown(function(e){
		if(e.keyCode == 13){ //绑定回车
			searchMetric();
		}
	});
	$('#show_metric_form').find(".queryBtn").linkbutton('RenderLB',{
		iconCls:'icon-search',
		onClick : function(){
			searchMetric();
		}
	});
	$('#show_metric_form').find(".resetBtn").linkbutton('RenderLB',{
		iconCls:'icon-reset',
		onClick : function(){
			resetMetric();
		}
	});
	
//	oc.ui.combobox({
//		selector:mainDiv.find(".metricType"),
//		fit:false,
//		width:100,
//		placeholder:null,
//		panelHeight : 'auto',
//		valueField:'id',    
//		textField:'name',
//		data:[
//		      {id:'',name:'--请选择--'},
//		      {id:'PerformanceMetric',name:'性能指标'},
//		      {id:'InformationMetric',name:'信息指标'},
//		      {id:'AvailabilityMetric',name:'可用性指标'}
//        ]
//	});
//	
//	oc.ui.combobox({
//		selector:mainDiv.find(".discoverWay"),
//		fit:false,
//		width:80,
//		placeholder:null,
//		panelHeight : 'auto',
//		valueField:'id',    
//		textField:'name',
//		data:[
//		      {id:'',name:'--请选择--'},
//		      {id:'JdbcPlugin',name:'JDBC'},
//		      {id:'SshPlugin',name:'SSH'},
//		      {id:'SnmpPlugin',name:'SNMP'},
//		      {id:'TelnetPlugin',name:'Telnet'}
//        ]
//	});


	function delCustomMetric(){
		var ids=getSelectIds(datagrid),len=ids.length;
		if(len){
			oc.ui.confirm("确认删除所选的指标吗？",function(){
				oc.util.ajax({
					url:oc.resource.getUrl('portal/resource/customMetric/deleteCustomMetric.htm'),
					data:{metricIds:ids.join()},
					successMsg:"",
					success:function(data){
						if(data.data){
							alert("指标删除成功");
							datagrid.reLoad();
						}else{
							alert("指标删除失败");
						}
					}
				});
			});
		}else{
			alert("请选择至少一条指标！",'danger');
		}
	}
	
	
	
	function _open(type,metric) {
		oc.resource.loadScript('resource/module/resource-management/customMetric/js/customMetricDetail.js', function() {
			oc.module.resource.custom.metric.open({
				type : type,
				metric : metric,
				placehodler : thresholdPlaceHolder,
				callback:function(){
					datagrid.reLoad();
				}
			});
		});
	}
	
	function _bindResource(metric){
		oc.resource.loadScript('resource/module/resource-management/customMetric/js/customMetricResource.js', function() {
			oc.module.custom.metric.resource.open(metric);
		});
	}
	
	function getSelectIds(dataGrid){
		var objs=dataGrid.selector.datagrid('getChecked'), ids=[];
		for(var i=0,len=objs.length;i<len;i++){
			var obj = objs[i];
			ids.push(obj.id);
		}
		return ids;
	}
	function searchMetric(){
		datagrid.reLoad();
	}
	function resetMetric(){
		conditionForm.reset();
	}
	
})(jQuery);