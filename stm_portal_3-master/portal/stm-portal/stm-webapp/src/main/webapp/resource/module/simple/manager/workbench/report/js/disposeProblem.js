$(function(){
	var $dp = $('#disposeProblem'),$showDivArr=$('.showdiv01,.showdiv,.showdiv02,.showdiv03'),$disposeTips=$('#disposeTips');
	$dp.find('#pople_p').on('click',function(){
		$dp.find('.showdiv').show();
		$dp.find('.showdiv03').hide();
		$disposeTips.text('对于人员问题来说，我们多数是通过内部的KPI和外部的SLA对人员的问题进行处理，如果设备的维护是由自身的人员进行的，建议通过内部KPI处理。如果设备外包给其它人员进行维护，建议能守外部SLA处理。');
	});
	$dp.find('#p_01').on('click',function(){
		$dp.find('.showdiv01').show();
		$dp.find('.showdiv02').hide();
		$disposeTips.text('内部的KPI一般包括培训流程、轮岗流程、招聘流程和辞退流程。针对新员工或新设备人员均不熟悉的情况，建议启动培训流程;如果是由于企业内部各部门的内耗导致问题的出现或者是磨合团队的协作配合能力，建议启动轮岗流程;由于人手不足的原因导致问题的出现，建议启动招聘流程;如果由于人员自身问题导致问题的出现，建议启动辞退流程。');
	});
	$dp.find('#p_02').on('click',function(){
		$dp.find('.showdiv01').hide();
		$dp.find('.showdiv02').show();
		$disposeTips.text('外部的SLA包括变更服务商流程和合同违约流程，如果人员的问题是由于服务商提供的人员不能满足要求，建议启动变更服务商流程，如果问题是由于服务商无法按合同的要求提供服务，建议启动合同违约流程。');
	});
	$dp.find('#implement_p').on('click',function(){
		$showDivArr.hide();
		$dp.find('.showdiv03').show();
		$disposeTips.text('设备问题包括更换流程、报修流程、报废流程、采购流程。一般情况下，如果设备自身性能导致问题出现且库存中有符合要求的设备，建议启动更换流程;如果设备自身的故障导致问题出现，建议启动报修流程;如果设备自身的故障问题且设备超过年限无法再次使用，建议启动报废流程;如果设备自身性能导致问题出现且无库存，建议启动采购流程。');
	});
})