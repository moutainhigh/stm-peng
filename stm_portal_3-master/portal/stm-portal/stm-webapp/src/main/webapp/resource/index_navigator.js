/**
 * 首页js脚本
 * @param $
 */
$(function(){
	var indexLayout=$('#oc_index_layout'),
		indexContent=$('#indexContent'),
		updatePasswordDlg=0,
		headerDiv=indexLayout.layout('panel','north').find('div.header:first'),
		divContent=$('<div class="oc-content" style="height: 100%;width: 100%; display: none;"></div>'),
		iframeContent=$('<iframe class="oc-content" style="height: 100%;width: 100%;overflow:hidden; display: none;"></iframe>');
	
	function _init(){
		oc.index.loadUserResponse=function(user){
		};
		
		/*************************************/
		//处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外  
		function banBackSpace(e){   
		    var ev = e || window.event;//获取event对象     
		    var obj = ev.target || ev.srcElement;//获取事件源     
		      
		    var t = obj.type || obj.getAttribute('type');//获取事件源类型    
		    //获取作为判断条件的事件类型  
		    var vReadOnly = obj.getAttribute('readonly');  
		    var vEnabled = obj.getAttribute('enabled');  
		    //处理null值情况  
		    vReadOnly = (vReadOnly == null) ? false : vReadOnly;  
		    vEnabled = (vEnabled == null) ? true : vEnabled;  
		      
		    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，  
		    //并且readonly属性为true或enabled属性为false的，则退格键失效  
		    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")   
		                && (vReadOnly==true || vEnabled!=true))?true:false;  
		    
		    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效  
		    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea") && t!=null 
		                ?true:false;          
		    //判断  
		    if(flag2){  
		        return false;  
		    }  
		    if(flag1){     
		        return false;     
		    }     
		}  
		  
		//禁止后退键 作用于Firefox、Opera  
		document.onkeypress=banBackSpace;  
		//禁止后退键  作用于IE、Chrome  
		document.onkeydown=banBackSpace; 
		/*************************************/
		
		oc.index.loadLoginUser(function(user){
			if(user.rights.length==0){
				indexContent.html('<div class="undistributed_Role_Tip">亲爱的<span>'+user.name+'</span>,您还没有分配系统角色，请联系系统管理员或域管理员分配角色!</div>');
			}else{
				_initNav(user.rights);
			}
			var status=oc.index.readyStatus({
				selector:headerDiv.find('.header_right'),
				user:user
			});
			oc.index.loadUserResponse=function(){
				status.setMarquee();
			};
		});
		oc.index.getSystemLogo(function(url){
			$(".header .header-left").css("background","url("+url+")  no-repeat");
		});
		oc.index.indexLayout=indexLayout;
		oc.index.indexLayout.data("tasks", []);
	}
	function _initNav(rights){
		var _statusImgUrl = oc.resource.getUrl('resource/themes/'+Highcharts.theme.currentSkin+'/images/default.png')
			,myCaches={};
		var nav=oc.index.nav=headerDiv.find(".header-menus").oc_nav({
	    	textField:'name',
	    	hrefFiled:'url',
	    	click:function(href,d,ds){
//	    		debugger;
	    		oc.index._activeRight=d.id;
    			if(d.type==0){
    				var url=oc.index.activeHref=oc.resource.getUrl(href),self;
    				if(d.id==17 || d.id==9001){
    					self = $('<iframe style="width:100%;height:100%; border: 0px;"/>').attr("src", d.url).appendTo(indexContent.find('> div:first'));
    				}else{
//    					if(!(self=myCaches[url])){
//    					    self=$('<div style="width:100%;height:100%;"/>')
//								.appendTo(indexContent.find('> div:first'));
//	    				    myCaches[url]=self;
//    					}
						indexContent.find('> div:first').html('');
						self=$('<div style="width:100%;height:100%;"/>')
							.appendTo(indexContent.find('> div:first'));
						self.load(url);
    				}
    				
    				var tasks = oc.index.indexLayout.data("tasks");
    				for(var i=0,len=tasks.length; i<len; i++){
    					tasks[i]&&clearInterval(tasks[i]);
    				}
        			(oc.index.activeContent=self).show().siblings().hide();
        			return true;
	    		}else if(d.type==1 && d.isNewTag==1){
	    			window.open(href,d.name);
	    		}else {
	    			var url=href,self;
    				self = $('<iframe style="width:100%;height:100%; border: 0px;"/>').attr("src", d.url).appendTo(indexContent.find('> div:first'));
    				var tasks = oc.index.indexLayout.data("tasks");
    				for(var i=0,len=tasks.length; i<len; i++){
    					tasks[i]&&clearInterval(tasks[i]);
    				}
        			(oc.index.activeContent=self).show().siblings().hide();
        			return true;
	    		}
	    	},
	    	background:true,
	    	filter:function(datas){
	    		for(var i=0,len=datas.length,d;i<len;i++){
	    			d=datas[i];
	    			d.imgUrl = d.fileId!=0 ? oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId=' + d.fileId) : _statusImgUrl;
	    			if(d.id==14){
	    				var sessionId;
	    				oc.util.ajax({
	    					url : oc.resource.getUrl('system/login/getSessionId.htm'),
	    					successMsg : null,
	    					async:false,
	    					success : function(data) {
	    						sessionId = data.data;
	    					}
	    				});
	    				
	    				var ss=d.url.split('?');
//	    				d.url=ss[0]+'?username='+oc.index.getUser().account+'&'+ss[1];
	    				d.url=ss[0]+'?sessionid='+sessionId;
	    			}
	    			if(d.id>10000){
	    				var sessionId;
	    				oc.util.ajax({
	    					url : oc.resource.getUrl('system/login/getSessionId.htm'),
	    					successMsg : null,
	    					async:false,
	    					success : function(data) {
	    						sessionId = data.data;
	    					}
	    				});
	    				var bool = d.url.indexOf("?");
	    				if(bool>0){
	    					d.url=d.url+'&sessionid='+sessionId;
	    				}else{
	    					d.url=d.url+'?sessionid='+sessionId;
	    				}
	    			}
	    		}
	    		return datas;
	    	},
	    	datas:rights
	    });
		
		nav._ul.find('li:first').click();
	}

	_init();
});