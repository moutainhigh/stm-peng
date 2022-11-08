(function($){
	function bizDepDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	bizDepDetail.prototype={
		constructor:bizDepDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,type=cfg.type,div=cfg.div,
				buttons=[{
					text:'关闭',
					iconCls:'ico ico-cancel',
					handler:function(){
						dlg.dialog('close');
					}
				}];
			if(type!='view'){
				buttons.unshift({
					text:'保存',
					iconCls:'ico ico-ok',
					handler:function(){
						if(that.form.validate() && that.form.find("input[name=name]").val().trim() != ""){
							if(that.form.find("input[name='file']").val().trim()!=""
								&& !that.form.find("input[name='file']").val().trim().match(/gif|GIF|jpg|JPG|jpeg|JPEG|bmp|BMP|png|PNG$/)){
								alert("图片只能上传jpg、jpeg、gif、png这几种格式");
								return;
							}
							that.form.submitWithFile({
								url:oc.resource.getUrl(
									(type=='add')?('portal/business/service/dep/insert.htm?type='+(cfg.bizType=="bizDep"?0:1)):
										'portal/business/service/dep/update.htm'),
								data:that.form.val(),
								successMsg:null,
								success:function(data){
									if(data.code && data.code==200){
										cfg.saveCall&&cfg.saveCall(type,div,data.data,that.form.val(),cfg.bizType);
										dlg.dialog('close');
										alert("保存成功");
									}else if(data.code==299){
										dlg.dialog('close');
										alert(data.data);
									}else if(data.code==201){
										alert('名称重复');
										return;
									}
								}
							})
						}
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/business-service/business_service_dep_detail.html'),
				title:cfg.bizType=='bizDep'?((type=="add"?"新建":"编辑")+'业务单位'):((type=="add"?"新建":"编辑")+'业务服务'),
				height:250,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg,cfg);
					$("#nameLabel").html(cfg.bizType=="bizDep"?"业务单位：":"业务服务：");
					(type!='add')&&that._loadResource(cfg.row.id);
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
			saveCall:function(type,div,data,json,bizType){
				if(type=="add"){
					var row = eval(data);
					var src = oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+row.fileId);
					div.append(oc.module.biz.service.draw.addPicDiv(bizType=="bizDep"?"bizDep":"bizSer",
							src,row.name,row.id,true,null,row.entryId));
					oc.business.service.west.appendBizDepOrBizSerAccordionCfg(row, bizType=="bizDep"?0:1);
					oc.module.biz.service.draw.toolsDroppable();
				}else if(type=="edit"){
					div.find('.text:first').attr("title",json.name);
					if(json.name.length > 6){
						div.find('.text:first').text(json.name.substring(0,6)+"...");
					}else{
						div.find('.text:first').text(json.name);
					}
					
					
				}
			}//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#bizDepDetail',
		_init:function(dlg,cfg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm(cfg);
		},
		_initForm:function(cfg){
			var that = this;
			this.form=oc.ui.form({
				selector:this._mainDiv.find('.bizDepDetailForm'),
				filebox:[{
					selector:'[name=file]',
					width:164,
					onChange:function(newValue,oldValue){
						that.form.filebox[0].selector.textbox('setText',newValue.substr(newValue.lastIndexOf("\\") + 1));
						setImagePreview($("input[type=file]"),$("#localImag"),$("#preview"));
						$("#fileId").val("");
					}
				}]
			});
			this.form.find("#preview").attr("src",
					oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+(cfg.bizType=="bizDep"?1001:1002)))
					.css({'display':'block',width:'67px',height:'67px'});
		},
		_loadResource:function(id){
			var that=this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/dep/get.htm'),
				data:{id:id},
				successMsg:null,
				success:function(d){
					that.form.val(d.data);
					that.form.find("#oldName").val(d.data.name);
					that.form.find("#preview").attr("src",
							oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+d.data.fileId))
							.css({'display':'block',width:'67px',height:'67px'});
				}
			});
		}
	};
	
	function setImagePreview(docObj,localImagId,imgObjPreview){
		docObj = docObj.get(0);
        if(docObj.files && docObj.files[0]){
            //火狐下，直接设img属性
        	imgObjPreview.css({'display':'block',width:'67px',height:'67px'})
        	.attr("src",window.URL.createObjectURL(docObj.files[0]));
        }else{
            //IE下，使用滤镜
        	  docObj.select();
              docObj.blur(); 
              var imgSrc = document.selection.createRange().text;
              //必须设置初始大小
              $("#preview").attr("src",imgSrc);
              $("#preview").css("display","none");
              localImagId.css({width:'67px',height:'67px'});
            //图片异常的捕捉，防止用户修改后缀来伪造图片
            try{
                localImagId.get(0).style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
                localImagId.get(0).filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
             }catch(e){
                $.messager.alert("info","提示","您上传的图片格式不正确，请重新选择!");
                return false;
              }  
             if(imgObjPreview.style!=undefined && imgObjPreview.style!=null){
            	 imgObjPreview.style.display = 'none';
             }
              document.selection.empty();
        }
        return true;
    }	
	
	oc.ns('oc.module.biz.service.dep');
	
	oc.module.biz.service.dep={
		open:function(cfg){
			var detail=new bizDepDetail(cfg);
			detail.open();
		}
	};
})(jQuery);
