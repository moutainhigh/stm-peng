/**
 * 配置信息
 */
buzdefine("bz/Cfg",[],function(){
	var cfg = function(args){
		this.args=$.extend({
    		root:null//画布
    	},args);
		
		this.default ={
			node:{
				attr:{
					fontStyle:{
						fill: "#fff",
						'font-color':'#fff',
						'font-size':'12',
						'font-weight':'normal'
					},
					bgStyle:{
						fill: "#fff",
						'fill-opacity': 0.5,
						stroke: "#000"
					}
				}
			},
			link:{
				attr:{
					attr:{
						//'stroke-dasharray':'8,8',//实线或者虚线
						'stroke-width':'2',//线宽
						'stroke':'#666668'//线颜色
					},
					type:'poly', //直线或折线
					arrow:'single-arrow',//是否有箭头：无箭头、单箭头、双箭头
				}
			},
			panel:{
				attr:{
					sytle:{
						'background-color':'white',
						'background-img':'url()'
					}
				}
			},
			editAble:true,
			showControlPanel:true
		}
		if(args.showControlPanel != undefined){
		 	this.default.showControlPanel = args.showControlPanel;
		}

		if(args.editAble != undefined){
		 	this.default.editAble = args.editAble;
		}

		this.topoSVG = args.topoSVG,

		this.init();
	};
	
	cfg.prototype = {
		init:function(){
			this.currentP = $.extend(true,{},this.default);
			this.selected = {
				links:{},
				nodes:{}
			};
			this.tools = {
				drawTextBox:false//标识当前是否选中了文本框绘制按钮
			};
			this.shapeControls = [];
			this.panelTools = null;
		},
		getBaseOffset:function(){
			return this.args.root.parent('.business-buz').offset();//获取画布的基础位置
		},
		addPanelTools:function(pt){
			this.panelTools = pt;
		},
		addSelected:function(type,id,obj){
			if(!this.selected[type]){
				this.selected[type] = {};
			}
			if(!this.selected[type][id]){
				this.selected[type][id]=obj;
				if(this.panelTools)
					this.panelTools.updateStatus();
			}
		},
		removeSelected:function(type,id){
			if(!this.selected[type]){
				return;
			}
			if(!id){
				this.selected[type] = {};
			}else{
				delete this.selected[type][id];
			}
			
		},
		set:function(name,value) {
			this.currentP[name] = value;
		},
		get:function(name){
			return this.currentP[name];
		},
		restore:function(){
			this.currentP = $.extend(true,{},this.default);
		}
		
	}
	
	return cfg;
});
