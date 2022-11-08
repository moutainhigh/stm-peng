$(function(){
	function userDeal(){
		this.mainLayoutDiv = $('#mainLayout');
		this.datagridDiv = this.mainLayoutDiv.find('#userGrid');
		this.authEdit = "authEdit";
		this.authSelect = "authSelect";
	}
	
	//datagrid添加方法，获取未勾选查看权限集合
	$.extend($.fn.datagrid.methods,{
		getUnChecked:function(jq){
			var rr = [];
			var rows = jq.datagrid('getRows');
			jq.datagrid('getPanel').find('div.datagrid-cell input[name="viewCheck"][checked!="checked"]').each(function(){
				var index = $(this).parents('tr:first').attr('datagrid-row-index');
				rr.push(rows[index]);
			});
			return rr;
		}
	});
	
	function sortRowsByMetricId(rows,fieldName,sortType){
		for (var i = 0; i < rows.length; i++) {
			for (var j = i; j < rows.length; j++) {
				var value_1 = null;
				var value_2 = null;
				
				if(rows[i][fieldName + "Value"]){
					value_1 = rows[i][fieldName + "Value"];
				}else{
					value_1 = rows[i][fieldName];
				}
				if(rows[j][fieldName + "Value"]){
					value_2 = rows[j][fieldName + "Value"];
				}else{
					value_2 = rows[j][fieldName];
				}
				value_1 = parseInt(value_1);
				value_2 = parseInt(value_2);
				if(isNaN(value_1)){
					value_1 = -1;
				}
				if(isNaN(value_2)){
					value_2 = -1;
				}
				
				if(sortType == 'asc'){
					if (value_1 > value_2) {
						var temp = rows[i];
						rows[i] = rows[j];
						rows[j] = temp;
					}
				}else{
					if (value_1 < value_2) {
						var temp = rows[i];
						rows[i] = rows[j];
						rows[j] = temp;
					}
				}
			}
		}
		return rows;
	}
	
	function addSort(rows,fieldId,gridObj,target){
		 var targetRows = null;
		 if(target.attr('class').indexOf('datagrid-sort-asc') < 0 && target.attr('class').indexOf('datagrid-sort-desc') < 0){
			 gridObj.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			 target.addClass('datagrid-sort-asc');
			 targetRows = sortRowsByMetricId(rows,fieldId,'asc');
		 }else if(target.attr('class').indexOf('datagrid-sort-asc')>0 || target.attr('class').indexOf('datagrid-sort-asc')==0){
			 target.attr('class',target.attr('class').replace('datagrid-sort-asc','datagrid-sort-desc'));
			 targetRows = sortRowsByMetricId(rows,fieldId,'desc');
		 }else if(target.attr('class').indexOf('datagrid-sort-desc')>0 || target.attr('class').indexOf('datagrid-sort-desc')==0){
			 target.attr('class',target.attr('class').replace('datagrid-sort-desc','datagrid-sort-asc'));
			 targetRows = sortRowsByMetricId(rows,fieldId,'asc');
		 }
		 
		 var localData = {"data":targetRows,"code":200};
		 gridObj.selector.datagrid('loadData',localData);
		 
	}
	
	userDeal.prototype = {
			name:null,//业务系统管理者
			grid:undefined,
			authEdit:undefined,
			authSelect:undefined,
			datagridDiv:undefined,
			queryForm:undefined,
			mainLayoutDiv:undefined,
			init:function(){
				var that = this;
				this.mainLayoutDiv.layout({
					fit:true
				});
				this.creatQueryForm();
				this.westMenu();
			},
			creatQueryForm:function(){
				var that = this;
				//域下拉选择框
				var domains = oc.index.getDomains();
				this.queryForm = oc.ui.form({
					selector:this.mainLayoutDiv.find('.oc-form'),
					combobox:[{
						selector:this.mainLayoutDiv.find('.oc-form').find('#yu'),
						data:domains,
						placeholder : '${oc.local.ui.select.placeholder}'
					}]
				});
				this.queryForm.selector.find(":input[name=queryName]").keyup(function(e){
					e.keyCode==13&&(that.grid.octoolbar.jq.find(".queryBtn").trigger('click'));
				});
			},
			creatDataGrid:function(bizId){
				var that = this;
				var queryFormObj = this.queryForm.selector;
				var grid = oc.ui.datagrid({
					selector:this.datagridDiv,
					url:oc.resource.getUrl('portal/business/user/getUserlistByBizId.htm?bizid='+bizId),
					queryParams:{
						queryName:'',
						domainId:0,
					},
					pagination:false,
					singleSelect:true,
					checkOnSelect:false,
					selectOnCheck:false,
					hideReset:true,
					hideSearch:true,
					columns:[[{
						field:'user_id',
						title:'user_id',
						hidden:true
					},{
						field:'biz_id',
						title:'biz_id',
						hidden:true
					},{
						field:'name',
						title:'用户名',
						align:'left',
						width:80,
					},{
						field:'edit',
						title:'编辑权限',
						align:'left',
						sortable:true,
						width:50,
						sortable:true,
						formatter:function(value,rowData,rowIndex){
							var check = value == 1 ? 'checked="true"' : '';
							return '<input type="checkbox" name="editSelect" disabled="disabled" '+check+'>';
						}
					},{
						field:'view',
						title:'<input type="checkbox" id="viewSelectAll" name="viewSelectAll">查看权限',
						align:'left',
						sortable:true,
						width:50,
						sortable:true,
						formatter:function(value,rowData,rowIndex){
							var check = value == 1 ? 'checked="true"' : '';
							var disabled = rowData.edit == 1 ? 'disabled="disabled"' : '';
                            var user_id = rowData.user_id;
                            var biz_id = rowData.biz_id;
                            return '<input type="checkbox" biz_id='+biz_id+' user_id='+user_id+' name="viewCheck" '+disabled+' '+check+'>';
						}
					}]],
					octoolbar:{
						left:[
						      queryFormObj
						],
						right:[]
					},
					onLoadSuccess:function(data){
						//全选
						$('#viewSelectAll').click(function(){
							if($(this).is(':checked')){
								$('input[name="viewCheck"][disabled!="disabled"]').prop('checked',true);
							}else{
								$('input[name="viewCheck"][disabled!="disabled"]').removeAttr('checked');
							}
						});
						
						$(".queryBtn").linkbutton('RenderLB',{
			    				iconCls:'fa fa-search',
			    				onClick : function(){
			    					var yu = that.mainLayoutDiv.find('.oc-form').find('#yu').combobox('getValue');
			    					grid.selector.datagrid('options').queryParams = {
			    						queryName:that.queryForm.selector.find("input[name=queryName]").val(),
			    						domainId:yu=='' || yu==null ? 0 : yu
			    					};
			    					grid.reLoad();
			    				}
			    			});
						$(".resetBtn").linkbutton('RenderLB',{
			    				iconCls:'icon-reset',
			    				onClick : function(){
			    					that.mainLayoutDiv.find('.oc-form').find('#yu').combobox('setValue','');
			    					that.queryForm.find(":input[name=queryName]").val("");
			    				}
			    		});
						
						$('input[name="viewCheck"]').on('click',function(){
							if($(this).prop('checked')){
								$(this).attr('checked',true);
							}else{
								$(this).removeAttr('checked');
							}
						});
						
						//添加排序方法
						var rows = $.extend(true,[],that.grid.selector.datagrid('getRows'));
						that.grid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().unbind('click');
						that.grid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().on('click',function(){
							 var target = $(this);
				    		 var fieldId = target.parent().attr('field');
				    		 if(fieldId == 'name'){
				    			 return;
				    		 }
				    		 addSort(rows,fieldId,that.grid,target);
				    	 });
					},
					loadFilter:function(data){
			        	return {total:data.data.length,rows:data.data};
			        }
				});
				
				return grid;
			},
			westMenu:function(){
				var that = this;
				var treeNodesString;
				oc.util.ajax({
					url:oc.resource.getUrl('portal/business/service/getAllBizList.htm'),
					async:false,
					success:function(data) {
						treeNodesString = that.transToTree(data.data);
					}
				});
				var menuDiv = this.mainLayoutDiv.layout('panel','west').find('#menuList');
				menuDiv.tree({
					data:treeNodesString,
					onSelect:function(node){
						//清空查询表单
						$(".resetBtn").trigger('click');
						
						that.name = node.attributes.managerName;
						
						//点击菜单添加active
						$('#menuList').find('li').removeClass('active');
						var nodeTarget = $(node.target);
						nodeTarget.parent().addClass('active');
						
						if(that.grid){
							var yu = that.mainLayoutDiv.find('.oc-form').find('#yu').combobox('getValue');
							that.grid.selector.datagrid('options').url = oc.resource.getUrl('portal/business/user/getUserlistByBizId.htm?bizid='+node.id);
							that.grid.selector.datagrid('options').queryParams = {
	    						queryName:that.queryForm.selector.find("input[name=queryName]").val(),
	    						domainId:yu=='' || yu==null ? 0 : yu
	    					};
							that.grid.reLoad();
						}else{
							that.grid = that.creatDataGrid(node.id);
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
						li.find('span.tree-title').addClass('text').css("line-height","40px");
						li.find('div').addClass('tree-group');
						li.find('span.tree-indent').addClass('tree-group-indent');
						
						//修改业务名过长
						menuDiv.find('li').find('span.tree-title').each(function(){
							var text = $(this).text();
							$(this).attr('title',text);
							$(this).text(text.length > 10 ? text.substring(0,10)+'..' : text);
						});
						
						if(data.length>0){
							var firstNode = menuDiv.tree('find',data[0].id);
							menuDiv.tree('select',firstNode.target);
						}
						
					}
				});
			},
			transToTree:function(rows){	//菜单树json封装
			  var nodes = [];  
			    for(var i=0; i<rows.length; i++){  
			        var row = rows[i];  
			        nodes.push({  
		                    id:row.id,
		                    text:row.name,
		                    attributes:row
			         });  
			    }  
			  return nodes;  
			},
			save:function(){
				var that = this;
				// var rowSet = that.grid.selector.datagrid('getUnChecked');
				var rows = that.grid.selector.datagrid('getRows');
                var rowSet = that.grid.selector.datagrid('getRows');
                for(var i = 0 && rows.length > 0; i < rows.length; ++i){
                    var userId =  rows[i].user_id;
                    var bizId =  rows[i].biz_id;
                    var checked = $('input[name="viewCheck"][user_id='+userId+'][biz_id='+bizId+']').attr("checked");
                    for(var j = 0 && rowSet.length > 0; j < rowSet.length; ++j){
                        var uId =  rowSet[j].user_id;
                        var bId =  rowSet[j].biz_id;
                        if(userId == uId && bizId == bId){
                            if(checked != null){
                                rowSet[j].view = 1;
                            }else{
                                rowSet[j].view = 0;
                            }
                        }
                    }
                }
				var biz_id = rowSet.length > 0 ? rowSet[0].biz_id : rows[0].biz_id;
				var allRows = new Array();
				for (var i = 0; i < rows.length; i++) {
					allRows.push(rows[i].user_id);
				}
				oc.util.ajax({
					url:oc.resource.getUrl('portal/business/user/update.htm'),
					data:{
						biz_id:biz_id,
						rowSet:rowSet,
						rows:that.queryForm.selector.find("input[name=queryName]").val() == null || '' || undefined ? null : allRows
					},
					success:function(data) {
						alert(data.data);
					}
				});
			}
	}

	
	oc.ns('oc.biz.user');
	var selfObj = null;
	oc.biz.user = {
			deal:function(){
				selfObj = new userDeal();
				selfObj.init();
			},
			save:function(){
				selfObj.save();
			}
	};
});