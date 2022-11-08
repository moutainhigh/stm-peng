$(function(){
	var table = $("#deviceTypeTB");
	var vd={},models = [],threedInfo = {},products ={};
	ajaxData(oc.resource.getUrl("portal/3d/model/getAll.htm"),vd,models),
	ajaxData(oc.resource.getUrl('portal/3d/url/get3DInfo.htm'),vd,null,threedInfo),
	ajaxData(oc.resource.getUrl("portal/3d/model/getProductInfo.htm"),vd,null,null,products);
	models = vd.models,threedInfo=vd.threedInfo,products=vd.products;
	if(models&&models.length>0){
		var tr;
		for(var i=0;i<models.length;i++){
			if(i%3==0||i==0){
				tr = $("<tr/>");
				table.find("#deviceTypeTBody").append(tr);
			}
			if(products==undefined || products==null) return;
			var product = products[models[i].model],picSrc="",model="",brand="",desc="";
			if(product){
				picSrc = threedInfo.picturePath + product['图片'];
				model = product['型号'];
				brand = product['品牌'];
				desc = product['描述']||desc;
			}
			tr.append($('<td width="33%">'
					+ '<div class="newsys-box" style="height:145px;  width:99%;">'
					+ '<div class="sys-boxtitle">'
					+ '<span type="' + models[i].type + '">资源类型：' + models[i].type + '</span>'
					+ '<a class="setDeaultImgBtn oc-pointer-operate ico ico-greenrelevance locate-right margin-top5" title="点我设置型号"></a>'
					+ '</div>'
					+ '<div class="sys-boxcon">'
			       	+ '<table class="td_b_no" width="100%" border="0" cellpadding="0" cellspacing="0">'
			        + '<tbody>'
			        + '<tr>'
			        + '<td rowspan="3" width="27%">'
			        + '<div class="sys-boxpic">'
			        + '<img src="' + picSrc + '" width="76" height="86" /></div></td>'
			        + '<td height="33" width="73%" align="left" title="' + model
			        + '" model="' + model + '"> 设备型号：' + limitLength(model,14) + ' </td>'
			        + '</tr>'
			        + '<tr>'
			        + '<td height="33" align="left" title="' + brand + '">厂商：' + limitLength(brand,20) + '</td>'
			        + '</tr>'
			        + '<tr>'
			        + '<td height="33" align="left" title="' + desc + '">描述：' + limitLength(desc,20) + '</td>'
			        + '</tr>'
			   		+ '</tbody>'
					+ '</table>'
					+ '</div>'
					+ '</div>'
					+ '</td>'
					+''));
		}
	}else{
		return;
	}
	table.find(".setDeaultImgBtn").on('click',function(e){
		e.stopPropagation();
		var type = $(this).prev("span").attr("type"),
			model = $(this).parent().next().find("td[model]").attr("model");
		oc.resource.loadScript('resource/module/3d/device/js/devicetype.js?t='+new Date(),function(){
			oc.module.thirdd.resource.devicetype.open({self:{type:type,model:model},
				threedInfo:threedInfo,products:products});
		});
	});
	/**
	 * ajax请求数据
	 */
	function ajaxData(url,vd,models,threedInfo,products){
		oc.util.ajax({
			url : url,
			successMsg : null,
			async:false,
			timeout:300000,
			success : function(data) {
				if(data.code&&data.code==200){
					if(models!=null) vd.models = data.data;
					if(threedInfo!=null) vd.threedInfo = data.data;
					if(products!=null) vd.products = JSON.parse(data.data).data;
				}else{alert(data.data);}
			}
		});
	}
	
	/**
	 * 字符串处理函数
	 */
	function limitLength(value, byteLength) { 
		if(value==undefined || value==null || value.toString().trim()=="") return "";
		var newvalue = value.toString()&&value.toString().replace(/[^\x00-\xff]/g, "**"); 
		var length = newvalue.length; 
	    //当填写的字节数小于设置的字节数 
	    if (length * 1 <=byteLength * 1){ 
	    	return value; 
	    } 
	    var limitDate = newvalue.substr(0, byteLength); 
	    var count = 0; 
	    var limitvalue = ""; 
	    for (var i = 0; i < limitDate.length; i++) { 
	    	var flat = limitDate.substr(i, 1); 
	    	if (flat == "*") { 
	    		count++; 
	        } 
	    } 
	    var size = 0; 
	    var istar = newvalue.substr(byteLength * 1 - 1, 1);//校验点是否为“×” 
	    //if 基点是×; 判断在基点内有×为偶数还是奇数  
	    if (count % 2 == 0) { 
	    	//当为偶数时 
	        size = count / 2 + (byteLength * 1 - count); 
	        limitvalue = value.substr(0, size)+"..."; 
	    } else { 
	    	//当为奇数时 
	        size = (count - 1) / 2 + (byteLength * 1 - count); 
	        limitvalue = value.substr(0, size)+"..."; 
	    } 
	    return limitvalue; 
	}
})
