(function(){
	function cmdb(){
		this.initDom();
		this.initData();
		this.registerEvent();
	}
	
	cmdb.prototype={
		constructor:cmdb,
		data:0,
		initDom:function(){
			this.id=oc.util.generateId();
			this.webServiceDom=$('#cmdbWebservice').attr('id',this.id);
			this.isOpenDom=this.webServiceDom.find('.isOpen:first span');
			this.webServiceForm=oc.ui.form({
				selector:this.webServiceDom
			});
			
		},
		registerEvent:function(){
			var t=this;
			t.webServiceDom.find('.webServiceApply').linkbutton('RenderLB',{
				text:'应用',
				onClick:function(){
					var urlValue = t.webServiceForm.find("input[name='URL']").val();
					if(urlValue == null || urlValue == "") {
						alert("URL不能为空!");
						return;
					}
					t.saveWebservice();
				}
			});
			t.webServiceDom.find('.resourceSynchronize').linkbutton('RenderLB',{
				text:'手动同步资源',
				onClick:function(){
					oc.util.ajax({
						url:oc.resource.getUrl('cmdbWebservice/synchronize/synchronizeAllResource.htm'),
						success:function(data){
	     	    			if(data.code==200){
	     	    				alert(data.data);
	     	    			}
	     	    		}
					});
				}
			});
			this.isOpenDom.click(function(){
				t.setOpen(!t.isOpenDom.is('.open'));
				t.webServiceForm.val(t.data);
				t.saveWebservice();
			});
		},
		saveWebservice:function(){
			var t=this,d=t.data,form=t.webServiceForm;
			if(d.open=t.isOpenDom.is('.open')){
				if(!form.validate())return;
				$.extend(d,form.val());
			}
			oc.util.ajax({
				url:oc.resource.getUrl('system/cmdb/saveWebService.htm'),
				data:{webServiceData:d},
				success:function(data){
					if(data.code == 200){
						oc.index.loadRights();
					}
				},
				successMsg:'操作成功!'
			});
		},
		initData:function(){
			var t=this;
			oc.util.ajax({
				url:oc.resource.getUrl('system/cmdb/getWebService.htm'),
				success:function(data){
					var dataset = $.parseJSON(data.data);
					t.data = dataset;
					t.setOpen(dataset.open);
					if(t.isOpenDom.is('.open')){
						t.webServiceForm.val(dataset);
					}
				}
			});
		},
		setOpen:function(open){
			this.isOpenDom.removeClass('open close');
			if(open){
				this.isOpenDom.addClass('open');
				this.webServiceForm.selector.fadeIn();
			}else{
				this.isOpenDom.addClass('close');
				this.webServiceForm.selector.fadeOut();
			}
		}
	};
	
	new cmdb();
})();