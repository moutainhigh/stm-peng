(function($){
	oc.ns('oc.module.knowledge.scriptmanage');
	var global = {
			mainDiv : $("#scriptManage"),tabDiv : $("#scriptManageTabs"),scriptType : 1,scriptFileType : new Array()};
	
	function changeScriptType(type){
		global.scriptType = type;
	}
	
	function datagridReload(type){
		if(1 == type && global.snapshotDatagrid){
			global.snapshotDatagrid.reLoad();
		}
		if(2 == type && global.recoverDatagrid){
			global.recoverDatagrid.reLoad();
		}
	}
	
	function getDatagridSelection(type){
		var datagridSelect;
		if(type==1){
			datagridSelect = global.snapshotDatagrid.selector.datagrid('getSelections');
		}else if(type==2){
			datagridSelect = global.recoverDatagrid.selector.datagrid('getSelections');
		}
		return datagridSelect;
	}
	
	function initPage(){
		global.tabDiv.tabs({
			fit : false,
			onSelect : function(title,index) {
				if(index == 0){
					changeScriptType(1);
					datagridReload(1);
//					if(global.snapshotDatagrid){
//						global.snapshotDatagrid.reLoad(oc.resource.getUrl('knowledge/scriptmanage/loadByType.htm?scriptTypeCode='+global.scriptType));
//					}
				}else if(index == 1){
					changeScriptType(2);
					datagridReload(2);
//					global.scriptType = 2;
//					if(global.recoverDatagrid){
//						global.recoverDatagrid.reLoad(oc.resource.getUrl('knowledge/scriptmanage/loadByType.htm?scriptTypeCode='+global.scriptType));
//					}
				}
			}
		});
		
		var toolbar = [];
		toolbar.push({
			text:'上传',
			iconCls:'fa fa-sign-in',
			onClick:function(){
				if(global.scriptFileType.length>0){
					upload(global.scriptType);
				}else{
					oc.util.ajax({
						url:oc.resource.getUrl('knowledge/scriptmanage/getUploadScriptFileType.htm'),
						failureMsg:'数据加载失败',
						success:function(data){
							for(var i=0;i<data.data.length;i++){
								global.scriptFileType.push(data.data[i]);
							}
							upload(global.scriptType);
						}
					});
				}
			}
		},{
			text:'下载',
			iconCls:'fa fa-sign-out',
			onClick:function(){
				var datagridSelect,fileId;
//				if(global.scriptType==1){
//					datagridSelect = global.snapshotDatagrid.selector.datagrid('getSelections');
//				}else{
//					datagridSelect = global.recoverDatagrid.selector.datagrid('getSelections');
//				}
				datagridSelect = getDatagridSelection(global.scriptType);
				if(datagridSelect.length==0){
					alert('请选择需要下载的脚本!');
					return;
				}else if(datagridSelect.length>1){
					alert('每次只能下载一个脚本!');
					return;
				}else if(datagridSelect.length==1){
					fileId = datagridSelect[0].fileId;
					oc.util.download(fileId);
				}else{}
			}
		},{
			text:'删除',
			iconCls:'fa fa-trash-o',
			onClick:function(){
				var datagridSelect,scriptIdArr = new Array(),fileIdArr = new Array();
//				if(global.scriptType==1){
//					datagridSelect = global.snapshotDatagrid.selector.datagrid('getSelections');
//				}else{
//					datagridSelect = global.recoverDatagrid.selector.datagrid('getSelections');
//				}
				datagridSelect = getDatagridSelection(global.scriptType);
				if(datagridSelect && datagridSelect.length<1){
					alert('请选择需要删除的脚本!');
					return ;
				}
				for(var i=0;i<datagridSelect.length;i++){
					scriptIdArr.push(datagridSelect[i].scriptId);
					fileIdArr.push(datagridSelect[i].fileId);
				}

				oc.ui.confirm('确认删除所选择的数据？', function(){
					oc.util.ajax({
					url:oc.resource.getUrl('knowledge/scriptmanage/delScript.htm'),
					data: {scriptId:scriptIdArr.join(','),fileId:fileIdArr.join(',')},
					failureMsg:'数据加载失败',
					success:function(datas){
						if(datas.data){
							alert('删除成功！');
						}else{
							alert('删除失败！');
						}
//						if(global.scriptType==1){
//							global.snapshotDatagrid.reLoad();
//						}else{
//							global.recoverDatagrid.reLoad();
//						}
						datagridReload(global.scriptType);
					}
					});
				});
				
			}
		});
		var parentDivObj = global.mainDiv.parent().parent();
		var heightMain = parentDivObj.height();
		var datagirdHeight = heightMain - global.tabDiv.find('.tabs-wrap').height();
		global.snapshotDatagrid = oc.ui.datagrid({
			selector : global.mainDiv.find("#snapshot_datagrid"),
			url : oc.resource.getUrl('knowledge/scriptmanage/loadByType.htm?scriptTypeCode=1'),
			fit : false,
			hideReset:true,
			hideSearch:true,
			height:datagirdHeight-40+'px',
			columns : [[
	            {field : 'scriptId',align : 'left',title : '',checkbox:true,width : 50},
	            {field : 'fileId',align : 'left',title : '',hidden:true},
				{field : 'docName',title : '文件名',align : 'left',width : 150,ellipsis:true}, 
				{field : 'userName',title : '上传者',align : 'left',width : 150,ellipsis:true},
		        {field : 'discription',title : '脚本描述',align : 'left',sortable : true,width : 250,ellipsis:true},
		        {field : 'fileSizeNum',title : '文件大小',align : 'left',sortable : true,width : 75,ellipsis:true,formatter:function(value,row,rowIndex){
		        	if(value){
		        		var num = new Number(value);
		        		if(num){
			        		return transformByte(num);
			        	}
		        	}
		        	return value;
		        }},
		        {field : 'updateTime',title : '更新时间',align : 'left',sortable : true,width : 150,ellipsis:true,formatter:function(value,row,rowIndex){
                	 return formatDate(new Date(value));
                }}]],
			onClickCell : function(rowIndex, field, value){
				var rows = global.snapshotDatagrid.selector.datagrid('getRows');
				if(field=='docName'){
					update(rows[rowIndex].scriptId,global.scriptType);
				}
			},
			octoolbar : {
				left : toolbar,
				right : [{
					text:'映射表',
					iconCls:'icon-found',
					onClick:function(){
						showScriptCode();
					}
				}]
			}
		});
		
		global.recoverDatagrid= oc.ui.datagrid({
			selector : global.mainDiv.find("#recover_datagrid"),
			url : oc.resource.getUrl('knowledge/scriptmanage/loadByType.htm?scriptTypeCode=2'),
			fit : false,
			hideReset:true,
			hideSearch:true,
			height:datagirdHeight-40+'px',
			columns : [[
	            {field : 'scriptId',align : 'left',title : '',checkbox:true,width : 50},
	            {field : 'fileId',align : 'left',title : '',hidden:true},
				{field : 'docName',title : '文件名',align : 'left',width : 150,ellipsis:true}, 
				{field : 'userName',title : '上传者',align : 'left',width : 150,ellipsis:true},
		        {field : 'discription',title : '脚本描述',align : 'left',sortable : true,width : 250,ellipsis:true},
		        {field : 'fileSizeNum',title : '文件大小',align : 'left',sortable : true,width : 75,ellipsis:true,formatter:function(value,row,rowIndex){
		        	if(value){
		        		var num = new Number(value);
		        		if(num){
			        		return transformByte(num);
			        	}
		        	}
		        	return value;
		        }},
		        {field : 'updateTime',title : '更新时间',align : 'left',sortable : true,width : 150,ellipsis:true,formatter:function(value,row,rowIndex){
                	 return formatDate(new Date(value));
                }}]],
                onClickCell : function(rowIndex, field, value){
    				var rows = global.recoverDatagrid.selector.datagrid('getRows');
    				if(field=='docName'){
    					update(rows[rowIndex].scriptId,global.scriptType);
    				}
    			},
				octoolbar : {
					left : toolbar,
					right : [{
						text:'映射表',
						iconCls:'icon-found',
						onClick:function(){
							showScriptCode();
						}
					}]
				}
		});
		
	}
	
	function upload(type){
		var fileTypeStr = '';
		if(global.scriptFileType.length>0){
			for(var i=0;i<global.scriptFileType.length;i++){
				fileTypeStr+='[ '+global.scriptFileType[i].toUpperCase()+' ]';
			}
		}
		
		var dlg = $('<div/>');
		var fileUpload = $('<form id="fileForm" class="oc-form col1 h-pad-mar oc-table-ocformbg">');
		var fileTable = $('<table style="width:100%;" class="octable-pop"/></table>');
		var fileTableTr1=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" align style="width:17%"><label>类型限制：</label></td><td>'+fileTypeStr+'  请按限制类型的文件上传!</td></tr>');
		var fileTableTr2=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" align style="width:17%"><label>脚本描述：</label></td><td><textarea placeholder="描述不能超过100个字" name="discripStr" style="resize: none;margin: 0px 2px; height: 50px; width: 100%;"></textarea><input name="scriptType" type="text" value="'+type+'" style="display:none" /></td></tr>');
		var fileTableTr3=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" align style="width:17%"><label>上传脚本：</label></td><td class="ie9-width"><input name="file" type="text" required="required" /></td></tr>');
		fileTable.append(fileTableTr1);
		fileTable.append(fileTableTr2);
		fileTable.append(fileTableTr3);
		fileUpload.append(fileTable);
		dlg.append(fileUpload);
		
		var form = oc.ui.form({
			selector:dlg.find('#fileForm'),
			filebox:[{selector:'[name=file]'}]
		});
		
		var buttons  = [{
		    	text: '确定',
		    	iconCls:"fa fa-check-circle",
		    	handler:function(){
		    		var filebox=form.filebox[0];
					var filePath = filebox.selector.filebox('getValue');
					var fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
					
					if(fileName.match(/([^\u0000-\u00FF])/g)){
						alert('文件名请不要有中文字符!');
						return ;
					}
					if(fileName && fileName.length>99){
						alert('文件名过长,请不要超过100个字符!');
						return ;
					}
					if(global.scriptFileType.length>0){
						var flag = false;
						var fileType = fileName.split('.')[1];
						
						for(var i=0;i<global.scriptFileType.length;i++){
							if(global.scriptFileType[i].toUpperCase()==fileType.toUpperCase()){
								flag = true;
							}
						}
						if(!flag){
							alert('请上传指定类型的文件!');
							return ;
						}
					}
					
		    		if(filePath){
		    			var discription = dlg.find('textarea[name=discripStr]').val();
		    			var discriptionStr = discription.replace(/([^\u0000-\u00FF])/g, function ($) { return escape('--'); });
		    			if(discriptionStr && discriptionStr.length>200){
		    				alert('文件描述过长!');
							return ;
		    			}
		    			
		    			oc.util.ajax({
		    				url:oc.resource.getUrl('knowledge/scriptmanage/selectByfileNameAndScriptType.htm'),
		    				data: {fileName:fileName,scriptType:type},
		    				failureMsg:'数据加载失败',
		    				success:function(data){
		    					if(data.data && data.data.length>0){
		    						oc.ui.confirm('已有相同文件名的脚本,继续上传将覆盖原脚本!', function(){
		    							form.jq.ajaxSubmit($.extend({}, filebox,{
											url : oc.resource.getUrl('knowledge/scriptmanage/scriptFileUpload.htm'),
											type : 'POST',
											iframe : true,
											timeout: 300000,
											async:false,
											dataType:'json',
											success : function(data) {
												
												if(data){
													alert("上传成功！");
												}else{
													alert("上传失败！");
												}
												datagridReload(type);
												dlg.window("destroy");
											},
											error : function(XMLHttpRequest, textStatus, errorThrown) {
												alert('文件上传失败！');
												log(XMLHttpRequest, textStatus, errorThrown);
											}
										}));
		    						});
		    					}else{
		    						form.jq.ajaxSubmit($.extend({}, filebox,{
										url : oc.resource.getUrl('knowledge/scriptmanage/scriptFileUpload.htm'),
										type : 'POST',
										iframe : true,
										timeout: 300000,
										async:false,
										dataType:'json',
										success : function(data) {
											
											
											if(data){
												alert("上传成功！");
											}else{
												alert("上传失败！");
											}
											datagridReload(type);
											dlg.window("destroy");
										},
										error : function(XMLHttpRequest, textStatus, errorThrown) {
											alert('文件上传失败！');
											log(XMLHttpRequest, textStatus, errorThrown);
										}
									}));
		    					}
		    					
		    				}
		    			});
					}
		    	}
		    },{
		    	text: '取消',
		    	iconCls:"fa fa-times-circle",
		    	handler:function(){
		    		dlg.dialog('destroy');
		    	}
		    }];
		
		
		dlg.dialog({
		    title: '脚本上传',
		    width : 450,
			height : 300,
			buttons:buttons,
		    onLoad:function(){}
		});
	}
	function update(scriptId,type){
		oc.util.ajax({
			url:oc.resource.getUrl('knowledge/scriptmanage/loadByscriptId.htm'),
			data: {scriptId:scriptId},
			failureMsg:'数据加载失败',
			success:function(datas){
				var scriptObj = datas.data;
				if(scriptObj)
				var dlg = $('<div style="width:100%;" />');
				var scriptInfoForm = $('<form class="oc-form col1 h-pad-mar oc-table-ocformbg" style="width:100%;" id="scriptInfoForm"></form>');
				var scriptInfoTable = $('<table class="octable-pop"/></table>');
				var scriptInfoTable1=$('<tr class="form-group h-pad-mar" ><td class="tab-l-tittle" style="width:100px"><label>文件名：</label></td><td><div style="width:370px;text-overflow: ellipsis;overflow:hidden;" id="docName" title="'+scriptObj.docName+'">'+scriptObj.docName+'</div></td></tr>');
				var scriptInfoTable2=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px"><label style="width:100px">文件描述：</label></td><td><textarea id="discription" placeholder="描述不能超过100个字" style="width:350px;resize: none;margin: 0px 2px; ">'+(null==scriptObj.discription?'':scriptObj.discription)+'</textarea></td></tr>');
				var scriptInfoTable3=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px"><label style="width:100px">文件内容：</label></td><td><textarea id="fileContent" disabled = "true" placeholder="文件内容..." style="width:350px;resize: none;margin: 0px 2px; ">'+(null==scriptObj.fileContent?'':scriptObj.fileContent)+'</textarea></td></tr>');
				
				var fileSizeStr = scriptObj.fileSizeNum;
				if(scriptObj.fileSizeNum){
	        		var num = new Number(scriptObj.fileSizeNum);
	        		if(num){
	        			fileSizeStr = transformByte(num);
		        	}
	        	}
				var scriptInfoTable4=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px"><label style="width:100px">文件大小：</label></td><td><label id="fileSizeNum" >'+fileSizeStr+'</label></td></tr>');
				var scriptInfoTable5=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px"><label style="width:100px">上传者：</label></td><td><label id="updateUser" >'+scriptObj.userName+'</label></td></tr>');

					
				var scriptInfoTable6=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px"><label >更新时间：</label></td><td><label id="updateTime">'+formatDate(new Date(scriptObj.updateTime))+'</label></td></tr>');
				scriptInfoTable.append(scriptInfoTable1);	
				scriptInfoTable.append(scriptInfoTable2);
				scriptInfoTable.append(scriptInfoTable3);
				scriptInfoTable.append(scriptInfoTable4);
				scriptInfoTable.append(scriptInfoTable5);
				scriptInfoTable.append(scriptInfoTable6);
				scriptInfoForm.append(scriptInfoTable);
				dlg.append(scriptInfoForm);
				
//				var form = oc.ui.form({
//					selector:dlg.find('#scriptInfoForm')
//				});
				
				var buttons  = [{
				    	text: '确定',
				    	iconCls:"fa fa-check-circle",
				    	handler:function(){
				    		var discription = dlg.find('#discription').val();
				    		if(discription ==  scriptObj.discription){
				    			dlg.dialog('destroy');
				    			return ;
				    		}
				    		
			    			var discriptionStr = discription.replace(/([^\u0000-\u00FF])/g, function ($) { return escape('--'); });
			    			if(discriptionStr && discriptionStr.length>200){
			    				alert('文件描述过长!');
								return ;
			    			}
				    		
				    		oc.util.ajax({
				    			url:oc.resource.getUrl('knowledge/scriptmanage/updateByscriptId.htm'),
				    			data: {scriptId:scriptId,discription:discription},
				    			failureMsg:'数据加载失败',
				    			success:function(datas){
				    				if(datas.data){
				    					alert('修改成功!');
				    				}else{
				    					alert('修改失败!');
				    				}
				    				datagridReload(type);
				    			}
				    		})
				    		dlg.dialog('destroy');
				    	}
				    },{
				    	text: '取消',
				    	iconCls:"fa fa-times-circle",
				    	handler:function(){
				    		dlg.dialog('destroy');
				    	}
				    }];
				
				
				dlg.dialog({
				    title: '编辑脚本',
				    width : 470,
					height : 420,
					buttons:buttons,
				    onLoad:function(){
				    	
				    }
				});
			}
			});
		
	}
	
	function showScriptCode(){
		oc.util.ajax({
			url:oc.resource.getUrl('knowledge/scriptmanage/loadAllScriptCode.htm'),
			failureMsg:'数据加载失败',
			success:function(datas){
				var scriptCodeArr = datas.data;
				if(scriptCodeArr)
				var dlg = $('<div style="width:550px;height:464px;"></div>');
				var scriptCodeDiv = $('<div style="width:100%;height:100%;" id="scriptCodeDatagrid"></div>');
				dlg.append(scriptCodeDiv);
				
				var datagridTemp = oc.ui.datagrid({
					selector : dlg.find("#scriptCodeDatagrid"),
					fit : false,
					hideReset:true,
					hideSearch:true,
					pagination:false,
					columns : [[
			            {field : 'codeId',hidden:true},
						{field : 'scriptCode',title : '宏值',width:260,align : 'left',ellipsis:true}, 
				        {field : 'codeDiscription',title : '描述',width:260,align : 'left',sortable : true,ellipsis:true}
				       ]]
				});
				
				var dataLength = scriptCodeArr.length;
				var localData = {"data":{"startRow":0,"rowCount":dataLength,"totalRecord":dataLength,"condition":null,
					"rows":scriptCodeArr,"total":dataLength},"code":200};
				datagridTemp.selector.datagrid('loadData',localData);
				
				dlg.dialog({
				    title: '脚本宏值映射',
				    width : 550,
					height : 520,
					onClose: function() {}
				});
			}
			});
		
//		oc.util.ajax({
//			url:oc.resource.getUrl('knowledge/scriptmanage/loadAllScriptCode.htm'),
//			failureMsg:'数据加载失败',
//			success:function(datas){
//				var scriptCodeArr = datas.data;
//				if(scriptCodeArr)
//				var dlg = $('<div/>');
//				var scriptCodeForm = $('<form class="oc-form col1 h-pad-mar oc-table-ocformbg" style="width:100%;height:100%;" id="scriptCodeForm"></form>');
//				var scriptCodeDiv = $('<div style="height:100%;overflow-x: hidden;overflow-y: auto;" ></div>');
//				var scriptCodeTable = $('<table style="width:100%;" class="octable-pop"/></table>');
//				var scriptCodeTr1=$('<tr class="form-group h-pad-mar"><td td-bgcolor  style="text-align:left;width:10%;font-weight:bold;"><label>序号</label></td><td td-bgcolor  style="width:45%;font-weight:bold;text-align:left;"><label>函数</label></td><td td-bgcolor  style="width:45%;font-weight:bold;text-align:left;"><label id="docName">描述</label></td></tr>');
//				scriptCodeTable.append(scriptCodeTr1);	
//				for(var i=0;i<scriptCodeArr.length;i++){
//					var scriptCodeObj = scriptCodeArr[i];
//					var scriptCodeTr=$('<tr class="form-group h-pad-mar"><td  style="width:10%"><label>'+(i+1)+'</label></td><td style="width:45%"><label id="docName">'+scriptCodeObj.scriptCode+'</label></td><td style="width:45%"><label id="docName">'+scriptCodeObj.codeDiscription+'</label></td></tr>');
//					
//					scriptCodeTable.append(scriptCodeTr);	
//				}
//				scriptCodeDiv.append(scriptCodeTable);
//				scriptCodeForm.append(scriptCodeDiv);
//				dlg.append(scriptCodeForm);
//				dlg.dialog({
//				    title: '脚本宏值対映',
//				    width : 550,
//					height : 500,
//				    onLoad:function(){
//				    	
//				    }
//				});
//			}
//			});
		
	}
	
	function formatDate(date){
		return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
	}
	function transformByte(byteNum){
		var sb = "";
		var precision = 2;
		
		if(byteNum > 1024 * 1024 * 1024){
			sb+=(byteNum / (1024 * 1024 * 1024)).toFixed(precision) + "GB";
		}else if(byteNum > 1024 * 1024){
			sb+=(byteNum / (1024 * 1024)).toFixed(precision) + "MB";
		}else if(byteNum > 1024){
			sb+=(byteNum / 1024).toFixed(precision) + "KB";
		}else{
			sb+=byteNum.toFixed(precision) + "Byte";
		}
		return sb;
	}
	
	initPage();
	
	oc.module.knowledge.scriptmanage = {
			open : function(){
				
			}
	};
	
})(jQuery);