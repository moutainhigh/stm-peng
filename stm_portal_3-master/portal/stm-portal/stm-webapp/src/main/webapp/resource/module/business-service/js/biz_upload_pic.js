(function() {
	var fileId=null;
	function uploadPic(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	function reloadDiv(){
	var datas=new Array(); 
	var picDiv=$("#loadpicDiv");
	picDiv.html("");
	var html="";
	 oc.util.ajax({
			url:oc.resource.getUrl("portal/biz/image/list/"+0+".htm"),
			async:false,
			success:function(data){
				if(data.data){
					datas= data.data;
					for(var i=0;i<datas.length;i++){
						html+='<div class="icon loadimgbox" >'+
						'<img style="width:60px;height:60px; margin: 10px 12px;" src="'+oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+datas[i].fileId)+'" name="uploadPic" id="'+datas[i].fileId+'" />'+
					'</div>';
					
					}
					picDiv.append(html);
				}
			}
			
	 });
	}
	function addClickImg(){
		 $("img[name='uploadPic']").each(function(){
			 $(this).click(function(){
				 fileId=$(this).attr("id");
				 $(".loadimgbox_active").each(function(){
					 $(this).removeClass('loadimgbox_active');
				 });
				 $(this).parent().addClass("loadimgbox_active");
//				 console.info($(this));
				 $(".bizBaseInfoForm").find("#previews").attr("src",
							oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+fileId))
							.css({'display':'block',width:'67px',height:'67px'});
				 $(".bizBaseInfoForm").find("input[name='fileId']").val(fileId);
			/*	 $(".bizBaseInfoForm").find("#preview").attr("src",
							oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+fildId))
							.css({'display':'block',width:'67px',height:'67px'});
				 $(".bizBaseInfoForm").find("input[name='file']").val(fildId);*/
			 });
		 });
	}
	uploadPic.prototype = {
		constructor : uploadPic,
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
		var dlg = $("<div/>"), that = this;
		var windowHeight = $(window).height(), dlgHeight = 520, dlgWidth = 600;
		// 有可能显示区域太小 关闭按钮不显示问题
		var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
		this.dlg = dlg;
		
		dlg.dialog({
			title : '选择图片', //oc.local.module.resource.discovery.discoverResource,
			href : oc.resource
					.getUrl('resource/module/business-service/biz_upload_pic.html'),
			width : dlgWidth,
			height : dlgHeight,
			top : dlgTop,
			buttons:[{
				text: '确定',
				iconCls:"fa fa-check-circle",
				handler: function () {
					if(fileId==null){
						fileId=1011;
					}
						 $(".bizBaseInfoForm").find("#previews").attr("src",
									oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+fileId))
									.css({'display':'block',width:'67px',height:'67px'});
						 $(".bizBaseInfoForm").find("input[name='fileId']").val(fileId);
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
				var datas=new Array(); 
				var picDiv=$("#loadpicDiv");
				var html="";
				 oc.util.ajax({
						url:oc.resource.getUrl("portal/biz/image/list/"+0+".htm"),
						async:false,
						success:function(data){
							if(data.data){
								datas= data.data;
								for(var i=0;i<datas.length;i++){
									html+='<div class="icon loadimgbox">'+
									'<img style="width:60px;height:60px ; margin: 10px 14%;" src="'+oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+datas[i].fileId)+'" name="uploadPic" id="'+datas[i].fileId+'" />'+
								'</div>';
								
								}
								picDiv.append(html);
							}
						}
						
				 });
				 
				 $("#uploadBtn").linkbutton('RenderLB',{
						  iconCls:"fa fa-file-picture-o",
						  onClick:function(){
								 oc.resource.loadScript("resource/module/business-service/js/biz_picManage_upload.js",function(){
										new BizImgUpload({
											callBack:function(){
												reloadDiv();//刷新图片面板
												addClickImg();//给新上传的图片新增点击时间
											}

										});
										
								});
						  }
					});
				 
				 $("img[name='uploadPic']").each(function(i,item){
				
					 $(this).click(function(){
						 fileId=$(this).attr("id");
						 $(".loadimgbox_active").each(function(){
							 $(this).removeClass('loadimgbox_active');
						 });
						 $(this).parent().addClass("loadimgbox_active");
					 });
				 });
			

			},
			 onClose:function(){
			    	dlg.dialog("destroy");
			    }
		});
		}
		
	};

	// 命名空间
	oc.ns('oc.business.service.uploadpic');
	// 对外提供入口方法
	var uploadpic = undefined;
	oc.business.service.uploadpic = {
		open : function(cfg) {
			uploadpic = new uploadPic(cfg);
			uploadpic.open();
		}
	};
})(jQuery);