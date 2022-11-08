$(function(){

	function outerPageConfig(opt){
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
		this.moduleCode  = 'outerPage';
		
		this.init();
	}

	var opcp =outerPageConfig.prototype;
	opcp.init = function() {
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/outPageConfig.html'),function(){
			_this._init();
		});
	};

	opcp._init = function(){

		var _this = this;
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
			$("#title").val(data.title);
	        $("#showName").attr('checked', data.showName);
	        $("#urladdress").val(data.url);
	    }
		
		_this.regEvent();

		_this.showConfigDialog.regConfirmFunction(function(){
			//校验url是否正确
			$("#urladdress").blur();
			if(_this.isTrue == false){
				return false;
			}
			
			var cfg = {};
			if(_this.opt.args && _this.opt.args.cfg){
	        	cfg =$.extend({},_this.opt.args.cfg,true);
	        }
	        cfg.props = _this._getData();
			_this.showConfigDialog.save(_this.moduleCode,cfg);
        });

		_this.refreshView();
	}

	opcp.regEvent = function(){
		var _this = this;   
		$("#urladdress").bind('blur',function(){
			var url = $(this).val();
			_this.isTrue = true;
			if(isURL(url)){
				$.ajax({
			        url: url,
			        type: 'GET',
			        complete: function(response){
			            if(response.status == 404){
			            	alert('网址无效');
			            	_this.isTrue = false;
			            }else{
			            	_this.refreshView();
			            }
			        }
			    });
			}else{
				alert('错误的url地址！');
				_this.isTrue = false;
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

	opcp._getData = function(){
		var data = {};
		data.title = $("#title").val();
        data.showName = $("#showName").is(":checked");
        data.url = $("#urladdress").val();

        return data;
	}

	opcp.refreshView = function(){
		var _this = this;   
		var ps = _this._getData();
	    ps.selector = $(".overview");
	    _this.widgetLoader.loadWedget(_this.moduleCode,ps);
	}

	function isURL(str_url) { 
		var strRegex = '^((https|http|ftp|rtsp|mms)+://)'
		+ '(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@ 
		+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184 
		+ '|' // 允许IP和DOMAIN（域名） 
		+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www. 
		+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名 
		+ '[a-z]{2,6})' // first level domain- .com or .museum 
		+ '(:[0-9]{1,4})?' // 端口- :80 
		+ '((/?)|' // a slash isn't required if there is no file name 
		+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$'; 
//		var strRegex = '^((https|http|ftp|rtsp|mms)+://)'
//			+ '+(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@ 
//			+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184 
//			+ '|' // 允许IP和DOMAIN（域名） 
//			+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www. 
//			+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名 
//			+ '[a-z]{2,6})' // first level domain- .com or .museum 
//			+ '(:[0-9]{1,4})?' // 端口- :80 
//			+ '((/?)|' // a slash isn't required if there is no file name 
//			+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$'; 
		var re=new RegExp(strRegex); 
		
		if (re.test(str_url)) { 
			return (true); 
		} else { 
			return (false); 
		} 
	}
    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.outerPageConfig = function(opt){
    	return new outerPageConfig(opt);
    }

})