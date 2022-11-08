(function(){
	var currenTexteraID=undefined;//记录焦点选择的文本域
	var isRecoverd=false;//是否点击恢复按钮
	var checkedNum=0,managerId=0;
	function getManagerId(id){
		oc.util.ajax({
			url:oc.resource.getUrl('portal/business/service/getBasicInfo.htm'),
			data:{bizId:id},
			successMsg:null,
			async:false,
			success:function(d){
				managerId=d.data.managerId;
			}
		});
	}
	 function checkStr(str){  
		 if(/[^${}]/.test(str)){  
		     str = str.replace(/[^${}]/g, '');  
		     return checkStr(str);  
		 }else{  
		     return str;  
		 }  
		  }  
	 function checkThreshold(){
		
	 }
		
	 function run(str,str1,title){  
		 var num=0;
		 if(str==""){
			 alert("告警内容不能为空！");
			 num=4;
		 }else{
			 str = checkStr(str);  
			 if(str.length % 3!=0){
				 num=1;
				 alert(title+"-表达式不正确，请检查，表达式格式为${xx}");
				
			 }else{
				 var data=["业务系统名称","健康度","告警节点名称","告警节点类型","节点告警内容","责任人","告警级别"];
				 var reg = /\{(.*?)\}/gi;
				 var  re=/\s*^\{[^{}]*}/gi;
				 var tmp = str1.match(reg); 
				var isRight=true;
				 if (tmp) {
			          for (var i = 0; i < tmp.length; i++){ 
			 
			        	  var thisValue=tmp[i].replace(reg, "$1");
			        	  if($.inArray(thisValue, data)==-1){
			        		  num=2;
			        		  alert(title+"-内容不匹配："+thisValue);
			        	  }
			          } 
			          } else {
			        	  num=3;
			           alert(title+"-没有匹配内容！"); 
			           } 
				 
			 } 
		 }
	
		 return num;  
		  }  
	
	function busniessSetting(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	//根据焦点位置插入参数
	function initSel(myValue,id){
		var $t = $(id)[0]; 
        if (document.selection) { // ie  
        	$(id).focus();  
            var sel = document.selection.createRange();  
            sel.text = myValue;  
            $(id).focus();  
            sel.moveStart('character', -l);  
            var wee = sel.text.length;  
            if (arguments.length == 2) {  
                var l = $t.value.length;  
                sel.moveEnd("character", wee + t);  
                t <= 0 ? sel.moveStart("character", wee - 2 * t  
                        - myValue.length) : sel.moveStart(  
                        "character", wee -t - myValue.length);  
                sel.select();  
            }  
        } else if ($t.selectionStart  
                || $t.selectionStart == '0') {  
            var startPos = $t.selectionStart;  
            var endPos = $t.selectionEnd;  
            var scrollTop = $t.scrollTop;  
            $t.value = $t.value.substring(0, startPos)  
                    + myValue  
                    + $t.value.substring(endPos,  
                            $t.value.length);  
            $(id).focus();  
            $t.selectionStart = startPos + myValue.length;  
            $t.selectionEnd = startPos + myValue.length;  
            $t.scrollTop = scrollTop;
//            if (arguments.length == 2) {  
//                $t.setSelectionRange(startPos -   $t,  
//                        $t.selectionEnd +   $t);  
//                $(id).focus();  
//            }  
        } else {  
        	$(id).value += myValue;  
        	$(id).focus();  
        } 
	}
	busniessSetting.prototype={
		constructor:busniessSetting,
		_form:undefined,
		_cancelWindowFlag:0,
		_rightsDiv:undefined,
		_loadAccordions:[],
		_updateStatus:false,
		_cache:{},
		cfg:undefined,
		_cancelBtn:false,
		open:function(){
			var dlg=$('<div/>'),that=this;
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/business-service/biz_warn_setting.html'),
				title:'告警设置',
				height:620,
				width:900,
				buttons:!that.cfg.readonly&&[{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						
						var num=that._checkSaveInfo(that,that._form,currenTexteraID);
						//	if(isRecoverd==true){
							var code=that._form.val();
							if(code){
								if(num==0){
									if(code.deathThreshold==undefined || code.seriousThreshold==undefined
											|| code.warnThreshold==undefined){
										alert("阈值不能为空");
									}else{
										oc.util.ajax({
											url:oc.resource.getUrl('portal/business/alarmInfo/insertalarmInfo.htm'),
											data:code,
											async:false,
											success:function(data){
												if(data.code==200){
													alert("保存成功");
												}
												//isRecoverd=true;
												dlg.dialog('destroy');
												if(that.cfg.currentID=="summary"){
													oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){
															oc.resource.loadScript('resource/module/business-service/buz/business.js',
																function(){
																	oc.business.service.edit.permissions.setLastEnter(1);
																	new businessTopo({
																		bizId:that.cfg.clickData
																	});
																});
														});
												}else{
													oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
												}
											}
										});	
									}
									
								}
							
							}
							
					//	}
							
					
						}
				},{
					text:'取消',
					iconCls:"fa fa-times-circle",
					handler:function(){
						that._cancelBtn = true;
						checkedNum=0;
						currenTexteraID=undefined;
						dlg.dialog('destroy');
					
						if(that.cfg.currentID=="summary"){
								oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){
									oc.resource.loadScript('resource/module/business-service/buz/business.js',
										function(){
											oc.business.service.edit.permissions.setLastEnter(1);
											new businessTopo({
												bizId:that.cfg.clickData
											});
										});
								});
						}else{
						oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
						}
						}
				}],
				resizable:true,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					$("#warnForm textarea").each(function(){
						$(this).click(function(){
							currenTexteraID=$(this).context.id;
						});
						$(this).blur(function(){
							currenTexteraID=$(this).context.id;
							var id=currenTexteraID;
							that._checkSaveInfo(that,that._form,id);
						});
					});
					var form=$("#warnForm");
					that.initForms(that.cfg.type,form);
					form.find("input[type='text']").each(function(){
						$(this).blur(function(){
							if($(this).context.value>100){
								alert("阈值不能超过100分");
							}
						});
					});
					that.cfg.type=='edit'&&that._loadMethods['阈值设置'](that);
					//指标
					oc.ui.combobox({
						selector:dlg.find(".myselect"),
						fit:false,
						width:80,
						placeholder:null,
						panelHeight : 'auto',
						valueField:'id',    
						textField:'name',
						data:[
						      {id:'Less',name:'<'},
						      {id:'LessEqual',name:'<='},
						      {id:'Equal',name:'='},
						      {id:'GreatEqual',name:'>='},
						      {id:'Great',name:'>'}
				        ]
					});
					//恢复默认值
					dlg.find(".recoverBtn").on('click',function(){
						form.find("#deathThreshold").val(20);
						form.find("#deathAlarmContent").val("${业务系统名称}不可用，故障节点：${告警节点名称}，故障节点类型：${告警节点类型}，故障节点告警内容：${节点告警内容}");
						form.find("#seriousThreshold").val(50);
						form.find("#seriousAlarmContent").val("${业务系统名称}健康度${健康度}分，故障节点：${告警节点名称}，故障节点类型：${告警节点类型}，故障节点告警内容：${节点告警内容}");
						form.find("#warnThreshold").val(80);
						form.find("#warnAlarmContent").val("${业务系统名称}健康度${健康度}分，故障节点：${告警节点名称}，故障节点类型：${告警节点类型}，故障节点告警内容：${节点告警内容}");
						form.find("#normalContent").val("${业务系统名称}恢复正常，健康度${健康度}分");	
						isRecoverd=true;
						checkedNum=0;
						isSave=true;
					});
					//选择参数
					dlg.find(".addParamter").on("click",function(){
						if(currenTexteraID==undefined){
						alert("请将鼠标移至需要插入参数的位置！");
					}else{
					//getAlarmInfos
					var parameterWindow = $('<div><div class="oc-toolbar datagrid-toolbar insert-btn">' +
						'<button id="addContent" style="float:right"> <span class="icon-insert margin5"></span>插入</button></div><div style="height:320px"><div id="parameterDatagridId"></div></div></div>');
					
					parameterWindow.dialog({
				  title:"选择参数",
				  width:600,
				  height:420,
				  modal:true,
					onLoad:function(){
				
							}
				});
				
				$("#addContent").on('click',function(){
									var checkedRow = parameterDatagrid.selector.datagrid('getChecked');
									var values=new Array();
									for(var i=0;i<checkedRow.length;i++){
										values.push(checkedRow[i].value);
									}
									
									var myValue = 'test',id="#"+currenTexteraID;
//									for(var i=0;i<values.length;i++){
//										initSel(values[i],id);
////										var value=$(id).val(); 
////										$(id).val("").focus().val(value); 
//									}
									var parameterValue = values.join('，');
									initSel(parameterValue,id);
									parameterWindow.dialog('close');
									
								});	
			 oc.util.ajax({
            url:oc.resource.getUrl('portal/business/alarmInfo/getAlarmInfos.htm'),
            timeout:null,
            success:function(data){
            	var localData = {"data":{"total":0,"rows":data.data},"code":200};
				
					parameterDatagrid.selector.datagrid('loadData',localData);
            }
            });
            var parameterDatagrid = oc.ui.datagrid({
					pagination:false,
					selector:$('#parameterDatagridId'),
					checkOnSelect:false,
					selectOnCheck:false,
					columns:[[
						 {field:'value',checkbox:true},
				         {field:'name',title:'参数',width:80},
				         {field:'description',title:'参数描述',width:80}
				     ]]
				});
            
					
					}
						
						
					
					});
					
				},
				
				onBeforeClose:function(){//关闭窗体前确认
				},
				onClose:function(){
					checkedNum=0;
					dlg.dialog('destroy');
					currenTexteraID=undefined;
					if(that.cfg.currentID=="summary"){
							oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){
								oc.resource.loadScript('resource/module/business-service/buz/business.js',
									function(){
										oc.business.service.edit.permissions.setLastEnter(1);
										new businessTopo({
											bizId:that.cfg.clickData
										});
									});
							});
					}else{
						oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
					}
				}
			});
			
		},
		_defaults:{
			type:'add',
			id:undefined
		},
		_flag:'add',
		_mainDiv:undefined,
		_id:'#oc_biz_busniess_detail',
		_init:function(dlg){
			var that = this;
			that._flag = that.cfg.type;
			that._mainDiv=dlg.find(that._id).attr('id',oc.util.generateId());
			if(that.cfg.type=='edit'){
				for(var i in that._initMethods){
					that._initMethods[i](that);
				}
			}else{
				that._initMethods['阈值设置'](that);
			}
			
			that._mainDiv.accordion({
				onSelect:function(title,index){
					that._initMethods[title](that);
					if(that.cfg.type=='edit'){
						that._loadAccordions.indexOf(title)&&that._loadMethods[title](that);
					}
				},
				onBeforeSelect:function(title, index){
					this.selectedIdx=index;
					if(that.cfg.type=='add'&&index!=0){
						return that._saveMethods['阈值设置'](that);
					}
					return true;
				}
			});
			
			that.cfg.type=='add'&&(function(target){
							for(var i in target._cache){
								target._cache[i].initData(target);
							}
						})(that);
		},
		initForms :function(type,form){
			var bizid=this.cfg.clickData;
			if(type=="add"){//新增
				form.find("input[name='bizId']").val(bizid);	
			}else{
				oc.util.ajax({
					url:oc.resource.getUrl('portal/business/alarmInfo/getalarmInfoByBizId.htm'),
					data:{bizid:bizid},
					async:false,
					success:function(data){
					if(data.code==200){
						form.find("input[name='id']").val(data.data.id);
						form.find("input[name='bizId']").val(data.data.bizId);
						form.find("#deathThreshold").val(data.data.deathThreshold);
						form.find("#deathAlarmContent").val(data.data.deathAlarmContent);
						form.find("#seriousThreshold").val(data.data.seriousThreshold);
						form.find("#seriousAlarmContent").val(data.data.seriousAlarmContent);
						form.find("#warnThreshold").val(data.data.warnThreshold);
						form.find("#warnAlarmContent").val(data.data.warnAlarmContent);
						form.find("#normalContent").val(data.data.normalContent);
					}
					}
				});
			}
			
		},
		
		_initMethods:{
			'阈值设置':function(target){
				if(!target._form){
					target._form=oc.ui.form({
						selector:target._mainDiv.find("form")
					});
					target.cfg.readonly&&target._form.disable();
				}
			},
			'告警规则':function(target){//点击告警规则，保存阈值
				var isSave=		target._saveInfo(target,target._form);//验证成功保存
				if(checkedNum==0 && isSave==true){
					var clickData=target.cfg.clickData;
					target._rightsDiv = target._mainDiv.find(".rights").first();
				target._rightsDiv.panel({//保存后打开告警规则界面
					href:oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
					onLoad:function(){
						oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
							getManagerId(clickData);
							oc.resource.alarmrules.bizOpen(clickData,managerId);	
							
						});
					
						
					}
					
				});
			
						
				}else{
					if(isSave==false){
						alert("阈值不能为空！");
					}else{
						alert("阈值设置内容验证失败，请检查！");	
					}
				
					target._rightsDiv.html(" ");
					return ;
				}
			
			}
		},
		_loadMethods:{
			'阈值设置':function(target){//阈值设置保存方法
				var that=this;
				target._loadAccordions.push('阈值设置');
			},
			'告警规则':function(target){
				target._loadAccordions.push('告警规则');
			},
		},
		_checkSaveInfo:function(target,form,id){
			var num=0;
			var content=form.find("#deathAlarmContent").val();
			if(id=="deathAlarmContent"){
				content=form.find("#deathAlarmContent").val();
				num=run(content,content,"致命告警");
			}else if(id=="seriousAlarmContent"){
				content=form.find("#seriousAlarmContent").val();
				num=	run(content,content,"严重告警");
			}else if(id=="warnAlarmContent"){
				content=form.find("#warnAlarmContent").val();
				num=	run(content,content,"警告告警");
			}else if(id=="normalContent"){
				content=form.find("#normalContent").val();
				num=	run(content,content,"正常告警");
			}
			checkedNum=num;
			return num;
			 
		},
		_saveInfo :function(target,form){
		var code=	form.val();
		var isSave=true;
		if(!code){
		}else{
			if(code.bizId!=undefined){
				if(isRecoverd==false){
					 var code=target._form.val();
						if(code.deathThreshold==undefined || code.seriousThreshold==undefined
								|| code.warnThreshold==undefined){
							alert("阈值不能为空");
							isSave=false;
						}else{
							oc.util.ajax({
								url:oc.resource.getUrl('portal/business/alarmInfo/insertalarmInfo.htm'),
								data:code,
								async:false,
								success:function(data){
									if(data.code==200){
										if(data.data==1){
											alert("保存成功");
											isSave=true;
										}else{
											alert("保存失败")
											isSave=false;
										}
										
									}else{
										alert("保存失败")
										isSave=false;
									}
								}
							});		
						}
						
					
					
				}
				
			}else{
				target.initForms(target.cfg.type,target._form)
			}
		
			return isSave;
			//
		}
	
		},
		_saveMethods:{
			'阈值设置':function(target){
				/*
				var flag = false;
				
				
				flag&&target._cache['阈值设置'].initData(target);
				return flag;
			*/},
			'告警规则':function(target){
				
				
			}
		}
		
	};
	
	oc.ns('oc.business.service.set');
	
	oc.business.service.set={
		open:function(cfg){
			new busniessSetting(cfg).open();
		}
	};
})(jQuery);