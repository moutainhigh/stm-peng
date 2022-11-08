function Physics(){}
Physics.prototype = {
	//间隔距离
	MARGIN:85,
	/**X递增的线性集合*/
	x_sort:function(cons){
		this.checkArgu(cons);
		for(var i=0;i<cons.length;i++){
			for(var j= i+1;j<cons.length;j++){
				if(cons[i].x > cons[j].x){
					var temp = cons[i];
					cons[i] = cons[j];
					cons[j] = temp;						
				}
			}
		}
	},
	/**Y递增的线性排序*/
	y_sort:function(cons){
		this.checkArgu(cons);
		for(var i=0;i<cons.length;i++){
			for(var j= i+1;j<cons.length;j++){
				if(cons[i].y > cons[j].y){
					var temp = cons[i];
					cons[i] = cons[j];
					cons[j] = temp;						
				}
			}
		}
	},
	/**垂直对齐,参照物为最上面容器*/
	x_align:function(cons){
		this.checkArgu(cons);
		this.y_sort(cons);
		var x = cons[0].x;
		for(var i=0;i<cons.length;i++){
			cons[i].drag(0,0,x,cons[i].y);
		}
	},
	/**水平对齐,参照物为最左边容器*/
	y_align:function(cons){
		this.checkArgu(cons);
		this.x_sort(cons);
		var y = cons[0].y;
		for(var i=0;i<cons.length;i++){
			cons[i].drag(0,0,cons[i].x,y);
		}
	},
	/**垂直排列,参照物为最上面容器*/
	x_rank:function(cons){
		this.checkArgu(cons);
		this.y_sort(cons);
		var x = cons[0].x,y = cons[0].y;
		for(var i=0;i<cons.length;i++){
			cons[i].drag(0,0,x,y);
			y += cons[i].h+this.MARGIN;
		}
	},
	/**水平排列,参照物为最左边容器*/
	y_rank:function(cons){
		this.checkArgu(cons);
		this.x_sort(cons);
		var x = cons[0].x,y = cons[0].y;
		for(var i=0;i<cons.length;i++){
			cons[i].drag(0,0,x,y);
			x += cons[i].w+this.MARGIN;
		}
	},
	/**水平中轴排列,给定中轴线往两边派生,参照物为中轴元素(最好宽度都一致)*/
	axis_rank:function(cons,axis,rowCount,height){
		this.checkArgu(cons);
		this.x_sort(cons);
		var margin = this.MARGIN,x_axis,y,offset = 0;
		if(cons.length%2==0){
			var half = cons.length/2,prev = half -1;
			y = (cons[half].y + cons[prev].y)/2;
			offset = (cons[half].w+margin)/2;
			x_axis = cons[half].x + cons[half].w/2;
		}else{
			var half = Math.floor(cons.length/2);
			y = cons[half].y;
			x_axis = cons[half].x+cons[half].w/2;		
		}
		if(axis){
			x_axis = axis;
		}else offset = 0;
		for(var i=0;i<cons.length;i++){
			var x = x_axis+(i-half)*(margin+cons[i].w)-cons[i].w/2+offset;
			var newY = y;
			if(rowCount && rowCount > 1){
				
				//偏移最大倍数
				var offsetMaxMultiple = Math.floor(rowCount / 2);
				//多行，进行错位排列
				if(rowCount % 2 == 1 && i % rowCount == (rowCount + 1) / 2){
					//中间一行，不需要调整y坐标
					newY = y;
				}else{
					//从中心线区分Y轴偏移方向
					if(i % rowCount <= rowCount / 2 && i % rowCount > 0){
						var offsetMultiple = offsetMaxMultiple - i % rowCount + 1;
						if(rowCount % 2 == 0){
							newY = y + offsetMultiple * height - height / 2;
						}else{
							newY = y + offsetMultiple * height;
						}
					}else{
						var offsetMultiple = null;
						if(i % rowCount == 0){
							offsetMultiple = -offsetMaxMultiple;
						}else{
							offsetMultiple = Math.ceil(rowCount / 2) - i % rowCount;
						}
						if(rowCount % 2 == 0){
							newY = y + offsetMultiple * height + height / 2;
						}else{
							newY = y + offsetMultiple * height;
						}
					}
					
				}
			}
			cons[i].drag(0,0,x,newY);
		}
	},
	checkArgu:function(cons){
		if(!(cons instanceof Array)) throw "the first argument must be Array";
		if(cons.length==0) throw "the array can't be empty";
	}
}