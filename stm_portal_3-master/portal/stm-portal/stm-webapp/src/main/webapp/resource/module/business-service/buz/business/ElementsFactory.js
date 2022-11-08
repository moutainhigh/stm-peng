/**
 * 元素工厂
 */
buzdefine("bz/ElementsFactory", ["bz/NetNode","bz/TextNode","bz/RectNode","bz/CircleNode","bz/EllipseNode",
    "bz/NormalLink"], function (NetNode,TextNode,RectNode,CircleNode,EllipseNode,NormalLink) {
    var tmp = function (args) {
    	this.args=$.extend({
            topoSVG:null,
            bizCfg:null,
    		drawer:null
    	},args);
    	if(!args.drawer || !args.topoSVG){
    		throw "drawer can't be null";
    	}

        this.topoSVG = args.topoSVG;
        this.drawer = args.drawer;
        this.bizCfg = args.bizCfg;

    	this.linkDrawer = this.drawer; //this.args.drawer.group();
    	this.nodeDrawer = this.drawer; //this.args.drawer.group();
    };
    tmp.prototype = {
    	getNode:function(info){
            var nn = undefined;
            switch(info.attr.type){
                case 'net':{
                    nn = new NetNode({
                        topoSVG:this.topoSVG,
                        bizCfg:this.bizCfg,
                        drawer:this.nodeDrawer
                    });
                    break;
                }
                case 'textNode' :{
                    nn = new TextNode({
                        topoSVG:this.topoSVG,
                        bizCfg:this.bizCfg,
                        drawer:this.nodeDrawer
                    });
                    break;
                }
                case 'rect' :{
                    nn = new RectNode({
                        topoSVG:this.topoSVG,
                        bizCfg:this.bizCfg,
                        drawer:this.nodeDrawer
                    });
                    break;
                }
                case 'circle' :{
                    nn = new CircleNode({
                        topoSVG:this.topoSVG,
                        bizCfg:this.bizCfg,
                        drawer:this.nodeDrawer
                    });
                    break;
                }
                case 'ellipse' :{
                    nn = new EllipseNode({
                        topoSVG:this.topoSVG,
                        bizCfg:this.bizCfg,
                        drawer:this.nodeDrawer
                    });
                    break;
                }
            }
    		nn.setValue(info);
    		return nn;
    	},
    	getLink:function(info,nodes){
    		var lk=undefined;
            lk = new NormalLink({
                topoSVG:this.topoSVG,
                nodes:nodes,
                bizCfg:this.bizCfg,
                drawer:this.linkDrawer
            });
    		lk.setValue(info);
    		return lk;
    	},
        clearALL:function(){
            this.drawer.clear();
        }
    };
    return tmp;
});