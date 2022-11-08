(function(){
	function pager(cfg){
		this._cfg=$.extend({},this.defaults,cfg);
		this._init();
	}
	
	pager.prototype={
		constructor:pager,
		_dom:0,
		_first:0,
		_tail:0,
		_prev:0,
		_next:0,
		_num:$('<span class="oc-num"></span>'),
		_cfg:0,
		_page:{
			maxPages:0,
			pageCount:0,
			currentPage:1,//从1开始 当前页
			allPage:0,//从1开始
			total:0
		},
		defaults:{
			dom:{
				main:'<div class="oc-pager"></div>',
				prev:'',
				next:'',
				num:'',
				first:0,
				tail:0
			},
			cssN:{
				active:'active',
				disable:'disable'
			},
			maxPages:5,//数字部分最打的标签页数
			pageCount:10,//每页显示的个数
			showFirst:0,
			showTail:0,
			showPrev:1,
			showNext:1
		},
		_init:function(){
			var cfg=this._cfg,dom=this._dom=$(cfg.dom.main),num=this._num,page=this._page;
			
			page.maxPages=cfg.maxPages;
			page.pageCount=cfg.pageCount;
			
			if(cfg.showFirst){
				dom.append(this._first=$(cfg.dom.first));
			}
			if(cfg.showPrev){
				dom.append(this._prev=$(cfg.dom.prev));
			}
			dom.append(num);
			
		},
		_caculate:function(page){
			var total=page.total,count=page.pageCount,di=total/count,re=total%count;
			page.allPage=di+1+((re==0)?-1:0);
			if(page.currentPage>page.allPage){
				page.currentPage=page.allPage;
			}
		},
		_renderBtn:function(){
			var begin,end,page=this._page,currentPage=page.currentPage,allPage=page.allPage;
		},
		onClick:function(){
			
		},
		load:function(length){
		}
	};
})();