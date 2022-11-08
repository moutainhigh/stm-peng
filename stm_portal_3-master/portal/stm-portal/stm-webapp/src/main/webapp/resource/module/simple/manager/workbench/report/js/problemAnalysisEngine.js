$(function(){
	var $alertWindow = $('#alertPageContainer'),myForm = $alertWindow.find('#expect_setting_form'),form,reportId = $alertWindow.data("reportId");
	form=oc.ui.form({
		selector : myForm
	});
	loadDefaultExpect();
	//加载默认期望值
	function loadDefaultExpect(){
		oc.util.ajax({
			url:oc.resource.getUrl("simple/manager/workbench/getDefaultExpect.htm"),
			successMsg:'',
			success:function(data){
				if(data.code==200 && data.data){
					var obj = data.data;
					myForm.find("[name=available]").val(obj.available?obj.available:"");
					myForm.find("[name=availableCheck]").attr('checked',obj.available?true:false);
					
					myForm.find("[name=mttr]").val(obj.mttr?obj.mttr:"");
					myForm.find("[name=mttrCheck]").attr('checked',obj.mttr?true:false);
					
					myForm.find("[name=mtbf]").val(obj.mtbf?obj.mtbf:"");
					myForm.find("[name=mtbfCheck]").attr('checked',obj.mtbf?true:false);
					
					myForm.find("[name=downTimes]").val(obj.downTimes?obj.downTimes:"");
					myForm.find("[name=downTimesCheck]").attr('checked',obj.downTimes?true:false);
					
					myForm.find("[name=downDuration]").val(obj.downDuration?obj.downDuration:"");
					myForm.find("[name=downDurationCheck]").attr('checked',obj.downDuration?true:false);
					
					myForm.find("[name=alarmTimes]").val(obj.alarmTimes?obj.alarmTimes:"");
					myForm.find("[name=alarmTimesCheck]").attr('checked',obj.alarmTimes?true:false);
					
					myForm.find("[name=unrecoveryAlarmTimes]").val(obj.unrecoveryAlarmTimes?obj.unrecoveryAlarmTimes:"");
					myForm.find("[name=unrecoveryAlarmTimesCheck]").attr('checked',obj.unrecoveryAlarmTimes?true:false);
				}
			}
		});
	}
	
	//设置复选框的值 ，是否启用期望值项
	myForm.find('input[type=checkbox]').change(function(){
		if($(this).attr('checked')=='checked' || $(this).attr('checked')==true){
			$(this).parent('span').nextAll().find('input[type=text]').attr('disabled',false);
		}else{
			$(this).parent('span').nextAll().find('input[type=text]').attr('disabled',true);
		}
	});
	
	//智能分析
	$alertWindow.find("#intelligence").click(function(){
		if(form.validate()){
			var data = $.extend({},{reportId:reportId},form.val());
			$alertWindow.data('expect',data);
			$alertWindow.load('module/simple/manager/workbench/report/mothReportDetails.html');
		}
	});
})