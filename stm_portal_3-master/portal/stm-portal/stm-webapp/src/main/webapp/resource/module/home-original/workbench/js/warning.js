(function($) {
	function WarningInfo() {
		this.selector = $("#oc-module-home-workbench-warning").attr("id",oc.util.generateId());
		oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
//		this.init();
	}
	WarningInfo.prototype = {
		selector : undefined,
		constructor : WarningInfo,
		groupId:'0',
		groupName:'全部',
		dg:undefined,
		gridData:undefined,
		init : function() {
			var that = this,domainId = oc.home.workbench.domainId;
			var workbenchId = that.selector.parent("div").attr("data-workbench-id");
			var workbenchSort = that.selector.parent("div").attr("data-workbench-sort");
			var groupStr = that.selector.parent("div").attr("data-workbench-ext");
			if(groupStr!=undefined){
				var group = groupStr.split(",");
				if(group!=undefined){
					that.groupId = group[0];
					that.groupName = group[1];
				}
			}
			that.initdatagrid();
			//重新调整渲染方式
//			oc.util.ajax({
//				url:oc.resource.getUrl('home/warning/getAll.htm'),
//				data:{groupId:that.groupId,domainId:domainId==null?0:domainId},
//				startProgress:false,
//				stopProgress:false,
//				success:function(data){
//					that.gridData = data;
//					that.loadDatagrid(that.gridData);
//				}
//			});
			
		},
		saveSetting:function(id,name){
			this.groupId = id;
			this.groupName = name;
			var workbenchId = this.selector.parent("div").attr("data-workbench-id");
			var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
			this.selector.parent("div").attr("data-workbench-ext",this.groupId+","+this.groupName);
			oc.home.workbench.setExt(workbenchId,workbenchSort,this.groupId+","+this.groupName);
		},
		//重新调整渲染方式
//		loadDatagrid:function(data){
//			this.selector.find("#oc_system_home_warning").datagrid("loadData",this.gridData);
//		},
		initdatagrid : function() {
			var that = this;
			var domainId = oc.home.workbench.domainId;
			domainId==null?0:domainId;
			that.dg = oc.ui.datagrid({
				selector : that.selector.find("#oc_system_home_warning"),
				url:oc.resource.getUrl('home/warning/getAll.htm?groupId='+that.groupId+'&domainId='+domainId),
				pagination : false,
				width : 600,
				height : 'auto',
				fit:true,
				columns : [ [
						{
					field : 'eventId',
					align : 'left',
					title : '',
					hidden : true
				},{
					field : 'sourceId',
					align : 'left',
					title : '',
					hidden : true
				},
				{
					field : 'instanceStatus',
					title:'',
					width : 30,
					formatter : function(value, row, index) {
						return "<span class='light-ico "+row.instanceStatus+"light' ></span>";
					}
				},
				{
					field : 'alarmContent',
					title : '告警内容',
					width : 280,
					align : 'left',
					ellipsis:true,
					formatter:function(value,row,rowIndex){
			        	 var formatterString = '';
		                 if(value.length > 45){
		                     formatterString = '<label style="cursor:pointer;" title="' + value.htmlspecialchars() + '" >'+value.htmlspecialchars().substr(0, 45)+'...'+'</label>';
		                 }else{
		                     formatterString = '<label style="cursor:pointer;" title="' + value.htmlspecialchars() + '" >'+value.htmlspecialchars()+'</label>';
		                 }
		                 return formatterString; 		
			         }
				}, {
					field : 'resourceName',
					title : '告警来源',
					width : 80,
					align : 'left',
					ellipsis:true,
					formatter:function(value,row,rowIndex){
			        	var formatterString = '';
			        	if(value != null){
			        		if(value.length > 15){
			        			formatterString = '<label style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars().substr(0, 15)+'...' +'</label>'
			        		}else{
			        			formatterString = '<label  style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars() +'</label>'
			        		}
			        		
			        	}
			        	return formatterString; 
			        }
				}, {
					field : 'ipAddress',
					title:'IP地址',
					width : 115,
					align : 'left',
					ellipsis:true
				},{
					field : 'eventTime',
					title : '产生时间',
					width : 115,
					align : 'left',
					ellipsis:true,
					 formatter:function(value,row,index){
 		        		 if(value != null){
 		        			 var date=new Date(value);
 		        			 if(isNaN(date)){
 		        				 return value;
 		        			 }else{
 		        				 return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
 		        			 }
 		        		 }
 		        		 return null;
 		        	 }
				} ]],
				//告警一览点击事件
				onClickCell : function(rowIndex, field, value){
					//告警内容
					if(field == 'alarmContent'){
						var row = that.dg.selector.datagrid('getRows')[rowIndex];
						var alarmId = row.eventID;
						var type = '1';
						oc.resource.loadScript('resource/module/alarm-management/js/alarm-detailed-information.js',function(){
							 oc.alarm.detail.inform.open(alarmId,type);
     	     			});
						
					};
					//告警来源
					if(field == 'resourceName'){
						var row = that.dg.selector.datagrid('getRows')[rowIndex];
						var resourceId = row.sourceID;
						oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailInfo.js', function(){
							oc.module.resmanagement.resdeatilinfo.open({instanceId:resourceId});
					 	});
					}
					//告警来源
					if(field == 'ipAddress'){
						var row = that.dg.selector.datagrid('getRows')[rowIndex];
						var resourceId = row.sourceID;
						oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailInfo.js', function(){
							oc.module.resmanagement.resdeatilinfo.open({instanceId:resourceId});
					 	});
					}
				}
			});
//			that.selector.bind(oc.events.resize,function(){
//				that.render();
//			});
		},
		reLoad:function(){
			this.init();
		},
		render:function(){
			if(this.gridData!=undefined){
				this.initdatagrid();
			}else{
				this.init();
			}
		}
	};
	var warningCharts = new WarningInfo();

	warningCharts.selector.find('#warningrefresh').click(function(){
		//warningCharts.selector.find("#oc_system_home_warning").datagrid('loadData',{"code":200,"data":{"total":0,"rows":[]}});
		warningCharts.reLoad();
	});
	warningCharts.selector.find('#warningSetting').click(function(){
		oc.home.workbench.resourcegroup.open({
			title:"告警一览",
			selectValue:warningCharts.groupId,
			fn:function(groupId,name){
				warningCharts.saveSetting(groupId,name);
				warningCharts.init();
			}
		});
	});
	
	oc.home.workbench.warning = function() {
		return warningCharts;
	};
	
})(jQuery);