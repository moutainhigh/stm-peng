$(function() {
	
	var $container = $('#pageTag-container-alarm');
	var nodeId;
    var myEditor;
    var myEditor1;
    var isValiable=0;
    var type="email";
    var configStr="";
    var user = oc.index.getUser();
	UMinstance();
	reloadSmsNode();
	reloadNode();
	$container.find('#tag-panel-alarm').panel('RenderP',{
		title:'通知模板管理<a class="blue-ico blue-ico-add locate-right " title="添加模板"></a>',
		left:155,
		onOpen:function(){
			/*var panelHeight = $container.find(".system-parent").height(),panelHeadHeight = $container.find(".system-parent .panel-header").height();
			$container.find(".system-parent .pageTag-panel").height(panelHeight-panelHeadHeight);*/
			if(user.domainUser || user.systemUser){
				$("#submitDiv").show();
				$("div[name='addParamter']").show();
				$("a.blue-ico.blue-ico-add.locate-right").attr("style","display:block");
			}else{
				$("#submitDiv").hide();
				$("div[name='addParamter']").hide();
				$("a.blue-ico.blue-ico-add.locate-right").attr("style","display:none");
			}
			
			reloadClick();
			getConfig();
			Ischange();
		}
	});
$(".blue-ico.blue-ico-add.locate-right").click(function(){//点击新增刷新右边编辑框
	refreshFor();
});
	var form = $container.find("#pageTag-form-alarm"),
		myform = oc.ui.form({selector:form}),
		Service=$container.find('#alarmService').panel('RenderP',{
			title:'添加模板'
		});
	$(".pageTag-content").parent().parent().height($container.height());
//	console.info($('div[data-options="region:'center'"]').height());
	 oc.ui.form({
		selector:form,
		combobox:[{
			selector:'[name=templateType]',
			placeholder:null,
			selected:false,
			value : 'email',
			data : [{
				id : 'email',
				name : '邮件',
				selected : true
			},{
				id : 'sms',
				name : '短信'
				
			}],
	        onChange:function(n,o){
	        	if(n=='sms'){
	        		$("#mail").hide();
	        	}else if(n=='email'){
	        		$("#mail").show();
	        	}
	        	type=n;
	        }
		}]
	});
	 //
	$("div[name='addParamter']").click(function(){
		addParamters("content");
	});
	$("div[name='addthemeParamter']").click(function(){
		addParamters("themes");
	});
	$container.find('#submitForm').linkbutton('RenderLB', {
		text : '应用',
		iconCls : 'fa fa-check-circle',
		onClick : function() {
			var node = myform.val();
		if(myEditor.getContent().length>5000){
			alert('模板字符数已超过5000个!');
		}else if(node.templateName==undefined ){
			alert('模板名称不能为空！');
			
		}/*else if(node.title==undefined ){
			alert('模板主题不能为空！');
			
		}*/else{
			
			if(myform.validate()){
				if(node.templateType=='sms'){//短信模板,保存纯文本内容
					var content=changeContent(myEditor.getContentTxt(),false);
					node.content=content;
				}else{//configStr
					var html="<p align='right'>"+configStr+"</p><br/>";
					if(node.content.indexOf("运维服务管理系统")>0){
						var content=changeContent(myEditor.getContent(),false);
						node.content=content;	
					}else{
//						UM.getEditor('myalarmEditor').execCommand('insertHtml', html);
						UM.getEditor('myalarmEditor').setContent(myEditor.getContent()+html);
						var content=changeContent(myEditor.getContent(),false);
						node.content=content;
					}
					
					
				}
				var title=changeContent(node.title,false);
				node.title=title;
				if(run(node.content,node.content)==0 &&  run(node.title,node.title)==0){
					oc.util.ajax({
					url : oc.resource.getUrl("system/alarm/saveAlarmNotice.htm"),
					data:node,
					successMsg : null,
					success : function(data) {
						if(data.data){
							alert("模板保存成功！");
							reloadSmsNode();
							reloadNode();
							refreshFor();
						}
					}
				});
				}else{}
				
			}
		}

		}
	});
	function reloadClick(){
	
		}
	//根据焦点位置插入参数
	function initSel(myValue){
		
		var $t = $("input[name='title']")[0]; 
        if (document.selection) { // ie  
        	$("input[name='title']").focus();  
            var sel = document.selection.createRange();  
            sel.text = myValue;  
            $("input[name='title']").focus();  
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
            $("input[name='title']").focus();  
            $t.selectionStart = startPos + myValue.length;  
            $t.selectionEnd = startPos + myValue.length;  
            $t.scrollTop = scrollTop;
//            if (arguments.length == 2) {  
//                $t.setSelectionRange(startPos -   $t,  
//                        $t.selectionEnd +   $t);  
//                $(id).focus();  
//            }  
        } else {  
        	$("input[name='title']").value += myValue;  
        	$("input[name='title']").focus();  
        } 
	}
	function addParamters(type){

		
		var dlg = $('<div/>');
		 dlg.dialog({
					href : oc.resource.getUrl('resource/module/system/warningway/choiceParamters.html'),
					title : '选择参数',
					width:300,
					height : 270,
					scrollbars:"yes",
					modal: true,
					resizable : false,
					overflow:'auto',
					onLoad : function() {
						var str="";
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/getParamters.htm"),
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
								var checkedType=	form.find('input[name="sysModuleEnum"]:checked');
								for(var key in data.data){
										if(checkedType.val()=="BUSSINESS"){//业务模板
											if(key!="sourceIP"){
												str+="<tr class='datagrid-row'><td><a href='javascript:void(0)' id='"+data.data[key]+"'>"+data.data[key]+"</a></td><td><span id='"+key+"'>"+data.data[key]+"</span></td></tr>";
											}
										}else{//资源模块
											str+="<tr class='datagrid-row'><td><a href='javascript:void(0)' id='"+data.data[key]+"'>"+data.data[key]+"</a></td><td><span id='"+key+"'>"+data.data[key]+"</span></td></tr>";
										}
										
									}
									$("#mybody").append(str);
									$("#mybody tr td a").each(function(){
										$(this).click(function(){
											var text=$(this).context.text;
											var key=$(this).context.id;
											if(type=="themes"){//添加主题
												var htmlsms='${'+text+'}';
												var themeVal=$("input[name='title']").val();
												var themeTxt=themeVal+htmlsms;
												initSel(htmlsms);
											/*	console.info(themeTxt);
												$("input[name='templateTheme']").focus();
												$("input[name='templateTheme']").val(themeTxt);
												$("input[name='templateTheme']").focus();*/
											}else{
//												UM.getEditor('myalarmEditor').focus();
												if(type=='sms'){
													var htmlsms='${'+text+'}';
													UM.getEditor('myalarmEditor').execCommand('insertHtml', htmlsms)
												}else{
													
													var html=key+':'+
													 ' ${'+text+'}';
													UM.getEditor('myalarmEditor').execCommand('insertHtml', html)
													UM.getEditor('myalarmEditor').isFocus();
												}	
											}

											
											    
											    dlg.dialog('close');
										});
									});
								}
							}
						});
					
						
					}
				 
				 
			 });
			
			
		
	}
	function Ischange(){
		$("input[name='templateType']").change(function(){
	

		if($(this).val()=="email")
		{
			$("#mail").hide();
			}
		});
	}
	function reloadSmsNode(){
		$("#Smsnoticelist").html('');
		var str="";
		oc.util.ajax({
		url : oc.resource.getUrl("system/alarm/getSelector.htm?type=1&profileNameType=0"),
		successMsg : null,
		sync:false,
		success : function(data) {//ico ico-back locate-right margin-top8
			if(data.data){
				for(var i=0;i<data.data.length;i++){
					if(data.data[i].alarmDefalut==true){
						if(user.domainUser || user.systemUser){

							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='back' title='恢复' style='padding-left:20px;'><a class='ico bandwide-recovery locate-right margin6' title='恢复' sysModuleEnum='"+data.data[i].sysModuleEnum+"' type='sms' value="+data.data[i].id+"></a></span></div></li>";			
						}else{
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='query' style='padding-left:20px;'><a class='ico ico-search locate-right' title='查看' value="+data.data[i].id+"></a></span></div></li>";
						}
						
					}else{
						if(user.domainUser || user.systemUser){
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='del'><a class='ico ico-del locate-right margin6' type='sms' title='删除' sysModuleEnum='"+data.data[i].sysModuleEnum+"' value="+data.data[i].id+"></a></span></div></li>";
							
						}else{
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='query' style='padding-left:20px;'><a class='ico ico-search locate-right' title='查看' value="+data.data[i].id+"></a></span></div></li>";
							
						}
					
					}
					}
				$("#Smsnoticelist").append(str);
				$("#Smsnoticelist li ").each(function(i,item){
					
					$(this).click(function(){
						var id=$(this).attr('value')
						
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/getAllById.htm"),
							data:{id:id},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									loadForm(data.data,'check');
									
								}
							}
						});
						
						$("#mail").hide();
						
						
					});
					
					$(this).find('div span a.ico.bandwide-recovery.locate-right.margin6').click(function(){
						var id=$(this).attr('value')
						var type=$(this).attr('type')
						var sysModuleEnum=$(this).attr('sysModuleEnum');
						
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/resetDefaultNotifyTemplate.htm"),
							data:{type:type,sysName:sysModuleEnum},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									loadForm(data.data,'back');
									reloadSmsNode();
									
								}
							}
						});
						
						
						
					});
					$(this).find('span a.ico.ico-search.locate-right').click(function(){
						var id=$(this).attr('value');
						oc.util.ajax({
							
							url : oc.resource.getUrl("system/alarm/getAllById.htm"),
							
							data:{id:id},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									loadForm(data.data,'search');
									
								}
							}
						});
						
						
						
					});
					
					$(this).find('div span a.ico.ico-del.locate-right').click(function(){
						var id=$(this).attr('value');
						var type=$(this).attr('type');
						var sysName=$(this).attr('sysModuleEnum');
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/checkNotifyTemplateEnabled.htm"),
							data:{id:id,type:type},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.code==200){
									if(data.data==false){
										 oc.ui.confirm("模板已被策略应用，您是否确认删除？",function() {//checkNotifyTemplateEnabled
								        		oc.util.ajax({
													url : oc.resource.getUrl("system/alarm/delById.htm"),
													data:{id:id,sysName:sysName},
													successMsg : null,
													sync:false,
													success : function(data) {
														
														if(data.code==200){
															if(data.data==true){
																alert("删除成功")
																reloadSmsNode();
																reloadNode();
																refreshFor();
														
															}
															
															
														}
													}
												}); 
								        	  
								          });
									}else{
										 oc.ui.confirm("是否确认删除？",function() {//checkNotifyTemplateEnabled
								        		oc.util.ajax({
													url : oc.resource.getUrl("system/alarm/delById.htm"),
													data:{id:id,sysName:sysName},
													successMsg : null,
													sync:false,
													success : function(data) {
														
														if(data.code==200){
															if(data.data==true){
																alert("删除成功")
																reloadSmsNode();
																reloadNode();
																refreshFor();
																
															}
															
															
														}
													}
												}); 
								        	  
								          });
									}
								}
							}
						}); 
						
					});
					
				});
			}
		}
	});
	}
	
	function reloadNode(){
		$("#Emailnoticelist").html('');
		var str="";
	oc.util.ajax({
		url : oc.resource.getUrl("system/alarm/getSelector.htm?type=2&profileNameType=0"),
		successMsg : null,
		sync:false,
		success : function(data) {

            // console.log(data.data);//ico ico-back locate-right margin-top8
			if(data.data){
				for(var i=0;i<data.data.length;i++){
					if(data.data[i].alarmDefalut==true){
						if(user.domainUser || user.systemUser){
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='back' title='恢复' style='padding-left:20px;'><a class='ico bandwide-recovery locate-right margin6' sysModuleEnum='"+data.data[i].sysModuleEnum+"' type='email'  title='恢复' value="+data.data[i].id+"></a></span></div></li>";			
						}else{
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='query' title='查看' style='padding-left:20px;'><a class='ico bandwide-recovery locate-right margin6' title='查看' value="+data.data[i].id+"></a></span></div></li>";
						}
						
					}else{
						if(user.domainUser || user.systemUser){
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span></span><span class='del'><a class='ico ico-del locate-right margin6' title='删除' sysModuleEnum='"+data.data[i].sysModuleEnum+"' value="+data.data[i].id+"></a></span></div></li>";
							
						}else{
							str+="<li  value="+data.data[i].id+"><div><span class='name' style='float:left'><a value="+data.data[i].id+" class='tempName'>"+data.data[i].name+"</a></span><span class='query' title='查看' style='padding-left:20px;'><a class='ico ico-search locate-right' title='查看' value="+data.data[i].id+"></a></span></div></li>";
							
						}
					
					}
					}
				$("#Emailnoticelist").append(str);
				$("#Emailnoticelist li ").each(function(i,item){
			
					$(this).click(function(){
						var id=$(this).attr('value')
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/getAllById.htm"),
							data:{id:id},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									loadForm(data.data,'check');
									
								}
							}
						});
						$("#mail").show();
						
						
					});
					$(this).find('div span a.ico.bandwide-recovery.locate-right.margin6').click(function(){
						var id=$(this).attr('value')
						var type=$(this).attr('type')
						var sysModuleEnum=$(this).attr('sysModuleEnum');
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/resetDefaultNotifyTemplate.htm"),
							data:{type:type,sysName:sysModuleEnum},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									loadForm(data.data,'back');
									reloadNode();
									
								}
							}
						});
						
						
						
					});
					$(this).find('span a.ico.ico-search.locate-right').click(function(){
						var id=$(this).attr('value');
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/getAllById.htm"),
							data:{id:id},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									loadForm(data.data,'search');
									
								}
							}
						});
						
						
						
					});
					
					$(this).find('div span a.ico.ico-del.locate-right').click(function(){
						var id=$(this).attr('value');
						var sysName=$(this).attr('sysModuleEnum');
						oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/checkNotifyTemplateEnabled.htm"),
							data:{id:id,type:type},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.code==200){
									if(data.data==false){
										 oc.ui.confirm("模板已被策略应用，您是否确认删除？",function() {//checkNotifyTemplateEnabled
								        		oc.util.ajax({
													url : oc.resource.getUrl("system/alarm/delById.htm"),
													data:{id:id,sysName:sysName},
													successMsg : null,
													sync:false,
													success : function(data) {
														
														if(data.code==200){
															if(data.data==true){
																alert("删除成功")
																refreshFor();
																reloadNode();
															}
															
															
														}
													}
												}); 
								        	  
								          });
									}else{
										 oc.ui.confirm("是否确认删除？",function() {//checkNotifyTemplateEnabled
								        		oc.util.ajax({
													url : oc.resource.getUrl("system/alarm/delById.htm"),
													data:{id:id,sysName:sysName},
													successMsg : null,
													sync:false,
													success : function(data) {
														
														if(data.code==200){
															if(data.data==true){
																alert("删除成功")
																refreshFor();
																reloadNode();
															}
															
															
														}
													}
												}); 
								        	  
								          });
									}
								}
								/*if(data.code==200){
									if(data.data==true){
										alert("删除成功")
										refreshFor();
										reloadNode();
									}
									
									
								}*/
							}
						}); 
						
						
				   /*       oc.ui.confirm("您是否确认删除？",function() {//checkNotifyTemplateEnabled
				        		oc.util.ajax({
									url : oc.resource.getUrl("system/alarm/delById.htm"),
									data:{id:id},
									successMsg : null,
									sync:false,
									success : function(data) {
										
										if(data.code==200){
											if(data.data==true){
												alert("删除成功")
												refreshFor();
												reloadNode();
											}
											
											
										}
									}
								}); 
				        	  
				          });*/
						
					
						
						
						
					});
					
				});
			}
		}
	});
	}
	function refreshFor(){
		$("#mail").show();
		form.find("#templateType").attr('disabled',false);
		form.find("#templateType").combobox({disable:false})
		form.find("#templateType").combobox('setValue','email');
		form.find('input[name="templateName"]').val("");
		form.find('input[name="templateID"]').val("");
		form.find('input[name="title"]').val("");
//		form.find('input[name="provider"]').val("");
		myEditor.setContent("",false);
		
	}
	function loadForm(data,way){
		
		if(way=="check"){
			form.find("#templateType").attr('disabled','disabled');
			form.find("#templateType").combobox('disable');
		}
		
		form.find("#templateType").combobox('setValue',data.templateType);
		form.find('input[name="templateID"]').val(data.templateID);
		form.find('input[name="isDefaultTemplate"]').val(data.defaultTemplate);
		form.find('input[name="templateType"]').val(data.templateType);
		form.find('input[name="templateName"]').val(data.templateName);
		var title=changeContent(data.title,true);
		form.find('input[name="title"]').val(title);
		//初始化sysModuleEnum
		form.find('input[name="sysModuleEnum"]').each(function(){

			var sysModuleEnum=$(this).context.value;
			if(data.sysModuleEnum==sysModuleEnum){
				$(this).prop("checked",'checked');
			}else{
				$(this).removeAttr("checked")
			}
		});
		var content=changeContent(data.content,true);
		myEditor.setContent(content,false);
	
	
	}
	function changeContent(content,bl){
		var datas=new Array();
		if(content==null){
			return content;
		}
		 oc.util.ajax({
				url : oc.resource.getUrl("system/alarm/getParamters.htm"),
				successMsg : null,
				async:false,
				success : function(data) {
					 var arr=new Array();
					datas=data.data;
				}
		 });
		 if(datas!=null||datas==undefined){
				for(var key in datas ){
					if(bl==true){
						if(content!=""&&content!=null&&content!=undefined){
						content=	content.replace(key,datas[key]);
						}
					}else{

						
						if(content!=""&&content!=null&&content!=undefined){

						content=	content.replace("${"+datas[key]+"}","${"+key+"}");
						}
					}
				
				}
				return content;
			}
	}
	function UMinstance(){
	/*	if(document.all){ 
			alert("IE"); 
			}else{ 
			alert("not ie"); 
			}*/
/*		var newId = oc.util.generateId();
		var umEditorHtml = $('#myalarmEditor').attr('id',newId);
//		UM.delEditor("myalarmEditor");
//		UM.getEditor('myalarmEditor').destroy();
		myEditor = UM.getEditor(newId);
		umEditorHtml.width("100%");*/
		UM.delEditor("myalarmEditor");
		myEditor = UM.getEditor('myalarmEditor');
		$(".edui-container #myalarmEditor").width("100%");
	}
   function getConfig(){
	   oc.util.ajax({
			url : oc.resource.getUrl("system/alarm/getConfig.htm"),
			successMsg : null,
			sync:false,
			success : function(data) {
				if(data.data){
					configStr=data.data;
				}
			}
		});
   }
   function replaceStr(str){  
		 if(/[^${}]/.test(str)){
			
			 if(typeof(str)!="undefined"){
		     str = str.replace(/[^${}]/g, '');  
		     return replaceStr(str); 
			 }
		 }else{  
		     return str;  
		 }  
		  }  
	 function run(str,str1){  
		 var num=0;
		 str = replaceStr(str);  
		 if(typeof(str)!="undefined"&&str.length % 3!=0){
			 num=1;
			 alert("表达式不正确，请检查，表达式格式为${xx}")
			 return num;
		 }else{
			
			 oc.util.ajax({
					url : oc.resource.getUrl("system/alarm/getParamters.htm"),
					successMsg : null,
					async:false,
					success : function(data) {
					
						 var arr=new Array();
						if(data.data){
							
							for(var key in data.data){
								arr.push(key);
							}
							 var reg = /\{(.*?)\}/gi;
							 if(typeof(str1)!="undefined"){
							 var tmp = str1.match(reg); 
							 }
							 var isRight=true;
							 if (tmp) {
						          for (var i = 0; i < tmp.length; i++){ 
					
						        	  var thisValue=tmp[i].replace(reg, "$1");
						        	  if($.inArray(thisValue, arr)==-1){
						        		  num=2; 
						        		  alert("告警内容不完整："+thisValue);
						        	  }
						          } 
						          } else {
						        	  if($("input[name='templateType']").val()=='email'){
						        	  num=3;
						        	  alert("告警内容不完整,请选择相应告警参数！"); 
						        	  }
						           } 
							
						}
					} 
				
				}); 
			 return num;
		 }
		// console.info(num);
		
		  } 

	    
});