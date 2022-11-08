$(function(){
	
	function metricSelect(opt){
		this.opt = {
			domainIds : undefined,//域Id
			resourceType: undefined,//主资源类型
			subMetrics:undefined,//选中的子资源
			choiseMetric:undefined//选中的指标
		};
		$.extend(this.opt,opt,true);
		
		if(this.opt.domainIds == undefined)
			this.opt.domainIds = 1;

		this.domainIds = this.opt.domainIds;
		this.resourceType = this.opt.resourceType;
		this.resourceIds = this.opt.resourceIds;
		this.subMetrics = this.opt.subMetrics;
		this.choiseMetric = this.opt.choiseMetric;

		//_this.performanceReportType = 1;
		this.isShowApartLeftPickGridInfo = false;
		//分页显示的每页行数
		this.pageSize = 12;
		//分页开始数
		this.startNum = 0;
		//用于判断滚动分页加载是否正在进行
		this.isLoadding = false;
		this.curLeftGridResourceInstanceList = new Array();
		this.selectChildInstanceTitle = '请选择子资源';
		
		
		this.init();
	}

	var msvp = metricSelect.prototype;

	msvp.init = function(){
		var _this = this;
		
		_this.isFirst = true;
		_this.isSubMetrics =true;
		if(this.subMetrics == null || this.subMetrics == ''){
			_this.isSubMetrics = false;
		}

		//渲染资源选择和指标选择的
		_this.comboTree = oc.ui.combotree({
			selector:$('#mainResourceSelect'),
			width:'200px',
			placeholder:'请选择资源类型',
			url:oc.resource.getUrl('portal/report/reportTemplate/getResourceCategoryList.htm'),
			filter:function(data){
				var td = data.data;
				td.push({id: "Business",name: "业务",pid: "Resource",state: "closed",type: 1 });
				td.push({id: "Link",name: "链路（拓扑）",pid: "Resource",state: "closed",type: 1 }); 
				return td;
			},
			onChange : function(newValue, oldValue, newJson, oldJson){
				//查询子资源
				if(newValue.trim()==''){
					  if(_this.pickGrid != null){ 
				  	_this.setPickgridLeftData([]);
				  	_this.setPickgridRightData([]);
					  }
				  _this.selectChildResourceCombobox.load([]);
				  _this.setDatagridData(_this.dataGrid,[]);
				  return;
				}

			 	_this.resetGridData();
			 	var queryParameter = {};
			 	queryParameter.id = newJson.id;
			 	queryParameter.type = newJson.type;

			 	_this.mainResourceId = newValue.trim();
			  	if('Business'==newValue.trim() || 'Link'==newValue.trim()){
			  	 	_this.selectChildResourceCombobox.load([]);
			  		//获取资源实例
					_this.startNum = 0;
					$('#parentResourceInstanceGrid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
					var domainId = _this.domainIds;

					_this.showResourceInstance({queryId:newJson.id,type:newJson.type,
						  domainId:domainId,
						  startNum:_this.startNum,pageSize:_this.pageSize},true,false);
					return;
				}
			  	oc.util.ajax({
			  	  successMsg:null,
				  url: oc.resource.getUrl('portal/report/reportTemplate/getChildResourceListByMainResourceOrCategory.htm'),
				  data:queryParameter,
				  success:function(data){
					  
					  if(data.data){
					  	_this.curSelectCategory = {};
					  	_this.curSelectCategory.id = newJson.id;
					  	_this.curSelectCategory.type = newJson.type;

						_this.selectChildResourceCombobox.load(data.data);
						  
						//获取资源实例
						_this.startNum = 0;
						$('#parentResourceInstanceGrid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
						var domainId = _this.domainIds;
						
						if(_this.isFirst && _this.isSubMetrics){
							_this.isFirst = false;
							return;
						}
					
						_this.showResourceInstance({queryId:newJson.id,type:newJson.type,
							domainId:domainId,
							startNum:_this.startNum,pageSize:_this.pageSize},true,false);
					  }else{
						  alert('查询子资源失败!');
					  }
				  }
			  });
			},
			onLoadSuccess:function(node, data){
				if(_this.resourceType){
					setTimeout(function(){
						_this.comboTree.cfg.selector.combotree('setValue',_this.resourceType);
					},100);
				}
			}
		});

		_this.selectChildResourceCombobox = oc.ui.combobox({
			selector:$('#childResourceSelect'),
			width:'160px',
			selected:false,
			placeholder:_this.selectChildInstanceTitle,
			data:null,
			onChange : function(newValue, oldValue){
			  //选择子资源查询资源实例
			  _this.resetGridData();
			  var parameter = {};
			  if(newValue == ''){
				  parameter.queryId = _this.curSelectCategory.id;
				  parameter.type = _this.curSelectCategory.type;
			  }else{
				  parameter.queryId = newValue;
				  parameter.type = 2;
			  }
			  parameter.domainId = _this.domainIds;

			  _this.startNum = 0;
			  parameter.startNum = _this.startNum;
			  parameter.pageSize = _this.pageSize;
			  
			  $('#parentResourceInstanceGrid').find('.pickList-left-width').find('.datagrid-view2>.datagrid-body').scrollTop(0);
			  _this.showResourceInstance(parameter,false,false);
			},
			onLoadSuccess:function(node, data){
				setTimeout(function(){
					if(_this.subMetrics){
						var data = _this.selectChildResourceCombobox.cfg.selector.combobox('getData');
						for(var i=0; i<data.length;i++){
							if(data[i].name == _this.subMetrics){
								_this.selectChildResourceCombobox.cfg.selector.combobox('setValue',data[i].id);
								return;
							}
							if(data[i].id == _this.subMetrics){
								_this.selectChildResourceCombobox.cfg.selector.combobox('setValue',_this.subMetrics);
								return;
							}
						}
					}
				},100);
				
			}
		});

		_this.createPickGridForInstance();

		//指标表格
		var dgselector = '#selectMetricGrid';
		_this.dataGrid = oc.ui.datagrid({
			selector:dgselector,
			pagination:false,
			fitColumns:true,
			noDataMsg:'未选择数据！',
			checkOnSelect:false,
			selectOnCheck:true,
			singleSelect:true,
			width:'100%',
			columns:[[
			          {field:'id',checkbox:true},
			          {field:'radioId',title:'',hidden:true,
				        	 formatter:function(value,row,rowIndex){
		        			    return '<input type="radio" class="metricGridRadioClass" name="metricGridRadio" value="' + rowIndex + '" />';
				         	 }
		         	  },
			          {field:'name',title:'指标名称',ellipsis:true},
			          {field:'metricSort',title:'排序',hidden:true,
				        	 formatter:function(value,row,rowIndex){
			        		 	 if(value == 1){
			        		 	 	return '<span class="pop-arrow-down"></span>';
			        		 	 }else if(value == 0){
			        		 	 	return '<span class="pop-arrow-up"></span>'
			        		 	 }else{
			        			    return '<span>-</span>';
			        		 	 }
				         	 }
				      },
				      {field:'metricExpectValue',title:'期望值',hidden:true},
				      {field:'unit',hidden:true}
			]],
		    onLoadSuccess:function(data){
		    	var dg = _this.dataGrid.selector;
		    	var rows = dg.datagrid('getRows');
		    	for(var i=0;i<rows.length;i++){
	    			if(rows[i].id == _this.choiseMetric){
			    	   dg.datagrid('checkRow',i);
			    	   break;
			   		}  
				}
				$(dgselector).parent().find("div.datagrid-header-check").children("input[type=\"checkbox\"]").eq(0).attr("style", "display:none;");
		    },
		    onCheck:function(rowIndex, rowData){
		     	var newData = $.extend(true,{},rowData);
		     	if(newData.metricSort == -1){
			     	newData.metricSort = 1;
					_this.dataGrid.selector.datagrid('updateRow',{
						index:rowIndex,
						row:newData
					});

					//easyui bug修改后需再次选中
					_this.dataGrid.selector.datagrid('checkRow',rowIndex);
		     	}
		     
		    },
		    onClickCell:function(index,field,value){
		     	var dt = _this.dataGrid.selector.datagrid('getChecked');
		     	
		     	_this.dataGrid.selector.datagrid('clearChecked');
		     	_this.dataGrid.selector.datagrid('checkRow',index);
		    }
		});
		//隐藏头部复选框
		$(dgselector).parent().find("div.datagrid-header-check").children("input[type=\"checkbox\"]").eq(0).attr("style", "display:none;");
		if($(dgselector).parent().find('.datagrid-view2 .table-datanull').length >= 2){
			$(dgselector).parent().find('.datagrid-view2 .table-datanull')[0].remove();
		}


		$('#searchInstanceListButton').unbind().on('click',function(e){
			var searchContent = $('#searchInstanceListInput').val().trim();
			if(!_this.curQueryResourceParameter){
				_this.curQueryResourceParameter = {};
			}
			if(!_this.comboTree.jq.combobox('getValue') || !_this.comboTree.jq.combobox('getValue').trim()){
				return;
			}

			_this.startNum = 0;
		    _this.curQueryResourceParameter.startNum = 0;
		    _this.curQueryResourceParameter.content = searchContent;
		    $('#parentResourceInstanceGrid').find('.datagrid-view2>.datagrid-body').scrollTop(0);
		    _this.showResourceInstance(_this.curQueryResourceParameter,true,false);
		});

		}

		//创建资源选择的pickgrid
	msvp.createPickGridForInstance = function (){
		var _this = this;

		$('#parentResourceInstanceGrid').html('');
		$('#parentResourceInstanceGrid').append('<div id="selectResourceInstancePickGrid"></div>');
		
		_this.pickGrid = oc.ui.datagrid({
			selector:'#selectResourceInstancePickGrid',
			pagination:false,
			width:'100%',
			data:[],
			checkOnSelect:false,
			selectOnCheck:false,
			//fitColumns:true,
			columns:[[
			            {field:'id',checkbox:true},
			            {field:'discoverIP',title:'IP地址',width:'33%',ellipsis:true},
			            {field:'showName',title:'名称',width:'33%',ellipsis:true},
			            {field:'resourceId',hidden:true},
			            {field:'resourceName',title:'资源类型',width:'33%',ellipsis:true}
	        ]],
	        onCheck:function(index,row){
	        	_this.curLeftGridResourceInstanceList.push(row.id);
	        	if(!_this.pickGridLaoding)
	        		_this.showMetricDataGridData(false);
	        },
	        onCheckAll:function(rows){
	        	_this.curLeftGridResourceInstanceCheckedAll = true;//标识选中所有的资源
            	out : for(var j = 0 ; j < rows.length ; j ++){
            		for(var i = 0 ; i < _this.curLeftGridResourceInstanceList.length ; i ++){
            			if(rows[j].id == _this.curLeftGridResourceInstanceList[i]){
            				continue out;
            			}
            		}
            		_this.curLeftGridResourceInstanceList.push(rows[j].id);
            	}
	        	_this.showMetricDataGridData(false);
	        },
	        onUncheck:function(index,row){
	        	if(_this.isAutoChecked){
	        		_this.isAutoChecked = false;
	        		return;
	        	}
            	for(var i = 0 ; i < _this.curLeftGridResourceInstanceList.length ; i ++){
            		if(_this.curLeftGridResourceInstanceList[i] == row.id){
            			_this.curLeftGridResourceInstanceList.splice(i,1);
            			break;
            		}
            	}
	        	_this.showMetricDataGridData(false);
	        	_this.curLeftGridResourceInstanceCheckedAll = false;
	        },
	        onUncheckAll:function(rows){
	        	_this.curLeftGridResourceInstanceCheckedAll = false;
	        	if(rows && rows.length > 0){
	        		out : for(var j = 0 ; j < rows.length ; j ++){
	        			for(var i = 0 ; i < _this.curLeftGridResourceInstanceList.length ; i ++){
	        				if(_this.curLeftGridResourceInstanceList[i] == rows[j].id){
	        					_this.curLeftGridResourceInstanceList.splice(i,1);
	        					continue out;
	        				}
	        			}
	        		}
	        		_this.showMetricDataGridData(false);
	        	}
	        },
	        onLoadSuccess:function(data){
        		_this.pickGridLaoding = true;//用于标识当前是数据加载中，避免checkRow的事件导致的问题
        		_this.curLeftGridResourceInstanceCheckedAll = (_this.opt.checkAll==true);

				var leftData = data.rows;
				if(!_this.resourceIds){
					_this.pickGridLaoding = false;
					return;
				}
				if(_this.curLeftGridResourceInstanceCheckedAll){
					for(var j = 0 ; j < leftData.length ; j ++){
						_this.isAutoChecked = true;
						_this.pickGrid.selector.datagrid('checkRow',_this.pickGrid.selector.datagrid('getRowIndex',leftData[j]));
					}
				}else{
					var ids = _this.resourceIds.split(',');
					next : for(var i = 0 ; i < ids.length ; i ++){
						for(var j = 0 ; j < leftData.length ; j ++){
							if(leftData[j].id == ids[i]){
								_this.isAutoChecked = true;
								_this.pickGrid.selector.datagrid('checkRow',_this.pickGrid.selector.datagrid('getRowIndex',leftData[j]));
								continue next;
							}
						}
					}
				}
				_this.showMetricDataGridData(true);

				_this.pickGridLaoding = false;
			}
				
		});
		  
	    //注册滚动条滚动到底部翻页事件
		var timeoutLeftGrid;
		var scrollDivLeftGrid = $('#parentResourceInstanceGrid').find('.datagrid-view2>.datagrid-body');
		scrollDivLeftGrid.unbind('scroll');
		scrollDivLeftGrid.on('scroll',function(){
			/*清除定时器，防止滚动滚动条时多次触发请求(IE)*/
			clearTimeout(timeoutLeftGrid);
				/*当请求完成并且在页面上显示之后才能继续请求*/
				if(!_this.isLoadding){
					 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
					 if($(this).get(0).scrollHeight - $(this).height() <= $(this).scrollTop() && _this.pickGrid.selector.datagrid('getRows').length > 0){
						 timeoutLeftGrid = setTimeout(function(){
							 	//发送请求刷新主资源列表
							 	_this.curQueryResourceParameter.startNum = _this.startNum;
								_this.showResourceInstance(_this.curQueryResourceParameter,false,true);
						 },200);
					 }
				}
		});			
	}

	msvp.resetGridData = function (){
		var _this = this;
		_this.curLeftGridResourceInstanceList = new Array();
		_this.curQueryResourceParameter = {};
		_this.setDatagridData(_this.dataGrid,[]);
		_this.setPickgridRightData([]);
	    $('#searchInstanceListInput').val('');
	}

	msvp.setDatagridData = function(grid,nowData,newMsg){
		var _this = this;

		var data = $.extend(true,[],nowData);
		if(!data || data.length <= 0){
			if(newMsg){
				_this.dataGrid.updateNoDataMsg(newMsg);
			}else{
				_this.dataGrid.updateNoDataMsg('未选择数据！');
			}
			_this.deleteAllGridData(grid);
		}else{
			grid.selector.datagrid('loadData',{"code":200,"data":{"total":0,"rows":data}});
		}
	}
	
	msvp.setPickgridLeftData = function(nowData){
		var _this = this;
		var data = $.extend(true,[],nowData);
		if(!data || data.length <= 0){
			_this.deleteAllGridData(_this.pickGrid);
		}else{
			_this.pickGrid.selector.datagrid('loadData',{"code":200,"data":{"total":0,"rows":data}});
		}
	}

	//发送分页获取资源列表
	msvp.showResourceInstance = function(parameter,isFirstCheckbox,isPaging){
		var _this = this;
		var url = oc.resource.getUrl('home/workbench/resource/getResourceInstanceList.htm');
		
		//链路
		if(_this.mainResourceId == 'Link')
			url = oc.resource.getUrl('system/home/getNewHomeLinkData.htm');
		if(_this.mainResourceId == 'Business')
			url = oc.resource.getUrl('system/home/getHomeBusinessData.htm');

		_this.isLoadding = true;
		  oc.util.ajax({
			  url: url,
			  timeout:null,
			  data:parameter,
			  success:function(data){
				  
				  if(data.data){
					  _this.curQueryResourceParameter = parameter;
					  
					  if(isPaging && (!data.data || data.data.length <= 0)){
					    	alert('所有数据已加载完毕');
					    	_this.isLoadding = false;
					    	return;
					  }else if(isPaging){
						  _this.startNum = parameter.startNum + data.data.length;
					  }else{
						  _this.startNum = data.data.length;
					  }

					  if(isPaging){
						  for(var i = 0 ; i < data.data.length ; i ++){
							  _this.pickGrid.selector.datagrid('appendRow',data.data[i]);
						  }
						  if(_this.curLeftGridResourceInstanceCheckedAll){
							  if(_this.resourceIds == null){
								  _this.resourceIds = '-1';
							  }else{
								  if(!(_this.resourceIds.indexOf('-1') > -1)){
									  _this.resourceIds += '-1';
								  }
							  }
						  }

						  if(_this.resourceIds){
						  		_this.pickGridLaoding = true;//避免多次加载
						  	 	for(var i = 0 ; i < data.data.length ; i ++){
							  		var isChecked = false;
								  	if(_this.resourceIds.indexOf(data.data[i].id)>-1){
								  		 isChecked = true;
								  	}
								  	if(isChecked || _this.curLeftGridResourceInstanceCheckedAll){
										_this.isAutoChecked = true;
										_this.pickGrid.selector.datagrid('checkRow',_this.pickGrid.selector.datagrid('getRowIndex',data.data[i]));
									}else{
										_this.isAutoChecked = true;
										_this.pickGrid.selector.datagrid('uncheckRow',_this.pickGrid.selector.datagrid('getRowIndex',data.data[i]));
									}
								}
								_this.pickGridLaoding = false;
							}
					  }else{
						_this.setPickgridLeftData(data.data);
					  }
					  
				  }else{
					  alert('查询资源实例失败!');
				  }
				  _this.isLoadding = false;
				  
			  }
		  });
		  
	}
	
	//清空表格数据
	msvp.deleteAllGridData = function(grid){
		var item = grid.selector.datagrid('getRows');  
		if (item) {  
			for (var i = item.length - 1; i >= 0; i--) {  
				var index = grid.selector.datagrid('getRowIndex', item[i]);
				grid.selector.datagrid('deleteRow', index);  
			}  
			grid.selector.datagrid('uncheckAll');
		}
	}

	msvp.setPickgridRightData = function(nowData){
		var data = $.extend(true,[],nowData);
		if(!data || data.length <= 0){
			this.deleteAllGridData(this.pickGrid);
		}else{
			this.pickGrid.loadData("right",{"code":200,"data":{"total":0,"rows":data}});
		}
	}

	msvp.showMetricDataGridData = function(isUpdate){
		this.showMetricDataGridDataAjax(isUpdate);
	}

	msvp.showMetricDataGridDataAjax = function(isUpdate){
		var _this = this;
		var curType = 3;//表示topoN
		var performanceReportType = 1;

		var param = {instanceId:_this.curLeftGridResourceInstanceList[0],
			reportType:curType,
			comprehensiveType:performanceReportType
		}

		var resourceInstanceIdArray = "";
		for(var i = 0 ; i < _this.curLeftGridResourceInstanceList.length ; i ++){
			resourceInstanceIdArray += _this.curLeftGridResourceInstanceList[i] + ",";
		}
		resourceInstanceIdArray = resourceInstanceIdArray.substring(0,resourceInstanceIdArray.length - 1);
		if(!resourceInstanceIdArray){
			return;
		}
		param.instanceIdList = resourceInstanceIdArray;
		var url = oc.resource.getUrl('portal/report/reportTemplate/getMetricListByInstanceId.htm');
		if(_this.mainResourceId == 'Business')
			url =  oc.resource.getUrl('portal/business/service/getBizMetricInfo.htm');
		oc.util.ajax({
			successMsg:null,
			url: url,
			data:param,
			startProgress:function(){},
			success:function(data){

				if(data.data){
					var noDataMsg = '';
					if(data.data.length <= 0){
						if(_this.curLeftGridResourceInstanceList.length > 1){
							noDataMsg = '所选资源无共有性能指标';
						}else{
							noDataMsg = '所选资源无性能指标';
						}
					}
					_this.setDatagridData(_this.dataGrid,data.data,noDataMsg);
				}else{
					alert('查询指标失败!');
				}
			}
		});
		
	}

	msvp.setDomains = function(domainIds){
		this.domainIds = domainIds;
		this.opt.domainIds = domainIds;
		this.init();
	}

	msvp.getData = function(){
		
		this.resourceType = this.comboTree.cfg.selector.combotree('getValue');
		
		var rids = this.pickGrid.selector.datagrid('getChecked');
		var trids = '';
		for(var i=0;i<rids.length;i++){
			trids += rids[i].id + ',';
		}
		if(trids.length >0){
			this.resourceIds = trids.substring(0,trids.length-1);
		}else{
			this.resourceIds = '';
		}
		
		this.subMetrics = this.selectChildResourceCombobox.cfg.selector.combobox('getValue');
		
		var me = this.dataGrid.selector.datagrid('getChecked');

		var data = {};
		if(me.length>0){
			this.choiseMetric = me[0].id;
			data.metricDetial = me[0];
		}else
			this.choiseMetric = '';

		//标识当前选中了所有资源
		data.checkAll = this.curLeftGridResourceInstanceCheckedAll==true;
		if(data.checkAll){
			this.resourceIds = '-1';
		}
		
		data.resourceType = this.resourceType;
		data.resourceIds = this.resourceIds;
		data.subMetrics = this.subMetrics;
		data.choiseMetric = this.choiseMetric;
		
		return data;
	}


	function topNConfig(opt) {
		this.opt = {
			panelArea : $(".widget-edit-panel .right"),//默认对象显示区域
			showConfigDialog: undefined,//注册父级调用函数
			args:undefined
		};
		$.extend(this.opt,opt,true);
		
		this.showConfigDialog = this.opt.showConfigDialog;
		this.metricSelect = undefined;//指标选择

		if(!this.showConfigDialog){
			throw '调用失败！';
		}
		
		this.moduleCode  = 'topN';

		this.init();
	}

	var tcp = topNConfig.prototype;
	
	tcp.init = function() {
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/topnConfig.html'),function(){
			_this._init();
		});
	};

	tcp._init = function(){
		var _this = this;
		
		var dataCom = [
			    {value:'5',text:'5',selected:true},
			    {value:'10',text:'10'}
	      ];
		
		var data = {};
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			data = JSON.parse(pros);
			data = data?data:{};
			
			if(data.chartType == 'table'){
				dataCom = [
						    {value:'5',text:'5',selected:true},
						    {value:'10',text:'10'},
						    {value:'15',text:'15'},
						    {value:'20',text:'20'},
						    {value:'25',text:'25'},
						    {value:'30',text:'30'}
				      ];
			}
			$('#sortCount').combobox({
				width:160,
				panelHeight:'auto',
				editable:true,
				data : dataCom
			});
			
			if(data.title == ''){
				$("#title").val("TOPN");
			}else{
				$("#title").val(data.title);
			}
			if(data.showPattern && data.showPattern != ""){
				if(data.showPattern == 'name'){
					$("#radioName").attr("checked",true);
				}else if(data.showPattern == 'ip'){
					$("#radioIp").attr("checked",true);
				}
				$("#showPattern").val(data.showPattern);
			}
	        $("#showName").attr('checked', data.showName);

	        var typ = data.chartType;
	        var $gobj = $('.graph-list-d[data-val="' + typ+'"]');
	        if($gobj.length==0)
	        	$gobj = $('.graph-list-d[data-val="vertical"]');
			$gobj.addClass('graph-list-d-selected');
			if(typ != 'horizontal' && typ != 'vertical'){
				$('#sortCount').combobox('destroy');
				var input = $('<input id="sortCount" name="sortCount"/>');
				$('input[name="sortMethod"][value="desc"]').before(input);
				$('#sortCount').combobox({
					width:160,
					panelHeight:'auto',
					editable:true,
					data : dataCom
				});
			}
			
			
	       $("#domains").val(data.domains);
	        if(data.domains){
	        	var user =  oc.index.getUser();
				var domains = user.domains;
				var domainsObj = {};
				for(var i=0; i< domains.length; i++){
					domainsObj[domains[i].id] = domains[i];
				}
				var dms = ""; 
				var csDoms = data.domains.split(',');
				for(var i=0; i< csDoms.length; i++){
					var id = csDoms[i] ;
					if(domainsObj[id] != undefined)
						dms += domainsObj[id].name + "，";
				}        
                if(dms.length >0){ 
                    dms = dms.substring(0,dms.length-1);
                }
                if(data.domains == "-1")
                	dms = '所有域';
	        	$(".view-domain").val(dms);

	        }
	        if(data.sortCount != undefined)
//	        	$("#sortCount").val(data.sortCount);
	        	$('#sortCount').combobox('setValue',data.sortCount);

	        $('[name=sortMethod][value="' + data.sortMethod + '"]').attr('checked',true);
	    }else{
	    	$('#sortCount').combobox({
				width:160,
				panelHeight:'auto',
				editable:false,
				data : dataCom
			});
	    	$('.graph-list-d[data-val="vertical"]').addClass('graph-list-d-selected');
	    }
	    var choiseMetricInfo = {};
	    if(data.choiseMetricInfo)
	    	choiseMetricInfo =  data.choiseMetricInfo;
	    choiseMetricInfo.domainIds = data.domains;
		_this.metricSelect = new metricSelect(choiseMetricInfo);

		_this.regEvent();
		
		if(!data.domains){
			var domains = oc.index.getUser().domains;
        	if(domains && domains.length > 0){
//        		var dms = "";
//        		var vals = "";
//        		for(var domainsIndex = 0; domainsIndex < domains.length;++domainsIndex){
//        			var obj = domains[domainsIndex];
//        			dms += obj.name + "，";
//                    vals += obj.id + ",";
//        		}
//    		 	if(dms.length >0){ 
//               	 	dms = dms.substring(0,dms.length-1);
//                }
    		 	$(".view-domain").val("所有域");
                $("#domains").val("-1");
                _this.metricSelect.setDomains("-1");
        	}
		}
		
		_this.showConfigDialog.setCustomColseDialog(true);
		_this.showConfigDialog.regConfirmFunction(function(){
			var cfg = {};
			if(_this.opt.args && _this.opt.args.cfg){
	        	cfg =$.extend({},_this.opt.args.cfg,true);
	        }
	        if(!_this._checkData())
	        	return;
	        
	        cfg.props = _this._getData();
	        var type = $(".graph-list-d").parent().find('.graph-list-d-selected').attr('data-val');
	        if(type == 'table'){
	        	if(isNaN(cfg.props.sortCount) || cfg.props.sortCount%1 != 0 || cfg.props.sortCount > 30 || cfg.props.sortCount < 1 ){
		        	alert("TOPN选项请输入1-30的正整数!");
		        	return false;
		        }
	        }
			_this.showConfigDialog.save(_this.moduleCode,cfg);
			_this.showConfigDialog.closeDialog();
			_this.showConfigDialog.setCustomColseDialog(false);
        });
        _this.showConfigDialog.regCancelFunction(function(){
        	_this.showConfigDialog.closeDialog();
        });
	}

	tcp.regEvent = function(){
		var _this = this;

		$(".graph-list-d").bind('click',function(){
			$(this).parent().find('.graph-list-d-selected').removeClass('graph-list-d-selected');
			$(this).addClass('graph-list-d-selected');
			var type = $(this).parent().find('.graph-list-d-selected').attr('data-val');
			if(type == 'horizontal' || type == 'vertical'){
				var countNum = $('#sortCount').combobox('getValue');
				$('#sortCount').combobox('destroy');
				var input = $('<input id="sortCount" name="sortCount"/>');
				$('input[name="sortMethod"][value="desc"]').before(input);
				$('#sortCount').combobox({
					width:160,
					panelHeight:'auto',
					editable:false,
					data:[
					    {value:'5',text:'5',selected:true},
					    {value:'10',text:'10'}
			      ]
				});
				if(countNum == 5 || countNum == 10){
					$('#sortCount').combobox('setValue',countNum);
				}else{
					$('#sortCount').combobox('setValue',5);
				}
			}else{
				var countNum = $('#sortCount').combobox('getValue');
				$('#sortCount').combobox('destroy');
				var input = $('<input id="sortCount" name="sortCount"/>');
				$('input[name="sortMethod"][value="desc"]').before(input);
				$('#sortCount').combobox({
					width:160,
					panelHeight:'auto',
					editable:true,
					data:[
					    {value:'5',text:'5',selected:true},
					    {value:'10',text:'10'},
					    {value:'15',text:'15'},
					    {value:'20',text:'20'},
					    {value:'25',text:'25'},
					    {value:'30',text:'30'}
			      ]
				});
				$('#sortCount').combobox('setValue',countNum);
			}

		});
		$("input[name=showPattern]").each(function(){
			$(this).bind('click',function(){
				$("#showPattern").val(this.value);
			})
		});
		
		//域选择
        $("#choiseDomain").unbind().bind("click",function(){
            var user =  oc.index.getUser();
            var $obj = $("<div/>");
           
            var domains = user.domains;
           	var divid = oc.util.generateId();
           	var $that = $("<div/>")
           	$that.attr('id',divid);
            $that.dialog({
            	href: oc.resource.getUrl('resource/module/home/edit/domainSelection.html'),
                title:'选择域',
                width:200,
                height:300,
                onLoad: function(){
                	//判断当前是否存在所选域，不存在则选中
		          	var checkDomain =  $("#domains").val();
		          	
		          	if(checkDomain.indexOf('-1') > -1){
		          		$("#allHint").next().remove();
		          	}else{
		          		$("#assignDomain").prop("checked",true);
		          		$("#allHint").css("display","none");
		          		App($obj);
		          	};
                	
                	
                	
                	//绑定单选时间
                	$("#allDomain").unbind().bind("click",function(){
                		$("#allHint").css("display","");
                		$("#allHint").next().remove();
                	});
                	$("#assignDomain").unbind().bind("click",function(){
                		$("#allHint").css("display","none");
                		App($obj);
                	});
                },
                buttons:[{
                    text:'确定',
                    handler:function(){
                    	var checkRadio = $("input[name='domain']:checked").val();
                    	if(checkRadio == -1){
                    		$(".view-domain").val("所有域");
                    		$("#domains").val("-1"); 
                    		_this.metricSelect.setDomains("-1");
                    	}else{
	                        var dats = $obj.datagrid('getChecked');
	                        var dms = "";
	                        var vals = ""; 
	                        $.each(dats,function(k,obj){
	                            dms += obj.name + "，";
	                            vals += obj.id + ",";
	                        });
	                        if(dms.length >0){ 
	                            dms = dms.substring(0,dms.length-1);
	                        }
	
	                        $(".view-domain").val(dms);
	                        $("#domains").val(vals);
	
							_this.metricSelect.setDomains(vals);
                    	}
                    	$that.dialog('close');
                    }
                }]
            });
            function App($obj){
	            $that.append($obj);
	            $obj.datagrid({
	                singleSelect:false,
	                fitColumns : true,
	                pagination:false,
	                width:200,
	                data:domains,
	                columns:[[{field:'ck',checkbox:true},
	                    {
	                        field:'id',
	                        title:'id', 
	                        hidden:true,
	                        width:80
	                    },
	                    {   
	                        field:'name',
	                        title:'域名称', 
	                        align:'center',
	                        formatter: function(value,row,index){
	                            return '<label title="' + value + '" >' + value + '</label>';
	                        },
	                        width:80
	                    }
	                ]],
	                onLoadSuccess:function(row){
	                	var data = _this._getData();
	                	var domains = data.domains;
	                	domains = domains.split(',');
	                	var p = {};
	                	for(var i=0;i<domains.length;i++){
	                		p[domains[i]] = domains[i];
	                	}
	                	var rowData = row.rows;
	                	$.each(rowData,function(idx,val){
	                		if(p[val.id])
	                        	$obj.datagrid("selectRow", idx);
	               		});
	                	 $obj.prev().attr("style","width:196;height:190px;overflow:auto;");
	                }
	            });
            }
        });
      //截取标题字段
        $("#title").bind('change',function(){
        	var maxlength = parseInt($(this).attr('maxlength'));
        	var inData = $(this).val();
        	var inlength = $(this).val().length;
        	if(inlength > maxlength){
        		inData = inData.substring(0,maxlength - 1);
        		$(this).val(inData);
        	}
        });
	}

	tcp._getData = function(){
		var data = {};
		if($("#title").val() == ''){
			data.title = "TOPN";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");
        data.sortCount = $('#sortCount').combobox('getValue');
        if(data.sortCount == null){
        	data.sortCount = $('#sortCount').combobox('getText');
        }
        data.sortMethod = $("[name=sortMethod]:checked").val();
        data.chartType = $(".graph-list-d-selected").attr('data-val');
        data.domains = $("#domains").val();
        data.time = $("#time").val();
        data.showPattern = $("#showPattern").val();

        data.choiseMetricInfo = this.metricSelect.getData();

        return data;
	}

	tcp._checkData = function(){
		var data = this._getData();

		var cmi =  data.choiseMetricInfo;
		if(cmi.choiseMetric == undefined || cmi.choiseMetric == ''){
			alert('请选择一个用于排序的指标！');
			return false;
		}
		return true;
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.topNConfig = function(opt){
    	return new topNConfig(opt);
    }

})