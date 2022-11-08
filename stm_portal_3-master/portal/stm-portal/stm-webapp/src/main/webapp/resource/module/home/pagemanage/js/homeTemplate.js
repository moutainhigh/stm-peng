function homeTemplate(widgetManager) {
	this.homeTempList = null; //模板列表
	this.currentTempId = null; //当前选择模板
	this.widgetManager = widgetManager;
	//初始化获取用户权限下所有模板
	this.initData();
}
var ht = homeTemplate.prototype;

ht.initData = function() {
	var _this = this;
	//查询所有的默认模板，显示列表
	oc.util.ajax({
		url: oc.resource.getUrl('system/home/layout/getAllTempLayout.htm'),
		timeout: null,
		success: function(data) {
			if(data.data) {
				_this.datas = data.data;
				$.each(_this.datas, function() {
					var name = $(this)[0].name;
					var id = $(this)[0].id;
				});

				//显示框
				_this.showDialog();
			}

		}
	});
}

ht.showDialog = function() {
	var _this = this;
	var dlg = $('<div/>');
	dlg.dialog({
		href: oc.resource.getUrl('resource/module/home/pagemanage/homeTemplate.html'),
		title: "选择模板",
		width: 1300,
		height: 620,
		scrollbars: "yes",
		modal: true,
		resizable: false,
		overflow: 'auto',
		onLoad: function() {
			var listDiv = $("#right-list");
			var width = listDiv.width();
			$.each(_this.datas, function() {
				var name = $(this)[0].name;
				var id = $(this)[0].id;
				var div = $("<div/>");
				div.attr('style','margin-bottom: 20px;background-color: white;color: black;margin-left: 15px')
				div.height(width - 130).width(width - 30);
				var litiimg = $("<img/>");
				litiimg.attr("style","height: 100%;width: 100%;");
				if(id == 1){
					litiimg.attr("src","themes/"+Highcharts.theme.currentSkin+"/images/home/home_temp_litimg0.png");
				}else if(id == 2){
					litiimg.attr("src","themes/"+Highcharts.theme.currentSkin+"/images/home/home_temp_litimg1.png");
				}else if(id == 3){
					litiimg.attr("src","themes/"+Highcharts.theme.currentSkin+"/images/home/home_temp_litimg2.png");
				}
				div.html(litiimg);
				listDiv.append(div);
				div.click(function() {
					$("#checkTempId").val(id);
					//有图片的时候替换成图片
					var img = $("<img/>");
					img.attr("style","height: 100%;width: 100%;");
					if(id == 1){
						img.attr("src","themes/"+Highcharts.theme.currentSkin+"/images/home/home_temp0.png");
					}else if(id == 2){
						img.attr("src","themes/"+Highcharts.theme.currentSkin+"/images/home/home_temp1.png");
					}else if(id == 3){
						img.attr("src","themes/"+Highcharts.theme.currentSkin+"/images/home/home_temp2.png");
					}
					$(this).css("border","1px solid #3E8CCA");
					$(this).siblings().css("border","");
					$("#gridster-readall").html(img);
				});
			})
			//添加应用按钮
			var btnDiv = $("<div style='margin-top:30px;margin-left:150px;'/>");
			listDiv.append(btnDiv);
			var applyBtn = '<a id="applyBtn" class="easyui-linkbutton">应用</a>';
			btnDiv.append(applyBtn);
			$('#applyBtn').linkbutton('RenderLB',{
		        iconCls: 'fa fa-check-circle',
		        onClick : function(){
		        	//判断是否选择模板
		        	var checkTempId = $("#checkTempId").val();
		        	if(checkTempId == "0"){
		        		alert("请选择模板");
		        		return;
		        	}
		        	//修改当前页面的布局为模板布局
		        	oc.util.ajax({
						url: oc.resource.getUrl('system/home/layout/updateCurrentHome.htm'),
						data: {
							layoutId: checkTempId,
							oldLayoutId:_this.widgetManager.data.homeLayoutBo.id
						},
						timeout: null,
						success: function(currData) {
							dlg.dialog('close');
							_this.widgetManager.layoutId = currData.data.homeLayoutBo.id;
							//刷新首页模块
							_this.widgetManager.widgets = {};
							_this.widgetManager.init();
							
//							//修改复制页面为默认页面
//							if(copyData.code == 200) {
//								oc.util.ajax({
//									url: oc.resource.getUrl('system/home/layout/updateHomeLayoutDefault.htm'),
//									data: {
//										layoutId: copyData.data.homeLayoutBo.id
//									},
//									timeout: null,
//									success: function(rData) {
//										if(rData.code == 200) {
//											dlg.dialog('close');
//											//刷新首页模块
//											_this.widgetManager.init();
//										} 
//									}
//								});
//							} else {
//								alert("应用失败");
//							}
						}

					});
		        }
		    });
			$("#right-list div").first().click();
			
			
		}
	});
}