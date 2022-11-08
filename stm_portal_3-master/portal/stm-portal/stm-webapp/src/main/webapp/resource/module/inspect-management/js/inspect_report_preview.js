(function($) {
	function InspectReportDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}

	InspectReportDetail.prototype = {
		_reportId: undefined,
		cfg: undefined,
		_reportstatus:undefined,
		reportIndex: 0,
		_tdIndex: 0,
		_lastTr: undefined,
		
		open: function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), 
				that = this;
			that._reportId = that.cfg.id;
			that._reportstatus=that.cfg.reportstatus;
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_report_preview.html'),
				title : '报告查看页面',
				width: 1135,//modify by sunhailiang on 20170607
				height : 580,
				top: 30,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
					
				},
				buttons:that._reportstatus=="true"?[{
					text: '导出EXCEL',
					iconCls: 'fa fa-file-excel-o',//icon-excel
					handler: function(e) {
						that._export(that._reportId, 'excel');
					}
				}, {
					text: '导出WORD',
					iconCls: 'fa fa-file-word-o',//icon-word
					handler: function(e) {
						that._export(that._reportId, 'word');
					}
				}, {
					text: '导出PDF',
					iconCls: 'fa fa-file-pdf-o',//icon-pdf
					handler: function(e) {
						that._export(that._reportId, 'pdf');
					}
				}]:[]
			});
		},
		_defaults : {
			_reportId : undefined
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
		_export: function(reportId, type) {
			switch (type) {
			case "excel":
				top.location = oc.resource.getUrl('inspect/report/excel.htm?id=' + reportId);
				break;
			case "word":
				top.location = oc.resource.getUrl('inspect/report/word.htm?id=' + reportId);
				break;
			case "pdf":
				top.location = oc.resource.getUrl('inspect/report/pdf.htm?id=' + reportId);
				break;
			}
		},
		_initPreview: {
			'initBasic': function(that) {
				
				that._mainDiv.find('.export-to-excel').linkbutton('RenderLB', {
					iconCls: 'fa fa-file-excel-o',
					onClick: function() {
						that._export(that._reportId, 'excel');
					}
				});
				that._mainDiv.find('.export-to-word').linkbutton('RenderLB', {
					iconCls: 'fa fa-file-word-o',
					onClick: function() {
						that._export(that._reportId,'word');
					}
				});
				that._mainDiv.find('.export-to-pdf').linkbutton('RenderLB', {
					iconCls: 'fa fa-file-pdf-o',
					onClick: function() {
						that._export(that._reportId,'pdf');
					}
				});
				
				if(that._reportId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/report/loadBasic.htm'),
						data: {id: that._reportId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							that._mainDiv.find('.ins-pre-report-name').html(r.inspectReportName);
							
							var h = that._mainDiv.find('.ins-report-pre-general');
							var table = h.find('.rep-normal-info-list');
							var tr = null;
							
							//巡检时间
							if(that._tdIndex % 6 == 0) {
								tr = $('<tr></tr>');
								table.append(tr);
							}
							var th = $('<td class="tab-l-tittle"><span>巡检时间：</span></td>');
							var td = $('<td align="left" style="width: 20%;"><span>'+r.inspectReportProduceTime+'</span></td>');
							tr.append(th).append(td);
							that._tdIndex+=2;
							that._lastTr = tr;

							//巡检人
							if(that._tdIndex % 6 == 0) {
								tr = $('<tr></tr>');
								table.append(tr);
							}
							var th = $('<td class="tab-l-tittle"><span>巡检人：</span></td>');
							var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportInspector)+'</span></td>');
							tr.append(th).append(td);
							that._tdIndex+=2;
							that._lastTr = tr;

							//报告生成时间
							if(r.inspectReportProduceTimeShow) {
								if(that._tdIndex % 6 == 0) {
									tr = $('<tr></tr>');
									table.append(tr);
								}
								var th = $('<td class="tab-l-tittle"><span>报告生成时间：</span></td>');
								var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportProduceTime || '')+'</span></td>');
								tr.append(th).append(td);
								that._tdIndex+=2;
								that._lastTr = tr;
							}
							
							//最后编辑时间
							if(r.inspectReportModifyTimeShow) {
								if(that._tdIndex % 6 == 0) {
									tr = $('<tr></tr>');
									table.append(tr);
								}
								var th = $('<td class="tab-l-tittle"><span>最后提交时间：</span></td>');
								var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportSubmitTime || '')+'</span></td>');
								tr.append(th).append(td);
								that._tdIndex+=2;
								that._lastTr = tr;
							}
							
							//最后编辑人
							if(r.inspectReportModifiorShow) {
								if(that._tdIndex % 6 == 0) {
									tr = $('<tr></tr>');
									table.append(tr);
								}
								var th = $('<td class="tab-l-tittle"><span>最后编辑人：</span></td>');
								var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportInspector || '')+'</span></td>');
								tr.append(th).append(td);
								that._tdIndex+=2;
								that._lastTr = tr;
							}
							
							//计划创建人
							if(r.inspectReportTaskCreator) {
								if(that._tdIndex % 6 == 0) {
									tr = $('<tr></tr>');
									table.append(tr);
								}
								var th = $('<td class="tab-l-tittle"><span>计划创建人：</span></td>');
								var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportTaskCreator || '')+'</span></td>');
								tr.append(th).append(td);
								that._tdIndex+=2;
								that._lastTr = tr;
							}
							
							//资源名称
							if(r.inspectReportResourceName) {
								if(that._tdIndex % 6 == 0) {
									tr = $('<tr></tr>');
									table.append(tr);
								}
								var th = $('<td class="tab-l-tittle"><span>资源名称：</span></td>');
								var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportResourceName || '')+'</span></td>');
								tr.append(th).append(td);
								that._tdIndex+=2;
								that._lastTr = tr;
							}

							//业务名称
							if(r.inspectReportBusinessName) {
								if(that._tdIndex % 6 == 0) {
									tr = $('<tr></tr>');
									table.append(tr);
								}
								var th = $('<td class="tab-l-tittle"><span>业务名称：</span></td>');
								var td = $('<td align="left" style="width: 20%;"><span>'+(r.inspectReportBusinessName || '')+'</span></td>');
								tr.append(th).append(td);
								that._tdIndex+=2;
								that._lastTr = tr;
							}
						}
					});
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/report/loadRoutine.htm'),
						data: {id: that._reportId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							var h = that._mainDiv.find('.ins-report-pre-general');
							var table = h.find('.rep-normal-info-list');
							var tr = that._lastTr;
							//显示自定义显示项
							if(r) {
								var items = r;
								for(var i=0,len=items.length; i<len; i++) {
									var item = items[i];
									
									if(item.inspectReportSelfItemType == 1) {
										
										if(that._tdIndex % 6 == 0) {
											tr = $('<tr></tr>');
											table.append(tr);
										}
										var th = $('<td class="tab-l-tittle"><span>'+item.inspectReportSelfItemName+'：</span></td>');
										var td = $('<td align="left" style="width: 20%;"><span>'+(item.inspectReportItemContent || '')+'</span></td>');
										tr.append(th).append(td);
										that._tdIndex+=2;
									} else if(item.inspectReportSelfItemType == 2) {
										if(that._tdIndex % 6 == 0) {
											tr = $('<tr></tr>');
											table.append(tr);
										}
										var th = $('<td class="tab-l-tittle"><span>'+item.inspectReportSelfItemName+'：</span></td>');
										var td = $('<td align="left" style="width: 20%;">'+(item.inspectReportItemContent || '')+'</td>');
										tr.append(th).append(td);
										that._tdIndex+=2;
									} else if(item.inspectReportSelfItemType==3) {
										if(that._tdIndex % 6 == 0) {
											tr = $('<tr></tr>');
											table.append(tr);
										}
										var th = $('<td class="tab-l-tittle"><span>'+item.inspectReportSelfItemName+'：</span></td>');
										var td = $('<td align="left" style="width: 20%;"><span>'+(item.inspectReportItemContent || '')+'</span></td>');
										tr.append(th).append(td);
										that._tdIndex+=2;
									}
								}
							}
						}
					});
				}
				
				
			},
			'initNormal': function(that) {
				if(that._reportId) {
					
				}
			},
			'initContent': function(that) {//查询巡检目录，并生成目录下的巡检项与巡检子项
				if(that._reportId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/report/loadInspectionItems.htm'),
						data: {id: that._reportId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							
							var dom = that._mainDiv.find('.ins-report-pre-dir');
							//var dirsDom = dom.find('.ins-report-pre-all-dirs');
							for(var i=0,len=r.length; i<len; i++) {
								var dir = r[i];
								var dirDom = '<div class="ins-report-pre-itera-dir-'+i+'" style="overflow: hidden;">' +
												'<div class="ins-report-pre-dir-name oc-poptitle">'+(i+1)+'. '+dir.inspectReportItemName+'：'+dir.inspectReportItemDescrible+'</div>' +
												'<table class="ins-report-pre-treegird-'+i+'" style="height: 100%;"></table>' +
											'</div>';
								dom.append(dirDom); //modify by sunhailiang on 20170607
								that.reportIndex = (i+1);
								var node = that._mainDiv.find('.ins-report-pre-itera-dir-'+i).find('.ins-report-pre-treegird-'+i);
								var dirId = dir.id;
								that._buildGrid(node, dirId, that, i);
							}
						}
					});
				}
			},
			'initResult': function(that) {//查询巡检结论
				if(that._reportId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/report/loadConclusions.htm'),
						data: {id: that._reportId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							var root = that._mainDiv.find('.ins-report-pre-result');
							var contWrapper = root.find('.ins-report-pre-result-content');
							var title = root.find('.ins-report-pre-result-title');
							title.html((++that.reportIndex)+'. 结论');
							
							var rIndex = that.reportIndex;
							
							var ec = 0;
							for(var i=0,len=r.length; i<len; i++) {
								var t = r[i];
								if(t.inspectReportSummeriseName == '' || t.inspectReportSummeriseName == null) {
									that._mainDiv.find('.ins-report-pre-result-content-area-firstone').val(t.inspectReportSumeriseDescrible || '');
								} else {
									that._genResult(rIndex, (ec+1), that, t.inspectReportSummeriseName || '', t.inspectReportSumeriseDescrible || '');
									ec++;
								}
							}
							
						}
					});
				}
			}
		},
		_genResult: function(count, index, that, name, value) {
			var what = '<div class="inspect-rpt-pre-rlt-blk">' +
						'<div class="oc-poptitle">' +
							'<span>'+count + '. ' + index + ' ' + (name || '') + '</span>' +
						'</div>' +
						'<div class="ins-report-result-content-line">' +
							'<textarea readonly="readonly" name="inspectPlanSumeriseDescrible">'+(value || '')+'</textarea>' +
						'</div>' +
					'</div>';
			
			that._mainDiv.find('.ins-report-pre-result-content').append(what);
		},
		_buildGrid: function(node, dirId, that, index) {
			//var length = 0; 
			node.treegrid({
				loader: function(param, success) { //获取treegrid的数据
					oc.util.ajax({
						url : oc.resource.getUrl('inspect/report/loadItem.htm'),
						data : {catalogId: dirId},
						async:false,
						successMsg : null,
						success : function(data) {
							if(data.code == 200) {
								/*if(data.data.length){
									length = data.data.length;
									that._mainDiv.find('.ins-report-pre-itera-dir-'+index).height(data.data.length * 50);
								}*/
								success(data.data);
							}
						}
					});
				},
				fit: true,
			    idField: 'id',
			    treeField: 'inspectReportItemName',
			    columns:[[
			         {field:'inspectReportItemName',title:'巡检项',sortable:false,width:250,align:'left',
			        	 formatter: function(value,row) {
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
			         {field:'inspectReportItemDescrible',title:'描述',sortable:false,width:210,align:'center'},
			         {field:'inspectReportItemReferenceSubfix',title:'参考值|单位',sortable:false,width:80,align:'center',
			        	 formatter: function(value,row,index) {
			        		 //单位
		        			 var unit = (row.inspectReportItemUnit == null || row.inspectReportItemUnit == '' || row.inspectReportItemUnit == undefined) ? '' : row.inspectReportItemUnit;
		        			 //前缀
		        			 var prefix = (row.reportItemReferencePrefix == null || row.reportItemReferencePrefix == '') ? '' : row.reportItemReferencePrefix;
		        			 //后缀
		        			 var suffix = (row.reportItemReferenceSubfix == null || row.reportItemReferenceSubfix == '') ? '' : row.reportItemReferenceSubfix;
		        			 var returnValue;
		        			 if(prefix == '' && suffix == '') {
		        				 returnValue = unit;
		        			 } else {
		        				 returnValue = prefix + ' ~ ' + suffix + unit;
		        			 }
		        			 return '<span title="'+returnValue+'">'+returnValue+'</span>';
			        	 }
			         },
			         {
			        	 field:'inspectReportItemValue',title:'巡检值',sortable:false,width:140,align:'center',
			        	 editor: {
			        		 type: 'text'
			        	 },
			        	 formatter: function(value,row,index) {
			        		 //单位
			        		 var unit = (row.inspectReportItemUnit == null || row.inspectReportItemUnit == '' || row.inspectReportItemUnit == undefined) ? '' : row.inspectReportItemUnit;
			        		 var value = (row.inspectReportItemValue == null  || row.inspectReportItemValue == '')?'':row.inspectReportItemValue;
			        		 var returnValue = value+unit;
			        		 if(value.length > 6){
			        			 returnValue = value.substring(0,5)+"...";
			        		 }
		        			 return '<span title="'+value+unit+'">' +returnValue+'</span>';
			        	 }
			         },
			         {
			        	 field:'type',title:'巡检类型',sortable:false,width:70,align:'center',
			        	 formatter: function(value,row,index) {
			        		 if(row.type) {
			        			 if(row.type*1 == 1) {
			        				 return '人工手检';
			        			 } else if(row.type*1 == 2) {
			        				 if(row.indicatorAsItem!=true){
				        				 return '系统自检';
				        			 }
			        			 }
			        		 }
			        	 }
			         },
			         {field:'reportItemConditionDescrible',title:'情况概要',sortable:false,width:256,align:'center', 
			        	 formatter: function(value,row,index) {
			        		 var value = row.reportItemConditionDescrible || '';
			        		 if(value.length > 20){
			        		 	value = value.substring(0,20)+"...";
			        		 }
		        			 return '<span title="'+((row.reportItemConditionDescrible ) || '')+'">' +value+'</span>';
			        	 }
			         },
			         {field:'inspectReportItemResult',title:'结果',sortable:false,width:120,align:'center', 
			        	 formatter: function(value,row,index) {
			        		if(row.inspectReportItemResult && row.inspectReportItemValue != '--'){
			        			return '正常';
			        		}else{
			        			return '<span style="color:red">异常</span>';
			        		}
			        	 }
			         }
			     ]],
			     onLoadSuccess: function(data) {}
			});
			
		}
};
	
	oc.ns('oc.module.inspect.report.preview');

	oc.module.inspect.report.preview = {
		open : function(cfg) {
			new InspectReportDetail(cfg).open();
		}
	};
})(jQuery);