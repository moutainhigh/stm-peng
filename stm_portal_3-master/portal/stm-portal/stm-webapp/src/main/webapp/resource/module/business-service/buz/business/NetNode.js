buzdefine("bz/NetNode",["bz/Node","bz/extend"],function(Node,extend){
	return extend(function(args){
		args=$.extend({
			attr:{
				x:0,y:0,w:50,h:60,src:"img/business/10_dbserver.png"
			}
		},args);
		
		if(!args.drawer && !args.bizCfg || !args.topoSVG){
    		throw "drawer can't be null";
    	}

    	this.topoSVG = args.topoSVG;

    	this.bizCfg=args.bizCfg;
		Node.apply(this,arguments);
		
	},Node,{
		init:function(){

			var drawer = this.args.drawer;
			this.node = drawer.group();
			this.links = [];
			with(this){
				this.node.fg = node.rect(1,1).attr({
					"fill-opacity":0,
					//"stroke":"#D0C5C5"
				});
				this.node.gdawer=node.group();
				regEvent();
			}
		},
		combineLink:function(link){
			var hasId = false;
			for(var i=0;i<this.links.length;i++){
                var tl = this.links[i];
                if(tl.data.id == link.data.id){
                    hasId = true;
                }
            }
            if(!hasId)
				this.links.push(link);
		},
		detachLink:function(linkId){
			var nlinks = [];
            for(var i=0;i<this.links.length;i++){
                var tl = this.links[i];
                if(tl.data.id != linkId){
                    nlinks.push(tl);
                }
            }
            this.links = nlinks;
		},
		regEvent:function(){
			var ctx = this;
			this.node.on("dragmove",function(){
				ctx.exe("dragmove",arguments);
			});
			
			//根据需要在这里可以作可编辑控制
			var editAble = this.bizCfg.get("editAble");
			if(editAble){
				buzrequire(["bz/NodeControl"],function(NodeControl){
					if(!ctx.handler)
					ctx.handler = new NodeControl({topoSVG:ctx.topoSVG,ctx:ctx,drawer:ctx.args.drawer});
				});
			}else{
				ctx.node.gdawer.on('mousedown',function(e1){
	                stopEvent(e1);
	                stopBubble(e1);
	                if(e1.which == 3){
	                    
	                    document.oncontextmenu = function()
	                    {   
	                        ctx.topoSVG.businessView.showRightMenu({mouse:e1,ctx:ctx,type:'node'});
	                        document.oncontextmenu=undefined;
	                        return false;
	                    }
	                    return;
	                }
	            });

	            ctx.node.gdawer.on('dblclick',function(e){
	            	$panel = ctx.topoSVG.args.root;
	            	$panel.css("cursor","");
	            	ctx.topoSVG.showDetail(ctx);	
	            });

	            ctx.node.gdawer.on('mouseover',function(e){
	            	$(this.node).css("cursor","pointer");
		            ctx.topoSVG.showToolTip(ctx,e);
	            });

	            ctx.node.gdawer.on('mouseout',function(e){
	            	$(this.node).css("cursor","");
	                ctx.topoSVG.closeTooltip(ctx,e);
	            });
			}
		},
		getValue:function(){
			var rbox=this.get_rbox();
			var bbox=this.node.img.bbox();
			var data=this.get_data();
			data.attr.x=rbox.x;
			data.attr.y=rbox.y;
			data.attr.w=bbox.w;
			data.attr.h=bbox.h;
			data.attr.zIndex = this.node.position();
			return data;
		},
		setValue:function(info){

			this.set_data(info);
			if(info.attr.src){
				if(!this.node.img)
					this.node.img= this.node.gdawer.image("",0,0).attr("preserveAspectRatio","none");
				this.node.img.size(info.attr.w,info.attr.h);
				this.node.img.load(info.attr.src);
			}

			if(info.attr.statusImg){
				if(!this.node.statusImg)
					this.node.statusImg= this.node.gdawer.image("",0,0).attr("preserveAspectRatio","none");
				this.node.statusImg.load(info.attr.statusImg);
				var siw = 26,sih = 26;
				var wscal = 1+(info.attr.w - 50)/50;
				var hscal = 1+(info.attr.h - 50)/50;
				
				siw=wscal*siw;
				sih=hscal*sih;

				this.node.statusImg.size(siw,sih).move(info.attr.w-siw, info.attr.h-sih);
			}

			if(!this.node.txt)
				this.node.txt = this.node.text("").attr("text-anchor","middle");

			if(info.showName){
				var showName = info.showName;
				if(info.nameHidden ==1){
					showName = "";
				}
				this.set_title(showName);
			}
			this.refresh();
		},
		set_title:function(title){
			if(title){
				var data=this.get_data(),
					attr=data.attr;
				$(this.node.txt.node).text(title);
			}
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
			var attr = this.data.attr;
			var tbox = this.node.txt.bbox();
			var w = (tbox.w>attr.w?tbox.w:attr.w)+10;
			this.node.txt.move(attr.w/2,attr.h);
			this.node.fg.size(w,attr.h+tbox.h+20).move((attr.w-w)/2,-10);

			this.node.img.size(attr.w,attr.h);
			this.node.move(attr.x,attr.y);
			
			var links = this.links;
			if(links){ 
                for (var i = 0; i < links.length; i++) {
                    links[i].refresh();
                }
            }
            //fontStyle = {'font-size':'12',fill:'pink'}
            if(this.data.attr.fontStyle)
            	this.node.txt.attr(this.data.attr.fontStyle);
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