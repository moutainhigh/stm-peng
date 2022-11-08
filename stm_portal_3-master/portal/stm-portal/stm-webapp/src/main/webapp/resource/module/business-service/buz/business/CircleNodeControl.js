buzdefine("bz/CircleNodeControl", function () {
    var f = function(args){
        this.args=$.extend({
            ctx:null
        },args);
       
        if(!this.args.ctx || !args.topoSVG){
            throw "drawer can't be null";
        }
        this.topoSVG = args.topoSVG;

        this.init();
    };
    f.prototype = {
        init:function(){
            var _this = this;
            this.regEvent();
            var ctx = this.args.ctx;
            buzrequire(["bz/ShapeControl"],function(ShapeControl){
                ctx.ShapeControl = new ShapeControl({
                    topoSVG:_this.topoSVG,
                    ctx:ctx,
                    drawer:ctx.args.drawer,
                    id:'shape_controls_'+ctx.data.id,
                    selectType:'single'
                    });
                _this.ShapeControl = ctx.ShapeControl;
            });
        },
    	set_ctx:function(ctx){
    		this.ctx=ctx;
    	},
    	get_ctx:function(ctx){
    		return this.ctx;
    	},
        attr:function(field,value){
            if(field){
                if(field instanceof Object){
                    for(var k in field){
                        this._attr(k,field[k]);
                    }
                }else{
                     return this._attr(field,value);
                }
            }
        },
        _attr:function(field,value){
            if(value==undefined && this["get_"+field]){
                return this["get_"+field].call(this);
            }
            if(value!=undefined  && this["set_"+field]){
                this["set_"+field].call(this,value);
            }
            return undefined;
        },
        regEvent:function(){
            var ctx = this.args.ctx;
            var _this = this;
            
            ctx.node.on('mousedown',function(e){
                stopEvent(e);
                stopBubble(e);

                ctx.ShapeControl.showShapeControls(e);
                ctx.ShapeControl.setShowContour(false);
                ctx.ShapeControl.dragable(e);

                if(e.which == 3){
                    
                    document.oncontextmenu = function()
                    {   
                        _this.topoSVG.businessView.showRightMenu({mouse:e,ctx:ctx,type:'node'});
                        document.oncontextmenu=undefined;
                        return false;
                    }
                    return;
                }
            });

            ctx.node.circle.on('mousedown',function(e){
                if(!ctx.ShapeControl){
                    require(["bz/ShapeControl"],function(ShapeControl){

                       ctx.ShapeControl = new ShapeControl({
                            ctx:ctx,
                            drawer:ctx.args.drawer,
                            id:'shape_controls_'+ctx.data.id,
                            selectType:'single'
                            });
                        ctx.ShapeControl.showShapeControls(e);
                        ctx.ShapeControl.setShowContour(false);
                        ctx.ShapeControl.dragable(e);
                    });
                }else{
                    ctx.ShapeControl.showShapeControls(e);
                    ctx.ShapeControl.dragable(e);
                }
            });
        }
    };
    return f;
});