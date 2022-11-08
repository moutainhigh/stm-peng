oc.index={
	_activeRight:0,
	_user:0,
	_rights:{},
	getDomains:function(){
		return (this._rights[this._activeRight].domains||[]).concat();
	},
	getUser:function(){
		return $.extend(true,{},this._user);
	},
	getModule:function(id){
		return this._rights[id];
	},
	loadUserResponse:0,
	_afterLoadUser:function(d){
		this._user=d;
		if(d.rights){
			for(var ds=d.rights,i=0,len=ds.length,rit=this._rights;i<len;i++){
				d=ds[i];
				rit[d.id]=d;
			}
		}
		this.loadUserResponse&&this.loadUserResponse(d);
	},
	loadLoginUser:function(fn){
		var t=this;
		t.reloadLoginUser(function(){
			fn&&fn(t.getUser());
		});
	},
	reloadLoginUser:function(fn){
		var t=this;
		oc.util.ajax({
			url:oc.resource.getUrl('system/login/reLoadLoginUser.htm'),
			successMsg:null,
			success:function(d){
				if(d.code == 207){
					alert('请导入License文件！');
					$('<div id="licenseActiveId" isOneCenter="true"></div>').dialog({
						href : oc.resource.getUrl('resource/license-active.html'),
						title : '重新注册',
						width:550,
						height : 300,
						closable:false,
						resizable : false,
						cache : false, 
						onLoad:function(){
						}
					});
				}else{
					if(d.code == 200){
						d=d.data;
						//设置用户名称
						$('#user_info_name_id').html(d.name);
						d.rights=d.rights.sort(function(o1,o2){
							return o1.sort>o2.sort ? 1 : -1;
						});
						t._afterLoadUser(d);
						fn&&fn();
					}else{
						alert('加载登录用户信息失败！');
					}
				}
			}
		});
	},
	loadRights:function(){
		this.reloadLoginUser(function(){
			oc.index.nav.load(oc.index.getUser().rights);
		});
	},
	getSystemLogo:function(fn){
		oc.util.ajax({
			url:oc.resource.getUrl('system/image/getSystemLogo.htm'),
			successMsg:null,
			failureMsg:'',
			success:function(d){
				if(d.data){
					fn&&fn(oc.resource.getUrl(d.data));
				}
			}
		});
	},
	skin:"blue",
	getSkin:function(){

		oc.util.ajax({
			url:oc.resource.getUrl('system/skin/get.htm'),
			successMsg:null,
			failureMsg:'',
			async:false,
			success:function(d){
				var data=$.parseJSON(d.data);
				var currentskin = data.selected;
				if(currentskin=="darkgreen"){
					currentskin="default";
				}
				
			oc.index.skin=currentskin;
		
			}
	
		});
		return oc.index.skin;
	},
	_categoryMapper:0,
	getCategoryMapper:function(){
		return $.extend({},this._categoryMapper);
	},
	body:$('body:first'),
	indexLayout:0,
	activeContent:0,//当前激活的主界面内容jQuery对象
	activeHref:'',
	isCurrentActiveModule:function(moduleFileName){
    	for(var i in this._rights){
    		if(i == oc.index._activeRight){
                // console.log(this._rights[i].url);
    			if(this._rights[i].url.indexOf(moduleFileName) >= 0){
    				return true;
    			}else{
    				return false;
    			}
    		}
    	}
    	return false;
	}
};

oc.util.ajax({
	url:oc.resource.getUrl('system/resource/getCategoryMapper.htm'),
	success:function(d){
		oc.index._categoryMapper=d.data;
	}
});
var loginTooltipsMsg = $.cookie('mainsteam-stm-login-tooltipsMsg');
if(loginTooltipsMsg != null && loginTooltipsMsg != undefined && loginTooltipsMsg != "null"){
	// 弹出登陆提示信息
	alert(loginTooltipsMsg);
	// 清除登陆提示信息
	$.cookie('mainsteam-stm-login-tooltipsMsg', null);
}