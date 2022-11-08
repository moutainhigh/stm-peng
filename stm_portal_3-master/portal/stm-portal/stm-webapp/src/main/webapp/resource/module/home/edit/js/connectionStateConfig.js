$(function(){

	function connectionStateConfig(opt){
		this.opt = {
			panelArea : $(".widget-edit-panel .right"),//默认对象显示区域
			showConfigDialog: undefined,//注册父级调用函数
			args:undefined
		};
		$.extend(this.opt,opt,true);
		
		this.showConfigDialog = this.opt.showConfigDialog;
		if(!this.showConfigDialog){
			throw '调用失败！';
		}
		
		//分页显示的每页行数
		this.pageSize = 12;
		//分页开始数
		this.startNum = 0;
		//用于判断滚动分页加载是否正在进行
		this.isLoadding = false;
		this.curQueryResourceParameter = {};
		
		this.widgetLoader = this.opt.widgetLoader;
		this.moduleCode  = 'connectionState';
		this.selectResources = {};
		this.init();
	}

	var cscp =connectionStateConfig.prototype;
	cscp.init = function() {
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/connectionStateConfig.html'),function(){
			_this._init();
		});
	};

	cscp._init = function(){
		this._initDataGrid();

		this.regEvent();
	}

	cscp._initDataGrid = function(){
		var _this = this;
		_this.startNum = 0;
		//获取已选择资源ID
		var data = {};
		var ids = [];
		var pros;
		if(this.opt.args && this.opt.args.cfg){
			pros = this.opt.args.cfg.props;
			data =  JSON.parse(pros);
			if(data.title == ''){
				$("#title").val("设备连通性");
			}else{
				$("#title").val(data.title);
			}
			$("#showName").attr('checked', data.showName);
			for(var id in data.selectedResource ){
				ids.push(data.selectedResource[id].id);
			}
			ids = ids.join(",");
		}
		if(ids.length == 0){
			ids = "";
		}
		_this.curQueryResourceParameter.ids = ids;
		_this.curQueryResourceParameter.pageSize = _this.pageSize;
		var searchContent = $('#instance-res-search').val();
		_this.curQueryResourceParameter.searchContent = searchContent;
		$u1 = $("#connectionState");
		_this.pickGrid = oc.ui.datagrid({
			selector:$u1,
			url: '/home/workbench/resource/getNewLeftResourceDefBySearchContent.htm',
			width:'100%',
			height:'100%',
			singleSelect:false,
			fitColumns:true,
			//showFooter:false,
			pagination:false,
			queryParams:{
		    	dataType:'json',
		    	searchContent:searchContent,
		    	ids:ids,
		    	startNum:_this.startNum,
		    	pageSize:_this.pageSize
		    },
		    columns:[[
		    	{field:'id',title:'id',width:100,checkbox:true},
		    	{field:'ip',title:'IP地址',width:180,align:'left'},
		    	{field:'name',title:'名称',width:180,align:'left'},
		        {field:'resourceType',title:'资源类型',width:180,align:'left'},
		    ]],
		    onClickRow:function(index,row){
		    	getCheckedRows(index,row,"check");
		    },
		    onCheck:function(index,row){
		    	getCheckedRows(index,row,"check");
		    },
		    onUncheck:function(index,row){
		    	getCheckedRows(index,row,"onCheck");
		    },
		    onCheckAll:function(rows){
		    	getCheckedAllRows(rows,"check");
		    },
		    onUncheckAll:function(rows){
		    	getCheckedAllRows(rows,"onCheck");
		    },
		    loadFilter:function(data){
		    	if(data.code == 200){
		    		var data = data.data;
		    		var obj = {
			    			size:data.length,
			    			rows:data
			    		}
		    		
		    		return obj;
		    	}
		    },
		    onLoadSuccess:function(data){
		    	_this.curQueryResourceParameter.dataLength = data.size;
		    	var leftData = data.rows;
				var idsA = ids.split(',')
				next : for(var i = 0 ; i < idsA.length ; i ++){
					for(var j = 0 ; j < data.size ; j ++){
						if(leftData[j].id == idsA[i]){
							_this.isAutoChecked = true;
							_this.pickGrid.selector.datagrid('checkRow',_this.pickGrid.selector.datagrid('getRowIndex',leftData[j]));
							continue next;
						}
					}
				}
//				_this.refreshView();
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
					 if($(this).get(0).scrollHeight - $(this).height() - $(this).scrollTop() < 1 && _this.pickGrid.selector.datagrid('getRows').length > 0){
						 timeoutLeftGrid = setTimeout(function(){
							 	//发送请求刷新主资源列表
							 if(_this.startNum == 0){
								 _this.startNum = _this.pageSize;
							 }
							 if(_this.curQueryResourceParameter.dataLength < _this.pageSize){
								 _this.startNum = _this.curQueryResourceParameter.dataLength;
							 }
						 	_this.curQueryResourceParameter.startNum = _this.startNum;
							_this.showResourceInstance(_this.curQueryResourceParameter,false,true);
						 },200);
					 }
				}
		});		

		function getCheckedRows(index,row,type){
//			var rows = $u1.datagrid('getChecked');
//			_this.selectResources={};
//			for (var i = 0; i < rows.length; i++) {
//				var row = rows[i];
			if(type == "check"){
				_this.selectResources[row.id] = {
						name:row.name,
						ip:row.ip,
						id:row.id,
						pId:row.pId,
						resourceType:row.resourceType
				}
			}else{
				delete _this.selectResources[row.id];
			}
//			}
//			_this.refreshView();
		}
		function getCheckedAllRows(rows,type){
			if(rows && rows.length > 0){
				for (var i = 0; i < rows.length; i++) {
					var row = rows[i];
					if(type == "check"){
						if(_this.selectResources[row.id] == null){
							_this.selectResources[row.id] = {
									name:row.name,
									ip:row.ip,
									id:row.id,
									pId:row.pId,
									resourceType:row.resourceType
							}
						}
					}else{
						if(_this.selectResources[row.id] != null){
							delete _this.selectResources[row.id];
						}
					}
				}
			}
		}

		//遍历所有资源
		function traveTree(children){
			var rows = [];
			for(var i=0;i<children.length;i++){
				var chd = children[i];
				if(chd.isParent){
					var childs = chd.children;
					for(var j =0;j <childs.length;j++ ){
	    				var oj =childs[j];
	    				oj['resourceType'] = chd.name;
	    				var name = oj.name;
	    				var idx = name.indexOf(')');
	    				oj.ip = name.substring(1,idx);
	    				oj.name = name.substring(idx+1);
			    	}
			    	var tp = traveTree(childs);
					rows = rows.concat(tp);
				}else{
					rows.push(chd);
				}

			}
			return rows;
		}
	}
	
	//发送分页获取资源列表
	cscp.showResourceInstance = function(parameter,isFirstCheckbox,isPaging){
		var _this = this;
		_this.isLoadding = true;
		  oc.util.ajax({
			  url: oc.resource.getUrl('home/workbench/resource/getNewLeftResourceDefBySearchContent.htm'),
			  timeout:null,
			  data:parameter,
			  success:function(data){
				  if(data.data){
					  _this.curQueryResourceParameter = parameter;
					  
					  if(isPaging && (!data.data || data.data.length <= 0)){
					    	alert('所有数据已加载完毕');
					    	parameter.pageSize = 0;
					    	_this.isLoadding = false;
					    	return;
					  }else if(isPaging){
						  _this.startNum = parameter.startNum + data.data.length;
					  }else{
						  _this.startNum = data.data.length;
					  }

					  if(isPaging){
						  for(var i = 0 ; i < data.data.length ; i ++){
							  var isChecked = false;
							  if(parameter.resourceIds){
								  var ids = parameter.ids.split(',');
								  for(var j = 0 ; j < ids.length ; j++){
									  if(ids[j] == data.data[i].id){
										  isChecked = true;
										  break;
									  }
									  
								  }
							  }
							  _this.pickGrid.selector.datagrid('appendRow',data.data[i]);
							  if(isChecked){
								  _this.isAutoChecked = true;
								  _this.pickGrid.selector.datagrid('checkRow',_this.pickGrid.selector.datagrid('getRowIndex',data.data[i]));
							  }else{
								  _this.isAutoChecked = true;
								  _this.pickGrid.selector.datagrid('uncheckRow',_this.pickGrid.selector.datagrid('getRowIndex',data.data[i]));
							  }
						  }
					  }
					  
				  }else{
					  alert('查询资源实例失败!');
				  }
				  _this.isLoadding = false;
//				  _this.refreshView();
			  }
		  });
		  
	}

	cscp.regEvent = function(){
		var _this = this;
		$('#instance-res-search-button').linkbutton('RenderLB',{
	        iconCls: 'fa fa-search',
	        onClick : function(){
	        	_this._initDataGrid();
	        }
	    });
//	    _this.showConfigDialog.setCustomColseDialog(true);
		_this.showConfigDialog.regConfirmFunction(function(){
            var cfg = {};
            if(_this.opt.args && _this.opt.args.cfg){
                cfg =$.extend({},_this.opt.args.cfg,true);
            }

            cfg.props = _this._getData();
            _this.showConfigDialog.save(_this.moduleCode,cfg);
            _this.showConfigDialog.closeDialog();
//            _this.showConfigDialog.setCustomColseDialog(false);
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

	cscp.refreshView = function(){
		var _this = this;   
		var ps = $.extend({},_this._getData(),true);
	    ps.selector = $(".overview");
	    _this.widgetLoader.loadWedget(_this.moduleCode,ps);

	}
	cscp._getData = function(){
		 var data = {};
	        data.selectedResource =this.selectResources;
	        if($("#title").val() == ''){
	        	data.title = "设备连通性";
	        }else{
	        	data.title = $("#title").val();
	        }
	        data.showName = $("#showName").is(":checked");
	        return data;
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.connectionStateConfig = function(opt){
    	return new connectionStateConfig(opt);
    }

})