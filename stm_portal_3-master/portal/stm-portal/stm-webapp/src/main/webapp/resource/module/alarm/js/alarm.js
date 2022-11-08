(function($){
	
	var alarm=$('#alarm').attr('id',oc.util.generateId());
	var form=alarm.find('.oc-toolbar').form({
		url:'',
		onSubmit:function(){
			
		},
		success:function(data){
			
		}
	});
	
	form.find('[name=sourceType]').combobox({
		width:180,
		data:[
		    {value:'1',text:'主机'},
		    {value:'2',text:'网络'},
		    {value:'3',text:'应用'},
		    {value:'4',text:'基础服务'}
      ]
	});
	
	alarm.find('table').datagrid({   
//	    url:'datagrid_data.json',  
	    columns:[[
	        {field:'code',title:'Code',width:100},   
	        {field:'name',title:'Name',width:100},   
	        {field:'price',title:'Price',width:100,align:'right'}   
	    ]]   
	});
})(jQuery);