$(function(){
		
		//命名空间
		oc.ns('oc.module.bizmanagement.status.define');
		
		var insertParameters = null;
		
		var curBizId = null;
		
		var oldType = 0;
		
		var oldStatusDefine = '';
		
		var newStatusDefine = '';
		
		function open(){
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getCanvasStatusDefine.htm'),
				data:{bizId:curBizId},
				timeout:null,
				success:function(data){
					if(data.code == 200){
						if(data.data && data.data != '' && data.data.trim() != ''){
							show(1,data.data);
							oldStatusDefine = data.data;
							oldType = 1;
						}else{
							show(0);
							oldType = 0;
						}
					}
				}
			});
		}
		
		function show(type,statusDefine){

			if(type == 1){
				$('#biz_status_define_content').val(statusDefine);
			}
			var bizSerStatusDiv  = $("#business-service_status").accordion({
				fit : true,
				border : true,
				selected:type,
				onSelect:function(title,index){
					index==0?$("input[name='status_radio']").eq(0).attr("checked","checked"):
						$("input[name='status_radio']").eq(1).attr("checked","checked");
					
					
					if(index == 1 && insertParameters == null){
						oc.util.ajax({
							url:oc.resource.getUrl('portal/business/service/getBizStatusDefineParameter.htm'),
							data:{bizId:curBizId},
							timeout:null,
							success:function(data){
								if(data.code == 200){
									insertParameters = data.data;
								}
							}
						});
					}
				}
			});
			$("input[name='status_radio']").each(function(){
				var self = $(this);
				self.on('click',function(e){
					e.stopPropagation();
					if($(this).is(":checked")){
						$("#business-service_status").accordion('select',$(this).val()==0?0:1);
					}
				})
			});
			
    		$('#insertStatusDefineTextArea').on('click',function(){
    			showStatusDefineParameter();
    		});
			
    		var weightCommboArray = new Array();
    		
			function showStatusDefineParameter(){
				var parameterWindow = $('<div><div class="oc-toolbar datagrid-toolbar insert-btn">选择节点：<input id="queryDefineStatusParameterInput" type="text" style="margin-right:10px;width:360px;" placeholder="请输入名称"/><button id="queryDefineStatusParameterButton"><span class=" fa fa-search margin5" style="top:5px;"></span>查询</button>' +
						'<button id="insertDefineStatusParameterButton"> <span class="icon-insert margin5"></span>插入</button></div><div style="height:320px"><div id="parameterDatagridId"></div></div></div>');

				//构建dialog
				parameterWindow.dialog({
				  title:"选择参数",
				  width:600,
				  height:420,
				  modal:true
				});
				$('#queryDefineStatusParameterButton').on('click',function(){
					var queryContent = $('#queryDefineStatusParameterInput').val();
					var allowAddParameters = new Array();
					if(queryContent && queryContent != ''){
						for(var i = 0 ; i < insertParameters.length ; i ++){
							if(insertParameters[i].nodeName.toUpperCase().indexOf(queryContent.toUpperCase()) < 0){
								continue;
							}
							if(insertParameters[i].type == 0){
								allowAddParameters.push(insertParameters[i]);
							}
						}
					}else{
						for(var i = 0 ; i < insertParameters.length ; i ++){
							if(insertParameters[i].type == 0){
								allowAddParameters.push(insertParameters[i]);
							}
						}
					}
					var localData = {"data":{"total":0,"rows":allowAddParameters},"code":200};
					 weightCommboArray = new Array();
					parameterDatagrid.selector.datagrid('loadData',localData);
				
				});
				
	    		$('#insertDefineStatusParameterButton').on('click',function(){
	    			var checkedRow = parameterDatagrid.selector.datagrid('getChecked');
					if(!checkedRow || checkedRow.length <= 0){
						alert('请至少选择一个节点');
						return;
					}
					var parameterValueArray = new Array();
					for(var i = 0 ; i < checkedRow.length ; i ++){
						var selectWeight = weightCommboArray[parameterDatagrid.selector.datagrid('getRowIndex',checkedRow[i])].combobox('getValue');
						if(selectWeight > 1){
							parameterValueArray.push(selectWeight + '*${' + checkedRow[i].nodeName + '}');
						}else{
							parameterValueArray.push('${' + checkedRow[i].nodeName + '}');
						}
					}
					var parameterValue = parameterValueArray.join(',');
					insertStatusDefineConetnt(parameterValue);
		            parameterWindow.dialog('close');
	    			
	    		});
				
				var parameterDatagrid = oc.ui.datagrid({
					pagination:false,
					selector:$('#parameterDatagridId'),
					checkOnSelect:false,
					selectOnCheck:false,
					columns:[[
						 {field:'id',checkbox:true},
				         {field:'nodeName',title:'节点名称',width:80},
				         {field:'weight',title:'权重',width:80,
				        	 formatter: function(value, rowData, rowIndex,div){
				        		 weightCommboArray.push($("<select/>").appendTo(div).combobox({
									 width:75,
									 placeholder:false,
									 selected:false,
									 editable:false,
									 valueField:'id',    
		    			    		 textField:'name',
									 value:1,
									 data:[{id:1,name:1},{id:2,name:2},{id:3,name:3},{id:4,name:4},{id:5,name:5}]
								}));
						 }},
				         {field:'nodeTypeName',title:'节点类型',width:80}
				     ]]
				});
				
				var allowAddParameters = new Array();
				for(var i = 0 ; i < insertParameters.length ; i ++){
					if(insertParameters[i].type == 0){
						allowAddParameters.push(insertParameters[i]);
					}
				}
				var localData = {"data":{"total":0,"rows":allowAddParameters},"code":200};
				 weightCommboArray = new Array();
				parameterDatagrid.selector.datagrid('loadData',localData);
				
			}
			
    		$('#checkStatusDefine').on('click',function(){
    			validationStatusDefine(false);
    		});
    		
    		$('.biz_define_symbol').on('click',function(e){
    			var content = $(e.target).text().trim();
    			if(content == 'OR' || content == 'AND' || content == 'AVG'){
    				content += '()';
    			}
    			insertStatusDefineConetnt(content);
    		});
			
		}
		
		function insertStatusDefineConetnt(content){
			parameterValue = content;
			var $t = $('#biz_status_define_content')[0];  
            if (document.selection) { // ie  
            	$('#biz_status_define_content').focus();  
                var sel = document.selection.createRange();  
                sel.text = parameterValue;  
                $('#biz_status_define_content').focus();  
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
                $('#biz_status_define_content').focus();  
                $t.selectionStart = startPos + parameterValue.length;  
                $t.selectionEnd = startPos + parameterValue.length;  
                $t.scrollTop = scrollTop;  
                if (arguments.length == 2) {  
                    $t.setSelectionRange(startPos - t,  
                            $t.selectionEnd + t);  
                    $('#biz_status_define_content').focus();  
                }  
            } else {  
            	$('#biz_status_define_content').value += parameterValue;  
            	$('#biz_status_define_content').focus();  
            }
		}
		
		function validationStatusDefine(isUpdate){

			
			var healthStatus = $('#biz_status_define_content').val();
			var resultStatus = healthStatus;
			var bracketCount = 0;
			if(healthStatus.indexOf('(') < 0){
				alert('无效表达式');
				return;
			}
			if(healthStatus.indexOf('()') >= 0){
				alert('存在空表达式()');
				return;
			}
			var isFirstLeftBracket = true;
			for(var i = 0 ; i < healthStatus.length ; i ++){
				if(bracketCount < 0){
					alert('括号不匹配');
					return;
				}
				
				if(healthStatus[i] == ')'){
					bracketCount--;
					for(var j = (i + 1) ; j < healthStatus.length ; j ++){
						if(healthStatus[j] && healthStatus[j] == ' '){
							continue;
						}else{
							if(healthStatus[j] && healthStatus[j] != ',' && healthStatus[j] != ')'){
								alert('右括号后缺少","号');
								return;
							}else{
								break;
							}
						}
					}
				}else if(healthStatus[i] == '('){
					var expression = '';
					if(isFirstLeftBracket){
						isFirstLeftBracket = false;
						expression = healthStatus.substring(0,i);
					}else{
						var temp = healthStatus.substring(0,i);
						var indexLeft = temp.lastIndexOf('(');
						var indexComma = temp.lastIndexOf(',');
						if(indexLeft >= 0 && indexComma >= 0){
							if(indexLeft > indexComma){
								expression = temp.substring(indexLeft + 1,i);
							}else{
								expression = temp.substring(indexComma + 1,i);
							}
						}else if(indexLeft >= 0 && indexComma < 0){
							expression = temp.substring(indexLeft + 1,i);
						}else if(indexLeft < 0 && indexComma >= 0){
							expression = temp.substring(indexComma + 1,i);
						}
					}
					expression = expression.trim();
					if(expression.toUpperCase() != 'AVG' && expression.toUpperCase() != 'OR' && expression.toUpperCase() != 'AND'){
						alert('不支持运算符 : ' + expression);
						return;
					}
					bracketCount++;
				}
				
			}
			
			if(bracketCount != 0){
				alert('括号不匹配');
				return;
			}
			
			//大小写问题
			var nodes = healthStatus.replace(/[Oo][Rr](\s*)\(/g,'').replace(/[Aa][Nn][Dd](\s*)\(/g,'').replace(/[Aa][Vv][Gg](\s*)\(/g,'').replace(/\)/g,'');
			var nodeArray = nodes.split(',');
			var nodeNameArray = new Array();
			for(var i = 0 ; i < nodeArray.length ; i ++){
				if(!nodeArray[i]){
					alert('存在空节点');
					return;
				}
				var parameters = nodeArray[i].split('*');
				if(!parameters || parameters.length <= 0 || parameters.length > 2){
					alert('表达式不合法 : ' + nodeArray[i]);
					return;
				}
				
				if(parameters.length == 1){
					
					nodeArray[i] = nodeArray[i].trim();
					
					//没有设置权重
					if(nodeArray[i].indexOf('${') != 0 || nodeArray[i].indexOf('}') != (nodeArray[i].length - 1)){
						alert('表达式不合法 : ' + nodeArray[i]);
						return;
					}
					var nodeName = nodeArray[i].substring(nodeArray[i].indexOf('${') + 2,nodeArray[i].lastIndexOf('}'));
					if(!nodeName || nodeName == '' || nodeName.trim() == ''){
						alert('表达式不合法 : ' + nodeArray[i]);
						return;
					}
					nodeNameArray.push(nodeName);
					
				}else{
					
					parameters[0] = parameters[0].trim();
					parameters[1] = parameters[1].trim();
					
					//有设置权重
					if(!isNaN(parameters[0])){
						
						if(!isNaN(parameters[1])){
							alert('表达式不合法 : ' + nodeArray[i]);
							return;
						}else{
							if(parameters[0].indexOf('.') >= 0){
								alert('权重只能为整数 : ' + parameters[0]);
								return;
							}
							if(parseInt(parameters[0]) <= 0 || parseInt(parameters[0]) > 5){
								alert('权重设置范围为1-5 : ' + nodeArray[i]);
								return;
							}
							if(parameters[1].indexOf('${') < 0 || parameters[1].indexOf('}') != (parameters[1].length - 1)){
								alert('表达式不合法 : ' + nodeArray[i]);
								return;
							}
							var nodeName = parameters[1].substring(parameters[1].indexOf('${') + 2,parameters[1].lastIndexOf('}'));
							if(!nodeName || nodeName == '' || nodeName.trim() == ''){
								alert('表达式不合法 : ' + nodeArray[i]);
								return;
							}
							nodeNameArray.push(nodeName);
						}
						
					}else{
						
						if(isNaN(parameters[1])){
							alert('表达式不合法 : ' + nodeArray[i]);
							return;
						}else{
							if(parameters[1].indexOf('.') >= 0){
								alert('权重只能为整数 : ' + parameters[1]);
								return;
							}
							if(parseInt(parameters[1]) <= 0 || parseInt(parameters[1]) > 5){
								alert('权重设置范围为1-5 : ' + nodeArray[i]);
								return;
							}
							if(parameters[0].indexOf('${') < 0 || parameters[0].indexOf('}') != (parameters[0].length - 1)){
								alert('表达式不合法 : ' + nodeArray[i]);
								return;
							}
							var nodeName = parameters[0].substring(parameters[0].indexOf('${') + 2,parameters[0].lastIndexOf('}'));
							if(!nodeName || nodeName == '' || nodeName.trim() == ''){
								alert('表达式不合法 : ' + nodeArray[i]);
								return;
							}
							if(isUpdate){
								resultStatus = resultStatus.replace(nodeArray[i],parameters[1] + "*" + parameters[0]);
							}
							nodeNameArray.push(nodeName);
						}
						
					}
				}
				
			}
			
			//验证节点名是否存在
			newStatusDefine = resultStatus;
			out : for(var i = 0 ; i < nodeNameArray.length ; i ++){
				for(var j = 0 ; j < insertParameters.length ; j ++){
					if(nodeNameArray[i].trim() == insertParameters[j].nodeName && insertParameters[j].type == -1){
						alert('不能以业务本身 : ' + nodeNameArray[i].trim() + ' 作为节点');
						return ;
					}else if(nodeNameArray[i].trim() == insertParameters[j].nodeName && insertParameters[j].type == -2){
						alert('子业务节点 : ' + nodeNameArray[i].trim() + ' 会造成多个业务系统状态计算环路');
						return ;
					}
					if(nodeNameArray[i].trim() == insertParameters[j].nodeName){
						if(isUpdate){
							resultStatus = resultStatus.replace(insertParameters[j].nodeName,insertParameters[j].nodeId);
						}
						continue out;
					}
				}
				alert('节点 : ' + nodeNameArray[i] + ' 不存在');
				return;
			}
			
			if(isUpdate){
				return resultStatus.replace(/\s/g, "");
			}else{
				alert('验证通过');
			}
		
		}
		
		oc.module.bizmanagement.status.define = {
				open:function(bizId){
					insertParameters = null;
					curBizId = bizId;
					var addWindow = $('<div/>');
					//构建dialog
					addWindow.dialog({
					  title:"状态定义",
					  href:oc.resource.getUrl('resource/module/business-service/biz_status_define.html'),
					  width:900,
					  height:560,
					  modal:true,
					  onLoad:function(){
						  open();
						  $("#exampleShow").on('click',function(){
							  oc.resource.loadScript('resource/module/business-service/js/biz_stauts_example.js',function(){
								  oc.business.service.example.open({
									
								  });
								
							  });
						  });
					  },
					  buttons:[{
							text:'确定',
							iconCls:'fa fa-check-circle',
							handler:function(){
								var p = $('#business-service_status').accordion('getSelected');
								if (p){
									var index = $('#business-service_status').accordion('getPanelIndex', p);
									if(index == 1){
										//自定义状态
										var statusDefine = validationStatusDefine(true);
										if(statusDefine){
											if(oldStatusDefine.replace(/\s/g, "") != statusDefine.replace(/\s/g, "")){
												oc.util.ajax({
													url:oc.resource.getUrl('portal/business/service/updateBizStatusDefine.htm'),
													data:{id:curBizId,statusDefine:statusDefine},
													timeout:null,
													success:function(data){
														if(data.data){
															alert('修改成功');
															addWindow.dialog('close');
														}else{
															alert('修改失败');
														}
													}
												});
											}else{
												addWindow.dialog('close');
											}
										}
									}else{
										if(oldType != 0){
											oc.util.ajax({
												url:oc.resource.getUrl('portal/business/service/updateBizStatusDefine.htm'),
												data:{id:curBizId,statusDefine:''},
												timeout:null,
												success:function(data){
													if(data.data){
														alert('修改成功');
														addWindow.dialog('close');
													}else{
														alert('修改失败');
													}
												}
											});
										}else{
											addWindow.dialog('close');
										}
									}
								}
							}},{
							text:'取消',
							iconCls:'fa fa-times-circle',
							handler:function(){
								addWindow.dialog('close');
							}
						}]
					});
				}
		};
})