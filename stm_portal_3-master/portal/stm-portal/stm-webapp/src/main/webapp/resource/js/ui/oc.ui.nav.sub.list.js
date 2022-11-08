(function($){
	function navsublist(cfg){
		cfg=this.cfg=$.extend({},this._defaults,cfg);
		this.ul=$(this._dom.ul);
		this.lis=[];
		if(cfg.url){
			this.reLoad(cfg.url);
		}else if(cfg.data){
			this.load(cfg.data);
		}
		if(cfg.selector){
			this.selector=(typeof cfg.selector=='string')?$(cfg.selector):cfg.selector;
			this.selector.append(this.ul);
		}
	}
	
	navsublist.prototype={
		constructor:navsublist,
		_activeL:undefined,
		_dom:{
			ul:'<ul class="oc-nav-sub-list"/>',
			li:'<li><span class="s-arrow-dot"></span><span class="text"></span></li>'
		},
		_defaults:{
			textField:'name',
			hrefField:'href',
			filter:function(ds){//远程数据过滤器
				return ds.data;
			},
			addRowed:undefined,//添加一个列表后的回调函数 两个参数分别为li的jquery对象、列表对应的json数据
			click:undefined//单击回调函数 三个参数分别为href、data、e
		},
		get:function(idx){
			return this.lis[idx];
		},
		remove:function(idx){
			this.lis[idx].remove();
		},
		removeAll:function(){
			if(!this.lis||this.lis.length<=0){
				return;
			}
			for(var i=0;i<this.lis.length;i++){
				this.lis[i].remove();
			}
			this.lis=new Array();
		},
		add:function(d){
			var that=this,l=$(this._dom.li),cfg=this.cfg,click=cfg.click,idx=this.lis.push(l);
			l.find('.text:first').html(d[cfg.textField]);
			l.click((function(h,dd,i){
				return function(e){
					if(that._activeL)that._activeL.removeClass('active');
					that._activeL=$(e.target).closest('li').addClass('active');
					click(h,dd,e,i);
				};
			})(d[cfg.hrefField],d,idx-1)).appendTo(this.ul);
			if(cfg.addRowed)cfg.addRowed(l,d,idx-1);
		},
		load:function(ds){
			this.ul.empty();
			this.data=ds;
			if($.isArray(ds)){
				for(var i=0,len=ds.length;i<len;i++){
					this.add(ds[i]);
				}
			}
		},
		reLoad:function(url){
			var that=this;
			oc.util.ajax({
				url:url,
				successMsg:null,
				failureMsg:null,
				success:function(ds){
					that.data=that.cfg.filter?that.cfg.filter(ds):ds;
					that.load(that.data);
				}
			});
		},
		setActiveLi:function(li){
			if(this._activeL)this._activeL.removeClass('active');
			this._activeL=li.addClass('active');
		},
		clearCurActiveLi:function(){
			if(this._activeL){
				this._activeL.removeClass('active');
				this._activeL=null;
			}
		}
	};
	
	oc.ui.navsublist=function(cfg){
		return new navsublist(cfg);
	};
})(jQuery);