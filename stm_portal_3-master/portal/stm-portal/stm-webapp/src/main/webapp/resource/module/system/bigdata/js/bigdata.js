(function(){
	function bigdata(){
		this.initDom();
		this.initData();
		this.registerEvent();
	}
	
	bigdata.prototype={
		constructor:bigdata,
		data:0,
		initDom:function(){
			this.id=oc.util.generateId();
			this.dom=$('#systemBigdata').attr('id',this.id);
			this.isOpenDom=this.dom.find('.isOpen:first span');
			this.form=oc.ui.form({
				selector:this.dom
			});
		},
		registerEvent:function(){
			var t=this;
			t.dom.find('.apply').linkbutton('RenderLB',{
				text:'应用',
				iconCls:'fa fa-check-circle',
				onClick:function(){
					t.save();
				}
			});
			this.isOpenDom.click(function(){
				t.setOpen(!t.isOpenDom.is('.open'));
				t.form.val(t.data);
				t.save();
			});
		},
		save:function(){
			var t=this,d=t.data,form=t.form;
			if(d.integrate=t.isOpenDom.is('.open')){
				if($("input[name='port']").val()==""||$("input[name='port']").val()==undefined){
					$("input[name='port']").val()=="9020";
				}
				if(!form.validate())return;
				$.extend(d,form.val());
			}
			oc.util.ajax({
				url:oc.resource.getUrl('system/bigdata/save.htm'),
				data:{bigdata:d},
				successMsg:'操作成功！'
			});
		},
		initData:function(){
			var t=this;
			oc.util.ajax({
				url:oc.resource.getUrl('system/bigdata/get.htm'),
				success:function(d){
					t.data=d.data;
					t.setOpen(t.data.integrate);
					t.data = d.data;
					if(t.isOpenDom.is('.open')){
						t.form.val(d.data);
					}
				}
			});
		},
		setOpen:function(integrate){
			this.isOpenDom.removeClass('open close');
			if(integrate){
				this.isOpenDom.addClass('open');
				
				this.form.selector.fadeIn();
			}else{
				this.isOpenDom.addClass('close');
				this.form.selector.fadeOut();
			}
		}
	};
	
	new bigdata();
})();