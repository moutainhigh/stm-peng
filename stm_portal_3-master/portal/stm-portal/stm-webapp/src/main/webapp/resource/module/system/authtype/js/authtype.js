(function($) {
	$('#saveAuth').linkbutton("RenderLB",{
		iconCls : 'fa fa-check-circle'
	});
	var authForm,auForm = $("#authForm");
	var authtype=$("#oc_system_authtype");
	authForm = oc.ui.form({
		selector:auForm
	});
	
	var getAuth = function(){
		oc.util.ajax({
			url:oc.resource.getUrl("system/authentication/get.htm"),
			async:false,
			success:function(data){
				var d = data.data;
				authForm.val(d);
				if(!d.encryptionMode){
					auForm.find("input[name=encryptionMode]:first").attr("checked","checked");
				}
			},
			successMsg:""
		});
	};
	
	authtype.find("#saveAuth").click(function(){
		var check=$("input[name='auMode']").val();
		if(check==1){
			authForm.enable();
	    	authForm.enableValidation();
		}
		if(authForm.validate()){
			oc.util.ajax({
				url:oc.resource.getUrl("system/authentication/insert.htm"),
				data:authForm.val(),
				successMsg:"设置成功！"
			});
		}
	});
	
	getAuth();
	var check=$("input[name='auMode']").val();
	if(check==0){
		$("#auModeOut1").attr("checked", true);
		 authForm.disable();
		 authForm.disableValidation();
		 auForm.find("input[name=encryptionMode]").each(function(){
    		 $(this).attr("disabled",true);
    	 });
	}
	else{
		$("#auModeOut2").attr("checked", true);
		authForm.enable();
		authForm.enableValidation();
		auForm.find("input[name=encryptionMode]").each(function(){
   		 $(this).attr("disabled",false);
   	 });
	}
	
	$("input[name='auModeOut']").val($("input[name='auMode']").val());
    $("#auModeOut2").change(function(){
    	authForm.enable();
    	authForm.enableValidation();
    	auForm.find("input[name=encryptionMode]").each(function(){
    		$(this).attr("disabled",false);
   	 	});
        $("input[name='auMode']").val("1");
        authForm.find("input[name=ip]").focus().blur();
    });
     $("#auModeOut1").change(function(){
    	 authForm.disable();
    	 auForm.find("input[name=encryptionMode]").each(function(){
    		 $(this).attr("disabled",true);
    	 });
    	 authForm.disableValidation();
        $("input[name='auMode']").val("0");
    }) ;
})(jQuery);