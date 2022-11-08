(function(){
	function status(cfg){
		this._cfg=$.extend({},this._defaults,cfg);
		this.init();
	}
	
	status.prototype={
		constructor:status,
		_defaults:{
			type:'classics',
			selector:0,
			user:0
		},
		_dom:{
			div:'<div class="top_set">'+
					'<span class="oc-name"><marquee scrollamount="3"></marquee></span>'+
					'<span class="oc-simplebtn">'+
			            '<a class="oc-righthome" href="index.html"></a>'+
			            '<a class="oc-rightsimple" href="index_simple.html"></a>'+
			        '</span>'+
					'<ul class="self-list classics">'+
						'<li title="系统健康度"><a class="showHealth"></a></li>'+
						'<li title="&#24110;&#21161;"><a class="help"></a></li>'+
//						'<li><a class="about"></a></li>'+
//						'<li title="修改密码"><a class="password"></a></li>'+
						'<li title="个人信息"><a class="user"></a></li>'+
						'<li title="授权信息"><a class="license"></a></li>'+
						'<li title="退出系统"><a class="close"></a></li>'+
					'</ul>'+
					'<ul class="oc-simp-self-list simple">'+
						'<li title="帮助"><a class="help"></a></li>'+
						'<li title="大屏显示"><a class="full_sc"></a></li>'+
						'<li><a class="user"></a></li>'+
						'<li title="退出系统"><a class="Mclose"></a></li>'+
					'</ul>'+
				'</div>',
			user:'<div class="user_details">'+
					'<form style="padding-bottom:5px;">'+
						'<table class="tab-border" width="98%">'+
							'<tr><td class="td-bgcolor"><span class="zd-name">姓名</span></td><td><span class="user_info" field="name"></span></td></tr>'+
							'<tr><td class="td-bgcolor"><span class="zd-name">性别</span></td><td><span class="user_info" field="sex"></span></td></tr>'+
							'<tr><td class="td-bgcolor"><span class="zd-name">用户类型</span></td><td><span class="user_info" field="userType"></span></td></tr>'+
							'<tr><td class="td-bgcolor"><span class="zd-name">状态</span></td><td><span class="user_info" field="status"></span></td></tr>'+
							'<tr><td class="td-bgcolor"><span class="zd-name">手机</span></td><td><span class="user_info" field="mobile"></span></td></tr>'+
							'<tr><td class="td-bgcolor"><span class="zd-name">邮箱</span></td><td><span class="user_info" field="email"></span></td></tr>'+
							'<tr class="editPass"><td class="td-bgcolor"><span class="zd-name">密码</span></td><td><span class="user_info">******</span><span class="oc-ico-down locate-right"title="修改密码"></span><span class="ico ico-save-info locate-right" style="display:none;" title="保存"></span></td></tr>'+
							'<tr class="pass" style="display:none;"><td class="td-bgcolor"><span class="zd-name">旧密码</span></td><td><input type="password" name="oldPassword" required="required"></td></tr>'+
							'<tr class="pass" style="display:none;"><td class="td-bgcolor"><span class="zd-name">新密码</span></td><td><input type="password" name="newPassword" required="required" validType="password"></td></tr>'+
							'<tr class="pass" style="display:none;"><td class="td-bgcolor"><span class="zd-name">确认密码</span></td><td><input type="password" name="newPasswordConfirm" required="required"'+
								'validType="equal[\'input[name=newPassword]:first\',\'输入密码不一致，请重新输入！\']"></td></tr>'+
						'</table>'+
					'</form>'+
				'</div>',
			show:'<div class="show_health index_status_healthy_box">'+
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
		dom:0,
		_cfg:0,
		_updatePassDlg:0,
		updatePass:function(e){
			this._updatePassDlg.dialog('open').parent()
				.position({of:$(e.target),at:'right bottom',my:'right top'});
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
							memc_temp.find(".memc_icon").addClass("light-ico_resource");
						}else{
							//设置红色图标
							memc_num++;
							memc_temp.find(".memc_icon").addClass("light-ico_resource res_critical_nothing");
						}
					}else{
						memc_num++;
						memc_temp.find(".memc_icon").addClass("light-ico_resource res_unknown_nothing");
					}
					callBack.call(that,memc_num);
				}
			});
		},
		///获取后台dhs,dcs数据，使用setValue方法设置dhs，dcs的值
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
								dcs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_normal_nothing");
							}else{
								dcs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_critical_nothing");
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
							dhs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_normal_nothing");
						}else{
							dhs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_normal_critical");
							dcs_num++;
						}
					}else{
						dhs_item.find(".status").addClass("tree-icon-width25 light-ico_resource res_critical_nothing");
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
		_userInfoDlg:0,
		openUserInfo:function(e){
			if(!this._userInfoDlg){
				var dlg=null,form=null;
				
				//admin用户密码验证最少5位
				var userInfo = oc.index.getUser();
				if(userInfo.account == 'admin'){
					this._dom.user = this._dom.user.replace('validType="password"','validType="adminPassword"');
				}
				dlg=this._userInfoDlg = $(this._dom.user).dialog({
					closed:true,
					title:'个人信息',
					width:420,
					modal:false,
					height:'auto',
					buttons:[{
						text:'取消',
						onClick:function(){dlg.dialog('close');}
					}],
					_onOpen:function(){
						var userInfo = oc.index.getUser();
						userInfo.userType = oc.util.getDictObj('user_type')[userInfo.userType].name;
						userInfo.sex = userInfo.sex==0 ? '男' : '女';
						userInfo.status = userInfo.status==1 ? '启用' : '停用';
						dlg.find('span.user_info').each(function(){
							var $this = $(this);
							$this.html(userInfo[$this.attr('field')]);
						});
						form.val(userInfo);
						validationable();
					},
					onClose:function(){
						form.clear();
						form.disable();
					}
				});
				form=oc.ui.form({selector:dlg.find('form:first')});
				var pass=dlg.find('tr.pass'),editPass=dlg.find('tr.editPass'),
					passInfo=editPass.find('.user_info'),save=editPass.find('.ico-save-info');
				save.click(function(){
					if(form.validate()){
						oc.util.ajax({
							url:oc.resource.getUrl('system/login/updatePassword.htm'),
							data:form.val(),
							successMsg:'密码修改成功!',
							success:function(d){
								if(d.code==200){
									oc.index.loadLoginUser();
									dlg.dialog('close');
								}
							}
						});
					}
				});
				var passBtn=editPass.find('.oc-ico-down').click(function(e){
					var jq=$(this);
					if(jq.is('.oc-ico-down')){
						passInfo.hide();
						save.show();
						pass.css('display','table-row');
						jq.removeClass('oc-ico-down').addClass('oc-ico-up');
					}else{
						passInfo.show();
						save.hide();
						pass.css('display','none');
						jq.removeClass('oc-ico-up').addClass('oc-ico-down');
					};
					validationable();
				});
				
				function validationable(){
					if(passBtn.is('.oc-ico-down')){
						form.disable();
					}else{
						form.enable();
					}
				}
			}
			oc.util.showFloat(this._userInfoDlg, e);
		},
		openOneCenterLicense:function(){
			var dlg = $('<div/>');
			 dlg.dialog({
				href : oc.resource.getUrl('resource/stm-license.html'),
				title : '授权信息',
				width:680,
				height : 470,
				resizable : false,
				cache : false,
				onLoad : function() {
					oc.util.ajax({
						url:oc.resource.getUrl("system/license/getLicense.htm"),
						success:function(d){
							if(d && d.code==200){
								var lic = d.data;
//								console.log(lic);
								if(lic){
									dlg.find("#licenseDeadLine").text(new Date(lic.deadLine).stringify("yyyy-mm-dd"));
									var modules = lic.portalModules;
									var monitorModule = lic.monitorModule;
									var collector = lic.collector;
									var vmComputerMode=lic.vmComputerMode;
									var licenseUseInfoBos = lic.licenseUseInfoBo;
									var zjnum=0;
									if(modules && modules.length>0){
										var html ="";
										for(var i=0;i<modules.length;i++){
											var modul = modules[i];
											var checked = modul.author?'checked="checked"':'';
											html+='<span class="m-r-40"><input type="checkbox" class="oc-checkbox-locate" '+checked+' disabled="disabled"><span class="w-100">'+modul.name+'</span></span>';
										}
										$(html).appendTo(dlg.find("#portalModulesDiv"));
									}
									if(vmComputerMode==undefined || vmComputerMode==0){//exit主机数
										zjnum=(monitorModule.vmUsedCount / 10) + ' * 10';
									}else{
										zjnum=	monitorModule.vmUsedCount;
										dlg.find("#vm").text('虚拟机数');
									}
//									console.log(lic.licenseUseInfoBo);
									if(lic.licenseUseInfoBo){
										for(var i=0;i<lic.licenseUseInfoBo.length;i++){
//											if(lic.licenseUseInfoBo[i].lmEnum=="oc4-num-busi"){
//												dlg.find("#monitorModulesTbody #busiTaotalNum").text(lic.licenseUseInfoBo[i].authorCount);
//												dlg.find("#monitorModulesTbody #busiUsedNum").text(lic.licenseUseInfoBo[i].usedCount);
//											}
											var lmEnmu=lic.licenseUseInfoBo[i].lmEnum;
											var authorCount=lic.licenseUseInfoBo[i].authorCount;
											var usedCount=lic.licenseUseInfoBo[i].usedCount;
											switch(lmEnmu){
											case "oc4-num-res-hd":
												dlg.find("#monitorModulesTbody #reshdTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #reshdUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-res-rd":
												dlg.find("#monitorModulesTbody #resrdTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #resrdUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-res-stor":
												dlg.find("#monitorModulesTbody #resstorTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #resstorUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-topo-mr":
												dlg.find("#monitorModulesTbody #mrTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #mrUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-vm-host":
												dlg.find("#monitorModulesTbody #vmhostTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #vmhostUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-vm-vm":
												dlg.find("#monitorModulesTbody #vmvmTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #vmvmUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-res-app":
												dlg.find("#monitorModulesTbody #appTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #appUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											case "oc4-num-busi":
												dlg.find("#monitorModulesTbody #busiTotalNum").text(authorCount!=0?authorCount:'0');
												dlg.find("#monitorModulesTbody #busiUsedNum").text(authorCount!=0?usedCount:'0');
												break;
											}
										}
									}
									
									if(monitorModule){
										dlg.find("#monitorModulesTbody #totalNum").text(monitorModule.authorCount);
										dlg.find("#monitorModulesTbody #usedNum").text(monitorModule.usedCount);
										dlg.find("#monitorModulesTbody #vmUsedNum").text(zjnum);
										if(monitorModule.remainCount>0){
											dlg.find("#monitorModulesTbody #surplusNum").text(monitorModule.remainCount);
										}else{
											dlg.find("#monitorModulesTbody #surplusNum").text(0);
											dlg.find("#monitorModulesTbody #surplusNum").css("color","red");
											
										}
									}
									
									if(collector){
										//采集器不能为0；
										dlg.find("#collectorCount").text(collector.count<1?1:collector.count);
									}
									dlg.find("#copyright").text(lic.copyright.replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&copy;/g,"©"));
									dlg.find("#oneCenterProjectName").text(lic.productName);
									dlg.find("#oneCenterProjectVersion").text(lic.version);
									dlg.find("#restarReg").click(function(){
										var reg = $('<div/>');
										reg.dialog({
												href : oc.resource.getUrl('resource/license-active.html'),
												title : '重新注册',
												width:550,
												height : 300,
												resizable : false,
												cache : false, 
												onLoad:function(){
												}
											 
											 
										 });
										
									});
									
								}
								
								
							}
							
							
						}
					});
				},
				onClose:function(){
					dlg.dialog('destroy');
				}
			});
		},
		setMarquee:function(text){
			var user=oc.index.getUser();
			this.dom.find('marquee').text((user.name||user.account)+',欢迎您！');
		},
		init:function(){
			var t=this,dom=this.dom=$(this._dom.div),cfg=this._cfg,user=cfg.user;
			this.setMarquee();
			
			//极简模式按钮显示控制
			var mode = $.cookie('mainsteam-stm-simple-mode');
			if(mode=='false'){
				dom.find(".oc-simplebtn").remove();
				dom.find(".oc-name").css("width","260px");
			}
			
			dom.find('.help').click(function(){//电话
				window.open('module/help/help.html','帮助手册',
						'height=600,width=1000,top=0,left=0,toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no');
				
			});
			
			dom.find('.Mclose,.close').click(function(){//退出
				oc.ui.confirm('确认退出本系统！',function(){
					oc.util.ajax({
						url:oc.resource.getUrl('system/login/loginOut.htm'),
						success:function(d){
							top.location.href=oc.resource.getUrl('resource/login.html');
						}
					});
				});
			});
			
			dom.find('.user').click(function(e){//用户
				t.openUserInfo(e);
			});
			//系统健康度显示
			dom.find('.showHealth').click(function(e){
				t.showHealth(e);
			});
			dom.find('.license').click(function(e){//用户
				t.openOneCenterLicense();
			});
			
			oc.util.ajax({
				url:oc.resource.getUrl('system/license/checkLicense.htm'),
				success:function(d){
					d=d.data;
					if(!d.simple){
						dom.find('.oc-rightsimple').click(function(e){
							e.stopPropagation();
							e.preventDefault();
						}).attr('title','极简模式没有被授权！');
					}
				}
			});
			if(cfg.type=='simple-manager'){
				dom.find('.classics').hide();
				dom.find('.oc-rightsimple').click(function(e){
					e.stopPropagation();
					e.preventDefault();
				});	
				this.dom.find('ul.simple li a.full_sc').click(function(){
					$('<div/>').dialog({
						maximized:true,
						title:'大屏展示',
						href:oc.resource.getUrl(oc.index.getModule(1).url)
					});
				});
			}else{
				if(cfg.type=='classics'){
					dom.find('.simple').hide();
					dom.find('.oc-righthome').click(function(e){
						e.stopPropagation();
						e.preventDefault();
					});
				}else if(cfg.type=='simple-other'){
					dom.find('.simple').hide();
					dom.find('.oc-rightsimple').click(function(e){
						e.stopPropagation();
						e.preventDefault();
					});
				}
				this.initClassics();
			}
			cfg.selector.append(this.dom);
		},
		initClassics:function(){
		}
	};
	
	oc.ns('oc.index');
	oc.index.readyStatus=function(cfg){
		return new status(cfg);
	};
})();
