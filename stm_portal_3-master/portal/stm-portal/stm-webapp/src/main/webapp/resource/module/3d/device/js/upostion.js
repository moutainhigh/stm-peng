(function($){
	function uPostionDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	uPostionDetail.prototype={
		constructor:uPostionDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,
			buttons=[{
				text:'关闭',
				iconCls:'ico ico-cancel',
				handler:function(){
					dlg.dialog('close');
				}
			}];
			var user = oc.index.getUser();
			if(user.systemUser==true){
				buttons.unshift({
					text:'确定',
					iconCls:'ico ico-ok',
					handler:function(){
						that._save(dlg,cfg);
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/3d/device/upostion.html'),
				title:'设置U位型号',
				width:980,
				height:500,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg,cfg);
				}
			});
		},
		_defaults:{
			id:undefined,//数据id
			saveCall:undefined//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#uPostionDetail',
		_init:function(dlg,cfg){
			if(cfg.openFlag){
				$("#ip").find("span").html(cfg.row.ip).attr("title",cfg.row.ip);
				$("#name").find("span").html(cfg.row.name).attr("title",cfg.row.name);
				$("#type").find("span").html(cfg.row.type).attr("title",cfg.row.type);
			}else{
				$("#ip").find("span").html(cfg.devices[cfg.row.id].ip).attr("title",cfg.devices[cfg.row.id].ip);
				$("#name").find("span").html(cfg.devices[cfg.row.id].name).attr("title",cfg.devices[cfg.row.id].name);
				$("#type").find("span").html(cfg.devices[cfg.row.id].type).attr("title",cfg.devices[cfg.row.id].type);
			}
			$("#brand").find("span").html(cfg.row.brand).attr("title",cfg.row.brand);
			$("#model").find("span").html(cfg.row.model).attr("title",cfg.row.model);
			$("#uplace").find("span").val(cfg.row.uplace).attr("title",cfg.row.uplace);
			
			cfg.row.layout=="后"?$("input[name='layout']").eq(1).attr("checked","checked"):"";
			$("#uPostionDetail").append($("<div/>").load(
					oc.resource.getUrl('resource/module/3d/device/devicetype.html'),function(){
						oc.resource.loadScript('resource/module/3d/device/js/devicetype.js?t='+new Date(),function(){
							oc.module.thirdd.resource.devicetype.openInner({self:{type:cfg.row.type,model:cfg.row.model},
								threedInfo:cfg.threedInfo,products:cfg.products});
						});
					}
				)
			);
		},
		_save:function(dlg,cfg){
			var row = cfg.row,uplace=$("#uplace").val(),activeObj;
			activeObj = $("#deviceType").find("div.active"),
			_msg0="请先设置U位和关联型号",_msg1="U位格式设置不对，请参考说明";
//			_msg2="U位设置重复，请设置其他U位";
			if(uplace==null || uplace.trim()=="" || activeObj.length==0){
				alert(_msg0);
			}else if(!/((^[1-3]\d{0,1}$)|(^4[0-6]{0,1}$))|(^(([1-3]\d{0,1})|(4[0-6]{0,1}))-(([1-3]\d{0,1})|(4[0-6]{0,1}))$)/.test(uplace.trim())){
				alert(_msg1);
			}else{
				var currRows = cfg.datagrid.selector.datagrid('getRows'), 
					_regex = /^(([1-3]\d{0,1})|(4[0-6]{0,1}))-(([1-3]\d{0,1})|(4[0-6]{0,1}))$/;
				if(cfg.exceptDevices) currRows = currRows.concat(cfg.exceptDevices);
				if(_regex.test(uplace.trim())){
					var uarr = uplace.trim().split("-");
					if(Number.parseInt(uarr[0])>=Number.parseInt(uarr[1])){
						alert(_msg1);
						return;
					}
				}
//					for(var i=0;i<currRows.length;i++){
//						if(currRows[i].uplace && _regex.test(currRows[i].uplace.trim())){
//							var cuarr = currRows[i].uplace.trim().split("-");
//							for(var m=Number.parseInt(uarr[0]);m<=Number.parseInt(uarr[1]);m++){
//								if(Number.parseInt(cuarr[0])<=m && m<=Number.parseInt(cuarr[1])){
//									alert(_msg2);
//									return;
//								}
//							}
//						}else if(currRows[i].uplace){
//							if(Number.parseInt(uarr[0])<=currRows[i].uplace.trim()
//									&& currRows[i].uplace.trim()<=Number.parseInt(uarr[1])){
//								alert(_msg2);
//								return;
//							}
//						}
//					}
//				}else{
//					for(var i=0;i<currRows.length;i++){
//						if(currRows[i].uplace && uplace.trim()==currRows[i].uplace.trim()){
//							alert(_msg2);
//							return;
//						}else if(currRows[i].uplace && _regex.test(currRows[i].uplace.trim())){
//							var uarr = currRows[i].uplace.trim().split("-");
//							if(Number.parseInt(uarr[0])<=Number.parseInt(uplace.trim())
//									&& Number.parseInt(uplace.trim())<=Number.parseInt(uarr[1])){
//								alert(_msg2);
//								return;
//							}
//						}
//					}
//				}
				row.layout = $("#layout").eq(0).is(":checked")?"前":"后";
				row.uplace = uplace.trim();
				row.model=activeObj.find("td[model]").attr("model");
				row.brand=activeObj.find("td[brand]").attr("brand");
				if(cfg.openFlag){
					cfg.datagrid.selector.datagrid('updateRow',{
						index:cfg.rowIndex,
						row:row
					});
					$(".uPostionBtn").linkbutton({ plain:true,iconCls:'ico ico-resource'}).attr("title","设置U位型号");
					dlg.dialog('close');
				}else{
					row.name = cfg.devices[cfg.row.id].name;
					row.ip = cfg.devices[cfg.row.id].ip;
					row.type = cfg.devices[cfg.row.id].type;
					oc.util.ajax({
						url: oc.resource.getUrl('portal/3d/cabinet/update.htm'),
						data:row,
						successMsg:null,
						success:function(data){
							if(data.code && data.code==200){
								dlg.dialog('close');
								alert("操作成功");
								$("#pageTag-container").find("#nodeTree").tree('getSelected').target.click();
							}else if(data.code==299){alert(data.data);}
						}
					});
				}
			}
		}
	};
	
	oc.ns('oc.module.thirdd.resource.uposition');
	oc.module.thirdd.resource.uposition={
		open:function(cfg){
			var uposition=new uPostionDetail(cfg);
			uposition.open();
		}
	};
})(jQuery);
