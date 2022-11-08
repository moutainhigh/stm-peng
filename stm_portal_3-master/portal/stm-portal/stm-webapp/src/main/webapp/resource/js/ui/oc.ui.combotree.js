/**
 * oc下拉树
 * @author ziwenwen
 * @param $
 */
(function($){
	function combotree(cfg){

		if (cfg.dictType){
			cfg.data=oc.util.getDict(cfg.dictType);
			cfg.valueField='code';
			cfg.pField='pcode';
		}
		
		var _cfg=this._cfg=$.extend({},this._defaults,this.cfg=cfg),that=this;
		if(this._checkCfg(_cfg)){
			this.input=(typeof _cfg.selector=='string')?$(_cfg.selector):_cfg.selector;
			this._filterCfg(_cfg);
			if(_cfg.onChange){
				var change=_cfg.onChange;
				_cfg.onChange=function(n,o){
					change(n,o,that._data[n],that._data[o]);
				};
			}
			
			this.jq=this.input.on(oc.events.form.loaded,function(e,ds){
				if(_cfg.loaded){
					_cfg.loaded(ds);
				}
			}).combotree(_cfg);
			
			if(cfg.url){
				this.reLoad(cfg.url);
			}else if(cfg.data){
				this.load(cfg.data);
			}
		};
	}
	
	combotree.prototype={
		constructor:combotree,
		id:undefined,//每个组件都可以有一个随机id
		jq:undefined,//所有ui组件需提供该对象，用于表示ui组件最顶层文档元素对应的jquery对象
		input:undefined,//如果为表单项（select、input、radio、textarea等），需提供此配置，用于包装用户提供的表单元素，是一个jquery对象
		cfg:undefined,//用户提供的原始配置
		_cfg:undefined,//组件渲染时所使用的配置 简写能够很容易表达其意的时候使用简写
		_data:{},
		_checkCfg:function(cfg){//此函数必须提供，用于检测用户的基本配置是否合法 线上环境可能去掉 合法返回true 否则返回false
			if(!cfg.selector){
				alert('没有提供表单项选择器或者表单项的jquery对象','error');
				return false;
			}
			return true;
		},
		_defaults:{//用户可能使用的默认配置
			selector:undefined,//表单项选择器或者表单项jquery对象
			url:undefined,//远程数据源 本地和远程都有时 使用远程数据源
			cache:true,//是否缓存数据源
			filter:undefined,//本地或者远程数据过滤函数 参数为本地配置数据或远程加载的数据 需返回一个数组
			valueField:'id',
			textField:'name',
			pField:'pid',
			editable:false,
			onChange:undefined,//change事件回调函数 传入四个参数 分别为新值、旧值、新值对象、旧值对象
			selectedMode:'all',//当为树形下拉框时，节点可选中的模式，包括all、leaf
			width:160,
			ignVal:null,//根据valueField需要过滤掉的数据
			placeholder:'${oc.local.ui.select.placeholder}',//为选择数据时提示信息，配置为null不显示
			data:[],
			loaded:undefined//数据加载完成后回调函数
		},
		disable:function(){
			this.jq.combotree('disable');
		},
		enable:function(){
			this.jq.combotree('enable');
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
			
			data=data?data.concat():[];

			$.isArray(data)||(data=[]);
			
			if(_cfg.placeholder){
				var obj={};
				obj[_cfg.valueField]='';
				obj[_cfg.textField]=_cfg.placeholder.toI18n();
				obj.iconCls='oc-blank-image';
				data.unshift(obj);
			}
			
			if(_cfg.valueField!='id'||_cfg.textField!='text'){
				for(var i=0,len=data.length,d,_ds=this._data;i<len;i++){
					d=data[i];
					d.id=d[_cfg.valueField];
					d.text=d[_cfg.textField];
					_ds[d.id]=d;
				}
			}
			
			_cfg.ignVal&&$.each(_cfg.ignVal,function(i,val){
				$.each(data,function(j,d){
					if(d[_cfg.valueField]==val){
						data.splice(j,1);
						return false;
					}
				});
			});
			
			_cfg.panelHeight=data.length*27;
			_cfg.panelHeight>202&&(_cfg.panelHeight=202);
			
			var treeDatas=oc.util.arr2tree(data, 'id', _cfg.pField,'children');
			data=treeDatas[0];
			_cfg.data=data;
			
			this.jq=this.input.combotree(_cfg);
			if(_cfg.placeholder&&!_cfg.value&&!this.jq.val()){
				this.jq.combotree('setValue','');
			}
		},
		_filterCfg:function(cfg){//控件初始化
			cfg.url=undefined;
			cfg.data=undefined;
			cfg.filter=undefined;
		}
	};
	
	oc.ui.combotree=function(cfg){
		return new combotree(cfg);
	};
})(jQuery);