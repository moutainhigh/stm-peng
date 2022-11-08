$(function(){

	function virtualConfig(opt){
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

		this.widgetLoader = this.opt.widgetLoader;
		this.moduleCode  = 'virtual';
		this.vcenterTree = []; 

		//console.log(opt);
		this.init();
	}

	var vlcp =virtualConfig.prototype;
	vlcp.init = function() {
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/virtualConfig.html'),function(){
			oc.util.ajax({
		    	url:oc.resource.getUrl('portal/vm/topologyVm/getLeftNavigateTree.htm'),
		    	timeout:null,
				startProgress:null,
				stopProgress:null,
		    	success:function(d){
		    		if(d.data && d.code ==200){
		    			_this.vcenterTree = _this._genEUITree(d.data);
		    			_this._init();
		    		}
		    	}
			
			});
		});
	};

	vlcp._init = function(){
		var _this = this;   
		//console.log(_this.vcenterTree);
		$('#business').combotree({
			width:160,
			data:_this.vcenterTree.tree,
			onChange:function(newValue,oldValue){
				_this.refreshView();
			}
		});
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
	        var item = data['item'];
	        if(item){
	        	var id = item.uuid;
				$('#business').combotree('setValue',id);
	        }
	        if(data.title == ''){
				$("#title").val("虚拟化拓扑图");
			}else{
				$("#title").val(data.title);
			}
	        $("#showName").attr('checked', data.showName);
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

		_this.refreshView();
	}

	vlcp.regEvent = function(){
		var _this = this;
		
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

	vlcp._genEUITree = function(dt){
		var tree = [];
		var treeObj = {};
		for(var i=0; i <dt.length;i++){
			dt[i].id = dt[i].uuid;
			dt[i].text = dt[i].vmname;
			treeObj[dt[i].id] = dt[i];
		}
		var obj = $.extend({},treeObj,true);

		for(var i=0; i <dt.length;i++){
			var chd = dt[i];
			var p = treeObj[chd.puuid]; 
			if(p){
				if(!p.children){
					p.children = [];
				}
				p.children.push(chd);
			}else{
				tree.push(chd);
			}
		}

		return {tree:tree,treeObj:obj};

	}
	vlcp._getData = function(){
		var id = $('#business').combotree('getValue');
		
		var data = {};
		data['item'] = this.vcenterTree.treeObj[id];
		if($("#title").val() == ''){
			data.title = "虚拟化拓扑图";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");

        return data;
	}

	vlcp.refreshView = function(){
		var _this = this;   
		var ps = _this._getData();
	    ps.selector = $(".overview");
	    _this.widgetLoader.loadWedget(_this.moduleCode,ps);
	}



    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.virtualConfig = function(opt){
    	return new virtualConfig(opt);
    }

})