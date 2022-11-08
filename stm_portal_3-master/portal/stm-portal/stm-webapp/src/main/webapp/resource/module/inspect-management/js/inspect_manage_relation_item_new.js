(function(){
	
	function RelatedInspectItemSubItem(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	RelatedInspectItemSubItem.prototype = {
		
		cfg : undefined,
		open : function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), 
				that = this,
				getData = that.cfg.getData;
			
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_manage_relation_item_new.html'),
				title : '编辑关联巡检子项',
				width: 940 ,   
    			height: 580,
    			top: 30,
				resizable : true,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
					dlg.dialog("destroy");//add by sunhailiang on 20170615
				},
				buttons:[
		             {
		               text:'确定',
		           	iconCls:"fa fa-check-circle",
		 	    		handler:function(){
		 	    			var rightData = that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('getChecked');
							var leftData = that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('getChecked');
					    	for(var loop = 0; loop < rightData.length; loop++){
					    		var value = rightData[loop].style;
					    		if(value == 'PerformanceMetric'){
					    			rightData[loop].style = '性能指标';
				        		}else if(value == 'InformationMetric'){
				        			rightData[loop].style = '信息指标';
				        		}else if(value == 'AvailabilityMetric'){
				        			rightData[loop].style = '可用性指标';
				        		}
					    	}
					    	var data = {};
					    	data.realtionchilditem = rightData;
					    	data.edittype = that.cfg.edittype;
					    	data.itemType = that.cfg.itemType;
					    	data.relationItem = leftData;
					    	data.inspectType = $("#inpsetcResourceType").combotree('getValues')[0];
					    	if('Business' == $("#inpsetcResourceType").combotree('getValues')[0] && $('#checkBusinessnodeInformation').is(':checked')){
					    		data.checkBusinessnodeInformation = true;	//增加业务
					    	}else{
					    		data.checkBusinessnodeInformation = false;
					    	}
						 	dlg.dialog("close");
							if(getData){
								setTimeout(function(){
									 getData(data);
								},10) 
							}
								
		 	    		}
		             }, {
		            	 text:'关闭',
		            	iconCls:"fa fa-times-circle",
			    		handler:function(){
			    			dlg.dialog("close");
			    		}
		            }
		       ]
			});
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_mainDiv : undefined,
		_id : '#add_related_inspect_items_new',
		_comboTree : undefined,
		_selectChildResourceCombobox : undefined,
		_relation_child_item_grid : undefined,
		_curSelectCategoryOrMainResource:undefined,
		_curLeftGridResourceInstanceList : undefined,
		_init : function(dlg) {
			searchPromptContent = '搜索资源名称|IP|类型';
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			//分页显示的每页行数
			pageSize = 12;
			//分页开始数
			startNum = 0;
			//用于判断滚动分页加载是否正在进行
			isLoadding = false;
			
			that._mainDiv.find("#inpsetcSearchChildResourceType").val(searchPromptContent);
			
			that._mainDiv.find(".china-oc-inspect-catalog-name").html(that.cfg.dirName);
			that._mainDiv.find(".china-oc-inspect-catalog-describel").html(that.cfg.dirDesc);
			that._mainDiv.find('.china-oc-inspect-report-name').html(that.cfg.reportName);
			  
			var queryFrom = oc.ui.form({
				selector: that._mainDiv.find('.add-related-inspect-items-form')
			});
				//渲染资源选择和指标选择的
			 var resouceid = that.cfg.resourceId;
			  if(resouceid!=''&&resouceid!=null&&resouceid!=undefined){
				that._comboTree = oc.ui.combotree({
					  selector:that._mainDiv.find('#inpsetcResourceType'),
					  width:'200px',
					 placeholder:false,
					  url:oc.resource.getUrl('inspect/plan/getResourceCategoryListByInspection.htm?id='+that.cfg.resourceId),
					  filter:function(data){
						  var type = that.cfg.resourceType;
						  if(resouceid == 'Business'){
                              data.data = [{id: "Business",name: "业务系统",pid: "Resource",state: "closed",type: 1 }];
                              that._mainDiv.find("#inpsetcSearchChildResourceType").val("搜索资源名称|责任人");
                              that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid({
                                  columns:[[
                                      {field:'id',checkbox:true},
                                      {field:'showName',title:'资源名称',width:165},
                                      {field:'resourceName',title:'责任人',width:165}
                                  ]]
                              });
                              $('#checkBusinessnodeInformation').prev().css('display',"none");
                              $('[name=checkBusinessnodeInformation]').css('display',"");
                          }
						  if(type!=2){
							  return data.data;
						  }else{
							  var arrayData =data.data;
							  var retData = [];
							  for(var loop=0; loop<arrayData.length; loop++){
								   var obj = arrayData[loop];
								  if(obj.id==that.cfg.resourceId){
									  retData.push(obj);
									  return retData;
								  }
							  }
						  }
					  },
					  onLoadSuccess:function(){
						  var resouceid = that.cfg.resourceId;
						  if(resouceid!=''&&resouceid!=null&&resouceid!=undefined){
							  that._mainDiv.find('#inpsetcResourceType').combotree('setValue', resouceid);
						  }
					  },
					  onChange : function(newValue, oldValue, newJson, oldJson){
						  startNum = 0;
						  $('#inspect_manage_resourcelist_grid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
						  
						  //查询子资源
						  if(newValue.trim() == ''){
							 // pickGrid.loadData("left",{"code":200,"data":{"total":0,"rows":[]}});
							  that._selectChildResourceCombobox.load([]);
							  return;
						  }
						  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":''}});
						  var queryParameter = {};
						  queryParameter.id = newJson.id;
						  if(queryParameter.id == 'Business'){
                              $('#checkBusinessnodeInformation').prev().css('display',"none");
                              $('[name=checkBusinessnodeInformation]').css('display',"");
                          }else{
                              $('#checkBusinessnodeInformation').prev().css('display',"");
                              $('[name=checkBusinessnodeInformation]').css('display',"none");
                          }
						  queryParameter.type = newJson.type;
						  oc.util.ajax({
							  url: oc.resource.getUrl('inspect/plan/getChildResourceListByMainResourceOrCategory.htm'),
							  data:queryParameter,
							  timeout:'600000',
							  success:function(data){  
								  if(data.data){
									  that_curSelectCategoryOrMainResource = {
											  id:newJson.id,
											  type:newJson.type
									  };
									  that._selectChildResourceCombobox.load(data.data);
									  
									  //加载表格数据
									  showResourceInstance({queryId:newJson.id, type:newJson.type, domainId:that.cfg.domainId,startNum:startNum, pageSize:pageSize},false);
									  //获取资源实例
									  //that._relation_child_item_grid.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
								  }else{
									  alert('查询子资源失败!');
								  }
							  }
						  });
					  }
				});	
			  }else{
				  that._comboTree = oc.ui.combotree({
					  selector:that._mainDiv.find('#inpsetcResourceType'),
					  width:'200px',
					 placeholder:'请选择资源类型',
					  url:oc.resource.getUrl('inspect/plan/getResourceCategoryListByInspection.htm?id='+that.cfg.resourceId+'&domainId='+that.cfg.domainId),
					  filter:function(data){
						  data.data.push({id: "Business",name: "业务系统",pid: "Resource",state: "closed",type: 1 });
						  return data.data;
					  },
					  onChange : function(newValue, oldValue, newJson, oldJson){
						  startNum = 0;
						  $('#inspect_manage_resourcelist_grid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
						  
						  //查询子资源
						  if(newValue.trim() == ''){
							 // pickGrid.loadData("left",{"code":200,"data":{"total":0,"rows":[]}});
							  that._selectChildResourceCombobox.load([]);
							  return;
						  }
						  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":''}});
						  var queryParameter = {};
						  queryParameter.id = newJson.id;
						  queryParameter.type = newJson.type;
						  oc.util.ajax({
							  url: oc.resource.getUrl('inspect/plan/getChildResourceListByMainResourceOrCategory.htm'),
							  data:queryParameter,
							  timeout:'600000',
							  success:function(data){  
								  if(data.data){
									  that_curSelectCategoryOrMainResource = {
											  id:newJson.id,
											  type:newJson.type
									  };
									  if(newJson.id != "Business"){
										  that._selectChildResourceCombobox.load(data.data);
									  }
									  
									  //加载表格数据
									  showResourceInstance({queryId:newJson.id, type:newJson.type, domainId:that.cfg.domainId,startNum:startNum, pageSize:pageSize},false);
								  }else{
									  alert('查询子资源失败!');
								  }
							  }
						  });
					  }
				});	
			  }
			that._selectChildResourceCombobox = oc.ui.combobox({
				  selector:that._mainDiv.find('#inpsetcChildResourceType'),
				  width:'120px',
				  placeholder:'请选择子资源',
				  data:null,
				  onChange : function(newValue, oldValue,newObj, oldObj){
					  startNum = 0;
					  $('#inspect_manage_resourcelist_grid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
					  
					  var parameter = {};
					  if(newValue == ''){
						  parameter.queryId = curSelectCategoryOrMainResource.id;
						  parameter.type = curSelectCategoryOrMainResource.type;
					  }else{
						  parameter.queryId = newValue;
						  parameter.type = 2;
					  }
					  parameter.domainId = that.cfg.domainId;
					  
					  //加载表格数据
					  showResourceInstance({queryId:parameter.queryId, type:parameter.type, domainId:parameter.domainId, startNum:startNum, pageSize:pageSize},false);
				  }
			});
			
			queryFrom.find(".inspect-plan-related-item-search").linkbutton('RenderLB', {
				text : '搜索',
				iconCls: 'icon-search',
				onClick: function()	{
					var searchContent = that._mainDiv.find('#inpsetcSearchChildResourceType').val().trim();	
					if(searchContent == '' || searchContent == searchPromptContent){
						  that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('loadData',{"code":200,"data":{"total":0,"rows":that._curLeftGridResourceInstanceList}});
						return;
					}
					
					var leftData = that._curLeftGridResourceInstanceList;
					if(leftData == null || leftData == undefined || leftData.length <= 0){
						return;
					}
					var resourceIdArray = '';
					for(var i = 0 ; i < leftData.length ; i ++){
						resourceIdArray += leftData[i].id + ",";
					}
					resourceIdArray = resourceIdArray.substring(0,resourceIdArray.length - 1);
					//增加业务判断表示
					var businessFlag = true;
					if($('#checkBusinessnodeInformation').css("display") == 'none'){
						businessFlag = false;
					}
					
					//过滤资源实例
					oc.util.ajax({
						  url: oc.resource.getUrl('inspect/plan/filterInstanceInfoByContent.htm'),
						  data:{instanceIds:resourceIdArray,content:searchContent,businessFlag:businessFlag,domainId:that.cfg.domainId},
						  timeout:'600000',
						  success:function(data){
							  
							  if(data.data){
								  that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('loadData',{"code":200,"data":{"total":0,"rows":data.data}});
								  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":''}});
							  }else{
								  alert('过滤资源实例失败!');
							  }
						  }
					});
				}
			});
					
			pickGrid = oc.ui.datagrid({
				selector:that._mainDiv.find("#inspect_manage_relation_item_datagrid"),
				pagination:false,
				checkOnSelect:false,
				fitColumns:false,
				selectOnCheck:false,
				columns:[[
		                     {field:'id',checkbox:true},
		                     {field:'showName',title:'资源名称',width:165},
		                     {field:'discoverIP',title:'IP地址',width:100},
		                     {field:'resourceId',hidden:true},
		                     {field:'resourceName',title:'资源类型',width:165},
		                     {field:'person',title:'责任人',width:165,formatter:function(value,rowData,rowIndex){
		                    	 return rowData.resourceName;
		                     }}
		        ]],
		        onCheckAll:function(rows){ 
					  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":[]}});
		        	var checkgrid = that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('getChecked');
		        	showmetricDataGridData(checkgrid);
		        },
		        onUncheckAll:function(rows){
					  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":[]}});
		        },
		        onCheck : function(index,row){
					  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":[]}});
		        	var checkgrid = that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('getChecked');
		        	showmetricDataGridData(checkgrid);
		        },
		        onUncheck : function(index,row){
					  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":[]}});
		        	var checkgrid = that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('getChecked');
		        	showmetricDataGridData(checkgrid);
		        },
		        onLoadSuccess:function(){
		        	//资源列表滚动条下拉事件
					var timeoutLeftGrid;
					var scrollDivLeftGrid = $('#inspect_manage_resourcelist_grid').find('.datagrid-view2>.datagrid-body');
					scrollDivLeftGrid.unbind('scroll');
					scrollDivLeftGrid.on('scroll',function(){
						/*清除定时器，防止滚动滚动条时多次触发请求(IE)*/
						clearTimeout(timeoutLeftGrid);
							/*当请求完成并且在页面上显示之后才能继续请求*/
							if(!isLoadding){
								 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
								 if($(this).get(0).scrollHeight - $(this).height() <= $(this).scrollTop() && pickGrid.selector.datagrid('getRows').length > 0){
									 timeoutLeftGrid = setTimeout(function(){
										 //发送请求刷新主资源列表
										 curQueryResourceParameter.startNum = startNum;
										 showResourceInstance(curQueryResourceParameter,true);
									 },200);
								 }
							}
					});
		        }
			});
			
			
			//发送分页获取资源列表
			function showResourceInstance(parameter,isPaging){
				isLoadding = true;
				
				oc.util.ajax({
					  url: oc.resource.getUrl('inspect/plan/getResourceInstanceList.htm'),
					  data:parameter,
					  timeout:'600000',
					  success:function(data){  
						  if(data.data){
							  curQueryResourceParameter = parameter;
							  
							  if(isPaging && (!data.data || data.data.length <= 0)){
							    	alert('所有数据已加载完毕');
							    	isLoadding = false;
							    	return;
							  }else if(isPaging){
								  startNum = startNum + data.data.length;
							  }else{
								  startNum = data.data.length;
							  }
							  
							  
							  //记录当前选择
							  curSelectCategoryOrMainResource = {
									  id:parameter.queryId,
									  type:parameter.type
							  };
							  that._curLeftGridResourceInstanceList = data.data;
							  
							  if(parameter.queryId == "Business"){
								  	that._mainDiv.find("#inpsetcSearchChildResourceType").val("搜索资源名称|责任人");
								  	that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('hideColumn','discoverIP');
								  	that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('hideColumn','resourceName');
								  	that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('showColumn','person');
								  	$('#checkBusinessnodeInformation').prev().css('display',"none");
								  	$('[name=checkBusinessnodeInformation]').css('display',"");
							  }else{
								  	that._mainDiv.find("#inpsetcSearchChildResourceType").val("搜索资源名称|IP|类型");
								  	that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('hideColumn','person');
								  	that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('showColumn','discoverIP');
								  	that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('showColumn','resourceName');
								  	$('#checkBusinessnodeInformation').prev().css('display',"");
								  	$('[name=checkBusinessnodeInformation]').css('display',"none");
							  }
							  
							  if(isPaging){
								  for(var i = 0; i < data.data.length; i++){
									  that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('appendRow',data.data[i]);
								  }
							  }else{
								  that._mainDiv.find("#inspect_manage_relation_item_datagrid").datagrid('loadData',{"code":200,"data":{"total":0,"rows":data.data}});
							  }
							  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":''}});
							  
						  }else{
							  alert('查询资源实例失败!');
						  }
						  isLoadding = false;
					  }
				  });
			}
			
			function showmetricDataGridData(rightData){
				var resType = "";
				if(rightData.length > 0 && rightData[0].resourceId == "Business"){
					resType = "Business";
				}
				var resourceIdArray = '';
				for(var i = 0 ; i < rightData.length ; i ++){
					//拼接resourceId和instanceId
					resourceIdArray += rightData[i].resourceId +";"+rightData[i].id+ ",";
				}
				resourceIdArray = resourceIdArray.substring(0,resourceIdArray.length - 1);
				 oc.util.ajax({
					  url: oc.resource.getUrl('inspect/plan/getMetricListByResourceId.htm'),
					  data:{
						  resourceIdList : resourceIdArray,
						  resType : resType
					  },
					  timeout:'600000',
					  success:function(data){
						  if(data.data){
							  that._mainDiv.find("#inspect_manage_relation_item_inspect").datagrid('loadData',{"code":200,"data":{"total":0,"rows":data.data}});
						  }else{
							  alert('查询指标失败!');
						  }
					  }
				  });
			}
			
			var columns=[[
					         {field:'id',checkbox:true},
					         {field:'name',title:'指标信息',width:'166px',
					        		formatter:function(value,row,rowIndex){
					        			var html="";
					        			var name=value;
					        			if(name.length>12){
					        				name=name.substring(0,12)+"...";
					        			}
					        			html='<div style="height:auto;" title='+value+' class="datagrid-cell datagrid-cell-c18-name">'+name+'</div>';
					        			return html;
					         }},
					         {field:'unit',title:'单位',width:'101px'},
					         {field:'style',title:'指标类型',width:'166px',
					        	formatter:function(value,row,rowIndex){
					        		if(value == 'PerformanceMetric'){
					        			value = '性能指标';
					        		}else if(value == 'InformationMetric'){
					        			value = '信息指标';
					        		}else if(value == 'AvailabilityMetric'){
					        			value = '可用性指标';
					        		}
					        		return value;
					         	}
					         }
					     ]];
			pickGrid1 = oc.ui.datagrid({
				selector:that._mainDiv.find("#inspect_manage_relation_item_inspect"),
				pagination:false,
				checkOnSelect:false,
				selectOnCheck:false,
				columns:columns
			});
			that._relation_child_item_grid = oc.ui.datagrid({
				selector:that._mainDiv.find("#inspect_manage_relation_item_inspect"),
				leftColumns:columns,
				rightColumns:columns,
				isInteractive:true,
				//leftUrl:oc.resource.getUrl('inspect/plan/getResourceInspect.htm'),
				leftCallback:{
					onLoadSuccess:function(data){
						
					}
				}
			});
			
			that._mainDiv.find('#inpsetcSearchChildResourceType').on('focus',function(e){
				if($(e.target).val() == searchPromptContent || $(e.target).val() == '搜索资源名称|责任人'){
					$(e.target).val('');
				}
				
			});
		}
			
	};
	
		
	oc.ns('oc.module.inspect.relations.subitems');
	oc.module.inspect.relations.subitems = {
		open : function(cfg) {
			new RelatedInspectItemSubItem(cfg).open();
		}
	};
	
})();