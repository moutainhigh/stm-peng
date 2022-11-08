(function() {
	function bizResp(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	function getUnChecked(cfg){
		//getUncheckedUrlInfo
		var datas=null;
		 oc.util.ajax({
				url:oc.resource.getUrl('portal/business/cap/getUncheckedUrlInfo.htm'),
				data:{bizid:cfg.bizId},
				async:false,
				success:function(data){
					if(data.data){
						datas= data.data;
						
					}
				}
				
		 });
		 return datas;
	}
	bizResp.prototype = {
		constructor : bizResp,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		isClickFirst : false,
		isClickSecond : false,
		isClickThrid : false,
		isClicklast : false,
		bizDom : undefined,
		discoveryFlag : false,
		dlg : undefined,
		open : function(cfg) { // 初始化页面方法
		var panel=$("#myurlPanel");
		var dlg = $("<div/>"), that = this;
		var windowHeight = $(window).height(), dlgHeight =500, dlgWidth = 800;
		// 有可能显示区域太小 关闭按钮不显示问题
		var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
		this.dlg = dlg;
		
		dlg.dialog({
			title : '业务响应时间', //oc.local.module.resource.discovery.discoverResource,
			href : oc.resource
					.getUrl('resource/module/business-service/biz_ResponeTimeMetric.html'),
			width : dlgWidth,
			height : dlgHeight,
			top : dlgTop,
			buttons:[{
				text: '确定',
				iconCls:"fa fa-check-circle",
				handler: function () {
				var url=new Array();
				var ids=	$("#urlTab tr td input[type=checkbox]");
				ids.each(function(){
					if($(this).prop("checked")==false){
						url.push($(this).context.value);
					}else{
					
					}
					
				});
				
				var datas=[{url:url}];
				 oc.util.ajax({
						url:oc.resource.getUrl('portal/business/cap/insertUrlCapInfo.htm'),
						data:{data:datas,bizid:that.cfg.bizId},
						async:false,
						success:function(data){
							if(data.data=="success"){
								alert("保存成功");
							}
						}
						
				 });
					dlg.dialog("destroy");
                }
				
			},{
				text: '取消',
				iconCls:"fa fa-times-circle",
				handler: function () {
					dlg.dialog("destroy");
                }
			}],
			onLoad : function() {
				$("#urlTab").html("");
			var Uncheckes=	getUnChecked(that.cfg);
				oc.util.ajax({
						url:oc.resource.getUrl('portal/business/cap/getResponeTimeMetricInstanceList.htm'),
						data:{bizid:that.cfg.bizId},
						async:false,
						success:function(data){
							var html="";
							if(data.data){
								for(var i=0;i<data.data.length;i++){
									if($.inArray(parseInt(data.data[i].id),Uncheckes)==-1){
										html+='<tr >'
											+'<td style="padding:10px;0px;"><input type="checkbox" name="check" checked=checked value="'+data.data[i].id+'"><span>'+data.data[i].name+'</span></td>'
											+'</tr>';	
									}else{
										html+='<tr >'
											+'<td style="padding:10px;0px;"><input type="checkbox" name="check"  value="'+data.data[i].id+'"><span>'+data.data[i].name+'</span></td>'
											+'</tr>';
									}
								
								}
							}
						$("#urlTab").append(html);
						}
						});
			},
			 onClose:function(){
			    	dlg.dialog("destroy");
			    }
		});
		}
		
	};

	// 命名空间
	oc.ns('oc.business.service.resp');
	// 对外提供入口方法
	var resp = undefined;
	oc.business.service.resp = {
		open : function(cfg) {
			resp = new bizResp(cfg);
			resp.open();
		}
	};
})(jQuery);