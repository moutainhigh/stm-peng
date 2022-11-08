$(function(){
	oc.ns('oc.vm.resourcecount');
	var globalVar = {
		globalDiv : $("#vmSourceCount"),
		vcenterDatagrid : {},
		clusterDatagrid : {},
		esxCmpDatagrid : {},
		vmDatagrid : {},
		storeDatagrid : {},
		vcenterJsonData : null,
		clusterJsonData : null,
		esxCmpJsonData : null,
		vmJsonData : null,
		storeJsonData : null
	};
	
	function init(){
		 var htmlStr='<span class="icon-expExcel locate-right" title="导出EXCEL" style="font-size:16px;"></span>';
			$('.oc-header-m').find('.panel-title').append(htmlStr);
		globalVar.globalDiv.append("<div style='height: 234px;' class='common back_ver_bg'><div class='back_ver_title'><span class='l-arrow  foxfrolistico b-l-arrow-right l-arrow-down'></span><span class='line-height30 font-bold'>vCenter</span></div><div style='height: 210px;'><div name='vcenterDatagrid'></div></div></div>");
		globalVar.globalDiv.append("<div style='height: 234px;' class='common back_ver_bg'><div class='back_ver_title'><span class='l-arrow  foxfrolistico b-l-arrow-right l-arrow-down'></span><span class='line-height30 font-bold'>Cluster</span></div><div style='height: 210px;'><div name='clusterDatagrid'></div></div></div>");
		globalVar.globalDiv.append("<div style='height: 234px;' class='common back_ver_bg'><div class='back_ver_title'><span class='l-arrow  foxfrolistico b-l-arrow-right l-arrow-down'></span><span class='line-height30 font-bold'>ESXi</span></div><div style='height: 210px;'><div name='esxCmpDatagrid'></div></div></div>");
		globalVar.globalDiv.append("<div style='height: 234px;' class='common back_ver_bg'><div class='back_ver_title'><span class='l-arrow  foxfrolistico b-l-arrow-right l-arrow-down'></span><span class='line-height30 font-bold'>数据存储</span></div><div style='height: 210px;'><div name='storeDatagrid'></div></div></div>");
		globalVar.globalDiv.append("<div style='height: 234px;' class='common back_ver_bg'><div class='back_ver_title'><span class='l-arrow  foxfrolistico b-l-arrow-right l-arrow-down'></span><span class='line-height30 font-bold'>VM</span></div><div style='height: 210px;'><div  name='vmDatagrid'></div></div></div></div>");
		globalVar.globalDiv.append("<div style='height: 234px;' class='common back_ver_bg'><div class='back_ver_title'><span class='l-arrow  foxfrolistico b-l-arrow-right l-arrow-down'></span><span class='line-height30 font-bold'>数据存储</span></div><div style='height: 210px;'><div name='storeDatagrid'></div></div></div>");
		
		var unitStr = '_Unit';
		globalVar.vcenterDatagrid = formatDatagrid({
				selector:globalVar.globalDiv.find("div[name=vcenterDatagrid]"),
				columnsData:[{field:'id',title:'id',checkbox:false,hidden:true},
		         {field:'vCenter',title:'vCenter',width:40,align:'left'},
//		         {field:'dataCenter',title:'DataCenter(个)',width:40,align:'left'},
		         {field:'cluster',title:'Cluster(个)',width:40,align:'left'},
		         {field:'esxi',title:'ESXi主机(台)',width:40,align:'left'},
		         {field:'vMare',title:'VM(台)',width:40,align:'left'},
		         {field:'dataStorage',title:'数据存储(个)',width:40,align:'left'},
		         {field:'CPUCountGhz',title:'总CPU',width:40,align:'left',formatter:function(value,row,rowIndex){
		        	 var unit = row['CPUCountGhz'+unitStr];
		        	 if(unit){
		        		 if("--"!=value){
		        			 var num = new Number(value);
		        			 return num.toFixed(2)+unit;
		        		 }
		        	 }
		        	 return value;
		         }},
		         {field:'memCount',title:'总内存',width:40,align:'left',formatter:function(value,row,rowIndex){
		        	 var unit = row['memCount'+unitStr];
		        	 if(unit){
		        		 if("--"!=value){
		        			 var num = new Number(value);
		        			 return unitTransform(num.toFixed(2),unit);
		        		 }
		        	 }
		        	 return value;
		         }},
		         {field:'storageCount',title:'总存储',width:40,align:'left',formatter:function(value,row,rowIndex){
		        	 var unit = row['storageCount'+unitStr];
		        	 if(unit){
		        		 if("--"!=value){
		        			 var num = new Number(value);
		        			 return num.toFixed(2)+unit;
		        		 }
		        	 }
		        	 return value;
		         }}]
		});
		globalVar.clusterDatagrid = formatDatagrid({
			selector:globalVar.globalDiv.find("div[name=clusterDatagrid]"),
			columnsData:[{field:'id',title:'id',checkbox:false,hidden:true},
	         {field:'cluster',title:'Cluster',width:40,align:'left'},
	         {field:'TotalCPU',title:'总CPU',width:40,align:'left',formatter:function(value,row,rowIndex){
	        	 var unit = row['TotalCPU'+unitStr];
	        	 if(unit){
	        		 if("--"!=value){
	        			 var num = new Number(value);
	        			 return num.toFixed(2)+unit;
	        		 }
	        	 }
	        	 return value;
	         }},
	         {field:'TotalMemSize',title:'总内存',width:40,align:'left',formatter:function(value,row,rowIndex){
	        	 var unit = row['TotalMemSize'+unitStr];
	        	 if(unit){
	        		 if("--"!=value){
	        			 var num = new Number(value);
	        			 return unitTransform(num.toFixed(2),unit);
	        		 }
	        	 }
	        	 return value;
	         }},
	         {field:'DatastoreSize',title:'总存储',width:40,align:'left',formatter:function(value,row,rowIndex){
	        	 var unit = row['DatastoreSize'+unitStr];
	        	 if(unit){
	        		 if("--"!=value){
	        			 var num = new Number(value);
	        			 return num.toFixed(2)+unit;
	        		 }
	        	 }
	        	 return value;
	         }},
	         {field:'HostNum',title:'ESXi主机(台)',width:40,align:'left',formatter:function(value,row,rowIndex){
	        	 var unit = row['HostNum'+unitStr];
	        	 if(unit){
	        		 if("--"!=value){
	        			 return value+unit;
	        		 }
	        	 }
	        	 return value;
	         }},
	         {field:'VMNum',title:'VM(台)',width:40,align:'left',formatter:function(value,row,rowIndex){
	        	 var unit = row['VMNum'+unitStr];
	        	 if(unit){
	        		 if("--"!=value){
	        			 return value+unit;
	        		 }
	        	 }
	        	 return value;
	         }},
	         {field:'HAStatus',title:'HA状态',width:40,align:'left'},
	         {field:'DRSStatus',title:'DRS状态',width:40,align:'left'},
	         {field:'EVCMode',title:'EVC模式',width:40,align:'left'},
//	         {field:'dataCenter',title:'DataCenter',width:40,align:'left'},
	         {field:'vCenter',title:'vCenter',width:40,align:'left'}]
		});
		
		globalVar.esxCmpDatagrid = formatDatagrid({
			selector:globalVar.globalDiv.find("div[name=esxCmpDatagrid]"),
			columnsData:[{field:'id',title:'id',checkbox:false,hidden:true},
				         {field:'ip',title:'ESXi地址',width:40,align:'left'},
				         {field:'TotalCPU',title:'总CPU',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['TotalCPU'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return num.toFixed(2)+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'TotalMemSize',title:'总内存',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['TotalMemSize'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return unitTransform(num.toFixed(2),unit);
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'DatastoreSize',title:'总存储',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['DatastoreSize'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return num.toFixed(2)+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'VMNum',title:'VM(台)',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['VMNum'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 return value+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'VMotion',title:'Vmotion状态',width:40,align:'left'},
				         {field:'EVC',title:'EVC模式',width:40,align:'left'},
				         {field:'cluster',title:'Cluster',width:40,align:'left'},
//				         {field:'dataCenter',title:'DataCenter',width:40,align:'left'},
				         {field:'vCenter',title:'vCenter',width:40,align:'left'}]
		});
		
		globalVar.vmDatagrid = formatDatagrid({
			selector:globalVar.globalDiv.find("div[name=vmDatagrid]"),
			columnsData:[{field:'id',title:'id',checkbox:false,hidden:true},
				         {field:'vMareName',title:'VM名称',width:100,align:'left'},
				         {field:'osVersion',title:'客户机操作系统',width:100,align:'left'},
				         {field:'DiskAssignedSpace',title:'占用空间',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['DiskAssignedSpace'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return num.toFixed(2)+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'cpuNum',title:'CPU数(个)',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 
				        	 return value;
				         }},
				         {field:'MEMVMSize',title:'内存大小',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	var unit = row['MEMVMSize'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return unitTransform(num.toFixed(2),unit);
				        		 }
				        	 }
				        	 return value;
				         }},
//				         {field:'hostCPUGhz',title:'主机CPU',width:40,align:'left',formatter:function(value,row,rowIndex){
//				        	 var unit = row['hostCPUGhz'+unitStr];
//				        	 if(unit){
//				        		 if("--"!=value){
//				        			 var num = new Number(value);
//				        			 return num.toFixed(2)+unit;
//				        		 }
//				        	 }
//				        	 return value;
//				         }},
//				         {field:'hostMem',title:'主机内存',width:40,align:'left',formatter:function(value,row,rowIndex){
//				        	 var unit = row['hostMem'+unitStr];
//				        	 if(unit){
//				        		 if("--"!=value){
//				        			 var num = new Number(value);
//				        			 return unitTransform(num.toFixed(2),unit);
//				        		 }
//				        	 }
//				        	 return value;
//				         }},
				         {field:'hostPC',title:'主机',width:40,align:'left'},
//				         {field:'dataCenter',title:'DataCenter',width:40,align:'left'},
				         {field:'vCenter',title:'vCenter',width:40,align:'left'}]
		});
		
		globalVar.storeDatagrid = formatDatagrid({
			selector:globalVar.globalDiv.find("div[name=storeDatagrid]"),
			columnsData:[{field:'id',title:'id',checkbox:false,hidden:true},
				         {field:'storageName',title:'存储名称',width:40,align:'left'},
				         {field:'HostNum',title:'连接主机数目(个)',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['HostNum'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 return value+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'VMNum',title:'虚拟机和模板数(个)',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['VMNum'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 return value+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'DataStorageVolume',title:'容量',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['DataStorageVolume'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return num.toFixed(2)+unit;
				        		 }
				        	 }
				        	 return value;
				         }},
				         {field:'DataStorageFreeSpace',title:'可用空间',width:40,align:'left',formatter:function(value,row,rowIndex){
				        	 var unit = row['DataStorageFreeSpace'+unitStr];
				        	 if(unit){
				        		 if("--"!=value){
				        			 var num = new Number(value);
				        			 return num.toFixed(2)+unit;
				        		 }
				        	 }
				        	 return value;
				         }}]
		});
		
//		var vcenterJsonData = validateDatagrid([
//		          		                            {
//		        		                                "memCount": "98078.78",
//		        		                                "cluster": "4",
//		        		                                "dataCenter": "--",
//		        		                                "storageCount": "5103.0",
//		        		                                "CPUCountGhz": "79.119995",
//		        		                                "CPUCountGhz_Unit": "GHz",
//		        		                                "esxi": "4",
//		        		                                "dataStorage": "5",
//		        		                                "storageCount_Unit": "GB",
//		        		                                "memCount_Unit": "MB",
//		        		                                "id": "2001",
//		        		                                "vCenter": "192.168.1.235",
//		        		                                "vMare": "69"
//		        		                            }
//		        		                        ]);
//		
//			datagridLoadData(globalVar.vcenterDatagrid,vcenterJsonData,'vCenter');
		
		$("span.icon-expExcel.locate-right").click(function(){
			var sheetName="虚拟化资源统计";
			window.location.href=oc.resource.getUrl('portal/vm/vmReport/exportVm.htm?sheetName='+sheetName);
		/*	oc.util.ajax({
				url:oc.resource.getUrl('portal/vm/vmReport/exportVm.htm'),
				data:{sheetName:'虚拟化资源统计'},
				successMsg:null,
				success:function(data){	
					
				}
				
			});*/
			
		});
		oc.util.ajax({
			url:oc.resource.getUrl('portal/vm/vmReport/getVCenterInfo.htm'),
			successMsg:null,
			success:function(data){
				var dataStr = data.data;
//				var datas = {"data":{"VirtualHost":[],"VirtualCluster":[],"VirtualVM":[],"VCenter":[],"VirtualStorage":[{"id":"2630","DataStorageFreeSpace":"--","DataStorageVolume":"--","storageName":"datastore1","HostNum":"--","VMNum":"--"},{"id":"5679","DataStorageFreeSpace":"--","DataStorageVolume":"--","storageName":"datastore1 (3)-1","HostNum":"--","VMNum":"--"},{"id":"2599","DataStorageFreeSpace_Unit":"GB","DataStorageFreeSpace":"2.32","DataStorageVolume":"926.00","storageName":"datastore1 (3)","HostNum":"1","VMNum_Unit":"","DataStorageVolume_Unit":"GB","HostNum_Unit":"","VMNum":"23"},{"id":"2653","DataStorageFreeSpace":"--","DataStorageVolume":"--","storageName":"datastore1","HostNum":"--","VMNum":"--"},{"id":"2572","DataStorageFreeSpace_Unit":"GB","DataStorageFreeSpace":"811.22","DataStorageVolume":"1855.50","storageName":"datastore1","HostNum":"1","VMNum_Unit":"","DataStorageVolume_Unit":"GB","HostNum_Unit":"","VMNum":"19"}]},"code":200};
//				var dataStr = datas.data;
				
				if(dataStr){
					if(dataStr.VCenter){
						globalVar.vcenterJsonData = validateDatagrid(dataStr.VCenter);
						
						if(globalVar.vcenterJsonData.length > 0){
							datagridLoadData(globalVar.vcenterDatagrid,globalVar.vcenterJsonData,'vCenter');
						}
					}
					if(dataStr.VirtualCluster){
						globalVar.clusterJsonData = dataStr.VirtualCluster;
						var excludeColumnsCLU = new Array();
						excludeColumnsCLU.push('HAStatus');
						excludeColumnsCLU.push('DRSStatus');
						excludeColumnsCLU.push('EVCMode');
						excludeColumnsCLU.push('vCenter');
						datagridLoadData(globalVar.clusterDatagrid,globalVar.clusterJsonData,'cluster',excludeColumnsCLU);
					}
					if(dataStr.VirtualHost){
						globalVar.esxCmpJsonData = dataStr.VirtualHost;
						var excludeColumnsHo = new Array();
						excludeColumnsHo.push('VMotion');
						excludeColumnsHo.push('EVC');
						excludeColumnsHo.push('cluster');
						excludeColumnsHo.push('vCenter');
						datagridLoadData(globalVar.esxCmpDatagrid,globalVar.esxCmpJsonData,'ip',excludeColumnsHo);
					}
					if(dataStr.VirtualVM){
						globalVar.vmJsonData = dataStr.VirtualVM;
						var excludeColumnsVM = new Array();
						excludeColumnsVM.push('osVersion');
						excludeColumnsVM.push('hostPC');
						excludeColumnsVM.push('vCenter');
						datagridLoadData(globalVar.vmDatagrid,globalVar.vmJsonData,'vMareName',excludeColumnsVM);
					}
					if(dataStr.VirtualStorage){
						globalVar.storeJsonData = dataStr.VirtualStorage;
						datagridLoadData(globalVar.storeDatagrid,globalVar.storeJsonData,'storageName');
					}
				}
				
			}
		})
	}
	
	function validateDatagrid(datas){
		var dataTemp = [];
		for(var i=0;i<datas.length;i++){
			var rowDatasTemp = datas[i];
			var flag = false;
			//组织数据
			for(var key in rowDatasTemp){
				if(key != 'vCenter' && key != 'id' && rowDatasTemp[key] && rowDatasTemp[key]!='--' && ''!=rowDatasTemp[key] && '0'!=rowDatasTemp[key] && 0!=rowDatasTemp[key]){
					flag = true;
				}
			}
			if(flag){
				dataTemp.push(rowDatasTemp);
			}
		}
		return dataTemp;
	}
	
	
	function datagridLoadData(datagrid,datas,firstColumn,excludeColumnArr){
		
		var dataArrTemp = datas;
		
//		var noSortConst = '--';
//		var noValueConst = 'N/A';
		var unitConst = '_Unit';
		
		//组装最后总计数据
		var lastData = {id:-1};
		lastData[firstColumn] = "总计";
		//记录指标单位
		var dataRowUnit = {};
		
		if(dataArrTemp.length>0){
			//循环一次,记录单位
			var rowDatasTemp = dataArrTemp[0];
			for(var key in rowDatasTemp){
				if("id"!=key && firstColumn!=key && key.indexOf(unitConst)>0){
					if(!dataRowUnit[key] && rowDatasTemp[key] && ""!=rowDatasTemp[key]){
						dataRowUnit[key] = rowDatasTemp[key];
					}
				}
				
			}
		}
		
		for(var i=0;i<dataArrTemp.length;i++){
			var rowDatasTemp = dataArrTemp[i];
			//组织数据
			for(var key in rowDatasTemp){
				if("id"!=key && firstColumn!=key && key.indexOf(unitConst)<0){
					var dataTemp = new String(rowDatasTemp[key]);
					
					if(""!= dataTemp.trim() && !isNaN(dataTemp)){
						if(lastData[key]){
							if("--"!=lastData[key]){
								lastData[key] = lastData[key]+parseFloat(dataTemp);
							}
						}else{
							
							lastData[key] = parseFloat(dataTemp);
						}
					}else{
						//如果不能转化为数字，且不是--,N/A,那么认为本列不能计算总数
						if("--"!=dataTemp && "N/A"!=dataTemp){
							lastData[key] = "--";
						}
					}
					//最后一行需添加单位
					if(i== (dataArrTemp.length-1)){
						if(dataRowUnit[key+unitConst] && !isNaN(lastData[key])){
							//添加单位
							var num = new Number(lastData[key]);
							lastData[key] = unitTransform(num.toFixed(2),dataRowUnit[key+unitConst]);
						}else{
							//过滤不用计算总数的列,避免出现累加逻辑错误
							if(excludeColumnArr){
								for(var x=0;x<excludeColumnArr.length;x++){
									var column = excludeColumnArr[x]
									if(column==key){
										lastData[key] = "";
									}
								}
							}
						}
					} 
				}
			}
		}
		//避免总计某列值为null(当某列值全为‘--’时出现)
		for(var key in lastData){
			var flag = true;
			if(excludeColumnArr){
				for(var x=0;x<excludeColumnArr.length;x++){
					var column = excludeColumnArr[x]
					if(column==key){
						lastData[key] = "";
						flag = false;
					}
				}
			}
			if(flag && !lastData[key] && !(0===lastData[key])){
				lastData[key] = '--';
			}
		}
		
		var dataLength = dataArrTemp.length;
		dataArrTemp[dataLength] = lastData;
		dataLength = dataLength+1;
		
		var localData = 
		{"data":{"startRow":0,"rowCount":dataLength,"totalRecord":dataLength,"condition":null,
			"rows":dataArrTemp,"total":dataLength},"code":200};
		
		datagrid.selector.datagrid('loadData',localData);
	}
	function formatDatagrid(formatDataObj){
		
		return oc.ui.datagrid({
			selector:formatDataObj.selector,
			pagination:false,
			fitColumns:true,
			url:oc.resource.getUrl(''),
			columns:[formatDataObj.columnsData],
		     onClickRow:function(rowIndex, rowData){
		    	 
		    	 oc.resource.loadScript('resource/module/vm/js/resourcesOutlook.js',function(){
			 			oc.module.reportmanagement.vm.rol.open({reportXmlDataId:1,callback:function(){
			 				
			 			}});
			 		});
		    	 
		     },
		     onClickCell : function(rowIndex, field, value){
		    	 
		     },
		     onLoadSuccess:function(data){ 
		    	 
		     }
		});
	}
	function unitTransform(value,unit){
		return oc.vm.indexpage.unitTransform(value,unit);
	}
	
	oc.vm.resourcecount = {
		open : function(){
			globalVar = {
					globalDiv : $("#vmSourceCount"),
					vcenterDatagrid : {},
					clusterDatagrid : {},
					esxCmpDatagrid : {},
					vmDatagrid : {},
					storeDatagrid : {},
					vcenterJsonData : null,
					clusterJsonData : null,
					esxCmpJsonData : null,
					vmJsonData : null,
					storeJsonData : null
				};
			globalVar.globalDiv.html("");
			
			init();
		}
			
	}
	
})