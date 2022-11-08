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
		if (typeof rfs != "undefined" && rfs) {
			rfs.call(el);
		} else if (typeof window.ActiveXObject != "undefined") {
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
		} else if (typeof window.ActiveXObject != "undefined") {
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
