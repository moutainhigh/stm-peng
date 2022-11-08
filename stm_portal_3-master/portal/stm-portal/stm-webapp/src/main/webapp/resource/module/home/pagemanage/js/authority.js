$(function(){
	oc.ns('oc.module.home.edit.authority');
	
	function authority(){
		
	}
	
	authority.prototype = {
			constructor:authority,
			mainDiv:undefined,
			dialogWindow:undefined,
			groupPicktree:undefined,
			leftTree:undefined,
			rightTree:undefined,
			rightDomainIds:[],
			layoutId:undefined,
			editDomain:false,
			open : function(){
				var that = this,dialogDiv = $("<div/>"),dialogMain="";
				
				dialogMain+='<div id="mainLayout" class="easyui-layout" style="width:100%;height:100%;">';
				dialogMain+='<div id="westLayout" data-options="region:\'west\',collapsible:false,">';
				dialogMain+='<div id="menuList"></div>';
				dialogMain+='</div>';
				dialogMain+='<div id="centerLayout" data-options="region:\'center\'">';
				dialogMain+='<form onkeydown="if(event.keyCode==13)return false;" class="oc-form col1">';
				dialogMain+='<div class="form-group" style="margin:0px 0px 10px 0px">';
				dialogMain+='<div><input style="width:190px" type="text" id="domainNameInput" maxlength=15 placeholder="输入域名" />';
				dialogMain+='<a class="queryBtn">查询</a>';
				dialogMain+='<span>将页面的查看权限授予下列域的用户</span>';
				dialogMain+='</div>';
				dialogMain+='</div>';
				dialogMain+='</form>';
				dialogMain+='<div id="domain">';
				dialogMain+='<div id="leftDomainTree"></div>';
				dialogMain+='<div id="midButton">';
				dialogMain+='<div class="midButton-context tab-tree-arrow">';
				dialogMain+='<a id="toRight" class="to_left tree-arrow-right"></a>';
				dialogMain+='<a id="toLeft" class="to_right tree-arrow-left"></a>';
				dialogMain+='</div>';
				dialogMain+='</div>';
				dialogMain+='<div id="rightDomainTree"></div>';
				dialogMain+='</div>';
				dialogMain+='</div>';
				dialogMain+='</div>';
				dialogMain+='';
				
				this.dialogWindow = dialogDiv.dialog({
					title:'权限设置',
					width:750,
					height:500,
					modal:true,
//					href:oc.resource.getUrl('resource/module/home/edit/authorityMain.html'),
					onLoad:function(){
//						oc.biz.user.deal();
					},
					modal:true,
					onOpen:function(){
						dialogDiv.append(dialogMain);
						that.mainDiv = dialogDiv.find('#mainLayout').layout({
							fit:true
						});
						that.westMenu();
						$(".queryBtn").linkbutton('RenderLB',{
		    				iconCls:'icon-search',
		    				onClick : function(){
		    					var searchContent = $('#domainNameInput').val();
		    					that.search(searchContent);
		    				}
		    			});
						$('#toRight').click(function(){
							that.toRight();
						});
						$('#toLeft').click(function(){
							that.toLeft();
						})
					},
					buttons:[{
						id:'btn_Sub',
						text:'确定',
						iconCls:'fa fa-check-circle',
						handler:function(){
							that.save();
						}
					},{
						id:'btn_Cancel',
						text:'取消',
						iconCls:'fa fa-times-circle',
						handler:function(){
							dialogDiv.dialog('destroy');
						}
					}]
				});
			},
			creatTree:function(div){
				var tree = div.tree({
					data:[],
					checkbox:true,
				});
				return tree;
			},
			transToTree:function(rows){	//菜单树json封装
				var nodes = [];
				for (var i = 0; i < rows.length; i++) {
					var row = rows[i];
					var nameText = row.name.length > 13 ? (row.name.substring(0,13)+'...') : row.name;
					nodes.push({
						id : row.id,
						text : '<span title="'+row.name+'">' + nameText + '</span>',
						iconCls : 'icon-blank',
						attributes : row
					});
				}
				return nodes;  
			},
			westMenu:function(){
				var that = this;
				var treeNodesString = null,
					leftDomainQueryUrl = "system/home/layout/domain/getLeftDomains.htm",
					rightDomainQueryUrl = "system/home/layout/domain/getRightDomains.htm";
				oc.util.ajax({
					url:oc.resource.getUrl('system/home/layout/getAllLayoutList.htm'),
					async:false,
					success:function(data) {
						treeNodesString = that.transToTree(data.data);
					}
				});
				var menuDiv = this.mainDiv.layout('panel','west').find('#menuList'),
					leftDiv = this.mainDiv.layout('panel','center').find('#leftDomainTree'),
					rightDiv = this.mainDiv.layout('panel','center').find('#rightDomainTree');
				menuDiv.tree({
					data:treeNodesString,
					onBeforeSelect:function(node){
						var newRightData = that.rightTree.tree('getRoots'),newDomainIds = new Array(),layout = that.layoutId;
						
						$.each(newRightData, function(i, item){
							newDomainIds.push(item.id);
						});
						if(that.rightDomainIds.sort().toString() != newDomainIds.sort().toString()){
							that.editDomain = true;
						}
						if(that.editDomain){
							var comfirmDialog = $('<div/>').dialog({
							    title: '确认消息',
							    width: 220,
							    height: 120,
							    content:'权限已被修改，是否保存？',
							    modal: true,
							    closable:false,
							    buttons:[{
							    	text:'确定',
							    	iconCls:"fa fa-check-circle",
									handler:function(){
										that.save(layout, newDomainIds);
										comfirmDialog.panel('destroy');
									}
							    },{
							    	text:'取消',
							    	iconCls:"fa fa-times-circle",
							    	handler:function(){
							    		//取消
										comfirmDialog.panel('destroy');
							    	}
							    }]
							});
						}
					},
					onSelect:function(node){
						//清空查询表单
						$('#domainNameInput').val("");
						
						//点击菜单添加active
						$('#menuList').find('li').removeClass('active');
						var nodeTarget = $(node.target);
						nodeTarget.parent().addClass('active');
						
						that.layoutId = node.id;
						that.editDomain = false;
						
						if(that.leftTree && that.rightTree){
							oc.util.ajax({
								url:oc.resource.getUrl(leftDomainQueryUrl+'?layoutId='+node.id),
								success:function(data) {
									var treeString = that.transToTree(data.data);
									that.leftTree.tree('loadData',treeString);
								}
							});
							
							oc.util.ajax({
								url:oc.resource.getUrl(rightDomainQueryUrl+'?layoutId='+node.id),
								success:function(data) {
									var domainIdsArr = new Array();
									$.each(data.data, function(i,item){
										domainIdsArr.push(item.id);
									});
									that.rightDomainIds = domainIdsArr;
									var treeString = that.transToTree(data.data);
									that.rightTree.tree('loadData',treeString);
								}
							});
						}else{
							alert('else');
						}
					},
					onLoadSuccess:function(node,data){
						//添加菜单样式
						$('#westLayout').addClass('resource_strategy_detail_west');
						menuDiv.addClass('oc-window-leftmenu');
						var li = $('#menuList').find('li');
						$('#menuList').append('<ul class="oc-nav-sub-list">');
						$('#menuList').find('ul').append(li);
						li.find('span.tree-icon').removeClass('tree-file').addClass('s-arrow-dot');
						li.find('span.tree-title').addClass('text').addClass('Index-authority');
						li.find('div').addClass('tree-group');
						li.find('span.tree-indent').addClass('tree-group-indent');
						
						//修改业务名过长
						menuDiv.find('li').find('span.tree-title').each(function(){
							var text = $(this).text();
							$(this).attr('title',text);
							$(this).text(text.length > 10 ? text.substring(0,10)+'..' : text);
						});
						
						that.leftTree = that.creatTree($('#leftDomainTree'));
						that.rightTree = that.creatTree($('#rightDomainTree'));
						
						if(data.length>0){
							var firstNode = menuDiv.tree('find',data[0].id);
							menuDiv.tree('select',firstNode.target);
						}
					}
				});
			},
			save:function(layout,domains){
				var that = this;
				var roots = that.rightTree.tree('getRoots'),queryDomainIds = "",queryLayoutId = "";
				if(layout && domains){
					//切换页面保存
					queryDomainIds = domains.join();
					queryLayoutId = layout;
				}else{
					//点击确定保存
					for(var i=0;i<roots.length;i++){
						queryDomainIds += (roots[i].id + ",");
					}
					if(queryDomainIds){
						queryDomainIds = queryDomainIds.substring(0,queryDomainIds.length - 1);
					}
					queryLayoutId = that.layoutId;
				}
				oc.util.ajax({
					url:oc.resource.getUrl('system/home/layout/domain/saveDomainsByLayoutId.htm'),
					data:{
						layoutId:queryLayoutId,
						domainsString:queryDomainIds
					},
					success:function(data) {
						alert(data.data);
						//切换页面不用关闭面板
						if(!layout && !domains){
							that.dialogWindow.panel('destroy');
						}
					}
				});
			},
			search:function(searchContent){
				var that = this;
				var leftDomainQueryUrl = "system/home/layout/domain/getLeftDomains.htm";
				if(that.leftTree){
					oc.util.ajax({
						url:oc.resource.getUrl(leftDomainQueryUrl),
						data:{
							layoutId: that.layoutId,
							searchContent: searchContent
						},
						success:function(data) {
							var searchData = $.extend([],data.data);
							var rightData = that.rightTree.tree('getRoots');
							for(var i=0; i<data.data.length; i++){
								for(var j=0; j< rightData.length; j++){
									if(data.data[i].id == rightData[j].id){
										searchData.splice(0,1);
									}
								}
							}
							
							var treeString = that.transToTree(searchData);
							that.leftTree.tree('loadData',treeString);
						}
					});
				}
			},
			toRight:function(){
				var that = this;
				var checkedNode = that.leftTree.tree('getChecked');
				for(var index in checkedNode){
					that.leftTree.tree('remove',checkedNode[index].target);
					that.rightTree.tree('append',{
						data:{
							id : checkedNode[index].id,
							text : checkedNode[index].text,
							iconCls : checkedNode[index].iconCls,
							attributes : checkedNode[index].attributes
						}
					});
				}
			},
			toLeft:function(){
				var that = this;
				var checkedNode = that.rightTree.tree('getChecked');
				for(var index in checkedNode){
					that.rightTree.tree('remove',checkedNode[index].target);
					that.leftTree.tree('append',{
						data:{
							id : checkedNode[index].id,
							text : checkedNode[index].text,
							iconCls : checkedNode[index].iconCls,
							attributes : checkedNode[index].attributes
						}
					});
				}
			}
	}
	
	oc.module.home.edit.authority = {
			open:function(){
				var selfObj = new authority();
				selfObj.open();
				var subButtonDom = $('#btn_Sub').find('span:first').html();
				$('#btn_Sub').find('span:first').html('').append('<div class="btn-l"><div class="btn-r"><div class="btn-m">'+subButtonDom+'</div></div></div>');
				var cancleButtonDom = $('#btn_Cancel').find('span:first').html();
				$('#btn_Cancel').find('span:first').html('').append('<div class="btn-l"><div class="btn-r"><div class="btn-m">'+cancleButtonDom+'</div></div></div>');

			}
	}
});