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
			this.dom=$('#systemWebservice').attr('id',this.id);
			this.form=oc.ui.form({
				selector:this.dom
			});
		},
		registerEvent:function(){
			var t=this;
			t.dom.find('.webServiceApply').linkbutton('RenderLB',{
				text:'应用',
				onClick:function(){
					var codeValue = t.form.find("input[name='CODE']").val();
					var urlValue = t.form.find("input[name='URL']").val();
					if(codeValue == null || codeValue == "" || urlValue == null || urlValue == "") {
						alert("CODE或URL不能为空","danger");
						return;
					}
					if(codeValue.length > 10) {
						alert("CODE不能超过10位","danger");
						return;
					}
					var urlTest = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
					if(!urlTest.test(urlValue)){//如果url格式不正确
						alert("URL格式不正确","danger");
						return;
					}
					t.save();
				}
			});
		},
		save:function(){
			var t=this,form=t.form;
			oc.util.ajax({
				url:oc.resource.getUrl('system/itsm/saveWebService.htm'),
				data:{webServiceData:form.val()},
				successMsg:'配置成功！重新登陆后生效!'
			});
		},
		initData:function(){
			var t=this;
			oc.util.ajax({
				url:oc.resource.getUrl('system/itsm/getWebService.htm'),
				success:function(data){
					var dataset = $.parseJSON(data.data);
					t.form.val(dataset);
				}
			});
		}
	};
	
	new itsm();
})();