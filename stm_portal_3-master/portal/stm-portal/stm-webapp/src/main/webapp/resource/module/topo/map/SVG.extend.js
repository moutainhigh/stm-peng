SVG.extend(SVG.Element, {
	animateMotion:function(attr){
		attr=$.extend({
			dur:"10s",rotate:"auto",fill:"freeze",mpath:null
		},attr);
		var dom = $(document.createElementNS("http://www.w3.org/2000/svg","animateMotion"));
		if(attr.mpath){
			var mpathDom = document.createElementNS("http://www.w3.org/2000/svg","mpath");
			mpathDom.setAttributeNS("http://www.w3.org/1999/xlink","xlink:href","#"+attr.mpath);
			var mpath = $(mpathDom);
			dom.append(mpath);
			delete attr.mpath;
			dom.attr(attr);
		}
		$(this.node).append(dom);
	}
});