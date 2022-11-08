(function($) {
	var curClickChartId = null;
	var argsLengthFlag = false;
	function metricGroupClass() {
	}
	metricGroupClass.prototype = {
		constructor : metricGroupClass,	
		onselects: false,
		open : function(resourceId) {
			var chartObj1 = new chartObj($('#metric_group_data_chart'),1.5);			
			var Idx=0;
			var instance_id = resourceId;
			var showChartBool = false;

			$("#metric_group_form_metricNameId").val(instance_id);
			var metricGroupDiv = $('#metric_group_Div');
			var metricGroupMain = $('#metric_group_datagrid_Div').attr('id', oc.util.generateId());
			var metricGroupForm = metricGroupDiv.find("#metric_group_form");
			var that = this;

			var showMetricDatagridDiv = metricGroupDiv.find('#showMetricDatagridDiv');
			var barDiv = showMetricDatagridDiv.find('.tablelefttopico').parent();
			var showMetricDatagridParent = metricGroupDiv.find('#metric_group_datagrid_Parent');
			var showMetricChartsDiv = metricGroupDiv.find('#showMetricChartsDiv');
			showMetricChartsDiv.attr('style','display:none;');
			var barDivHeight = barDiv.height();
			var showMetricDatagridDivHeight = metricGroupDiv.height() - barDivHeight;
			showMetricDatagridDiv.height(showMetricDatagridDivHeight);
			showMetricDatagridParent.height(showMetricDatagridDivHeight - metricGroupForm.height());
			if(argsLengthFlag){
				showMetricDatagridParent.height(showMetricDatagridDivHeight - 10);
			}
			showMetricChartsDiv.height(barDivHeight);
			
			//绑定chartDiv收缩
			showMetricChartsDiv.find('.w-table-title').on('click',function(){
				showHideChartMethod(metricGroupDiv,showMetricDatagridDiv,showMetricChartsDiv,barDiv.find('useableQuerySpan'));
				showMetricChartsDiv.attr('style','display:none;');
				
			});
			//showMetricChartsDiv.find('.w-table-title').on('click',function(){
		    //	showHideChartMethod(metricGroupDiv,showMetricDatagridDiv,showMetricChartsDiv,barDiv.find('useableQuerySpan'));
		    //});

			// 表格查询表单对象
			var form = $('#metric_group_form');
			queryForm=oc.ui.form({
				selector : $("#metric_group_Div").find("form")
				
			});
			
			metricGroupMain.find('#customMetric').linkbutton('RenderLB',{
					text : '自定义指标',
					iconCls:"fa fa-search",			
						onClick : function(){
							that.customMetricBtnEvent(instance_id);
						}
			});

			metricGroupMain.find('#queryResourceShowName').linkbutton('RenderLB',{
				  text:'查询',
				  iconCls:"fa fa-search",
				  onClick:function(){
					  dataGrid.reLoad();
				  }
			});
			
			metricGroupMain.find('#metricName').keyup(function(e){
						if(e.keyCode == 13){
							dataGrid.reLoad();
						}
			});
			
			//datagrid
			var datagridDiv=$('#metric_group_datagrid');
			var dataGrid = oc.ui.datagrid({
	 			selector:datagridDiv,	 			
	 			pagination:false,
	 			queryForm:queryForm,
				queryConditionPrefix:'',
				fitColumns:true,
	 			instanceid:$("#metric_group_form_metricNameId").val(),
	 			url:oc.resource.
	 				getUrl("portal/resource/resourceDetailInfo/getcustomMetric.htm"),
	 			columns:[[
							{
								field : 'text',
								title : '指标名称',
								width : '221px',
//								formatter : function(value, row, rowIndex) {
									// 加入手形样式
//									if(row.type == "性能指标"){
//										var statusLabel = $("<label/>").addClass("light-ico_resource "+ row.status
//														+ " oc-pointer-operate");
//										statusLabel.attr('rowIndex', rowIndex);
//										if (value != null) {
//											statusLabel.attr('title', value).html(
//													value.htmlspecialchars());
//										}
//									}
//									return statusLabel;
//								}
							},{
								field : 'currentVal',
								title : '指标值',
								width : '221px',
								//ellipsis : true,
								formatter : function(value,row,rowIndex) {
									if(row.type == '性能指标'){
										if(value == '--'){
											return value;
										}else{
											var id = 'currentVal' + row.id;
											return '<a id="' + id + '" metric="'+row.id+'" resourceId="' + instance_id + '" class="ico icon-chart margin-top-seven showMetricDataChart">' + value + '</a>';
										}
									}else{
										if(value=='Null'){
											value = '--'; 
										}
										return '<div data-toggle="tooltip" style="overflow: hidden;text-overflow:ellipsis;white-space:nowrap;" title="'+value+'">'+ value +'</div>';
									}
//									var statusLabel = $("<div/>").addClass('ico ico-chart showMetricDataChart');
//									statusLabel.attr('rowIndex', rowIndex);
//									if (value != null) {
//										statusLabel.attr('title', value).html(
//												value.htmlspecialchars());
//									}
//									return statusLabel;
								}
							}, {
								field : 'lastCollTime',
								title : '采集时间',
								width : '221px'
							}, {
								field : 'type',
								title : '指标类型',
								width : '221px'
							}, {
								field : 'profileId',
								title : '阈值',
								width : '221px',
								formatter : function(value,row) {
									var statusLabel = $("<div/>").addClass('thresholds').addClass('oc-info-value');
									var metricShowName = row.metricName;
									if(row.unit){
										metricShowName += '(' + row.unit + ')';
									}
									if(row.isCustomMetric){
										statusLabel.append('<a metricname="' + metricShowName + '" metricid="' + row.id + '" class="light_blue icon-threshold updateCustomThresholdClass" title="编辑阈值"></a>');
									}else{
										statusLabel.append('<a metricname="' + metricShowName + '" metricid="' + row.id + '" timelineid="' + row.timelineid + '" profileid="' + row.profileId + '" class="light_blue icon-threshold updateThresholdClass" title="编辑阈值"></a>');
									}
									return statusLabel;
								}
							}
	 	 		     ]],
	 	 		loadFilter : function(data){
						var rowss = new Array();
						for(var i = 0; i < data.data.rows.length; i ++){
							var row = data.data.rows[i];
							var color = 'green';
							if(row.text){
								row.metricName = row.text;
								var currentVal = row.currentVal;
								row.currentVal = (currentVal != '' && currentVal != undefined) ? currentVal : "--";
								var lastCollTime = row.lastCollTime;
								row.lastCollTime = !!lastCollTime ? lastCollTime : "--";
								var type = row.type;
								
								var status = row.status;
								var text = row.text;
								if(row.isAlarm){
									switch(row.status){
									case 'CRITICAL':
										color = 'red'
										break;
									case 'SERIOUS':
										color = 'orange'
										break;
									case 'WARN':
										color = 'yellow'
										break;
									case 'NORMAL':
									case 'NORMAL_NOTHING':
										color = 'green'
										break;
									default :
										color = 'gray'
										break;
									}
									row.text = "<span class='light-ico "+ color +"light'/><span title='" + row.text + "'>" + row.text + "</span>";
								}

								if(!row.isAlarm){
									row.text = "<span class='light-ico graylight'/><span title='" + row.text + "'>" + row.text + "</span>";
								}

//								if(row.type == "InformationMetric"){
//									row.text = "<span class='light-ico information_ico'/><span title='" + row.text + "'>" + row.text + "</span>";
//								}

								row.type = row.type == 'InformationMetric'?'信息指标':'性能指标';
							}
								rowss.push(row);
						}	
						data.data.rows = rowss;
						return data.data;
					},
				onLoadSuccess:function(data){		 		    	
	 		    	//图表
	 		    	$('.showMetricDataChart').on('click',function(e){
						showMetricChartsDiv.attr('style','display:block;');
			    		 if(curClickChartId){
			    			 $('#' + curClickChartId).attr('class','ico icon-chart margin-top-seven showMetricDataChart');
			    		 }
			    		 curClickChartId = $(e.target).attr('id');
			    		 $(e.target).attr('class','ico icon-chartred margin-top-seven showMetricDataChart');
			    		 chartObj1.setIds($(e.target).attr('metric'),$(e.target).attr('resourceId'));
			    		 showHideChartMethod(metricGroupDiv,showMetricDatagridDiv,showMetricChartsDiv,barDiv.find('useableQuerySpan'),'show');
	 		    	});
	 		    	
					$('.updateThresholdClass').click(function(){
						var that = this;
						oc.resource.loadScript('resource/module/resource-management/js/strategy_threshold_setting.js',function(){
							oc.module.resourcemanagement.strategythresholdsetting.open({metricName:$(that).attr('metricname'),metricId:$(that).attr('metricid'),
								profileId:$(that).attr('profileid'),timelineId:$(that).attr('timelineid'),readOnly:true});
						});
					});
					
					$('.updateCustomThresholdClass').click(function(){
						var that = this;
						oc.resource.loadScript('resource/module/resource-management/js/strategy_threshold_setting.js',function(){
							oc.module.resourcemanagement.strategythresholdsetting.openWidthCustom({metricName:$(that).attr('metricname'),metricId:$(that).attr('metricid'),readOnly:true});
						});
					});

					//datagrid错位滚动条调整
					metricGroupDiv.find('.datagrid-view2>.datagrid-body').scroll(function(){
						if($(this).get(0).scrollWidth - $(this).width() < $(this).scrollLeft() + 15){
							$(this).scrollLeft($(this).get(0).scrollWidth - $(this).width() - 15);
						}
					 });

					//动态调整宽度
					//head tr
					var headerTr = $(".datagrid-header-inner table tr:first-child");

					//table  datagrid-btable
					var bodyTable = $(".datagrid-body table:first-child");

					//释放
					metricGroupDiv.mouseup(function(){
						var bodyTableh = $(bodyTable.get(1));
						var headerTrh = $(headerTr.get(2));

						setTimeout(function(){
							bodyTableh.width(headerTrh.width());
						},100);
					});
	 		    }
	 		});
		},
		

		customMetricBtnEvent : function(instanceId){
			oc.resource.loadScript('resource/module/resource-management/customMetric/js/customMetricList.js', function() {
				oc.module.resource.custom.metric.list.open({
					id : instanceId
				});
			});
		}
	};
	
		/******************** 
	* 图标tab显示隐藏 
	*******************/  
	function showHideChartMethod(rootDiv,datagridDiv,chartDiv,tabTitleDiv,showType){
		var rootDivHeight = rootDiv.height();
	    //显示图标时,datagridDiv高度
	    var showMetricDatagridDivHeight = rootDivHeight*0.45;
	    var tabTitleHeight = tabTitleDiv.parent().height();
	    
	    if(argsLengthFlag){
	    	showMetricDatagridDivHeight = 300;
	    	if(!(datagridDiv.height()>showMetricDatagridDivHeight)){
				//表示收缩
				chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_down');
				
				//有showType表示点击小图标展示线图
				if(showType && 'show'==showType){
					chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
				}else{
					showMetricDatagridDivHeight = rootDivHeight ;
				}
				
			}else{
				chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
			}
			//表格和图标高度控制	
			datagridDiv.height(showMetricDatagridDivHeight);
			var showMetricDatagridParentDiv = datagridDiv.find('#metric_group_datagrid_Parent');
			showMetricDatagridParentDiv.height(showMetricDatagridDivHeight-tabTitleHeight-34);
			chartDiv.height(rootDivHeight-showMetricDatagridDivHeight);
			
			var headerHeight = showMetricDatagridParentDiv.find('.datagrid-header').height();
			showMetricDatagridParentDiv.find('.datagrid-body').each(function(e){
				var targetTemp =  $(this);
				targetTemp.height(showMetricDatagridDivHeight-tabTitleHeight-headerHeight);
			});
	    }else{
	    	if(!(datagridDiv.height()>showMetricDatagridDivHeight)){
				//表示收缩
				chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_down');
				
				//有showType表示点击小图标展示线图
				if(showType && 'show'==showType){
					chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
				}else{
					showMetricDatagridDivHeight = rootDivHeight - tabTitleHeight;
				}
				
			}else{
				chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
			}
			//表格和图标高度控制	
			datagridDiv.height(showMetricDatagridDivHeight);
			var showMetricDatagridParentDiv = datagridDiv.find('#metric_group_datagrid_Parent');
			showMetricDatagridParentDiv.height(showMetricDatagridDivHeight-tabTitleHeight-10);
			chartDiv.height(rootDivHeight-showMetricDatagridDivHeight);
			var headerHeight = showMetricDatagridParentDiv.find('.datagrid-header').height();
			showMetricDatagridParentDiv.find('.datagrid-body').each(function(e){
				var targetTemp =  $(this);
				targetTemp.height(showMetricDatagridDivHeight-tabTitleHeight-headerHeight);
			});
	    }
	}

	 //命名空间
	oc.ns('oc.resource.metricgroup.list');
	oc.resource.metricgroup.list={
			open:function(resourceId){
				return new metricGroupClass().open(resourceId);
			}
	};
	
})(jQuery);