(function($) {
	
	
	function initFrom(alarmId,alarmDialog,data){
		var fromDiv = alarmDialog.find('#alarm_management_detail_id');
		var from =oc.ui.form({
			selector:fromDiv,
			combobox:[{
				selector:fromDiv.find("[name='ipAddress']"),
				data:[{id:data.data.ipAddress, name:data.data.ipAddress}],
				value:data.data.ipAddress,
				placeholder:null
			}]
		});
		fromDiv.find("[name='alarmContent']").val(data.data.alarmContent);
		fromDiv.find("[name='alarmType']").val(data.data.alarmType);
		fromDiv.find("[name='resourceName']").val(data.data.resourceName);
		function formatDate(date){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
		}
		fromDiv.find("[name='collectionTime']").val(formatDate(new Date(data.data.collectionTime)));
		function toStatus(){
			 var status =  data.data.instanceStatus;
			 var values = "";
			 if(status =="red"){
				 values = "致命";
				 $("#alarmLight").addClass("redlight");
				 return values;
			 }else if(status =="orange"){
				 values = "严重";
				 $("#alarmLight").addClass("orangelight");
				 return values;
			 }else if(status =="yellow"){
				 values = "警告";
				 $("#alarmLight").addClass("yellowlight");
				 return values;
			 }else if(status =="green"){
				 values = "正常";
				 $("#alarmLight").addClass("greenlight");
				 return values;
			 }else{
				 values = "正常";
				 $("#alarmLight").addClass("greenlight");
				 return values;
//				 values = "未知";
//				 $("#alarmLight").addClass("graylight");
//				 return values;
			 }
		}
		fromDiv.find("[name='alarmStatus']").val(toStatus());
		var datagridDiv=alarmDialog.find('#alarm_detail_datagrid');
		function formatDate(date){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
		}
		datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('alarm/alarmManagement/getAlarmNotify.htm?alarmId='+ data.data.alarmId),
			columns:[[
		         {field:'notifyStatus',title:'状态',sortable:true,align:'left',width:10
		        	 ,formatter:function(value,row,rowIndex){
		        		 if(row.notifyStatus == '发送成功'){
		        			 return '<span class="ico ico-already"/>' + row.notifyStatus;
		        		 }else if(row.notifyStatus == '发送失败'){
		        			 return '<span class="ico ico-noready"/>'+ row.notifyStatus;
		        		 }else if(row.notifyStatus == '禁止发送'){
		        			 return '<span class="ico ico-not-send"/>'+ row.notifyStatus;
		        		 }else if(row.notifyStatus =='等待发送'){
		        			 return '<span class="ico ico-wate-send"/>'+ row.notifyStatus;
		        		 }else if(row.notifyStatus =='发送中'){
		        			 return '<span class="ico oc-ico-sending"/>'+ row.notifyStatus;
		        		 }else{
		        			 return ''; 
		        		 }
		        		 
		         }}, 
		         {field:'userName',title:'接收人',sortable:true,align:'left',width:15},
		         {field:'sendTime',title:'发送时间',sortable:true,align:'left',width:20,
	 			         formatter:function(value,row,index){
		 			        	if(value != null){
		 			        		return  formatDate(new Date(value));
		 			        	 }
		 			         return null;
	 			         } 
		        	 },
		         {field:'notifytType',title:'接收方式',sortable:true,align:'left',width:15
		        	 ,formatter:function(value,row,rowIndex){
		        		 if(row.notifytType == 'email'){
		        			 return '<span class="fa fa-envelope bule-color"/>';//ico ico-email
		        		 }else if(row.notifytType == 'sms'){
		        			 return '<span class="fa fa-phone-square bule-color"/>';//ico ico-message
		        		 }else{
		        			 return '<span class="fa fa-comments bule-color"/>';//ico ico-alert
		        		 }
		         }},
		         {field:'notifyAdrr',title:'接收地址',sortable:true,align:'left',width:20}
		         
		     ]]
		});
		return from;
	}
	
	/**
	 * 打开告警详细
	 */
	function open(alarmId,type,flag){
		var alarmDialogDiv=$('<div/>');
		
		var alarmDialog;
		alarmDialog=alarmDialogDiv.dialog({
		    title: '发送状态',
		    flt:false,
		    width: 800, 
		    height: 520,
		    href: oc.resource.getUrl('resource/module/alarm-management/alarm-management-detail.html'),
		    onLoad:function(){
		    	if(flag == false){
		    		$("#settr").hide();
		    		$("#settrName").show();
		    		$("#alarmTypeIdName").hide();
		    		$("#alarmTypeId").hide();
		    		type = '0';
		    	}
		    	oc.util.ajax({
		    		url: oc.resource.getUrl('alarm/alarmManagement/getAlarmById.htm'),
		    		data:{alarmId:alarmId, alarmType:type},
		    		successMsg:null,
		    		success:function(data){
		    			if(data.data == null){
		    				alert("该条未恢复告警已恢复");
		    			}else{
		    				initFrom(alarmId,alarmDialog,data);
		    			}	
		    		}
		    	});
		    }
		});
		return alarmDialog;
	}
	
	
	
	
	 //命名空间
	oc.ns('oc.alarm.detail.dialog');
	oc.alarm.detail.dialog={
			open:function(alarmId,type,flag){
				return open(alarmId,type,flag);
			}
	};
})(jQuery);