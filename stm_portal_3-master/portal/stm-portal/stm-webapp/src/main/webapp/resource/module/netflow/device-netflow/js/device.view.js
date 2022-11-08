(function(){
	var datagridObj_div;

	var deviceViewForm = oc.ui.form({
		selector: $('.device-list-param-form'),
		combobox:[{
			selector:'.device-view-timeinterval',
			data : [ {
				id : '1hour',
				name : '最近一小时'
			}, {
				id : '6hour',
				name : '最近六小时'
			}, {
				id : '1day',
				name : '最近一天'
			}, {
				id : '7day',
				name : '最近一周'
			}, {
				id : '30day',
				name : '最近一个月'
			}, {
				id: 'custom',
				name: '自定义'
			} ],
			selected: '1hour',
			onSelect: function(d) {

				var rowCount = $('.device-view-displaycount').combobox('getValue');
				var indicator = $('#device_view_indicator').combobox('getValue');
				
				datagridObj_div.load({
					'indicator': indicator,
					'timePerid': d.id,
					'onePageRows': rowCount
				});
			}
		}, {
			selector: '.device-view-displaycount',
			data: [
			    {id: 5,name: 5},
			    {id: 10,name: 10}
			],
			selected: '5',
			onSelect: function(d) {
				var t = $('.device-view-timeinterval').combobox('getValue');
				var indicator = $('#device_view_indicator').combobox('getValue');
				
				datagridObj_div.load({
					'indicator': indicator,
					'timePerid': t,
					"onePageRows": d.id
				});
			}
		}, {
			selector: '#device_view_indicator',
			data:[
			    { id: 1, name: '流量'}, 
			    { id: 2, name: '包数' }, 
			    { id: 3, name: '连接数'}, 
			    { id: 4, name: '带宽使用率'}
			],
			selected: '1',
			onSelect: function(d) {
				var timeInterval = $('.device-view-timeinterval').combobox('getValue');
				var rowCount = $('.device-view-displaycount').combobox('getValue');

				datagridObj_div.load({
					'indicator': d.id,
					'timePerid': timeInterval,
					"onePageRows": rowCount
				});
			}
		}]
	});
	
	/*$("#device_view_search").linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			
		}
	});*/

	$("#device_view_export").linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			top.location = oc.resource.getUrl("netflow/device/export.htm");
		}
	});
	
	var timeInterval = $('.device-view-timeinterval').combobox('getValue'),
		displayCount = $('.device-displaycount').combobox('getValue');
	
	datagridObj_div=oc.ui.datagrid({
		queryForm: deviceViewForm,
		selector: $('#device_view_list_datagrid'),
		url:oc.resource.getUrl('netflow/device/findManagedDeviceList.htm'),
		queryParams: {
			rowCount: displayCount,
			timePerid: timeInterval,
			rowCount: 20,
			needPagination: true,
			querySize: 20
		},
	    octoolbar:{
			left:[reportMainCenterTop,{
				iconCls: 'icon-search',
				text:"搜索",
				onClick: function(){
					//TODO
					datagridObj_div.reLoad();
				}
			}]
	    },
		sortOrder: 'desc',
		sortName: 'flowIn',
		columns:[[
	         {field:'name',title:'设备名称',sortable:false,width:'10%',align:'center'},
	         {field:'flowIn',title:'流入流量',sortable:true, width:'8%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'flowOut',title:'流出流量',sortable:true,width:'8%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'flowTotal',title:'总流量',sortable:true,width:'7%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'packetIn',title:'流入包数',sortable:true,width:'7%',align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetOut',title:'流出包数',sortable:true,width:'7%',align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetTotal',title:'总包数',sortable:true,width:'6%',align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'speedIn',title:'流入速率',sortable:true,width:'6%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'speedOut',title:'流出速率',sortable:true,width:'6%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'speedTotal',title:'总速率',sortable:true,width:'6%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
        		 return (value == null ? 0 : value) + '%';
	         }},
	         {field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return (value == null ? 0 : value) + '%';
	         }},
	         {field:'bwUsage',title:'带宽使用率',sortable:true,width:'7%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return (value == null ? 0 : value) + '%';
	         }},
	         {field:'flowPctge',title:'占比',sortable:true,width:'5%',align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return (value == null ? 0 : (value*100).toFixed(2)) + '%';
	         }}
	     ]],
	     onLoadSuccess: function(data) {
	    	//不显示每页条数列表
			var p = datagridDiv.datagrid('getPager');
			$(p).pagination({
				showPageList: false
			});
			
			$('.export-device-view-list').on('click', function(e) {
				//TODO
			})
	     }
	});
	
})();