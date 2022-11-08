buzdefine("bz/extend",[],function(){
	return function(child,parent,methods){
		var t = function(){
		};
		t.prototype=parent.prototype;
		child.prototype=new t();
		child.prototype.constructor=child;
		for(var k in methods){
			child.prototype[k]=methods[k];
		}
		return child;
	};
});