/**
 * 
 */
(function($) {
	var myForm,form=$("#accessControlForm");
	myForm = oc.ui.form({
		selector:form
	});
	
	var loadForm = function(){

		oc.util.ajax({
			url:oc.resource.getUrl("system/accesscontrol/getAccessControl.htm"),
			async:false,
			success:function(data){
                // console.log(data.data);
				var accvo = data.data;
				if(accvo){
					form.find("input[type='radio']").each(function(){
						if($(this).val()==accvo.accessType){
							$(this).prop("checked", true);
						}
					});
					if(accvo.isEnable || accvo.isEnable == true || accvo.isEnable=="true"){
						form.find("[name=isEnable]").prop("checked",true);
						disabledForm(false);
					}else{
						form.find("[name=isEnable]").prop("checked",false);
						disabledForm(true);
					}
					form.find("[name=allow]").val(accvo.allow);
					form.find("[name=notAllowed]").val(accvo.notAllowed);
					// 登录失败次数限制
					if(accvo.loginFailTimeIsEnable){
						form.find("[name=loginFailTimeIsEnable]").prop("checked", true);
						form.find("[name=loginFailTime]").prop('disabled', false);
						form.find("[name=loginDeblockIsEnable]").prop('disabled', false);
					}else{
						form.find("[name=loginFailTimeIsEnable]").prop("checked", false);
						form.find("[name=loginFailTime]").prop('disabled', true);
						form.find("[name=loginDeblockIsEnable]").prop("checked", false);
						form.find("[name=loginDeblockIsEnable]").prop('disabled', true);
						form.find("[name=loginDeblockMinutes]").prop('disabled', true);
					}
					form.find("[name=loginFailTime]").val(accvo.loginFailTime);
					// 自动解锁
					if(accvo.loginDeblockIsEnable){
						form.find("[name=loginDeblockIsEnable]").prop("checked", true);
						form.find("[name=loginDeblockMinutes]").prop('disabled', false);
					}else{
						form.find("[name=loginDeblockIsEnable]").prop("checked", false);
						form.find("[name=loginDeblockMinutes]").prop('disabled', true);
					}
					form.find("[name=loginDeblockMinutes]").val(accvo.loginDeblockMinutes);
					// 强制要求更换密码
					if(accvo.loginPassValidityIsEnable){
						form.find("[name=loginPassValidityIsEnable]").prop("checked", true);
						form.find("[name=loginPassValidityDays]").prop('disabled', false);
						form.find("[name=loginPassValidityAlertDays]").prop('disabled', false);
					}else{
						form.find("[name=loginPassValidityIsEnable]").prop("checked", false);
						form.find("[name=loginPassValidityDays]").prop('disabled', true);
						form.find("[name=loginPassValidityAlertDays]").prop('disabled', true);
					}
					form.find("[name=loginPassValidityDays]").val(accvo.loginPassValidityDays);
					form.find("[name=loginPassValidityAlertDays]").val(accvo.loginPassValidityAlertDays);
				}
			},
			successMsg:""
		});
	};
	loadForm();
	
	function disabledForm(isDisabled){
		form.find("input[type='radio']").each(function(){
			$(this).prop("disabled", isDisabled);
		});
		form.find("textarea").each(function(){
			$(this).prop("disabled", isDisabled);
		});
		if(!isDisabled){
			var type;
			form.find("input[name=accessType]").each(function(){
				if($(this).prop("checked") || $(this).prop("checked")){
					type = $(this).val();
				}
			});
			if(type=="whiteList"){
				form.find("textarea[name=notAllowed]").prop("disabled", true);
				form.find("textarea[name=allow]").prop("disabled", false);
			}else if(type=="blackList"){
				form.find("textarea[name=allow]").prop("disabled", true);
				form.find("textarea[name=notAllowed]").prop("disabled", false);
			}
		}
	};
	
	form.find("[name=isEnable]").change(function(){
		var isEnable = form.find("[name=isEnable]").prop("checked");
		if(isEnable){
			form.find("[name=isEnable]").prop("checked",true);
			disabledForm(false);
		}else{
			form.find("[name=isEnable]").prop("checked",false);
			disabledForm(true);
		}
	});
	
	form.find("[name=accessType]").bind("change",function(){
		var value = $(this).val();
		if(myForm.validate()){
			if(value=="whiteList"){
				form.find("textarea[name=notAllowed]").prop("disabled", true);
				form.find("textarea[name=allow]").prop("disabled", false);
			}else if(value=="blackList"){
				form.find("textarea[name=allow]").prop("disabled", true);
				form.find("textarea[name=notAllowed]").prop("disabled", false);
			}
		}else{
			$(this).attr("checked",false);
			if(value=="whiteList"){
				form.find("#blackList").prop("checked",true);
			}else if(value=="blackList"){
				form.find("#whiteList").prop("checked",true);
			}
			return false;
		}
	});
	form.find("[name=loginFailTimeIsEnable]").bind("change",function(){
		
		var isEnable = form.find("[name=loginFailTimeIsEnable]").prop("checked");
		if(isEnable){
			form.find("[name=loginFailTime]").prop('disabled', false);
			form.find("[name=loginDeblockIsEnable]").prop('disabled', false);
		}else{
			form.find("[name=loginFailTime]").prop('disabled', true);
			form.find("[name=loginDeblockIsEnable]").prop("checked", false);
			form.find("[name=loginDeblockIsEnable]").prop('disabled', true);
			form.find("[name=loginDeblockMinutes]").prop('disabled', true);
		}
	});
	form.find("[name=loginDeblockIsEnable]").bind("change",function(){
		var isEnable = form.find("[name=loginDeblockIsEnable]").prop("checked");
		if(isEnable){
			form.find("[name=loginDeblockMinutes]").prop('disabled', false);
		}else{
			form.find("[name=loginDeblockMinutes]").prop('disabled', true);
		}
	});
	form.find("[name=loginPassValidityIsEnable]").bind("change",function(){
		var isEnable = form.find("[name=loginPassValidityIsEnable]").prop("checked");
		if(isEnable){
			form.find("[name=loginPassValidityDays]").prop('disabled', false);
			form.find("[name=loginPassValidityAlertDays]").prop('disabled', false);
		}else{
			form.find("[name=loginPassValidityDays]").prop('disabled', true);
			form.find("[name=loginPassValidityAlertDays]").prop('disabled', true);
		}
	});
	
	form.find("#submitForm").linkbutton('RenderLB', {
		iconCls : 'fa fa-check-circle',
		onClick : function(){
			if(myForm.validate()){
				form.find("input[type='radio']").prop("disabled", false)
				form.find("textarea").prop("disabled", false)
				var values = myForm.val();
				var isEnable = form.find("[name=isEnable]").prop("checked");
				if(isEnable){
					values.isEnable = true;
				}else{
					values.allow==null?"":values.allow;
					values.notAllowed==null?"":values.notAllowed;
					values.isEnable = false;
				}
				values.accessType =form.find("input[name='accessType']:checked").val();
				disabledForm(true);
				
				isEnable = form.find("[name=loginFailTimeIsEnable]").prop("checked");
				if(isEnable){
					values.loginFailTimeIsEnable = true;
				}else{
					values.loginFailTimeIsEnable = false;
				}
				
				isEnable = form.find("[name=loginDeblockIsEnable]").prop("checked");
				if(isEnable){
					values.loginDeblockIsEnable = true;
				}else{
					values.loginDeblockIsEnable = false;
				}
				
				isEnable = form.find("[name=loginPassValidityIsEnable]").prop("checked");
				if(isEnable){
					values.loginPassValidityIsEnable = true;
				}else{
					values.loginPassValidityIsEnable = false;
				}
				
				oc.util.ajax({
					url:oc.resource.getUrl('system/accesscontrol/updateAccessControl.htm'),
					data:values,
					success:function(data){
						loadForm();
					},
					successMsg:"设置成功！"
				});
			}
		}
	});
})(jQuery);