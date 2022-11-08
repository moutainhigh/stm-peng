$(function(){
	var selectedUsers = [];
	oc.topo=oc.topo||{};
	oc.topo.topoWarnUsers={
		$root:$('#topo_warn_users_container'),
		$datagridDiv:$("#warnUserDatagrid"),
		init:function(alarmType,type,selectedUsers){
			var sendersDatagrid = oc.ui.datagrid({
				selector:this.$datagridDiv,
				url : oc.resource.getUrl("topo/mac/warn/senders.htm?alarmType="+alarmType+"&type="+type),
				singleSelect:false,
				pagination:false,
				columns:[[
			         {field:'id',checkbox:true},
			         {field:'name',title:'姓名',/*sortable:true,*/width:100}
		         ]],
		         loadFilter: function (datas) {
		        	 var ctx = this;
		        	 var rows = datas.data.rows;
		        	 for(var i=0;i<selectedUsers.length;i++){
		        		 for (var j = rows.length-1; j>=0; j--) {
		        			 if(selectedUsers[i].id == rows[j].id){
		        				 rows.splice(j,1);
		        			 }
		        		 }
		        	 }
		             return {total:datas.length,rows:rows};
		         }
			});
			
			this.$datagrid = sendersDatagrid;
			this.selectedUsers = selectedUsers;
		},
		getSelectedUsers:function(dia){
			var rows = this.$datagrid.getSelections();
			if (rows.length == 0) {
				alert('请至少选择一个人', 'danger');
			}else{
				dia.dialog("close");
				rows = this.selectedUsers.concat(rows);
				return rows;
			}
		}
	};
});