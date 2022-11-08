Mapper = function(t, n) {
	
	oc.ns('oc.module.topo.map');
	
    var e= {
        fillColor: Highcharts.theme.topoMapfillColor,
        strokeColor: Highcharts.theme.topoMapstrokeColor,
        strokeWidth: 1,
        width: 0,
        height: 0,
        scale: 1,
        translate: [0, 0],
        fontSize: 11,
        fontColor: Highcharts.theme.topoMapfontColor,
        fontFamily: "微软雅黑",
        marginLeft: 0,
        marginTop: 0
    },
    errorCode = ['150784','152923','632801','632523','230102'],
	errorCodePointIds = {
    		'120.18895545769_50.248493334329':'150784',
    		'101.07819618026_41.965961244553':'152923',
    		'90.861843_38.254849':'632801',
    		'93.340752_38.743352':'632801',
    		'95.369282_37.855802':'632801',
    		'94.909022005708_36.406805165854':'632801',
    		'101.443736_36.04576':'632523',
    		'126.59154812073_45.896578570552':'230102',
    		'126.61759170425_45.845481805027':'230102',
    		'126.99225507991_45.554512606945':'230102',
    		'126.67812563799_45.592556957384':'230102',
    		'126.65038461638_45.720561885473':'230102',
    		'126.65723329759_45.792452252099':'230102',
    		'126.60934912238_45.756921898422':'230102',
    		'126.64411633036_45.765418632679':'230102',
    		'126.67983924558_45.765201278898':'230102',
    		'126.69598862272_45.749950749145':'230102',
    		'126.65038461638_45.720561885473':'230102'
    }
    curLevel=0,
    curKey=0,
    a = {
        handleLoadProvince: null,
        handleLoadCity: null,
        handleLoadCounty: null
    };
    this.config = e,
    that = this,
    $.extend(e, t),
    $.extend(a, n);
    var i = this,
    o = null,
    r = null,
    l = null,
    d = null,
    h = null,
    c = null,
    u = null,
    s = Map.createElement("svg"),
    v = Map.createElement("g"),
    f = function() {
        s.setAttribute("width", "100%"),
        s.setAttribute("height", "100%"),
        s.appendChild(v),
        v.setAttribute("transform", "scale(1,1) translate(" + e.marginLeft + "," + e.marginTop + ")"),
        o = new MapLayer(e, {
            handleAreaClick: function(t) {
            	//澳门,香港,台湾无数据
                var n = t.attr("key");
                i.loadCity(n);
                if(!that.isIncludeSpecialArea(n)){
                	$('#court-map').find('image').remove();
                	$('#court-map').find('.level1').css('display','none');
                }
            },
            level: 1
        }),
        r = new MapLayer(e, {
            handleAreaClick: function(t) {
                var n = t.attr("key");
                i.loadCounty(n);
                if ("重庆" != c.name && "北京" != c.name && "上海" != c.name && "天津" != c.name
                		&& "海南省三沙市" != c.name){
                	$('#court-map').find('image').remove();
                	$('#court-map').find('.level1').css('display','none');
                	$('#court-map').find('.level2').css('display','none');
                }
            },
            level: 2
        }),
        l = new MapLayer(e, {
            handleAreaClick: function(t) {
                t.attr("key");
            },
            level: 3
        }),
        d = new PointLayer(e, {}),
        o.appendTo(v),
        r.appendTo(v),
        l.appendTo(v),
        d.appendTo(v)
    };
    this.appendTo = function(t) {
        t.appendChild(s)
    };
    var g = function(t) {
        var n = t.right - t.left,
        a = t.bottom - t.top;
        return n > a ? e.width / n: e.height / a
    };
    this.zoom = function(t, n) {
        o.zoom(t, n);
        r.zoom(t, n);
        l.zoom(t, n);
        d.zoom(t, n);
        e.scale = t
    },
    this.getScale = function() {
        return e.scale
    };
    var p = function(t, n) {
    	$.getJSON(oc.resource.getUrl("resource/module/map/json/" + t), n);
    };
    this.load = function() {
        p("china.json",
        function(t) {
            h = t,
            p("province.json",
            function(t) {
                for (var n in t) {
                	if(n==110000||n==120000||n==310000){
                		continue;
                	}
                    var e = o.convertToXYList(t[n]);
                    $.extend(h[n], e);
                    var i = o.draw(h[n]);
                    i.setAttribute("key", n)
                }
                
                e = o.convertToXYList(t[110000]);
                $.extend(h[110000], e);
                i = o.draw(h[110000]);
                i.setAttribute("key", 110000);
                
                e = o.convertToXYList(t[120000]);
                $.extend(h[120000], e);
                i = o.draw(h[120000]);
                i.setAttribute("key", 120000);
                
                e = o.convertToXYList(t[310000]);
                $.extend(h[310000], e);
                i = o.draw(h[310000]);
                i.setAttribute("key", 310000);
                
                !! a.handleLoadProvince && a.handleLoadProvince();
                curLevel=0;
                eve("topo.map.dbclick",this,1,curLevel);
                curKey=0;
                oc.topo.mapalready = true;
                /************/
//    			var path = Raphael($("#court-map>svg").get(0),1,1).path("M0,0L1,1");
//    			var images = $('#court-map').find('.layer').last().find('image');
//    			var result = {};
//    			out : for(var j = 0 ; j < images.length ; j ++){
//    				var x = parseFloat($(images[j]).attr("x")),y=parseFloat($(images[j]).attr("y"));
//    				var doms = $('.level1').find(".topoMapPathClass");
//    				for(var i=0;i<doms.length;i++){
//    					var dom = doms[i];
//    					var tmp=$(dom);
//    					path.attr("path",tmp.attr("d"));
//    					if(path.isPointInside(x,y)){
//    						var mapid = tmp.attr("key");
//    						var key = x + "_" + y;
//    						result[key] = mapid;
//    						continue out;
//    					}
//    				}
//    			}
//    			console.debug(JSON.stringify(result));
                /**************/
            })
        })
    };
    var y = function(t) {
        if (!t) return 0;
        var n = 0;
        for (var e in t) n++;
        return n
    };
    this.loadCity = function(t) {
        var n = h[t].list;
        y(n) <= 0 || p(t + ".json",
        function(o) {
            r.clear();
            for (var l in o) {
                var d = r.convertToXYList(o[l]);
                $.extend(n[l], d);
                var u = r.draw(n[l]);
                u.setAttribute("key", l)
            }
            var s = h[t].center;
            v = g(h[t].range);
            f = [e.width / 2 - s[0] - (e.width * v / 2 - e.width / 2) / v, e.height / 2 - s[1] - (e.height * v / 2 - e.height / 2) / v];
            i.zoom(v, f);
            c = h[t];
            !!a.handleLoadCity && a.handleLoadCity(c);
            curLevel=1;
            eve("topo.map.dbclick",this,t,curLevel);
            curKey=t;
        })
    },
    this.loadCounty = function(t) {
        var n = c.list[t].list;
        y(n) <= 0 || p(t + ".json",
        function(o) {
            l.clear();
            for(var r in o){
            	for(var j = 0 ; j < errorCode.length ; j++){
            		if(r == errorCode[j]){
            			var d = l.convertToXYList(o[r]);
            			$.extend(n[r], d);
            			var h = l.draw(n[r]);
            			h.setAttribute("key", r)
            		}
            	}
            }
            out:for (var r in o) {
            	for(var j = 0 ; j < errorCode.length ; j++){
            		if(r == errorCode[j]){
            			continue out;
            		}
            	}
                var d = l.convertToXYList(o[r]);
                $.extend(n[r], d);
                var h = l.draw(n[r]);
                h.setAttribute("key", r)
            }
            var s = c.list[t].center,
            v = g(c.list[t].range),
            f = [e.width / 2 - s[0] - (e.width * v / 2 - e.width / 2) / v, e.height / 2 - s[1] - (e.height * v / 2 - e.height / 2) / v];
            i.zoom(v, f),
            u = c.list[t],
            !!a.handleLoadCounty && !!a.handleLoadCounty(c, u);
            curLevel=2;
            eve("topo.map.dbclick",this,t,curLevel);
            curKey=t;
        })
    },
    this.backToProvince = function() {
    	$('#court-map').find('.level2').css('display','');
        var t = c.code,
        n = h[t].center,
        o = g(h[t].range),
        r = [e.width / 2 - n[0] - (e.width * o / 2 - e.width / 2) / o, e.height / 2 - n[1] - (e.height * o / 2 - e.height / 2) / o];
        i.zoom(o, r),
        c = h[t],
        l.clear(),
        !!a.handleLoadCity && a.handleLoadCity(c);
        curLevel=1;
        eve("topo.map.dbclick",this,t,curLevel);
        curKey=t;
    },
    this.backToChina = function() {
    	$('#court-map').find('.level1').css('display','');
        l.clear(),
        r.clear(),
        i.zoom(1, [0, 0]),
        !!a.handleLoadProvince && a.handleLoadProvince();
        curLevel=0;
        eve("topo.map.dbclick",this,1,curLevel);
    },
    this.addPoint = function(t) {
        var n = o.convertToXY(t.lat, t.lng);
        return t.left = n[0],
        t.top = n[1],
        d.addPoint(t)
    },
    this.clearPoint = function() {
        d.clearPoint()
    },
    //澳门,香港,台湾,钓鱼岛
    this.isIncludeSpecialArea = function(key){
    	if("710000" == key || "810000" == key || "820000" == key || "900000" == key){
    		return true;
    	}else{
    		return false;
    	}
    },
    //直辖市
    this.isIncludeSpecialArea2 = function(key){
    	if("500000" == key || "110000" == key || "310000" == key || "120000" == key){
    		return true;
    	}else{
    		return false;
    	}
    },
    f();
    
    oc.module.topo.map = {
    	loadMapData:function(id,nextLevel){
    		if(nextLevel == 0){
    			if(curLevel == 2){
    				that.backToProvince();
    			}else{
    				that.backToChina();
    			}
    		}else if(nextLevel == 1){
            	//澳门,香港,台湾无数据
                if(that.isIncludeSpecialArea(id)){
                	return;
                }
    			if(curLevel == 2){
    		    	$('#court-map').find('.level2').css('display','');
    		        var t = c.code,
    		        n = h[t].center;
    		        var o = g(h[t].range);
    		        var qq = [e.width / 2 - n[0] - (e.width * o / 2 - e.width / 2) / o, e.height / 2 - n[1] - (e.height * o / 2 - e.height / 2) / o];
    		        i.zoom(o, qq),
    		        c = h[t],
    		        l.clear(),
    		        curLevel=1;
    			}
				that.loadCity(id);
				if(!that.isIncludeSpecialArea(id)){
					$('#court-map').find('image').remove();
					$('#court-map').find('.level1').css('display','none');
				}
    		}else if(nextLevel == 2){

    			//海南省三沙市的几个特殊区域
    			if(id == '460321' || id == '460322' || id == '460323'){
    				return
    			}
    			
    			var provinceId = (id + "").substring(0,2) + '0000';
    			
	            if (that.isIncludeSpecialArea2(provinceId)){
	            	return;
	            }
    			
    			if(curLevel == 0 || provinceId != c.code){
		            
		            var n = h[parseInt(provinceId)].list;
		            y(n) <= 0 || p(provinceId + ".json",
		            function(o) {
		                r.clear();
		                for (var l in o) {
		                    var d = r.convertToXYList(o[l]);
		                    $.extend(n[l], d);
		                    var u = r.draw(n[l]);
		                    u.setAttribute("key", l);
		                }
		                var s = h[parseInt(provinceId)].center;
	    	            var v = g(h[parseInt(provinceId)].range);
	    	            f = [e.width / 2 - s[0] - (e.width * v / 2 - e.width / 2) / v, e.height / 2 - s[1] - (e.height * v / 2 - e.height / 2) / v];
	    	            i.zoom(v, f);
			            c = h[parseInt(provinceId)];
			            !!a.handleLoadCity && a.handleLoadCity(c);
		                if(!that.isIncludeSpecialArea(provinceId)){
		                	$('#court-map').find('image').remove();
		                	$('#court-map').find('.level1').css('display','none');
		                }
			            that.loadCounty(id);
			            if ("重庆" != c.name && "北京" != c.name && "上海" != c.name && "天津" != c.name){
			            	$('#court-map').find('image').remove();
			            	$('#court-map').find('.level1').css('display','none');
			            	$('#court-map').find('.level2').css('display','none');
			            }
		            });
		            
    			}else{
    				that.loadCounty(id);
		            if ("重庆" != c.name && "北京" != c.name && "上海" != c.name && "天津" != c.name){
		            	$('#court-map').find('image').remove();
		            	$('#court-map').find('.level1').css('display','none');
		            	$('#court-map').find('.level2').css('display','none');
		            }
    			}
    			
    		}
    	},
    	getErrorAreaCode:function(){
    		return errorCode;
    	},
    	getErrorAreaPonit:function(){
    		return errorCodePointIds;
    	},
    	isCharteredCities:function(key){
    		return that.isIncludeSpecialArea2(key);
    	}
    };
    
};