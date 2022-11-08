(function($){
	var mainId=oc.util.generateId(),
		mainDiv=$('#oc_module_system_user_main').attr('id',mainId),
		datagridDiv=mainDiv.find('.oc_module_system_user_main_datagrid'),
		datagrid=null;
		var queryForm=mainDiv.find('form:first');
		var operType = {add:'添加',update:'编辑',del:'删除',search:'查询',other:'其他'};
		var operTypeData = [
		                    {id:'add',name:'添加'},
		                    {id:'update',name:'编辑'},
		                    {id:'del',name:'删除'},
		                    {id:'search',name:'查询'},
		                    {id:'other',name:'其他'}
		                    ];
		var conditionForm=oc.ui.form({
			selector:queryForm,
			combobox:[{
				selector:'[name="oper_module_id"]',
				url:oc.resource.getUrl("system/auditlog/getOperModule.htm"),
				width:120,
				valueField:'moduleId',
				textField:'moduleName',
				placeholder:'-- 操作模块 --',
				filter:function(data){
					return data.data;
				}
			},{
				selector:'[name="oper_type"]',
				width:100,
				valueField:'id',
				textField:'name',
				placeholder:'-- 操作类型 --',
				data:operTypeData
			}],
			datetimebox:[{
				selector:'[name="beginDate"]',
				width:150,
				value:oc.util.dateAddDay(new Date(), -1).stringify()
			},{
				selector:'[name="endDate"]',
				width:150,
			}]
		});
		
		var htmlStr = '<div class="audit-tool m-heade-tool"><div class="oc-header-m"><span style="margin-top:5px;margin-right:0px;color: #4C99DE;" class="fa fa-cog locate-right" title="操作"></span><div class="h-open-ico locate-right audit-right"  style="display:none;">';
			htmlStr+='<span class="fa fa-file-excel-o" style="font-size:15px;color: #4C99DE;" title="导出"></span>';
			htmlStr+='<span class="ico icon_emptyaa" title="清空"></span>';
			htmlStr+='<span class="fa fa-trash-o" style="font-size:18px;color: #4C99DE;" title="删除"></span></div></div></div>';

		
		placeholder4ie8();
//		var serchValue = queryForm.find(":input[name=keyword]").val();
//		if(serchValue == '搜索用户名|IP|操作对象'){
//			queryForm.find(":input[name=keyword]").val("");
//		}
//		oc.condition=conditionForm;
	datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		url:oc.resource.getUrl('system/auditlog/pageSelect.htm'),
		queryForm:conditionForm,
		queryConditionPrefix:"",
		sortName:'OPER_DATE',
		sortOrder:'DESC',
		columns:[[
	         {field:'id',checkbox:true},
	         {field:'oper_date',title:'操作时间',sortable:true,formatter:function(value,row,index){
	             //var unixTimestamp = new Date(value); unixTimestamp.toLocaleString()
	        	 
	             return formatCreateTime(value);},width:60},
	         {field:'oper_user',title:'操作人',sortable:true,width:40},
	         {field:'oper_ip',title:'客户端IP',sortable:true,width:40},
	         {field:'oper_module',title:'操作模块',sortable:true,width:40},
	         {field:'oper_type',title:'操作类型',width:20,formatter:function(v,c,i){
	        	 return operType[v];
	         }},
	         {field:'oper_object',title:'操作对象',sortable:true,width:160,ellipsis:true}
         ]],
         octoolbar:{
        	left:[
        	    conditionForm.selector,
        	    htmlStr
			]
         }
	});
	$('.oc-header-m').unbind('click');
	$('.oc-header-m').on('click',function(e){
		switch ($(e.target).attr('class')) {
		case 'fa fa-cog locate-right':
			var openIco = $('.oc-header-m').find('.h-open-ico');
			var showStatus = openIco.css('display');
			
			if(showStatus=='block'){
				openIco.hide();
			}else{
				openIco.show();
			}
			break;
		case 'fa fa-file-excel-o':
			var conditionObj = conditionForm.val();
			var condition = "";
			var i=0;
			for(var key in conditionObj){
				if(i==0){
					condition+='?'+key+'='+encodeURI(encodeURI(conditionObj[key]));
				}else{
					condition+='&'+key+'='+encodeURI(encodeURI(conditionObj[key]));
				}
				i++;
			}
			window.location.href=oc.resource.getUrl('system/auditlog/exportlog.htm'+condition);
			break;
		case 'fa fa-trash-o':
			var id = datagrid.getSelectIds();
			if(id==null||id==""){
				alert("请选择要删除的日志");
				return;
			}
			oc.ui.confirm("确认删除所选的日志吗？",function(){
				oc.util.ajax({
					  url: oc.resource.getUrl('system/auditlog/deleteSelect.htm?id='+id),
					  success:function(data){
						  if(data=="FALSE"){
							  alert("删除失败");
						  }else{
							  alert("删除成功");
							  datagrid.reLoad();
						  }
						  
					  }
				})
			});
			break;
		case 'ico icon_emptyaa':
			oc.ui.confirm("确认删除所有的日志吗？",function(){
			oc.util.ajax({
				  url: oc.resource.getUrl('system/auditlog/deleteAll.htm'),
				  success:function(data){
					  if(data=="FALSE"){
						  alert("清空失败");
					  }else{
						  alert("数据已清空");
						  datagrid.reLoad();
					  }
					  
				  }
			})
		});
			break;
		}
	})
	
	function formatCreateTime(templateCreateTime){
		var date = new Date(parseFloat(templateCreateTime));
		
		return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+' '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
		
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