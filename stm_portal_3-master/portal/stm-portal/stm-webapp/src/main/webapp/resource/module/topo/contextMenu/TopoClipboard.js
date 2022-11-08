function TopoClipboard(){
	this.items=null;
	//复制动作类型
	this.ACTION_COPY="copy",this.ACTION_CUT="cut";
	this.action=null;
};
TopoClipboard.prototype={
	//检查数据类型，返回的类型必是数组
	_check:function(_itms){
		if(_itms){
			if(_itms instanceof Array){
				return _itms;
			}else{
				return [_itms];
			}
		}else{
			return [];
		}
	},
	//复制
	copy:function(_items){
		with(this){
			items=_check(_items);
			action=ACTION_COPY;
		}
	},
	//剪切
	cut:function(_itms){
		with(this){
			items=_check(_itms);
			action=ACTION_CUT;
		}
	},
	//检查是否有数据
	hasData:function(){
		return this.items&&this.items.length>0;
	},
	/**
	 * 粘贴
	 * topoId 要粘贴到哪个子拓扑中去
	 * callBack 成功操作后的回调函数
	 */
	paste:function(topoId,callBack){
		if(!topoId && topoId!=0) throw "topoId can't be null";
		var ctx = this;
		with(this){
			if(!hasData()) return;
			var ids = $.map(items,function(itm){
				return itm.rawId;
			}).join(",");
			switch(action){
			case ACTION_COPY:
				oc.util.ajax({
					url:oc.resource.getUrl("topo/clipboard/copy.htm"),
					data:{
						topoId:topoId,
						ids:ids
					},
					success:function(result){
						if(result.status==200){
							if(callBack){
								callBack.call(ctx,result.items,false);
							}else{
								ctx.dulicateHandler(topoId,result.items,false);
							}
						}else{
							alert(result.msg||"复制失败!","warning");
						}
					}
				});
				break;
			case ACTION_CUT:
				oc.util.ajax({
					url:oc.resource.getUrl("topo/clipboard/move.htm"),
					data:{
						topoId:topoId,
						ids:ids
					},
					success:function(result){
						if(result.status==200){
							items=null;
							if(callBack){
								callBack.call(ctx,result.items,true);
							}else{
								ctx.dulicateHandler(topoId,result.items,true);
							}
						}else{
							alert(result.msg||"剪贴失败!","warning");
						}
					}
				});
				break;
			}
		}
	},
	dulicateHandler:function(topoId,items,isMove){
		//替换对话框
		oc.resource.loadScript("resource/module/topo/contextMenu/TopoOverrideDialog.js", function(){
			new TopoOverrideDialog({
				items:items,
				topoId:topoId,
				isMove:isMove,
				onFinished:function(){
					eve("topo.refresh");
				}
			});
		});
	},
	/**
	 * 提示覆盖
	 */
	_alertOverride:function(){
		
	}
};