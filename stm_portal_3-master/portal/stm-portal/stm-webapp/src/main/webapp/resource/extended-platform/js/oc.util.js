//Highcharts.setOptions(Highcharts.theme);
var _defaultGridOpt = {
	fitColumns : true,
	pagination : true,
	fit : true,
	singleSelect : true,
	border:false,
	striped : true,
	pageSize : 10,
	pageList : [ 10, 20, 30 ],
	loadFilter : function(data) {
		if(Object.prototype.toString.call(data) === '[object Array]'){
			var d = {};
			d.rows = data;
			d.total=data.length;
			return d;
		}else{
			if (data.data) {
				data.rows = data.data;
				return data;
			} else {
				return data;
			}
		}
	}
}

var ajaxcfg={
//	url:'#',请求地址 客户必须提供
//	params:{} 请求参数，请求参数也可以配置成data：{}，任选其一
	success:$.noop(),//正常返回，oc状态码在200-299之间，业务根据状态吗进行相应处理
//	successMsg:'${oc.local.util.ajax.success}',//操作成功提示，配置为null则不提示
//	failureMsg:'${oc.local.util.ajax.failure}',
//	errorMsg:'${oc.local.util.ajax.error}',//请求失效错误，业务无须关心，配置为null则不提示
	exceptionMsg:true,//服务器异常提示,正常返回，但oc状态码在200-299之外（此段状态吗无须业务处理）的提示信息，配置为null则不提示,配置成false服务端异常信息不显示
	async:true,
	dataType:'json',//"xml" "html" "script" "json" "jsonp" "text"
	responseType:'json',
	contentType:'application/x-www-form-urlencoded; charset=UTF-8',
	type:'post',
	timeout:30000,
	urlMsg:'${oc.local.util.ajax.urlMsg}',
	error:function(req,status,error){
		this.errorMsg&&alert(this.errorMsg.toI18n());
	},
	startProgress:function(){
	},
	stopProgress:function(){
	}
};


var _defaultDatalistOpt = {
	lines : true,
	border:false
};

var _defaultDailogOpt = {
	title : '',
	closed : false,
	cache : false,
	modal : true,
	resizable:false
}

var oc = {
	resource : {
		href : window.location.href,
		baseUrl : window.location.pathname.split('resource')[0],// 形如/oc/等
		getUrl : function(url) {
			return this.baseUrl + url;
		}
	},
	form : {
		getFormVal : function(id) {
			var json={};
			id.find("input[type!=button][type!=radio],select").each(function(){
				if($(this).attr("name")){
					json[$(this).attr("name")]=$(this).val();
				}
			});
			id.find("input[type=radio]").each(function(){
				if($(this).attr("name")){
					if($(this).attr("checked")){
						json[$(this).attr("name")]=$(this).val();
					}
				}
			});
			return json;
		}
	},
	util : {
		datagrid : function(cfg) {
			cfg = $.extend({}, _defaultGridOpt, cfg);
			if (!cfg.selector) {
				alert("没有Datagrid父容器，无法初始化Datagrid");
				return false;
			} else {
				cfg.selector = (typeof cfg.selector == 'string') ? $(cfg.selector)
						: cfg.selector;
				return cfg.selector.datagrid(cfg);
			}
		},
		datalistMenu : function(cfg) {
			cfg = $.extend({}, _defaultDatalistOpt, cfg);
			if (!cfg.selector) {
				alert("没有datalist父容器，无法初始化datalist");
				return false;
			} else {
				cfg.selector = (typeof cfg.selector == 'string') ? $(cfg.selector)
						: cfg.selector;
				cfg.contentSelector = (typeof cfg.contentSelector == 'string') ? $(cfg.contentSelector)
						: cfg.contentSelector;
				cfg.onClickRow = function(index, row) {
					cfg.contentSelector.panel({
						title : row.text,
						href : row.value
					});
				}
				return cfg.selector.datalist(cfg);
			}
		},
		dialog : function(cfg) {
			var dialog_div = $('<div></div>');
			cfg = $.extend({}, _defaultDailogOpt, cfg);
			cfg.onClose=function(){
				dialog_div.dialog('destroy');
			}
			return dialog_div.dialog(cfg);
		},
		convertComboTree:function(treeData,idField,textField,rid,rname){
			for(var i=0;i<treeData.length;i++){
				var data = treeData[i];
				data[rid?rid:"id"]=data[idField];
				data[rname?rname:"text"]=data[textField];
				if(data.children){
					oc.util.convertComboTree(data.children,idField, textField,rid,rname);
				}
			}
		},
		ajax:function(cfg){
			cfg=$.extend({},ajaxcfg,cfg);
			if(cfg.url){
				cfg.startProgress&&cfg.startProgress();
//				cfg.data=$.extend({},cfg.params,cfg.data);
				cfg.data=cfg.params||cfg.data;
				cfg.data&&$.each(cfg.data,function(i,obj){
					if(typeof obj=='object'){
						cfg.data[i]=JSON.stringify(obj);
					}
				});
				cfg.failure_=cfg.failure;
				cfg.failure=null;
				cfg.success_=cfg.success;
				cfg.success=function(data){
					cfg.stopProgress&&cfg.stopProgress();
					var msg=null;
					if(data&&data.code){
						if(data.code > 199 && data.code < 300){
							if(data.code!=200){
								msg=cfg.failureMsg||data.data;
							}else{
								msg=cfg.successMsg;
							}
							cfg.success_&&cfg.success_(data);
						}else{
							cfg.failure_&&cfg.failure_(data);
							if(typeof cfg.exceptionMsg=='boolean'){
								cfg.exceptionMsg&&(msg=data.data);
							}else{
								msg=data.data||cfg.exceptionMsg;
							}
						}
					}else{
						cfg.success_&&cfg.success_(data);
					}
					if(msg&&(typeof msg=='string'))alert(msg.toI18n());
				};
				$.ajax(cfg);
			}else{
				alert(cfg.urlMsg.toI18n());
			}
		},
		ztree:{
			_defaultSetting:{
				check : {
					enable : true,
					chkboxType : {"Y" : "p","N" : "s"}
				},
				data:{
					key:{
						children:"children",
						name:"name",
						title:"name"
					}
				},
				async : {
					enable : true,
					autoParam : ["fid"],
					dataType : "json",
					dataFilter : function(id, node, newNodes){
						var User_Data=newNodes.data;				
						oc.util.convertComboTree(User_Data[1], "fid", "fname","id","name");
						return User_Data[1];
					}
				},
				callback:{
					onAsyncSuccess:function(event,treeId){
						var treeObj = $.fn.zTree.getZTreeObj(treeId)
						treeObj.expandAll(true);
					}
				}
			},
			initZtree:function(cfg){
				var setting = $.extend(true,{},oc.util.ztree._defaultSetting,cfg);
				if(cfg.selector){
					var selector =  (typeof cfg.selector == 'string') ? $(cfg.selector): cfg.selector;
					$.fn.zTree.init(selector,setting);
				}
			},
			getCheckedNodes:function(treeId){
				var treeObj = $.fn.zTree.getZTreeObj(treeId);
				var nodes = treeObj.getCheckedNodes(true);
				return nodes;
			}
		}
	}
};

var getBeforeDate=function getBeforeDate(n){
    var n = n;
    var d = new Date();
    var year = d.getFullYear();
    var mon=d.getMonth()+1;
    var day=d.getDate();
    if(day <= n){
            if(mon>1) {
               mon=mon-1;
            }
           else {
             year = year-1;
             mon = 12;
             }
           }
          d.setDate(d.getDate()-n);
          year = d.getFullYear();
          mon=d.getMonth()+1;
          day=d.getDate();
     s = year+"-"+(mon<10?('0'+mon):mon)+"-"+(day<10?('0'+day):day);
     return s;
}

$.extend($.fn.validatebox.defaults.rules, {
    minLength: {
        validator: function(value, param){
            return value.length >= param[0];
        },
        message: '请输入 至少 {0}个字.'
    },
    maxLength: {
        validator: function(value, param){
            return value.length <= param[0];
        },
        message: '请输入少于 {0}个字.'
    },
    min:{
    	validator: function(value, param){
            return value >= param[0];
        },
        message: '允许输入的最小值为{0}.'
    },
    max:{
    	validator: function(value, param){
            return value <= param[0];
        },
        message: '允许输入的最大值为{0}.'
    },
    number: {  
        validator: function (value, param) {  
            return /^\d+$/.test(value);  
        },  
        message: '请输入数字.'  
    },  
});

