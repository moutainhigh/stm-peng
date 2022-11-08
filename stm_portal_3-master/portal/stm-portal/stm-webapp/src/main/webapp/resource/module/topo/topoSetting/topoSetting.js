function TopoGlobalSettingDia() {
	var ctx = this;
	oc.util.ajax({
		url : oc.resource.getUrl("resource/module/topo/topoSetting/topoSetting.html"),
		type : "get",
		success : function(html) {
			oc.resource.loadScripts(["resource/module/topo/contextMenu/ColorPalette.js"],function(){
				ctx.init(html);
			});
		},
		dataType : "html"
	});
};
TopoGlobalSettingDia.prototype = {
	init : function(html) {
		var ctx = this;
		this.root = $(html);
		this.root.dialog({
			title : "拓扑全局设置",
			width : 600,
			height : 520,
			buttons : [ {
				text : "保存",
				handler : function() {
					ctx.save();
					ctx.root.dialog("close");
				}
			}, {
				text : "取消",
				handler : function() {
					ctx.root.dialog("close");
				}
			} ],
			onBeforeClose:function(){
				if(oc.topo.topo) oc.topo.topo.refresh();
			}
		});
		this.fields = {};
		// 搜索域
		this.root.find("[data-field]").each(function(idx, dom) {
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")] = tmp;
		});
		// 初始化accord
		this.fields.accord.accordion();
		// 初始化表单
		this.topoForm = new FormUtil(this.fields.topoForm);
		this.linkForm = new FormUtil(this.fields.linkForm);
		this.deviceForm = new FormUtil(this.fields.deviceForm);
		// 字体大小
		this.fields.topoFontSize.combobox({
			width : 100,
			editable : false
		});
		// 字体颜色
		this.fields.fontColorChoser.combobox({
			readonly : true,
			width : 100,
			editable : false
		});
		//图元尺寸大小
		this.fields.topoPelSize.combobox({
			width:100,
			panelHeight:110,
			editable:false,
			onSelect:function(record){
				var val = record.value;
				if(1==val || 1.5==val || 2==val){	//隐藏
					ctx.fields.pelSliderParent.hide();
				}else{	//显示
					ctx.fields.pelSliderParent.show();
				}
			}
		});
		
		//自定义图元拖拽
		this.fields.topoPelSlider.slider({
		    mode: 'h',
		    width:300,
		    showTip:true,
		    value:100,
		    max:200,
		    tipFormatter: function(value){    
		        return value + '%';
		    },
		    //改变大小后，重新设置图元参数
		    onChange:function(newValue,oldValue){
		    	//获取当前图元
		    	if(!newValue) return;
				var curNodes = oc.topo.topo.els;
		    	ctx._refreshNodeSize(curNodes,newValue/100);
		    }
		});
		// 刷新时间
		var freshData = [];
		for (var i = 1; i < 10; ++i) {
			freshData.push({
				id : i,
				text : i * 5 + "分钟",
				value : 5 * i * 60000
			});
		}
		this.fields.refreshTypeIpt.combobox({
			width : 100,
			valueField : "value",
			textField : "text",
			data : freshData,
			editable : false
		});
		this.regEvent();
		// 需要隐藏的
		this.fields.linkTagType.hide();
		/*this.fields.linkShowWayIpt.hide();*/
		this.fields.refreshTypeIpt.next().hide();
		// 加载已有配置
		$.post(oc.resource.getUrl("topo/setting/get/globalSetting.htm"),function(setting) {
			ctx.setValue($.extend({
				fontColor : "white"
			}, setting));
		},"json");
	},
	//刷新图元大小
	_refreshNodeSize:function(nodes,multiple){
		var baseWidth = 30,baseHeight = 30;
		$.each(nodes, function(index, node) {
			if (node.d && node.d.id.indexOf("node") >= 0) {
				var pos = node.getPos();
				//修改当前尺寸和位置
				var w=baseWidth*multiple,h=baseHeight*multiple,x=pos.x,y=pos.y;
				if(!multiple && 0!=multiple){
					w=node.w,h=node.h;
				}
				node.refresh({x:x,y:y,w:w,h:h});
			}
		});
	},
	regEvent : function() {
		var ctx = this;
		// 链路标注-无标注的时候隐藏标注类型
		this.fields.linkHasTag.on("change", "input", function() {
			var tmp = $(this);
			var val = tmp.val();
			if (val == "false") {
				ctx.fields.linkTagType.hide();
			} else {
				ctx.fields.linkTagType.fadeIn();
			}
		});
		// 标注类型只能选择俩
		this.fields.linkTagType.on("click", "input", function() {
			var length = ctx.fields.linkTagType.find("input:checked").length;
			if (length > 2) {
				alert("最多选择两项");
				$(this).prop("checked", false);
				return false;
			}
			return true;
		});
		// 链路显示方式-单链路显示时候隐藏输入框
		/*this.fields.linkShowWay.on("change", "input[type='radio']", function() {
			var tmp = $(this);
			var val = tmp.val();
			if (val == "single") {
				ctx.fields.linkShowWayIpt.hide();
			} else {
				ctx.fields.linkShowWayIpt.fadeIn();
			}
		});*/
		// 刷新方式-手动刷新隐藏输入框
		this.fields.refreshType.on("change", "input[type='radio']", function() {
			var tmp = $(this);
			var val = tmp.val();
			if (val == "manual") {
				ctx.fields.refreshTypeIpt.next().hide();
			} else {
				ctx.fields.refreshTypeIpt.next().fadeIn();
			}
		});
		// 字体颜色
		this.colorPicker = oc.topo.colorChoser;
		this.fields.fontColorChoser.next().on(
				"click",
				function(e) {
					ctx.colorPicker.show(e);
					ctx.colorPicker.root.css("z-index", parseInt(ctx.root.parent(".window").css("z-index")) + 1);
					ctx.colorPicker.onece("selected", function() {
						ctx.fields.fontColorFg.css("background-color", this.getValue().color);
						ctx.colorPicker.hide();
					});
				});
	},
	getValue : function() {
		var topo = this.topoForm.getValue();
		var link = this.linkForm.getValue();
		var device = this.deviceForm.getValue();
		var refreshTime = this.fields.refreshTypeIpt.combobox("getValue");
		var fontSize = parseInt(this.fields.topoFontSize.combobox("getValue"));
		topo.fontColorReserve = this.fields.fontColorReserve.prop("checked");
		topo.fontColor = this.fields.fontColorFg.css("background-color");
		topo.fontsizeReserve = this.fields.fontsizeReserve.prop("checked");
		//图元尺寸拖动
		topo.pelSize = this.fields.topoPelSize.combobox("getValue");
		if(-1 == topo.pelSize){
			topo.pelSize = this.fields.topoPelSlider.slider("getValue")/100;
		}
		if (fontSize) {
			topo.fontSize = fontSize;
		}
		if (refreshTime) {
			topo.refreshTime = parseInt(refreshTime);
		}else{
			topo.refreshTime = 300000;
		}
		return {
			topo : topo,
			link : link,
			device : device,
			effect : {
				flow : this.fields.enableFlowEffect.prop("checked")
			}
		};
	},
	setValue : function(value) {
		value = $.extend({
			"effect" : {
				"flow" : false
			},
			"link" : {
				"multiNo" : "",
				"singleSettingTag" : true,
				"colorWarning" : "device",
				"hasTag" : false,
				"showDownDirection" : false,
				"showType" : "single"
			},
			"topo" : {
				"fontColor" : "rgb(255, 255, 255)",
				"freshType" : "auto",
				"refreshTime":300000,
				"fontSize" : 10,
				"fontsizeReserve" : false,
				"fontColorReserve" : false,
				"showRes" : [ "net", "server" ]
			},
			"device" : {
				"colorWarning" : "device",
				"tagField" : "ip"
			}
		}, value);
		if (value.topo) {
			this.topoForm.setValue(value.topo);
			var multiple = value.topo.pelSize || 1;
			this.fields.topoPelSize.combobox("setValue",multiple);
			if(multiple !=1 && multiple!=1.5 && multiple!=2){
				this.fields.topoPelSize.combobox("setValue",-1);
				this.fields.pelSliderParent.show();
			}
			this.fields.topoPelSlider.slider("setValue",value.topo.pelSize*100);
		}
		if (value.link) {
			this.linkForm.setValue(value.link);
			if (value.link.hasTag) {
				this.fields.linkTagType.fadeIn();
			}
			if (value.link.showType == "multi") {
				this.fields.linkShowWayIpt.fadeIn();
			}
		}
		if (value.device) {
			this.deviceForm.setValue(value.device);
		}
		// 效果
		this.fields.enableFlowEffect.prop("checked", value.effect.flow);
		var topo = value.topo;
		if (topo) {
			// 拓扑的其他设置
			if (topo.fontSize) {
				this.fields.topoFontSize.combobox("setValue", topo.fontSize);
			}
			if (topo.freshType == "auto") {
				this.fields.refreshTypeIpt.next().show();
			}
			if (topo.refreshTime) {
				this.fields.refreshTypeIpt.combobox("setValue",
						parseInt(topo.refreshTime));
			}
			this.fields.fontsizeReserve.prop("checked",
					topo.fontsizeReserve == "true");
			this.fields.fontColorReserve.prop("checked", topo.fontColorReserve);
			this.fields.fontColorFg.css("background-color", topo.fontColor);
			this.fields.fontsizeReserve.prop("checked", topo.fontsizeReserve);
			// 拓扑图上要显示的资源
			if (topo.showRes) {
				var showRes = JSON.stringify(topo.showRes);
				this.fields.net.attr("checked", showRes.indexOf("net") >= 0);
				this.fields.server.attr("checked",
						showRes.indexOf("server") >= 0);
			}
		} else {// 初始化系统的时候：网络设备和服务器则全部显示
			this.fields.net.attr("checked", true);
			this.fields.server.attr("checked", true);
		}
	},
	save : function() {
		var value = this.getValue();
		oc.util.ajax({
			url : oc.resource.getUrl("topo/setting/save.htm"),
			data : {
				key : "globalSetting",
				value : JSON.stringify(this.getValue())
			},
			success : function(info) {
				alert(info.data);
				eve("topo.refresh", this, value);
			}
		});
	}
};