(function($) {
	var alarmtabValue=null;
	var  domainIdbz=null;
	var groupIdbz =null
	var alarmdlg =$('<div/>');
	function HomeAlarmChart(){
		this.selector = $("#oc-module-home-workbench-alarm").attr("id",oc.util.generateId());
		oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
//		this.init();
		this.initAlarmTab();

	
	}
	
	HomeAlarmChart.prototype={
		selector:undefined,
		constructor:HomeAlarmChart,
		alarmChart:undefined,
		resource:'All',
		groupId:0,
		groupName:'全部',
		loadData:undefined,
		setResource:function(value){
			this.resource = value;
			this.init();
		},
		saveSetting:function(groupId,groupName){
			this.groupId = groupId;
			this.groupName = groupName;
			var workbenchId = this.selector.parent("div").attr("data-workbench-id");
			var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
			this.selector.parent("div").attr("data-workbench-ext",this.groupId+","+this.groupName);
			oc.home.workbench.setExt(workbenchId,workbenchSort,this.groupId+","+this.groupName);
		},
		init:function(){
			var that = this,groupInfo = this.selector.parent("div").attr("data-workbench-ext");
			if(groupInfo!=undefined && groupInfo!=""){
				var group = groupInfo.split(",");
				that.groupId = group[0];
				that.groupName = group[1];
			}
			that.selector.find(".oc-highcharts-subtitle").html(that.groupName+'资源组的告警');
			var chartData = [{
				name : "致命",
				type:'critical',
				y : 0,
				color : "#C22727"
			}, {
				name : "严重",
				type:'serious',
				y : 0,
				color : "#FF7F02"
			}, {
				name : "警告",
				type:'warn',
				y : 0,
				color : "#F1C337"
			}
//			, {
//				name : "未知",
//				y : 0,
//				color : "gray"
//			},
//			{
//				name : "正常",
//				y : 0,
//				color : "green"
//			}
			];
			var domainId = oc.home.workbench.domainId;
			domainIdbz=domainId;
			groupIdbz=that.groupId;
			var divs=	this.selector.find(".alert-con div");
			
			divs.each(function(){
				$(this).click(function(){
					var id=$(this).context.id;
					var title=null;
					var type=null;
					if(id=='deadly'){//致命
						title="致命告警列表";
						type="down";
					}else if(id=='serious'){//严重
						title="严重告警列表";
						type="metric_error";
					}else{//警告
						title="警告告警列表";
						type="metric_warn";
					}
					if($("#alarm_management_main_form1").length>0){
						$("#alarm_management_main_form1").each(function(){
							$(this).remove();
						});
					}
					alarmdlg.dialog({
							href : oc.resource.getUrl('resource/module/home/workbench/alarm-list.html'),
							title : title,
							width:800,
							height : 500,
							resizable : false,
							cache : false, 
							onLoad:function(){
								initAlarm(alarmtabValue,type,domainIdbz,groupIdbz);
								
							},
							onClose:function(){
								alarmChar.selector.find("#refrashAlarm").trigger('click');
								alarmdlg.dialog('destory');
								
							}
					 });
				});
			});
			oc.util.ajax({
				url:oc.resource.getUrl("system/home/getHomoAlarmData.htm"),
				data:{resource:that.resource,groupId:that.groupId,domainId:domainId==null?0:domainId},
				startProgress:false,
				stopProgress:false,
				success:function(d){
					if(d.code && d.code==200){
						var obj = d.data;
						if(obj!=null){
							var newChart = null;
							if(obj.total>0){
								var criticalPe = obj.total==0?0:(obj.critical/obj.total)*100;
								var seriousPe = obj.total==0?0:(obj.serious/obj.total)*100;
								var warnPe = obj.total==0?0:(obj.warn/obj.total)*100;
								if(criticalPe>0 && criticalPe<1){
									criticalPe=1;
								}else{
									criticalPe = Math.round(criticalPe);
								}
								if(seriousPe>0 && seriousPe<1){
									seriousPe=1;
								}else{
									seriousPe = Math.round(seriousPe);
								}
								if(warnPe>0 && warnPe<1){
									warnPe=1;
								}else{
									warnPe = Math.round(warnPe);
								}
								chartData[0].y = criticalPe;
								chartData[1].y = seriousPe;
								chartData[2].y = warnPe;
//								var newChart = chartData;
								var newChart = chartData.sort(function(a,b){
									 return (a['y']<b['y'])?1:-1; 
								});
								//百分比省略小数点，导入百分比总数小于100%，将第一个数加上总数与100相差的数，构成100%
								var count = ((newChart[0].y)+(newChart[1].y)+(newChart[2].y));
								if(count<100 && count>0){
									newChart[0].y=newChart[0].y+(100-count);
								}else if(count>100){
									newChart[0].y=newChart[0].y-(count-100);
								}
							}
							that.loadData = {data:newChart,label:[obj.critical,obj.serious,obj.warn],title:obj.total};
							that.initChart(that.loadData);
						}
						
					}
				}
				
			});
			
		},
		initChart:function(data){
			var titleY = parseInt((this.selector.height()*0.75)/2),that = this;
			that.selector.find("#alarmPic").highcharts({
				chart : {
					type : 'pie'
				},
				tooltip : {
					 formatter : function() {
							return "<b>"+this.point.name+":</b>"+this.y+"%";
					 }
				},
				title : {
					text : data.title+'个',
					floating : true,
					y : titleY
				},
				plotOptions: {
		            pie: {
		                borderWidth:0,
		                dataLabels:{
		                    connectorWidth:2//图形外连线宽度
		                },
		                states:{
		                    hover:{
		                        brightness:0.15,//鼠标移上去饼图透明度
		                        halo:{
		                            size:0,//鼠标移上去虚影大小
		                            opacity:0.1//鼠标移上去虚影透明度
		                        }
		                    }
		                }
//		                ,startAngle:0
//		                ,endAngle:360
//		                ,slicedOffset:10
		            }
		        },
				series : [ {
					name : '',
					size : '70%',
					data:data.data,
					innerSize : '50%',
					dataLabels : {
						formatter : function() {
							if(parseInt(data.title)>0){
								return '<span>' + this.y+ '%' + '</span>';
							}else{
								return null;
							}
							
						}
					}
				} ]
			});
			var labels = data.label;
			this.selector.find("#fatalLabel").text(labels[0]+"个");
			this.selector.find("#seriousLabel").text(labels[1]+"个");
			this.selector.find("#warningLabel").text(labels[2]+"个");
//			this.selector.find("#unkownLabel").text(labels[3]+"个");
//			this.selector.find("#normalLabel").text(labels[4]+"个");
		},
		initAlarmTab:function(){
			var that = this;
			oc.home.workbench.homeResourceTab({
				selector : that.selector.find("#alarmTab"),
				tabs : [ {
					text : "全部",
					onClick : function() {
						that.setResource("All");
						alarmtabValue=null;
					}
				}, {
					text : "主机",
					onClick : function() {
						that.setResource("Host");
						alarmtabValue="Host";
					}
				}, {
					text : "网络",
					onClick : function() {
						that.setResource("NetworkDevice");
						alarmtabValue="NetworkDevice";
					}
				}, {
					text : "应用",
					onClick : function() {
						that.setResource("App");
						alarmtabValue="App";
					}
				} ]
			});
			
		},
		reLoad:function(){
			this.init();
		},
		render:function(){
			if(this.loadData!=undefined){
				this.initChart(this.loadData);
			}else{
				this.init();
			}
		}
	};
	
	var alarmChar = new HomeAlarmChart();
	oc.home.workbench.alarm=function(){
		return alarmChar;
	};
	alarmChar.selector.find("#alarmSetting").click(function(){
		oc.home.workbench.resourcegroup.open({
			title:"告警统计",
			selectValue:alarmChar.groupId,
			fn:function(groupId,name){
				alarmChar.saveSetting(groupId,name);
				alarmChar.init();
			}
		});
	});
	
	alarmChar.selector.find("#refrashAlarm").click(function(){
		alarmChar.reLoad();
	});
	
/*	$("#deadly").click(function(){
		if(domainIdbz!=null || groupIdbz!=null){
		var reg = $('<div/>').addClass("alarmDiv");
		reg.dialog({
		href : oc.resource.getUrl('resource/module/home/workbench/alarm-list.html'),
		title : "致命告警列表",
		width:1000,
		height : 500,
		resizable : false,
		cache : false, 
		onLoad:function(){
	
				initAlarm(alarmtabValue,"down",domainIdbz,groupIdbz);
			
			
		}
 });
		}
	});
	$("#serious").click(function(){
		if(domainIdbz!=null || groupIdbz!=null){
			var reg2 = $('<div/>').addClass("alarmDiv");
			reg2.dialog({
			href : oc.resource.getUrl('resource/module/home/workbench/alarm-list.html'),
			title : "严重告警列表",
			width:1000,
			height : 500,
			resizable : false,
			cache : false, 
			onLoad:function(){
			
				initAlarm(alarmtabValue,"metric_error",domainIdbz,groupIdbz);
				
			}
			});
		}
	
	});
	$("#warning").click(function(){
		if(domainIdbz!=null || groupIdbz!=null){
		var reg3 = $('<div/>').addClass("alarmDiv");
		reg3.dialog({
		href : oc.resource.getUrl('resource/module/home/workbench/alarm-list.html'),
		title : "警告告警列表",
		width:1000,
		height : 500,
		resizable : false,
		cache : false, 
		onLoad:function(){
			initAlarm(alarmtabValue,"metric_warn",domainIdbz,groupIdbz);
		}
		});
		}
	});*/

	
	
//	$(".alert-con div").click(function(){
//		$(this).css("cursor","pointer");

//		
//		
//	});
	
	
	
})(jQuery);


function initAlarm(resource,type,domainIdbz,groupIdbz){
	function alarmManMainClass() {
	} 
	alarmManMainClass.prototype = {
		constructor : alarmManMainClass,
		cfg : undefined,
		mainDiv : undefined,
		tabs : undefined,
		queryForm : undefined,
		alarmDataGrid : undefined,
		flag : false,
		freshBtn : undefined,
		ipMenuButton : undefined,
		isSwitchTabs : false,
		onselects: false,
		open : function() {
			this.mainDiv = $('#alarm_management_main_id').attr('id', oc.util.generateId());
			var that = this;
			var tabDiv = this.mainDiv.find("#alarm_management_tabs");
			this.tabs = tabDiv.tabs({
				fit : false,
				width : "100%",
				onSelect : function(title,index) {
					var url = oc.resource.getUrl('alarm/alarmManagement/getNotRestoreMonitorAlarm.htm');
					if(that.onselects){
						var option = $('#alarm_datagrid1').datagrid("options");
						option.columns[0][9].hidden = true;
						option.columns[0][8].hidden = false;
						option.columns[0][7].hidden = false;
						option.columns[0][10].hidden = false;
						option.columns[0][11].hidden = false;
						option.columns[0][6].hidden = false;
						option.columns[0][2].width = '35%';
					}
					that.loadAlarmDataGrid(url);
				}
			});
			// 表格查询表单对象
			this.queryForm = oc.ui.form({
				selector : $('#alarm_management_main_form1'),
				combobox : [
						 {
							selector : '[name=queryTime]',
							fit : false,
							width:120,
							value : '4',
							data : [ {
								id : '4',
								name : '最近1天',
								selected : true
							}, {
								id : '1',
								name : '最近1小时'
							},{
								id : '2',
								name : '最近2小时'
							}, {
								id : '3',
								name : '最近4小时'
							} ],
							placeholder:null
						} ]/*,datetimebox : [ {
							selector:this.mainDiv.find('[name="startTime"]')
						}, {
							selector:this.mainDiv.find('[name="endTime"]')
						}]*/
				
			});
			
			var datagirdHeight = parseFloat(this.mainDiv.height()) - parseFloat(tabDiv.height()) + 'px';
		
			$('#alarm_management_main_form_instanceStatus').val(type);
			$('#prentCategorys').val(resource);
			$('#queryTime').val(4);
			function formatDate(date){
				return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
			}
			
			this.alarmDataGrid = oc.ui.datagrid({
				selector : this.mainDiv.find("#alarm_datagrid1"),
				queryForm : this.queryForm,
				url : oc.resource.getUrl('alarm/alarmManagement/getNotRestoreMonitorAlarm.htm?type='+type+"&groupId="+groupIdbz+"&domainId="+domainIdbz),
				fit : false,
				hideReset:true,
				hideSearch:true,
				width : "100%",
				height : "100%",
				columns : [[ {
					field : 'alarmId',
					align : 'left',
					title : '',
					hidden : true
				},{
					field : 'dataClass',
					align : 'left',
					title : '',
					hidden : true
				},{
					field : 'alarmContent',
					title : '告警内容',
					sortable : true,
					align : 'left',
					width : '30%',
					ellipsis:true,
			        formatter:function(value,row,rowIndex){
			        	 var formatterString = '';
							if(value == null){
								formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" > </label>';
							}else{
		                        if(value.length > 30){
		                        	formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars().substr(0, 30)+'...'+'</label>';
		                        }else{
		                        	formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars()+'</label>';
		                        }
						 	}
							return formatterString; 		
			         }
				}, {
					field : 'resourceName',
					title : '告警来源',
					align : 'left',
					sortable : true,
					width : '12%',
			        formatter:function(value,row,rowIndex){
			        	var formatterString = '';
			        	if(value != null){
			        		if(value.length > 15){
			        			formatterString = '<label style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars().substr(0, 15)+'...' +'</label>'
			        		}else{
			        			formatterString = '<label  style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars() +'</label>'
			        		}
			        		
			        	}
			        	return formatterString; 
			        }
				}, {
					field : 'ipAddress',
					title : 'IP地址<span id="ipSearchInputButton" class="datagrid-title-ico">&nbsp;</span>',
					ellipsis:true,
					width : '8%'
				}, {
					field : 'alarmType',
					title : '监控类型',
					align : 'left',
					sortable : true,
					ellipsis:true,
					width : '10%'
				}
				, {
					field : 'itmsData',
					title : '工单状态',
					align : 'left',
					sortable : true,
					width : '8%'
				}, {
					field : 'collectionTime',
					title : '产生时间',
					align : 'left',
		        	 formatter:function(value,row,index){
		        		 if(value != null){
				             return  formatDate(new Date(value));
		        		 }
		        		 return null;
		        	 },sortable:true,width:'16%'
				}, {
					field : 'acquisitionTime',
					align : 'left',
					title : '最近采集时间',
		        	 formatter:function(value,row,index){
		        		 if(value != null){
		        			 return  formatDate(new Date(value));
		        		 }
		        		 return null;
		            },width:'16%'
				}, {
					field : 'recoveryTime',
					align : 'left',
					title : '告警恢复时间',
					hidden:  true,
		        	 formatter:function(value,row,index){
		        		 if(value != null){
		        			 return  formatDate(new Date(value));
		        		 }
		        		 return null;
		            },sortable:true,width:'15%'
				} ]],
				onLoadSuccess:function(){
					 if(that.isSwitchTabs){
						 
							//创建文本搜索框(IP)
							var filterMenu = $('<div style="width:175px;"></div>');
							filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter text-box" placeholder="搜索IP地址" id="searchIpInput" type="text" ></input></label></div>');
							
							that.ipMenuButton = that.mainDiv.find('#ipSearchInputButton').menubutton({
						  		 menu: filterMenu
							});
							filterMenu.find('#searchIpInput').on('click',function(e){
								e.stopPropagation();
							});
							filterMenu.find('#searchIpInput').on('change',function(e){
								//过滤资源实例
								var queryIp = $(e.target).val();
								if(queryIp){
									that.mainDiv.find('#queryIpInput').val(queryIp);
								}else{
									that.mainDiv.find('#queryIpInput').val('');
								}
								that.alarmDataGrid.reLoad();
							});
                            
                            //支持ie回车事件 add by sunhailiang on 20170628
				            if(oc.util.isIE()){
							   filterMenu.find('#searchIpInput').keypress(function(e){
							   	  e = e || window.event;
								  e.stopPropagation();
								  if(e.keyCode == 13){ //绑定回车 
									  $(this).trigger('change');
								  }
							   });
							}

							if(that.mainDiv.find('#queryIpInput').val()){
								filterMenu.find('#searchIpInput').val(that.mainDiv.find('#queryIpInput').val());
							}
							
							that.isSwitchTabs = false;
					 }
					 $(".alarm_detail").linkbutton({ plain:true, iconCls:'ico-mark' });
					 $('.alarm_detail').on('click', function(){   
						 var alarmId =$(this).attr('id');
						 var type =$(this).attr('name');
                          oc.resource.loadScript('resource/module/alarm-management/js/alarm-management-detail.js',function(){
                        	  oc.alarm.detail.dialog.open(alarmId,type,true);
      	     			});
		         	  });
					 $(".alarm_repository").linkbutton({ plain:true, iconCls:'icon-relevance' });
					 $('.alarm_repository').on('click', function(){   
						 var alarmId =$(this).attr('id');
                          oc.resource.loadScript('resource/module/alarm-management/js/alarm-knowledge-base.js',function(){
                        	  oc.alarm.knowledge.base.open(alarmId);
      	     			});
		         	  });
				},
				onClickCell : function(rowIndex, field, value){
					if(field == 'alarmContent'){
						var row = $(this).datagrid('getRows')[rowIndex];
						var alarmId = row.alarmId;
						var type = 1;
						 oc.resource.loadScript('resource/module/alarm-management/js/alarm-detailed-information.js',function(){
							 oc.alarm.detail.inform.open(alarmId,type);
     	     			});
						
					};
					if(field == 'resourceName'){
						var row = $(this).datagrid('getRows')[rowIndex];
						var resourceId = row.resourceId;
						oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailInfo.js', function(){
							oc.module.resmanagement.resdeatilinfo.open({instanceId:resourceId});
					 	});
					}
					if(field=='ipAddress'){//新增点击IP地址打开资源详情页面
						var row = $(this).datagrid('getRows')[rowIndex];
						var resourceId = row.resourceId;
						oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailInfo.js', function(){
							oc.module.resmanagement.resdeatilinfo.open({instanceId:resourceId});
					 	});	
					}
				},
				octoolbar : {
					left : [$('.lightAlarmClasss'), this.queryForm.selector],
					right : [{
						id : 'queryBtn',
						iconCls:'icon-search',
						text : '查询',
						onClick : function(){
							if($('#alarm_management_main_form_isCheckedRadioTwo').val()=="isChecked"){
								var startStr = $('#alarm_management_main_form1').find('#startTime').datetimebox('getValue').trim();
								var endStr = $('#alarm_management_main_form1').find('#endTime').datetimebox('getValue').trim();
								var dateStart = new Date(startStr.replace(/-/g,"/"));
								var dateEnd = new Date(endStr.replace(/-/g,"/"));
								var timeSub = dateEnd.getTime() - dateStart.getTime();
								
								if(timeSub < 0){
									alert('开始日期不能晚于结束日期');
								}else{
									that.alarmDataGrid.reLoad();
								}
							}else{
								that.alarmDataGrid.reLoad();
							}
							
						}
					},{
						id : 'resetBtn',
						iconCls:'icon-back',
						text : '重置',
						onClick : function(){
						//	that.removeStatusLightActive();
							that.queryForm.reset();
							$('#searchIpInput').val('');
							//$('#queryIpInput').val('');
							$('#alarm_management_main_form_instanceStatus').val(type);
							$('#prentCategorys').val(resource);
							$('#queryTime').val(4);
						}
					}]
				}
			});
			
			//创建文本搜索框(IP)
			var filterMenu = $('<div style="width:175px;"></div>');
			filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter text-box" placeholder="搜索IP地址" id="searchIpInput" type="text" ></input></label></div>');
			
			this.ipMenuButton = this.mainDiv.find('#ipSearchInputButton').menubutton({
		  		 menu: filterMenu
			});
			filterMenu.find('#searchIpInput').on('click',function(e){
				e.stopPropagation();
			});
		 
			filterMenu.find('#searchIpInput').on('change',function(e){
				//过滤资源实例
				var queryIp = $(e.target).val();
				if(queryIp){
					that.mainDiv.find('#queryIpInput').val(queryIp);
				}else{
					that.mainDiv.find('#queryIpInput').val('');
				}
				that.alarmDataGrid.reLoad();
			});

			//支持ie回车事件 add by sunhailiang on 20170628
	        if(oc.util.isIE()){
				  filterMenu.find('#searchIpInput').keypress(function(e){
				   	  e = e || window.event;
					  e.stopPropagation();
					  if(e.keyCode == 13){ //绑定回车 
						  $(this).trigger('change');
					  }
				  });
		    }

			this.addFreshBtn();
		},
		addFreshBtn : function(){
			var that = this;
			//tabs头上加一个刷新按钮
			var tabsWrap = this.mainDiv.find(".tabs-header > .tabs-wrap");
			var tabs = tabsWrap.find(".tabs").css("float", "left");
			this.freshBtn = $("<span/>").css({
				height : '33px',
				float : "right",
				"margin-right" : "10px"
			}).append($("<span>").addClass('ico ico-refrash').css("margin-top", "9px").attr("title", "刷新"));
			tabsWrap.append(this.freshBtn);
			this.freshBtn.find("span").on('click', function(){
				that.alarmDataGrid.reLoad();
			});
		},
	/*	removeStatusLightActive : function(){
			$('#alarm_management_main_form_instanceStatus').val('all');
			$('.lightAlarmClass').find("span").removeClass("active");
		},*/
	/*	addStatusLightActive : function(light){
			$(light).parent().find("span").removeClass('active');
			$(light).find("span").addClass('active');
		},*/
		loadAlarmDataGrid : function(url){
			if(this.flag){
				
				this.queryForm.reset();
				
				this.isSwitchTabs = true;
				
				this.ipMenuButton.menubutton('destroy');
				
				this.mainDiv.find("#alarm_datagrid1").datagrid({
					url : url
				});
			}
			this.flag = true;
		},
		loadState :function(state){
			
			this.isSwitchTabs = true;
			
			this.ipMenuButton.menubutton('destroy');
			
			$('#alarm_management_main_form_instanceStatus').val(type);
			
			this.mainDiv.find("#alarm_datagrid1").datagrid({
			});
		}
		
	};
	
	oc.ns('oc.module.alarm.management');
	new alarmManMainClass().open();

}