(function(){
	function itsm(){
		this.initDom();
		this.initData();
		this.registerEvent();
	}
	
	itsm.prototype={
		constructor:itsm,
		data:0,
		initDom:function(){
			this.id=oc.util.generateId();
			this.dom=$('#systemItsm').attr('id',this.id);
			this.isOpenDom=this.dom.find('.isOpen:first span');
			this.form=oc.ui.form({
				selector:this.dom
			});
			
			this.webServiceDom=$('#systemWebservice').attr('id',this.id);
			this.webServiceForm=oc.ui.form({
				selector:this.webServiceDom
			});
			
		},
		registerEvent:function(){//itsm将应用合并
			var t=this;
		/*	t.dom.find('.apply').linkbutton('RenderLB',{
				text:'应用',
				onClick:function(){
					t.save();
				}
			});*/
			
			t.webServiceDom.find('.webServiceApply').linkbutton('RenderLB',{
				text:'应用',
				iconCls:"fa fa-check-circle",
				onClick:function(){
					t.save();
					var codeValue = t.webServiceForm.find("input[name='CODE']").val();
					var urlValue = t.webServiceForm.find("input[name='URL']").val();
					var levelValue = t.webServiceForm.find("input[name='LEVEL']:checked");
					if(codeValue == null || codeValue == "" || urlValue == null || urlValue == "") {
						alert("CODE或URL不能为空!");
						return;
					}
					if(codeValue.length > 10) {
						alert("CODE不能超过10位!");
						return;
					}
					if(levelValue.length < 1){
						alert("请选择LEVEL!");
						return;
					}
					t.saveWebservice();
				}
			});
			
			this.isOpenDom.click(function(){
				t.setOpen(!t.isOpenDom.is('.open'));
				t.save();
				t.saveWebservice();
			});
		},
		save:function(){
			var t=this,d=t.data,form=t.form,port=$('input[name="port"]').val();
			if(port==""){
				port=0;
			}
			if(d.open=t.isOpenDom.is('.open')){
				if(!form.validate())return;
				$.extend(d,form.val());
			}
			oc.util.ajax({
				url:oc.resource.getUrl('system/itsm/save.htm'),
				data:{itsmData:d,port:port},
				success:function(data){
					if(data.code == 200){
						oc.index.loadRights();
					}
				},
				successMsg:'操作成功！'
			});
		},
		saveWebservice:function(){
			var t=this,d=t.data,form=t.webServiceForm;
			if(!form.validate()) {
				return;
			}
			var formData = form.val();
			$.extend(formData,{"AVAILABLE":d.open});
			oc.util.ajax({
				url:oc.resource.getUrl('system/itsm/saveWebService.htm'),
				data:{webServiceData:formData},
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
				url:oc.resource.getUrl('system/itsm/get.htm'),
				success:function(d){
					t.data=d.data;
					t.setOpen(t.data.open);
					t.form.val(d.data);
					if(d.data.port==0){
						$("input[name='port']").val("");
					}
					
				}
			});
			
			oc.util.ajax({
				url:oc.resource.getUrl('system/itsm/getWebService.htm'),
				success:function(data){
					var dataset = $.parseJSON(data.data);
					var level = dataset.LEVEL;
					dataset.LEVEL=level&&level.split(",");
					t.webServiceForm.val(dataset);
				}
			});
		},
		setOpen:function(open){
			this.isOpenDom.removeClass('open close');
			if(open){
				this.isOpenDom.addClass('open');
				this.form.selector.fadeIn();
				this.webServiceForm.selector.fadeIn();
			}else{
				this.isOpenDom.addClass('close');
				this.form.selector.fadeOut();
				this.webServiceForm.selector.fadeOut();
			}
		}
	};
	
	new itsm();
})();