/**
 * oc下拉菜单
 * @author ziwenwen
 * @param $
 */
(function($){
	function select(cfg){
		cfg=this.cfg=$.extend({},this._defaults,cfg);
		if(this._checkCfg(cfg)){
			this.input=(typeof cfg.selector=='string')?$(cfg.selector):cfg.selector;
			this._init(cfg);
		};
	}
	
	select.prototype={
		constructor:select,
		id:undefined,//每个组件都可以有一个随机id
		jq:undefined,//所有ui组件需提供该对象，用于表示ui组件最顶层文档元素对应的jquery对象
		input:undefined,//如果为表单项（select、input、radio、textarea等），需提供此配置，用于包装用户提供的表单元素，是一个jquery对象
		cfg:undefined,//组件渲染时所使用的配置 简写能够很容易表达其意的时候使用简写
		_selectedSpan:undefined,//所有在初始化过程中可能将赋予ui组件对象的值都需声明，即便因某些配置可能不会赋值也需声明，以增强可读性
		_ul:undefined,//前面加断线表示私有属性，禁止用户直接调用
		val:function(v){
			
		},
		_dom:{
			div:$(
				'<div class="btn-group dropup"><button type="button" class="btn btn-default">Dropup</button>'+
					'<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">'+
						'<span class="caret"></span>'+
						'<span class="sr-only"></span>'+
					'</button>'+
					'<ul class="dropdown-menu" role="menu">'+
				  	'</ul>'+
				'</div>'),
			 li:$('<li><a href="#"></a></li>'),
			 icon:$('<span class="glyphicon glyphicon-search"></span>'),
		},
		_checkCfg:function(cfg){//此函数必须提供，用于检测用户的基本配置是否合法 线上环境可能去掉 合法返回true 否则返回false
			if(!cfg.selector){
				alert('没有提供表单项选择器或者表单项的jquery对象','error');
				return false;
			}
		},
		_position:function(){
			this.jq.css(this.input.css(['position','left','top','margin']));
		},
		_defaults:{//用户可能使用的默认配置
			direction:undefined,//下拉列表方向 可取dropdown、dropup
			selector:undefined,//表单项选择器或者表单项jquery对象
			change:undefined,//用户配置的change事件
			valueFiled:'id',//内部存储值字段
			labelFiled:'name',//界面展示值字段
			iconFiled:'icon',//选项图标字段
			filter:undefined,//本地或者远程数据过滤函数 参数为本地配置数据或远程加载的数据 需返回一个数组
			data:[],//本地数据源
			href:undefined,//远程数据源 本地和远程都有时 使用远程数据源
			cache:true,//是否缓存数据源
			placeholder:'${oc.local.ui.select.placeholder}'
		},
		_init:function(cfg){//控件初始化
			var jq=this.jq=this._dom.div.clone(),//获取文档对象不能频繁使用‘ $ ’符号
				selectedSpan=this.selectedSpan=jq.find('span.sr-only'),
				ul=this._ul=jq.find('> ul');
		},
		load:function(data){
			this.cfg.filter&&(data=this.cfg.filter(data));
			$.isArray(data)||(data=[]);
			for(var i=0,len=data.length,li=this._dom.li,l,d,ul=this._ul;i<len;i++){
				d=data[i];
				l=li.clone();
			}
		}
	};
	
	$.fn.extend({
		select:function(cfg){
			return new select(cfg);
//			return $(this).data('oc-select',new select(cfg));
		}
	});
	$('div').select({});
	oc.ui.select=function(cfg){
		return new select(cfg);
	};
	oc.ui.select({});
})(jQuery);