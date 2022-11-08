/**
 * oc下拉菜单
 * @author ziwenwen
 * @param $
 */
(function($){
	
	
	
	$.fn.textbox.methods.setText = function(jq, _480) {
		return jq.each(function() {
			var opts = $(this).textbox("options");
			var _481 = $(this).textbox("textbox");
			if ($(this).textbox("getText") != _480) {
				opts.value = _480;
				_481.attr('title',_480);
				_481.val(_480);
			}
			if (!_481.is(":focus")) {
				if (_480) {
					_481.removeClass("textbox-prompt");
				} else {
					_481.attr('title',opts.prompt);
					_481.val(opts.prompt).addClass("textbox-prompt");
				}
			}
			$(this).textbox("validate");
		});
	};
	
	$.fn.textbox.methods.initValue = function(jq, _482) {
		return jq.each(function() {
			var _483 = $.data(this, "textbox");
			_483.options.value = "";
			$(this).textbox("setText", _482);
			_483.textbox.find(".textbox-value").val(_482);
			$(this).attr('title',_482);
			$(this).val(_482);
		});
	};
	
	function combobox(cfg){
		
		if (cfg.dictType){
			cfg.data=oc.util.getDict(cfg.dictType);
			cfg.valueField='code';
		}
		var _cfg=this._cfg=$.extend({},this._defaults,this.cfg=cfg);
		if(this._checkCfg(_cfg)){
			this.input=(typeof _cfg.selector=='string')?$(_cfg.selector):_cfg.selector;
			this._filterCfg(_cfg);

			this.jq=this.input.on(oc.events.form.loaded,function(e,ds){
				if(_cfg.loaded){
					_cfg.loaded(ds);
				}
			}).combobox(_cfg);
			
			if(cfg.url){
				this.reLoad(cfg.url,this._selected);
			}else if(cfg.data){
				this.load(cfg.data,this._selected);
			}
		};
	}
	
	combobox.prototype={
		constructor:combobox,
		id:undefined,//每个组件都可以有一个随机id
		jq:undefined,//所有ui组件需提供该对象，用于表示ui组件最顶层文档元素对应的jquery对象
		input:undefined,//如果为表单项（select、input、radio、textarea等），需提供此配置，用于包装用户提供的表单元素，是一个jquery对象
		cfg:undefined,//用户提供的原始配置
		_cfg:undefined,//组件渲染时所使用的配置 简写能够很容易表达其意的时候使用简写
		_checkCfg:function(cfg){//此函数必须提供，用于检测用户的基本配置是否合法 线上环境可能去掉 合法返回true 否则返回false
			if(!cfg.selector){
				alert('没有提供表单项选择器或者表单项的jquery对象','error');
				return false;
			}
			return true;
		},
		_selected:function(ds){
			var val=this._cfg.selected,type=typeof val,len;
			if(type=='string'){
				this.input.combobox('setValue',val);
			}else if(type=='number'&&(len=ds.length)>0){
				(val>=len)&&(val=len-1);
				this.input.combobox('setValue',ds[val][this._cfg.valueField]);
			}
		},
		_defaults:{//用户可能使用的默认配置
			selector:undefined,//表单项选择器或者表单项jquery对象
			url:undefined,//远程数据源 本地和远程都有时 使用远程数据源
			cache:true,//是否缓存数据源
			filter:undefined,//本地或者远程数据过滤函数 参数为本地配置数据或远程加载的数据 需返回一个数组
			valueField:'id',
			textField:'name',
			editable:false,
			selected:0,//默认选中第一行
			dictType:null,
			width:160,
			ignVal:null,//根据valueField需要过滤掉的数据
			placeholder:'${oc.local.ui.select.placeholder}',//为选择数据时提示信息，配置为null不显示
			data:[],
			loaded:undefined//数据加载完成后回调函数
		},
		disable:function(){
			this.jq.combo('disable');
		},
		enable:function(){
			this.jq.combo('enable');
		},
		reLoad:function(url,fn){
			var that=this;
			oc.util.ajax({
				url:url,
				successMsg:null,
				failureMsg:null,
				success:function(data){
					that.load(data,fn);
				}
			});
		},
		load:function(data,fn){
			var _cfg=this._cfg,cfg=this.cfg;
			
			cfg.filter&&(data=cfg.filter(data));
			
			data=data?data.concat():[];

			$.isArray(data)||(data=[]);
			
			_cfg.ignVal&&$.each(_cfg.ignVal,function(i,val){
				$.each(data,function(j,d){
					if(d[_cfg.valueField]==val){
						data.splice(j,1);
						return false;
					}
				});
			});
			
			if(_cfg.placeholder){
				var obj={};
				obj[_cfg.valueField]='';
				obj[_cfg.textField]=_cfg.placeholder.toI18n();
				data.unshift(obj);
			}
			
			_cfg.data=data;
			
			_cfg.panelHeight=data.length*36+5;
			_cfg.panelHeight>202&&(_cfg.panelHeight=202);
			
			this.jq=this.input.combobox(_cfg);
			fn&&fn.call(this,data);
		},
		_filterCfg:function(cfg){//控件初始化
			cfg.url=undefined;
			cfg.data=undefined;
			cfg.filter=undefined;
		}
	};
	
	oc.ui.combobox=function(cfg){
		return new combobox(cfg);
	};
})(jQuery);