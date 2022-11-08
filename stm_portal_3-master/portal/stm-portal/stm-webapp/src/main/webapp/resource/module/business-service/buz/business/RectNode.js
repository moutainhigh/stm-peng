buzdefine("bz/RectNode",["bz/Node","bz/extend"],function(Node,extend){
	return extend(function(args){
		args=$.extend({
			attr:{
				x:0,y:0,w:100,h:60,attr:{},type:'rect'
			}
		},args);
		
		if(!args.drawer || !args.bizCfg || !args.topoSVG){
    		throw "drawer can't be null";
    	}

    	this.topoSVG = args.topoSVG;
    	this.bizCfg = args.bizCfg;

		Node.apply(this,arguments);

	},Node,{
		init:function(){
			var drawer = this.args.drawer;
			this.node = drawer.group();
			with(this){
				this.node.fg = node.rect(1,1).attr({
					"fill-opacity":0,
					//"stroke":"#D0C5C5"
				});
				this.node.gdawer=node.group();
				this.node.rect= this.node.gdawer.rect(1,1);

				regEvent();
			}
		},
		regEvent:function(){
			var ctx = this;

			//根据需要在这里可以作可编辑控制
			var editAble = this.bizCfg.get("editAble");
			if(editAble){
				buzrequire(["bz/RectNodeControl"],function(RectNodeControl){
					if(!ctx.handler)
						ctx.handler = new RectNodeControl({topoSVG:ctx.topoSVG,ctx:ctx,drawer:ctx.args.drawer});
				});
			}

		},
		getValue:function(){
			var rbox=this.get_rbox();
			var bbox=this.node.rect.bbox();
			
			var data=this.get_data();
			data.attr.zIndex = this.node.position();

			//data.attr.x=rbox.x;
			//data.attr.y=rbox.y;
			//data.attr.w=bbox.w;
			//data.attr.h=bbox.h;
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
				h = this.data.attr.h,
				r = this.data.attr.radius ? this.data.attr.radius:0;

			this.node.move(x,y);
			this.node.rect.size(w,h);
			this.node.fg.size(w,h);
			//this.node.fg.move(x,y);
			if(r>0){
				this.node.rect.radius(r);
			}

			if(this.data.attr.bgStyle)
            	this.node.rect.attr(this.data.attr.bgStyle);

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