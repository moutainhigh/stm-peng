$(function(){
	var rootDiv = $("#script_config_main");
	$('#script_config_main_center').layout({
		fit:true
	});
	
	$('#script_config_main_west').layout({
		fit:true
	});
	
	var rootDivWidth = rootDiv.width();
	var treeWidth = rootDivWidth*0.25;
	var editBtnSpan = "<span class='scriptTreeEditBtn mouse-btn-left' style='opacity:0; z-index:10;'></span>";
	
	//type 1:目录,2:脚本
	var currentClickSetchil = {};
	$('#configScriptQueryName').fixPlaceHolder();
	var queryBtn = rootDiv.find(".queryBtn").linkbutton('RenderLB',{
		iconCls:'icon-search',
		onClick : function(){
			
			var configScriptQueryName = $('#configScriptQueryName').val();
			var flag = true;
			if(configScriptQueryName){
				flag = false;
			}
			oc.util.ajax({
				url: oc.resource.getUrl('portal/config/configscriptmanage/queryConfigScript.htm'),
				data : {queryCode : configScriptQueryName , flag:flag},
				successMsg:null,
				success:function(data){
					var treeData = parseData(data.data,0,true);
					treeObj.tree('loadData',treeData);
				}
			});
			
		}
	});

    // add by sunhailiang on 20170626 
	$("#scriptConfigForm",rootDiv).keypress(function(e){
		 var e = e || window.event;
		 if(e.keyCode == 13){
		    queryBtn.click();
			return false;
		 }
		 e.stopPropagation();
	 });

	rootDiv.find(".resetBtn").linkbutton('RenderLB',{
		iconCls:'icon-reset',
		onClick : function(){
			$('#configScriptQueryName').val('');
		}
	});
	
	//初始化右键menu
	if(oc.index.scriptMenu){
		oc.index.scriptMenu.menu('destroy');
	}
	oc.index.scriptMenu = $('#config-ser-t-window').menu({
		onClick:function(item){
			var node = $(item.target).data('node');
			var customGroupId = node.id;
			switch (item.text) {
			case '添加目录':
				configAddDirectoryDialog({
					level:node.level,
					id:node.id
				});
				break;
			case '添加配置':
				configAddScriptDialog({});
				break;
			case '编辑':
				configEdit();
				break;
			case '删除':
				configDelete({
					id:node.id,
					type:node.type
				});
				break;
			case '向上移动':
				
				break;
			case '向下移动':
				
				break;
			default:
				break;
			}
		},
	});
	
	var treeObj = $('#script_tree').tree({
		fit: true,
		animate: true,
		lines: true,
		data: [],
		onClick: function(node){
			currentClickSetchil = {};
			currentClickSetchil['id'] = node.id;
			currentClickSetchil['type'] = node.type;
			currentClickSetchil['level'] = null;
			
			if(node.type!=1){
				refreshCenter(node.text);
			}
		},
//		onContextMenu: function(e, node){
//			//修改选中状态
//			var selectedNode = $('#script_tree').tree('getSelected');
//			if(selectedNode) $(selectedNode.target).removeClass('tree-node-selected');
//			$(node.target).addClass('tree-node-selected');
//			
//			currentClickSetchil['id'] = node.id;
//			currentClickSetchil['type'] = node.type;
//			currentClickSetchil['level'] = node.level;
//			
//			//取消浏览器右键菜单
//			e.preventDefault();
//			
//			if(node.type==1){
//				$("#config-ser-t-window").find("#configAddDirectory").show();
//				$("#config-ser-t-window").find("#configAddScript").show();
//				$("#config-ser-t-window").find("#configEdit").show();
//				$("#config-ser-t-window").find("#configDelete").show();
//				if(1==node.level){
//					$("#config-ser-t-window").find("#configEdit").hide();
//					$("#config-ser-t-window").find("#configDelete").hide();
//				}
//			}else{
//				$("#config-ser-t-window").find("#configAddDirectory").hide();
//				$("#config-ser-t-window").find("#configAddScript").hide();
//				$("#config-ser-t-window").find("#configEdit").show();
//				$("#config-ser-t-window").find("#configDelete").show();
//			}
//			
//			//右键菜单赋值
//			$('#config-ser-t-window').find('#configAddDirectory').data('node',node);
//			$('#config-ser-t-window').find('#configAddScript').data('node',node);
//			$('#config-ser-t-window').find('#configEdit').data('node',node);
//			$('#config-ser-t-window').find('#configDelete').data('node',node);
//			//$('#config-ser-t-window').find('#menu_up').data('node',node);
//			//$('#config-ser-t-window').find('#menu_down').data('node',node);
//			$('#config-ser-t-window').menu('show',{
//				left:e.pageX,
//				top:e.pageY
//			});
//		},
		onLoadSuccess:function(node,data){
			currentClickSetchil = {};
			
			//绑定编辑图标事件
			$('#script_tree').find('.tree-node').each(function(){
				$(this).hover(function(e){
					$(this).parent().find('.scriptTreeEditBtn:first').css("opacity","1");
				},function(e){
					$(this).parent().find('.scriptTreeEditBtn:first').css("opacity","0");
				});
			});
			
			$(".scriptTreeEditBtn").on('click', function(e){
				e.stopPropagation();
				
				var selectNodeTarget = $(this).parent().parent();
				treeObj.tree('select', selectNodeTarget);
				var thisSelectNode = treeObj.tree('getData', selectNodeTarget);
				
				currentClickSetchil['id'] = thisSelectNode.id;
				currentClickSetchil['type'] = thisSelectNode.type;
				currentClickSetchil['level'] = thisSelectNode.level;
				
				if(thisSelectNode.type==1){
					$("#config-ser-t-window").find("#configAddDirectory").show();
					$("#config-ser-t-window").find("#configAddScript").show();
					$("#config-ser-t-window").find("#configEdit").show();
					$("#config-ser-t-window").find("#configDelete").show();
					if(1==thisSelectNode.level){
						$("#config-ser-t-window").find("#configEdit").hide();
						$("#config-ser-t-window").find("#configDelete").hide();
					}
				}else{
					$("#config-ser-t-window").find("#configAddDirectory").hide();
					$("#config-ser-t-window").find("#configAddScript").hide();
					$("#config-ser-t-window").find("#configEdit").show();
					$("#config-ser-t-window").find("#configDelete").show();
				}
				
				//右键菜单赋值
				$('#config-ser-t-window').find('#configAddDirectory').data('node',thisSelectNode);
				$('#config-ser-t-window').find('#configAddScript').data('node',thisSelectNode);
				$('#config-ser-t-window').find('#configEdit').data('node',thisSelectNode);
				$('#config-ser-t-window').find('#configDelete').data('node',thisSelectNode);
				$('#config-ser-t-window').menu('show',{
					left:e.pageX,
					top:e.pageY
				});
				
				// 树节点展开后事件
				$(".scriptTreeEditBtn").hover(function(e){
					e.stopPropagation();
				}, function(e){
					$(this).parent().find('.scriptTreeEditBtn:first').css("opacity","0");
					e.stopPropagation();
				});
			});
		}
	});
	
	rootDiv.find('.fa-trash-o').on('click',function(){
		var jqTemp = $(this);
		var type = jqTemp.attr('type');
		var scriptId = jqTemp.attr('scriptId');
		
		var title ;
		var tempContainer ;
		switch(type){
		case "backUp":
			title = "备份脚本";
			tempContainer = rootDiv.find('#backUpConfigScriptDiv');
			break;
		case "recovery":
			title = "恢复脚本";
			tempContainer = rootDiv.find('#recoveryConfigScriptDiv');
			break;
		}
		if(tempContainer.find(":checked").length==0){
			alert('请选择脚本!');
			return;
		}
		
		oc.ui.confirm('确认删除'+title+'?',function(){
			if(tempContainer.find(":checked").length==0){
				alert('请选择要删除的脚本!');
				return ;
			}
			var scriptNameArr = new Array();
			tempContainer.find(":checked").each(function(){
				var checkTemp = $(this);
				var scriptName = checkTemp.attr('scriptName');
				
				scriptNameArr.push(scriptName);
			})
			
			oc.util.ajax({
    			url: oc.resource.getUrl('portal/config/configscriptmanage/delConfigScriptModelByType.htm'),
    			data:{saveType:type,configScriptId : scriptId,fileNameArr:scriptNameArr.join(',')},
    			successMsg:null,
    			success:function(data){
    				if(!data.data){
    					alert('删除失败,请重试!');
    				}
    				//刷新
    				oc.util.ajax({
		    			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScriptModelByConfigScriptId.htm'),
		    			data:{configScriptId : scriptId},
		    			successMsg:null,
		    			success:function(data){
		    				var dataTemp = data.data;
		    				var bakcUpdata = dataTemp['bakcUp'];
		    				fillTempDiv(scriptId,bakcUpdata,rootDiv.find('#backUpConfigScriptDiv'),1);
		    				
		    				var recoverydata = dataTemp['recovery'];
		    				fillTempDiv(scriptId,recoverydata,rootDiv.find('#recoveryConfigScriptDiv'),2);
		    			}
		    		});
    			}
    		});
			
		},function(){
		});
	})
	
	rootDiv.find('.fa-plus').on('click',function(){
		var jqTemp = $(this);
		var type = jqTemp.attr('type');
		var scriptId = jqTemp.attr('scriptId');
		
		if(scriptId>0){
			addOrUpdateScriptDialog({dlgAddScriptID:scriptId},type,'save');
		}else{
			alert('请先选择要添加脚本的设备!');return;
		}
		
	});
	
	rootDiv.find('.fa-file').on('click',function(){
		
	});
	
	function configEdit(){
		var id = currentClickSetchil['id'];
		var type = currentClickSetchil['type'];
		// console.log(type);
		if(type==1){
			oc.util.ajax({
				url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigDirectoryByConfigDirectoryId.htm'),
				data:{configDirectoryId : id},
				successMsg:null,
				success:function(data){
					var dataTemp = data.data;
					configAddDirectoryDialog({configScriptDirectoryName:dataTemp['name'],directoryId:id});
				}
			});
			
		}else{
			oc.util.ajax({
				url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScriptByConfigScriptId.htm'),
				data:{configScriptId : id},
				successMsg:null,
				success:function(data){
					var dataTemp = data.data;
					configAddScriptDialog({configScriptName:dataTemp['name'],configScriptOID:dataTemp['oid'], scriptId:id});
				}
			});
		}
	}
		
	function configDelete(params){
		var id = params.id ? params.id : currentClickSetchil['id'];
		var type = params.type ? params.type : currentClickSetchil['type'];
		
		if(type==1){
			//删除目录
			var temp = $('<div><form id="" class="oc-form float" ><div class="form-group">'+
					'<input delType="delAll" type="radio" name="delInput" ><label>删除目录及目录内所有内容.</label></div>'+
					'<div class="form-group"><input  delType="directoryOnly" type="radio" name="delInput" checked="checked" ><label>删除目录,目录内所有内容移动到上层.</label>'+
					'</div></form></div>');
					var dlg = $('<div/>');
					dlg.append(temp);
					dlg.dialog({
					    title: '删除目录',
					    width : 360,
						height : 160,
					    onLoad:function(){
					    	_init(dlg);
					    },
						buttons:[{
					    	text: '删除',
					    	handler:function(){
					    		var delType = 'directoryOnly';
					    		delType = dlg.find(':checked').attr('delType');
					    		
					    		oc.util.ajax({
					    			url: oc.resource.getUrl('portal/config/configscriptmanage/delConfigScriptDirectory.htm'),
					    			data:{configScriptDirectoryId : id , delType : delType},
					    			successMsg:null,
					    			success:function(data){
					    				if(!data.data){
					    					alert('删除失败,请重试!');
					    				}
					    				treeRefresh();
					    				rootDiv.find('#script_config_main_center').layout('panel','center').panel('setTitle','请选择脚本');
					    				fillTempDiv(-1,null,rootDiv.find('#backUpConfigScriptDiv'));
					    				fillTempDiv(-1,null,rootDiv.find('#recoveryConfigScriptDiv'));
					    				dlg.dialog('destroy');
					    			}
					    		});
					    	}
					    },{
					    	text: '取消',
					    	handler:function(){
					    		dlg.dialog('destroy');
					    	}
					    }]
					});
		}else{
			//直接删除配置
			oc.ui.confirm('确认删除脚本?',function(){
				oc.util.ajax({
	    			url: oc.resource.getUrl('portal/config/configscriptmanage/delConfigScriptById.htm'),
	    			data:{configScriptId : id},
	    			successMsg:null,
	    			success:function(data){
	    				if(!data.data){
	    					alert('删除失败,请重试');
	    				}
	    				treeRefresh();
	    				var twtw = rootDiv.find('#script_config_main_center').layout('panel','center').find('.panel-title');
	    				rootDiv.find('#script_config_main_center').layout('panel','center').panel('setTitle','请选择脚本');
	    				currentClickSetchil = {};
	    				fillTempDiv(-1,null,rootDiv.find('#backUpConfigScriptDiv'));
	    				fillTempDiv(-1,null,rootDiv.find('#recoveryConfigScriptDiv'));
	    				//dlg.dialog('destroy');
	    			}
	    		});
				
			},function(){
			});
		}
	}
	
	/*
	 * ===============================================
	 * 工具js方法
	 * ===============================================
	 * */
	
	function refreshCenter(title){
		rootDiv.find('#script_config_main_center').layout('panel','center').panel('setTitle',title);
		
		oc.util.ajax({
			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScriptModelByConfigScriptId.htm'),
			data:{configScriptId : currentClickSetchil['id']},
			successMsg:null,
			success:function(data){
				var dataTemp = data.data;
				if(dataTemp['bakcUp']){
					var bakcUpdata = dataTemp['bakcUp'];
    				fillTempDiv(currentClickSetchil['id'],bakcUpdata,rootDiv.find('#backUpConfigScriptDiv'),1);
				}else{
					rootDiv.find('#backUpConfigScriptDiv').html('');
				}
				
				if(dataTemp['recovery']){
    				var recoverydata = dataTemp['recovery'];
    				fillTempDiv(currentClickSetchil['id'],recoverydata,rootDiv.find('#recoveryConfigScriptDiv'),2);
				}else{
					rootDiv.find('#recoveryConfigScriptDiv').html('');
				}
				
			}
		});
	}
	
	function treeRefresh(){ 
		oc.util.ajax({
			url: oc.resource.getUrl('portal/config/configscriptmanage/getTreeView.htm'),
			successMsg:null,
			success:function(data){
				var treeData = parseData(data.data);
				$('#script_tree').tree('loadData',treeData);
			}
		});
	}
	
	treeRefresh();
	function fillTempDiv(scriptId,data,selector,type){
		var reportListInfoDivTemp = selector;
		reportListInfoDivTemp.html('');
		
		rootDiv.find('.fa-plus').each(function(){
			var jqTemp = $(this);
			jqTemp.attr('scriptId',scriptId);
		})
		rootDiv.find('.fa-trash-o').each(function(){
			var jqTemp = $(this);
			jqTemp.attr('scriptId',scriptId);
		})
		
		if(!data){
			return ;
		}
		
		var tempDivInside = $('<div/>').css('text-align','center').css('margin','0 auto').css('width','90%').attr('style','margin:10px 0 0 14px;');
		var skin=Highcharts.theme.currentSkin;
		for(var x=0;x<data.length;x++){
			var reSpacing = $('<div/>').addClass('re-spacing');
			var bg = $('<div/>').addClass('scriptConfig-bg').attr('scriptName',data[x].fileName).attr('scriptConfigId',scriptId);
			var bgImgSrc ;
			switch (type) {
			case 1:
				bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/copy.png');
				bg.attr('scriptType','backUp');
				break;
			case 2:
				bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/recovery.png');
				bg.attr('scriptType','recovery');
				break;
			}
			
			var bgImg = $('<img>').attr('src',bgImgSrc).addClass('re-ico-position');
			bg.append(bgImg);
			var fot = $('<div/>').addClass('re-fot-l')
			.attr('style','width:98px;text-overflow: ellipsis;white-space: nowrap;overflow:hidden;')
			.attr('title',data[x].fileName)
			.append('<input type="checkbox" scriptName='+data[x].fileName+' >'+data[x].fileName);
			reSpacing.append(bg).append(fot);
			tempDivInside.append(reSpacing);
		}
		reportListInfoDivTemp.append(tempDivInside);
		
		reportListInfoDivTemp.find('.scriptConfig-bg').on('click',function(){
			var jqObj = $(this);
			var scriptName = jqObj.attr('scriptName');
			var scriptType = jqObj.attr('scriptType');
			var scriptConfigId = jqObj.attr('scriptConfigId');
			
			oc.util.ajax({
    			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScript.htm'),
    			data:{scriptName:scriptName,scriptConfigId:scriptConfigId,scriptType:scriptType},
    			successMsg:null,
    			success:function(data){
    				var dataTemp = data.data;
    				addOrUpdateScriptDialog({dlgAddScriptName:dataTemp.fileName,dlgAddScriptCMD:dataTemp.cmd,dlgAddScriptID:scriptConfigId},scriptType,'update');
    			}
    		});
		})
	}
	
	function parseData(data,levelNum,isSearch){
		var treeDataArr = new Array();
		
		var level = 0;
		if(!levelNum || levelNum<1){
			level=1;
		}else if(levelNum>5){
			level=5;
		}else{
			level = parseInt(levelNum);
		}
		
		for(var i=0;i<data.length;i++){
			var dataTemp = data[i];
			var temp = {};
			
			temp['id'] = dataTemp['id'];
			temp['level'] = dataTemp['level'];
			temp['type'] = dataTemp['type'];
			temp['text'] = dataTemp['text']+editBtnSpan;
			temp['state'] = 'open';
			if(dataTemp.children && dataTemp.children.length>0){
				temp['children'] = parseData(dataTemp.children,level+1,isSearch);
			}else if(dataTemp['type']==1){
				temp['state'] = 'closed';
			}else if(dataTemp['type']==2){
				var oidTemp =  dataTemp['oid'];
				if(!oidTemp){
					oidTemp = '--';
				}
				temp['text'] = '<div class="configScriptSpan" id='+dataTemp['id']+' type='+dataTemp['type']+'>'+dataTemp['text']+'('+oidTemp+')</div>'+editBtnSpan;
			}
			
			treeDataArr.push(temp);
		}
		return treeDataArr;
	}
	
	function addOrUpdateScriptDialog(dataObj,type,openType){
		var dlgAddScriptName = '',dlgAddScriptCMD = '',dlgAddScriptID = 0;
		if(dataObj){
			if(dataObj['dlgAddScriptName']){
				dlgAddScriptName = dataObj['dlgAddScriptName'];
			}
			if(dataObj['dlgAddScriptCMD']){
				dlgAddScriptCMD = dataObj['dlgAddScriptCMD'];
			}
			if(dataObj['dlgAddScriptID'] && dataObj['dlgAddScriptID']>0){
				dlgAddScriptID = dataObj['dlgAddScriptID'];
			}
		}
		
		var title = '';
		var openTypeStr = '';
		var dlgAddScriptNameInputDisable = '';
		
		var dialogStr1 = "";
		switch(type){
		case 'backUp':
			title = '备份';
			dialogStr1 = '${fileName}:保存的文件名</p>';
			break;
		case 'recovery':
			title = '恢复';
			dialogStr1 = '${fileName}:本地文件名<br>${newFileName}:保存的文件名</p>';
			break;
		}
		if(openType=='update'){
			dlgAddScriptNameInputDisable = 'disabled="true"';
			openTypeStr = '修改';
			title = title+openTypeStr;
		}else{
			openTypeStr = '新增';
			title = title+openTypeStr;
		}
		
		var temp = $('<form name="dialogForm" style="width:100%;height:100%;"><table style="width:100%;height:100%;"><tr height="100%"><td width="70%" height="100%">'+
				'<table style="width:100%;height:100%;"><tr height="20%"><td width="20%" align="right"><label>文件名称：</label></td><td  align="left" width="30%"><input type="text" name="dlgAddScriptName" '+dlgAddScriptNameInputDisable+' style="width:130px;" value="'+dlgAddScriptName+'" required="required"></td>'+
				'<td width="20%"  align="right"><label>协议：</label></td><td  align="left" width="30%"><select id="scriptType"  name="dlgAddScriptType" style="width:150px;" value="" ></select></td></tr>'+
				'<tr height="80%"><td  align="right" width="20%"><label>执行命令：</label></td><td colspan=3  align="left" width="20%"><textarea name="dlgAddScriptCMD" style="resize: none;margin: 0px 2px; height: 70%;width:100%;" >'+dlgAddScriptCMD+'</textarea></td>'+
				'</tr></table>'+
				'<td width="30%" style="padding-left:5px"><p class="notice-ico" ><br>'+
				'|:命令终结符<br>'+
				'${}:待替换占位符<br>'+
				'${enableUserName}:enable用户名<br>'+
				'${enablePassword}:enable密码<br>'+
				'${tftpIp}:tftpServer地址<br>'+
				dialogStr1+
				'</td></tr></table></form>'
				);
				var dlg = $('<div/>');
				dlg.append(temp);
				
				oc.ui.form({
					selector:dlg.find('form[name=dialogForm]'),
					combobox:[{
						selector:'#scriptType',
						placeholder : false,
						width:50,
						data:[
						      {id:'tftp',name:'tftp'}
				        ]
					}]
				});
				
				
				
				dlg.dialog({
				    title: title,
				    width : 700,
					height : 300,
				    onLoad:function(){
				    	_init(dlg);
				    },
					buttons:[{
				    	text: '保存',
				    	handler:function(){
				    		var protocolType = dlg.find('#scriptType').combobox('getValue');
				    		//name去除所有空格
				    		var fileName = dlg.find('input[name=dlgAddScriptName]').val().replace(/\s+/g,'');
				    		//cmd前后去空格
				    		var cmd = dlg.find('textarea[name=dlgAddScriptCMD]').val().replace(/(^\s*)|(\s*$)/g,'');
				    		
				    		if(!fileName){
				    			alert('请填写文件名称!');
				    			return ;
				    		}else if(!cmd){
				    			alert('请填写执行命令!');
				    			return;
				    		}
				    		if(openType=='update'){
				    			//修改
				    			oc.util.ajax({
									url: oc.resource.getUrl('portal/config/configscriptmanage/addConfigScriptModelByType.htm'),
									data : {saveType : type,configScriptId:dlgAddScriptID,fileName:fileName,type:protocolType,cmd:cmd},
									successMsg:null,
									success:function(data){
										if(data.data){
											alert(openTypeStr+'成功!');
											
											//刷新
						    				oc.util.ajax({
								    			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScriptModelByConfigScriptId.htm'),
								    			data:{configScriptId : dlgAddScriptID},
								    			successMsg:null,
								    			success:function(data){
								    				var dataTemp = data.data;
								    				var bakcUpdata = dataTemp['bakcUp'];
								    				fillTempDiv(dlgAddScriptID,bakcUpdata,rootDiv.find('#backUpConfigScriptDiv'),1);
								    				
								    				var recoverydata = dataTemp['recovery'];
								    				
								    				fillTempDiv(dlgAddScriptID,recoverydata,rootDiv.find('#recoveryConfigScriptDiv'),2);
								    			}
								    		});
											
											dlg.dialog('destroy');
										}else{
											alert(openTypeStr+'失败,请重试!');
										}
									}
								});
				    		}else{
				    			//新增,先判断名字是否有重复
				    			oc.util.ajax({
				        			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScript.htm'),
				        			data:{scriptName:fileName,scriptConfigId:dlgAddScriptID,scriptType:type},
				        			successMsg:null,
				        			success:function(data){
				        				var dataTemp = data.data;
				        				if(dataTemp){
				        					alert('文件名已存在!');
				        					return;
				        				}
				        				
				        				oc.util.ajax({
											url: oc.resource.getUrl('portal/config/configscriptmanage/addConfigScriptModelByType.htm'),
											data : {saveType : type,configScriptId:dlgAddScriptID,fileName:fileName,type:protocolType,cmd:cmd},
											successMsg:null,
											success:function(data){
												if(data.data){
													alert(openTypeStr+'成功!');
													
													//刷新
								    				oc.util.ajax({
										    			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScriptModelByConfigScriptId.htm'),
										    			data:{configScriptId : dlgAddScriptID},
										    			successMsg:null,
										    			success:function(data){
										    				var dataTemp = data.data;
										    				var bakcUpdata = dataTemp['bakcUp'];
										    				fillTempDiv(dlgAddScriptID,bakcUpdata,rootDiv.find('#backUpConfigScriptDiv'),1);
										    				
										    				var recoverydata = dataTemp['recovery'];
										    				
										    				fillTempDiv(dlgAddScriptID,recoverydata,rootDiv.find('#recoveryConfigScriptDiv'),2);
										    			}
										    		});
													
													dlg.dialog('destroy');
												}else{
													alert(openTypeStr+'失败,请重试!');
												}
											}
										});
				        			}
				        		});
				    			
				    		}
				    		
				    	}
				    },{
				    	text: '取消',
				    	handler:function(){
				    		dlg.dialog('destroy');
				    	}
				    }]
				});
	}
	
	function configAddDirectoryDialog(dataObj){
		var directoryId = 0;
		if(dataObj['directoryId']) directoryId = dataObj['directoryId'];
		var configScriptDirectoryName = '';
		if(dataObj['configScriptDirectoryName'])configScriptDirectoryName = dataObj['configScriptDirectoryName'];
		
		var title = '添加目录';
		if(directoryId>0){
			title = '修改目录';
		}
		var temp = $('<div><form id="" class="oc-form float" ><div class="form-group">'+
				'<label>目录名称：</label><input id="addConfigScriptDirectoryName" type="text" name="" style="width:150px;" value="'+configScriptDirectoryName+'" placeholder="名称必填">'+
				'</div></form></div>');
				var dlg = $('<div/>');
				dlg.append(temp);
				dlg.dialog({
				    title: title,
				    width : 360,
					height : 120,
				    onLoad:function(){
				    	_init(dlg);
				    },
					buttons:[{
				    	text: '保存',
				    	handler:function(){
				    		var levelThis = dataObj['level'] ? dataObj['level'] : currentClickSetchil.level;
				    		var parentId = 0;
				    		if(directoryId==0){
				    			levelThis = levelThis+1;
					    		if(levelThis>5){
					    			alert('最多只能添加5级目录');
					    			return;
					    		}
					    		parentId = dataObj['id'] ? dataObj['id'] : currentClickSetchil.id;
				    		}
				    		var name = dlg.find("#addConfigScriptDirectoryName").val();
				    		if(!name){
				    			alert('请填写目录名!');
				    			return ;
				    		}
				    		
				    		oc.util.ajax({
				    			url: oc.resource.getUrl('portal/config/configscriptmanage/addConfigScriptDirectory.htm'),
				    			data:{name:name,parentId:parentId,level:levelThis,directoryId:directoryId},
				    			successMsg:null,
				    			success:function(data){
				    				if(data.data['saveState']){
				    					treeRefresh();
				    					dlg.dialog('destroy');
				    				}else if(data.data['nameIsRepeat']){
				    					alert('目录名称不能重复,请重新输入!');
				    				}else{
				    					alert('添加失败,请重试!');
				    				}
				    			}
				    		});
				    		
				    	}
				    },{
				    	text: '取消',
				    	handler:function(){
				    		dlg.dialog('destroy');
				    	}
				    }]
				});
	}
	
	function configAddScriptDialog(dataObj){
		var scriptId = 0;
		if(dataObj['scriptId']) scriptId = dataObj['scriptId'];
		var configScriptName = '';
		if(dataObj['configScriptName'])configScriptName = dataObj['configScriptName'];
		var configScriptOID = '';
		if(dataObj['configScriptOID'])configScriptOID = dataObj['configScriptOID'];
		
		var oidCheckFlag = true;
		var title = '添加配置';
		if(scriptId>0){
			title = '修改配置';
			oidCheckFlag = false;
		}
		var temp = $('<div><form id="" class="oc-form col1" ><div class="form-group">'+
				'<label width="80px">设备类型：</label><input id="addConfigScriptName" type="text" name="" style="width:150px;" value="'+configScriptName+'" placeholder="必填"></div>'+
				'<div class="form-group"><label width="80px">设备OID：</label><input id="addConfigScriptOID" type="text" name="" style="width:150px;" value="'+configScriptOID+'" placeholder="OID必填">'+
				'</div></form></div>');
				var dlg = $('<div/>');
				dlg.append(temp);
				dlg.dialog({
				    title: title,
				    width : 360,
					height : 160,
				    onLoad:function(){
				    	_init(dlg);
				    },
					buttons:[{
				    	text: '保存',
				    	handler:function(){
				    		var name = dlg.find("#addConfigScriptName").val();
				    		if(!name){
				    			alert('请填写配置类型!');
				    			return ;
				    		}
				    		var oid = dlg.find("#addConfigScriptOID").val();
				    		if(!oid || oid.trim()==""){
				    			alert('请填写OID!');
				    			return ;
				    		}
				    		
				    		//格式校验 
				    		var reg = /(^(\.(\d+))+$)|(^((\d+\.)+\d+$))/;
				    		
				    		if(!reg.test(oid)){
				    			alert('请注意OID格式,且只能由数字和点构成!');
				    			return ;
				    		}
				    		
			    			//验证OID
				    		oc.util.ajax({
				    			url: oc.resource.getUrl('portal/config/configscriptmanage/getConfigScriptByOID.htm'),
				    			data:{oid:oid},
				    			successMsg:null,
				    			success:function(data){
				    				if(data.data && data.data[0].id!=scriptId){
				    					var tempData = data.data[0];
				    					alert('该OID已被使用,请修改!');
				    					return ;
				    				}else{
				    					oc.util.ajax({
							    			url: oc.resource.getUrl('portal/config/configscriptmanage/addConfigScript.htm'),
							    			data:{name:name,oid:oid,id:currentClickSetchil.id,scriptId:scriptId},
							    			successMsg:null,
							    			success:function(data){
							    				if(data.data){
							    					treeRefresh();
//							    					rootDiv.find('#script_config_main_center').find('.panel-title').html('');
								    				currentClickSetchil = {};
//								    				fillTempDiv(null,rootDiv.find('#backUpConfigScriptDiv'));
//								    				fillTempDiv(null,rootDiv.find('#recoveryConfigScriptDiv'));
							    					dlg.dialog('destroy');
							    				}else{
							    					alert('添加失败,请重试!');
							    				}
							    			}
							    		});
				    				}
				    			}
				    		});
				    		
				    	}
				    },{
				    	text: '取消',
				    	handler:function(){
				    		dlg.dialog('destroy');
				    	}
				    }]
				});
	}
})