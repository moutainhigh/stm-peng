function FormatIp(args){};

FormatIp.prototype={
	formatter:function(value){
		var content = (value!=null)?value.split(","):new Array();
		var valTmp = "";
		if(content.length<=1){
			return value;
		}else{
			valTmp=content[0]+"..."
		};
		
		var contentTmp = "";
		for(var i=0;i<content.length;i++){
			if((i+1)%5 == 0){	//一行显示5个ip
				contentTmp += content[i]+"&#13;";
			}else{
				contentTmp += content[i]+"&nbsp;&nbsp;&nbsp;&nbsp;";
			}
		}
		
		return '<span title="' + contentTmp + '" class="easyui-tooltip">' + valTmp + '</span>';  
	}
}