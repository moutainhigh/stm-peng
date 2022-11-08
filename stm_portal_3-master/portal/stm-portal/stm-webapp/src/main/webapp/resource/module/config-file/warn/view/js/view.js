(function($){
	var mainDiv=$('#oc_module_config_warn_view').attr('id',oc.util.generateId()).panel({
			fit:true,
			isOcAutoWidth:true
		}),
	datagridDiv=mainDiv.find('.oc_module_config_warn_view_datagrid');
	//chooseGroupId = oc.module.config.index.getChooseGroupId();
	var datagrid=null;
	var _dialog = $('<div class="oc-dialog-float"/>');
	var toolbar = [{
		text:'导出本页',
		iconCls:'l-btn-icon icon-expExcel',
		onClick:function(){
			if(!checkTime()) return;
			var options = datagridDiv.datagrid('getPager').data("pagination").options;  
			oc.util.ajax({
				url: oc.resource.getUrl('portal/config/warn/config/exportCheckData.htm?all=false'
						+'&rowCount='+options.pageSize+'&startRow='
						+(options.pageNumber>1?options.pageSize*(options.pageNumber-1):0)
						+'&total='+options.total
						+'&ipOrName='+$('input[name=ipOrName]').val()
						+'&timeRadio='+($('input[name=timeRadio]').eq(0).is(':checked')?0
								:($('input[name=timeRadio]').eq(1).is(':checked')?1:null))
						+'&queryTime='+$('input[name=queryTime]').val()
						+'&startTime='+$('input[name=startTime]').val()
						+'&endTime='+$('input[name=endTime]').val()),
				successMsg:null,
				success:function(data){
					var flag = data.data;
					if(flag){
						window.location.href=oc.resource.getUrl('portal/config/warn/config/exportWarnView.htm?all=false'
								+'&rowCount='+options.pageSize+'&startRow='
								+(options.pageNumber>1?options.pageSize*(options.pageNumber-1):0)
								+'&total='+options.total
								+'&ipOrName='+$('input[name=ipOrName]').val()
								+'&timeRadio='+($('input[name=timeRadio]').eq(0).is(':checked')?0
										:($('input[name=timeRadio]').eq(1).is(':checked')?1:null))
								+'&queryTime='+$('input[name=queryTime]').val()
								+'&startTime='+$('input[name=startTime]').val()
								+'&endTime='+$('input[name=endTime]').val());
						return ;
					}else{
						alert('无数据,无法导出!');
						return ;
					}
				}
			});
		}
	}];
	var form = $('<form class="oc-form float"/>')
		.append($('<div class="form-group"/>')
				.append('<input type="text" class="oc-enter" placeholder="名称/IP" name="ipOrName" style="width:90px;"/>')
				.append('<input type="radio" name="timeRadio" value="0"/><input type="text" name="queryTime"/>')
				.append('<input type="radio" name="timeRadio" value="1"/><input type="text" name="startTime" id="startTime"/>--')
				.append('<input type="text" name="endTime" id="endTime"/>'))
		.append($('<div class="form-group"/>').append('<a class="queryBtn">查询</a>'))
		.append($('<div class="form-group"/>').append('<a class="resetBtn">重置</a>'));
	var queryForm = oc.ui.form({ selector : form,
		combobox:[{selector : '[name=queryTime]', fit : false,width:84,value : 1,
	        	 	data : [{id : '1', name : '最近1小时', selected : true},{
							id : '2', name : '最近2小时' }, {
							id : '3', name : '最近4小时' }, {
							id : '4', name : '最近1天' }, {
							id : '5', name : '最近7天' } ],
						placeholder:null
					}       
		         ],
         datetimebox : [{selector:'[name="startTime"]',editable:false}, {selector:'[name="endTime"]',editable:false}]
	});
	form.find('.oc-enter').keypress(function(e,i){
		if(e.keyCode==13)
			datagrid.reLoad({ipOrName:$("input[name='ipOrName']").val()});
	});
	form.find(".queryBtn").linkbutton('RenderLB',{
		iconCls:'icon-search',
		onClick : function(){
			if(!checkTime()) return;
			datagrid.reLoad({ipOrName:$("input[name='ipOrName']").val()});
		}
	});
	form.find(".resetBtn").linkbutton('RenderLB',{
		iconCls:'icon-reset',
		onClick : function(){
			queryForm.reset();
		}
	});
	form.find('input[name="ipOrName"]').fixPlaceHolder(); //add by sunhailiang on 20170630 ie9支持placeholder
	toolbar.push({
		text:'导出全部',
		iconCls:'l-btn-icon icon-expExcel',
		onClick:function(){
			if(!checkTime()) return;
			var options = datagridDiv.datagrid('getPager').data("pagination").options;  
			oc.util.ajax({
				url: oc.resource.getUrl('portal/config/warn/config/exportCheckData.htm?all=true'
						+'&rowCount='+options.pageSize+'&startRow='
						+(options.pageNumber>1?options.pageSize*(options.pageNumber-1):0)
						+'&total='+options.total
						+'&ipOrName='+$('input[name=ipOrName]').val()
						+'&timeRadio='+($('input[name=timeRadio]').eq(0).is(':checked')?0
								:($('input[name=timeRadio]').eq(1).is(':checked')?1:null))
						+'&queryTime='+$('input[name=queryTime]').val()
						+'&startTime='+$('input[name=startTime]').val()
						+'&endTime='+$('input[name=endTime]').val()),
				successMsg:null,
				success:function(data){
					var flag = data.data;
					if(flag){
						window.location.href=oc.resource.getUrl('portal/config/warn/config/exportWarnView.htm?all=true'
								+'&rowCount='+options.pageSize+'&startRow='
								+(options.pageNumber>1?options.pageSize*(options.pageNumber-1):0)
								+'&total='+options.total
								+'&ipOrName='+$('input[name=ipOrName]').val()
								+'&timeRadio='+($('input[name=timeRadio]').eq(0).is(':checked')?0
										:($('input[name=timeRadio]').eq(1).is(':checked')?1:null))
								+'&queryTime='+$('input[name=queryTime]').val()
								+'&startTime='+$('input[name=startTime]').val()
								+'&endTime='+$('input[name=endTime]').val());
						return ;
					}else{
						alert('无数据,无法导出!');
						return ;
					}
					
				}
			});
		}
	});
	datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_view')==null ? 15 : $.cookie('pageSize_view'),
		url:oc.resource.getUrl('portal/config/warn/config/getWarnViewPage.htm'),
		queryForm:queryForm,
		queryConditionPrefix:'',
		hideReset:true,
		hideSearch:true,
		octoolbar:{
			left:[queryForm.selector],
			right:toolbar
		},
		columns:[[
	         {field:'id',title:'',width:20,hidden:true},
	         {field:'name',title:'设备名称',sortable:true,width:70,formatter:function(value,rowData,rowIndex){
	        	 return '<span title="'+value+'">'+value+'</span>';
	         }},
	         {field:'ipAddress',title:'IP地址',sortable:true, width:50},
	         {field:'content',title:'告警内容',width:200,formatter:function(value){
	        	 return "<span title='点我查看详情' class='oc-pointer-operate'>"+value+"</span>";
	         }},
	         {field:'userNames',title:'接收人',width:80,formatter:function(value,row,rowIndex){
	        	 if(row.configWarnRuleBos!=null && row.configWarnRuleBos.length>0){
	        		 var result="",warnUserName="";
	        		 for(var rule=0;rule<row.configWarnRuleBos.length;rule++){
	        			 result += row.configWarnRuleBos[rule].userName+",";
	        		 }
	        		 result = result.substring(0,result.length - 1);
	        		 warnUserName = result;
	        		 if(result.length > 6){
	        			 result = result.substring(0, 6) + "...";
	        		 }
	        		 return "<span title='"+warnUserName+"'>"+result+"</span>";
	        	 }
	         }},
	         {field:'warnTime',title:'告警时间',sortable:true,width:100,formatter:function(value){
	        	  var datetime = new Date();
	        	    datetime.setTime(value);
	        	    var year = datetime.getFullYear();
	        	    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	        	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	        	    var hour = datetime.getHours()< 10 ? "0" + datetime.getHours() : datetime.getHours();
	        	    var minute = datetime.getMinutes()< 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
	        	    var second = datetime.getSeconds()< 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
     			if(value == undefined||value == ""|| value==null){
     				return "";
     			} else{
     				return     year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
     			}
//    			var unixTimestamp = new Date(value),hours = unixTimestamp.getHours();
//    			if(0==hours)
//    	 			return unixTimestamp.toLocaleString().replace('12:',"0:");
//    	 		else
//    	     	 	return unixTimestamp.toLocaleString();
	         }},
	         {field:'sendStatus',title:'发送记录',width:50,formatter:function(value,row,rowIndex){
	        	 var result = $("<span class='ico ico-mark'></span>");
	        	 result.on('click',function(e){
	        		 var alarmId =row.id;
                     oc.resource.loadScript('resource/module/alarm-management/js/alarm-management-detail.js',function(){
                    	 oc.alarm.detail.dialog.open(alarmId,null,false);
 					 });
	        	 });
	        	 return result;
	         }}
         ]],
         onClickCell:function(rowIndex, field, value){
        	 if("content" == field){
        		 var rows = $(this).datagrid('getRows'),row;
        		 for(var i=0;i<rows.length;i++){
        			 if(i==rowIndex){
        				 row = rows[i];
        				 break;
        			 }
        		 }
        		 if(row.lastFileId!=null && row.currFileId!=null){
                     oc.resource.loadScript('resource/module/config-file/device/js/device.js',function(){
                    	 oc.module.config.file.device.openCompareWin('compare',{fileId:row.lastFileId,
                    		 fileName:row.lastFileName},{fileId:row.currFileId,fileName:row.currFileName},row.ipAddress);
 					 });
        		 }
        	 }
         }
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_view');
				$.cookie('pageSize_view',pageSize);
			}
		});
	}
	
	function checkTime(){
		var timeRadio = ($('input[name=timeRadio]').eq(0).is(':checked')?0
				:($('input[name=timeRadio]').eq(1).is(':checked')?1:null));
		if(1==timeRadio){
			var startTime = new Date(form.find("#startTime").datetimebox('getValue')).getTime(),
				endTime = new Date(form.find("#endTime").datetimebox('getValue')).getTime();
			if(startTime>=endTime){
				alert("开始时间不能大于等于结束时间");
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
})(jQuery);