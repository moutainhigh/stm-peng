/**
 * 对话框
 * @author ziwenwen
 * @param $
 */
(function($){
	function dlg(cfg){
		cfg=this.cfg=$.extend(true,{},this._defaults,cfg);
		this.id=oc.util.generateId();
		this._init();
		if(cfg.autoOpen)this.show();
	}
	
	dlg.prototype={
		constructor:dlg,
		id:null,
		cfg:null,
		dlg:null,
		header:null,
		body:null,
		footer:null,
		bottomBar:null,
		show:function(){
			this.dlg.modal('show');
		},
		hide:function(){
			this.dlg.modal('hide');
		},
		destroy:function(){
			this.dlg.modal('hide').remove();
		},
		_defaults:{//默认配置
			closeText:undefined,
			closeBtnCfg:{
				disabled:false,
				click:function(e){
				}
			},
			bottomBar:{
				right:[],
				btn:{
					disabled:true
				}
			},//底部工具条配置，配置方式详见oc.ui.toolbar控件
			width:'500',
			height:'auto',
			maxWidth:'960',
			maxHeight:'593',
			minWidth:'350',
			minHeight:'216',
//			container:null,//dialog所在的容器，默认为body
			autoOpen:true,//初始化后是否默认展示
//			draggable:true,//支持拖动
//			resizable:true,//支持尺寸可变
			modal:false,//是否模态
			buttons:null,//按钮，支持数组配置、object对象配置，jquery选择器配置、jquery对象配置
			title:undefined,//标题,不配置不显示
//			position:{my:"center center",at:"center center",of:window},
			content:undefined,//内容，与href至少配置一个
			href:undefined,//内容链接，与content至少配置一个，同时配置时href优先使用
			loaded:undefined//使用href方式加载dialog内容完成时回调函数，回传参数为弹出框主体内容部分
		},
		_dom:{
			dlg:$('<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">'+//aria-labelledby
					'<div class="modal-dialog"><div class="modal-content"><div class="modal-body"></div></div></div></div>'),
			header:$('<div class="modal-header">'+
						'<button type="button" class="close" data-dismiss="modal">'+
							'<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>'+
						'</button>'+
						'<h4 class="modal-title" id="myModalLabel"></h4>'+
					'</div>'),
			footer:$('<div class="modal-footer"></div>'),
			closeButton:'<button type="button" class="btn btn-default" data-dismiss="modal"></button>'
		},
		_init:function(){
			var dom=this._dom,dlg=this.dlg=dom.dlg.clone(),body=this.body=dlg.find('.modal-body'),cfg=this.cfg,bottomBar=cfg.bottomBar;
			
			if(cfg.title){
				var h=this.header=dom.header.clone(),hId=oc.util.generateId();
				h.find('.modal-title').attr('id',hId).append(cfg.title);
				dlg.attr('aria-labelledby',hId);
				body.before(h);
			}
			
			if(cfg.closeText){
				var closeBtn=$.extend({},cfg.closeBtnCfg);
				closeBtn.text=cfg.closeText;
				bottomBar.right.push(closeBtn);
			}
			
			if((bottomBar.left&&bottomBar.left.length)||bottomBar.right.length){
				this.footer=dom.footer.clone();
				this.footer.append(this.bottomBar);
				bottomBar.container=this.footer;
				this.bottomBar=oc.ui.toolbar(bottomBar);
				body.after(this.footer);
			}
			
			if(cfg.href){
				body.load(cfg.href,function(){
					cfg.loaded&&cfg.loaded(body);
					if(this.bottomBar){
						this.bottomBar.enable();
					}
				});
			}else{
				body.append(cfg.contend);
				if(this.bottomBar){
					this.bottomBar.enable();
				}
			}
			
			$(document.body).append(dlg);
		}
	};
	
	oc.ui.dlg=function(cfg){
		return new dlg(cfg);
	};
	
	$.fn.extend({
		dlg:function(cfg){
			return new dlg(cfg);
		}
	});
})(jQuery);