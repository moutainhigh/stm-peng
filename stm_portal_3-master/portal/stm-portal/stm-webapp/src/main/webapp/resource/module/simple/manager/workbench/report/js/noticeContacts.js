$(function(){
	var $alertWindow = $('#alertPageContainer'),$noticeContacts = $('#noticeContacts'),$noticeContactsList = $('#noticeContactsList');
	appendSend();
	getCoticeContacts();
	var skin=Highcharts.theme.currentSkin;
	Array.prototype.in_array = function(e){  
		for(i=0;i<this.length && this[i]!=e;i++);  
		return !(i==this.length);  
	}
	/**
	 * 增加一个div用来装发送消息之后的内容，以便做返回处理
	 * */
	function appendSend(){
		$alertWindow.append('<div class="append_send"></div>');
	}
	/**
	 * 获取联系人
	 * */
	function getCoticeContacts(){
		var errorBizs = $alertWindow.data('errorBizs');
		if(errorBizs){
			var ids = new Array();
			for(var i=0;i<errorBizs.length;i++){
				ids.push(errorBizs[i].bizId);
			}
			oc.util.ajax({
				url:oc.resource.getUrl('simple/manager/workbench/getBizContacts.htm'),
				data:{ids:ids.join()},
				success:function(data){
					var d=data.data||'',len=d.length,html='';
					if(len > 0){
						for(var j=0;j<d.length;j++){
							var biz;
							for(var i=0;i<errorBizs.length;i++){
								if(errorBizs[i].bizId==d[j].id){
									biz=d[j];
								}
							}
							if(biz){
								html +='<li><span><input data-id="'+biz.id+'" data-name="'+biz.name+'" data-userId="'+biz.userId+'" data-userName="'+biz.userName+'" type="checkbox" class="p-10"/></span><span>'+biz.userName+'</span><span class="call-m-l-pos">'+biz.name+'负责人</span></li>';
							}else{
								html +='<li><span><input disabled="disabled" data-id="'+errorBizs[i].bizId+'" data-name="'+errorBizs[j].bizName+'" type="checkbox" class="p-10"/></span><span>业务已被删除</span><span class="call-m-l-pos">'+errorBizs[j].bizName+'负责人</span></li>';
							}
						}
						$noticeContactsList.html(html);
					}else{
						$noticeContacts.find('.oc_simple_door').html('<div class="no-noticecontact"></div>');
					}
					
					$noticeContactsList.off('click').on('click','input:checkbox',function(){
						var ischecked=false;
						$noticeContactsList.find('input:checkbox').each(function(){
							if($(this).prop('checked')){
								ischecked = true;
							}
						});
						var $ableBtn = $noticeContacts.find('#ico-c-b'),$disableBtn=$noticeContacts.find('#ico-c-h');
						if(ischecked){
							$disableBtn.hide();
							$ableBtn.show();
						}else{
							$ableBtn.hide();
							$disableBtn.show();
						}
						$ableBtn.find('.ico-mail').off('click').on('click',function(){
							//发送短信
							
							$alertWindow.find('.append_send').load('module/simple/manager/workbench/report/sendMsg.html',function(){
								backNoticeList();
								beforeSend('message');
							});
							var contacts = getCheckedNoticeContacts();
							if(contacts && contacts.length>0){
								var message = $noticeContacts.find("#noticeContent").val();
								oc.util.ajax({
									url:oc.resource.getUrl("simple/manager/workbench/sendMessageToContacts.htm"),
									data:{messageInfos:JSON.stringify(contacts),message:message,expectId:$alertWindow.data('expectId')},
									startProgress:false,
									stopProgress:false,
									success:function(){
										//alert("短信发送完成！");
										afterSend(true);
									}
								});
							}else{
								alert("没有选择负责人！");
							}
						});
						$ableBtn.find('.ico-alert').off('click').on('click',function(){
							//发送邮件
							$alertWindow.find('.append_send').load('module/simple/manager/workbench/report/sendMsg.html',function(){
								backNoticeList();
								beforeSend('email');
							});
							var contacts = getCheckedNoticeContacts();
							if(contacts && contacts.length>0){
								var message = $noticeContacts.find("#noticeContent").val();
								oc.util.ajax({
									url:oc.resource.getUrl("simple/manager/workbench/sendEmailToContacts.htm"),
									data:{messageInfos:JSON.stringify(contacts),message:message,expectId:$alertWindow.data('expectId')},
									startProgress:false,
									stopProgress:false,
									success:function(){
										//$.messager.progress('close');
										afterSend(true);
									}
								});
							}else{
								alert("没有选择负责人！");
							}
						});
					});
					
				}
			});
		}
	}
	function backNoticeList(){
		if($('#backNoticeList').length==0){
			$('body').append('<div id="backNoticeList"></div>');
		}
		var $backNoticeList = $('#backNoticeList');
		$alertWindow.find('#noticeContacts').hide();
		$alertWindow.find('.back_btn').off('click').on('click',function(){
			$backNoticeList.empty();
			$alertWindow.find('#noticeContacts').show();
		});
	}
	/**
	 * 发送消息时
	 * */
	function beforeSend(type){
		var $bgDiv = $alertWindow.find('.sendding');
		//$alertWindow.find('.transparent').css('opacity',0.7);
		if(type=="message"){
			$bgDiv.css('background','url(themes/'+skin+'/simple/images/Managers/sentinfo.gif) no-repeat');
		}else if(type=="email"){
			$bgDiv.css('background','url(themes/'+skin+'/simple/images/Managers/sentemail.gif) no-repeat');
		}
	}
	/**
	 * 发送消息后
	 * */
	function afterSend(isSucess){
		var $bgDiv = $alertWindow.find('.sendding');
		setTimeout(function(){
			$bgDiv.css({'background':'url(themes/'+skin+'/simple/images/PopImages/simp_ico.png) no-repeat','height': '45px','margin-top': '50px'
			,'margin-left': '-170px','padding':'16px 60px'});
			if(isSucess){
				$bgDiv.css({'background-position':'0px -75px'}).html('<h2>已发送</h2>');
				var name = $alertWindow.data('currentDispose');
				$('#reportManage .develop_ol').find('li[name='+name+'] span:first').css('background-position','0px -605px')
			}else{
				$bgDiv.css({'background-position':'0px -135px'}).html('<h2>发送失败</h2>');
			}
			setTimeout(function(){
				$alertWindow.find('.transparent').remove();
				$alertWindow.find('.close_btn').click();
			},1000);
		},3000);
	}
	
	/**
	 * 获取选择的联系人信息
	 * */
	function getCheckedNoticeContacts(){
		var contacts = [];
		$noticeContacts.find('input[type=checkbox]').each(function(){
			if($(this).attr("checked")=="checked" || $(this).attr("checked")==true || $(this).attr("checked")=="true"){
				var userId = $(this).attr("data-userId");
				var bizId = $(this).attr("data-id");
				var bizName = $(this).attr("data-name");
				var contactObj = {userId:userId,bizId:bizId,bizName:bizName};
				contacts.push(contactObj);
			}
		});
		return contacts;
	}
});