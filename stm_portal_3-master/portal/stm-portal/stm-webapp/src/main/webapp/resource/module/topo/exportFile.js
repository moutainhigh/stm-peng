function ExportFile(args){};

ExportFile.prototype={
	exportFile:function(urlParam){
		//导出文件，ajax无法接受流数据，故用此方法实现 
		window.location.href = this.getUrl()+urlParam;
	},
	getUrl:function(urlParam){	//获取访问地址
		var curPath = window.document.location.href; 
		var pathName = window.document.location.pathname; 
		var pos = curPath.indexOf(pathName);
		var localhostPaht = curPath.substring(0,pos); 
//		var projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1); 
//		var newPath = localhostPaht + projectName;
		var newPath = localhostPaht;
		
		return newPath;
	}
}