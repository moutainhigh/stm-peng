(function($) {
	function AvaInfo() {
		this.selector = $("#oc-module-home-workbench-availability").attr("id", oc.util.generateId());
		oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
//		this.init();
	}
	AvaInfo.prototype = {
		selector:undefined,
		resourctId:undefined,
		loadData:undefined,
		setResourceId : function(value) {
			var workbenchId = this.selector.parent("div").attr("data-workbench-id");
			var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
			this.resourctId = value;
			oc.home.workbench.setExt(workbenchId, workbenchSort, this.resourctId,null,this.selector);
		},
		init : function() {
			this.resourctId = this.selector.parent("div").attr("data-workbench-ext");
			var that = this;
			//通过AJAX查询后台数据，判断是否已经选择过资源
			if (this.resourctId != undefined && this.resourctId != "") {
				this.selector.find("#avaMainNull").hide();
				this.selector.find("#oc-module-home-workbench-availability").show();
				this.initdatagrid();
				//修改渲染方式
//				oc.util.ajax({
//					url:oc.resource.getUrl('home/availability/getById.htm'),
//					data:{resourceId:this.resourctId},
//					startProgress:false,
//					stopProgress:false,
//					success:function(d){
//						that.loadData = d;
//						that.loadDatagrid();
//					}
//				});
			} else {
				this.selector.find("#avaMainNull").show();
				this.selector.find("#oc-module-home-workbench-availability").hide();
			}
			
		},
		//修改渲染方式
//		loadDatagrid:function(){
//			this.selector.find('#oc_module_home_availability').datagrid('loadData',this.loadData);
//		},
		statusName:{
			gray:'未知/未监控',
			red:'致命',
			orange:'严重',
			yellow:'警告',
			green:'正常'
		},
		initdatagrid : function() {
			var target = this,
			statusName = target.statusName;
			var datagrid = oc.ui.datagrid({
				selector : this.selector.find('#oc_module_home_availability'),
				url : oc.resource.getUrl('home/availability/getById.htm'),
				queryParams:{resourceId:this.resourctId},
				pagination : false,
				columns : [[
				 {field:'sourceName',title:'资源名称',sortable:true,align:'left',width:'20%',
			         formatter:function(value,row,rowIndex){
							// 加入手形样式
							var statusLabel = $("<label/>").addClass("light-ico_resource " + row.instanceStatus);
							statusLabel.attr('rowIndex', rowIndex).addClass('quickSelectDetailInfo');
							if(value != null){
								statusLabel.attr('title', value).html(value.htmlspecialchars());
						 	}
							return statusLabel;
						}}, {
					field : 'ipAddress',
					title : 'IP地址',
					width : '15%'
				}, {
					field : 'monitorType',
					title : '监控类型',
					width : '15%'
				}, {field:'cpuAvailability',title:'CPU利用率',sortable:false,align:'left',width:'25%',
			         formatter:function(value,row,rowIndex){
			        	 var cpuPercent = null;
			        	 var cpuStatus = 'gray';
			        	 if(null != row.cpuAvailability){
			        		 cpuPercent = row.cpuAvailability;
			        	 }else{
			        		 cpuPercent = '--';
			        	 }
			        	 if(null != row.cpuStatus){
			        		 if(row.instanceStatus!='gray'){
			        			 cpuStatus = row.cpuStatus;
			        		 }
			        	 }
			        	 
			        	 
			        	 return '<div class="rate rate-t-'+cpuStatus+'"><span class="rate-'+cpuStatus+'" style="width:'+cpuPercent+'"></span></div><span class="rt">'+cpuPercent+'</span>';
			    }}, {field:'memoryAvailability',title:'内存利用率',sortable:false,align:'left',width:'25%',
		        	 formatter:function(value,row,rowIndex){
		        		 var memoPercent = null;
		        		 var memStatus = 'gray';
			        	 if(null != row.memoryAvailability){
			        		 memoPercent = row.memoryAvailability;
			        	 }else{
			        		 memoPercent = '--';
			        	 }
			        	 if(null != row.memoryStatus&&row.instanceStatus!='gray'){
			        		 memStatus = row.memoryStatus;
			        	 }
		        	     return '<div class="rate rate-t-'+memStatus+'"><span class="rate-'+memStatus+'" style="width:'+memoPercent+'"></span></div><span class="rt">'+memoPercent+'</span>';
		         }}]]
			});
		},
		reLoad:function(){
			this.init();
		},
		render:function(){
			if(this.loadData!=undefined){
				this.loadDatagrid();
			}else{
				this.init();
			}
		}
	};
	var avaCharts = new AvaInfo();

	oc.home.workbench.availability = function() {
		return avaCharts;
	};
	
	avaCharts.selector.find("#availsetting,#addMyava").click(function() {
		oc.home.workbench.resource.select.open({
			type : 'all', //默认展示所有资源
			title : '资源选择',
			value:avaCharts.resourctId,
			maxSelects:6,
			confirmFn : function(value, obj) {
				avaCharts.selector.find("#oc-module-home-workbench-avashow").show();
				avaCharts.setResourceId(value);
				avaCharts.selector.find("#avaMainNull").hide();
				avaCharts.initdatagrid();
			}
		});
	});
	avaCharts.selector.find('#avarefresh').bind("click", refresh);
	function refresh() {
		if($('#avaMainNull').css('display')=='none'){
			avaCharts.reLoad();
		}
	}
	avaCharts.selector.bind(oc.events.resize,function(){
		avaCharts.render();
	});
})(jQuery); 