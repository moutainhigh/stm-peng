$(function() {
	device_group_netflow_datagrid = oc.ui.datagrid({
		selector : $("#datagrid_device_group_netflow"),
		queryParams : {
			time : $('#time').combobox('getValue'),
			displayCount : $('#displayCount').combobox('getValue')
		},
		url : oc.resource.getUrl("netflow/deviceGroupnetflow/list.htm"),
		width : 'auto',
		autoRowHeight : false,
		onLoadSuccess : function(data) {
			$('#container').highcharts(
					{
						chart : {
							backgroundColor : '#011d01',
							// height : 240,
							// width : 680,
							type : 'line',
							borderRadius : 3,
							borderWidth : 1,
							borderColor : '#000',
							ignoreHiddenSeries : false,
						},
						credits : {
							enabled : false
						},
						exporting : {
							enabled : false
						},
						colors : [ '#ff0000', "#ffff00", '#0000ff', '#00ff00',
								'#ff00ff', '#00ffff',
								// 未变
								'#DB843D', '#92A8CD', '#A47D7C', '#B5CA92' ],
						title : {
							text : '',
						},
						xAxis : {
							categories : data.categories,
							// categories : [ '1', '2', '3', '4', '5', '6', '7',
							// '8', '9', '10', '11', '12' ],
							labels : {
								style : {
									color : '#fff'
								}
							},
							lineColor : '#000',
							lineWidth : 1,
							tickWidth : 1,
							tickColor : '#000',
							tickmarkPlacement : 'on'
						},
						yAxis : {
							title : {
								text : '',
							},
							plotLines : [ {
								value : 0,
								width : 1,
								color : '#000000'
							} ],
							gridLineColor : '',
							min : 0,
							labels : {
								format : '{value} MB',
								style : {
									color : '#fff'
								}
							},
							lineColor : '#000',
							lineWidth : 1,
							tickWidth : 1,
							tickColor : '#000',
						},
						legend : {
							layout : 'vertical',
							align : 'right',
							verticalAlign : 'middle',
							itemStyle : {
								color : '#fff',
							},
						},
						plotOptions : {
							series : {
								marker : {
									enabled : false
								}
							}
						},
						series : data.highchartsDatas,
					// series : [
					// {
					// name : 'Tokyo',
					// data : [ 7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2,
					// 26.5, 23.3, 18.3, 13.9, 9.6 ]
					// },
					// {
					// name : 'New York',
					// data : [ 0, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1,
					// 20.1, 14.1, 8.6, 2.5 ]
					// },
					// {
					// name : 'Berlin',
					// data : [ 0, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9,
					// 14.3, 9.0, 3.9, 1.0 ]
					// },
					// {
					// name : 'London',
					// data : [ 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0,
					// 16.6, 14.2, 10.3, 6.6, 4.8 ]
					// },
					// {
					// name : 'London1',
					// data : [ 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0,
					// 16.6, 14.2, 10.3, 6.6, 4.8 ]
					// },
					// {
					// name : 'London2',
					// data : [ 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0,
					// 16.6, 14.2, 10.3, 6.6, 4.8 ]
					// },
					// {
					// name : 'London3',
					// data : [ 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0,
					// 16.6, 14.2, 10.3, 6.6, 4.8 ]
					// },
					// {
					// name : 'London4',
					// data : [ 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0,
					// 16.6, 14.2, 10.3, 6.6, 4.8 ]
					// },
					// {
					// name : 'London5',
					// data : [ 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0,
					// 16.6, 14.2, 10.3, 6.6, 4.8 ]
					// } ]
					});

			var p = $(this).datagrid('getPager');
			$(p).pagination({
				showPageList : false
			});
		},
		columns : [ [ {
			field : 'name',
			title : '设备组名称',
			width : 20
		}, {
			field : 'inFlow',
			title : '流入流量',
			sortable : true,
			width : 20
		}, {
			field : 'outFlow',
			title : '流出流量',
			sortable : true,
			width : 20
		}, {
			field : 'totalFlow',
			sortable : true,
			title : '总流量',
			width : 20
		}, {
			field : 'inPackage',
			title : '流入包数',
			sortable : true,
			width : 20
		}, {
			field : 'outPackage',
			title : '流出包数',
			sortable : true,
			width : 20
		}, {
			field : "totalPackage",
			title : "总包数",
			sortable : true,
			width : 20
		}, {
			field : "inSpeed",
			title : "流入速率",
			sortable : true,
			width : 20
		}, {
			field : "outSpeed",
			title : "流出速率",
			sortable : true,
			width : 20
		}, {
			field : "totalSpeed",
			title : "总速率",
			sortable : true,
			width : 20
		}, {
			field : "accounting",
			title : "占比",
			sortable : true,
			width : 20
		} ] ],
	});

});