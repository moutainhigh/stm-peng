$(function(){
	function querySnapShot(){
		
	}
	querySnapShot.prototype = {
		constructor : querySnapShot,
		alarmInfo : undefined,
		fileInfo : undefined,
		open : function(row){
			var that = this;
			this.alarmInfo = row;
			this.fileInfo = $.parseJSON(row.snapShotJSON);
			this.fileInfo.snapshotFileId = this.fileInfo.snapshotFileId == undefined ? 0 : this.fileInfo.snapshotFileId;
			this.fileInfo.recoverFileId = this.fileInfo.recoverFileId == undefined ? 0 : this.fileInfo.recoverFileId;
			oc.util.ajax({
				url : oc.resource.getUrl('alarm/alarmManagement/getSnapShotFile.htm'),
				data : {
					snapshotFileId : this.fileInfo.snapshotFileId,
					recoverFileId : this.fileInfo.recoverFileId
				},
				success : function(data){
					that.openDialog(data.data);
				}
			});
		},
		openDialog : function(data){
			var dlg = $("<div/>");
			var tabsDiv = $("<div class='window-tabsbg'><div class='snapshotFileTab' title='快照文件'></div><div class='resumFileTab' title='故障恢复文件'></div></div>");
			dlg.append(tabsDiv).dialog({
				title : '快照内容',
				flt : false,
				width : 800,
				height : 500
			});
			tabsDiv.tabs({
				fit : true
			});
			tabsDiv.find(".snapshotFileTab").html(data.snapShotContent);
			tabsDiv.find(".resumFileTab").html(data.recoverContent);
		}
	}
	oc.ns('oc.module.alarm.management.querysnapshot');
	oc.module.alarm.management.querysnapshot = {
		open : function(row){
			new querySnapShot().open(row);
		}
	};
});