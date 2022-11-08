//html5 登录背景动画效果
particlesJS("particles-js", {
	"particles" : {
		"number" : {
			"value" : 70,
			"density" : {
				"enable" : true,
				"value_area" : 800
			}
		},
		"color" : {
			"value" : "#fff"
		},
		"shape" : {
			"type" : "circle",
			"stroke" : {
				"width" : 0,
				"color" : "#fff"
			},
			"polygon" : {
				"nb_sides" : 5
			},

		},
		"opacity" : {
			"value" : 0.5,
			"random" : false,
			"anim" : {
				"enable" : false,
				"speed" : 1,
				"opacity_min" : 0.1,
				"sync" : false
			}
		},
		"size" : {
			"value" : 3,
			"random" : true,
			"anim" : {
				"enable" : false,
				"speed" : 1,
				"size_min" : 0.1,
				"sync" : false
			}
		},
		"line_linked" : {
			"enable" : true,
			"distance" : 150,
			"color" : "#fff",
			"opacity" : 0.4,
			"width" : 1
		},
		"move" : {
			"enable" : true,
			"speed" : 2,
			"direction" : "none",
			"random" : false,
			"straight" : false,
			"out_mode" : "out",
			"bounce" : false,
			"attract" : {
				"enable" : false,
				"rotateX" : 600,
				"rotateY" : 1200
			}
		}
	},
	"interactivity" : {
		"detect_on" : "canvas",
		"events" : {
			"onhover" : {
				"enable" : false,
				"mode" : "grab"
			},
			"onclick" : {
				"enable" : false,
				"mode" : "push"
			},
			"resize" : true
		},
		"modes" : {
			"grab" : {
				"distance" : 140,
				"line_linked" : {
					"opacity" : 1
				}
			},
			"bubble" : {
				"distance" : 400,
				"size" : 40,
				"duration" : 2,
				"opacity" : 8,
				"speed" : 3
			},
			"repulse" : {
				"distance" : 200,
				"duration" : 0.4
			},
			"push" : {
				"particles_nb" : 4
			},
			"remove" : {
				"particles_nb" : 2
			}
		}
	},
	"retina_detect" : true
});
(function($){
	//获取url中?后面的字符串
	var url = encodeURI(location.search);
	var globalSubmit = true; 
	//验证码
	var verificationCode = 0;
	 
	if(url != "") {
		var theRequest = new Object();
		if(url.indexOf("?") != -1) {
			var str = url.substr(1);
			strs = str.split("&");
			for(var i = 0; i < strs.length; i++) {
				var temp = strs[i].substr(strs[i].indexOf("=") + 1);
				var arr = strs[i].split("=");
				theRequest[arr[0]] = decodeURI(temp);
			}
		}
		oc.util.ajax({
			url:oc.resource.getUrl('system/IBSLogin/fromIBSLogin.htm'),
			data:theRequest,
			success:function(d){
				if(d.code==200){
					// cookie里面有用户名和密码不安全
					 setCookie(val);
					top.location.href=oc.resource.getUrl('resource/index.html');
				}else{
					top.location.href=oc.resource.getUrl('resource/login.html');
				}
			},
			failureMsg:null,
			successMsg:null
		});
		return;
	}
	initShowModeButton();
	initSystemSkin();
	initSystemLoginLogo();
	var loginDiv=$('.login-bg:first'),
		loginForm=oc.ui.form({
			selector:loginDiv,
			novalidate:true
		});
	//取消验证
	loginForm.selector.form('disableValidation');
	loginForm.find('.login-btn').click(function(){
		login();
	});
	
	var loginMode=loginForm.find('.login-mode');
	loginMode.on('click',function(){
		if(!loginMode.hasClass('switching')){
			  $(this).addClass("switching");
		}else{
		  $(this).removeClass("switching");
		}

	});
	

	
	var loginMemory=loginForm.find('.login-memory').click(function(){

		loginMemory.toggleClass('selected');
		
	});
	
	/*************************************/
	//处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外  
	function banBackSpace(e){    
	    var ev = e || window.event;//获取event对象     
	    var obj = ev.target || ev.srcElement;//获取事件源     
	      
	    var t = obj.type || obj.getAttribute('type');//获取事件源类型    
	      
	    //获取作为判断条件的事件类型  
	    var vReadOnly = obj.getAttribute('readonly');  
	    var vEnabled = obj.getAttribute('enabled');  
	    //处理null值情况  
	    vReadOnly = (vReadOnly == null) ? false : vReadOnly;  
	    vEnabled = (vEnabled == null) ? true : vEnabled;  
	      
	    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，  
	    //并且readonly属性为true或enabled属性为false的，则退格键失效  
	    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")   
	                && (vReadOnly==true || vEnabled!=true))?true:false;  
	     
	    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效  
	    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")  && t!=null 
	                ?true:false;
	    
	    //判断  
	    if(flag2){  
	        return false;  
	    }  
	    if(flag1){     
	        return false;     
	    }     
	    
	    //添加登录页面对enter的处理
	    if(ev.keyCode == 13){
	    	return globalSubmit;
	    }
	}  
	  
	//禁止后退键 作用于Firefox、Opera  
	document.onkeypress=banBackSpace;  
	//禁止后退键  作用于IE、Chrome  
	document.onkeydown=banBackSpace; 
	/*************************************/
	
	function getCookie(){
		var account = $.cookie('mainsteam-stm-login-account') ? oc.util.AESdecrypt($.cookie('mainsteam-stm-login-account')) : $.cookie('mainsteam-stm-login-account'),
		    pass = $.cookie('mainsteam-stm-login-pass') ? oc.util.AESdecrypt($.cookie('mainsteam-stm-login-pass')) : $.cookie('mainsteam-stm-login-pass'),
			memoryFlag = $.cookie('mainsteam-stm-login-memory') == 'on';
        // add by sunhailiang on 20170608
		if(memoryFlag){
			loginMemory.removeClass('selected');
		}else{
			loginMemory.addClass('selected');
		}
		
		loginForm.val({
			account:account || " ",
			password:pass
		});
	}
	// 没有在cookie里面储存用户名和密码则不用去获取
	getCookie();
	function setCookie(val){
		//add by sunhailiang on 20170608
		var memoryFlag = !loginMemory.is('.selected');
		$.cookie('mainsteam-stm-login-memory',memoryFlag ? "on" : "off", { expires: 7 });
		if(memoryFlag){
			var encryptAccount = oc.util.AESencrypt(val.account);
			var encryptPwd = oc.util.AESencrypt(val.password);
			$.cookie('mainsteam-stm-login-account',encryptAccount, { expires: 7 }),
			$.cookie('mainsteam-stm-login-pass',encryptPwd, { expires: 7 });
		}else{
			$.removeCookie('mainsteam-stm-login-account');
			$.removeCookie('mainsteam-stm-login-pass');
		}
	}
	
	
	/**
	 * 检查Licens已经过期
	 * */
	(function(){
		oc.util.ajax({
			url:oc.resource.getUrl("system/license/checkLicense.htm"),
			successMsg:"",
			success:function(d){
				//#loginForm //input[name='password']
			
					if(d && d.code==200){
						var lic = d.data;
						if(lic && lic.tip){
							$("#licenseTipInfo").show();
							if(lic.overTime){
								tipLicenseInfo("您的产品授权已经过期！",true);
							}else{
								tipLicenseInfo("您的产品授权将于"+new Date(lic.deadLine).stringify("yyyy-mm-dd")+"过期！");
								$("body").keypress(function(e,i){
									if(e.keyCode==13)login();
									});
							}
						}else{
							$("#licenseTipInfo").hide();
							$("body").keypress(function(e,i){
								if(e.keyCode==13)login();
								});
						}

						if(lic.simple){
//							loginMode.click(function(){
//								$(this).toggleClass("switching");
//							});
						}else{
							tipLicenseInfo("授权文件无效或者升级您的授权文件到最新版本！",true);
							loginMode.attr('title','极简模式没有被授权！');
						}
						if(!lic.ifMatch){
							tipLicenseInfo("license控制已升级,您需要升级授权文件！",true);
						}
					}else{
						tipLicenseInfo("您的产品授权文件不存在或已过期！",true);
					}	
		
		
			},
			failure:function(data){
				tipLicenseInfo("您的产品授权文件不存在或已过期！",true);
			},
			exceptionMsg:false
		});
		
		function tipLicenseInfo(info,isNotLogin){
			$("#licenseTipInfo").show();
			$("#licenseTipInfo #tipInfoSpan").text(info);
			isNotLogin&&disabledLoginBtn();
		}
		
		/**禁用登录按钮*/
		function disabledLoginBtn(){
			loginForm.find('.login-btn').unbind("click");
			loginForm.find('.login-btn').addClass("login-btn-h");
			//添加不能提交标示
			globalSubmit = false;
		}
		
		$("#LicenseActiveBtn").click(function(){
			$("#loginForm").hide();
			$("#loginFormDiv").load('login-active.html');
		});

	})();
	
	// 密码修改框是否已显示
	var isUpdatePassDialog = false;
	// 登陆提交
	function login(){
		// 如果已弹出密码修改框则不提交 
		if(isUpdatePassDialog){
			return;
		}
		var newVal = {};
		// 获取输入框的值
		var val=loginForm.val();
		if(!val.account){
			alert('请输入用户名！');
			return;
		}
		if(val.account.length>32){
			alert('用户名过长！');
			return;
		}
		if(!val.password){
			alert('请输入密码！');
			return;
		}
		if(!val.code && verificationCode==1){
			alert('请输入验证码！');
			return;
		}else if(verificationCode==1){
			newVal.c = oc.util.AESencrypt(val.code);
		}

		newVal.u = oc.util.AESencrypt(val.account);
		newVal.p = oc.util.MD5(val.password);
		
		oc.util.ajax({
			url:oc.resource.getUrl('system/login/login.htm'),
			data:newVal,
			success:function(d){
				if(d.code==200){
					// cookie里面有用户名和密码不安全
					 setCookie(val);
					if(val.account=="oc-dev-admin" && val.password=="oc-dev-admin-112233445566"){
						top.location.href=oc.resource.getUrl('resource/extended-platform/index.html');
					}else{
						if(loginMode.is('.switching')){
							top.location.href=oc.resource.getUrl('resource/index_simple.html');
						}else{
							top.location.href=oc.resource.getUrl('resource/index.html');
						}
					}
					// 设置登陆提示信息在登陆后显示 index_user.js
					$.cookie('mainsteam-stm-login-tooltipsMsg', URLEncoder.encode(d.data), { expires: 7 });
				}else if(d.code == 206){
					isUpdatePassDialog = true;
					alertUpdatePassDialog(val);
				}
				
				if(d.code!=200 && verificationCode==1){
					 $("#code").val('');
					initkaptchaImage();
				}
				
			},
			failureMsg:null,
			successMsg:null
		});
	}
	function initSystemLoginLogo(){
		oc.util.ajax({
			url:oc.resource.getUrl('system/image/getLoginLogo.htm'),
			successMsg:null,
			failureMsg:'',
			success:function(d){
				var data = d.data;
				if(data){
					url = oc.resource.getUrl(data.logo);
					$(".login-panel .login-panelbox .login-logo").css("background","url("+url+")  no-repeat");
					$(".copyright").html(data.copyright.replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&copy;/g,"©"));
					
					verificationCode=data.verificationCode;
					if(verificationCode==1){
						$(".login-left").addClass("show-verification-code");
						
						
						$("#verificationCode").html("<span>验证码</span><input name='code' class='login-text-panel login-input-yzm' type='text' id='code' maxlength='4'/>" +
								"<img id='kaptchaImage' style='cursor:pointer' />");
						
						$('#kaptchaImage').click(function(){ 
							initkaptchaImage();
					    });
						
						initkaptchaImage();
					}else{
						$(".login-left").removeClass("show-verification-code");
					}				
				}
			}
		});
	}
	
	function initShowModeButton(){
		$.cookie('mainsteam-stm-simple-mode', true, { expires: 7 });
		oc.util.ajax({
			url:oc.resource.getUrl('system/simplemode/get.htm'),
			successMsg:null,
			failureMsg:'',
			success:function(d){
				try{
					var data=$.parseJSON(d.data);
                    // console.info(data);
					if(data.open){
                        // console.info("1");
					
						//$(".login-mode1").show();
						
					}else{
                        // console.info("2");
					//	$(".login-mode1").remove();
						$("#loginForm").addClass("no_simplemode");

					}
					$("#loginForm").show();
					$.cookie('mainsteam-stm-simple-mode', data.open, { expires: 7 });
				}catch(e){
					$("#loginForm").show();
				}
			},
			error:function(d){
				$("#loginForm").show();
			},
			failure:function(d){
				$("#loginForm").show();
			}
		});
	}
	
	function initSystemSkin(){
		oc.util.ajax({
			url:oc.resource.getUrl('system/skin/get.htm'),
			successMsg:null,
			failureMsg:'',
			success:function(d){
				var data=$.parseJSON(d.data);
				var skin = data.selected;
				var skin_easyui = $("#skin_easyui"), skin_oc = $("#skin_oc");
				if(skin != null && skin != undefined && skin != ""){
					switch (skin) {
					case "darkgreen":
						skin_easyui.attr("href", "third/jquery-easyui-1.4/themes/default/easyui.css");
						skin_oc.attr("href", "themes/default/css/oc.css");
						break;
					default:
						skin_easyui.attr("href", "third/jquery-easyui-1.4/themes/" + skin + "/easyui.css");
						skin_oc.attr("href", "themes/" + skin + "/css/oc.css");
						break;
					}
				}
			}
		});
	}
	
	
	function initkaptchaImage(){
		var url=oc.resource.getUrl('system/login/getKaptchaImage.htm?');
		$("#kaptchaImage").attr('src', url + Math.floor(Math.random()*100) );
	}
	
	function alertUpdatePassDialog(user){
		var dlgDom = $("<div/>");
		dlgDom.append("<form>" +
						"<div class='modPassDiv'>" +
							"<span class='modPassDesc'>旧密码：</span><input type='password' name='oldPassword' class='modPassInput' required='required'>" +
						"</div>" +
						"<div class='modPassDiv'>" +
							"<span class='modPassDesc'>新密码：</span><input type='password' name='password' class='modPassInput' required='required' validType='password'>" +
						"</div>" +
						"<div class='modPassDiv'>" +
							"<span class='modPassDesc'>确认密码：</span><input type='password' name='rePassword' class='modPassInput' required='required' validType='password'>" +
						"</div>" +
					"<form>");
		var form = oc.ui.form({
			selector:dlgDom.find("form"),
			novalidate:true
		});
		var dlg = dlgDom.dialog({
			title : '修改密码',
			height : 210,
			width : 400,
			buttons : [{
				text : '确认',
				handler : function(){
					// 表单验证
					if(!form.validate()){
						return false;
					}
					var oldPassword = dlgDom.find("[name='oldPassword']").val();
					var password = dlgDom.find("[name='password']").val();
					var rePassword = dlgDom.find("[name='rePassword']").val();
					if(!oldPassword){
						alert('旧密码不能为空！');
						return false;
					}
					if(!password){
						alert('密码不能为空！');
						return false;
					}
					if(!rePassword){
						alert('确认密码不能为空！');
						return false;
					}
					if(password != rePassword){
						alert('密码和确认密码不相同！');
						return false;
					}
					if(oldPassword == password){
						alert('新旧密码一致，不能修改！');
						return false;
					}
					
					oc.util.ajax({
						url : oc.resource.getUrl('system/login/updatePasswordByUserName.htm'),
						data : {
							u : oc.util.AESencrypt(user.account),
							op : oc.util.MD5(oldPassword),
							p : oc.util.MD5(password)
						},
						success : function(d){
							if(d.code == 200){
								alert('修改成功！');
								dlg.dialog('close');
							}
						},
						failureMsg:null,
						successMsg:null
					});
				}
			},{
				text : '取消',
				handler : function(){
					dlg.dialog('close');
				}
			}],
			onClose : function(){
				isUpdatePassDialog = false;
			}
		});
	}
	
})(jQuery);
//
