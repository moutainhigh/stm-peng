(function() {
	function BizEditPermissions(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	BizEditPermissions.prototype = {
		constructor : BizEditPermissions,
		cfg : undefined, // 外界传入的配置参数 json对象格式
		_defaults : {},
		bizPermissionsData:new Array(),
		lastEnterIndex : 0,//0为汇总，1为列表
		init : function() {
			var that = this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getAllPermissionsInfoList.htm'),
				startProgress:null,
				success:function(data){
					if(data.code == 200){
						that.bizPermissionsData = data.data;
					}else{
						alert('获取业务权限信息失败！');
					}
				}
			});
		},
		update:function(bizInfo){
			if(this.bizPermissionsData){
				for(var i = 0 ; i < this.bizPermissionsData.length ; i ++){
					if(bizInfo.id == this.bizPermissionsData[i].id){
						bizInfo.createId = this.bizPermissionsData[i].createId;
						this.bizPermissionsData[i] = bizInfo;
						break;
					}
				}
			}
		},
		add:function(bizInfo){
			var user = oc.index.getUser();
			bizInfo.createId = user.id;
			this.bizPermissionsData.push(bizInfo);
		},
		checkPermissionsEdit:function(bizId){
			
			//判断当前用户是否对该业务有编辑权限
			var user = oc.index.getUser();
			if(user.systemUser){
				//系统管理员或超级管理员有修改全部业务的权限
				return true;
			}else{
				if(!this.bizPermissionsData || this.bizPermissionsData.length <= 0){
					return true;
				}
				var info = null;
				for(var i = 0 ; i < this.bizPermissionsData.length ; i++){
					if(this.bizPermissionsData[i].id == bizId){
						info = this.bizPermissionsData[i];
						break;
					}
				}
				if(user.domainUser){
					//自己创建的系统或为责任人的系统
					if(info.createId == user.id || info.managerId == user.id){
						return true;
					}
				}else{
					if(info.managerId == user.id){
						//只能编辑为责任人的业务系统
						return true;
					}
				}
			}
			
			return false;
			
		},
		checkPermissionsAdd:function(){
			
			//判断当前用户是否有创建
			var user = oc.index.getUser();
			if(user.domainUser || user.systemUser){
				return true;
			}else{
				return false;
			}
			
		},
		checkPermissionsDelete : function(bizId){
			
			//判断当前用户是否对该业务有删除权限
			var user = oc.index.getUser();
			if(user.systemUser){
				//系统管理员或超级管理员有删除全部业务的权限
				return true;
			}else{
				if(!this.bizPermissionsData || this.bizPermissionsData.length <= 0){
					return true;
				}
				var info = null;
				for(var i = 0 ; i < this.bizPermissionsData.length ; i++){
					if(this.bizPermissionsData[i].id == bizId){
						info = this.bizPermissionsData[i];
						break;
					}
				}
				if(user.domainUser){
					//自己创建的系统
				
					if(info.createId == user.id){
						return true;
					}
				}
			}
			
			return false;
		},
		getLastEnter : function(){
			return this.lastEnterIndex;
		},
		setLastEnter : function(index){
			this.lastEnterIndex = index;
		}
	};

	// 命名空间
	oc.ns('oc.business.service.edit.permissions');
	// 对外提供入口方法
	var permissions = undefined;
	oc.business.service.edit.permissions = {
		init : function(){
			if(!permissions){
				permissions = new BizEditPermissions();
				permissions.init();
			}
		},
		checkEdit : function(bizId){
			return permissions.checkPermissionsEdit(bizId);
		},
		checkAdd : function(){
			return permissions.checkPermissionsAdd();
		},
		checkDelete : function(bizId){
			return permissions.checkPermissionsDelete(bizId);
		},
		add : function(bizInfo){
			permissions.add(bizInfo);
		},
		update : function(bizInfo){
			permissions.update(bizInfo);
		},
		getLastEnter : function(){
			return permissions.getLastEnter();
		},
		setLastEnter : function(index){
			permissions.setLastEnter(index);
		}
	};
})(jQuery);