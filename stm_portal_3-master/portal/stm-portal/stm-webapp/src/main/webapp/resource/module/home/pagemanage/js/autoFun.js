$(function(){
function AutoPage(mainDiv){
		
	}
	AutoPage.prototype = {
			constructor:AutoPage,
			mainDiv:undefined,
			open : function(){
			var dlg = $('<div/>');
			 dlg.dialog({
			 	href : oc.resource.getUrl('resource/module/home/pagemanage/autoFun.html'),
				title : "轮播设置",
				width:600,
				height : 500,
				scrollbars:"yes",
				modal: true,
				resizable : false,
				overflow:'auto',
					buttons:[{
					text:'确定',
					iconCls:'ico ico-ok',
					handler:function(){
					//选中页面,轮播时间,动画效果
					var checkDivid="";
						$("#checkDiv").find("li").each(function(){
				 checkDivid+=$(this).find("input").attr("value")+",";
			
				});
				var checkDivids=checkDivid.substring(0,checkDivid.length-1);
				var slideTime=$("#slideTime").val();
				var animation=$(".animation_active").find("input").val();
		 		 oc.util.ajax({
          			  url:oc.resource.getUrl('system/home/layout/slide/saveSlide.htm'),
          			  timeout:null,
          			  data:{
          			  checkDivids:checkDivids,
          			  slideTime:slideTime,
          			  animation:animation
          			  },
         			  success:function(data){
         			  
         			   dlg.dialog('close');
         			  }
         			  
         			  });
					}
					
					},{
					text:'取消',
					iconCls:'fa fa-times-circle',
					handler:function(){
						   dlg.dialog('close');
					}
					
					}],
					onLoad : function(){
					//初始化表格数据,获取用户下的所有页面
					initUncheckDiv();
					initCheckDiv();//初始化用户所有的轮播页面
					addAnimationCss();
				
					}
				
				});
				
			},
			add : function(){
	
			

			}
	}
	
function initForm(data){
$("#slideTime").val(data[0].slideTime);
$("input[value='"+data[0].animation+"']").parent().addClass("animation_active");

}	
//为动画效果div添加选中样式
function addAnimationCss(){
 $("#showDiv div").each(function(){
 $(this).on('click',function(){
 	 $(".animation_active").each(function(){
					 $(this).removeClass('animation_active');
				 });
				 $(this).addClass("animation_active");
 
 });
 });

}
	
	function initCheckDiv(){
		var id=0;var name="";var defaultLayout=undefined;
		
	  oc.util.ajax({
          			  url:oc.resource.getUrl('system/home/layout/slide/getSlide.htm'),
          			  timeout:null,
          			async:false,
         			  success:function(data){
            			var list=data.data.list;
            			initForm(list);
            			$.each(list,function(){
            				 id=$(this)[0].layoutId;
            				 name=$(this)[0].name;
            				 defaultLayout=$(this)[0].defaultLayout;
            			var html="";
            			html+='<li class="oc-workbench-rtitle" workbenchId="'+id+'" >'+
            			'<label title="'+$(this)[0].name+'">'+$(this)[0].name+'</label>'+
            			'<input type="hidden" layoutname="'+name+'" defaultLayout="'+defaultLayout+'" value="'+id+'"/>'+
            			'<span class="oc-workbench-rtitle-del fa fa-close" layoutname="'+name+'" defaultLayout="'+defaultLayout+'" value="'+id+'" ></span>'+
            			
						' </li>';
            			$("#checkDiv").append(html);
            		$('#SelectDiv').find("ul:first").sortable();
            			});
            			  rightDiv(name,defaultLayout,undefined);
            
        		    }
					});	

	     	
	}
	
	function initUncheckDiv(){
		var id=0;var name="";var defaultLayout=undefined;
		  oc.util.ajax({
          			  url:oc.resource.getUrl('system/home/layout/getAllLayouts.htm'),
          			  timeout:null,
          			  asnyc:false,
         			  success:function(data){
            			var list=data.data.list;
            			$.each(list,function(){
            			 name=$(this)[0].name;
            			 id=$(this)[0].id;
            			 defaultLayout=$(this)[0].defaultLayout;
            			var html="";
            			html+='<li class="oc-workbench-rtitle" workbenchId="'+id+'">'+
            			' <span class="ico ico-add oc-workbench-rtitle-add " defaultLayout="'+defaultLayout+'" layoutname="'+name+'" value="'+id+'" ></span>'+
            			'<label title="'+name+'">'+name+'</label>'+
            			'<input type="hidden" layoutname="'+name+'" value="'+id+'"/>'+
            			' </li>';
            			$("#uncheckDiv").append(html);
            				
					
            			});
            			  leftDiv(name,defaultLayout,undefined);
            			
        		    }
					});	
	
	} 
	
	
	oc.ns('oc.module.home.page.auto');
	oc.module.home.page.auto = {
			open:function(mainDiv){
				var selfObj = new AutoPage(mainDiv);
				selfObj.open();
			
			}
	}
	
});
function rightDiv (name,defaultLayout,id){
	if(id==undefined){
		$('#SelectDiv').find(".oc-workbench-rtitle-del").each(function(){
			$(this).on('click',function(){
				var name=$(this).attr('layoutname');
				var defaultLayout=$(this).attr("defaultLayout");
				var layoutId= $(this).attr("value");
				changeUNCheck(name,defaultLayout,layoutId);
			});
		});	
	}else{
		$('#SelectDiv').find("li[workbenchId='"+id+"']").on('click',function(){
			changeUNCheck(name,defaultLayout,id);
		});
	}

}
function leftDiv (name,defaultLayout,id){
	if(id==undefined){
		$('#uncheckDiv').find("li span").each(function(){
			$(this).on('click',function(){
				var name=$(this).attr('layoutname');
				var defaultLayout=$(this).attr("defaultLayout");
				var layoutId= $(this).attr("value");
				changeCheck(name,defaultLayout,layoutId);
			});
		});
	}else{
		$('#uncheckDiv').find("li[workbenchId='"+id+"']").on('click',function(){
			changeCheck(name,defaultLayout,id);
		});
	}

}
function changeCheck(name,defaultLayout,id){
var html='<li class="oc-workbench-rtitle" workbenchId="'+id+'" >'+
	    '<label title="'+name+'">'+name+'</label>'+
	     '<input type="hidden" defaultLayout="'+defaultLayout+'" layoutname="'+name+'" value="'+id+'"/>'+
	     ' <span class="oc-workbench-rtitle-del fa fa-close"  defaultLayout="'+defaultLayout+'" layoutname="'+name+'" value="'+id+'"></span>'+
	        ' </li>';
			$("#checkDiv").append(html);
			$('#SelectDiv').find("ul:first").sortable();
			$("#uncheckDiv").find("li[workbenchId='"+id+"']").remove();
		
			rightDiv(name,defaultLayout,id);
		//	$(this).remove();

}

function changeUNCheck(name,defaultLayout,id){
var html='<li class="oc-workbench-rtitle" workbenchId="'+id+'" >'+
' <span class="ico ico-add  " layoutname="'+name+'" defaultLayout="'+defaultLayout+'" value="'+id+'"></span>'+
	    '<label title="'+name+'">'+name+'</label>'+
	     '<input type="hidden" layoutname="'+name+'" defaultLayout="'+defaultLayout+'" value="'+id+'"/>'+
	        ' </li>';
		
		/*	$("#checkDiv").find("li").each(function(){
				var checkDivid=$(this).find("input").attr("value");
				console.info(checkDivid+"---"+id+"  --"+defaultLayout);
			
			
			});*/

if(defaultLayout==0){
	alert("默认页面不能被删除");
}else{
	$("#uncheckDiv").append(html);
	$("#checkDiv").find("li[workbenchId='"+id+"']").remove();


}


			leftDiv(name,defaultLayout,id);
}
