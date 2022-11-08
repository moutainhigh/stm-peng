$(function(){
	function snapshot(){
		
	}
	snapshot.prototype = {
		constructor	: snapshot,
		mainDiv : undefined,
		profileDatagrid : undefined,
		init : function(){
			var id = oc.util.generateId();
			this.mainDiv = $("#oc_module_resource_management_snapshotProfile").attr("id", id);
			this.mainDiv.panel({
				fit : true,
				isOcAutoWidth : true
			});
			this.createSnapshotDatagrid();
		},
		createSnapshotDatagrid : function(){
			var that = this;
			var toolbar = [ {
				text : '新建',
				iconCls : 'fa fa-plus',
				onClick : function() {
					that.addSnapshot();
				}
			}, {
				text : '复制',
				iconCls:'icon-copy',
				onClick : function() {
					that.copySnapshot();
				}
			},{
				text : '删除',
				iconCls : 'fa fa-trash-o',
				onClick : function() {
					that.delSnapshot();
				}
			} ];
			this.profileDatagrid = oc.ui.datagrid({
				selector : that.mainDiv.find(".oc_module_resource_management_snapshotProfile_datagrid"),
				pageSize:$.cookie('pageSize_snapshotProfile')==null ? 15 : $.cookie('pageSize_snapshotProfile'),
				url : oc.resource.getUrl('portal/resource/snapshotProfile/getAllSnapshotProfile.htm'),
				checkOnSelect : false,
				columns : [[{
					field : 'profileId',
					title : '-',
					checkbox : true,
					width : 20
				},
				{
					field:'isUse',
					title:'是否启用',
					width:30,
					formatter: function(value,row,index){
						return "<span class='oc-top0 locate-left status oc-switch "+(value == 1 ? "open" : "close")+"' data-data='"+(value == 1)+"'></span>";
					}
				},
		        {
					field : 'profileName',
					title : '策略名称',
					width : 40,
					formatter : function(value, row, index) {
						return "<span class='oc-pointer-operate'>" + value + "</span>";
					}
				},{
					field : 'profileDesc',
					title : '策略描述',
					width : 40,
					formatter : function(value, row, index) {
						return value;
					}
				},{
					field : 'createUser',
					title : '创建者',
					width : 40
				}]],
				octoolbar : {
					right : toolbar
				},
				onClickCell:function(rowIndex, field, value, e){
					var row = that.profileDatagrid.selector.datagrid("getRows")[rowIndex];
					if(field=='isUse'){
						that.updateIsUse(row, rowIndex);
					} else if(field=='profileName'){
						oc.resource.loadScript('resource/module/resource-management/snapshotProfile/js/snapshotProfileDetail.js', function() {
							oc.resource.snapshotdetail.edit(row, that.profileDatagrid);
						});
					}
				}
			});
			
			//cookie记录pagesize
			var paginationObject = that.mainDiv.find(".oc_module_resource_management_snapshotProfile_datagrid").datagrid('getPager');
			if(paginationObject){
				paginationObject.pagination({
					onChangePageSize:function(pageSize){
						var before_cookie = $.cookie('pageSize_snapshotProfile');
						$.cookie('pageSize_snapshotProfile',pageSize);
					}
				});
			}
		},
		addSnapshot : function(){
			var that = this;
			oc.resource.loadScript('resource/module/resource-management/snapshotProfile/js/snapshotProfileDetail.js', function() {
				oc.resource.snapshotdetail.open(that.profileDatagrid);
			});
		},
		copySnapshot : function(){
			var that = this;
			var rows = this.profileDatagrid.selector.datagrid('getChecked');
			if(rows == undefined || rows == null || rows.length == 0){
				alert('请勾选一个要复制的策略');
				return false;
			}
			if(rows.length > 1){
				alert('只能勾选一个策略进行复制');
				return false;
			}
			oc.resource.loadScript('resource/module/resource-management/snapshotProfile/js/snapshotProfileDetail.js', function() {
				oc.resource.snapshotdetail.copy(rows[0], that.profileDatagrid);
			});
		},
		delSnapshot : function(){
			var that = this;
			var ids = this.getSelectIds(this.profileDatagrid);
			if(ids == undefined||ids == ""){
				 alert("请选择需要删除的策略");
			}else{
				oc.ui.confirm("是否确认删除策略？",function() {
					oc.util.ajax({
						url:oc.resource.getUrl('portal/resource/snapshotProfile/delSnapshotProfile.htm'),
						timeout : null,
						data:{profileIds:ids.join()},
						successMsg:null,
						success:function(d){
							that.profileDatagrid.load();
						}
					});
				});
			}
		},
		updateIsUse : function(row, rowIndex){
			var that = this;
			row.isUse = row.isUse == 1 ? 0 : 1;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/snapshotProfile/updateIsUse.htm'),
				timeout : null,
				data : {
					profileId : row.profileId,
				},
				successMsg:null,
				success : function(d){
					that.profileDatagrid.selector.datagrid("updateRow",{
						index : rowIndex,
						row : row
					});
					alert("操作成功！");
				}
			});
		},
		getSelectIds : function(dataGrid){
			var objs=dataGrid.selector.datagrid('getChecked'), ids=[];
			for(var i=0,len=objs.length;i<len;i++){
				var obj = objs[i];
				ids.push(obj.profileId);
			}
			return ids;
		}
	};
	
	new snapshot().init();
});