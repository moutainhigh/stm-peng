$(function(){
	function navigator(cfg){
		this.cfg = cfg;
		this.selector = cfg.selector;
		this.datas = cfg['data'];
		this.user = cfg['user'];
	}
	
	navigator.prototype = {
		cfg : undefined,
		selector : undefined,
		navList	: undefined,
		datas : undefined,
		user : undefined,
		topRightId : 0,
		_dom:{
		show:
			'<div class="show_health index_status_healthy_box">'+
			'<div class="content">'+
			'<div class="dcs">'+
				'<ul class="dcs_content"></ul>'+	
			'</div>'+
			'<div class="dhs">'+
				'<ul class="dhs_content"></ul>'+
			'</div>'+
			'<div class="memcatched">'+
				'<ul class="memc_content"></ul>'+
			'</div>'+
			'</div>'+
			'<div class="foot"></div>'
			},
		
			setMemcachedStatus:function(callBack){
				var that=this;
				//设置memcached
				oc.util.ajax({
					url : oc.resource.getUrl("system/service/getMemcachedStatus.htm"),
					success : function(result){
						var memc_num=0;
						var memcatched=that.healthDlg.find(".memcatched");
						var memc_content=that.healthDlg.find(".memc_content");
						var memc_title=$('<div class="title">MEMCACHED状态</div>');
						var memc_temp=$('<li class="memcState"><span class="memc_icon status"></span><span class="name">缓存服务器</span></li>');
						memcatched.prepend(memc_title);
						memc_content.append(memc_temp);
						if(result.code==200){
							var data = result.data;
							if(data==true){
								//设置绿色图标
								memc_temp.find(".memc_icon").addClass("alert-ico lightnormal");
							}else{
								//设置红色图标
								
								memc_num++;
								memc_temp.find(".memc_icon").addClass("alert-ico redlight");
							}
						}else{
							memc_num++;
							memc_temp.find(".memc_icon").addClass("alert-ico grizzly");
						}
						callBack.call(that,memc_num);
					}
				});
			},
			getData:function (){
				var that = this;
				oc.util.ajax({
					url : oc.resource.getUrl("system/service/getNodeForTree.htm"),
					success : function(result){
						if(result.data){
							var code = result.code;
							var data=result.data;
							var dhs=[];
							$.each(data,function(idx,_dhs){
								dhs.push({
									ip:_dhs.ip,
									port:_dhs.port,
									alive:_dhs.alive,
									children:_dhs.children
									
								});
							});
							that.setValue(dhs);
						}
					}
				});
			},
			setValue:function (value){
				var that = this;
				var dcs_title = $('<div class="title">DCS状态</div>');
				var dcs_content=that.healthDlg.find('.dcs_content');
				var dcs_temp = $('<li><span class="status"></span><span class="dcs_name"></span><span class="dcs_ip"></span>:<span class="dcs_port"></span></li>');
				var dcs_num = 0;
				var dhs_title = $('<div class="title">DHS状态</div>');
				var dhs_content=that.healthDlg.find(".dhs_content");
				var dhs_temp = $('<li><span class="status"></span><span class="dhs_name"></span><span class="dhs_ip"></span>:<span class="dhs_port"></span></li>');
				var dhs_num = 0;
				if(value!=null && value.length!=0){
					for (var i = 0; i < value.length; i++) {
						var dhs = value[i];
						var dhs_item = dhs_temp.clone();
						var state = true;
						//遍历当前dhs下的所有dcs
						for(var j = 0;j < dhs.children.length; j++){
							var dcs = dhs.children[j];
							var dcs_item=dcs_temp.clone();
							if(!dcs.alive){
								state = false;
							}
							if(dhs.alive){
								
								if(dcs.alive){
									dcs_item.find(".status").addClass("tree-icon-width25 alert-ico lightnormal");
								}else{
									dcs_item.find(".status").addClass("tree-icon-width25 alert-ico redlight");
									dcs_num++;
								}
							}else{
								dcs_item.find(".status").addClass("tree-icon-width25 alert-ico grizzly");
								dhs_num++;
							}
							that.healthDlg.find('.dcs').prepend(dcs_title);
							dcs_item.find(".dcs_name").text("采集器");
							dcs_item.find(".dcs_ip").text(dcs.ip);
							dcs_item.find(".dcs_port").text(dcs.port);
							dcs_content.append(dcs_item);
						}
						if(dhs.alive){
							if(state){
								dhs_item.find(".status").addClass("tree-icon-width25 alert-ico lightnormal");
							}else{
								var dcsArray=new Array();
								$.each(dhs.children,function(index,item){
									dcsArray[index]=item.alive;
								})
								if($.inArray(true,dcsArray)==-1&&dhs.children.length>1){
									dhs_item.find(".status").addClass("tree-icon-width25 alert-ico redlight");
								}else{
								dhs_item.find(".status").addClass("tree-icon-width25 alert-ico yellowlight");
								}
								dcs_num++;
							}
						}else{
							dhs_item.find(".status").addClass("tree-icon-width25 alert-ico redlight");
							dhs_num++;
						}
						that.healthDlg.find('.dhs').prepend(dhs_title);
						dhs_item.find('.dhs_name').text("处理器");
						dhs_item.find('.dhs_ip').text(dhs.ip);
						dhs_item.find('.dhs_port').text(dhs.port);
						dhs_content.append(dhs_item);
					};
				}
				
				//设置memcatched状态
				that.setMemcachedStatus(function(number){
					//获取异常的服务个数
					var num = dcs_num + dhs_num +number;
					//设置异常的系统服务个数
					var foot = that.healthDlg.find(".foot");
					var abnormal=$('<div class="service title">异常的服务 [ <span class="num"></span> ]</div>');
					if(num >= 0){
						foot.append(abnormal);
						foot.find(".num").text(num).css({"color":"#EC2428"});;
					}
				});
			},
			_health:0,
			showHealth:function(e){
				var that = this;
				if (!this._health) {
					var dia = this.healthDlg=$(this._dom.show).dialog({
						title:"系统健康度",
						width:320,
						height:'auto',
						modal:false,
						closed:true,
						_onOpen:function(){
							that.getData();
						}
					});
					oc.util.showFloat(dia, e);
				};
			},
				
		
		init:function(){
			$('.main-content').height(document.documentElement.scrollHeight - 45);
			$('#mainMenuDiv').height($('#main-container').height() - $('#sidebar-shortcuts-large').height() - $('#sidebar-collapse').height() - 18);
			oc.index.indexLayout = $('.main-content');
			oc.index.activeContent = $('.main-content').find('#mainMenuContent');
			var that = this;
			this.navList = that.selector.find('#mainMenu');
			that.mainContent = that.selector.parent().find('#mainMenuContent');
			
			that._initNav();
			
		},
		_initNav :function(){
			var that = this;
			var navListObj = that.navList;
			var rights = that.datas;
			var user = that.user;
			var parentMenu = new Array(),childrenMenu = new Array();
			for(var i=0;i<rights.length;i++){
							
				var rightTemp = rights[i];
				var pid = rightTemp['pid'];
				var fileId = rightTemp['fileId'];
				var name = rightTemp['name'];
				var url = rightTemp['url'];
				var id = rightTemp['id'];
				if(pid&&pid>0){
					
					if(!user.systemUser){						
						if(name=='人员管理' || name == '指标自定义'){							
							continue;
						}
					};
					if(user.commonUser&&user.managerUser){
						if(name=='策略'){
							continue;
						}
					};
					childrenMenu.push(rightTemp);
				}else{
					//渲染到页面
					var setting = that.iconAndSettingSwitch(name);
					var menuArrow = '';
					var ifHaveChildren = that.checkChildren(id,rights);
					
					url = url.replace('resource\/','');
					
					var li = $('<li>').attr('class','module-href');//.attr('module',url)
					var a = $('<a>').attr('href','#').attr('rightMark','rights').attr('module',url).attr('rightId',id).attr('type',rightTemp.type).attr('isNewTag',rightTemp.isNewTag);
					var b = $('<b>').attr('class','menu_arrow');
					var ul = $('<ul>').attr('class','submenu');//.html('<li class=""><a href="#"><i class="left-menu-icon fa fa-caret-right"></i>人员管理</a></li>');
					li.append(a);
					if(ifHaveChildren){
						parentMenu.push(rightTemp);
						menuArrow = '<b class="menu_arrow fa fa-angle-down"></b>';
						a.attr('class','dropdown-toggle').attr('module','');
						li.append(b).append(ul);
					}
					a.html('<i class="'+setting.icon+'"></i><span class="left-menu-text">'+name+'</span>'+menuArrow);
					rightTemp['jqObj'] = ul;
					
					navListObj.append(li);
				}
			}
			oc.index.getSystemLogo(function(url){
				$(".navbar-brand").css("background","url("+url+")  no-repeat");
			});
			
			$('#mainMenu').find('>li').mouseover(function(){
				if($('#sidebar').hasClass('menu-min')){
					$('.sidebar.menu-min .nav-list > li > a > .left-menu-text').css('display','none');
					$('.sidebar.menu-min .nav-list > li > .submenu').css('display','none');
					$(this).find('.left-menu-text').first().css('display','block');
					$(this).find('.submenu').first().css('display','block');
					var offsetTop = $(this).offset().top;
					var maxHeight = 0;
					if($(this).find('>.submenu') && $(this).find('>.submenu').length > 0 && $(this).find('>.submenu').attr('childnum')){
						maxHeight = parseInt($(this).find('>.submenu').attr('childnum')) * 40;
					}else{
						maxHeight = 0;
					}
					if(offsetTop + maxHeight > $('#main-container').height()){
						if($('#main-container').height() - offsetTop < 250){
							$(this).find('>.submenu').height(250).css('overflow','auto');
							offsetTop = offsetTop - (250 - ($('#main-container').height() - offsetTop));
						}else{
							$(this).find('>.submenu').height($('#main-container').height() - offsetTop).css('overflow','auto');
						}
					}
					$('.sidebar.menu-min .nav-list > li > a > .left-menu-text').css('top',offsetTop+1);
					$('.sidebar.menu-min .nav-list > li > .submenu').css('top',offsetTop);
				}
				
				$('#mainMenu').find('>li>a[rightid!=' + that.topRightId + ']').removeClass('oc-nav-list-a-hover').addClass('oc-nav-list-a');
				$(this).find('>a').removeClass('oc-nav-list-a').addClass('oc-nav-list-a-hover');
				
			});
			
	        $(document).mousedown(function(e){
	        	e.stopPropagation();
	        	if($(e.target).hasClass('window-mask')){
	        		return;
	        	}
	        	if(!$(e.target).closest("ul#mainMenu") || $(e.target).closest("ul#mainMenu").length <= 0){
	        		if($('#sidebar').hasClass('menu-min')){
	        			$('#mainMenu>li>a>span.left-menu-text').css('display','none');
	        			$('#mainMenu>li>ul.submenu').css('display','none');
	        		}
	        	}
	        	$('#mainMenu').find('>li>a[rightid!=' + that.topRightId + ']').removeClass('oc-nav-list-a-hover').addClass('oc-nav-list-a');
	        });
			
			that.renderChildrenMenu(parentMenu,childrenMenu);
			that.selector.find('ul.submenu').each(function(){
				$(this).attr('childnum',$(this).find('>li').length);
			});
			$('#mainMenu').find('>li>ul').each(function(){
				var childNum = 0;
				var maxNum = 0;
				$(this).find('>li').each(function(){
					childNum++;
					if($(this).find('ul') && $(this).find('ul').length > 0){
						if(parseInt($(this).find('ul').attr('childnum')) > maxNum){
							maxNum = parseInt($(this).find('ul').attr('childnum'));
						}
					}
				});
				$(this).attr('childnum',childNum + maxNum);
			});
			that.renderClick();
			
		},
		checkChildren:function(id,rights){
			//检察是否有子菜单
			for(var x=0;x<rights.length;x++){
				var rightX = rights[x];
				var pidx = rightX['pid'];
				if(pidx && pidx>0 && pidx==id){
					return true;
				}
			}
			return false;
		},
		renderChildrenMenu :function(parentMenu,childrenMenu){
			var that = this;
			var user = that.user;
			
			for(var i=0;i<parentMenu.length;i++){
				var parentM = parentMenu[i];
				var id = parentM['id'];
				var parentJQ = parentM['jqObj'];
				var levelParent = new Array();
				var levelChildren = new Array();
				for(var x=0;x<childrenMenu.length;x++){
					var childrenM = childrenMenu[x];
					var pid = childrenM['pid'];
					var name = childrenM['name'];
					var url = childrenM['url'];
					var cid = childrenM['id'];
					
					if(pid&&id==pid){
						//渲染到相应菜单下
						url = url!=null ? url.replace('resource\/','') : '';
						var li = $('<li>');
						var a = $('<a>').attr('href','#').attr('module',url).attr('rightMark','rights').attr('rightMarkName',name).attr('rightId',cid);
						var ifHaveChildren = that.checkChildren(cid,childrenMenu);
						var menuArrow='';
						li.append(a);
						if(ifHaveChildren){
							var b = $('<b>').attr('class','menu_arrow');
							var ul = $('<ul>').attr('class','submenu');
							menuArrow = '<b class="menu_arrow fa fa-angle-down"></b>';

							a.attr('class','dropdown-toggle').attr('module','');
							li.append(b).append(ul);
							
							childrenM['jqObj'] = ul;
							levelParent.push(childrenM);
						}
						a.html('<i class="left-menu-icon"></i><span class="left-menu-text">'+name+'</span>'+menuArrow);// fa fa-caret-right
						parentJQ.append(li);
					}else{
						levelChildren.push(childrenM);
					}
				}
				
				if(levelParent.length>0&&levelChildren.length>0){
					that.renderChildrenMenu(levelParent,levelChildren);
				}
			}
			
		},
		iconAndSettingSwitch:function(name){
			var icon = 'left-menu-icon ';
			
			switch(name){
				
				case '视频设备管理':
					icon += 'fa fa-video-camera';
				break;
			
				case '业务管理':
					icon += 'fa fa-briefcase';
				break;
				case '拓扑管理':
					icon += 'fa fa-sitemap';
				break;
				case '资源管理':
					icon += 'fa fa-globe';
				break;
				case '配置文件管理':
					icon += 'fa fa-folder';
				break;
				case '巡检管理':
					icon += 'icon-inspection';
				break;
				case '报表管理':
					icon += 'fa fa-pie-chart';
				break;
				case '告警管理':
					icon += 'fa fa-bell';
				break;
				case '系统管理':
					icon += 'fa fa-cog';
				break;
				case '3D机房':
					icon += 'fa fa-cube';
				break;
				case '虚拟化':
					icon += 'fa fa-cogs';
				break;
				case '首页':
				icon += 'fa fa-home';
				break;
				default :
					icon += 'fa fa-desktop';
					break;
					
			}
			return {icon:icon};
		},
		renderClick:function(){
			//菜单点击
			var that = this;
			that.selector.find('a[rightMark=rights]').click(function(e){
				
				var target = $(this);
				var targetUrl = target.attr('module');
				
				if(!targetUrl){
					return;
				}

				//每次点击左侧菜单栏都清除首页自动刷新
				window.clearInterval(window.timeid); 
				window.clearInterval(window.fullScreenTimeId); 
				if(oc.index.home != null && oc.index.home.widgetMger != null && oc.index.home.widgetMger.gridster != null){
					oc.index.home.widgetMger.gridster.destroy();
					oc.index.home.widgetMger = null;
				}
				
				that.topRightId = target.parents('li').last().find('>a').attr('rightid');
				$('#mainMenu').find('>li>a[rightid!=' + that.topRightId + ']').removeClass('oc-nav-list-a-hover').addClass('oc-nav-list-a');
				target.parents('li').last().find('>a').removeClass('oc-nav-list-a').addClass('oc-nav-list-a-hover');
				
				if($('#sidebar').hasClass('menu-min')){
					$('#mainMenu>li>a>span.left-menu-text').css('display','none');
					$('#mainMenu>li>ul.submenu').css('display','none');
				}
				
				that.selector.find('li').removeClass('highlight');
				that.selector.find('li').removeClass('pull_up');
				$(e.target).parents('li').addClass('highlight');
				
				if(!oc.index._activeRight){
					oc.index._activeRight=1;
				}
				
				var content = that.mainContent;
				content.html('');
				if(!that.bodyHeight){
					that.bodyHeight = $('.main-content').height() - 23;
				}
				content.height(that.bodyHeight);
				
				var type = target.attr('type');
				var isNewTag = target.attr('isNewTag');
				var url;
				var rightId = target.attr('rightId');
				if(rightId>=0){
					oc.index._activeRight=rightId;
				}
				
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					for(var i=0,len=tasks.length; i<len; i++){
						tasks[i]&&clearInterval(tasks[i]);
					}
					oc.index.indexLayout.data("tasks",new Array());
				}
				
				var moduleName = target.find('span').html();
				if(moduleName == '首页' || moduleName == '业务管理' || moduleName == '拓扑管理'){
					$('#main-container').addClass('oc-maincontainer');
				}else{
					$('#main-container').removeClass('oc-maincontainer');
				}
				
				$('.main-content').unbind('mouseover');
				if(!type || type == 0){
					content.load(targetUrl);
				}else if(type == 1 && isNewTag == 1){
					window.open(targetUrl,moduleName);
				}else{
					var sessionId;
					oc.util.ajax({
						url : oc.resource.getUrl('system/login/getSessionId.htm'),
						successMsg : null,
						success : function(data) {
							sessionId = data.data;
							var bool = targetUrl.indexOf("?");
							if(bool>0){
								targetUrl = targetUrl+'&sessionid='+sessionId;
							}else{
								targetUrl = targetUrl+'?sessionid='+sessionId;
							}
							$('<iframe style="width:100%;height:100%; border: 0px;"/>').attr("src", targetUrl).appendTo(content);
					        $('.main-content').mouseover(function(e){
					        	e.stopPropagation();
				        		if($('#sidebar').hasClass('menu-min')){
				        			$('#mainMenu>li>a>span.left-menu-text').css('display','none');
				        			$('#mainMenu>li>ul.submenu').css('display','none');
				        		}
					        	$('#mainMenu').find('>li>a[rightid!=' + that.topRightId + ']').removeClass('oc-nav-list-a-hover').addClass('oc-nav-list-a');
					        });
						}
					});
				}
			});
			
			that.selector.find('.helpManual').click(function(e){
				//每次点击左侧菜单栏都清除首页自动刷新
				window.clearInterval(window.timeid);
				window.clearInterval(window.fullScreenTimeId); 
				if(oc.index.home != null && oc.index.home.widgetMger != null && oc.index.home.widgetMger.gridster != null){
					oc.index.home.widgetMger.gridster.destroy();
					oc.index.home.widgetMger = null;
				}
				
				var rightId = oc.index._activeRight;
				
				var content = that.mainContent;
				var targetDom = that.selector.find('a[rightMark=rights][rightid='+ rightId+']');
				var targetUrl = targetDom.attr('module');
				
				if(!that.bodyHeight){
					that.bodyHeight = $('.main-content').height() - 23;
				}
				
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					for(var i=0,len=tasks.length; i<len; i++){
						tasks[i]&&clearInterval(tasks[i]);
					}
					oc.index.indexLayout.data("tasks",new Array());
				}
				
				content.height(that.bodyHeight);
				content.html('');
				content.load("module/help/guide.html");
			});
			that.selector.find('.btn-success').click(function(e){
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					for(var i=0,len=tasks.length; i<len; i++){
						tasks[i]&&clearInterval(tasks[i]);
					}
					oc.index.indexLayout.data("tasks",new Array());
				}
				that.showHealth(e);
			});
			that.selector.find('.license').click(function(e){
				//每次点击左侧菜单栏都清除首页自动刷新
				window.clearInterval(window.timeid); 
				window.clearInterval(window.fullScreenTimeId); 
				if(oc.index.home != null && oc.index.home.widgetMger != null && oc.index.home.widgetMger.gridster != null){
					oc.index.home.widgetMger.gridster.destroy();
					oc.index.home.widgetMger = null;
				}
				
				var rightId = oc.index._activeRight;
				
				var content = that.mainContent;
				var targetDom = that.selector.find('a[rightMark=rights][rightid='+ rightId+']');
				var targetUrl = targetDom.attr('module');

				content.html('');
				if(!that.bodyHeight){
					that.bodyHeight = $('.main-content').height() - 23;
				}
				content.height(that.bodyHeight);
				
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					for(var i=0,len=tasks.length; i<len; i++){
						tasks[i]&&clearInterval(tasks[i]);
					}
					oc.index.indexLayout.data("tasks",new Array());
				}
				
				content.attr('style','display:block;');
				content.load("stm-license.html");
			});
			
			//极速 极简
			that.selector.find('.sidebarButton').click(function(e){
				//每次点击左侧菜单栏都清除首页自动刷新
				window.clearInterval(window.timeid); 
				window.clearInterval(window.fullScreenTimeId); 
				if(oc.index.home != null && oc.index.home.widgetMger != null && oc.index.home.widgetMger.gridster != null){
					oc.index.home.widgetMger.gridster.destroy();
					oc.index.home.widgetMger = null;
				}
				
				if(window.onecenterFlag){
					//门户界面
					top.location.href=oc.resource.getUrl('resource/itm_simple.html');
				}else{
					top.location.href=oc.resource.getUrl('resource/index_simple.html');
				}

			});
			
			//默认显示第一个菜单
			that.selector.find('a[rightMark=rights]').each(function(){
				if($(this).attr('module')){
					$(this).trigger('click');
					return false;
				}
			});
			
		}
	}
	
	oc.ns('oc.index.navigator');
	
	var navi = null;
	
	oc.index.navigator={
			
		open:function(cfg){
			
			oc.index.loadLoginUser(function(user){
				if(user.rights.length==0){

				}else{
					cfg['user'] = user;
					cfg['data'] = user.rights;
					
					navi = new navigator(cfg);
					navi.init();
				}
				
			});
		},
		moduleChange:function(ids,show){
				
			if(show){
				oc.index.loadLoginUser(function(user){
					var idArray = ids.split(',');
					
					for(var x = 0 ; x < idArray.length ; x++){
						var thridModuleSort = 0;
						var thridModuleRight = null;
						for(var i = 0 ; i < user.rights.length ; i ++){
							if(user.rights[i].id == idArray[x]){
								thridModuleSort = user.rights[i].sort;
								thridModuleRight = user.rights[i];
								break;
							}
						}
						var curModuleId = 0;
						for(var i = 0 ; i < user.rights.length ; i ++){
							if(user.rights[i].sort == thridModuleSort){
								break;
							}
							if(user.rights[i].pid == 0){
								curModuleId = user.rights[i].id;
							}
						}
						
						var setting = navi.iconAndSettingSwitch(thridModuleRight.name);
						var menuArrow = '';
						
						var url = thridModuleRight.url.replace('resource\/','');
						
						var li = $('<li>').attr('class','module-href');//.attr('module',url)
						var a = $('<a>').attr('href','#').attr('rightMark','rights').attr('module',url).attr('rightId',thridModuleRight.id).attr('type',thridModuleRight.type).attr('isNewTag',thridModuleRight.isNewTag);
						var b = $('<b>').attr('class','menu_arrow');
						var ul = $('<ul>').attr('class','submenu');//.html('<li class=""><a href="#"><i class="left-menu-icon fa fa-caret-right"></i>人员管理</a></li>');
						li.append(a);
						a.html('<i class="'+setting.icon+'"></i><span class="left-menu-text">'+thridModuleRight.name+'</span>');
						
						$('#mainMenuDiv').find('a[rightMark=rights][rightid=' + curModuleId + ']').parent().after(li);
						
						a.bind('click',function(e){
							
							$('#mainMenuDiv').find('li').removeClass('highlight');
							$('#mainMenuDiv').find('li').removeClass('pull_up');
							$(e.target).parents('li').addClass('highlight');
							
							var target = $(this);
							var moduleName = target.find('span').html();
							
							var targetUrl = target.attr('module');
							var type = target.attr('type');
							var isNewTag = target.attr('isNewTag');
							var url;
							var rightId = target.attr('rightId');
							if(rightId>=0){
								oc.index._activeRight=rightId;
							}
							
							if(type == 1 && isNewTag == 1){
								window.open(targetUrl,moduleName);
							}else{
			    				var sessionId;
			    				oc.util.ajax({
			    					url : oc.resource.getUrl('system/login/getSessionId.htm'),
			    					successMsg : null,
			    					success : function(data) {
			    						sessionId = data.data;
			    						var bool = targetUrl.indexOf("?");
			    						if(bool>0){
			    							targetUrl = targetUrl+'&sessionid='+sessionId;
			    						}else{
			    							targetUrl = targetUrl+'?sessionid='+sessionId;
			    						}
			    						$('<iframe style="width:100%;height:100%; border: 0px;"/>').attr("src", targetUrl).appendTo(content);
			    					}
			    				});
							}
						
						});
					}
					
				});
			}else{
				var idArray = ids.split(',');
				
				for(var x = 0 ; x < idArray.length ; x++){
					$('#mainMenuDiv').find('a[rightMark=rights][rightid=' + idArray[x] + ']').parent().remove();
				}
			}
				
		}
	}
	
});