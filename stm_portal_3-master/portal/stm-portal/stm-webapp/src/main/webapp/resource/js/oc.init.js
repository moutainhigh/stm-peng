/**
 * 做一些前端界面或js对象的初始化操作
 * @author ziwenwen
 */
if(!oc){
	var oc={
		nsp:{
			//命名规则如下正在表达式
			regex:/^[a-z]+(\.[a-z]+)*$/,
			parse:function(parent,alias,cur){
				var alia=alias[cur];
				var newName=parent[alia];
				if(!newName){
					newName=parent[alia]={};
				}
				if(cur==alias.length-1){
					return newName;
				}else{
					return this.parse(newName, alias, ++cur);
				}
			},
			randomVal:((Math.random()*999999999*Math.random()*999999999).toString().split('.')[0]),
			generateConfirm:function(val){
				var falsify='falsify'+val;
				this[falsify]=this.regex;
				this[val]=function(){
					if(this.regex!=this[falsify]){
						this.regex=this[falsify];
					}
					return true;
				};
			}
		},
		//设置或者获取一个命名空间
		ns:function(alia){
			if(oc.nsp[oc.nsp.randomVal]){
				if(alia.match(oc.nsp.regex)){
					var alias=alia.split('.');
					return oc.nsp.parse(window,alias,0);
				}else{
					alert(alia+':命名空间设置不合法,命名规则:'+oc.nsp.regex);
				}
			}
		}
	};
	
	//开启防篡改
	oc.nsp.generateConfirm(oc.nsp.randomVal);
	
	/**
	 * 资源对象
	 * 1、包装动态资源路径
	 */
	oc.resource={
		lang:(window.navigator.userLanguage||window.navigator.language).toLowerCase(),
		href:window.location.href,
		baseUrl:window.location.pathname.split('resource')[0],//形如/oc/等
		linkDom:$('<link rel="stylesheet">'),
		scriptDom:document.createElement('script'),//$('<script type="text/javascript"></script>'),
		headDom:document.getElementsByTagName('head')[0],
		cssStyle:'resource/themes/default/',
		init:function(){
		},
		local:{
			'zh-cn':'zh-CN',
			'en-us':'en-US'
		},
		resources:{},
		addEvent:function(elem,e,fn){
//			if(e=='load'&&$.browser.msie){
			if(e=='load'&&navigator.appName=='Microsoft Internet Explorer'){
				elem.onreadystatechange=function(){
					if('complete'==elem.readyState||'loaded'==elem.readyState){
						fn();
					}
				};
			}else{
				if ( elem.attachEvent) {
					elem.attachEvent( "on" + e, fn );
				} else if ( elem.addEventListener  ) {
					elem.addEventListener( e, fn, false );
				}
			}
		},
		getUrl:function(url){
			return this.baseUrl+url;
		},
		loadScript:function(url,fn){
			fn=fn||$.noop;
			if(!this.resources[url]){
				var script=this.scriptDom.cloneNode();
				script.setAttribute('src', this.baseUrl+url);
				this.addEvent(script,'load', function(){
					fn&&fn();
				});
				this.headDom.appendChild(script);
				this.resources[url]=true;
			}else{
				fn&&fn();
			}
		},
		loadScripts:function(urls,fn){
			var i=0,len=urls.length,url;
			if(len<1)return;
			function load(){
				if(i==len){
					fn&&fn();
					return;
				}
				url=urls[i++];
				oc.resource.loadScript(url,load);
			}
			load();
		},
		loadCss:function(url,force){
			if(this.resources[url]&&!force)return;
			var link=this.linkDom.clone();
			link.attr('href',this.baseUrl+url);
			$(this.headDom).append(link);
			this.resources[url]=true;
		},
		getCss:function(relativeUrl){
			return this.baseUrl+this.cssStyle+relativeUrl;
		},
		loadI18n:function(jsName,fn){
			this.loadScript(this.getI18nUrl(jsName), fn);
		},
		getI18nUrl:function(jsName){
			return 'resource/i18n/'+this.local[this.lang]+'/'+jsName;
		}
	};
	
	oc.resource.common={//用于存放一些常用的资源地址
		dict:oc.resource.getUrl('dict/get.htm')//数据字典
	};
	
	oc.ns('oc.ui');
	
	oc.isSimple=oc.resource.href.indexOf('index_simple')!=-1;
	
	oc.resource.loadI18n('oc.ui.js');
}

