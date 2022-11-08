/**
 * 获取不同指标类型对应的datagird的列
 */
/**
 * type: 1:适配我返回的列，2：适配金润返回的列
 */

/**
 * 指标类型改变，加载对应的列
 * type: 1:流量 2：包 3：连接数 4：带宽使用率
 * 
 */
function changeIndicatorDetail_bak(datagrid, type, colName, who, isSession) {
	if(1 == type) {
		datagrid.selector.datagrid({
			columns: getFlowCol(colName, who, isSession)
		});
	} else if(2 == type) {
		datagrid.selector.datagrid({
			columns: getPacketCol(colName, who, isSession)
		});
	} else if(3 == type) {
		datagrid.selector.datagrid({
			columns: getConnectCol(colName, who, isSession)
		});
	} else if(4 == type) {
		datagrid.selector.datagrid({
			columns: getBwCol(colName, who, isSession)
		});
	}
}

function changeIndicatorDetail(datagrid, type, colName, who, isSession) {
	if(1 == type) {
		datagrid.datagrid({
			columns: getFlowCol(colName, who, isSession)
		});
	} else if(2 == type) {
		datagrid.datagrid({
			columns: getPacketCol(colName, who, isSession)
		});
	} else if(3 == type) {
		datagrid.datagrid({
			columns: getConnectCol(colName, who, isSession)
		});
	} else if(4 == type) {
		datagrid.datagrid({
			columns: getBwCol(colName, who, isSession)
		});
	}
}

/**
 * 得到第一列名称：如：终端名称，应用名称。。。
 */
function getNameCol(colName, who) {
	var name;
	if(who == 1) {
		if(colName == 'terminal') {
			name = {
				field:'terminalIp',title:'终端名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			};
		} else if(colName == 'app') {
			name = {
				field:'name',title:'应用名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			};
		} else if(colName == 'proto') {
			name = {
				field:'name',title:'协议名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		} else if(colName == 'nexthop') {
			name = {
				field:'nextHop',title:'下一跳IP',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		} else if(colName == 'tos') {
			name = {
				field:'name',title:'TOS',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		} else if(colName == 'ipg') {
			name = {
				field:'name',title:'IP分组名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		}
	} else if(who == 2) {
		if(colName == 'terminal') {
			name = {
				field:'terminal_name',title:'终端名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			};
		} else if(colName == 'app') {
			name = {
				field:'app_name',title:'应用名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			};
		} else if(colName == 'session') {
			var srcIp = {
				field:'src_ip',title:'源IP',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			};
			var dstIp = {
				field:'dst_ip',title:'目的IP',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			};
			name = [srcIp, dstIp];
		} else if(colName == 'proto') {
			name = {
				field:'proto_name',title:'协议名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		} else if(colName == 'nexthop') {
			name = {
				field:'next_hop',title:'下一跳IP',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		} else if(colName == 'tos') {
			name = {
				field:'tos_name',title:'TOS',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		} else if(colName == 'ipg') {
			name = {
				field:'ipgroup_name',title:'IP分组名称',sortable:false,width:30,align:'left',
				formatter: function(value, row, index) {
					return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
				}
			}
		}
	}
	return name;
}

/**
 * 流量指标对应的终端的列
 */
function getFlowCol(colName, who, isSession) {
	if(who == 1) {
		if(isSession) {
			var flowColumns = [[
				{
					field:'srcIp',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
				{
					field:'dstIp',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
                {
                	field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, null);
                	}
                },
                {
                	field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, null);
                	}
                },
                {
                	field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, null);
                	}
                },
                {
                	field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, 'speed');
                	}
                },
                {
                	field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, 'speed');
                	}
                },
                {
                	field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, 'speed');
                	}
                },
                {
                	field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return pctgeFormatter(value);
                	}
                }
                ]];
			return flowColumns;
		} else {
			var name = getNameCol(colName, 1);
			var flowColumns = [[
                name,
                {
                	field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, null);
                	}
                },
                {
                	field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, null);
                	}
                },
                {
                	field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, null);
                	}
                },
                {
                	field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, 'speed');
                	}
                },
                {
                	field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, 'speed');
                	}
                },
                {
                	field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return flowFormatter(value, 'speed');
                	}
                },
                {
                	field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
                	formatter:function(value,row,index) {
                		return pctgeFormatter(value);
                	}
                }
                ]];
			return flowColumns;
		}
	} else if(who == 2) {
		if(isSession) {
			var flowColumns = [[
				{
					field:'src_ip',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
				{
					field:'dst_ip',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
                {
               	 field:'in_flows',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
               		 return flowFormatter(value, null);
               	 }
                },
                {
               	 field:'out_flows',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
               		 return flowFormatter(value, null);
               	 }
                },
                {
               	 field:'total_flows',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
               		 return flowFormatter(value, null);
               	 }
                },
                {
               	 field:'in_speed',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
               		 return flowFormatter(value, 'speed');
               	 }
                },
                {
               	 field:'out_speed',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
               		 return flowFormatter(value, 'speed');
               	 }
                },
                {
               	 field:'total_speed',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
               		 return flowFormatter(value, 'speed');
               	 }
                },
                {
               	 field:'flows_rate',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
               	 formatter:function(value,row,index) {
           		 	return pctgeFormatter(value);
               	 }
                }
    	   	]];
    	   	return flowColumns;
		} else {
			var name = getNameCol(colName, 2);
			var flowColumns = [[
	            name,
	            {
	           	 field:'in_flows',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	           		 return flowFormatter(value, null);
	           	 }
	            },
	            {
	           	 field:'out_flows',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	           		 return flowFormatter(value, null);
	           	 }
	            },
	            {
	           	 field:'total_flows',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	           		 return flowFormatter(value, null);
	           	 }
	            },
	            {
	           	 field:'in_speed',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	           		 return flowFormatter(value, 'speed');
	           	 }
	            },
	            {
	           	 field:'out_speed',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	           		 return flowFormatter(value, 'speed');
	           	 }
	            },
	            {
	           	 field:'total_speed',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	           		 return flowFormatter(value, 'speed');
	           	 }
	            },
	            {
	           	 field:'flows_rate',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
	           	 formatter:function(value,row,index) {
	       		 	return pctgeFormatter(value);
	           	 }
	            }
		   	]];
		   	return flowColumns;
		}
	} else {
		return null;
	}
}

/**
 * 包数对应的列
 * @param colName
 * @param who
 * @returns
 */
function getPacketCol(colName, who, isSession) {
	if(who == 1) {
		if(isSession) {
			var packetColumns = [[
	              {
						field:'srcIp',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value ;
						}
					},
					{
						field:'dstIp',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
	              {
	             	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
	             	 formatter:function(value, row, index) {
	             		 return packetFormatter(value, null);
	             	 }
	              },
	              {
	             	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
	             	 formatter:function(value, row, index) {
	             		 return packetFormatter(value, null);
	             	 }
	              },
	              {
	             	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
	             	 formatter:function(value, row, index) {
	             		 return packetFormatter(value, null);
	             	 }
	              },
	              {
	             	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
	             	 formatter: function(value, row, index) {
	             		 return packetFormatter(value, 'speed');
	             	 }
	              },
	              {
	             	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
	             	 formatter: function(value, row, index) {
	             		 return packetFormatter(value, 'speed');
	             	 }
	              },
	              {
	             	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
	             	 formatter: function(value, row, index) {
	             		 return packetFormatter(value, 'speed');
	             	 }
	              },
	              {
	             	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
	             	 formatter:function(value,row,index) {
	         		 	return pctgeFormatter(value);
	             	 }
	              }
	     	]];
	     	return packetColumns;
		} else {
			var name = getNameCol(colName, 1);
			var packetColumns = [[
	              name,
	              {
	             	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
	             	 formatter:function(value, row, index) {
	             		 return packetFormatter(value, null);
	             	 }
	              },
	              {
	             	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
	             	 formatter:function(value, row, index) {
	             		 return packetFormatter(value, null);
	             	 }
	              },
	              {
	             	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
	             	 formatter:function(value, row, index) {
	             		 return packetFormatter(value, null);
	             	 }
	              },
	              {
	             	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
	             	 formatter: function(value, row, index) {
	             		 return packetFormatter(value, 'speed');
	             	 }
	              },
	              {
	             	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
	             	 formatter: function(value, row, index) {
	             		 return packetFormatter(value, 'speed');
	             	 }
	              },
	              {
	             	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
	             	 formatter: function(value, row, index) {
	             		 return packetFormatter(value, 'speed');
	             	 }
	              },
	              {
	             	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
	             	 formatter:function(value,row,index) {
	         		 	return pctgeFormatter(value);
	             	 }
	              }
	     	]];
	     	return packetColumns;
		}
	} else if(who == 2) {
		if(isSession) {
			var packetColumns = [[
					{
						field:'src_ip',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
					{
						field:'dst_ip',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
                  {
                 	 field:'in_packages',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
                 	 formatter:function(value, row, index) {
                 		 return packetFormatter(value, null);
                 	 }
                  },
                  {
                 	 field:'out_packages',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
                 	 formatter:function(value, row, index) {
                 		 return packetFormatter(value, null);
                 	 }
                  },
                  {
                 	 field:'total_package',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
                 	 formatter:function(value, row, index) {
                 		 return packetFormatter(value, null);
                 	 }
                  },
                  {
                 	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
                 	 formatter: function(value, row, index) {
                 		 return packetFormatter(value, 'speed');
                 	 }
                  },
                  {
                 	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
                 	 formatter: function(value, row, index) {
                 		 return packetFormatter(value, 'speed');
                 	 }
                  },
                  {
                 	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
                 	 formatter: function(value, row, index) {
                 		 return packetFormatter(value, 'speed');
                 	 }
                  },
                  {
                 	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
                 	 formatter:function(value,row,index) {
             		 	return pctgeFormatter(value);
                 	 }
                  }
         	]];
         	return packetColumns;
		} else {
			var name = getNameCol(colName, 2);
			var packetColumns = [[
                  name,
                  {
                 	 field:'in_packages',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
                 	 formatter:function(value, row, index) {
                 		 return packetFormatter(value, null);
                 	 }
                  },
                  {
                 	 field:'out_packages',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
                 	 formatter:function(value, row, index) {
                 		 return packetFormatter(value, null);
                 	 }
                  },
                  {
                 	 field:'total_package',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
                 	 formatter:function(value, row, index) {
                 		 return packetFormatter(value, null);
                 	 }
                  },
                  {
                 	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
                 	 formatter: function(value, row, index) {
                 		 return packetFormatter(value, 'speed');
                 	 }
                  },
                  {
                 	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
                 	 formatter: function(value, row, index) {
                 		 return packetFormatter(value, 'speed');
                 	 }
                  },
                  {
                 	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
                 	 formatter: function(value, row, index) {
                 		 return packetFormatter(value, 'speed');
                 	 }
                  },
                  {
                 	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
                 	 formatter:function(value,row,index) {
             		 	return pctgeFormatter(value);
                 	 }
                  }
         	]];
         	return packetColumns;
		}
	} else {
		return null;
	}
}

/**
 * 连接数对应的列
 * @param colName
 * @param who
 * @returns {Array}
 */
function getConnectCol(colName, who, isSession) {
	if(who == 1) {
		if(isSession) {
			var connectColumns = [[
				{
					field:'srcIp',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
				{
					field:'dstIp',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
		   		{
		       	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
		       	 formatter:function(value,row,index) {
		   		 	return pctgeFormatter(value);
		       	 }
		       }
		   	]];
		   	return connectColumns;
		} else {
			var name = getNameCol(colName, who);
			var connectColumns = [[
		   		name,
		   		{
		       	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
		       	 formatter:function(value,row,index) {
		   		 	return pctgeFormatter(value);
		       	 }
		       }
		   	]];
		   	return connectColumns;
		}
	} else if(who == 2) {
		if(isSession) {
			var connectColumns = [[
				{
					field:'src_ip',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
				{
					field:'dst_ip',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
					formatter:function(value,row,index) {
						return value || '';
					}
				},
		   		{
		       	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
		       	 formatter:function(value,row,index) {
		   		 	return pctgeFormatter(value);
		       	 }
		       }
		   	]];
		   	return connectColumns;
		} else {
			var name = getNameCol(colName, who);
			var connectColumns = [[
		   		name,
		   		{
		       	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, null);
		       	 }
		       },
		       {
		       	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
		       	 formatter: function(value, row, index) {
		       		 return connectFormatter(value, 'speed');
		       	 }
		       },
		       {
		       	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left', order: 'desc',
		       	 formatter:function(value,row,index) {
		   		 	return pctgeFormatter(value);
		       	 }
		       }
		   	]];
		   	return connectColumns;
		}
	}
}

/**
 * 带宽使用率对应的列
 * @param colName
 * @param who
 */
function getBwCol(colName, who, isSession) {
	if(who == 1) {
		if(isSession) {
			var bwColumns = [[
		    		{
						field:'srcIp',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
					{
						field:'dstIp',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
		    		{
		    	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            },
		            {
		    	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            },
		            {
		    	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            }
		    	]];
		  	return bwColumns;
		} else {
			var name = getNameCol(colName, who);
			var bwColumns = [[
		    		name,
		    		{
		    	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            },
		            {
		    	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            },
		            {
		    	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            }
		    	]];
		  	return bwColumns;
		}
	} else if(who == 2) {
		if(isSession) {
			var bwColumns = [[
		    		{
						field:'src_ip',title:'源IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
					{
						field:'dst_ip',title:'目的IP',sortable:true, width:20,align:'left',order: 'desc',
						formatter:function(value,row,index) {
							return value || '';
						}
					},
		    		{
		    	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            },
		            {
		    	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            },
		            {
		    	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		    	       	 formatter:function(value,row,index) {
		    	       		 return pctgeFormatter(value);
		    	       	 }
		            }
		    	]];
		  	return bwColumns;
		} else {
			var name = getNameCol(colName, who);
			var bwColumns = [[
	    		name,
	    		{
	    	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	    	       	 formatter:function(value,row,index) {
	    	       		 return pctgeFormatter(value);
	    	       	 }
	            },
	            {
	    	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	    	       	 formatter:function(value,row,index) {
	    	       		 return pctgeFormatter(value);
	    	       	 }
	            },
	            {
	    	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	    	       	 formatter:function(value,row,index) {
	    	       		 return pctgeFormatter(value);
	    	       	 }
	            }
            ]];
	  		return bwColumns;
		}
	}
	
}

/**
 * 占比显示格式化
 * @param value
 * @returns {String}
 */
function pctgeFormatter(value) {
	if(value == null) {
		return '<0.01' + '%';
	} else if(value*100 == 0) {
		return '0.00%';
	} else if(value*100 == 0.01) {
		return '0.01%';
	} else if(value*100 < 0.01) {
		return '<0.01%';
	} else {
		return (value*100).toFixed(2) + '%';
	}
}

/**
 * 流量显示格式化
 * @param value
 * @param type
 * @returns
 */
function flowFormatter(value, type) {
	if(value != null) {
		 var kb = 1024;
		 var mb = kb*kb;
		 var gb = mb*kb;
		 var tb = gb*kb;
		 if(value >= tb) {
			 var ret = value / tb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Tbps' : 'TB');
		 } else if(value >= gb) {
			 var ret = value / gb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Gbps' : 'GB');
		 } else if(value >= mb) {
			 var ret = value / mb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Mbps' : 'MB');
		 } else if(value >= kb) {
			 return (value/kb).toFixed(2) + (type == 'speed' ? 'Kbps' : 'KB');
		 } else {
			 return (value*1.00).toFixed(2) + (type == 'speed' ? 'Bps' : 'B');
		 }
	 } else {
		 return (type == 'speed' ? '0Bps' : '0B');
	 }
}

/**
 * 包显示格式化
 * @param value
 * @param type
 * @returns
 */
function packetFormatter(value, type) {
	if(value != null) {
		 var kb = 1024;
		 var mb = kb*kb;
		 var gb = mb*kb;
		 if(value >= gb) {
			 var ret = value / gb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Gpps' : 'GP');
		 } else if(value >= mb) {
			 var ret = value / mb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Mpps' : 'MP');
		 } else if(value >= kb){
			 return (value/kb).toFixed(2) + (type == 'speed' ? 'Kpps' : 'KP');
		 } else {
			 return (value*1.0).toFixed(2) + (type == 'speed' ? 'Pps' : 'P');
		 }
	 } else {
		 return (type == 'speed' ? '0Pps' : '0P');
	 }
}

/**
 * 连接数显示格式化
 * @param value
 * @param type
 * @returns
 */
function connectFormatter(value, type) {
	if(value != null) {
		 var kb = 1024;
		 var mb = kb*kb;
		 var gb = mb*kb;
		 if(value >= gb) {
			 var ret = value / gb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Gfps' : 'GF');
		 } else if(value >= mb) {
			 var ret = value / mb;
			 ret = ret.toFixed(2);
			 return ret + (type == 'speed' ? 'Mfps' : 'MF');
		 } else if(value >= kb){
			 return (value/kb).toFixed(2) + (type == 'speed' ? 'Kfps' : 'KF');
		 } else {
			 return (value*1.0).toFixed(2) + (type == 'speed' ? 'Fps' : 'F');
		 }
	 } else {
		 return (type == 'speed' ? '0Fps' : '0F');;
	 }
}

function parseTime(timeTag) {
	var timeObj = {}, start, end;
	switch (timeTag) {
	case '1hour':
		var now = moment();
		var minute = now.minute();
		minute = minute - minute % 10;
		now.minute(minute);
		now.second(0);
		end = now.format("YYYY-MM-DD HH:mm:ss");
		start = now.subtract(1, 'hours').format("YYYY-MM-DD HH:mm:ss");
		break;
	case '2hour':
		var now = moment();
		var minute = now.minute();
		minute = minute - minute % 10;
		now.minute(minute);
		now.second(0);
		end = now.format("YYYY-MM-DD HH:mm:ss");
		start = now.subtract(2, 'hours').format("YYYY-MM-DD HH:mm:ss");
		break;
	case '6hour':
		var now = moment();
		var minute = now.minute();
		minute = minute - minute % 10;
		now.minute(minute);
		now.second(0);
		end = now.format("YYYY-MM-DD HH:mm:ss");
		start = now.subtract(6, 'hours').format("YYYY-MM-DD HH:mm:ss");
		break;
	case '1day':
		var now = moment();
		var minute = now.minute();
		minute = minute - minute % 10;
		now.minute(minute);
		now.second(0);
		end = now.format("YYYY-MM-DD HH:mm:ss");
		start = now.subtract(1, 'days').format("YYYY-MM-DD HH:mm:ss");
		break;
	case '7day':
		var now = moment();
		var hour = now.hour();
		hour = hour - hour % 2;
		now.hour(hour);
		now.minute(0);
		now.second(0);
		end = now.format("YYYY-MM-DD HH:mm:ss");
		start = now.subtract(7, 'days').format("YYYY-MM-DD HH:mm:ss");
		break;
	default:
		var now = moment();
		var end = now.format("YYYY-MM-DD HH:mm:ss");
		var start = now.format("YYYY-MM-DD HH:mm:ss");
	}

	timeObj.startTime = start;
	timeObj.endTime = end;

	return timeObj;
}

//=====================================接口主页面列显示===============================================
/**
 * 接口流量对应的列
 */
function getIfFlowColumns() {
	var flowColumns = [[
		{
			field:'ifName',title:'接口名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
		{
			field:'name',title:'设备名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},		
        {
        	field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 接口包对应的列
 * @returns {Array}
 */
function getIfPacketColumns() {
	var packetColumns = [[
			{
				field:'ifName',title:'接口名称',sortable:true, width:20,align:'left',order: 'desc',
				formatter:function(value,row,index) {
					return value || '';
				}
			},
			{
				field:'name',title:'设备名称',sortable:true, width:20,align:'left',order: 'desc',
				formatter:function(value,row,index) {
					return value || '';
				}
			},
          {
         	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
         	 formatter:function(value, row, index) {
         		 return packetFormatter(value, null);
         	 }
          },
          {
         	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
         	 formatter:function(value, row, index) {
         		 return packetFormatter(value, null);
         	 }
          },
          {
         	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
         	 formatter:function(value, row, index) {
         		 return packetFormatter(value, null);
         	 }
          },
          {
         	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
         	 formatter: function(value, row, index) {
         		 return packetFormatter(value, 'speed');
         	 }
          },
          {
         	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
         	 formatter: function(value, row, index) {
         		 return packetFormatter(value, 'speed');
         	 }
          },
          {
         	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
         	 formatter: function(value, row, index) {
         		 return packetFormatter(value, 'speed');
         	 }
          },
          {
         	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
         	 formatter:function(value,row,index) {
     		 	return pctgeFormatter(value);
         	 }
          }
 	]];
 	return packetColumns;
}

/**
 * 接口连接数对应的列
 */
function getIfConnectColumns() {
	var connectColumns = [[
		{
			field:'ifName',title:'接口名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
		{
			field:'name',title:'设备名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
   		{
       	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return connectFormatter(value, null);
       	 }
       },
       {
       	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return connectFormatter(value, null);
       	 }
       },
       {
       	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return connectFormatter(value, null);
       	 }
       },
       {
       	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return connectFormatter(value, 'speed');
       	 }
       },
       {
       	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return connectFormatter(value, 'speed');
       	 }
       },
       {
       	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return connectFormatter(value, 'speed');
       	 }
       },
       {
       	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
       }
   	]];
   	return connectColumns;
}

/**
 * 接口带宽使用率对应的列
 */
function getIfBwColumns() {
	var bwColumns = [[
			{
				field:'ifName',title:'接口名称',sortable:true, width:20,align:'left',order: 'desc',
				formatter:function(value,row,index) {
					return value || '';
				}
			},
			{
				field:'name',title:'设备名称',sortable:true, width:20,align:'left',order: 'desc',
				formatter:function(value,row,index) {
					return value || '';
				}
			},
			{
		       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		       	 formatter:function(value,row,index) {
		       		 return pctgeFormatter(value);
		       	 }
	        },
	        {
		       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		       	 formatter:function(value,row,index) {
		       		 return pctgeFormatter(value);
		       	 }
	        },
	        {
		       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
		       	 formatter:function(value,row,index) {
		       		 return pctgeFormatter(value);
		       	 }
	        }
		]];
	return bwColumns;
}

/**
 * 接口指标类型改变方法
 */
function changeIfIndicator(datagrid, type) {
	if(type == 1) {
		datagrid.datagrid({
			columns: getIfFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getIfPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getIfConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getIfBwColumns()
		});
	}
}

/*==============设备组对应列显示=============================*/
/**
 * 设备组流量列
 */
function getDeviceGroupFlowColumns() {
	var flowColumns = [[
		{
			field:'name',title:'设备组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},		
        {
        	field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 设备组包对应的列
 * @returns {Array}
 */
function getDeviceGroupPacketColumns() {
	var packetColumns = [[
		{
			field:'name',title:'设备组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
        {
       	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
        }
]];
return packetColumns;
}

/**
 * 设备组连接数对应的列
 * @returns {Array}
 */
function getDeviceGroupConnectColumns() {
	var connectColumns = [[
   		{
   			field:'name',title:'设备组名称',sortable:true, width:20,align:'left',order: 'desc',
   			formatter:function(value,row,index) {
   				return value || '';
   			}
   		},
      		{
          	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
          	 formatter:function(value,row,index) {
      		 	return pctgeFormatter(value);
          	 }
          }
      	]];
      	return connectColumns;
}

/**
 * 设备组带宽使用率对应的列
 * @returns {Array}
 */
function getDeviceGroupBwColumns() {
	var bwColumns = [[
		{
			field:'name',title:'设备组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
return bwColumns;
}

/**
 * 设备组指标类型改变方法
 * @param datagrid
 * @param type
 */
function changeDeviceGroupIndicator(datagrid, type) {
	if(type == 1) {
		datagrid.datagrid({
			columns: getDeviceGroupFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getDeviceGroupPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getDeviceGroupConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getDeviceGroupBwColumns()
		});
	}
}
/*==============设备组对应列显示结束=============================*/


/*==============最大化页面公共方法=============================*/
function initMoreTimeInterval(selfTimeIntervalClass, cusDateWrapper, searchId, selfIndicatorClass, datagridObj, isIf) {

	var displayCount = 20;
	
	oc.ui.combobox({
		selector : selfTimeIntervalClass,
		placeholder: null,
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
		selected : '1hour',
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				$(cusDateWrapper).removeClass('hide');
				$(searchId).removeClass('hide');
				
			} else {
				$(cusDateWrapper).addClass('hide');
				$(searchId).addClass('hide');
				
				var indictor = $(selfIndicatorClass).combobox('getValue');
				var data = {
						'onePageRows' : displayCount,
						"deviceIp" : $('.netflow-main').data('deviceIp'),
						"sort" : "in_flows",
						"order" : 'desc',
						"needPagination":false,
						"showpagination" : false,
						"recordCount" : displayCount,
						"querySize" : displayCount,
						"rowCount":displayCount,
						"recordCount":displayCount,
						'timePerid': d.id
				};
				if(isIf) {
					data = {
						'onePageRows' : displayCount,
						"ifId" : $('.netflow-main').data('ifId'),
						"sort" : "in_flows",
						"order" : 'desc',
						"needPagination":false,
						"showpagination" : false,
						"recordCount" : displayCount,
						"querySize" : displayCount,
						"rowCount":displayCount,
						"recordCount":displayCount,
						'timePerid': d.id
					};
				}
				datagridObj.datagrid('load', data);
			}
			
		}
	});
}

function initMoreType(selfIndicatorClass, datagridObj, colName, who, isSession) {
	oc.ui.combobox({
		selector : selfIndicatorClass,
		placeholder: null,
		data : [{
			id : 1,
			name : '流量'
		}, {
			id: 2,
			name: '包数'
		}, {
			id: 3,
			name: '连接数'
		}/*, {
			id: 4,
			name: '带宽使用率'
		}*/],
		selected: '1',
		onSelect : function(d) {
			changeIndicatorDetail(datagridObj, d.id, colName, who, isSession);
		}
	});
}	
	
function initExportBtn(expBtnId, exportFn) {
	$(expBtnId).linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			exportFn();
		}
	});
}

function initSearchBtn(searchBtnId, startTimeCls, endTimeCls, searchFn) {
	
	$(searchBtnId).linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var stime = $(startTimeCls).datetimebox('getValue');
			var etime = $(endTimeCls).datetimebox('getValue');
			var msg = checkCustomTime(stime, etime);
			if('' != msg) {
				alert(msg);
				return;
			} else {
				if(searchFn) searchFn();
			}
		}
	});
}

function initCustomDateStatus(selfIndicatorCls, startTimeCls, endTimeCls, selfCusDateWrapperCls, searchBtnId,
		selfTimeIntervalCls, dlgWrapperCls, detailTimeIntervalCls, detailIndicatorCls) {

	var timeInterval = $(detailTimeIntervalCls).combobox('getValue');
	var type = $(detailIndicatorCls).combobox('getValue');

	if(type) {
		$(selfIndicatorCls).combobox('setValue', type);
	}
	
	$(startTimeCls).datetimebox();
	$(endTimeCls).datetimebox();

	$(selfCusDateWrapperCls).addClass('hide');
	$(searchBtnId).addClass('hide');
	if(timeInterval) {
		$(selfTimeIntervalCls).combobox('setValue', timeInterval);

		if(timeInterval == 'custom') {
			$(selfCusDateWrapperCls).removeClass('hide');
			$(searchBtnId).removeClass('hide');

			var sstime = $(dlgWrapperCls).data('starttime');
			var eetime = $(dlgWrapperCls).data('endtime');
			
			$(startTimeCls).datetimebox('setValue', sstime);
			$(endTimeCls).datetimebox('setValue', eetime);
		}
	}
}
/*==============最大化页面公共方法结束=============================*/

/*==============接口组列=============================*/
/**
 * 接口组流量列
 */
function getIfGroupFlowColumns() {
	var flowColumns = [[
		{
			field:'name',title:'接口组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},		
        {
        	field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 接口组包对应的列
 * @returns {Array}
 */
function getIfGroupPacketColumns() {
	var packetColumns = [[
		{
			field:'name',title:'接口组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
        {
       	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
        }
]];
return packetColumns;
}

/**
 * 接口组连接数对应的列
 * @returns {Array}
 */
function getIfGroupConnectColumns() {
	var connectColumns = [[
   		{
   			field:'name',title:'接口组名称',sortable:true, width:20,align:'left',order: 'desc',
   			formatter:function(value,row,index) {
   				return value || '';
   			}
   		},
      		{
          	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
          	 formatter:function(value,row,index) {
      		 	return pctgeFormatter(value);
          	 }
          }
      	]];
      	return connectColumns;
}

/**
 * 接口组带宽使用率对应的列
 * @returns {Array}
 */
function getIfGroupBwColumns() {
	var bwColumns = [[
		{
			field:'name',title:'接口组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
return bwColumns;
}

/**
 * 设备组指标类型改变方法
 * @param datagrid
 * @param type
 */
function changeIfGroupIndicator(datagrid, type) {
	if(type == 1) {
		datagrid.datagrid({
			columns: getIfGroupFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getIfGroupPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getIfGroupConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getIfGroupBwColumns()
		});
	}
}
/*==============接口组列结束=============================*/



/*==============IP分组列=============================*/
/**
 * IP组流量列
 */
function getIpGroupFlowColumns() {
	var flowColumns = [[
		{
			field:'name',title:'IP分组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},		
        {
        	field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, null);
        	}
        },
        {
        	field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return flowFormatter(value, 'speed');
        	}
        },
        {
        	field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
        	formatter:function(value,row,index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 接口组包对应的列
 * @returns {Array}
 */
function getIpGroupPacketColumns() {
	var packetColumns = [[
		{
			field:'name',title:'IP分组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
        {
       	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
       	 formatter:function(value, row, index) {
       		 return packetFormatter(value, null);
       	 }
        },
        {
       	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
        }
]];
return packetColumns;
}

/**
 * IP分组连接数对应的列
 * @returns {Array}
 */
function getIpGroupConnectColumns() {
	var connectColumns = [[
   		{
   			field:'name',title:'IP分组名称',sortable:true, width:20,align:'left',order: 'desc',
   			formatter:function(value,row,index) {
   				return value || '';
   			}
   		},
      		{
          	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
          	 formatter:function(value,row,index) {
      		 	return pctgeFormatter(value);
          	 }
          }
      	]];
      	return connectColumns;
}

/**
 * IP分组带宽使用率对应的列
 * @returns {Array}
 */
function getIpGroupBwColumns() {
	var bwColumns = [[
		{
			field:'name',title:'IP分组名称',sortable:true, width:20,align:'left',order: 'desc',
			formatter:function(value,row,index) {
				return value || '';
			}
		},
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
return bwColumns;
}

/**
 * IP分组指标类型改变方法
 * @param datagrid
 * @param type
 */
function changeIpGroupIndicator(datagrid, type) {
	if(type == 1) {
		datagrid.datagrid({
			columns: getIpGroupFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getIpGroupPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getIpGroupConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getIpGroupBwColumns()
		});
	}
}
/*==============IP分组列结束=============================*/


/*==============终端对应的列=============================*/
/**
 * 终端组流量列
 */
function getTerminalFlowColumns() {
	var flowColumns = [[
		{
			field:'terminal_name',
			title:'终端名称',
			sortable:false, 
			width:20, 
			align:'center',
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'in_flows', 
			title:'流入流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
		{
			field:'out_flows', 
			title:'流出流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
		{
			field:'total_flows', 
			title:'总流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
        {
			field:'in_speed', 
			title:'流入速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
		},
        {
			field:'out_speed', 
			title:'流出速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
		},
        {
			field:'total_speed', 
			title:'总速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
        },
        {
        	field:'flowPctge', 
        	title:'占比', 
        	sortable:true, 
        	order: 'desc',
        	formatter: function(value, row, index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 终端包对应的列
 * @returns {Array}
 */
function getTerminalPacketColumns() {
	var packetColumns = [[
	    {
	    	field:'terminal_name',
	    	title:'终端名称',
	    	sortable:false,
	    	width:20,
	    	align:'center',
	    	formatter: function(value, row, index) {
	    		return value || '';
	    	}
	    },
        {
	    	field:'in_packages',
	    	title:'流入包数',
	    	sortable:true,
	    	width:20,
	    	align:'center',
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
	    	field:'out_packages', 
	    	title:'流出包数', 
	    	sortable:true, 
	    	width:20, 
	    	align:'center', 
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
	    	field:'total_packages', 
	    	title:'总包数', 
	    	sortable:true, 
	    	width:20, 
	    	align:'center', 
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
       	 	field: 'packetInSpeed', 
       	 	title: '流入速率', 
       	 	sortable: true, 
       	 	width: 20, 
       	 	align: 'left',
       	 	order: 'desc',
	       	formatter: function(value, row, index) {
	       		return packetFormatter(value, 'speed');
	       	}
        },
        {
       	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
        }
]];
return packetColumns;
}

/**
 * 终端连接数对应的列
 * @returns {Array}
 */
function getTermibalConnectColumns() {
	var connectColumns = [[
   	    {
	    	field:'terminal_name',
	    	title:'终端名称',
	    	sortable:false,
	    	width:20,
	    	align:'center',
	    	formatter: function(value, row, index) {
	    		return value || '';
	    	}
	    },
      		{
          	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
          	 formatter:function(value,row,index) {
      		 	return pctgeFormatter(value);
          	 }
          }
      	]];
      	return connectColumns;
}

/**
 * 终端带宽使用率对应的列
 * @returns {Array}
 */
function getTerminalBwColumns() {
	var bwColumns = [[
  	    {
	    	field:'terminal_name',
	    	title:'终端名称',
	    	sortable:false,
	    	width:20,
	    	align:'center',
	    	formatter: function(value, row, index) {
	    		return value || '';
	    	}
	    },
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
return bwColumns;
}

/**
 * 设备组指标类型改变方法
 * @param datagrid
 * @param type
 */
function changeTerminalIndicator(datagrid, type) {
	if(type == 1) {
		datagrid.datagrid({
			columns: getTerminalFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getTerminalPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getTermibalConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getTerminalBwColumns()
		});
	}
}


/*==============会话对应的列===========================*/
/**
 *会话组流量列
 */
function getSessionFlowColumns() {
	var flowColumns = [[
		{
			field:'src_ip',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'dst_ip',
			title:'目的地址',
			sortable:false,
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'in_flows', 
			title:'流入流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
		{
			field:'out_flows', 
			title:'流出流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
		{
			field:'total_flows', 
			title:'总流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
        {
			field:'in_speed', 
			title:'流入速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
		},
        {
			field:'out_speed', 
			title:'流出速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
		},
        {
			field:'total_speed', 
			title:'总速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
        },
        {
        	field:'flowPctge', 
        	title:'占比', 
        	sortable:true, 
        	order: 'desc',
        	formatter: function(value, row, index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 会话包对应的列
 * @returns {Array}
 */
function getSessionPacketColumns() {
	var packetColumns = [[
		{
			field:'src_ip',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'dst_ip',
			title:'目的地址',
			sortable:false,
			formatter: function(value, row, index) {
				return value || '';
			}
		},
        {
	    	field:'in_packages',
	    	title:'流入包数',
	    	sortable:true,
	    	width:20,
	    	align:'center',
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
	    	field:'out_packages', 
	    	title:'流出包数', 
	    	sortable:true, 
	    	width:20, 
	    	align:'center', 
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
	    	field:'total_packages', 
	    	title:'总包数', 
	    	sortable:true, 
	    	width:20, 
	    	align:'center', 
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
       	 	field: 'packetInSpeed', 
       	 	title: '流入速率', 
       	 	sortable: true, 
       	 	width: 20, 
       	 	align: 'left',
       	 	order: 'desc',
	       	formatter: function(value, row, index) {
	       		return packetFormatter(value, 'speed');
	       	}
        },
        {
       	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
        }
]];
return packetColumns;
}

/**
 * 会话连接数对应的列
 * @returns {Array}
 */
function getSessionConnectColumns() {
	var connectColumns = [[
		{
			field:'src_ip',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'dst_ip',
			title:'目的地址',
			sortable:false,
			formatter: function(value, row, index) {
				return value || '';
			}
		},
      		{
          	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
          	 formatter:function(value,row,index) {
      		 	return pctgeFormatter(value);
          	 }
          }
      	]];
      	return connectColumns;
}

/**
 * 会话带宽使用率对应的列
 * @returns {Array}
 */
function getSessionBwColumns() {
	var bwColumns = [[
 		{
			field:'src_ip',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'dst_ip',
			title:'目的地址',
			sortable:false,
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
return bwColumns;
}

/**
 * 会话指标类型改变方法
 * @param datagrid
 * @param type
 */
function changeSessionIndicator(datagrid, type) {

	if(type == 1) {
		datagrid.datagrid({
			columns: getSessionFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getSessionPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getSessionConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getSessionBwColumns()
		});
	}
}
/*==============会话对应的列结束===========================*/

/*==============应用对应的列===========================*/
/**
 *应用组流量列
 */
function getAppFlowColumns() {
	var flowColumns = [[
		{
			field:'app_name',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
			field:'in_flows', 
			title:'流入流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
		{
			field:'out_flows', 
			title:'流出流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
		{
			field:'total_flows', 
			title:'总流量', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, null);
			}

		},
        {
			field:'in_speed', 
			title:'流入速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
		},
        {
			field:'out_speed', 
			title:'流出速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
		},
        {
			field:'total_speed', 
			title:'总速率', 
			sortable:true, 
			width:20, 
			align:'center', 
			order: 'desc',
			formatter: function(value, row, index) {
				return flowFormatter(value, 'speed');
			}
        },
        {
        	field:'flowPctge', 
        	title:'占比', 
        	sortable:true, 
        	order: 'desc',
        	formatter: function(value, row, index) {
        		return pctgeFormatter(value);
        	}
        }
    ]];
	return flowColumns;
}

/**
 * 应用包对应的列
 * @returns {Array}
 */
function getAppPacketColumns() {
	var packetColumns = [[
		{
			field:'app_name',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
        {
	    	field:'in_packages',
	    	title:'流入包数',
	    	sortable:true,
	    	width:20,
	    	align:'center',
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
	    	field:'out_packages', 
	    	title:'流出包数', 
	    	sortable:true, 
	    	width:20, 
	    	align:'center', 
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
	    	field:'total_packages', 
	    	title:'总包数', 
	    	sortable:true, 
	    	width:20, 
	    	align:'center', 
	    	order: 'desc',
	    	formatter:function(value, row, index) {
	    		return packetFormatter(value, null);
	    	}
	    },
        {
       	 	field: 'packetInSpeed', 
       	 	title: '流入速率', 
       	 	sortable: true, 
       	 	width: 20, 
       	 	align: 'left',
       	 	order: 'desc',
	       	formatter: function(value, row, index) {
	       		return packetFormatter(value, 'speed');
	       	}
        },
        {
       	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
       	 formatter: function(value, row, index) {
       		 return packetFormatter(value, 'speed');
       	 }
        },
        {
       	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
       	 formatter:function(value,row,index) {
   		 	return pctgeFormatter(value);
       	 }
        }
]];
return packetColumns;
}

/**
 * 应用连接数对应的列
 * @returns {Array}
 */
function getAppConnectColumns() {
	var connectColumns = [[
		{
			field:'app_name',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
      	{
          	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, null);
          	 }
          },
          {
          	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
          	 formatter: function(value, row, index) {
          		 return connectFormatter(value, 'speed');
          	 }
          },
          {
          	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
          	 formatter:function(value,row,index) {
      		 	return pctgeFormatter(value);
          	 }
          }
      	]];
      	return connectColumns;
}

/**
 * 应用带宽使用率对应的列
 * @returns {Array}
 */
function getAppBwColumns() {
	var bwColumns = [[
 		{
			field:'app_name',
			title:'原地址',
			sortable:false, 
			formatter: function(value, row, index) {
				return value || '';
			}
		},
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
return bwColumns;
}

/**
 * 应用指标类型改变方法
 * @param datagrid
 * @param type
 */
function changeAppIndicator(datagrid, type) {
	if(type == 1) {
		datagrid.datagrid({
			columns: getAppFlowColumns()
		});
	} else if(type == 2) {
		datagrid.datagrid({
			columns: getAppPacketColumns()
		});
	} else if(type == 3) {
		datagrid.datagrid({
			columns: getAppConnectColumns()
		});
	} else if(type == 4) {
		datagrid.datagrid({
			columns: getAppBwColumns()
		});
	}
}
/*==============应用对应的列结束===========================*/


/*==============highcharts渲染公共方法=====================*/
/**
 * 根据查询的时间段，选择X轴需要跳跃显示的点的个数，避免X轴显示的坐标过多
 */
function getStep(ti, timeLine) {
	var step = 0;
	switch(ti) {
	case '6hour':
		step = 3;
		break;
	case '1day':
		step = 12;
		break;
	case '7day':
		step = 12;
		break;
	case '30day':
		step = 5;
		break;
	case 'custom':
		step = timeLine.length / 13;
		step = Math.floor(step);
		break;
	}
	return step;
}

/**
 * 子页面线图渲染
 * @param node
 * @param width
 * @param height
 * @param timeLine
 * @param flowData
 * @param unit
 * @param yAxisName
 * @param step
 * @param ti
 */
function genNetflowChart(node, width, height, timeLine, flowData, unit, yAxisName, step, ti, st, et) {
	node.empty();
	node.highcharts({
		chart: {
        	backgroundColor: '#111718',
            type: 'line',
//            width: width,
//            height: height,
            ignoreHiddenSeries : false
        },
        title: {
        	text: ''
        },
        xAxis: {
        	type: 'datetime',
            categories: timeLine,
            lineColor : '#000',
            gridLineWidth: 0,
            labels : {
            	rotation: 20,
				style : {
					color : '#fff'
				},
				maxStaggerLines: 1,
				step: step,
				formatter: function() {
					if('7day' == ti || '30day' == ti) {
						return moment(this.value).format('DD-M月');
					} else if('1hour' == ti || '6hour' == ti || '1day' == ti) {
						return moment(this.value).format('HH:mm');
					} else {
						var interval = new Date(et).getTime() - new Date(st).getTime();
						var minute = 1000*60;
						var day = 24*60*minute;
						var month = day * 30;
						var year = day * 365;
						if(interval >= year) {
							return moment(this.value).format('YY/DD');
						} else if(interval > month) {
							return moment(this.value).format('YYYY/MM/DD');
						} else if(interval > day) {
							return moment(this.value).format('YYYY/MM/DD HH:mm');
						} else {
							return moment(this.value).format('HH:mm');
						}
					}
				}
			},
			lineWidth : 1,
			tickWidth : 1,
			tickColor : '#000',
			tickmarkPlacement : 'on'
        },
        yAxis: {
            title: {
                text: yAxisName
            },
            gridLineWidth: 0,
            plotLines: [{
				value: 0,
				width: 0,
				color: '#000000'
			}],
			min: 0,
			lineColor : '#000',
			lineWidth : 1,
			tickWidth : 1,
			tickColor : '#000',
        	labels: {
                formatter: function() {
           			var kb = 1024;
        			var mb = kb*kb;
        			var gb = mb*kb;

                	if(unit == 'MB') {
			        	 if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GB';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MB';
			        		 } else if(this.value >= kb) {
			        			 return (this.value/1024).toFixed(2) + "KB";
			        		 } else {
			        			 return this.value.toFixed(2) + "B";
			        		 }
			        	 } else {
			        		 return '0B';
			        	 }
                	} else if(unit == 'Mbps') {
	   		        	 if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'Gbps';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'Mbps';
			        		 } else if(this.value >= kb) {
			        			 return (this.value/kb).toFixed(2) + 'Kbps';
			        		 } else {
			        			 return this.value.toFixed(2) + 'bps';
			        		 }
			        	 } else {
			        		 return '0bps';
			        	 }
                	} else if(unit == '%') {
                		if(this.value != null) {
                			return this.value.toFixed(2) + '%';
                		} else {
                			return '0.00%';
                		}
                	} else if(unit == 'MPbps') { //包
                		if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GPbps';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MPbps';
			        		 } else if(this.value >= kb) {
			        			 var ret = this.value / kb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'KPbps';
			        		 } else {
			        			 return this.value.toFixed(2) + 'Pbps';
			        		 }
                		} else {
                			return this.value + '0Pbps';
                		}
                	} else if(unit == 'MP') { //包速率
                		if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GP';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MP';
			        		 } else if(this.value >= kb) {
			        			 var ret = this.value / kb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'KP';
			        		 } else {
			        			 return this.value.toFixed(2) + 'P';
			        		 }
                		} else {
                			return '0P';
                		}
                	} else if(unit == 'MF') { //连接数
                		if(null != this.value) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GF';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MF';
			        		 } else if(this.value >= kb) {
			        			 return (this.value/kb).toFixed(2) + 'KF';
			        		 } else {
			        			 return this.value.toFixed(2) + 'F';
			        		 }
                		} else {
                			return '0F';
                		}
                	} else if(unit == 'MFbps') { //连接数速率
                		if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GFbps';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MFbps';
			        		 } else if(this.value >= kb) {
			        			 return (this.value / kb).toFixed(2) + 'KFbps';
			        		 } else {
			        			 return this.value.toFixed(2) + 'Fbps';
			        		 }
                		} else {
                			return '0Fbps';
                		}
                	}
                }
            }
        },
        tooltip: {
            enabled: true,
            formatter: function() {
       			var kb = 1024;
    			var mb = kb*kb;
    			var gb = mb*kb;

            	if(unit == 'MB') {
	            	if(this.y != null) {
		        		 if(this.y >= gb) {
		        			 var ret = this.y / gb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'GB') + '</b>';
		        		 } else if(this.y >= mb) {
		        			 var ret = this.y / mb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'MB') + '</b>';
		        		 } else if(this.y >= 1024) {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y/1024).toFixed(2) + "KB") + '</b>';
		        		 } else {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y).toFixed(2) + "B") + '</b>';
		        		 }
		        	 } else {
		        		 return '<b>' + this.series.name + '<br>' + "0B" + '</b>';
		        	 }
            	} else if(unit == 'Mbps') {
	            	if(this.y != null) {
		        		 if(this.y >= gb) {
		        			 var ret = this.y / gb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'Gbps') + '</b>';
		        		 } else if(this.y >= mb) {
		        			 var ret = this.y / mb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'MBbs') + '</b>';
		        		 } else if(this.y >= 1024) {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y/1024).toFixed(2) + "Kbps") + '</b>';
		        		 } else {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y).toFixed(2) + "bps") + '</b>';
		        		 }
		        	 } else {
		        		 return '<b>' + this.series.name + '<br>' + "0bps" + '</b>';
		        	 }
            	} else if(unit == '%') {
            		return '<b>' + this.series.name + '<br>' + (this.y*100).toFixed(2) + '%';
            	} else if(unit == 'MP') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GP") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y / mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MP") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KP") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + ((this.y).toFixed(2) + "P") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + "0P" + '</b>';
            		}

            	} else if(unit == 'MPbps') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GPbps") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y/mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MPbps") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KPbps") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + (this.y.toFixed(2) + "Pbps") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + "0Pbps" + '</b>';
            		}
            	} else if(unit == 'MFbps') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GFbps") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y/mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MFbps") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KFbps") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + (this.y.toFixed(2) + "Fbps") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + (ret + "Fbps") + '</b>';
            		}
            	} else if(unit == 'MF') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GF") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y/mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MF") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KF") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + (this.y.toFixed(2) + "F") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + (ret + "F") + '</b>';
            		}
            	}
            }
        },
        plotOptions: {
            line: {
            	allowPointSelect: true,
                dataLabels: {
                    enabled: false
                },
                enableMouseTracking: true
            },
            series: {
				marker: {
					enabled: false
				}
			}
        },
        legend: {
        	itemStyle: {
        		"color": "white"
        	},
        	borderColor: '#172D17',
        	symbolRadius: 50,
        	x: -20,
        	y: -30,
        	align: 'right',
        	layout: 'vertical'
        },
        credits: {
        	enabled: false
        },
        exporting: {
       		enabled: false
        },
        series: flowData
    });

}

/**
 * 讲数据转换为饼图需要的数据格式
 * @param src
 * @returns {Array}
 */
function dataTransfer(src) {
	var dst = [];
	if(src) {
		for(var i=0; i<src.length; i++) {
			
			var dataList = src[i].data;
			var ele = [];

			var data = 0;
			var name = src[i].name;
			if(dataList && dataList.length > 0) {
				for(var j=0; j<dataList.length; j++) {
					data += dataList[j];
				}
			}
			ele.push(name);
			ele.push(data);
			
			dst.push(ele);
		}
	}
	return dst;
}

/**
 * 首页饼图
 * @param node
 * @param flowData
 */
function genMainPagePieChart(node, flowData) {
	node.empty();
	node.highcharts({
        chart: {
        	backgroundColor: '#001C00',
        	borderRadius : 3,
			borderWidth : 1,
			borderColor: '#006100',
        	type: 'pie'
        },
        title: {
        	text: ''
        },
        tooltip: {
        	pointFormat: '{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                innerSize: 30,
                showInLegend: true,
                dataLabels: {
                	formatter: function() {
                		return Highcharts.numberFormat(this.percentage, 2) +'%';
                	}
                }
            }
        },
        legend: {
        	itemStyle: {
        		"color": "white"
        	},
        	x: -20,
        	y: -30,
        	borderColor: '#172D17',
        	align: 'right',
        	layout: 'vertical'
        },
        credits: {
        	enabled: false
        },
        exporting: {
       		enabled: false
        },
        series: [{
        	type: 'pie',
        	data: flowData
        }]
    });
}

/**
 * 首页线图
 * @param node
 * @param timeLine
 * @param flowData
 * @param unit
 * @param yAxisName
 * @param step
 * @param ti
 */
function genMainPageChart(node, timeLine, flowData, unit, yAxisName, step, ti, st, et) {
	node.empty();
	node.highcharts({
        chart: {
        	backgroundColor: '#001C00',
            type: 'line',
//            width: 665,
            height: 245,
            ignoreHiddenSeries : false,
            borderRadius : 3,
			borderWidth : 1,
			borderColor: '#006100'
        },
        title: {
        	text: ''
        },
        xAxis: {
        	type: 'datetime',
            categories: timeLine,
            lineColor : '#000',
            gridLineWidth: 0,
            labels : {
            	rotation: 20,
				style : {
					color : '#fff'
				},
				maxStaggerLines: 1,
				step: step,
				formatter: function() {
					if('7day' == ti || '30day' == ti) {
						return moment(this.value).format('DD-M月');
					} else if('1hour' == ti || '6hour' == ti || '1day' == ti) {
						return moment(this.value).format('HH:mm');
					} else {
						var interval = new Date(et).getTime() - new Date(st).getTime();
						var minute = 1000*60;
						var day = 24*60*minute;
						var month = day * 30;
						var year = day * 365;
						if(interval >= year) {
							return moment(this.value).format('YYYY/DD');
						} else if(interval > month) {
							return moment(this.value).format('YYYY/MM/DD');
						} else if(interval > day) {
							return moment(this.value).format('YYYY/MM/DD HH:mm');
						} else {
							return moment(this.value).format('HH:mm');
						}
					}
				}
			},
			lineWidth : 1,
			tickWidth : 1,
			tickColor : '#000',
			tickmarkPlacement : 'on'
        },
        yAxis: {
            title: {
                text: yAxisName
            },
            gridLineWidth: 0,
            plotLines: [{
				value: 0,
				width: 0,
				color: '#000000'
			}],
			min: 0,
			lineColor : '#000',
			lineWidth : 1,
			tickWidth : 1,
			tickColor : '#000',
        	labels: {
        		formatter: function() {
           			var kb = 1024;
        			var mb = kb*kb;
        			var gb = mb*kb;

                	if(unit == 'MB') {
			        	 if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GB';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MB';
			        		 } else if(this.value >= kb) {
			        			 return (this.value/1024).toFixed(2) + "KB";
			        		 } else {
			        			 return this.value.toFixed(2) + "B";
			        		 }
			        	 } else {
			        		 return '0B';
			        	 }
                	} else if(unit == 'Mbps') {
	   		        	 if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'Gbps';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'Mbps';
			        		 } else if(this.value >= kb) {
			        			 return (this.value/kb).toFixed(2) + 'Kbps';
			        		 } else {
			        			 return this.value.toFixed(2) + 'bps';
			        		 }
			        	 } else {
			        		 return '0bps';
			        	 }
                	} else if(unit == '%') {
                		if(this.value != null) {
                			return this.value.toFixed(2) + '%';
                		} else {
                			return '0.00%';
                		}
                	} else if(unit == 'MPbps') { //包
                		if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GPbps';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MPbps';
			        		 } else if(this.value >= kb) {
			        			 var ret = this.value / kb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'KPbps';
			        		 } else {
			        			 return this.value.toFixed(2) + 'Pbps';
			        		 }
                		} else {
                			return this.value + '0Pbps';
                		}
                	} else if(unit == 'MP') { //包速率
                		if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GP';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MP';
			        		 } else if(this.value >= kb) {
			        			 var ret = this.value / kb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'KP';
			        		 } else {
			        			 return this.value.toFixed(2) + 'P';
			        		 }
                		} else {
                			return '0P';
                		}
                	} else if(unit == 'MF') { //连接数
                		if(null != this.value) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GF';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MF';
			        		 } else if(this.value >= kb) {
			        			 return (this.value/kb).toFixed(2) + 'KF';
			        		 } else {
			        			 return this.value.toFixed(2) + 'F';
			        		 }
                		} else {
                			return '0F';
                		}
                	} else if(unit == 'MFbps') { //连接数速率
                		if(this.value != null) {
			        		 if(this.value >= gb) {
			        			 var ret = this.value / gb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'GFbps';
			        		 } else if(this.value >= mb) {
			        			 var ret = this.value / mb;
			        			 ret = ret.toFixed(2);
			        			 return ret + 'MFbps';
			        		 } else if(this.value >= kb) {
			        			 return (this.value / kb).toFixed(2) + 'KFbps';
			        		 } else {
			        			 return this.value.toFixed(2) + 'Fbps';
			        		 }
                		} else {
                			return '0Fbps';
                		}
                	}
                }
            }
        },
        tooltip: {
            enabled: true,
            formatter: function() {
       			var kb = 1024;
    			var mb = kb*kb;
    			var gb = mb*kb;

            	if(unit == 'MB') {
	            	if(this.y != null) {
		        		 if(this.y >= gb) {
		        			 var ret = this.y / gb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'GB') + '</b>';
		        		 } else if(this.y >= mb) {
		        			 var ret = this.y / mb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'MB') + '</b>';
		        		 } else if(this.y >= 1024) {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y/1024).toFixed(2) + "KB") + '</b>';
		        		 } else {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y).toFixed(2) + "B") + '</b>';
		        		 }
		        	 } else {
		        		 return '<b>' + this.series.name + '<br>' + "0B" + '</b>';
		        	 }
            	} else if(unit == 'Mbps') {
	            	if(this.y != null) {
		        		 if(this.y >= gb) {
		        			 var ret = this.y / gb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'Gbps') + '</b>';
		        		 } else if(this.y >= mb) {
		        			 var ret = this.y / mb;
		        			 ret = ret.toFixed(2);
		        			 return '<b>' + this.series.name + '<br>' + (ret + 'MBbs') + '</b>';
		        		 } else if(this.y >= 1024) {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y/1024).toFixed(2) + "Kbps") + '</b>';
		        		 } else {
		        			 return '<b>' + this.series.name + '<br>' + ((this.y).toFixed(2) + "bps") + '</b>';
		        		 }
		        	 } else {
		        		 return '<b>' + this.series.name + '<br>' + "0bps" + '</b>';
		        	 }
            	} else if(unit == '%') {
            		return '<b>' + this.series.name + '<br>' + (this.y*100).toFixed(2) + '%';
            	} else if(unit == 'MP') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GP") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y / mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MP") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KP") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + ((this.y).toFixed(2) + "P") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + "0P" + '</b>';
            		}

            	} else if(unit == 'MPbps') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GPbps") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y/mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MPbps") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KPbps") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + (this.y.toFixed(2) + "Pbps") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + "0Pbps" + '</b>';
            		}
            	} else if(unit == 'MFbps') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GFbps") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y/mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MFbps") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KFbps") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + (this.y.toFixed(2) + "Fbps") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + (ret + "Fbps") + '</b>';
            		}
            	} else if(unit == 'MF') {
            		if(this.y != null) {
            			if(this.y >= gb) {
            				var ret = this.y/gb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "GF") + '</b>';
            			} else if(this.y >= mb) {
            				var ret = this.y/mb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "MF") + '</b>';
            			} else if(this.y >= kb) {
            				var ret = this.y/kb;
            				ret = ret.toFixed(2);
            				return '<b>' + this.series.name + '<br>' + (ret + "KF") + '</b>';
            			} else {
            				return '<b>' + this.series.name + '<br>' + (this.y.toFixed(2) + "F") + '</b>';
            			}
            		} else {
            			return '<b>' + this.series.name + '<br>' + (ret + "F") + '</b>';
            		}
            	}
            }        
        },
        plotOptions: {
            line: {
            	allowPointSelect: true,
                dataLabels: {
                    enabled: false
                },
                enableMouseTracking: true
            },
            series: {
				marker: {
					enabled: false
				}
			}
        },
        legend: {
        	itemStyle: {
        		"color": "white"
        	},
        	borderColor: '#172D17',
        	symbolRadius: 50,
        	x: -20,
        	y: -50,
        	itemMarginTop: 10,
        	align: 'right',
        	layout: 'vertical'
        },
        credits: {
        	enabled: false
        },
        exporting: {
       		enabled: false
        },
        series: flowData
    });
}

/**
 * 是否是IP
 * @param what
 * @returns {Boolean}
 */
function isIp(what) {
	var ipReg = /^([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])$/;
    var ret = ipReg.test(what);
    if(ret) { // ip
    	return true;
    }
    return false;
}

/**
 * 验证起止时间
 * @param stime
 * @param etime
 * @returns {String}
 */
function checkCustomTime(stime, etime) {
	if(!stime || !etime || stime == null || etime == null || stime == '' || etime == '') {
		return '请选择自定义起止时间';
	}
	var stimeObj = new moment(stime);
	var etimeObj = new moment(etime);
	var now = new moment();
	if(etimeObj.isBefore(stimeObj)) {
		return '结束时间不能小于开始时间';
	}
	if(now.isBefore(etimeObj)) {
		return '结束时间不能大于当前时间';
	}
	return '';
}

/**
 * ...
 * @param sortcolum
 * @returns {___anonymous122985_123018}
 */
function parseForJR(sortcolum) {
	var unit = '', yAxisName = '';
		switch(sortcolum){
		case 'in_flows': unit = 'MB'; yAxisName = '流入流量'; break;
		case 'out_flows': unit = 'MB'; yAxisName = '流出流量'; break;
		case 'total_flows': unit = 'MB'; yAxisName = '总流量'; break;
		case 'in_speed': unit = 'Mbps'; yAxisName = '流入速率'; break;
		case 'out_speed': unit = 'Mbps'; yAxisName = '流出速率'; break;
		case 'total_speed': unit = 'Mbps'; yAxisName = '总速率'; break;
		
		case 'in_packages': unit = 'MP'; yAxisName = '流入包数'; break;
		case 'out_packages': unit = 'MP'; yAxisName = '流出包数'; break;
		case 'total_packages': unit = 'MP'; yAxisName = '总包数'; break;
		case 'packetInSpeed': unit = 'MPbps'; yAxisName = '流入速率'; break;
		case 'packetOutSpeed': unit = 'MPbps'; yAxisName = '流出速率'; break;
		case 'packetTotalSpeed': unit = 'MPbps'; yAxisName = '总速率'; break;
		
		case 'connectNumberIn': unit = 'MF'; yAxisName = '流入连接数'; break;
		case 'connectNumberOut': unit = 'MF'; yAxisName = '流出连接数'; break;
		case 'connectNumberTotal': unit = 'MF'; yAxisName = '总连接数'; break;
		case 'connectNumberInSpeed': unit = 'MFbps'; yAxisName = '流入速率'; break;
		case 'connectNumberOutSpeed': unit = 'MFbps'; yAxisName = '流出速率'; break;
		case 'connectNumberTotalSpeed': unit = 'MFbps'; yAxisName = '总速率'; break;
		
		case 'flowPctge':
		case 'flows_rate':
			unit = '%'; yAxisName = '流量占比'; break;
		case 'packetPctge':
		case 'packets_rate':
			unit = '%'; yAxisName = '包占比'; break;
		case 'connectPctge':
		case 'connects_rate':
			unit = '%'; yAxisName = '连接数占比'; break;
		}
		return {unit: unit, yAxisName: yAxisName}
}

/**
 * ...
 * @param rows
 * @param sort
 * @param which
 * @returns {Array}
 */
function parseForJRPie(rows, sort, which) {
	 var series = [];
	 for(var i = 0; i < rows.length; i++){
		 var element = [];
		 if(which == 'proto') {
			 element.push(rows[i].proto_name);
		 } else if(which == 'terminal') {
			 element.push(rows[i].terminal_name);
		 } else if(which == 'app') {
			 element.push(rows[i].app_name);
		 }
		 switch(sort){
		 case "in_flows":  element.push(parseFloat(rows[i].in_flows));break;
		 case "out_flows":  element.push(parseFloat(rows[i].out_flows));break;
		 case "total_flows":  element.push(parseFloat(rows[i].total_flows));break;
		 case "in_speed":  element.push(parseFloat(rows[i].in_speed));break;
		 case "out_speed":  element.push(parseFloat(rows[i].out_speed));break;
		 case "total_speed":  element.push(parseFloat(rows[i].total_speed));break;
		 
		 case "in_packages":  element.push(parseFloat(rows[i].in_packages));break;
		 case "out_packages":  element.push(parseFloat(rows[i].out_packages));break;
		 case "total_package":  element.push(parseFloat(rows[i].total_package));break;
		 case "packetInSpeed": element.push(parseFloat(rows[i].packetInSpeed));break;
		 case "packetOutSpeed": element.push(parseFloat(rows[i].packetOutSpeed));break;
		 case "packetTotalSpeed": element.push(parseFloat(rows[i].packetTotalSpeed));break;
		 
		 case "flows_rate": element.push(parseFloat(rows[i].flows_rate));break;
		 case "flowPctge":  element.push(parseFloat(rows[i].flowPctge));break;
		 case "packets_rate": element.push(parseFloat(rows[i].packets_rate));break;
		 case "packetPctge": element.push(parseFloat(rows[i].packetPctge));break;
		 case "connects_rate": element.push(parseFloat(rows[i].connects_rate));break;
		 case "connectPctge": element.push(parseFloat(rows[i].connectPctge));break;
		 }
		 series.push(element);
	 }
	 return series;
}

function parseForJROfAppTerminalSession(sortcolum) {
	var unit = '';
	switch(sortcolum){
	case '流入流量': 
	case '流出流量': 
	case '总流量' : unit = 'MB'; break;
	case '流入包数':
	case '流出包数':
	case '总包数':unit = 'MP';break;
	case '流入速率' :
	case '流出速率':
	case '总速率':unit = 'Mbps';break;
	case '占比':unit = '%';break;
	case '流入包数速率':
	case '流出包数速率':
	case '总包数速率':
		unit = 'MPbps'; break;
	case '流入连接数':
	case '流出连接数':
	case '总连接数':
		unit = 'MF'; break;
	case '流入连接数速率':
	case '流出连接数速率':
	case '总连接数速率':
		unit = 'MFbps'; break;
	}
	return unit;
}
/*==============highcharts渲染公共方法结束==================*/
