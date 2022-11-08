function SSHDia(args){
	var dia = $("<div></div>");
	var iframe = $("<iframe style='border:none;width:100%;height:400px;overflow-y:auto;'></iframe>");
	iframe.attr("src","module/resource-management/resourceDetailInfo/tinytool/ssh.jsp?host="+args.node.d.ip);
	dia.append(iframe);
	dia.dialog({
		width:700,height:450,
		title:"SSH &nbsp;&nbsp;"+args.node.d.ip
	});
};