(function($) {
	function inspectPlanDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	var properties = {};
	var domainIds=new Array();
	var userid=0;
	var inspType=null;
	inspectPlanDetail.prototype = {

		//id
		_inspectPlanId: undefined,
		_selectedResTypeId: undefined,
		
		//form
		_inspectPlanBasicInfoForm : undefined,
		_inspectNormalInfoForm: undefined,
		_inspectResultForm: undefined,

		//obj
		_dialog:undefined,
		cfg : undefined,
		_treegrid: undefined,
		_dirAccordion: undefined,
		_mainAccordion: undefined,
		itemArrays: [],
		typeEvent: undefined,
		_multiDatesPicker: undefined,
		
		//boolean
		_loadBaseInfo: undefined,
		_loadNormalInfo: {},
		_loadResultInfo: undefined,
		
		_savedBaseInfo: undefined,
		_savedNormalInfo: undefined,
		_savedResultInfo: undefined,
		
		_resourceName: '',
		_bizName: '',
		_reportName: '',
		
		_dialog: undefined,
		
		_templateIndex: 0,
		_resultIndex: 1,
		_inspectorName: '',
		_domainId: undefined,
		_flagId:"",//复制巡检的标志
		edidType:undefined,
	    /*结果及其描述数据封装 add by sunhailiang on 20170615*/
	    getResultParams :function(){
	   	    var that = this;
			var params = {},resultForm = that._mainDiv.find(".inspect-plan-result-form");
			params.id = $("input[name='id']",resultForm).val();
			$("textarea[name='inspectPlanSumeriseDescrible']",resultForm).each(function(i){
        		params["planResultsList["+i+"].inspectPlanSummeriseName"] = $("input[name='inspectPlanSummeriseName']",resultForm).eq(i).val() || "";
        		params["planResultsList["+i+"].inspectPlanSumeriseDescrible"] = this.value;
        	});
			return params;
	    },
		open : function() {//打开添加、编辑域弹出框
			
			var dlg = this._mainDiv = $('<div/>'), 
			that = this, 
			
			type = that.cfg.type;
			edidType = that.cfg.type;
			inspType=inspType;
			oc.util.ajax({
				url: oc.resource.getUrl('inspect/plan/getInspectProperties.htm'),
				successMsg : null,
				success : function(data) {
					if(data.code == 200){
						properties = data.data;
						that._dialog = dlg.dialog({
							href : oc.resource.getUrl('resource/module/inspect-management/inspect_plan_edit.html'),
							title : (type == 'edit' ? '编辑' : '添加') + '巡检计划 - 基本信息设置',
							width: 950,
							height : 346,
							top: 30,
							resizable : false,
							cache : false,
							onLoad : function() {
								that._init(dlg);
							},
							onClose:function(){
								$("input[name='inspectPlanDomain']").val("");
								var refresh = that.cfg.refresh;
								if(refresh) refresh();
								_myform.val("");
								dlg.dialog("destroy");//add by sunhailiang on 20170615
							}
							
						});
					}
				}
			});
			
			
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_mainDiv : undefined,
		_id : '#oc_module_inspect_add_or_edit_detail',
		_myform : undefined,
		_init : function(dlg) {
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			that._initBlocks(that);
			
			that._initMethods.baseInfo(that);//初始化基本信息
			
			//如果是编辑
			if(that.cfg.type != 'add') {
				that._reportName = that._mainDiv.find('input[name="insepctPalnTaskName"]').val();
				that._loadEdit.loadId(that);
				that._loadEdit.loadBasicInfoForm(that);//加载域基本信息
				that._loadEdit.loadReportTemplate(that);
			}
			
			//init '下一步'，‘保存’按钮
			that._initButtons(that);
			that._initTab(that);//init choicebtn
			_myform=	oc.ui.form({
				selector:$('.inspect-plan-result-form')
			});
		},
		_nextStep: function(that) {
			if(!that._savedBaseInfo||that._savedBaseInfo==false){
				return;
			}
			that._mainDiv.find('.ins-plan-basic-info-setting').addClass('hide');
			that._mainDiv.find('.ins-plan-template-info-setting').removeClass('hide').fadeIn();
			
			that._mainDiv.find('.ins-plan-step1-buttons').addClass('hide');
			that._mainDiv.find('.ins-plan-step2-buttons').removeClass('hide');

			var title = (that.cfg.type == 'add' ? '添加' : '编辑') + '巡检计划 - 巡检报告设置';
			that._dialog.dialog('setTitle', title);
			that._dialog.dialog('resize', {
				width: 980,
				height: 610
			});
		},
		_preStep: function(that) {
			that._mainDiv.find('.ins-plan-template-info-setting').addClass('hide');
			that._mainDiv.find('.ins-plan-basic-info-setting').removeClass('hide').fadeIn();
			
			that._mainDiv.find('.ins-plan-step2-buttons').addClass('hide');
			that._mainDiv.find('.ins-plan-step1-buttons').removeClass('hide');

			var title = (that.cfg.type == 'add' ? '添加' : '编辑') + '巡检计划 - 基本信息设置';
			that._dialog.dialog('setTitle', title);
			that._dialog.dialog('resize', {
				width: 950,
				height: 340
			});
		},
		_initBlocks: function(that) {
			that._mainDiv.find('.ins-plan-template-info-setting').addClass('hide');
			that._mainDiv.find('.ins-plan-step2-buttons').addClass('hide');
		},
		_initTab:function(that){
			var planid=this._inspectPlanId;
			that._mainDiv.find('#choiceDomain').click(function(){
				
				var domainId=that._mainDiv.find('input[name="inspectPlanInspector"]').val();//inspectPlanInspector
				if(domainId=="" || domainId==null){
					alert("请先选择巡检人！");
				}else{
					var inspData=null;
					var dlg = $("<div/>").attr("id",'domainDiv');
					dlg.dialog({
						title : '选择域',
						width : '292px',
						height : '230px',
						modal: false,
						onClose : function(){
						   dlg.dialog("destroy");//add by sunhailiang on 20170615
						},
						href : oc.resource.getUrl('resource/module/inspect-management/warn_user_domain.html'),
						onLoad : function(){
							var DatagridDiv=$("#domain_table_data");
							inspData=	oc.ui.datagrid({
								selector:DatagridDiv,
								fit : true,
								pagination:false,
								columns:[[
								    {field:'id',checkbox:true,align:'center',
								    	isChecked:function(value, row, index){
											if(row.checked=="true"){
												return true;
											}else{
												return false;
											}
								        }},
								    {field:'name',title:'域',width:'80',align:'center',
								    	formatter:function(value,row,rowIndex){ 
							        		return "<div id='"+row.id+"' value='"+row.name+"'>"+row.name+"</div>"; 
						        	    }}
								]]
							});
							oc.util.ajax({
								url : oc.resource.getUrl("inspect/plan/getDomainRoles.htm"),
								data:{id:domainId,planId:planid},
								timeout:null,
								success:function(data){
									var d = data.data,
					    			domains = {},
					    			result = [];
					    			for(var i=0; i<d.length; i++){
					    				var item = d[i],
					    				domain = domains[item.id];
					    				if(domainId==1){//admin
					    					result.push(item);
					    				}else{//others
					    					if(domain){
						    					domain.roleName += "," + item.roleName;
						    				}else{
						    					result.push(item);
						    					domains[item.id] = item;
						    				}
					    				}
					    			
					    			}
									DatagridDiv.datagrid('loadData',result); 
								}
							});
						},
						buttons :[{
	    					text:"确定",
	    					iconCls:"fa fa-check-circle",
	    					handler:function(){
	    						$("textarea[name='inspectPlanDomainShowname']").val("");
	    						$("input[name='inspectPlanDomain']").val("");
	    						var id = new Array();
	    						var name = new Array();
	    						id.length=0;
	    						
	    						$("#domainDiv").find("input[name='id']:checked").each(function(){
	    							id.push($(this).val());
	    						});
	    						var valStr="",ids="";
	    						for(var i=0;i<id.length;i++){
	    							if(i==0){
	    								if($("div[id='"+id[i]+"']").attr('value')!=undefined){
	    									ids=id[i]+",";
		    								valStr=$("div[id='"+id[i]+"']").attr('value')+",";	
	    								}
	    								
	    							}else{
	    								if($("div[id='"+id[i]+"']").attr('value')!=undefined){
	    									ids+=id[i]+",";
		    								valStr+=$("div[id='"+id[i]+"']").attr('value')+",";
	    								}
	    								
	    							}
	    							
	    						}
	        					var vals=valStr.substring(0,valStr.length-1);
	        					var idArr=ids.substring(0,ids.length-1);
	    					$("textarea[name='inspectPlanDomainShowname']").val(vals);
	    				
	    					$("input[name='inspectPlanDomain']").val(idArr);
	    					$("#domainDiv").dialog('close');
	    					}
	    				}]
					});
					
				}
	
			});
			
		},
		_initButtons: function(that) {
			//下一步
			that._mainDiv.find('.ins-plan-next-step').linkbutton('RenderLB', {
				onClick: function(e) {
					/*if(that._savedBaseInfo!=true){
						return;
					}*/
					var msg1 = that._checkForm.basicForm(that);
					if(msg1 != '') {
						alert(msg1);
						return;
					}
					that._saveMethods.saveBasicInfo(that);
					//保存域ID
					
					that._domainId = that._mainDiv.find('input[name="inspectPlanDomain"]').val();
					//复制巡检不保存
					that._flagId = that._mainDiv.find('.inspect-plan-result-form').find('input[name="id"]').val();
					if(that._flagId==1||that._flagId==""){
						return;
					}
					//加载第二页信息
					that._initMethods.initNormal(that);//初始化巡检报告模板信息
					that._initMethods.contentSetting(that);//初始化巡检报告内容设置
					that._initMethods.sumarySetting(that);//初始化报告结论设置

					//结论添加按钮事件绑定
//					that._mainDiv.find(".inspect-plan-result-add").css({
					
					$('.childReportMax').html(properties.resultMax);
					
					that._mainDiv.find("#inspect_plan_result_add").css({
						'position': 'relative',
						'right': '125px',
						'bottom': '27px',
						'float': 'right'
					}).click(function(e) {
						var childReportCount = parseInt($('.childReportCount').html());
						if(childReportCount > properties.resultMax - 1){
							alert('最多添加' + properties.resultMax + '个子结论');
							return;
						}
						var count = that._mainDiv.find('.ins-report-title-index').html();
						var index = that._mainDiv.find('.ins-report-result-title-line').length + 1;
						that._initMethods.genResult(count, index, that, null, null);
						that._resultIndex += 1;
						$('.childReportCount').html(parseInt($('.childReportCount').html()) + 1);
					});
					
					if(that.cfg.type == 'add') {
						var rname = that._reportName = that._mainDiv.find('input[name="insepctPalnTaskName"]').val();
						that._mainDiv.find('.ins-plan-report-name-for-display').val(rname);
						if(!that._savedBaseInfo) {
//							if(that.cfg.id==1){
//								alert("巡检计划名称重复")
//							}else{
								alert('请保存基本信息');
//							}
						} else {
							that._nextStep(that);
						}
					} else {
						that._nextStep(that);
					}
				}
			});
			
			//上一步
			that._mainDiv.find('.ins-plan-pre-step').linkbutton('RenderLB', {
				onClick: function(e) {
					that._preStep(that);
				}
			});
			//保存巡检报告模板
			that._mainDiv.find('.ins-plan-save-step2').linkbutton('RenderLB', {
				onClick: function(e) {
					//save report name
					var success = that._saveMethods.saveReportName(that);
					//save result
					if(success > 0) {
						that._saveMethods.saveResult(that);
					}
				}
			});
			
			//完成
			that._mainDiv.find('.ins-plan-finish-step2').linkbutton('RenderLB', {
				onClick: function(e) {
					//完成也保存页面信息，成功后关闭窗口
					var success = that._saveMethods.saveReportName(that);
					if(success > 0) {
						var msg = that._checkForm.resultForm(that);
						if(msg != '') {
							alert(msg);
							return;
						} else {
							oc.util.ajax({
								url: oc.resource.getUrl('inspect/plan/updateConclusion.htm'),
								data: that.getResultParams(),
								async:false,
								successMsg : '保存成功',
								success : function(data) {
									if(data.code == 200) {
										that._savedResultInfo = true;
										
									}
								}
							});
							var result = that.enablePlan(that);
							if(result > 0) {
								var refresh = that.cfg.refresh;
								if(refresh) refresh(that._flagId);
								
								that._dialog.dialog('destroy');
							}
						}
						
					}
				}
			});
		},
 		_initMethods : {
			'baseInfo' : function(that) {
				var monthOrCustomSelected = function(type) {
					if(type == 'month') {
						//初始化日期多选控件
						var mds = that._multiDatesPicker = oc.ui.multidateselect({
							container: '.inspect-type-month'
						});
						mds.setValues([1, 15]);
					} else if(type == 'custom') {
						var custom = that._mainDiv.find('.inspect-type-custom');
						custom.removeClass('hide');
						var dateList = custom.find('.ins-plan-custom-date-list');
						
						//reset status of 自定义时间
						dateList.empty();
						that._mainDiv.find('input[name="inspectCustomDate"]').val('');
						
						var itemIndex = 1;
						//render the button that add the custom date
						custom.find('.icon-add-custom-date').linkbutton({
							iconCls: 'fa fa-plus',
							onClick: function(e) {
								
								var addedDates = [];
								
								var getAddedCustomDates = function() {
									//find seted custom date and set those values to a hidden input
									that._mainDiv.find('.ins-plan-custom-datetime-single').each(function(i, t) {
										addedDates.push($(t).val());
									});
									that._mainDiv.find('input[name="inspectCustomDate"]').val(addedDates.toString());
								}

								var datetime = that._mainDiv.find('.ins-plan-custom-date-selection').datetimebox('getValue');
								var fomatedText = new moment(datetime).format('YYYY年MM月DD日 HH:mm');
								var formatedValue = new moment(datetime).format('YYYY-MM-DD HH:mm');
								var div = '<div class="ins-plan-custome-date-item custom-item-date-counter-'+itemIndex+' cus_execution"> ' +
										'<span class="locate-left show-custom-date" style="margin-left: 44px;">'+fomatedText+'</span> ' +
										'<input type="hidden" class="ins-plan-custom-datetime-single" value="'+formatedValue+'">' +
										'<span style="margin-left: 85px;" class="icon-del-custom-item-'+itemIndex+'"><span class="fa fa-times-circle"></span></span> ' +
									'</div>';
								dateList.append(div);

								that._mainDiv.find('.icon-del-custom-item-'+itemIndex).on('click', function(e) {
									$(this).parent().remove();
									addedDates = [];
									getAddedCustomDates();
								});
								itemIndex++;

								getAddedCustomDates();
							}
						});
					}
				}
				that.typeEvent = monthOrCustomSelected;
				
				that._mainDiv.find('.basic-info-parent').addClass('hide');
				
				that._inspectPlanBasicInfoForm = oc.ui.form({
					selector : that._mainDiv.find('.inspect-plan-add-form'),
					datetimebox:[
					    {
					    	selector: '.ins-plan-custom-date-selection',
					    	width: 245,
					    	showSeconds : false
					    }
					],
					combobox: [
					    {
						selector: '[name=inspectPlanType]',
						placeholder: false,
						fit : false,
//						width: 315,
						data:[
						      {id:1, name: '手动执行'},
						      {id:2, name: '自动执行(每天)'},
						      {id:3, name: '自动执行(每周)'},
						      {id:4, name: '自动执行(每月)'},
						      {id:5, name: '自动执行(自定义)'}
						],
						selected: '1',
						onChange: function(id) {
							that._mainDiv.find('.basic-info-parent').removeClass('hide');

							switch(id) {
							case 1:
								that._mainDiv.find('.basic-info-parent').addClass('hide');
								break;
							case 2:
								that._mainDiv.find('.basic-info-parent').removeClass('hide');
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide').siblings().addClass('hide');
								break;
							case 3:
								that._mainDiv.find('.inspect-type-detail').removeClass('hide');
								that._mainDiv.find('.inspect-type-week').removeClass('hide').siblings().addClass('hide');
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide');
								
								break;
							case 4:
								that._mainDiv.find('.inspect-type-month').removeClass('hide').siblings().addClass('hide');
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide');
								monthOrCustomSelected('month');
								break;
							case 5:
								that._mainDiv.find('.inspect-type-custom').removeClass('hide').siblings().addClass('hide');
								monthOrCustomSelected('custom');
								break;
							default:
								that._mainDiv.find('.basic-info-parent').addClass('hide');
								break;
							}
						}
					}, {
						selector: '[name=inspectTypeHour]',
						placeholder: false,
						fit: false,
						width: 90,
						data:[
						      {id:00, name:'00时'},
						      {id:01, name:'01时'},
						      {id:02, name:'02时'},
						      {id:03, name:'03时'},
						      {id:04, name:'04时'},
						      {id:05, name:'05时'},
						      {id:06, name:'06时'},
						      {id:07, name:'07时'},
						      {id:08, name:'08时'},
						      {id:09, name:'09时'},
						      {id:10, name:'10时'},
						      {id:11, name:'11时'},
						      {id:12, name:'12时'},
						      {id:13, name:'13时'},
						      {id:14, name:'14时'},
						      {id:15, name:'15时'},
						      {id:16, name:'16时'},
						      {id:17, name:'17时'},
						      {id:18, name:'18时'},
						      {id:19, name:'19时'},
						      {id:20, name:'20时'},
						      {id:21, name:'21时'},
						      {id:22, name:'22时'},
						      {id:23, name:'23时'}
						]
					}, {
						selector: '[name=inspectTypeMinute]',
						placeholder: false,
						fit: false,
						width: 100,
						data:[
						      {id:00, name:'00分'},
						      {id:05, name:'05分'},
						      {id:10, name:'10分'},
						      {id:15, name:'15分'},
						      {id:20, name:'20分'},
						      {id:25, name:'25分'},
						      {id:30, name:'30分'},
						      {id:35, name:'35分'},
						      {id:40, name:'40分'},
						      {id:45, name:'45分'},
						      {id:50, name:'50分'},
						      {id:55, name:'55分'}
						]
					}/*, {
						selector: '[name=inspectPlanDomain]',
						fit: false,
						placeholder: '请选择域',
						width: 164,
						data: oc.index.getDomains(),
						onChange: function(id) {
							var lenh = that._inspectPlanBasicInfoForm.ocui.length || 0;
							that._inspectPlanBasicInfoForm.ocui[lenh-1].reLoad(oc.resource.getUrl('inspect/plan/getUser.htm?domainId='+id))
						}
					}*/, {
						selector: '[name=inspectPlanInspector]',
						fit: false,
						placeholder: '请选择巡检人',
						url:oc.resource.getUrl('inspect/plan/getAllUser.htm'),
						width: 164,
						placeholder:'--选择巡检人--',
						filter:function(data){
							if(edidType=="edit"){
								that._mainDiv.find("input[name='inspectPlanInspector']").combobox('setValue',userid);
								return data.data;
							}else{
								return data.data;
							}
					
							
						},
						onChange : function(newValue, oldValue, newJson, oldJson){
							that._mainDiv.find("textarea[name='inspectPlanDomainShowname']").val("");
							that._mainDiv.find("input[name='inspectPlanDomain']").val("");
					 	
						},
						onSelect: function(d) {
							that._inspectorName = d.name;
							that._mainDiv.find('.inspector-name-for-display').html(d.name);
						}
					}]
				});
				
			},
			'initNormal': function(that) {
				//页面重新加载常规信息
				that._initMethods.reloadNormalInfoItems(that);
				//添加常规信息
				that._mainDiv.find('.icon-add-ins-plan-normal-info').linkbutton({
					iconCls: 'icon-edit',
					onClick: function(e)	{
						var reportName = that._mainDiv.find('.ins-plan-report-name-for-display').val() || '';
						oc.resource.loadScript('resource/module/inspect-management/js/inspect_plan_normal_info.js',function(){
							oc.module.inspect.plan.normal.info.open({
								id: that.cfg.id,
								reportName: reportName,
								getNames: function(resourceName, bizName) {
									that._resourceName = resourceName;
									that._bizName = bizName;
								},
								reloadNormalInReport: function() {
									that._initMethods.reloadNormalInfoItems(that);
								},
								reloaddir:function(){
									that._initMethods.genDir(that);
								}
							});
						});
					}
				});
				
			},
			'initBaiscItem': function(counter, table, tr, that, reportName) {
				if(counter % 6 == 0) {
					tr = $('<tr></tr>');
					table.append(tr);
				}
				var rename = that._mainDiv.find('input[name="insepctPalnTaskName"]').val();
				if(!reportName || reportName == '') {
					reportName = rename;
				}
				
				var th = $('<td class="tab-l-tittle" style="width:12.3%; text-align: right;"><span>巡检报告名称：</span></th>');
				var td = $('<td align="left" style="width: 15%;"><input class="ins-plan-report-name-for-display" value="'+reportName+'" style="width: 150px;" name="inspectReportName" type="text" ></td>');
				tr.append(th).append(td);
				counter+=2;
				
				if(counter % 6 == 0) {
					tr = $('<tr></tr>');
					table.append(tr);
				}
				var th = $('<td class="tab-l-tittle" style="width:12.3%; text-align: right;"><span>巡检时间：</span></th>');
				var td = $('<td align="left" style="width: 20%;"><span></span></td>');
				tr.append(th).append(td);
				counter+=2;
				
				if(counter % 6 == 0) {
					tr = $('<tr></tr>');
					table.append(tr);
				}
				var th = $('<td class="tab-l-tittle" style="width:12.3%;"><span>巡检人：</span></th>');
				var td = $('<td align="left" style="width: 25%;"><span class="inspector-name-for-display"></span></td>');
				tr.append(th).append(td);
				counter+=2;
			},
			'reloadNormalInfoItems': function(that, type) {
				
				var table = that._mainDiv.find('.rep-normal-info-list');
				table.empty();
				var tr = null;
				var counter = 0;
				
				if(that.cfg.id) {
					oc.util.ajax({
						url : oc.resource.getUrl('inspect/plan/loadRoutine.htm'),
						data : {
							id : that.cfg.id
						},
						async:false,
						successMsg : null,
						success : function(data) {
							if(data.code == 200) {
								that._loadNormalInfo = data.data;
								var items = data.data.selfItems;
								
								var defItemObj = data.data;

								if(defItemObj.inspectReportResourceName == 'Business'){
                                    defItemObj.resourceName = '业务系统';
                                }
								
								//显示报告名称
								that._initMethods.initBaiscItem(counter, table, tr, that, defItemObj.reportName);
								that._reportName = defItemObj.reportName;
								//显示巡检人
								that._mainDiv.find('.inspector-name-for-display').html(defItemObj.inspectorName);
								
								if(defItemObj.reportProduceTimeShow == 'on') {
									if(counter % 6 == 0) {
										tr = $('<tr></tr>');
										table.append(tr);
									}
									
									var th = $('<td class="tab-l-tittle" style="width: 15%; text-align: right;"><span>报告生成时间：</span></th>');
									var td = $('<td align="left" style="width: 25%;"><span></span>'+defItemObj.reportProduceTime+'</td>');
									
									tr.append(th).append(td);
									counter+=2;
								}
								if(defItemObj.reportModifyTimeShow == 'on') {
									if(counter % 6 == 0) {
										tr = $('<tr></tr>');
										table.append(tr);
									}
									var th = $('<td class="tab-l-tittle" style="width: 15%; text-align: right;"><span>最后编辑时间：</span></th>');
									var td = $('<td align="left" style="width: 20%;"><span>'+defItemObj.reportModifyTime+'</span></td>');
									
									tr.append(th).append(td);
									counter+=2;
								}
								if(defItemObj.reportModifyTimeShow == 'on') {
									if(counter % 6 == 0) {
										tr = $('<tr></tr>');
										table.append(tr);
									}
									var th = $('<td class="tab-l-tittle" style="width: 15%; text-align: right;"><span>最后编辑人姓名：</span></th>');
									var td = $('<td align="left" style="width: 20%;"><span></span></td>');
									
									tr.append(th).append(td);
									counter+=2;
								}
								if(defItemObj.inspectReportResourceNameShow == 'on') {
									if(counter % 6 == 0) {
										tr = $('<tr></tr>');
										table.append(tr);
									}
									var th = $('<td class="tab-l-tittle" style="width: 15%; text-align: right;"><span>资源类型：</span></th>');
									var td = $('<td align="left" style="width: 20%;"><span>'+(defItemObj.resourceName || '')+'</span></td>');
									
									tr.append(th).append(td);
									counter+=2;
								}
								if(defItemObj.inspectReportBusinessNameShow == 'on') {
									if(counter % 6 == 0) {
										tr = $('<tr></tr>');
										table.append(tr);
									}
									var th = $('<td class="tab-l-tittle" style="width: 15%; text-align: right;"><span>业务名称：</span></th>');
									var bn = defItemObj.businessName;
									var subBn = bn || '';
									if(bn != undefined && bn != ''&& bn.length > 15) {
										subBn = bn.substring(0, 13) + ' ...';
									}
									var td = $('<td align="left" style="width: 20%;"><span title="'+bn+'">'+subBn+'</span></td>');
									
									tr.append(th).append(td);
									counter+=2;
								}
								table.append(tr);
								
								//iterate the added custom display control to generate customed item
								for(var i=0; i<items.length; i++) {
									var item = items[i];
									
									if(0 == (counter % 6)) {
										tr = $('<tr></tr>');
										table.append(tr);
									}
									
									switch(item.inspectPlanSelfItemType*1) {
									case 1:
										var th = $('<td class="tab-l-tittle" style="text-align: right;"><span>'+item.inspectPlanSelfItemName+'：</span></th>');
										var td = $('<td align="left" style="width: 20%;"><span>'+(item.inspectPlanItemContent || '')+'</span></td>');
										tr.append(th).append(td);
										counter+=2;
										break;
									case 2:
										var th = $('<td class="tab-l-tittle" style="text-align: right;"><span>'+item.inspectPlanSelfItemName+'：</span></th>');
										var td = $('<td align="left" style="width: 20%;"><span>'+(item.inspectPlanItemContent || '')+'</span></td>');
										tr.append(th).append(td);
										counter+=2;
										break;
									default:
										var th = $('<td class="tab-l-tittle" style="text-align: right;"><span>'+item.inspectPlanSelfItemName+'：</span></th>');
										var td = $('<td align="left" style="width: 20%;"><span>'+(item.inspectPlanItemContent || '')+'</span></td>');
										tr.append(th).append(td);
										counter+=2;
										break;
									}
								}
							}
						}
					});
				}
			},
			'genDir': function(that) {
				var dirParent = that._mainDiv.find('.inspect-plan-dir');
				dirParent.empty();
				var len = dirParent.find('.ins-plan-dir-value').length || 0;
				var index = len + 1;
				//请求后台，获取保存的巡检目录
				if(that.cfg.id) {
					oc.util.ajax({
						url : oc.resource.getUrl('inspect/plan/loadInspectionItems.htm'),
						data : {
							id: that.cfg.id,//1581000,that.cfg.id
						},
						async:false,
						successMsg : null,
						success : function(data) {
							var dirs = data.data;
							for(var i=0; i<dirs.length; i++) {
								var dir = dirs[i];
								var dirHead = '<div class="ins-dir-head-'+i+' ins-plan-dir-value ins-plan-dir-set" dirid="'+dir.id+'" >' +
								'<span style="margin-left:10px;line-height: 30px;">'+(i+1) + '. '+ dir.inspectPlanItemName+'：'+dir.inspectPlanItemDescrible+'</span>' + 
								'<span title="编辑巡检项" class="icon-edit-inspect-items-'+dir.id+'" style="margin-left: 30px;"></span>' +
								'</div>';
								var treegridBody = '<div><table class="inspect-items-treegrid-'+i+'"></table></div>';
								dirParent.append(dirHead);
								dirParent.append(treegridBody);
								
								var selector = 'inspect-items-treegrid-'+i;
								var treegridNode = that._buildGrid(that, selector, dir);
								
								that._templateIndex = (i+1);
							}
						}
					});
					
				}
			},
			'contentSetting' : function(that) {
				
				that._mainDiv.find('.icon-add-or-edit-ins-plan-dir').linkbutton({
					iconCls: 'icon-edit',
					onClick: function(e) {
						that.open_editInspectDir(that);
					}
				});
				that._initMethods.genDir(that);
			},
			'genResult': function(count, index, that, name, value) {
				var resultForm = that._inspectResultForm = that._mainDiv.find('.inspect-plan-result-form');
				var what = '<div class="ins-report-edit-result-content" style="width: 100%; height: 140px;">' +
					'<div class="ins-report-result-title-line oc-poptitle" style="width: 97%; height:32px;padding: 3px 0 0 25px; margin-bottom:5px;">' +
						'<span class="inspect-plan-result-title-index" rChildIndex="'+count+'">'+count + '. ' + index +'</span><input type="text" name="inspectPlanSummeriseName" value="'+(name || '')+'" placeholder="请输入结论名称" >' +
						'<span title="删除该巡检结论" class="fa fa-times-circle locate-right del-inspect-plan-result-'+index+'"></span>' +
					'</div>' +
					'<div class="ins-report-result-content-line" style="margin-left:20px; width: 80%; height: 70%;">' +
						'<textarea name="inspectPlanSumeriseDescrible" class="ins-report-pre-result-content-area" placeholder="请输入结论描述" style="width: 100%; height: 100px;resize:none;">'+(value || '')+'</textarea>' +
					'</div>' +
				'</div>';
				
				resultForm.find('.result-wrapper').append(what);
	
				resultForm.find('.del-inspect-plan-result-'+index).click(function(e) {
					$('.childReportCount').html(parseInt($('.childReportCount').html()) - 1);
					$(this).parent().parent().remove();
					//add by sunhailiang on 20170622
					that._mainDiv.find('.ins-report-edit-result-content').find('.ins-report-result-title-line').each(function(i){
					   $(this).find(".inspect-plan-result-title-index").html(function(){
					       return $(this).attr("rChildIndex") + '. ' + (i+1);
					   });
					});
				});
			},
			'sumarySetting' : function(that) {
				
				var count = that._templateIndex + 1; 
                that._mainDiv.find('.ins-report-title-index').html(count+". 结论");//add by sunhailiang on 20170622
				var resultForm = that._inspectResultForm = that._mainDiv.find('.inspect-plan-result-form');
//				that._mainDiv.find('.ins-report-edit-result-title').html(count + '. ' + '结论');
				that._mainDiv.find('.ins-report-edit-result-title').find('.ins-report-title-index').html(count);
				
				//加载以后的结论
				if(that.cfg.id) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/loadConclusions.htm'),
						data: {
							id: that.cfg.id
						},
						async: false,
						successMsg: null,
						success: function(data) {
							var results = data.data;
							var notNullNameList = [];
							var nullNameList = [];
							for(var s=0;s<results.length;s++) {
								var result = results[s];
								if( result.inspectPlanSummeriseName == null || result.inspectPlanSummeriseName.length == 0) {
									nullNameList.push(result);
								} else {
									if(result.inspectPlanSummeriseName == ""){
										result.inspectPlanSummeriseName ='结论';
									}
									notNullNameList.push(result);
								}
							}
							for(var i=0,len=notNullNameList.length; i<len; i++) {
								var result = notNullNameList[i];
								that._initMethods.genResult(count, i+1, that, result.inspectPlanSummeriseName, result.inspectPlanSumeriseDescrible);
								that._resultIndex = i+1+1;
							}
							$('.childReportCount').html(notNullNameList.length);
							for(var i2=0,len2=nullNameList.length; i2<len2; i2++) {
								var result = nullNameList[i2];
								that._mainDiv.find('.ins-report-pre-result-content-area-first').val(result.inspectPlanSumeriseDescrible);
							}
						}
					});
				}
			}
		},
		_buildGrid: function(that, selector, dir) {
			var tree = that._treegrid = that._mainDiv.find('.' + selector).treegrid({
				loader: function(param, success) { //获取treegrid的数据
					oc.util.ajax({
						url : oc.resource.getUrl('inspect/plan/loadItem.htm'),
						data : {catalogId: dir.id},
						async:false,
						successMsg : null,
						success : function(data) {
							if(data.code == 200) success(data.data);
						}
					});
				},
				fit: true,
			    idField: 'id',
			    treeField: 'inspectPlanItemName',
			    columns:[[
			         {
			        	 field:'inspectPlanItemName',title:'巡检项',sortable:false,width:278,align:'left',
			        	 formatter: function(value, row) {
			        		 var name=value;
			        		 if(row._parentId==undefined){//父节点
			        			 if(name.length>20){
			        				 name=name.substring(0,19)+"...";
			        			 }
			        			 return "<div title='"+value+"'>"+name+"</div>"; 
			        		 }else{
			        			 if(name!=''||name!=undefined||name!=null){
				        				if(name.length>=16){
				        					name=name.substring(0,13)+"...";
				        			 }
				        		 }
				        		 return "<div title='"+value+"'>"+name+"</div>"; 
			        		 }
			        		 
			        	 
			        	 }
			         },
			         {
			        	 field:'inspectPlanItemDescrible',title:'描述',sortable:false,width:280,align:'left',
			        	 formatter: function(value, row) {
			        		 if(!row.inspectPlanItemDescrible) {
			        			 return '';
			        		 } else {
			        			 return row.inspectPlanItemDescrible;
			        		 }
			        	 }
			         },
			         {
			        	 field:'inspectPlanItemReferencePrefix',title:'参',sortable:false,width:30,align:'right',
			        	 formatter: function(value,row) {
		        			 var prefix = (row.inspectPlanItemReferencePrefix == null || row.inspectPlanItemReferencePrefix == '') ? '' : row.inspectPlanItemReferencePrefix;
		        			 if(prefix!=''&&prefix.length > 6){
		        				 var check = prefix.substring(0,5);
		        				 return '<span title="'+prefix+'">'+check+'</span>';
		        			 }else{
		        				 return prefix;
		        			 }
			        	 }
			         },
			         {
			        	 field:'#',title:'考',sortable:false,width:20,align:'center',
			        	 formatter: function(value,row) {
			        		 if(row.children == null || row.children.length == 0) {
			        			 return ' ~ ';
			        		 } else {
			        			 return '&nbsp;&nbsp;&nbsp;';
			        		 }
			        	 }
			         },
			         {
			        	 field:'inspectPlanItemReferenceSubfix',title:'值',sortable:false,width:30,align:'left',
			        	 formatter: function(value,row) {
			        		 var suffix = (row.inspectPlanItemReferenceSubfix == null || row.inspectPlanItemReferenceSubfix == '') ? '' : row.inspectPlanItemReferenceSubfix;
			        		 var unit = (row.inspectPlanItemUnit == null ? '' : row.inspectPlanItemUnit);
			        		 if(suffix!=''&&suffix.length > 6){
		        				 var check = suffix.substring(0,6);
		        				 return '<span title="'+suffix+'">'+check+'</span>';
		        			 }else{
		        				 return suffix;
		        			 }
			        	 }
			         },
			         {
			        	 field:'inspectPlanItemUnit',title:'单位',sortable:false,width:70,align:'left',
			        	 formatter: function(value,row) {
			        		 var unit = (row.inspectPlanItemUnit == null ? '' : row.inspectPlanItemUnit);
			        		 if(unit!=''&&unit.length > 6){
		        				 var check = unit.substring(0,5);
		        				 return '<span title="'+unit+'">'+check+'</span>';
		        			 }else{
		        				 return unit;
		        			 }
			        	 }
			         },
			         {
			        	 field:'inspectPlanItemValue',title:'巡检值',sortable:false,width:80,align:'left',
			        	 formatter: function(value, row) {
			        		 
			        		 var inspectPlanItemValue = (row.inspectPlanItemValue == null ? '' : row.inspectPlanItemValue);
			        		 if(inspectPlanItemValue!=''&&inspectPlanItemValue.length > 6){
		        				 var check = inspectPlanItemValue.substring(0,5)+"...";
		        				 return '<span title="'+inspectPlanItemValue+'">'+check+'</span>';
		        			 }else{
		        				 return inspectPlanItemValue;
		        			 }
			        	 }
			         },
			         {
			        	 field:'type',title:'巡检方式',sortable:false,width:70,align:'left',
			        	 formatter: function(value, row) {
			        		 if(row.type == 1) {
			        			 return '人工手检';
			        		 } else if(row.type == 2) {
			        			 if(row.indicatorAsItem!=true){
			        				 return '系统自检';
			        			 }
			        		 }
			        	 }
			         },
			         {
			        	 field:'itemConditionDescrible',title:'情况概要',sortable:false,width:110,align:'left',
			        	 formatter: function(value,row) {
			        		 var sumary = (value != null && value != '') ? value : '';
			        		 if(sumary!=''&&sumary.length > 10){
		        				 var check = sumary.substring(0,9);
		        				 return '<span title="'+sumary+'">'+check+'</span>';
		        			 }else{
		        				 return sumary;
		        			 }
			        	 }
			         }
			     ]],
			     onLoadSuccess: function(row, data) {
			     }
			});
			that._mainDiv.find('.icon-edit-inspect-items-'+dir.id).linkbutton({
				iconCls: 'icon-edit',
				onClick: function(e) {
					that.open_managerItem(that, dir.id, dir.inspectPlanItemName, dir.inspectPlanItemDescrible, tree, that._reportName);
				}
			});
			return tree;
		},
		_loadEdit: {
			'loadId': function(that) {
				var id = that.cfg.id;
				that._mainDiv.find('.inspect-plan-add-form').find('input[name="id"]').val(id);
				that._mainDiv.find('.inspect-plan-normal-info').find('input[name="id"]').val(id);
				that._mainDiv.find('.inspect-plan-result-form').find('input[name="id"]').val(id);
			 },
			'loadBasicInfoForm': function(that) {
				oc.util.ajax({
					url : oc.resource.getUrl('inspect/plan/loadBasic.htm'),
					data : {
						id : that.cfg.id
					},
					async:false,
					successMsg : null,
					success : function(data) {
						if(data.code == 200) {
							that._inspectPlanId = data.data.id;
							if(that.cfg.type == 'add') {
								that._reportName = data.data.insepctPalnTaskName;
							}
							that._loadBaseInfo = data.data;
							//加载巡检人列表
							that._mainDiv.find('#inspectPlanInspector').combobox('loadData', data.data.users);
							that._inspectPlanBasicInfoForm.val(data.data);
							that._mainDiv.find('#inspectPlanInspector').combobox('select',data.data.inspectPlanInspector);
							userid=data.data.inspectPlanInspector;
							//加载基本信息的时候，判断当前巡检方式来显示和隐藏
							that._mainDiv.find('.basic-info-parent').removeClass('hide');
							var type = data.data.inspectPlanType;
							var lines = data.data.lines || [];
							switch(type*1) {
							case 1:
								that._mainDiv.find('.basic-info-parent').addClass('hide');
								break;
							case 2:
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide').siblings().addClass('hide');
								break;
							case 3:
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide').siblings().addClass('hide');
								that._mainDiv.find('.inspect-type-detail').removeClass('hide');
								that._mainDiv.find('.inspect-type-week').removeClass('hide');
								break;
							case 4:
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide');
								that._mainDiv.find('.inspect-type-week').addClass('hide');
								that._mainDiv.find('.inspect-type-custom').addClass('hide');
								
								that.typeEvent('month');
								
								if(lines.length > 0) {
									that._mainDiv.find('.multi-date-place').empty();
//									var md = oc.ui.multidateselect({
//										container: '.multi-date-place',
//										inputField: '.multi-date-input'
//									});
//									md.setValues(lines[0].inspectTypeMonthDate);
									that._multiDatesPicker.setValues(lines[0].inspectTypeMonthDate);
								}
								that._inspectPlanBasicInfoForm.val(lines[0]);
								break;
							case 5:
								that._mainDiv.find('.inspect-type-time-mil').addClass('hide');
								that._mainDiv.find('.inspect-type-week').addClass('hide');
								
								that.typeEvent('custom');
								
								var customDates = ((lines[0] != null && lines[0] != undefined) ? lines[0].customDate : []);
								for(var i=0,len=customDates.length; i<len; i++) {
									var date = customDates[i];
									var cdate = new moment(date).format('YYYY-MM-DD HH:mm');
									that._mainDiv.find('.icon-add-custom-date').trigger('click');
									that._mainDiv.find('.custom-item-date-counter-'+(i+1)).find('.ins-plan-custom-datetime-single').val(date);
									that._mainDiv.find('.custom-item-date-counter-'+(i+1)).find('.show-custom-date').html(cdate);
								}
								that._mainDiv.find('input[name="inspectCustomDate"]').val(customDates.toString());
								
								break;
							default:
								that._mainDiv.find('.inspect-type-time-mil').removeClass('hide');//.siblings().addClass('hide')
								that._mainDiv.find('.inspect-type-date-only').removeClass('hide');
								that._mainDiv.find('.inspect-type-year').removeClass('hide').siblings().addClass('hide');
								break;
							}
						}
					}
				});
			},
			'loadReportTemplate': function(that) {
//				that._initMethods.initNormal(that);
				that._initMethods.reloadNormalInfoItems(that);
			}
		},
		_saveMethods: {
			'saveBasicInfo': function(that) {
//				var msg = that._checkForm.basicForm(that);
//				if(msg != '') {
//					alert(msg);
//					return;
//				} else {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/updateBasic.htm'),
						data : that._inspectPlanBasicInfoForm.val(),
						async: false,
//						successMsg : '保存成功',
						success: function(data) {
							flag = data.code == 200;
							if(data.data == 1){
								alert("巡检计划名称重复");
								return;
							}else{
								alert("保存成功");
							
							}
							if (flag) {
								that._mainDiv.find('.inspect-plan-add-form').find('input[name="id"]').val(data.data);
								that._mainDiv.find('.inspect-plan-result-form').find('input[name="id"]').val(data.data);
								that._savedBaseInfo = true;
								that._inspectPlanId = data.data; //1581000,data.data;
								that.cfg.id = that._inspectPlanId;
							}
						},
					});
//				}			
			},
			'saveResult': function(that) {
				var msg = that._checkForm.resultForm(that);
				if(msg != '') {
					alert(msg);
					return;
				} else {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/updateConclusion.htm'),
						data: that.getResultParams(),
						async:false,
						successMsg : '保存成功',
						success : function(data) {
							if(data.code == 200) {
								that._savedResultInfo = true;
							}
						}
					});
				}
			},
			'saveReportName': function(that) {
				var result = 0;
				var msg = that._checkForm.reportName(that);
				if(msg != '') {
					alert(msg);
					return false;
				} else {
					var reportName = that._mainDiv.find('input[name="inspectReportName"]').val();
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/updateReportName.htm'),
						data : {
							planId: that.cfg.id,
							name: reportName
						},
						async:false,
						success : function(data) {
							result = data.data;
						}
					});
				}
				return result;
			}
		},
		_checkForm: {
			'reportName': function(that) {
				var reportName = $.trim(that._mainDiv.find('input[name="inspectReportName"]').val()||"");
				if(reportName == '') {
					return '请填写报告名称';
				} else if(reportName.length > 20){
					return '报告名称不能超过20个字符！';
				} else if((/\！|\@|\#|\￥|\%|\…|\&|\*|\“|\‘|\；|\?|\||\\|\`|\~|\<|\>/g).test(reportName)){
					return '报告名称不能出现特殊字符！';
				}
				return '';
			},
			'basicForm': function(that) {
				var form = that._mainDiv.find('.inspect-plan-add-form');
				var planName = $.trim(form.find('input[name="insepctPalnTaskName"]').val()||"");
				var type = form.find('input[name="inspectPlanType"]').val();
				var domain = form.find('input[name="inspectPlanDomain"]').val();
				var inspector = form.find('input[name="inspectPlanInspector"]').val();
				var desc = form.find('#inspect_plan_basic_desc').val();
				if (planName == '') {
					return '请填写巡检计划名称';
				} else if(planName.length > 20){
					return '巡检计划名称不能超过20个字符！';
				} else if((/\！|\@|\#|\￥|\%|\…|\&|\*|\“|\‘|\；|\?|\||\\|\`|\~|\<|\>/g).test(planName)){
					return '巡检计划名称不能出现特殊字符！';
				}
				if(desc && desc.length > 200) {
					return '描述输入长度必须介于0和200之间';
				}
				if (type == null || type == '') {
					return '请选择巡检方式';
				}
				if (domainIds == null || domain == '') {
					return '请选择域';
				} 
				if (inspector == null || inspector == '') {
					return '请选择巡检人';
				}
				var hour = form.find('input[name="inspectTypeHour"]').val();
				var minute = form.find('input[name="inspectTypeMinute"]').val();
				var hmCheck = function(hour, minute) {
					if(hour == '' || hour.length == 0) {
						return '请选择小时';
					} else if(minute == '' || minute.length == 0) {
						return '请选择分';
					} else {
						return '';
					}
				}
				//巡检类型的每月全选
				type = type*1;
				if(type == 1) {
				} else if(type == 2) {
					if('' != hmCheck(hour, minute)) {
						return hmCheck(hour, minute);
					}
				} else if(type == 3) {
					var chkboxes = form.find('.chkbox-week');
					var checked = false;
					for(var chk=0,chkLen=chkboxes.length; chk<chkLen; chk++) {
						if($(chkboxes[chk]).attr('checked') == 'checked') {
							checked = true;
							break;
						}
					}
					if(!checked) {
						return '请选择星期';
					}
					if('' != hmCheck(hour, minute)) {
						return hmCheck(hour, minute);
					}
				} else if(type == 4) {//每月
					var selectedDates = that._multiDatesPicker.getValues();
					if(!selectedDates || selectedDates.length == 0) {
						return '请选择日期';
					}
					if('' != hmCheck(hour, minute)) {
						return hmCheck(hour, minute);
					}
				} else if(type == 5) {//自定义，至少选择分钟
					var customDates = form.find('.ins-plan-custome-date-item');
					if(customDates.length == 0) {
						return '请添加自定义日期';
					}
				}
				return '';
			},
			'resultForm': function(that) {
				var resultForm = that._mainDiv.find('.inspect-plan-result-form');
				
				var firstTaVal = resultForm.find('.ins-report-pre-result-content-area-first').val();
				if(firstTaVal != undefined && firstTaVal != '' && firstTaVal.length > 200) {
					return '结论描述输入长度必须介于0和200之间';
				}
				
				var items = resultForm.find('.result-wrapper').find('.ins-report-edit-result-content');
				for(var i=0; i<items.length; i++) {
					var name = $(items[i]).find('input[name="inspectPlanSummeriseName"]').val();
					var value = $(items[i]).find('textarea').val();
					var index = $(items[i]).find('.inspect-plan-result-title-index').text();
					if(name == undefined || name == '') {
						return '请填写'+index+'结论名称'
					} else if(name.length > 20){
						return index+'结论名称不能超过20个字符';
					} else if((/\！|\@|\#|\￥|\%|\…|\&|\*|\“|\‘|\；|\?|\||\\|\`|\~|\<|\>/g).test(name)){
						return index+'结论名称不能出现特殊字符！';
					}
					if(value != undefined && value != '' && value.length > 200) {
						return '【'+index + name+'】的结论描述输入长度必须介于0和200之间';
					}
				}
				return '';
			}
		},
		open_editInspectDir: function(that) {
			var reportName = that._mainDiv.find('.ins-plan-report-name-for-display').val() || '';
			oc.resource.loadScript('resource/module/inspect-management/js/inspect_plan_add_content_detail.js',function(){
				oc.module.inspect.inspectdir.open({
					type:"add",
					id: that.cfg.id,
					reportName: reportName,
					reload: function() {
						that._initMethods.genDir(that);
						
						that._resultIndex = 1;
						that._mainDiv.find('.result-wrapper').empty();
						that._initMethods.sumarySetting(that);
					}
				});
			});
		},
		open_managerItem: function(that, dirId, dirName, dirDesc, treegridNode, reportName){
			var reportName1 = that._mainDiv.find('.ins-plan-report-name-for-display').val() || '';
			oc.resource.loadScript('resource/module/inspect-management/js/inspect_manage_item_new.js',function(){
				oc.module.inspect.items.open({
					reportName: reportName1,
					dirId: dirId,
					dirName: dirName,
					dirDesc: dirDesc,
					resourceId: that._loadNormalInfo.inspectReportResourceName,
					resourceType:that._loadNormalInfo.inspectReportResourceType,
					domainId: that._domainId,
					refreshTreegrid: function() {
						$(treegridNode).treegrid('reload'); //保存巡检项后刷新treegrid
					}
				});
			});
		},
		enablePlan: function(that) {
			var result = 0;
			oc.util.ajax({
				url : oc.resource.getUrl('inspect/plan/updateState.htm'),
				data : {
					id : that.cfg.id,
					state: true
				},
				async:false,
				successMsg : null,
				success : function(data) {
					result = data.data;
					$("input[name='inspectPlanDomain']").val("");
				}
			});
			return result;
		}
	};
	
	oc.ns('oc.module.inspect.plan');

	oc.module.inspect.plan = {
		open : function(cfg) {
			new inspectPlanDetail(cfg).open();
		},
		getProperties : function(){
			return properties;
		}
	};
})(jQuery);