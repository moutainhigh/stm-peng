(function($) {
	function InspectPlanNormalInfo(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}

	InspectPlanNormalInfo.prototype = {
		_planId: undefined,
		
		cfg: undefined,
		//boolean
		_loadNormalInfo: {},
		
		_savedNormalInfo: undefined,
		
		_inspectNormalInfoForm: undefined,
		
		reportIndex: 0,
		
		getResourceName: undefined,
		
		_resourceName: '',
		_bizName: '',
		open: function() {//打开添加、编辑域弹出框
			
			
			var dlg = this._mainDiv = $('<div/>'), 
				that = this;
			that._planId = that.cfg.id
			that.getResourceName = that.cfg.getResourceName;
		
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_plan_normal_info.html'),
				title : '巡检计划 - 巡检报告常规信息设置',
				width: 890,
				height : 380,
				top: 80,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
					dlg.dialog("destroy");//add by sunhailiang on 20170615
				},
				buttons:[{
					text: '确定',
					iconCls:"fa fa-check-circle",
					handler: function(e) {
						//checkform
						var msg = that._checkForm(that);
						if(msg != '') {
							alert(msg)
						} else {
							that._saveNormalInfo(that);
							dlg.dialog('close'); 
						}
					}
				}, {
					text: '取消',
					iconCls:"fa fa-times-circle",
					handler: function(e) {
						dlg.dialog('close');
					}
				}]
			});
		},
		_defaults : {
			_planId : undefined
		},
		_mainDiv : undefined,
		_id : '#ins_plan_normal_info_setting',
		_myform:undefined,
		_init : function(dlg) {

			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			$('.addSelfItemMax').html(oc.module.inspect.plan.getProperties().basicMax);


			_myform=	oc.ui.form({
				selector:$('.inspect-plan-normal-info')
			});
			that._initInfo(that);
			that._InitReportNormalSetting(that);
			that._loadNormalInfo(that);
		},
		_loadNormalInfo: function(that) {
			oc.util.ajax({
				url : oc.resource.getUrl('inspect/plan/loadRoutineWidthResourceBiz.htm'),
				data : {
					id : that.cfg.id
				},
				async:false,
				successMsg : null,
				success : function(data) {
					if(data.code == 200) {

						that._loadNormalInfo = data.data.routineInfo;
						
						var dobj = data.data.routineInfo;
						
						that._resourceName = dobj.resourceName;
						that._bizName = dobj.businessName;
					
						oc.ui.combotree({
							selector: that._mainDiv.find('.normal-info-resource-name'),
							data: data.data.resources
						});
						that._mainDiv.find('.normal-info-biz-name').combobox('loadData', data.data.businesses);
						
						that._inspectNormalInfoForm.val(data.data.routineInfo);
						//回显时勾选多选框
						if(dobj.reportProduceTimeShow == 'on') {
							that._mainDiv.find('input[name="inspectPlanReportProduceTimeShow"]').attr('checked',true);
						}
						if(dobj.reportModifyTimeShow == 'on') {
							that._mainDiv.find('input[name="inspectPlanReportModifyTimeShow"]').attr('checked',true);
						}
						
						var resourceChkbox = that._mainDiv.find('input[name="inspectReportResourceNameShow"]')
						if(dobj.inspectReportResourceNameShow == 'on') {
							resourceChkbox.attr('checked', 'checked');
						} else {
							resourceChkbox.removeAttr('checked');
						}
						var bizChkbox = that._mainDiv.find('input[name="inspectReportBusinessNameShow"]');
						if(dobj.inspectReportBusinessNameShow == 'on') {
							bizChkbox.attr('checked', 'checked');
						} else {
							bizChkbox.removeAttr('checked');
						}

//						if(dobj.inspectReportResourceName) {
//							setTimeout(function() {
//								that._mainDiv.find('.normal-info-resource-name').combotree('setValue', dobj.inspectReportResourceName);
//							}, 100);
//						}
//						if(dobj.inspectReportBusinessName) {
//							setTimeout(function() {
//								that._mainDiv.find('.normal-info-biz-name').combobox('setValue', dobj.inspectReportBusinessName);
//							}, 100);
//						}
						//初始化页面的时候判断resourceType是否为空，不为空则保存后台传过来的值
						
//						if(dobj.inspectReportResourceType) {
//							setTimeout(function() {
//								that._mainDiv.find('.normal-info-resource-type').val(dobj.inspectReportResourceType);
//							}, 100);
//						}
						
						//iterate the added custom display control to generate customed item
						var items = data.data.routineInfo.selfItems;
						for(var i=0; i<items.length; i++) {
							that._mainDiv.find('.inspect-plan-self-item-add').trigger('click');
							
							var rows = that._mainDiv.find('.self-item-index') || [];
							if(rows && rows.length != 0) {
								switch(items[i].inspectPlanSelfItemType*1) {
								case 1:
									var _cont = '<input placeholder="请填写要显示的内容" style="width: 240px;" name="inspectPlanItemContent" type="text">';
									$(rows[i]).find('.self-item-value-'+(i+1)).removeClass('hide').html(_cont);
									break;
								case 2:
									var _cont = '<textarea class="ta-content-'+i+'" placeholder="请填写要显示的内容" name="inspectPlanItemContent">'+items[i].inspectPlanItemContent+'</textarea>';
//									var _cont = '<input class="ta-content-'+i+'" placeholder="请填写要显示的内容" name="inspectPlanItemContent" type="text" value="'+items[i].inspectPlanItemContent+'" />';
									$(rows[i]).find('.self-item-value-'+(i+1)).removeClass('hide').html(_cont);
									break;
								default:
									var _cont = '<input name="inspectPlanItemContent" type="text">';
									$(rows[i]).find('.self-item-value-'+(i+1)).html(_cont).addClass('hide');
									break;
								}
							}
							that._mainDiv.find('.self-item-'+(i+1)).find('input[name="inspectPlanSelfItemName"]').val((items[i].inspectPlanSelfItemName || '')).fixPlaceHolder();
							that._mainDiv.find('.self-item-name-'+(i+1)).combobox('setValue', (items[i].inspectPlanSelfItemType*1));
							that._mainDiv.find('.self-item-ctrl-'+(i+1)).find('input[name="inspectPlanItemContent"]').val((items[i].inspectPlanItemContent || '')).fixPlaceHolder();
//							that._mainDiv.find('.self-item-ctrl-'+(i+1)).find('.ta-content-'+ i).val((items[i].inspectPlanItemContent));
							that._mainDiv.find('.self-item-ctrl-'+(i+1)).find('textarea').val((items[i].inspectPlanItemContent)).fixPlaceHolder();
						}
					}
				}
			});
		},
		_saveNormalInfo: function(that) {
			//自定义选项数据请求封装 add by sunhailiang on 20170615
			var getSelfItemParams = function(){
            	var params = {},itemWrap = $(".self-item-wrapper",that._mainDiv.find('.inspect-plan-normal-info'));
            	$('input[name="inspectPlanSelfItemName"]',itemWrap).each(function(i){ 
            		console.info(itemWrap.find("input[name='inspectPlanItemContent']").eq(i).val());
            		console.info(itemWrap.find("textarea[name='inspectPlanItemContent']").eq(0).val());
            		count=i;
            		var num=(count-i-1);
            		params['planSelfItemsList['+i+'].inspectPlanSelfItemName'] = this.value;
            		params['planSelfItemsList['+i+'].inspectPlanSelfItemType'] = itemWrap.find("input[name='inspectPlanSelfItemType']").eq(i).val() ||"";

            		params['planSelfItemsList['+i+'].inspectPlanItemContent']  = itemWrap.find("[name='inspectPlanItemContent']").eq(i).val() ||"";

            	});
            	return params;
            };
			oc.util.ajax({//submit
				url: oc.resource.getUrl('inspect/plan/updateRoutine.htm'),//+ "?" +data,// that._mainDiv.find('.inspect-plan-normal-info').serialize(),
				data : $.extend({},_myform.val(),getSelfItemParams()), //modify by sunhailiang on 20170615
				async:false,
				successMsg : '保存成功',
				success : function(data) {
					that._savedNormalInfo = true;
					that._loadNormalInfo.inspectReportResourceName = that._mainDiv.find('input[name="inspectReportResourceName"]').val();
					var getResourceName = that.getResourceName; 
					if(getResourceName) {
						getResourceName(that._mainDiv.find('input[name="inspectReportResourceName"]').val());
					}
					var reload = that.cfg.reloadNormalInReport;
					var dir = that.cfg.reloaddir;
					var getNames = that.cfg.getNames;
					if(getNames) {
						getNames(that._resourceName, that._bizName);
					}
					if(reload) {
						reload();
					}
					if(dir){
						dir();
					}
				}
			});
		},
		_checkForm: function(that) {
			var selfWrapper = that._mainDiv.find('.self-item-wrapper');
			var items = selfWrapper.find('.self-item-index') || [];
			for(var i=0; i<items.length; i++) {
				var itemName = $(items[i]).find('input[name="inspectPlanSelfItemName"]').val();
				var itemType = $(items[i]).find('input[name="inspectPlanSelfItemType"]').val();
				var wbkVal = $.trim($(items[i]).find('input[name="inspectPlanItemContent"]').val());
				var taContent = $(items[i]).find('.textarea-content').val();
				if(itemName == undefined || itemName == '' || itemType == undefined || itemType == '') {
					return '请填写第'+(i+1)+'个自定义显示项名称';
				} else if(itemName.length > 20){
					return '第'+(i+1)+'个自定义显示项名称不能超过20个字符';
				} else if((/\！|\@|\#|\￥|\%|\…|\&|\*|\“|\‘|\；|\?|\||\\|\`|\~|\<|\>/g).test(itemName)){
					return '第'+(i+1)+'个自定义显示项名称不能出现特殊字符！';
				}
				if(itemType == 1 && (wbkVal != undefined && wbkVal != '' && wbkVal.length > 20)) {
					return '第'+(i+1)+'个自定义显示项文本框内容不能超过20个字符';
				}
				if(itemType == 2 && (taContent != undefined && taContent == '' || taContent.length == 0)) {
					return '请填写第'+(i+1)+'个自定义显示项文本显示的值';
				}
				if(itemType == 2 && (taContent.length > 20)) {
					return '第'+(i+1)+'个自定义显示项文本显示内容不能超过20个字符';
				}
			}

			var form = that._mainDiv.find('.inspect-plan-normal-info');
			
			var resourceChx = form.find('input[name="inspectReportResourceNameShow"]').prop('checked');
			var resourceSelect = form.find('input[name="inspectReportResourceName"]').val();
			var bizChx = form.find('input[name="inspectReportBusinessNameShow"]').prop('checked');
			var bizSelect = form.find('input[name="inspectReportBusinessName"]').val();
			
			if(resourceChx && resourceSelect == '' || resourceSelect == null){
				return '请选择资源类型';
			}
			
			if(bizChx && bizSelect == '' || bizSelect == null){
				return '请选择业务名称';
			}
			return '';
		},
		_initInfo: function(that) {
			that._mainDiv.find('.report-name-in-tempalte').html(that.cfg.reportName);
			that._mainDiv.find('#plan_id').val(that.cfg.id);
//			that._mainDiv.find('#plan_id').val(1581000);
		},
		_InitReportNormalSetting: function(that) {

			var selector = that._mainDiv.find('.inspect-plan-normal-info');
			var normalForm = oc.ui.form({
				selector: selector,
				combotree: [{
					  selector: '[name="inspectReportResourceName"]',
					  width:'150px',
					  placeholder:'请选择资源类型',
//					  url:oc.resource.getUrl('inspect/plan/getResourceCategoryList.htm'),
//					  filter:function(data){
//						  return data.data;
//					  },
					  onChange : function(newValue, oldValue, newObj, oldObj){
						  if(newValue != '') {
							  if(newObj && newObj.type == 0) {
								  alert('请选择具体资源类型');
								  that._mainDiv.find('input[name="inspectReportResourceName"]').val('');
							  }
							  
							  that._mainDiv.find('input[name="inspectReportResourceNameShow"]').attr('checked', 'checked');
							  if(newObj!=null&&newObj!=undefined){
								  that._mainDiv.find('input[name="inspectReportResourceType"]').val(newObj.type);
							  }
							  
		            	  } else {
		            		  that._mainDiv.find('input[name="inspectReportResourceNameShow"]').removeAttr('checked');
		            	  }
					  }
				}],					
				combobox:[
			      	{
			        	  selector: '[name="inspectReportBusinessName"]',
			        	  placeholder: '请选择业务',
			        	  width:'150px',
			        	  fit: false,
//			        	  filter: function(d) {
//			        		  return d.data;
//			        	  },
//			        	  url: oc.resource.getUrl('inspect/plan/getBusiness.htm'),
			        	  onChange: function(id) {
			        		  if(id != '') {
			        			  that._mainDiv.find('input[name="inspectReportBusinessNameShow"]').attr('checked', 'checked');
			        		  } else {
			        			  that._mainDiv.find('input[name="inspectReportBusinessNameShow"]').removeAttr('checked');
			        		  }
			        	  }
			          }
				]
			});
			that._inspectNormalInfoForm = normalForm;
			var index = 0;
			that._mainDiv.find(".inspect-plan-self-item-add").linkbutton({
				iconCls: 'fa fa-plus',
				onClick: function()	{
					var len = (normalForm.find('.self-item-index').length) || 0;
					if(len == oc.module.inspect.plan.getProperties().basicMax){
						//最多添加50个自定义显示项
						alert('最多添加' + oc.module.inspect.plan.getProperties().basicMax + '个自定义显示项');
						return;
					}
					
					index = len + 1;
					$('.currentAddSelfItem').html(index);
					var tmpIndex = index;
					
					var what = 
						'<div class="self-item-index">' +
							'<table border="0" width="100%" class="octable-form">' +
								'<tr>' +
									'<td>' +
										'<div class="self-item-'+index+'">' +
											'<label>字段显示名称：</label>' +
											'<span class="oc-valid-required-star">*</span>' +
											'<input placeholder="请填写自定义显示名称" type="text" name="inspectPlanSelfItemName" required="required" >' +
										'</div>' +
									'</td>' +
									'<td>' +
										'<div class="self-itm-type-'+index+'">' +
											'<label>字段类型：</label>' +
											'<span class="oc-valid-required-star">*</span>' +
											'<input type="text" class="self-item-name-'+index+'" name="inspectPlanSelfItemType" required="required" >' +
										'</div>' +
									'</td>' +
									'<td class="self-item-ctrl-'+index+'" style="width: 35%;"><div class="self-item-value-'+index+'"></div></td>' +
									'<td style="text-align: right;"><span title="删除该自定义显示项" class="fa fa-times-circle locate-right del-inspect-plan-self-item-'+index+'"></span></td>' +
								'</tr>' +
							'</table>' +
						'</div>';
					
					normalForm.find('.self-item-wrapper').append(what);
                    normalForm.find('.self-item-wrapper').find('.self-item-'+index).fixPlaceHolder();//add by sunhailiang on 20170630 ie9 placeholder修复 
					oc.ui.combobox({
						selector: that._mainDiv.find('.self-item-name-'+index),
						placeholder: false,
						data:[
						      {id: 1, name: '文本框'},
						      {id: 2, name: '文本显示'},
						      {id: 3, name: '日期时间选择框'}
						],
						onChange: function(id) {
							switch(id) {
							case 1:
								var _cont = '<input placeholder="请填写要显示的内容" style="width: 240px;" name="inspectPlanItemContent" type="text">';
								$(this).parents('table:first').find('.self-item-value-'+tmpIndex).removeClass('hide').html(_cont).fixPlaceHolder();
								break;
							case 2:
								var _cont = '<textarea placeholder="请填写要显示的内容" class="textarea-content" name="inspectPlanItemContent" style="max-width: 240px; height: 28px;"></textarea>';
//								var _cont = '<input type="text" placeholder="请填写要显示的内容" name="inspectPlanItemContent" style="width: 240px; height: 22px;" />';
								$(this).parents('table:first').find('.self-item-value-'+tmpIndex).removeClass('hide').html(_cont).fixPlaceHolder();
								break;
							case 3:
								var _cont = '<input name="inspectPlanItemContent" type="text">';
								$(this).parents('table:first').find('.self-item-value-'+tmpIndex).html(_cont).find('[name=inspectPlanItemContent]').datetimebox({
									width: 145,
									showSeconds: false,
									value:oc.util.dateAddDay(new Date(),-0).stringify(),
									onSelect: function(date) {
									}
								});
//								$('[name=inspectPlanItemContent]').datetimebox({
//									width: 145,
//									showSeconds: false,
//									value:oc.util.dateAddDay(new Date()).stringify(),
//									onSelect: function(date) {
//									}
//								});
								break;
								default:
							}
						}
					});

					normalForm.find('.del-inspect-plan-self-item-'+index).click(function(e) {
						$(this).parents('div:first').remove();
						$('.currentAddSelfItem').html(normalForm.find('.self-item-index').length || 0);
					});
					
					index++;
				}
			});
		}
	};
	
	oc.ns('oc.module.inspect.plan.normal.info');

	oc.module.inspect.plan.normal.info = {
		open : function(cfg) {
			new InspectPlanNormalInfo(cfg).open();
		}
	};
})(jQuery);