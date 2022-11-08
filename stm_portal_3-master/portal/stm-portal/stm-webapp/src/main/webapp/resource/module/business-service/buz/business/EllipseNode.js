buzdefine("bz/EllipseNode",["bz/Node","bz/extend"],function(Node,extend){
	return extend(function(args){
		args=$.extend({
			attr:{
				x:0,y:0,w:100,h:60,attr:{},type:'ellipse'
			}
		},args);

		if(!args.drawer || !args.bizCfg || !args.topoSVG){
    		throw "drawer can't be null";
    	}
    	
    	this.topoSVG = args.topoSVG;
    	this.bizCfg=args.bizCfg;
		Node.apply(this,arguments);
		
	},Node,{
		init:function(){
			var drawer = this.args.drawer;
			this.node = drawer.group();
			with(this){
				this.node.fg = node.rect(1,1).attr({
					"fill-opacity":0,
					//"stroke":"#000"
				});
				this.node.gdawer=node.group();
				this.node.ellipse= this.node.gdawer.ellipse(1,1);

				regEvent();
			}
		},
		regEvent:function(){
			var ctx = this;
			//根据需要在这里可以作可编辑控制
			var editAble = this.bizCfg.get("editAble");
			if(editAble){
				buzrequire(["bz/EllipseNodeControl"],function(EllipseNodeControl){
					if(!ctx.handler)
						ctx.handler = new EllipseNodeControl({topoSVG:ctx.topoSVG,ctx:ctx,drawer:ctx.args.drawer});
				});
			}

		},
		getValue:function(){
			var rbox=this.get_rbox();
			var bbox=this.node.ellipse.bbox();
			var data=this.get_data();
			this.data.attr.zIndex = this.node.position();

			return data;
		},
		setValue:function(info){
			this.set_data(info);
			this.refresh();
		},
		get_bbox:function(){
			return this.node.bbox();
		},
		set_data:function(info){
			this.data=info;
		},
		get_data:function(){
			return this.data;
		},
		refresh:function(){
			var x = this.data.attr.x
				y = this.data.attr.y,
				w = this.data.attr.w,
				h = this.data.attr.h;

			this.node.move(x,y);
			this.node.fg.size(w,h);
			
			this.node.ellipse.radius(w/2,h/2);
			this.node.ellipse.move(0,0);

			//this.node.fg.move(x,y);
			
			if(this.data.attr.bgStyle)
            	this.node.ellipse.attr(this.data.attr.bgStyle);


		},
		get_rbox:function(){
			var rb = this.node.rbox();
			
			//因为有transform，所以x，y的取值要是变换后的值才对
			rb.x = this.node.x();
			rb.y = this.node.y();

			return rb;
		}
	});
});