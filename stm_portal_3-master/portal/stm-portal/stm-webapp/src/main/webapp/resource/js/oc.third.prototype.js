/**
 * 对第三方插件默认行为的改造（本js文件可能不会使用）
 * @author ziwenwen
 */
(function(){
//	$.fn.ocload=$.fn.load;
//	$.fn.load=function(u,d,fn){
//		oc.ui.progress();
//		var t=this;
//		setTimeout(function(){
//			t.ocload(u,d,function(){
//				$.messager.progress('close');
//				fn&&fn();
//			});
//		});
//	};
	$.fn.linkbutton.defaults.plain=true;
	
	//*********************  dialog begin ******************
	$.extend($.fn.dialog.defaults,{
		width:750,
		height:465,
		resizable:false,
		cache:true,
		modal:true,
		onClose:function(){
			$(this).dialog('destroy');
		}
	});
	//*********************  dialog end ******************

	$.extend($.fn.tabs.defaults,{
		fit:true,
		border:true
	});
	$.extend($.fn.accordion.defaults,{
		onBeforeSelect:function(t,i){
			this.selectedIdx=i;
		},
		onBeforeUnSelect:function(t,i){
			if((typeof this.selectedIdx=='number')&&i==this.selectedIdx){
				return false;
			}
		}
	});
	
	oc.ui.progress=function(cfg){
		$.messager.progress({text:''});
	};
	
	$.fn.accordion.defaults.fit=true;

//	var dateboxSetText=$.fn.datebox.methods.setText;
//	$.fn.datebox.methods.setText=function(jq, _6d) {	
//		var _6c=jq.datebox('getValues');
//		if($.isArray(_6c)&&_6c.length==1){	
//			if(!_6c[0].toString().trim()){
//				var place=jq.datebox('options').placeholder;
//				if(place)_6d=place.toI18n();
//			}
//		}
//		return dateboxSetText(jq,_6d);
//	};
//	
//	var gridSetValues=$.fn.combo.methods.setValues;
//	$.fn.combo.methods.setValues=function(jq, _6d) {	
//		if($.isArray(_6d)&&_6d.length==1){	
//			var val=_6d[0],type=typeof val;
//			if(type=='undefined'){
//				return jq;
//			}else if(type=='string'){
//				if(!val.trim())return jq;
//			}
//		}
//		return gridSetValues(jq,_6d);
//	};
	
	$.fn.datebox.defaults.buttons.splice(1, 1, {
		text: '清除',
		handler: function(jq){
			jq=$(jq).datebox('clear').datebox('hidePanel');
			var place=jq.datebox('options').placeholder;
			if(place){
				jq.datebox('setText',place.toI18n());
			}
		}
	});
	
	$.fn.datetimebox.defaults.buttons.splice(2, 1, {
		text: '清除',
		handler: function(jq){
			jq=$(jq).datetimebox('clear').datetimebox('hidePanel');
			var place=jq.datetimebox('options').placeholder;
			if(place){
				jq.datetimebox('setText',place.toI18n());
			}
		}
	});
	var comboClear=$.fn.combo.methods.clear;
	$.fn.combo.methods.clear=function(jq){
		comboClear(jq);
		var place=jq.combo('options').placeholder;
		if(place)comboSetText(jq,place.toI18n());
		return jq;
	};

	oc.ui.confirm=function(msg,ok,cancel){
		$.messager.confirm(oc.local.ui.confirm.title,msg, function(r){
			if(r){
	    		ok&&ok();
			}else{
	    		cancel&&cancel();
			}
		});
	};

	$.ajaxSetup({
		beforeSend:function(r,s){
			if(s.data){
				s.data+='&dataType='+s.dataType;
			}else{
				s.data='dataType='+s.dataType;
			}
		},complete:function(obj,status){
			if(status=='success'){
				try {
					obj=JSON.parse(obj.responseText);
					if(obj.code==400){
						top.location.href=obj.data;
					}
				} catch (e) {
				}
			}
		}
	});
	

	$.extend($.fn.treegrid.defaults.view,{
		_onAfterRender:$.fn.treegrid.defaults.view.onAfterRender,
		onAfterRender:function(target){
			var datagrid=$(target),opts=datagrid.treegrid('options'),selectedCfg=opts.selectedCfg;
			if(selectedCfg){
				var sField=selectedCfg.field,sFieldValue=selectedCfg.fieldValue,idField=opts.idField,
				rows=datagrid.treegrid('getData');
				opts.view._onAfterRender(target);
				for(var i=0,len=rows.length;i<len;i++){
					var children = rows[i].children;
					for(var j in children){
						if(children[j][sField]==sFieldValue)datagrid.treegrid('select',children[j][idField]);
					}
				}
			}
		}
	});
	
	$.fn.filebox.defaults.buttonText='...';
	
/*********************  linkButton begin ******************/
	$.extend($.fn.linkbutton.methods,{
			RenderLB:function(jq,obj){
			return jq.each(function(){
				var $this = $(this);
				var	$left,
					_html;
				obj = obj||{};
				$this.linkbutton(obj);
				$left = $this.find('.l-btn-left');
				_html=$('<div class="btn-l">'+
				'  <div class="btn-r">'+
				'    <div class="btn-m"></div>'+
				'  </div>'+
				'</div>');
				_html.find('.btn-m').append($left.children());
				$left.append(_html);
			});
		}
	});
/*********************  linkButton end ******************/
	$.fn.panel.defaults.loadingMessage='努力加载中...';
	$.extend($.fn.panel.methods,{
		RenderP:function(jq,obj){
		return jq.each(function(){
			var $this = $(this);
			var	$left,
				_html;
			obj = obj||{};
			$this.panel(obj);
			$left = $this.parent().find('.panel-header');
			_html=$('<div class="oc-header-r"><div class="oc-header-m"></div></div>'),
			btn_m=_html.find('.oc-header-m').append($left.children());
			$left.append(_html);
		});
	}
});
	$.extend($.fn.dialog.defaults,{
		onOpen:function(){
			var that=$(this),_html,opts=that.dialog('options');
			opts._onOpen&&opts._onOpen();
			if(opts.isOcOpened)return;
			opts.isOcOpened=true;
			setTimeout(function(){
				that.parent('.panel').find('.panel-header').each(function(){
					_html=$('<div class="oc-header-r"><div class="oc-header-m"></div></div>'),
					btn_m=_html.find('.oc-header-m').append($(this).children());
					$(this).append(_html);
				});
				that.parent('.panel').find('.dialog-button .l-btn-left').each(function(){
					_html=$('<div class="btn-l">'+
							'  <div class="btn-r">'+
							'    <div class="btn-m"></div>'+
							'  </div>'+
							'</div>'),
							btn_m=_html.find('.btn-m').append($(this).children());
							$(this).append(_html);
				});
			},1);
		}
	});	
	
	$.extend($.fn.window.defaults,{
		onOpen:function(){
			var that=$(this);
			setTimeout(function(){
				that.parent('.panel').find('.panel-header').each(function(){
					_html=$('<div class="oc-header-r"><div class="oc-header-m"></div></div>'),
					btn_m=_html.find('.oc-header-m').append($(this).children());
					$(this).append(_html);
				});
				that.parent('.panel').find('.messager-button .l-btn-left').each(function(){
					_html=$('<div class="btn-l">'+
					'  <div class="btn-r">'+
					'    <div class="btn-m"></div>'+
					'  </div>'+
					'</div>'),
					btn_m=_html.find('.btn-m').append($(this).children());
					$(this).append(_html);
				});
			},1);
		}
	});
})();