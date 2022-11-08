$(function (){
	
	var datagridDiv=$('<div/>');
	var dataGrid;
	
	var dataGridCfg = {
			selector:datagridDiv,
			fit:false,
			width:'100%',
			pagination:false,
			checkOnSelect:false,
			columns:[[
		         {field:'monitor',title:'<input type="checkbox" name="monitor" value=2>监控',align:'center',width:'50px'},
		         {field:'alarm',title:'<input type="checkbox" name="alarm" value=3>告警',align:'center',width:'50px'},
		         {field:'meticsName',title:'指标',align:'center',width:'240px'},
		         {field:'monitorFrequency',title:'监控频度',align:'center',width:'120px'},
		         {field:'metricThresholds',title:'阈值定义',align:'center',width:'170px'},
		         {field:'alarmFlapping',title:'告警Flapping',align:'center',width:'80px'}
		     ]]
			
		};

	
});