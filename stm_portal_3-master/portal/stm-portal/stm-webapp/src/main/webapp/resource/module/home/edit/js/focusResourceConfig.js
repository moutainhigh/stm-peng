$(function(){

	function focusResourceConfig(opt){
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

		this.moduleCode  = 'focusResource';

		this.init();
	}

	var frcp =focusResourceConfig.prototype;
	frcp.init = function() {
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/focusResourceConfig.html'),function(){
			_this._init();
		});
	}

	frcp._init = function() {
		var _this = this;
		
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
			if(data.title == ''){
				$("#title").val("关注资源");
			}else{
				$("#title").val(data.title);
			}
	        $("#showName").attr('checked', data.showName);

	        var typ = data.showMethod;
	        if(!typ)
	        	typ = 'resourceDetail';
			$('.graph-resource-li[data-val="' + typ+'"]').addClass('graph-resource-li-selected');
	       
	        $('[name=showPanel][value="' + data.showPanel + '"]').attr('checked',true);

	        $("#choisedResource").val(data.resources);
	        _this.showResouceList(typ);
	    }else{
	    	$('.graph-resource-li[data-val="resourceDetail"]').addClass('graph-resource-li-selected');
	    	$("#choisedResource").val("{}");
	    	_this.showResouceList("resourceDetail");
	    }

		_this.regEvent();
		
		_this.showConfigDialog.regConfirmFunction(function(){
			var cfg = {};
			if(_this.opt.args && _this.opt.args.cfg){
	        	cfg =$.extend({},_this.opt.args.cfg,true);
	        }
	        cfg.props = _this._getData();
			_this.showConfigDialog.save(_this.moduleCode,cfg);
        });
	};

	frcp.regEvent = function(){
		var _this = this;

		$(".graph-resource-li").bind('click',function(){
			$(this).parent().find('.graph-resource-li-selected').removeClass('graph-resource-li-selected');
			$(this).addClass('graph-resource-li-selected');
			var showMethod = $(this).attr('data-val');
			if(showMethod == 'resourceDetail' ){
				$('.graph-resource').find('div.graph-img').removeClass('graph-resource-zyzl').removeClass('graph-resource-ywzl').addClass('graph-resource-zyxq');
			}else if(showMethod == 'resourceList' ){
				$('.graph-resource').find('div.graph-img').removeClass('graph-resource-ywzl').removeClass('graph-resource-zyxq').addClass('graph-resource-zyzl');
			}else if(showMethod == 'businessList' ){
				$('.graph-resource').find('div.graph-img').removeClass('graph-resource-zyzl').removeClass('graph-resource-zyxq').addClass('graph-resource-ywzl');
			}
			_this.showResouceList(showMethod);
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

	frcp.showResouceList = function(type){
		var _this = this;
		if(type == 'resourceDetail' || type == 'resourceList'){//资源相关列表
			$('#choise-resource').show();
			$('#choise-business').hide();
			var maxSelects = (type == 'resourceList')?10:1;
			var res = $("#choisedResource").val();
			try{
				res = (res ==undefined)?{}:JSON.parse(res);
				var isNet = false;
				if(maxSelects ==1 ){
					$.each(res,function(k,v){
						if(v.rootId =='NetworkDevice'){
							 isNet = true;
						}
					});
				}
				isNet?$("#showPanel").show():$("#showPanel").hide();//如果是网络设备则显示网络设备面板设置

			}catch(e){
				res = undefined;
			}
			//res = {{43003:{}}};

			oc.resource.loadScripts(['resource/module/home/edit/js/resourceSelect.js'], function(){
					var a = {selector:$('#choise-resource'),
								maxSelects:maxSelects,
								resources:res,
								callback:function(nodes,val,treeNode){
//									var t = JSON.stringify(val);
									var t = '';
									var res = $("#choisedResource").val();
									if(res == ''){
										res = "{}";
									}
									if(res != null){
										res = JSON.parse(res);
										//判断是增加节点还是删除节点
										if(val[treeNode.id] != null){//增加节点
											res = $.extend( true, res, val );
											if(treeNode.pId && treeNode.pId == 'URL'){
												res = val;
											}
										}else{
											delete res[treeNode.id];
										}
										t = JSON.stringify(res);
									}
									//资源详情的时候直接添加当前结点
									if(type == 'resourceDetail'){
										var t = JSON.stringify(val);
									}
//									var t = JSON.stringify(res);
									$("#choisedResource").val(t);
									if(maxSelects == 1){
										var isNet = false;
										$.each(val,function(k,v){
											if(v.rootId =='NetworkDevice'){
												isNet = true;
											}
										});
										isNet?$("#showPanel").show():$("#showPanel").hide();
									}else{
										$("#showPanel").hide();
									}
								}
							};
					var rs = new oc.index.home.widgetedit.resourceSelect(a);
			});

		}else{//业务相关列表选择
			$('#choise-resource').hide();
			$('#showPanel').hide();
			$('#choise-business').show();

			$("#choise-business-grid").datagrid({
				url:oc.resource.getUrl('home/biz/getList.htm?dataType=json'),
				pagination:false,
				width:'60%',
				fitColumns:true,
				columns:[[
			             {field:'id',checkbox:true},
			             {field:'name',title:'业务系统名称'},
			             {field:'managerId',hidden:true},
			             {field:'managerName',title:'责任人',width:100}
	        	]],
				loadFilter:function(data){
	        		if(data.code == 200){
	        			return data.data;
	        		}else{
	        			return {rows:[],size:0};
	        		}
	        	},
	        	onLoadSuccess:function(data){
	        		_this.rows = data.rows;
	        		if(_this.opt.args && _this.opt.args.cfg){
	        			var pros = _this.opt.args.cfg.props;
	        			var d = JSON.parse(pros);
	        			if(d.resources != null && d.resources.length > 0 && data.rows != null && data.rows.length > 0 && d.showMethod == 'businessList'){
	        				var resourcesList = d.resources;
	        				var rows = data.rows;
	        				for(var reIndex = 0; reIndex < resourcesList.length; ++reIndex){
	        					for(var rowsIndex = 0; rowsIndex < rows.length; ++rowsIndex){
	        						if(resourcesList[reIndex].id == rows[rowsIndex].id){
	        							$('[name=id][value="' + resourcesList[reIndex].id + '"]').attr('checked','checked');
	        						}
	        					}
	        				}
	        			}
	        		}
	        	}
			});
		}


	}

	frcp._getData = function(){
		var _this = this;
		var data = {};
		if($("#title").val() == ''){
			data.title = "关注资源";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");
        data.showMethod = $(".graph-resource-li-selected").attr('data-val');

        data.showPanel = $("[name=showPanel]:checked").val();
        data.resources = $("#choisedResource").val();
        if('businessList' == data.showMethod){
//        	data.resources = $("#choise-business-grid").datagrid('getChecked');
        	var checkRows = $("input[name=id]:checked");
        	var resourceslist = [];
        	var rows = _this.rows;
        	for(var i = 0 ; i < checkRows.length; ++i){
        		for(var j = 0; j < rows.length; ++j){
        			if(rows[j].id == checkRows[i].value){
        				resourceslist.push(rows[j]);
        			}
        		}
        	}
        	data.resources = resourceslist;
        }
        return data;
	}




   	oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.focusResourceConfig = function(opt){
     	//alert("关注资源配置");
    	return new focusResourceConfig(opt);
    }
})