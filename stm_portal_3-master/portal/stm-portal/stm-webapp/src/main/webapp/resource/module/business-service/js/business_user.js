$(function(){
	oc.ns('oc.module.resmanagement.biz.user');
	
	function userDeal(mainDiv){
		
	}
	
	userDeal.prototype = {
			constructor:userDeal,
			mainDiv:undefined,
			userDialog:undefined,
			open : function(){
				var that = this,dialogDiv = $("<div/>");
				this.userDialog = dialogDiv.dialog({
					title:'业务权限管理',
					width:750,
					height:500,
					modal:true,
					href:oc.resource.getUrl('resource/module/business-service/business_userMain.html'),
					onLoad:function(){
						oc.biz.user.deal();
					},
					modal:true,
					buttons:[{
						id:'btn_Sub',
						text:'确定',
						iconCls:'fa fa-save',
						handler:function(){
							oc.biz.user.save();
						}
					},{
						id:'btn_Cancel',
						text:'取消',
						iconCls:'fa fa-times-circle',
						handler:function(){
							dialogDiv.dialog('destroy');
						}
					}]
				});
			}
	}
	
	oc.module.resmanagement.biz.user = {
			open:function(mainDiv){
				var selfObj = new userDeal(mainDiv);
				selfObj.open();
			}
	}
});