(function($){
	function backup(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	backup.prototype={
		constructor:backup,
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			
			var dlg=$('<div/>'),that=this,cfg=this.cfg,
			buttons=[{
				text:'取消',
				iconCls:'fa fa-times-circle',
				handler:function(){
					dlg.dialog('close');
				}
			}];
			var user = oc.index.getUser();
			if(user.systemUser==true){
				buttons.unshift({
					iconCls:'fa fa-check-circle',
					text:'确定',
					handler:function(){
						var ids = that._datagrid.getSelectIds();
						if(ids != undefined&&ids != ""){
							ids = ids.join();
						}else{
							ids = oc.sid;
						}
						if(ids == undefined||ids == ""){
							alert("请选择备份计划");
						}else{
							oc.util.ajax({
								url:oc.resource.getUrl('portal/config/plan/upateDevicePlan.htm'),
								data:{planDevices:cfg.id,planId:ids},
								successMsg:null,
								success:function(data){
									dlg.dialog('close');
									if(data.code&&data.code==200){
										alert("保存成功");
										cfg.datagrid.reLoad();
									}else if(data.code==299){alert(data.data);}
								}
							});
						}
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/config-file/device/backup.html'),
				title:'备份计划',
				width:750,
				height:450,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg,cfg.id,cfg.backupId);
				}
			});
		},
		_defaults:{
			id:undefined//数据id
		},
		_mainDiv:undefined,
		_datagrid:undefined,
		_id:'#oc_config_device_backup',
		_init:function(dlg,id,backupId){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			var datagridDiv=this._mainDiv.find('.oc_config_device_backup_datagrid');
			//表格实例
			this._datagrid = oc.ui.datagrid({
				selector:datagridDiv,
				singleSelect:true,
				url:oc.resource.getUrl('portal/config/plan/getPager.htm'),
				columns:[[
					 {field: 'id',title: '<input type="radio"/>',width: 20,formatter: function(value, rowData, rowIndex){
						 return '<input type="radio" name="selectRadio" id="selectRadio"' + 
						 	rowIndex + ' value="' + rowData.oid + '" />';
					 }},
			         {field:'name',title:'任务名称',sortable:true,width:80},
			         {field:'entryName',title:'创建人',sortable:true,width:80},
			         {field:'entryTime',title:'创建时间',sortable:true,width:120,formatter:function(value){
			        	  var datetime = new Date();
			        	    datetime.setTime(value);
			        	    var year = datetime.getFullYear();
			        	    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
			        	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
			        	    var hour = datetime.getHours()< 10 ? "0" + datetime.getHours() : datetime.getHours();
			        	    var minute = datetime.getMinutes()< 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
			        	    var second = datetime.getSeconds()< 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
	        			if(value == undefined||value == "" || value==null) {
	        				return "";
	        			}else{
	        				return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
	        			}
	        				
//	        			var unixTimestamp = new Date(value),hours = unixTimestamp.getHours();
//	        			if(0==hours)
//			     			return unixTimestamp.toLocaleString().replace('12:',"0:");
//			     		else
//		             	 	return unixTimestamp.toLocaleString();
			         }},
			         {field:'type',title:'备份类型',sortable:true,width:80,formatter:typeFormatter},
			         {field:'desc',title:'描述',sortable:true,width:230}
			     ]],
			     onLoadSuccess:function(data){
			    	 $('input[type=radio]').eq(0).hide();
			    	 var item = datagridDiv.datagrid('getRows'),index;
			    	 if(item){
			    		 for(var i=item.length-1; i >= 0; i--){
			    			 oc.sid="";//保存被选中的radio id;
			    			 if(item[i].id == backupId){
			    				 index = datagridDiv.datagrid('getRowIndex', item[i]);
			    				 ($("input[type='radio']")[index+1]).checked=true;
			    				 oc.sid = index+1;
			    				 break;
			    			 }
			    		 }
			    	 }
			     }
			});
			function typeFormatter(value){
			   if(value ==1) return "日";
		   	   else if(value == 2) return "周";
			   else if(value == 3) return "月";
			   else if(value == 4) return "年";
			}
		}
	};
	
	oc.ns('oc.module.config.device.backup');
	oc.module.config.device.backup={
		open:function(cfg){
			var backupWin=new backup(cfg);
			backupWin.open();
		}
	};
})(jQuery);
