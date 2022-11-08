define("wiki-court:widget/courtMap/courtMap.js",
function(t, a, i) {
    function e() {
        var t = [],
        a = c("#header .container").width(),
        i = c(window).width(),
        e = (c("#main").height(), (i - a) / 2),
        r = c("#court-nav").width();
        t[0] = i - e - r;
        var n = c(window).height() - c("#header").outerHeight(!0),
        o = c("#court-nav").outerHeight(!0) + 50;
        return o > n && (n = o),
        t[0] / Map.MAP_RATE > n ? (t[1] = n, t[0] = t[1] * Map.MAP_RATE, t[3] = (i - e - r - t[0])/2 + i / 5) : (t[1] = t[0] / Map.MAP_RATE, t[3] = i / 5),
        c("#main").height(c("#main").parent().height()),
        t
    }
	function r(t,level) {
		var relation = {"116.44333553842_39.910121914452":"110000","126.69598862272_45.749950749145":"230000","108.96690397726_34.198630424284":"610000","113.35937967589_23.126899356903":"440000","112.53956802608_37.846464440765":"140000","106.53043792518_29.598453696164":"500000","104.07488416321_30.675848381681":"510000","106.70997703723_26.588365131547":"520000","101.76007689626_36.635532243108":"630000","117.17918600031_39.130159272875":"120000","106.27459424008_38.485571976647":"640000","111.72898896611_40.805550708245":"150000","117.12237778867_36.666972329925":"370000","117.28160270981_31.868283828565":"340000","113.02650708962_28.217247719755":"430000","102.69206175524_25.004720727361":"530000","124.027313_45.008349":"220000","108.38111435995_22.819605677769":"450000","114.32845837967_30.560127749739":"420000","88.915388_29.276919":"540000","87.608686890615_43.843480319622":"650000","102.916474_34.982222":"620000","118.77771634345_32.063716680577":"340000","110.34860581439_19.995548108007":"440000","123.461429_41.722803":"210000","113.71739414126_34.768533694397":"410000","121.46590007359_31.209339954613":"320000","120.15562456012_30.280741501097":"330000","115.9186209916_28.681646548732":"360000","119.28744838011_26.104803602721":"350000","114.46713069955_38.041450797439":"130000","112.029442_16.34006289402":"460300"};
		var a = o.addPoint({
			src: oc.resource.getUrl("resource/module/map/img/gaofa_white.png"),
			width: 10,
			height: 10,
			lat: t.longitude,
			lng: t.latitude
		});
		if(level == 1){
			c(a).attr("key", relation[t.longitude + "_" + t.latitude]);
		}
		c(a).attr("data-courtname", t.courtName),
		c(a).attr("data-id", t.longitude + "_" + t.latitude),
        c(a).hover(function() {
        	c(a).attr('old_href',c(a).attr('href'));
        	c(a).attr('href',oc.resource.getUrl("resource/module/map/img/dack_blue.png"));
        	
        },function() {
        	c(a).attr('href',c(a).attr('old_href'));
        });
	}
    function n() {
        var t = e();
        o = new Mapper({
            marginLeft: t[3],
            width: t[0],
            height: t[1]
        },
        {
            handleLoadProvince: function() {
                courtList.searchByProvince("beijing"),
                c("#back-china").hide(),
                c("#court-map").attr("class", ""),
                courtList.markerLevel1()
            },
            handleLoadCity: function(t) {
                courtList.searchByProvince(h[t.code]);
                c("#back-china").show();
                s = 2;
                c("#court-map").attr("class", "mapLevel2");
                courtList.markerLevel2(h[t.code]);
            },
            handleLoadCounty: function(t, a) {
                courtList.searchByCity(a.name),
                s = 3,
                c("#court-map").attr("class", "mapLevel3"),
                courtList.markerLevel3()
            }
        }),
        o.appendTo(document.getElementById("court-map")),
        o.load(),
        c("#back-china").on("click",
        function() {
            2 == s ? (o.backToChina(), s = 1) : 3 == s && (o.backToProvince(), s = 2);
        })
    }
    var o, c = t("wiki-common:widget/lib/jquery/jquery.js"),
    h = {
        110000 : "beijing",
        120000 : "tianjin",
        130000 : "hebei",
        140000 : "shanxi",
        150000 : "neimenggu",
        210000 : "liaoning",
        220000 : "jilin",
        230000 : "heilongjiang",
        310000 : "shanghai",
        320000 : "jiangsu",
        330000 : "zhejiang",
        340000 : "anhui",
        350000 : "fujian",
        360000 : "jiangxi",
        370000 : "shandong",
        410000 : "henan",
        420000 : "hubei",
        430000 : "hunan",
        440000 : "guangdong",
        450000 : "guangxi",
        460000 : "hainan",
        500000 : "chongqing",
        510000 : "sichuan",
        520000 : "guizhou",
        530000 : "yunnan",
        540000 : "xizang",
        610000 : "shaanxi",
        620000 : "gansu",
        630000 : "qinghai",
        640000 : "ningxia",
        650000 : "xinjiang",
        710000 : "taiwan",
        810000 : "xianggang",
        820000 : "aomen",
        460300 : "hainansansha"
    },
    s = 1;
    i.exports = {
        init: n,
        markerCourt: r
    }
});