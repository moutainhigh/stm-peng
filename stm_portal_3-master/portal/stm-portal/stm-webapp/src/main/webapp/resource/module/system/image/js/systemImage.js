(function($){
//	$('.system-images').panel('RenderP');
	var imageform,form = $("#systemImageForm"),defauleInfo={};
	var skin=Highcharts.theme.currentSkin;
	imageform = oc.ui.form({
		selector:form
	});
	form.find(".file-button").linkbutton("RenderLB",{
		iconCls : 'icon-browse'
	});
//	form.find(".file-button-button").linkbutton("RenderLB");
	
	function loadForm(){
		oc.util.ajax({
			url:oc.resource.getUrl("system/image/getImageConfig.htm"),
			async:false,
			success:function(data){
				console.log(data);
				var image = data.data;
				if(image){
					image.defaultCopyRight = replaceCopy(image.defaultCopyRight);
					if(image.currentCopyRight)image.currentCopyRight=replaceCopy(image.currentCopyRight);
					imageform.val(image);
					defauleInfo.systemDefaultLogo = image.systemDefaultLogo;
					defauleInfo.loginDefaultLogo = image.loginDefaultLogo;
					defauleInfo.defaultCopyRight = image.defaultCopyRight;
					form.find("#default-system-logo").attr("src",oc.resource.getUrl(!image.systemCurrentLogo?image.systemDefaultLogo:"platform/file/getFileInputStream.htm?fileId="+image.systemCurrentLogo));
//					form.find("#current-system-logo").attr("src","themes/default/images/waitupload.png");
					$("#current-system-logo").remove();
					if(skin=="blue"){
						$('<img id="current-system-logo" class="upload-img" src="themes/blue/images/waitupload.png" style="width: 240px; height: 79px;"/>').appendTo($("#current-system-logo-preview"));
					}else{
						$('<img id="current-system-logo" class="upload-img" src="themes/default/images/waitupload.png" style="width: 240px; height: 79px;"/>').appendTo($("#current-system-logo-preview"));	
					}
					
					form.find("#default-login-logo").attr("src",oc.resource.getUrl(!image.loginCurrentLogo?image.loginDefaultLogo:"platform/file/getFileInputStream.htm?fileId="+image.loginCurrentLogo));
//					form.find("#current-login-logo").attr("src","themes/default/images/waitupload.png");
					form.find("#current-login-logo").remove();
					if(skin=="blue"){
					$('<img id="current-login-logo" class="upload-img" src="themes/blue/images/waitupload.png"/>').appendTo($("#current-login-logo-preview"));
					}else{
						$('<img id="current-login-logo" class="upload-img" src="themes/default/images/waitupload.png"/>').appendTo($("#current-login-logo-preview"));
					}
					if(!image.currentCopyRight){
						form.find("[name=currentCopyRight]").val(defauleInfo.defaultCopyRight);
					}
				}
			},
			successMsg:""
		});
	}
	loadForm();
	$('#choiceSystemLogo').change(function(){
		$("#current-system-logo").remove();
		if(skin=="blue"){
			$('<img id="current-system-logo" class="upload-img" src="themes/blue/images/waitupload.png" style="width: 240px; height: 79px;"/>').appendTo($("#current-system-logo-preview"));
		}else{
			$('<img id="current-system-logo" class="upload-img" src="themes/default/images/waitupload.png" style="width: 240px; height: 79px;"/>').appendTo($("#current-system-logo-preview"));
		}
		setImagePreview($("#choiceSystemLogo"),$("#current-system-logo-preview"),$("#current-system-logo"));
		$("#current-system-logo").show();
	});
	
	function replaceCopy(copy){
		return copy.replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&copy;/g,"??");
	}
	
	/**
	 * ??????????????????LOGO
	 * */
	$('#recoverySystemLogo').linkbutton("RenderLB",{
		iconCls : 'icon-default',
		onClick : function() {
			$("#current-system-logo").get(0).style.filter = "";
			form.find("#current-system-logo").attr("src",oc.resource.getUrl(defauleInfo.systemDefaultLogo));
			form.find("[name=systemCurrentLogo]").val("");
//			var file = $("#choiceSystemLogo");
//			file.after(file.clone().val("")); 
//			file.remove();
			$("#choice-system-logo-btn").remove();
			$('<a href="javascript:void(0);" class="file-button locate-left" id="choice-system-logo-btn">??????'+
					'<input class="file-button-file" type="file" name="systemLogo"'+
					' id="choiceSystemLogo"></a>').appendTo($("#system-choice-logo-package"));
			form.find("#choice-system-logo-btn").linkbutton("RenderLB",{
				iconCls : 'icon-browse'
			});
			$('#choiceSystemLogo').change(function(){
				$("#current-system-logo").remove();
				if(skin=="blue"){
				$('<img id="current-system-logo"  src="themes/blue/images/waitupload.png" class="upload-img" style="width: 240px; height: 79px;"/>').appendTo($("#current-system-logo-preview"));
				}else{
					$('<img id="current-system-logo"  src="themes/default/images/waitupload.png" class="upload-img" style="width: 240px; height: 79px;"/>').appendTo($("#current-system-logo-preview"));
				}
				setImagePreview($("#choiceSystemLogo"),$("#current-system-logo-preview"),$("#current-system-logo"));
				$("#current-system-logo").show();
			});
			$("#current-system-logo").show();
		}
	});
	
	
	$('#choiceLoginLogo').change(function(){
		form.find("#current-login-logo").remove();
		if(skin=="blue"){
		$('<img id="current-login-logo" class="upload-img" src="themes/blue/images/waitupload.png"/>').appendTo($("#current-login-logo-preview"));
		}else{
		$('<img id="current-login-logo" class="upload-img" src="themes/default/images/waitupload.png"/>').appendTo($("#current-login-logo-preview"));
		}
		setImagePreview($("#choiceLoginLogo"),$("#current-login-logo-preview"),$("#current-login-logo"));
		$("#current-login-logo").show();
	});
	
	/**
	 * ??????????????????LOGO
	 * */
	$('#recoveryLoginLogo').linkbutton("RenderLB",{
		iconCls : 'icon-default',
		onClick : function() {
			$("#current-login-logo").get(0).style.filter = "";
			form.find("#current-login-logo").attr("src",oc.resource.getUrl(defauleInfo.loginDefaultLogo));
			form.find("[name=loginCurrentLogo]").val("");
//			var file = $("#choiceLoginLogo");
//			file.after(file.clone().val("")); 
//			file.remove();
			form.find("#choice-login-logo-btn").remove();
			
			$('<a href="javascript:void(0);" class="file-button locate-left" id="choice-login-logo-btn">??????'+
					'<input class="file-button-file" type="file" name="loginLogo"'+
						'id="choiceLoginLogo"></a>').appendTo($("#lgoin-choice-logo-package"));
			form.find("#choice-login-logo-btn").linkbutton("RenderLB",{
				iconCls : 'icon-browse'
			});
			
			$('#choiceLoginLogo').change(function(){
				form.find("#current-login-logo").remove();
				if(skin=="blue"){
				$('<img id="current-login-logo"  src="themes/blue/images/waitupload.png" class="upload-img"/>').appendTo($("#current-login-logo-preview"));
				}else{
				$('<img id="current-login-logo"  src="themes/default/images/waitupload.png" class="upload-img"/>').appendTo($("#current-login-logo-preview"));
				}
				setImagePreview($("#choiceLoginLogo"),$("#current-login-logo-preview"),$("#current-login-logo"));
				$("#current-login-logo").show();
			});
			$("#current-login-logo").show();
		}
	});
	
	/**
	 * ????????????????????????
	 * */
	$('#recoveryCopyright').linkbutton("RenderLB",{
		iconCls : 'icon-default',
		onClick : function() {
			form.find("[name=currentCopyRight]").val(defauleInfo.defaultCopyRight);
		}
	});
	
	/**
	 * ??????PSD?????????
	 * */
	$('#downloadPSD').linkbutton("RenderLB",{
		iconCls : 'icon-download',
		onClick : function() {
			oc.util.ajax({
				url:oc.resource.getUrl("system/image/getDownloadPadName.htm"),
				successMsg:"",
				success:function(d){
					window.location.href=oc.resource.getUrl("resource/module/system/image/system_logo/"+d.data);
				}
					
			});
		}
	});
	
	/**
	 * ????????????
	 * */
	$('#submitForm').linkbutton("RenderLB",{
		iconCls : 'fa fa-check-circle',
		onClick : function() {
			$('#systemImageForm').ajaxSubmit({
				url : oc.resource.getUrl('system/image/updateImageConfig.htm'),
				type : 'POST',
				timeout: 3000,
				iframe : true,
				dataType: "json",
				success : function(responseText, statusText, xhr, $form) {
					if(responseText && responseText.data == true){
						loadForm();
						alert("????????????,???????????????????????????????????????????????????");
					}else{
						alert("???????????????");
					}
				},
				error:function(response, statusText, xhr){
					alert("???????????????");
				}
			});
		}
	});
	

	function setImagePreview(docObj, localImagId, imgObjPreview) {
		docObj = docObj.get(0);
		if (docObj.files && docObj.files[0]) {
			var fileTypes = [ 'image/png', 'image/jpeg', 'image/bmp' , 'image/gif'];
			var file = docObj.files && docObj.files[0];
			if (fileTypes.indexOf(file.type) > -1) {
				//?????????????????????img??????
				//	        	imgObjPreview.css({'display':'block',width:'245px',height:'77px'})
				imgObjPreview.attr("src", window.URL
						.createObjectURL(docObj.files[0]));
			} else {
				$.messager.alert("??????", "???????????????????????????????????????????????????!");
				return false;
			}
		} else {
			//IE??????????????????
			docObj.select();
			docObj.blur();
			var imgSrc = document.selection.createRange().text;
			//????????????????????????
			//	            localImagId.css({width:'245px',height:'77px'});
			//???????????????????????????????????????????????????????????????
			try {
				imgObjPreview.get(0).style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
				imgObjPreview.get(0).filters
						.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
			} catch (e) {
				$.messager.alert("??????", "???????????????????????????????????????????????????!");
				return false;
			}
			imgObjPreview.show();
			document.selection.empty();
		}
		return true;

	}
})(jQuery);