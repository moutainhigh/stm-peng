function Layout(args){
	this.args=args;
	this.init();
	this.treeNodes={};
	this.util = new TopoUtil(args);
	this.svgUtil=new TopoSvg(args);
};
Layout.prototype={
	//初始化
	init:function(){
		
	},
	//将指定节点平移到屏幕中间
	viewToCenter:function(el){
		if(el){
			var w = this.args.width,h = this.args.height;
			var pos= el.getPos();
			var vb = this.util.getViewBox();
			//现对于当前左上角的边距
			var rx=pos.cx-vb.x;
			var ry=pos.cy-vb.y;
			//计算当前应该偏移多少
			if(rx<=w/2) rx=vb.x-(w/2-rx);
			if(rx>w/2) rx=vb.x+(rx-w/2);
			if(ry<=h/2) ry=vb.y-(h/2-ry);
			if(ry>h/2) ry=vb.y+(ry-h/2);
			this.args.paper.setViewBox(rx,ry,vb.w,vb.h);
			//将该节点选中
			eve("oc_topo_image_checked",el);
		}
	},
	//将一节点设置到屏幕中间
	toCenter:function(el){
		var viewBox = this.svgUtil.getViewBox();
		if(el){
			var pos= el.getPos();
			//更新各个节点到相应的位置处，修正中心点位置
			pos.x=viewBox.cx-pos.w/2;
			pos.y=viewBox.cy-pos.h/2;
			el.refresh(pos);
		}
	},
	//解决chrome图片不显示的问题
	_fixChromeBug:function(){
		var vb = this.util.getViewBox();
		this.args.paper.setViewBox(vb.x+1,vb.y+1,vb.w,vb.h);
	},
	//星状布局
	star:function(args){
		args = $.extend({
			r:100,//内半径
			rr:100,//递增半径
			per:Math.PI/6,//内圈递增角度，外圈层按照这个角度进行减少，以便防止更多的节点
			callBack:function(){}
		},args);
		//中心参考节点
		var cel = args.refEl;
		//获取连接数最多的节点，把它作为中心点
		var tmpMax=-1;
		$.each(this.args.data,function(key,el){
			if(el.connects && tmpMax<el.connects.length){
				tmpMax=el.connects.length;
				cel=el;
			}
		});
		//将参考点放置到屏幕中间
		if(!cel) return ;
		this.toCenter(cel);
		var cp = {},count=0,ctx = this,r=args.r,level=0,_tp=cel.getPos();
		cp.x=_tp.cx,cp.y=_tp.cy;
		//计算位置
		$.each(this.args.data,function(key,el){
			if(key.indexOf("node")>=0 && el.d.id!=cel.d.id && !el.groupFlag){
				var angle = (args.per)*count;
				//mod用来判断是否加层
				if(angle>=(2*Math.PI)){
					r += args.rr;
					args.per=args.per*0.75;
					count=0;
					level++;
				}
				//奇数层错个空隙
				if(level%2==1){
					angle += args.per/2;
				}
				var pos= el.getPos();
				//更新各个节点到相应的位置处，修正中心点位置
				pos.x=cp.x+Math.cos(angle)*r-pos.w/2,pos.y=cp.y+Math.sin(angle)*r-pos.h/2;
				el.refresh(pos);
				count++;
			}
		});
		this._fixChromeBug();
		//调用回调
		args.callBack.call(this);
	},
	tree:function(){
		//查找根
		function findRoot(els){
			var tmpMax=-1,retnEl=null;
			$.each(els,function(key,el){
				if(el.connects){
					if(tmpMax<el.connects.length){
						retnEl=el;
						tmpMax=el.connects.length;
					}
				}else{
					retnEl=el;
					tmpMax=0;
				}
			});
			//已经用过的标记
			retnEl.treeFlag=true;
			return retnEl;
		}
		//第一层
		var level=[];
		level[0]=[findRoot(this.args.data)];
		//分层
		function devideLevel(le){
			var items=level[le];//当前层元素
			var toLevel = level[le+1] = [];
			//分析当前层
			$.each(items,function(key,item){
				var conns = items.connects;
				if(conns){
					for(var i=0;i<conns.length;++i){
						var con = conns[i];
						//保存连线的节点
						if(!con.toNode.treeFlag){
							con.toNode.treeFlag=true;
							toLevel.push(con.toNode);
						}
						if(!con.fromNode.treeFlag){
							con.fromNode.treeFlag=true;
							toLevel.push(con.fromNode);
						}
					}
				}
			});
			//是否进行下一层分析
			if(toLevel.length>0){
				devideLevel(le+1);
			}
		}
		//开始分层
		devideLevel(0);
		//那些孤立的没有链路的节点
		var lastLevel=[];
		$.each(this.args.data,function(key,el){
			if(!el.treeFlag){
				lastLevel.push(el);
			}
		});
		level[level.length]=lastLevel;
	},
	//网格布局
	grid:function(args){
		args = $.extend({
			r:60
		},args);
		var ref=args.refEl,length=0;
		$.each(this.args.data,function(key,el){
			if(el.d.id!=ref.d.id && el.d.id.indexOf("node")>=0){
				length++;
			}
		});
		//计算多少行，多少列
		var row=0,col=1,colsLen = Math.floor(Math.sqrt(length+1));
		//计算参考点的初始位置
		var viewBox = this.svgUtil.getViewBox(),
			dis = colsLen*(args.r/2);
		
		var position=$.extend(ref.getPos(),{
			x:viewBox.cx-dis,
			y:viewBox.cy-dis
		});
		ref.refresh(position);
		var p = ref.getPos();
		
		$.each(this.args.data,function(key,el){
			if(el.d.id!=ref.d.id){
				if(el.getPos){
					var tp = el.getPos();
					tp.x=row*args.r+p.cx-tp.w/2;
					tp.y=col*args.r+p.cy-tp.h/2;
					el.refresh(tp);
					col++;
					if(col>=colsLen){
						row++;
						col=0;
					}
				}
			}
		});
	}
};