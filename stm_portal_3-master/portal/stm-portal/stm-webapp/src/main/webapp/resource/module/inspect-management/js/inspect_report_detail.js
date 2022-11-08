var inspect_report_div;
var dlg;
var globalitem = [];
(function($) {
	function InspectReportDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}

	InspectReportDetail.prototype = {
		_reportId: undefined,
		cfg: undefined,
		
		reportIndex: 0,
		
		_treegrid: undefined,
		_treegridArray: [],
		_dlg: undefined,
		
		_tdIndex: 0,
		_lastTr: undefined,
		
		open: function() {//打开添加、编辑域弹出框
			dlg = this._mainDiv = $('<div/>'), 
				that = this;
			that._reportId = that.cfg.id;
			that._dlg = dlg;
			
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_report_detail.html'),
				title : '报告编辑页面',
				width: 1135,//modify by sunhailiang on 20170607
				height : 580,
				top: 30,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
				   dlg.dialog('destroy');//modify by sunhailiang on 20170614 弹窗关闭同时应该销毁	
				},
				buttons : [{
					text: '提交',
					handler: function() {
						var curUser = oc.index.getUser();
						var reportInspectorId = that.cfg.inspectorId;
						if(curUser.id != reportInspectorId) {
							alert('你没有该报告的提交权限');
						} else {
							oc.util.ajax({
								url: oc.resource.getUrl('inspect/report/loadBasic.htm'),
								data: {id: that._reportId},
								async: false,
								successMsg: null,
								success: function(data) {
									var r = data.data;
									if(r && r.editTime && r.editTime != null && r.editTime != ""){
										oc.util.ajax({
											url : oc.resource.getUrl('inspect/report/updateState.htm'),
											data : {
												id: that.cfg.id
											},
											async:false,
											successMsg : '提交成功',
											errorMsg : '提交失败',
											success : function(data) {
												if(data.code == 200 && data.data > 0) {
                                                    updateInfo();
													var refresh = that.cfg.refresh;
													if(refresh) {
														refresh();
													}
												}
											}
										});
										dlg.dialog('close');
									}else{
										oc.ui.confirm('是否确认提交？', function() {
											oc.util.ajax({
												url : oc.resource.getUrl('inspect/report/updateState.htm'),
												data : {
													id: that.cfg.id
												},
												async:false,
												successMsg : '提交成功',
												errorMsg : '提交失败',
												success : function(data) {
													if(data.code == 200 && data.data > 0) {
                                                        updateInfo();
														var refresh = that.cfg.refresh;
														if(refresh) {
															refresh();
														}
													}
												}
											});
											dlg.dialog('close');
										});
									}
								}
							});
                            //增加保存信息的操作
                            function updateInfo() {
                                var msg1 = that._checkMethods.checkBasic(that);
                                var msg2 = that._checkMethods.checkResult(that);
                                if(msg1 != '') {
                                    alert(msg1);
                                    return;
                                }
                                if(msg2 != '') {
                                    alert(msg2);
                                    return;
                                }
                                if(!that._checkMethods.checkContent(that)){
                                    return;
                                }
                                that._updateMethods.updateNormal(that);
                                that._updateMethods.updateContent(that);
                                that._updateMethods.updateResult(that);
                                that._updateMethods.updateEditDate(that);
                            }
                            //
						}
					}
				},{
					text: "保存",
					handler: function(){
						//check length of self items and result
						var msg1 = that._checkMethods.checkBasic(that);
						var msg2 = that._checkMethods.checkResult(that);
						if(msg1 != '') {
							alert(msg1);
							return;
						}
						if(msg2 != '') {
							alert(msg2);
							return;
						}
						if(!that._checkMethods.checkContent(that)){
							return;
						}
						
						
						//save basic info
//						that._updateMethods.updateBasic(that);
						//save normal info
						that._updateMethods.updateNormal(that);
						//save content
						that._updateMethods.updateContent(that);
						//save result
						that._updateMethods.updateResult(that);
						that._updateMethods.updateEditDate(that);
						//close dialog
						dlg.dialog('close');
					}
				},{
					text: "取消",
					handler:function(){
						dlg.dialog('close');
					}
				}]
			});
		},
		_defaults : {
			_reportId : undefined
		},
		_mainDiv : undefined,
		_edit : undefined,
		_id : '#inspect_report_edit_main',
		_init : function(dlg) {
			var that = this;
			inspect_report_div=that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			//设置form表单中报告id
			that._initId(that);
			
			//初始化各部分
			that._initEdit.initBasic(that);
			that._initEdit.initNormal(that);
			that._initEdit.initContent(that);
			that._initEdit.initResult(that); 
		},
		_initId: function(that) {
			var id = that._reportId;
			that._mainDiv.find('#basic_report_id').val(id);
			that._mainDiv.find('#normal_report_id').val(id);
			that._mainDiv.find('#result_report_id').val(id);
		},
		_export: function(reportId, type) {
			oc.util.ajax({
				url : oc.resource.getUrl(''),
				data : {
					id: reportId,
					type: type
				},
				async:false,
				successMsg : null,
				success : function(data) {
				}
			});
		},
		
		_initEdit: {
			'initBasic': function(that) {

				if(that._reportId) {
					oc.util.ajax({
						url: oc.resource.getUrl('inspect/report/loadBasic.htm'),
						data: {id: that._reportId},
						async: false,
						successMsg: null,
						success: function(data) {
							var r = data.data;
							that._edit = r.edit;
							that._mainDiv.find('.ins-edit-report-name').html("巡检报告名称："+r.inspectReportName);

							var h = that._mainDiv.find('.ins-report-edit-general');
							var table = h.find('.rep-normal-info-list');
							var tr = null;
							
							//巡检时间
							if(that._tdIndex % 6 == 0) {
								tr = $('<tr></tr>');
								table.append(tr);
							}
							var th = $('<td class="tab-l-tittle"><span>巡检时间：</span></td>');
							var td = $('<td><span>'+r.inspectReportProduceTime+'</span></td>');
							tr.append(th).append(td);
							that._tdIndex+=2;
							that._lastTr = tr;

							//巡检人
							if(that._tdIndex % 6 == 0) {
								tr = $('<tr></tr>');
								table.append(tr);
							}
							var th = $('<td class="tab-l-tittle"><span>巡检人：</span></td>');
							var td = $('<td><span>'+(r.inspectReportInspector || '')+'</span></td>');
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
								var td = $('<td><span>'+(r.inspectReportProduceTime || '')+'</span></td>');
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
								var th = $('<td class="tab-l-tittle"><span>最后编辑时间：</span></td>');
								var td = $('<td><span>'+(r.editTime || '')+'</span></td>');
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
								var td = $('<td><span>'+(r.inspectReportInspector || '')+'</span></td>');
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
								var td = $('<td><span>'+(r.inspectReportTaskCreator || '')+'</span></td>');
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
								var td = $('<td><span>'+(r.inspectReportResourceName || '')+'</span></td>');
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
								var td = $('<td><span>'+(r.inspectReportBusinessName || '')+'</span></td>');
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
							var h = that._mainDiv.find('.ins-report-edit-general');
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
										var th = $('<td class="tab-l-tittle"><span class="validation-rpt-input-title">'+item.inspectReportSelfItemName+'：</span><input type="hidden" name="id" value="'+item.id+'"><input type="hidden" name="inspectReportSelfItemName" value="'+(item.inspectReportSelfItemName || '')+'"></td>');
										var td = $('<td><input type="hidden" name="inspectReportSelfItemType" value="1" /><input class="validation-rpt-input-value" style="width: 150px;" name="inspectReportItemContent" type="text" value="'+(item.inspectReportItemContent || '')+'" ></td>');
										tr.append(th).append(td);
										that._tdIndex+=2;
									} else if(item.inspectReportSelfItemType == 2) {
										if(that._tdIndex % 6 == 0) {
											tr = $('<tr></tr>');
											table.append(tr);
										}
										var th = $('<td class="tab-l-tittle"><span>'+item.inspectReportSelfItemName+'：</span><input type="hidden" name="id" value="'+item.id+'"><input type="hidden" name="inspectReportSelfItemName" value="'+(item.inspectReportSelfItemName || '')+'"></td>');
										var td = $('<td><input type="hidden" name="inspectReportSelfItemType" value="2" /><textarea style="resize:none" name="inspectReportItemContent" /></textarea></td>');
										td.find('[name=inspectReportItemContent]').val(item.inspectReportItemContent);
										tr.append(th).append(td);
										that._tdIndex+=2;
									} else if(item.inspectReportSelfItemType==3) {
										if(that._tdIndex % 6 == 0) {
											tr = $('<tr></tr>');
											table.append(tr);
										}
										var th = $('<td class="tab-l-tittle"><span>'+item.inspectReportSelfItemName+'：</span><input type="hidden" name="id" value="'+item.id+'"><input type="hidden" name="inspectReportSelfItemName" value="'+(item.inspectReportSelfItemName || '')+'"></td>');
										var td = $('<td align="left"><input type="hidden" name="inspectReportSelfItemType" value="3" /><input class="textarea-content if-datebox-'+i+'" value="'+(item.inspectReportItemContent || '')+'" style="width: 150px;" name="inspectReportItemContent" ></td>');
										tr.append(th).append(td);
										that._tdIndex+=2;
										that._mainDiv.find('.if-datebox-'+i).datetimebox({showSeconds: false });
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
							
							var dom = that._mainDiv.find('.ins-report-edit-dir');
							//var dirsDom = dom.find('.ins-report-edit-all-dirs');
							for(var i=0,len=r.length; i<len; i++) {
								var dir = r[i];
								globalitem[i]=dir.inspectReportItemName;
								var dirDom = '<div class="ins-report-edit-itera-dir-'+i+' treegrid-flag-for-count " style="overflow: hidden;">' +
												'<div class="ins-report-edit-dir-name oc-poptitle">'+(i+1)+'. '+dir.inspectReportItemName+'：'+dir.inspectReportItemDescrible+'</div>' +
												'<table class="ins-report-edit-treegird-'+i+'" style="height: 100%;"></table>' +
											'</div>';
								dom.append(dirDom);//modify by sunhailiang on 20170607
								that.reportIndex = (i+1);
								var dirId = dir.id;
								
								var selector = 'ins-report-edit-treegird-'+i;
								that._buildGrid(dirId, that, selector,i);
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
							var root = that._mainDiv.find('.ins-report-edit-result');
							var title = root.find('.ins-report-edit-result-title');
							var rIndex = that.reportIndex;
							title.html((++rIndex)+'. 结论');
							
							var ec = 0;
							for(var i=0,len=r.length; i<len; i++) {
								var t = r[i];
								if(t.inspectReportSummeriseName == '' || t.inspectReportSummeriseName == null) {
									that._mainDiv.find('.the-first-result-id').val(t.id);
									that._mainDiv.find('.ins-report-edit-result-content-area-firstone').val(t.inspectReportSumeriseDescrible || '');
								} else {
									that._genResult(rIndex, (ec+1), that, (t.inspectReportSummeriseName || ''), (t.inspectReportSumeriseDescrible || ''), t.id);
									ec++;
								}
							}

						}
					});
				}
			}
		},
		_genResult: function(count, index, that, name, value, id) {
			var what = '<div class="inspect-rpt-edit-rlt-blk">' +
						'<div class="oc-poptitle">' +
							'<span class="inspect-rpt-edit-rlt-pre">'+count + '. ' + index + ' ' + (name || '') + '</span><input type="hidden" name="id" value="'+id+'" />' +
						'</div>' +
						'<div class="ins-report-result-content-line">' +
							'<textarea name="inspectReportSumeriseDescrible" placeholder="请输入结论描述">'+(value || '')+'</textarea>' +
						'</div>' +
					'</div>';
			
			that._mainDiv.find('.ins-report-edit-result-content').append(what);
		},
	
		_buildGrid: function(dirId, that, selector,index) {
			oc.util.ajax({
				url : oc.resource.getUrl('inspect/report/loadItem.htm'),
				data : {catalogId: dirId},
				async:false,
				successMsg : null,
				success : function(data) {
					
					var dataTreeData = null;
					if(data.code == 200){
						dataTreeData = data.data;
						/*if(data.data.length){
							that._mainDiv.find('.ins-report-edit-itera-dir-' + index).height(data.data.length * 50);
						}*/
					}else{
						dataTreeData = new Array();
					}
					
					var tree = that._mainDiv.find('.' + selector).treegrid({
						data:dataTreeData,
						fit: true,
						fitColumns:true,
						pagination:false,
					    idField: 'id',
					    treeField: 'inspectReportItemName',
					    columns:[[
					         {
					        	 field:'inspectReportItemName',title:'巡检项',sortable:false,width:250,align:'left',
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
					         {
					        	 field:'inspectReportItemDescrible',title:'描述',sortable:false,width:210,align:'center'
					         },
					         {
					        	 field:'reportItemReferencePrefix',title:'参考值',sortable:false,width:80,align:'center',
					        	 formatter: function(value,row) {
				        			 var unit = (row.inspectReportItemUnit == null || row.inspectReportItemUnit == '') ? '' : row.inspectReportItemUnit;
				        			 var prefix = (row.reportItemReferencePrefix == null || row.reportItemReferencePrefix == '') ? '' : row.reportItemReferencePrefix;
				        			 var suffix = (row.reportItemReferenceSubfix == null || row.reportItemReferenceSubfix == '') ? '' : row.reportItemReferenceSubfix;
				        			 if(row.children == null) { //是巡检子项，才显示参考值
				        			 }
				        			 if(unit!='' && unit!=null){
				        				 if(unit=="ms" || unit=="毫秒" || unit=="s" || unit=="秒" 
				        						|| unit=="Bps" || unit=="bps" || unit=="比特/秒"
				        							|| unit=="KB/s" || unit=="Byte" || unit=="KB" || unit=="MB"){
				        					 var values=unitTransform(row.inspectReportItemValue, unit).split("-");
						        			 unit=values[1];
				        					}
				        			
				        			 }
				        			
				        			 var returnValue = prefix + ' ~ ' + suffix + unit;
				        			 return '<span title="'+returnValue+'" class="p_value_span" prefix="'+prefix+'" suffix="'+suffix+'" unit="'+unit+'">'+(prefix + ' ~ ' + suffix + unit)+'</span>';
					        	 }
					         },
					         {
					        	 field:'inspectReportItemValue',title:'巡检值',sortable:false,width:140,align:'center',
					        	 editor: {
					        		 type: 'text'
					        	 },
					        	 formatter: function(value, row) {
					        		 var unit = (row.inspectReportItemUnit == null || row.inspectReportItemUnit == '') ? '' : row.inspectReportItemUnit;
					        		 var value = ((unitTransform(row.inspectReportItemValue, unit) ) || '');
					        		 var values=unitTransform(row.inspectReportItemValue, unit).split("-");
					        		 value=values[0]+unit;
					        		 if(value.length >6){
					        		 	value = value.substring(0,5)+"...";
					        		 }
					        		 
				        			 return '<span title="'+(values[0] + unit|| '')+'">' +value+'</span>';
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
					         {
					        	 field:'reportItemConditionDescrible',title:'情况概要',sortable:false,width:256,align:'center',
					        	 editor: {
					        		 type: 'text'
					        	 },
					        	 formatter: function(value,row) {
				        			 var value = row.reportItemConditionDescrible || '';
					        		 if(value.length > 20){
					        		 	value = value.substring(0,20)+"...";
					        		 }
				        			 return '<span title="'+((row.reportItemConditionDescrible ) || '')+'">' +value+'</span>';
					        	 }
					         },
					         {
					        	 field:'inspectReportItemResult',title:'结果',sortable:false,width:120,align:'center',
					        	 formatter: function(value,row) {
					        		 if(row.inspectReportItemResult && row.inspectReportItemValue != '--') {
					        			 return '<div style="text-align: left; margin-left: -3px;"><input type="radio" id="'+row.id+'" class="ins-report-result-ok-'+row.id+'" name="inspectReportItemResult'+row.id+'" checked="checked" value="true" onclick="_changeColor(1,'+row.id+')"><span>正常</span>' +
					        			 '<input type="radio" id="'+row.id+'" class="ins-report-result-notok-'+row.id+'" name="inspectReportItemResult'+row.id+'" value="false" onclick="_changeColor(2,'+row.id+')"><span id="span-'+row.id+'">异常</span></div>';
					        		 } else {
					        			 return '<div style="text-align: left; margin-left: -3px;"><input type="radio" id="'+row.id+'" class="ins-report-result-ok-'+row.id+'" name="inspectReportItemResult'+row.id+'" value="true" onclick="_changeColor(1,'+row.id+')"><span>正常</span>' +
					        			 '<input type="radio" id="'+row.id+'" class="ins-report-result-notok-'+row.id+'" name="inspectReportItemResult'+row.id+'" checked="checked" value="false" onclick="_changeColor(2,'+row.id+')"><span id="span-'+row.id+'" style="color:red">异常</span></div>';
					        		 }
					        	 }
					         }
					     ]],
					     onLoadSuccess: function(row, data) {
					     },
						  onDblClickRow : function(row){
							  	 var edit = that._edit;
							  	 if(row.type==1){
							  		 that._mainDiv.find('.' + selector).treegrid('beginEdit', row.id);
							  	 }
							  	 else if((row.type==2&&edit==true)){
						    		 that._mainDiv.find('.' + selector).treegrid('beginEdit', row.id);
						    		 var root = that._mainDiv.find('.' + selector).siblings('.datagrid-view2:first');
						    		 var trs = root.find('.datagrid-btable').find('tr') || [];
						    		 trs.each(function(i, t) {
						    			 if($(this).attr("node-id")==row.id){
						    				 $(this).find('td').eq(3).find('input:first').change(function(){
				    							 var tr = $(this).parent().parent().parent().parent().parent().parent().parent();
				    							 var span = tr.find("td[field=reportItemReferencePrefix]").find(".p_value_span");
				    							 var p = span.attr("prefix");
				    							 var s = span.attr("suffix");
				    							 if(p != "" || s != ""){
				    								 var v = $(this).val();
				    								 var rodios = tr.find("td[field=inspectReportItemResult]").find("input[type=radio]");
				    								 if(s == ""){
				    									 if(!isNaN(p) && !isNaN(v) && parseFloat(v) >= parseFloat(p)){//是数字
				    										 $(rodios[0]).attr("checked","checked");
				    									 }else if(isNaN(p) && isNaN(v) && v == p){
				    										 $(rodios[0]).attr("checked","checked");
				    									 }else{
				    										 $(rodios[1]).attr("checked","checked");
				    									 }
				    								 }else if(p == ""){
				    									 if(!isNaN(s) && !isNaN(v) && parseFloat(v) <= parseFloat(s)){//是数字
				    										 $(rodios[0]).attr("checked","checked");
				    									 }else if(isNaN(s) && isNaN(v) && v == s){
				    										 $(rodios[0]).attr("checked","checked");
				    									 }else{
				    										 $(rodios[1]).attr("checked","checked");
				    									 }
				    								 }else if(p != "" && s != "" && ((parseFloat(v) >= parseFloat(p) && parseFloat(v) <= parseFloat(s)) || (v == p || v == s))){
				    									 if(!isNaN(p) && !isNaN(s) && !isNaN(v) && parseFloat(v) >= parseFloat(p) && parseFloat(v) <= parseFloat(s)){//是数字
				    										 $(rodios[0]).attr("checked","checked");
				    									 }else if(v == p || v == s){
				    										 $(rodios[0]).attr("checked","checked");
				    									 }else{
				    										 $(rodios[1]).attr("checked","checked");
				    									 }
				    								 }else{
				    									 $(rodios[1]).attr("checked","checked");
				    								 }
												 
				    								 if($(rodios[0]).attr("checked")=="checked"){
				    									 $(rodios[1]).next().css("color","white");
				    								 }
				    								 if($(rodios[1]).attr("checked")=="checked"){
				    									 $(rodios[1]).next().css("color","red");
				    								 }
				    							 }
				    						 });
						    			 }
			    					 });
						    	}else{
						    		alert("不能编辑此项!");
						    	}
						  }
					});
					
				}
			});
			
		},
		_updateMethods: {
			'updateBasic': function(that) {
				oc.util.ajax({
					url: oc.resource.getUrl('inspect/report/updateBasic.htm'),
					data: oc.ui.form({selector : that._mainDiv.find('.report-save-basic-form')}).val() || {},//modify by sunhailiang on 2017.6.5  that._mainDiv.find('.report-save-basic-form').serialize()
					async: false,
					successMsg: '',
					errorMsg: '更新基本信息出错',
					success: function(data) {
					}
				});
			},
			'updateEditDate': function(that) {
				oc.util.ajax({
					url: oc.resource.getUrl('inspect/report/updateEditDate.htm'),
					data: {"id":that._reportId},
					async: false,
					successMsg: '',
					errorMsg: '更新编辑时间出错',
					success: function(data) {
					}
				});
			},
			'updateNormal': function(that) {
				/*自定义选项参数封装 add by sunhailiang on 20170615*/
				var getSelfItemsParams = function(){
				   var params = {};
				   $('input[name="id"]',that._mainDiv.find('.report-save-normal-info-form')).each(function(i){
				       params['reportSelfItemsList['+i+'].id'] = this.value;
				       params['reportSelfItemsList['+i+'].inspectReportSelfItemType'] = that._mainDiv.find("input[name='id'][value='"+this.value+"']").parent().next().find("input[name='inspectReportSelfItemType']").val() ||"";
					   params['reportSelfItemsList['+i+'].inspectReportSelfItemName'] = that._mainDiv.find("input[name='id'][value='"+this.value+"']").parent().find("input[name='inspectReportSelfItemName']").val() ||"";
					   params['reportSelfItemsList['+i+'].inspectReportItemContent'] = that._mainDiv.find("input[name='id'][value='"+this.value+"']").parent().next().find("[name='inspectReportItemContent']").val() ||"";
/*
				       params['reportSelfItemsList['+i+'].inspectReportSelfItemType'] = that._mainDiv.find("input[name='inspectReportSelfItemType']").eq(i).val() ||"";
					   params['reportSelfItemsList['+i+'].inspectReportSelfItemName'] = that._mainDiv.find("input[name='inspectReportSelfItemName']").eq(i).val() ||"";
					   params['reportSelfItemsList['+i+'].inspectReportItemContent'] = that._mainDiv.find("input[name='inspectReportItemContent']").eq(i).val() ||"";
*/
				   });
				   return params;
				};

				oc.util.ajax({
					url: oc.resource.getUrl('inspect/report/updateNormal.htm'),
					data: getSelfItemsParams(),
					async: false,
					errorMsg: '更新常规信息出错',
					success: function(data) {
					}
				});
			},
			'updateContent': function(that) {
				//取得所有的treegrid
				//modify by sunhailiang on 20170607
				var trees = that._mainDiv.find('.ins-report-edit-dir').find('.treegrid-flag-for-count') || [];
				//遍历每个treegrid,执行endEdit
				for(var i=0,len01=trees.length; i<len01; i++) {
					
					//获取treegrid最新的数据
					var updatedData = that._mainDiv.find('.ins-report-edit-treegird-'+i).treegrid('getData');
					//获取结果的值
					if(updatedData) {
						//执行ajax请求，保存最新数据
						oc.util.ajax({
							url: oc.resource.getUrl('inspect/report/updateItems.htm'),
							data: {
								items: JSON.stringify(updatedData)
							},
							async: false,
							errorMsg: '更新巡检内容出错',
							success: function(data) {
							}
						});
					}
				}
			},
			'updateResult': function(that) {
				/*结论描述参数封装 add by sunhailiang on 20170615*/
		        var  getResultParams = function(){ 
					var params = {};  
					$('input[name="id"]',that._mainDiv.find('.report-save-result-info-form')).each(function(i){   
					   params['reportResultsList['+i+'].id'] = this.value;
					   params['reportResultsList['+i+'].inspectReportSumeriseDescrible'] = that._mainDiv.find("textarea[name='inspectReportSumeriseDescrible']").eq(i).val() ||"";
					});  
					  
		            return params;
				};

				oc.util.ajax({
					url: oc.resource.getUrl('inspect/report/updateResult.htm'),
					data: getResultParams(),
					async: false,
					errorMsg: '更新巡检结论出错',
					successMsg: '保存成功',
					success: function(data) {
					}
				});
			}
		},
		_checkMethods: {
			'checkBasic': function(that) {
				var normalTb = that._mainDiv.find('.rep-normal-info-list');
//				var title = normalTb.find('input[name="inspectReportSelfItemName"]').val();
//				var wbk = normalTb.find('input[name="inspectReportItemContent"]').val();
				var titles = normalTb.find('.validation-rpt-input-title');
				var wbks = normalTb.find('.validation-rpt-input-value');
				for(var i=0,len=wbks.length; i<len; i++) {
					var wbk = $(wbks[i]).val();
					var title = $(titles[i]).text();
					if(title.indexOf('：') != -1) {
						title = title.substring(0, title.indexOf('：'));
					}
					if(wbk != undefined && wbk != '' && wbk.length > 20) {
						return '【'+ title +'】' + '内容不能超过20个字符';
					}
				}
				return '';
			},
			'checkResult': function(that) {
				var resultForm = that._mainDiv.find('.report-save-result-info-form');
				var defaultTa = resultForm.find('.ins-report-edit-result-content-area-firstone').val();
				if(defaultTa != undefined && defaultTa != '' && defaultTa.length > 200) {
					return '结论描述不能超过200个字符';
				}
//				var otherTa = resultForm.find('.ins-report-result-content-line').find('textarea').val();
//				if(otherTa != undefined && otherTa != '' && otherTa.length > 200) {
//					return '结论描述不能超过200个字符';
//				}
				var addedRlts = resultForm.find('.inspect-rpt-edit-rlt-blk');
				for(var i=0,len=addedRlts.length; i<len; i++) {
					var block = $(addedRlts[i]);
					var ta = block.find('textarea').val();
					if(ta != undefined && ta != '' && ta.length > 200) {
						var index = block.find('.inspect-rpt-edit-rlt-pre').text();
						return '【'+index+'】' + '结论描述不能超过200个字符';
					}
				}
				return '';
				
			},
			'checkContent':function(that){
				var result = true;
				//modify by sunhailiang on 20170607
				var trees = that._mainDiv.find('.ins-report-edit-dir').find('.treegrid-flag-for-count') || [];
				//遍历每个treegrid,执行endEdit
				for(var i=0,len01=trees.length; i<len01; i++) {
					var items = that._mainDiv.find('.ins-report-edit-treegird-'+i).treegrid('getData');
					if(items && items.length != 0) {	
						for(var m=0,len02=items.length; m<len02; m++) {
							var ud = items[m];
							var checkEvent = function(ud) {
								/*
								var ok = that._mainDiv.find('.ins-report-result-ok-'+ud.id);
								var notok = that._mainDiv.find('.ins-report-result-notok-'+ud.id);
								if(ok.attr('checked') == 'checked') {
									ud.inspectReportItemResult = true;
								} else if(notok.attr('checked') == 'checked') {
									ud.inspectReportItemResult = false;
								}
								 */
								//modify by sunhailiang on 20170609
								ud.inspectReportItemResult = that._mainDiv.find('input[type="radio"][name="inspectReportItemResult'+ud.id+'"]').prop("checked");
								
								if(ud.children && ud.children.length != 0) {
									for(var xx=0,lenxx=ud.children.length; xx<lenxx; xx++) {
										checkEvent(ud.children[xx]);
									}
								}
							}
							checkEvent(ud);
						}
						
						var loop = function(items) {
							for(var j=0,len03=items.length; j<len03; j++) {
								var item = items[j];
								that._mainDiv.find('.ins-report-edit-treegird-'+i).treegrid('endEdit', item.id);
								if(item.children && item.children.length != 0) {
									loop(item.children);
								}
							}
						}
						loop(items);
					}
					//获取treegrid最新的数据
					var updatedData = that._mainDiv.find('.ins-report-edit-treegird-'+i).treegrid('getData');
					var catlog = globalitem[i];
					for(var k = 0; k< updatedData.length;k++){
						var item = updatedData[k];
						var rootname = item.inspectReportItemName;
						var value = item.inspectReportItemValue;
						var desc = item.reportItemConditionDescrible;
						if(value!=null&&value!=''&&value!=undefined&&value.length>500){
							alert("章节:"+catlog+"的根巡检项:"+rootname+"的巡检值不能超过500个字符");
							result = false;
							return result;
						}
						if(desc!=null&&desc!=''&&desc!=undefined&&desc.length>500){
							alert("章节:"+catlog+"的根巡检项:"+rootname+"的情况概要不能超过500个字符");
							result = false;
							return result;
						}
						var childItem = item.children;
						if(childItem&&childItem.length>0){
							for(var j = 0; j<childItem.length;j++){
								var child = childItem[j]
								var name = child.inspectReportItemName;
								var value = child.inspectReportItemValue;
								var desc = child.reportItemConditionDescrible;
								if(value!=null&&value!=''&&value!=undefined&&value.length>500){
									alert("章节:"+catlog+"的设备:"+rootname+"巡检项:"+name+"的巡检值不能超过500个字符");
									result = false;
									return result;
								}
								if(desc!=null&&desc!=''&&desc!=undefined&&desc.length>500){
									alert("章节:"+catlog+"的设备:"+rootname+"巡检项:"+name+"的情况概要不能超过500个字符");
									result = false;
									return result;
								}
							}
						}
					}
				}
				return result;
			}	
		}
	};
	
	oc.ns('oc.module.inspect.report.detail');

	oc.module.inspect.report.detail = {
		open : function(cfg) {
			new InspectReportDetail(cfg).open();
		}
	};
})(jQuery);

//-- 单位转换方法开始 --
function unitTransform(value,unit){
	var str;
	var valueTemp;
	if(null==value){
		return '--';
	}
	if(isNaN(value)){
		valueTemp = new Number(value.trim());	
	}else{
		valueTemp = new Number(value);
	}
		
	if(isNaN(valueTemp)){
		return value + "-" +unit;
	}
	switch(unit){ 
	case "ms":
	case "毫秒":
		str = transformMillisecond(valueTemp);
		break;
	case "s":
	case "秒":
		str = transformSecond(valueTemp);
		break;
	case "Bps":
		str = transformByteps(valueTemp);
		break;
	case "bps":
	case "比特/秒":
		str = transformBitps(valueTemp);
		break;
	case "KB/s":
		str = transformKBs(valueTemp);
		break;
	case "Byte":
		str = transformByte(valueTemp);
		break;
	case "KB":
		str = transformKB(valueTemp);
		break;
	case "MB":
		str = transformMb(valueTemp);
		break;
	default:
		str = value + "-"+unit;
		break;
	}
	return str;
	
}
function transformMillisecond(milliseconds){
	var millisecond = milliseconds;
	var seconds = 0, second = 0;
	var minutes = 0, minute = 0;
	var hours = 0, hour = 0;
	var day = 0;
	if(milliseconds > 1000){
		millisecond = milliseconds % 1000;
		second = seconds = (milliseconds - millisecond) / 1000;
	}
	if(seconds > 60){
		second = seconds % 60;
		minute = minutes = (seconds - second) / 60;
	}
	if(minutes > 60){
		minute = minutes % 60;
		hour = hours = (minutes - minute) / 60;
	}
	if(hours > 24){
		hour = hours % 24;
		day = (hours - hour) / 24;
	}
	var sb = "";
	sb = day > 0 ? (sb+=(day + "-天")) : sb;
	sb = hour > 0 ? (sb+=(hour + "-小时")) : sb;
	sb = minute > 0 ? (sb+=(minute + "-分")) : sb;
	sb = second > 0 ? (sb+=(second + "-秒")) : sb;
	sb = millisecond > 0 ? (sb+=(millisecond + "-毫秒")) : sb;
	sb = ""==sb ? (sb+=(millisecond + "-毫秒")) : sb;
	return sb;
}

function transformSecond(seconds){
	var second = seconds;
	var minutes = 0, minute = 0;
	var hours = 0, hour = 0;
	var day = 0;
	if(seconds > 60){
		second = seconds % 60;
		minute = minutes = (seconds - second) / 60;
	}
	if(minutes > 60){
		minute = minutes % 60;
		hour = hours = (minutes - minute) / 60;
	}
	if(hours > 24){
		hour = hours % 24;
		day = (hours - hour) / 24;
	}
	var sb = "";
	sb = day > 0 ? (sb+=(day + "-天")) : sb;
	sb = hour > 0 ? (sb+=(hour + "-小时")) : sb;
	sb = minute > 0 ? (sb+=(minute + "-分")) : sb;
	sb = second > 0 ? (sb+=(second + "-秒")) : sb;
	sb = ""==sb.toString() ? (sb+=(second + "-秒")) : sb;
	return sb;
}

function transformByteps(bpsNum){
	var sb = "";
	var precision = 2;
	if(bpsNum > 1000 * 1000 * 1000){
		sb+=(bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "-GBps";
	}else if(bpsNum > 1000 * 1000){
		sb+=(bpsNum / (1000 * 1000)).toFixed(precision) + "-MBps";
	}else if(bpsNum > 1000){
		sb+=(bpsNum / 1000).toFixed(precision) + "-KBps";
	}else{
		sb+=bpsNum.toFixed(precision) + "-Bps";
	}
	return sb;
} 
function transformBitps(bpsNum){
	var sb = "";
	var precision = 2;
	if(bpsNum > 1000 * 1000 * 1000){
		sb+=(bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "-Gbps";
	}else if(bpsNum > 1000 * 1000){
		sb+=(bpsNum / (1000 * 1000)).toFixed(precision) + "-Mbps";
	}else if(bpsNum > 1000){
		sb+=(bpsNum / 1000).toFixed(precision) + "-Kbps";
	}else{
		sb+=bpsNum.toFixed(precision) + "-bps";
	}
	return sb;
} 

function transformKBs(KBsNum){
	var sb = "";
	var precision = 2;
	if(KBsNum > 1024 * 1024){
		sb+=(KBsNum / (1024 * 1024)).toFixed(precision) + "-GB/s";
	}else if(KBsNum > 1024){
		sb+=(KBsNum / 1024).toFixed(precision) + "-MB/s";
	}else{
		sb+=KBsNum.toFixed(precision) + "-KB/s";
	}
	return sb;
}

function transformByte(byteNum){
	var sb = "";
	var precision = 2;
	
	if(byteNum > 1024 * 1024 * 1024){
		sb+=(byteNum / (1024 * 1024 * 1024)).toFixed(precision) + "-GB";
	}else if(byteNum > 1024 * 1024){
		sb+=(byteNum / (1024 * 1024)).toFixed(precision) + "-MB";
	}else if(byteNum > 1024){
		sb+=(byteNum / 1024).toFixed(precision) + "-KB";
	}else{
		sb+=byteNum.toFixed(precision) + "-Byte";
	}
	return sb;
}

function transformKB(KBNum){
	var sb = "";
	var precision = 2;
	
	if(KBNum > 1024 * 1024){
		sb+=(KBNum / (1024 * 1024)).toFixed(precision) + "-GB";
	}else if(KBNum > 1024){
		sb+=(KBNum / 1024).toFixed(precision) + "-MB";
	}else{
		sb+=KBNum.toFixed(precision) + "-KB";
	}
	return sb;
}

function transformMb(mbNum){
	var sb = "";
	var precision = 2;
	
	if(mbNum > 1024){
		sb+=(mbNum / 1024).toFixed(precision) + "-GB";
	}else{
		sb+=mbNum.toFixed(precision) + "-MB";
	}
	return sb;
}
//-- 单位转换方法结束 --

function  _changeColor(type,id){
	if(type==1){
		inspect_report_div.find("#span-"+id).css("color","#666");
		return;
	}
	if(type==2){
		inspect_report_div.find("#span-"+id).css("color","red");
		return;
	}
	
};