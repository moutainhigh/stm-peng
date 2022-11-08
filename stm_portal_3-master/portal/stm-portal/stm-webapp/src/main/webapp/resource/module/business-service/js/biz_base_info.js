(function($){
	var obj=this;;
	function bizBaseInfo(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	var smsId=4,emailId=3;
	function getSmsDefaultTempl(){
	
			oc.util.ajax({
				url:oc.resource.getUrl('system/alarm/getSelector.htm?type=1&profileNameType=3'),
				async:false,
				success:function(d){
//					form.find('.mailchoice').combobox('loadData',d.data);
					for(var i=0;i<d.data.length;i++){
						if(d.data[i].alarmDefalut==true){
							smsId=d.data[i].id;
						}
					}
					
				}
			});
		
	}
	function getEmailDefaultTempl(){
		
			oc.util.ajax({
				url:oc.resource.getUrl('system/alarm/getSelector.htm?type=2&profileNameType=3'),
				async:false,
				success:function(d){
//					form.find('.mailchoice').combobox('loadData',d.data);
					for(var i=0;i<d.data.length;i++){
						if(d.data[i].alarmDefalut==true){
							emailId=d.data[i].id;
						}
					}
					
				}
			});
		
	}
	function cleanAlarmRulePerson(bizId){
		oc.util.ajax({
			successMsg:null,
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/delAlarmRule.htm'),
			data:{profileId:bizId,profileType:'biz_profile'},
			success:function(data){
				if(data.code==200){
					if(data.data!=0){
						alert("更新责任人信息失败！");
					}
				}else{
					alert("更新责任人信息失败！");
				}
				
			}
		});
	}
	function addAlarmRuleCondition(userid,bizId){
		getSmsDefaultTempl();
		getEmailDefaultTempl();
		oc.util.ajax({
			successMsg:null,
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/addAlarmRuleContent.htm'),
			data:{receiveUser:userid,profileTypeId:bizId,profileType:"biz_profile",
				isSmsAlarm_SMS:"checked",alarmLevel_SMS:"down,metric_error,metric_warn,metric_recover",sendTimeSet_SMS:"",sendTimeNum_SMS:0,sendTimeType_SMS:"minute",
				isSmsAlarm_EMAIL:"checked",alarmLevel_EMAIL:"down,metric_error,metric_warn,metric_recover",sendTimeSet_EMAIL:"",sendTimeNum_EMAIL:0,sendTimeType_EMAIL:"minute",
				isSmsAlarm_ALERT:"checked",alarmLevel_ALERT:"down,metric_error,metric_warn,metric_recover",sendTimeSet_ALERT:"",sendTimeNum_ALERT:0,sendTimeType_ALERT:"minute",
				alarmSendTimeTypeSms:"allTime",alarmSendTimeTypeEmail:"allTime",alarmSendTimeTypeAlert:"allTime",ifSendUnalarmTimeAlarmSms:"unChecked",ifSendUnalarmTimeAlarmEmail:"unChecked",
				ifSendUnalarmTimeAlarmAlert:"unChecked",alarmForDayDataSms:"",alarmForDayDataEmail:"",alarmForDayDataAlert:"",alarmForWeekDataSms:"",
				alarmForWeekDataEmail:"",alarmForWeekDataAlert:"",templateIdSms:smsId,templateIdEmail:emailId,templateIdAlert:0
			},
			success:function(data){
				if(data.code==200){
					if(data.data=='success'){
					}else{
					alert("责任人关联告警规则失败！");
					}
				}
			}
		})
		
	}
	bizBaseInfo.prototype={
		constructor:bizBaseInfo,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,type=cfg.type,id=cfg.id,name=cfg.name,openWay=cfg.openWay,bztype=cfg.type;
				buttons=[{
					text:'关闭',
					iconCls:' fa fa-times-circle',
					handler:function(){
						dlg.dialog('close');
					}
				}];
			if(type!='view'){
				buttons.unshift({
					iconCls:'fa fa-save',
					text:'保存',
					handler:function(){
						if(that.form.validate() && that.form.find("input[name=name]").val().trim() != ""){
//							if(that.form.find("input[name='file']").val().trim()!=""
//								&& !that.form.find("input[name='file']").val().trim().match(/gif|GIF|jpg|JPG|jpeg|JPEG|bmp|BMP|png|PNG$/)){
//								alert("图片只能上传jpg、jpeg、gif、png这几种格式");
//								return;
//							}
//							console.info(that.form.val());
//							console.info("type: "+type);
							if(type!='add'){
								cleanAlarmRulePerson(that.form.val().id);
							}
						
							oc.util.ajax({
								url:oc.resource.getUrl(
										(type=='add')?'portal/business/service/insertBasicInfo.htm':'portal/business/service/updateBasicInfo.htm'),
								data:that.form.val(),
								successMsg:null,
								success:function(data){
									if(data.code && data.code==200){
										dlg.dialog('close');
										if(type == 'add'){
											oc.business.service.edit.permissions.add({id:data.data,managerId:that.form.val().managerId})
											oc.ui.confirm('是否继续进行业务架构设置?',function(){
										    	 oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){
														oc.resource.loadScript('resource/module/business-service/buz/business.js',
															function(){
																new businessTopo({
																	bizId:data.data
																});
															});
												 });
											},function(){});
											if(that.form.val().managerId!=undefined){
												addAlarmRuleCondition(that.form.val().managerId,data.data);
											}
										
										}else{
											if(that.form.val().managerId!=undefined){
												addAlarmRuleCondition(that.form.val().managerId,that.form.val().id);
											}
											oc.business.service.edit.permissions.update({id:that.form.val().id,managerId:that.form.val().managerId})
										}
										//列表界面新增后刷新列表
										if(openWay!=undefined && openWay=="list"){
											oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
										}
										if(typeof(cfg.saveCallBack)=='function')
											cfg.saveCallBack();
										
										alert("保存成功！");
									}else if(data.code==299){
										alert(data.data);
									}else if(data.code==201){
										alert("业务名称不能与任何业务节点名称相同！");
										return;
									}
								}
							})
						}
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/business-service/biz_base_info.html'),
				title:(type=='add')?'新建业务系统':'编辑'+cfg.name,
				height:440,
				width:600,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					(type!='add')&&that._loadResource(cfg.id);
					if(that.cfg.type=='view'){
						that.form.disable();
					}
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
			saveCall:function(type,bizAccordion,id,json,rowDiv){
			}
		},
		_mainDiv:undefined,
		_id:'#bizBaseInfo',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			var that = this;
			this.form=oc.ui.form({
				selector:this._mainDiv.find('.bizBaseInfoForm')
			});
			
			//增加域的选择 20161202 dfw
			var domains = oc.index.getDomains();
			var defaultValue = 1;
			if(domains && domains.length > 0){
				defaultValue = domains[0].id;
			}
			oc.ui.combobox({
				  selector:$('#domainId'),
				  width:'162px',
				  placeholder:false,
				  selected:false,
				  value:defaultValue,
				  data:domains,
	  			  onChange : function(newValue, oldValue){
				  }
			});
			
			this.form.find("#previews").attr("src",
					oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+1011))
					.css({'display':'block',width:'67px',height:'67px'});
			
			this.form.find('#selectPicButton').linkbutton('RenderLB',{
				  iconCls:"fa fa-file-picture-o",
				  onClick:function(){
					  var name=$("input[name='name']").val();
					  if(name==""){
//						  alert("请先填写业务名称！");
					  }else{
						  oc.resource.loadScript("resource/module/business-service/js/biz_upload_pic.js", function(){
								oc.business.service.uploadpic.open();
								
							});  
					  }
						
				  }
			});
			
			var user = oc.index.getUser();
			if(user.userType==3){
				this.form.find("input[name='managerId']").val(user.id);
				this.form.find("input[name='managerName']").val(user.name);
			}
			this.form.find("input[name='managerName'],#selectEntryBtn").on('click',function(e){
//				var that=this;
//				console.info(that);
				e.stopPropagation();
				selectEntry(that);
			});
			
			this.form.find("input[name='managerName'],#clearParameterButton").on('click',function(){
//				entryDatagrid.selector.datagrid('options').queryParams = {searchContent:$('#queryManagerUserInput').val()};
//				entryDatagrid.reLoad();
				$(".bizBaseInfoForm").find("input[name='managerId']").val("");
				$(".bizBaseInfoForm").find("input[name='managerName']").val("");
				
			});
			function selectEntry(objs){
				var entryDlg=$('<div/>'),entryContent=null,
				entryButtons=[{
					text:'确定',
					iconCls:'fa fa-check-circle',
					handler:function(){
						var selectRow = $('#bizManagerSelectDatagrid').datagrid('getChecked');
						var data=$("input[type='radio']:Checked");
						if(selectRow&&selectRow.length>0){
							$("input[name='managerName']").val(selectRow[0].name);
							$("input[name='managerId']").val(selectRow[0].id);
							entryDlg.dialog('close');
						}else{
							if(data.length>0){
							var id=	data.attr('rid');
							var name=	data.attr('rname');
							$("input[name='managerName']").val(name);
							$("input[name='managerId']").val(id);
							entryDlg.dialog('close');
							}else{
//								console.info(objs);
								//清空责任人
//								console.info("type: "+objs.cfg.type);
							
								var value=that.form.val();
								if(objs.cfg.type!="add"){
									cleanAlarmRulePerson(value.id);
								}
							
								$("input[name='managerName']").val("");
								$("input[name='managerId']").val("");
								entryDlg.dialog('close');
//								alert('请选择负责人');
							}
							
						}
					}
				},{
					text:'取消',
					iconCls:'fa fa-times-circle',
					handler:function(){
						entryDlg.dialog('close');
					}
				}],
				entryContent = $("<div><div><div class='oc-toolbar datagrid-toolbar insert-btn'><input id='queryManagerUserInput' style='margin-right:40px;width:200px;' type='text' placeholder='输入用户姓名'/>" +
						"<button id='searchManagerUserByName'><span class='fa fa-search font16'></span>查询</button>" +
						"</div><div style='height:235px'><div id='bizManagerSelectDatagrid'/></div></div>");
				
				entryDlg.dialog({
					content:entryContent,
					title:'选择负责人',
					width:410,
					height:356,
					resizable:true,
					buttons:entryButtons,
					cache:false
				});
				var entryDatagrid = oc.ui.datagrid({
					pagination:false,
					selector:$('#bizManagerSelectDatagrid'),
					singleSelect:true,
					url:oc.resource.getUrl('portal/business/service/getManagerUsers.htm'),
					queryParams:{searchContent:''},
					columns:[[
						 {field: 'id',title: '<input type="radio" style="display:none;"/>',width: 20,
							 formatter: function(value, rowData, rowIndex){
							 return '<input type="radio" name="selectRadio" id="selectRadio' + 
							 	rowIndex + '" value="' + rowData.oid + '" rid="'+rowData.id+'" rname="'+rowData.name+'" />';
						 }},
				         {field:'account',title:'用户名',sortable:true,width:80},
				         {field:'name',title:'姓名',sortable:true,width:80}
				     ]],
				     onLoadSuccess:function(data){
				    	 var item = $('#bizManagerSelectDatagrid').datagrid('getRows'),index;
				    	 if(item){
				    		 for(var i=item.length-1; i >= 0; i--){
				    			 if(item[i].id == $("input[name='managerId']").val()){
				    				 index = $('#bizManagerSelectDatagrid').datagrid('getRowIndex', item[i]);
				    				 ($("input[type='radio']")[index+1]).checked=true;
				    				 break;
				    			 }
				    		 }
				    	 }
				    		var inputObj = $("#queryManagerUserInput");
							inputObj.keyup(function(e){
								if(e.keyCode==13){
									entryDatagrid.selector.datagrid('options').queryParams = {searchContent:$('#queryManagerUserInput').val()};
									entryDatagrid.reLoad();
								}
							});
				    	 
				     },
				     onSelect : function(index,row){
				    	 $('#selectRadio' + index).attr("checked",true);
				     }
				});
				$('#searchManagerUserByName').on('click',function(){
						entryDatagrid.selector.datagrid('options').queryParams = {searchContent:$('#queryManagerUserInput').val()};
						entryDatagrid.reLoad();
				});
			
			}
		},
		_loadResource:function(id){
			var that=this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getBasicInfo.htm'),
				data:{bizId:id},
				successMsg:null,
				success:function(d){
					that.form.val(d.data);
					that.form.find("#oldName").val(d.data.name);
					that.form.find("#previews").attr("src",
							oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+d.data.fileId))
							.css({'display':'block',width:'67px',height:'67px'});
				}
			});
		}
	};
	
	
	function setImagePreview(docObj,localImagId,imgObjPreview){
		docObj = docObj.get(0);
        if(docObj.files && docObj.files[0]){
            //火狐下，直接设img属性
        	imgObjPreview.css({'display':'block',width:'67px',height:'67px'})
        	.attr("src",window.URL.createObjectURL(docObj.files[0]));
        }else{
            //IE下，使用滤镜
            docObj.select();
            var imgSrc = document.selection.createRange().text;
            //必须设置初始大小
            localImagId.css({width:'67px',height:'67px'});
            //图片异常的捕捉，防止用户修改后缀来伪造图片
            try{
                localImagId.get(0).style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
                localImagId.get(0).filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
             }catch(e){
                $.messager.alert("info","提示","您上传的图片格式不正确，请重新选择!");
                return false;
              }                         
              imgObjPreview.style.display = 'none';
              document.selection.empty();
        }
        return true;
    }
	
	oc.ns('oc.module.biz.baseinfo');
	oc.module.biz.baseinfo={
		open:function(cfg){
			var detail=new bizBaseInfo(cfg);
			detail.open();
		}
	};
})(jQuery);