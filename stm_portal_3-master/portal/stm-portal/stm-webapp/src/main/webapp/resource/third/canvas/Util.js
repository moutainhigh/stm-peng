function Util(args) {
	this.paper = args.paper;
	this.holder = args.holder;
	this.$svg = $(this.holder).find(">svg,div");
	this.rawViewBox = this.getViewBox();
};
Util.prototype = {
	getViewBox : function() {
		var svg = this.$svg.get(0);
		if(!svg) return;
		if (svg.tagName.toLowerCase() == "svg") {
			var vb = svg.getAttribute("viewBox");
			var r = {
				x : 0,
				y : 0,
				w : parseFloat(svg.getAttribute("width")),
				h : parseFloat(svg.getAttribute("height"))
			};
			if (vb) {
				var vs = vb.split(" ");
				r.x = parseFloat(vs[0]);
				r.y = parseFloat(vs[1]);
				r.w = parseFloat(vs[2]);
				r.h = parseFloat(vs[3]);
			} else {
				r.w = parseFloat(this.$svg.width());
				r.h = parseFloat(this.$svg.height());
				r.x = 0;
				r.y = 0;
			}
			;
			r.cx = r.x + r.w / 2;
			r.cy = r.y + r.h / 2;
			return r;
		} else if (svg.tagName.toLowerCase() == "div") {
			var r = {
				x : parseInt(this.$svg.css("left")),
				y : parseInt(this.$svg.css("top")),
				w : parseInt(this.$svg.css("width")),
				h : parseInt(this.$svg.css("height"))
			};
			r.cx = r.x + r.w / 2;
			r.cy = r.y + r.h / 2;
			return r;
		}
	},
	getSvg : function() {
		return this.$svg;
	},
	getRoundRectPath : function(args) {
		args = $.extend({
			x : 0,
			y : 0,
			w : 0,
			r : 0,
			cx : 0,
			cy : 0,
			h : 0
		}, args);
		var x = args.x, y = args.y, w = args.w, h = args.h, r = args.r || 0, rtl = args.rtl
				|| args.r, rtr = args.rtr || args.r, rbr = args.rbr || args.r, rbl = args.rbl
				|| args.r;
		var npath = [];
		npath.push([ "M", x + rtl, y ], [ "H", x + w - rtr ], [ "C",
				x + w - rtr, y, x + w, y, x + w, y + rtr ],
				[ "V", y + h - rbr ], [ "C", x + w, y + h - rbr, x + w, y + h,
						x + w - rbr, y + h ], [ "H", x + rbl ], [ "C", x + rbl,
						y + h, x, y + h, x, y + h - rbl ], [ "V", y + rtl ], [
						"C", x, y + rtl, x, y, x + rtl, y ], [ "Z" ]);
		return npath;
	},
	realPos : function(pos) {
		var newvb = this.getViewBox(), oldvb = this.rawViewBox;
		var xr = newvb.w / oldvb.w, yr = newvb.h / oldvb.h;
		pos.x = pos.x * xr;
		pos.y = pos.y * yr;
		return pos;
	},
	getEventPosition : function(e) {
		var of = this.$svg.offset();
		var newvb = this.getViewBox(), oldvb = this.rawViewBox;
		var r = {
			pageX : e.pageX,
			pageY : e.pageY,
			x : (e.pageX - of.left) * (newvb.w / oldvb.w) + newvb.x,
			y : (e.pageY - of.top) * (newvb.h / oldvb.h) + newvb.y
		};
		return r;
	}
};