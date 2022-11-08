$(function () {
	var id=oc.util.generateId();

	//策略ID,基线ID,指标集合
	var _profileId,_resourceId,_timelineId,_metrics,_creatorId;
	//From 和指标的表格
	var fromDiv,datagrid, datagridDiv,status=0;
	
	//基线数组
	var weeklyArray=new Array();
	var dateArray=new Array();
	
	var timelineWeekDiv,timelineDailyDiv;
	
	var _currentTimeline;
	
	
	//时间区间校验
	var timeLineArray=new Array();
	
	//初始化基线页面
	function initFrom(profileId,timelineDialog,data){
		_profileId=profileId;
		fromDiv=timelineDialog.find('#timelineDialogForm');
		//循环加载基线列表
		timeLineArray=data.data;
		//加载基线列表
		timelineWeekDiv=timelineDialog.find('#timeline_list_week');
		timelineDailyDiv=timelineDialog.find('#timeline_list_daily');
		
		//指标的表格
		datagridDiv=timelineDialog.find('#timeline_datagrid');
		initDatagrid(datagridDiv);
		oc.util.ajax({
			  url: oc.resource.getUrl('portal/resource/timeline/getMetrics.htm'),
			  data:{profileId:profileId},
			  success:function(data){
				  _metrics=data;
			  }
		});
		
		for(var i=0,len=timeLineArray.length;i<len;i++){
			var timeLine = timeLineArray[i];
			var timeLineId=timeLine.timeLineId;
			var name=timeLine.name;
			var timeLineType=timeLine.timeLineType;
			var startTime=timeLine.startTime;
			var endTime=timeLine.endTime;
			
			var day = new Date(startTime);
			var week=day.getDay();
			var tool;
			//添加常规基线
			if(timeLineType=="WEEKLY"){				
				tool= initTimelineToolBar(timeLine);
				weeklyArray.push(tool)
				timelineWeekDiv.append(tool);
				tool.find('#showName').focus();
			}
			//添加指定
			if(timeLineType=="DATE"){
				tool= initTimelineToolBar(timeLine);
				dateArray.push(tool);
				timelineDailyDiv.append(tool);
			}
			timelineDailyDiv.find('#showName').focus();
		}

		//绑定添加基线按钮
		fromDiv.find('#addWeekTimeline').linkbutton({
			onClick:function(){
				if(oc.index.getUser().id==_creatorId){
					var isContinue = true;
		    		fromDiv.find(".editDiv").each(function(i,div){
		    			if($(this).css("display")!="none"){
		    				isContinue = false;
		    				return false;
		    			}
		    		});
		    		if(!isContinue){
		    			alert('还有基线没有保存,请保存后继续操作!');
		    			return;
		    		}
					_currentTimeline = {
							profileId:_profileId,
							timeLineId:-1,
							timeLineType:'WEEKLY',
							name:'基线名称',
							startTime:new Date(),
							endTime:new Date(),
							metricsString:null,
							baseMetrics:_metrics
					}
					var tool= initTimelineToolBar(_currentTimeline);
					weeklyArray.push(tool);
					timelineWeekDiv.append(tool);
					//timeline_list_div
					_currentTimeline.startTime = _currentTimeline.startTime.stringify('yyyy-mm-dd hh:MM:ss');
					_currentTimeline.endTime = _currentTimeline.endTime.stringify('yyyy-mm-dd hh:MM:ss');
					saveTimeline(tool,_currentTimeline);
					var count = timelineWeekDiv.children("div").length;
					fromDiv.find("#timeline_list_div").scrollTop(timelineWeekDiv.innerHeight()/count*(count-4));
				}else{
					alert("你没有权限添加基线，请联系策略的创建者");
				}

			}

		});
		
		fromDiv.find('#addDailyTimeline').linkbutton({
			onClick:function(){
				if(oc.index.getUser().id==_creatorId){
					var isContinue = true;
		    		fromDiv.find(".editDiv").each(function(i,div){
		    			if($(this).css("display")!="none"){
		    				isContinue = false;
		    				return false;
		    			}
		    		});
		    		if(!isContinue){
		    			alert('还有基线没有保存,请保存后继续操作!');
		    			return;
		    		}
					_currentTimeline = {
							profileId:_profileId,
							timeLineId:-1,
							timeLineType:'DATE',
							name:'基线名称',
							startTime:new Date(),
							endTime:new Date(),
							metricsString:null,
							baseMetrics:_metrics
					}
					var tool= initTimelineToolBar(_currentTimeline);
					//fromDiv.find('#timeline_datagrid').datagrid('loadData',{"data":{"total":0,"rows":rows},"code":200});
					dateArray.push(tool);
					timelineDailyDiv.append(tool);
					_currentTimeline.startTime = _currentTimeline.startTime.stringify('yyyy-mm-dd hh:MM:ss');
					_currentTimeline.endTime = _currentTimeline.endTime.stringify('yyyy-mm-dd hh:MM:ss');
					saveTimeline(tool,_currentTimeline);
					fromDiv.find("#timeline_list_div").scrollTop(timelineWeekDiv.innerHeight()+timelineDailyDiv.innerHeight());
                    // console.log("daily--"+timelineDailyDiv.innerHeight());
				}else{
					alert("你没有权限添加基线，请联系策略的创建者");
				}

			}
		});
		
		showFirstTimeLine();
		
	}
	
	//保存-----基线
	function saveTimeline(formGroupObj,timeLine){		
		//当前 timeline
		timeLine.baseMetrics  = $.extend(true,[],_metrics.data.metrics);
		//添加基线
		oc.util.ajax({
			url: oc.resource.getUrl('portal/resource/timeline/addTimeline.htm'),
			data:timeLine,
			successMsg:null,
			success:function(data){
				timeLineArray.push(data.data);
				//更新基线
				_timelineId = data.data.timeLineId;
				fromDiv.find('#timeline_datagrid').datagrid('loadData',{"data":{"total":0,"rows":data.data.baseMetrics},"code":200});
				formGroupObj.attr('id','timeline-'+data.data.timeLineId);
				formGroupObj.find("#-1").attr('id',data.data.timeLineId);
				fromDiv.find('.form-group').removeClass("active");
				formGroupObj.addClass('active');
				alert('基线添加成功！');
			}
		
		});

	}
	
	//保存-----基线
	function updateTimeline(formGroupObj,timeLine){		
		//当前 timeline
		timeLine.baseMetrics  = $.extend(true,[],_metrics.data.metrics);
		//获取表格对象的值
		oc.timeline_datagrid_form=oc.ui.form({
			selector:fromDiv.find("#timeline_datagrid_form")
		});

		var datagridFormVal =oc.timeline_datagrid_form.val();
		timeLine.metricsString=datagridFormVal;
		
		var timelineVo = {
				profileId:timeLine.profileId,
				timeLineId:_timelineId,
				name:timeLine.name,
				timeLineType:timeLine.timeLineType,
				startTime:timeLine.startTime,
				endTime:timeLine.endTime,
				metricsString:datagridFormVal,
				baseMetrics:timeLine.baseMetrics
		}
		
		oc.util.ajax({
			url: oc.resource.getUrl('portal/resource/timeline/updateTimeline.htm'),
			data:timelineVo,
			successMsg:null,
			success:function(data){
				alert('基线更新成功！');
				//更新基线
				formGroupObj.find('input[type=hidden][name=timeLine]').attr('value',JSON.stringify(data.data));
			}
		
		});

	}
	
	//删除基线
	function delTimeline(timelineId){
		if(timelineId!=-1){
			  oc.util.ajax({
				  url: oc.resource.getUrl('portal/resource/timeline/delTimeline.htm'),
				  data:{timelineId:timelineId},
				  successMsg:null,
				  success:function(data){
					var tempArray = new Array();
					for(var i=0,len=timeLineArray.length;i<len;i++){
						if(timeLineArray[i].timeLineId != timelineId){
							tempArray.push(timeLineArray[i]);
						}
					}
					timeLineArray = $.extend(true,[],tempArray);
					alert('基线删除成功！');
				  }
			  });
		}
	}
	
	//显示基线的指标信息
	function showTimeline(timeLine){
		var rows = null;
		_timelineId = timeLine.timeLineId;
		if(timeLine.baseMetrics && timeLine.baseMetrics.data){
			rows = timeLine.baseMetrics.data.rows;
		}else{
			rows = timeLine.profileMetricPageVo.rows;
		}
		fromDiv.find('#timeline_datagrid').datagrid('loadData',{"data":{"total":0,"rows":rows},"code":200}); 
	}
	
	//初始化timeline tool bar
	function initTimelineToolBar(timeLine){
		var profileId=timeLine.profileId;
		var timeLineId=timeLine.timeLineId;
		var name=timeLine.name;
		var timeLineType=timeLine.timeLineType;
		var startTime=new Date(timeLine.startTime);
		var endTime=new Date(timeLine.endTime);
		
		var day = new Date(startTime);
		var week=day.getDay();
		
		var formGroup;
		var formGroupObj;
		if(timeLineType=="WEEKLY"){
			formGroup="<div id='timeline-"+timeLineId+"' class='form-group add-linebg'>" +
					"<div id='showName' class='infoDiv' style='width:200px'>"+name+"</div>" +
					"<div id='showWeek' class='infoDiv' style='width:100px'>"+getWeekValue(week)+"</div>" +
					"<div id='showTime' class='infoDiv' style='width:300px'>"+startTime.stringify('hh:MM:ss')+"&nbsp;&nbsp;至&nbsp;&nbsp;"+endTime.stringify('hh:MM:ss')+"</div>" +
					"<div class='editDiv'><input id='name' name='name' type='text' value='"+name+"' style='vertical-align:middle;margin-right:5px;'/>"+
					"<select id='week' name='week' class='week'></select>"+
					"<input name='startTime' id='startTime' class='timeSpinner' value='"+startTime.stringify('hh:MM:ss')+"' style='width:100px'></input>&nbsp;&nbsp;至&nbsp;&nbsp;" +
					"<input name='endTime' id='endTime' class='timeSpinner' value='"+endTime.stringify('hh:MM:ss')+"' style='width:100px'></input></div>"+
					"<a name='saveTimeline' title='保存' class='r-h-ico r-h-ok' id='"+timeLineId+"'></a>"+
					"<a name='editTimeline' title='编辑' class='r-h-ico r-h-edit' id='"+timeLineId+"'></a>"+
					"<a name='deleteTimeline' title='删除' class='r-h-ico r-h-delete' id='"+timeLineId+"'></a>" +
					"<input type='hidden' name='timeLine' value='"+ JSON.stringify(timeLine) +"' /> " +
					"</div>";
			formGroupObj=$(formGroup);
			 
			formGroupObj.find('.timeSpinner').timespinner({    
			    showSeconds: true
			});
			
			oc.ui.combobox({
				selector:formGroupObj.find('.week'),
				fit:false,
				width:60,
				placeholder:null,
				value:week,
				data:[
				      {id:'1',name:'周一'},
				      {id:'2',name:'周二'},
				      {id:'3',name:'周三'},
				      {id:'4',name:'周四'},
				      {id:'5',name:'周五'},
				      {id:'6',name:'周六'},
				      {id:'0',name:'周日'}
		        ]
			});
			formGroupObj.find('.week').combobox('setValue', week);
		}else{
			formGroup="<div id='timeline-"+timeLineId+"' class='form-group add-linebg'>"+
			"<div id='showName' class='infoDiv' style='width:200px'>"+name+"</div>" +
			"<div id='showTime' class='infoDiv' style='width:400px'>"+startTime.stringify('yyyy-mm-dd hh:MM:ss')+"&nbsp;&nbsp;至&nbsp;&nbsp;"+endTime.stringify('yyyy-mm-dd hh:MM:ss')+"</div>" +
			"<div class='editDiv'>"+
			"<input id='name' name='name' type='text' value='"+name+"' style='vertical-align:middle;margin-right:5px;'/>"+
			"<select name='DateTimeBox' id='startTime' style='width:150px'></select>&nbsp;&nbsp;至&nbsp;&nbsp;"+
			"<select name='DateTimeBox' id='endTime' style='width:150px'></select>"+
			"</div>"+
			"<a name='saveTimeline' title='保存' class='r-h-ico r-h-ok' id='"+timeLineId+"'></a>"+
			"<a name='editTimeline' title='编辑' class='r-h-ico r-h-edit' id='"+timeLineId+"'></a>"+
			"<a name='deleteTimeline' title='删除' class='r-h-ico r-h-delete' id='"+timeLineId+"'></a>" +
			"<input type='hidden' name='timeLine' value='"+ JSON.stringify(timeLine) +"' /> " +
			"</div>";
			formGroupObj=$(formGroup);
			
			formGroupObj.find('#startTime').datetimebox({
				value:startTime.stringify('yyyy-mm-dd hh:MM:ss'),
			    showSeconds: true
			});
			formGroupObj.find('#endTime').datetimebox({
				value:endTime.stringify('yyyy-mm-dd hh:MM:ss'),
			    showSeconds: true
			});
		}
		//显示基线的指标列表
		formGroupObj.bind('click', function(){
            // console.log('formGroupObj click');
			
			var isContinue = true;
    		fromDiv.find(".editDiv").each(function(i,div){
    			if($(this).css("display")!="none"){
    				isContinue = false;
    				return false;
    			}
    		});
    		if(!isContinue){
    			alert('还有基线没有保存,请保存后继续操作!');
    			return;
    		}
			
			fromDiv.find('.form-group').removeClass("active");
			formGroupObj.addClass('active');
			
	    	var timelineStr=formGroupObj.find('input[type=hidden][name=timeLine]').val();
	    	var timeline = JSON.parse(timelineStr);
			showTimeline(timeline); 
		});
		
		formGroupObj.find('.editDiv').bind('click',function(e){
			e.stopPropagation();
			return ;
		});
		
		//保存按钮
		formGroupObj.find('[name=saveTimeline]').linkbutton({
			onClick:function(e){
				e.stopPropagation();
				name=formGroupObj.find('#name').val();

				formGroupObj.find('#showName').text(name);

				timeLine.name=name;
				if(timeLineType=="WEEKLY"){		
					formGroupObj.find('#showWeek').text(formGroupObj.find('.week').combobox('getText'));
					formGroupObj.find('#showTime').text(formGroupObj.find('#startTime').val()+"  至   "+formGroupObj.find('#endTime').val());
					
					var week =formGroupObj.find('#week').combobox('getValue');
					var time=getWeekDate(week);
					//起始时间
					var startTime=new Date();
					var startHours =formGroupObj.find('#startTime').timespinner('getHours');
					var startMinutes =formGroupObj.find('#startTime').timespinner('getMinutes');
					var startSeconds =formGroupObj.find('#startTime').timespinner('getSeconds');
					//结束时间
					var endTime=new Date();
					var endtHours =formGroupObj.find('#endTime').timespinner('getHours');
					var endMinutes =formGroupObj.find('#endTime').timespinner('getMinutes');
					var endSeconds =formGroupObj.find('#endTime').timespinner('getSeconds');
					
					time.setHours(startHours, startMinutes, startSeconds, '0');
					startTime.setTime(time.getTime());
					time.setHours(endtHours, endMinutes, endSeconds, '0');
					endTime.setTime(time.getTime());
					
					timeLine.startTime=startTime.stringify('yyyy-mm-dd hh:MM:ss');
					timeLine.endTime=endTime.stringify('yyyy-mm-dd hh:MM:ss');
					
				}else{
					timeLine.startTime=formGroupObj.find('#startTime').datebox('getValue');
					timeLine.endTime=formGroupObj.find('#endTime').datebox('getValue');
					
					
					formGroupObj.find('#showTime').text(timeLine.startTime+'  至  '+timeLine.endTime);
				}

				if(timeLine.startTime>timeLine.endTime){
					alert("起始时间不能大于结束时间！");
					return;
				}
				
				if(!vidationDateTime(timeLine)){
					alert("同类型基线的时间不能交叉！");
					return;
				}
				
				updateTimeline(formGroupObj,timeLine);
				
				$(this).css('display','none');
				formGroupObj.find('[name=editTimeline]').css('display','inline-block');
				formGroupObj.find('.infoDiv').show();
				formGroupObj.find('#showTime').css('display','inline-block');
				formGroupObj.find('#showName').css('display','inline-block');
				formGroupObj.find('#showWeek').css('display','inline-block');
				formGroupObj.find('.editDiv').hide();
			}
		});
		//编辑按钮
		formGroupObj.find('[name=editTimeline]').linkbutton({
			onClick:function(e){
				e.stopPropagation();
				if(oc.index.getUser().id==_creatorId){
					
					var isContinue = true;
		    		fromDiv.find(".editDiv").each(function(i,div){
		    			if($(this).css("display")!="none"){
		    				isContinue = false;
		    				return false;
		    			}
		    		});
		    		if(!isContinue){
		    			alert('还有基线没有保存,请保存后继续操作!');
		    			return;
		    		}
					
					$(this).css('display','none');
					formGroupObj.find('[name=saveTimeline]').css('display','inline-block');
					formGroupObj.find('.infoDiv').hide();
					formGroupObj.find('.editDiv').show();
					
					fromDiv.find('.form-group').removeClass("active");
					formGroupObj.addClass('active');
					
					var myFormGroup=$(this).parent();
					var id=myFormGroup.attr('id').split('-')[1];
					if(id != _timelineId){
						var timelineStr=formGroupObj.find('input[type=hidden][name=timeLine]').val();
						var timeline = JSON.parse(timelineStr);
						showTimeline(timeline); 
					}
				}else{
					alert("你没有修改当前基线的权限，请联策略的系创建者！")
				}

			}
		});
		//删除按钮
		formGroupObj.find('[name=deleteTimeline]').linkbutton({
			onClick:function(e){
				e.stopPropagation();
				if(oc.index.getUser().id==_creatorId){
					var myFormGroup=$(this).parent();
					var id=myFormGroup.attr('id').split('-')[1];
					if(id!=-1){
				    	oc.ui.confirm('确认删除当前基线?',function(){
							delTimeline(id);
							myFormGroup.remove();
							if(id == _timelineId){
								showFirstTimeLine();
							}
				    	},function(){
				    		//
				    	});
					}else{
						myFormGroup.remove();
					}
				}else{
					alert("你没有删除当前基线的权限，请联策略的系创建者！")
				}

			}
		});
		
		if(timeLineId==-1){
			formGroupObj.find('[name=editTimeline]').hide();
			formGroupObj.find('.infoDiv').hide();
		}else{
			formGroupObj.find('[name=saveTimeline]').hide();
			formGroupObj.find('.editDiv').hide();
		}
		
		return formGroupObj;
	}
	
	function showFirstTimeLine(){
		
		var isShowNullGrid = true;
		$('#timeline_list_div').find('.form-group').each(function(){
			var timelineid = parseInt($(this).attr('id').replace('timeline-',''));
			if(timelineid > 0){
				$(this).click();
				isShowNullGrid = false;
				return false;
			}
		});
		
		if(isShowNullGrid){
			fromDiv.find('#timeline_datagrid').datagrid('loadData',{"data":{"total":0,"rows":[]},"code":200});
		}
	}
	
	//获取周
	function getWeekValue(week){
		var weekValue;
		switch(week)
		{
		case 1:
			weekValue= '周一'
		  break;
		case 2:
			weekValue= '周二'
		  break;
		case 3:
			weekValue= '周三'
		  break;
		case 4:
			weekValue= '周四'
		  break;
		case 5:
			weekValue= '周五'
		  break;
		case 6:
			weekValue= '周六'
		  break;
		case 0:
			weekValue= '周日'
		  break;
		}
		return weekValue;
	}
	
	//获取周的日期
	function getWeekDate(week){
		var now = new Date(); //当前日期 
		var nowDayOfWeek = now.getDay(); //今天本周的第几天 
		var nowDay = now.getDate(); //当前日 
		var nowMonth = now.getMonth(); //当前月 
		var nowYear = now.getFullYear(); //当前年 
		
		var weekEndDate = new Date(nowYear, nowMonth, nowDay + (week - nowDayOfWeek));

		return weekEndDate;
		
	}
	
	function validateTimeScope(timeline){
		
		var type=timeline.timeLineType;
		var start=new Date(timeline.startTime);
		var end=new Date(timeline.endTime);
		
		var week=start.getDay();
		
		var startTimes ;
		var endTimes ;

		var startTimes=fromDiv.find('#timeline_list_week').find("#startTime");
		
		$.each(startTimes, function(i, n){
			 var time=$(n).val();
		});
		
		var endTimes=fromDiv.find('#timeline_list_week').find("#endTime");

		if(type=='WEEKLY'){
			var weeks=timelineWeekDiv;

		}else{

		}

		return false;
	}
	
	//加载表格
	function initDatagrid(datagridDiv){
		datagrid = oc.ui.datagrid({
			selector:datagridDiv,	
			pagination:false,
		    checkOnSelect: false, 
		    selectOnCheck: false,
		    columns:[[
			 {field:'monitor',title:'<input type="checkbox" id="monitorAll" value=2 />监控',align:'left',width:'60px',				 
				 formatter: function(value,row,index){
					 var checkbox;
					 if(value){
						 checkbox= $("<input class='monitor' checked name='monitor_"+row.metricId+"' type='checkbox' value='"+row.monitor+"' />");
					 }else{
						 checkbox= $("<input class='monitor' name='monitor_"+row.metricId+"' type='checkbox' value='"+row.monitor+"' />");
					 }
					 
					 if(row.metricTypeEnum == 'AvailabilityMetric'){
						 checkbox= "<input class='monitor'  name='monitor_"+row.metricId+"' disabled='true' checked type='checkbox' disabled='true' value='"+row.monitor+"' />";
					 }
					 return checkbox;
				 }
			 },
	         {field:'alarm',title:'<input type="checkbox" id="alarmAll" name="alarmAll" />告警',align:'left',width:'60px',
				 formatter: function(value,row,index){
					 var checkbox;
					 if(value){
						 checkbox= "<input class='alarm' checked name='alarm_"+row.metricId+"' type='checkbox' value='"+row.alarm+"' />";
					 }else{
						 checkbox= "<input class='alarm'  name='alarm_"+row.metricId+"' type='checkbox' value='"+row.alarm+"' />";
					 }
					 if(row.metricTypeEnum == 'AvailabilityMetric'){
						 checkbox= "<input class='alarm'  name='alarm_"+row.metricId+"' checked type='checkbox' disabled='true' value='"+row.alarm+"' />";
					 }
					 return checkbox;
				 }
			 },
	         {field:'metricName',title:'指标',align:'left',width:'250px',ellipsis:true},
	         {field:'metricTypeEnum',title:'指标类型',align:'left',width:'100px',ellipsis:true,
				 formatter:function(value,row,index){
					 var metricType = '<span class="timeline_metricType">性能指标</span>';
					 	if(row.metricTypeEnum == 'AvailabilityMetric'){
							metricType = '<span class="timeline_metricType">信息指标</span>';
						}else if(row.metricTypeEnum == 'InformationMetric'){
							metricType = '<span class="timeline_metricType">信息指标</span>';
						}
					 return  metricType;
				 }
	         },
	         {field:'dictFrequencyId',title:'监控频度',align:'center',width:'130px',
	        	 formatter: function(value,row,index,div){
	        		 $("<select name='dictFrequencyId_"+row.metricId+"'/>").appendTo(div).combobox({
	        			 fit:false,
		    				width:70,
		    				placeholder:null,
		    				panelHeight : 'auto',
		    			    valueField:'id',    
		    			    textField:'name' ,
		    				value:value,
		    				data:row.supportFrequentList
	        		 });
	        	 }

	         },
	         {field:'alarmFlapping',title:'告警Flapping',align:'center',width:'130px',
	        	 formatter: function(value,row,index,div){
	        		 if(value==0 || value==null){
	        			 value=1;
	        		 }
	        		 //disabled='true'
	        		 
	        		 var isAllowUpdateFlapping = false;
	        		 if(row.metricTypeEnum == 'InformationMetric'){
	        			 isAllowUpdateFlapping = true;
	        		 }
	        		 $("<select name='alarmFlapping_"+row.metricId+"'/>").appendTo(div).combobox({
	        			 fit:false,
	        			 width:70,
	        			 placeholder:null,
	        			 panelHeight : 'auto',
	        			 valueField:'id',    
	        			 textField:'name' , 
	        			 value:value,
	        			 readonly:isAllowUpdateFlapping,
	        			 data:[
	        			       {id:'1',name:'1'},
	        			       {id:'2',name:'2'},
	        			       {id:'3',name:'3'},
	        			       {id:'4',name:'4'},
	        			       {id:'5',name:'5'}
	        			       ]
	        		 });
	        	 }
	         },
	         {field:'metricThresholds',title:'阈值定义',align:'center',width:'163px',
	        	 formatter: function(value,row,index,div){
	        		 if(row.metricTypeEnum != 'AvailabilityMetric'){
	        			 div.append('<a metricname="' + row.metricName + '" metricid="' + row.metricId + '" class="light_blue icon-threshold timeLineupdateThresholdClass" title="编辑阈值"></a>');
	        		 }
	        	 }
	         }
 		     ] ], 
			onLoadSuccess : function(data) {
				fromDiv.find("#monitorAll").unbind();
				fromDiv.find("#alarmAll").unbind();
				
				//监控
				fromDiv.find(".monitor").unbind().bind("click",function(){
					var thisAlarm=$(this).parents('tr').find('.alarm');
					var metricType = $(this).parents('tr').find('.timeline_metricType').html();
					if (this.checked == true) {
						$(this).attr('value',true);
						if(metricType!='信息指标'){
							thisAlarm.removeAttr("disabled"); 
							thisAlarm.removeAttr("checked"); 
						}
						thisAlarm.attr('value',false);
					}else{
						$(this).attr('value',false);
						thisAlarm.attr('disabled','true');
						thisAlarm.removeAttr("checked"); 

					}
				});
				
				//告警
				fromDiv.find(".alarm").unbind().bind("click",function(){
					if (this.checked == true) {
						$(this).attr('value',true);
					}else{
						$(this).attr('value',false);
					}
				});
				
				$('.timeLineupdateThresholdClass').click(function(){
					var that = this;
					oc.resource.loadScript('resource/module/resource-management/js/strategy_threshold_setting.js',function(){
						oc.module.resourcemanagement.strategythresholdsetting.open({metricName:$(that).attr('metricname'),profileId:_profileId,
							metricId:$(that).attr('metricid'),timelineId:_timelineId});
					});
				});
				
				//监控所有
				fromDiv.find("#monitorAll").bind("click",
						function() {
							if (this.checked == false) {
								fromDiv.find('#alarmAll').attr('disabled','true');
								fromDiv.find('#alarmAll').prop('checked',false);
								fromDiv.find('[class=monitor]:checkbox').each(function(i){
									if(!$(this).attr('disabled')){
										$(this).prop('checked',false);
										$(this).attr('value',false);
										var thisAlarm = $(this).parents('tr').find('.alarm');
										thisAlarm.attr('disabled','true');
										thisAlarm.prop('checked',false);
										thisAlarm.attr('value',false);
									}
								});
							} else {
								fromDiv.find('#alarmAll').removeAttr('disabled');
								fromDiv.find('[class=monitor]:checkbox').each(function(i){
									if(!$(this).attr('disabled')){
										$(this).prop('checked',true);
										$(this).attr('value',true);
										var thisAlarm = $(this).parents('tr').find('.alarm');
										var metricType = $(this).parents('tr').find('.timeline_metricType').html();
										if(metricType != '信息指标'){
											thisAlarm.removeAttr('disabled');
											thisAlarm.attr('value',true);
										}
									}
								});
						}
				});
				
				//告警所有
				fromDiv.find("#alarmAll").bind("click", function () {
	            	if(this.checked==false){
	            		fromDiv.find('[class=alarm]:checkbox').each(function(i){
	            			if(!$(this).attr('disabled')){
	            				$(this).prop('checked',false);
	            				$(this).attr('value',false);
	            			}
	            		});
	            	}else{
	            		fromDiv.find('[class=alarm]:checkbox').each(function(i){
	            			if(!$(this).attr('disabled')){
	            				$(this).prop('checked',true);
	            				$(this).attr('value',true);
	            			}
	            		});
	            	}
	            });
		    	 
		     },
		     onLoadError:function (){
		    	 log('onLoadError...');
		     }
		});
		
		oc.timeline.datagrid=datagrid;
		
//		return datagrid;
	}
	
	/**
	 * 打开基线的dialog
	 */
	function open(profileId){
		 var timelineDialogDiv=$('<div/>');
		 var timelineDialog=timelineDialogDiv.dialog({
		    title: '基线设置',
		    flt:false,
		    width: 910, 
		    height: 612,
		    buttons:[{
		    	text: '确定',
		    	iconCls:"fa fa-check-circle",
		    	handler:function(){
//		    		var unSaveSize=fromDiv.find("#timeline_list_div #timeline--1").size();
		    		
		    		var unSaveTimeline=false;
		    		fromDiv.find(".editDiv").each(function(i,div){
		    			if($(this).css("display")!="none"){
		    				unSaveTimeline=true;
		    			}
		    		});
		    		
		    		
		    		if(unSaveTimeline){
				    	oc.ui.confirm('还有基线没有保存，是否关闭窗口？',function(){
				    		timelineDialogDiv.dialog('close');
				    	},function(){
				    		//
				    	});
		    		}else{
		    			timelineDialogDiv.dialog('close');
		    		}
		    		
		    		var newCount=timeLineArray.length;
		    		oc.customprofile.strategy.detail.updateTimeLineCount(newCount);
		    	}
		    },{
		    	text: '取消',
				iconCls: 'fa fa-times-circle',
		    	handler:function(){

		    		timelineDialogDiv.dialog('close');
		    		var newCount=timeLineArray.length;
		    		oc.customprofile.strategy.detail.updateTimeLineCount(newCount);
		    	}
		    }],
		    href: oc.resource.getUrl('resource/module/resource-management/timeline/timeline.html'),
		    onLoad:function(){
				  oc.util.ajax({
					  url: oc.resource.getUrl('portal/resource/timeline/getTimelines.htm'),
					  data:{profileId:profileId,resourceId:_resourceId},
					  success:function(data){		
						  timelineDialog.find('#timeline_datagrid_form').height('340px');
						  initFrom(profileId,timelineDialog,data);
					  }
					  
				  });
		    },
		    onClose:function(){
		    	timelineDialog.panel('destroy');
		    	var newCount=timeLineArray.length;
	    		oc.customprofile.strategy.detail.updateTimeLineCount(newCount);
		    }
		});
		return timelineDialog;
	}
	
	/**
	 * 在panel中展示基线
	 */
	function show(panel,profileId){
		panel.css('width','913px');
		timelinePanel = panel.panel({
		    href: oc.resource.getUrl('resource/module/resource-management/timeline/timeline.html'),
		    onLoad:function(){
				  $('#profile_timeline_center_content').removeClass('panel-body-noheader');
		    	  timelinePanel.removeClass('panel-body-noheader');
				  oc.util.ajax({
					  url: oc.resource.getUrl('portal/resource/timeline/getTimelines.htm'),
					  data:{profileId:profileId,resourceId:_resourceId},
					  success:function(data){		
						  timelinePanel.find('#timeline_datagrid_form').height('284px');
						  initFrom(profileId,timelinePanel,data);
					  }
					  
				  });
		    }
		});
	}
	
	//验证基线的时间区间
	function vidationDateTime(timeLine){
		var currentId=timeLine.timeLineId;
		var currentType=timeLine.timeLineType;
		var currentStart=new Date(timeLine.startTime);
		var currentEnd=new Date(timeLine.endTime);
		
		for(var i=0,len=timeLineArray.length;i<len;i++){
			var oldTimeLine=timeLineArray[i];
			var oldTimeLineId=oldTimeLine.timeLineId;
			var oldType=oldTimeLine.timeLineType;
			var oldStart=new Date(oldTimeLine.startTime);
			var oldEnd=new Date(oldTimeLine.endTime);
			
			//相同类型
			if(currentId!=oldTimeLineId && currentType==oldType){
				//同一周
				if(currentType=="WEEKLY" && currentStart.getDay()==oldStart.getDay()){
					
					 var currentStart_second= currentStart.getHours()*60*60+ currentStart.getMinutes()*60+ currentStart.getSeconds();
					 var currentEnd_second= currentEnd.getHours()*60*60+ currentEnd.getMinutes()*60+ currentEnd.getSeconds();
					 var oldStart_second= oldStart.getHours()*60*60+ oldStart.getMinutes()*60+ oldStart.getSeconds();
					 var oldEnd_second= oldEnd.getHours()*60*60+ oldEnd.getMinutes()*60+ oldEnd.getSeconds();

					if((currentStart_second > oldStart_second && currentStart_second < oldEnd_second) 
							|| (currentEnd_second > oldStart_second && currentEnd_second < oldEnd_second)
							|| (currentStart_second < oldStart_second && currentEnd_second > oldEnd_second)){
						return false;
					}

					
				}else if(currentType=="DATE"){//日
					if((currentStart > oldStart && currentStart < oldEnd) 
							|| (currentEnd > oldStart && currentEnd < oldEnd)
							|| (currentStart < oldStart && currentEnd > oldStart)){
						return false;
					}	

				}
				
			}
			
			
		}

		return true;
	}
	
	 //命名空间
	oc.ns('oc.timeline');
	oc.timeline.dialog={
			open:function(profileId,resourceId,creatorId){
				_creatorId=creatorId;
				_resourceId=resourceId
				return open(profileId);
			},
			show:function(panel,profileId,resourceId,creatorId){
				_creatorId=creatorId;
				_resourceId=resourceId;
				show(panel,profileId);
			}
	};
	
});
