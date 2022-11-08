/**
 * 扩展easyui验证器
 */
oc.resource.loadI18n('oc.validate.js',function(){
	$.extend($.fn.validatebox.defaults.rules,{
		min:{
			validator:function(v,ps){
				return parseInt(v)>=ps[0];
			},
			message:oc.local.valid.min
		},max:{
			validator:function(v,ps){
				return parseInt(v)<=ps[0];
			},
			message:oc.local.valid.max
		},range:{
			validator:function(v,ps){
				return (v>=ps[0])&&(v<=ps[1]);
			},
			message:oc.local.valid.range
		},minLength:{
			validator:function(v,ps){
				return v.toString().trim().length>=ps[0];
			},
			message:oc.local.valid.minLength
		},maxLength:{
			validator:function(v,ps){
				return v.toString().trim().length<=ps[0];
			},
			message:oc.local.valid.maxLength
		},int:{
			validator:function(v,ps){
				return /^\d+$/.test(v);
			},
			message:oc.local.valid.int
		},digit:{
			validator:function(v,ps){
				v=Number(v);
				return v||((typeof v=='number')&&v==0);
			},
			message:oc.local.valid.digit
		},reg:{
			validator:function(v,ps){
	    		return ps[0].test(v);
			},
    		message:'{1}'
		},accessIps:{
			validator:function(v,ps){
				var reg=/^((\d|([1-9]\d{1,2}))(\.(\d|([1-9]\d{1,2}))){2,3})(\-(\d|([1-9]\d{1,2}))(\.(\d|([1-9]\d{1,2}))){2,3})?(;((\d|([1-9]\d{1,2}))(\.(\d|([1-9]\d{1,2}))){2,3})(\-(\d|([1-9]\d{1,2}))(\.(\d|([1-9]\d{1,2}))){2,3})?)*$/;
				return reg.test(v);
			},
			message:'IP输入不合法'
		},ip:{
			validator:function(v,ps){
				var reg=/^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])(\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])){3}$/;
				return reg.test(v);
			},
			message:'IP输入不合法'
		},port:{
			validator:function(v,ps){
				var reg = /^([1-9]|[0-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
				return reg.test(v);
			},
			message:'端口号输入不合法'
		},equal:{
			validator:function(v,ps){
				return $(this).closest('form').find(ps[0]).val()==v;
			},
			message:'{1}'
		},dateBegin:{
			validator:function(v,ps){
				var cjq=$(this),
					dsjq=cjq.closest('form').find(ps[0]),
					dvjq=dsjq.prev('input'),
					djq=dsjq.parent().prev('.datebox-f:first'),
					flag=true,val;
				if(djq.length>0){
					val=djq.datetimebox('getValue');
					flag=(!v||!val)||v<=val;
					if((dvjq.length>0)&&!dvjq.data('autoValid')){
						dvjq.data('autoValid',true);
						djq.datetimebox('validate');
					}
				}
				cjq.data('autoValid',false);
				return flag;
			},
			message:'起始日期不能晚于截止日期！'
		},dateEnd:{
			validator:function(v,ps){
				var cjq=$(this),
					dsjq=cjq.closest('form').find(ps[0]),
					dvjq=dsjq.prev('input'),
					djq=dsjq.parent().prev('.datebox-f:first'),
					flag=true,val;
				if(djq.length>0){
					val=djq.datetimebox('getValue');
					flag=(!v||!val)||v>=val;
					if((dvjq.length>0)&&!dvjq.data('autoValid')){
						dvjq.data('autoValid',true);
						djq.datetimebox('validate');
					}
				}
				cjq.data('autoValid',false);
				return flag;
			},
			message:'截止日期不能早于起始日期！'
		},datetimeBegin:{
			validator:function(v,ps){
				var cjq=$(this),
					dsjq=cjq.closest('form').find(ps[0]),
					dvjq=dsjq.prev('input'),
					djq=dsjq.parent().prev('.datetimebox-f:first'),
					flag=true,val;
				if(djq.length>0){
					val=djq.datetimebox('getValue');
					flag=(!v||!val)||v<=val;
					if((dvjq.length>0)&&!dvjq.data('autoValid')){
						dvjq.data('autoValid',true);
						djq.datetimebox('validate');
					}
				}
				cjq.data('autoValid',false);
				return flag;
			},
			message:'起始时间不能晚于截止时间！'
		},datetimeEnd:{
			validator:function(v,ps){
				var cjq=$(this),
					dsjq=cjq.closest('form').find(ps[0]),
					dvjq=dsjq.prev('input'),
					djq=dsjq.parent().prev('.datetimebox-f:first'),
					flag=true,val;
				if(djq.length>0){
					val=djq.datetimebox('getValue');
					flag=(!v||!val)||v>=val;
					if((dvjq.length>0)&&!dvjq.data('autoValid')){
						dvjq.data('autoValid',true);
						djq.datetimebox('validate');
					}
				}
				cjq.data('autoValid',false);
				return flag;
			},
			message:'截止时间不能早于起始时间！'
		},mac:{
			validator:function(v,ps){
				var reg = /^([0-9a-fA-F]{2})(([/\s:-][0-9a-fA-F]{2}){5})$/;
				return reg.test(v);
			},
			message:'请输入合法的mac地址'
		},sysOid:{
			validator:function(v,ps){
				var reg = /^1.3.6.1.\d+(\.\d+)*$/;
				return reg.test(v);
			},
			message:'需以“1.3.6.1.”开头'
		},password:{
			validator:function(v,ps){
				var reg =/(^.{6,18}$)|(^.{32}$)/;
				return reg.test(v);
			},
			message:'密码长度需为6-18位！'
		},mobile:{
			validator:function(v,ps){
				var reg =/^1[0-9]{10}$/;
				return reg.test(v);
			},
			message:'手机号码输入不合法！'
		},internalUrl:{
			validator:function(v){
				var reg =/^(\w{1,}\/){1,}\w{1,}\.html$/
				return reg.test(v);
			},
			message:'请输入有效的url地址！'
		},now:{
			validator:function(v){
				if(Date.parse($(this).val()) < new Date().getTime()){
					return false;
				}
				return true;
			},
			message:'请选择当前日期以后的时间'
		}
	});
});