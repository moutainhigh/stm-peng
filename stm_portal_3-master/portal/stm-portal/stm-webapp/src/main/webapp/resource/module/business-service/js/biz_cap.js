(function() {
	function bizCap(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	bizCap.prototype = {
		constructor : bizCap,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		isClickFirst : false,
		isClickSecond : false,
		isClickThrid : false,
		isClicklast : false,
		bizDom : undefined,
		dlg : undefined,
		open : function() { // 初始化页面方法
			var dlg = $("<div/>"), that = this;
			var windowHeight = $(window).height(), dlgHeight = 550, dlgWidth = 850;//1008;
			// 有可能显示区域太小 关闭按钮不显示问题
			var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
			this.dlg = dlg;
			
			dlg.dialog({
				title : '业务容量', //oc.local.module.resource.discovery.discoverResource,
				href : oc.resource
						.getUrl('resource/module/business-service/biz_cap.html'),
				width : dlgWidth,
				height : dlgHeight,
				top : dlgTop,
				buttons:[{
					text: '确定',
					handler: function () {
						
						 var bandwidthtree = $('ul[name="bandwidth"]');
						 var plantree = $('ul[name="plan"]');
						 var databasetree = $('ul[name="database"]');
						 var stroagetree = $('ul[name="storage"]');
						 var bandwidth=new Array();
						 var plan=new Array();
						 var database=new Array();
						 var stroage=new Array();
						 bandwidthtree.each(function(){
							 var checkedboxs=$(this).find("span.tree-checkbox0").next().find('input');
							 checkedboxs.each(function(){
								 bandwidth.push($(this).context.value);
							 });
						 });
						 databasetree.each(function(){
							 var checkedboxs=$(this).find("span.tree-checkbox0").next().find('input');
							
							 checkedboxs.each(function(){
								 database.push($(this).context.value);
							 });
						 });
						 plantree.each(function(){
							 var checkedboxs=$(this).find("span.tree-checkbox0").next().find('input');
							 checkedboxs.each(function(){
								 plan.push($(this).context.value);
							 });
						 });
						 stroagetree.each(function(){
							 var checkedboxs=$(this).find("span.tree-checkbox0").next().find('input');
							 checkedboxs.each(function(){
								 stroage.push($(this).context.value);
							 });
						 });
						 var datas=[{plan:plan},{database:database},{stroage:stroage},{bandwidth:bandwidth}]//{plan:plan,database:database,stroage:stroage,bandwidth:bandwidth};
						 oc.util.ajax({
								url:oc.resource.getUrl('portal/business/cap/insertCapInfo.htm'),
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
					handler: function () {
						dlg.dialog("destroy");
                    }
				}],
				onLoad : function() {
					var id = oc.util.generateId();
					that.bizDom = $("#oc_module_biz_cap").attr('id', id);
					$("#hideid").val(that.cfg.bizId);
					that._init();
				},
				 onClose:function(){
				    	dlg.dialog("destroy");
				    }
			});
		},
		_defaults : {},
		_init : function() {
			var that = this;
			var account = that.bizDom.find(".account");
			var accounttwo = that.bizDom.find(".accounttwo");
			that.showHideAccounttwo(account, "", 0);
			var currenSkin=Highcharts.theme.currentSkin;//that.getCurrentSkin();//default
			this.bizDom.layout({
				fit : true
			});
			that.bizDom.find(".nav_plan_btn").trigger("click");
			this.bizDom.find(".bizContent:first").panel({
				fit : true,
				border : false,
				href : oc.resource.getUrl('resource/module/business-service/biz_plancap.html'),
				onLoad:function(){
					$("#queryBtn").click(function(){
						$("#queryName").val($("#queryname").val());
						that.bizDom.find(".bizContent:first").panel('refresh',oc.resource.getUrl('resource/module/business-service/biz_plancap.html'));
						$("#queryname").val($("#queryName").val());
					});
					$("#queryname").val($("#queryName").val());
				var datas=	that.initTree(that.cfg.bizId,0);
					 var tree = $('ul[name="plan"]');
					 tree.each(function(){
						 var checkedboxs=$(this).find("span.tree-checkbox0");
						 var checkedboxdatas=$(this).find("span.tree-checkbox0").next().find('input');
						 checkedboxs.each(function(){
							
								var value=$(this).next().find('input')[0].value;//.context.value;
								if($.inArray(parseInt(value),datas)==-1){
									$(this).removeClass('tree-checkbox0').addClass('tree-checkbox1');
								}
							 });
					
					 });
					 if(currenSkin=="blue"){
							$("#mydivs div ul li div").each(function(){
								$(this).attr("style","border-bottom:1px solid #174f99;");
							}); 
					 }
				
					$("#mydivs div ul li div span.tree-folder").remove();
					$("#mydivs div ul li div span.tree-file").remove();
				},
				onClose:function(){
				}
		
			});
			this.isClickFirst = true;
			// 为导航按钮绑定点击事件
			that.bizDom.find(".nav_plan_btn").on('click', function(){
				that.bizDom.find(".bizContent").hide();
				that.bizDom.find(".bizContent:first").show();
				if(!that.isClickFirst){
					that.bizDom.find(".bizContent:first").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/business-service/biz_plancap.html')
					});
					that.isClickFirst = true;
				}
				that.showHideAccounttwo(account, "", 0);
			});
			that.bizDom.find(".nav_storage_btn").on('click', function(){
				that.bizDom.find(".bizContent").hide();
				that.bizDom.find(".bizContent:eq(1)").show();
				if(!that.isClickSecond){
					that.bizDom.find(".bizContent:eq(1)").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/business-service/biz_storagecap.html'),
						onLoad : function(){
							//queryBtn
							$("#querystroageBtn").click(function(){
								$("#queryName").val($("#stroagename").val());
								that.bizDom.find(".bizContent:eq(1)").panel('refresh',oc.resource.getUrl('resource/module/business-service/biz_storagecap.html'));
							});
							$("#stroagename").val($("#queryName").val());
							var datas=that.initTree(that.cfg.bizId,1);
							 var tree = $('ul[name="storage"]');
							 tree.each(function(){
								 var checkedboxs=$(this).find("span.tree-checkbox0");
								 checkedboxs.each(function(){
								var value=$(this).next().find('input')[0].value;//.context.value;
								if($.inArray(parseInt(value),datas)==-1){
									$(this).removeClass('tree-checkbox0').addClass('tree-checkbox1');
								}
								 });
							 });
							 if(currenSkin=="blue"){
							$("#StoPaneldivs div ul li div").each(function(){
								$(this).attr("style","border-bottom:1px solid #174f99;");
							});
							 }
							$("#StoPaneldivs div ul li div span.tree-folder").remove();
							$("#StoPaneldivs div ul li div span.tree-file").remove();
						}
					});
					that.isClickSecond = true;
				}
				that.showHideAccounttwo(account, "", 1);
			});
			that.bizDom.find(".nav_db_btn").on('click', function(){
				that.bizDom.find(".bizContent").hide();
				that.bizDom.find(".bizContent:eq(2)").show();
				if(!that.isClickThrid){
					that.bizDom.find(".bizContent:eq(2)").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/business-service/biz_dbcap.html'),
						onLoad : function(){
							//queryBtn
							$("#querydbBtn").click(function(){
								$("#queryName").val($("#dbname").val());
								that.bizDom.find(".bizContent:eq(2)").panel('refresh',oc.resource.getUrl('resource/module/business-service/biz_dbcap.html'));
							});
							$("#dbname").val($("#queryName").val());
						var datas=	that.initTree(that.cfg.bizId,2);
							 var tree = $('ul[name="database"]');
							 tree.each(function(){
								 var checkedboxs=$(this).find("span.tree-checkbox0");
								 checkedboxs.each(function(){
										var value=$(this).next().find('input')[0].value;//.context.value;
										if($.inArray(parseInt(value),datas)==-1){
											$(this).removeClass('tree-checkbox0').addClass('tree-checkbox1');
										}
								 });
							 });
							 if(currenSkin=="blue"){
							$("#dbdivs div ul li div").each(function(){
								$(this).attr("style","border-bottom:1px solid #174f99;");
							});
							 }
							$("#dbdivs div ul li div span.tree-folder").remove();
							$("#dbdivs div ul li div span.tree-file").remove();
						}
						//dbdivs
					});
					that.isClickThrid = true;
				}
				that.showHideAccounttwo(account, "", 2);
			});
			that.bizDom.find(".nav_bandwidth_btn").on('click', function(){
				that.bizDom.find(".bizContent").hide();
				that.bizDom.find(".bizContent:eq(3)").show();
				if(!that.isClicklast){
					that.bizDom.find(".bizContent:eq(3)").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/business-service/biz_bandwidthcap.html'),
						onLoad : function(){
							//queryBtn
							$("#querybdBtn").click(function(){
								$("#queryName").val($("#bdname").val());
								that.bizDom.find(".bizContent:eq(3)").panel('refresh',oc.resource.getUrl('resource/module/business-service/biz_bandwidthcap.html'));
							});
							$("#bdname").val($("#queryName").val());
						var datas=	that.initTree(that.cfg.bizId,3);
							 var tree = $('ul[name="bandwidth"]');
							 tree.each(function(){
								 var checkedboxs=$(this).find("span.tree-checkbox0");
								 checkedboxs.each(function(){
										var value=$(this).next().find('input')[0].value;//.context.value;
										if($.inArray(parseInt(value),datas)==-1){
											$(this).removeClass('tree-checkbox0').addClass('tree-checkbox1');
										}
								 });
							 });
							 if(currenSkin=="blue"){
							$("#bandwidthdivs div ul li div").each(function(){
								$(this).attr("style","border-bottom:1px solid #174f99;");
							});
							 }
							$("#bandwidthdivs div ul li div span.tree-folder").remove();
							$("#bandwidthdivs div ul li div span.tree-file").remove();
						}
					});
					that.isClicklast = true;
				}
				that.showHideAccounttwo(account, "", 3);
			});
		},
		initTree : function(bizId,type){
			var datas= new Array();
			var url=oc.resource.getUrl('portal/business/cap/getUncheckedhostInfo.htm');//getUncheckedhostInfo
			if(type==1){
				url=oc.resource.getUrl('portal/business/cap/getUncheckedStorageInfo.htm');
			}else if(type==2){
				url=oc.resource.getUrl('portal/business/cap/getUncheckedDatabaseInfo.htm');
			}else if(type==3){
				url=oc.resource.getUrl('portal/business/cap/getUncheckedBandwidthInfo.htm');
			}else{
				url=oc.resource.getUrl('portal/business/cap/getUncheckedhostInfo.htm');
			}
			oc.util.ajax({
				url:url,//getUncheckedhostInfo
				data:{bizid:bizId},
				async:false,
				success:function(data){
					datas=data.data;
				}
				
			});
			return datas;
		},
		getCurrentSkin: function(){
			var skin="";
			oc.util.ajax({
				url:oc.resource.getUrl('system/skin/getCurrentSkin.htm'),
				async:false,
				success:function(data){
					skin=data.data;
				}
				});
			return skin;
		},
		showHideAccounttwo : function(account, accounttwo, No){
			for(var i = 0; i < 4; i ++){
				if(i == parseInt(No)){
					$(account.get(i)).parent().addClass('active');//$(account.get(i)).addClass('active');
				//	$(accounttwo.get(i)).show();
				}else{
					$(account.get(i)).parent().removeClass('active');//$(account.get(i)).removeClass('active');
				//	$(accounttwo.get(i)).hide();
				}
			}
		},
		close : function(){
			this.dlg.dialog('close');
		}
	};

	// 命名空间
	oc.ns('oc.business.service.cap');
	// 对外提供入口方法
	var biz = undefined;
	oc.business.service.cap = {
		open : function(cfg) {
			biz = new bizCap(cfg);
			biz.open();
		},
		close : function(){
			if(biz != undefined){
				biz.close();
			}
		}
	};
})(jQuery);