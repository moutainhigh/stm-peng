function FindSettingDia(args){
	this.args=args;
	var ctx = this;
	//是否有权限
	this.auth=true;
	//请求权限
	$.get(oc.resource.getUrl("topo/auth/hasAuth.htm"),function(info){
		ctx.auth=info.auth;
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/FindSettingDia.html"),
			success:function(html){
				ctx.init(html);
			},
			type:"get",
			dataType:"html"
		});
	},"json");
	
};
FindSettingDia.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.dialog({
			draggable:true,
			title:"拓扑发现",
			width:920,height:600
		});
		//搜索全局域
		this.searchField("field");
		//搜索面板
		this.searchField("panel");
		ctx.initUi();
		ctx.regEvent();
	},
	//搜索域
	searchField:function(type){
		var ctx = this;
		this[type]={};
		this.root.find("[data-"+type+"]").each(function(idx,dom){
			var tmp = $(dom);
			ctx[type][tmp.attr("data-"+type)]=tmp;
		});
	},
	_initwholeNet:function(){
		var ctx = this;
		if(!this.wholeNetFlag){
			//搜索全网域
			this.searchField("wholeNet");
			var wn = this.wholeNet;
			wn.addBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			wn.delBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			wn.startBtn.linkbutton("RenderLB",{
				disabled:!this.auth
			});
			wn.ipIpt.validatebox({
				validType:"ip"
			});
			this._regwholeNetEvent();
			//读取数据库配置
			this.getCfg("wholeNet", function(cfg){
				ctx._setWholeNetValue(cfg);
			});
		}
		this.wholeNetFlag=true;
	},
	_regwholeNetEvent:function(){
		var ctx = this;
		var wn = this.wholeNet;
		var template = wn.ipContainer.find(".template");
		template.span=template.find("span");
		//添加元素
		wn.addElement=function(ip){
			template.span.text(ip);
			var tmp = template.clone();
			tmp.removeClass("hide template");
			tmp.attr("ip",ip);
			wn.ipContainer.append(tmp);
		};
		//添加按钮
		wn.addBtn.on("click",function(){
			var val = wn.ipIpt.val();
			if(val && wn.ipIpt.validatebox("isValid")){
				wn.addElement(val);
				ctx.saveCfg("wholeNet",ctx._getWholeNetValue());
			}
		});
		//是否保留自定义拓扑
		/*wn.reserve.on("change",function(){
			var checked = $(this).prop("checked");
			if(!checked){
				$.messager.confirm("警告","不保留自定义拓扑会<span style='color:red;'>删除现存的所有子拓扑</span>，确定继续？",function(r){
					if(r){
						ctx.saveCfg("wholeNet",ctx._getWholeNetValue());
					}else{
						wn.reserve.attr("checked","checked");
					}
				});
			}else{
				ctx.saveCfg("wholeNet",ctx._getWholeNetValue());
			}
		});*/
		//删除按钮
		wn.delBtn.on("click",function(){
			$.messager.confirm("警告","确定删除？",function(r){
				if(r){
					wn.ipContainer.find("input:checked").parent().remove();
					ctx.saveCfg("wholeNet",ctx._getWholeNetValue());
				}
			});
		});
		//开始发现按钮
		wn.startBtn.on("click",function(){
			$.messager.confirm("警告","做全网发现会删除已有的图元、链路、地图拓扑链路不删除，是否继续？",function(r){
				if(r){
					ctx.startFind("wholeNet");
				}
			});
		});
	},
	_getWholeNetValue:function(){
		var wn = this.wholeNet;
		var retn = {ips:[]};
		wn.ipContainer.find("label[ip]").each(function(idx,label){
			retn.ips.push($(label).attr("ip"));
		});
//		retn.reserve=!!wn.reserve.prop("checked");
		return retn;
	},
	_setWholeNetValue:function(val){
		if(val && val.ips){
			var wn = this.wholeNet;
			for(var i = 0;i<val.ips.length;++i){
				wn.addElement(val.ips[i]);
			}
//			wn.reserve.prop("checked",val.reserve);
		}
	},
    /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 start*/
    _formatter: function (v, r, i) {
        if (v != null && v.length > 0) {
            return v.replace(/(\s|\S)/g, "*");
        }
        return v;
    },
    /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 end*/
	_initcommonBody:function(){
		var ctx = this;
		if(!this.commonBodyFlag){
			//配置共同体
			this.searchField("commonBody");
			this.commonBody.addBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.commonBody.delBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
            this.commonBody.remarks.validatebox({
                required: true,
            });
			//共同体表格
			this.commonBody.grid.datagrid({
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				},
				pagination:false,
				rows:[],
				columns:[[{
					field:"id",checkbox:true
				},{
                    field: "remarks", title: "备注", width: "25%"
                }, {
                    /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 start*/
                    /*field:"readOnly",title:"只读共同体",width:"35%"*/
                    field: "readOnly", title: "只读共同体", width: "35%", formatter: this._formatter
                    /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 end*/
				},{
                    /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 start*/
                    /*field:"readWrite",title:"读写共同体",width:"35%"*/
                    field: "readWrite", title: "读写共同体", width: "35%", formatter: this._formatter
                    /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 end*/
				}]]
			});
			//重试次数
			this.commonBody.retryTimes.numberbox({
				min:1
			});
			//超时时间
			this.commonBody.timeout.numberbox({
				min:1000
			});
			//端口
			this.commonBody.port.validatebox({
				validType:"port",
				onBlur:function(){
					ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
				},
				value:161
			});
			//snmp版本
			this.commonBody.snmpversion.combobox({
				editable:false,
				value:"1",
				data:[{
					value:0,text:"v1"
				},{
					value:1,text:"v2"
				},{
					value:3,text:"v3"
				}],
				textField:"text",
				valueField:"value"
			});
			this.commonBody.snmpversion.combobox("setValue",1);	//默认v2
			this._regcommonBodyEvent();
			this.getCfg("commonBody", function(cfg){
				ctx._setcommonBodyValue(cfg);
			});
		}
		this.commonBodyFlag=true;
	},
	_regcommonBodyEvent:function(){
		var ctx = this;
		var cb = this.commonBody;
		//添加按钮
		cb.addBtn.on("click",function(){
            /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 start*/
            /*var rv = cb.rIpt.val(),rwv=cb.rwIpt.val();
            if(rv || rwv){
                ctx.addDataToGrid(cb.grid,{
                    readOnly:rv,
                    readWrite:rwv
                });
                ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
            }*/
            var rv = cb.rIpt.val(), rwv = cb.rwIpt.val(), remarks = cb.remarks.val();
            if ((rv || rwv) && remarks) {
				ctx.addDataToGrid(cb.grid,{
                    remarks: remarks,
					readOnly:rv,
					readWrite:rwv
				});
				ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
            } else {
                if (!rv && !rwv) {
                    cb.rIpt.focus();
                    alert("请填写共同体");
                } else {
                    cb.remarks.focus();
                }
            }
            /*BUG #48969 拓扑管理模块的团体字，都采用密码的输入方式，不显示明文 huangping 2017/12/6 end*/
		});
		//删除按钮q
		cb.delBtn.on("click",function(){
			$.messager.confirm("警告","确定删除？",function(r){
				if(r){
					ctx.deleteSelectedFromGrid(cb.grid);
					ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
				}
			});
		});
		//端口
		cb.port.on("blur",function(){
			ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
		});
		//重试次数
		cb.retryTimes.numberbox({
			onChange:function(oldVal,newVal){
				if(oldVal && newVal){//需判断，否则页面初始化就会执行该事件，导致数据问题
					ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
				}
			}
		});
		//超时
		cb.timeout.numberbox({
			onChange:function(oldVal,newVal){
				if(oldVal && newVal){//需判断，否则页面初始化就会执行该事件，导致数据问题
					ctx.saveCfg("commonBody", ctx._getcommonBodyValue());//TODO:2次
				}
			}
		});
		//snmp版本号
		cb.snmpversion.combobox({
			onSelect:function(){
				ctx.saveCfg("commonBody", ctx._getcommonBodyValue());
			}
		});
	},
	_getcommonBodyValue:function(){
		var cb = this.commonBody;
		var retn = {
			rows:cb.grid.datagrid("getRows"),
			retryTimes:parseInt(cb.retryTimes.numberbox("getValue")),
			snmpversion:cb.snmpversion.combobox("getValue"),
			port:parseInt(cb.port.val()),
			timeout:parseInt(cb.timeout.numberbox("getValue"))
		};
		return retn;
	},
	_setcommonBodyValue:function(v){
		var cb = this.commonBody;
		if(v){
			cb.grid.datagrid("loadData",v.rows);
			cb.retryTimes.numberbox("setValue",v.retryTimes||1);
			cb.snmpversion.combobox("setValue",v.snmpversion||1);
			cb.port.val(v.port||161);
			cb.timeout.numberbox("setValue",(v.timeout||1600));
		}
	},
	_initextend:function(){
		var ctx = this;
		if(!this.extendFlag){
			//扩展发现
			this.searchField("extend");
			this.extend.addBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.extend.delBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			this.extend.startBtn.linkbutton("RenderLB",{
				disabled:!this.auth
			});
			this.extend.ipIpt.validatebox({
				validType:"ip"
			});
			this._regextendEvent();
			//读取数据库配置
			this.getCfg("extend", function(cfg){
				ctx._setextendValue(cfg);
			});
		}
		this.extendFlag=true;
	},
	_regextendEvent:function(){
		var ctx = this;
		var ex = this.extend;
		var template = ex.ipContainer.find(".template");
		template.span=template.find("span");
		//添加元素
		ex.addElement=function(ip){
			template.span.text(ip);
			var tmp = template.clone();
			tmp.removeClass("hide template");
			tmp.attr("ip",ip);
			ex.ipContainer.append(tmp);
		};
		//添加按钮
		ex.addBtn.on("click",function(){
			var val = ex.ipIpt.val();
			if(val && ex.ipIpt.validatebox("isValid")){
				ex.addElement(val);
				ctx.saveCfg("extend",ctx._getextendValue());
			}
		});
		//删除按钮
		ex.delBtn.on("click",function(){
			$.messager.confirm("警告","确定删除？",function(r){
				if(r){
					ex.ipContainer.find("input:checked").parent().remove();
					ctx.saveCfg("extend",ctx._getextendValue());
				}
			});
		});
		//开始发现按钮
		ex.startBtn.on("click",function(){
			ctx.startFind("extend");
		});
	},
	_getextendValue:function(){
		var ex = this.extend;
		var retn = [];
		ex.ipContainer.find("label[ip]").each(function(idx,label){
			retn.push($(label).attr("ip"));
		});
		return retn;
	},
	_setextendValue:function(val){
		if(val){
			var ex = this.extend;
			for(var i = 0;i<val.length;++i){
				ex.addElement(val[i]);
			}
		}
	},
	_initsubnet:function(){
		var ctx = this;
		if(!this.subnetFlag){
			//子网发现
			this.searchField("subnet");
			this.subnet.addBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.subnet.delBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			this.subnet.startBtn.linkbutton("RenderLB",{
				disabled:!this.auth
			});
			this.subnet.grid.datagrid({
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				},
				pagination:false,
				height:100,
				columns:[[{
					field:"id",checkbox:true
				},{
					field:"ip",title:"子网地址",width:"50%"
				},{
					field:"mask",title:"子网掩码",width:"50%"
				}]]
			});
			this.subnet.ipIpt.validatebox({
				validType:"ip"
			});
			this.subnet.maskIpt.validatebox({
				validType:"ipmask"
			});
			this._regsubnetEvent();
			this.getCfg("subnet", function(v){
				ctx._setsubnetValue(v);
			});
		}
		this.subnetFlag=true;
	},
	_regsubnetEvent:function(){
		var ctx = this;
		var sn = this.subnet;
		//添加按钮
		sn.addBtn.on("click",function(){
			var ip = sn.ipIpt.val(),mask=sn.maskIpt.val();
			if(sn.ipIpt.validatebox("isValid")&&ip){
				if(sn.maskIpt.validatebox("isValid")&&mask){
					ctx.addDataToGrid(sn.grid, {
						ip:ip,
						mask:mask
					});
					ctx.saveCfg("subnet",ctx._getsubnetValue());
				}else{
					sn.maskIpt.focus();
				}
			}else{
				sn.ipIpt.focus();
			}
		});
		//删除按钮
		sn.delBtn.on("click",function(){
			var selections = sn.grid.datagrid("getSelections");
			if(selections && selections.length > 0){
				$.messager.confirm("警告","确定删除？",function(r){
					if(r){
						ctx.deleteSelectedFromGrid(sn.grid);
						ctx.saveCfg("subnet",ctx._getsubnetValue());
					}
				});
			}else{
				$.messager.alert('警告','至少选择一条要删除的信息','warning');
			}
			
		});
		//重新发现子网
		sn.research.on("change",function(){
			var checked = $(this).prop("checked");
			if(checked){
				$.messager.confirm("警告","重新发现子网会删除以前发现的信息，确定继续？",function(r){
					if(r){
						ctx.saveCfg("subnet",ctx._getsubnetValue());
					}else{
						sn.research.removeAttr("checked");
					}
				});
			}else{
				ctx.saveCfg("subnet",ctx._getsubnetValue());
			}
		});
		//开始发现按钮
		sn.startBtn.on("click",function(){
			ctx.startFind("subnet");
		});
	},
	_getsubnetValue:function(){
		var sn = this.subnet;
		var retn = {};
		retn.subnets=sn.grid.datagrid("getRows");
		retn.research = sn.research.prop("checked");
		return retn;
	},
	_setsubnetValue:function(vs){
		var sn = this.subnet;
		if(vs){
			sn.grid.datagrid("loadData",vs.subnets);
			sn.research.prop("checked",vs.research);
		}
	},
	_initsegment:function(){
		var ctx = this;
		if(!this.segmentFlag){
			this.searchField("segment");
			this.segment.startBtn.linkbutton("RenderLB",{
				disabled:!this.auth
			});
			this.segment.addBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.segment.delBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			this.segment.beginIpt.validatebox({
				validType:"ip"
			});
			this.segment.endIpt.validatebox({
				validType:"ip"
			});
			this.segment.grid.datagrid({
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				},
				data:[],
				pagination:false,
				columns:[[{
					field:"id",checkbox:true
				},{
					field:"startIp",title:"开始IP地址",width:"45%"
				},{
					field:"endIp",title:"结束IP地址",width:"50%"
				}]]
			});
			this._regsegmentEvent();
			this.getCfg("segment", function(v){
				ctx._setsegmentValue(v);
			});
		}
		this.segmentFlag=true;
	},
	_regsegmentEvent:function(){
		var ctx = this;
		var sm = this.segment;
		function getSegVal(){
            /*BUG #41042 升级版本---拓扑管理---拓扑发现的网段发现界面 网段参数不填能够添加 huangping 2017/6/30 start*/
            /*if(sm.beginIpt.validatebox("isValid")){
				if(sm.endIpt.validatebox("isValid")){
					return {
						startIp:sm.beginIpt.val(),
						endIp:sm.endIpt.val()
					};
				}else{
					sm.endIpt.focus();
				}
			}else{
				sm.beginIpt.focus();
			}
             return null;*/

            var bip = sm.beginIpt.val(), eip = sm.endIpt.val();
            if (sm.beginIpt.validatebox("isValid") && bip) {
                if (sm.endIpt.validatebox("isValid") && eip) {
                    return {
                        startIp: bip,
                        endIp: eip
                    };
                } else {
                    sm.endIpt.focus();
                }
            } else {
                sm.beginIpt.focus();
            }
            /*BUG #41042 升级版本---拓扑管理---拓扑发现的网段发现界面 网段参数不填能够添加 huangping 2017/6/30 end*/
		}
		//开始发现
		sm.startBtn.on("click",function(){
			ctx._savesegmentValue(function(){
				ctx.startFind("segment");
			});
		});
		//添加按钮
		sm.addBtn.on("click",function(){
			var row = getSegVal();
			if(row){
				sm.grid.datagrid("appendRow",row);
				ctx._savesegmentValue();
			}
		});
		//删除按钮
		sm.delBtn.on("click",function(){
			var rows = sm.grid.datagrid("getSelections");
			if(rows && rows.length>0){
				for(var i=0;i<rows.length;i++){
					var idx = sm.grid.datagrid("getRowIndex",rows[i]);
					sm.grid.datagrid("deleteRow",idx);
				}
				ctx._savesegmentValue();
			}else{
				alert("请在下边的表格中选中要删除的行");
			}
		});
	},
	_savesegmentValue:function(cb){
		var value=this._getsegmentValue();
		if(value){
			this.saveCfg("segment",value,cb);
		}
	},
	_getsegmentValue:function(){
		var sm = this.segment;
		return sm.grid.datagrid("getRows");
	},
	_setsegmentValue:function(v){
		var sm = this.segment;
		if(v && v instanceof Array){
			sm.grid.datagrid("loadData",v);
		}
	},
	_inittopoSetting:function(){
		var ctx = this;
		if(!this.topoSettingFlag){
			//拓扑设置
			this.searchField("topoSetting");
			this.topoSetting.includeGridAddBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.topoSetting.includeGridDelBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			this.topoSetting.excludeGridAddBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.topoSetting.excludeGridDelBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			this.topoSetting.snmpv3AddBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-plus"
			});
			this.topoSetting.snmpv3DelBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-trash-o"
			});
			this.topoSetting.includeGrid.datagrid({
				pagination:false,
				height:100,
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				},
				columns:[[{
					field:"id",checkbox:true
				},{
					field:"ip",title:"子网地址",width:"50%"
				},{
					field:"mask",title:"子网掩码",width:"50%"
				}]]
			});
			this.topoSetting.excludeGrid.datagrid({
				pagination:false,
				height:100,
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				},
				columns:[[{
					field:"id",checkbox:true
				},{
					field:"ip",title:"子网地址",width:"50%"
				},{
					field:"mask",title:"子网掩码",width:"50%"
				}]]
			});
			this.topoSetting.snmpv3Grid.datagrid({
				pagination:false,
				height:100,
				align:"center",
				halign:"center",
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				},
				columns:[[{field:"id",checkbox:true},
		          {field:'userName',title:'用户名',width:50},
		          {field:'safeLvLabel',title:'安全级别',width:100},
		          {field:'protocal',title:'认证协议',width:50},
		          {field:'passWord',title:'认证密码',width:100,formatter:function(v){
		        	  if(v && v!=""){
		        		  return "******";
		        	  }else{
		        		  return "";
		        	  }
		          }},
		          {field:'eagreement',title:'加密协议',width:60},
		          {field:'epassword',title:'加密密码',width:100,formatter:function(v){
		        	  if(v && v!=""){
		        		  return "******";
		        	  }else{
		        		  return "";
		        	  }
		          }},
		          {field:'contentName',title:'上下文名',width:100}
		          ]]
			});
			this.field.topoSetting.find("input[data-topoSetting]").validatebox({
				validType:"ip"
			});
			this.topoSetting.formDom=new FormUtil(this.topoSetting.form);
			this._regtopSettingEvent();
            /*BUG #40287 拓扑管理：非admin账号登录系统，拓扑发现配置页面，域和dcs显示为数字 huangping 2017/6/30 start*/
            /*this.getCfg("topoSetting", function(v){
             ctx._settopSettingValue(v);
             });*/
            /*BUG #40287 拓扑管理：非admin账号登录系统，拓扑发现配置页面，域和dcs显示为数字 huangping 2017/6/30 end*/
			var userinfo = oc.index.getUser();
			//域
			this.topoSetting.dmsIpt.combobox({
				textField:"name",
				valueField:"id",
				data:userinfo.domains,
				onSelect:function(record){
					if(record){
						ctx.topoSetting.dcsIpt.combobox("clear");
						ctx.topoSetting.dcsIpt.combobox("loadData",record.dcsNodes);
					}
				}
			});
			//dcs
			this.topoSetting.dcsIpt.combobox({
				textField:"name",
				valueField:"groupId",
				data:[]
			});
            /*BUG #40287 拓扑管理：非admin账号登录系统，拓扑发现配置页面，域和dcs显示为数字 huangping 2017/6/30 start*/
            this.getCfg("topoSetting", function (v) {
                ctx._settopSettingValue(v);
            });
            /*BUG #40287 拓扑管理：非admin账号登录系统，拓扑发现配置页面，域和dcs显示为数字 huangping 2017/6/30 end*/
			//搜索深度
			this.topoSetting.hop.combobox({
				value:1,
				valueField:"value",
				textField:"value",
				data:[{
					value:0
				},{
					value:1
				},{
					value:2
				},{
					value:3
				},{
					value:4
				},{
					value:5
				}]
			});
			//-------------发现参数------------
			//端口
			this.topoSetting.port.validatebox({
				validType:"port"
			});
			this.topoSetting.port.val(161);
			//重试次数
			this.topoSetting.retryTimes.validatebox({
				validType:"min[1]",
				invalidMessage:"最小值为1"
			});
			this.topoSetting.retryTimes.val(1);
			//超时时间
			this.topoSetting.timeout.validatebox({
				validType:"min[1000]",
				invalidMessage:"最小值为1000毫秒"
			});
			this.topoSetting.timeout.val(1600);
		}
		this.topoSettingFlag=true;
	},
	_regtopSettingEvent:function(){
		var ctx = this;
		var ts = this.topoSetting;
		//所有单选按钮
		ts.form.find(".topo_find_checkbox").on("click",function(){
			ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
		});
		//搜索深度
		ts.hop.combobox({
			onSelect:function(){
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}
		});
		//dcs
		ts.dcsIpt.combobox({
			onSelect:function(){
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}
		});
		//域
		ts.dmsIpt.combobox({
			onSelect:function(){
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}
		});
		//附加子网添加按钮
		ts.includeGridAddBtn.on("click",function(){
			var ip = ts.includeIpIpt.val(),mask=ts.includeMaskIpt.val();
			if(ip && mask && ts.includeIpIpt.validatebox("isValid") && ts.includeMaskIpt.validatebox("isValid")){
				ctx.addDataToGrid(ts.includeGrid, {
					ip:ip,mask:mask
				});
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}
		});
		//附加子网删除按钮
		ts.includeGridDelBtn.on("click",function(){
			$.messager.confirm("警告","确定删除？",function(r){
				if(r){
					ctx.deleteSelectedFromGrid(ts.includeGrid);
					ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
				}
			});
		});
		//屏蔽子网添加按钮
		ts.excludeGridAddBtn.on("click",function(){
			var ip = ts.excludeIpIpt.val(),mask=ts.excludeMaskIpt.val();
			if(ip && mask && ts.excludeIpIpt.validatebox("isValid") && ts.excludeMaskIpt.validatebox("isValid")){
				ctx.addDataToGrid(ts.excludeGrid, {
					ip:ip,mask:mask
				});
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}
		});
		//屏蔽子网删除按钮
		ts.excludeGridDelBtn.on("click",function(){
			$.messager.confirm("警告","确定删除？",function(r){
				if(r){
					ctx.deleteSelectedFromGrid(ts.excludeGrid);
					ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
				}
			});
		});
		//端口
		ts.port.on("blur",function(){
			var $this = $(this);
			if($this.validatebox("isValid")){
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}else{
				$this.focus();
			}
		});
		//重试次数
		ts.retryTimes.on("blur",function(){
			var $this = $(this);
			if($this.validatebox("isValid")){
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}else{
				$this.focus();
			}
		});
		//超时时间
		ts.timeout.on("blur",function(){
			var $this = $(this);
			if($this.validatebox("isValid")){
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}else{
				$this.focus();
			}
		});
		//SNMPV3添加按钮
		ts.snmpv3AddBtn.on("click",function(e){
			ctx.field.snmpv3.show();
			ctx._initSnmpV3Dia();
		});
		//SNMPV3删除按钮
		ts.snmpv3DelBtn.on("click",function(){
			$.messager.confirm("警告","确定删除？",function(r){
				if(r){
					ctx.deleteSelectedFromGrid(ts.snmpv3Grid);
					ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
				}
			});
		});
	},
	_gettopSettingValue:function(){
		var ts = this.topoSetting;
		var retn = ts.formDom.getValue();
		retn.include=ts.includeGrid.datagrid("getRows");
		retn.exclude=ts.excludeGrid.datagrid("getRows");
		retn.snmpv3= ts.snmpv3Grid.datagrid("getRows");
		retn.domainId=ts.dmsIpt.combobox("getValue");
		retn.groupId=ts.dcsIpt.combobox("getValue");
		retn.hop=ts.hop.numberbox("getValue");
		retn.port=ts.port.val();
		retn.retryTimes=ts.retryTimes.val();
		retn.timeout=ts.timeout.val();
		return retn;
	},
	_settopSettingValue:function(v){
		if(v){
			var ts = this.topoSetting;
			ts.formDom.setValue(v);
			ts.includeGrid.datagrid("loadData",v.include);
			ts.excludeGrid.datagrid("loadData",v.exclude);
			ts.snmpv3Grid.datagrid("loadData",v.snmpv3);
            /*BUG #40287 拓扑管理：非admin账号登录系统，拓扑发现配置页面，域和dcs显示为数字 huangping 2017/6/30 start*/
            /*ts.dmsIpt.combobox("select",v.domainId);
             ts.dcsIpt.combobox("setValue",v.groupId);*/
            if (v.domainId == 1) {
                var val = ts.dmsIpt.combobox('getData');
                ts.dmsIpt.combobox("select", val[0].id);
            } else {
                ts.dmsIpt.combobox("select", v.domainId);
            }
            ts.dcsIpt.combobox("setValue", v.groupId);
            /*BUG #40287 拓扑管理：非admin账号登录系统，拓扑发现配置页面，域和dcs显示为数字 huangping 2017/6/30 end*/
			ts.hop.combobox("setValue",v.hop||1);
			ts.port.val(v.port||161);
			ts.retryTimes.val(v.retryTimes||1);
			ts.timeout.val(v.timeout||1600);
		}
	},
	_initSnmpV3Dia:function(){
		if(!this.snmpV3DiaFlag){
			this.searchField("snmpv3");
			var sv = this.snmpv3;
			sv.okBtn.linkbutton("RenderLB",{
				iconCls:"icon-save"
			});
			sv.cancelBtn.linkbutton("RenderLB",{
				iconCls:"fa fa-times-circle"
			});
			sv.safeLv.combobox({
				editable:false,
				width:165,
				textField:"text",
				valueField:"value",
				data:[{
					value:1,text:"NoAuth NoPriv"
				},{
					value:2,text:"Auth NoPriv"
				},{
					value:3,text:"Auth Priv"
				}],
				value:3
			});
			sv.protocal.combobox({
				editable:false,
				width:165
			});
			sv.eagreement.combobox({
				editable:false,
				width:165
			});
			sv.safeLv.combobox("setValue",3);
			this.snmpV3DiaFlag=true;
			sv.formDom = new FormUtil(sv.form);
			this._regSnmpV3DiaEvent();
		}
	},
	_regSnmpV3DiaEvent:function(){
		var sv = this.snmpv3;
		var ctx = this;
		//确定按钮
		sv.okBtn.on("click",function(){
			var value = ctx._getSnmpV3DiaValue();
			if(value){
				ctx.addDataToGrid(ctx.topoSetting.snmpv3Grid, value);
				ctx.field.snmpv3.hide();
				//保存topo设置
				ctx.saveCfg("topoSetting", ctx._gettopSettingValue());
			}
		});
		//取消按钮
		sv.cancelBtn.on("click",function(){
			ctx.field.snmpv3.hide();
		});
		//安全级别
		sv.safeLv.combobox({
			onChange:function(nv,ov){
				ctx._switchSafeLvView(nv);
			},
		});
	},
	//切换安全级别视图
	_switchSafeLvView:function(level){
		var sv = this.snmpv3;
		switch(parseInt(level)){
		case 1:
			sv.protocal.parents(".topo_find_item").hide();
			sv.passWord.parents(".topo_find_item").hide();
			sv.eagreement.parents(".topo_find_item").hide();
			sv.epassword.parents(".topo_find_item").hide();
			break;
		case 2:
			sv.protocal.parents(".topo_find_item").show();
			sv.passWord.parents(".topo_find_item").show();
			sv.eagreement.parents(".topo_find_item").hide();
			sv.epassword.parents(".topo_find_item").hide();
			break;
		case 3:
			sv.protocal.parents(".topo_find_item").show();
			sv.passWord.parents(".topo_find_item").show();
			sv.eagreement.parents(".topo_find_item").show();
			sv.epassword.parents(".topo_find_item").show();
			break;
		}
	},
	_getSnmpV3DiaValue:function(){
		var sv = this.snmpv3;
		var retn = sv.formDom.getValue();
		retn.safeLv=sv.safeLv.combobox("getValue");
		retn.protocal=sv.protocal.combobox("getValue");
		retn.safeLvLabel=sv.safeLv.combobox("getText");
		retn.eagreement=sv.eagreement.combobox("getValue");
		switch(parseInt(retn.safeLv)){
		case 1:
			retn.protocal="";
			retn.passWord="";
			retn.eagreement="";
			retn.epassword="";
			break;
		case 2:
			retn.eagreement="";
			retn.epassword="";
			break;
		}
		return retn;
	},
	_intChangeSafeLv:function(){
		this.snmpv3.sageLv.combobox({
			onChange:function(n,o){
				alert("wocaishi laoshi"+n);
			}
		});
	},
	initUi:function(){
		this._initcommonBody();
		this._initwholeNet();
	},
	//向表格中添加数据
	addDataToGrid:function(grid,row){
		var rows = grid.datagrid("getRows");
		if(!(rows instanceof Array)){
			rows = [];
		}
		rows.push(row);
		grid.datagrid("loadData",rows);
	},
	//删除表格中选中的数据
	deleteSelectedFromGrid:function(grid){
		var rows = grid.datagrid("getSelections");
		for(var i=0;i<rows.length;++i){
			var ridx=grid.datagrid("getRowIndex",rows[i]);
			grid.datagrid("deleteRow",ridx);
		}
	},
	regEvent:function(){
		var ctx = this;
		//tabs
		var tabs = this.field.findTabs.find("li");
		log(this.field.snmpv3);
		tabs.on("click",function(){
			ctx.field.findPanels.find("[data-panel]").hide();
			var tmp = $(this);
			ctx.field.findTabs.find(".topo_find_setting_tab_icon").removeClass("activetab");
			ctx.field.findTabs.find(".topo_find_setting_tab_text").removeClass("active");
			tmp.find(".topo_find_setting_tab_icon").addClass("activetab");
			tmp.find(".topo_find_setting_tab_text").addClass("active");
			var panelKey = tmp.attr("data-panel");
			var panel = ctx.panel[panelKey];
			panel.fadeIn();
			ctx["_init"+panelKey]();
			var tab = ctx[panelKey];
			if(tab && tab.commonBodyHolder){
				tab.commonBodyHolder.append(ctx.field.commonBody);
			}
		});
		//如果进入“拓扑管理”系统提示点击“暂无拓扑视图，请首先进行拓扑发现”，则默认进入第五个“发现配置”否则默认进入第一个“全网发现”
		$.post(oc.resource.getUrl("topo/hasTopo.htm"),function(home){
			if(home.hasTopo){
				$(tabs.get(0)).trigger("click");
			}else{
				$(tabs.get(4)).trigger("click");
			}
		},"json");
	},
	//保存配置
	saveCfg:function(type,val,callBack){
		oc.util.ajax({
			url:oc.resource.getUrl("topo/setting/save.htm"),
			urlMsg:"保存中...",
			data:{
				key:type,
				value:JSON.stringify(val)
			},
			success:function(msg){
				if(callBack){
					callBack(msg);
				}
			}
		});
	},
	//获取配置
	getCfg:function(type,callBack){
		$.post(oc.resource.getUrl("topo/setting/get/"+type+".htm"),callBack,"json");
	},
	//开始发现
	startFind:function(type){
		var ctx = this;
		//先查看是否有域信息
		oc.util.ajax({
			url:oc.resource.getUrl("topo/help/userinfo.htm"),
			success:function(result){
				var userinfo=result.data;
				if(userinfo && userinfo.groupId){
					//开始发现
					oc.util.ajax({
						url:oc.resource.getUrl("topo/find/type/"+type+".htm"),
						success:function(result){
							var info = result.data;
							if(!info.failed){
								//关闭当前对话框
								ctx.root.dialog("close");
								if(oc.topo.topoIsRunning){
									alert("正在进行拓扑发现，请稍后重试","warning");
									eve("oc.topo.findResultDia.open");
								}else{
									alert(info.msg,"info");
									//打开发现结果对话框
									eve("topo.loadsubtopo",this,0,true);
									eve("oc.topo.addSubTopo.finished");
									oc.topo.findResultDia.show();
									oc.topo.findResultDia.start();
								}
							}else{
								alert(info.msg,"danger");
							}
						}
					});
				}else{
					//打开域设置面板
					alert("还未设置DCS,请设置好后重试","warning");
					$(ctx.field.findTabs.find("li[data-panel=topoSetting]")).trigger("click");
				}
			},
			error:function(){
				//打开域设置面板
				alert("还未设置DCS,请设置好后重试","warning");
				ctx.panel.topoSetting.trigger("click");
			}
		});
	}
};