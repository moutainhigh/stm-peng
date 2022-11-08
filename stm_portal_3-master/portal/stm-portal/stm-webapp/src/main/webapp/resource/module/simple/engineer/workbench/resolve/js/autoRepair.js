$(function(){
	var $alertPageContainer = $('#alertPageContainer'),
		$solveDetail = $('#solveDetail'),
		$form = $('#autoRepairForm'),
		$auoRepairWindow = $('#auoRepairWindow'),
		i,t,$connect = $('#connect'),$repairTxt = $('.repair_txt'),$bar = $('.repair_pro_bar'),title;
	init();
	function init(){
		getResourceInfo();
		autoRepair();
		backSolveDetail();
		close();
	}
	function getResourceInfo(){
		title = snapshotStr.title;
		if(title.getByteLength() > 200){
			title = title.subByteLength(200);
		}
		$('.oc_simple_repair .reh3').html('<h3 class="reh3">点击连接为您修复<font>'+title+'</font>的问题</h3>'); 
		$('.pop_title').text('ID:'+$alertPageContainer.data('eventId')+'-'+title);
		oc.util.ajax({
			url:oc.resource.getUrl('simple/engineer/workbench/getResourceInfo.htm'),
			data:{"eventId":$alertPageContainer.data('eventId')},
			success:function(t){
				var data = t.data||'';
				$form.find('input[name=repairType][value='+data.repairType+']').prop('checked',true);
				$form.find('input[name=ip]').val(data.ip);
				$form.find('input[name=port]').val(data.port);
				$form.find('input[name=account]').val(data.account);
				$form.find('input[name=password]').val(data.password);
			}
		});
	}
	
	function autoRepair(){
		$connect.off('click').on('click',function(){
			//IP正则：((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))
			if($.trim($form.find('input[name=ip]').val())!=""){
				$('.oc_simple_repair .reh3').html('<h3 class="reh3">正在为您修复<font>'+title+'</font>的问题</h3>');
				$repairTxt.text('开始建立连接，请稍等...');
				//这个定时器是为了模拟进度条，让用户能看到开始建立连接
				$connect.addClass('oc_simple_btn_login').removeClass('oc_simple_btn_green');
				setTimeout(function(){
							requestScript();
				},500);
			}else{
				alert("IP不能为空");
			}
		});
	}
	/**
	 * 请求执行脚本
	 * */
	function requestScript(){
		var DataDeal = {
		          formToJson: function (data) {
		              data=data.replace(/&/g,"\",\"");
		              data=data.replace(/=/g,"\":\"");
		              data="{\""+data+"\"}";
		              return data;
		           },
		};
		var backData,data = $form.serialize()+'&eventId='+$alertPageContainer.data('eventId')+'&resolveId='+$alertPageContainer.data('resolveId');
		var json=DataDeal.formToJson(data);
        // console.log(json);
		$form.find('input').css({'background-color':'#868b91','color':'#666'}).prop('readOnly','readOnly');
		//console.log($.parseJSON(data));
		oc.util.ajax({
			
			url:oc.resource.getUrl('simple/engineer/workbench/executeScriptAutoRepair.htm'),
			data:jQuery.parseJSON(json),
				 //{"repairType":"2","ip":"192.168.1.137","port":"22","account":"shanh","password":"123456","eventId":"1684536","resolveId":"1783500"},
			startProgress:false,
			stopProgress:false,
			success:function(d){
				backData = d.data;
					if(backData==true||backData==false){
					var i = 0,random=Math.floor(Math.random()*100), 
						t1 = setInterval(function(){//这个定时器是为了模拟进度条，在上个定时器的基础上继续走进度，并提示用户正在执行脚本
						if (i > random) {
							$repairTxt.text('正在执行脚本'+i+'%');
							$bar.css('width',i+'%');
						}else{
							$repairTxt.text('读取脚本'+i+'%');
							$bar.css('width',i+'%');
						}
						i++;
						if (i >= 100) {
							clearInterval(t1);
							if(backData==true){
								$repairTxt.text('执行脚本成功');
								setTimeout(function(){
									$alertPageContainer.load('module/simple/engineer/workbench/resolve/repairSuccess.html',function(){
										$('.transparent').css('opacity','0.7');
										$auoRepairWindow.remove();
									}).show();
								},500);
							}else if(backData==false){
								$repairTxt.text('执行脚本失败');
								setTimeout(function(){
									$alertPageContainer.load('module/simple/engineer/workbench/resolve/repairFaild.html',function(){
										$('.transparent').css('opacity','0.7');
										$auoRepairWindow.remove();
								}).show();
								},500);
							}
						}
						i++;
					}, 20);
					}else{
						
						$form.find('input').css({'background-color':'#fbfbfb','color':'#999'}).removeAttr("readOnly");
						$form.find('input[name=ip]').attr("readOnly","readOnly");
						$connect.removeClass('oc_simple_btn_login').addClass('oc_simple_btn_green link_btn');
						autoRepair();//请求失败之后可以继续点击连接
						$('.oc_simple_repair .reh3').html('<h3 class="reh3">点击连接为您修复<font>'+title+'</font>的问题</h3>'); 
						$repairTxt.text('连接失败，请输入正确信息，重新连接...');
						clearInterval(t);
					}
				}
			});
	}
	function backSolveDetail(){
		$auoRepairWindow.off('click.back').on('click.back','#backSolveDetail',function(){
			$auoRepairWindow.remove();
			$solveDetail.show();
		});
	}
	function close(){
		$auoRepairWindow.find('.oc_simple_close_btn').on('click',function(){
			$auoRepairWindow.remove();
		});
	}
});