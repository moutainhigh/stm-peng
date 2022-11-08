$(function() {
	var innerDatagrid=null;
	function open(cfg){
		var mainDiv,datagridDiv,datagrid,toolbar = [],openFlag=cfg.openFlag,devices=cfg.devices,localData=[],
			products=cfg.products,models=cfg.models,cabinetResource={},
			threedInfo=cfg.threedInfo,belong=cfg.belong,exceptDevices=cfg.exceptDevices;
		if(openFlag){
			mainDiv=$('#oc_module_3d_resource_main');
			datagridDiv=mainDiv.find('.oc_module_3d_resource_datagrid');
			oc.util.ajax({
				url : oc.resource.getUrl("portal/3d/cabinet/getAllCabinetResource.htm"),
				successMsg : null,
				async:false,
				timeout:300000,
				success : function(data) {
					if(data.code&&data.code==200){
						for(var i=0;i<data.data.length;i++){
							cabinetResource[data.data[i].id] = data.data[i];
						}
					}else{alert(data.data);}
				}
			});
		}else{
			$("#3dResource").empty();
			mainDiv=$('<div><div class="oc_module_3d_device_datagrid"></div></div>').appendTo($("#3dResource"));
			datagridDiv=mainDiv.find('.oc_module_3d_device_datagrid');
			var url = oc.resource.getUrl("portal/3d/cabinet/getCabinetResource.htm");
			oc.util.ajax({
				url : url,
				data : {belong:cfg.belong},
				successMsg : null,
				async:false,
				timeout:300000,
				success : function(data) {
					if(data.code&&data.code==200){
						localData = data.data.datas;
					}else{alert(data.data);}
				}
			});
		}
		mainDiv.attr('id',oc.util.generateId()).panel({
			fit:true,
			isOcAutoWidth:true
		});
		toolbar.push({
			text:'添加到机柜',
			iconCls:'ico ico-add',
			onClick:function(){
				addToCabinet();
			}
		},{
			text:'从机柜中移除',
			iconCls:'icon-move',
			onClick:function(){
				delFromCabinet();
			}
		});
		var datagridObj = {
			selector:datagridDiv,
			remoteSort:false,
			loadFilter:function(data){
				var rows = data.rows||[];
				for(var i=0,len=rows.length; i<len; i++){
					var row = rows[i],
					device = devices[row.id];
					if(device){
						row.ip = device.ip||'';
						row.name = device.name||'';
						row.type = device.type||'';
					}else{
						row.ip = '';
						row.name = '';
						row.type = '';
					}
				}
				return data;
			},
			columns:[[
		         {field:'id',title:'-',checkbox:true,width:20},
		         {field:'ip',title:'IP地址',sortable:true, width:50,formatter:function(value,row,index){
		        	 return '<span title="'+value+'">'+value+'</span>';
		         },sorter:function(a,b){
		        	 return _ip2int(a)==_ip2int(b)?0:(_ip2int(a)>_ip2int(b) ? 1 : -1);
		         }},
		         {field:'name',title:'资源名称',sortable:true,width:60,formatter:function(value,row,index){
		        	 return '<span title="'+value+'">'+value+'</span>';
		         }},
		         {field:'type',title:'资源类型',sortable:true,width:80,formatter:function(value,row,index){
		        	 return '<span title="'+value+'">'+value+'</span>';
		         }},
		         {field:'brand',title:'厂商',width:50,formatter:function(value,row,index){
		        	 return '<span title="'+(value||"")+'">'+(value||"")+'</span>';
		         }},
		         {field:'model',title:'型号',width:80,formatter:function(value,row,index){
		        	 return '<span title="'+(value||"")+'">'+(value||"")+'</span>';
		         }},
		         {field:'uplace',title:'U位置',width:30},
		         {field:'layout',title:'布局',width:30},
		         {field:'operate',title:'操作',width:30,formatter:operateFormart}
		     ]],
		     onClickCell:function(rowIndex, field, value){
		     },
		     onLoadSuccess:function(){
		    	 $(".uPostionBtn").linkbutton({ plain:true,iconCls:'ico ico-resource'}).attr("title","设置U位型号");
		       	if(!openFlag){
					exceptDevices = datagrid.selector.datagrid('getData').rows;
				}
		     }
		};
		if(!openFlag){
			datagridObj.octoolbar = {
				right:toolbar,
			};
		}
		datagrid=oc.ui.datagrid(datagridObj);
		
		if(openFlag){
			if(datagrid){
				var exceptObj = {};
				for(var m in exceptDevices){
					exceptObj[exceptDevices[m].id] = exceptDevices[m];
				}
				for(var k in devices){
					if(!exceptObj[k])
					localData.push(devices[k]);
				}
				innerDatagrid = datagrid;
			}
		}

		var options = datagrid.selector.datagrid('getPager').data("pagination").options;
		var pPageIndex = options.pageNumber, pPageSize = options.pageSize;
		datagrid.selector.datagrid('loadData',{rows:getLocalRows(pPageIndex, pPageSize), 
			total:localData.length});
		
		datagrid.selector.datagrid('getPager').pagination({   
			displayMsg:'当前显示从 {from} 到 {to} 共{total}条记录',   
			onSelectPage : function(pPageIndex, pPageSize) {   
				//使用loadDate方法加载local返回的数据   
				datagrid.selector.datagrid('loadData',{"total":localData.length,
					"rows":getLocalRows(pPageIndex, pPageSize)});   
			}   
		}); 
		
		/**
		 * 获取本地数据时的当前页rows数据
		 * @param pPageIndex 当前页
		 * @param pPageSize 当前页条数
		 */
		function getLocalRows(pPageIndex, pPageSize){
			var rows=[];
			var gridOpts = datagrid.selector.datagrid('options');   
	        gridOpts.pageNumber = pPageIndex;   
	        gridOpts.pageSize = pPageSize;     
	        var startRow=(gridOpts.pageNumber-1)*gridOpts.pageSize+1,
	        	endRow = gridOpts.pageNumber*gridOpts.pageSize;
	        if(endRow>localData.length) endRow=localData.length;
	        //添加厂商和型号
	        var modelsObj = {};
	        for(var k in cfg.models){
	        	modelsObj[cfg.models[k].type] = cfg.models[k].model;
	        }
			for(var i=startRow-1;i<endRow;i++){
				var row = localData[i],product = cfg.products[modelsObj[row.type]];
				if(product){
					row.model = modelsObj[row.type];
					row.brand = product['品牌'];
				}
				if(cabinetResource[localData[i].id]){
					localData[i].layout = cabinetResource[localData[i].id].layout;
					localData[i].uplace = cabinetResource[localData[i].id].uplace;
					localData[i].model = cabinetResource[localData[i].id].model;
					localData[i].brand = cabinetResource[localData[i].id].brand;
				}
				rows.push(localData[i]);
			}
			return rows;
		}
		
		/**
		 *格式化操作栏 
		 */
		function operateFormart(value,row,index){
			var result = $("<span/>");
			result.append($('<span class="uPostionBtn"/>').on('click',function(){
				oc.resource.loadScript('resource/module/3d/device/js/upostion.js?t='+new Date(),function(){
					oc.module.thirdd.resource.uposition.open({row:row,devices:devices,products:products,
						models:models,threedInfo:threedInfo,rowIndex:index,datagrid:datagrid,openFlag:openFlag,exceptDevices:exceptDevices});
				});
			}));
			return result;
		}
		/**
		 * IP转成整型
		 */
		function _ip2int(ip){
			if(ip==undefined || ip.trim()=="" || ip==null) return 0;
		    var num = 0;
		    var ips = ip.split(".");
		    num = Number.parseInt(ips[0])*256*256*256 + Number.parseInt(ips[1])*256*256 + Number.parseInt(ips[2])*256 + Number.parseInt(ips[3]);
		    return num;
		} 
		/**
		 * 添加到机柜
		 */
		function addToCabinet(){
			var addToCabinetWindow = $('<div/>');
			//构建dialog
			addToCabinetWindow.dialog({
			  content:"<div id='cabinetWindow'/>",
			  href:oc.resource.getUrl('resource/module/3d/device/resource.html'),
			  title:"添加到机柜",
			  width:900,
			  height:450,
			  modal:true,
			  buttons:[{
					text:'确定',
					iconCls:'ico ico-ok',
					handler:function(){
						var inner = oc.module.thirdd.resource.getInnerDataGrid();
						var selectRows = inner.getSelections();
						if(selectRows && selectRows.length>0){
							var flag = true;
							for(var k=0;k<selectRows.length;k++){
								if(selectRows[k].brand=="" || selectRows[k].brand==null
										|| selectRows[k].name=="" || selectRows[k].name==null
										|| selectRows[k].model=="" || selectRows[k].model==null
										|| selectRows[k].uplace=="" || selectRows[k].uplace==null){
									flag = false;
									break;
								}
							}
							if(flag){
								oc.util.ajax({
									url: oc.resource.getUrl('portal/3d/cabinet/batchAdd.htm'),
									data:{data:JSON.stringify(selectRows),belong:belong},
									successMsg:null,
									success:function(data){
										if(data.code && data.code==200){
											$("#pageTag-container").find("#nodeTree").tree('getSelected').target.click();
											addToCabinetWindow.dialog('close');
											alert('操作成功');
										}else if(data.code==299){alert(data.data);}
									}
								});
							}else{
								alert("数据不全无法推送");
							}
						}else{
							alert("请选择至少一条数据");
						}
					}
			  	},{
					text:'取消',
					iconCls:'ico ico-cancel',
					handler:function(){
						addToCabinetWindow.dialog('close');
				}
			 }],
			 onLoad:function(){
				 oc.resource.loadScript('resource/module/3d/device/js/resource.js?t'+new Date(),function(){
					oc.module.thirdd.resource.open({openFlag:true,belong:belong,devices:devices,products:products,
						models:models,threedInfo:threedInfo,datagrid:datagrid,exceptDevices:exceptDevices});
				});
			 }
			});
		}
		/**
		 * 从机柜中移除
		 */
		function delFromCabinet(){
			var ids = datagrid.getSelectIds();
			if(ids == undefined||ids == ""){
				alert("请勾选需要从机柜中移出的资源");
			}else{
				oc.ui.confirm("是否确认移出资源？",function(){
					oc.util.ajax({
						url:oc.resource.getUrl('portal/3d/cabinet/batchRemove.htm'),			            
						data:{ids:ids.join()},
						successMsg:null,
						success:function(d){
							if(d.code&&d.code==200){
								$("#pageTag-container").find("#nodeTree").tree('getSelected').target.click();
								alert("移除成功");
							}else if(d.code==299){alert(d.data);}
						}
					});
				});
			}
		}
	}	
	oc.ns("oc.module.thirdd.resource");
	oc.module.thirdd.resource={
		open:function(cfg){
			open(cfg);
		},
		getInnerDataGrid:function(){
			return innerDatagrid;
		}
	}
});