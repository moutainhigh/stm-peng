(function($){
	var mainId=oc.util.generateId(),
		mainDivBuckup=$('#oc_module_system_backup').attr('id',mainId),
		datagridDivBuckup=mainDivBuckup.find('.oc_module_system_user_main_datagrid'),
		datagridBuckup=null;
		var queryForm=mainDivBuckup.find('form:first');
		var conditionForm=oc.ui.form({
			selector:queryForm,
			datetimebox:[{
				selector:'[name="beginDate"]',
				width:150,
				value:oc.util.dateAddDay(new Date(), -29).stringify()
			},{
				selector:'[name="endDate"]',
				width:150,
				value:oc.util.dateAddDay(new Date(), +1).stringify()
			}]
		});
//		placeholder4ie8();
		datagridBuckup=oc.ui.datagrid({
		selector:datagridDivBuckup,
		url:oc.resource.getUrl('system/auditlog/getBuList.htm'),
		queryForm:conditionForm,
		queryConditionPrefix:"",
		sortName:'OPER_DATE',
		sortOrder:'DESC',
		columns:[[
	         {
		    	 field:'backup_date',
		    	 title:'备份时间',
	             sortable:true,
	             formatter:function(value,row,index){
	            	 return formatCreateTime(value);
	             },
	             width:60
	         },
	         {
	             field:'oper_object',
	             title:'操作',
	             sortable:true,
	             formatter:function(value,row,index){
	            	 var butime = formatCreateTime(row.oper_date);
	            	 return "<a href='javascript:formatRecovery(\"" + butime + "\")' class='ico ico-back' title='日志恢复'>恢复</a>";
	             },
	             width:160,
	             ellipsis:true
	         }
         ]],
         octoolbar:{
        	right:[conditionForm.selector,{
				text:"手动备份",
				onClick:function(){
					oc.util.ajax({
						  url: oc.resource.getUrl('system/auditlog/backUpAudit.htm'),
						  timeout : null,
						  success:function(data){
							  if(data=="FALSE"){
								  alert("备份失败");
							  }else{
								  alert("数据已备份");
                                  // console.log(datagridBuckup);
								  datagridBuckup.reLoad();
							  }
							  
						  }
					})
				}
			}]
         }
	});
	
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

function formatRecovery(operDate){
	oc.util.ajax({
		  url: oc.resource.getUrl('system/auditlog/batchUpdate.htm?oper_date='+operDate),
//		  data:{instanceId : insId},
		  timeout : null,
		  success:function(data){
			  if(data=="FALSE"){
				  alert("数据恢复失败");
			  }else{
				  alert("数据已恢复");
//				  datagrid.reLoad();
			  }
			  
		  }
	})
}