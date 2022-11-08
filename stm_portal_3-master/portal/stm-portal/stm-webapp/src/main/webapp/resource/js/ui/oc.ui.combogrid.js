/**
 * oc下拉菜单
 * @author ziwenwen
 * @param $
 */
(function($){
	function combogrid(cfg){
		var _cfg=this._cfg=$.extend({},this._defaults,this.cfg=cfg);
		if(this._checkCfg(_cfg)){
			this.input=(typeof _cfg.selector=='string')?$(_cfg.selector):_cfg.selector;
			this._filterCfg(_cfg);
			
			var cCount=2,width=0;
			for(var css=_cfg.columns,i=0,len=css.length;i<len;i++){
				for(var j=0,cs=css[i],jl=cs.length,c;j<jl;j++){
					c=cs[j];
					width+=c.width||(c.width=80);
				}
				cCount+=jl;
			}
			_cfg.panelWidth=width+cCount;
			
			this.jq=this.input.on(oc.events.form.loaded,function(e,ds){
				if(_cfg.loaded){
					_cfg.loaded(ds);
				}
			}).combogrid(_cfg);
			
			if(cfg.url){
				this.reLoad(cfg.url);
			}else if(cfg.data){
				this.load(cfg.data);
			}
		};
	}
	
	combogrid.prototype={
		constructor:combogrid,
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
			if(!cfg.columns){
				alert('没有配置下拉grid的列：columns','error');
				return false;
			}
			return true;
		},
		_defaults:{//用户可能使用的默认配置
			selector:undefined,//表单项选择器或者表单项jquery对象
			url:undefined,//远程数据源 本地和远程都有时 使用远程数据源
			cache:true,//是否缓存数据源
			filter:undefined,//本地或者远程数据过滤函数 参数为本地配置数据或远程加载的数据 需返回一个数组
			idField:'id',
			textField:'name',
			editable:false,
			width:160,
			ignVal:null,//根据valueField需要过滤掉的数据
			placeholder:'${oc.local.ui.select.placeholder}',//为选择数据时提示信息，配置为null不显示
			data:[],
			loaded:undefined//数据加载完成后回调函数
		},
		disable:function(){
			this.jq.combogrid('disable');
		},
		enable:function(){
			this.jq.combogrid('enable');
		},
		reLoad:function(url){
			var that=this;
			oc.util.ajax({
				url:url,
				successMsg:null,
				failureMsg:null,
				success:function(data){
					that.load(data);
				}
			});
		},
		load:function(data){
			var _cfg=this._cfg,cfg=this.cfg;
			
			cfg.filter&&(data=cfg.filter(data));

			$.isArray(data)||(data=[]);
			_cfg.ignVal&&$.each(_cfg.ignVal,function(i,val){
				$.each(data,function(j,d){
					if(d[_cfg.valueField]==val){
						data.splice(j,1);
						return false;
					}
				});
			});

			_cfg.data=data;
			
			_cfg.panelHeight=data.length*27+77;
			_cfg.panelHeight>202&&(_cfg.panelHeight=202);
			
			this.jq=this.input.combogrid(_cfg).trigger(oc.events.form.loaded,data);
			if(!_cfg.value){
				this.jq.combogrid('setValue',' ');
			}
		},
		_filterCfg:function(cfg){//控件初始化
			cfg.url=undefined;
			cfg.data=undefined;
			cfg.filter=undefined;
		}
	};
	
	oc.ui.combogrid=function(cfg){
		return new combogrid(cfg);
	};
})(jQuery);