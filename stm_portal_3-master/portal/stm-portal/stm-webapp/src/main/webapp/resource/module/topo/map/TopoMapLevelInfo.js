function TopoMapLevelInfo(args){
	this.args=$.extend({
		holder:null,
		onLoad:function(){}
	},args);
	var ctx = this;
	if(this.args.holder){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/map/TopoMapLevelInfo.html"),
			success:function(htl){
				ctx.init(htl,args.key);
			},
			type:"get",
			dataType:"html"
		});
	}else{
		throw "bad arguments";
	}
};
TopoMapLevelInfo.prototype={
	init:function(htl,key){
		this.root=$(htl);
		this.root.appendTo(this.args.holder);
		this.fields={};
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.args.onLoad.call(this);
		if(key){
			var text = '';
			key = parseInt(key);
			switch(key)
			{
			case 110000:
			  text = '北京';
			  break;
			case 120000:
			  text = '天津';
			  break;
			case 130000:
			  text = '河北';
			  break;
			case 140000:
			  text = '山西';
			  break;
			case 150000:
			  text = '内蒙古';
			  break;
			case 210000:
			  text = '辽宁';
			  break;
			case 220000:
			  text = '吉林';
			  break;
			case 230000:
			  text = '黑龙江';
			  break;
			case 310000:
			  text = '上海';
			  break;
			case 320000:
			  text = '江苏';
			  break;
			case 330000:
			  text = '浙江';
			  break;
			case 340000:
			  text = '安徽';
			  break;
			case 350000:
			  text = '福建';
			  break;
			case 360000:
			  text = '江西';
			  break;
			case 370000:
			  text = '山东';
			  break;
			case 410000:
			  text = '河南';
			  break;
			case 420000:
			  text = '湖北';
			  break;
			case 430000:
			  text = '湖南';
			  break;
			case 440000:
			  text = '广东';
			  break;
			case 450000:
			  text = '广西';
			  break;
			case 460000:
			  text = '海南';
			  break;
			case 460300:
			  text = '海南省三沙市';
			  break;
			case 500000:
			  text = '重庆';
			  break;
			case 510000:
			  text = '四川';
			  break;
			case 520000:
			  text = '贵州';
			  break;
			case 530000:
			  text = '云南';
			  break;
			case 540000:
			  text = '西藏';
			  break;
			case 610000:
			  text = '陕西';
			  break;
			case 620000:
			  text = '甘肃';
			  break;
			case 630000:
			  text = '青海';
			  break;
			case 640000:
			  text = '宁夏';
			  break;
			case 650000:
			  text = '新疆';
			  break;
			default:
			  text = '北京';
			}
			$('#topo_map_level_info_province_title').html(text);
		}
	},
	/**
	 * {
	 * 	level:2,
	 *  level1:{
	 *  	available:24,amount:45
	 *  },
	 *  level2:{
	 *  	available:156,amount:356
	 *  },
	 *  level3:{
	 *  	available:256,amount:456
	 *  },
	 *  level4:{
	 *  	available:564,amount:756
	 *  }
	 * }
	 */
	setValue:function(value){
		var fd = this.fields;
		fd.level1.hide();
		//隐藏高层
		for(var i=1;i<value.level;i++){
			fd["level"+i].hide();
		}
		//设置值
		for(var i=value.level;i<=4;i++){
			fd["level"+i].show();
			this.fillLevel(fd["level"+i],value["level"+i],i);
		}
	},
	fillLevel:function(levelDom,v,l){
		if(oc.topo.util.module=="GF"){
			if(l==2){
				levelDom.find(".amount").text("412");
			}else if(l==3){
				levelDom.find(".amount").text("3068");
			}else{
				levelDom.find(".amount").text(v.amount);
			}
		}else{
			levelDom.find(".amount").text(v.amount);
		}
		levelDom.find(".available").text(v.available);
	}
};