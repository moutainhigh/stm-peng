(function($) {
	function InspectReportPreview(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}

	InspectReportPreview.prototype = {
		_planId: undefined,
		cfg: undefined,
		
		reportIndex: 0,
		
		open: function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), 
				that = this;
			that._planId = that.cfg.id;
			
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_plan_report_preview.html'),
				title : '巡检报告预览',
				width: 1065,
				height : 618,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
					dlg.dialog("destroy");//add by sunhailiang on 20170615
				}
			});
		},
		_defaults : {
			_planId : undefined
		},
		_mainDiv : undefined,
		_id : '#inspect_report_pre_main',
		_init : function(dlg) {
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			that._initPreview.initBasic(that);
			that._initPreview.initNormal(that);
			that._initPreview.initContent(that);
			that._initPreview.initResult(that);
			
		},
		/*_export: function(planId, type) {
			oc.util.ajax({
				url : oc.resource.getUrl(''),
				data : {
					id: planId,
					type: type
				},
				async:false,
				successMsg : null,
				success : function(data) {
					
				}
			});
		},*/
		_initPreview: {
			'initBasic': function(that) {
				
				that._mainDiv.find('.export-to-excel').linkbutton('RenderLB', {
					iconCls: 'icon-excel',
					onClick: function() {
						//that._export(that._planId, 'excel');
					}
				});
				that._mainDiv.find('.export-to-word').linkbutton('RenderLB', {
					iconCls: 'icon-word',
					onClick: function() {
						//that._export('word');
					}
				})
				that._mainDiv.find('.export-to-pdf').linkbutton('RenderLB', {
					iconCls: 'icon-pdf',
					onClick: function() {
						//that._export('pdf');
					}
				});
				
				if(that._planId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/loadBasic.htm'),
						data: {id: that._planId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							that._mainDiv.find('.ins-pre-report-name').html(r.insepctPalnTaskName+'报告');
						}
					});
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/loadRoutine.htm'),
						data: {id: that._planId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							var h = that._mainDiv.find('.ins-report-pre-general');
							//显示系统默认项
							if(r.reportProduceTimeShow && r.reportProduceTimeShow == 'on') {
								h.append('<div class="locate-left"><span>报告生成时间：'+new moment().format('YYYY年MM月DD日 HH时mm分ss秒')+'</span></div>');
							}
							if(r.reportModifyTimeShow && r.reportModifyTimeShow == 'on') {
								h.append('<div class="locate-left"><span>&nbsp;&nbsp;&nbsp;&nbsp;最后编辑时间：'+new moment().format('YYYY年MM月DD日 HH时mm分ss秒')+'</span></div>');
							}
							if(r.inspectPlanReportModifiorShow && r.inspectPlanReportModifiorShow == 'on') {
								h.append('<div class="locate-left"><span>&nbsp;&nbsp;&nbsp;&nbsp;最后编辑人姓名：'+oc.index.getUser().name+'</span></div>')
							}
							if(r.inspectReportResourceName && r.inspectReportResourceName == 'on') {
								h.append('<div class="locate-left"><span>&nbsp;&nbsp;&nbsp;&nbsp;资源类型名称：'+(r.inspectReportResourceName || '')+'</span></div>');
							}
							if(r.inspectReportBusinessName && r.inspectReportBusinessName == 'on') {
								h.append('<div class="locate-left"><span>&nbsp;&nbsp;&nbsp;&nbsp;业务名称：'+(r.inspectReportBusinessName || '')+'</span></div>');
							}
							//显示自定义显示项
							if(r.selfItems) {
								var items = r.selfItems;
								for(var i=0,len=items.length; i<len; i++) {
									var item = items[i];
									var c = '<div class="locate-left"> ' +
												'<span>&nbsp;&nbsp;&nbsp;&nbsp;'+(item.inspectPlanSelfItemName || '')+'：';
									if(item.inspectPlanSelfItemType == 1) {
										c += '</span><input type="text" value="'+(item.inspectPlanItemContent || '')+'" >';
									} else if(item.inspectPlanSelfItemType == 2) {
										c += '</span><textarea>'+(item.inspectPlanItemContent || '')+'</textarea>';
									} else if(item.inspectPlanSelfItemType) {
										c += '</span><input class="if-datebox-'+i+'" >';
									}
										c += '</div>';
									h.append(c);
									that._mainDiv.find('.if-datebox-'+i).datetimebox({
										required: true
									});
								}
							}
						}
					});
				}
				
				
			},
			'initNormal': function(that) {
				if(that._planId) {
					
				}
			},
			'initContent': function(that) {//查询巡检目录，并生成目录下的巡检项与巡检子项
				if(that._planId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/loadInspectionItems.htm'),
						data: {id: that._planId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							
							var dom = that._mainDiv.find('.ins-report-pre-dir');
							var dirsDom = dom.find('.ins-report-pre-all-dirs');
							for(var i=0,len=r.length; i<len; i++) {
								var dir = r[i];
								var dirDom = '<div class="ins-report-pre-itera-dir-'+i+'" style="overflow: hidden;">' +
												'<span class="ins-report-pre-dir-name locate-left" style="font-weight:bold;">'+(i+1)+'. '+dir.inspectPlanItemName+'：'+dir.inspectPlanItemDescrible+'</span>' +
												'<table class="ins-report-pre-treegird-'+i+'" style="height: 100%;"></table>' +
											'</div>';
								dirsDom.append(dirDom);
								that.reportIndex = (i+1);
								var node = that._mainDiv.find('.ins-report-pre-itera-dir-'+i).find('.ins-report-pre-treegird-'+i);
								var dirId = dir.id;
								that._buildGrid(node, dirId, that);
							}
						}
					});
				}
			},
			'initResult': function(that) {//查询巡检结论
				if(that._planId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/plan/loadConclusions.htm'),
						data: {id: that._planId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							var root = that._mainDiv.find('.ins-report-pre-result');
							var contWrapper = root.find('.ins-report-pre-result-content-area');
							var title = root.find('.ins-report-pre-result-title');
							title.html((++that.reportIndex)+'. 结论');
							var rIndex = that.reportIndex;
							var tmp = '';
							//iterate the added conclusion and generate it
							for(var i=0,len=r.length; i<len; i++) {
								var t = r[i];
								tmp += rIndex + '.' + (i+1) + ' ' + t.inspectPlanSummeriseName + '：' + t.inspectPlanSumeriseDescrible + '\r';
								
							}
							contWrapper.val(tmp);
						}
					});
				}
			}
		},
		_buildGrid: function(node, dirId, that) {
			node.treegrid({
				loader: function(param, success) { //获取treegrid的数据
					oc.util.ajax({
						url : oc.resource.getUrl('inspect/plan/loadItem.htm'),
						data : {catalogId: dirId},
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
			        	 field:'inspectPlanItemName',title:'巡检项',sortable:false,width:230,align:'left',
			        	 formatter: function(value, row) {
			        		 if(!row.inspectPlanItemName || row.inspectPlanItemName == 'undefined') {
			        			 return '';
			        		 } else {
			        			 return row.inspectPlanItemName;
			        		 }
			        	 }
			         },
			         {
			        	 field:'inspectPlanItemDescrible',title:'描述',sortable:false,width:200,align:'left',
			        	 formatter: function(value, row) {
			        		 if(row.inspectPlanItemDescrible == 'null' || row.inspectPlanItemDescrible == 'undefined') {
			        			 return '';
			        		 } else {
			        			 return row.inspectPlanItemDescrible;
			        		 }
			        	 }
			         },
			         {field:'inspectPlanItemReferencePrefix',title:'参考值',sortable:false,width:150,align:'center',
			        	 formatter: function(value,row) {
			        		 if(row.edit) {
			        			 var unit = (row.inspectPlanItemUnit == null || row.inspectPlanItemUnit == '' || row.inspectPlanItemUnit == undefined) ? '' : row.inspectPlanItemUnit;
			        			 var prefix = (row.inspectPlanItemReferencePrefix == null || row.inspectPlanItemReferencePrefix == '') ? '' : row.inspectPlanItemReferencePrefix;
			        			 var suffix = (row.inspectPlanItemReferenceSubfix == null || row.inspectPlanItemReferenceSubfix == '') ? '' : row.inspectPlanItemReferenceSubfix;
			        			 return prefix + ' ~ ' + suffix + unit;
			        		 }
			        	 }
			         },
			         {field:'inspectPlanItemValue',title:'巡检值',sortable:false,width:150,align:'center',
			        	 formatter: function(value,row) {
			        		 var inspectValue = (row.inspectPlanItemValue != null && row.inspectPlanItemValue != '') ? row.inspectPlanItemValue : '';
			        		 return '';
			        	 }
			         },
			         {field:'itemConditionDescrible',title:'情况概要',sortable:false,width:225,align:'left', 
			        	 formatter: function(value,row) {
			        		 var sumary = (value != null && value != '') ? value : '';
			        		 return sumary;
			        	 }
			         },
			         {field:' ',title:'结果',sortable:false,width:100,align:'center', 
			        	 formatter: function(value,row) {
			        		 return '';
			        	 }
			         }
			     ]],
			     onLoadSuccess: function(data) {
			     }
			});
		}
	};
	
	oc.ns('oc.module.inspect.report.preview');

	oc.module.inspect.report.preview = {
		open : function(cfg) {
			new InspectReportPreview(cfg).open();
		}
	};
})(jQuery);