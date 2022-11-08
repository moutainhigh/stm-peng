(function() {
	function NodeTip(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	NodeTip.prototype = {
		constructor : NodeTip,
		cfg : undefined, // 外界传入的配置参数 json对象格式
		open : function() {
			var that = this;
			$('.biz_main_runinfo_tip').remove();
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getRunInfoForTooltip.htm'),
				data:{bizId:this.cfg.bizId},
				startProgress:null,
				success:function(data){
					if(data.code == 200){
						var div = $('<div class="biz_main_runinfo_tip topo_tip"></div>');
						var table = $("<table/>");
						//<tr> <td>设备名称：<span data-field="name" title="Xiaopf-PC">Xiaopf-PC</span></td> </tr>
						table.append('<tr><td>健康度：<span>' + data.data.healthScore + '分</span></td></tr>');
						table.append('<tr><td>可用率：<span>' + data.data.availableRate + '</span></td></tr>');
						table.append('<tr><td>宕机次数：<span>' + data.data.outageTimes + '</span></td></tr>');
						table.append('<tr><td>MTTR：<span>' + data.data.mttr + '</span></td></tr>');
						table.append('<tr><td>MTBF：<span>' + data.data.mtbf + '</span></td></tr>');
						table.append('<tr><td>责任人：<span>' + (data.data.managerName ? data.data.managerName : '暂无') + '</span></td></tr>');
						table.append('<tr><td>电话：<span>' + (data.data.phone ? data.data.phone : '暂无') + '</span></td></tr>');
						div.append(table);
						div.css('position','absolute').css('left',that.cfg.x + 'px').css('top',that.cfg.y + 'px').css('background-color','white');
						div.appendTo($('body'));
					}else{
						alert('获取业务运行情况失败！');
					}
				}
			});

		},
		_defaults : {},
		close : function(){
			$('.biz_main_runinfo_tip').remove();
		}
	};

	// 命名空间
	oc.ns('oc.business.canvas.nodetip');
	// 对外提供入口方法
	var tip = undefined;
	oc.business.canvas.nodetip = {
		open : function(cfg) {
			tip = new NodeTip(cfg);
			tip.open();
		},
		close : function(){
			if(tip){
				tip.close();
			}
		}
	};
})(jQuery);