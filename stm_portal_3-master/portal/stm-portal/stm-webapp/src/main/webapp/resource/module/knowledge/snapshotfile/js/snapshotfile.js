(function($){
	oc.ns('oc.module.knowledge.snapshotfile');
	
	initPage();
	keydown();
	function initPage(){
		var snapshotDiv = $('#konwledgeSnapshot');
		var toolbar = snapshotDiv.find('#snapshotToolbar');
		var datagridDiv = snapshotDiv.find('#snapshotDatagrid');
		var form = snapshotDiv.find('#snapshotForm');
		//表格查询表单对象
		var queryForm=oc.ui.form({selector:form});
		
		
		var datagrid = null;
		datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('knowledge/snapshotFile/getAllsnapshotFileByPage.htm'),
			queryForm:queryForm,
			fitColumns:true,
			hideReset:true,
			hideSearch:true,
			octoolbar:{
				left:[{
					iconCls: 'fa fa-trash-o',
					text:"删除",
					onClick: function(){
						var datagridSelect,fileIdArr = new Array();
						
						datagridSelect = datagrid.selector.datagrid('getSelections');
						if(datagridSelect && datagridSelect.length<1){
							alert('请选择需要删除的文件!');
							return ;
						}
						for(var i=0;i<datagridSelect.length;i++){
							fileIdArr.push(datagridSelect[i].id);
						}

						oc.ui.confirm('确认删除所选择的数据？', function(){
							oc.util.ajax({
							url:oc.resource.getUrl('knowledge/snapshotFile/delSnapshotFileById.htm'),
							data: {fileIds:fileIdArr.join(',')},
							failureMsg:'数据加载失败',
							success:function(datas){
								//清除可能的选中项,导致id字符串提交到后台访问方法出错
								datagrid.selector.datagrid('clearSelections');
								
								if(datas.data){
									alert('删除成功！');
								}else{
									alert('删除失败！');
								}
//								if(global.scriptType==1){
//									global.snapshotDatagrid.reLoad();
//								}else{
//									global.recoverDatagrid.reLoad();
//								}
								datagrid.reLoad();
							}
							});
						});
					}
				},{
					iconCls: 'fa fa-sign-out',
					text:"导出",
					onClick: function(){
						var datagridSelect,fileIdArr = new Array();
						
						datagridSelect = datagrid.selector.datagrid('getSelections');
						if(datagridSelect && datagridSelect.length<1){
							alert('请选择需要导出的文件!');
							return ;
						}
						for(var i=0;i<datagridSelect.length;i++){
							fileIdArr.push(datagridSelect[i].id);
						}
						
						var iframeHtml = $("<iframe style='display:none' src='"+oc.resource.getUrl('knowledge/snapshotFile/downloadSnapshotFileById.htm?fileIds='+fileIdArr.join(','))+"'/>");
						iframeHtml.appendTo("body");
					}
				}],right:[toolbar,
				        {
					 	id : 'searchBtn',
						iconCls:'icon-search',
						text : '查询',
						onClick : function(){
							//清除可能的选中项,导致id字符串提交到后台访问方法出错
							datagrid.selector.datagrid('clearSelections');
							
							var value = snapshotDiv.find('#ipAdressValue').val();
							var ipAdress = snapshotDiv.find('#ipAdress');
							if(value){
								ipAdress.val(value);
							}else{
								ipAdress.val(null);
							}
							datagrid.load();
						}
					 }]
			},
			columns:[[
				{field:'id',checkbox:true},
				{field:'fileName',title:'文件名',align:'left',width:60},
				{field:'fileSize',title:'文件大小',align:'left',width:60,formatter:function(value,row,rowIndex){
		        	if(value){
		        		var num = new Number(value);
		        		if(num){
			        		return transformByte(num);
			        	}
		        	}
		        	return value;
		        	
		        }},
				{field:'createDatetime',title:'文件更新时间',align:'left',width:60,formatter:function(value,row,rowIndex){
                	 return formatDate(new Date(value));
                }}
			]],
			onClickCell : function(rowIndex, field, value){
				var rows = datagrid.selector.datagrid('getRows');
				var rowObj = rows[rowIndex];
				if(field=='fileName'){
					showSnapshotFileInfo(rowObj.id);
				}
			},
			onBeforeLoad:function(p){
				p.startRow=p.page?((p.page-1)*p.rows):0;
				p.rowCount=p.rows;
				var opts=$(this).datagrid('options');
				if(opts.queryForm){
					var ps=opts.queryForm.val(),prefix=opts.queryConditionPrefix||'';
					for(var n in ps){
						if(n=='id') continue;
						p[prefix+n]=ps[n];
					}
				}
				return true;
			}
		});
		
	}
	function keydown(){ 
		$(document).keydown(function(event){ 
		if(event.keyCode==13){ 
		$("#searchBtn").click(); 
		} 
		})
	}
	function showSnapshotFileInfo(snapshotFileId){
		oc.util.ajax({
			url:oc.resource.getUrl('knowledge/snapshotFile/getSnapshotFileById.htm'),
			data: {id:snapshotFileId},
			failureMsg:'数据加载失败',
			success:function(datas){
				var snapshotFileObj = datas.data;
				if(!snapshotFileObj){
					alert('快照文件查询错误!');
					return ;
				}
				
				var dlg = $('<div style="width:100%;height:100%;" />');
				var scriptInfoForm = $('<form class="oc-form col1 h-pad-mar oc-table-ocformbg" style="width:100%;height:100%;" id="scriptInfoForm"></form>');
				var scriptInfoTable = $('<table class="octable-pop"/></table>');
				var scriptInfoTable1=$('<tr class="form-group h-pad-mar" ><td class="tab-l-tittle" style="width:100px;height:35px;"><label>文件名：</label></td><td><div style="width:450px;text-overflow: ellipsis;overflow:hidden;" title="'+snapshotFileObj.fileName+'">'+snapshotFileObj.fileName+'</div></td></tr>');
				var scriptInfoTable2=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px;height:35px;"><label style="width:150px">文件大小：</label></td><td><label id="fileSizeNum" >'+transformByte(snapshotFileObj.fileSize)+'</label></td></tr>');
				var scriptInfoTable3=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px;height:280px;"><label style="width:150px">文件内容：</label></td><td><textarea id="fileContent" disabled = "true" placeholder="文件内容..." style="width:500px;height:280px;resize: none;margin: 0px 2px; ">'+(null==snapshotFileObj.fileContent?'':snapshotFileObj.fileContent)+'</textarea></td></tr>');
				var scriptInfoTable4=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:100px;height:35px;"><label >更新时间：</label></td><td><label id="updateTime">'+formatDate(new Date(snapshotFileObj.createDatetime))+'</label></td></tr>');
				scriptInfoTable.append(scriptInfoTable1);
				scriptInfoTable.append(scriptInfoTable2);
				scriptInfoTable.append(scriptInfoTable3);
				scriptInfoTable.append(scriptInfoTable4);
				scriptInfoForm.append(scriptInfoTable);
				dlg.append(scriptInfoForm);
				
				dlg.dialog({
				    title: '快照文件',
				    width : 620,
					height : 435
				});
			}
			});
		
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
	
	
})(jQuery);