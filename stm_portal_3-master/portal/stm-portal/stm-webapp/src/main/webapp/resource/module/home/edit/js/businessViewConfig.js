$(function(){
	function businessViewConfig(opt) {
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
		this.moduleCode  = 'businessView';

		this.init();
	}
	var bvcp = businessViewConfig.prototype;

	bvcp.init = function(){
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/businessConfig.html'),function(){
			oc.resource.loadScript(['resource/module/home/edit/js/businessViewConfig.js'], function(){
			 	_this._init();
			});
		});
	}

	bvcp._init = function(){
		var _this = this;
		var data = {};
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
		}
	
		if(data.title == '' || data.title == null){
			$("#title").val("业务视图");
		}else{
			$("#title").val(data.title);
		}
		$("#showName").attr('checked',data.showName);
	

		var url = oc.resource.getUrl('home/screen/getBizSetData.htm')
		$.getJSON(url,{dataType:'json'},function(d){
			if(d.code == 200){
				var d = d.data.biz;
//				var html = '';
//				html += '<option value="ywhz">业务汇总</option>';
//				$.each(d,function(k,v){
//					html += '  <option value="' + v.bizId +'">' + v.title +  '</option>';
//				});
//				$("#business").html(html);
				var bS = [];
				bS.push({value:'ywhz',text:'业务汇总',selected:true});
				$.each(d,function(k,v){
					var str = {
						value : v.bizId,
						text : v.title
					};
					bS.push(str);
				});
				$('#business').combobox({
					width:160,
					panelHeight:'auto',
					editable:false,
					data:bS
				});
				if(data.bizId)
					$('#business').combobox('setValue', data.bizId);
			}
			
			_this.refreshView();
			_this.regEvent();
		});

		_this.showConfigDialog.regConfirmFunction(function(){
			var cfg = {};
			if(_this.opt.args && _this.opt.args.cfg){
	        	cfg =$.extend({},_this.opt.args.cfg,true);
	        }
	        cfg.props = _this._getData();
			_this.showConfigDialog.save(_this.moduleCode,cfg);
        });

	}

	bvcp.regEvent = function(){
		var _this = this;   
		$('#business').combobox({
			onChange:function(newValue,oldValue){
				var url = newValue;
				_this.refreshView();
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

	bvcp.refreshView = function(){
		var _this = this;   
		var ps = _this._getData();
		ps.selector = $(".overview");
		if(ps.bizId == 'ywhz'){
			ps.selector.html('<div class="img-ywgl"></div>');
		}else{
			ps.selector.height(300);
			_this.widgetLoader.loadWedget(_this.moduleCode,ps);
		}
	}

	bvcp._getData = function(){
		var data = {};
		if($("#title").val() == ''){
			data.title = "业务视图";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");
        data.bizId = $('#business').combobox('getValue');
       
        return data;
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.businessViewConfig = function(opt){
    	return new businessViewConfig(opt);
    }

})