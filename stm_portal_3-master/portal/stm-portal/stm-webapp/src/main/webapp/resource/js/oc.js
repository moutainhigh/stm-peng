//oc.prototype.js
(function($){if(!window.log){log=function(){if(window.console&&console.log){for(var i=0,len=arguments.length;i<len;i++){console.log(arguments[i])}}}}$.extend(Date.prototype,{stringify:function(format){format=format||"yyyy-mm-dd hh:MM:ss";format=format.replace("yyyy",this.getFullYear());var month=this.getMonth()+1,d=this.getDate(),h=this.getHours(),m=this.getMinutes(),s=this.getSeconds();format=format.replace("mm",month<10?"0"+month:month);format=format.replace("dd",d<10?"0"+d:d);format=format.replace("hh",h<10?"0"+h:h);format=format.replace("MM",m<10?"0"+m:m);format=format.replace("ss",s<10?"0"+s:s);return format}});$.extend(Number.prototype,{toDate:function(){return isNaN(this)?new Date():new Date(this)}});$.extend(String.prototype,{trim:function(){return this.replace(/(^\s*)|(\s*$)/g,"")},toDate:function(){if(this){var vals=/((\d+)-(\d+)-(\d+))?( (\d+):(\d+):(\d+))?/.exec(this);if(vals){return new Date(vals[2],vals[3],vals[4],vals[6]||0,vals[7]||0,vals[8]||0)}return null}return null},toI18n:function(){var str=this.toString(),ms=str.match(/\$\{([a-zA-Z]+(?:\.[a-zA-Z]+)*)\}/g);if(ms){for(var i=0,len=ms.length,m,v;i<len;i++){m=ms[i];try{v=eval(m.substr(2).slice(0,-1))||m;str=str.replace(m,v)}catch(e){log("没有配置国际化键："+m)}}return str}else{return str}},htmlspecialchars:function(){var str=this.toString();var s="";if(str.length==0||str==undefined||str==null){return""}for(var i=0;i<str.length;i++){switch(str.substr(i,1)){case"<":s+="&lt;";break;case">":s+="&gt;";break;case"&":s+="&amp;";break;case" ":if(str.substr(i+1,1)==" "){s+=" &nbsp;";i++}else{s+=" "}break;case'"':s+="&quot;";break;case"\n":s+="<br>";break;default:s+=str.substr(i,1);break}}return s}});_sortFn=function(o1,o2){return o1-o2};$.extend(Array.prototype,{indexOf:function(o){if(typeof o=="function"){for(var i=0,len=this.length;i<len;i++){if(o(this[i])){return i}}}else{for(var i=0,len=this.length;i<len;i++){if(o==this[i]){return i}}}return -1},sort:function(fn){var dest=[],src=this;fn=fn||_sortFn;for(var i=0,len=src.length,j,dLen=0,o;i<len;i++,dLen++){o=src[i];for(j=0;j<dLen;j++){if(fn(o,dest[j])<0){break}}dest.splice(j,0,o)}$.extend(this,dest);return this}})})(jQuery);

//oc.init.js
if(!oc){var oc={nsp:{regex:/^[a-z]+(\.[a-z]+)*$/,parse:function(D,C,A){var B=C[A];var E=D[B];if(!E){E=D[B]={}}if(A==C.length-1){return E}else{return this.parse(E,C,++A)}},randomVal:((Math.random()*999999999*Math.random()*999999999).toString().split(".")[0]),generateConfirm:function(B){var A="falsify"+B;this[A]=this.regex;this[B]=function(){if(this.regex!=this[A]){this.regex=this[A]}return true}}},ns:function(A){if(oc.nsp[oc.nsp.randomVal]){if(A.match(oc.nsp.regex)){var B=A.split(".");return oc.nsp.parse(window,B,0)}else{alert(A+":命名空间设置不合法,命名规则:"+oc.nsp.regex)}}}};oc.nsp.generateConfirm(oc.nsp.randomVal);oc.resource={lang:(window.navigator.userLanguage||window.navigator.language).toLowerCase(),href:window.location.href,baseUrl:window.location.pathname.split("resource")[0],linkDom:$('<link rel="stylesheet">'),scriptDom:document.createElement("script"),headDom:document.getElementsByTagName("head")[0],cssStyle:"resource/themes/default/",init:function(){},local:{"zh-cn":"zh-CN","en-us":"en-US"},resources:{},addEvent:function(C,A,B){if(A=="load"&&navigator.appName=="Microsoft Internet Explorer"){C.onreadystatechange=function(){if("complete"==C.readyState||"loaded"==C.readyState){B()}}}else{if(C.attachEvent){C.attachEvent("on"+A,B)}else{if(C.addEventListener){C.addEventListener(A,B,false)}}}},getUrl:function(A){return this.baseUrl+A},loadScript:function(A,B){B=B||$.noop;if(!this.resources[A]){var C=this.scriptDom.cloneNode();C.setAttribute("src",this.baseUrl+A);this.addEvent(C,"load",function(){B&&B()});this.headDom.appendChild(C);this.resources[A]=true}else{B&&B()}},loadScripts:function(F,C){var A=0,D=F.length,B;if(D<1){return}function E(){if(A==D){C&&C();return}B=F[A++];oc.resource.loadScript(B,E)}E()},loadCss:function(A,C){if(this.resources[A]&&!C){return}var B=this.linkDom.clone();B.attr("href",this.baseUrl+A);$(this.headDom).append(B);this.resources[A]=true},getCss:function(A){return this.baseUrl+this.cssStyle+A},loadI18n:function(B,A){this.loadScript(this.getI18nUrl(B),A)},getI18nUrl:function(A){return"resource/i18n/"+this.local[this.lang]+"/"+A}};oc.resource.common={dict:oc.resource.getUrl("dict/get.htm")};oc.ns("oc.ui");oc.isSimple=oc.resource.href.indexOf("index_simple")!=-1;oc.resource.loadI18n("oc.ui.js")};

//oc.util.js
/**
 * 提供的一些工具函数
 * @author ziwenwen
 */

oc.resource.loadI18n('oc.util.js');

oc.util={
	generateId:function(){
		return 'oc-id'+Math.round(Math.random()*99999999999);
	},
	ajaxcfg:{
//		url:'#',请求地址 客户必须提供
//		params:{} 请求参数，请求参数也可以配置成data：{}，任选其一
		success:$.noop(),//正常返回，oc状态码在200-299之间，业务根据状态吗进行相应处理
//		successMsg:'${oc.local.util.ajax.success}',//操作成功提示，配置为null则不提示
//		failureMsg:'${oc.local.util.ajax.failure}',
//		errorMsg:'${oc.local.util.ajax.error}',//请求失效错误，业务无须关心，配置为null则不提示
		exceptionMsg:true,//服务器异常提示,正常返回，但oc状态码在200-299之外（此段状态吗无须业务处理）的提示信息，配置为null则不提示,配置成false服务端异常信息不显示
		async:true,
		dataType:'json',//"xml" "html" "script" "json" "jsonp" "text"
		responseType:'json',
		contentType:'application/x-www-form-urlencoded; charset=UTF-8',
		type:'post',
		timeout:30000,
		urlMsg:'${oc.local.util.ajax.urlMsg}',
		error:function(req,status,error){
			this.stopProgress&&this.stopProgress();
			if(req.status == 0){
				//发送请求错误
				alert('请求超时,请重试或检查服务器状态！');
			}
			this.errorMsg&&alert(this.errorMsg.toI18n());
			log(JSON.stringify(req),JSON.stringify(status),JSON.stringify(error),
					req,status,error);
		},
		startProgress:function(){
			oc.ui.progress();
		},
		stopProgress:function(){
			$.messager.progress('close');
		}
	},
	ajax : function(cfg) {
		cfg=$.extend({},this.ajaxcfg,cfg);
		if(cfg.url){
			cfg.startProgress&&cfg.startProgress();
//			cfg.data=$.extend({},cfg.params,cfg.data);
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
	dictCache:{},
	getDict:function(type,fn){
		fn=fn||$.noop;
		var d=null;
		if(type){
			d=this.dictCache[type];
			if(!d){
				this.ajax({
					url:oc.resource.common.dict,
					data:{type:type},
					successMsg:null,
					async:false,
					success:function(ds){
						d=ds.data;
					}
				});
				this.dictCache[type]=d;
			}
		}
		fn(d);
		return d;
	},
	getDictObj:function(type,fn){
		fn=fn||$.noop;
		var obj={},arr;
		if(type){
			arr=this.dictCache[type]||this.getDict(type);
			for(var i=0,len=arr.length,item;i<len;i++){
				item=arr[i];
				obj[item.code]=item;
			}
		}
		fn(obj);
		return obj;
	},
	/**
	 * 将平行数组转换为js对象
	 * ds：要转换的数组数据
	 * key：作为键的字段 默认为id
	 */
	arr2Obj:function(ds,key){
		var os={};
		if(!ds||ds.length==0)return os;
		key=key||'id';
		for(var i=0,len=ds.length,o;i<len;i++){
			o=ds[i];
			os[o[key]]=o;
		}
		return os;
	},
	/**
	 * 将平行数组形式json对象转换成树形结构
	 * datas，field:比对的属性，
	 * pField:子对象引用的父对象的字段值
	 * cField:父对象存储的子对象数组
	 * data:如果data不为空，则需将data加入datas中的某个元素作为其子节点，需迭代处理
	 * return [treeDatas,childrenDatas]
	 */
	arr2tree:function(datas,field,pField,cField){//更改算法(不使用迭代方式)
		if(!datas||!field||!pField)return [datas,[]];
		var ps={},i=0,len=datas.length,children=[];
		for(i;i<len;i++){ps[datas[i][field]]=datas[i];}
		for(i=0;i<len;i++){
			var data=datas[i];
			if(data[pField]){
				var p=ps[data[pField]];
				if(!p){continue;}
				datas.splice(i,1);
				children.push(data);
				if(p[cField]){
					p[cField].push(data);
				}else{
					p[cField]=[data];
				}
				i--;len--;
			}
		}
		return [datas,children];
	},
	/**
	 * 将平行分组形式json对象转换成树形结构
	 * datas，
	 * idField:比对的属性，
	 * cField:父对象存储的子对象数组
	 * data:如果data不为空，则需将data加入datas中的某个元素作为其子节点，需迭代处理
	 */
	arrGroup2tree:function(datas,idField,cField){//更改算法(不使用迭代方式)
		if(!datas||!idField||!idField)return datas;
		datas=datas.concat();
		var ps={},i=0,len=datas.length,result=[],id=1,d;
		for(i;i<len;i++){
			d=$.extend(datas[i],{eTreeId:id++});
			ps[d[idField]]=$.extend({},d,{eTreeId:id++});
		}
		for(i=0;i<len;i++){
			var data=datas[i];
			if(data[idField]){
				var p=ps[data[idField]];
				if(!p){continue;}
				if(p[cField]){
					p[cField].push(data);
				}else{
					p[cField]=[data];
				}
			}
		}
		for(var pd in ps){
			result.push(ps[pd]);
		}
		return result;
	},
	
	arrGroup2tree:function(config){//更改算法(不使用迭代方式)
		var datas = config.datas,
		idField = config.idField,
		cField = config.cField,
		pFields = config.pFields;
		if(!datas||!idField||!cField)return datas;
		var ps={},i=0,len=datas.length,result=[],id=1,d;
		for(i;i<len;i++){
			d=$.extend(datas[i],{eTreeId:id++});
			ps[d[idField]]=$.extend({},d,{eTreeId:id++});
		}
		for(i=0;i<len;i++){
			var data=$.extend({},datas[i]);
			if(pFields){
				for(var j in pFields){
					data[pFields[j]] = '';
				}
			}
			if(data[idField]){
				var p=ps[data[idField]];
				if(!p){continue;}
				if(p[cField]){
					p[cField].push(data);
				}else{
					p[cField]=[data];
				}
			}
		}
		for(var pd in ps){
			var pNode = ps[pd];
			var node = {};
			if(pFields){
				node.eTreeId = ps[pd].eTreeId;
				for(var j in pFields){
					node[pFields[j]] = pNode[pFields[j]];
				}
				node[cField] = pNode[cField];
			}else{
				node = $.extend({},pNode);
			}
			result.push(node);
		}
		return result;
	},
	/**
	 * 将树形结构的数据转换成平行结构
	 * @param datas	树形结构的数据
	 * @param field	需要拼接的列名
	 * @param CName	子节点的列名（如 children）
	 * @param checkFiled 是否选中的标识字段名
	 * @param checkValue	选中标识值（如：1，表示选中， ）
	 * @returns {Array}
	 *  example  oc.util.treeToList(oc.util.arrGroup2tree(data.data,'userId','children')||[],'domainName', 'children', 'isChecked', 1);
	 */
	treeToList:function(data){
		var result = [];
		var datas = data.datas,
		field = data.field,
		CName = data.CName,
		checkFiled = data.checkFiled,
		checkValue = data.checkValue;
		for(var i in datas){
			var node = $.extend({},datas[i]);
			var CField = [];
			var children = $.extend({},datas[i][CName]);
			for(var j in children){
				(children[j][checkFiled] == checkValue)&&CField.push(children[j][field]);
			}
			node[field] = CField.join();
			node[field]&&result.push(node);
		}
		return result;
	},
	fullScreen:function(el){
		var rfs = el.requestFullScreen || el.webkitRequestFullScreen|| el.mozRequestFullScreen || el.msRequestFullScreen;
		if (wscript != null) {
			wscript.SendKeys("{F11}");
		}
		if (typeof rfs != "undefined" && rfs) {
			rfs.call(el);
		} else if (window.ActiveXObject || new ActiveXObject("WScript.Shell")) {
			try{
				var wscript = new ActiveXObject("WScript.Shell");
				if (wscript != null) {
					wscript.SendKeys("{F11}");
				}
			}catch (e) {
				log(e);
			}
		}
	},
	cancelFullScreen : function() {
		if (document.exitFullscreen) {
			document.exitFullscreen();
		} else if (document.mozCancelFullScreen) {
			document.mozCancelFullScreen();
		} else if (document.webkitExitFullscreen) {
			document.webkitExitFullscreen();
		} else if (window.ActiveXObject || new ActiveXObject("WScript.Shell")) {
			try{
				var wscript = new ActiveXObject("WScript.Shell");
				if (wscript != null) {
					wscript.SendKeys("{F11}");
				}
			}catch (e) {
				log(e);
			}
		}
	},
	tree2arr:function(ds,field,filter){
		var cs=[],d,c;
		filter=filter||true;
		if(!$.isArray(ds))ds=ds?[ds]:[];	
		for(var i=0,len=ds.length;i<len;i++){
			d=ds[i];
			if(filter)cs.push(d);
			c=d[field];
			if(c){
				var t=arguments.callee(c,field);
				for(var j=0;j<t.length;j++){
					cs.push(t[j]);
				}
			}
		}
		return cs;
	},
	download:function(fileId,failureMsg){
		iframe=this.downloadFrame;
		if(!iframe){
			iframe=this.downloadFrame=$('<iframe name="downloadFrame" style="display: none;"/>').appendTo('body').load(function(){
				$(iframe[0].contentWindow.document.body).children().remove().length!=0&&alert(failureMsg||'下载失败！');
			});
		}
		iframe.attr('src',oc.resource.getUrl('platform/file/downloadFile.htm?fileId='+fileId));
	},
	downloadFromUrl:function(url){
		iframe=this.downloadFrame;
		if(!iframe){
			iframe=this.downloadFrame=$('<iframe name="downloadFrame" style="display: none;"/>').appendTo('body').load(function(){
				$(iframe[0].contentWindow.document.body).children().remove().length!=0&&alert(failureMsg||'下载失败！');
			});
		}
		iframe.attr('src',url);
	},
	/**
	 * 判断输入关键字是否为IP， 是为true， 否 为false
	 * @param keyword
	 * @returns
	 */
	IPCheck:function(keyword){
		return /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])(\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])){3}$/.test(keyword);
	},
	/**
	 * 日期天数计算
	 * */
	dateAddDay:function(dd,dadd){
		var a = new Date(dd);
		a = a.valueOf();
		a = a + dadd * 24 * 60 * 60 * 1000;
		a = new Date(a);
		return a;
	},
	/**
	 * 让target浮动在事件e周围
	 * @param e 触发的事件
	 * @param target 要定位显示的目标对象
	 */
	float:function(e,target){
		var t=$(target),x=e.pageX,y=e.pageY,tw=t.width(),th=t.height(),offset=20,poffset=11,body=$('body'),bw=body.width(),bh=body.height(),
			dx,dy;
		if((x+tw+offset)>bw){
			dx=bw-tw-poffset;
		}else{
			dx=x+poffset;
		}
		
		if((y+th+offset)>bh){
			dy=bh-th-poffset;
		}else{
			dy=y+poffset;
		}
		t.css({left:dx,top:dy});
	},
	_float:0,//如果为弹出框则存储弹出框的顶层
	_clearFloat:function(){
		var f=this._float;
		if(typeof f!='number'){
			if(f.is('.window')){
				try{
					f.find('.window-body').dialog('close');	//此处有bug，以后解决
				}catch(e){
					log(e);
				}
			}else{
				f.hide();
			}
			this._float=1;
		}
	},
	/**
	 * @param target 要浮动的dialog或div层
	 * @param e 触发的事件
	 */
	showFloat:function(target,e){
		var t=this,tar=$(target),temp,cstr='oc-util-float-div';
		
		if(t._float==0){
			$(document).click(function(e){
				if($(e.target).closest('.'+cstr).length==0)t._clearFloat();
			});
		}
		
		t._clearFloat();
		
		temp=tar.closest('.window');
		if(temp.length>0){
			t._float=temp;
			temp.find('.window-body').dialog('open');
		}else{
			t._float=tar.show();
		}

		t._float.addClass(cstr);
		
		if(e){
			e.stopPropagation();
			t.float(e, t._float);
		}
	},
	ellipcis:function(dom,len){
		dom=$(dom);
		var v=dom.text();
		dom.attr('title',dom.text());
		if(len&&(typeof len=='number')){
			if(v&&v.length>len){
				dom.text(v.substr(0,len)+'...');
			}
		}else{
			dom.css('text-overflow','ellipsis');
		}
	},
	loginDecrypt : function(s){
		var r = "";
		var hexes = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F");
		for (var i = 0; i < s.length; i++) {
			r += hexes[s.charCodeAt(i) >> 4] + hexes[s.charCodeAt(i) & 0xF];
		}
		return r;
	},
	/**
	 * MD5 加密
	 * @param value 待加密字符
	 */
	MD5 : function(data){
		return CryptoJS.MD5(data).toString();
	},
	/**
	 * AES 加密
	 * @param key 加密秘钥
	 * @param data 待加密字符
	 */
	AESencrypt : function(data){
		var key = CryptoJS.enc.Utf8.parse("X2B4C6D8E0F2G4H6");
		var encrypted = CryptoJS.AES.encrypt(data, key, {
            mode: CryptoJS.mode.ECB,
			padding: CryptoJS.pad.Pkcs7
       });
       return encrypted.toString();
	},
	/**
	 * AES 解密
	 * @param key 解密秘钥
	 * @param data 待解密字符
	 * @Author zhangkai
	 */
	AESdecrypt : function(data){
		var key = CryptoJS.enc.Utf8.parse("X2B4C6D8E0F2G4H6");
		var decrypted = CryptoJS.AES.decrypt(data, key, {
			mode: CryptoJS.mode.ECB,
			padding: CryptoJS.pad.Pkcs7
		});
		return CryptoJS.enc.Utf8.stringify(decrypted);;
	},
	/**
	*是否是ie浏览器
	*@return boolean
	*/
	isIE : function(){
	   return (!!window.ActiveXObject) || ("ActiveXObject" in window);
	}
};

(function(){
	function WhileOne(jq,e,time,fn){
		var t=this;
		t._fn=fn;
		t._time=time;
		t._e=e;
		t._jq=$(jq).on(e,function(){
			t._id=oc.util.generateId();
			if(!t._flag){
				t._one();
			}
		});
	}
	
	WhileOne.prototype={
		constructor:WhileOne,
		_flag:false,//当前是否有定时任务
		_fn:0,//响应函数
		_time:0,
		_e:0,
		_jq:0,
		_id:0,//用于标记某个时间范围内后续是否有事件
		off:function(){
			this._jq.off(this._e);
		},
		_one:function(){
			var t=this;
			t._flag=true;
			setTimeout((function(id){
				return function(){
					if(t._id==id){//没有新的事件，进行操作
						t._fn&&t._fn();
						t._flag=false;
					}else{
						t._one();
					}
				};
			})(t._id),t._time);
		}
	};

	/**
	 * 在给定的时间范围内发生在某个元素上的事件只执行一次
	 * @param jq 发生事件的元素
	 * @param e jquery事件名称
	 * @param time 时间发生的最大间隔时间，超过该时间的事件将会被触发
	 * @param fn 执行的函数
	 */
	oc.util.whileOne=function(jq, e, time, fn){
		return new WhileOne(jq, e, time, fn);
	};
	
	function Session(){}
	
	Session.prototype={
		constructor:Session,
		_int:0,
		_holdInterval:0,
		hold:function(){
			if(!this._int){
				this._holdInterval=setInterval(function(){
					oc.util.ajax({url:oc.resource.getUrl('system/login/keepSession.htm')});
				},1000*60*20);
			}
			this._int++;
		},
		stopHold:function(){
			this._int--;
			if(this._int<0)this._int=0;
			if(this._int==0&&this._holdInterval){
				clearInterval(this._holdInterval);
				this._holdInterval=0;
			}
		}
	};
	
	oc.session=new Session();
})();

//oc.events.js
(function(A){oc.events={form:{loaded:"oc.loaded"},resize:"oc.events.resize",home:{userWorkbenchReady:"oc_home_userWorkbenchReady"}}})(jQuery);

//oc.validate.js
/**
 * 扩展easyui验证器
 */
oc.resource.loadI18n('oc.validate.js',function(){
	$.extend($.fn.validatebox.defaults.rules,{
		min:{
			validator:function(v,ps){
				return v>=ps[0];
			},
			message:oc.local.valid.min
		},max:{
			validator:function(v,ps){
				return v<=ps[0];
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
		},adminPassword:{
			validator:function(v,ps){
				var reg =/(^.{5,18}$)|(^.{32}$)/;
				return reg.test(v);
			},
			message:'密码长度需为5-18位！'
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

//oc.third.prototype.js
(function(){$.fn.linkbutton.defaults.plain=true;$.extend($.fn.dialog.defaults,{width:750,height:465,resizable:false,cache:true,modal:true,onClose:function(){$(this).dialog("destroy")}});$.extend($.fn.tabs.defaults,{fit:true,border:true});$.extend($.fn.accordion.defaults,{onBeforeSelect:function(C,B){this.selectedIdx=B},onBeforeUnSelect:function(C,B){if((typeof this.selectedIdx=="number")&&B==this.selectedIdx){return false}}});oc.ui.progress=function(B){$.messager.progress({text:""})};oc.ui.stopprogress=function(B){$.messager.progress('close')};$.fn.accordion.defaults.fit=true;$.fn.datebox.defaults.buttons.splice(1,1,{text:"清除",handler:function(B){B=$(B).datebox("clear").datebox("hidePanel");var C=B.datebox("options").placeholder;if(C){B.datebox("setText",C.toI18n())}}});$.fn.datetimebox.defaults.buttons.splice(2,1,{text:"清除",handler:function(B){B=$(B).datetimebox("clear").datetimebox("hidePanel");var C=B.datetimebox("options").placeholder;if(C){B.datetimebox("setText",C.toI18n())}}});var A=$.fn.combo.methods.clear;$.fn.combo.methods.clear=function(B){A(B);var C=B.combo("options").placeholder;if(C){comboSetText(B,C.toI18n())}return B};oc.ui.confirm=function(D,B,C){$.messager.confirm(oc.local.ui.confirm.title,D,function(E){if(E){B&&B()}else{C&&C()}})};$.ajaxSetup({beforeSend:function(B,C){if(C.data){C.data+="&dataType="+C.dataType}else{C.data="dataType="+C.dataType}},complete:function(B,D){if(D=="success"){try{B=JSON.parse(B.responseText);if(B.code==400){top.location.href=B.data}}catch(C){}}}});$.extend($.fn.treegrid.defaults.view,{_onAfterRender:$.fn.treegrid.defaults.view.onAfterRender,onAfterRender:function(J){var M=$(J),H=M.treegrid("options"),K=H.selectedCfg;if(K){var E=K.field,G=K.fieldValue,L=H.idField,B=M.treegrid("getData");H.view._onAfterRender(J);for(var F=0,D=B.length;F<D;F++){var C=B[F].children;for(var I in C){if(C[I][E]==G){M.treegrid("select",C[I][L])}}}}}});$.fn.filebox.defaults.buttonText="...";$.extend($.fn.linkbutton.methods,{RenderLB:function(B,C){return B.each(function(){var E=$(this);var D,F;C=C||{};E.linkbutton(C);D=E.find(".l-btn-left");F=$('<div class="btn-l">  <div class="btn-r">    <div class="btn-m"></div>  </div></div>');F.find(".btn-m").append(D.children());D.append(F)})}});$.fn.panel.defaults.loadingMessage="努力加载中...";$.extend($.fn.panel.methods,{RenderP:function(B,C){return B.each(function(){var E=$(this);var D,F;C=C||{};E.panel(C);D=E.parent().find(".panel-header");F=$('<div class="oc-header-r"><div class="oc-header-m"></div></div>'),btn_m=F.find(".oc-header-m").append(D.children());D.append(F)})}});$.extend($.fn.dialog.defaults,{onOpen:function(){var C=$(this),D,B=C.dialog("options");B._onOpen&&B._onOpen();if(B.isOcOpened){return}B.isOcOpened=true;setTimeout(function(){C.parent(".panel").find(".panel-header").each(function(){D=$('<div class="oc-header-r"><div class="oc-header-m"></div></div>'),btn_m=D.find(".oc-header-m").append($(this).children());$(this).append(D)});C.parent(".panel").find(".dialog-button .l-btn-left").each(function(){D=$('<div class="btn-l">  <div class="btn-r">    <div class="btn-m"></div>  </div></div>'),btn_m=D.find(".btn-m").append($(this).children());$(this).append(D)})},1)}});$.extend($.fn.window.defaults,{onOpen:function(){var B=$(this);setTimeout(function(){B.parent(".panel").find(".panel-header").each(function(){_html=$('<div class="oc-header-r"><div class="oc-header-m"></div></div>'),btn_m=_html.find(".oc-header-m").append($(this).children());$(this).append(_html)});B.parent(".panel").find(".messager-button .l-btn-left").each(function(){_html=$('<div class="btn-l">  <div class="btn-r">    <div class="btn-m"></div>  </div></div>'),   btn_m=_html.find('.btn-m').append($(this).children().hide()); if(_html.find('.l-btn-text')[0].innerHTML=='确定'){btn_m=_html.find(".btn-m").append('<span class="l-btn-icon fa fa-check-circle">&nbsp;</span><span class="l-btn-text" style="margin-left:18px">确定</span>'); }else{ btn_m=_html.find(".btn-m").append('<span class="l-btn-icon fa fa-times-circle">&nbsp;</span><span class="l-btn-text" style="margin-left:18px">取消</span>'); }  $(this).append(_html)})},1)}})})();
//oc.placeholder.js
(function($){$.fn.fixPlaceHolder=function(){var that=$(this),placeHolderFlag="placeholder" in document.createElement("input");if(placeHolderFlag){return that}var target=new Array();if(that.attr("placeholder")){target.push(that)}if(that.find("[placeholder]").size()){target.push(that.find("[placeholder]"))}if(!target.length){return that}$(target).each(function(){var self=$(this),txt=self.attr("placeholder")||"",holder=null;if(!txt.length){return false}if(self.is("textarea")){self.nextAll('textarea[name^="textAreaHolder"]')&&self.nextAll('textarea[name^="textAreaHolder"]').remove();holder=self.clone().attr("name","textAreaHolder"+Date.now())}else{if(self.is("input")){self.nextAll('input[name^="inputHolder"]')&&self.nextAll('input[name^="inputHolder"]').remove();holder=self.clone().attr("type","text").attr("name","inputHolder"+Date.now())}}if(holder){holder.attr("readonly","readonly").removeAttr("placeholder").unbind().val(txt).insertAfter(self);if(!$.trim(self.val()||"").length){holder.show();self.hide()}else{holder.hide();self.show()}self.focusin(function(e){e.stopPropagation();holder.hide()}).focusout(function(e){e.stopPropagation();var that=$(this);if(!$.trim(that.val()||"").length){holder.show();that.hide()}else{that.show();holder.hide()}});holder.click(function(e){e.stopPropagation();$(this).hide();self.show();self.focus()})}});that.closest("form").find(".resetBtn").click(function(e){e.stopPropagation();var holderTimeout=window.setTimeout(function(){$(target).each(function(){var current=$(this);current.hide().nextAll('[name*="Holder"]').show().val(current.attr("placeholder"));holderTimeout=null})},0)});return that}})(jQuery);