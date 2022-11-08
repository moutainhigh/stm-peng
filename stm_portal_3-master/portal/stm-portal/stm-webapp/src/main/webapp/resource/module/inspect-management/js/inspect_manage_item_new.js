(function() {
	
	function InspectItem(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	InspectItem.prototype = {
		cfg : undefined,
		open : function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), 
				that = this, 
				type = that.cfg.type;
			    
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_manage_item_new.html'),
				title : '编辑巡检项<label style="font-weight:normal">(每章节最多支持' + oc.module.inspect.plan.getProperties().itemMax + '巡检项/已设置<a class="inspectItemCount">0</a>项)</label>',
				width: 1020,
			    height: 600,
			    top: 30,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
					dlg.dialog("destroy");//add by sunhailiang on 20170615
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var items = that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getData');
						var submit = true;
						if(items && items.length != 0) {
							//遍历结果，执行endEdit
							var loop = function(items) {
								for(var i2=0,len=items.length; i2<len; i2++) {
									var item = items[i2];
									that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('endEdit', item.id);
									if(item.children && item.children.length != 0) {
										loop(item.children);
									}
								}
							}
							loop(items);
						}
						var items1 = that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getData');
						if(items1 && items1.length != 0) {
							var check = function(items1) {
								for(var i2=0,len=items1.length; i2<len; i2++) {
									var item = items1[i2];
									var name = item.inspectPlanItemName;
									if(item.type==1){
//										var name = item.inspectPlanItemName;
										var desc = item.inspectPlanItemDescrible;
										if(name==''||name==undefined||name==null){
											alert("巡检项不能为空");
											submit = false;
											return false;
										}else{
											if(name.length>20){
												name = name.substring(0,20);
												name = name+'......';
												alert("巡检项:'"+name+"'名字不能长度大于20个字符");
												submit = false;
												return false;
											}
										}
										if(desc!=null&&desc!=''&&desc!=undefined&&desc.length>20){
											alert("巡检项:'"+name+"'描述长度不能大于20个字符");
											submit = false;
											return false;
										}
									}
									 var prefix = item.inspectPlanItemReferencePrefix;
									 var subfix = item.inspectPlanItemReferenceSubfix;
									 if(prefix!=null&&prefix!=''&&prefix!=undefined&&prefix.length>20){
										 alert("巡检项:"+name+"参考值前缀长度不能大于20个字符");
											submit = false;
											return false;
									 }
									 if(subfix!=null&&subfix!=''&&subfix!=undefined&&subfix.length>20){
										 alert("巡检项:"+name+"参考值后缀不能长度大于20个字符");
											submit = false;
											return false;
									 }
									 var unit = item.inspectPlanItemUnit;
									 if(unit!=null&&unit!=''&&unit!=undefined&&unit.length>20){
										 alert("巡检项:"+name+"单位长度不能大于20个字符");
											submit = false;
											return false;
									 }
									 var conditionDesc = item.itemConditionDescrible;
									 if(conditionDesc!=null&&conditionDesc!=''&&conditionDesc!=undefined&&conditionDesc.length>20){
										 alert("巡检项:"+name+"情况概要长度不能大于20个字符");
											submit = false;
											return false;
									 }
									 var planItemValue = item.inspectPlanItemValue;
									 if(planItemValue!=null&&planItemValue!=''&&planItemValue!=undefined&&planItemValue.length>30){
										 alert("巡检项:"+name+"巡检值长度不能大于30个字符");
											submit = false;
											return false;
									 }
									 if(item.children && item.children.length != 0) {
										 check(item.children);
									}
									 
									 
								}
							}
							check(items1);
						}

						if(submit){
							var newItems = that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getData') || [];
							oc.util.ajax({
				                url:oc.resource.getUrl('inspect/plan/updateItems.htm'),
				                timeout:0,
				                data:{
				                	json:JSON.stringify(newItems),
				                	dirId : that.cfg.dirId
				                },
				                async: false,
				                error: function(request) {
				                    alert("Connection error");
				                },
				                success: function(data) {
				                	var cb = that.cfg.refreshTreegrid;
				                	if(cb){
				                		cb();
				                	}
				                	
				                	alert('提交成功');
				                }
				            });	
							if(that._deleteIdList.length > 0){
								oc.util.ajax({
									url : oc.resource.getUrl('inspect/plan/delItems.htm'),
									data : {itemIds: that._deleteIdList},
									async:false,
									successMsg : null,
									success : function(data) {
									}
								});
							}
							dlg.dialog("close");
						}
					
						}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						var cb = that.cfg.refreshTreegrid;
	                	if(cb){
	                		cb();
	                	}
						dlg.dialog("close");
					}
				}]
			});
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_mainDiv : undefined,
		_addForm : undefined,
		_id : '#item_add_mian_div_new',
		_deleteID : undefined,
		_returnData : [],
		__treegrid : undefined,
		_index : 0,
		_treeid : -1,
		_treechildid:-100000,
		_deleteIdList:[],
		_init : function(dlg) {
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			var $inspect = that._mainDiv;
			that._mainDiv.find(".relation-item").click(addRelationChildItem);
			$inspect.on('click','.add-inspect-item-icon',addNewItem);
			
			  var type = that.cfg.type;
			  that._mainDiv.find(".china-oc-inspect-catalog").html(that.cfg.dirName);
			  that._mainDiv.find(".china-oc-inspect-item-desc").html(that.cfg.dirDesc);
			  that._mainDiv.find('.china-oc-inspect-reportname').html(that.cfg.reportName);
			  that._mainDiv.data("editType",type);
			  var selector = 'china-oc-inspect-management-treegrid';
			
			  
			  that._treegrid = that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid({
					loader: function(param, success) { //获取treegrid的数据
						oc.util.ajax({
							url : oc.resource.getUrl('inspect/plan/loadItem.htm'),
							data : {catalogId: that.cfg.dirId},
							async:false,
							successMsg : null,
							success : function(data) {
								if(data.code == 200) {
									$('.inspectItemCount').html(data.data.length);
									success(data.data);
								}
							}
						});
					},
					dnd:true,
					fit: false,
					width : '1008px',
					height : '346px',
				    idField: 'id',
				    treeField: 'inspectPlanItemName',
				    columns:[[
				         {
				        	 field:'inspectPlanItemName',title:'巡检项',sortable:false,width:210,align:'left',
				        	 editor:{
				        		/* type:'validatebox',
				        		 options:{
				        			 required:false,
				        			 validType:"length[1,20]"
				        		 }*/
				        		type:'text'
				        	 },
				        	 formatter: function(value, row) {
				        		 if(!row.inspectPlanItemName) {
				        			 return '';
				        		 } else {
				        			 return row.inspectPlanItemName;
				        		 }
				        	 }
				         },
				         {
				        	 field:'inspectPlanItemDescrible',title:'描述',sortable:false,width:280,align:'left',
				        	 editor:{
				        		/* type:'validatebox',
				        		 options:{
				        			 required:false,
				        			 validType:"length[0,20]"
				        		 }*/
				        		 type:'text'
				        	 },
				        	 formatter: function(value, row) {
				        		 if(!row.inspectPlanItemDescrible) {
				        			 return '';
				        		 } else {
				        			 return row.inspectPlanItemDescrible;
				        		 }
				        	 }
				         },
				         {
				        	 field:'inspectPlanItemReferencePrefix',title:'参',sortable:false,width:50,align:'right',
				        	 editor:{
				        		 type:'text'
				        	 },
				        	 formatter: function(value,row) {
				        			 var prefix = (row.inspectPlanItemReferencePrefix == null || row.inspectPlanItemReferencePrefix == '') ? '' : row.inspectPlanItemReferencePrefix;
				        			 if(prefix!=''&&prefix.length > 6){
				        				 var check = prefix.substring(0,5);
				        				 return '<span title="'+prefix+'">'+check+'</span>';
				        			 }else{
				        				 return prefix;
				        			 }
				        			 
				        	 }
				         },
				         {
				        	 field:'#',title:'考',sortable:false,width:4,align:'center',
				        	 formatter: function(value,row) {
				        		 if(row.children == null || row.children.length == 0) {
				        			 return '<span style="display:inline-block;width:16px"> ~ </span>';
				        		 } else {
				        			 return '<span style="display:inline-block;width:16px"> &nbsp; </span>';
				        		 }
				        	 }
				         },
				         {
				        	 field:'inspectPlanItemReferenceSubfix',title:'值',sortable:false,width:50,align:'left',
				        	 editor:{
				        		 type:'text'
				        	 },
				        	 formatter: function(value,row) {
				        			 var suffix = (row.inspectPlanItemReferenceSubfix == null || row.inspectPlanItemReferenceSubfix == '') ? '' : row.inspectPlanItemReferenceSubfix;
				        			 if(suffix!=''&&suffix.length > 6){
				        				 var check = suffix.substring(0,6);
				        				 return '<span title="'+suffix+'">'+check+'</span>';
				        			 }else{
				        				 return suffix;
				        			 }
				        			 
				        	 }
				         },
				         {
				        	 field:'inspectPlanItemUnit',
				        	 title:'单位',
				        	 sortable:false,
				        	 width:50,
				        	 align:'left',
				        	 editor:{
				        		 type:'text'
				        	 },
				        	 formatter: function(value, row) {
				        		 var unit = (value != null && value != '') ? value : '';
				        		 if(unit!=''&&unit.length > 6){
			        				 var check = unit.substring(0,5);
			        				 return '<span title="'+unit+'">'+check+'</span>';
			        			 }else{
			        				 return unit;
			        			 }
				        	 }
				         },
				         {
				        	 field:'inspectPlanItemValue',
				        	 title:'巡检值',
				        	 sortable:false,
				        	 width:70,
				        	 align:'center',
				        	 editor:{
				        		 type:'text'
				        	 },
				        	 formatter: function(value, row) {
				        		 var inspectPlanItemValue = (row.inspectPlanItemValue!= null && row.inspectPlanItemValue != '') ? row.inspectPlanItemValue : '';
				        		 if(inspectPlanItemValue!=''&&inspectPlanItemValue.length > 6){
			        				 var check = inspectPlanItemValue.substring(0,5);
			        				 return '<span title="'+inspectPlanItemValue+'">'+check+'</span>';
			        			 }else{
			        				 return inspectPlanItemValue;
			        			 }
				        	 }
				         },
				         {
				        	 field:'type',title:'巡检类型',sortable:false,width:70,align:'center',
				        	 formatter: function(value, row) {
				        		 if(row.type == 2) {
				        			 if(row.indicatorAsItem!=true){
				        				 return '系统自检';
				        			 }
				        		 } else if(row.type == 1){
				        			 return '人工手检';
				        		 } else {
				        		 }
				        		 
				        	 }
				         },
				         {
				        	 field:'itemConditionDescrible',title:'情况概要',sortable:false,width:120,align:'left',
				        	 editor:{
				        		 type:'text'
				        	 },
				        	 formatter: function(value,row) {
				        		 var sumary = (value != null && value != '') ? value : '';
				        		 if(sumary!=''&&sumary.length > 15){
			        				 var check = sumary.substring(0,14);
			        				 return '<span title="'+sumary+'">'+check+'</span>';
			        			 }else{
			        				 return sumary;
			        			 }
			        			 
				        	 }
				         },
				         {
				        	 field:'-',title:'操作',sortable:false,width:79,align:'left',//indicatorAsItem用来标识这个节点是否是根节点
				        	 formatter: function(value,row) {
				        		 if(row.indicatorAsItem==true&&row.type==1) {
				        			 return '<span><a class="item-add-child-item fa fa-plus" onclick="addNewChildItem('+row.id+')"></a><a class="itme-delete fa fa-times-circle" href="javascript:deleteCurrentItem('+row.id+')"></a></span>';
				        		 }else{
				        			 return '<span><a class="childitme-delete fa fa-times-circle" onclick="deleteCurrentItem('+row.id+')"></a></span>';
				        		 }
				        	 }
				         },
				         {
				        	 field:'resourceId',title:'资源ID',hidden:true,width:1,
				         },
				         {
				        	 field:'modelId',title:'资源模型ID',hidden:true,width:1,
				         },
				         {
				        	 field:'indexModelId',title:'指标ID',hidden:true,width:1,
				         }
				     ]],
					onLoadSuccess: function(row, data) {
					},
				     onDblClickRow : function(row){//根据赵总的意思，在添加和编辑巡检项的时候，手动的项和子项，除了巡检类型之外，其他的所有的都是可以编辑的，自动的巡检项和子项，巡检项的名字和巡检类型是不能编辑的。
				    	 
				    	 var clickedTr = null;
				    	 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('beginEdit',row.id);
				    	 if(row.indicatorAsItem == true&&row.type==2){//如果既是根节点，并且还是系统自检，那么巡检项不让编辑
				    		 var tr = that._mainDiv.find('tr').each(function(i, t) {
					    		 if($(t).attr('node-id')*1 == row.id) {
					    			 clickedTr = $(t)
					    			 $(this).find('td').each(function(i, t) {
										 var f = $(t).attr('field');
										 if(//f == 'inspectPlanItemReferencePrefix' || 
										 	f == '#' ||
										 	//f == 'inspectPlanItemReferenceSubfix' ||
										 	f == '$' ||
										 	//f == 'inspectPlanItemUnit' ||
										 	//f == 'inspectPlanItemValue'||
										 	f=='inspectPlanItemName' ||
										 	f=='type'||
										 	f=='inspectPlanItemValue') {
											 $(t).find('input:first').attr('readonly',true);
										 }
										 if(f=='inspectPlanItemValue'){//系统自检不允许编辑预设巡检值
											 $(t).find('input').remove();
										 }
									 });
					    		 }
					    	 });
				    	 }
				    	 else if(row.indicatorAsItem == false&&row.type==2){//如果是子节点，并且巡检类型是系统自检，然么隐藏巡检项，巡检类型,
				    		 var tr = that._mainDiv.find('tr').each(function(i, t) {
					    		 if($(t).attr('node-id')*1 == row.id) {
					    			 $(this).find('td').each(function(i, t) {
										 var f = $(t).attr('field');
										 if(f == '#' ||
										 	f == '$' ||
										 	f=='inspectPlanItemName' ||
										 	f=='inspectPlanItemDescrible'||
										 	f=='type'||
										 	f=='inspectPlanItemValue') {
											 $(t).find('input:first').attr('readonly',true);
											 //<input type="text" class="datagrid-editable-input" readonly="readonly" style="width: 69px; height: 22px;" disabled="">
										 }
										 if(f=='inspectPlanItemValue'){//系统自检不允许编辑预设巡检值
											 $(t).find('input').remove();
											 }
									 });
					    		 }
					    	 });
				    	 }
				    	 else{
				    		 var tr = that._mainDiv.find('tr').each(function(i, t) {
					    		 if($(t).attr('node-id')*1 == row.id) {
					    			 $(this).find('td').each(function(i, t) {
										 var f = $(t).attr('field');
										 if(f == '#' ||
										 	f == '$' ||
										 	f=='type') {
											 $(t).find('input:first').attr('readonly',true);
										 }
									 });
					    		 }
					    	 });
				    	 }
				    	 
				    	 $(document).on('click', function(e) {
				    		 var sn = that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getSelected');
				    		 if(!sn || sn.id != row.id) {
				    			 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('endEdit',row.id);
				    		 }
				    	 });
				    	 
				     }
				});	 	 	
			  
			  
			  deleteCurrentItem = function (id){
					 //that._treegrid.treegrid.remove(id);
				  that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('remove',id);
				  $('.inspectItemCount').html(that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getRoots').length);
				  if(id>0){
					  that._deleteIdList.push(id);
					 // that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('reload');
					 /* var itemIds = [];
					  itemIds.push(id);
					  oc.util.ajax({
							url : oc.resource.getUrl('inspect/plan/delItems.htm'),
							data : {itemIds: id},
							async:false,
							successMsg : null,
							success : function(data) {
								alert("删除成功");
								that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('reload');
							}
						});*/
				  }
				 }
			 addNewChildItem = function(id){
				 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('endEdit',id);
				 var datas = [];
				 var data = {};
				 data.id = (that._treechildid)--;
				 data.inspectPlanItemName = '';
				 data.inspectPlanItemDescrible = '';
				 data.inspectPlanItemReferencePrefix = '';
				 data.inspectPlanItemReferenceSubfix = '';
				 data.inspectPlanItemUnit = '';
				 data.inspectPlanItemValue = '';
				 data.type = 1;
				 data.itemConditionDescrible = '';
				 data.indicatorAsItem = false;
				 datas.push(data);
				 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('append',{parent:id,data:datas});
			 }
			 
			 
			 
			 /**
			  * 新增指标作为子节点的方法，需要保存资源的ID,指标的ID
			  */
			
			 
			 function addNewResoucechildItem(id,realtionchilditem,resourceId,modelId){
				 var root =  that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getChildren',id);
				 var isexist = false;
				 var datas = [];
				 for(var j = 0; j<realtionchilditem.length; j++){
					 for(var i = 0; i < root.length;i++){
						 if(root[i].indexModelId==realtionchilditem[j].id){
							 isexist = true;
							 break;
						 }
					 }
					 if(isexist == false){
						 var obj = realtionchilditem[j];
						 var data = {};
						 data.id = (that._treechildid)--;
						 data.inspectPlanItemName = obj.name;
						 data.inspectPlanItemDescrible = obj.style;
						 data.inspectPlanItemReferencePrefix = obj.inspectPlanItemReferencePrefix;
						 data.inspectPlanItemReferenceSubfix = obj.inspectPlanItemReferenceSubfix;
						 data.inspectPlanItemUnit = obj.unit;
						 data.inspectPlanItemValue = obj.inspectPlanItemValue;
						 data.type = 2;
						 data.modelId=modelId;
						 data.resourceId = resourceId;
						 data.indexModelId = obj.id;
						 data.indicatorAsItem=false;
						 data.itemConditionDescrible = '';
						 datas.push(data);
					 }
					 isexist = false;
				 }
				 if(datas.length > 0){
					 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('append',{parent:id,data:datas});	
				 }
			 }
			
			 function addNewItem(){
				var root =  that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getRoots');
				if(root.length > oc.module.inspect.plan.getProperties().itemMax - 1){
					alert('最多添加' + oc.module.inspect.plan.getProperties().itemMax + '个配置项');
					return;
				}
				 var datas = [];
				 var data = {};
				 data.id = (that._treeid)--;
				 data.inspectPlanItemName = '';
				 data.inspectPlanItemDescrible = '';
				 data.inspectPlanItemReferencePrefix = '';
				 data.inspectPlanItemReferenceSubfix = '';
				 data.inspectPlanItemUnit = '';
				 data.inspectPlanItemValue = '';
				 data.type = 1;
				 data.modelId = '';
				 data.indicatorAsItem = true;
				 data.itemConditionDescrible = '';
				 datas.push(data);
				 $('.inspectItemCount').html(root.length + 1);
				if(root!=undefined&&root!=null&&root!=null&&root.length == 0){
					that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('append',{data:datas});
					// that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('refresh');
				}
				else{
					var index = root[root.length-1].id;
					that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('insert',{after:index,data:data});
					//that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('reload');
				}
				
			 }
			 
			 function addNewResouceItem(obj){
				 var isexist = false;
				 var id  = '';
				 var relationId = '';
				 var discoverIP = '';
				 var index = '';
				 var root =  that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getRoots');
				  if(root==''||root==null||root==undefined||root.length==0){
					  var name = '';
						 if(obj.showName==''||obj.showName==undefined){
								name = "未知"+"["+obj.discoverIP+"]";
							}else{
								name = obj.showName+"["+obj.discoverIP+"]";
							}
						 var datas = [];
						 var data = {};
						 data.id = (that._treeid)--;
						 data.inspectPlanItemName = name;
						 data.inspectPlanItemDescrible = obj.resourceName;
						 data.inspectPlanItemReferencePrefix = '';
						 data.inspectPlanItemReferenceSubfix = '';
						 data.inspectPlanItemUnit = '';
						 data.inspectPlanItemValue = '';
						 data.type = 2;
						 data.modelId = obj.resourceId;
						 data.resourceId = obj.id;
						 data.indicatorAsItem = true;
						 data.itemConditionDescrible = ''; 
						 datas.push(data);
						 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('append',{data:datas});	
						 return data.id;
				  }else{
					  for(var i = 0; i< root.length;i++){
						  if(root[i].resourceId!=''&&root[i].resourceId!=null&&root[i].resourceId!=undefined){
							  if(root[i].resourceId == obj.id){
								  isexist = true;
								  id = root[i].id;
								  break;
							  }
							  discoverIP = root[i].inspectPlanItemName;
							  index = discoverIP.indexOf('[');
							  discoverIP = discoverIP.substr(index);
							  index = discoverIP.indexOf(']');
							  discoverIP=discoverIP.substring(1,index);
							  if(discoverIP==obj.discoverIP){
								  relationId = root[i].id;
							  }
						  }
					  }
					  if(relationId!=''&&relationId!=null&&relationId!=undefined&&isexist==false){
						  var name = '';
							 if(obj.showName==''||obj.showName==undefined){
									name = "未知"+"["+obj.discoverIP+"]";
								}else{
									name = obj.showName+"["+obj.discoverIP+"]";
								}
							 var datas = [];
							 var data = {};
							 data.id = (that._treeid)--;
							 data.inspectPlanItemName = name;
							 data.inspectPlanItemDescrible = obj.resourceName;
							 data.inspectPlanItemReferencePrefix = '';
							 data.inspectPlanItemReferenceSubfix = '';
							 data.inspectPlanItemUnit = '';
							 data.inspectPlanItemValue = '';
							 data.type = 2;
							 data.modelId = obj.resourceId;
							 data.resourceId = obj.id;
							 data.indicatorAsItem = true;
							 data.itemConditionDescrible = '';
							 datas.push(data);
							 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('insert',{after:relationId,data:data});
							 return data.id;
					  }
					  if(isexist == false){
						  var name = '';
							 if(obj.showName==''||obj.showName==undefined){
									name = "未知"+"["+obj.discoverIP+"]";
								}else{
									name = obj.showName+"["+obj.discoverIP+"]";
								}
							 var datas = [];
							 var data = {};
							 data.id = (that._treeid)--;
							 data.inspectPlanItemName = name;
							 data.inspectPlanItemDescrible = obj.resourceName;
							 data.inspectPlanItemReferencePrefix = '';
							 data.inspectPlanItemReferenceSubfix = '';
							 data.inspectPlanItemUnit = '';
							 data.inspectPlanItemValue = '';
							 data.type = 2;
							 data.modelId = obj.resourceId;
							 data.resourceId = obj.id;
							 data.indicatorAsItem = true;
							 data.itemConditionDescrible = '';
							 datas.push(data);
							 var index = root[root.length-1].id;
							 that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('insert',{after:index,data:data});
							return data.id;
					  }else{
						  return id;
					  }
				  }
				
			 }
			 
			 function addRelationResouceItem(reference){
					if(that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getRoots').length > oc.module.inspect.plan.getProperties().itemMax){
						alert('最多添加' + oc.module.inspect.plan.getProperties().itemMax + '个配置项');
						return;
					}
				 	var resourceId;//资源的ID
				 	var indexmodelid;//资源的实例ID
				 	var Itemisexist = false; //判断当前项是否存在
				 	var childItemisexist = false;//判断当前子项是否存在
				 	var currentItem = null;//用来存放当前项
				 	var discoverIP = null;
				 	var relationId = null;
				 	var data =  that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getData');
				 	//组装二级树结构
				 	if(data.length > 0){
				 		var newChildData = [];
				 		for(var y = 0; y < data.length; ++y){
				 			var childData = data[y].children;
				 			if(childData != null){
				 				for(var cy = 0; cy < childData.length; cy++){
				 					if(childData[cy].children != null && childData[cy].children.length > 0){
				 						childData[cy].parentId = data[y].resourceId;
				 						newChildData.push(childData[cy]);
				 						childData.splice(cy--,1);
				 					}
				 				}
				 			}
				 		}
				 		for(var y = 0; y < newChildData.length; ++y){
				 			data.push(newChildData[y]);
				 		}
				 	}
				 	for (index in reference){
				 		var relationItem = JSON.parse(index,null);//取到从来台返回回来的一个资源
				 		var relationChildItem = reference[index];//这个资源对应选择的指标
				 		for(var i = 0; i< data.length;i++){
				 			currentItem = data[i];
			 				if(currentItem.resourceId == relationItem.id){//判断当前这个巡检项在页面中是否已经存在了。
			 					if(relationItem.parentId != null || currentItem.parentId != null){
			 						if(currentItem.parentId != null && currentItem.parentId == relationItem.parentId){
			 							Itemisexist = true;
				 						break;
			 						}
			 					}else{
			 						Itemisexist = true;
			 						break;
			 					}
			 				}
			 			  	discoverIP = currentItem.inspectPlanItemName;
			 			  	index = discoverIP.indexOf('[');
			 			  	discoverIP = discoverIP.substr(index);
			 			  	index = discoverIP.indexOf(']');
			 			  	discoverIP=discoverIP.substring(1,index);
			 			  	if(discoverIP==relationItem.discoverIP){
			 			  		relationId = i;
			 			  	}
				 		}
				 		if(relationId!=''&&relationId!=null&&relationId!=undefined&&Itemisexist==false){
				 			 var elementdata = {};
				 			 var name = '';
							 if(relationItem.showName==''||relationItem.showName==undefined){
									name = "未知"+"["+relationItem.discoverIP+"]";
								}else{
									if(relationItem.resourceId != null && relationItem.resourceId == "Business"){
										name = relationItem.showName;
									}else{
										name = relationItem.showName+"["+relationItem.discoverIP+"]";
									}
							 }
							 elementdata.id = (that._treeid)--;
							 elementdata.inspectPlanItemName = name;
							 elementdata.inspectPlanItemDescrible = relationItem.resourceName;
							 elementdata.inspectPlanItemReferencePrefix = '';
							 elementdata.inspectPlanItemReferenceSubfix = '';
							 elementdata.inspectPlanItemUnit = '';
							 elementdata.inspectPlanItemValue = '';
							 elementdata.type = 2;
							 elementdata.modelId = relationItem.resourceId;
							 elementdata.resourceId = relationItem.id;
							 elementdata.indicatorAsItem = true;
							 elementdata.itemConditionDescrible = '';
							 if(relationItem.parentId != null){
								 elementdata.parentId = relationItem.parentId;
							 }
							 childrenElement = [];
							 resourceId = relationItem.id;
					 		 indexmodelid = relationItem.resourceId;
							 for(var j = 0; j<relationChildItem.length;j++){//将所有的指标作为子节点加入到当前节点中
								 var childElement = relationChildItem[j];
								 var childdata = {};
								 childdata.id = (that._treechildid)--;
								 childdata.inspectPlanItemName = childElement.name;
								 childdata.inspectPlanItemDescrible = childElement.style;
								 childdata.inspectPlanItemReferencePrefix = childElement.inspectPlanItemReferencePrefix;
								 childdata.inspectPlanItemReferenceSubfix = childElement.inspectPlanItemReferenceSubfix;
								 childdata.inspectPlanItemUnit = childElement.unit;
								 childdata.inspectPlanItemValue = childElement.inspectPlanItemValue;
								 childdata.type = 2;
								 childdata.modelId=indexmodelid;
								 childdata.resourceId = resourceId;
								 childdata.indexModelId = childElement.id;
								 childdata.indicatorAsItem=false;
								 childdata.itemConditionDescrible = '';
								 childrenElement.push(childdata);
							 }
							 elementdata.children = childrenElement;
							 data.splice(relationId+1,0,elementdata);
						  }
				 		else if(Itemisexist == false){//如果新添加的资源在当前树中不存在
				 			 var elementdata = {};
				 			 var name = '';
							 if(relationItem.showName==''||relationItem.showName==undefined){
									name = "未知"+"["+relationItem.discoverIP+"]";
								}else{
									if(relationItem.resourceId != null && relationItem.resourceId == "Business"){
										name = relationItem.showName;
									}else{
										name = relationItem.showName+"["+relationItem.discoverIP+"]";
									}
							 }
							 elementdata.id = (that._treeid)--;
							 elementdata.inspectPlanItemName = name;
							 elementdata.inspectPlanItemDescrible = relationItem.resourceName;
							 elementdata.inspectPlanItemReferencePrefix = '';
							 elementdata.inspectPlanItemReferenceSubfix = '';
							 elementdata.inspectPlanItemUnit = '';
							 elementdata.inspectPlanItemValue = '';
							 elementdata.type = 2;
							 elementdata.modelId = relationItem.resourceId;
							 elementdata.resourceId = relationItem.id;
							 elementdata.indicatorAsItem = true;
							 elementdata.itemConditionDescrible = '';
							 if(relationItem.parentId != null){
								 elementdata.parentId = relationItem.parentId;
							 }
							 childrenElement = [];
							 resourceId = relationItem.id;
					 		 indexmodelid = relationItem.resourceId;
							 for(var j = 0; j<relationChildItem.length;j++){//将所有的指标作为子节点加入到当前节点中
								 var childElement = relationChildItem[j];
								 var childdata = {};
								 childdata.id = (that._treechildid)--;
								 childdata.inspectPlanItemName = childElement.name;
								 childdata.inspectPlanItemDescrible = childElement.style;
								 childdata.inspectPlanItemReferencePrefix = childElement.inspectPlanItemReferencePrefix;
								 childdata.inspectPlanItemReferenceSubfix = childElement.inspectPlanItemReferenceSubfix;
								 childdata.inspectPlanItemUnit = childElement.unit;
								 childdata.inspectPlanItemValue = childElement.inspectPlanItemValue;
								 childdata.type = 2;
								 childdata.modelId=indexmodelid;
								 childdata.resourceId = resourceId;
								 childdata.indexModelId = childElement.id;
								 childdata.indicatorAsItem=false;
								 childdata.itemConditionDescrible = '';
								 childrenElement.push(childdata);
							 }
							 elementdata.children = childrenElement;
							 data.push(elementdata);
				 		}else{//如果当前节点存在，那么久判断某个子节点是否存在
				 			 var childrenElement = currentItem.children;//当前节点的已经存在的所有的子节点
				 			 resourceId = relationItem.id;
					 		 indexmodelid = relationItem.resourceId;
				 			for(var k = 0; k<relationChildItem.length;k++){
				 				var currentchildItem = relationChildItem[k];
				 				for(var l = 0; l < childrenElement.length;l++){
				 					if(childrenElement[l].indexModelId==currentchildItem.id){//判断当前要新增的这个子节点在原来的子节点中是否存在
				 						childItemisexist = true;
				 						break;
				 					}
				 				}
				 				if(childItemisexist == false){
									 var childdata = {};
									 childdata.id = (that._treechildid)--;
									 childdata.inspectPlanItemName = currentchildItem.name;
									 childdata.inspectPlanItemDescrible = currentchildItem.style;
									 childdata.inspectPlanItemReferencePrefix = currentchildItem.inspectPlanItemReferencePrefix;
									 childdata.inspectPlanItemReferenceSubfix = currentchildItem.inspectPlanItemReferenceSubfix;
									 childdata.inspectPlanItemUnit = currentchildItem.unit;
									 childdata.inspectPlanItemValue = currentchildItem.inspectPlanItemValue;
									 childdata.type = 2;
									 childdata.modelId=indexmodelid;
									 childdata.resourceId = resourceId;
									 childdata.indexModelId = currentchildItem.id;
									 childdata.indicatorAsItem=false;
									 childdata.itemConditionDescrible = '';
									 childrenElement.push(childdata);
				 				}
				 				childItemisexist = false;
				 			}
				 		}
				 		relationId = null;
				 		Itemisexist = false;
			 			/*parentid = addNewResouceItem(relationItem);
			 			resourceId = relationItem.id;
			 			indexmodelid = relationItem.resourceId;
			 			addNewResoucechildItem(parentid,relationChildItem,resourceId,indexmodelid);*/
				 	}
				 	$('.inspectItemCount').html(that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('getRoots').length);
				 	//组装业务树
			 		var tempData = [];
			 		var rootObj = [];
			 		for(var x = 0; x < data.length; ++x){
			 			if(data[x].parentId == null){
			 				rootObj.push(data[x]);
			 			}
			 		}
			 		for(var x = 0; x < rootObj.length; ++x){
			 			for(var y = 0; y < data.length; ++y){
			 				if(rootObj[x].resourceId != null && rootObj[x].resourceId == data[y].parentId){
			 					var ins = data[y];
			 					tempData.push(ins);
			 					//组装子资源指标
			 					for(var c = 0; c < data.length; ++c){
			 						if(ins.resourceId == data[c].parentId){
			 							ins.children.push(data[c]);
			 						}
			 					}
			 				}
			 			}
			 			if(rootObj[x].children == null){
			 				rootObj[x].children = [];
			 			}
			 			rootObj[x].children = rootObj[x].children.concat(tempData);
			 			tempData = [];
			 		}
			 		data = rootObj;
				 	that._mainDiv.find('#china-oc-inspect-management-treegrid').treegrid('loadData',data);	
				 	
			 }
			 
				 
			function addRelationChildItem(){
				oc.resource.loadScript('resource/module/inspect-management/js/inspect_manage_relation_item_new.js', function(){
					oc.module.inspect.relations.subitems.open({
						edittype: that.cfg.type,
						inpsectPlanContentID : that.cfg.dirId,
						dirName : that.cfg.dirName,
						dirDesc : that.cfg.dirDesc,
						reportName : that.cfg.reportName,
						resourceId : that.cfg.resourceId,
						resourceType : that.cfg.resourceType,
						domainId: that.cfg.domainId,
						getData: function(data) {
					    	var item = [];
							var reference = '';
							var resouceItem = data.relationItem;
							var pointItem = data.realtionchilditem;	
							var inspectType = data.inspectType;
							var checkBusinessnodeInformation = data.checkBusinessnodeInformation;
							
							 oc.util.ajax({
								url : oc.resource.getUrl('inspect/plan/getReferenceValue.htm'),
								data : {resouceItem:JSON.stringify(resouceItem),
									pointItem:JSON.stringify(pointItem),
									inspectType : inspectType,
									checkBusinessnodeInformation : checkBusinessnodeInformation
									},
									async:true,
									stopProgress:null,
									statProgress:null,
									timeout:0,
									success : function(data) {
										reference = data.data;
										oc.ui.progress();
										addRelationResouceItem(reference);
										$.messager.progress('close');
									}
							});
							 
							
							
						}
					});
				});
				
			}
		}
	};
	oc.ns('oc.module.inspect.items');
	oc.module.inspect.items = {
		open : function(cfg) {
			new InspectItem(cfg).open();
		}
	};
	
})();
