/**
 * 工具条的js方式封装
 * @author ziwenwen
 * @param $
 */
(function($){
	function toolbar(cfg){
		cfg=this.cfg=$.extend(true,{},this._defaults,cfg);
		this._init();
	}
	
	toolbar.prototype={
		constructor:toolbar,
		cfg:undefined,//组件配置信息
		jq:undefined,
		enable:function(idx){//启用一个或者全部按钮
			var btns=this.btns,len=btns.length;
			if(typeof idx=='number'){
				if(idx<0)idx=0;
				if(idx>len-1)return;
				btns[idx].attr('disabled',false);
			}else{
				for(var i=0;i<len;i++){
					btns[i].attr('disabled',false);
				}
			}
		},
		disable:function(idx){//禁用一个或者全部按钮
			var btns=this.btns,len=btns.length;
			if(typeof idx=='number'){
				if(idx<0)idx=0;
				if(idx>len-1)return;
				btns[idx].attr('disabled',true);
			}else{
				for(var i=0;i<len;i++){
					btns[i].attr('disabled',true);
				}
			}
		},
		_dom:{
			toolbar:'<div class="oc-toolbar"><div class="clear"/></div>',
			leftClass:'left',
			rightClass:'right'
		},
		_defaults:{
			left:[],//左对齐按钮集合，为一个数组对象
			right:[],//右对齐按钮集合，为一个数组对象
			selector:undefined,//工具条所在容器
			btn:{
				id:0,
				onClick:undefined//单击绑定事件
			}
		},
		_init:function(){
			var t=this.jq=$(this._dom.toolbar),cfg=this.cfg,clear=t.find('div.clear');
			if(cfg.left&&$.isArray(cfg.left)){
				for(var i=0,left=cfg.left,len=left.length,lCls=this._dom.leftClass,o;i<len;i++){
					o=this._generateObj(left[i],lCls);
					o&&clear.before(o);
				}
			}
			
			if(cfg.right&&$.isArray(cfg.right)){
				for(var i=0,right=cfg.right,len=right.length,rCls=this._dom.rightClass,o;i<len;i++){
					o=this._generateObj(right[i],rCls);
					o&&(clear=o.insertBefore(clear));
				}
			}
			
			if(cfg.selector){
				if(typeof cfg.selector=='string')cfg.selector=$(cfg.selector);
				cfg.selector.append(t);
			}
		},
		_generateObj:function(o,dCls){
			if(o){
				var div=$('<div><a/><div/>').addClass(dCls),a;
				if($.isPlainObject(o)){
					o=$.extend({},this._defaults,o);
					a=$('<a '+(o.id?('id="'+o.id+'"'):'')+'/>').appendTo(div).linkbutton('RenderLB',o);
					if(a.onClick){
						a.click(a.onClick);
					}
				}else{
					div.append(o);
				}
				return div;
			}
		}
	};
	
	oc.ui.toolbar=function(cfg){
		return new toolbar(cfg);
	};
})(jQuery);