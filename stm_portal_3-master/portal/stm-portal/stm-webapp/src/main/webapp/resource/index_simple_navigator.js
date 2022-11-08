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
		simpleList : undefined,
		datas : undefined,
		user : undefined,
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
								memc_temp.find(".memc_icon").addClass("light-ico_resource ava_available");
							}else{
								//设置红色图标
								
								memc_num++;
								memc_temp.find(".memc_icon").addClass("light-ico_resource ava_notavailable");
							}
						}else{
							memc_num++;
							memc_temp.find(".memc_icon").addClass("light-ico_resource res_unknown_nothing");
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
									dcs_item.find(".status").addClass("tree-icon-width25 light-ico_resource ava_available");
								}else{
									dcs_item.find(".status").addClass("tree-icon-width25 light-ico_resource ava_notavailable");
									dcs_num++;
								}
							}else{
								dcs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_unknown_nothing");
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
								dhs_item.find(".status").addClass("tree-icon-width25 light-ico_resource ava_available");
							}else{
								dhs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_critical");
								dcs_num++;
							}
						}else{
							dhs_item.find(".status").addClass("tree-icon-width25 light-ico_resource ava_notavailable");
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
			$('.main-content').height(document.documentElement.scrollHeight - $('#navbar').height());
			$('#mainMenuDiv').height($('#main-container').height() - $('#sidebar-shortcuts-large').height() - $('#sidebar-collapse').height() - 18);
			oc.index.indexLayout = $('.main-content');
			oc.index.activeContent = $('.main-content');
			var that = this;
			this.simpleList = that.selector.find('#simpleMenu');
			that.simpleContent = that.selector.parent().find('#simpleMenuContent');
			
			that._initNav();
			
			
		},
		_initNav :function(){

			this.appendOtherRights();
			
			this.renderClick();
			
		},
		appendOtherRights:function(){
			var that = this;
			var simListObj = that.simpleList;
			
			//添加极简模式菜单
			var simpleRight = new Array();
			var user = that.user;
			//根据是否有业务模块,是否显示工作台
			oc.util.ajax({
	    		url:oc.resource.getUrl('simple/manager/workbench/ifHaveBizAuthority.htm'),
	    		success:function(data){
	    			if(data.data){
						if(user.managerUser){
							simpleRight.push({name:'工作台',url:'module/simple/manager/workbench/index.html',id:'workbench'});
						}else{
							simpleRight.push({name:'工作台',url:'module/simple/engineer/workbench/index.html',id:'workbench'});
						}
	    			}
	    			if(!user.managerUser){
	    				simpleRight.push({name:'全局搜索',url:'module/simple/engineer/workbench/search/globalsearch.html',id:'globalsearch'});
	    			}
	    			if(user.managerUser){
	    				simpleRight.push({name:'最佳实践',url:'module/simple/manager/workbench/best_practice.html',id:'practice'});	
	    			}else{
	    				simpleRight.push({name:'最佳实践',url:'module/simple/engineer/workbench/best_practice.html',id:'practice'});
	    				
	    			}
	    			
	    			
	    			//极简模式菜单
	    			for(var i=0;i<simpleRight.length;i++){
	    				var temp = simpleRight[i];
	    				
	    				var li = $('<li>').attr('class','module-href');
	    				var a = $('<a>').attr('href','#').attr('rightMark','simpleRights').attr('module',temp.url).attr('rightId',temp.id)
	    					.html('<i class="' + that.iconAndSettingSwitch(temp.name).icon + '"></i><span class="left-menu-text">'+temp.name+'</span>');
	    				li.append(a);
	    				simListObj.append(li);
	    			}
	    			
	    			that.selector.find('a[rightMark=simpleRights]').click(function(e){
	    				that.selector.find('li').removeClass('highlight');
	    				$(e.target).parents('li').addClass('highlight');
	    				if(!oc.index._activeRight){
	    					oc.index._activeRight=1;
	    				}
	    				
	    				var content = that.simpleContent;
	    				content.html('');
	    				if(!that.bodyHeight){
	    					that.bodyHeight = document.documentElement.scrollHeight - $('#navbar').height() - 23;
	    				}
	    				content.height(that.bodyHeight);
	    				
	    				var target = $(this);
	    				var targetUrl = target.attr('module');
	    				var url;
	    				var rightId = target.attr('rightId');
	    				if(rightId>0){
	    					oc.index._activeRight=rightId;
	    				}
	    				
	    				var tasks = oc.index.indexLayout.data("tasks");
	    				if(tasks && tasks.length > 0){
	    					for(var i=0,len=tasks.length; i<len; i++){
	    						tasks[i]&&clearInterval(tasks[i]);
	    					}
	    					oc.index.indexLayout.data("tasks",new Array());
	    				}
	    				
	    				if(targetUrl){
	    					content.load(targetUrl);
	    				}else{
	    					target.parent().find('ul:first').find('a:first').trigger('click');
	    				}
	    			});
	    			
	    			//默认显示第一个菜单
	    			that.selector.find('a[rightMark=simpleRights]').first().trigger('click');
	    			
	    		}
	    	});
			
		},
		iconAndSettingSwitch:function(name){
			var icon = 'left-menu-icon fa ';
			
			switch(name){
				case '工作台':
					icon += 'fa-laptop';
				break;
				case '全局搜索':
					icon += 'fa-search';
				break;
				case '最佳实践':
					icon += 'fa-flag';
				break;
				case '首页':
				default :
					icon += 'fa-home';
					break;
			}
			return {icon:icon};
		},
		renderClick:function(){
			//菜单点击
			var that = this;
			
			that.selector.find('.helpManual').click(function(e){
				var rightId = oc.index._activeRight;
				
				var content = that.simpleContent;
				var targetDom = that.selector.find('a[rightMark=simpleRights]').eq(0);
				var targetUrl = targetDom.attr('module');

				if(!that.bodyHeight){
					that.bodyHeight = document.documentElement.scrollHeight - $('#navbar').height() - 23;
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
				content.attr('style','display: block; height: 553px;');
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
				var rightId = oc.index._activeRight;
				
				var content = that.simpleContent;
				var targetDom = that.selector.find('a[rightMark=simpleRights]').eq(0);
				var targetUrl = targetDom.attr('module');

				content.html('');
				if(!that.bodyHeight){
					that.bodyHeight = document.documentElement.scrollHeight - $('#navbar').height() - 23;
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
				
				if(window.onecenterFlag){
					//门户
					top.location.href=oc.resource.getUrl('resource/itm.html');
				}else{
					top.location.href=oc.resource.getUrl('resource/index.html');
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
		}
	}
	
});