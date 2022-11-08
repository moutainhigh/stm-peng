$(function(){
var managedatagrid=undefined;
var loginUser = oc.index.getUser();
	oc.ns('oc.module.home.page.manage');

	
	function pageManage(mainDiv){
		
	}
	function editPage(type,name,data){
	var title="新增页面";
	var url=oc.resource.getUrl('system/home/layout/addLayout.htm');
	var pageForm=undefined;
	if(type=="edit"){//编辑页面
		title="编辑-"+name;
		url=oc.resource.getUrl('system/home/layout/updateLayoutBaseInfo.htm');
	}
			var dlg = $('<div/>');
			 dlg.dialog({
				href : oc.resource.getUrl('resource/module/home/pagemanage/manageEdit.html'),
				title : title,
				width:500,
				height : 300,
				scrollbars:"yes",
				modal: true,
				resizable : false,
				overflow:'auto',
				buttons:[{
					text:'确定',
					iconCls:'ico ico-ok',
					handler:function(){
					var data=new Object();
					
					var tempid=$(".checkTemp").attr('value');
				
						data.id=pageForm.val().id;
						data.name=pageForm.val().name;
						data.refreshTime=pageForm.val().refreshTime;
						data.tempId=tempid;
						data.tempSet=pageForm.val().tempSet;
						var lt = {w:1366,h:677,widgets:[]};
						data.layout= JSON.stringify(lt);
						 if(pageForm.val().name=="" || pageForm.val().name==null){
							alert("页面名称不能为空");
						}else if(pageForm.val().name.length>50){
							alert("名称最多输入50个字符");
						}else if(pageForm.val().refreshTime<30){
							alert("刷新周期不能小于30秒");
						}else{
							oc.util.ajax({
								url:url,
								data:data,
								successMsg:null,
								success:function(data){
								if(data.code==200){
								if(type=="edit"){
									alert(data.data);
								}else{
									alert("保存成功！");
								}
									dlg.dialog('close');
									if(pageForm.val().tempSet == 1){
										oc.index.home.widgetMger.option.layoutId = pageForm.val().id;
									}
											 managedatagrid.reLoad();
								}
								}
								
								});
									 managedatagrid.reLoad();
						}
					
					}
				
				},{
					text:'取消',
					iconCls:'fa fa-times-circle',
					handler:function(){
						dlg.dialog('close');
					}
				}],
				onLoad : function() {
				//初始化模板
		
				var mmform=$(".pageManageForm");
				pageForm = oc.ui.form({
						selector:mmform
					});
				if(type=="edit"){
				initTemp(data.bo.tempId);
					initForm(data.bo);
				}else{
				initTemp("");
				}	
			
  			$("#isDefault").click(function(){
				$("#isDefault").toggleClass("haveChecked");
			
				
				if($("#isDefault").attr('class')==undefined || $("#isDefault").attr('class')==""){
					 $("#tempSet").attr('value',0);
				}else{
					 $("#tempSet").attr('value',1);
				}
 			/* $("#isDefault").toggleClass("haveChecked");
 			 if($("#isDefault").attr('class')=="haveChecked"){
 			 		 $("#tempSet").attr('value',1);
 			 
 			 }else{
 			  	 $("#tempSet").attr('value',0);
 			 }*/
			});
	$("#templateList").find("span[name='temps']").each(function(index, value){
	$(".checkTemp").each(function(){
	$(this).removeClass('checkTemp');
	});
	$(this).click(function(){
		$(this).addClass("checkTemp");
		$("#tempId").val($(this).attr('value'));
	
	});

	
	});
      
				
				
				}
				
				
				});
			
	}
	function initForm(data){
	$("#id").val(data.id);
	$("#name").val(data.name);
	$("#refreshTime").val(data.refreshTime);
	$("#tempId").val(data.tempId);
	if(data.tempSet=="1"){//默认模板
	 $("#tempSet").attr('value',1);
	$("#isDefault").attr("checked","checked");
	}else{
	  $("#tempSet").attr('value',0);
	$("#isDefault").removeAttr("checked");
	$("#isDefault").removeClass("haveChecked");
	}
	$("#templateList").find("span[name='temps']").each(function(index, value){
	if($(this).attr('value')==data.tempId){
	$(this).addClass("checkTemp");
		$("#tempId").val($(this).attr('value'));
	}
	});
	}
 	 function initTemp(tempId){
 	 		//查询所有的默认模板，显示列表
		    oc.util.ajax({
            url:oc.resource.getUrl('system/home/layout/getAllTempLayout.htm'),
            timeout:null,
            success:function(data){
            if(data.data){
              var datas=data.data;
              $.each(datas,function(){
               var name=$(this)[0].name;
                var id=$(this)[0].id;//layout="'+layout+'"
                if(tempId!=""){//编辑状态
                 if(id==tempId){
                $("#templateList").append('<span name="temps" class="checkTemp"  value="'+id+'">'+name+'</span></br>'); 
                }else{
                $("#templateList").append('<span name="temps" value="'+id+'">'+name+'</span></br>'); 
                }
        		
                }else{
                 if(id==1){
                $("#templateList").append('<span name="temps" class="checkTemp"  value="'+id+'">'+name+'</span></br>'); 
                }else{
                $("#templateList").append('<span name="temps" value="'+id+'">'+name+'</span></br>'); 
                }
        		
                }
               
              
              });
            }
   
			$("#templateList span[name='temps']").each(function(){
			$(this).click(function(){
			$(".checkTemp").each(function(){
			$(this).removeClass('checkTemp');
			});
			$(this).addClass("checkTemp");
			var layout=$(this).attr("value");
			$("#tempId").attr("value",layout);
			});
			});	
            
            }
 
		
				});
		
 	 
 	 }	
	function cerateDatagrid(){
	 IsCheckFlag = true;
	managedatagrid=	oc.ui.datagrid({ 
		selector:$("#managetab"),
		pageSize:$.cookie('pageSize_user')==null ? 15 : $.cookie('pageSize_user'),
		queryConditionPrefix:'',
		checkOnSelect:false,
		url:oc.resource.getUrl('system/home/layout/getPageLayout.htm'),
//		delCfg:loginUserType!=2 ? {url:oc.resource.getUrl('system/user/del.htm')} : 0,
		columns:[[
	         {field:'id',title:'-',checkbox:true,width:50},
	        
	         {field:'name',title:'页面名称',sortable:true,width:80,formatter: function(value,row,index){
	        	 var showname = loginUser.id == row.userId ? ("<a value="+row.id+" name="+value+"  makeId='editHome' style='color:white'><span data-index='"+index+"' class='user_account' style='text-decoration: underline;cursor:pointer;' title='单击编辑此页面'>"+value+"</span></a>") : value;
	        return showname;
	        	 // return "<a value="+row.id+" name="+value+"  makeId='editHome' style='color:white'><span data-index='"+index+"' class='user_account' style='text-decoration: underline;cursor:pointer;' title='双击进入编辑'>"+value+"</span></a>";
        	 }},
	         {field:'createTimeStr',title:'创建时间',sortable:true, width:80},
	         {field:'cz',title:'操作',width:80,formatter:function(value,row,index){
	         var html="";
	         var statueCls=(row.isDefault==0)?'icon-shouye_shouye':'icon-shouye_shouye1';
	         var editBtnDom = (loginUser.id == row.userId || loginUser.id == 1) ? ('<a class="icon-edit light_blue"  makeId="editPage" copyUserId="'+row.copyUserId+'" value="'+row.id+'" name="'+row.name+'" title="参数设置"></a>') : '';
	         html='<a class="iconfont light_blue '+statueCls+' "   makeId="setHome" value="'+row.id+'" name="'+row.name+'" title="设为首页展示"></a>'+
	        '<a class="icon-copy light_blue"  makeId="copy" class="" value="'+row.id+'" title="复制"></a>'+
	        '<a class="icon-preview light_blue"  class="" makeId="detailInfo" value="'+row.id+'" name="'+row.name+'" title="预览"></a>' +
	        editBtnDom;
	         
	        	 return html;
	         }}
         ]],
         onLoadSuccess:function(){
          var panel = managedatagrid.selector.datagrid("getPanel"),
        	 rows = managedatagrid.selector.datagrid("getRows");
         //复制页面
         panel.find('a[makeId="copy"]').on('click',function(){
      
         var layoutId=$(this).attr('value');
         	       oc.util.ajax({
            url:oc.resource.getUrl('system/home/layout/copyLayout.htm'),
            data:{layoutId:layoutId},
            timeout:null,
            success:function(data){
            if(data.code==200){
            alert("复制成功");
          	 managedatagrid.reLoad();
            }else{
            alert("复制失败");
            }
            }
            
            });
         
         
         });
         //makeId="setHome" 设为首页
	        panel.find('a[makeId="setHome"]').on('click',function(){
	     	   	var layoutId = $(this).attr('value');
	     	   	var pageName = $(this).attr('name');
	        	oc.ui.confirm("是否确认覆盖首页设置？",function(){
	     	   		oc.util.ajax({
		            	url:oc.resource.getUrl('system/home/layout/updateHomeLayoutDefault.htm'),
		            	data:{layoutId:layoutId},
		            	timeout:null,
		            	success:function(data){
			           		if(data.code==200){
				            	alert("设置成功");
				         		$(".edit-mode .tab:first").text(pageName);
				            	oc.index.home.widgetMger.option.layoutId = this.data.layoutId;
				            	managedatagrid.reLoad();
			            	}else{
			            		alert("设置失败");
			            	}
	            		}
	     	   		});
	       		});
         });
          //makeId="editHome" 双击编辑页面事件
            panel.find('a[makeId="editHome"]').on('click',function(){
            	alert("编辑页面");
            	oc.index.home.widgetMger.widgetLoader.widgets = {};
	         	var layoutId=$(this).attr('value');
	            oc.index.home.widgetMger.option.layoutId = layoutId;
	         	$(".edit-mode .tab:first").click();
	         	var pageName = $(this).attr('name');
	         	$(".edit-mode .tab:first").text(pageName);
            });
            
            
         // makeId="editPage" 编辑页面
             panel.find('a[makeId="editPage"]').on('click',function(){
               var name=$(this).attr('name');
               	 var layoutId=$(this).attr('value');
               	var user = oc.index.getUser();
           
               	 var copyUserId=$(this).attr('copyUserId');
               	 if(copyUserId=="0"){//不是复制页面
               		 oc.util.ajax({
                         url:oc.resource.getUrl('system/home/layout/getLayout.htm'),
                         data:{layoutId:layoutId},
                         timeout:null,
                         success:function(data){
                         if(data.code==200){
                       	   editPage("edit",name,data.data);
                         }else{
                         alert("查询失败");
                         }
                         }
                         
                         });
               	 }else{
               		if(parseInt(copyUserId)==user.id){
                  		 oc.util.ajax({
                            url:oc.resource.getUrl('system/home/layout/getLayout.htm'),
                            data:{layoutId:layoutId},
                            timeout:null,
                            success:function(data){
                            if(data.code==200){
                          	   editPage("edit",name,data.data);
                            }else{
                            alert("查询失败");
                            }
                            }
                            
                            });
                  	 }else{
                  		 alert("没有编辑权限！");
                  	 }	 
               		 
               	 }
               	 
         
               
             
        //	 alert("editPage page");
        

         
         });
         //makeId="detailInfo" 预览
             panel.find('a[makeId="detailInfo"]').on('click',function(){
              	 var layoutId=$(this).attr('value');
              	 var title=$(this).attr('name');
        
         		 oc.resource.loadScripts(['resource/module/home/widgets/widgetLoader.js'],function(){
        

    });
         		var widgetMger=undefined;
         var dlg = $('<div/>').addClass('showDia');
			 dlg.dialog({
				href : oc.resource.getUrl('resource/module/home/pagemanage/showLayout.html'),
				title : "页面预览("+title+")",
				width:1200,
				height : 600,
				scrollbars:"yes",
				modal: true,
				resizable : false,
				overflow:'auto',
				onLoad : function() {
				var widgetLoader = new oc.index.home.widgetLoader();
			          widgetMger = new widgetManager({
			            managerMode:false,
			            gridster:'.gridster.readyw',
			            widgetLoader:widgetLoader,
			            layoutId:layoutId,
			            isQuery:false,
			            isPreview:true	//是否预览
			        });

					
					},onClose:function(){
						widgetMger.gridster.destroy();
						widgetMger.destroy();
						widgetMger={};
						$(".showDia").remove();
						dlg.dialog("destory");
					}
				
				
				});//*/
         
         });
         
         },onClickCell: function (rowIndex, field, value) {
             IsCheckFlag = false;
         },
         onSelect: function (rowIndex, rowData) {
             if (!IsCheckFlag) {
                 IsCheckFlag = true;
                $("#managetab").datagrid("unselectRow", rowIndex);
             }
         }/*,                    
         onUnselect: function (rowIndex, rowData) {
             if (!IsCheckFlag) {
                 IsCheckFlag = true;
                 managedatagrid.datagrid("selectRow", rowIndex);
             }
         }*/
      
	});
	
	//cookie记录pagesize
	var paginationObject = $("#managetab").datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				$.cookie('pageSize_biz',pageSize);
			},
		});
	}
		
	
	}
	pageManage.prototype = {
			constructor:pageManage,
			mainDiv:undefined,
			userDialog:undefined,
			open : function(){
				cerateDatagrid();//创建数据表格
				
				
				
			},
			add : function(){
	
			editPage('add','');

			},
			del :function(){
					var objs=managedatagrid.selector.datagrid('getChecked'), ids="",deletable=true;
					var idarr=new Array(),userIdArr = new Array();
					for(var i=0,len=objs.length;i<len;i++){
						var obj = objs[i];
						ids+=obj.id+",";
						idarr[i]=obj.id;
						userIdArr.push(obj.userId);
					}
					$.each(userIdArr, function(i, n){
						if(loginUser.id != n){
							deletable = false;
						}
					});
					
					if(!deletable){
						alert('只能删除由您创建的页面！');
						deletable=true;
						return;
					}

					ids=ids.substring(0,ids.length-1);	
					if(ids!=""){
						oc.ui.confirm("是否确认删除？",function() {
							for(var i=0;i<idarr.length;i++){
								oc.util.ajax({
									url : oc.resource.getUrl("system/home/layout/deleteLayout.htm"),
									data:{ids:ids},
									successMsg : null,
									sync:false,
									success : function(data) {
										if(data.code==200){
											alert(data.data);
											managedatagrid.reLoad();
										}
									}
								}); 
							}
						});
					}
			}
	}
	
	oc.module.home.page.manage = {
			open:function(mainDiv){
				var selfObj = new pageManage(mainDiv);
				selfObj.open();
			
			},
			add : function(mainDiv){
				var selfObj = new pageManage(mainDiv);
				selfObj.add();
			},
			del : function(mainDiv){
				var selfObj = new pageManage(mainDiv);
				selfObj.del();
			}
	}
});