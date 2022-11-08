(function($){
	var mainDiv=$('#oc_module_busniess_main').attr('id',oc.util.generateId()).panel({
			fit:true,
			isOcAutoWidth:true
		}),
		datagridDiv=mainDiv.find('.oc_module_busniess_main_datagrid').first(),
		user = oc.index.getUser(),
		loginUserType = user.userType,
		loginUserId = user.id,
		form = mainDiv.find(".oc-form").first(),
	queryForm = oc.ui.form({
		selector:form
	});
	// console.log(mainDiv.height() - 33 - $('#busniessToolbar').height());
	datagridDiv.parent().css('height',mainDiv.height() - 33 - $('#busniessToolbar').height() - 12);

	var status=-1;
	form.find(":input[name=status]").val(status);
	$("#busniessToolbar label[name='status']").each(function(){

		 $(this).click(function(){
			 mainDiv.find(".actived").each(function(){
					 $(this).removeClass('actived');
				 });
		$(this).find("a").addClass("actived");
		
			 if($(this).context.id=="all"){
				 status=-1;
				
			 }else if($(this).context.id=="down"){
				 status=3;
			 }else if($(this).context.id=="metric_error"){
				 status=2;
			 }else if($(this).context.id=="metric_warn"){
				 status=1;
			 }else if($(this).context.id=="metric_recover"){
				 status=0;
			 }
			 form.find(":input[name=status]").val(status)
			 oc.datag.reLoad();
		 });
	 });
	$("#Summary").on('click',function(){
		oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_summary_model.html'));
	});

	$("span.ico-refrash").click(function(){
		oc.datag.reLoad();
	});
		form.find(":input[name=queryName]").keyup(function(e){
			e.keyCode==13&&(datagrid.octoolbar.jq.find(".fa-search").trigger('click'));
		});
		placeholder4ie8();
		var queryName = form.find(":input[name=queryName]").val();
//		if(serchValue == '用户名/姓名/手机号码/邮箱'){
//			form.find(":input[name=keyword]").val("");
//		}
		var datagrid=null,
		_dialog = null;
		var userTypes = oc.util.getDictObj('user_type'),
		toolbar = (user.domainUser || user.systemUser) ? [{
			text:'添加',
			iconCls:'fa fa-plus',
			onClick:function(){
				
				  oc.resource.loadScript('resource/module/business-service/js/biz_base_info.js',function(){
					  oc.module.biz.baseinfo.open({
						  type:'add',
						  id:null,
						  name:null,
						  openWay:'list'
					  });
					
				  });
				//	oc.datag.reLoad();
				//	$("span.ico-refrash").trigger('click');
//					oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
				//  oc.datag.octoolbar.jq.find(".icon-search").trigger('click')
			}
	
		},{
			text:'删除',
			iconCls:'fa fa-trash-o',
			onClick:function(){
				var objs=oc.datag.selector.datagrid('getChecked'), ids="";
			var length=objs.length;
			var idarr=new Array();		
			for(var i=0,len=objs.length;i<len;i++){
					var obj = objs[i];
					ids+=obj.bizId+",";
					idarr[i]=obj.bizId;
				}

			ids=ids.substring(0,ids.length-1);
			if(ids!=""){
				 oc.ui.confirm("是否确认删除？",function() {//checkNotifyTemplateEnabled
					 for(var i=0;i<idarr.length;i++){
					var isAllowDelete=	 oc.business.service.edit.permissions.checkDelete(idarr[i]);	
					if(isAllowDelete){
							 oc.util.ajax({
						url : oc.resource.getUrl("portal/business/service/deleteBiz.htm"),
						data:{ids:ids},
						successMsg : null,
						sync:false,
						success : function(data) {
							
							if(data.code==200){
									alert("删除成功")
									oc.datag.reLoad();
							}
						}
					}); 
					}/*else{
						alert("没有删除权限！")
					}*/
					 }
				
		        	  
		          });
			}
				
				
			}
		}] : [];
		toolbar.push();
		//console.log(queryForm);

	oc.datag=datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_biz')==null ? 15 : $.cookie('pageSize_biz'),
		queryForm:queryForm,
		queryName:queryName,
		status:status,
		queryConditionPrefix:'',
		hideReset : true,
		checkOnSelect:false,
		hideSearch : true,
		url:oc.resource.getUrl('portal/business/service/getListPages.htm'),
		octoolbar:{
			left:[queryForm.selector],
			right:toolbar
		},
		columns:[[
	         {field:'bizId',title:'-',checkbox:true,width:50,isDisabled:function(value,row,index){
	        	var isDelete=oc.business.service.edit.permissions.checkDelete(row.bizId);
	        	if(isDelete==false){
	        		 return true
	        	 }else{
	        		 return false;
	        	 }
	        	 
	         }},
	         {field:'bizName',title:'名称',sortable:true,width:80,formatter: function(value,row,index){
	        	var light="";
	        	 if(row.bizStatus==0){
	        		 light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'" class="light-ico greenlight bizname">'+row.bizName+'</label>';
	        	 }else if(row.bizStatus==1){
	        		 light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'" class="light-ico yellowlight bizname">'+row.bizName+'</label>';
	        	 }else if(row.bizStatus==2){
	        		 light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'" class="light-ico orangelight bizname">'+row.bizName+'</label>';
	        	 }else if(row.bizStatus==3){
	        		 light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'"  class="light-ico redlight bizname">'+row.bizName+'</label>';
	        	 }
	        	 return light;
	         }},
	         {field:'healthScore',title:'健康度',sortable:true,width:80,
	        	 formatter:function(value,row,index){
	        		 return '<label style="cursor:pointer" rowIndex="'+index+'" class="bizhealthScore">'+row.healthScore+'</label>';
	        	 }	 
	         },
	         {field:'availableRate',title:'可用率',sortable:true, width:80},
	         {field:'mttr',title:'MTTR',sortable:true,width:80},
	         {field:'mtbf',title:'MTBF',sortable:true,width:80},
	         {field:'outageTimes',title:'宕机次数',sortable:true,width:80},
	         {field:'managerName',title:'责任人',sortable:true,width:80,formatter:function(value,row,index){
	        	 return row.managerName!=null ? value : '暂无';
	         }
	         },
	         {field:'phone',title:'电话号码',sortable:true,width:80,formatter:function(value,row,index){
	        	 if(row.phone==null || row.phone==''){
	        			return '暂无';
	        		 
	        	 }else{
	        		 return value;
	        	 }
	         }},
	         {field:'cz',title:'操作',sortable:true,width:100,formatter:function(value,row,index){
	        	 var isAllowEdit = oc.business.service.edit.permissions.checkEdit(row.bizId);
	        	 if(isAllowEdit){
	        		 return '<a class="icon-edit light_blue" name="'+row.bizName+'" openid="editBiz" id="editBiz" title="编辑业务系统" value="'+row.bizId+'"></a><a class="icon-stateye light_blue" openid="stateye" id="stateye" title="状态定义" value="'+row.bizId+'"></a><a class="icon-alarmset light_blue" openid="preset" id="preset" title="告警设置" value="'+row.bizId+'"></a><a class=" icon-detailed_info light_blue"  openid="detailInfo" id="detailInfo" title="详情信息" value="'+row.bizId+'" name="'+row.bizName+'"></a>';	 
	        	 }else{
	        		 return '<a class="icon-detailed_info light_blue" openid="detailInfo" id="detailInfo" title="详情信息" value="'+row.bizId+'" name="'+row.bizName+'"></a>';
	        	 }detailed_info
	        	
	         }}
         ]],
         onLoadSuccess:function(){
     	/*	var startTime=new Date();
    		var endTime=startTime.getDate()-7;
    		startTime.format("yyyy-MM-dd HH:mm:ss");
    		endTime.format("yyyy-MM-dd HH:mm:ss");
    		console.info(startTime);
    		console.info(endTime);*/
        	 var panel = datagrid.selector.datagrid("getPanel"),
        	 rows = datagrid.selector.datagrid("getRows");
        	 panel.find(".bizname").on('click',function(){
        		 var rowIndex = $(this).attr('rowIndex');
		    	 var row = datagrid.selector.datagrid("getRows")[rowIndex];
		    	 oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),		function(){
						oc.resource.loadScript('resource/module/business-service/buz/business.js',
							function(){
								oc.business.service.edit.permissions.setLastEnter(1);
								new businessTopo({
									bizId:row.bizId
								});
							});
					});
        	 });
        	 panel.find(".bizhealthScore").on('click',function(){
        		 var rowIndex = $(this).attr('rowIndex');
        		 var row = datagrid.selector.datagrid("getRows")[rowIndex];
        			oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
        				oc.module.biz.detailinfo.open({bizId:row.bizId,bizName:row.bizName});
        			});
        	 });
        	 
        	/* panel.find("#alarm").on('click',function(e){
        		 var id=$(this).attr('value');
        		 var name=$(this).attr('name');
        		 var row=null;
			  		e.stopPropagation();
		  			var addWindow = $('<div/>');
		  			var alarmDiv = $('<div/>');
		  			//构建dialog
		  			addWindow.dialog({
		  			  title:"告警信息",
		  			  href:oc.resource.getUrl('resource/module/business-service/alarm.html'),
		  			  width:800,
		  			  height:510,
		  			  modal:true,
		  			  onLoad:function(){
		  				oc.resource.loadScript('resource/module/business-service/js/alarm.js',function(){
		  					oc.module.biz.ser.alarm.open({id:id,name:name});
		  				});
		  			  }
		  			});
		  
        		 
        		 
        	 });*/
		//
        	panel.find("a[openid='preset']").each(function(){
        		$(this).on("click",function(){
        	 // console.info("preset...");
        		 var bizId=$(this).attr('value');
        			oc.resource.loadScript('resource/module/business-service/js/biz_warn_setting.js',function(){
        				oc.business.service.set.open({
        					clickData:bizId,
        					type:"edit",
        					currentID:'list'
        				});
        			});
        		
        			$("#busniessToolbar label[name='status']").each(function(){
		 					 $(".actived").each(function(){
								 $(this).removeClass('actived');
							 });
					
				 });
        	 });
        	
        	});
 
      panel.find("a[openid='detailInfo']").each(function(){
      $(this).on('click',function(e){
        			 var bizId=$(this).attr('value');
        			 var bizName=$(this).attr('name');
        			oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
        				oc.module.biz.detailinfo.open({bizId:bizId,bizName:bizName});
        			});
        			
        		});
      })
        	
        		
        		panel.find("a[openid='stateye']").each(function(){
        			$(this).on('click',function(e){
        			 var bizId=$(this).attr('value');
        			oc.resource.loadScript('resource/module/business-service/js/biz_status_define.js', function(){
        				oc.module.bizmanagement.status.define.open(bizId);
        			})
        			
        			
        		});
        		});
        	
        		//
			
        		panel.find("a[openid='editBiz']").each(function(){
        			$(this).on('click',function(e){
        			 var name=$(this).attr('name');
        			 var bizId=$(this).attr('value');
        			  oc.resource.loadScript('resource/module/business-service/js/biz_base_info.js',function(){
    					  oc.module.biz.baseinfo.open({
    						  type:'edit',
    						  id:bizId,
    						  name:name,
    						  openWay:'list'
    					  });
    				  });
        			
        		});
        		});
        	 panel.find(".queryBtn").linkbutton('RenderLB',{
    				iconCls:'fa fa-search',
    				onClick : function(){
//    					this.datagrid.reLoad();
    					oc.datag.reLoad();
    				}
    			});
        	 panel.find(".resetBtn").linkbutton('RenderLB',{
    				iconCls:'icon-reset',
    				onClick : function(){	
    					form.find(":input[name=queryName]").val("");
    				
    						$("#busniessToolbar label[name='status']").each(function(){
	 						 $(".actived").each(function(){
							 $(this).removeClass('actived');
				 });
	
	});
    				}
    			});
        	 

         }
		,onClickCell: function (rowIndex, field, value) {
             IsCheckFlag = false;
         }
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				$.cookie('pageSize_biz',pageSize);
			},
		});
	}
	
	function _openModel(row, e){}

	
	function getEventTop(e){
		var top = 0;
		if(navigator.userAgent.indexOf('Firefox')!=-1){
			top = 90;
		} else if(navigator.userAgent.indexOf('MSIE')!=-1){
			top = 100;
		} else{
			top = 58;
		}
		return e.screenY - top;
	}
	function _openWarn(cfg){
		oc.resource.loadScript('resource/module/business-service/js/biz_warn_setting.js',function(){
			oc.business.service.set.open($.extend({
				datagrid:datagrid
			},cfg));
		});
	}
	function placeholder4ie8(){
		if( !('placeholder' in document.createElement('input')) ){  
			$('input[placeholder],textarea[placeholder]').each(function(){   
				var that = $(this),   
				text= that.attr('placeholder');   
				if(that.val()===""){   
					that.val(text).addClass('placeholder');   
				}   
				that.focus(function(){   
					if(that.val()===text){   
						that.val("").removeClass('placeholder');   
					}   
				})   
				.blur(function(){   
					if(that.val()===""){   
						that.val(text).addClass('placeholder');   
					}   
				})   
				.closest('form').submit(function(){   
					if(that.val() === text){   
						that.val('');   
					}   
				});   
			});   
		}  
	}
})(jQuery);