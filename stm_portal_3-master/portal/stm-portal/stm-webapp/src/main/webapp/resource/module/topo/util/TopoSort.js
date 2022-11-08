function TopoSort(data){
	this.data=data;
	this.template = data[0];
}
TopoSort.prototype={
	check:function(field){
		if(this.template[field]==null){
			throw field+"未定义";
		}
	},
	desc:function(field){
		this.check(field);
		this.sort(field,false);
	},
	swap:function(i,j){
		var tmp=this.data[i];
		this.data[i]=this.data[j];
		this.data[j]=tmp;
	},
	getPrimitiveVal:function(val){
		try{
			return parseFloat(val);
		}catch(e){
			return val;
		}
	},
	/*
		可覆盖比较算法
	*/
	compare:function(a,b){
		return a>b?1:-1;
	},
	sort:function(field,isAsc){
		for(var i=0;i<this.data.length;i++){
			var valOuter = this.data[i][field];
			for(var j=i+1;j<this.data.length;j++){
				var valInner = this.data[j][field];
				var cp = this.compare(valOuter,valInner);
				if(cp==-1 && !isAsc){
					this.swap(i,j);
					valOuter=valInner;
				}else if(cp==1 && isAsc){
					this.swap(i,j);
					valOuter=valInner;
				}
			}
		}
	},
	asc:function(field){
		this.check(field);
		this.sort(field,true);
	},
	toString:function(){
		return JSON.stringify(this.data);
	}
};