var FormUtil = function(formDom){
	this.$dom=$(formDom);
	this.inputs={};
	var ctx = this;
	this.$dom.find("[name]").each(function(idx,dom){
		var $tmp=$(dom);
		var key = $tmp.attr("name");
		if(key=="") return;
		var type=$tmp.attr("type");
		if(type=="checkbox" || type=="radio"){
			if(!ctx.inputs[key]) ctx.inputs[key]={formUtilFlag:true};
			ctx.inputs[key][$tmp.val()]=$tmp;
		}else{
			ctx.inputs[key]=$tmp;
		}
	});
};
FormUtil.prototype.getValue=function(){
	var vals = {};
	$.each(this.inputs,function(key,obj){
		if(obj.formUtilFlag){
			vals[key]=[];
			$.each(obj,function(_key,r){
				if(_key!="formUtilFlag"&&r&&r.prop("checked")){
					var v = r.val();
					if(v=="true"){
						v=true;
					}else if(v=="false"){
						v=false
					}
					vals[key].push(v);
				}
			});
			if(vals[key].length==1){
				vals[key]=vals[key][0];
			}
			if(vals[key].length==0){
				delete vals[key];
			}
		}else{
			vals[key]=obj.val();
		}
	});
	return vals;
};
FormUtil.prototype.setValue=function(vals){
	var ctx = this;
	$.each(vals,function(key,obj){
		var tmp = ctx.inputs[key];
		if(tmp && tmp.formUtilFlag){
			if(obj instanceof Array){
				for(var i=0;i<obj.length;++i){
					tmp[obj[i]].attr("checked","checked");
				}
			}else{
				tmp[obj].attr("checked","checked");
			}
		}else if(tmp){
			tmp.val(obj);
		}
	});
};
Date.prototype.formate=function(formatter){
	return formatter.replace("y",this.getFullYear())
		.replace("M",this.getMonth())
		.replace("d",this.getDate())
		.replace("H",this.getHours())
		.replace("m",this.getMinutes())
		.replace("s",this.getSeconds())
		.replace("S",this.getMilliseconds());
};