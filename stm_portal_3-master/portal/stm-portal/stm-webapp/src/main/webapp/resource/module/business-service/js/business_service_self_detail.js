(function($){
	function bizDepDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	bizDepDetail.prototype={
		constructor:bizDepDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,type=cfg.type,div=cfg.div,canvas=cfg.canvas,dragType=cfg.dragType,
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
						if(that.form.validate() && that.form.find("input[name=imgName]").val().trim() != ""){
							if(that.form.find("input[name='file']").val().trim()!=""
								&& !that.form.find("input[name='file']").val().trim().match(/gif|GIF|jpg|JPG|jpeg|JPEG|bmp|BMP|png|PNG$/)){
								alert("图片只能上传jpg、jpeg、gif、png这几种格式");
								return;
							}
							that.form.submitWithFile({
								url:oc.resource.getUrl(
									(type=='add')?'portal/business/service/self/insert.htm':'portal/business/service/self/update.htm'),
								data:that.form.val(),
								successMsg:null,
								success:function(data){
									cfg.saveCall&&cfg.saveCall(type,div,data,that.form.val(),canvas,dragType);
									dlg.dialog('close');
									alert("保存成功");
								}
							});
						}
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/business-service/business_service_self_detail.html'),
				title:((type=='add')?'新增':(type=='edit')?'编辑':'查看')+'自定义元素',
				height:280,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					(type!='add')&&that._loadResource(cfg.id);
					if(that.cfg.type=='view'){
						that.form.disable();
					}
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
			saveCall:function(type,div,data,json,canvas,dragType){
				var obj = eval(data.data);
				var src = oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+obj.fileId);
				div.append(oc.module.biz.service.draw.addPicDiv(dragType,src,obj.imgName,obj.id,true,canvas));
				oc.module.biz.service.draw.toolsDroppable();
			}//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#bizDepDetail',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			var data = [];
			var cfg=this.cfg,type=cfg.dragType;
			if(type=="liuchengpic"){
				data.push({id:"0",name:"流程"});
			}else if(type=="quyupic"){
				data.push({id:"1",name:"区域与节点"});
			}else if(type=="ismappic"){
				data.push({id:"2",name:"地图"});
			}else if(type=="backgroundpic"){
				data.push({id:"3",name:"背景"});
			}
			this.form=oc.ui.form({
				selector:this._mainDiv.find('.bizDepDetailForm'),
				filebox:[{
					selector:'[name=file]',
					width:164,
					onChange:function(newValue,oldValue){
						setImagePreview($("input[type=file]"),$("#localImag"),$("#preview"));
						$("#fileId").val("");
					}
				}],
				combobox:[{
	                selector:"[name='type']",
	                multiple:false,
	                width:164,
	                data:data
	            }]
			});
			this.form.find("#preview").attr("src",
					oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+1000))
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
					that.form.find("#preview").attr("src",oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+d.data.fileId));
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
            var imgSrc = document.selection.createRange().text;
            //必须设置初始大小
            localImagId.css({width:'67px',height:'67px'})
            //图片异常的捕捉，防止用户修改后缀来伪造图片
            try{
                localImagId.get(0).style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
                localImagId.get(0).filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
             }catch(e){
                $.messager.alert("info","提示","您上传的图片格式不正确，请重新选择!");
                return false;
              }                         
              imgObjPreview.style.display = 'none';
              document.selection.empty();
        }
        return true;
    }	
	
	oc.ns('oc.module.biz.service.self');
	
	oc.module.biz.service.self={
		open:function(cfg){
			var detail=new bizDepDetail(cfg);
			detail.open();
		}
	};
})(jQuery);
