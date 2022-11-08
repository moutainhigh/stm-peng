(function(){
  function UserAccountInfo(){
   this.form = null;
   this.dlg = null;
   this.TITLE = '个人信息';
   this.HTML_URL = 'resource/index_user_account_info.html';
   this.init();
   this.passBtn = null;
  }
  
  UserAccountInfo.prototype={
			init : function(){
				var self = this;
				$(".userAccoutInfo").bind("click",function(e){
				   e.stopPropagation();
				   try{
					   self.dlg && self.dlg.dialog('close');
				   }catch(error){}
                   self.openUserInfo($('<div/>'),e);
			    });
			},
			openUserInfo:function(div,e){
				var self = this;
				self.dlg = div.dialog({
						href : oc.resource.getUrl(self.HTML_URL),
						closed:true,
						title: self.TITLE,
						width:420,
						modal:false,
						height:'auto',
						buttons:[{
							text:'取消',
							onClick:function(){
								self.dlg.dialog('close');
							}
						}],
						onLoad:function(){
							var userInfo = oc.index.getUser();
							userInfo.userType = oc.util.getDictObj('user_type')[userInfo.userType].name;
							userInfo.sex = userInfo.sex==0 ? '男' : '女';
							userInfo.status = userInfo.status==1 ? '启用' : '停用';
							self.dlg.find('span.user_info').each(function(){
								var $this = $(this);
								$this.html(userInfo[$this.attr('field')]);
							});
							self.form = oc.ui.form({selector : self.dlg.find('form:first')});
							self.form.val(userInfo);
							//admin用户密码验证最少5位 
							if(userInfo.account == 'admin'){
								$("input[validType='password']").attr("validType","adminPassword");
							} 
							var editPass = self.dlg.find('tr.editPass'), 
								passInfo = editPass.find('.user_info'),
								pass     = self.dlg.find('tr.pass'),
								save     = editPass.find('.ico-save-info');
							save.click(function(){
								if(self.form.validate()){
									oc.util.ajax({
										url:oc.resource.getUrl('system/login/updatePassword.htm'),
										data: self.form.val(),
										successMsg:'密码修改成功!',
										success:function(d){
											if(d.code==200){
												oc.index.loadLoginUser();
												self.dlg.dialog('close');
											}
										}
									});
								}
							});  
							self.passBtn = editPass.find('.oc-ico-down').click(function(e){
								var jq=$(this);
								if(jq.is('.oc-ico-down')){
									passInfo.hide();
									save.show();
									pass.css('display','table-row');
									jq.removeClass('oc-ico-down').addClass('oc-ico-up');
								}else{
									passInfo.show();
									save.hide();
									pass.css('display','none');
									jq.removeClass('oc-ico-up').addClass('oc-ico-down');
								};
								self.validationable();
							}); 
							self.validationable();
						},
						onClose:function(){ 
							self.dlg && self.dlg.dialog("destroy");
						}
			    });
                try{
				  oc.util.showFloat(self.dlg, e);
				}catch(e){}
			},
		    validationable : function(){
				var self = this;
				if(self.passBtn.is('.oc-ico-down')){
					self.form.disable();
				}else{
					self.form.enable();
				}
			}
	}
	var userAccountInfo = new UserAccountInfo();
	userAccountInfo = null;
})();