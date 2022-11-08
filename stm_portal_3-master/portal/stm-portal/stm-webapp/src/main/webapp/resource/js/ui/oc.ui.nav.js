(function($){
	function nav(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
		if(typeof cfg.selector=='string')cfg.selector=$(cfg.selector);
		if(this._checkCfg(cfg)){
			this._init(cfg);
			this.jq.appendTo(cfg.selector);
			this._registPage();
		}
	}
	
	nav.prototype={
		constructor:nav,
		id:undefined,//每个组件都可以有一个随机id
		jq:undefined,//所有ui组件需提供该对象，用于表示ui组件最顶层文档元素对应的jquery对象
		cfg:undefined,//组件渲染时所使用的配置 简写能够很容易表达其意的时候使用简写
		_ul:undefined,
		_up:0,
		_down:0,
		_dom:{
			div:$(
				'<div class="oc-nav">'+
					'<ul class="nav-list" style="top:0px;"></ul>'+
					'<div class="nav-page">'+
						'<span class="page-up test_up"></span> '+
						'<span class="page-down test_down"></span>'+
					'</div>'+
				'</div>'),
//			 li:$('<li><a href="#"></a></li>'),
			 li:$('<li>'+
		    	'	<div class="main-box-icon">'+
	            '     	<span class="oc-panel-pic"><img/></span>' + 
	            '		<span class="oc-panel-font"></span>' +
	            '	</div>'+
		        '</li>')
		},
		_checkCfg:function(cfg){//此函数必须提供，用于检测用户的基本配置是否合法 线上环境可能去掉 合法返回true 否则返回false
			if(!cfg.selector){
				alert('没有提供表单项选择器或者表单项的jquery对象','error');
				return false;
			}
			return true;
		},
		_active:0,
		_defaults:{//用户可能使用的默认配置
			selector:undefined,//表单项选择器或者表单项jquery对象
			textFiled:'name',//内部存储值字段
			hrefFiled:'href',//界面展示值字段
			iconFiled:'icon',//选项图标字段
			filter:undefined,//本地或者远程数据过滤函数 参数为本地配置数据或远程加载的数据 需返回一个数组
			datas:[],//本地数据源
			click:$.noop,//单击菜单触发事件 返回true成功激活，返回false激活失败
			href:undefined,//远程数据源 本地和远程都有时 使用远程数据源
			background : false
		},
		_registPage:function(){
			var ul=this._ul,top=parseInt(ul.css('top')),height=parseInt(ul.height()),that=this;
			this._up.addClass('disabled').off('click');
			this._down.addClass('disabled').off('click');
			if(top!=0){
				this._up.removeClass('disabled').on('click',function(){
					that._upDown(true);
				});
			}
			
			if((top+height)>153){
				this._down.removeClass('disabled').on('click',function(){
					that._upDown();
				});
			}
		},
		_upDown:function(isUp){
			var ul=this._ul,top=parseInt(ul.css('top')),that=this;
			this._up.addClass('disabled').off('click');
			this._down.addClass('disabled').off('click');
			if(isUp){//上移
				ul.animate({top:(top+77)+'px'},300,function(){
					that._registPage();
				});
			}else{
				ul.animate({top:(top-77)+'px'},300,function(){
					that._registPage();
				});
			}
			
		},
		_init:function(cfg){//控件初始化
			var jq=this.jq=this._dom.div.clone();//获取文档对象不能频繁使用‘ $ ’符号
			this._ul=jq.find('> ul');
			this._up=jq.find('span.page-up');
			this._down=jq.find('span.page-down');
			if(cfg.href){
				this.reLoad(cfg.href);
			}else if(cfg.datas){
				this.load(cfg.datas);
			}
		},
		highLight:function(id){
			var li=this._ul.find(['data-id='+id]);
			if(li.length>0){
				li.addClass('active').siblings().removeClass('active');
			}
		},
		_liObj:{},
		active:function(id){
			this._liObj[id].click();
		},
		_beforeClick:{},
		setBeforeClick:function(id,fn){
			this._beforeClick[id]=fn;
		},
		load:function(data){
			this.cfg.filter&&(data=this.cfg.filter(data));
			$.isArray(data)||(data=[]);
			var that=this,li=this._dom.li,l,ul=this._ul.empty(),d,
				icon=this.cfg.iconField,href=this.cfg.hrefFiled,text=this.cfg.textField;
			for(var i=0,len=data.length;i<len;i++){
				d=data[i];
				l=li.clone().attr('data-id',d.id).children('div').addClass('test_nav_'+d.id).end();
				
				l.click((function(h,d,data,l,ul){
					return function(e){
						e.preventDefault();
						if(that._active){
							var cId=that._active.data('data-id'),tId=d.id,fn=that._beforeClick[cId];
							if(fn){
								if(!fn(tId))return;
							}
						}
						if(that.cfg.click(h,d,data)){
							ul.find('li').removeClass('active');
							l.addClass('active');
							that._active=l;
						};
					};
				}(d[href],d,data,l,ul))).find('.oc-panel-font').addClass(d[icon]).text(d[text]);
				this.cfg.background&&l.find('.oc-panel-pic img').attr('src',d.imgUrl);
				this._liObj[d.id]=l.appendTo(ul);
			}
			this._registPage();
			if(that._active){
				that._active.addClass('active').siblings().removeClass('active');
			}
		},
		reLoad:function(href){
			var that=this;
			oc.util.ajax({
				url:href,
				success:function(data){
					that.cfg.datas=data;
					that.load(data);
				}
			});
		}
	};
	
	$.fn.extend({
		oc_nav:function(cfg){
			cfg.selector=$(this);
			return new nav(cfg);
		}
	});
})(jQuery);