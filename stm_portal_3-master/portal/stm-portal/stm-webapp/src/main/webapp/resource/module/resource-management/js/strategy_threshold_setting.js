$(function() {
	
	
	function strategyThresholdSetting(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	
	strategyThresholdSetting.prototype = {
		constructor : strategyThresholdSetting,
		cfg : undefined,
		_defaults : {},
		_tagsArray : null,
		_curOpenLevel : null,
		_conditionPanelArray : {},
		_conditionTableMaxNum : {},
		_longPanelWidth : 743,
		_shortPanelWidth : 343,
		_longInputWidth : 485,
		_shortInputWidth : 120,
		_maxConditionGroupCount : 2,
		_maxConditionCount : 5,
		_showThresholdInfo : null,
		_alarmContentParameter : null,
		_thresholdOperation : null,//所有关系操作符
		_thresholdUnaryComparisonOperation : null,//特殊关系操作符
		_thresholdConditionRation : null,//逻辑操作符
		_thresholdSingleOperations : ['HAS_CHANGED'],//只允许单个存在的操作符
		open : function(){
			var that = this;
			
			that._tagsArray = [{id:'Major',name:'严重',icon:'oc-panel-pics res-size-nav Major serious'},
			                   {id:'Minor',name:'警告',icon:'oc-panel-pics res-size-nav Minor warning'},
			                   {id:'Normal',name:'正常',icon:'oc-panel-pics res-size-nav resources Normal normal'}];
			
			//创建阈值设置内容界面
			
			var htmlContent = '<div class="add_c_head" style="height:70px"><div id="oc-threshold-nav-menu" style="width:69%;float:left;height:70px;"></div><div id="thresholdButtonId" class="thresho_bk" style="width:31%;float:left;height: 70px; "></div></div><div id="thresholdSettingDiv" style="height:85%"></div>';
			
			if(this.cfg.selector){
				//嵌入形式
				this.cfg.selector.append(htmlContent);
				//界面渲染
				that._showView();
			}else{
				//弹出框形式
				var thresholdDialog = $('<div/>').dialog({
					title: that.cfg.metricName + '-阈值设置',
					width: 760,
					height: 540,
					content:htmlContent,
					modal: true,
					buttons:[{
						text:'确定',
						iconCls:"fa fa-check-circle",
						handler:function(){
							
							if(that.cfg.readOnly){
								thresholdDialog.panel('destroy');
							}
							
							var updateData = {};
							updateData.profileId = that.cfg.profileId;
							
							//获取设置阈值的值
							var parameter = that.getThresholdValues();
							
							if(!parameter){
								return;
							}
							
							updateData.thresholdsMap = parameter;
							
							//提交修改内容
							oc.util.ajax({
								successMsg:null,
								url: oc.resource.getUrl('portal/strategydetail/updateMetricThresholdInfo.htm'),
								data:{metricString:updateData},
								success:function(data){
									
									if(data.code == 200 && data.data){
										alert('修改成功!');
										thresholdDialog.panel('destroy');
									}else{
										alert('修改失败!');
									}
									
								}
							});
							
						}
					},{
						text:'取消',
						iconCls:"fa fa-times-circle",
						handler:function(){
							//取消
							thresholdDialog.panel('destroy');
							
						}
					}],
					onBeforeOpen:function(){
						
						//界面渲染
						that._showView();
						
						return true;
					}
				});
				
				thresholdDialog.panel('body').css('padding',0);
				
			}
		
		},
		_createContent : function(level){
			
			var that = this;
			
	    	for(var i = 0 ; i < that._tagsArray.length ; i ++){

	    		if(level && level != that._tagsArray[i].id){
	    			continue;
	    		}
	    		
	    		that._conditionPanelArray[that._tagsArray[i].id] = {};
	    		$('#thresholdSettingDiv').append('<div level="' + that._tagsArray[i].id + '" class="thresholdSettingDivClass" id="settingCotent_' + that._tagsArray[i].id + '" style="display:none;height:100%;padding:15px 8px"></div>');
	    		
	    		var settingContentAlarmAddHeight = 0;
	    		
	    		//找出对应级别的阈值表达式以及告警内容
	    		var thresholdExpression = null;
	    		var alarmContentValue = null;
	    		for(var j = 0 ; j < that._showThresholdInfo.length ; j ++){
	    			if(that._showThresholdInfo[j].perfMetricStateEnum == that._tagsArray[i].id){
	    				thresholdExpression = that._showThresholdInfo[j].thresholdExpression;
	    				alarmContentValue = that._showThresholdInfo[j].alarmContent;
	    				break;
	    			}
	    		}
	    		
	    		//替换告警内容中的参数
	    		for(var j = 0 ; j < that._alarmContentParameter.length ; j ++){
	    			var alarmReg = new RegExp('\\$\\{' + that._alarmContentParameter[j].id + '\\}',"g"); 
	    			alarmContentValue = alarmContentValue.replace(alarmReg,'${' + that._alarmContentParameter[j].nodeName + '}');
	    		}
	    		
	    		if(that._tagsArray[i].id == 'Normal'){
	    			$('#settingCotent_' + that._tagsArray[i].id).append('<div id="settingContentAlarm_' +  that._tagsArray[i].id + '"></div>');
	    			$('#addConditionGroupLinkButtonId').linkbutton('disable');
	    			settingContentAlarmAddHeight = 250;
	    		}else{
	    			
	    			
	    			$('#addConditionGroupLinkButtonId').linkbutton('enable');
	    			$('#settingCotent_' + that._tagsArray[i].id).append('<div style="overflow:auto"><div id="settingContentCondition_' + that._tagsArray[i].id + '"></div></div><div id="settingContentAlarm_' +  that._tagsArray[i].id + '"></div>');
	    			
	    			if(thresholdExpression){
	    				
	    				//解析阈值表达式
	    				var group = that._analysisExpression(thresholdExpression);
	    				
	    				//条件组数量
	    				var conditionGroupNum = parseInt(group.length / 2 + 1);
	    				var panelWidth = that._longPanelWidth;
	    				var inputWidth = that._longInputWidth;
	    				if(conditionGroupNum > 1){
	    					panelWidth = that._shortPanelWidth;
	    					inputWidth = that._shortInputWidth;
	    				}
	    				//面板宽度+条件组间的条件宽度+加各个组件的间隔宽度
	    				$('#settingContentCondition_' + that._tagsArray[i].id).width(conditionGroupNum * (panelWidth + 50 + 30) - 50);
	    				for(var j = 0 ; j < conditionGroupNum ; j ++){
	    					
	    					//创建条件组
	    					var trGroup = that._analysisExpressionPerGroup(group[j * 2]);
	    					that._createConditionGroup(that._tagsArray[i].id,j,panelWidth,inputWidth,trGroup.length,trGroup);
	    					
	    					if(j != conditionGroupNum - 1){
	    						that._createConditionGroupRelation(that._tagsArray[i].id,group[j * 2 + 1]);
	    					}
	    					
	    				}
	    			}else{
	    				that._createEmptyPanel(that._tagsArray[i].id);
	    			}
	    			
	    		}
	    		
	    		var insertParameterButtonTag = '';
	    		
	    		if(!that.cfg.readOnly){
	    			insertParameterButtonTag = '<a id="insertParameterButton_' + that._tagsArray[i].id + '" style="margin-left: 600px;margin-top:-7px"></a>';
	    		}
	    		
				var disabled = '';
				if(that.cfg.readOnly){
					disabled = 'disabled="disabled"';
				}
				$('#settingContentAlarm_' + that._tagsArray[i].id).append('<div class="strategy_alert_conment">告警内容' + insertParameterButtonTag + '</div>');
				$('#settingContentAlarm_' + that._tagsArray[i].id).append('<textarea ' + disabled + ' id="settingContentAlarmTextArea_' + that._tagsArray[i].id + '" style="width: 743px;height: ' + (73 + settingContentAlarmAddHeight) + 'px;">' + alarmContentValue + '</textarea>');

		    	if(!that.cfg.readOnly){
		    		
		    		$('#insertParameterButton_' + that._tagsArray[i].id).linkbutton('RenderLB',{
		    			text:'插入参数',
		    			iconCls:"icon-insert margin5",
		    			onClick:function(){
		    				
		    				var parameterWindow = $('<div><div class="oc-toolbar datagrid-toolbar insert-btn"><button id="insertDefineStatusParameterButton"><span class="icon-insert margin5"></span>插入</button></div>' +
		    				'<div style="height:260px"><div id="parameterDatagridId"></div></div></div>');
		    				
		    				//构建dialog
		    				parameterWindow.dialog({
		    					title:"告警内容参数选择",
		    					width:330,
		    					height:360,
		    					modal:true
		    				});
		    				
		    				$('#insertDefineStatusParameterButton').on('click',function(){
		    					var checkedRow = parameterDatagrid.selector.datagrid('getChecked');
		    					if(!checkedRow || checkedRow.length <= 0){
		    						alert('请至少选择一个节点');
		    						return;
		    					}
		    					var parameterValueArray = new Array();
		    					for(var i = 0 ; i < checkedRow.length ; i ++){
		    						parameterValueArray.push('${' + checkedRow[i].nodeName + '}');
		    					}
		    					var parameterValue = parameterValueArray.join(',');
		    					that._insertParameterDefineConetnt(parameterValue);
		    					parameterWindow.dialog('close');
		    					
		    				});
		    				
		    				var parameterDatagrid = oc.ui.datagrid({
		    					pagination:false,
		    					selector:$('#parameterDatagridId'),
		    					checkOnSelect:false,
		    					selectOnCheck:false,
		    					data:that._alarmContentParameter,
		    					columns:[[
		    					          {field:'id',checkbox:true},
		    					          {field:'nodeName',title:'参数',width:80},
		    					          {field:'nodeDes',title:'参数描述',width:80}
		    					          ]]
		    				});
		    				
		    			}
		    		});
		    		
		    	}
				
	    	}
	    	
		},
		_createConditionGroup : function(levelId,index,panelWidth,inputWidth,conditionCount,initValue){
			var that = this;
			
			$('#settingContentCondition_' + levelId).append('<div id="settingContentConditionSection_'+ levelId +'_' + index + '"></div>');
			
			var deleteGroupButtonTag = '';
			
			if(!that.cfg.readOnly){
				deleteGroupButtonTag = '<a class="childitme-delete fa fa-remove locate-right conditionGroupDelete" id="conditionGroupDelete_' + levelId + '_' + index + '" tagsid="' + levelId + '" groupindex="' + index + '" style="margin-right: 10px;"></a>';
			}
			
			var conditionPanel = $('#settingContentConditionSection_' + levelId +'_' + index).panel({
				cls:"strategy_set_box",
				width:panelWidth,
				height:251,
				title:'条件组' + deleteGroupButtonTag,
				content:'<div id="settingContentConditionPanel_' + levelId + '_' + index + '"></div>'
			});
			that._conditionPanelArray[levelId][index] = conditionPanel;
			that._conditionTableMaxNum[levelId + index] = 0;
			$('#settingContentConditionPanel_' + levelId +'_' + index).append('<table id="settingContentConditionTable_' + levelId + '_' + index + '" style="border-collapse:separate;border-spacing:0px 12px;"></table>');
			//各个条件组中条件数量
			for(var x = 0 ; x < conditionCount ; x++){
				
				//添加一行条件
				if(x != 0){
					if(initValue){
						that._createConditionRow(initValue[x],levelId,index,x,inputWidth,false);
					}else{
						that._createConditionRow(null,levelId,index,x,inputWidth,false);
					}
				}else{
					if(initValue){
						that._createConditionRow(initValue[x],levelId,index,x,inputWidth,true);
					}else{
						that._createConditionRow(null,levelId,index,x,inputWidth,true);
					}
				}
				
			}
			
	    	//条件组按钮删除功能
	    	$('#conditionGroupDelete_' + levelId + '_' + index).click(function(){

	    		var conditionPanel = that._conditionPanelArray[$(this).attr('tagsid')][$(this).attr('groupindex')].panel('panel');
	    		if(conditionPanel.prev() && conditionPanel.prev().length > 0){
	    			conditionPanel.prev().remove();
	    		}else{
	    			conditionPanel.next().remove();
	    		}
	    		
	    		that._conditionPanelArray[$(this).attr('tagsid')][$(this).attr('groupindex')].panel('destroy');
	    		delete that._conditionPanelArray[$(this).attr('tagsid')][$(this).attr('groupindex')];
	    		
	    		//删除过后若只剩一个条件组，则修改宽度
	    		if(Object.getOwnPropertyNames(that._conditionPanelArray[$(this).attr('tagsid')]).length == 1){
	    			
	    			var onlyIndex = $('#settingContentCondition_' + $(this).attr('tagsid')).find('a.conditionGroupDelete:last').attr('groupindex');
	    			
	    			that._conditionPanelArray[$(this).attr('tagsid')][onlyIndex].panel('resize',{width:that._longPanelWidth});
	    			
	    			$('#settingContentConditionTable_' + $(this).attr('tagsid') + '_' + onlyIndex).find('input.thresholdValueInputClass').width(that._longInputWidth - 4);
	    			$('#settingContentConditionTable_' + $(this).attr('tagsid') + '_' + onlyIndex).find('input.thresholdValueInputClass').parent().width(that._longInputWidth + 10);
	    		}
	    		
	    		//条件组被删除完
	    		if(Object.getOwnPropertyNames(that._conditionPanelArray[$(this).attr('tagsid')]).length == 0){
	    			
	    			that._createEmptyPanel(levelId);
	    		}
	    		
	    	});
			
		},
		_createEmptyPanel : function(levelId){
			
			var that = this;
			
			//创意一个空的面板用于添加条件组
			$('#settingContentCondition_' + levelId).append('<div id="settingEmptySection_' + levelId + '"></div>');
			
			var emptyContent = '<div class="add_condition"><a id="add_condition_button_' + levelId + '"></a><p>条件为空,请添加条件</p></div>';
			var emptyTitle = '添加条件';
			if(that.cfg.readOnly){
				emptyContent = '<div class="add_condition"></div>';
				emptyTitle = '暂无条件';
			}
			
			$('#settingEmptySection_' + levelId).panel({
				cls:"strategy_set_box",
				width:this._longPanelWidth,
				height:251,
				title:emptyTitle,
				content:emptyContent
			});
			
			$('#add_condition_button_' + levelId).on('click',function(){
				that._addConditionArray();
			})
			
		},
		_createConditionGroupRelation : function(levelId,value){
			//添加条件组之间的与或关系
			var curValue = this._thresholdConditionRation[0].id;
			if(value){
				curValue = value;
			}
			$('#settingContentCondition_' + levelId).append('<div style="float:left;width:50px;margin-right:7px;margin-top:112px;padding-left:7px;"><select></select></div>');
	    	oc.ui.combobox({
				  selector:$('#settingContentCondition_' + levelId).find('select:last'),
				  width:44,
				  placeholder:false,
				  readonly:this.cfg.readOnly,
				  selected:false,
				  value:curValue,
				  data:this._thresholdConditionRation
	    	});
		},
		//levelId  tabs级别
		//groupIndex 条件组位置
		//rowIndex 条件位置
		//inputWidth input宽度
		//isFirstCondition 是否第一行
		//trObject 在指定位置追加一行的tr jquery对象
		_createConditionRow : function(initValue,levelId,groupIndex,rowIndex,inputWidth,isFirstCondition,trObject){
			
			var that = this;

			//记录每个条件组中条件行的最大行数
			if(parseInt(rowIndex) > this._conditionTableMaxNum[levelId + groupIndex]){
				this._conditionTableMaxNum[levelId + groupIndex] = parseInt(rowIndex);
			}
			
			var values = null;
			if(initValue){
				values = initValue.match(/".+"|\S*\s*/g);
			}
			
			var firstCombobox = '';
			if(!isFirstCondition){
				firstCombobox = '<select id="condition1_' + levelId + '_' + groupIndex + '_' + rowIndex + '"></select>';
			}
			
			var curComboValue2 = '>';
			
			var inputValue = '';
			if(values && isFirstCondition){
				curComboValue2 = values[1].trim();
				if(!that._estimateOperator(curComboValue2)){
					inputValue = values[2].trim();
				}
			}else if(values && !isFirstCondition){
				curComboValue2 = values[2].trim();
				if(!that._estimateOperator(curComboValue2)){
					inputValue = values[3].trim();
				}
			}
			
			if(inputValue && isNaN(inputValue)){
				inputValue = inputValue.substring(1,inputValue.length - 1);
				inputValue = inputValue.replace(/"/g,'&quot;').replace(/\\/g,'');
			}
			
			var addAndDeleteButtonTag = '';
			
			var disabled = '';
			if(!that.cfg.readOnly){
				addAndDeleteButtonTag = '<a id="addButton_' + levelId + '_' + groupIndex + '_' + rowIndex + '" class="relation-item fa fa-plus-square paddingr-10"></a><a id="deleteButton_' + levelId + '_' + groupIndex + '_' + rowIndex + '" class="childitme-delete fa fa-times-circle"></a>';
			}else{
				disabled = 'disabled="disabled"';
				inputWidth += 38;
			}

			var appendContent = '<tr><td style="padding-left:10px;width:54px">' + firstCombobox + '</td><td style="padding-left:10px"><select id="condition2_' + levelId + '_' + groupIndex + '_' + rowIndex + '"></select></td><td style="padding-left:10px;width:' + (inputWidth + 10) + 'px"><input class="thresholdValueInputClass" ' + disabled + ' style="width:' + inputWidth + 'px" type="text" value="' + inputValue + '"></input></td><td style="padding-left:10px">' + addAndDeleteButtonTag + '</td></tr>';
			
			if(!trObject){
				$('#settingContentConditionTable_' + levelId + '_' + groupIndex).append(appendContent);
			}else{
				trObject.after(appendContent);
			}
			
			if(!isFirstCondition){
				var curComboValue1 = that._thresholdConditionRation[0].id;
				if(values){
					curComboValue1 = values[0].trim();
				}
				oc.ui.combobox({
					selector:$('#condition1_' + levelId + '_' + groupIndex + '_' + rowIndex),
					width:44,
					placeholder:false,
					readonly:that.cfg.readOnly,
					selected:false,
					value:curComboValue1,
					data:that._thresholdConditionRation
				});
			}
			
			oc.ui.combobox({
				selector:$('#condition2_' + levelId + '_' + groupIndex + '_' + rowIndex),
				width:80,
				placeholder:false,
				readonly:that.cfg.readOnly,
				selected:false,
				value:curComboValue2,
				data:that._thresholdOperation,
			    onChange:function(newValue,oldValue){
			    	if(that._estimateOperator(newValue)){
						$(this).parent().next().find('input').hide();
						return;
					}
					for(var i = 0 ; i < that._thresholdUnaryComparisonOperation.length ; i ++){
						if(oldValue == that._thresholdUnaryComparisonOperation[i].id){
							$(this).parent().next().find('input').show();
							return;
						}
					}
				}
			});
			
			if(that._estimateOperator(curComboValue2)){
				$('#condition2_' + levelId + '_' + groupIndex + '_' + rowIndex).parent().next().find('input').hide();
			}
			
			$('#addButton_' + levelId + '_' + groupIndex + '_' + rowIndex).click(function(){
				if($('#settingContentConditionTable_' + levelId + '_' + groupIndex).find('tr').length == that._maxConditionCount){
					alert('各个条件组添加' + that._maxConditionCount + '个条件!');
					return;
				}
				var curInputWidth = that._shortInputWidth;
				if(Object.getOwnPropertyNames(that._conditionPanelArray[levelId]).length == 1){
					curInputWidth = that._longInputWidth;
				}
				that._createConditionRow(null,levelId,groupIndex,that._conditionTableMaxNum[levelId + groupIndex] + 1,curInputWidth,false,$(this).parents('tr:first'));
			});
			
			$('#deleteButton_' + levelId + '_' + groupIndex + '_' + rowIndex).click(function(){
				//只剩一个条件则不允许删除
				if($('#settingContentConditionTable_' + levelId + '_' + groupIndex).find('tr') && $('#settingContentConditionTable_' + levelId + '_' + groupIndex).find('tr').length == 1){
					alert('各个条件组至少保留一个条件!');
					return;
				}
				//如果删除的为第一个条件，则将下一个条件的与或判断去掉
				if($('#settingContentConditionTable_' + levelId + '_' + groupIndex).find('.childitme-delete:first').attr('id') == $(this).attr('id')){
					$(this).parents('tr:first').next().find('td:first').html('');
				}
				$(this).parents('tr:first').remove();
			});
			
		},
		_insertParameterDefineConetnt : function(content){
			parameterValue = content;
			var $t = $('#settingContentAlarmTextArea_' + this._curOpenLevel)[0];  
            if (document.selection) { // ie  
            	$('#settingContentAlarmTextArea_' + this._curOpenLevel).focus();  
                var sel = document.selection.createRange();  
                sel.text = parameterValue;  
                $('#settingContentAlarmTextArea_' + this._curOpenLevel).focus();  
                sel.moveStart('character', -l);  
                var wee = sel.text.length;  
                if (arguments.length == 2) {  
                    var l = $t.value.length;  
                    sel.moveEnd("character", wee + t);  
                    t <= 0 ? sel.moveStart("character", wee - 2 * t  
                            - parameterValue.length) : sel.moveStart(  
                            "character", wee - t - parameterValue.length);  
                    sel.select();  
                }  
            } else if ($t.selectionStart  
                    || $t.selectionStart == '0') {  
                var startPos = $t.selectionStart;  
                var endPos = $t.selectionEnd;  
                var scrollTop = $t.scrollTop;  
                $t.value = $t.value.substring(0, startPos)  
                        + parameterValue  
                        + $t.value.substring(endPos,  
                                $t.value.length);  
                $('#settingContentAlarmTextArea_' + this._curOpenLevel).focus();  
                $t.selectionStart = startPos + parameterValue.length;  
                $t.selectionEnd = startPos + parameterValue.length;  
                $t.scrollTop = scrollTop;  
                if (arguments.length == 2) {  
                    $t.setSelectionRange(startPos - t,  
                            $t.selectionEnd + t);  
                    $('#settingContentAlarmTextArea_' + this._curOpenLevel).focus();  
                }  
            } else {  
            	$('#settingContentAlarmTextArea_' + this._curOpenLevel).value += parameterValue;  
            	$('#settingContentAlarmTextArea_' + this._curOpenLevel).focus();  
            }
		},
		_addConditionArray : function(){

			var that = this;
			
			if(Object.getOwnPropertyNames(that._conditionPanelArray[that._curOpenLevel]).length >= that._maxConditionGroupCount){
				
				//最多支持5个条件组
				alert('最多支持添加' + that._maxConditionGroupCount + '个条件组!');
				return;
				
			}

			//查出当前最大条件组index
			var lastIndex = $('#settingContentCondition_' + that._curOpenLevel).find('a.conditionGroupDelete:last').attr('groupindex');
			
			//之前只有一个条件组则缩小宽度
			if(Object.getOwnPropertyNames(that._conditionPanelArray[that._curOpenLevel]).length == 1){
				
				that._conditionPanelArray[that._curOpenLevel][lastIndex].panel('resize',{width:that._shortPanelWidth});
				
				$('#settingContentConditionTable_' + that._curOpenLevel + '_' + lastIndex).find('input.thresholdValueInputClass').width(that._shortInputWidth - 4);
				$('#settingContentConditionTable_' + that._curOpenLevel + '_' + lastIndex).find('input.thresholdValueInputClass').parent().width(that._shortInputWidth + 10);
				
			}
			
			//之前没有条件组则删除空条件组
			if(Object.getOwnPropertyNames(that._conditionPanelArray[that._curOpenLevel]).length == 0){
				
				$('#settingEmptySection_' + that._curOpenLevel).panel('destroy');
				lastIndex = -1;
				
			}
			
			var newIndex = parseInt(lastIndex) + 1;
			
			//创建条件组
			var oldWidth = $('#settingContentCondition_' + that._curOpenLevel).width();
			$('#settingContentCondition_' + that._curOpenLevel).width(oldWidth + that._shortPanelWidth + 70);
			var panelWidth = that._longPanelWidth;
			var inputWidth = that._longInputWidth;
			if(parseInt(lastIndex) >= 0){
				that._createConditionGroupRelation(that._curOpenLevel);
				panelWidth = that._shortPanelWidth;
				inputWidth = that._shortInputWidth;
			}
			that._createConditionGroup(that._curOpenLevel,newIndex + "",panelWidth,inputWidth,1);
			
		},
		_analysisExpression : function(expression){
			var group = new Array();
			var reg = /\([^\(]*\)/g;
			var result = expression.match(reg);
			if(result){
				//有多个条件组
				var operators = expression.replace(reg,',').trim().replace(/\s/g,"").split(',');
				for(var i = 0 ; i < result.length ; i ++){
					group.push(result[i].replace('(','').replace(')','').trim());
					if(i != result.length - 1){
						group.push(operators[i + 1]);
					}
				}
			}else{
				//只有一个条件组
				group.push(expression.trim());
			}
			
			return group;
			
		},
		_analysisExpressionPerGroup : function(expression){
			var group = new Array();
			var value = new Array();
			for(var i = 0 ; i < this._thresholdConditionRation.length ; i ++){
				value.push(this._thresholdConditionRation[i].id);
			}
			var reg = new RegExp("( " + value.join(' | ') + " )","g");
			var result = expression.split(reg);
			if(result){
				//有多个条件组
				for(var i = 0 ; i < result.length ; i = i + 2){
					if(i - 1 >= 0){
						group.push((result[i - 1] + result[i]).trim());
					}else{
						group.push(result[i].trim());
					}
				}
			}
			return group;
		},
		_estimateOperator : function(value){
			
			for(var i = 0 ; i < this._thresholdUnaryComparisonOperation.length ; i ++){
				if(value == this._thresholdUnaryComparisonOperation[i].id){
					return true;
				}
			}
			
			return false;
			
		},
		_isSingleOperator : function(value){

			for(var i = 0 ; i < this._thresholdSingleOperations.length ; i ++){
				if(value == this._thresholdSingleOperations[i]){
					return true;
				}
			}
			
			return false;
			
		},
		updateAlarmContent : function(content){
			out : for(var i = 0 ; i < content.length ; i ++){
				for(var j = 0 ; j < this._tagsArray.length ; j ++){
					if(this._tagsArray[j].id == content[i].perfMetricStateEnum){
			    		//替换告警内容中的参数
						var alarmContentValue = content[i].alarmContent;
			    		for(var x = 0 ; x < this._alarmContentParameter.length ; x ++){
			    			var alarmReg = new RegExp('\\$\\{' + this._alarmContentParameter[x].id + '\\}',"g"); 
			    			alarmContentValue = alarmContentValue.replace(alarmReg,'${' + this._alarmContentParameter[x].nodeName + '}');
			    		}
						$('#settingContentAlarmTextArea_' + this._tagsArray[j].id).val(alarmContentValue);
						continue out;
					}
				}
			}
		},
		getThresholdValues : function(curMetricId){
			
			if(curMetricId){
				this.cfg.metricId = curMetricId;
			}
			
			var commitData = $.extend(true,[],this._showThresholdInfo);
			var that = this;
			var thresholdIsNull = true;
			
			//获取阈值条件设置和告警内容
			for(var i = 0 ; i < that._tagsArray.length ; i ++){
				
				var curThresholdIndex = 0;
				
	    		for(var j = 0 ; j < commitData.length ; j ++){
	    			if(commitData[j].perfMetricStateEnum == that._tagsArray[i].id){
	    				curThresholdIndex = j;
	    				break;
	    			}
	    		}
				
				if(that._tagsArray[i].id != 'Normal'){
					
					var thresholdValue = '';
					
					for(var j in that._conditionPanelArray[that._tagsArray[i].id]){
						
						if(Object.getOwnPropertyNames(that._conditionPanelArray[that._tagsArray[i].id]).length > 1){
							thresholdValue += ' (';
						}
						
						for(var x = 0 ; x < that._conditionPanelArray[that._tagsArray[i].id][j].find('tr').length ; x ++){
							
							var tds = $(that._conditionPanelArray[that._tagsArray[i].id][j].find('tr')[x]).find('td');
							
							var operator = $(tds[1]).find('select').combo('getValue');
							
							var textValue = '';
							if(!that._estimateOperator(operator)){
								textValue = $(tds[2]).find('input').val();
								
								if(isNaN(textValue)){
									//非数字
									textValue = textValue.replace(/\"/g,'\\"').replace(/\'/g,"\\'");
									textValue = "\"" + textValue + "\"";
								}
								
								if(!textValue || !textValue.trim()){
									alert('请将\"' + that._tagsArray[i].name + '\"级别中阈值设置的条件值输入完整!');
									return false;
								}
							}
							
							if(that._isSingleOperator(operator)){
								if(Object.getOwnPropertyNames(that._conditionPanelArray[that._tagsArray[i].id]).length > 1){
									alert('\"是否改变\"为单一条件，无法与其他条件组合，请修改\"' + that._tagsArray[i].name + '\"级别中阈值条件!');
									return false;
								}else{
									for(var y in that._conditionPanelArray[that._tagsArray[i].id]){
										if(that._conditionPanelArray[that._tagsArray[i].id][y].find('tr').length > 1){
											alert('\"是否改变\"为单一条件，无法与其他条件组合，请修改\"' + that._tagsArray[i].name + '\"级别中阈值条件!');
											return false;
										}
									}
								}
							}
							
							
							if($(tds[0]).find('select') && $(tds[0]).find('select').length > 0){
								thresholdValue += ' ' + $(tds[0]).find('select').combo('getValue') + ' ' + that.cfg.metricId + ' ' + operator + ' ' + textValue;
							}else{
								thresholdValue += ' ' + that.cfg.metricId + ' ' + operator + ' ' + textValue;
							}
							
						}
						
						if(that._conditionPanelArray[that._tagsArray[i].id][j].parent().next().find('select').length > 0){
							thresholdValue += ') ' + that._conditionPanelArray[that._tagsArray[i].id][j].parent().next().find('select').combo('getValue');
						}else{
							if(Object.getOwnPropertyNames(that._conditionPanelArray[that._tagsArray[i].id]).length > 1){
								thresholdValue += ')';
							}
						}
						
					}
					
					if(thresholdValue){
						thresholdIsNull = false;
					}
					commitData[curThresholdIndex].thresholdExpression = thresholdValue;
				}
				
				var alarmContentValue = $('#settingContentAlarmTextArea_' + that._tagsArray[i].id).val();
				if(!alarmContentValue || alarmContentValue == ''){
					alert('请填写\"' + that._tagsArray[i].name + '\"级别中告警内容!');
					return false;
				}
	    		//替换告警内容中的参数
	    		for(var j = 0 ; j < that._alarmContentParameter.length ; j ++){
	    			var alarmReg = new RegExp('\\$\\{' + that._alarmContentParameter[j].nodeName + '\\}',"g"); 
	    			alarmContentValue = alarmContentValue.replace(alarmReg,'${' + that._alarmContentParameter[j].id + "}");
	    		}
	    		
	    		commitData[curThresholdIndex].alarmContent = alarmContentValue;
	    		if(that.cfg.timelineId && that.cfg.timelineId > 0){
	    			commitData[curThresholdIndex].timelineId = that.cfg.timelineId;
	    		}
				
			}
			
			if(thresholdIsNull){
				alert('至少填写一项告警级别的阈值!');
				return false;
			}
			
			return commitData;
			
		},
		_showView : function(){
			
			  var that = this;
			
			  //获取告警内容参数
			  oc.util.ajax({
				  successMsg:null,
				  url: oc.resource.getUrl('portal/strategydetail/getAlarmContentParameter.htm'),
				  success:function(data){
					  
					  if(data.code == 200 && data.data){
						  that._alarmContentParameter = data.data;
						  
						  //获取阈值操作符
						  oc.util.ajax({
							  successMsg:null,
							  url: oc.resource.getUrl('portal/strategydetail/getProfileMetricThresholdOperation.htm'),
							  success:function(data){
								  
								  if(data.code == 200 && data.data){
									  that._thresholdOperation = data.data.binaryComparisonOperators.concat(data.data.unaryComparisonOperators);
									  that._thresholdConditionRation = data.data.logicalOperators;
									  that._thresholdUnaryComparisonOperation = data.data.unaryComparisonOperators;
									  
									  //创建内容
									  that._createContent();
									  
								      var nav = $("#oc-threshold-nav-menu").oc_nav({
								    		textField:'name',
									    	click:function(href,d,ds){
									    		
									    		that._curOpenLevel = d.id;
									    		
									    		if(d.id == 'Normal'){
									    			$('#addConditionGroupLinkButtonId').linkbutton('disable');
									    		}else{
									    			$('#addConditionGroupLinkButtonId').linkbutton('enable');
									    		}
									    		
									    		$("#oc-threshold-nav-menu").find(".nav-actived").removeClass("nav-actived");
									    		$("#oc-threshold-nav-menu").find("."+d.id).parent().parent().addClass("nav-actived");

									    		$(".thresholdSettingDivClass").each(function(){
													if($(this).attr("level") == d.id){
														$(this).css('display','block');
													}else{
														$(this).css('display','none');
													}
													
												});
									    		
									    	},
									    	background:true,
									    	datas:that._tagsArray
									    });
								    	$("#oc-threshold-nav-menu").find(".nav-page").css('display','none');
								    	
								    	if(!that.cfg.readOnly){
								    		
								    		$("#thresholdButtonId").append($('<a id="addConditionGroupLinkButtonId" style="margin-top: 20px;"></a>').linkbutton('RenderLB',{
								    			text:'添加条件组',
								    			iconCls:"fa fa-plus",
								    			onClick:function(){
								    				
								    				that._addConditionArray();
								    				
								    			}
								    		}));
								    		if(!that.cfg.selector){
								    			$("#thresholdButtonId").append($('<a style="margin-top: 20px;"></a>').linkbutton('RenderLB',{
								    				text:'恢复默认值',
								    				iconCls:"fa fa-reply-all",
								    				onClick:function(){
								    					
								    					//获取默认值内容
								    					oc.util.ajax({
								    						url: oc.resource.getUrl('portal/strategydetail/getDefaultProfileMetricThreshold.htm'),
								    						data:{strategyId:that.cfg.profileId,metricId:that.cfg.metricId,level:that._curOpenLevel},
								    						success:function(data){
								    							
								    							if(data.code == 200 && data.data){
								    								
								    								for(var attr in that._conditionTableMaxNum){
								    									if(attr.indexOf(that._curOpenLevel) > 0){
								    										delete that._conditionTableMaxNum[attr];
								    									}
								    								}
								    								
								    								for(var i = 0 ; i < that._showThresholdInfo.length ; i ++){
								    									if(that._showThresholdInfo[i].perfMetricStateEnum == that._curOpenLevel){
								    										that._showThresholdInfo[i] = data.data;
								    										break;
								    									}
								    								}
								    								
								    								$('#thresholdSettingDiv').find('div[level=' + that._curOpenLevel + ']').remove();
								    								that._createContent(that._curOpenLevel);
								    								nav._ul.find('li[data-id=' + that._curOpenLevel + ']').click();
								    								
								    							}else{
								    								alert('获取默认信息失败!');
								    							}
								    							
								    						}
								    						
								    					});
								    					
								    				}
								    			}));
								    		}else{
								    			$('#oc-threshold-nav-menu').css('width','82%');
								    			$('#thresholdButtonId').css('width','18%');
								    		}
								    		
								    	}
										
										nav._ul.find('li:first').click();
									  
								  }else{
									  alert('获取阈值操作符失败!');
								  }
								  
							  }
						  
						  });
						  
					  }else{
						  alert('获取告警内容参数失败!');
					  }
					  
				  }
			  
			  });
			
		}
	}
	oc.ns('oc.module.resourcemanagement.strategythresholdsetting');
	oc.module.resourcemanagement.strategythresholdsetting = {
		open : function(cfg) {
			var setting = new strategyThresholdSetting(cfg);
			
			var queryData = {strategyId:cfg.profileId,metricId:cfg.metricId};
			
			if(cfg.timelineId && cfg.timelineId > 0){
				queryData.timelineId = cfg.timelineId;
			}
			
			//获取阈值信息
			oc.util.ajax({
				  successMsg:null,
				  url: oc.resource.getUrl('portal/strategydetail/getProfileMetricThreshold.htm'),
				  data:queryData,
				  success:function(data){
					  
					  if(data.code == 200){
						  
						  setting._showThresholdInfo = data.data;
						  setting.open();
						  
					  }else{
						  alert('获取信息失败!');
					  }
					  
				  }
				  
			});
			
		},
		openWidthCustom : function(cfg) {
			var setting = new strategyThresholdSetting(cfg);
			
			//获取阈值信息
			oc.util.ajax({
				  successMsg:null,
				  url: oc.resource.getUrl('portal/resource/customMetric/getCustomMetricsByMetricId.htm'),
				  data:{metricId:cfg.metricId},
				  success:function(data){
					  
					  if(data.code == 200 && data.data){
						  
						  setting._showThresholdInfo = data.data.thresholdsMap;
						  setting.open();
						  
					  }else{
						  alert('获取信息失败!');
					  }
					  
				  }
				  
			});
			
		},
		addWithCumstom : function(cfg){
			
			var setting = new strategyThresholdSetting(cfg);
			//获取阈值信息
			oc.util.ajax({
				  successMsg:null,
				  url: oc.resource.getUrl('portal/resource/customMetric/getDefaultAlarmContent.htm'),
				  data:{type:'PerformanceMetric'},
				  success:function(data){
					  
					  if(data.code == 200){
						  
						  setting._showThresholdInfo = data.data;
						  setting.open();
						  
					  }else{
						  alert('获取信息失败!');
					  }
					  
				  }
				  
			});
			
			return setting;
			
		},
		updateWithCumstom : function(cfg){
			
			var setting = new strategyThresholdSetting(cfg);
			setting._showThresholdInfo = cfg.thresholdData;
			setting.open();
			return setting;
			
		}
	}
});